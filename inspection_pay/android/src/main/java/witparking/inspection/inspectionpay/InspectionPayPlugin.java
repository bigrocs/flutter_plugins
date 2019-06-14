package witparking.inspection.inspectionpay;

import android.content.Intent;
import android.os.Bundle;
import org.json.JSONException;
import org.json.JSONObject;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import com.ums.AppHelper;

/**
 * InspectionPayPlugin
 */
public class InspectionPayPlugin implements MethodCallHandler {

  private final String UNIONPAYSCAN = "pay_union_scan";

  private Registrar registrar;

  private Result unionPayResult;

  private InspectionPayPlugin(Registrar registrar) {
    this.registrar = registrar;
    registrar.addActivityResultListener(new PluginRegistry.ActivityResultListener() {
      @Override
      public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 1000) {
          Bundle bundle = intent.getExtras();
          assert bundle != null;
          String result = bundle.getString("result");
          unionPayResult.success(result);
        }
        return false;
      }
    });
  };

  /**
   * Plugin registration.
   */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "inspection_pay");
    channel.setMethodCallHandler(new InspectionPayPlugin(registrar));
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {

    switch (call.method) {
      case UNIONPAYSCAN:
        unionPay(call, result);
        break;
      case "":
        result.notImplemented();
        break;
      default:
        result.notImplemented();
        break;
    }
  }

  private void unionPay(MethodCall call, Result result) {

    unionPayResult = result;

    JSONObject transData = new JSONObject();
    try {
      transData.put("appId", call.argument("appId"));
      transData.put("amt", call.argument("Amount"));
      transData.put("isNeedPrintReceipt", true);

      String transType = call.argument("TransType");

      assert transType != null;
      if(transType.equals("2")){
        transData.put("extOrderNo", call.argument("outTradeNo"));
        transData.put("extBillNo", call.argument("outTradeNo"));
        AppHelper.callTrans(registrar.activity(), "银行卡收款", "消费", transData);
      }else if(transType.equals("110")){
        transData.put("extOrderNo", call.argument("outTradeNo"));
        transData.put("extBillNo", call.argument("outTradeNo"));
        AppHelper.callTrans(registrar.activity(), "POS 通", "扫一扫", transData);
      }else{
        AppHelper.callTrans(registrar.activity(), "公共资源", "签到",transData);
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

}
