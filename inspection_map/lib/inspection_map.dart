import 'dart:async';
import 'package:flutter/services.dart';

class InspectionMap {

  static const MethodChannel _channel =
  const MethodChannel('inspection_map');

  //位置变化监听 steam
  static const EventChannel _stream = const EventChannel('map.event.location');
  //鹰眼轨迹变化 steam
  static const EventChannel _hawkEyeStream = const EventChannel('map.event.hawk_eye');

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
  * 展示地图
  * */
  static Future showMapView() async {
    dynamic res = await _channel.invokeMethod('showMapView');
    return res;
  }

  /*
  * 放置大头针
  * */
  static Future placePin() async {
    dynamic res = await _channel.invokeMethod('placePin');
    return res;
  }

  /*
  * 监听用户定位变化
  * */
  static StreamSubscription _subscription;
  static subscribeUserLocation({Function callback}) {
    if (_subscription == null) {
      _subscription = _stream.receiveBroadcastStream().listen(callback);
    }
  }

  /*
  * 取消监听 位置变化
  * */
  static unsubscribeUserLocation() {
    _subscription.cancel();
    _subscription = null;
  }

  /*
  * 监听鹰眼轨迹数据
  * */
  static StreamSubscription _hawkEyeStreamsubscription;
  static subscribeHawEye({Function callback}) {
    if (_hawkEyeStreamsubscription == null) {
      _hawkEyeStreamsubscription = _hawkEyeStream.receiveBroadcastStream().listen(callback);
    }
  }
  /*
  * 取消监听
  * */
  static unsubscribeHawEye() {
    _hawkEyeStreamsubscription.cancel();
    _hawkEyeStreamsubscription = null;
  }
}
