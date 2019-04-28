import 'dart:async';

import 'package:flutter/services.dart';

class InspectionPrinter {

  static const MethodChannel _channel =
  const MethodChannel('inspection_printer');

  static Future<dynamic> print(List param) async {
    final dynamic res = await _channel.invokeMethod('print', {'list' : param});
    return res;
  }

  /*
  * 蓝牙打印机连接状态
  * */
  static Future<dynamic> getBLEPrinterState() async {
    final dynamic res = await _channel.invokeMethod('getBLEPrinterState');
    return res;
  }

  /*
  * 连接蓝牙打印机
  * */
  static Future<dynamic> connectToBLEPrinter() async {
    final dynamic res = await _channel.invokeMethod('connectToBLEPrinter');
    return res;
  }
}
