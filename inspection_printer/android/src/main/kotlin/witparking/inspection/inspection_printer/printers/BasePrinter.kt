package witparking.inspection.inspection_printer.printers

interface BasePrinter {
    fun init(): Boolean
    fun getStatus(): String
    fun getSerialNumber(): String
    fun printText(text: String, size: Int? = null, bold: Boolean? = null, underline: Boolean? = null, align: String? = null): Boolean
    fun printBarCode(data: String, height: Int? = null, width: Int? = null, textPosition: Int? = null): Boolean
    fun printQRCode(data: String, moduleSize: Int? = null, errorLevel: Int? = null): Boolean
    fun printImage(img: ByteArray): Boolean
    fun feedPaper(lines: Int = 1): Boolean
    fun cutPaper(): Boolean
    fun openDrawer(): Boolean
    fun reset(): Boolean
    fun release()
}