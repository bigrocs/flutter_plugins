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

    private static Registrar registrar;
    private static int REQ_CAMERA = 85775482;

    private static Result cameraResult;
    private static Uri photoUri;
    /**
     * Plugin registration.
     */
    public static void registerWith(final Registrar registrar) {

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
                        if (cameraResult != null) {
                            cameraResult.success(base64Image);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
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
    * 拍照
    * */
    private void takePhoto(Result result) {
        cameraResult = result;
        AndPermission
                .with(CameraPlugin.registrar.activity())
                .requestCode(100)
                .permission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .callback(new PermissionListener() {
                    @Override
                    public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (intent.resolveActivity(registrar.activity().getPackageManager()) != null) {//判断是否有相机应用
                            photoUri = Uri.fromFile(getSavePhotoFile());
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

    private static Bitmap ImageCompress(Bitmap bitmap) {
        // 图片允许最大空间 单位：KB
        double maxSize = 700.00;
        // 将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        // 将字节换成KB
        double mid = b.length / 1024;
        // 判断bitmap占用空间是否大于允许最大空间 如果大于则压缩 小于则不压缩
        if (mid > maxSize) {
            // 获取bitmap大小 是允许最大大小的多少倍
            double i = mid / maxSize;
            // 开始压缩 此处用到平方根 将宽带和高度压缩掉对应的平方根倍
            bitmap = zoomImage(bitmap, bitmap.getWidth() / Math.sqrt(i),
                    bitmap.getHeight() / Math.sqrt(i));
        }
        return bitmap;
    }

    private static Bitmap zoomImage(Bitmap image, double newWidth, double newHeight) {
        // 获取这个图片的宽和高
        float width = image.getWidth();
        float height = image.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(image, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }
}
