package com.xpple.jahoqy.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.bean.MyBmobInstallation;
import com.xpple.jahoqy.bean.OfferHelp;
import com.xpple.jahoqy.bean.SeekHelp;
import com.xpple.jahoqy.ui.activity.Shownearuser;
import com.xpple.jahoqy.view.RoundImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Koreleone on 2015/10/7.
 */
public class AcceptHelpAdapter extends BaseListAdapter<OfferHelp> {
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private int gn=1;//由于数据更新后列表没有刷新，contract里的giveHelpNum仍为原来的值，再次更新需要计数
    public AcceptHelpAdapter(Context context, List<OfferHelp> items) {
        super(context, items);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.help_useritem, null);
        }
        final OfferHelp contract=getList().get(position);
        RoundImageView ima= ViewHolder.get(convertView, R.id.imageView);
        TextView uname= ViewHolder.get(convertView, R.id.nname);
        ImageView sex= ViewHolder.get(convertView, R.id.sex);
        TextView rep= ViewHolder.get(convertView, R.id.repu);
        final Button cc= ViewHolder.get(convertView, R.id.accept);
        if(!contract.getAccept().equals("0")){
            cc.setText("接受帮忙");
        }else{

        }

        cc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId()== R.id.accept){
                    if(cc.getText().equals("接受帮忙")) {
                        if((contract.getSh().getGiveHelpNum()+gn)>contract.getSh().getNeedNum()){
                                 ShowToast("人数已满");
                        }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("确定接受？");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //确定
                                OfferHelp x = new OfferHelp();
                                x.setAccept("1");
                                x.update(mContext, contract.getObjectId(), new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        cc.setText("已接受");

                                      /*  BmobQuery<SeekHelp> qq=new BmobQuery<SeekHelp>();
                                        qq.addQueryKeys("giveHelpNum");
                                        qq.addWhereEqualTo("objectId", contract.getSh().getObjectId());
                                        qq.findObjects(mContext, new FindListener<SeekHelp>() {
                                            @Override
                                            public void onSuccess(List<SeekHelp> list) {
                                                gn=list.get(0).getGiveHelpNum()+1;
                                            }

                                            @Override
                                            public void onError(int i, String s) {

                                            }
                                        });*/
                                        SeekHelp ss = new SeekHelp();
                                        ss.setGiveHelpNum(contract.getSh().getGiveHelpNum() + gn);
                                        ss.setNeedNum(contract.getSh().getNeedNum());
                                        ss.setAwardInteral(contract.getSh().getAwardInteral());
                                        ss.setGivemoney(contract.getSh().getGivemoney());
                                        ss.update(mContext, contract.getSh().getObjectId(), new UpdateListener() {
                                            @Override
                                            public void onSuccess() {
                                                gn++;
                                                //发送消息给热心用户
                                                BmobInstallation.getCurrentInstallation(mContext).save();
                                                BmobPushManager bmobPushManager=new BmobPushManager(mContext);
                                                BmobQuery<MyBmobInstallation> query = MyBmobInstallation.getQuery();
                                                query.addWhereEqualTo("userName",contract.getUser().getUsername());
                                                bmobPushManager.setQuery(query);
                                                bmobPushManager.pushMessage(",ac," + BmobUser.getCurrentUser(mContext).getUsername() + "," + contract.getSh().getObjectId() + ",", new PushListener() {
                                                    @Override
                                                    public void onSuccess() {

                                                    }

                                                    @Override
                                                    public void onFailure(int i, String s) {
                                                              ShowToast(s);
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onFailure(int i, String s) {

                                            }
                                        });
                                        if (contract.getSh().getAwardInteral() != 0) {
                                            AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
                                            JSONObject params = new JSONObject();
                                            try {
                                                params.put("objectId", contract.getUser().getObjectId());
                                                int b = contract.getUser().getUserIntegral() + contract.getSh().getAwardInteral();
                                                params.put("userIntegral", b);
                                            } catch (JSONException e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                            }

                                            ace.callEndpoint(mContext, "updateUserIntegral", params,
                                                    new CloudCodeListener() {
                                                        @Override
                                                        public void onSuccess(Object object) {

                                                        }

                                                        @Override
                                                        public void onFailure(int code, String msg) {

                                                        }
                                                    });
                                        }
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
                    }
                    }else{

                    }
                }
            }
        });
        ima.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
                  if(view.getId()== R.id.imageView){
                      Intent intent=new Intent(mContext, Shownearuser.class);
                      intent.putExtra("item",contract.getUser().getObjectId());
                      mContext.startActivity(intent);
                  }
           }
       }

        );
        if(contract.getUser().getUsername().length()>10){
            uname.setText(contract.getUser().getUsername().substring(0, 10) + "...");
        }else{
            if(contract.getUser().getUsername()!=null) {
                uname.setText(contract.getUser().getUsername());
            }
        }

        if(contract.getUser().getGender()!=null){
            if(contract.getUser().getGender().equals("男")) {
                sex.setImageResource(R.mipmap.boy);
            }else{
                sex.setImageResource(R.mipmap.girl);
            }
        }

        if (contract.getUser().getExperience() <= -500)
        {
            rep.setText("无恶不作");
        }
        else if (contract.getUser().getExperience() > -500 && contract.getUser().getExperience() <= -300)
        {
            rep.setText("混世魔王");
        }
        else if (contract.getUser().getExperience() > -300 && contract.getUser().getExperience() <= -200)
        {
            rep.setText("道貌岸然");
        }
        else if (contract.getUser().getExperience() > -200 && contract.getUser().getExperience() <= -100)
        {
            rep.setText("附庸风雅");
        }
        else if (contract.getUser().getExperience() > -100 && contract.getUser().getExperience() < 0)
        {
            rep.setText("经验欠缺");
        }
        else if (contract.getUser().getExperience() >= 0 && contract.getUser().getExperience() <= 100)
        {
            rep.setText("初步江湖");

        }
        else if (contract.getUser().getExperience() > 100 && contract.getUser().getExperience() <= 200)
        {
            rep.setText("江湖小虾");
        }
        else if (contract.getUser().getExperience() > 200 && contract.getUser().getExperience() <= 400)
        {
            rep.setText("明日之星");
        }
        else if (contract.getUser().getExperience() > 400 && contract.getUser().getExperience() <= 700)
        {
            rep.setText("行侠仗义");
        }
        else if (contract.getUser().getExperience() > 700 && contract.getUser().getExperience() <= 1100)
        {
            rep.setText("江湖少侠");
        }
        else if (contract.getUser().getExperience() > 1100 && contract.getUser().getExperience() <= 1600)
        {
            rep.setText("声明远杨");
        }
        else if (contract.getUser().getExperience() > 1600 && contract.getUser().getExperience() <= 2200)
        {
            rep.setText("一代名侠");
        }
        else if (contract.getUser().getExperience() > 2200 && contract.getUser().getExperience() <= 2900)
        {
            rep.setText("名震江湖");
        }
        else if (contract.getUser().getExperience() > 2900)
        {
            rep.setText("名冠天下");
        }
        else
        {
            rep.setText("");
        }

        if(contract.getUser().getUserPhoto() !=null) {
            imageLoader.displayImage("http://file.bmob.cn/" + contract.getUser().getUserPhoto().getUrl(), ima, new ImageLoadingListener() {
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


        return convertView;
    }
}
