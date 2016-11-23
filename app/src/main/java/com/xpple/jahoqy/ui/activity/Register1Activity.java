package com.xpple.jahoqy.ui.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.proxy.UserProxy;
import com.xpple.jahoqy.util.StringUtils;
import com.xpple.jahoqy.view.DeletableEditText;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Register1Activity extends BaseActivity implements View.OnClickListener,
        UserProxy.IRequestSmsCodeListener, UserProxy.IVerifySmsCodeListener {

    private DeletableEditText et_phone;
    private DeletableEditText et_code;
    private Button btn_get_code;
    private TextView tv_next;

    private UserProxy userProxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);
        initView();
    }

    private void initView() {
        et_phone = $(R.id.et_phone);
        et_code = $(R.id.et_code);
        btn_get_code = $(R.id.btn_get_code);
        btn_get_code.setOnClickListener(this);
        tv_next = $(R.id.tv_next);
        tv_next.setOnClickListener(this);

        userProxy = new UserProxy(mContext);

        //设置字体
        AssetManager mgr=getAssets();
        Typeface tf=Typeface.createFromAsset(mgr, "fonts/hk.TTF");
        tv_next.setTypeface(tf);

        //检查网络是否可用
        if (!isNetConnected()) {
            showToast("当前网络不可用，请检查网络");
            return;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_get_code:
                if (!isNetConnected()) {
                    showToast("当前网络不可用，请检查网络");
                    return;
                }
                if (TextUtils.isEmpty(et_phone.getText())){
                    et_phone.setShakeAnimation();
                    showToast("请输入手机号");
                    return;
                }
                if (!StringUtils.isPhoneNumberValid(et_phone.getText().toString())){
                    et_phone.setShakeAnimation();
                    showToast("手机号格式不正确");
                    return;
                }
                btn_get_code.setText("获取中...");
                btn_get_code.setClickable(false);
                userProxy.setOnRequestSmsCodeListener(this);//获取验证码
                userProxy.requestSMSCode(et_phone.getText().toString());
                break;
            case R.id.tv_next:   //点击下一步
                if (!isNetConnected()) {
                    showToast("当前网络不可用，请检查网络");
                    return;
                }
                if (TextUtils.isEmpty(et_phone.getText())) {
                    et_phone.setShakeAnimation();
                    showToast("请输入手机号");
                    return;
                }
                if (!StringUtils.isPhoneNumberValid(et_phone.getText().toString())) {
                    et_phone.setShakeAnimation();
                    showToast("手机号格式不正确");
                    return;
                }
                if (TextUtils.isEmpty(et_code.getText()))
                {
                    et_code.setShakeAnimation();
                    showToast("请输入验证码");
                    return;
                }
                userProxy.setOnVerifySmsCodeListener(this);
                userProxy.verifySmsCode(et_phone.getText().toString(),et_code.getText().toString());
                break;
        }
    }

    @Override
    public void onRequestSmsCodeSuccess() {
        showToast("验证码获取成功");
        MyCount mc = new MyCount(60000, 1000);
        mc.start();
        btn_get_code.setClickable(false);
    }

    @Override
    public void onRequestSmsCodeFailure(String msg) {
        showToast("验证码获取失败。" + msg);
        btn_get_code.setText("重新获取");
        btn_get_code.setClickable(true);
    }

    @Override
    public void onVerifySmsCodeSuccess() {
        Intent intent = new Intent(this,Register2Activity.class);
        intent.putExtra("phone",et_phone.getText().toString());
        startActivity(intent);
    }

    @Override
    public void OnVerifySmsCodeFailure(String msg) {
        showToast("验证码错误" + msg);
    }


    class MyCount extends CountDownTimer { //60秒倒计时

        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            Date date = new Date(millisUntilFinished);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
            String str = sdf.format(date);
            System.out.println(str);
            btn_get_code.setText("重新获取(" + millisUntilFinished / 1000 + ")");
        }

        @Override
        public void onFinish() {
            btn_get_code.setClickable(true);
            btn_get_code.setText("重新获取");
        }
    }
}