package witparking.inspection.inspectionmqtt;

import android.util.Log;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.view.FlutterNativeView;

/**
 * InspectionMqttPlugin
 */
public class InspectionMqttPlugin implements MethodCallHandler {

    private Registrar registrar;
    private static EventChannel.EventSink eventSink;

    private MqttManager mqttManager;

    private InspectionMqttPlugin(Registrar registrar) {

        this.registrar = registrar;

        mqttManager = MqttManager.getInstance(registrar.activity(), "DD8F9EFA1BAC44D9B3B583BC00BE805D", new MqttManager.ReceiveMessageInterface() {
            @Override
            public void onMessage(SendMessageEvent event) {
                if (InspectionMqttPlugin.eventSink != null) {
                    InspectionMqttPlugin.eventSink.success(event.message);
                }
            }
        });

        registrar.addViewDestroyListener(new PluginRegistry.ViewDestroyListener() {
            @Override
            public boolean onViewDestroy(FlutterNativeView flutterNativeView) {

                if (mqttManager != null) {
                    mqttManager.onDestroy();
                }

                return false;
            }
        });
    }

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {

        final MethodChannel channel = new MethodChannel(registrar.messenger(), "inspection_mqtt");
        channel.setMethodCallHandler(new InspectionMqttPlugin(registrar));

        /*
        * 消息上报通道
        * */
        final EventChannel message_channel = new EventChannel(registrar.messenger(), "mqtt.event.message");
        message_channel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink eventSink) {
                InspectionMqttPlugin.eventSink = eventSink;
            }

            @Override
            public void onCancel(Object o) {
                InspectionMqttPlugin.eventSink = null;
            }
        });
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        switch (call.method) {
            case "get_mqtt_client_id":
                getClientID(call, result);
                break;
            default:
                break;
        }
    }

    /*
    * 获取clientid
    * 开启mqtt推送
    * */
    private void getClientID(final MethodCall call, final Result result) {

        MqttManager.clientIdSavePath = call.argument("clientid_save_file_path");
        MqttManager.getClientId(registrar.activity(), new MqttManager.ClientIdCallback() {
            @Override
            public void callBack(String clentid, String name) {
                if (clentid != null) {
                    result.success(clentid);
                    mqttManager.setTopics(call.argument("topics") + clentid);
                    mqttManager.startReconnect((String) call.argument("mqtt_service_url"), (String) call.argument("userName"), (String) call.argument("passWord"));
                } else {
                    result.success(null);
                }
            }
        }, (String) call.argument("clientid_service_url"));

    }

    /*
     * 处理MQTT接收到的消息
     * event_bus通知
     * */
    public void onEventMainThread(SendMessageEvent event) {

    }

}
