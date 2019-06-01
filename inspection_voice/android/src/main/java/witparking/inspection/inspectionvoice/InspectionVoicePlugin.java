package witparking.inspection.inspectionvoice;

import android.Manifest;
import android.support.annotation.NonNull;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.ypy.eventbus.EventBus;

import java.util.List;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.view.FlutterNativeView;

/**
 * InspectionVoicePlugin
 */
public class InspectionVoicePlugin implements MethodCallHandler {

    private Registrar registrar;
    private Speech speech;
    private Result result;
    private TTSUtil ttsUtil;

    private InspectionVoicePlugin(Registrar registrar) {

        this.registrar = registrar;
        
        speech = new Speech(registrar.activity());
        //ttsUtil = TTSUtil.getInstance(InspectionVoicePlugin.registrar.activity());
        EventBus.getDefault().register(InspectionVoicePlugin.this);

        registrar.addViewDestroyListener(new PluginRegistry.ViewDestroyListener() {
            @Override
            public boolean onViewDestroy(FlutterNativeView flutterNativeView) {
                EventBus.getDefault().unregister(InspectionVoicePlugin.this);
                return false;
            }
        });
    }

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "inspection_voice");
        channel.setMethodCallHandler(new InspectionVoicePlugin(registrar));
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        switch (call.method) {
            case "speechSynthesis":
                speechSynthesis(call);
                break;
            case "startSpeechInput":
                startSpeechInput();
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
        if (ttsUtil == null) {
            ttsUtil = TTSUtil.getInstance(registrar.activity());
        }
        String content = (String) call.argument("words");
        if (content != null) {
            ttsUtil.add(content);
        }
    }

    public void onEvent(VoiceInputEvent event) {
        if (event.message != null) {
            result.success(event.message);
        }
    }

    /*
    * 开始语音播报
    * */
    private void startSpeechInput() {
        AndPermission
                .with(registrar.activity())
                .requestCode(100)
                .permission(Manifest.permission.RECORD_AUDIO)
                .callback(new PermissionListener() {
                    @Override
                    public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                        speech.startASR();
                    }
                    @Override
                    public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {

                    }
                }).start();
    }
}
