package com.xpple.jahoqy.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.BaseClass.Constant;
import com.xpple.jahoqy.BaseClass.DemoHXSDKHelper;
import com.xpple.jahoqy.BaseClass.HXSDKHelper;
import com.xpple.jahoqy.BaseClass.UserDao;
import com.xpple.jahoqy.CustomApplication;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.bean.HuanXinUser;
import com.xpple.jahoqy.bean.MyBmobInstallation;
import com.xpple.jahoqy.bean.User;
import com.xpple.jahoqy.proxy.UserProxy;
import com.xpple.jahoqy.util.StringUtils;
import com.xpple.jahoqy.view.DeletableEditText;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindCallback;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class LoginActivity extends BaseActivity implements View.OnClickListener, UserProxy.ILoginListener {

    private DeletableEditText et_phone;
    private DeletableEditText et_password;
    private Button btn_login;
    private TextView tv_forget_password;
    private TextView tv_register;

    private UserProxy userProxy;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        setupView();
        userProxy = new UserProxy(mContext);

    }


    private void initView() {
        et_phone = $(R.id.et_phone);
        et_password = $(R.id.et_password);
        tv_forget_password = $(R.id.tv_forget_password);
        tv_forget_password.setOnClickListener(this);
        btn_login = $(R.id.btn_login);
        btn_login.setOnClickListener(this);
        tv_register = $(R.id.tv_register);
        tv_register.setOnClickListener(this);

        progress = new ProgressDialog(LoginActivity.this);
        if (!isNetConnected())
        {
            showToast("网络连接不可用，请检查网络");
            return;
        }

    }

    private void setupView() {
        tv_forget_password.setText(Html.fromHtml("<u>忘记密码</u>"));
        tv_register.setText(Html.fromHtml("<u>注册</u>"));
        //当回传的手机号和密码不为空时将注册完成的手机号和密码自动填写
        if(!TextUtils.isEmpty(getIntent().getStringExtra("phone")) &&
                !TextUtils.isEmpty(getIntent().getStringExtra("password")))
        {
            et_phone.setText(getIntent().getStringExtra("phone"));
            et_password.setText(getIntent().getStringExtra("password"));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_login:
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
                if (!StringUtils.isPhoneNumberValid(et_phone.getText().toString())) {
                    et_phone.setShakeAnimation();
                    showToast("手机号格式不正确");
                    return;
                }
                if (TextUtils.isEmpty(et_password.getText())) {
                    et_password.setShakeAnimation();
                    showToast("请输入密码");
                    return;
                }
                if (!StringUtils.isValidPassword(et_password.getText().toString())) {
                    et_password.setShakeAnimation();
                    showToast("密码长度不正确，请输入6-16位");
                    return;
                }
                progress.setMessage("正在登录...");
                progress.setCanceledOnTouchOutside(false);
                progress.show();
                userProxy.setOnLoginListener(this);
                userProxy.login(et_phone.getText().toString(), et_password.getText().toString());
                break;
            case R.id.tv_forget_password:
                startAnimActivity(FindPasswordActivity.class);
                break;
            case R.id.tv_register:
                startAnimActivity(Register1Activity.class);
                break;
        }
    }


    @Override
    public void onLoginSuccess() {
        try{
            //进入主界面
            BmobQuery<User> query = new BmobQuery<User>();
            query.addWhereEqualTo("mobilePhoneNumber", et_phone.getText().toString());
            query.findObjects(mContext, new FindListener<User>() {
                @Override
                public void onSuccess(List<User> list) {
                    if(list.size()<1){
                        showToast("无效的用户名和密码");
                        return;
                    }
                    final String password=list.get(0).getHuanxinPassword();
                    final String phone=et_phone.getText().toString();
                    EMChatManager.getInstance().login(phone, password, new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            // 登陆成功，保存用户名密码
                            CustomApplication.getInstance().setUserName(et_phone.getText().toString());
                            CustomApplication.getInstance().setPassword(et_password.getText().toString());

                            try {
                                // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                                // ** manually load all local groups and
                                EMGroupManager.getInstance().loadAllGroups();
                                EMChatManager.getInstance().loadAllConversations();
                                // 处理好友和群组
                                initializeContacts();
                            } catch (Exception e) {
                                e.printStackTrace();
                                // 取好友或者群聊失败，不让进入主页面
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        progress.dismiss();
                                        DemoHXSDKHelper.getInstance().logout(true, null);
                                        Toast.makeText(getApplicationContext(), R.string.login_failure_failed, Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return;
                            }
                            if (!LoginActivity.this.isFinishing() && progress.isShowing()) {
                                progress.dismiss();
                            }

                            //下次自动登录
                            SharedPreferences setting = getSharedPreferences("setting", 0);
                            setting.edit().putBoolean("FIRST", false).apply();
                            // 进入主页面
                            Intent intent = new Intent(LoginActivity.this,
                                    MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onProgress(int progress, String status) {
                        }

                        @Override
                        public void onError(final int code, final String message) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    progress.dismiss();
                                    Toast.makeText(mContext, getString(R.string.Login_failed) + message,
                                            Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    });
                }

                @Override
                public void onError(int i, String s) {

                }
            });
        }catch (Exception e){
            showToast(e.getMessage().toString());
        }
    }


    @Override
    public void onLoginFailure(String msg) {
        progress.dismiss();
        showToast("登录失败" + msg);
    }
    private void initializeContacts() {
        Map<String, HuanXinUser> userlist = new HashMap<String, HuanXinUser>();
        // 添加user"申请与通知"
        HuanXinUser newFriends = new HuanXinUser();
        newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
        String strChat = getResources().getString(
                R.string.Application_and_notify);
        newFriends.setNick(strChat);
        userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);

        // 添加"群聊"
        HuanXinUser groupUser = new HuanXinUser();
        String strGroup = getResources().getString(R.string.group_chat);
        groupUser.setUsername(Constant.GROUP_USERNAME);
        groupUser.setNick(strGroup);
        groupUser.setHeader("");
        userlist.put(Constant.GROUP_USERNAME, groupUser);

        // 添加"Robot"
        HuanXinUser robotUser = new HuanXinUser();
        String strRobot = getResources().getString(R.string.robot_chat);
        robotUser.setUsername(Constant.CHAT_ROBOT);
        robotUser.setNick(strRobot);
        robotUser.setHeader("");
        userlist.put(Constant.CHAT_ROBOT, robotUser);

        // 存入内存
        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).setContactList(userlist);

        // 存入db
        UserDao dao = new UserDao(LoginActivity.this);
        List<HuanXinUser> users = new ArrayList<HuanXinUser>(userlist.values());
        dao.saveContactList(users);
    }
}
