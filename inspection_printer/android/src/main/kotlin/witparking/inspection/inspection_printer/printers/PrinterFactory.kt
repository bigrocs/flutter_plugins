package witparking.inspection.inspection_printer.printers

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager

class PrinterFactory(private val context: Context) {
    private var usbManager: UsbManager? = null

    init {
        usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    }

    fun createPrinter(): BasePrinter? {
        // 根据设备制造商判断打印机类型
        val manufacturer = android.os.Build.MANUFACTURER
        android.util.Log.d("PrinterFactory", "当前设备制造商: $manufacturer")
        
        // 判断是否为商米设备
        if ("SUNMI".equals(manufacturer)) {
            android.util.Log.d("PrinterFactory", "检测到商米设备，正在初始化商米打印机...")
            val sunmiPrinter = SunmiPrinter(context)
            if (sunmiPrinter.init()) {
                android.util.Log.d("PrinterFactory", "商米打印机初始化成功")
                return sunmiPrinter
            } else {
                android.util.Log.e("PrinterFactory", "商米打印机初始化失败")
            }
        }
        
        // 判断是否为联迪设备
        if ("LANDI".equals(manufacturer)) {
            android.util.Log.d("PrinterFactory", "检测到联迪设备，正在初始化联迪打印机...")
            val landiPrinter = LandiPrinter(context)
            if (landiPrinter.init()) {
                android.util.Log.d("PrinterFactory", "联迪打印机初始化成功")
                return landiPrinter
            } else {
                android.util.Log.e("PrinterFactory", "联迪打印机初始化失败")
            }
        }
        
        // 如果不是特定制造商的设备，尝试USB打印机
        val usbPrinter = findUsbPrinter()
        if (usbPrinter != null) {
            // TODO: 实现USB打印机类并返回实例
            return null
        }

        return null
    }

    private fun findUsbPrinter(): UsbDevice? {
        val deviceList = usbManager?.deviceList ?: return null
        return deviceList.values.firstOrNull { device ->
            device.interfaceCount > 0 && isValidPrinter(device)
        }
    }

    private fun isValidPrinter(device: UsbDevice): Boolean {
        try {
            // 检查设备类型是否为打印机
            // USB 打印机通常属于打印机类 (0x07)
            for (i in 0 until device.interfaceCount) {
                val usbInterface = device.getInterface(i)
                if (usbInterface.interfaceClass == 0x07) {
                    return true
                }
            }
            return false
        } catch (e: Exception) {
            return false
        }
    }
}