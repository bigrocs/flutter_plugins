package witparking.inspection.inspectionvoice;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import static com.baidu.tts.client.SpeechSynthesizer.MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI;


public class TTSUtil {

    // 语音合成客户端
    private SpeechSynthesizer mSpeechSynthesizer;
    private Activity activity;
    private ArrayList<String> speech = new ArrayList<String>();
    private boolean speaking = false;

    private String voiceType = new voices().f7;

    private TTSUtil(Activity activity) {
        this.activity = activity;
        speaking = false;
        initTTS();
    }

    /*
    * 单例
    * */
    private static TTSUtil mSingleton = null;

    public static TTSUtil getInstance(Activity activity) {
        if (mSingleton == null) {
            synchronized (TTSUtil.class) {
                if (mSingleton == null) {
                    mSingleton = new TTSUtil(activity);
                }
            }
        }
        return mSingleton;
    }

    /*语音合成*/
    private void initTTS() {

        String sdurl = Environment.getExternalStorageDirectory().getPath();
        final String PARAM_TTS_TEXT_MODEL_FILE = sdurl + "/bd_etts_text.dat";
        final String PARAM_TTS_SPEECH_MODEL_FILE = sdurl + "/" + voiceType;
        copySource(false);
        // 获取语音合成对象实例
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        // 设置context
        mSpeechSynthesizer.setContext(activity);
        // 设置语音合成状态监听器
        mSpeechSynthesizer.setSpeechSynthesizerListener(mSpeechSynthesizerListener);
        // 设置在线语音合成授权，需要填入从百度语音官网申请的api_key和secret_key
        mSpeechSynthesizer.setApiKey("sCmADchrasNEHxv3uECiwZ2a", "0f2d2216c406cbefd2a45fc8f3e11e44");
        // 设置离线语音合成授权，需要填入从百度语音官网申请的app_id
        mSpeechSynthesizer.setAppId("9090687");
        // 设置语音合成文本模型文件
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, PARAM_TTS_TEXT_MODEL_FILE);
        // 设置语音合成声音模型文件
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, PARAM_TTS_SPEECH_MODEL_FILE);

        //设置合成方案 mix模式下，仅wifi使用在线合成,返回速度如果慢（超时，一般为1.2秒）直接切换离线，适用于仅WIFI网络环境较差的情况)
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI);
        //合成引擎速度优化等级，取值范围[0, 2]，值越大速度越快（离线引擎）
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOCODER_OPTIM_LEVEL, String.valueOf(2));

        // 获取语音合成授权信息
        AuthInfo authInfo = mSpeechSynthesizer.auth(TtsMode.MIX);
        // 判断授权信息是否正确，如果正确则初始化语音合成器并开始语音合成，如果失败则做错误处理
        if (authInfo.isSuccess()) {
            Log.i("console", "授权成功");
            mSpeechSynthesizer.initTts(TtsMode.MIX);
        } else {
            // 授权失败
            Log.i("console", "授权失败");
        }
    }

    public void add(String speakString) {
        speech.add(speakString);
        if (speech.size() > 5) {
            speech.remove(0);
        }
        if (!speaking) {
            Speak();
        }
    }

    public void Speak() {

        String speakString;

        try {
            if (speech.size() > 0) {
                speakString = speech.get(0);
                if (speakString != null) {
                    speaking = true;
                    mSpeechSynthesizer.speak(speakString);
                    speech.remove(speakString);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("console", "Speak: " + "语音播报错误");
            speech.clear();
            speaking = false;
        }
    }

    private SpeechSynthesizerListener mSpeechSynthesizerListener = new SpeechSynthesizerListener() {

        @Override
        public void onSynthesizeStart(String s) {

        }

        @Override
        public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {

        }

        @Override
        public void onSynthesizeFinish(String s) {

        }

        @Override
        public void onSpeechStart(String s) {
        }

        @Override
        public void onSpeechProgressChanged(String s, int i) {

        }

        @Override
        public void onSpeechFinish(String s) {
            speaking = false;
            Speak();
        }

        @Override
        public void onError(String s, SpeechError speechError) {
            speaking = false;
            if (speechError.code == 400) {
                initTTS();
            }
        }
    };

    /**
     * @param isCover 是否必须更新
     */
    public void copySource(final boolean isCover) {
        String sdurl = Environment.getExternalStorageDirectory().getPath();
        final String PARAM_TTS_TEXT_MODEL_FILE = sdurl + "/bd_etts_text.dat";
        final String PARAM_TTS_SPEECH_MODEL_FILE = sdurl + "/" + voiceType;
        copyFromAssetsToSdcard(isCover, "bd_etts_text.dat", PARAM_TTS_TEXT_MODEL_FILE);
        copyFromAssetsToSdcard(isCover, voiceType, PARAM_TTS_SPEECH_MODEL_FILE);
    }


    /**
     * 将sample工程需要的资源文件拷贝到SD卡中使用（授权文件为临时授权文件，请注册正式授权）
     *
     * @param isCover 是否覆盖已存在的目标文件
     * @param source
     * @param dest
     */
    private synchronized void copyFromAssetsToSdcard(boolean isCover, String source, String dest) {

        File file = new File(dest);
        if (!file.exists()) {//如果文件没有加载成功过，必须加载
            isCover = true;
        }
        if (!file.exists()) {
            try {
                boolean ss2 = file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (isCover || (!isCover && !file.exists())) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = activity.getResources().getAssets().open(source);
                String path = dest;
                fos = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int size = 0;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stop() {
        try {
            mSpeechSynthesizer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class voices {
        /*
        * m15 离线男声
        * f7 离线女声
        * yyjw 度逍遥
        * as 度丫丫
        * */
        String m15 = "bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat";
        String f7 = "bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat";
        String yyjw = "bd_etts_common_speech_yyjw_mand_eng_high_am-mix_v3.0.0_20170512.dat";
        String as = "bd_etts_common_speech_as_mand_eng_high_am_v3.0.0_20170516.dat";
    }
}
