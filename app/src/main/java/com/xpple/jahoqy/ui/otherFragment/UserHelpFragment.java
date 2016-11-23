package com.xpple.jahoqy.ui.otherFragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.xpple.jahoqy.BaseClass.BaseFragment;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.adapter.UserHelpAdapter;
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
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Koreleone on 2015/10/3.
 */
public class UserHelpFragment extends BaseFragment {
    private View parentView;
    private ZrcListView listView;
    private Handler handler;//用于接收子线程的信息以刷新主线程
    private UserHelpAdapter adapter;
    private ArrayList<OfferHelp> items = new ArrayList<>();
    int curPage = 0;
    private String obid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,//View是android所有组件的基类，ViewGroup是组件的容器
                             Bundle savedInstanceState)
    {
        parentView = inflater.inflate(R.layout.fragment_anything_list, container, false);
        setUpView();
        return parentView;
    }

    private void setUpView() {
        Bundle data = getArguments();
        obid=data.getString("obid");
        listView = (ZrcListView) parentView.findViewById(R.id.zlv_anything);//ZrcListView为自定义的列表控件
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
        adapter = new UserHelpAdapter(getActivity(), items);
        listView.setAdapter(adapter);
        if (items.size() <= 0)
        {
            listView.refresh(); // 主动下拉刷新
        }
    }
    private void refresh() {
        curPage=0;
        handler.post(new Runnable() {
            @Override
            public void run() {
                queryUserAid();
            }
        });
    }

    private void queryUserAid() {
        BmobQuery<OfferHelp> query=new BmobQuery<>();
        query.addWhereEqualTo("result","1");
        query.include("sh,sh.user");
        query.addWhereEqualTo("accept","1");
/*        BmobQuery<Question> shquery =new BmobQuery<>();
        shquery.include("user");
        query.addWhereMatchesQuery("sh","SeekHelp",shquery);*/
        User a=new User();
        a.setObjectId(obid);
        query.addWhereEqualTo("user", a);
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
                listView.setRefreshFail("加载失败");
                items.clear();
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void loadMore() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                queryCountAid();
            }
        });
    }

    private void queryCountAid() {
        BmobQuery<OfferHelp> query=new BmobQuery<>();
        query.addWhereEqualTo("result","1");
        query.addWhereEqualTo("accept","1");
        User a=new User();
        a.setObjectId(obid);
        query.addWhereEqualTo("user", a);
        query.setLimit(10);
        query.count(getActivity(), SeekHelp.class, new CountListener() {
            @Override
            public void onSuccess(int count) {
                if (count > items.size()) {
                    curPage++;
                    querymore(curPage);
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

    private void querymore(int curPage) {
        BmobQuery<OfferHelp> query=new BmobQuery<>();
        query.addWhereEqualTo("accept","1");
        query.include("user,sh");
        query.addWhereEqualTo("result","1");
        User a=new User();
        a.setObjectId(obid);
        query.addWhereEqualTo("user", a);

        query.setLimit(10);
        query.setSkip(curPage * 10); // 从第几条数据开始，
        query.findObjects(getActivity(), new FindListener<OfferHelp>() {
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
