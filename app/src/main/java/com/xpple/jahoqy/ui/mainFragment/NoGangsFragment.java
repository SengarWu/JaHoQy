package com.xpple.jahoqy.ui.mainFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.xpple.jahoqy.BaseClass.BaseFragment;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.bean.User;
import com.xpple.jahoqy.ui.activity.FoundGangsActivity;
import com.xpple.jahoqy.ui.activity.GangListActivity;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

public class NoGangsFragment extends BaseFragment implements View.OnClickListener {

    private Button btn_join_gangs;
    private Button btn_found_gangs;
    private View parentview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentview = inflater.inflate(R.layout.fragment_no_gangs, container, false);
        btn_join_gangs = (Button) parentview.findViewById(R.id.btn_join_gangs);
        btn_join_gangs.setOnClickListener(this);
        btn_found_gangs = (Button) parentview.findViewById(R.id.btn_found_gangs);
        btn_found_gangs.setOnClickListener(this);
        return parentview;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_join_gangs:
                startAnimActivity(GangListActivity.class);
                //startActivityForResult(new Intent(getActivity(), GangListActivity.class),0);
                break;
            case R.id.btn_found_gangs:
                startActivityForResult(new Intent(getActivity(),FoundGangsActivity.class),0);
                break;
        }

    }
}
