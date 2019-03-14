import 'dart:async';

import 'package:flutter/services.dart';

class InspectionUpdate {
  static const MethodChannel _channel =
      const MethodChannel('inspection_update');

  static Future update(String url) async {
    final dynamic res = await _channel.invokeMethod('update', {'url' : url});
    return res;
  }
}
