package com.xpple.jahoqy.ui.mainFragment;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xpple.jahoqy.BaseClass.BaseFragment;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.adapter.gangsItemAdapter;
import com.xpple.jahoqy.addimages.ImageDownLoader;
import com.xpple.jahoqy.bean.Gangs;
import com.xpple.jahoqy.bean.User;
import com.xpple.jahoqy.ui.activity.ChatActivity;
import com.xpple.jahoqy.ui.activity.GangsSetting;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

public class GangsFragment extends BaseFragment {
    ListView gang_list;
    gangsItemAdapter adapter;
    Button btn_gangs_manage;
    ImageView iv_gangs_photo;
    Button btn_group_chat;
    TextView tv_gangs_name;
    ImageView iv_gangs_grade;
    TextView tv_gangs_creator;
    TextView tv_gangs_purpose;
    TextView gangGao;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gangs, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public void onResume(){
        initDate();
        super.onResume();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initTextView();
    }

    private void initTextView() {
        AssetManager mgr= getResources().getAssets();
        Typeface tf=Typeface.createFromAsset(mgr, "fonts/hk.TTF");
        tv_gangs_name.setTypeface(tf);
        tv_gangs_creator.setTypeface(tf);
        tv_gangs_purpose.setTypeface(tf);
        gangGao.setTypeface(tf);
    }

    private void initView(){
        iv_gangs_photo=(ImageView)getView().findViewById(R.id.iv_gangs_photo);
        btn_gangs_manage=(Button)getView().findViewById(R.id.btn_gangs_manage);
        btn_group_chat=(Button)getView().findViewById(R.id.btn_group_chat);
        tv_gangs_name=(TextView)getView().findViewById(R.id.tv_gangs_name);
        iv_gangs_grade=(ImageView)getView().findViewById(R.id.iv_gangs_grade);
        tv_gangs_creator=(TextView)getView().findViewById(R.id.tv_gangs_creator);
        tv_gangs_purpose=(TextView)getView().findViewById(R.id.tv_gangs_purpose);
        gangGao=(TextView)getView().findViewById(R.id.gangGao);
        gang_list=(ListView)getView().findViewById(R.id.gang_list);
    }
    private void initDate(){

        BmobQuery<User> query1=new BmobQuery<>();
        query1.addWhereEqualTo("objectId",CurrentUser.getObjectId());
        query1.findObjects(getActivity(), new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                if (list != null&&list.size()>0) {
                    BmobQuery<User> query2=new BmobQuery<>();
                    query2.addWhereEqualTo("gangsName",list.get(0).getGangsName());
                    query2.order("gangsPosition");
                    query2.findObjects(getActivity(), new FindListener<User>() {
                        @Override
                        public void onSuccess(List<User> list) {
                            if (list != null) {
                                adapter = new gangsItemAdapter(getActivity(), R.layout.row_gangs_item1, list);
                                gang_list.setAdapter(adapter);
                            }
                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });

                    BmobQuery<Gangs> query=new BmobQuery<>();
                    query.addWhereEqualTo("gangsName",list.get(0).getGangsName());
                    query.findObjects(getActivity(), new FindListener<Gangs>() {
                        @Override
                        public void onSuccess(List<Gangs> list) {
                            if (list != null && list.size() > 0) {
                                Gangs gangs = list.get(0);
                                tv_gangs_name.setText(gangs.getGangsName());
                                tv_gangs_creator.setText(gangs.getGangsCreater());
                                tv_gangs_purpose.setText(gangs.getGangsPurpose());
                                ImageDownLoader.showNetImage(getActivity(), gangs.getGangsPhoto(), iv_gangs_photo, R.drawable.hostgang);
                                setGangsPhoto(gangs.getGangsGrade());
                                if (gangs.getGanggao() != null && !gangs.getGanggao().equals("")) {
                                    gangGao.setText(gangs.getGanggao());
                                }else{
                                    gangGao.setText("希望大家热心助人，提高我们帮在江湖上的威望！！让我们一起拉帮结派玩转江湖吧！");
                                }
                            }
                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });

        btn_gangs_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), GangsSetting.class),0);
            }
        });
        btn_group_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BmobQuery<User> query1=new BmobQuery<>();
                query1.addWhereEqualTo("objectId",CurrentUser.getObjectId());
                query1.findObjects(getActivity(), new FindListener<User>() {
                    @Override
                    public void onSuccess(List<User> list) {
                        if(list!=null&&list.size()>0){
                            BmobQuery<Gangs> query = new BmobQuery<>();
                            query.addWhereEqualTo("gangsName", list.get(0).getGangsName());
                            query.findObjects(getActivity(), new FindListener<Gangs>() {
                                @Override
                                public void onSuccess(List<Gangs> list) {
                                    if (list != null && list.size() > 0) {
                                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                                        intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                                        String groupId = list.get(0).getGangsObjectId();
                                        intent.putExtra("groupId", groupId);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onError(int i, String s) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
            }
        });
    }
    private void  setGangsPhoto(int i){
        if(i<0){i=0;}
        if(i>4){i=4;}
        int dw=getResources().getIdentifier("xing"+(i-1),"drawable",getActivity().getPackageName());
        iv_gangs_grade.setImageResource(dw);
    }
    public void updatePage(){
        BmobQuery<User> query1=new BmobQuery<>();
        query1.addWhereEqualTo("objectId", CurrentUser.getObjectId());
        query1.findObjects(getActivity(), new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                if (list != null && list.size() > 0) {
                    BmobQuery<User> query2 = new BmobQuery<>();
                    query2.addWhereEqualTo("gangsName", list.get(0).getGangsName());
                    query2.findObjects(getActivity(), new FindListener<User>() {
                        @Override
                        public void onSuccess(List<User> list) {
                            if (list != null) {
                                adapter = new gangsItemAdapter(getActivity(), R.layout.row_gangs_item1, list);
                                gang_list.setAdapter(adapter);
                            }
                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });

                    BmobQuery<Gangs> query = new BmobQuery<>();
                    query.addWhereEqualTo("gangsName", list.get(0).getGangsName());
                    query.findObjects(getActivity(), new FindListener<Gangs>() {
                        @Override
                        public void onSuccess(List<Gangs> list) {
                            if (list != null && list.size() > 0) {
                                Gangs gangs = list.get(0);
                                tv_gangs_name.setText(gangs.getGangsName());
                                tv_gangs_creator.setText(gangs.getGangsCreater());
                                tv_gangs_purpose.setText(gangs.getGangsPurpose());
                                ImageDownLoader.showNetImage(getActivity(), gangs.getGangsPhoto(), iv_gangs_photo, R.drawable.hostgang);
                                setGangsPhoto(gangs.getGangsGrade());
                                if (gangs.getGanggao() != null && !gangs.getGanggao().equals("")) {
                                    gangGao.setText(gangs.getGanggao());
                                }
                            }
                        }

                        @Override
                        public void onError(int i, String s) {

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
