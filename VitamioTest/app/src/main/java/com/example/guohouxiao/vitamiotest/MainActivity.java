package com.example.guohouxiao.vitamiotest;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class MainActivity extends AppCompatActivity {
    private VideoView videoView;
    private TextView percentTv;
    private TextView netSpeedTv;
    private int mVideoLayout = 0;

    private String url1 = "http://img.netbian.com/file/2017/0306/d91d6214344be970f109e32c38a2a937.jpg";
    private String url2 = "https://b-ssl.duitang.com/uploads/item/201510/13/20151013170940_KQCTS.thumb.700_0.jpeg";
    private String url3 = "https://b-ssl.duitang.com/uploads/item/201507/28/20150728121646_TKRYM.thumb.700_0.jpeg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //检查vitamio框架是否可用
        if (!LibsChecker.checkVitamioLibs(this)) {
            return;
        }
        setContentView(R.layout.activity_main);
        //显示缓冲百分比的TextView
        percentTv = (TextView) findViewById(R.id.buffer_percent);
        //显示下载网速的TextView
        netSpeedTv = (TextView) findViewById(R.id.net_speed);
        //初始化加载库文件
        if (Vitamio.isInitialized(this)) {
            videoView = (VideoView) findViewById(R.id.vitamio);
            String[] url = {url1, url2, url3};
            for (int i = 0; i < 3; i++) {
                videoView.setVideoURI(Uri.parse(url[i]));
            }
            //videoView.setVideoURI(Uri.parse(url2));
            videoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);
            MediaController controller = new MediaController(this);
            videoView.setMediaController(controller);
            videoView.setBufferSize(10240); //设置视频缓冲大小。默认1024KB，单位byte
            videoView.requestFocus();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    // optional need Vitamio 4.0
                    mediaPlayer.setPlaybackSpeed(1.0f);
                    //mediaPlayer.setLooping(true);
                }
            });

            videoView.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    percentTv.setText("已缓冲：" + percent + "%");
                }
            });
            videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    switch (what) {
                        //开始缓冲
                        case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                            percentTv.setVisibility(View.VISIBLE);
                            netSpeedTv.setVisibility(View.VISIBLE);
                            mp.pause();
                            break;
                        //缓冲结束
                        case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                            percentTv.setVisibility(View.GONE);
                            netSpeedTv.setVisibility(View.GONE);
                            mp.start(); //缓冲结束再播放
                            break;
                        //正在缓冲
                        case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                            netSpeedTv.setText("当前网速:" + extra + "kb/s");
                            break;
                    }
                    return true;
                }
            });
        }
    }
    public void changeLayout(View view) {
        mVideoLayout++;
        if (mVideoLayout == 4) {
            mVideoLayout = 0;
        }
        switch (mVideoLayout) {
            case 0:
                mVideoLayout = VideoView.VIDEO_LAYOUT_ORIGIN;
                //view.setBackgroundResource(R.mipmap.mediacontroller_sreen_size_100);
                break;
            case 1:
                mVideoLayout = VideoView.VIDEO_LAYOUT_SCALE;
                //view.setBackgroundResource(R.mipmap.mediacontroller_screen_fit);
                break;
            case 2:
                mVideoLayout = VideoView.VIDEO_LAYOUT_STRETCH;
                //view.setBackgroundResource(R.mipmap.mediacontroller_screen_size);
                break;
            case 3:
                mVideoLayout = VideoView.VIDEO_LAYOUT_ZOOM;
                //view.setBackgroundResource(R.mipmap.mediacontroller_sreen_size_crop);
                break;
        }
        videoView.setVideoLayout(mVideoLayout, 0);
    }
}
