package witparking.inspection.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * CameraPlugin
 */
public class CameraPlugin implements MethodCallHandler {

    private static Registrar registrar;
    private static int REQ_CAMERA = 85775482;

    private static Result cameraResult;
    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {

        CameraPlugin.registrar = registrar;

        final MethodChannel channel = new MethodChannel(registrar.messenger(), "camera");
        channel.setMethodCallHandler(new CameraPlugin());

        /*
        * 监听照相机返回的结果
        * */
        registrar.addActivityResultListener(new PluginRegistry.ActivityResultListener() {
            @Override
            public boolean onActivityResult(int i, int i1, Intent intent) {
                if (i == REQ_CAMERA && i1 == Activity.RESULT_OK) {
                    Bundle extras = intent.getExtras();
                    assert extras != null;
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    try {
                        baos.flush();
                        baos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    byte[] bitmapBytes = baos.toByteArray();
                    String base64Image = Base64.encodeToString(bitmapBytes, Base64.NO_WRAP);
                    if (cameraResult != null) {
                        cameraResult.success(base64Image);
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {


        switch (call.method) {
            case "takePhoto":
                takePhoto(result);
                break;
            default:
                break;
        }

    }


    /*
    * 获取缩略图
    * */
    private void takePhoto(Result result) {
        cameraResult = result;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(registrar.activity().getPackageManager()) != null) {//判断是否有相机应用
            registrar.activity().startActivityForResult(intent, REQ_CAMERA);
        }
    }
}
