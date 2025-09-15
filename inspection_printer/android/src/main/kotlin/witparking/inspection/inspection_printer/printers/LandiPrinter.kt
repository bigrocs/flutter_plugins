package witparking.inspection.inspection_printer.printers

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import com.landicorp.android.eptapi.DeviceService
import com.landicorp.android.eptapi.device.Printer
import com.landicorp.android.eptapi.device.Printer.Format
import com.landicorp.android.eptapi.device.Printer.Alignment
import com.landicorp.android.eptapi.utils.QrCode
data class Quadruple<out A, out B, out C>(
    val first: A,
    val second: B,
    val third: C,
)
class LandiPrinter(private val context: Context) : BasePrinter {
    private var selectPrinter: Printer? = null
    private var isConnected: Boolean = false

    companion object {
        private const val TAG = "LandiPrinter"
    }

    override fun init(): Boolean {
        try {
            // 1. 登录设备服务（必须步骤）
            DeviceService.login(context)

            // 2. 获取打印机实例
            selectPrinter = Printer.getInstance()
    
            // 3. 检查打印机状态
            val status = selectPrinter?.status ?: 0
            if (status != Printer.ERROR_NONE) {
                val errorMessage = when (status) {
                    Printer.ERROR_PAPERENDED -> "打印机缺纸"
                    Printer.ERROR_HARDERR -> "打印机硬件错误"
                    Printer.ERROR_OVERHEAT -> "打印机过热"
                    Printer.ERROR_LOWVOL -> "打印机电压过低"
                    Printer.ERROR_PAPERENDING -> "打印机纸张即将用尽"
                    else -> "打印机状态异常: ${Printer.getErrorDescription(status)}"
                }
                Log.e(TAG, errorMessage)
                release()
                throw Exception(errorMessage)
            }

            isConnected = true
            Log.d(TAG, "打印机服务初始化成功")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "初始化失败: ${e.message}")
            release()
            throw e
        }
    }

    override fun getStatus(): String {
        return try {
            if (!isConnected) return "未连接"
            when (selectPrinter?.status) {
                Printer.ERROR_NONE -> "就绪"
                Printer.ERROR_PAPERENDED -> "缺纸"
                Printer.ERROR_HARDERR -> "硬件错误"
                else -> "未知状态"
            }
        } catch (e: Exception) {
            Log.e(TAG, "获取状态失败: ${e.message}")
            "状态获取失败"
        }
    }

    override fun getSerialNumber(): String {
        return "LANDI_${System.currentTimeMillis()}"
    }

    override fun printText(text: String, size: Int?, bold: Boolean?, underline: Boolean?, align: String?): Boolean {
        if (!isConnected) return false

        val progress = object : Printer.Progress() {
            override fun doPrint(printer: Printer) {
                // 1. 设置基础格式
                val format = Format()
                // 字体缩放设置
                val sizeConfig = when ((size ?: 1).coerceIn(1, 12)) {
                    in 1..4 -> Quadruple(Format.ASC_SC1x1, Format.HZ_SC1x1, 12)
                    in 5..8 -> Quadruple(Format.ASC_SC2x2, Format.HZ_SC2x2, 24)
                    in 9..12 -> Quadruple(Format.ASC_SC3x3, Format.HZ_SC3x3, 48)
                    else -> Quadruple(Format.ASC_SC1x1, Format.HZ_SC1x1, 12)
                }
                format.setAscScale(sizeConfig.first)
                format.setHzScale(sizeConfig.second)
                printer.setFormat(format)

                // // 2. 设置对齐方式（关键修改）
                val alignCode = when (align?.uppercase()) {
                    "CENTER" -> 0x0A   // EM_prn_MIDALIGN
                    "RIGHT" -> 0x09    // EM_prn_RIGHTALIGN
                    else -> 0x08        // EM_prn_LEFTALIGN
                }
                printer.setAutoTrunc(false)
                
                // 2. 计算对齐空格（新增代码）
                val PAPER_WIDTH_DOTS = selectPrinter?.getValidWidth() ?: 384
                val PAPER_WIDTH_CHARS = PAPER_WIDTH_DOTS / sizeConfig.third
                val scaledTextLength = text.length
                val leadingSpaces = when (align?.uppercase()) {
                    "CENTER" -> (PAPER_WIDTH_CHARS - scaledTextLength) / 2
                    "RIGHT" -> PAPER_WIDTH_CHARS - scaledTextLength
                    else -> 0
                }.coerceAtLeast(0)
                // 3. 执行打印（修改后的代码）
                printer.printText(" ".repeat(leadingSpaces) + text)
                
                // 优化调试日志
                Log.d(TAG, "打印内容=${text},物理宽度=${PAPER_WIDTH_DOTS}点, 字体缩放=${sizeConfig.first}x, 原始长度=${text.length}, 计算点数=$scaledTextLength, 可用字符数=$PAPER_WIDTH_CHARS, 前导空格=$leadingSpaces")
                printer.feedPix(1)
            }

            override fun onFinish(error: Int) {
                if (error == Printer.ERROR_NONE) {
                    Log.d(TAG, "打印成功")
                } else {
                    Log.e(TAG, "打印失败，错误码: $error")
                }
            }

            override fun onCrash() {
                Log.e(TAG, "打印机服务崩溃")
            }
        }

        return try {
            progress.start()
            true
        } catch (e: Exception) {
            Log.e(TAG, "打印异常: ${e.message}")
            false
        }
    }

    override fun printBarCode(data: String, height: Int?, width: Int?, textPosition: Int?): Boolean {
        if (!isConnected) return false
        // try {
        //     printer?.let { p ->
        //         p.setBarCodeHeight(height ?: 80)
        //         p.setBarCodeWidth(width ?: 2)
        //         p.setBarCodeTextPosition(textPosition ?: 2)
        //         p.addBarCode(data)
        //         p.feedLine(1)
        //         return true
        //     }
        //     return false
        // } catch (e: Exception) {
        //     Log.e(TAG, "打印条形码失败: ${e.message}")
        //     return false
        // }
        return true
    }

    override fun printQRCode(data: String, moduleSize: Int?, errorLevel: Int?): Boolean {
        if (!isConnected) return false

        val progress = object : Printer.Progress() {
            override fun doPrint(printer: Printer) {
               // 参数校验
               val qrSize = moduleSize?.coerceIn(1, 40) ?: 10  // 有效范围1-40
               val eccLevel = errorLevel?.coerceIn(0, 3) ?: QrCode.ECLEVEL_Q // 0:L, 1:M, 2:Q, 3:H

               // 1. 创建二维码对象
               val qrCode = QrCode(data, eccLevel)

               // 2. 设置二维码打印参数（文档3.6.1节）
               printer.printQrCode(
                   Printer.Alignment.CENTER, // 二维码居中
                   qrCode,
                   qrSize * 40 // 根据文档实际尺寸=原始尺寸*qrSize
               )
            }

            override fun onFinish(error: Int) {
                if (error == Printer.ERROR_NONE) {
                    Log.d(TAG, "打印成功")
                } else {
                    Log.e(TAG, "打印失败，错误码: $error")
                }
            }

            override fun onCrash() {
                Log.e(TAG, "打印机服务崩溃")
            }
        }

        return try {
            progress.start()
            true
        } catch (e: Exception) {
            Log.e(TAG, "走纸失败: ${e.message}")
            false
        }
        return true
    }

    override fun printImage(img: ByteArray): Boolean {
        if (!isConnected) return false
        // try {
        //     printer?.let { p ->
        //         val bitmap = BitmapFactory.decodeByteArray(img, 0, img.size)
        //         p.addImage(bitmap)
        //         p.feedLine(1)
        //         return true
        //     }
        //     return false
        // } catch (e: Exception) {
        //     Log.e(TAG, "打印图片失败: ${e.message}")
        //     return false
        // }
        return true
    }

    override fun feedPaper(lines: Int): Boolean {
        if (!isConnected) return false

        val progress = object : Printer.Progress() {
            override fun doPrint(printer: Printer) {
                printer?.feedLine(lines)
            }

            override fun onFinish(error: Int) {
                if (error == Printer.ERROR_NONE) {
                    Log.d(TAG, "打印成功")
                } else {
                    Log.e(TAG, "打印失败，错误码: $error")
                }
            }

            override fun onCrash() {
                Log.e(TAG, "打印机服务崩溃")
            }
        }

        return try {
            progress.start()
            true
        } catch (e: Exception) {
            Log.e(TAG, "走纸失败: ${e.message}")
            false
        }
        return true
    }

    override fun cutPaper(): Boolean {
        if (!isConnected) return false

        val progress = object : Printer.Progress() {
            override fun doPrint(printer: Printer) {
                printer?.feedLine(6)
                printer?.cutPaper()
            }

            override fun onFinish(error: Int) {
                if (error == Printer.ERROR_NONE) {
                    Log.d(TAG, "切纸成功")
                } else {
                    Log.e(TAG, "切纸失败，错误码: $error")
                }
            }

            override fun onCrash() {
                Log.e(TAG, "打印机切纸服务崩溃")
            }
        }

        return try {
            progress.start()
            true
        } catch (e: Exception) {
            Log.e(TAG, "走纸失败: ${e.message}")
            false
        }
        DeviceService.logout()
        return true
    }

    override fun openDrawer(): Boolean {
        if (!isConnected) return false
        // try {
        //     printer?.openCashBox()
        //     return true
        // } catch (e: Exception) {
        //     Log.e(TAG, "打开钱箱失败: ${e.message}")
        //     return false
        // }
        return true
    }

    override fun reset(): Boolean {
        if (!isConnected) return false
        // try {
        //     printer?.init()
        //     return true
        // } catch (e: Exception) {
        //     Log.e(TAG, "重置打印机失败: ${e.message}")
        //     return false
        // }
        return true
    }

    override fun release() {
        try {
            DeviceService.logout()
            isConnected = false
            Log.d(TAG, "打印机资源已释放")
        } catch (e: Exception) {
            Log.e(TAG, "释放资源失败: ${e.message}")
        }
    }
}