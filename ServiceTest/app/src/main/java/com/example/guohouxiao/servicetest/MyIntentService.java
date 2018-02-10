package com.example.guohouxiao.servicetest;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by guohouxiao on 2017/3/29.
 */

public class MyIntentService extends IntentService {
    public MyIntentService() {
        super("MyIntentService");//调用父类的有参构造函数
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //打印当前线程的id
        Log.i("MyIntentService","Thread id is " + Thread.currentThread().getId());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("MyIntentService","onDestory executed");
    }
}
