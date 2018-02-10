package com.example.guohouxiao.lessontwo;

import android.os.Bundle;
import android.app.Activity;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by guohouxiao on 2017/1/7.
 */

public class HelloActivity extends AppCompatActivity {
    private static String TAG ="HelloActivity";
    private TextView mTvshow;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);
        mTvshow = (TextView) findViewById(R.id.tv_show);
        if(getIntent()!=null){
            String str=getIntent().getStringExtra("Username");
            mTvshow.setText(str);
        }
    }
}
