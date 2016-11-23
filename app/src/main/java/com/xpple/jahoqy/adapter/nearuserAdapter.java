package com.xpple.jahoqy.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xpple.jahoqy.CustomApplication;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.bean.NearUser;
import com.xpple.jahoqy.view.RoundImageView;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Koreleone on 2015/9/12.
 */
public class nearuserAdapter extends BaseListAdapter<NearUser> {
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    public nearuserAdapter(Context context, List<NearUser> items) {
        super(context, items);
    }
    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.include_nearuserlist_item, null);
        }
        NearUser contract=getList().get(position);
        RoundImageView ima= ViewHolder.get(convertView, R.id.imageView);
        TextView uname= ViewHolder.get(convertView, R.id.nname);
        ImageView sex= ViewHolder.get(convertView, R.id.sex);
        TextView distance= ViewHolder.get(convertView, R.id.distance);
        TextView jhn= ViewHolder.get(convertView, R.id.jhname);
        if(contract.getUser().getUsername()!=null) {
            uname.setText(contract.getUser().getUsername());
        }
      sex.setImageResource(R.mipmap.notknow);
        if(contract.getUser().getGender()!=null){
            if(contract.getUser().getGender().equals("男")){
                sex.setImageResource(R.mipmap.boy);
            }else{
                sex.setImageResource(R.mipmap.girl);
            }
        }
                //根据用户经验值生成对应称号
                if (contract.getUser().getExperience() <= -500)
                {
                    jhn.setText("无恶不作");
                }
                else if (contract.getUser().getExperience() > -500 && contract.getUser().getExperience() <= -300)
                {
                    jhn.setText("混世魔王");
                }
                else if (contract.getUser().getExperience() > -300 && contract.getUser().getExperience() <= -200)
                {
                    jhn.setText("道貌岸然");
                }
                else if (contract.getUser().getExperience() > -200 && contract.getUser().getExperience() <= -100)
                {
                    jhn.setText("附庸风雅");
                }
                else if (contract.getUser().getExperience() > -100 && contract.getUser().getExperience() < 0)
                {
                    jhn.setText("经验欠缺");
                }
                else if (contract.getUser().getExperience() >= 0 && contract.getUser().getExperience() <= 100)
                {
                    jhn.setText("初步江湖");

                }
                else if (contract.getUser().getExperience() > 100 && contract.getUser().getExperience() <= 200)
                {
                    jhn.setText("江湖小虾");
                }
                else if (contract.getUser().getExperience() > 200 && contract.getUser().getExperience() <= 400)
                {
                    jhn.setText("明日之星");
                }
                else if (contract.getUser().getExperience() > 400 && contract.getUser().getExperience() <= 700)
                {
                    jhn.setText("行侠仗义");
                }
                else if (contract.getUser().getExperience() > 700 && contract.getUser().getExperience() <= 1100)
                {
                    jhn.setText("江湖少侠");
                }
                else if (contract.getUser().getExperience() > 1100 && contract.getUser().getExperience() <= 1600)
                {
                    jhn.setText("声明远杨");
                }
                else if (contract.getUser().getExperience() > 1600 && contract.getUser().getExperience() <= 2200)
                {
                    jhn.setText("一代名侠");
                }
                else if (contract.getUser().getExperience() > 2200 && contract.getUser().getExperience() <= 2900)
                {
                    jhn.setText("名震江湖");
                }
                else if (contract.getUser().getExperience() > 2900)
                {
                    jhn.setText("名冠天下");
                }
                else
                {
                    jhn.setText("");
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

        if(contract.getPlace()!=null){
            LatLng other=new LatLng(contract.getPlace().getLongitude(),contract.getPlace().getLatitude());
            LatLng my=new LatLng(CustomApplication.lastPoint.getLongitude(), CustomApplication.lastPoint.getLatitude());
            double mine= DistanceUtil. getDistance(my, other)*10;
            if(mine>1000){
                double s=mine/1000;
                BigDecimal bg = new BigDecimal(s);
                double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                distance.setText(String.valueOf(f1)+"km");
            }else{
                if(mine<100){
                    distance.setText("100m以内");
                }else {
                    BigDecimal bg = new BigDecimal(mine);
                    double f1 = bg.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
                    int a=(int)f1;
                    distance.setText(String.valueOf(a) + "m");
                }
            }
        }
        return convertView;
    }
}
