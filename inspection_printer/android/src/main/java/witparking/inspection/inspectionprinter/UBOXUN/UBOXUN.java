package witparking.inspection.inspectionprinter.UBOXUN;

import android.app.Activity;
import android.device.PrinterManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import witparking.inspection.inspectionprinter.CreateErWei;
import witparking.inspection.inspectionprinter.WPPrinter;

public class UBOXUN extends WPPrinter {

    Activity activity;
    private PrinterManager printerManager = new PrinterManager();

    public UBOXUN(Activity activity) {
        this.activity = activity;
        //打印机基本设定
        printerManager.prn_setSpeed(0);
        printerManager.setGrayLevel(4);
    }

    /*
     * 检测打印机当前状态
     * */
    private boolean checkPrinterState(UBOXUNPrintInterface callback) {
        //检测状态
        if (printerManager.getStatus() != 0) {
            if (printerManager.getStatus() == -1) {
                callback.onError("打印机缺纸");
            } else if (printerManager.getStatus() == -2) {
                callback.onError("打印头过热");
            } else {
                callback.onError("打印机异常");
            }
            return false;
        }
        return true;
    }

    /*
     * 打印文本
     * */
    public void printText(String text, UBOXUNPrintInterface callback) {
        if (!checkPrinterState(callback)) return;
        //状态正常开始打印
        printerManager.setupPage(384, -1);
        printerManager.drawTextEx(text, 5, 0, 384, -1, "arial", 26, 0, 0, 0);
        printerManager.printPage(0);
    }

    /*
     * 打印二维码
     * @param content 内容
     * */
    void printQRCode(String content, UBOXUNPrintInterface callback) {
        if (!checkPrinterState(callback)) return;
        printerManager.setupPage(384, -1);
        Bitmap _img = new CreateErWei().createQRImage(content);
        printerManager.drawBitmap(_img, 10, 0);
    }

    /*
     * 打印图片
     * */
    public void printImage(String base64Image, UBOXUNPrintInterface callback) {
        if (!checkPrinterState(callback)) return;
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(base64Image, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
            printerManager.setupPage(384, -1);
            printerManager.drawBitmap(bitmap, 10, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 打印空行
     * */
    void printBlankLine(UBOXUNPrintInterface callback) {
        if (!checkPrinterState(callback)) return;
        printerManager.setupPage(384, -1);
        printerManager.printPage(0);
    }
}
