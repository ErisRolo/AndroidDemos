package com.example.guohouxiao.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by guohouxiao on 2017/5/3.
 * 用于批量下载缩略图的后台线程(消息循环)
 * 在Android中，使用消息队列(message queue)的线程叫作消息循环(message loop)
 * 消息循环由线程和looper组成，Looper对象管理线程的消息队列
 * looper不断从消息队列中抓取信息，然后完成消息指定的任务
 */

public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;//用来标识下载请求消息（即消息的What属性）
    private static final int MESSAGE_PRELOAD = 1;//用来标识预加载请求消息

    private Boolean mHasQuit = false;//用来标志线程是否退出

    //用来存储对Handler的引用，这个Handler负责在后台线程上管理下载请求消息队列，也负责从消息队列里取出并处理下载请求消息
    private Handler mRequestHandler;

    //ConcurrentHashmap是一种线程安全的HashMap;这里使用一个标记下载请求的T类型对象作为key，可以存取和请求关联的URL下载链接
    private ConcurrentMap<T,String> mRequestMap = new ConcurrentHashMap<>();

    //用来存放来自于主线程的Handler
    private Handler mResponseHandler;
    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;

    private LruCache<String,Bitmap> mCache;//缓存

    //用来（在请求者和结果间）通信的监听器接口
    public interface ThumbnailDownloadListener<T> {
        void onThumbnailDownloaded(T target, Bitmap thumbnail);
    }

    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> listener) {
        mThumbnailDownloadListener = listener;
    }

    //用一个接受Handler的构造方法替换原有构造方法
    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }

    @Override
    //在Looper首次检查消息队列之前调用，因此适合在该方法创建Handler实现
    protected void onLooperPrepared() {
        //创建一个新Handler
        mRequestHandler = new Handler() {
            @Override
            //实现handleMessage()方法,队列中的下载消息取出并可以处理时，就会触发调用该方法
            public void handleMessage(Message msg) {
                //首先检查消息类型
                if (msg.what == MESSAGE_DOWNLOAD) {
                    T target = (T) msg.obj;//获取obj值（T类型下载请求）
                    Log.i(TAG, "Got a request for URL: " + mRequestMap.get(target));
                    handleRequest(target);//处理消息（下载请求）
                } else if (msg.what == MESSAGE_PRELOAD) {
                    String urlToPreload = (String) msg.obj;
                    handlePreload(urlToPreload);//处理预加载请求
                }
            }
        };

        //建立Cache
        int maxCacheSize = 4 * 1024 * 1024;//4MB
        mCache = new LruCache<>(maxCacheSize);
    }

    @Override
    //重写quit()方法，用于停止HandlerThread
    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }

    //存根方法,T类型对象用来标识具体那次下载，String参数为URL下载链接
    //PhotoAdapter在其onBindViewHolder()方法中调用该方法，发消息给Handler
    public void queueThumbnail(T target, String url) {
        Log.i(TAG, "Got a URL: " + url);

        if (url == null) {
            mRequestMap.remove(target);
        } else {
            mRequestMap.put(target, url);//存储消息
            //Handler.obtainMesage()方法从公共循环池里获取消息，可避免创建新的Message对象，提高效率
            //从mRequestHandler直接获取消息后，mRequestHandler也就自动成为这个新Message对象的target，会负责处理刚从消息队列中取出的这个消息
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();//获取并发送消息
        }
    }

    //预加载用
    public void queuePreloadThumbnail(String url) {
        mRequestHandler.obtainMessage(MESSAGE_PRELOAD, url).sendToTarget();
    }

    //添加清理方法
    public void clearQueue() {
        mResponseHandler.removeMessages(MESSAGE_DOWNLOAD);
        mResponseHandler.removeMessages(MESSAGE_PRELOAD);
    }

    //执行下载的方法
    private void handleRequest(final T target) {
        try {
            final String url = mRequestMap.get(target);//从mRequestMap中取出图片URL

            if (url == null) {
                return;
            }

            final Bitmap bitmap;
            //首先查看缓存确认图片是否存在
            if (mCache.get(url) == null) {

                //使用FlickrFetchr从URL下载图片字节数据，然后再转换为位图
                byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
                bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                Log.i(TAG, "Bitmap created");

                mCache.put(url, bitmap);//下载完毕后存入缓存中
            } else {
                bitmap = mCache.get(url);
                Log.i(TAG, "Bitmap from cache");
            }

            //图片下载与显示
            //Handler.post()用来发布Message，通知与之关联的Looper所在的线程完成操作
            //mResponseHandler与主线程的Looper相关联，所以UI更新代码会在主线程中完成
            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    //首先在此检查requestMap，保证每个PhotoHolder都能获取到正确的图片，即使中间发了其他请求也不会有影响
                    //选中mHasQuit，如果ThumbnailDownloader已经退出，运行任何回调方法都不太安全
                    if (mRequestMap.get(target) != url || mHasQuit) {
                        return;
                    }

                    mRequestMap.remove(target);//从requestMap中删除配对的PhotoHolder-URL
                    mThumbnailDownloadListener.onThumbnailDownloaded(target, bitmap);//将位图设置到目标PhotoHolder上
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "Error downloading image", e);
        }
    }

    //预加载方法
    private void handlePreload(String url) {
        try {
            if (url == null) {
                return;
            }

            if (mCache.get(url) == null) {
                byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                mCache.put(url, bitmap);
            }

        } catch (IOException e) {
            Log.e(TAG, "Error preloading image", e);
        }
    }

}
