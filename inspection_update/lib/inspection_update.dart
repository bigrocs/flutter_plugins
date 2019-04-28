import 'dart:async';

import 'package:flutter/services.dart';

class InspectionUpdate {


  static const MethodChannel _channel =
      const MethodChannel('inspection_update');

  static Future update(String url, Function callback) async {
    StreamSubscription streamSubscription = _subscribeUpdateProgress(callback: callback);
    final dynamic res = await _channel.invokeMethod('update', {'url' : url});
    if (res == 'done') {
      _unSubscribeUpdateProgress(streamSubscription);
    }
    return res;
  }

  /*
  * 更新进度
  * */
  static const EventChannel _stream = const EventChannel('update.event.progress');

  static StreamSubscription _subscribeUpdateProgress({Function callback}) {
    return _stream.receiveBroadcastStream().listen(callback);
  }

  static _unSubscribeUpdateProgress(StreamSubscription streamSubscription) {
    streamSubscription.cancel();
    streamSubscription = null;
  }
}
