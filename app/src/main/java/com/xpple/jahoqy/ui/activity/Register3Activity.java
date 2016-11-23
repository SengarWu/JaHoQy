package com.xpple.jahoqy.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.CustomApplication;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.bean.User;
import com.xpple.jahoqy.proxy.UserProxy;
import com.xpple.jahoqy.view.DeletableEditText;

import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.SaveListener;

public class Register3Activity extends BaseActivity {

    private DeletableEditText et_username;
    private TextView tv_next;

    private String phone;
    private String password;

    private UserProxy userProxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);
        initView();
    }

    private void initView() {
        et_username = $(R.id.et_username);
        tv_next = $(R.id.tv_next);
        tv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetConnected())
                {
                    showToast("当前网络不可用，请检查网络");
                    return;
                }
                if (TextUtils.isEmpty(et_username.getText())) {
                    et_username.setShakeAnimation();
                    showToast("请输入用户名");
                    return;
                }
                final ProgressDialog progress = new ProgressDialog(
                        Register3Activity.this);
                progress.setMessage("正在注册...");
                progress.setCanceledOnTouchOutside(false);
                progress.show();
                Intent intent = getIntent();
                phone = intent.getStringExtra("phone");
                password = intent.getStringExtra("password");
                final User user = new User();
                user.setUsername(et_username.getText().toString());
                user.setMobilePhoneNumber(phone);
                user.setPassword(password);
                user.setUserIntegral(50);
                //环信密码
                user.setHuanxinPassword(password);
                //开始注册
                user.signUp(mContext, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    EMChatManager.getInstance().createAccountOnServer(phone, password);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showToast("注册成功");
                                            Intent intent = new Intent(Register3Activity.this, LoginActivity.class);
                                            intent.putExtra("phone", phone);
                                            intent.putExtra("password", password);
                                            startActivity(intent);
                                            // 保存用户名
                                            CustomApplication.getInstance().setUserName(phone);
                                            finish();
                                        }
                                    });
                                }catch (final EaseMobException e){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            user.delete(mContext, new DeleteListener() {
                                                @Override
                                                public void onSuccess() {
                                                }

                                                @Override
                                                public void onFailure(int i, String s) {

                                                }
                                            });
                                            if(progress!=null){
                                                progress.dismiss();
                                            }
                                            Toast.makeText(mContext, "注册失败", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }).start();
                    }
                    @Override
                    public void onFailure(int i, String s) {
                        showToast("注册失败" + s);
                        progress.dismiss();
                    }
                });

            }
        });
    }
}
