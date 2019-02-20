package witparking.inspection.inspectionmqtt;

import android.util.Log;

/**
 * Created by LYC on 2017/2/7.
 */

public class SendMessageEvent {

  public String message;

  public SendMessageEvent(String message) {
    this.message = message;
  }
}
