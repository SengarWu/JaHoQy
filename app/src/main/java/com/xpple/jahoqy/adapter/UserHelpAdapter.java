package com.xpple.jahoqy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xpple.jahoqy.R;
import com.xpple.jahoqy.bean.OfferHelp;

import java.util.List;

/**
 * Created by Koreleone on 2015/10/3.
 */
public class UserHelpAdapter extends BaseListAdapter<OfferHelp> {
    public UserHelpAdapter(Context context, List<OfferHelp> items) {
        super(context, items);
    }
    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.include_userhelp, null);
        }
        TextView time= ViewHolder.get(convertView, R.id.time);
        TextView aidmfo= ViewHolder.get(convertView, R.id.aid);
        OfferHelp contract=getList().get(position);
        time.setText(contract.getUpdatedAt());
        aidmfo.setText("帮助  "+contract.getSh().getUser().getUsername()+"  解决了"+contract.getSh().getType()+"类型的问题");
        return convertView;
    }
}
