package com.xpple.jahoqy.adapter;

import android.content.Context;
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
import com.xpple.jahoqy.bean.Question;
import com.xpple.jahoqy.view.RoundImageView;

import java.util.List;

/**
 * Created by Koreleone on 2015/9/26.
 */
public class Questionadapter extends BaseListAdapter<Question> {
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    public Questionadapter(Context context, List<Question> items) {
        super(context, items);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.include_questionitem, null);
        }
        Question contract=getList().get(position);
        RoundImageView ima= ViewHolder.get(convertView, R.id.imageView);
        TextView username= ViewHolder.get(convertView, R.id.username);
        ImageView sex= ViewHolder.get(convertView, R.id.sex);
        TextView time= ViewHolder.get(convertView, R.id.time);
        TextView title= ViewHolder.get(convertView, R.id.tt);
        TextView Award= ViewHolder.get(convertView, R.id.Award);
        TextView city= ViewHolder.get(convertView, R.id.city);

        if(contract.getUser().getUserPhoto()!=null){
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

        if(contract.getUser().getUsername()!=null){
            username.setText(contract.getUser().getUsername());
        }
        if(contract.getTitle()!=null){
            title.setText(contract.getTitle());
        }
        if(contract.getAward()!=0){
            Award.setText(String.valueOf(contract.getAward()));
        }
        if(contract.getUser().getGender()!=null) {
            if (contract.getUser().getGender().equals("男")) {
                sex.setImageResource(R.mipmap.boy);
            } else {
                if (contract.getUser().getGender().equals("女")) {
                    sex.setImageResource(R.mipmap.girl);
                } else {
                    sex.setImageResource(R.mipmap.notknow);
                }
            }
        }
        if(contract.getCity()!=null){
            if(contract.getCity().equals("所有城市")) {
                city.setText("不分地区");
            }else{
                city.setText(contract.getCity());
            }
        }

        time.setText(contract.getUpdatedAt());
        return convertView;
    }
}
