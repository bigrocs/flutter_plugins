package witparking.inspection.inspectionmap;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * InspectionMapPlugin
 */
public class InspectionMapPlugin implements MethodCallHandler {

    private static Registrar registrar;
    private static EventChannel.EventSink eventSink;

    private LBSTrace lbsTrace;

    private InspectionMapPlugin() {
        lbsTrace = new LBSTrace();
        lbsTrace.init(InspectionMapPlugin.registrar.activity());
    };

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {

        InspectionMapPlugin.registrar = registrar;

        final MethodChannel channel = new MethodChannel(registrar.messenger(), "inspection_map");
        channel.setMethodCallHandler(new InspectionMapPlugin());

        final EventChannel message_channel = new EventChannel(registrar.messenger(), "map.event.location");
        message_channel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink eventSink) {
                InspectionMapPlugin.eventSink = eventSink;
            }

            @Override
            public void onCancel(Object o) {
                InspectionMapPlugin.eventSink = null;
            }
        });

    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {

        switch (call.method) {
            case "startGather":
                startGather(call, result);
                break;
            case "stopGather":
                break;
            default:
                result.notImplemented();
                break;
        }

    }

    /*
    * 开启鹰眼轨迹
    * */
    void startGather(MethodCall call, Result result) {
        lbsTrace.start((String) call.argument("entity"));
    }
}
