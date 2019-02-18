import 'dart:async';
import 'dart:convert';
import 'package:flutter/services.dart';

class InspectionPay {

  static const MethodChannel _channel =
      const MethodChannel('inspection_pay');

  static Future<String> get union_pay async {
    dynamic res = await _channel.invokeMethod('pay_union');
    res = json.decode(res);
    return res;
  }
}
