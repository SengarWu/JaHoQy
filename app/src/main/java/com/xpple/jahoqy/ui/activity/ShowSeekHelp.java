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
import com.xpple.jahoqy.bean.MyBmobInstallation;
import com.xpple.jahoqy.bean.OfferHelp;
import com.xpple.jahoqy.bean.SeekHelp;
import com.xpple.jahoqy.bean.User;
import com.xpple.jahoqy.view.RoundImageView;

import java.util.List;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class ShowSeekHelp extends BaseActivity implements View.OnClickListener {
    private String obid;
    private TextView tit,una,inter,mon,tim,det,peo;
    private RoundImageView ima;
    private ImageView isex,p1,p2,p3;
    private String url1,url2,url3;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private Button ghh;
    private int gnn=0,nnn=0;
    private User a=new User();
   /* private int awardInter=0;
    private int givemon=0;*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_seek_help);
        init();
    }

    private void init() {
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
        ghh=(Button)findViewById(R.id.gh);
        ghh.setOnClickListener(this);
        p1=(ImageView)findViewById(R.id.pic1);
        p2=(ImageView)findViewById(R.id.pic2);
        p3=(ImageView)findViewById(R.id.pic3);
        Intent intent=getIntent();
        obid=intent.getStringExtra("item");
        if(intent.getStringExtra("E_or_Not").equals("map")){
            noExtraquery();
        }else{
            querywithExtra();
        }
    }

    private void noExtraquery(){
        Intent intent =getIntent();
        BmobQuery<SeekHelp> query=new BmobQuery<>();
        query.addWhereEqualTo("objectId",intent.getStringExtra("item"));
        query.include("user");
        query.findObjects(this, new FindListener<SeekHelp>() {//查询多条数据

            @Override
            public void onSuccess(List<SeekHelp> object) {
                SeekHelp x = object.get(0);
                if(x.getUser().getUserPhoto()!=null){
                    imageLoader.displayImage("http://file.bmob.cn/" + x.getUser().getUserPhoto().getUrl(), ima, new ImageLoadingListener() {
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
                if(x.getUser().getUsername()!=null){
                    una.setText(x.getUser().getUsername());
                }
                  if(x.getTitle()!=null) {
                    tit.setText(x.getTitle());
                }
                inter.setText(String.valueOf(x.getAwardInteral()));
                //awardInter=x.getAwardInteral();

                mon.setText(String.valueOf(x.getGivemoney()));
                //givemon=x.getGivemoney();


                if(x.getUser().getGender()!=null) {
                    if (x.getUser().getGender().equals("男")) {
                        isex.setImageResource(R.mipmap.boy);
                    } else {
                        if (x.getUser().getGender().equals("女")) {
                            isex.setImageResource(R.mipmap.girl);
                        } else {
                            isex.setImageResource(R.mipmap.notknow);
                        }
                    }
                }
                nnn=x.getNeedNum();
                gnn=x.getGiveHelpNum();
                peo.setText(String.valueOf(gnn + "/" + nnn));
                tim.setText(x.getUpdatedAt());
                if(x.getDetails()!=null){
                    det.setText(x.getDetails());
                }
                if(x.getPicture1()!=null){
                    imageLoader.displayImage("http://file.bmob.cn/" + x.getPicture1().getUrl(), p1);
                    p1.setOnClickListener(ShowSeekHelp.this);
                    url1=x.getPicture1().getUrl();
                }
                if(x.getPicture2()!=null){
                    imageLoader.displayImage("http://file.bmob.cn/"+x.getPicture2().getUrl(),p2);
                    p2.setOnClickListener(ShowSeekHelp.this);
                    url2=x.getPicture2().getUrl();
                }
                if(x.getPicture3()!=null){
                    imageLoader.displayImage("http://file.bmob.cn/"+x.getPicture3().getUrl(),p3);
                    p3.setOnClickListener(ShowSeekHelp.this);
                    url3=x.getPicture3().getUrl();
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(ShowSeekHelp.this,"无法查看",Toast.LENGTH_LONG);
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
        peo.setText(intent.getStringExtra("gn")+"/"+intent.getStringExtra("nn"));

        gnn= Integer.valueOf(intent.getStringExtra("gn"));
        nnn=Integer.valueOf(intent.getStringExtra("nn"));

        inter.setText(intent.getStringExtra("award"));
        //awardInter=Integer.parseInt(intent.getStringExtra("award"));

        mon.setText(intent.getStringExtra("money"));
        //givemon=Integer.parseInt(intent.getStringExtra("money"));

        BmobQuery<SeekHelp> query=new BmobQuery<>();
        query.addWhereEqualTo("objectId",intent.getStringExtra("item"));
        query.addQueryKeys("details,picture1,picture2,picture3");
        query.findObjects(this, new FindListener<SeekHelp>() {//查询多条数据

            @Override
            public void onSuccess(List<SeekHelp> object) {
                SeekHelp x = object.get(0);
                if(x.getDetails()!=null){
                    det.setText(x.getDetails());
                }
                if(x.getPicture1()!=null){
                    imageLoader.displayImage("http://file.bmob.cn/" + x.getPicture1().getUrl(), p1);
                    p1.setOnClickListener(ShowSeekHelp.this);
                    url1=x.getPicture1().getUrl();
                }
                if(x.getPicture2()!=null){
                    imageLoader.displayImage("http://file.bmob.cn/"+x.getPicture2().getUrl(),p2);
                    p2.setOnClickListener(ShowSeekHelp.this);
                    url2=x.getPicture2().getUrl();
                }
                if(x.getPicture3()!=null){
                    imageLoader.displayImage("http://file.bmob.cn/"+x.getPicture3().getUrl(),p3);
                    p3.setOnClickListener(ShowSeekHelp.this);
                    url3=x.getPicture3().getUrl();
                }

            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(ShowSeekHelp.this,"无法查看",Toast.LENGTH_LONG);
            }
        });
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.gh:
/*                BmobQuery<OfferHelp> query1=new BmobQuery<>();
                SeekHelp shp1=new SeekHelp();
                shp1.setObjectId(obid);
                query1.addWhereEqualTo("sh", shp1);
                query1.count(this, OfferHelp.class, new CountListener() {
                    @Override
                    public void onSuccess(int i) {
                        if(nnn>i){
                            //查看当前用户有没有申请过
                            final BmobQuery<OfferHelp> query=new BmobQuery<>();
                            a.setObjectId(BmobUser.getCurrentUser(mContext).getObjectId());
                            query.addWhereEqualTo("user", a);
                            SeekHelp shp=new SeekHelp();
                            shp.setObjectId(obid);
                            query.addWhereEqualTo("sh",shp);
                            query.count(ShowSeekHelp.this, OfferHelp.class, new CountListener() {
                                @Override
                                public void onFailure(int i, String s) {
                                    showToast("错误");
                                }

                                @Override
                                public void onSuccess(int i) {
                                    if (i == 0) {
                                        OfferHelp x = new OfferHelp();
                                        SeekHelp s = new SeekHelp();
                                        s.setObjectId(obid);
                                        x.setUser(a);
                                        x.setSh(s);
                                        x.setResult("2");
                                        x.setAccept("0");
                                        x.save(ShowSeekHelp.this, new SaveListener() {
                                            @Override
                                            public void onSuccess() {
                                                BmobInstallation.getCurrentInstallation(ShowSeekHelp.this).save();
                                                BmobPushManager bmobPushManager=new BmobPushManager(ShowSeekHelp.this);
                                                BmobQuery<MyBmobInstallation> query = MyBmobInstallation.getQuery();
                                                query.addWhereEqualTo("userName",una.getText());
                                                bmobPushManager.setQuery(query);
                                                bmobPushManager.pushMessage(",wh," + CurrentUser.getUsername()+","+obid+",");
                                                showToast("申请成功，等待对方确认,感谢您仗义出手");
                                            }

                                            @Override
                                            public void onFailure(int i, String s) {
                                                showToast("申请失败");
                                            }
                                        });
                                    } else {
                                        showToast("您已申请过");
                                    }
                                }
                            });
                        }else{
                            showToast("人数已满");
                        }
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });*/
                  //查看当前用户有没有申请过
                  final BmobQuery<OfferHelp> query=new BmobQuery<>();
                  a.setObjectId(BmobUser.getCurrentUser(mContext).getObjectId());
                  query.addWhereEqualTo("user", a);
                  SeekHelp shp=new SeekHelp();
                  shp.setObjectId(obid);
                  query.addWhereEqualTo("sh",shp);
                  query.count(this, OfferHelp.class, new CountListener() {
                      @Override
                      public void onFailure(int i, String s) {
                          showToast("错误");
                      }

                      @Override
                      public void onSuccess(int i) {
                          if (i == 0) {
                          //查看人数是否已满
                          BmobQuery<OfferHelp> query1=new BmobQuery<>();
                          query1.addWhereEqualTo("accept","1");
                          SeekHelp shp1=new SeekHelp();
                          shp1.setObjectId(obid);
                          query1.addWhereEqualTo("sh", shp1);
                          query1.count(ShowSeekHelp.this, OfferHelp.class, new CountListener() {
                              @Override
                              public void onSuccess(int i) {
                                  if (nnn > i) {//人数未满
                                      //申请帮忙
                                      OfferHelp x = new OfferHelp();
                                      SeekHelp s = new SeekHelp();
                                      s.setObjectId(obid);
                                      x.setUser(a);
                                      x.setSh(s);
                                      x.setResult("2");
                                      x.setAccept("0");
                                      x.save(ShowSeekHelp.this, new SaveListener() {
                                          @Override
                                          public void onSuccess() {
                                              BmobInstallation.getCurrentInstallation(ShowSeekHelp.this).save();
                                              BmobPushManager bmobPushManager = new BmobPushManager(ShowSeekHelp.this);
                                              BmobQuery<MyBmobInstallation> query = MyBmobInstallation.getQuery();
                                              query.addWhereEqualTo("userName", una.getText());
                                              bmobPushManager.setQuery(query);
                                              bmobPushManager.pushMessage(",wh," + CurrentUser.getUsername() + "," + obid + ",");
                                              showToast("申请成功，等待对方确认,感谢您仗义出手");
                                          }

                                          @Override
                                          public void onFailure(int i, String s) {
                                              showToast("申请失败");
                                          }
                                      });
                                  } else {
                                      showToast("人数已满");
                                  }
                              }

                              @Override
                              public void onFailure(int i, String s) {

                              }
                          });
                          } else {
                              showToast("您已申请过");
                          }
                      }
                  });
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
