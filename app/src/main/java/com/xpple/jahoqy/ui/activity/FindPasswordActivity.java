package com.xpple.jahoqy.ui.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.proxy.UserProxy;
import com.xpple.jahoqy.util.StringUtils;
import com.xpple.jahoqy.view.DeletableEditText;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FindPasswordActivity extends BaseActivity implements View.OnClickListener, UserProxy.IRequestSmsCodeListener, UserProxy.IVerifySmsCodeListener, UserProxy.IResetPasswordListener {

    private DeletableEditText et_phone;
    private DeletableEditText et_new_password;
    private DeletableEditText et_code;
    private Button btn_get_code;
    private ImageView iv_find_password;
    private UserProxy userProxy;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);
        initView();
    }

    private void initView() {
        et_phone = $(R.id.et_phone);
        et_new_password = $(R.id.et_new_password);
        et_code = $(R.id.et_code);
        btn_get_code = $(R.id.btn_get_code);
        btn_get_code.setOnClickListener(this);
        iv_find_password = $(R.id.iv_find_password);
        iv_find_password.setOnClickListener(this);

        userProxy = new UserProxy(mContext);

        progress = new ProgressDialog(FindPasswordActivity.this);
        progress.setMessage("密码重置中...");
        progress.setCanceledOnTouchOutside(false);
        if (!isNetConnected())
        {
            showToast("网络连接不可用，请检查网络");
            return;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_get_code://获取验证码
                if (!isNetConnected())
                {
                    showToast("网络连接不可用，请检查网络");
                    return;
                }
                if (TextUtils.isEmpty(et_phone.getText())) {
                    et_phone.setShakeAnimation();
                    showToast("请输入手机号");
                    return;
                }
                if (!StringUtils.isPhoneNumberValid(et_phone.getText().toString()))
                {
                    et_phone.setShakeAnimation();
                    showToast("手机号格式不正确");
                    return;
                }
                btn_get_code.setText("获取中...");
                btn_get_code.setClickable(false);
                userProxy.setOnRequestSmsCodeListener(this);
                userProxy.requestSMSCode(et_phone.getText().toString());
                break;
            case R.id.iv_find_password: //找回密码
                if (!isNetConnected())
                {
                    showToast("网络连接不可用，请检查网络");
                    return;
                }
                if (TextUtils.isEmpty(et_phone.getText())) {
                    et_phone.setShakeAnimation();
                    showToast("请输入手机号");
                    return;
                }
                if (!StringUtils.isPhoneNumberValid(et_phone.getText().toString()))
                {
                    et_phone.setShakeAnimation();
                    showToast("手机号格式不正确");
                    return;
                }
                if (TextUtils.isEmpty(et_new_password.getText())) {
                    et_new_password.setShakeAnimation();
                    showToast("请输入密码");
                    return;
                }
                if (!StringUtils.isValidPassword(et_new_password.getText().toString())) {
                    et_new_password.setShakeAnimation();
                    showToast("密码长度不正确，请输入6-16位");
                    return;
                }
                if (TextUtils.isEmpty(et_code.getText()))
                {
                    et_code.setShakeAnimation();
                    showToast("请输入验证码");
                    return;
                }
                userProxy.setOnVerifySmsCodeListener(this);
                userProxy.setOnResetPasswordListener(this);
                progress.show();
                userProxy.resetPasswordOnCloud(et_phone.getText().toString(),
                        et_new_password.getText().toString(),
                        et_code.getText().toString());
                et_phone.setText("");
                et_code.setText("");
                et_new_password.setText("");
                break;
        }
    }

    @Override
    public void onRequestSmsCodeSuccess() {
        showToast("验证码获取成功");
        MyCount mc = new MyCount(60000, 1000);
        mc.start();
    }

    @Override
    public void onRequestSmsCodeFailure(String msg) {
        showToast("验证码获取失败" + msg);
        btn_get_code.setText("重新获取");
        btn_get_code.setClickable(true);
    }

    @Override
    public void onVerifySmsCodeSuccess() {

    }

    @Override
    public void OnVerifySmsCodeFailure(String msg) {
        progress.dismiss();
        et_code.setShakeAnimation();
        showToast("验证码错误。" + msg);
    }

    @Override
    public void onResetSuccess() {
        progress.dismiss();
        showToast("重置密码成功，请登录");
        startAnimActivity(LoginActivity.class);
    }

    @Override
    public void onResetFailure(String msg) {
        progress.dismiss();
        showToast("重置密码失败:" + msg);
    }

    class MyCount extends CountDownTimer { //60秒倒计时

        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            Date date = new Date(millisUntilFinished);
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
            String str = sdf.format(date);
            System.out.println(str);
            btn_get_code.setClickable(false);
            btn_get_code.setText("重新获取("+millisUntilFinished / 1000 + ")");
        }

        @Override
        public void onFinish() {
            btn_get_code.setClickable(true);
            btn_get_code.setText("重新获取");
        }
    }
}
