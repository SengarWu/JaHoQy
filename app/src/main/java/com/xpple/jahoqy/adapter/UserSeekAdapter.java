package com.xpple.jahoqy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xpple.jahoqy.R;
import com.xpple.jahoqy.bean.SeekHelp;

import java.util.List;

/**
 * Created by Koreleone on 2015/10/3.
 */
public class UserSeekAdapter extends BaseListAdapter<SeekHelp> {
    public UserSeekAdapter(Context context, List<SeekHelp> items) {
        super(context, items);
    }
    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.include_userseek, null);
        }
        SeekHelp contract=getList().get(position);
        TextView t= ViewHolder.get(convertView, R.id.time);
        TextView s= ViewHolder.get(convertView, R.id.state);
        TextView tit= ViewHolder.get(convertView, R.id.title);
        t.setText(contract.getUpdatedAt());
        if(contract.getState().equals("0")) {
            s.setText("待帮忙");
        }else{
            s.setText("已完结");
        }
        if(contract.getTitle()!=null){
            tit.setText(contract.getTitle());
        }
        return convertView;
    }
}
