package witparking.inspection.inspectionqrcode;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Base64;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.ypy.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import witparking.inspection.inspectionqrcode.zxing.android.CaptureActivity;
import witparking.inspection.inspectionqrcode.zxing.util.CreateErWei;

/**
 * InspectionQrcodePlugin
 */
public class InspectionQrcodePlugin implements MethodCallHandler {

    private static Registrar registrar;
    private Result scanResult;

    private InspectionQrcodePlugin() {
        
        EventBus.getDefault().register(InspectionQrcodePlugin.this);

        InspectionQrcodePlugin.registrar.addViewDestroyListener(new PluginRegistry.ViewDestroyListener() {
            @Override
            public boolean onViewDestroy(FlutterNativeView flutterNativeView) {
                EventBus.getDefault().unregister(InspectionQrcodePlugin.this);
                return false;
            }
        });
    }
    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {

        InspectionQrcodePlugin.registrar = registrar;

        final MethodChannel channel = new MethodChannel(registrar.messenger(), "inspection_qrcode");
        channel.setMethodCallHandler(new InspectionQrcodePlugin());
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        switch (call.method) {
            case "startScan":
                scanResult = result;
                AndPermission
                        .with(InspectionQrcodePlugin.registrar.activity())
                        .requestCode(100)
                        .permission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .callback(new PermissionListener() {
                            @Override
                            public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                                Intent intent = new Intent(InspectionQrcodePlugin.registrar.activity(), CaptureActivity.class);
                                InspectionQrcodePlugin.registrar.activity().startActivity(intent);
                            }
                            @Override
                            public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {

                            }
                        }).start();
                break;
            case "createQRCode":
                String base64Image = "";
                CreateErWei createErWei = new CreateErWei();
                Bitmap bitmap = createErWei.createQRImage((String) call.argument("content"));
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
                result.success(base64Image);
                break;
            default:
                break;
        }
    }

    /*
    * 扫码返回结果
    * */
    public void onEventMainThread(QRCodeEvent event) {
        if (event.str != null) {
            scanResult.success(event.str);
        }
    }
}
