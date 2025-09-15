/*
 * @Author: BigRocs
 * @Date: 2025-02-22 16:30:41
 * @LastEditTime: 2025-03-06 19:07:38
 * @LastEditors: BigRocs
 * @Description: QQ: 532388887, Email:bigrocs@qq.com
 */

import 'inspection_printer_platform_interface.dart';

import 'dart:typed_data';

class InspectionPrinter {
  Future<String?> getPlatformVersion() {
    return InspectionPrinterPlatform.instance.getPlatformVersion();
  }

  /// 初始化打印机
  static Future<void> init() async {
    return InspectionPrinterPlatform.instance.init();
  }

  /// 获取打印机状态
  static Future<int> getStatus() async {
    return InspectionPrinterPlatform.instance.getStatus();
  }

  /// 获取打印机序列号
  static Future<String?> getSerialNumber() async {
    return InspectionPrinterPlatform.instance.getSerialNumber();
  }

  /// 打印文本
  static Future<void> printText(String text, {int? size, bool? bold, bool? underline, String? align}) async {
    return InspectionPrinterPlatform.instance.printText(text, size: size, bold: bold, underline: underline, align: align);
  }

  /// 打印条形码
  static Future<void> printBarCode(String data, {int? height, int? width, int? textPosition}) async {
    return InspectionPrinterPlatform.instance.printBarCode(data, height: height, width: width, textPosition: textPosition);
  }

  /// 打印二维码
  static Future<void> printQRCode(String data, {int? moduleSize, int? errorLevel}) async {
    return InspectionPrinterPlatform.instance.printQRCode(data, moduleSize: moduleSize, errorLevel: errorLevel);
  }

  /// 打印图片
  static Future<void> printImage(Uint8List img) async {
    return InspectionPrinterPlatform.instance.printImage(img);
  }

  /// 走纸
  static Future<void> feedPaper({int lines = 1}) async {
    return InspectionPrinterPlatform.instance.feedPaper(lines: lines);
  }

  /// 切纸
  static Future<void> cutPaper() async {
    return InspectionPrinterPlatform.instance.cutPaper();
  }

  /// 打开钱箱
  static Future<void> openDrawer() async {
    return InspectionPrinterPlatform.instance.openDrawer();
  }

  /// 重置打印机
  static Future<void> reset() async {
    return InspectionPrinterPlatform.instance.reset();
  }
}
