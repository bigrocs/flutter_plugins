package witparking.inspection.inspectionupdate;


import android.Manifest;
import android.support.annotation.NonNull;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.ypy.eventbus.EventBus;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.view.FlutterNativeView;

/** InspectionUpdatePlugin */
public class InspectionUpdatePlugin implements MethodCallHandler {

  private static Registrar registrar;

  private static EventChannel.EventSink eventSink;
  private Result updateResult;


  private InspectionUpdatePlugin() {
    
    EventBus.getDefault().register(InspectionUpdatePlugin.this);
    
    InspectionUpdatePlugin.registrar.addViewDestroyListener(new PluginRegistry.ViewDestroyListener() {
      @Override
      public boolean onViewDestroy(FlutterNativeView flutterNativeView) {
        EventBus.getDefault().unregister(InspectionUpdatePlugin.this);
        return false;
      }
    });
    
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
      final String url = call.argument("url");
      final AppUpdateUtils appUpdateUtils = new AppUpdateUtils(InspectionUpdatePlugin.registrar.activity());
      final String fileName = "巡检端.apk";
      AndPermission
              .with(InspectionUpdatePlugin.registrar.activity())
              .requestCode(100)
              .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
              .callback(new PermissionListener() {
                @Override
                public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                  appUpdateUtils.downloadAppWithUrl(url, fileName);
                }
                @Override
                public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {

                }
              }).start();
    } else {
      result.notImplemented();
    }
  }

  public void onEvent(AppUpdateEvent event) {
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
