import 'dart:async';

import 'package:flutter/services.dart';

class InspectionMqtt {
  static const MethodChannel _channel =
      const MethodChannel('inspection_mqtt');

  /*
  * 获取clientid
  * */
  static Future getMQTTClientID() async {
    final dynamic result = await _channel.invokeMethod('get_mqtt_client_id');
    return result;
  }

  /*
  * 事件渠道用以接收消息
  * */
}
