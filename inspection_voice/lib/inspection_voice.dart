import 'dart:async';

import 'package:flutter/services.dart';

class InspectionVoice {
  static const MethodChannel _channel =
      const MethodChannel('inspection_voice');

  /*
  * 语音播报
  * */
  static Future speechSynthesis({String param}) async {
    dynamic res = await _channel.invokeMethod('speechSynthesis', {"words": param});
    return res;
  }

  /*
  * 开始语音输入
  * */
  static Future startSpeechInput() async {
    dynamic res = await _channel.invokeMethod('startSpeechInput');
    return res;
  }

  /*
  * 结束语音输入
  * 此时会返回语音输入结果
  * */
  static Future stopSpeechInput() async {
    dynamic res = await _channel.invokeMethod('stopSpeechInput');
    return res;
  }

}
