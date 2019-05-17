import 'dart:async';

import 'package:flutter/services.dart';

class Camera {
  static const MethodChannel _channel =
      const MethodChannel('camera');

  static Future<String> takePhoto() async {
    final String version = await _channel.invokeMethod('takePhoto');
    return version;
  }
}
