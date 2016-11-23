package com.xpple.jahoqy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.bean.SeekHelp;
import com.xpple.jahoqy.bean.User;

import java.util.List;

/**
 * Created by Koreleone on 2015/10/6.
 */
public class myPubTaskAdapter extends BaseListAdapter<SeekHelp> {
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private User user;
    public myPubTaskAdapter(Context context, List<SeekHelp> items, User me) {
        super(context, items);
        user=me;
    }
    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.include_myseekitem, null);
        }
        final SeekHelp contract=getList().get(position);

        TextView time= ViewHolder.get(convertView, R.id.time);
        TextView title= ViewHolder.get(convertView, R.id.tt);
        TextView Award= ViewHolder.get(convertView, R.id.Award);
        TextView money= ViewHolder.get(convertView, R.id.mon);
        TextView people= ViewHolder.get(convertView, R.id.peo);

        if(contract.getTitle()!=null){
            title.setText(contract.getTitle());
        }
        if(contract.getAwardInteral()!=0){
            Award.setText(String.valueOf(contract.getAwardInteral()));
        }

        if(contract.getGivemoney()!=0){
            money.setText(String.valueOf(contract.getGivemoney()));
        }

        if(contract.getNeedNum()!=0){
            people.setText(0+"/"+contract.getNeedNum());
           if(contract.getGiveHelpNum()!=0){
               people.setText(contract.getGiveHelpNum()+"/"+contract.getNeedNum());
           }
        }
        time.setText(contract.getUpdatedAt());
        return convertView;
    }
}
