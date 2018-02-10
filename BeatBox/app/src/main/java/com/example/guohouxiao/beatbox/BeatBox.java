package com.example.guohouxiao.beatbox;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by guohouxiao on 2017/4/25.
 */

public class BeatBox {
    private static final String TAG = "BeatBox";

    private static final String SOUNDS_FOLDER = "sample_sounds";
    private static final int MAX_SOUNDS = 5;

    private AssetManager mAssets;//AssetManager类用来访问assets
    private List<Sound> mSounds = new ArrayList<>();
    private SoundPool mSoundPool;//SoundPool类能加载声音资源到内存中，并支持同时播放多个音频文件

    public BeatBox(Context context) {
        //在访问assets时，不关心使用哪个Context对象，所有Context中的AssetManager管理的都是同一套assets资源
        mAssets = context.getAssets();

        //This old constructor is deprecated, but we need it for compatibility.
        //第一个参数指定同时播放多少个音频
        //第二个参数确定音频流类型，STREAM_MUSIC使用的是同音乐和游戏一样的音量控制
        //最后一个参数指定采样率转换品质
        mSoundPool = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0);

        loadSounds();
    }

    //播放音频
    public void play(Sound sound) {
        Integer soundId = sound.getSoundId();
        if (soundId == null) {
            return;
        }
        //参数分别为音频ID、左音量、右音量、优先级（无效）、是否循环以及播放速率
        mSoundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    //释放SoundPool
    public void release() {
        mSoundPool.release();
    }

    public List<Sound> getSounds() {
        return mSounds;
    }

    private void loadSounds() {
        String[] soundNames;
        //查看assets资源
        try {
            //获得assets中的资源清单，AssetManager.list(String)方法能列出指定目录中的所有文件名
            soundNames = mAssets.list(SOUNDS_FOLDER);
            Log.i(TAG,"Found " + soundNames.length + " sounds");
        } catch (IOException ioe) {
            Log.e(TAG,"Could not list assets", ioe);
            return;
        }

        //创建Sound列表
        for (String filename : soundNames) {
            try {
                String assetPath = SOUNDS_FOLDER + "/" + filename;
                Sound sound = new Sound(assetPath);
                load(sound);//载入全部音频文件
                mSounds.add(sound);
            } catch (IOException ioe) {
                Log.e(TAG,"Could not load sound " + filename, ioe);
            }
        }
    }

    //加载音频
    private void load(Sound sound) throws IOException {
        //AssetFileDescriptor 资源描述符，通过它获取资源
        //AssetManager.openFd(String)方法通过文件名打开资源文件，返回值为资源描述符
        AssetFileDescriptor afd = mAssets.openFd(sound.getAssetPath());
        //SoundPool.load(AssetFileDescriptor, int)方法可以把文件载入SoundPool待播，返回一个int值为sound的id
        int soundId = mSoundPool.load(afd, 1);
        sound.setSoundId(soundId);
    }

}
