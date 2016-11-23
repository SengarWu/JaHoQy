package com.xpple.jahoqy.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.bean.OfferHelp;
import com.xpple.jahoqy.ui.activity.Shownearuser;
import com.xpple.jahoqy.view.RoundImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Koreleone on 2015/10/7.
 */
public class endhelpAdapter extends BaseListAdapter<OfferHelp> {
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private String res;
    private int mark;
    public endhelpAdapter(Context context, List<OfferHelp> items) {
        super(context, items);
    }

    @Override
    public View bindView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.include_endhelpitem, null);
        }
        final OfferHelp contract=getList().get(position);
        RoundImageView ima= ViewHolder.get(convertView, R.id.imageView);
        TextView uname= ViewHolder.get(convertView, R.id.nname);
        TextView sex= ViewHolder.get(convertView, R.id.sex);
        final Button cc= ViewHolder.get(convertView, R.id.comment);
        if(contract.getResult().equals("2")){
            cc.setText("评价");
        }else{
            cc.setText("已评价");
        }
        cc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId()== R.id.comment){
                    if(cc.getText().equals("评价")){
                        final OfferHelp x=new OfferHelp();
                        LinearLayout a=new LinearLayout(mContext);
                        a.setOrientation(LinearLayout.VERTICAL);
                        RadioGroup rb=new RadioGroup(mContext);
                        final RadioButton good=new RadioButton(mContext);
                        good.setText("好评");
                        final RadioButton bad=new RadioButton(mContext);
                        bad.setText("差评");
                        rb.setOrientation(LinearLayout.HORIZONTAL);
                        rb.addView(good);
                        rb.addView(bad);
                        a.addView(rb);
                        final EditText comment=new EditText(mContext);
                        a.addView(comment);
                        new AlertDialog.Builder(mContext).setTitle("请评价")
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setView(a).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        if (good.isChecked() || bad.isChecked()) {
                                            if (good.isChecked()) {
                                                x.setResult("1");
                                                res="G";
                                            } else {
                                                x.setResult("0");
                                                res="B";
                                            }
                                            if (!comment.getText().equals("")) {
                                                x.setComment(String.valueOf(comment.getText()));
                                                x.update(mContext, contract.getObjectId(), new UpdateListener() {
                                                    @Override
                                                    public void onSuccess() {
                                                        cc.setText("已评价");

                                                        if(res.equals("G")){
                                                                mark=5;
                                                                contract.setResult("1");
                                                        }else{
                                                               mark=-5;
                                                                contract.setResult("0");
                                                        }
                                                        //调用云端代码
                                                        AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
                                                        JSONObject params = new JSONObject();
                                                        try {
                                                            params.put("objectId",contract.getUser().getObjectId());
                                                            int b=contract.getUser().getExperience()+mark;
                                                            params.put("experience",b);
                                                        } catch (JSONException e) {
                                                            // TODO Auto-generated catch block
                                                            e.printStackTrace();
                                                        }

                                                        ace.callEndpoint(mContext, "updateExperience", params,
                                                                new CloudCodeListener() {
                                                                    @Override
                                                                    public void onSuccess(Object object) {
                                                                    }

                                                                    @Override
                                                                    public void onFailure(int code, String msg) {
                                                                    }
                                                                });
                                                    }

                                                    @Override
                                                    public void onFailure(int i, String s) {

                                                    }
                                                });
                                            } else {

                                            }
                                        } else {

                                        }
                                    }
                                }).show();

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
            sex.setText(contract.getUser().getGender());
        }

        if(contract.getUser().getExperience()!=0){

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
