package witparking.inspection.inspectionvoice;

import com.ypy.eventbus.EventBus;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * InspectionVoicePlugin
 */
public class InspectionVoicePlugin implements MethodCallHandler {

    private static Registrar registrar;
    private Speech speech;
    Result result;

    private InspectionVoicePlugin() {
        speech = new Speech(InspectionVoicePlugin.registrar.activity());
        EventBus.getDefault().register(this);
    }

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        InspectionVoicePlugin.registrar = registrar;
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "inspection_voice");
        channel.setMethodCallHandler(new InspectionVoicePlugin());
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        switch (call.method) {
            case "speechSynthesis":
                speechSynthesis(call);
                break;
            case "startSpeechInput":
                speech.startASR();
                break;
            case "stopSpeechInput":
                this.result = result;
                speech.stopASR();
                break;
            default:
                break;
        }
    }

    /*
    * 语音播报
    * */
    private void speechSynthesis(MethodCall call) {
        TTSUtil ttsUtil = new TTSUtil(InspectionVoicePlugin.registrar.activity());
        ttsUtil.add((String) call.argument("words"));
    }

    public void onEvent(VoiceInputEvent event) {
        if (event.message != null) {
            result.success(event.message);
        }
    }
}
