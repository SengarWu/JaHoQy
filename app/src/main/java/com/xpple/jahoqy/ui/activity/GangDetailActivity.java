package com.xpple.jahoqy.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.addimages.ImageDownLoader;
import com.xpple.jahoqy.bean.ApplicationGangs;
import com.xpple.jahoqy.bean.Gangs;
import com.xpple.jahoqy.bean.User;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.SaveListener;


/**
 * Created by caolin on 2015/10/4.
 */
public class GangDetailActivity extends BaseActivity {

    private Gangs myGangs;
    private TextView gangName;
    private TextView gangNumber;
    private TextView gangCreater;
    private TextView gangGrade;
    private TextView gangPurpose;
    private TextView selectType;
    private ImageView gangHeader;
    private EditText gangreason;
    private TextView submit;
    private int[] number=new int[]{
            21,24,27,
            31,34,37,
            41,44,47,
            52,54,57,
    };
    private int selectTypeNumebr=21;
    String[] gangsStr=new String[]{
            "青龙堂青木坛","青龙堂白马坛","青龙堂红袖坛",
            "白虎堂青木坛","白虎堂白马坛","白虎堂红袖坛",
            "朱雀堂青木坛","朱雀堂白马坛","朱雀堂红袖坛",
            "玄武堂青木坛","玄武堂白马坛","玄武堂红袖坛",};
    AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gang_detail);
        myGangs=(Gangs)getIntent().getSerializableExtra("gang");
        initView();
        initData(myGangs);
    }

    private void initData(final Gangs myGangs) {
        gangName.setText(myGangs.getGangsName());
        gangGrade.setText(myGangs.getGangsGrade()+"");
        gangCreater.setText(myGangs.getGangsCreater());
        gangPurpose.setText(myGangs.getGangsPurpose());
        ImageDownLoader.showNetImage(mContext, myGangs.getGangsPhoto(), gangHeader, R.drawable.choice_2);
        try {
            BmobQuery<User> query = new BmobQuery<>();
            query.addWhereEqualTo("gangsName", myGangs.getGangsName());
            query.count(mContext, User.class, new CountListener() {
                @Override
                public void onSuccess(int count) {
                    // TODO Auto-generated method stub
                    final String co = count + "";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gangNumber.setText(co);
                        }
                    });
                }

                @Override
                public void onFailure(int code, String msg) {
                    // TODO Auto-generated method stu
                }
            });
        }catch (Exception e){
            showToast(e.getMessage().toString());
        }
        selectType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                alertDialog.setTitle("帮派香会").setIcon(R.drawable.ic_launcher);
                alertDialog.setSingleChoiceItems(gangsStr, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectType.setText(gangsStr[i]);
                        selectTypeNumebr = number[i];
                        dialog.dismiss();
                    }
                });
                dialog=alertDialog.create();
                dialog.show();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(gangreason.getText())){
                    showToast("申请理由不能为空！！！");
                    return;
                }
                if(TextUtils.isEmpty(selectType.getText())){
                    showToast("请选择添加的帮派香会");
                    return;
                }
                String nickName= BmobUser.getCurrentUser(mContext).getUsername();
                String gangname=myGangs.getGangsName();
                String reason=gangreason.getText().toString();
                ApplicationGangs applicationGangs = new ApplicationGangs();
                applicationGangs.setpplicationGangs(nickName,gangname,reason,selectTypeNumebr);
                applicationGangs.save(mContext, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        showToast("申请成功");
                        setResult(16);
                        finish();
                    }

                    @Override
                    public void onFailure(int code, String arg0) {
                        // TODO Auto-generated method stub
                        showToast("申请失败，请检查网络设置！");
                    }
                });
            }
        });
    }

    private void initView() {
        initTopBarForLeft("帮派详情", R.mipmap.actionbar_gangs, R.color.color_transparent_bg);
        gangName=$(R.id.tv_gangs_name);
        gangGrade=$(R.id.tv_gangs_grade);
        gangCreater=$(R.id.tv_gangs_founder);
        gangNumber=$(R.id.tv_gangs_number);
        gangPurpose=$(R.id.tv_gangs_purpose);
        gangHeader=$(R.id.iv_gangs_header);
        gangreason=$(R.id.tv_gang_reason);
        selectType=$(R.id.tv_select_type);
        submit=$(R.id.submit);
    }

}
