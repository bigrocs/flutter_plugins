import 'dart:async';

import 'package:flutter/services.dart';

class InspectionQrcode {
  static const MethodChannel _channel =
      const MethodChannel('inspection_qrcode');

  /*
  * 启动扫码组件
  * */
  static Future startScan() async {
    dynamic res = await _channel.invokeMethod('startScan');
    return res;
  }

  /*
  * 生成二维码
  * */
  static Future createQRCode(String content) async {
    String res = await _channel.invokeMethod('createQRCode', {content: content});
    return res;
  }

}
