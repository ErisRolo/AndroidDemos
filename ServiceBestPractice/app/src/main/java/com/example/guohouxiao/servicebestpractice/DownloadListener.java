package com.example.guohouxiao.servicebestpractice;

/**
 * Created by guohouxiao on 2017/3/29.
 */

public interface DownloadListener {
    void onProgress(int progress);
    void onSuccess();
    void onFailed();
    void onPaused();
    void onCanceled();
}
