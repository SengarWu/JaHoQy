package com.xpple.jahoqy.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.bean.User;
import com.xpple.jahoqy.ui.otherFragment.UserHelpFragment;
import com.xpple.jahoqy.ui.otherFragment.UserSeekFragment;
import com.xpple.jahoqy.view.RoundImageView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class Shownearuser extends BaseActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    private TextView nin,repu,announce;
    private RoundImageView ima;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    List<Fragment> list = null;
    private String obid;
    private RadioButton ra,rb;
    private RadioGroup rg;
    private Button care,chat;
    private String phone;
    private ImageView sex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shownearuser);
        initTopBarForLeft("侠客信息", R.mipmap.actionbar_brjh, R.color.black_deep);
        initView();
    }

    private void initView() {
        rg=(RadioGroup)findViewById(R.id.rgg);
        ra=(RadioButton)findViewById(R.id.rb_searchhelp);
        rb=(RadioButton)findViewById(R.id.rb_help);
        rg.setOnCheckedChangeListener(this);
        nin=(TextView)findViewById(R.id.nina);
        repu=(TextView)findViewById(R.id.reputation);
        sex=(ImageView)findViewById(R.id.sex);
        sex.setImageResource(R.mipmap.notknow);
        announce=(TextView)findViewById(R.id.jahoannounce);
        ima=(RoundImageView)findViewById(R.id.userphoto);
        care=(Button)findViewById(R.id.care);
        chat=(Button)findViewById(R.id.chat);
        care.setOnClickListener(this);
        chat.setOnClickListener(this);
        Intent intent =getIntent();
        obid=intent.getStringExtra("item");
        BmobQuery<User> query=new BmobQuery<>();
        query.addWhereEqualTo("objectId", obid);
        query.findObjects(this, new FindListener<User>() {//查询多条数据

            @Override
            public void onSuccess(List<User> object) {
                User x = object.get(0);
                if (x.getUsername() != null) {
                    nin.setText(x.getUsername());
                }
                if (x.getGender() != null) {
                    if(x.getGender().equals("男")){
                        sex.setImageResource(R.mipmap.boy);
                    }else{
                        sex.setImageResource(R.mipmap.girl);
                    }
                }
                if (x.getJahoAnnounce() != null) {
                    if (x.getJahoAnnounce().length() > 20) {
                        announce.setText(x.getJahoAnnounce().substring(0, 20) + "...");
                    } else {
                        if (!x.getJahoAnnounce().equals("")) {
                            announce.setText(x.getJahoAnnounce());
                        }
                    }
                }
                //根据用户经验值生成对应称号
                if (x.getExperience() <= -500)
                {
                    repu.setText("无恶不作");
                }
                else if (x.getExperience() > -500 && x.getExperience() <= -300)
                {
                    repu.setText("混世魔王");
                }
                else if (x.getExperience() > -300 && x.getExperience() <= -200)
                {
                    repu.setText("道貌岸然");
                }
                else if (x.getExperience() > -200 && x.getExperience() <= -100)
                {
                    repu.setText("附庸风雅");
                }
                else if (x.getExperience() > -100 && x.getExperience() < 0)
                {
                    repu.setText("经验欠缺");
                }
                else if (x.getExperience() >= 0 && x.getExperience() <= 100)
                {
                    repu.setText("初步江湖");

                }
                else if (x.getExperience() > 100 && x.getExperience() <= 200)
                {
                    repu.setText("江湖小虾");
                }
                else if (x.getExperience() > 200 && x.getExperience() <= 400)
                {
                    repu.setText("明日之星");
                }
                else if (x.getExperience() > 400 && x.getExperience() <= 700)
                {
                    repu.setText("行侠仗义");
                }
                else if (x.getExperience() > 700 && x.getExperience() <= 1100)
                {
                    repu.setText("江湖少侠");
                }
                else if (x.getExperience() > 1100 && x.getExperience() <= 1600)
                {
                    repu.setText("声明远杨");
                }
                else if (x.getExperience() > 1600 && x.getExperience() <= 2200)
                {
                    repu.setText("一代名侠");
                }
                else if (x.getExperience() > 2200 && x.getExperience() <= 2900)
                {
                    repu.setText("名震江湖");
                }
                else if (x.getExperience() > 2900)
                {
                    repu.setText("名冠天下");
                }
                else
                {
                    repu.setText("");
                }
                phone=x.getMobilePhoneNumber();
                if (x.getUserPhoto() != null) {
                    imageLoader.displayImage("http://file.bmob.cn/" + x.getUserPhoto().getUrl(), ima, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                        }

                        @Override
                        public void onLoadingCancelled(String s, View view) {

                        }
                    });
                }


            }

            @Override
            public void onError(int code, String msg) {
                showToast("未知错误发生");
                Shownearuser.this.finish();
            }
        });
        UserSeekFragment u=new UserSeekFragment();
        Bundle data = new Bundle();
        data.putString("obid", obid);
        u.setArguments(data);
        changeFragment(u);
    }


    @Override
    public void onCheckedChanged(RadioGroup arg0, int checkedId) {

        if (checkedId == ra.getId()) {
            UserSeekFragment u=new UserSeekFragment();
            Bundle data = new Bundle();
            data.putString("obid",obid);
            u.setArguments(data);
            changeFragment(u);
        } else if (checkedId ==  rb.getId()) {
            UserHelpFragment uh=new UserHelpFragment();
            Bundle data = new Bundle();
            data.putString("obid",obid);
            uh.setArguments(data);
            changeFragment(uh);
        }
    }

    private void changeFragment(Fragment targetFragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.imfomation, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.care:
                try{
                   EMContactManager.getInstance().addContact(phone, "请求加为好友");
                    showToast("已发出请求");
                }
                catch (EaseMobException e){
                    showToast(e.toString());
                }
                break;
            case R.id.chat:
                startActivity(new Intent(mContext, ChatActivity.class).putExtra("userId", phone));
                break;
        }
    }
}
