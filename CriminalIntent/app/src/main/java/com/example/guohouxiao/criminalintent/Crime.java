package com.example.guohouxiao.criminalintent;

import java.util.Date;
import java.util.UUID;

/**
 * Created by guohouxiao on 2017/4/12.
 */

public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;//表示crime是否已得到处理
    private String mSuspect;//嫌疑人

    public Crime(){
        this(UUID.randomUUID());
/*        mId = UUID.randomUUID();
        mDate = new Date();*/
    }

    //因为需要返回具有UUID的Crime，因此新增一个有此用途的构造方法
    public Crime(UUID id) {
        mId = id;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public String getSuspect(){
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    //文件名获取方法，由于文件名基于Crime ID编制，因此该方法也具有唯一性
    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }
}
