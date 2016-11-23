package com.xpple.jahoqy.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xpple.jahoqy.R;
import com.xpple.jahoqy.addimages.ImageDownLoader;
import com.xpple.jahoqy.bean.GangsPost;
import com.xpple.jahoqy.bean.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by caolin on 2015/10/7.
 */
public class GangSelectPositionAdapter extends ArrayAdapter<GangsPost> {

    private Context context;
    private String gangsname;
    TypedArray positionicon;
    String[] str=new String[]{"帮主","副帮主",
            "青龙堂","青龙堂 青木坛","青龙堂 白马坛","青龙堂 红袖坛",
            "白虎堂","白虎堂 青木坛","白虎堂 白马坛","白虎堂 红袖坛",
            "朱雀堂","朱雀堂 青木坛","朱雀堂 白马坛","朱雀堂 红袖坛",
            "玄武堂","玄武堂 青木坛","玄武堂 白马坛","玄武堂 红袖坛",};

    public GangSelectPositionAdapter(Context context, int textViewResourceId, List<GangsPost> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        positionicon=getContext().getResources().obtainTypedArray(R.array.gangs_position_icons);

        User user= BmobUser.getCurrentUser(context, User.class);
        gangsname=user.getGangsName();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.row_gangs_select_position, null);
            holder.title=(RelativeLayout)convertView.findViewById(R.id.title);
            holder.title_name = (TextView) convertView.findViewById(R.id.title_name);
            holder.gangs_name = (TextView) convertView.findViewById(R.id.gangs_name);
            holder.gangs_position= (TextView) convertView.findViewById(R.id.gangs_position);
            holder.title_photo=(ImageView)convertView.findViewById(R.id.title_photo);
            holder.gangs_photo=(ImageView)convertView.findViewById(R.id.gangs_photo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final GangsPost post=getItem(position);

        final int number=post.getPositionGrade();
        int num2=number%10;
        if(num2!=0&&(num2-1)%3!=0){
            holder.gangs_name.setText(post.getPositionName());
            holder.gangs_position.setText("可选");
            if((num2-1)%3==1){
                holder.gangs_photo.setImageResource(R.drawable.elite);
            }else{
                holder.gangs_photo.setImageResource(R.drawable.masses);
            }
        }else{
            BmobQuery<User> query=new BmobQuery<>();
            query.addWhereEqualTo("gangsPosition", number);
            query.addWhereEqualTo("gangsName",gangsname);
            query.findObjects(context, new FindListener<User>() {
                @Override
                public void onSuccess(List<User> list) {
                    if (list != null && list.size() > 0) {
                        User user = list.get(0);
                        holder.gangs_name.setText(user.getUsername());
                        holder.gangs_position.setText(post.getPositionName());
                        ImageDownLoader.showNetImage(context, user.getUserPhoto(), holder.gangs_photo, R.mipmap.bg_gangs);
                    } else {
                        holder.gangs_photo.setImageResource(R.mipmap.ic_help);
                        holder.gangs_name.setText(post.getPositionName());
                        holder.gangs_position.setText("职位空缺");
                    }
                }

                @Override
                public void onError(int i, String s) {

                }
            });
        }
        if (position==0){
            holder.title.setVisibility(View.VISIBLE);
            holder.title_name.setText(str[GradeConvert(number)]);
            holder.title_photo.setImageDrawable(positionicon.getDrawable(GradeIcon(number)));
        }else{
            int oldNumber=getItem(position-1).getPositionGrade();
            if(GradeConvert(number)!=GradeConvert(oldNumber)){
                holder.title.setVisibility(View.VISIBLE);
                holder.title_name.setText(str[GradeConvert(number)]);
                holder.title_photo.setImageDrawable(positionicon.getDrawable(GradeIcon(number)));
            }else{
                holder.title.setVisibility(View.GONE);
            }
        }
        return convertView;
    }
    private static class ViewHolder {
        RelativeLayout title;
        ImageView title_photo;
        ImageView gangs_photo;
        TextView gangs_position;
        TextView title_name;
        TextView gangs_name;
    }
    public int GradeConvert(int num){
        int grade=0;
        int num1=num/10;
        if (num1==0){grade=0;}
        else if(num1==1){grade=1;}
        else if(num1>1){
            grade=2;
            grade+=(num1-2)*4;
            int num2=num%10;
            if(num2!=0){
                grade+=((num2-1)/3+1);
            }
        }
        return grade;
    }
    public int GradeIcon(int num){
        int grade=0;
        int num2=num%10;
        if(num2==0){
            grade=num/10;
        }else{
            grade=6;
            grade+=(num2-1)/3;
        }
        return grade;
    }
}
