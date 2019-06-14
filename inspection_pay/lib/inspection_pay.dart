import 'dart:async';
import 'package:flutter/services.dart';

class InspectionPay {

  static const MethodChannel _channel =
      const MethodChannel('inspection_pay');

  /*
  * 银联扫码支付
  * */
  static Future union_pay_scan(dynamic param) async {
    dynamic res = await _channel.invokeMethod('pay_union_scan', param);
    print(res);
    return res;
  }

}
