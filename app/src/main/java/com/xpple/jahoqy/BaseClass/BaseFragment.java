package com.xpple.jahoqy.BaseClass;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.bean.User;
import com.xpple.jahoqy.util.CommonUtils;

import cn.bmob.v3.BmobUser;

/**
 * Fragmenet 基类
 */
public class BaseFragment extends Fragment {
    public static String TAG;
    protected View contentView;

    private Handler handler = new Handler();

    public LayoutInflater mInflater;

    protected User CurrentUser;;

    public void runOnWorkThread(Runnable action) {
        new Thread(action).start();
    }

    public void runOnUiThread(Runnable action) {
        handler.post(action);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        CurrentUser = BmobUser.getCurrentUser(getActivity(),User.class); //获取当前用户
        mInflater = LayoutInflater.from(getActivity());
    }


    Toast mToast;

    public void showToast(String text) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
         View layout = inflater.inflate(R.layout.diy_toast, (ViewGroup) getActivity().findViewById(R.id.toast_message));
        ((TextView) layout.findViewById(R.id.toast_message)).setText(text);
        if (mToast == null) {
            mToast = new Toast(getActivity().getApplicationContext());
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.setView(layout);
        } else {
            mToast.setView(layout);
        }
        mToast.show();
    }

    public void showToast(int text) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
       View layout = inflater.inflate(R.layout.diy_toast, (ViewGroup) getActivity().findViewById(R.id.toast_message));
        ((TextView) layout.findViewById(R.id.toast_message)).setText(text);
        if (mToast == null) {
            mToast = new Toast(getActivity().getApplicationContext());
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.setView(layout);
        } else {
            mToast.setView(layout);
        }
        mToast.show();
    }

    public boolean isNetConnected() {
        return CommonUtils.isNetworkAvailable(getActivity());
    }

    public void startAnimActivity(Class<?> cla) {
        this.startActivity(new Intent(getActivity(), cla));
    }

    public void startAnimActivity(Intent intent) {
        this.startActivity(intent);
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

    /**
     * 打Log ShowLog
     */
    public void ShowLog(String msg) {
        Log.i("life", msg);
    }


}
