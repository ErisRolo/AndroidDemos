package com.example.guohouxiao.beatbox;

/**
 * Created by guohouxiao on 2017/4/25.
 */

public class Sound {
    private String mAssetPath;
    private String mName;
    private Integer mSoundId;//用Integer类型而不是int，可以在Sound的mSoundId没有值时设置其为null

    public Sound(String assetPath) {
        mAssetPath = assetPath;
        String[] components = assetPath.split("/");//分离出文件名
        String filename = components[components.length - 1];//获得文件名;
        mName = filename.replace(".wav","");//删除.wav后缀
    }

    public String getAssetPath() {
        return mAssetPath;
    }

    public String getName() {
        return mName;
    }

    public Integer getSoundId() {
        return mSoundId;
    }

    public void setSoundId(Integer soundId) {
        mSoundId = soundId;
    }
}
