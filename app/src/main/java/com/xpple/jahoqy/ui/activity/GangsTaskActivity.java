package com.xpple.jahoqy.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.R;

public class GangsTaskActivity extends BaseActivity implements View.OnClickListener {

    private Button btn_task1;
    private Button btn_task2;
    private Button btn_task3;
    private Button btn_task4;
    private boolean static_task1 = false;
    private boolean static_task2 = false;
    private boolean static_task3 = false;
    private boolean static_task4 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gangs_task);
        initTopBarForLeft("帮派任务", R.mipmap.actionbar_gangs_task, Color.BLACK);
        initView();

    }

    private void initView() {
        btn_task1 = $(R.id.btn_task1);
        btn_task1.setOnClickListener(this);
        btn_task2 = $(R.id.btn_task2);
        btn_task2.setOnClickListener(this);
        btn_task3 = $(R.id.btn_task3);
        btn_task3.setOnClickListener(this);
        btn_task4 = $(R.id.btn_task4);
        btn_task4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_task1:
                if (static_task1 == false)
                {
                    static_task1 = true;
                    btn_task1.setText("取消任务");
                    showToast("成功领取任务，快去完成任务吧！");
                }
                else
                {
                    static_task1 = false;
                    btn_task1.setText("领取任务");
                    showToast("任务已取消！");
                }
                break;
            case R.id.btn_task2:
                if (!static_task2)
                {
                    static_task2 = true;
                    btn_task2.setText("取消任务");
                    showToast("成功领取任务，快去完成任务吧！");
                }
                else
                {
                    static_task2 = false;
                    btn_task2.setText("领取任务");
                    showToast("任务已取消！");
                }
                break;
            case R.id.btn_task3:
                if (!static_task3)
                {
                    static_task3 = true;
                    btn_task3.setText("取消任务");
                    showToast("成功领取任务，快去完成任务吧！");
                }
                else
                {
                    static_task3 = false;
                    btn_task3.setText("领取任务");
                    showToast("任务已取消！");
                }
                break;
            case R.id.btn_task4:
                if (!static_task4)
                {
                    static_task4 = true;
                    btn_task4.setText("取消任务");
                    showToast("成功领取任务，快去完成任务吧！");
                }
                else
                {
                    static_task4 = false;
                    btn_task4.setText("领取任务");
                    showToast("任务已取消！");
                }
                break;
        }
    }
}
