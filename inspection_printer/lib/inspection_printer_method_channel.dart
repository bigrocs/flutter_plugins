import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'inspection_printer_platform_interface.dart';

/// An implementation of [InspectionPrinterPlatform] that uses method channels.
class MethodChannelInspectionPrinter extends InspectionPrinterPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('inspection_printer');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<void> init() async {
    await methodChannel.invokeMethod<void>('init');
  }

  @override
  Future<int> getStatus() async {
    final status = await methodChannel.invokeMethod<int>('getStatus');
    return status ?? 0;
  }

  @override
  Future<String?> getSerialNumber() async {
    final serialNumber = await methodChannel.invokeMethod<String>('getSerialNumber');
    return serialNumber;
  }

  @override
  Future<void> printText(String text, {int? size, bool? bold, bool? underline, String? align}) async {
    await methodChannel.invokeMethod<void>('printText', {
      'text': text,
      'size': size,
      'bold': bold,
      'underline': underline,
      'align': align,
    });
  }

  @override
  Future<void> printBarCode(String data, {int? height, int? width, int? textPosition}) async {
    await methodChannel.invokeMethod<void>('printBarCode', {
      'data': data,
      'height': height,
      'width': width,
      'textPosition': textPosition,
    });
  }

  @override
  Future<void> printQRCode(String data, {int? moduleSize, int? errorLevel}) async {
    await methodChannel.invokeMethod<void>('printQRCode', {
      'data': data,
      'moduleSize': moduleSize,
      'errorLevel': errorLevel,
    });
  }

  @override
  Future<void> printImage(Uint8List img) async {
    await methodChannel.invokeMethod<void>('printImage', {'img': img});
  }

  @override
  Future<void> feedPaper({int lines = 1}) async {
    await methodChannel.invokeMethod<void>('feedPaper', {'lines': lines});
  }

  @override
  Future<void> cutPaper() async {
    await methodChannel.invokeMethod<void>('cutPaper');
  }

  @override
  Future<void> openDrawer() async {
    await methodChannel.invokeMethod<void>('openDrawer');
  }

  @override
  Future<void> reset() async {
    await methodChannel.invokeMethod<void>('reset');
  }
}
