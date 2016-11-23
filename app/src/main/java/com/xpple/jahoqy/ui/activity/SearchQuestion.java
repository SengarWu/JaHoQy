package com.xpple.jahoqy.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.adapter.Questionadapter;
import com.xpple.jahoqy.bean.Question;
import com.xpple.jahoqy.bean.User;
import com.xpple.jahoqy.util.CommonUtils;
import com.xpple.jahoqy.view.xlistview.SimpleFooter;
import com.xpple.jahoqy.view.xlistview.SimpleHeader;
import com.xpple.jahoqy.view.xlistview.ZrcListView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

public class SearchQuestion extends BaseActivity implements View.OnClickListener {
    private String sex="";
    private String ty="所有类型";
    private ZrcListView listView;
    private Handler handler;//用于接收子线程的信息以刷新主线程
    private Questionadapter adapter;
    private ArrayList<Question> items = new ArrayList<>();
    int curPage = 0;
    private Spinner type;
    private String city="";
    private ImageView bk;
    private TextView cit;
    private RadioGroup rg;
    private String ttt;
    private ImageView cer,rest;
    private DrawerLayout mdrawlayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_help);
        initView();
    }

    private void initView() {
        mdrawlayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        cer=(ImageView)findViewById(R.id.certain);
        rest=(ImageView)findViewById(R.id.reset);
        cer.setOnClickListener(this);
        rest.setOnClickListener(this);
        type=(Spinner)findViewById(R.id.type);
        rg=(RadioGroup)findViewById(R.id.sexgroup);
        bk=(ImageView)findViewById(R.id.back);
        cit=(TextView)findViewById(R.id.city);
        bk.setOnClickListener(this);
        cit.setOnClickListener(this);
        initSelectdrawer();
        listView = (ZrcListView)findViewById(R.id.searchlist);
        handler = new Handler();
        // 设置下拉刷新的样式（可选，但如果没有Header则无法下拉刷新）
        SimpleHeader header = new SimpleHeader(this);
        header.setTextColor(0xffee71a1);
        header.setCircleColor(0xffee71a1);
        listView.setHeadable(header);

        // 设置加载更多的样式（可选）
        SimpleFooter footer = new SimpleFooter(this);
        footer.setCircleColor(0xffee71a1);
        listView.setFootable(footer);

        // 设置列表项出现动画（可选）
        listView.setItemAnimForTopIn(R.anim.top_item_in);
        listView.setItemAnimForBottomIn(R.anim.bottom_item_in);

        adapter = new Questionadapter(this, items);
        listView.setAdapter(adapter);

        if (items.size() <= 0)
        {
            listView.refresh(); // 主动下拉刷新
        }

        // 下拉刷新事件回调（可选）
        listView.setOnRefreshStartListener(new ZrcListView.OnStartListener() {
            @Override
            public void onStart() {
                refresh();
            }
        });

        // 加载更多事件回调（可选）
        listView.setOnLoadMoreStartListener(new ZrcListView.OnStartListener() {
            @Override
            public void onStart() {
                loadMore();
            }
        });
        listView.setOnItemClickListener(new ZrcListView.OnItemClickListener() {
            @Override
            public void onItemClick(ZrcListView parent, View view, int position, long id) {
                Intent item = new Intent(SearchQuestion.this, ShowQuesiondetails.class);
                item.putExtra("item", items.get(position).getObjectId());
                item.putExtra("username",items.get(position).getUser().getUsername());
                item.putExtra("content",items.get(position).getDetails());
                if(items.get(position).getUser().getUserPhoto()!=null) {
                    item.putExtra("url", items.get(position).getUser().getUserPhoto().getUrl());
                }
                startActivity(item);
            }
        });


    }

    private void initSelectdrawer() {
        type=(Spinner)findViewById(R.id.type);
        List<String> data_list2 = new ArrayList<String>();
        data_list2.add("所有类型");
        data_list2.add("寻人启示");
        data_list2.add("找人维修");
        data_list2.add("寻物启示");
        data_list2.add("生活帮带");
        data_list2.add("学习互助");
        data_list2.add("江湖眼线");
        data_list2.add("江湖救急");
        data_list2.add("其它");
        ArrayAdapter<String> adapter2=new ArrayAdapter<String>(this,
                R.layout.spinner_check,data_list2);
        type.setAdapter(adapter2);
    }

    private void refresh() {
        curPage=0;
        handler.post(new Runnable() {
            @Override
            public void run() {
                Queryquestion();//queryProxy为自定义的查询类
            }
        });
    }
    private void loadMore() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                QueryCountquestion();
            }
        });
    }

    private void Queryquestion(){
        BmobQuery<Question> query = new BmobQuery<Question>();
        query.addWhereEqualTo("state","0");
        query.include("user");
        query.order("-updatedAt");
        BmobQuery<User> userquery=new BmobQuery<User>();
        if(!sex.equals("")) {
            userquery.addWhereEqualTo("gender", sex);
        }
        if(!city.equals("")) {
            query.addWhereEqualTo("city", city);
        }
        if(!ty.equals("所有类型")) {
            query.addWhereEqualTo("type", ty);
        }
        query.setLimit(10);
        userquery.addWhereNotEqualTo("objectId",CurrentUser.getObjectId());
        query.addWhereMatchesQuery("user", "_User", userquery);
        query.findObjects(this, new FindListener<Question>() {//查询多条数据
            @Override
            public void onSuccess(List<Question> object) {
                if (object.size() != 0) {
                    if (CommonUtils.isNotNull(object)) {//监测网络等是否可用
                        items.clear();
                        adapter.addAll(object);
                        if (object.size() < 10) {
                            listView.setRefreshSuccess("加载完成"); // 通知加载完成
                            listView.stopLoadMore();
                        } else {
                            listView.setSelection(0);
                            listView.setRefreshSuccess("加载成功"); // 通知加载成功
                            listView.startLoadMore(); // 开启LoadingMore功能
                        }
                    } else {
                        listView.setRefreshSuccess("暂无数据");
                        listView.stopLoadMore();
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    listView.setRefreshFail("抱歉，没搜到任何结果");
                    items.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(int code, String msg) {
                listView.setRefreshFail("加载失败");
                items.clear();
                adapter.notifyDataSetChanged();
            }
        });

    }

    private void QueryCountquestion(){
        BmobQuery<Question> query=new BmobQuery<>();
        query.addWhereEqualTo("state","0");
        query.include("user");
        BmobQuery<User> userquery=new BmobQuery<User>();
        userquery.addWhereNotEqualTo("objectId",CurrentUser.getObjectId());
        if(!sex.equals("")) {
            userquery.addWhereEqualTo("gender", sex);
        }
        if(!city.equals("")) {
            query.addWhereEqualTo("city", city);
        }
        if(!ty.equals("所有类型")) {
            query.addWhereEqualTo("type", ty);
        }

        query.addWhereMatchesQuery("user", "_User", userquery);
        query.count(this, Question.class, new CountListener() {
            @Override
            public void onSuccess(int count) {
                if (count > items.size()) {
                    curPage++;
                    QueryMorequestion(curPage);
                } else {
                    listView.stopLoadMore();
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                listView.stopLoadMore();
            }
        });
    }

    private void QueryMorequestion(int page){
        BmobQuery<Question> query = new BmobQuery<Question>();
        query.addWhereEqualTo("state","0");
        query.include("user");
        query.order("-updatedAt");
        BmobQuery<User> userquery=new BmobQuery<User>();
        userquery.addWhereNotEqualTo("objectId",CurrentUser.getObjectId());
        userquery.addQueryKeys("sex");
        if(!sex.equals("")) {
            userquery.addWhereEqualTo("gender", sex);
        }
        if(!city.equals("")) {
            query.addWhereEqualTo("city", city);
        }
        if(!ty.equals("所有类型")) {
            query.addWhereEqualTo("type", ty);
        }
        query.setLimit(10);
        query.setSkip(page*10);
        query.addWhereMatchesQuery("user", "_User", userquery);
        query.findObjects(this, new FindListener<Question>() {//查询多条数据
            @Override
            public void onSuccess(List<Question> object) {
                if (CommonUtils.isNotNull(object)) {
                    adapter.addAll(object);
                }
                adapter.notifyDataSetChanged();
                listView.setLoadMoreSuccess();
            }

            @Override
            public void onError(int code, String msg) {
                Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                listView.stopLoadMore();
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.city:
                startActivityForResult(new Intent(this, SelectCityActivity.class), 99);
                break;
            case R.id.certain:
                switch (rg.getCheckedRadioButtonId()){
                    case R.id.onlyboy:
                        sex="男";
                        break;
                    case R.id.onlygirl:
                        sex="女";
                        break;
                    case R.id.nolimit:
                        sex="";
                        break;
                    default:
                        sex="";
                        break;
                }
                ty=type.getSelectedItem().toString();
                mdrawlayout.closeDrawer(GravityCompat.START);
                listView.setSelection(0);
                listView.refresh();
                break;
            case R.id.reset:
                rg.clearCheck();
                type.setSelection(0);
                break;
            case R.id.back:
                this.finish();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        try {
            switch (resultCode) {
                case 99:
                    String receive=data.getStringExtra("lngCityName");
                    cit.setText(receive);
                    if(!receive.equals("所有城市")) {
                        city = data.getStringExtra("lngCityName");
                    }else{
                        city="";
                    }
                    listView.setSelection(0);
                    listView.refresh();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
