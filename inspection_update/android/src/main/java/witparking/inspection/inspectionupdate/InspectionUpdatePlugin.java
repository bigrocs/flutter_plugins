package witparking.inspection.inspectionupdate;


import com.ypy.eventbus.EventBus;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** InspectionUpdatePlugin */
public class InspectionUpdatePlugin implements MethodCallHandler {

  static Registrar registrar;

  private static EventChannel.EventSink eventSink;
  private Result updateResult;


  InspectionUpdatePlugin() {
    EventBus.getDefault().register(this);
  }

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {

    InspectionUpdatePlugin.registrar = registrar;

    final MethodChannel channel = new MethodChannel(registrar.messenger(), "inspection_update");
    channel.setMethodCallHandler(new InspectionUpdatePlugin());

    final EventChannel message_channel = new EventChannel(registrar.messenger(), "update.event.progress");
    message_channel.setStreamHandler(new EventChannel.StreamHandler() {
      @Override
      public void onListen(Object o, EventChannel.EventSink eventSink) {
        InspectionUpdatePlugin.eventSink = eventSink;
      }

      @Override
      public void onCancel(Object o) {
        InspectionUpdatePlugin.eventSink = null;
      }
    });
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    /*
    * 更新APP
    * */
    if (call.method.equals("update")) {
      updateResult = result;
      String url = call.argument("url");
      AppUpdateUtils appUpdateUtils = new AppUpdateUtils(InspectionUpdatePlugin.registrar.activity());
      String fileName = "巡检端.apk";
      appUpdateUtils.downloadAppWithUrl(url, fileName);

    } else {
      result.notImplemented();
    }
  }

  public void onEventMain(AppUpdateEvent event) {
    if (event.error.equals("")) {
      Map map = new HashMap();
      map.put("schedule", event.progress);
      map.put("carryOut", event.complete);
      if (InspectionUpdatePlugin.eventSink != null) {
        InspectionUpdatePlugin.eventSink.success(map);
      }
      if (event.progress >= 100) {
        updateResult.success("done");
      }
    }else
    {
      if (InspectionUpdatePlugin.eventSink != null) {
        InspectionUpdatePlugin.eventSink.error("0", event.error, event.error);
      }
    }
  }
}
