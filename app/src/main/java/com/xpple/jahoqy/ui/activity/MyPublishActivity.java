package com.xpple.jahoqy.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.ui.otherFragment.myPublishTask;
import com.xpple.jahoqy.ui.otherFragment.mypublicshques;

import java.util.ArrayList;
import java.util.List;

public class MyPublishActivity extends BaseActivity implements View.OnClickListener,RadioGroup.OnCheckedChangeListener {
    private Button pub;
    private RadioButton que,task;
    List<Fragment> list = null;
    private ViewPager vp_mypub;
    private Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_publish);
        init();
    }

    private void init() {
        btn_back = $(R.id.btn_back);
        btn_back.setOnClickListener(this);
        vp_mypub=(ViewPager)findViewById(R.id.mypublis);
        que=(RadioButton)findViewById(R.id.Question);
        task=(RadioButton)findViewById(R.id.task);
        que.setOnClickListener(this);
        task.setOnClickListener(this);
        pub=(Button)findViewById(R.id.btn_publish);
        pub.setOnClickListener(this);
        list = new ArrayList<>();
        list.add(new mypublicshques());
        list.add(new myPublishTask());
        ZxzcAdapter zxzc = new ZxzcAdapter(getSupportFragmentManager(), list);
        vp_mypub.setAdapter(zxzc);

        //滑动切换
        vp_mypub.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                switch (arg0) {
                    case 0:
                        que.setChecked(true);
                        break;
                    case 1:
                        task.setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        que.setChecked(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_publish:
                startAnimActivity(SelectPublishActivity.class);
                break;
            case R.id.Question:
                vp_mypub.setCurrentItem(0);
                break;
            case R.id.task:
                vp_mypub.setCurrentItem(1);
                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (i == que.getId()) {
            vp_mypub.setCurrentItem(0);
        } else if (i == que.getId()) {
            vp_mypub.setCurrentItem(1);
        }
    }

    class ZxzcAdapter extends FragmentStatePagerAdapter {

        List<Fragment> list;

        public ZxzcAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.list = list;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int arg0) {
            return list.get(arg0);
        }

        @Override
        public int getCount() {
            return list.size();
        }
    }
}
