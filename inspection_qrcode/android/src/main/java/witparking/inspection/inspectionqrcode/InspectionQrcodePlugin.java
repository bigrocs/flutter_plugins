package witparking.inspection.inspectionqrcode;

import android.content.Intent;

import com.ypy.eventbus.EventBus;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import witparking.inspection.inspectionqrcode.zxing.android.CaptureActivity;

/**
 * InspectionQrcodePlugin
 */
public class InspectionQrcodePlugin implements MethodCallHandler {

    private static Registrar registrar;
    private Result scanResult;

    private InspectionQrcodePlugin() {
        EventBus.getDefault().register(this);
    }
    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {

        InspectionQrcodePlugin.registrar = registrar;

        final MethodChannel channel = new MethodChannel(registrar.messenger(), "inspection_qrcode");
        channel.setMethodCallHandler(new InspectionQrcodePlugin());
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        switch (call.method) {
            case "startScan":
                scanResult = result;
                Intent intent = new Intent(InspectionQrcodePlugin.registrar.activity(), CaptureActivity.class);
                InspectionQrcodePlugin.registrar.activity().startActivity(intent);
                break;
            default:
                break;
        }
    }

    /*
    * 扫码返回结果
    * */
    public void onEventMainThread(QRCodeEvent event) {
        if (event.str != null) {
            scanResult.success(event.str);
        }
    }
}
