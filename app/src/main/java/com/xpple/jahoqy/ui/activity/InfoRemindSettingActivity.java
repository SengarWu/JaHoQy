package com.xpple.jahoqy.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.R;

public class InfoRemindSettingActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_js0;
    private ImageView iv_js1;
    private Button btn_back;
    /*private ImageView iv_xwqs0;
    private ImageView iv_xwqs1;
    private ImageView iv_xrqs0;
    private ImageView iv_xrqs1;
    private ImageView iv_zrwx0;
    private ImageView iv_zrwx1;
    private ImageView iv_jhjj0;
    private ImageView iv_jhjj1;
    private ImageView iv_shbd0;
    private ImageView iv_shbd1;
    private ImageView iv_xxhz0;
    private ImageView iv_xxhz1;
    private ImageView iv_jhyx0;
    private ImageView iv_jhyx1;
    private RelativeLayout rl1;
    private RelativeLayout rl2;
    private RelativeLayout rl3;
    private RelativeLayout rl4;
    private RelativeLayout rl5;
    private RelativeLayout rl6;
    private RelativeLayout rl7;

    private View cv_info_1;
    private View cv_info_2;
    private View cv_info_3;
    private View cv_info_4;
    private View cv_info_5;
    private View cv_info_6;
    private View cv_info_7;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_remind_setting);
        initView();
    }

    private void initView() {
        iv_js0 = $(R.id.iv_js0);
        iv_js0.setOnClickListener(this);
        iv_js1 = $(R.id.iv_js1);
        iv_js1.setOnClickListener(this);
        btn_back = $(R.id.btn_back);
        btn_back.setOnClickListener(this);
       /* iv_xwqs0 = $(R.id.iv_xwqs0);
        iv_xwqs0.setOnClickListener(this);
        iv_xwqs1 = $(R.id.iv_xwqs1);
        iv_xwqs1.setOnClickListener(this);
        iv_xrqs0 = $(R.id.iv_xrqs0);
        iv_xrqs0.setOnClickListener(this);
        iv_xrqs1 = $(R.id.iv_xrqs1);
        iv_xrqs1.setOnClickListener(this);
        iv_zrwx0 = $(R.id.iv_zrwx0);
        iv_zrwx0.setOnClickListener(this);
        iv_zrwx1 = $(R.id.iv_zrwx1);
        iv_zrwx1.setOnClickListener(this);
        iv_jhjj0 = $(R.id.iv_jhjj0);
        iv_jhjj0.setOnClickListener(this);
        iv_jhjj1 = $(R.id.iv_jhjj1);
        iv_jhjj1.setOnClickListener(this);
        iv_shbd0 = $(R.id.iv_shbd0);
        iv_shbd0.setOnClickListener(this);
        iv_shbd1 = $(R.id.iv_shbd1);
        iv_shbd1.setOnClickListener(this);
        iv_xxhz0 = $(R.id.iv_xxhz0);
        iv_xxhz0.setOnClickListener(this);
        iv_xxhz1 = $(R.id.iv_xxhz1);
        iv_xxhz1.setOnClickListener(this);
        iv_jhyx0 = $(R.id.iv_jhyx0);
        iv_jhyx0.setOnClickListener(this);
        iv_jhyx1 = $(R.id.iv_jhyx1);
        iv_jhyx1.setOnClickListener(this);

        rl1 = $(R.id.rl1);
        rl2 = $(R.id.rl2);
        rl3 = $(R.id.rl3);
        rl4 = $(R.id.rl4);
        rl5 = $(R.id.rl5);
        rl6 = $(R.id.rl6);
        rl7 = $(R.id.rl7);

        cv_info_1 = $(R.id.cv_info_1);
        cv_info_2 = $(R.id.cv_info_2);
        cv_info_3 = $(R.id.cv_info_3);
        cv_info_4 = $(R.id.cv_info_4);
        cv_info_5 = $(R.id.cv_info_5);
        cv_info_6 = $(R.id.cv_info_6);
        cv_info_7 = $(R.id.cv_info_7);*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.iv_js0:  //未选中
                iv_js0.setVisibility(View.INVISIBLE);
                iv_js1.setVisibility(View.VISIBLE);
                /*rl1.setVisibility(View.INVISIBLE);
                rl2.setVisibility(View.INVISIBLE);
                rl3.setVisibility(View.INVISIBLE);
                rl4.setVisibility(View.INVISIBLE);
                rl5.setVisibility(View.INVISIBLE);
                rl6.setVisibility(View.INVISIBLE);
                rl7.setVisibility(View.INVISIBLE);

                cv_info_1.setVisibility(View.INVISIBLE);
                cv_info_2.setVisibility(View.INVISIBLE);
                cv_info_3.setVisibility(View.INVISIBLE);
                cv_info_4.setVisibility(View.INVISIBLE);
                cv_info_5.setVisibility(View.INVISIBLE);
                cv_info_6.setVisibility(View.INVISIBLE);
                cv_info_7.setVisibility(View.INVISIBLE);*/
                break;
            case R.id.iv_js1: //选中
                iv_js1.setVisibility(View.INVISIBLE);
                iv_js0.setVisibility(View.VISIBLE);
               /* rl1.setVisibility(View.VISIBLE);
                rl2.setVisibility(View.VISIBLE);
                rl3.setVisibility(View.VISIBLE);
                rl4.setVisibility(View.VISIBLE);
                rl5.setVisibility(View.VISIBLE);
                rl6.setVisibility(View.VISIBLE);
                rl7.setVisibility(View.VISIBLE);

                cv_info_1.setVisibility(View.VISIBLE);
                cv_info_2.setVisibility(View.VISIBLE);
                cv_info_3.setVisibility(View.VISIBLE);
                cv_info_4.setVisibility(View.VISIBLE);
                cv_info_5.setVisibility(View.VISIBLE);
                cv_info_6.setVisibility(View.VISIBLE);
                cv_info_7.setVisibility(View.VISIBLE);*/
                break;
            /*case R.id.iv_xwqs0:
                iv_xwqs0.setVisibility(View.INVISIBLE);
                iv_xwqs1.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_xwqs1:
                iv_xwqs1.setVisibility(View.INVISIBLE);
                iv_xwqs0.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_xrqs0:
                iv_xrqs0.setVisibility(View.INVISIBLE);
                iv_xrqs1.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_xrqs1:
                iv_xrqs1.setVisibility(View.INVISIBLE);
                iv_xrqs0.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_zrwx0:
                iv_zrwx0.setVisibility(View.INVISIBLE);
                iv_zrwx1.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_zrwx1:
                iv_zrwx1.setVisibility(View.INVISIBLE);
                iv_zrwx0.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_jhjj0:
                iv_jhjj0.setVisibility(View.INVISIBLE);
                iv_jhjj1.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_jhjj1:
                iv_jhjj1.setVisibility(View.INVISIBLE);
                iv_jhjj0.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_shbd0:
                iv_shbd0.setVisibility(View.INVISIBLE);
                iv_shbd1.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_shbd1:
                iv_shbd1.setVisibility(View.INVISIBLE);
                iv_shbd0.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_xxhz0:
                iv_xxhz0.setVisibility(View.INVISIBLE);
                iv_xxhz1.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_xxhz1:
                iv_xxhz1.setVisibility(View.INVISIBLE);
                iv_xxhz0.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_jhyx0:
                iv_jhyx0.setVisibility(View.INVISIBLE);
                iv_jhyx1.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_jhyx1:
                iv_jhyx1.setVisibility(View.INVISIBLE);
                iv_jhyx0.setVisibility(View.VISIBLE);
                break;*/
            case R.id.btn_back:
                finish();
                break;
        }
    }
}
