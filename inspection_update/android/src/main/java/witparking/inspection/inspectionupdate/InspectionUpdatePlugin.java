package witparking.inspection.inspectionupdate;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.Date;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** InspectionUpdatePlugin */
public class InspectionUpdatePlugin implements MethodCallHandler {

  static Registrar registrar;

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {

    InspectionUpdatePlugin.registrar = registrar;

    final MethodChannel channel = new MethodChannel(registrar.messenger(), "inspection_update");
    channel.setMethodCallHandler(new InspectionUpdatePlugin());
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    /*
    * 更新APP
    * */
    if (call.method.equals("update")) {
      String url = call.argument("url");
      AppUpdateUtils appUpdateUtils = new AppUpdateUtils(InspectionUpdatePlugin.registrar.activity());
      //String fileName = "WP" + new Date().getTime() + ".apk";
      String fileName = "巡检端.apk";
      appUpdateUtils.downloadAppWithUrl(url, fileName);
    } else {
      result.notImplemented();
    }
  }
}
