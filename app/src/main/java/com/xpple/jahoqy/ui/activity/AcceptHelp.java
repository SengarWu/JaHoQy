package com.xpple.jahoqy.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.adapter.AcceptHelpAdapter;
import com.xpple.jahoqy.bean.OfferHelp;
import com.xpple.jahoqy.bean.SeekHelp;
import com.xpple.jahoqy.bean.User;
import com.xpple.jahoqy.util.CommonUtils;
import com.xpple.jahoqy.view.xlistview.SimpleFooter;
import com.xpple.jahoqy.view.xlistview.SimpleHeader;
import com.xpple.jahoqy.view.xlistview.ZrcListView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

public class AcceptHelp extends BaseActivity {
    private ZrcListView listView;
    private Handler handler;//用于接收子线程的信息以刷新主线程
    int curPage = 0;
    private AcceptHelpAdapter adapter;
    private ArrayList<OfferHelp> items = new ArrayList<>();
    private String obid;//存储传过来的求助id
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_help);
        initView();
    }

    private void initView() {
        Intent intent=getIntent();
        obid=intent.getStringExtra("shid");
        initTopBarForLeft("热心侠客", R.mipmap.actionbar_brjh, R.mipmap.ic_launcher);
        listView=(ZrcListView)findViewById(R.id.helplist);
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

        adapter = new AcceptHelpAdapter(this, items);
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

    }

    private void refresh() {
        curPage=0;
        handler.post(new Runnable() {
            @Override
            public void run() {
                Queryhelp();//queryProxy为自定义的查询类
            }
        });
    }

    private void Queryhelp() {
       BmobQuery<OfferHelp> query =new BmobQuery<>();
        query.include("user,sh");
        query.addWhereEqualTo("accept","0");
        BmobQuery<SeekHelp> shquery=new BmobQuery<>();
        //shquery.include("user");
/*        User a=new User();
        a.setObjectId(BmobUser.getCurrentUser(mContext).getObjectId());
        shquery.addWhereEqualTo("user", a);*/
        shquery.addWhereEqualTo("objectId",obid);
        query.addWhereMatchesQuery("sh", "SeekHelp", shquery);
        query.findObjects(mContext, new FindListener<OfferHelp>() {
            @Override
            public void onSuccess(List<OfferHelp> list) {
                if (list.size() != 0) {
                    if (CommonUtils.isNotNull(list)) {//监测网络等是否可用
                        items.clear();
                        adapter.addAll(list);
                        if (list.size() < 10) {
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
            public void onError(int i, String s) {
                listView.setRefreshFail("加载失败,没有任何结果");
                items.clear();
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void loadMore() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                QueryCounthelp();
            }
        });
    }

    private void QueryCounthelp() {
        BmobQuery<OfferHelp> query =new BmobQuery<>();
        query.include("user,sh");
        query.addWhereEqualTo("accept","0");
        BmobQuery<SeekHelp> shquery=new BmobQuery<>();
        shquery.addWhereEqualTo("objectId",obid);
        query.addWhereMatchesQuery("sh", "SeekHelp", shquery);
        query.count(this, OfferHelp.class, new CountListener() {
            @Override
            public void onSuccess(int count) {
                if (count > items.size()) {
                    curPage++;
                    QuerymoreOff(curPage);
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

    private void  QuerymoreOff(int page){
        BmobQuery<OfferHelp> query =new BmobQuery<>();
        query.include("user,sh");
        query.addWhereEqualTo("accept","0");
        BmobQuery<SeekHelp> shquery=new BmobQuery<>();
        shquery.addWhereEqualTo("objectId",obid);
        query.addWhereMatchesQuery("sh", "SeekHelp", shquery);
        query.setLimit(10);
        query.setSkip(page*10);
        query.findObjects(this, new FindListener<OfferHelp>() {//查询多条数据
            @Override
            public void onSuccess(List<OfferHelp> object) {
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
}
