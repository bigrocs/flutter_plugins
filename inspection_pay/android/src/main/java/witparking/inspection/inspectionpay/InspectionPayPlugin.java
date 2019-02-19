package witparking.inspection.inspectionpay;

import android.content.pm.ApplicationInfo;

import org.json.JSONException;
import org.json.JSONObject;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import com.ums.AppHelper;

/** InspectionPayPlugin */
public class InspectionPayPlugin implements MethodCallHandler {

  private static final String SCANTOPAY = "pay_scan";
  private static final String UNIONPAY = "pay_union";

  private static final String WUWEIUNIONAPPID = "e7e157a1475e453ea82d17b4f9184551";

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

      JSONObject transData = new JSONObject();
      try {
        transData.put("appId", WUWEIUNIONAPPID);
        transData.put("amt", 0.01);
      } catch (JSONException e) {
        e.printStackTrace();
      }

      AppHelper.callTrans(null, "POS 通", "扫一扫", transData);
      //AppHelper.callTrans(cordova.getActivity(), "银行卡收款", "消费", transData);

      result.success("--------");

    } else {
      result.notImplemented();
    }
  }
}
