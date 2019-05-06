package witparking.inspection.inspectionprinter.UBOXUN;

import android.app.Activity;
import android.device.PrinterManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Base64;

import witparking.inspection.inspectionprinter.CreateErWei;
import witparking.inspection.inspectionprinter.WPPrinter;

import static witparking.inspection.inspectionprinter.UBOXUN.UBXPrinterManager.CONTENT_ALIGN_CENTER;
import static witparking.inspection.inspectionprinter.UBOXUN.UBXPrinterManager.CONTENT_ALIGN_LEFT;
import static witparking.inspection.inspectionprinter.UBOXUN.UBXPrinterManager.CONTENT_ALIGN_RIGHT;


public class UBOXUN extends WPPrinter {

    Activity activity;
    private PrinterManager printerManager = new PrinterManager();
    private UBXPrinterManager ubxPrinterManager = UBXPrinterManager.getInstance();

    public UBOXUN(Activity activity) {
        this.activity = activity;
        //打印机基本设定
        printerManager.prn_setSpeed(0);
        printerManager.setGrayLevel(4);
        ubxPrinterManager.init(new Handler(

        ), activity, 1);
    }

    /*
     * 检测打印机当前状态
     * */
    private boolean checkPrinterState(UBOXUNPrintInterface callback) {
        //检测状态
        if (printerManager.getStatus() != 0) {
            if (printerManager.getStatus() == -1) {
                if (callback != null) callback.onError("打印机缺纸");
            } else if (printerManager.getStatus() == -2) {
                if (callback != null) callback.onError("打印头过热");
            } else {
                if (callback != null) callback.onError("打印机异常");
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
        //printerManager.setupPage(384, -1);
        if (text.indexOf("centered:") == 0) {
            text = text.split("centered:")[1];
            ubxPrinterManager.appendText(text, 1, CONTENT_ALIGN_CENTER);
            ubxPrinterManager.prtLayout();
            return;
        }
        ubxPrinterManager.appendText(text, 1, CONTENT_ALIGN_LEFT);
        ubxPrinterManager.prtLayout();
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
            ubxPrinterManager.appendBitmap(bitmap, CONTENT_ALIGN_CENTER);
            ubxPrinterManager.prtLayout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
