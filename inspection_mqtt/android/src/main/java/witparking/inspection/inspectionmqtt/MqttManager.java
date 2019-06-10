package witparking.inspection.inspectionmqtt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

import com.sonli.spush.codec.pole.ICodec;
import com.sonli.spush.codec.pole.PoleCodec;
import com.sonli.spush.codec.pole.Rule;
import com.witparking.encryption3des.utils3des;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.ypy.eventbus.EventBus;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by LYC on 2017/8/17.
 */

public class MqttManager {

    private int i = 1;

    private Handler handler;

    private MqttClient client;
    private String[] myTopics;
    private MqttConnectOptions options;

    private ScheduledExecutorService scheduler;
    private Context mContext;
    private utils3des mUtils3des;
    private static String appId;

    private static String ClientId;
    private static String mId;

    public static String clientIdSavePath;

    public ReceiveMessageInterface receiveMessageInterface;

    private static MqttManager mSingleton = null;

    public static MqttManager getInstance(Context mContext, String appId, ReceiveMessageInterface receiveMessageInterface) {
        if (mSingleton == null) {
            synchronized (MqttManager.class) {
                if (mSingleton == null) {
                    mSingleton = new MqttManager(mContext, appId, receiveMessageInterface);
                }
            }
        }
        return mSingleton;

    }

    @SuppressLint("HandlerLeak")
    private MqttManager(Context mContext, String appId, ReceiveMessageInterface receiveMessageInterface) {
        this.receiveMessageInterface = receiveMessageInterface;
        MqttManager.appId = appId;
        this.mContext = mContext;
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 2) {
                    Log.e("console", "连接成功");
                    try {
                        if (myTopics != null) {
                            for (String topic : myTopics) {
                                client.subscribe(topic, 2);
                                Log.e("console", "订阅=" + topic);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (msg.what == 3) {
                    Log.e("console", "连接失败，系统正在重连");
                }
            }
        };
    }


    public void startReconnect(final String mqttService, final String userName, final String passWord) {

        if (client != null && client.isConnected()) {
            try {
                for (String topic : myTopics) {
                    client.unsubscribe(topic);
                    client.disconnect();
                    client = null;
                }
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (myTopics == null) {
                    Log.e("console", "id未获取");
                    return;
                }
                if (client == null) {
                    String url = mqttService;
                    init(url, userName, passWord, MqttManager.mId);
                }
                if (!client.isConnected()) {
                    Log.e("console", "开始连接推送");
                    connect();
                } else {
                    Log.e("console", "推送连接中");
                }

            }
        }, 0 * 1000, 10 * 1000, TimeUnit.MILLISECONDS);
    }


    public void setTopics(String... myTopics) {
        this.myTopics = myTopics;
    }

    public void init(String host, String userName, String passWord, String name) {
        try {
            //host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
            client = new MqttClient(host, name,
                    new MemoryPersistence());
            //MQTT的连接设置
            options = new MqttConnectOptions();
            //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(false);

            //设置连接的用户名
            options.setUserName(userName);
            //设置连接的密码
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(20);

            //设置回调
            client.setCallback(new MqttCallback() {

                @Override
                public void connectionLost(Throwable cause) {
                    //连接丢失后，一般在这里面进行重连
                    Log.e("console", "connectionLost----------");
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //publish后会执行到这里
                    Log.e("console", "deliveryComplete---------"
                            + token.isComplete());
                }

                @Override
                public void messageArrived(String topicName, MqttMessage message)
                        throws Exception {
                    //subscribe后得到的消息会执行到这里面
                    Log.e("console", "messageArrived----------");
                    try {

                        if (codec == null) {
                            mUtils3des = new utils3des();
                            InputStream input = mContext.getAssets().open("inspectorRuleV1.json");
                            Rule rule = new Rule(input);
                            codec = new PoleCodec(rule);
                        }

                        Log.e("console", "message=" + printHexBinary(message.getPayload()));

                        String byteString = Base64.encodeToString(message.getPayload(), 2);

                        Log.e("console", "byteString=" + byteString);
                        byte[] decStr = mUtils3des.decToByte(byteString);
                        Log.e("console", "decStr=" + printHexBinary(decStr));

                        String[] decode1 = codec.decode(decStr);

                        Log.e("MQTT消息", decode1[2]);
                        if (receiveMessageInterface != null) {
                            receiveMessageInterface.onMessage(new SendMessageEvent(decode1[2]));
                        }
                    } catch (Exception e) {
                        Log.e("console", "解析异常");
                        e.printStackTrace();
                    }

                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = topicName + "---" + message.toString();
                    handler.sendMessage(msg);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ICodec codec;

    private void connect() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    client.connect(options);
                    Message msg = new Message();
                    msg.what = 2;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = 3;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    public void reStart() {
        try {
            scheduler.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //startReconnect();
    }

    public void onDestroy() {
        try {
            if (scheduler != null) scheduler.shutdown();
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    private static List<ClientIdCallback> listClientIdCallback = new ArrayList<ClientIdCallback>();

    public static void getClientId(final Context mContext, final ClientIdCallback clientIdCallback, final String serviceUrl) {

        String result = MqttManager.getClientIdByCooke(mContext);

        if (result != null) {
            try {
                JSONObject json = new JSONObject(result);
                String id = json.optString("id");
                String name = json.optString("mId");
                MqttManager.ClientId = id;
                MqttManager.mId = name;
                clientIdCallback.callBack(id, name);
                return;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        AndPermission
                .with(mContext)
                .requestCode(100)
                .permission(Manifest.permission.READ_PHONE_STATE)
                .callback(new PermissionListener() {
                    @Override
                    public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                HttpURLConnection connection = null;
                                try {

                                    StringBuffer urls = new StringBuffer();

                                    urls.append(serviceUrl + "&mac="+getCCID(mContext)+"&clientType=0");

                                    URL url = new URL(urls.toString());
                                    Log.e("console", "url:" + url.toString());
                                    connection = (HttpURLConnection) url.openConnection();
                                    // 设置请求方法，默认是GET
                                    connection.setRequestMethod("POST");
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

                                            JSONObject jsonObject = new JSONObject(message);
                                            JSONObject reuturnObject = jsonObject.optJSONObject("returnObject");
                                            if (jsonObject.optInt("code") == 30 && reuturnObject != null) {
                                                String id = reuturnObject.optString("id");
                                                String name = reuturnObject.optString("mId");
                                                MqttManager.ClientId = id;
                                                MqttManager.mId = name;
                                                clientIdCallback.callBack(id, name);
                                                MqttManager.SaveClientIdToCooke(mContext, reuturnObject.toString());
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
                    @Override
                    public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {

                    }
                }).start();

    }

    public interface ClientIdCallback {
        public void callBack(String id, String name);
    }

    private static String getClientIdByCooke(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("mqtt", Context.MODE_PRIVATE);
        String clientId = sharedPreferences.getString("clientId", null);
        if (clientId != null && clientId.length() > 0) {
            return clientId;
        }
        String str = FileSaveUtil.readSDcard(mContext, clientIdSavePath);
        if (str == null || str.length() == 0) {
            return null;
        }
        return str;
    }

    private static void SaveClientIdToCooke(Context mContext, String str) {
        FileSaveUtil.writeSDcard(mContext, str, clientIdSavePath);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("mqtt", Context.MODE_PRIVATE); //私有数据
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putString("clientId", str);
        editor.commit();//提交修改
    }

    public static String printHexBinary(byte[] data) {

        char[] hexCode = "0123456789ABCDEF".toCharArray();
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }

    public static String getCCID(Context mContext){

        TelephonyManager TelephonyMgr = (TelephonyManager)mContext.getSystemService(TELEPHONY_SERVICE);
        String m_szImei = TelephonyMgr.getDeviceId();

        String m_szDevIDShort = "35" + //we make this look like a valid IMEI

                Build.BOARD.length()%10 +
                Build.BRAND.length()%10 +
                Build.CPU_ABI.length()%10 +
                Build.DEVICE.length()%10 +
                Build.DISPLAY.length()%10 +
                Build.HOST.length()%10 +
                Build.ID.length()%10 +
                Build.MANUFACTURER.length()%10 +
                Build.MODEL.length()%10 +
                Build.PRODUCT.length()%10 +
                Build.TAGS.length()%10 +
                Build.TYPE.length()%10 +
                Build.USER.length()%10 ; //13 digits



        String m_szAndroidID = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);

        WifiManager wm = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
        String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();

        BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth adapter
        m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String m_szBTMAC = m_BluetoothAdapter.getAddress();

        String m_szLongID = m_szImei + m_szDevIDShort
                + m_szAndroidID+ m_szWLANMAC + m_szBTMAC+mContext.getPackageName();
        // compute md5
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.update(m_szLongID.getBytes(),0,m_szLongID.length());
        // get md5 bytes
        byte p_md5Data[] = m.digest();
        // create a hex string
        String m_szUniqueID = new String();
        for (int i=0;i<p_md5Data.length;i++) {
            int b =  (0xFF & p_md5Data[i]);
        // if it is a single digit, make sure it have 0 in front (proper padding)
            if (b <= 0xF)
                m_szUniqueID+="0";
        // add number to string
            m_szUniqueID+=Integer.toHexString(b);
        }   // hex string to uppercase
        m_szUniqueID = m_szUniqueID.toUpperCase();
        return m_szUniqueID;
    }

    public interface ReceiveMessageInterface {
        void onMessage(SendMessageEvent event);
    }
}
