package com.xpple.jahoqy.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.bean.OfferHelp;

import cn.bmob.v3.listener.UpdateListener;

public class DialogAccessActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private RadioGroup access_radio_group;
    private RadioButton rb_good;
    private RadioButton rb_poor;
    private EditText et_message;
    private Button btn_submit;
    private Button btn_cancel;
    private static String access;
    private String obid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //先去除应用程序标题栏  注意：一定要在setContentView之前
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog_access);
        initView();
    }

    private void initView() {
        Intent intent=getIntent();
        obid=intent.getStringExtra("objectId");
        access_radio_group = $(R.id.access_radio_group);
        access_radio_group.setOnCheckedChangeListener(this);
        rb_good = $(R.id.rb_good);
        rb_poor = $(R.id.rb_poor);
        rb_good.setChecked(true);
        et_message = $(R.id.et_message);
        btn_submit = $(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
        btn_cancel = $(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_submit://提交

                break;
            case R.id.btn_cancel:
                finish();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == rb_good.getId())
        {
            access = "好评";
        }
        else if (checkedId == rb_poor.getId())
        {
            access = "差评";
            showToast("差评将会扣除用户20点江湖信誉");
        }
    }
}
