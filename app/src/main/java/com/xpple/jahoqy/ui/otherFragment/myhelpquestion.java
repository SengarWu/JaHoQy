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
import com.xpple.jahoqy.adapter.helpAnswerAdapter;
import com.xpple.jahoqy.bean.Question;
import com.xpple.jahoqy.bean.User;
import com.xpple.jahoqy.ui.activity.ShowQuesiondetails;
import com.xpple.jahoqy.util.CommonUtils;
import com.xpple.jahoqy.view.xlistview.SimpleFooter;
import com.xpple.jahoqy.view.xlistview.SimpleHeader;
import com.xpple.jahoqy.view.xlistview.ZrcListView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

public class myhelpquestion extends BaseFragment {
    private View parentView;
    private ZrcListView listView;
    private Handler handler;
    private ArrayList<Question> items = new ArrayList<>();
    int curPage = 0;
    private helpAnswerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_anything_list, container, false);
        setUpView();
        return parentView;
    }

    private void setUpView() {
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

        adapter = new helpAnswerAdapter(getActivity(), items);
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
                Intent item = new Intent(getActivity(), ShowQuesiondetails.class);
                item.putExtra("item", items.get(position).getObjectId());
                item.putExtra("username", items.get(position).getUser().getUsername());
                if(items.get(position).getUser().getUserPhoto()!=null) {
                    item.putExtra("url", items.get(position).getUser().getUserPhoto().getUrl());
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
                Queryhelpquestion();
                // Querymyquestion();//queryProxy为自定义的查询类
            }
        });
    }

    private void Queryhelpquestion() {
        BmobQuery<Question> query=new BmobQuery<>();
        query.order("-updatedAt");
        query.include("user");
        User user=new User();
        user.setObjectId(BmobUser.getCurrentUser(getActivity()).getObjectId());
        query.addWhereRelatedTo("answerQuestion", new BmobPointer(user));

         query.findObjects(getActivity(), new FindListener<Question>() {
             @Override
             public void onSuccess(List<Question> list) {
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

             }
         });
    }

    private void loadMore() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                QueryCountmyquestion();
            }
        });
    }

    private void QueryCountmyquestion() {
        BmobQuery<Question> query=new BmobQuery<>();
        query.include("user");
        User user=new User();
        user.setObjectId(BmobUser.getCurrentUser(getActivity()).getObjectId());
        query.addWhereRelatedTo("answerQuestion", new BmobPointer(user));
        query.count(getActivity(), Question.class, new CountListener() {
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
        BmobQuery<Question> query=new BmobQuery<>();
        query.order("-updatedAt");
        query.include("user");
        User user=new User();
        user.setObjectId(BmobUser.getCurrentUser(getActivity()).getObjectId());
        query.addWhereRelatedTo("answerQuestion", new BmobPointer(user));
        query.setLimit(10);
        query.setSkip(page*10);
        query.findObjects(getActivity(), new FindListener<Question>() {//查询多条数据
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
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                listView.stopLoadMore();
            }
        });
    }



}
