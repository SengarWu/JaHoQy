package com.xpple.jahoqy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.addimages.ImageDownLoader;
import com.xpple.jahoqy.bean.ApplicationGangs;
import com.xpple.jahoqy.bean.Gangs;
import com.xpple.jahoqy.bean.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by caolin on 2015/10/7.
 */
public class GangsAplicationAdapter extends ArrayAdapter<ApplicationGangs> {

    private Context context;
    String[] str=new String[]{"帮主","副帮主",
            "青龙堂","青龙堂青木坛","青龙堂白马坛","青龙堂红袖坛",
            "白虎堂","白虎堂青木坛","白虎堂白马坛","白虎堂红袖坛",
            "朱雀堂","朱雀堂青木坛","朱雀堂白马坛","朱雀堂红袖坛",
            "玄武堂","玄武堂青木坛","玄武堂白马坛","玄武堂红袖坛",};

    public GangsAplicationAdapter(Context context, int textViewResourceId, List<ApplicationGangs> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.row_gangs_application, null);
            holder.gangs_name = (TextView) convertView.findViewById(R.id.gangs_name);
            holder.gangs_photo=(ImageView)convertView.findViewById(R.id.gangs_photo);
            holder.bt_agree=(Button)convertView.findViewById(R.id.bt_agree);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ApplicationGangs application=getItem(position);
        final int number=application.getGangsPosition();
        holder.gangs_name.setText(application.getNickName()+"请求加入"+str[GradeConvert(number)]);
        String nickName=getItem(position).getNickName();
        BmobQuery<User> query=new BmobQuery<>();
        query.addWhereEqualTo("username", nickName);
        query.findObjects(context, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                if (list != null && list.size() > 0) {
                    ImageDownLoader.showNetImage(context, list.get(0).getUserPhoto(),
                            holder.gangs_photo, R.mipmap.bg_gangs);

                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
        holder.bt_agree.setTag(application);
        holder.bt_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setEnabled(false);
                ((Button)view).setText("已同意");
                //异步更新用户数据和删除申请表的用户
                try {
                    final ApplicationGangs gangs = (ApplicationGangs) view.getTag();
                    BmobQuery<User> query=new BmobQuery<>();
                    query.addWhereEqualTo("username", gangs.getNickName());
                    query.findObjects(context, new FindListener<User>() {
                        @Override
                        public void onSuccess(List<User> list) {
                            if (list != null && list.size() > 0) {
                                try{
                                    final User userAgress=list.get(0);
                                    final String userObjectId=userAgress.getObjectId();
                                    JSONObject myJesn=new JSONObject();
                                    myJesn.put("objectId", userObjectId);
                                    myJesn.put("gangsPosition", gangs.getGangsPosition());
                                    myJesn.put("gangsName",gangs.getGangsName());

                                    AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
                                    //第一个参数是上下文对象，第二个参数是云端代码的方法名称，第三个参数是上传到云端代码的参数列表（JSONObject cloudCodeParams），第四个参数是回调类
                                    ace.callEndpoint(context, "updateGangs", myJesn,
                                            new CloudCodeListener() {
                                                @Override
                                                public void onSuccess(Object object) {
                                                    if (object.toString().equals("yes")) {
                                                        BmobQuery<Gangs> query=new BmobQuery<>();
                                                        query.addWhereEqualTo("gangsName", gangs.getGangsName());
                                                        query.findObjects(context, new FindListener<Gangs>() {
                                                            @Override
                                                            public void onSuccess(List<Gangs> list) {
                                                                if(list!=null&list.size()>0){
                                                                    final Gangs ga=list.get(0);
                                                                    User my= BmobUser.getCurrentUser(context, User.class);
                                                                    try{
                                                                        if(ga.getGangsCreater()!=my.getUsername() ){
                                                                            EMGroupManager.getInstance().inviteUser(ga.getGangsObjectId(), new String[]{userAgress.getMobilePhoneNumber()}, null);
                                                                        }else{
                                                                            EMGroupManager.getInstance().addUsersToGroup(ga.getGangsObjectId(), new String[]{userAgress.getMobilePhoneNumber()});
                                                                        }
                                                                        gangs.delete(context, new DeleteListener() {
                                                                            @Override
                                                                            public void onSuccess() {
                                                                                Toast.makeText(context, "已添加", Toast.LENGTH_SHORT).show();
                                                                            }

                                                                            @Override
                                                                            public void onFailure(int i, String s) {
                                                                                //
                                                                            }
                                                                        });
                                                                    }catch (EaseMobException e){
                                                                        Toast.makeText(context,"申请失败，请检查网络设置！",Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onError(int i, String s) {

                                                            }
                                                        });

                                                    } else {
                                                        Toast.makeText(context,object.toString(),Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                                @Override
                                                public void onFailure(int code, String msg) {
                                                    // TODO Auto-generated method stub
                                                    Toast.makeText(context,"申请失败，请检查网络设置！",Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }catch (JSONException e){
                                    Toast.makeText(context, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });
                }catch (Exception e){
                    Toast.makeText(context, e.getMessage().toString(), Toast.LENGTH_LONG).show();
                }


            }
        });
        return convertView;
    }
    private static class ViewHolder {
        ImageView gangs_photo;
        TextView gangs_name;
        Button bt_agree;
    }
    public int GradeConvert(int num){
        int grade=0;
        int num1=num/10;
        if (num1==0){grade=0;}
        else if(num1==1){grade=1;}
        else if(num1>1){
            grade=2;
            grade+=(num1-2)*4;
            int num2=num%10;
            if(num2!=0){
                grade+=((num2-1)/3+1);
            }
        }
        return grade;
    }
    public int GradeIcon(int num){
        int grade=0;
        int num2=num%10;
        if(num2==0){
            grade=num/10;
        }else{
            grade=6;
            grade+=(num2-1)/3;
        }
        return grade;
    }
}
