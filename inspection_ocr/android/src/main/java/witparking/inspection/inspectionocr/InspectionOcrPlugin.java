package witparking.inspection.inspectionocr;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.ypy.eventbus.EventBus;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.view.FlutterNativeView;

/**
 * InspectionOcrPlugin
 */
public class InspectionOcrPlugin implements MethodCallHandler {

    private SpiteUtil spiteUtil;
    private Result recognitionResult;
    private Registrar registrar;

    private InspectionOcrPlugin(Registrar registrar) {

        this.registrar = registrar;

        EventBus.getDefault().register(InspectionOcrPlugin.this);

        spiteUtil = new SpiteUtil(registrar.activity());
        spiteUtil.init();
        

        registrar.addViewDestroyListener(new PluginRegistry.ViewDestroyListener() {
            @Override
            public boolean onViewDestroy(FlutterNativeView flutterNativeView) {
                EventBus.getDefault().unregister(InspectionOcrPlugin.this);
                if (spiteUtil != null) spiteUtil.onDestroy();
                return false;
            }
        });

    }

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {

        final MethodChannel channel = new MethodChannel(registrar.messenger(), "inspection_ocr");
        channel.setMethodCallHandler(new InspectionOcrPlugin(registrar));
    }

    @Override
    public void onMethodCall(MethodCall call, final Result result) {
        switch (call.method) {
            case "recognitionTheplate":

                System.gc();

                AndPermission
                        .with(registrar.activity())
                        .requestCode(100)
                        .permission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .callback(new PermissionListener() {
                            @Override
                            public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                                recognitionResult = result;
                                spiteUtil.spite(true);
                            }
                            @Override
                            public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {

                            }
                        }).start();
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    /*
    * 车牌识别结果
    * */
    public void onEventMainThread(final SpiteBackEvent event) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    String base64Image = "";
                    File dir = new File(event.path);
                    if (dir.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(event.path);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        try {
                            baos.flush();
                            baos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        byte[] bitmapBytes = baos.toByteArray();
                        base64Image = Base64.encodeToString(bitmapBytes, Base64.NO_WRAP);
                        bitmap.recycle();
                        bitmap = null;
                        baos = null;
                    }

                    Map<String, String> map = new HashMap<String, String>();
                    map.put("number", event.number);
                    map.put("color", event.color);
                    map.put("path", event.path);
                    map.put("base64Image", base64Image);

                    recognitionResult.success(map);
                    recognitionResult = null;
                    base64Image = null;
                    map = null;

                } catch (Exception e) {
                    Log.e("OCR 异常", e.getMessage());
                }

            }
        }).start();
    }
}
