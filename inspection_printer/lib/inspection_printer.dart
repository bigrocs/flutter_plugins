import 'dart:async';

import 'package:flutter/services.dart';

class InspectionPrinter {
  static const MethodChannel _channel =
      const MethodChannel('inspection_printer');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
