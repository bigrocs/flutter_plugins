package witparking.inspection.inspectionpay;

import org.json.JSONException;
import org.json.JSONObject;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** InspectionPayPlugin */
public class InspectionPayPlugin implements MethodCallHandler {

  private static final String SCANTOPAY = "pay_scan";
  private static final String UNIONPAY = "pay_union";

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "inspection_pay");
    channel.setMethodCallHandler(new InspectionPayPlugin());
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {

    if (call.method.equals(UNIONPAY)) {

      JSONObject jsonObject = new JSONObject();
      try {
        jsonObject.put("test", "AAAA");
      } catch (JSONException e) {
        e.printStackTrace();
      }

      result.success(jsonObject.toString());

    } else {
      result.notImplemented();
    }
  }
}
