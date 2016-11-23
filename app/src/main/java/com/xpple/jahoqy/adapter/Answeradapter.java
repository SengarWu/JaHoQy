package com.xpple.jahoqy.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.bean.Answer;
import com.xpple.jahoqy.view.RoundImageView;

import java.util.List;

/**
 * Created by Koreleone on 2015/9/29.
 */
public class Answeradapter extends BaseListAdapter<Answer> {
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    public Answeradapter(Context context, List<Answer> items) {
        super(context, items);
    }
    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.include_answeritem, null);
        }
        Answer contract=getList().get(position);
        RoundImageView upp= ViewHolder.get(convertView, R.id.up);
        TextView username= ViewHolder.get(convertView, R.id.commentuser);
        TextView time= ViewHolder.get(convertView, R.id.time);
        TextView content= ViewHolder.get(convertView, R.id.answercontent);
        ImageView result= ViewHolder.get(convertView, R.id.result);
        result.setImageResource(R.color.color_transparent_bg);
        if(contract.getResult().equals("1")) {
            result.setImageResource(R.mipmap.goodanswer);
        }
        if(contract.getUser().getUsername()!=null){
            username.setText(contract.getUser().getUsername()+"  说：");
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
        time.setText(contract.getUpdatedAt());
        return convertView;
    }
}
