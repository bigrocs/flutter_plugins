import 'dart:async';
import 'package:flutter/services.dart';

class InspectionPay {

  static const MethodChannel _channel =
      const MethodChannel('inspection_pay');

  /*
  * 银联SDK签到
  * */
  static Future<String> union_sign_in(dynamic param) async {
    dynamic res = await _channel.invokeMethod('union_sign_in', param);
    return res;
  }
  /*
  * 银联扫码支付
  * */
  static Future<String> union_pay_scan(dynamic param) async {
    dynamic res = await _channel.invokeMethod('pay_union_scan', param);
    return res;
  }
  /*
  * 银联刷卡支付
  * */
  static Future<String> union_pay_card(dynamic param) async {
    dynamic res = await _channel.invokeMethod('union_pay_card', param);
    return res;
  }
  /*
  * 扫码支付
  * */
  static Future<String> pay_scan(dynamic param) async {
    dynamic res = await _channel.invokeMethod('pay_scan', param);
    return res;
  }
}
