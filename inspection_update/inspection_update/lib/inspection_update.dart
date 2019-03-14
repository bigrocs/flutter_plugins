import 'dart:async';

import 'package:flutter/services.dart';

class InspectionUpdate {
  static const MethodChannel _channel =
      const MethodChannel('inspection_update');

  static Future platformVersion() async {
    dynamic res = await _channel.invokeMethod('update');
    return res;
  }
}
