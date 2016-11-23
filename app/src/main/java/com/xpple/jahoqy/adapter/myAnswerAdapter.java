package com.xpple.jahoqy.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.bean.Answer;
import com.xpple.jahoqy.bean.Question;
import com.xpple.jahoqy.ui.activity.Shownearuser;
import com.xpple.jahoqy.view.RoundImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Koreleone on 2015/10/6.
 */
public class myAnswerAdapter extends BaseListAdapter<Answer> {
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private String qstate;
    public myAnswerAdapter(Context context, List<Answer> items, String questionstate) {
        super(context, items);
        qstate=questionstate;
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.include_myanswer, null);
        }
        final Answer contract=getList().get(position);
        final ImageView result= ViewHolder.get(convertView, R.id.resultima);
        RoundImageView upp= ViewHolder.get(convertView, R.id.up);
        TextView username= ViewHolder.get(convertView, R.id.commentuser);
        TextView time= ViewHolder.get(convertView, R.id.time);
        TextView content= ViewHolder.get(convertView, R.id.answercontent);
        ImageView sele= ViewHolder.get(convertView, R.id.select);
        sele.setImageResource(R.mipmap.selectgoodanswer);
        result.setImageResource(R.color.color_transparent_bg);
        if(contract.getUser().getObjectId().equals(BmobUser.getCurrentUser(mContext).getObjectId()))
        {
            sele.setImageResource(R.color.color_transparent_bg);
        }else {
            sele.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view.getId() == R.id.select) {
                        if (qstate.equals("0")) {
                            if (contract.getResult().equals("0")) {
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                                builder.setTitle("确定选择？");
                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //确定
                                        //更改问题的状态
                                        Question q = new Question();
                                        q.setState("1");
                                        q.setAward(contract.getQuestion().getAward());
                                        q.update(mContext, contract.getQuestion().getObjectId(), new UpdateListener() {
                                            @Override
                                            public void onSuccess() {
                                                qstate = "1";
                                                //更改回答为最佳答案
                                                Answer a = new Answer();
                                                a.setResult("1");
                                                a.update(mContext, contract.getObjectId(), new UpdateListener() {
                                                    @Override
                                                    public void onSuccess() {
                                                        Toast.makeText(mContext, "选择成功", Toast.LENGTH_LONG);
                                                        //给予用户积分
                                                        if (contract.getQuestion().getAward() != 0) {//判断有无悬赏
                                                            AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
                                                            JSONObject params = new JSONObject();
                                                            try {
                                                                params.put("objectId", contract.getUser().getObjectId());
                                                                int b = contract.getUser().getUserIntegral() + contract.getQuestion().getAward();
                                                                params.put("userIntegral", b);
                                                            } catch (JSONException e) {
                                                                // TODO Auto-generated catch block
                                                                e.printStackTrace();
                                                            }

                                                            ace.callEndpoint(mContext, "updateUserIntegral", params,
                                                                    new CloudCodeListener() {
                                                                        @Override
                                                                        public void onSuccess(Object object) {
                                                                            result.setImageResource(R.mipmap.goodanswer);
                                                                        }

                                                                        @Override
                                                                        public void onFailure(int code, String msg) {

                                                                        }
                                                                    });
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(int code, String msg) {
                                                        Toast.makeText(mContext, "操作失败", Toast.LENGTH_LONG);
                                                    }
                                                });
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

                            } else {
                                ShowToast("已是最佳答案");
                            }
                        } else {
                            ShowToast("已选过最佳答案");
                        }
                    }
                }
            });
        }
        if(contract.getResult().equals("1")){

            result.setImageResource(R.mipmap.goodanswer);
        }
        if(contract.getUser().getUsername()!=null){
            username.setText(contract.getUser().getUsername()+"  ：");
        }
        if(contract.getContent()!=null){
            content.setText(contract.getContent());
        }
        if(contract.getUser().getUserPhoto()!=null){
            imageLoader.displayImage("http://file.bmob.cn/" + contract.getUser().getUserPhoto().getUrl(), upp, new ImageLoadingListener() {
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
        upp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, Shownearuser.class);
                intent.putExtra("item",contract.getUser().getObjectId());
                mContext.startActivity(intent);
            }
        });
        time.setText(contract.getUpdatedAt());
        return convertView;
    }
}
