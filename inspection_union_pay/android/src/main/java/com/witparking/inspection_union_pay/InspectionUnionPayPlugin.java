package com.witparking.inspection_union_pay;

import android.content.Intent;
import android.os.Bundle;

import com.ums.AppHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * InspectionUnionPayPlugin
 */
public class InspectionUnionPayPlugin implements MethodCallHandler {

    private Registrar registrar;
    private Result payResult;

    private InspectionUnionPayPlugin(final Registrar registrar) {
        this.registrar = registrar;
        registrar.addActivityResultListener(new PluginRegistry.ActivityResultListener() {
            @Override
            public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {
                if (requestCode == 1000) {
                    Bundle bundle = intent.getExtras();
                    assert bundle != null;
                    String result = bundle.getString("result");
                    payResult.success(result);
                }
                return false;
            }
        });
    }

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "inspection_union_pay");
        channel.setMethodCallHandler(new InspectionUnionPayPlugin(registrar));
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {

        switch (call.method) {
            case "pay":
                unionPay(call, result);
                break;
            case "":
                break;
            default:
                result.notImplemented();
                break;
        }

    }

    private void unionPay(MethodCall call, Result result) {

        payResult = result;

        JSONObject transData = new JSONObject();
        try {
            transData.put("appId", call.argument("appId"));
            transData.put("amt", call.argument("Amount"));
            transData.put("isNeedPrintReceipt", true);

            String transType = call.argument("TransType");

            assert transType != null;
            if(transType.equals("2")){
                transData.put("extOrderNo", call.argument("outTradeNo"));
                transData.put("extBillNo", call.argument("outTradeNo"));
                AppHelper.callTrans(registrar.activity(), "银行卡收款", "消费", transData);
            }else if(transType.equals("110")){
                transData.put("extOrderNo", call.argument("outTradeNo"));
                transData.put("extBillNo", call.argument("outTradeNo"));
                AppHelper.callTrans(registrar.activity(), "POS 通", "扫一扫", transData);
            }else{
                AppHelper.callTrans(registrar.activity(), "公共资源", "签到",transData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Map<String,String> getStringToMap(String str){

        //判断str是否有值
        if(null == str || "".equals(str)){
            return null;
        }
        //根据&截取
        String[] strings = str.split("&");
        //设置HashMap长度
        int mapLength = strings.length;
        //判断hashMap的长度是否是2的幂。
        if((strings.length % 2) != 0){
            mapLength = mapLength+1;
        }

        Map<String,String> map = new HashMap<>(mapLength);
        //循环加入map集合
        for (int i = 0; i < strings.length; i++) {
            //截取一组字符串
            String[] strArray = strings[i].split("=");
            //strArray[0]为KEY  strArray[1]为值
            map.put(strArray[0],strArray[1]);
        }
        return map;
    }

}
