package com.xpple.jahoqy.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.xpple.jahoqy.BaseClass.DemoHXSDKHelper;
import com.xpple.jahoqy.BaseClass.HXSDKHelper;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.addimages.ImageDownLoader;
import com.xpple.jahoqy.bean.HuanXinUser;
import com.xpple.jahoqy.bean.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class UserUtils {
    /**
     * 根据username获取相应user，由于demo没有真实的用户数据，这里给的模拟的数据；
     * @param username
     * @return
     */
    public static HuanXinUser getUserInfo(String username){
        HuanXinUser user = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList().get(username);
        if(user == null){
            user = new HuanXinUser(username);
        }
            
        if(user != null){
            //demo没有这些数据，临时填充
        	if(TextUtils.isEmpty(user.getNick()))
        		user.setNick(username);
        }
        return user;
    }
    
    /**
     * 设置用户头像
     * @param username
     */
    public static void setUserAvatar(final Context context, String username, final ImageView imageView){
    	//HuanXinUser user = getUserInfo(username);

		BmobQuery<User> query=new BmobQuery<>();
		query.addWhereEqualTo("mobilePhoneNumber",username);
		query.findObjects(context, new FindListener<User>() {
			@Override
			public void onSuccess(List<User> list) {
				if(list!=null&&list.size()>0){
					ImageDownLoader.showNetImage(context, list.get(0).getUserPhoto(), imageView, R.drawable.default_avatar);
				}
			}

			@Override
			public void onError(int i, String s) {

			}
		});

//        if(user != null && user.getAvatar() != null){
//			Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
//        }else{
//			Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
//        }
    }
    
    /**
     * 设置当前用户头像
     */
	public static void setCurrentUserAvatar(final Context context, final ImageView imageView) {
		HuanXinUser user = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();

		BmobQuery<User> query=new BmobQuery<>();
		query.addWhereEqualTo("mobilePhoneNumber",user.getUsername());
		query.findObjects(context, new FindListener<User>() {
			@Override
			public void onSuccess(List<User> list) {
				if(list!=null&&list.size()>0){
					ImageDownLoader.showNetImage(context, list.get(0).getUserPhoto(), imageView, R.drawable.default_avatar);
				}
			}

			@Override
			public void onError(int i, String s) {

			}
		});
//		if (user != null && user.getAvatar() != null) {
//			Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
//		} else {
//			Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
//		}
	}
    
    /**
     * 设置用户昵称
     */
    public static void setUserNick(String username,TextView textView){
    	HuanXinUser user = getUserInfo(username);
    	if(user != null){
    		textView.setText(user.getNick());
    	}else{
    		textView.setText(username);
    	}
    }
    
    /**
     * 设置当前用户昵称
     */
    public static void setCurrentUserNick(TextView textView){
    	HuanXinUser user = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
    	if(textView != null){
    		textView.setText(user.getNick());
    	}
    }
    
    /**
     * 保存或更新某个用户
     * @param user
     */
	public static void saveUserInfo(HuanXinUser newUser) {
		if (newUser == null || newUser.getUsername() == null) {
			return;
		}
		((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveContact(newUser);
	}
    
}
