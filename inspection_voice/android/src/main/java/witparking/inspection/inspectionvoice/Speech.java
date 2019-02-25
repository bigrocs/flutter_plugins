package witparking.inspection.inspectionvoice;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

import com.baidu.speech.VoiceRecognitionService;
import com.ypy.eventbus.EventBus;

import java.util.ArrayList;


public class Speech implements RecognitionListener {
  private SpeechRecognizer speechRecognizer;
  Activity activity;
  public Speech(Activity activity){
    this.activity=activity;
    // 创建识别器
    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity, new ComponentName(activity, VoiceRecognitionService.class));
    // 注册监听器
    speechRecognizer.setRecognitionListener(this);
//    startASR();
  }
  // 开始识别
  public void startASR() {
    Log.e("console", "开始录音");
    Intent intent = new Intent();
    bindParams(intent);
    speechRecognizer.startListening(intent);

  }
  public void stopASR(){
    Log.e("console", "结束录音");
    speechRecognizer.stopListening();
  }
  void bindParams(Intent intent) {
    // 设置识别参数
//    intent.putExtra("grammar","file:///android_asset/s_2_Navi");
    intent.putExtra("language","cmn-Hans-CN");
    intent.putExtra("sample",16000);
    intent.putExtra("prop", 10060);
    intent.putExtra("vad", "touch");
  }

  @Override
  public void onReadyForSpeech(Bundle bundle) {

  }

  @Override
  public void onBeginningOfSpeech() {

  }

  @Override
  public void onRmsChanged(float v) {

  }

  @Override
  public void onBufferReceived(byte[] bytes) {

  }

  @Override
  public void onEndOfSpeech() {

  }

  @Override
  public void onError(int i) {
    Log.e("console", ""+ "识别出错"+i);
    EventBus.getDefault().post(new VoiceInputEvent(null));
  }

  @Override
  public void onResults(Bundle bundle) {
    ArrayList<String> nbest = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
    if(nbest.size()>0){
      String chepai= Chepai.getNum(nbest.get(0));
      if(chepai!=null){
        EventBus.getDefault().post(new VoiceInputEvent(chepai));
      }else{
        EventBus.getDefault().post(new VoiceInputEvent(nbest.get(0)));
      }

    }else{
      EventBus.getDefault().post(new VoiceInputEvent(null));
    }

    for(String s:nbest){
      Log.e("console", ""+ s);
    }
  }

  @Override
  public void onPartialResults(Bundle bundle) {
    ArrayList<String> nbest = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
    for(String s:nbest){
      Log.e("console", "临时识别结果"+ s);
    }
  }

  @Override
  public void onEvent(int i, Bundle bundle) {

  }
}
