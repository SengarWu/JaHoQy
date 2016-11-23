package com.xpple.jahoqy.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.R;

public class JHKTMsg2Activity extends BaseActivity {

    private TextView title;
    private TextView msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jhktmsg2);
        initTopBarForLeft("江湖课堂", R.mipmap.actionbar_jhkt, Color.BLACK);
        initView();
        initData();
    }

    private void initData() {
        switch (getIntent().getStringExtra("msg"))
        {
            case "info":
                title.setText(R.string.title_info);
                msg.setText(R.string.info);
                break;
            case "use":
                title.setText(R.string.title_use);
                msg.setText(R.string.use);
                break;
            case "question":
                title.setText(R.string.title_question);
                msg.setText(R.string.question);
                break;
            case "attention":
                title.setText(R.string.title_attention);
                msg.setText(R.string.attention);
                break;
            default:
                title.setText("");
                msg.setText("");
                break;
        }
    }

    private void initView() {
        title = $(R.id.tv_title);
        msg = $(R.id.tv_msg);
    }

}
