package com.xpple.jahoqy.BaseClass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;
import android.widget.Toast;
import com.xpple.jahoqy.CustomApplication;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.bean.User;
import com.xpple.jahoqy.util.CommonUtils;
import com.xpple.jahoqy.view.HeaderLayout;

import cn.bmob.v3.BmobUser;


/**
 * 基类
 *
 * @author nEdAy
 */
public class BaseActivity extends FragmentActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    protected Context mContext;
    protected CustomApplication mApplication;
    protected User CurrentUser;
    public HeaderLayout mHeaderLayout;
    Toast mToast;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = this;
        CurrentUser=BmobUser.getCurrentUser(mContext,User.class);
        mApplication = CustomApplication.getInstance();
    }

    public void showToast(String text) {
        LayoutInflater inflater = this.getLayoutInflater();
        View layout = inflater.inflate(R.layout.diy_toast, (ViewGroup) this.findViewById(R.id.toast_message));
        ((TextView) layout.findViewById(R.id.toast_message)).setText(text);
        if (mToast == null) {
            mToast = new Toast(this.getApplicationContext());
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.setView(layout);
            //toast.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL);
            //toast.setMargin(0, 1);
        } else {
            mToast.setView(layout);
        }
        mToast.show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //曹林
        HXSDKHelper.getInstance().getNotifier().reset();
    }
    /**
     * 打Log ShowLog
     */
    public void ShowLog(String msg) {
        Log.i("life", msg);
    }

    public void startAnimActivity(Class<?> cla) {
        this.startActivity(new Intent(this, cla));
    }

    public void startAnimActivity(Intent intent) {
        this.startActivity(intent);
    }


    public boolean isNetConnected() {
        return CommonUtils.isNetworkAvailable(mContext);
    }


    // 按钮模拟心脏跳动
    public void playHeartbeatAnimation(final View imageView) {
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(new ScaleAnimation(1.0f, 0.9f, 1.0f, 0.9f,
                Animation.RELATIVE_TO_SELF, 0.9f, Animation.RELATIVE_TO_SELF,
                0.9f));
        animationSet.addAnimation(new AlphaAnimation(1.0f, 0.4f));

        animationSet.setDuration(400);
        animationSet.setInterpolator(new AccelerateInterpolator());
        animationSet.setFillAfter(true);

        animationSet.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                AnimationSet animationSet = new AnimationSet(true);
                animationSet.addAnimation(new ScaleAnimation(0.9f, 1.0f, 0.9f,
                        1.0f, Animation.RELATIVE_TO_SELF, 0.9f,
                        Animation.RELATIVE_TO_SELF, 0.9f));
                animationSet.addAnimation(new AlphaAnimation(0.7f, 1.0f));

                animationSet.setDuration(600);
                animationSet.setInterpolator(new DecelerateInterpolator());
                animationSet.setFillAfter(false);

                // 实现心跳的View
                imageView.startAnimation(animationSet);
            }
        });

        // 实现心跳的View
        imageView.startAnimation(animationSet);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // TODO Auto-generated method stub
        //可用于监听设置参数，然后作出响应
    }

    /**
     * 只有title initTopBarLayoutByTitle
     * @Title: initTopBarLayoutByTitle
     * @throws
     *
     */
    //此部分做过修改
    public void initTopBarForOnlyTitle(String titleName,int TopBarBackgroundId,int titleTextColor) {
        mHeaderLayout = (HeaderLayout)findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderLayout.HeaderStyle.DEFAULT_TITLE);
        mHeaderLayout.setDefaultTitle(titleName);
        mHeaderLayout.setBackgroundResource(TopBarBackgroundId);
        mHeaderLayout.setTitleColor(titleTextColor);
    }

    /**
     * 初始化标题栏-带左右按钮
     *
     * @return void
     * @throws
     */
    public void initTopBarForBoth(String titleName, int rightDrawableId,int TopBarBackgroundId,int titleTextColor,
                                  HeaderLayout.onRightImageButtonClickListener listener) {
        mHeaderLayout = (HeaderLayout)findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderLayout.HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
        mHeaderLayout.setTitleAndLeftImageButton(titleName,
                R.drawable.base_action_bar_back_bg_selector,
                new OnLeftButtonClickListener());
        mHeaderLayout.setTitleAndRightImageButton(titleName, rightDrawableId,
                listener);
        mHeaderLayout.setBackgroundResource(TopBarBackgroundId);
        mHeaderLayout.setTitleColor(titleTextColor);
    }

    /**
     * 只有左边按钮和Title initTopBarLayout
     *
     * @throws
     */
    public void initTopBarForLeft(String titleName,int TopBarBackgroundId,int titleTextColor) {
        mHeaderLayout = (HeaderLayout)findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderLayout.HeaderStyle.TITLE_LIFT_IMAGEBUTTON);
        mHeaderLayout.setTitleAndLeftImageButton(titleName,
                R.drawable.base_action_bar_back_bg_selector,
                new OnLeftButtonClickListener());
        mHeaderLayout.setBackgroundResource(TopBarBackgroundId);
        mHeaderLayout.setTitleColor(titleTextColor);
    }

    /** 右边+title
     * initTopBarForRight
     * @return void
     * @throws
     */
    public void initTopBarForRight(String titleName,int rightDrawableId,int TopBarBackgroundId,int titleTextColor,
                                   HeaderLayout.onRightImageButtonClickListener listener) {
        mHeaderLayout = (HeaderLayout)findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderLayout.HeaderStyle.TITLE_RIGHT_IMAGEBUTTON);
        mHeaderLayout.setTitleAndRightImageButton(titleName, rightDrawableId,
                listener);
        mHeaderLayout.setBackgroundResource(TopBarBackgroundId);
        mHeaderLayout.setTitleColor(titleTextColor);
    }

    // 左边按钮的点击事件
    public class OnLeftButtonClickListener implements
            HeaderLayout.onLeftImageButtonClickListener {

        @Override
        public void onClick() {
            finish();
        }
    }

    public void SetTextType(TextView tv)
    {
        //设置TextView字体
        AssetManager mgr=getAssets();
        Typeface tf=Typeface.createFromAsset(mgr, "fonts/hk.TTF");
        tv.setTypeface(tf);
    }


    protected   <T extends View> T $(int resId) {
        return (T) super.findViewById(resId);
    }

}
