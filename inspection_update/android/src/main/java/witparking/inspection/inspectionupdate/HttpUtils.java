package witparking.inspection.inspectionupdate;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;


/**
 * Created by yangyuxi on 2017/11/28.
 */

public class HttpUtils {

    public HttpUtils() {

    }

    /*
    * 网络请求 GET
    * */
    public void getData(String url, HttpResultCallback callback) {

        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        //创建一个Request
        final Request request = new Request.Builder()
                .url(url)
                .build();
        //new call
        Call call = mOkHttpClient.newCall(request);

        //请求加入调度
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Request request, IOException e)
            {
            }

            @Override
            public void onResponse(final Response response) throws IOException
            {
                //response.body().string();
            }
        });
    }
    /*
    * 网络请求 POST JSON
    * */
    public void postDataWithJSON(String url, String param, HttpResultCallback callback) {

        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), param);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Call call = mOkHttpClient.newCall(request);
        //请求加入调度
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Request request, IOException e)
            {
            }

            @Override
            public void onResponse(final Response response) throws IOException
            {
                response.body().string();
            }
        });
    }
    /*
    * 网络请求 POST URLENCODED
    * 直接传递URL的形式
    * */
    public void postDataWithUrlencoded(String url, final HttpResultCallback callback) {

        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "");
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        final Call call = mOkHttpClient.newCall(request);
        //请求加入调度
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Request request, IOException e)
            {
                if (callback != null) {
                    callback.onError(request, e);
                }
            }

            @Override
            public void onResponse(final Response response) throws IOException
            {
                if (callback != null) {
                    callback.onGetResult(response.body().string());
                }
            }
        });

    }
}
