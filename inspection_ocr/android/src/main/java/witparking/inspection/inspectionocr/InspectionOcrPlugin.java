package witparking.inspection.inspectionocr;

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

    private InspectionOcrPlugin() {
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
                spiteUtil.spite(false);
                break;
            default:
                result.notImplemented();
                break;
        }
    }
}
