/*
 * @Author: BigRocs
 * @Date: 2025-02-22 16:30:41
 * @LastEditTime: 2025-03-06 19:08:10
 * @LastEditors: BigRocs
 * @Description: QQ: 532388887, Email:bigrocs@qq.com
 */
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'inspection_printer_method_channel.dart';

import 'dart:typed_data';

abstract class InspectionPrinterPlatform extends PlatformInterface {
  /// Constructs a InspectionPrinterPlatform.
  InspectionPrinterPlatform() : super(token: _token);

  static final Object _token = Object();

  static InspectionPrinterPlatform _instance = MethodChannelInspectionPrinter();

  /// The default instance of [InspectionPrinterPlatform] to use.
  ///
  /// Defaults to [MethodChannelInspectionPrinter].
  static InspectionPrinterPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [InspectionPrinterPlatform] when
  /// they register themselves.
  static set instance(InspectionPrinterPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  /// 初始化打印机
  Future<void> init() {
    throw UnimplementedError('init() has not been implemented.');
  }

  /// 获取打印机状态
  Future<int> getStatus() {
    throw UnimplementedError('getStatus() has not been implemented.');
  }

  /// 获取打印机序列号
  Future<String?> getSerialNumber() {
    throw UnimplementedError('getSerialNumber() has not been implemented.');
  }

  /// 打印文本
  Future<void> printText(String text, {int? size, bool? bold, bool? underline, String? align}) {
    throw UnimplementedError('printText() has not been implemented.');
  }

  /// 打印条形码
  Future<void> printBarCode(String data, {int? height, int? width, int? textPosition}) {
    throw UnimplementedError('printBarCode() has not been implemented.');
  }

  /// 打印二维码
  Future<void> printQRCode(String data, {int? moduleSize, int? errorLevel}) {
    throw UnimplementedError('printQRCode() has not been implemented.');
  }

  /// 打印图片
  Future<void> printImage(Uint8List img) {
    throw UnimplementedError('printImage() has not been implemented.');
  }

  /// 走纸
  Future<void> feedPaper({int lines = 1}) {
    throw UnimplementedError('feedPaper() has not been implemented.');
  }

  /// 切纸
  Future<void> cutPaper() {
    throw UnimplementedError('cutPaper() has not been implemented.');
  }

  /// 打开钱箱
  Future<void> openDrawer() {
    throw UnimplementedError('openDrawer() has not been implemented.');
  }

  /// 重置打印机
  Future<void> reset() {
    throw UnimplementedError('reset() has not been implemented.');
  }
}
