package witparking.inspection.inspectionmap;

import android.app.Activity;
import android.util.Log;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.IListener;
import com.baidu.trace.Trace;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.model.LocationMode;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.ProtocolType;
import com.baidu.trace.model.PushMessage;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class LBSTrace {

    LBSTraceClient client;
    Trace trace;
    Activity activity;
    //鹰眼服务ID
    long serviceId = 108674;
    //entity标识
    String entityName;

    public void init(Activity activity) {
        this.activity = activity;
        client = new LBSTraceClient(activity.getApplicationContext());
    }

    /*
    * 鹰眼轨迹服务监听器
    * */
    private final OnTraceListener mTraceListener = new OnTraceListener() {

        @Override
        public void onBindServiceCallback(int i, String s) {

        }

        // 开启服务回调
        @Override
        public void onStartTraceCallback(int status, String message) {
            client.startGather(mTraceListener);
        }

        // 停止服务回调
        @Override
        public void onStopTraceCallback(int status, String message) {
        }

        // 开启采集回调
        @Override
        public void onStartGatherCallback(int status, String message) {
        }

        // 停止采集回调
        @Override
        public void onStopGatherCallback(int status, String message) {
        }

        // 推送回调
        @Override
        public void onPushCallback(byte messageNo, PushMessage message) {
        }

        @Override
        public void onInitBOSCallback(int i, String s) {

        }
    };

    public void start(String entity) {
        // 采集周期
        int gatherInterval = 10;
        // 打包周期
        int packInterval = 60;
        // http协议类型
        int protocolType = 1;
        // 设置采集和打包周期
        client.setInterval(gatherInterval, packInterval);
        // 设置定位模式
        client.setLocationMode(LocationMode.High_Accuracy);
        // 设置http协议类型
        client.setProtocolType(ProtocolType.HTTP);
        //鹰眼服务ID
        serviceId = 108674;
        //entity标识
        entityName = entity;
        Log.e("console鹰眼标识", entity);
        //轨迹服务类型（0 : 不上传位置数据，也不接收报警信息； 1 : 不上传位置数据，但接收报警信息；2 : 上传位置数据，且接收报警信息）
        int traceType = 2;
        //实例化轨迹服务
        trace = new Trace(serviceId, entityName, false);
        //开启轨迹服务
        client.startTrace(trace, mTraceListener);
    }

    public void stop() {
        client.stopGather(mTraceListener);
        client.stopTrace(trace, mTraceListener);
    }

}
