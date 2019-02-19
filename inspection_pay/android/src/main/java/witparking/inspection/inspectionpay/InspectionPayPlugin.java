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

/**
 * InspectionPayPlugin
 */
public class InspectionPayPlugin implements MethodCallHandler {

  private static final String SCANTOPAY = "pay_scan";
  private static final String UNIONPAYSCAN = "pay_union_scan";

  private static final String WUWEIUNIONAPPID = "e7e157a1475e453ea82d17b4f9184551";

  private static Registrar registrar;

  /**
   * Plugin registration.
   */
  public static void registerWith(Registrar registrar) {
    InspectionPayPlugin.registrar = registrar;
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "inspection_pay");
    channel.setMethodCallHandler(new InspectionPayPlugin());
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {

    switch (call.method) {
      case UNIONPAYSCAN:
        unionPay(call, result);
        break;
      default:
        result.notImplemented();
        break;
    }
  }

  private void unionPay(MethodCall call, Result result) {

    int a = call.argument("appId");

    JSONObject transData = new JSONObject();
    try {
      transData.put("appId", WUWEIUNIONAPPID);
      transData.put("amt", 0.01);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    try {
      AppHelper.callTrans(InspectionPayPlugin.registrar.activity(), "POS 通", "扫一扫", transData);
      //AppHelper.callTrans(cordova.getActivity(), "银行卡收款", "消费", transData);
    } catch (Exception e) {
      result.success("未安装银联客户端");
      return;
    }
    //result.success("支付成功");
  }
}
