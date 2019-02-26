package witparking.inspection.inspectionocr;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.ice.iceplate.ActivateService;
import com.ice.iceplate.RecogService;


public class SpiteUtil {
    public ActivateService.ActivateBinder acBinder;
    public Activity activity;

    public SpiteUtil(Activity activity) {
        this.activity = activity;
    }

    public ServiceConnection acConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            acConnection = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, final IBinder service) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    acBinder = (ActivateService.ActivateBinder) service;
                    String snInput = "B2305B28185AA93CC932C979CE7E56E8";
                    int code = acBinder.login(snInput);

                    if (code == 0) {
                        Log.e("console", "程序激活成功");
                    } else if (code == 1795) {
                        Log.e("console", "程序激活失败,激活的机器数量已达上限，授权码不能在更多的机器上使用");
                    } else if (code == 1793) {
                        Log.e("console", "程序激活失败,授权码已过期");
                    } else if (code == 276) {
                        Log.e("console", "程序激活失败,没有找到相应的本地授权许可数据文件");
                    } else if (code == 284) {
                        Log.e("console", "程序激活失败,授权码输入错误，请检查授权码拼写是否正确");
                    } else {
                        Log.e("console", "程序激活失败,错误码为：" + code);
                    }
                }
            }).start();

        }
    };

    public void init() {
        Intent actiIntent = new Intent(activity, ActivateService.class);
        activity.bindService(actiIntent, acConnection, Service.BIND_AUTO_CREATE);
    }

    public void spite(boolean s) {
        Intent intent = new Intent(activity, CameraActivity.class);
        intent.putExtra("camera", s);

        activity.startActivity(intent);
    }

    public void onDestroy() {
        if (acBinder != null) {
            activity.unbindService(acConnection);
        }
    }

}
