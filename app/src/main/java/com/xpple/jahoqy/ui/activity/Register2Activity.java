package com.xpple.jahoqy.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.util.StringUtils;
import com.xpple.jahoqy.view.DeletableEditText;

public class Register2Activity extends BaseActivity {

    private DeletableEditText et_password;
    private DeletableEditText et_password_again;
    private TextView tv_next;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        initView();
    }

    private void initView() {
        et_password = $(R.id.et_password);
        et_password_again = $(R.id.et_password_again);
        tv_next = $(R.id.tv_next);
        tv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_password.getText()))
                {
                    et_password.setShakeAnimation();
                    showToast("请输入密码");
                    return;
                }
                if (!StringUtils.isValidPassword(et_password.getText()))
                {
                    et_password.setShakeAnimation();
                    showToast("密码格式不正确，请输入6-16位");
                    return;
                }
                if (TextUtils.isEmpty(et_password_again.getText()))
                {
                    et_password_again.setShakeAnimation();
                    showToast("请输入确认密码");
                    return;
                }
                if (!et_password.getText().toString().equals(et_password_again.getText().toString()))
                {
                    et_password_again.setShakeAnimation();
                    showToast("两次输入密码不一致");
                    return;
                }
                Intent intent = getIntent();
                phone = intent.getStringExtra("phone");
                Intent intent1 = new Intent(Register2Activity.this,Register3Activity.class);
                intent1.putExtra("phone",phone);
                intent1.putExtra("password",et_password.getText().toString());
                startActivity(intent1);
            }
        });
    }

}
