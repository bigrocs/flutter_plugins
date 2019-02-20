package witparking.inspection.inspectionmqtt;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * InspectionMqttPlugin
 */
public class InspectionMqttPlugin implements MethodCallHandler {

    private static Registrar registrar;

    private MqttManager mqttManager;

    private InspectionMqttPlugin() {
        mqttManager = new MqttManager(InspectionMqttPlugin.registrar.activity(), "DD8F9EFA1BAC44D9B3B583BC00BE805D");
    }

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        InspectionMqttPlugin.registrar = registrar;
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "inspection_mqtt");
        channel.setMethodCallHandler(new InspectionMqttPlugin());
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

        MqttManager.getClientId(InspectionMqttPlugin.registrar.activity(), new MqttManager.ClientIdCallback() {
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
}
