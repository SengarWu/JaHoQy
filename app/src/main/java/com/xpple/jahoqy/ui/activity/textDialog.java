package com.xpple.jahoqy.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.bean.Gangs;
import com.xpple.jahoqy.bean.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by caolin on 2015/10/14.
 */
public class textDialog extends BaseActivity {
    private Button bt_submit;
    private Button bt_cancel;
    private EditText tv_ganggao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_text);
        bt_cancel=$(R.id.bt_cancel);
        bt_submit=$(R.id.bt_submit);
        tv_ganggao=$(R.id.tv_ganggao);
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tv_ganggao.getText().toString().equals("")){
                    showToast("内容不能为空");
                    return;
                }
                CurrentUser= BmobUser.getCurrentUser(mContext, User.class);

                BmobQuery<User> query=new BmobQuery<>();
                query.addWhereEqualTo("objectId",CurrentUser.getObjectId());
                query.findObjects(mContext, new FindListener<User>() {
                    @Override
                    public void onSuccess(List<User> list) {
                        if(list!=null&&list.size()>0)
                        {
                            BmobQuery<Gangs> query=new BmobQuery<>();
                            query.addWhereEqualTo("gangsName", list.get(0).getGangsName());
                            query.findObjects(mContext, new FindListener<Gangs>() {
                                @Override
                                public void onSuccess(List<Gangs> list) {
                                    if (list != null && list.size() > 0) {
                                        final Gangs gangs = list.get(0);
                                        gangs.setGanggao(tv_ganggao.getText().toString());
                                        gangs.update(mContext, new UpdateListener() {
                                            @Override
                                            public void onSuccess() {
                                                showToast("公告添加成功");
                                                finish();
                                            }

                                            @Override
                                            public void onFailure(int i, String s) {

                                                showToast("请检查网络设置");
                                                finish();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onError(int i, String s) {
                                    showToast("公告添加失败");
                                    finish();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        showToast("请检查网络设置");
                        finish();
                    }
                });

            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


}