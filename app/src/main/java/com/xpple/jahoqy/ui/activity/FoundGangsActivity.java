package com.xpple.jahoqy.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.bean.Gangs;
import com.xpple.jahoqy.view.DeletableEditText;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.SaveListener;

public class FoundGangsActivity extends BaseActivity implements View.OnClickListener {
    private DeletableEditText et_gangs_name;
    private EditText et_gangs_purpose;
    private TextView tv_create_integral;
    private Button btn_cancel;
    private Button btn_create_gangs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //先去除应用程序标题栏  注意：一定要在setContentView之前
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_found_gangs);
        initView();
    }

    private void initView() {
        et_gangs_name = $(R.id.et_gangs_name);
        et_gangs_purpose = $(R.id.et_gangs_purpose);
        tv_create_integral =$(R.id.tv_create_integral);
        btn_cancel = $(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
        btn_create_gangs = $(R.id.btn_create_gangs);
        btn_create_gangs.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_create_gangs:
                if (TextUtils.isEmpty(et_gangs_name.getText().toString()))
                {
                    showToast("帮派名称不能为空");
                    return;
                }
                if (TextUtils.isEmpty(et_gangs_purpose.getText().toString()))
                {
                    showToast("帮派宗旨不能为空");
                    return;
                }
                btn_create_gangs.setEnabled(false);
                final String gangsname=et_gangs_name.getText().toString();
                try {
                    EMGroup group= EMGroupManager.getInstance().createPublicGroup(gangsname, "", null, true, 200);

                    Gangs gangs = new Gangs();
                    gangs.setGangsName(gangsname);
                    String groupId=group.getGroupId();
                    gangs.setGangsObjectId(groupId);
                    gangs.setGangsPurpose(et_gangs_purpose.getText().toString());
                    gangs.setGangsCreater(CurrentUser.getUsername().toString());
                    gangs.save(mContext, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            JSONObject myJesn = new JSONObject();
                            try {
                                myJesn.put("objectId", CurrentUser.getObjectId());
                                myJesn.put("gangsPosition", 0);
                                myJesn.put("gangsName", et_gangs_name.getText().toString());

                                AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
                                //第一个参数是上下文对象，第二个参数是云端代码的方法名称，第三个参数是上传到云端代码的参数列表（JSONObject cloudCodeParams），第四个参数是回调类
                                ace.callEndpoint(mContext, "updateGangs", myJesn,
                                        new CloudCodeListener() {
                                            @Override
                                            public void onSuccess(Object object) {
                                                String str = object.toString();
                                                if (str.equals("yes")) {
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            showToast("建帮派成功");
                                                            setResult(12);
                                                            finish();
                                                        }
                                                    });
                                                } else {
                                                    showToast(object.toString());
                                                    btn_create_gangs.setEnabled(false);
                                                }
                                            }

                                            @Override
                                            public void onFailure(int code, String msg) {
                                                // TODO Auto-generated method stub
                                                //检查网络设置
                                                showToast("请检查网络设置");
                                                btn_create_gangs.setEnabled(false);
                                            }
                                        });
                            } catch (JSONException e) {
                                showToast("请检查网络设置");
                                btn_create_gangs.setEnabled(false);
                            }
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            showToast("创建失败" + s);
                            btn_create_gangs.setEnabled(false);
                        }
                    });

                } catch (EaseMobException ee) {
                    btn_create_gangs.setEnabled(false);
                    showToast("建群失败");
                }

                break;
        }
    }
}
