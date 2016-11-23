package com.xpple.jahoqy.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.bean.OfferHelp;
import com.xpple.jahoqy.bean.SeekHelp;
import com.xpple.jahoqy.bean.User;
import com.xpple.jahoqy.view.RoundImageView;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class Showmytaskhelp extends BaseActivity implements View.OnClickListener{
    private String obid;
    private TextView tit,una,inter,mon,tim,det,peo,com;
    private RoundImageView ima;
    private ImageView isex,p1,p2,p3,result;
    private String url1,url2,url3;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private Button ghh;
    private int gnn=0,nnn=0;
    private User a=new User();
    private String Lo,La;
    private String state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showmytaskhelp);
        init();
    }

    private void init() {
        result=(ImageView)findViewById(R.id.result);
        com=(TextView)findViewById(R.id.comment);
        p1=(ImageView)findViewById(R.id.pic1);
        p2=(ImageView)findViewById(R.id.pic2);
        p3=(ImageView)findViewById(R.id.pic3);
        initTopBarForLeft("详情", R.mipmap.actionbar_xxzy, R.color.black_deep);
        una=(TextView)findViewById(R.id.username);
        ima=(RoundImageView)findViewById(R.id.imageView);
        tit=(TextView)findViewById(R.id.tt);
        inter=(TextView)findViewById(R.id.Award);
        mon=(TextView)findViewById(R.id.mon);
        tim=(TextView)findViewById(R.id.time);
        det=(TextView)findViewById(R.id.details);
        isex=(ImageView)findViewById(R.id.sex);
        peo=(TextView)findViewById(R.id.peo);
        ghh=(Button)findViewById(R.id.gohelp);
        ghh.setOnClickListener(this);
        p1=(ImageView)findViewById(R.id.pic1);
        p2=(ImageView)findViewById(R.id.pic2);
        p3=(ImageView)findViewById(R.id.pic3);
        Intent intent=getIntent();
        String ex=intent.getStringExtra("ABC");
        if(ex.equals("Yes")) {
            obid = intent.getStringExtra("item");
            Lo = intent.getStringExtra("Lo");
            La = intent.getStringExtra("La");
            state = intent.getStringExtra("state");
            querywithExtra();
        }else{
            obid=intent.getStringExtra("shid");
            querywithNOExtra();
        }
    }

    private void querywithNOExtra() {
        Intent intent =getIntent();
        BmobQuery<SeekHelp> query=new BmobQuery<>();
        query.addWhereEqualTo("objectId", intent.getStringExtra("shid"));
        query.findObjects(this, new FindListener<SeekHelp>() {//查询多条数据

            @Override
            public void onSuccess(List<SeekHelp> object) {
                SeekHelp x = object.get(0);
                Lo=String.valueOf(x.getAddress().getLongitude());
                La=String.valueOf(x.getAddress().getLatitude());
                una.setText(x.getUser().getUsername());
                tim.setText(x.getUpdatedAt());
                tit.setText(x.getTitle());
                peo.setText(String.valueOf(x.getGiveHelpNum())+"/"+String.valueOf(x.getNeedNum()));
                inter.setText(String.valueOf(x.getAwardInteral()));
                mon.setText(String.valueOf(x.getGivemoney()));
                state=x.getState();
                if(x.getUser().getGender()!=null){
                    if(x.getUser().getGender().equals("男")){
                        isex.setImageResource(R.mipmap.boy);
                    }else{
                        isex.setImageResource(R.mipmap.girl);
                    }
                }
                if (x.getDetails() != null) {
                    det.setText(x.getDetails());
                }
                if (x.getPicture1() != null) {
                    imageLoader.displayImage("http://file.bmob.cn/" + x.getPicture1().getUrl(), p1);
                    p1.setOnClickListener(Showmytaskhelp.this);
                    url1 = x.getPicture1().getUrl();
                }
                if (x.getPicture2() != null) {
                    imageLoader.displayImage("http://file.bmob.cn/" + x.getPicture2().getUrl(), p2);
                    p2.setOnClickListener(Showmytaskhelp.this);
                    url2 = x.getPicture2().getUrl();
                }
                if (x.getPicture3() != null) {
                    imageLoader.displayImage("http://file.bmob.cn/" + x.getPicture3().getUrl(), p3);
                    p3.setOnClickListener(Showmytaskhelp.this);
                    url3 = x.getPicture3().getUrl();
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(Showmytaskhelp.this, "无法查看", Toast.LENGTH_LONG);
            }
        });
    }

    private void querywithExtra(){
        Intent intent =getIntent();
        if(intent.hasExtra("url")){
            imageLoader.displayImage("http://file.bmob.cn/" + intent.getStringExtra("url"), ima, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    Toast.makeText(mContext, failReason.toString(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }
        una.setText(intent.getStringExtra("username"));
        tim.setText(intent.getStringExtra("time"));
        tit.setText(intent.getStringExtra("title"));
        peo.setText(intent.getStringExtra("gn") + "/" + intent.getStringExtra("nn"));
        gnn= Integer.valueOf(intent.getStringExtra("gn"));
        nnn=Integer.valueOf(intent.getStringExtra("nn"));
        if(intent.hasExtra("award")&&intent.getStringExtra("award")!=null){
            inter.setText(intent.getStringExtra("award"));
        }else{
            inter.setText(intent.getStringExtra("0"));
        }
        if(intent.hasExtra("money")&&intent.getStringExtra("money")!=null){
            mon.setText(intent.getStringExtra("money"));
        }else{
            mon.setText(intent.getStringExtra("0"));
        }
        if(intent.hasExtra("result")){
            if(intent.getStringExtra("result").equals("0")){
                   result.setImageResource(R.mipmap.chaping);
            }
            if(intent.getStringExtra("result").equals("1")){
                result.setImageResource(R.mipmap.haoping);
            }
        }
        if(intent.hasExtra("comment")){
            com.setText(intent.getStringExtra("comment"));
        }
        if(intent.hasExtra("sex")){
            String sex=intent.getStringExtra("sex");
            if(sex.equals("男")) {
                isex.setImageResource(R.mipmap.boy);
            }else{
                isex.setImageResource(R.mipmap.girl);
            }
        }
        //再次查询评价以及结果
        BmobQuery<OfferHelp> query1=new BmobQuery<>();
        query1.addQueryKeys("comment,result");
        query1.addWhereEqualTo("objectId", intent.getStringExtra("ofid"));
        query1.findObjects(this, new FindListener<OfferHelp>() {
            @Override
            public void onSuccess(List<OfferHelp> list) {
                OfferHelp a=list.get(0);
                com.setText(a.getComment());
                if(a.getResult().equals("0")){
                    state="1";//设置事务结束标志，求助用户操作肯能有遗漏
                    result.setImageResource(R.mipmap.chaping);
                }else{
                    if (a.getResult().equals("1")) {
                        state = "1";//设置事务结束标志，求助用户操作肯能有遗漏
                        result.setImageResource(R.mipmap.haoping);
                    }
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });

        BmobQuery<SeekHelp> query=new BmobQuery<>();
        query.addWhereEqualTo("objectId", intent.getStringExtra("item"));
        query.addQueryKeys("details,picture1,picture2,picture3");//添加state
        query.findObjects(this, new FindListener<SeekHelp>() {//查询多条数据

            @Override
            public void onSuccess(List<SeekHelp> object) {
                SeekHelp x = object.get(0);
                //state=x.getState();//再次读取求助的状态
                if (x.getDetails() != null) {
                    det.setText(x.getDetails());
                }
                if (x.getPicture1() != null) {
                    imageLoader.displayImage("http://file.bmob.cn/" + x.getPicture1().getUrl(), p1);
                    p1.setOnClickListener(Showmytaskhelp.this);
                    url1 = x.getPicture1().getUrl();
                }
                if (x.getPicture2() != null) {
                    imageLoader.displayImage("http://file.bmob.cn/" + x.getPicture2().getUrl(), p2);
                    p2.setOnClickListener(Showmytaskhelp.this);
                    url2 = x.getPicture2().getUrl();
                }
                if (x.getPicture3() != null) {
                    imageLoader.displayImage("http://file.bmob.cn/" + x.getPicture3().getUrl(), p3);
                    p3.setOnClickListener(Showmytaskhelp.this);
                    url3 = x.getPicture3().getUrl();
                }

            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(Showmytaskhelp.this, "无法查看", Toast.LENGTH_LONG);
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.gohelp:
                if(state.equals("0")) {
                    Intent intent4 = new Intent(mContext, GuideMap.class);
                    intent4.putExtra("Lo", Lo);
                    intent4.putExtra("La", La);
                    startActivity(intent4);
                }else{
                    showToast("事务已结束");
                }
                break;
            case R.id.pic1:
                Intent intent1=new Intent(mContext,ShowImage.class);
                intent1.putExtra("url",url1);
                startActivity(intent1);
                break;
            case R.id.pic2:
                Intent intent2=new Intent(mContext,ShowImage.class);
                intent2.putExtra("url",url2);
                startActivity(intent2);
                break;
            case R.id.pic3:
                Intent intent3=new Intent(mContext,ShowImage.class);
                intent3.putExtra("url",url3);
                startActivity(intent3);
                break;
        }
    }
}
