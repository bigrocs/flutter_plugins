package android.src.main.java.witparking.inspection.inspectionprinter.LIANDI;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.landicorp.android.eptapi.DeviceService;
import com.landicorp.android.eptapi.device.Printer;
import com.landicorp.android.eptapi.exception.ReloginException;
import com.landicorp.android.eptapi.exception.RequestException;
import com.landicorp.android.eptapi.exception.ServiceOccupiedException;
import com.landicorp.android.eptapi.exception.UnsupportMultiProcess;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import witparking.inspection.inspectionprinter.CreateErWei;
import witparking.inspection.inspectionprinter.WPPrinter;

public class LIANDI extends WPPrinter {

    private Activity activity;
    private Printer.Format format = new Printer.Format();
    private Printer printer;
    public ArrayList list;  //一次打印的数据

    public LIANDI(Activity activity) {
        this.activity = activity;
        bindDeviceService();

        try {
            progress.start();
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    /*
       打印机流程控制
    */
    public final Printer.Progress progress = new Printer.Progress() {

        @Override
        public void doPrint(Printer printer) throws Exception {
            format = new Printer.Format();
            format.setAscSize(Printer.Format.ASC_DOT5x7);
            format.setAscScale(Printer.Format.ASC_SC1x1);
            printer.setFormat(format);
            //超出一行是否自动截断
            printer.setAutoTrunc(false);

            preparedForPrint(printer);
        }

        @Override
        public void onFinish(int i) {

        }

        @Override
        public void onCrash() {
            DeviceService.logout();
        }
    };

    /*
    * 打印机初始化完成
    * */
    private void preparedForPrint(Printer printer) {
        this.printer = printer;
        try {
            doPrint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    * 进行一项完整的打印工作
    * */
    private void doPrint() throws Exception {

        for (Object item : list) {

            String value = (String) item;

            // base64图片
            if (value.indexOf("pictureStream") == 0) {
                String base64Image = value.split("pictureStream")[1];
                Bitmap bitmap = null;
                try {
                    InputStream isBm = activity.getResources().getAssets().open("image240.bmp");
                    printer.printImage(0, isBm);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                value += '\n';
                printer.printText(value);
            }
        }

        printer.printText(" \n");
        printer.printText(" \n");
        printer.printText(" \n");
        printer.printText(" \n");
        printer.printText(" \n");

    }

    /*
    * 检测打印机状态
    * */
    private boolean checkPrinterState(LIANDIPrintInterface callback) {
        try {
            int code = printer.getStatus();
            if (code == Printer.ERROR_NONE) {
                return true;
            }else
            {
                String msg = getErrorDescription(code);
                if (callback != null) callback.onError(msg);
            }
        } catch (RequestException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
    * 打印二维码
    * */
    public void printQRCode(String content, LIANDIPrintInterface callback) {
        if (!checkPrinterState(callback)) return;
        Bitmap _img = new CreateErWei().createQRImage(content);
        ByteArrayOutputStream baos = null;
        try {

            baos = new ByteArrayOutputStream();
            _img.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            baos.flush();
            baos.close();

            byte[] bitmapBytes = baos.toByteArray();

            printer.printImage(10, _img.getWidth(), _img.getHeight(), bitmapBytes);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    * 打印图片
    * */
    private void printImage(String base64Image, Printer p) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(base64Image, Base64.NO_WRAP);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
            p.printImage(10, bitmap.getWidth(), bitmap.getHeight(), bitmapArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //绑定设备
    private void bindDeviceService() {

        try {
            DeviceService.login(activity);
        } catch (ServiceOccupiedException e) {
            e.printStackTrace();
        } catch (ReloginException e) {
            e.printStackTrace();
        } catch (UnsupportMultiProcess unsupportMultiProcess) {
            unsupportMultiProcess.printStackTrace();
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    private String getErrorDescription(int code) {
        switch (code) {
            case Printer.ERROR_PAPERENDED:
                return "缺纸";
            case Printer.ERROR_HARDERR:
                return "硬件错误";
            case Printer.ERROR_OVERHEAT:
                return "打印头过热";
            case Printer.ERROR_BUFOVERFLOW:
                return "缓冲模式下所操作的位置超出范围";
            case Printer.ERROR_LOWVOL:
                return "低压保护";
            case Printer.ERROR_PAPERENDING:
                return "纸张将要用尽，还允许打印(单步进针打特有返回值)";
            case Printer.ERROR_MOTORERR:
                return "打印机芯故障(过快或者过慢)";
            case Printer.ERROR_PENOFOUND:
                return "自动定位没有找到对齐位置,纸张回到原来位置";
            case Printer.ERROR_PAPERJAM:
                return "卡纸";
            case Printer.ERROR_NOBM:
                return "没有找到黑标";
            case Printer.ERROR_BUSY:
                return "打印机处于忙状态";
            case Printer.ERROR_BMBLACK:
                return "黑标探测器检测到黑色信号";
            case Printer.ERROR_WORKON:
                return "打印机电源处于打开状态";
            case Printer.ERROR_LIFTHEAD:
                return "打印头抬起(自助热敏打印机特有返回值)";
            case Printer.ERROR_CUTPOSITIONERR:
                return "切纸刀不在原位(自助热敏打印机特有返回值)";
            case Printer.ERROR_LOWTEMP:
                return "低温保护或AD 出错(自助热敏打印机特有返回值)";
        }
        return "unknown error (" + code + ")";
    }
}
