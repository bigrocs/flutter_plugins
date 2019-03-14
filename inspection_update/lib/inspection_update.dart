import 'dart:async';

import 'package:flutter/services.dart';

class InspectionUpdate {
  static const MethodChannel _channel =
      const MethodChannel('inspection_update');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
