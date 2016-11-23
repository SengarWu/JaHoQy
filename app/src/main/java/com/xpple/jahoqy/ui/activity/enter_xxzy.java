package com.xpple.jahoqy.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.bean.MyBmobInstallation;

import java.util.List;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class enter_xxzy extends BaseActivity implements View.OnClickListener {
    private ImageView jhrw,jhbxs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        init();
        initTopBarForLeft("", R.mipmap.actionbar_publish, Color.BLACK);
    }

    private void init() {
        jhrw=(ImageView)findViewById(R.id.iv_publish_rw);
        jhbxs=(ImageView)findViewById(R.id.iv_publish_bxs);
        jhrw.setOnClickListener(this);
        jhbxs.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_publish_rw:
                Intent item = new Intent(enter_xxzy.this, Searchseek.class);
                startActivity(item);
                break;
            case R.id.iv_publish_bxs:
                Intent item2 = new Intent(enter_xxzy.this, SearchQuestion.class);
                startActivity(item2);
                break;

        }
    }
}
