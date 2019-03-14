package witparking.inspection.inspectionupdate;

import com.squareup.okhttp.Request;

import java.io.IOException;

/**
 * Created by yangyuxi on 2017/11/28.
 */

public interface HttpResultCallback {
    //回调的方法
    public void onGetResult(String result);
    //网络请求失败
    public void onError(Request request, IOException e);
}
