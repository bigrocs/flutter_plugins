package witparking.inspection.camera;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

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

    private Registrar registrar;
    private static int REQ_CAMERA = 85775482;

    private static Result cameraResult;
    private static Uri photoUri;

    private CameraPlugin(final Registrar registrar) {

        this.registrar = registrar;

        /*
         * 监听照相机返回的结果
         * */
        registrar.addActivityResultListener(new PluginRegistry.ActivityResultListener() {
            @Override
            public boolean onActivityResult(int i, int i1, Intent intent) {

                if (i == REQ_CAMERA && i1 == Activity.RESULT_OK) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ContentResolver contentResolver = registrar.activity().getContentResolver();

                            try {
                                Bitmap bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(photoUri));

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                                try {
                                    baos.flush();
                                    baos.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                byte[] bitmapBytes = baos.toByteArray();
                                String base64Image = Base64.encodeToString(bitmapBytes, Base64.NO_WRAP);


                                final String finalBase64Image = base64Image;
                                registrar.activity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        cameraResult.success(finalBase64Image);
                                    }
                                });

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                return false;
            }
        });
    };

    /**
     * Plugin registration.
     */
    public static void registerWith(final Registrar registrar) {

        final MethodChannel channel = new MethodChannel(registrar.messenger(), "camera");
        channel.setMethodCallHandler(new CameraPlugin(registrar));
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {

        switch (call.method) {
            case "takePhoto":
                System.gc();
                takePhoto(result);
                break;
            default:
                break;
        }

    }


    /*
    * 拍照
    * */
    private void takePhoto(Result result) {
        cameraResult = result;
        AndPermission
                .with(registrar.activity())
                .requestCode(100)
                .permission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .callback(new PermissionListener() {
                    @Override
                    public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (intent.resolveActivity(registrar.activity().getPackageManager()) != null) {//判断是否有相机应用
                            photoUri = FileProvider.getUriForFile(
                                    registrar.activity(),
                                    registrar.activity().getPackageName() + ".provider",
                                    getSavePhotoFile());
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                            registrar.activity().startActivityForResult(intent, REQ_CAMERA);
                        }
                    }
                    @Override
                    public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {

                    }
                }).start();
    }

    private File getSavePhotoFile () {

        String photoPath = registrar.activity().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "inspection_" + System.currentTimeMillis() + ".jpg";
        File file = new File(photoPath);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        return file;
    }

}
