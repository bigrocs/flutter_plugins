import 'dart:async';
import 'package:flutter/services.dart';

class InspectionPay {

  static const MethodChannel _channel =
      const MethodChannel('inspection_pay');

  /*
  * 银联SDK签到
  * */
  static Future<String> get union_sign_in async {
    dynamic res = await _channel.invokeMethod('union_sign_in');
    return res;
  }
  /*
  * 银联扫码支付
  * */
  static Future<String> get union_pay_scan async {
    dynamic res = await _channel.invokeMethod('pay_union_scan');
    return res;
  }
  /*
  * 银联刷卡支付
  * */
  static Future<String> get union_pay_card async {
    dynamic res = await _channel.invokeMethod('union_pay_card');
    return res;
  }
  /*
  * 扫码支付
  * */
  static Future<String> get pay_scan async {
    dynamic res = await _channel.invokeMethod('pay_scan');
    return res;
  }
}
