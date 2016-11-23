package com.xpple.jahoqy.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.bean.Question;
import com.xpple.jahoqy.view.RoundImageView;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by Koreleone on 2015/10/6.
 */
public class myQuestionAdapter extends BaseListAdapter<Question> {
    public myQuestionAdapter(Context context, List<Question> items) {
        super(context, items);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.include_myquestion1, null);
        }
        Question contract=getList().get(position);
        TextView time=ViewHolder.get(convertView, R.id.time);
        TextView award=ViewHolder.get(convertView,R.id.award);
        TextView title=ViewHolder.get(convertView,R.id.title);
        ImageView state=ViewHolder.get(convertView,R.id.state);
        time.setText(contract.getCreatedAt());
        award.setText(String.valueOf(contract.getAward()));
        title.setText(contract.getTitle());
        if(contract.getState().equals("0")){
            state.setImageResource(R.mipmap.nofinish);
        }else{
            state.setImageResource(R.mipmap.finish);
        }
        return convertView;
    }
}
