import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:inspection_union_pay/inspection_union_pay.dart';

void main() {
  const MethodChannel channel = MethodChannel('inspection_union_pay');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await InspectionUnionPay.platformVersion, '42');
  });
}
