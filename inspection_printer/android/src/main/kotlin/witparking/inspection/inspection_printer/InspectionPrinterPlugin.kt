package witparking.inspection.inspection_printer

import androidx.annotation.NonNull
import android.content.Context

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

import witparking.inspection.inspection_printer.printers.BasePrinter
import witparking.inspection.inspection_printer.printers.PrinterFactory

/** InspectionPrinterPlugin */
class InspectionPrinterPlugin: FlutterPlugin, MethodCallHandler {
  private lateinit var channel : MethodChannel
  private lateinit var context: Context
  private var printer: BasePrinter? = null

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "inspection_printer")
    channel.setMethodCallHandler(this)
    context = flutterPluginBinding.applicationContext
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    when (call.method) {
      "init" -> {
        try {
          val printerFactory = PrinterFactory(context)
          try {
              printer = printerFactory.createPrinter()
              
              if (printer == null) {
                  result.error("NO_PRINTER", "No compatible printer found", null)
                  return
              }
          } catch (e: Exception) {
              result.error("PRINTER_ERROR", e.message ?: "Unknown printer error", null)
              return
          }

          val initSuccess = printer?.init() ?: false
          if (!initSuccess) {
            val status = printer?.getStatus() ?: "Unknown error"
            result.error("INIT_ERROR", status, null)
            return
          }
          result.success(true)
        } catch (e: Exception) {
          result.error("INIT_ERROR", e.message ?: "Unknown error", null)
        }
      }
      "getStatus" -> {
        try {
          if (printer == null) {
            result.error("NOT_INITIALIZED", "Printer not initialized", null)
            return
          }
          result.success(printer?.getStatus())
        } catch (e: Exception) {
          result.error("STATUS_ERROR", e.message, null)
        }
      }
      "getSerialNumber" -> {
        if (printer == null) {
          result.error("NOT_INITIALIZED", "Printer not initialized", null)
          return
        }
        try {
          val serialNumber = printer?.getSerialNumber() ?: ""
          result.success(serialNumber)
        } catch (e: Exception) {
          result.error("SERIAL_ERROR", e.message, null)
        }
      }

      "printText" -> {
        try {
          val text = call.argument<String>("text")
          val size = call.argument<Int>("size")
          val bold = call.argument<Boolean>("bold")
          val underline = call.argument<Boolean>("underline")
          val align = call.argument<String>("align")
          
          if (text == null) {
            result.error("INVALID_DATA", "Text cannot be null", null)
            return
          }

          if (printer == null) {
            result.error("NOT_INITIALIZED", "Printer not initialized", null)
            return
          }
          
          val success = printer?.printText(text, size, bold, underline, align) ?: false
          result.success(success)
        } catch (e: Exception) {
          result.error("PRINT_ERROR", e.message, null)
        }
      }
      "printBarCode" -> {
        try {
          val data = call.argument<String>("data")
          val width = call.argument<Int>("width") ?: 2
          val height = call.argument<Int>("height") ?: 100
          val position = call.argument<Int>("position") ?: 2

          if (data == null) {
            result.error("INVALID_DATA", "Barcode data cannot be null", null)
            return
          }

          if (printer == null) {
            result.error("NOT_INITIALIZED", "Printer not initialized", null)
            return
          }
          val success = printer?.printBarCode(data, width, height, position) ?: false
          result.success(success)
        } catch (e: Exception) {
          result.error("PRINT_ERROR", e.message, null)
        }
      }
      "printQRCode" -> {
        try {
          val data = call.argument<String>("data")
          val size = call.argument<Int>("size") ?: 5
          val errorLevel = call.argument<Int>("errorLevel") ?: 2

          if (data == null) {
            result.error("INVALID_DATA", "QR code data cannot be null", null)
            return
          }

          if (printer == null) {
            result.error("NOT_INITIALIZED", "Printer not initialized", null)
            return
          }
          val success = printer?.printQRCode(data, size, errorLevel) ?: false
          result.success(success)
        } catch (e: Exception) {
          result.error("PRINT_ERROR", e.message, null)
        }
      }
      "cutPaper" -> {
        try {
          if (printer == null) {
            result.error("NOT_INITIALIZED", "Printer not initialized", null)
            return
          }
          val success = printer?.cutPaper() ?: false
          result.success(success)
        } catch (e: Exception) {
          result.error("PRINT_ERROR", e.message, null)
        }
      }
      "feedPaper" -> {
        try {
          val lines = call.argument<Int>("lines") ?: 1
          
          if (printer == null) {
            result.error("NOT_INITIALIZED", "Printer not initialized", null)
            return
          }
          val success = printer?.feedPaper(lines) ?: false
          result.success(success)
        } catch (e: Exception) {
          result.error("PRINT_ERROR", e.message, null)
        }
      }
      "openDrawer" -> {
        try {
          if (printer == null) {
            result.error("NOT_INITIALIZED", "Printer not initialized", null)
            return
          }
          val success = printer?.openDrawer() ?: false
          result.success(success)
        } catch (e: Exception) {
          result.error("PRINT_ERROR", e.message, null)
        }
      }
      "reset" -> {
        try {
          if (printer == null) {
            result.error("NOT_INITIALIZED", "Printer not initialized", null)
            return
          }
          val success = printer?.reset() ?: false
          result.success(success)
        } catch (e: Exception) {
          result.error("PRINT_ERROR", e.message, null)
        }
      }
      "printImage" -> {
        try {
          val img = call.argument<ByteArray>("img")
          
          if (img == null) {
            result.error("INVALID_DATA", "Image data cannot be null", null)
            return
          }

          if (printer == null) {
            result.error("NOT_INITIALIZED", "Printer not initialized", null)
            return
          }
          val success = printer?.printImage(img) ?: false
          result.success(success)
        } catch (e: Exception) {
          result.error("PRINT_ERROR", e.message, null)
        }
      }
      else -> result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
    printer?.release()
    printer = null
  }


}
