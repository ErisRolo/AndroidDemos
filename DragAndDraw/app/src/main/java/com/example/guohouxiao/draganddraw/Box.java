package com.example.guohouxiao.draganddraw;

import android.graphics.PointF;

/**
 * Created by guohouxiao on 2017/7/1.
 * 一个矩形框的定义数据
 */

public class Box {
    private PointF mOrigin;
    private PointF mCurrent;

    public Box(PointF origin) {
        mOrigin = origin;
        mCurrent = origin;
    }

    public PointF getCurrent() {
        return mCurrent;
    }

    public void setCurrent(PointF current) {
        mCurrent = current;
    }

    public PointF getOrigin() {
        return mOrigin;
    }
}
