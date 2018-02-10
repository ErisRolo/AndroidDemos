package com.example.guohouxiao.bmobtest;

import cn.bmob.v3.BmobObject;

/**
 * Created by guohouxiao on 2017/7/3.
 * JavaBean
 */

public class Person extends BmobObject {
    private String name;
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
