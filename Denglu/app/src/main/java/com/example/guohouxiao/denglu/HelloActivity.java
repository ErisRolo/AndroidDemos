package com.example.guohouxiao.denglu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by guohouxiao on 2017/1/7.
 */

public class HelloActivity extends AppCompatActivity {
    private static String TAG = "HelloActivity";
    private TextView mTvshow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);//设置activity_hello的显示界面
        mTvshow=(TextView) findViewById(R.id.tv_show);//全局私有变量mTvshow获得activity_hello.xml中的TextView的各项值
        if(getIntent()!=null){
            String str=getIntent().getStringExtra("Username");
            mTvshow.setText(str);
        }

    }
}
