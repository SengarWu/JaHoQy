package com.xpple.jahoqy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xpple.jahoqy.R;
import com.xpple.jahoqy.addimages.ImageDownLoader;
import com.xpple.jahoqy.bean.Gangs;

import java.util.List;

/**
 * Created by caolin on 2015/10/4.
 */
public class GangAdapt extends ArrayAdapter<Gangs> {

    private Context context;

    public GangAdapt(Context context, int textViewResourceId, List<Gangs> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.row_gangs_item, null);
            holder.gangPhoto = (ImageView) convertView.findViewById(R.id.gang_photo);
            holder.gangName= (TextView) convertView.findViewById(R.id.gang_name);
            holder.gangGrade=(ImageView)convertView.findViewById(R.id.gangs_grade);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Gangs gang=getItem(position);
        ImageDownLoader.showNetImage(context, gang.getGangsPhoto(), holder.gangPhoto, R.mipmap.gangs_icon);
        holder.gangName.setText(gang.getGangsName());
        loadGrade(gang.getGangsGrade(),holder.gangGrade);
        return convertView;
    }
    private static class ViewHolder {
        ImageView gangPhoto;
        TextView gangName;
        ImageView gangGrade;
    }
    private void loadGrade(int num,ImageView img){
        if (num==0){
            img.setImageResource(R.drawable.xing1);
        }else if(num==1){
            img.setImageResource(R.drawable.xing2);
        }else if(num==2){
            img.setImageResource(R.drawable.xing3);
        }else if(num==3){
            img.setImageResource(R.drawable.xing4);
        }else {
            img.setImageResource(R.drawable.xing5);
        }
    }
}
