package com.xpple.jahoqy.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.R;

public class JHKTActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout rl_info;
    private RelativeLayout rl_use;
    private RelativeLayout rl_question;
    private RelativeLayout rl_attention;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jhkt);
        initTopBarForLeft("江湖课堂", R.mipmap.actionbar_jhkt, Color.BLACK);
        initView();
        intent = new Intent(JHKTActivity.this,JHKTMsg2Activity.class);
    }

    private void initView() {
        rl_info = $(R.id.rl_info);
        rl_info.setOnClickListener(this);
        rl_use = $(R.id.rl_use);
        rl_use.setOnClickListener(this);
        rl_question = $(R.id.rl_question);
        rl_question.setOnClickListener(this);
        rl_attention = $(R.id.rl_attention);
        rl_attention.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.rl_info:
                intent.putExtra("msg","info");
                startActivity(intent);
                break;
            case R.id.rl_use:
                intent.putExtra("msg","use");
                startActivity(intent);
                break;
            case R.id.rl_question:
                intent.putExtra("msg","question");
                startActivity(intent);
                break;
            case R.id.rl_attention:
                intent.putExtra("msg","attention");
                startActivity(intent);
                break;
        }
    }
}
