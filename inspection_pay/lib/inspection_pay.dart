import 'dart:async';
import 'package:flutter/services.dart';

class InspectionPay {

  static const MethodChannel _channel =
      const MethodChannel('inspection_pay');

  static Future<String> get union_pay async {
    dynamic res = await _channel.invokeMethod('pay_union');
    return res;
  }
}
