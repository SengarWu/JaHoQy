package com.xpple.jahoqy.ui.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.R;

public class PrivacyActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_phone0;
    private ImageView iv_phone1;
    private ImageView iv_city0;
    private ImageView iv_city1;
    private ImageView iv_chat0;
    private ImageView iv_chat1;
    private ImageView iv_my_publish0;
    private ImageView iv_my_publish1;
    private ImageView iv_my_help0;
    private ImageView iv_my_help1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        initTopBarForLeft("隐私", R.mipmap.actionbar_me, Color.BLACK);
        initView();
    }

    private void initView() {
        iv_phone0 = $(R.id.iv_phone0);
        iv_phone0.setOnClickListener(this);
        iv_phone1 = $(R.id.iv_phone1);
        iv_phone1.setOnClickListener(this);
        iv_city0 = $(R.id.iv_city0);
        iv_city0.setOnClickListener(this);
        iv_city1 = $(R.id.iv_city1);
        iv_city1.setOnClickListener(this);
        iv_chat0 = $(R.id.iv_chat0);
        iv_chat0.setOnClickListener(this);
        iv_chat1 = $(R.id.iv_chat1);
        iv_chat1.setOnClickListener(this);
        iv_my_publish0 = $(R.id.iv_my_publish0);
        iv_my_publish0.setOnClickListener(this);
        iv_my_publish1 = $(R.id.iv_my_publish1);
        iv_my_publish1.setOnClickListener(this);
        iv_my_help0 = $(R.id.iv_my_help0);
        iv_my_help0.setOnClickListener(this);
        iv_my_help1 = $(R.id.iv_my_help1);
        iv_my_help1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.iv_phone0:
                iv_phone0.setVisibility(View.INVISIBLE);
                iv_phone1.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_phone1:
                iv_phone1.setVisibility(View.INVISIBLE);
                iv_phone0.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_city0:
                iv_city0.setVisibility(View.INVISIBLE);
                iv_city1.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_city1:
                iv_city1.setVisibility(View.INVISIBLE);
                iv_city0.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_chat0:
                iv_chat0.setVisibility(View.INVISIBLE);
                iv_chat1.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_chat1:
                iv_chat1.setVisibility(View.INVISIBLE);
                iv_chat0.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_my_publish0:
                iv_my_publish0.setVisibility(View.INVISIBLE);
                iv_my_publish1.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_my_publish1:
                iv_my_publish1.setVisibility(View.INVISIBLE);
                iv_my_publish0.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_my_help0:
                iv_my_help0.setVisibility(View.INVISIBLE);
                iv_my_help1.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_my_help1:
                iv_my_help1.setVisibility(View.INVISIBLE);
                iv_my_help0.setVisibility(View.VISIBLE);
                break;

        }
    }
}
