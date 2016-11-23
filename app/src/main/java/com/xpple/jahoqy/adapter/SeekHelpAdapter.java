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
import com.xpple.jahoqy.bean.SeekHelp;
import com.xpple.jahoqy.view.RoundImageView;

import java.util.List;

/**
 * Created by Koreleone on 2015/9/29.
 */
public class SeekHelpAdapter extends BaseListAdapter<SeekHelp> {
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    public SeekHelpAdapter(Context context, List<SeekHelp> items) {
        super(context, items);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.include_seekitem, null);
        }
        final SeekHelp contract=getList().get(position);
        RoundImageView ima= ViewHolder.get(convertView, R.id.imageView);
        TextView username= ViewHolder.get(convertView, R.id.username);
        ImageView sex= ViewHolder.get(convertView, R.id.sex);
        TextView time= ViewHolder.get(convertView, R.id.time);
        TextView title= ViewHolder.get(convertView, R.id.tt);
        TextView Award= ViewHolder.get(convertView, R.id.Award);
        TextView money= ViewHolder.get(convertView, R.id.mon);
        final TextView people= ViewHolder.get(convertView, R.id.peo);
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
        if(contract.getAwardInteral()!=0){
            Award.setText(String.valueOf(contract.getAwardInteral()));
        }

        if(contract.getGivemoney()!=0){
            money.setText(String.valueOf(contract.getGivemoney()));
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

        if(contract.getNeedNum()!=0){
            people.setText(String.valueOf("0/"+contract.getNeedNum()));
            if(contract.getGiveHelpNum()!=0){
                people.setText(String.valueOf(contract.getGiveHelpNum()+"/"+contract.getNeedNum()));
            }
        }
        time.setText(contract.getUpdatedAt());
        return convertView;
    }
}
