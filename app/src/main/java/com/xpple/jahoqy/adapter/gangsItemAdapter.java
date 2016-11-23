package com.xpple.jahoqy.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xpple.jahoqy.R;
import com.xpple.jahoqy.addimages.ImageDownLoader;
import com.xpple.jahoqy.bean.GangsPost;
import com.xpple.jahoqy.bean.User;
import com.xpple.jahoqy.ui.activity.Shownearuser;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by caolin on 2015/10/14.
 */
public class gangsItemAdapter extends ArrayAdapter<User> {

    private Context context;

    public gangsItemAdapter(Context context, int textViewResourceId, List<User> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.row_gangs_item1, null);
            holder.gangPhoto = (ImageView) convertView.findViewById(R.id.user_photo);
            holder.gangs_position= (TextView) convertView.findViewById(R.id.gangs_position);
            holder.gangName= (TextView) convertView.findViewById(R.id.user_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final User user= getItem(position);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, Shownearuser.class);
                intent.putExtra("item",user.getObjectId());
                context.startActivity(intent);
            }
        });
        ImageDownLoader.showNetImage(context, user.getUserPhoto(), holder.gangPhoto, R.mipmap.gangs_0);
        holder.gangName.setText(user.getUsername());
        BmobQuery<GangsPost> query1=new BmobQuery<>();
        final TextView grade=holder.gangs_position;
        query1.addWhereEqualTo("positionGrade", user.getGangsPosition());
        query1.findObjects(context, new FindListener<GangsPost>() {
            @Override
            public void onSuccess(List<GangsPost> list) {
                if (list != null&&list.size()>0) {
                    grade.setText(list.get(0).getPositionName());
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
        return convertView;
    }

    private static class ViewHolder {
        ImageView gangPhoto;
        TextView gangName;
        TextView gangs_position;
    }
}

