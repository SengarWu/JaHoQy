package com.xpple.jahoqy.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.R;

public class SelectPublishActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_publish_rw;
    private ImageView iv_publish_bxs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_publish);
        initView();
        initTopBarForLeft("",R.mipmap.actionbar_xxzy, Color.BLACK);
    }

    private void initView() {
        iv_publish_rw = $(R.id.iv_publish_rw);
        iv_publish_rw.setOnClickListener(this);
        iv_publish_bxs = $(R.id.iv_publish_bxs);
        iv_publish_bxs.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.iv_publish_rw:
                startAnimActivity(Publish2Activity.class);
                finish();
                break;
            case R.id.iv_publish_bxs:
                startAnimActivity(Publish1Activity.class);
                finish();
                break;
        }
    }
}
