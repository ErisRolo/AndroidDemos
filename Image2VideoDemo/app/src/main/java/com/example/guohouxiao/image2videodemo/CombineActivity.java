package com.example.guohouxiao.image2videodemo;

import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


import com.jph.takephoto.model.TImage;


import java.util.ArrayList;

/**
 * Created by guohouxiao on 2017/5/6.
 */

public class CombineActivity extends AppCompatActivity {
    ArrayList<TImage> mImages;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combine);
        mImages =(ArrayList<TImage>) getIntent().getSerializableExtra("images");
    }


}
