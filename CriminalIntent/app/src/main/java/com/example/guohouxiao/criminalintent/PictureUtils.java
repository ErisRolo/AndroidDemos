package com.example.guohouxiao.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

/**
 * Created by guohouxiao on 2017/4/23.
 */

public class PictureUtils {

    //静态Bitmap估算方法
    public static Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();//size是个点，包含两个整数坐标
        activity.getWindowManager().getDefaultDisplay().getSize(size);//确认屏幕的尺寸

        //缩放图像，保证载入的ImageView永远不会过大
        return getScaledBitmap(path, size.x, size.y);
    }

    //BitmapFactory类用来解码创建一个Bitmap
    //BitmapFactory.Options用于解码Bitmap时的各种参数控制
    //获得缩放的bitmap的方法
    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        //Read in the dimensions of the image on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        //这个值设为true，那么在解码的时候不会返回bitmap，只会返回这个bitmap的尺寸；一般想知道bitmap的尺寸但又不想将其加载到内存时使用
        options.inJustDecodeBounds = true;
        //decodeFile()方法用来将图片文件解码成bitmap
        BitmapFactory.decodeFile(path,options);

        //获得原文件的宽和高，outWidth和outHeight即这个bitmap的width和height
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        //Figure out how much to scale down by
        //isSampleSize决定缩略图像素大小，值为1则表明缩略图和源氏照片的水平像素大小一样；值为2则水平像素比为1:2，缩略图的像素数就是原始文件的四分之一
        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            if (srcWidth > srcHeight) {
                //Math.round()表示四舍五入运算
                inSampleSize = Math.round(srcHeight / destHeight);
            } else {
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        //Read in and create final bitmap
        return BitmapFactory.decodeFile(path, options);
    }
}
