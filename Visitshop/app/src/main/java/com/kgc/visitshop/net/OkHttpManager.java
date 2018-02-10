package com.kgc.visitshop.net;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 网络请求管理
 * 对OkHttp请求进行封装
 */
public class OkHttpManager {

    private static OkHttpManager instance;
    private OkHttpClient mOkHttpClient;
    private Handler okHandler;

    private OkHttpManager() {
		//声明Handler对象，指定为主线程Looper，确保执行方法在主线程中
        okHandler = new Handler(Looper.getMainLooper());

        //指定超时时间等参数
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS);
        mOkHttpClient = builder.build();
    }

    /** 
     * 获取当前实例对象，确保唯一
     * @return
     */
    public static OkHttpManager getInstance() {
        if (instance == null) {
            synchronized (OkHttpManager.class) {
                if (instance == null) {
                    instance = new OkHttpManager();
                }
            }
        }
        return instance;
    }

    /**
     * get请求
     *
     * @param url
     * @param resultCallback
     */
    public void getNet(String url, ResultCallback resultCallback) {
        Request request = new Request.Builder()
                .url(url)//url 接口地址
                .method("GET", null)//此设置默认为get,可以不设置
                .build();
        dealNet(request, resultCallback);
    }

    /**
     * post请求
     *
     * @param url
     * @param resultCallback
     * @param param 传入参数，数量不定
     */
    public void postNet(String url, ResultCallback resultCallback, Param... param) {
        if (param == null) {
            param = new Param[0];
        }
        FormBody.Builder formBody = new FormBody.Builder();
        for (Param p : param) {
            formBody.add(p.key, p.value);
        }
        RequestBody requestBody = formBody.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)//传入构建好的参数
                .build();
        dealNet(request, resultCallback);
    }

   /**
     * 发送网络请求
     * @param request
     * @param resultCallback 自己定义的监听回调
     */
    private void dealNet(final Request request, final ResultCallback resultCallback) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                okHandler.post(new Runnable() {
                    @Override
                    public void run() {
						//失败时执行的方法
                        resultCallback.onFailed(request, e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = "";
                try {
                    str = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final String finalStr = str;
                Log.i("OkHttpManager", "onResponse: " + finalStr);
                okHandler.post(new Runnable() {
                    @Override
                    public void run() {
						//成功时执行的方法
                        resultCallback.onSuccess(finalStr);
                    }
                });
            }
        });
    }

    public static abstract class ResultCallback {
        public abstract void onFailed(Request request, IOException e);

        public abstract void onSuccess(String response);
    }

    /**
     * 参数封装类
     */
    public static class Param {
        String key;
        String value;

        public Param() {
        }

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
