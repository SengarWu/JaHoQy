package com.xpple.jahoqy.ui.otherFragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xpple.jahoqy.BaseClass.BaseFragment;
import com.xpple.jahoqy.CustomApplication;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.adapter.nearuserAdapter;
import com.xpple.jahoqy.bean.NearUser;
import com.xpple.jahoqy.bean.User;
import com.xpple.jahoqy.ui.activity.Shownearuser;
import com.xpple.jahoqy.util.CommonUtils;
import com.xpple.jahoqy.view.xlistview.SimpleFooter;
import com.xpple.jahoqy.view.xlistview.SimpleHeader;
import com.xpple.jahoqy.view.xlistview.ZrcListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;


/**
 * Created by Koreleone on 2015/9/12.
 */
public class nearFragment extends BaseFragment {
    private View parentView;
    private ZrcListView listView;
    private Handler handler;//用于接收子线程的信息以刷新主线程
    private nearuserAdapter adapter;
    private ArrayList<NearUser> items = new ArrayList<>();
    int curPage = 0;
    public nearFragment(){}
    private Date Now;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,//View是android所有组件的基类，ViewGroup是组件的容器
                             Bundle savedInstanceState)
    {
        parentView = inflater.inflate(R.layout.fragment_anything_list, container, false);
        setUpView();
        return parentView;
    }

    private void setUpView()
    {
        Now=new Date(System.currentTimeMillis());
        listView = (ZrcListView)parentView.findViewById(R.id.zlv_anything);//ZrcListView为自定义的列表控件
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

        adapter = new nearuserAdapter(getActivity(), items);
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
                Intent item = new Intent(getActivity(),
                        Shownearuser.class);
                item.putExtra("item", items.get(position).getUser().getObjectId());
                startActivity(item);
            }
        });

    }

    private void refresh() {
        curPage=0;
        handler.post(new Runnable() {
            @Override
            public void run() {
                Querynearuserimfo();//queryProxy为自定义的查询类
            }
        });
    }
    private void loadMore() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                QueryCountnearuserimfo();
            }
        });
    }
    public void Querynearuserimfo(){

            BmobQuery<NearUser> query=new BmobQuery<>();

            BmobQuery<User> userquery=new BmobQuery<>();
            userquery.addWhereNotEqualTo("objectId", BmobUser.getCurrentUser(getActivity()).getObjectId());
            query.addWhereMatchesQuery("user", "_User", userquery);

            query.include("user");
            query.addWhereWithinRadians("place", new BmobGeoPoint(CustomApplication.lastPoint.getLongitude(), CustomApplication.lastPoint.getLatitude()), 3);

            Date d=new Date(Now.getTime()-600000);
            query.addWhereGreaterThan("updatedAt", new BmobDate(d));

            query.setLimit(10);
            query.findObjects(getActivity(), new FindListener<NearUser>() {//查询多条数据
                @Override
                public void onSuccess(List<NearUser> object) {
                    if (object.size() != 0) {
                        items.clear();//items是列表中的数据项
                        adapter.addAll(object);//用适配器向列表中填数据
                        if (object.size() < 10) {
                            listView.setRefreshSuccess("加载完成"); // 通知加载完成
                            listView.stopLoadMore();
                        } else {
                            listView.setRefreshSuccess("加载成功"); // 通知加载成功
                            listView.startLoadMore();
                        }
                    } else {
                        listView.setRefreshFail("没有搜到其他大虾");
                        listView.stopLoadMore();
                    }
                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onError(int code, String msg) {
                    listView.setRefreshFail("加载失败");
                    listView.stopLoadMore();
                }
            });
    }

    public void QueryCountnearuserimfo()
    {
        BmobQuery<NearUser> query = new BmobQuery<>();
        Date d=new Date(Now.getTime()-600000);
        query.addWhereGreaterThan("updatedAt", new BmobDate(d));
        query.addWhereWithinRadians("place", new BmobGeoPoint(CustomApplication.lastPoint.getLongitude(), CustomApplication.lastPoint.getLatitude()), 3);
        query.count(getActivity(), NearUser.class, new CountListener() {
            @Override
            public void onSuccess(int count) {
                if (count > items.size()) {
                    curPage++;
                    QueryMorenearuserimfo(curPage);
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



    public void QueryMorenearuserimfo(int page){
        BmobQuery<NearUser> query=new BmobQuery<>();
        BmobQuery<User> userquery=new BmobQuery<>();
        userquery.addWhereNotEqualTo("objectId", BmobUser.getCurrentUser(getActivity()).getObjectId());
        query.addWhereMatchesQuery("user", "_User", userquery);
        query.include("user");
        query.addWhereWithinRadians("place", new BmobGeoPoint(CustomApplication.lastPoint.getLongitude(), CustomApplication.lastPoint.getLatitude()), 3);

        Date d=new Date(Now.getTime()-600000);
        query.addWhereGreaterThan("updatedAt", new BmobDate(d));
        query.setLimit(10);
        query.setSkip(page * 10);
        query.findObjects(getActivity(), new FindListener<NearUser>() {//查询多条数据
            @Override
            public void onSuccess(List<NearUser> object) {
                if (CommonUtils.isNotNull(object)) {
                    adapter.addAll(object);
                }
                adapter.notifyDataSetChanged();
                listView.setLoadMoreSuccess();
            }

            @Override
            public void onError(int code, String msg) {
                listView.stopLoadMore();
            }
        });
    }

}
