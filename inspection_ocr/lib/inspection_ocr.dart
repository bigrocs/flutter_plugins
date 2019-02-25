import 'dart:async';

import 'package:flutter/services.dart';

class InspectionOcr {

  static const MethodChannel _channel =
      const MethodChannel('inspection_ocr');

  static Future recognitionTheplate() async {
    final dynamic res = await _channel.invokeMethod('recognitionTheplate');
    return res;
  }

}
