import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'dart:async';
import 'dart:convert';

import 'package:flutter/services.dart';
import 'package:inspection_qrcode/inspection_qrcode.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  Uint8List bytes = Uint8List(0);
  
  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {

  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          children: <Widget>[
            GestureDetector(
              child: Container(
                width: 100,
                height: 100,
                color: Colors.red,
              ),
              onTap: () {
                Future future = InspectionQrcode.createQRCode("石文东大傻子");
                future.then((dynamic res) {
                  setState(() {
                    bytes = Base64Codec().decode(res);
                    print(bytes);
                  });
                });
              },
            ),
            Image.memory(bytes)
          ],
        ),
      ),
    );
  }
}
