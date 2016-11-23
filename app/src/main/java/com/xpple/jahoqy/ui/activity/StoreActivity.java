package com.xpple.jahoqy.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.R;

public class StoreActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_buy;
    private Button btn_task;
    private Button btn_exchange;
    private Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        initView();
    }

    private void initView() {
        btn_back = $(R.id.btn_back);
        btn_back.setOnClickListener(this);
        btn_buy = $(R.id.btn_buy);
        btn_buy.setOnClickListener(this);
        btn_task = $(R.id.btn_task);
        btn_task.setOnClickListener(this);
        btn_exchange = $(R.id.btn_exchange);
        btn_exchange.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_buy://购买积分
                showToast("敬请期待");
                break;
            case R.id.btn_task://积分任务
                showToast("敬请期待");
                break;
            case R.id.btn_exchange://积分兑换
                showToast("敬请期待");
                break;
            case R.id.btn_back:
                this.finish();
                break;
        }
    }
}
