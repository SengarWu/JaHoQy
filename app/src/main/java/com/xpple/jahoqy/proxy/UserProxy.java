package com.xpple.jahoqy.proxy;

import android.content.Context;

import com.xpple.jahoqy.bean.User;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;

/**
 * Created by Sengar Wu on 2015/7/31.
 */
public class UserProxy {

    private Context mContext;

    public UserProxy(Context context) {
        this.mContext = context;
    }

    public void requestSMSCode(String phone) {
        BmobSMS.requestSMSCode(mContext, phone, "注册模板", new RequestSMSCodeListener() {
            @Override
            public void done(Integer smsId, BmobException ex) {
                if (ex == null && requestSmsCodeListener != null) {//验证码发送成功
                    requestSmsCodeListener.onRequestSmsCodeSuccess();
                } else {
                    if (requestSmsCodeListener != null) {
                        requestSmsCodeListener.onRequestSmsCodeFailure(ex.getLocalizedMessage());
                    }
                }
            }
        });
    }

    public interface IRequestSmsCodeListener {
        void onRequestSmsCodeSuccess();

        void onRequestSmsCodeFailure(String msg);
    }

    private IRequestSmsCodeListener requestSmsCodeListener;

    public void setOnRequestSmsCodeListener(IRequestSmsCodeListener requestSmsCodeListener) {
        this.requestSmsCodeListener = requestSmsCodeListener;
    }

    //验证码验证操作
    public void verifySmsCode(String phone,String code)
    {
        BmobSMS.verifySmsCode(mContext, phone, code, new VerifySMSCodeListener() {
            @Override
            public void done(BmobException ex) {
                // TODO Auto-generated method stub
                if (ex == null) {//短信验证码已验证成功
                    if (verifySmsCodeListener != null) {
                        verifySmsCodeListener.onVerifySmsCodeSuccess();
                    }
                } else {
                    if (verifySmsCodeListener != null) {
                        verifySmsCodeListener.OnVerifySmsCodeFailure(ex.getLocalizedMessage());
                    }
                }
            }
        });
    }


    public interface IVerifySmsCodeListener {
        void onVerifySmsCodeSuccess();

        void OnVerifySmsCodeFailure(String msg);
    }

    private IVerifySmsCodeListener verifySmsCodeListener;

    public void setOnVerifySmsCodeListener(IVerifySmsCodeListener verifySmsCodeListener) {
        this.verifySmsCodeListener = verifySmsCodeListener;
    }



    public User getCurrentUser() {
        User user = BmobUser.getCurrentUser(mContext, User.class);
        if (user != null) {
            return user;
        }
        return null;
    }

    //登录：手机号，密码
    public void login(String phone, String password) {
        BmobUser.loginByAccount(mContext, phone, password, new LogInListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                // TODO Auto-generated method stub
                if (user != null) {
                    if (loginListener != null) {
                        loginListener.onLoginSuccess();
                    }
                } else {
                    if (loginListener != null) {
                        loginListener.onLoginFailure(e.getLocalizedMessage());
                    }
                }
            }
        });
    }


    public interface ILoginListener {
        void onLoginSuccess();

        void onLoginFailure(String msg);
    }

    private ILoginListener loginListener;

    public void setOnLoginListener(ILoginListener loginListener) {
        this.loginListener = loginListener;
    }

    public void logout() {
        BmobUser.logOut(mContext);
    }

    public void update(String... args) {
        User user = getCurrentUser();
        user.setUsername(args[0]);
        user.setEmail(args[1]);
        user.setPassword(args[2]);
        //...
        user.update(mContext, new UpdateListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                if (updateListener != null) {
                    updateListener.onUpdateSuccess();
                }
            }

            @Override
            public void onFailure(int arg0, String msg) {
                // TODO Auto-generated method stub
                if (updateListener != null) {
                    updateListener.onUpdateFailure(msg);
                }
            }
        });
    }

    public interface IUpdateListener {
        void onUpdateSuccess();

        void onUpdateFailure(String msg);
    }

    private IUpdateListener updateListener;

    public void setOnUpdateListener(IUpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    public void resetPasswordOnCloud(final String phone, final String newpassword, String code) {
        BmobSMS.verifySmsCode(mContext, phone, code, new VerifySMSCodeListener() {
            @Override
            public void done(BmobException ex) {
                // TODO Auto-generated method stub
                if (ex == null) {//短信验证码已验证成功
                    if (verifySmsCodeListener != null) {
                        verifySmsCodeListener.onVerifySmsCodeSuccess();
                    }
                    JSONObject params = new JSONObject();
                    try {
                        params.put("phone", phone);
                        params.put("password", newpassword);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
                    //第一个参数是上下文对象，第二个参数是云端代码的方法名称，第三个参数是上传到云端代码的参数列表（JSONObject cloudCodeParams），第四个参数是回调类
                    ace.callEndpoint(mContext, "resetPassword", params,
                            new CloudCodeListener() {
                                @Override
                                public void onSuccess(Object object) {
                                    // TODO Auto-generated method stub
                                    if (resetPasswordListener != null) {
                                        resetPasswordListener.onResetSuccess();
                                    }
                                }

                                @Override
                                public void onFailure(int code, String msg) {
                                    // TODO Auto-generated method stub
                                    if (resetPasswordListener != null) {
                                        resetPasswordListener.onResetFailure(msg);
                                    }
                                }
                            }
                    );
                } else {
                    if (verifySmsCodeListener != null) {
                        verifySmsCodeListener.OnVerifySmsCodeFailure(ex.getLocalizedMessage());
                    }
                }
            }
        });

    }

    public interface IResetPasswordListener {
        void onResetSuccess();

        void onResetFailure(String msg);

    }

    private IResetPasswordListener resetPasswordListener;

    public void setOnResetPasswordListener(IResetPasswordListener resetPasswordListener) {
        this.resetPasswordListener = resetPasswordListener;
    }
}
