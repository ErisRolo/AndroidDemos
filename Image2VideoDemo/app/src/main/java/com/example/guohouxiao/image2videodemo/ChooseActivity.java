package com.example.guohouxiao.image2videodemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;

import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by guohouxiao on 2017/5/6.
 */

public class ChooseActivity extends TakePhotoActivity implements View.OnClickListener {
    private TakePhoto mtakePhoto;
    private int limit = 1000;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choose_photo:
                File file=new File(Environment.getExternalStorageDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg");
                if (!file.getParentFile().exists())
                    file.getParentFile().mkdirs();

                mtakePhoto = getTakePhoto();
                if (limit != 0) {
                    mtakePhoto.onPickMultiple(limit);
                    return;
                }
                mtakePhoto.onPickFromGallery();//从相册中选取图片
                break;
            default:
                break;
        }
    }


    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        sendPhotos(result.getImages());//选取完成后将选取的数据传出
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    //传递选中的照片数据
    private void sendPhotos(ArrayList<TImage> images) {
        Intent intent = new Intent(this,CombineActivity.class);
        intent.putExtra("images", images);
        startActivity(intent);
    }

}
