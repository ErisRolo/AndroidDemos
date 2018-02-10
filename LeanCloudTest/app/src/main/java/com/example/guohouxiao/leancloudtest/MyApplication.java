package com.example.guohouxiao.leancloudtest;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;

/**
 * Created by guohouxiao on 2017/7/20.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AVOSCloud.initialize(this,"x8ApP1CKjA0G2uA1IALaLnii-gzGzoHsz","UmM92iqJOabChCK1OTxHekxf");
    }
}
