import 'dart:async';

import 'package:flutter/services.dart';

class InspectionQrcode {
  static const MethodChannel _channel =
      const MethodChannel('inspection_qrcode');

  static Future startScan() async {
    dynamic res = await _channel.invokeMethod('startScan');
    return res;
  }
}
