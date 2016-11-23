package com.xpple.jahoqy.ui.activity;

import android.os.Bundle;
import android.widget.ListView;

import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.adapter.GangsAplicationAdapter;
import com.xpple.jahoqy.bean.ApplicationGangs;
import com.xpple.jahoqy.bean.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by caolin on 2015/10/7.
 */
public class GangsApplicationActivity extends BaseActivity {
    ListView listView;
    private GangsAplicationAdapter adapter;
    private int start;
    private int end;
    List<ApplicationGangs> gangslist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gangs_application);
        listView=$(R.id.gangs_application_list);
        BmobQuery<User> query=new BmobQuery<User>();
        query.addWhereEqualTo("objectId", CurrentUser.getObjectId());
        query.findObjects(mContext, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                if(list!=null&&list.size()>0){
                    //list.get(0).getGangsPosition()
                    //初始化搜索范围
                    int num=list.get(0).getGangsPosition();
                    int num1=num/10;
                    if(num1==0){start=0;end=100;}
                    else if(num1==1){start=10;end=100;}
                    else{
                        int num2=num%10;
                        start=num;
                        if(num2==0){
                            end=num+10;
                        }
                        else{
                            end=num+3;
                        }
                    }
                    BmobQuery<ApplicationGangs> query=new BmobQuery<>();
                    query.addWhereLessThan("gangsPosition",end);
                    query.addWhereGreaterThan("gangsPosition", start);
                    query.addWhereEqualTo("gangsName",list.get(0).getGangsName());
                    query.findObjects(mContext, new FindListener<ApplicationGangs>() {
                        @Override
                        public void onSuccess(List<ApplicationGangs> list) {
                            if(list!=null){
                                if(list.size()==0){
                                    showToast("无用户申请加入！！");
                                    finish();
                                }else{
                                    adapter=new GangsAplicationAdapter(mContext, R.layout.row_gangs_application,list);
                                    listView.setAdapter(adapter);
                                }
                                gangslist=list;
                            }
                        }

                        @Override
                        public void onError(int i, String s) {
                            showToast("网络出错，请检查网络设置");
                        }
                    });
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }
}
