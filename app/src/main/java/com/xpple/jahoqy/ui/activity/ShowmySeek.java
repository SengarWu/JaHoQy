package com.xpple.jahoqy.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.bean.SeekHelp;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class ShowmySeek extends BaseActivity implements View.OnClickListener {
    private String obid;
    private TextView tit,inter,mon,tim,det,peo;
    private ImageView p1,p2,p3;
    private String url1,url2,url3;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private int nnn,gnn,AIn,givemon;
    private Button ac,end;
    private String state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showmy_seek);
        init();
    }
    private void init() {
        ac=(Button)findViewById(R.id.accept);
        ac.setOnClickListener(this);
        end=(Button)findViewById(R.id.end);
        end.setOnClickListener(this);
        p1=(ImageView)findViewById(R.id.pic1);
        p2=(ImageView)findViewById(R.id.pic2);
        p3=(ImageView)findViewById(R.id.pic3);
        initTopBarForLeft("详情", R.mipmap.actionbar_xxzy, R.color.black_deep);
        tit=(TextView)findViewById(R.id.tt);
        inter=(TextView)findViewById(R.id.Award);
        mon=(TextView)findViewById(R.id.mon);
        tim=(TextView)findViewById(R.id.time);
        det=(TextView)findViewById(R.id.details);
        peo=(TextView)findViewById(R.id.peo);
        p1=(ImageView)findViewById(R.id.pic1);
        p2=(ImageView)findViewById(R.id.pic2);
        p3=(ImageView)findViewById(R.id.pic3);
        Intent intent=getIntent();
        obid=intent.getStringExtra("item");
        querywithExtra();
    }
    private void querywithExtra(){
        Intent intent =getIntent();
        state=intent.getStringExtra("state");
        tim.setText(intent.getStringExtra("time"));
        tit.setText(intent.getStringExtra("title"));
        peo.setText(intent.getStringExtra("gn")+"/"+intent.getStringExtra("nn"));
        gnn=Integer.parseInt(intent.getStringExtra("gn"));
        nnn=Integer.parseInt(intent.getStringExtra("nn"));

        inter.setText(intent.getStringExtra("award"));
        AIn=Integer.parseInt(intent.getStringExtra("award"));

        mon.setText(intent.getStringExtra("money"));
        givemon=Integer.parseInt(intent.getStringExtra("money"));
        BmobQuery<SeekHelp> query=new BmobQuery<>();
        query.addWhereEqualTo("objectId",intent.getStringExtra("item"));
        query.addQueryKeys("state,details,picture1,picture2,picture3,giveHelpNum");
        query.findObjects(this, new FindListener<SeekHelp>() {//查询多条数据
            @Override
            public void onSuccess(List<SeekHelp> object) {
                SeekHelp x = object.get(0);
                if(x.getDetails()!=null){
                    det.setText(x.getDetails());
                }
                if(x.getPicture1()!=null){
                    imageLoader.displayImage("http://file.bmob.cn/" + x.getPicture1().getUrl(), p1);
                    p1.setOnClickListener(ShowmySeek.this);
                    url1=x.getPicture1().getUrl();
                }
                if(x.getPicture2()!=null){
                    imageLoader.displayImage("http://file.bmob.cn/"+x.getPicture2().getUrl(),p2);
                    p2.setOnClickListener(ShowmySeek.this);
                    url2=x.getPicture2().getUrl();
                }
                if(x.getPicture3()!=null){
                    imageLoader.displayImage("http://file.bmob.cn/"+x.getPicture3().getUrl(),p3);
                    p3.setOnClickListener(ShowmySeek.this);
                    url3=x.getPicture3().getUrl();
                }
                gnn=x.getGiveHelpNum();
                peo.setText(String.valueOf(gnn)+"/"+String.valueOf(nnn));
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(ShowmySeek.this,"无法查看",Toast.LENGTH_LONG);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
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
            case R.id.accept:
                if(!state.equals("1")) {
                    Intent intent4 = new Intent(mContext, AcceptHelp.class);
                    intent4.putExtra("state", state);
                    intent4.putExtra("shid", obid);
                    startActivity(intent4);
                }else {
                    showToast("事务已完结！");
                }
                break;
            case R.id.end:
                if(state.equals("0")) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                    builder.setTitle("确定结束(其他人将看不到您的求助)？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SeekHelp s = new SeekHelp();
                            s.setState("1");
                            s.setNeedNum(nnn);
                            s.setGiveHelpNum(gnn);
                            s.setAwardInteral(AIn);
                            s.setGivemoney(givemon);
                            s.update(mContext, obid, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    state = "1";
                                    Intent intent5 = new Intent(mContext, endmySeek.class);
                                    intent5.putExtra("obid", obid);
                                    startActivity(intent5);
                                }

                                @Override
                                public void onFailure(int i, String s) {

                                }
                            });
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //取消
                        }
                    });
                    builder.create().show();
                } else{
                    state="1";
                    Intent intent5 = new Intent(mContext, endmySeek.class);
                    intent5.putExtra("obid",obid);
                    startActivity(intent5);
                }

                break;

        }
    }
}
