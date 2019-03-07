package witparking.inspection.inspectionmap;


import com.baidu.mapapi.map.MapView;

import java.util.HashMap;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.common.StandardMessageCodec;

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
    public static void registerWith(final Registrar registrar) {

        InspectionMapPlugin.registrar = registrar;

        final MethodChannel channel = new MethodChannel(registrar.messenger(), "inspection_map");
        channel.setMethodCallHandler(new InspectionMapPlugin());

        final EventChannel message_channel = new EventChannel(registrar.messenger(), "map.event.location");
        message_channel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, final EventChannel.EventSink eventSink) {
                InspectionMapPlugin.eventSink = eventSink;

                LBSLocation lbsLocation = new LBSLocation(registrar.activity());
                lbsLocation.start(new LocationInterface() {
                    @Override
                    public void onLocationUpdate(HashMap res) {
                        eventSink.success(res);
                    }
                });
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
                lbsTrace.stop();
                break;
            case "getUserLocation":
                getUserLocation(result);
                break;
            case "showMapView":
                registrar.platformViewRegistry().registerViewFactory("MapView", new BMapViewFactory(new StandardMessageCodec(), new SIMapView(registrar.activity())));
                break;
            case "placePin":
                break;
            default:
                result.notImplemented();
                break;
        }

    }

    /*
     * 开启鹰眼轨迹
     * */
    private void startGather(MethodCall call, Result result) {
        lbsTrace.start((String) call.argument("entity"));
        result.success("成功开启鹰眼轨迹");
    }

    /*
    * 获取用户位置信息
    * */
    private void getUserLocation(final Result result) {

        LBSLocation lbsLocation = new LBSLocation(registrar.activity());
        lbsLocation.onceGet = true;
        lbsLocation.start(new LocationInterface() {
            @Override
            public void onLocationUpdate(HashMap res) {
                result.success(res);
            }
        });
    }

}
