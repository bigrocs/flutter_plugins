package witparking.inspection.inspectionupdate;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.squareup.okhttp.Request;
import com.ypy.eventbus.EventBus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by yangyuxi on 2017/12/5.
 */

public class AppUpdateUtils {

    private static final String TAG = AppUpdateUtils.class.getSimpleName();
    private Context mContext;
    private File sdcardFile = Environment.getExternalStorageDirectory();

    public AppUpdateUtils(Context context) {
        mContext = context;
    }

    /*
    * 检测更新
    * */
    public void checkOutAppVersionOnService(String url) {

        HttpUtils httpUtils = new HttpUtils();

        httpUtils.postDataWithUrlencoded(url, new HttpResultCallback() {

            @Override
            public void onGetResult(String result) {
                Log.i(TAG, result);
                try {
                    JSONObject data = new JSONObject(result);
                    String downloadUrl = data.getJSONObject("returnObject").getJSONObject("attachment").getString("fileName");
                    Log.i(TAG, downloadUrl);
                    if (compareVersionNameAndVersionCode("", data.getJSONObject("returnObject").getInt("clientVersion"))) {
                        /*
                        * 下载APK文件
                        * */
                        String fileName = data.getJSONObject("returnObject").getString("appName");
                        fileName = fileName.substring(0, fileName.indexOf(".apk") + 4);
                        Log.i(TAG, fileName);

                        //showUpdateDialog(downloadUrl, fileName);

                        downloadAppWithUrl(downloadUrl, fileName);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Request request, IOException e) {
                Log.e(TAG, e + "");
            }
        });
    }

    /*
    * 展示是否更新的对话框
    * */
    private void showUpdateDialog(final String url, final String name) {

        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                //已在主线程中，可以更新UI

            }
        });
    }

    /*
    * 比较版本号和版本名称
    * */
    private boolean compareVersionNameAndVersionCode(String versionNameOnServer, int versionCodeOnServer) {
        try {

            String versionName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
            int versionCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;

            Log.i(TAG, "当前版本：" + versionName + ", 当前版本号：" + versionCode);
            Log.i(TAG, "服务器版本：" + versionNameOnServer + ", 服务器版本号：" + versionCodeOnServer);

            if (versionNameOnServer.equals("") || versionNameOnServer.compareTo(versionName) >= 0) {
                if (versionCodeOnServer > versionCode) {
                    //此时更新
                    Log.i(TAG, "准备更新......");

                    return true;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    /*
    * 下载更新内容
    * */
    public void downloadAppWithUrl(String url, String name) {

        DownloadUtil.get().download(url, "Download", name, new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {
                Log.i(TAG, "下载完成");
                AppUpdateEvent event = new AppUpdateEvent();
                event.progress = 100;
                event.complete = true;
                EventBus.getDefault().post(event);
                installAPK(file);
            }

            @Override
            public void onDownloading(final int progress) {

                AppUpdateEvent event = new AppUpdateEvent();
                event.progress = progress;
                EventBus.getDefault().post(event);
            }

            @Override
            public void onDownloadFailed() {
                Log.i(TAG, "下载失败");
                AppUpdateEvent event = new AppUpdateEvent();
                event.progress = 0;
                event.error = "文件下载失败，请检查网络";
                EventBus.getDefault().post(event);
            }
        });
    }

    /*
    * 安装App
    * */
    private void installAPK(File file) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        }else
        {
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setDataAndType(Uri.fromFile(file.getAbsoluteFile()),
                    "application/vnd.android.package-archive");
        }

        mContext.startActivity(intent);
    }


    private File copyAssets(Context context, String filename) {
        AssetManager assetManager = context.getAssets();
        InputStream in;
        OutputStream out;
        try {
            in = assetManager.open(filename);
            String outFileName = Environment.getExternalStorageDirectory().
                    getAbsolutePath();//保存到外部存储,大部分设备是sd卡根目录
            //String copyName = System.currentTimeMillis() + "wshlauncher.apk";//copy后具体名称
            String copyName = "Download/wshlauncher.apk";
            File outFile = new File(outFileName, copyName);

            if (outFile.exists()) {
                outFile.delete();
            }

            out = new FileOutputStream(outFile);
            copyFile(in, out);
            in.close();
            out.flush();
            out.close();
            return outFile;
        } catch (IOException e) {
            System.out.println("");
        }
        return new File("");
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public static boolean isAvilible(Context context, String packageName) {
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        //从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }


}
