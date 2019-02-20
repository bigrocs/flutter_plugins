package witparking.inspection.inspectionmqtt;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.sonli.spush.codec.pole.ICodec;
import com.sonli.spush.codec.pole.PoleCodec;
import com.sonli.spush.codec.pole.Rule;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("all")
public class MqttUtil {

  private TextView resultTv;

  private String host = "tcp://172.16.100.114:1883";
  private String userName = "sonliInspector";
  private String passWord = "mN8HbSVTKOzUJ4M3";
  private int i = 1;

  private Handler handler;

  private MqttClient client;

  private String myTopic = "inspector/down/2";

  private MqttConnectOptions options;

  private ScheduledExecutorService scheduler;
  private Context mContext;
  public MqttUtil(Context mContext){
    this.mContext=mContext;
    handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.what == 1) {

//          NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//          Notification notification = new Notification(R.mipmap.ic_launcher, "Mqtt即时推送", System.currentTimeMillis());
//          notification.contentView = new RemoteViews("com.hxht.testmqttclient", R.layout.activity_notification);
//          notification.contentView.setTextViewText(R.id.tv_desc, (String) msg.obj);
//          notification.defaults = Notification.DEFAULT_SOUND;
//          notification.flags = Notification.FLAG_AUTO_CANCEL;
//          manager.notify(i++, notification);

        } else if (msg.what == 2) {
          Log.e("console","连接成功");
          try {
            client.subscribe(myTopic, 2);
          } catch (Exception e) {
            e.printStackTrace();
          }
        } else if (msg.what == 3) {
          Log.e("console","连接失败，系统正在重连");
        }
      }
    };
  }



  private void startReconnect() {
    scheduler = Executors.newSingleThreadScheduledExecutor();
    scheduler.scheduleAtFixedRate(new Runnable() {

      @Override
      public void run() {
        if (!client.isConnected()) {
          connect();
        }
      }
    }, 0 * 1000, 10 * 1000, TimeUnit.MILLISECONDS);
  }

  private void init() {
    try {
      //host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
      client = new MqttClient(host, "ParkingSpaceStatusV3",
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
          Log.e("console","connectionLost----------");
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
          //publish后会执行到这里
          Log.e("console","deliveryComplete---------"
            + token.isComplete());
        }

        @Override
        public void messageArrived(String topicName, MqttMessage message)
          throws Exception {
          //subscribe后得到的消息会执行到这里面
          Log.e("console","messageArrived----------");
          try{


//            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("inspectorRuleV1.json"), "UTF-8")); // 实例化输入流，并获取网页代码
//            String s; // 依次循环，至到读的值为空
//            StringBuilder sb = new StringBuilder();
//            while ((s = reader.readLine()) != null) {
//              sb.append(s);
//            }
//            reader.close();
//
//            String str = sb.toString();
//            Log.e("console",str);
            if(codec==null){
              Log.e("console","input开始");
              InputStream input= mContext.getAssets().open("inspectorRuleV1.json");
              Log.e("console","input结束");
              Rule rule= new Rule(input);
              Log.e("console","Rule");
              codec = new PoleCodec(rule);
              Log.e("console","codec");
            }

            String[] decode1 = codec.decode(message.getPayload());

            for(int i=0;i<decode1.length;i++){
              Log.e("console",decode1[i]);
            }
          }catch (Exception e){
            Log.e("console","解析异常");
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


//  public boolean onKeyDown(int keyCode, KeyEvent event) {
////    if (client != null && keyCode == KeyEvent.KEYCODE_BACK) {
////      try {
////        client.disconnect();
////      } catch (Exception e) {
////        e.printStackTrace();
////      }
////    }
//  }


  protected void onDestroy() {
    try {
      scheduler.shutdown();
      client.disconnect();
    } catch (MqttException e) {
      e.printStackTrace();
    }
  }
}

