package com.xpple.jahoqy.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;

import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.bean.User;
import com.xpple.jahoqy.view.HeaderLayout;

import cn.bmob.v3.listener.UpdateListener;

public class IntroduceEditActivity extends BaseActivity {

    private EditText et_introduce;
    private Intent intent2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce_edit);
        et_introduce = $(R.id.et_introduce);
        if (getIntent().getStringExtra("introduce") != null)
        {
            et_introduce.setText(getIntent().getStringExtra("introduce"));
        }

        initTopBarForBoth("江湖宣言", R.mipmap.ic_publish, R.mipmap.actionbar_me, Color.BLACK, new HeaderLayout.onRightImageButtonClickListener() {
            @Override
            public void onClick() {
                intent2 = new Intent();
                intent2.putExtra("introduce_et", et_introduce.getText().toString());
                setResult(RESULT_OK, intent2);
                finish();
                User user = new User();
                user.setJahoAnnounce(et_introduce.getText().toString());
                user.setUserIntegral(CurrentUser.getUserIntegral());
                user.setExperience(CurrentUser.getExperience());
                user.setGangsPosition(CurrentUser.getGangsPosition());
                user.update(mContext, CurrentUser.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        showToast("保存成功");
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        showToast("保存失败" + s);
                    }
                });
            }
        });
    }
}
