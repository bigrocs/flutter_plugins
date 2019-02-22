import 'dart:async';
import 'package:flutter/services.dart';

class InspectionMap {

  static const MethodChannel _channel =
      const MethodChannel('inspection_map');

  static const EventChannel _stream = const EventChannel('map.event.location');
  /*
  * 开启百度鹰眼轨迹
  * */
  static Future startGather({dynamic param}) async {
    dynamic res = await _channel.invokeMethod('startGather', param);
    return res;
  }

  /*
  * 关闭百度鹰眼轨迹
  * */
  static Future stopGather() async {
    dynamic res = await _channel.invokeMethod('stopGather');
    return res;
  }

  /*
  * 获取用户当前位置
  * */
  static Future getUserLocation() async {
    dynamic res = await _channel.invokeMethod('getUserLocation');
    return res;
  }

  /*
  * 监听用户定位变化
  * */
  static StreamSubscription _subscription = null;
  static subscribeUserLocation({Function callback}) {
    if (_subscription == null) {
      _subscription = _stream.receiveBroadcastStream().listen(callback);
    }
  }

  /*
  * 取消监听
  * */
  static unsubscribeUserLocation() {
    _subscription.cancel();
    _subscription = null;
  }
}
