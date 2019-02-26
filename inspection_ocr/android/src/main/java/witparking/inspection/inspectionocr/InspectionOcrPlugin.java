package witparking.inspection.inspectionocr;

import com.ypy.eventbus.EventBus;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * InspectionOcrPlugin
 */
public class InspectionOcrPlugin implements MethodCallHandler {

    private static Registrar registrar;
    private SpiteUtil spiteUtil;
    private Result recognitionResult;

    private InspectionOcrPlugin() {
        EventBus.getDefault().register(this);
        spiteUtil = new SpiteUtil(InspectionOcrPlugin.registrar.activity());
        spiteUtil.init();
    }

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {

        InspectionOcrPlugin.registrar = registrar;

        final MethodChannel channel = new MethodChannel(registrar.messenger(), "inspection_ocr");
        channel.setMethodCallHandler(new InspectionOcrPlugin());
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        switch (call.method) {
            case "recognitionTheplate":
                recognitionResult = result;
                spiteUtil.spite(true);
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    public void onEventMainThread(SpiteBackEvent event) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("number", event.number);
        map.put("color", event.color);
        map.put("path", event.path);
        recognitionResult.success(map);
    }
}
