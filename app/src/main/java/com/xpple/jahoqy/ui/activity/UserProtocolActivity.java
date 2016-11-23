package com.xpple.jahoqy.ui.activity;

import android.graphics.Color;
import android.os.Bundle;

import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.R;

public class UserProtocolActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_protocol);
        initTopBarForLeft("用户协议", R.mipmap.actionbar_me, Color.BLACK);
    }

}
