package com.example.guohouxiao.mvvmtest;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

/**
 * Created by guohouxiao on 2017/3/12.
 */

public class User extends BaseObservable {
    private String username;
    private String password;

    private boolean visible;

    //alt + insert

    @Bindable
    public boolean isVisible() {
        return visible;
    }

    @Bindable
    public void setVisible(boolean visible) {
        this.visible = visible;
        notifyPropertyChanged(BR.visible);
    }

    @Bindable
    public String getUsername() {
        return username;
    }

    @Bindable
    public void setUsername(String username) {
        this.username = username;
        notifyPropertyChanged(BR.username);
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    @Bindable
    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

}
