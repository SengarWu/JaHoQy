package com.xpple.jahoqy.bean;

import android.content.Context;

import cn.bmob.v3.BmobInstallation;

/**
 * Created by Koreleone on 2015/11/3.
 */
public class MyBmobInstallation extends BmobInstallation {
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public MyBmobInstallation(Context context) {
        super(context);
    }

}
