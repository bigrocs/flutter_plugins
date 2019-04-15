package witparking.inspection.inspectionmap;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
import java.util.HashMap;
import java.util.Map;
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

    TraceInterface traceInterface;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");

    void init(Activity activity) {
        this.activity = activity;
        client = new LBSTraceClient(activity.getApplicationContext());
    }

    /*
     * 鹰眼轨迹服务监听器
     * */
    private final OnTraceListener mTraceListener = new OnTraceListener() {

        @Override
        public void onBindServiceCallback(int i, String s) {
            Log.e("", "");
        }

        // 开启服务回调
        @Override
        public void onStartTraceCallback(int status, String message) {
            client.startGather(mTraceListener);
        }

        // 停止服务回调
        @Override
        public void onStopTraceCallback(int status, String message) {
            Log.e("", "");
        }

        // 开启采集回调
        @Override
        public void onStartGatherCallback(int status, String message) {
            Log.e("", "");
        }

        // 停止采集回调
        @Override
        public void onStopGatherCallback(int status, String message) {
            Log.e("", "");
        }

        // 推送回调
        @Override
        public void onPushCallback(byte messageNo, PushMessage message) {
            Log.e("", "");
        }

        @Override
        public void onInitBOSCallback(int i, String s) {
            Log.e("", "");
        }
    };

    void start(String entity, TraceInterface traceInterface) {

        this.traceInterface = traceInterface;

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
        //轨迹服务类型（0 : 不上传位置数据，也不接收报警信息； 1 : 不上传位置数据，但接收报警信息；2 : 上传位置数据，且接收报警信息）
        int traceType = 2;
        //实例化轨迹服务
        trace = new Trace(serviceId, entityName, false);
        //开启轨迹服务
        client.startTrace(trace, mTraceListener);

        initHistroyTrack();
        initLastTime();
    }

    void stop() {
        client.stopGather(mTraceListener);
        client.stopTrace(trace, mTraceListener);
        stopHistroy();
    }

    private Timer timer;
    private TimerTask task;

    private void initHistroyTrack() {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                Log.e("console鹰眼", "开始查询");
                Date date = new Date();
                getHistoryByUrl(lastTime, date.getTime() / 1000);

            }
        };
        int delay = 1000 * 60 * 10;
        delay = 3 * 1000;
        timer.schedule(task, delay, delay);
    }

    private void stopHistroy(){
        try{
            timer.cancel();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void getHistoryByUrl(final long starttime, final long endtime) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String address = "";
                HttpURLConnection connection = null;
                try {

                    String urls = "http://api.map.baidu.com/trace/v2/track/gethistory?ak=pOQMoT522zMAAG5mLE6Me26w" +
                            "&service_id=" + serviceId +
                            "&start_time=" + starttime +
                            "&end_time=" + endtime +
                            "&entity_name=" + entityName +
                            "&page_index=" + 1 +
                            "&page_size=" + 3000 +
                            "&is_processed=" + 1 +
                            "&process_option=" + "need_denoise=1,radius_threshold=0,need_vacuate=1,need_mapmatch=1,radius_threhold=0,transport_mode=walking" +
                            "&mcode=" + "5B:BA:78:D8:3A:78:75:BE:CD:F5:9D:E1:3F:49:4C:6B:A3:B1:58:4F;com.rpms.sdmandroid";
                    URL url = new URL(urls);
                    Log.e("console", "url:" + url.toString());
                    connection = (HttpURLConnection) url.openConnection();
                    // 设置请求方法，默认是GET
                    connection.setRequestMethod("GET");
                    // 设置字符集
                    connection.setRequestProperty("Charset", "UTF-8");
                    // 设置文件类型
                    connection.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
                    // 设置请求参数，可通过Servlet的getHeader()获取
                    connection.setRequestProperty("Cookie", "AppName=" + URLEncoder.encode("你好", "UTF-8"));
                    // 设置自定义参数
                    connection.setRequestProperty("MyProperty", "this is me!");

                    if (connection.getResponseCode() == 200) {
                        Log.e("console", "connection.getResponseCode() == 200");
                        InputStream is = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                        Log.e("console", sb.toString());
                        is.close();
                        try {
                            String message = sb.toString();
                            Log.e("鹰眼", message);

                            Map res = new HashMap();

                            JSONObject jsonObject = new JSONObject(message);

                            res.put("distance", jsonObject.getDouble("distance"));
                            res.put("timeStart", jsonObject.getJSONObject("start_point").getLong("loc_time") * 1000);
                            res.put("timeEnd", jsonObject.getJSONObject("end_point").getLong("loc_time") * 1000);

                            JSONArray jsonArray = jsonObject.getJSONArray("points");
                            JSONArray UpBody = new JSONArray();
                            if (jsonArray.length() == 0) {
                                Log.e("console鹰眼", "位置站暂时没有上传");
                                return;
                            }
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject item = jsonArray.getJSONObject(i);
                                JSONObject UpBodyItem = new JSONObject();
                                UpBodyItem.put("coord_type", 3);
                                UpBodyItem.put("isOpenGps", 1);
                                UpBodyItem.put("isUpLoadTrace", 0);
                                UpBodyItem.put("primaryKeyId", 1);
                                //
                                UpBodyItem.put("createDate", format.parse(item.getString("create_time")).getTime());
                                UpBodyItem.put("entity_name", entityName);
                                UpBodyItem.put("latitude", item.getJSONArray("location").getDouble(1));
                                UpBodyItem.put("longitude", item.getJSONArray("location").getDouble(0));
                                UpBodyItem.put("loc_time", item.getLong("loc_time"));
                                UpBodyItem.put("modifyDate", item.getLong("loc_time") * 1000);
                                UpBody.put(UpBodyItem);
                            }
                            res.put("upBody", UpBody.toString());
                            saveLastTime(endtime + 1);

                            if (traceInterface != null) {
                               traceInterface.onRes(res);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("console", e.getMessage());
                        }
                    } else {
                        Log.e("console", connection.getResponseCode() + "");
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }

                }
            }
        }).start();

    }

    long lastTime = 0;

    private void initLastTime() {
        long time = getSharedPreLong(activity, entityName);
        if (time == 0) {
            lastTime = new Date().getTime() / 1000;
        } else {
            Date date = new Date();
            date.setTime(lastTime * 1000);
            String lastStr = formatDate.format(date);
            String thisTime = formatDate.format(new Date());
            if (!thisTime.equals(lastStr)) {
                lastTime = new Date().getTime() / 1000;
            } else {
                lastTime = time;
            }
        }
        saveLastTime(lastTime);
    }

    public void saveLastTime(long lasttime) {
        this.lastTime = lasttime;
        saveSharedPreLong(activity, entityName, lasttime);
    }

    public static long getSharedPreLong(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return settings.getLong(key, 0);
    }

    public static void saveSharedPreLong(Context context, String key, long value) {
        SharedPreferences settings = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        settings.edit().putLong(key, value).commit();
    }
}
