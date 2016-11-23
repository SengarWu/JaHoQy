package com.xpple.jahoqy.ui.mainFragment;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xpple.jahoqy.BaseClass.BaseFragment;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.addimages.ImageDownLoader;
import com.xpple.jahoqy.bean.User;
import com.xpple.jahoqy.ui.activity.EditInfoActivity;
import com.xpple.jahoqy.ui.activity.MyHelpActivity;
import com.xpple.jahoqy.ui.activity.MyPublishActivity;
import com.xpple.jahoqy.ui.activity.SettingActivity;
import com.xpple.jahoqy.ui.activity.StoreActivity;
import com.xpple.jahoqy.view.RoundImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.FindListener;

public class MeFragment extends BaseFragment implements View.OnClickListener {

    private View parentview;
    private RoundImageView iv_photo;
    private TextView tv_username;
    private TextView tv_gender;
    private TextView tv_city;
    private TextView tv_gangs;
    private TextView tv_user_title;
    private TextView tv_user_integral;
    private TextView tv_user_jahoAnnounce;
    private TextView tv_edit_info;
    private Button btn_my_publish;
    private Button btn_my_help;
    private Button btn_setting;
    private Button btn_store;
    private Button btn_sign;
    private TextView tv_experience;

    private TextView stv_username;
    private TextView stv_gender;
    private TextView stv_gangs;
    private TextView stv_user_title;
    private TextView stv_user_integral;
    private TextView stv_user_jahoAnnounce;
    private TextView stv_experience;

    private static int userExper;
    private static String userTitle;

    protected ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentview = inflater.inflate(R.layout.fragment_me, container, false);
        initView();
        initTextView();
        return parentview;
    }

    private void initTextView() {
        AssetManager mgr= getResources().getAssets();
        Typeface tf=Typeface.createFromAsset(mgr, "fonts/hk.TTF");
        tv_username.setTypeface(tf);
        tv_gender.setTypeface(tf);
        tv_city.setTypeface(tf);
        tv_gangs.setTypeface(tf);
        tv_user_title.setTypeface(tf);
        tv_user_integral.setTypeface(tf);
        tv_user_jahoAnnounce.setTypeface(tf);
        tv_edit_info.setTypeface(tf);
        tv_experience.setTypeface(tf);
        stv_username.setTypeface(tf);
        stv_gender.setTypeface(tf);
        stv_gangs.setTypeface(tf);
        stv_user_title.setTypeface(tf);
        stv_user_integral.setTypeface(tf);
        stv_user_jahoAnnounce.setTypeface(tf);
        stv_experience.setTypeface(tf);
    }

    @Override
    public void onStart() {
        initData();
        super.onStart();
    }


    private void initView() {
        iv_photo = (RoundImageView) parentview.findViewById(R.id.iv_photo);
        tv_username = (TextView) parentview.findViewById(R.id.tv_username);
        tv_gender = (TextView) parentview.findViewById(R.id.tv_gender);
        tv_city = (TextView) parentview.findViewById(R.id.tv_city);
        tv_gangs = (TextView) parentview.findViewById(R.id.tv_gangs);
        tv_user_title = (TextView) parentview.findViewById(R.id.tv_user_title);
        tv_user_integral = (TextView) parentview.findViewById(R.id.tv_user_integral);
        tv_user_jahoAnnounce = (TextView) parentview.findViewById(R.id.tv_user_jahoAnnounce);
        tv_edit_info = (TextView) parentview.findViewById(R.id.tv_edit_info);
        tv_edit_info.setOnClickListener(this);
        btn_my_publish = (Button) parentview.findViewById(R.id.btn_my_publish);
        btn_my_publish.setOnClickListener(this);
        btn_my_help = (Button) parentview.findViewById(R.id.btn_my_help);
        btn_my_help.setOnClickListener(this);
        btn_setting = (Button) parentview.findViewById(R.id.btn_setting);
        btn_setting.setOnClickListener(this);
        btn_store = (Button) parentview.findViewById(R.id.btn_store);
        btn_store.setOnClickListener(this);
        btn_sign=(Button)parentview.findViewById(R.id.btn_sign);
        btn_sign.setOnClickListener(this);
        tv_experience = (TextView) parentview.findViewById(R.id.tv_experience);

        stv_username = (TextView) parentview.findViewById(R.id.stv_username);
        stv_gender = (TextView) parentview.findViewById(R.id.stv_gender);
        stv_gangs = (TextView) parentview.findViewById(R.id.stv_gangs);
        stv_user_title = (TextView) parentview.findViewById(R.id.stv_user_title);
        stv_user_integral = (TextView) parentview.findViewById(R.id.stv_user_integral);
        stv_user_jahoAnnounce = (TextView) parentview.findViewById(R.id.stv_user_jahoAnnounce);
        stv_experience = (TextView) parentview.findViewById(R.id.stv_experience);
    }

    private void initData() {
        CurrentUser = BmobUser.getCurrentUser(getActivity(), User.class);
        BmobQuery<User> query=new BmobQuery<>();
        query.addWhereEqualTo("objectId",CurrentUser.getObjectId());
        query.findObjects(getActivity(), new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                if(list!=null&&list.size()>0){
                    tv_user_integral.setText(Integer.toString(list.get(0).getUserIntegral()));
                    userExper = list.get(0).getExperience();
                    UerTitle();
                    tv_user_title.setText(userTitle);
                    if (list.get(0).getJahoAnnounce() != null)
                    {
                        tv_user_jahoAnnounce.setText(list.get(0).getJahoAnnounce().toString());
                    }
                    else
                    {
                        tv_user_jahoAnnounce.setText("");
                    }
                    if (list.get(0).getGangsName() != null)
                    {
                        tv_gangs.setText(list.get(0).getGangsName().toString());
                    }
                    else
                    {
                        tv_gangs.setText("");
                    }
                    Boolean sign=list.get(0).getSign();
                    if(sign!=null&&sign){
                        btn_sign.setText("已签到");
                        btn_sign.setEnabled(false);
                    }else{
                        btn_sign.setEnabled(true);
                        btn_sign.setText("签到");
                    }
                    if (list.get(0).getUserPhoto() != null) {
                        ImageDownLoader.showNetImage(getContext(), CurrentUser.getUserPhoto(), iv_photo, R.mipmap.ic_photo);
                    }
                    tv_username.setText(list.get(0).getUsername().toString());
                    if (list.get(0).getGender() != null)
                    {
                        tv_gender.setText(list.get(0).getGender().toString());
                    }
                    else
                    {
                        tv_gender.setText("男");
                    }
                    if (list.get(0).getCity() != null)
                    {
                        tv_city.setText(list.get(0).getCity().toString());
                    }
                    else
                    {
                        tv_city.setText("");
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                showToast("请检查网络设置");
            }
        });

    }

    private void UerTitle() {
        //根据用户经验值生成对应称号
        if (userExper <= -500)
        {
            userTitle = "无恶不作";
            tv_experience.setText(userExper + "/-500");
        }
        else if (userExper > -500 && userExper <= -300)
        {
            userTitle = "混世魔王";
            tv_experience.setText(userExper + "/-300");
        }
        else if (userExper > -300 && userExper <= -200)
        {
            userTitle = "道貌岸然";
            tv_experience.setText(userExper + "/-200");
        }
        else if (userExper > -200 && userExper <= -100)
        {
            userTitle = "附庸风雅";
            tv_experience.setText(userExper + "/-100");
        }
        else if (userExper > -100 && userExper < 0)
        {
            userTitle = "经验欠缺";
            tv_experience.setText(userExper + "/0");
        }
        else if (userExper >= 0 && userExper <= 100)
        {
            userTitle = "初步江湖";
            tv_experience.setText(userExper + "/100");
        }
        else if (userExper > 100 && userExper <= 200)
        {
            userTitle = "江湖小虾";
            tv_experience.setText(userExper + "/200");
        }
        else if (userExper > 200 && userExper <= 400)
        {
            userTitle = "明日之星";
            tv_experience.setText(userExper + "/400");
        }
        else if (userExper > 400 && userExper <= 700)
        {
            userTitle = "行侠仗义";
            tv_experience.setText(userExper + "/700");
        }
        else if (userExper > 700 && userExper <= 1100)
        {
            userTitle = "江湖少侠";
            tv_experience.setText(userExper + "/1100");
        }
        else if (userExper > 1100 && userExper <= 1600)
        {
            userTitle = "声明远杨";
            tv_experience.setText(userExper + "/1600");
        }
        else if (userExper > 1600 && userExper <= 2200)
        {
            userTitle = "一代名侠";
            tv_experience.setText(userExper + "/2200");
        }
        else if (userExper > 2200 && userExper <= 2900)
        {
            userTitle = "名震江湖";
            tv_experience.setText(userExper + "/2900");
        }
        else if (userExper > 2900)
        {
            userTitle = "名冠天下";
            tv_experience.setText(userExper);
        }
        else
        {
            userTitle = "";
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_edit_info:
                startActivityForResult(new Intent(getActivity(), EditInfoActivity.class), 0);
                break;
            case R.id.btn_my_publish:
                System.out.println("click");
                startAnimActivity(MyPublishActivity.class);
                break;
            case R.id.btn_my_help:
                startAnimActivity(MyHelpActivity.class);
                break;
            case R.id.btn_store:
                startAnimActivity(StoreActivity.class);
                break;
            case R.id.btn_setting:
                startAnimActivity(SettingActivity.class);
                break;
            case R.id.btn_sign:
                btn_sign.setEnabled(false);
                JSONObject myJesn = new JSONObject();
                try {
                    myJesn.put("objectId", CurrentUser.getObjectId());

                    AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
                    //第一个参数是上下文对象，第二个参数是云端代码的方法名称，第三个参数是上传到云端代码的参数列表（JSONObject cloudCodeParams），第四个参数是回调类
                    ace.callEndpoint(getActivity(), "sign", myJesn,
                            new CloudCodeListener() {
                                @Override
                                public void onSuccess(Object object) {
                                    String str = object.toString();
                                    if (!str.equals("no")) {
                                        tv_user_integral.setText(str);
                                        showToast("签到成功,送2积分");
                                        btn_sign.setText("已签到");
                                    } else {
                                        btn_sign.setEnabled(true);
                                        showToast("签到失败,请检查网络设置");
                                    }
                                }

                                @Override
                                public void onFailure(int code, String msg) {
                                    // TODO Auto-generated method stub
                                    //检查网络设置
                                    btn_sign.setEnabled(true);
                                    showToast("签到失败,请检查网络设置");
                                }
                            });
                } catch (JSONException e) {
                    btn_sign.setEnabled(true);
                    showToast("签到失败,请检查网络设置");
                }
                break;
        }
    }
    public void  updateInfo(){
        initData();
    }
}
