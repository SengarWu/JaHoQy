package com.xpple.jahoqy.ui.activity;

import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.easemob.EMCallBack;
import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.BaseClass.DemoHXSDKHelper;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.bean.MyBmobInstallation;

import java.util.List;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_message_setting;
    private Button btn_chat_setting;
    private Button btn_privacy;
    private Button btn_user_protocol;
    private Button btn_about;
    private Button btn_clear_cache;
    private Button btn_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initTopBarForLeft("设置", R.mipmap.actionbar_me, Color.BLACK);
        initView();
    }

    private void initView() {
        btn_message_setting = $(R.id.btn_message_setting);
        btn_message_setting.setOnClickListener(this);
        btn_chat_setting=$(R.id.btn_chat_setting);
        btn_chat_setting.setOnClickListener(this);
        btn_privacy = $(R.id.btn_privacy);
        btn_privacy.setOnClickListener(this);
        btn_user_protocol = $(R.id.btn_user_protocol);
        btn_user_protocol.setOnClickListener(this);
        btn_about = $(R.id.btn_about);
        btn_about.setOnClickListener(this);
        btn_clear_cache = $(R.id.btn_clear_cache);
        btn_clear_cache.setOnClickListener(this);
        btn_exit=$(R.id.btn_exit);
        btn_exit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_message_setting:
                startAnimActivity(InfoRemindSettingActivity.class);
                break;
            case R.id.btn_chat_setting:
                startActivityForResult(new Intent(SettingActivity.this,ChatSettingActivity.class),0);
                break;
            case R.id.btn_privacy:
                startAnimActivity(PrivacyActivity.class);
                break;
            case R.id.btn_user_protocol:
                startAnimActivity(UserProtocolActivity.class);
                break;
            case R.id.btn_about:
                startAnimActivity(AboutActivity.class);
                break;
            case R.id.btn_clear_cache:
                showToast("缓存清除成功！");
                break;
            case R.id.btn_exit:
                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("你确定要退出吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearDevice();
                        logout();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
            default:
                break;
        }
    }

    private void clearDevice() {
        //保存设备信息
        BmobInstallation.getCurrentInstallation(mContext).save();

        BmobQuery<MyBmobInstallation> query = new BmobQuery<MyBmobInstallation>();//新建对设备的查询
        query.addWhereEqualTo("installationId", BmobInstallation.getInstallationId(mContext));//查询条件为匹配当前设备
        query.findObjects(SettingActivity.this, new FindListener<MyBmobInstallation>() {

            @Override
            public void onSuccess(List<MyBmobInstallation> object) {
                // TODO Auto-generated method stub
                if(object.size() > 0){
                    MyBmobInstallation mbi = object.get(0);
                    mbi.setUserName("");//退出登陆清空用户名
                    mbi.update(SettingActivity.this, new UpdateListener() {

                        @Override
                        public void onSuccess() {
                            // TODO Auto-generated method stub
                            Log.i("bmob", "设备信息更新成功");
                        }

                        @Override
                        public void onFailure(int code, String msg) {
                            // TODO Auto-generated method stub
                            Log.i("bmob", "设备信息更新失败:" + msg);
                        }
                    });
                } else {
                }
            }

            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
            }
        });
    }

    void logout() {
        final ProgressDialog pd = new ProgressDialog(SettingActivity.this);
        String st = getResources().getString(R.string.Are_logged_out);
        pd.setMessage(st);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        //bmob退出
        new Thread(new Runnable() {
            @Override
            public void run() {

                BmobUser.logOut(mContext);
                SharedPreferences setting = getSharedPreferences("setting", 0);
                setting.edit().putBoolean("FIRST", true).apply();
                //环信退出
                DemoHXSDKHelper.getInstance().logout(true,new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        SettingActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                pd.dismiss();
                                // 重新显示登陆页面
                                //((MainActivity) getActivity()).finish();
//                                setResult(15);
                                startAnimActivity(LoginActivity.class);
                                setResult(15);
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }

                    @Override
                    public void onError(int code, String message) {
                        SettingActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                pd.dismiss();
                                showToast("unbind devicetokens failed");
                            }
                        });
                    }
                });
            }
        }).start();
    }
}
