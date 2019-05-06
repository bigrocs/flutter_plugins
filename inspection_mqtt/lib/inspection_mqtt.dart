import 'dart:async';

import 'package:flutter/services.dart';

class InspectionMqtt {

  static const MethodChannel _channel =
      const MethodChannel('inspection_mqtt');

  static const EventChannel _stream = const EventChannel('mqtt.event.message');

  /*
  * 获取clientid
  * */
  static Future getMQTTClientID({Object param}) async {
    final dynamic result = await _channel.invokeMethod('get_mqtt_client_id', param);
    return result;
  }

  /*
  * 事件渠道用以接收消息
  * */
  static Stream _subscription = null;
  static Stream subscribeMQTTMessage() {
    if (_subscription == null) {
      _subscription = _stream.receiveBroadcastStream();
    }
    return _subscription;
  }

  static unsubscribeMQTTMessage() {
    _subscription = null;
  }
}
