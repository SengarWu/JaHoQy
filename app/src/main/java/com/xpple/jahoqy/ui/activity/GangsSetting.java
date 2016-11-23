package com.xpple.jahoqy.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.easemob.chat.EMGroupManager;
import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.bean.ApplicationGangs;
import com.xpple.jahoqy.bean.Gangs;
import com.xpple.jahoqy.bean.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by caolin on 2015/10/4.
 */
public class GangsSetting extends BaseActivity {
    private Gangs gangs;
    private Button GangAppointment;
    private Button EnterGangApply;
    private Button GangAnnounce;

    private Button btn_gangs_type;
    private Button btn_gangs_purpose;
    private Button btn_exit_gangs;
    private Button btn_discuss_gangs;
    private int gangsTypeNumber=-1;
    String[] gangsType=new String[]{
            "高校生活","家园小区","城市互帮",
            "学习互助","生活帮忙","休闲娱乐"};
    AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gangs_setting);
        intiView();
        intiData();
    }
    private void intiData() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        initTopBarForLeft("帮派设置", R.mipmap.actionbar_gangs, R.color.color_transparent_bg);
        GangAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BmobQuery<User> query=new BmobQuery<User>();
                query.addWhereEqualTo("objectId",CurrentUser.getObjectId());
                query.findObjects(mContext, new FindListener<User>() {
                    @Override
                    public void onSuccess(List<User> list) {
                        if(list!=null&&list.size()>0){
                            final int number = list.get(0).getGangsPosition();
                            int num2 = number % 10;
                            if (num2 != 0 && (num2 - 1) % 3 != 0) {
                                showToast("您没有任命的权限！！");
                            } else {
                                startActivity(new Intent(GangsSetting.this, GangsAppointmentActivity.class));
                            }
                        }
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });

            }
        });
        EnterGangApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BmobQuery<User> query = new BmobQuery<User>();
                query.addWhereEqualTo("objectId", CurrentUser.getObjectId());
                query.findObjects(mContext, new FindListener<User>() {
                    @Override
                    public void onSuccess(List<User> list) {
                        if (list != null && list.size() > 0) {
                            final int number = list.get(0).getGangsPosition();
                            int num2 = number % 10;
                            if (num2 != 0 && (num2 - 1) % 3 != 0) {
                                showToast("您没有添加群众的权限！！");
                            } else {
                                int end;
                                int num1 = number / 10;
                                int start = number;
                                if (num1 < 2) {
                                    end = 100;
                                } else {
                                    if (num2 == 0) {
                                        end = number + 10;
                                    } else {
                                        end = number + 3;
                                    }
                                }
                                BmobQuery<ApplicationGangs> query = new BmobQuery<>();
                                query.addWhereLessThan("gangsPosition", end);
                                query.addWhereGreaterThan("gangsPosition", start);
                                query.addWhereEqualTo("gangsName", list.get(0).getGangsName());
                                query.findObjects(mContext, new FindListener<ApplicationGangs>() {
                                    @Override
                                    public void onSuccess(List<ApplicationGangs> list) {
                                        if (list != null) {
                                            if (list.size() == 0) {
                                                showToast("无用户申请加入！！");
                                            } else {
                                                startActivity(new Intent(GangsSetting.this, GangsApplicationActivity.class));
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError(int i, String s) {
                                        showToast("网络出错，请检查网络设置");
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });

            }
        });
        GangAnnounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BmobQuery<User> query = new BmobQuery<User>();
                query.addWhereEqualTo("objectId", CurrentUser.getObjectId());
                query.findObjects(mContext, new FindListener<User>() {
                    @Override
                    public void onSuccess(List<User> list) {
                        if (list != null && list.size() > 0) {
                            if (list.get(0).getGangsPosition() < 20) {
                                startAnimActivity(textDialog.class);
                            } else {
                                showToast("您没有修改公告的权限！！");
                            }
                        }
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
            }
        });
        btn_gangs_purpose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BmobQuery<User> query = new BmobQuery<User>();
                query.addWhereEqualTo("objectId", CurrentUser.getObjectId());
                query.findObjects(mContext, new FindListener<User>() {
                    @Override
                    public void onSuccess(List<User> list) {
                        if (list != null && list.size() > 0) {
                            if (list.get(0).getGangsPosition() < 20) {
                                startAnimActivity(activity_dialog_purpose.class);
                            } else {
                                showToast("您没有修改宗旨的权限！！");
                            }
                        }
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
            }
        });

        BmobQuery<User> query=new BmobQuery<User>();
        query.addWhereEqualTo("objectId", CurrentUser.getObjectId());
        query.findObjects(mContext, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                if (list != null && list.size() > 0) {
                    BmobQuery<Gangs> query=new BmobQuery<>();
                    query.addWhereEqualTo("gangsName", list.get(0).getGangsName());
                    query.findObjects(mContext, new FindListener<Gangs>() {
                        @Override
                        public void onSuccess(List<Gangs> list) {
                            if (list != null && list.size() > 0) {
                                gangs = list.get(0);
                                if (gangs.getGangsType() != null) {
                                    String gt = gangs.getGangsType();
                                    for (int i = 0; i < gangsType.length; i++) {
                                        String type = gangsType[i];
                                        if (gt == type) {
                                            gangsTypeNumber = i;
                                        }
                                    }
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



        btn_gangs_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                alertDialog.setTitle("帮派类型").setIcon(R.drawable.ic_launcher);
                alertDialog.setSingleChoiceItems(gangsType, gangsTypeNumber, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        gangsTypeNumber = i;
                        gangs.setGangsType(gangsType[gangsTypeNumber]);
                        gangs.update(mContext, new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                showToast("帮派类型更新成功");
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                showToast("请检查网络设施");
                            }
                        });
                        dialog.dismiss();
                    }
                });
                dialog = alertDialog.create();
                dialog.show();
            }
        });

        btn_exit_gangs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BmobQuery<User> query=new BmobQuery<User>();
                query.addWhereEqualTo("objectId",CurrentUser.getObjectId());
                query.findObjects(mContext, new FindListener<User>() {
                    @Override
                    public void onSuccess(List<User> list) {
                        if(list!=null&&list.size()>0){
                            if(list.get(0).getGangsPosition()!=0){
                                //退出帮派
                                dialog = new AlertDialog.Builder(mContext).setTitle("退出帮派").
                                        setMessage("您确定退出帮派吗？")
                                        .setIcon(R.drawable.ic_launcher)
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // TODO Auto-generated method stub  
                                                progressDialog=new ProgressDialog(mContext);
                                                progressDialog.setMessage("正在做退出处理...");
                                                exitGrop(gangs.getGangsObjectId());
                                            }
                                        })
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // TODO Auto-generated method stub 
                                                dialog.cancel();
                                            }
                                        }).create();
                                dialog.show();
                            }else{
                                showToast("群主只能解散群");
                            }
                        }
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });


            }
        });
        btn_discuss_gangs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BmobQuery<User> query=new BmobQuery<User>();
                query.addWhereEqualTo("objectId",CurrentUser.getObjectId());
                query.findObjects(mContext, new FindListener<User>() {
                    @Override
                    public void onSuccess(final List<User> list) {
                        if (list != null && list.size() > 0) {
                            //解散帮派
                            dialog = new AlertDialog.Builder(mContext).setTitle("解散帮派").
                                    setMessage("您确定解散帮派吗？")
                                    .setIcon(R.drawable.ic_launcher)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO Auto-generated method stub  
                                            if (list.get(0).getGangsPosition() == 0) {
                                                progressDialog = new ProgressDialog(mContext);
                                                progressDialog.setMessage("正在解散帮派...");
                                                deleteGrop(gangs.getGangsObjectId());
                                            } else {
                                                showToast("没有解散群的权限");
                                            }
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO Auto-generated method stub 
                                            dialog.cancel();
                                        }
                                    }).create();
                            dialog.show();
                        }
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
            }
        });
    }

    private void intiView() {
        GangAppointment=$(R.id.GangAppointment);
        EnterGangApply=$(R.id.EnterGangApply);
        GangAnnounce=$(R.id.GangAnnounce);

        btn_gangs_type=$(R.id.btn_gangs_type);
        btn_gangs_purpose=$(R.id.btn_gangs_purpose);
        btn_exit_gangs=$(R.id.btn_exit_gangs);
        btn_discuss_gangs=$(R.id.btn_discuss_gangs);
    }

//    @Override
//    public void onBackPressed() {
//        setResult(14);
//        super.onBackPressed();
//    }

    private ProgressDialog progressDialog;
    /**
     * 退出群组
     *
     * @param groupId
     */
    private void exitGrop(final String groupId) {
        String st1 = getResources().getString(R.string.Exit_the_group_chat_failure);
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMGroupManager.getInstance().exitFromGroup(groupId);
                    JSONObject myJesn=new JSONObject();
                    try{
                        myJesn.put("objectId", CurrentUser.getObjectId());
                        myJesn.put("gangsPosition", 0);
                        myJesn.put("gangsName","");

                        AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
                        //第一个参数是上下文对象，第二个参数是云端代码的方法名称，第三个参数是上传到云端代码的参数列表（JSONObject cloudCodeParams），第四个参数是回调类
                        ace.callEndpoint(mContext, "updateGangs", myJesn,
                                new CloudCodeListener() {
                                    @Override
                                    public void onSuccess(Object object) {
                                        String str=object.toString();
                                        if (str.equals("yes") ){
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    progressDialog.dismiss();
                                                    setResult(14);
                                                    finish();
                                                    if (ChatActivity.activityInstance != null)
                                                        ChatActivity.activityInstance.finish();
                                                }
                                            });
                                        } else {
                                            showToast(object.toString());
                                        }
                                    }
                                    @Override
                                    public void onFailure(int code, String msg) {
                                        // TODO Auto-generated method stub
                                        //检查网络设置
                                    }
                                });
                    }catch (JSONException e){
                        showToast("josn化失败");
                    }
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            showToast("请检查网络设置");
                        }
                    });
                }
            }
        }).start();
    }
    /**
     * 解散群组
     *
     * @param groupId
     */
    private void deleteGrop(final String groupId) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMGroupManager.getInstance().exitAndDeleteGroup(groupId);//需异步处理
                    JSONObject myJesn=new JSONObject();
                    try{
                        myJesn.put("gangsName",gangs.getGangsName());

                        AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
                        //第一个参数是上下文对象，第二个参数是云端代码的方法名称，第三个参数是上传到云端代码的参数列表（JSONObject cloudCodeParams），第四个参数是回调类
                        ace.callEndpoint(mContext, "dissolutionGangs", myJesn,
                                new CloudCodeListener() {
                                    @Override
                                    public void onSuccess(Object object) {
                                        String str=object.toString();
                                        if (str.equals("yes") ){
                                            gangs.delete(mContext, new DeleteListener() {
                                                @Override
                                                public void onSuccess() {

                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            progressDialog.dismiss();
                                                            setResult(14);
                                                            finish();
                                                            if (ChatActivity.activityInstance != null)
                                                                ChatActivity.activityInstance.finish();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onFailure(int i, String s) {

                                                }
                                            });
                                        } else {
                                            showToast(object.toString());
                                        }
                                    }
                                    @Override
                                    public void onFailure(int code, String msg) {
                                        // TODO Auto-generated method stub
                                        //检查网络设置
                                        showToast("检查网络设置");
                                    }
                                });
                    }catch (JSONException e){
                        showToast("josn化失败");
                    }
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            showToast("检查网络设置");
                        }
                    });
                }
            }
        }).start();
    }
}
