package com.xpple.jahoqy;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.baidu.location.LocationClient;
import com.xpple.jahoqy.config.Config;
import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.ui.activity.LoginActivity;
import com.xpple.jahoqy.ui.activity.MainActivity;

import cn.bmob.im.BmobChat;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobUser;

/**
 * 引导页
 */
public class SplashActivity extends BaseActivity {

    private static final int GO_HOME = 100;
    private static final int GO_GUIDE = 200;

    // 定位获取当前用户的地理位置
    private LocationClient mLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // 初始化 Bmob SDK 使用时请将第二个参数Application ID替换成你在Bmob服务器端创建的Application ID
        Bmob.initialize(this, Config.BMOB_KEY);

        //保存设备信息
        BmobInstallation.getCurrentInstallation(this).save();

        // 初始化推送
        BmobChat.getInstance(this).init(Config.BMOB_KEY);
        //定时检测
        BmobChat.getInstance(this).startPollService(30);
        // 开启定位
        initLocClient();
    }


    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences setting = getSharedPreferences("setting", 0);
        Boolean user_first = setting.getBoolean("FIRST", true);
        if (!user_first) {
             //调用sdk注册方法
            mHandler.sendEmptyMessageDelayed(GO_HOME, 2000);
        } else {
            mHandler.sendEmptyMessageDelayed(GO_GUIDE, 2000);
       }
    }

     /*
     * 开启定位，更新当前用户的经纬度坐标i
     */
    private void initLocClient() {
        mLocationClient = CustomApplication.getInstance().mLocationClient;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_HOME:
                    startAnimActivity(MainActivity.class);
                    finish();
                    break;
                case GO_GUIDE:
                    startAnimActivity(LoginActivity.class);
                    finish();
                    break;
            }
        }
    };


    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
        super.onDestroy();
    }
}



