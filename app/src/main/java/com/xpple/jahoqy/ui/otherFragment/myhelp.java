package com.xpple.jahoqy.ui.otherFragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.xpple.jahoqy.BaseClass.BaseFragment;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.adapter.myhelpAdapter;
import com.xpple.jahoqy.bean.OfferHelp;
import com.xpple.jahoqy.bean.Question;
import com.xpple.jahoqy.ui.activity.Showmytaskhelp;
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

/**
 * Created by Koreleone on 2015/10/7.
 */
public class myhelp extends BaseFragment {
    private View parentView;
    private ZrcListView listView;
    private Handler handler;
    private ArrayList<OfferHelp> items = new ArrayList<>();
    int curPage = 0;
    private myhelpAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_anything_list, container, false);
        setUpView();
        return parentView;
    }

    private void setUpView() {
        listView = (ZrcListView)parentView.findViewById(R.id.zlv_anything);
        handler = new Handler();
        // 设置下拉刷新的样式（可选，但如果没有Header则无法下拉刷新）
        SimpleHeader header = new SimpleHeader(getActivity());
        header.setTextColor(0xffee71a1);
        header.setCircleColor(0xffee71a1);
        listView.setHeadable(header);

        // 设置加载更多的样式（可选）
        SimpleFooter footer = new SimpleFooter(getActivity());
        footer.setCircleColor(0xffee71a1);
        listView.setFootable(footer);

        // 设置列表项出现动画（可选）
        listView.setItemAnimForTopIn(R.anim.top_item_in);
        listView.setItemAnimForBottomIn(R.anim.bottom_item_in);

        adapter = new myhelpAdapter(getActivity(), items);
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
                Intent item = new Intent(getActivity(), Showmytaskhelp.class);
                if(items.get(position).getResult()!=null) {
                    item.putExtra("result", items.get(position).getResult());
                    item.putExtra("comment",items.get(position).getComment());
                }
                item.putExtra("item", items.get(position).getSh().getObjectId());
                item.putExtra("ABC","Yes");
                item.putExtra("username",items.get(position).getSh().getUser().getUsername());
                item.putExtra("title",items.get(position).getSh().getTitle());
                item.putExtra("time",items.get(position).getSh().getUpdatedAt());
                item.putExtra("gn",String.valueOf(items.get(position).getSh().getGiveHelpNum()));
                item.putExtra("nn",String.valueOf(items.get(position).getSh().getNeedNum()));
                item.putExtra("Lo",String.valueOf(items.get(position).getSh().getAddress().getLongitude()));
                item.putExtra("La",String.valueOf(items.get(position).getSh().getAddress().getLatitude()));
                item.putExtra("state",items.get(position).getSh().getState());
                item.putExtra("award",String.valueOf(items.get(position).getSh().getAwardInteral()));
                item.putExtra("money",String.valueOf(items.get(position).getSh().getGivemoney()));
                item.putExtra("ofid",items.get(position).getObjectId());
                if(items.get(position).getSh().getUser().getGender()!=null) {
                    item.putExtra("sex", String.valueOf(items.get(position).getSh().getUser().getGender()));
                }
                if(items.get(position).getSh().getUser().getUserPhoto()!=null) {
                    item.putExtra("url", items.get(position).getSh().getUser().getUserPhoto().getUrl());
                }
                if(items.get(position).getResult()!=null){
                    item.putExtra("result",items.get(position).getResult());
                }
                if(items.get(position).getComment()!=null){
                    item.putExtra("comment", items.get(position).getComment());
                }

                startActivity(item);
            }
        });
    }

    private void refresh() {
        curPage=0;
        handler.post(new Runnable() {
            @Override
            public void run() {
                Querysh();//queryProxy为自定义的查询类
            }
        });
    }
    private void loadMore() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                QueryCountsh();
            }
        });
    }

    private void Querysh() {
        BmobQuery<OfferHelp> query = new BmobQuery<OfferHelp>();
        query.addWhereEqualTo("accept","1");
        query.include("sh,sh.user");
        query.addWhereEqualTo("user", BmobUser.getCurrentUser(getActivity()).getObjectId());
        query.order("-updatedAt");//时间排序
        query.setLimit(10);
        query.findObjects(getActivity(), new FindListener<OfferHelp>() {//查询多条数据
            @Override
            public void onSuccess(List<OfferHelp> object) {
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
                listView.setRefreshFail("加载失败,没有任何结果");
                items.clear();
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void QueryCountsh(){
        BmobQuery<OfferHelp> query = new BmobQuery<OfferHelp>();
        query.addWhereEqualTo("accept","1");
        query.addWhereEqualTo("user", BmobUser.getCurrentUser(getActivity()).getObjectId());
        query.count(getActivity(), Question.class, new CountListener() {
            @Override
            public void onSuccess(int count) {
                if (count > items.size()) {
                    curPage++;
                    Querymore(curPage);
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

    private void Querymore(int page){
        BmobQuery<OfferHelp> query = new BmobQuery<OfferHelp>();
        query.include("sh,sh.user");
        query.addWhereEqualTo("accept", "1");
        query.addWhereEqualTo("user", BmobUser.getCurrentUser(getActivity()).getObjectId());
        query.setLimit(10);
        query.setSkip(page * 10);
        query.order("-updatedAt");//时间排序
        query.findObjects(getActivity(), new FindListener<OfferHelp>() {//查询多条数据
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
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                listView.stopLoadMore();
            }
        });
    }
}
