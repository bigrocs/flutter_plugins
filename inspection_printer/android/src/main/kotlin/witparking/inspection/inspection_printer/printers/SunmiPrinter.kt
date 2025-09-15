package witparking.inspection.inspection_printer.printers
import com.sunmi.printerx.PrinterSdk
import com.sunmi.printerx.style.*
import com.sunmi.printerx.enums.*
import android.content.Context
import android.util.Log
import android.graphics.BitmapFactory

var selectPrinter: PrinterSdk.Printer? = null

class SunmiPrinter(private val context: Context) : BasePrinter {
    companion object {
        private const val TAG = "SunmiPrinter"
    }

    private var isConnected: Boolean = false

    override fun init(): Boolean {
        try {
            
            // 获取打印机实例
            PrinterSdk.getInstance().getPrinter(context, object : PrinterSdk.PrinterListen {
                override fun onDefPrinter(printer: PrinterSdk.Printer?) {
                    if (selectPrinter == null) {
                        selectPrinter = printer
                        Log.d(TAG, "获取默认打印机成功")
                    }
                }
                override fun onPrinters(printers: MutableList<PrinterSdk.Printer>?) {
                    Log.d(TAG, "检测到 ${printers?.size ?: 0} 台打印机")
                }
            })
            
            isConnected = true
            return true
        } catch (e: Exception) {
            Log.e(TAG, "初始化打印机失败: ${e.message}")
            isConnected = false
            return false
        }
    }

    override fun getStatus(): String {
        return try {
            Log.d(TAG, "获取打印机状态: ${if (isConnected) "normal" else "error"}")
            if (isConnected) "normal" else "error"
        } catch (e: Exception) {
            Log.e(TAG, "获取打印机状态失败: ${e.message}")
            "error"
        }
    }

    override fun getSerialNumber(): String {
        return try {
            val mockSerial = "TEST-SERIAL-001"
            Log.d(TAG, "获取打印机序列号: $mockSerial")
            mockSerial
        } catch (e: Exception) {
            Log.e(TAG, "获取打印机序列号失败: ${e.message}")
            ""
        }
    }

    override fun printText(text: String, size: Int?, bold: Boolean?, underline: Boolean?, align: String?): Boolean {
        if (!isConnected) return false
        return try {
            selectPrinter?.lineApi()?.run {
                // 1. 初始化基础样式
                val baseStyle = BaseStyle.getStyle().setAlign(when (align?.uppercase()) {
                    "CENTER" -> Align.CENTER
                    "RIGHT" -> Align.RIGHT
                    else -> Align.LEFT
                })
                initLine(baseStyle)
                
                // 2. 设置文本样式
                val textStyle = TextStyle.getStyle()
                    .setTextSize(((size?: 4).coerceIn(1, 16) * 6).coerceIn(6, 96))
                    .enableBold(bold ?: false)
                    .enableUnderline(underline ?: false)
                
                // 3. 打印文本
                printText(text, textStyle)
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "打印文本失败: ${e.message}")
            Log.e(TAG, "异常堆栈: ", e)
            false
        }
    }

    override fun printBarCode(data: String, height: Int?, width: Int?, textPosition: Int?): Boolean {
        if (!isConnected) return false
        return try {
            selectPrinter?.lineApi()?.run {
                val barcodeStyle = BarcodeStyle.getStyle()
                    .setAlign(Align.CENTER)
                    .setDotWidth(width ?: 2)
                    .setBarHeight(height ?: 100)
                    .setReadable(when(textPosition) {
                        0 -> HumanReadable.HIDE
                        1 -> HumanReadable.POS_ONE
                        2 -> HumanReadable.POS_TWO
                        else -> HumanReadable.POS_TWO
                    })
                printBarCode(data, barcodeStyle)
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "打印条形码失败: ${e.message}")
            false
        }
    }

    override fun printQRCode(data: String, moduleSize: Int?, errorLevel: Int?): Boolean {
        if (!isConnected) return false
        return try {
            selectPrinter?.lineApi()?.run {
                printDividingLine(DividingLine.EMPTY, 1*6*2)
                val qrStyle = QrStyle.getStyle()
                    .setAlign(Align.CENTER)
                    .setDot(moduleSize?.coerceIn(1, 16) ?: 12)
                    .setErrorLevel(when(errorLevel) {
                        0 -> ErrorLevel.L
                        1 -> ErrorLevel.M
                        2 -> ErrorLevel.Q
                        3 -> ErrorLevel.H
                        else -> ErrorLevel.Q
                    })
                printQrCode(data, qrStyle)
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "打印二维码失败: ${e.message}")
            false
        }
    }

    override fun printImage(img: ByteArray): Boolean {
        if (!isConnected) return false
        return try {
            selectPrinter?.lineApi()?.run {
                val bitmap = BitmapFactory.decodeByteArray(img, 0, img.size)
                val bitmapStyle = BitmapStyle.getStyle()
                    .setAlign(Align.CENTER)
                    .setAlgorithm(ImageAlgorithm.DITHERING)
                printBitmap(bitmap, bitmapStyle)
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "打印图片失败: ${e.message}")
            false
        }
    }

    override fun feedPaper(lines: Int): Boolean {
        if (!isConnected) return false
        return try {
            selectPrinter?.lineApi()?.run {
                printDividingLine(DividingLine.EMPTY, lines*6*4)
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "走纸失败: ${e.message}")
            false
        }
    }

    override fun cutPaper(): Boolean {
        if (!isConnected) return false
        return try {
            selectPrinter?.lineApi()?.run {
                // 先走纸6行，再切纸
                printDividingLine(DividingLine.EMPTY, 3*6*4)
                autoOut()
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "切纸失败: ${e.message}")
            false
        }
    }

    override fun openDrawer(): Boolean {
        if (!isConnected) return false
        return try {
            // 商米打印机暂不支持钱箱功能
            Log.w(TAG, "商米打印机暂不支持钱箱功能")
            false
        } catch (e: Exception) {
            Log.e(TAG, "打开钱箱失败: ${e.message}")
            false
        }
    }

    override fun reset(): Boolean {
        if (!isConnected) return false
        return try {
            selectPrinter?.lineApi()?.run {
                // 重置打印机样式
                initLine(BaseStyle.getStyle())
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "重置打印机失败: ${e.message}")
            false
        }
    }

    override fun release() {
        try {
            selectPrinter = null
            isConnected = false
            Log.d(TAG, "打印机资源已释放")
        } catch (e: Exception) {
            Log.e(TAG, "释放打印机资源失败: ${e.message}")
        }
    }
}