import 'dart:async';

import 'package:flutter/services.dart';

class InspectionUnionPay {

  static const MethodChannel _channel =
      const MethodChannel('inspection_union_pay');

  static Future<String> pay(dynamic param) async {
    final String res = await _channel.invokeMethod('pay', param);
    return res;
  }

}
