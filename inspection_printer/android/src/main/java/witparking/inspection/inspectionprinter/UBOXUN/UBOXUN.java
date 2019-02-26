package witparking.inspection.inspectionprinter.UBOXUN;

import android.app.Activity;
import android.device.PrinterManager;
import android.util.Log;
import android.widget.Toast;

public class UBOXUN {

    Activity activity;
    private PrinterManager printerManager = new PrinterManager();

    UBOXUN(Activity activity) {
        this.activity = activity;
        //打印机基本设定
        printerManager.prn_setSpeed(0);
        printerManager.setGrayLevel(4);
    }

    /*
    * 打印文本
    * */
    void printText (String text, UBOXUNPrintInterface callback) {

        //检测状态
        if (printerManager.getStatus() != 0) {
            if (printerManager.getStatus() == -1) {
                callback.onError("打印机缺纸");
            } else if (printerManager.getStatus() == -2) {
                callback.onError("打印头过热");
            } else {
                callback.onError("打印机异常");
            }
            return;
        }

        //状态正常开始打印
        printerManager.setupPage(384, -1);
        printerManager.drawTextEx(text, 5, 0, 384, -1, "arial", 26, 0, 0, 0);

    }

    /*
    * 打印二维码
    * */
    void printQRCode (String content, UBOXUNPrintInterface callback) {

    }
}
