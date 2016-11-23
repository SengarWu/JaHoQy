package com.xpple.jahoqy.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMGroupChangeListener;
import com.easemob.EMNotifierEvent;
import com.easemob.EMValueCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;
import com.easemob.util.NetUtils;
import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.BaseClass.Constant;
import com.xpple.jahoqy.BaseClass.DemoHXSDKHelper;
import com.xpple.jahoqy.BaseClass.HXSDKHelper;
import com.xpple.jahoqy.BaseClass.InviteMessgeDao;
import com.xpple.jahoqy.BaseClass.UserDao;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.bean.HuanXinUser;
import com.xpple.jahoqy.bean.InviteMessage;
import com.xpple.jahoqy.bean.InviteMessage.InviteMesageStatus;
import com.xpple.jahoqy.bean.MyBmobInstallation;
import com.xpple.jahoqy.bean.User;
import com.xpple.jahoqy.ui.mainFragment.GangsFragment;
import com.xpple.jahoqy.ui.mainFragment.IndexFragment;
import com.xpple.jahoqy.ui.mainFragment.MeFragment;
import com.xpple.jahoqy.ui.mainFragment.MessageFragment;
import com.xpple.jahoqy.ui.mainFragment.NoGangsFragment;
import com.xpple.jahoqy.util.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener,EMEventListener {
    private RadioButton rb_index, rb_message, rb_gangs, rb_me;

    private int index;
    public IndexFragment indexFragment;
    public MessageFragment messageFragment;
    public GangsFragment gangsFragment;
    public NoGangsFragment noGangsFragment;
    public MeFragment meFragment;

    public static Fragment[] fragments,fragments0,fragments1;

    // 当前fragment的index
    private int currentTabIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initDevice();
        indexFragment = new IndexFragment();
        messageFragment = new MessageFragment();
        gangsFragment = new GangsFragment();
        meFragment = new MeFragment();
        noGangsFragment = new NoGangsFragment(); //用户没有加入任何帮派时显示的页面

        fragments0 = new Fragment[] {indexFragment, messageFragment, gangsFragment, meFragment};

        fragments1 = new Fragment[] {indexFragment, messageFragment, noGangsFragment, meFragment};

        if (CurrentUser.getGangsName()!= null&&!CurrentUser.getGangsName().equals(""))
        {
            fragments = fragments0;
        }
        else
        {
            fragments = fragments1;
        }
        // 添加显示第一个fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, indexFragment)
                .add(R.id.fragment_container, messageFragment)
                .add(R.id.fragment_container, noGangsFragment)
                .add(R.id.fragment_container, gangsFragment)
                .add(R.id.fragment_container, meFragment)
                .hide(noGangsFragment).hide(gangsFragment)
                .hide(messageFragment).hide(meFragment)
                .show(indexFragment)
                .commit();
        currentTabIndex = 0;

        BmobQuery<User> query =new BmobQuery<>();
        query.addWhereEqualTo("objectId", CurrentUser.getObjectId());
        query.findObjects(mContext, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                if(list.get(0).getGangsName()==null||list.get(0).getGangsName().equals("")){
                    fragments = fragments1;
                }
                else{
                    fragments = fragments0;
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });

        //曹林
        if (savedInstanceState != null && savedInstanceState.getBoolean(Constant.ACCOUNT_REMOVED, false)) {
            // 防止被移除后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
            // 三个fragment里加的判断同理
            DemoHXSDKHelper.getInstance().logout(true,null);
            BmobUser.logOut(mContext);
            SharedPreferences setting = getSharedPreferences("setting", 0);
            setting.edit().putBoolean("FIRST", true).apply();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        } else if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
            // 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
            // 三个fragment里加的判断同理
            DemoHXSDKHelper.getInstance().logout(true,null);
            BmobUser.logOut(mContext);
            SharedPreferences setting = getSharedPreferences("setting", 0);
            setting.edit().putBoolean("FIRST", true).apply();
            finish();
            startActivity(new Intent(this,LoginActivity.class));
            return;
        }

        if (getIntent().getBooleanExtra("conflict", false) && !isConflictDialogShow) {
            showConflictDialog();
        } else if (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
            showAccountRemovedDialog();
        }

        inviteMessgeDao = new InviteMessgeDao(this);
        userDao = new UserDao(this);
        init();
        //异步获取当前用户的昵称和头像
        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager().asyncGetCurrentUserInfo();
    }

    private void initDevice() {
        //保存设备信息
        BmobInstallation.getCurrentInstallation(mContext).save();

        BmobQuery<MyBmobInstallation> query = new BmobQuery<MyBmobInstallation>();//新建对设备的查询
        query.addWhereEqualTo("installationId", BmobInstallation.getInstallationId(mContext));//查询条件为匹配当前设备
        query.findObjects(MainActivity.this, new FindListener<MyBmobInstallation>() {

            @Override
            public void onSuccess(List<MyBmobInstallation> object) {
                // TODO Auto-generated method stub
                if(object.size() > 0){
                    MyBmobInstallation mbi = object.get(0);
                    mbi.setUserName(CurrentUser.getUsername());//登录成功是设为本用户名
                    mbi.update(MainActivity.this, new UpdateListener() {

                        @Override
                        public void onSuccess() {
                            // TODO Auto-generated method stub
                            Log.i("bmob", "设备信息更新成功");
                        }

                        @Override
                        public void onFailure(int code, String msg) {
                            // TODO Auto-generated method stub
                            Log.i("bmob", "设备信息更新失败:" + msg);
                        }
                    });
                } else {
                }
            }

            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void initView() {
        RadioGroup rg_main = $(R.id.rg_main);
        rb_index = $(R.id.rb_index);
        rb_message = $(R.id.rb_message);
        rb_gangs = $(R.id.rb_gangs);
        rb_me = $(R.id.rb_me);
        rg_main.setOnCheckedChangeListener(this);
        rb_index.setChecked(true);

        //unreadLabel = (TextView) findViewById(R.id.unread_msg_number);
        //unreadAddressLable = (TextView) findViewById(R.id.unread_address_number);
    }

    private void changeFragment(Fragment targetFragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    @Override
    public void onCheckedChanged(RadioGroup arg0, int checkedId) {

        if (rb_index.getId() == checkedId) {
            index = 0;
        } else if (rb_message.getId() == checkedId) {
            index = 1;
        } else if (rb_gangs.getId() == checkedId) {
            index = 2;
        } else if (rb_me.getId() == checkedId) {
            index = 3;
        }
        if(index==2){
            undateGangs();
        }
        if(index==3){
            meFragment.updateInfo();
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            trx.show(fragments[index]).commit();
        }
        currentTabIndex = index;
    }


    private Boolean ActionSheetFlag = false;

    private long firstTime;

    /**
     * 连续按两次返回键就退出
     */

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (ActionSheetFlag) {
            super.onBackPressed();
        } else {
            if (firstTime + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
            } else {
                showToast("再按一次退出程序");
            }
            finish();
            firstTime = System.currentTimeMillis();
        }
    }


    //曹林
    protected static final String TAG = "MainActivity";
//    // 未读消息textview
//    private TextView unreadLabel;
//    // 未读通讯录textview
//    private TextView unreadAddressLable;
    // 账号在别处登录
    public boolean isConflict = false;
    // 账号被移除
    private boolean isCurrentAccountRemoved = false;

    private MyConnectionListener connectionListener = null;
    private MyGroupChangeListener groupChangeListener = null;

    /**
     * 检查当前用户是否被删除
     */
    public boolean getCurrentAccountRemoved() {
        return isCurrentAccountRemoved;
    }

    private void init() {
        // setContactListener监听联系人的变化等
        EMContactManager.getInstance().setContactListener(new MyContactListener());
        // 注册一个监听连接状态的listener

        connectionListener = new MyConnectionListener();
        EMChatManager.getInstance().addConnectionListener(connectionListener);

        groupChangeListener = new MyGroupChangeListener();
        // 注册群聊相关的listener
        EMGroupManager.getInstance().addGroupChangeListener(groupChangeListener);


        //内部测试方法，请忽略
        registerInternalDebugReceiver();
    }

    static void asyncFetchGroupsFromServer(){
        HXSDKHelper.getInstance().asyncFetchGroupsFromServer(new EMCallBack() {

            @Override
            public void onSuccess() {
                HXSDKHelper.getInstance().noitifyGroupSyncListeners(true);

                if (HXSDKHelper.getInstance().isContactsSyncedWithServer()) {
                    HXSDKHelper.getInstance().notifyForRecevingEvents();
                }
            }

            @Override
            public void onError(int code, String message) {
                HXSDKHelper.getInstance().noitifyGroupSyncListeners(false);
            }

            @Override
            public void onProgress(int progress, String status) {

            }

        });
    }

    static void asyncFetchContactsFromServer(){
        HXSDKHelper.getInstance().asyncFetchContactsFromServer(new EMValueCallBack<List<String>>() {

            @Override
            public void onSuccess(List<String> usernames) {
                Context context = HXSDKHelper.getInstance().getAppContext();

                System.out.println("----------------" + usernames.toString());
                EMLog.d("roster", "contacts size: " + usernames.size());
                Map<String, HuanXinUser> userlist = new HashMap<String, HuanXinUser>();
                for (String username : usernames) {
                    HuanXinUser user = new HuanXinUser();
                    user.setUsername(username);
                    setUserHearder(username, user);
                    userlist.put(username, user);
                }
                // 添加user"申请与通知"
                HuanXinUser newFriends = new HuanXinUser();
                newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
                String strChat = context.getString(R.string.Application_and_notify);
                newFriends.setNick(strChat);

                userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
                // 添加"群聊"
                HuanXinUser groupUser = new HuanXinUser();
                String strGroup = context.getString(R.string.group_chat);
                groupUser.setUsername(Constant.GROUP_USERNAME);
                groupUser.setNick(strGroup);
                groupUser.setHeader("");
                userlist.put(Constant.GROUP_USERNAME, groupUser);

                // 添加"聊天室"
                HuanXinUser chatRoomItem = new HuanXinUser();
                String strChatRoom = context.getString(R.string.chat_room);
                chatRoomItem.setUsername(Constant.CHAT_ROOM);
                chatRoomItem.setNick(strChatRoom);
                chatRoomItem.setHeader("");
                userlist.put(Constant.CHAT_ROOM, chatRoomItem);

                // 添加"Robot"
                HuanXinUser robotUser = new HuanXinUser();
                String strRobot = context.getString(R.string.robot_chat);
                robotUser.setUsername(Constant.CHAT_ROBOT);
                robotUser.setNick(strRobot);
                robotUser.setHeader("");
                userlist.put(Constant.CHAT_ROBOT, robotUser);

                // 存入内存
                ((DemoHXSDKHelper) HXSDKHelper.getInstance()).setContactList(userlist);
                // 存入db
                UserDao dao = new UserDao(context);
                List<HuanXinUser> users = new ArrayList<HuanXinUser>(userlist.values());
                dao.saveContactList(users);

                HXSDKHelper.getInstance().notifyContactsSyncListener(true);

                if (HXSDKHelper.getInstance().isGroupsSyncedWithServer()) {
                    HXSDKHelper.getInstance().notifyForRecevingEvents();
                }

                ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager().asyncFetchContactInfosFromServer(usernames, new EMValueCallBack<List<HuanXinUser>>() {

                    @Override
                    public void onSuccess(List<HuanXinUser> uList) {
                        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).updateContactList(uList);
                        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager().notifyContactInfosSyncListener(true);
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                HXSDKHelper.getInstance().notifyContactsSyncListener(false);
            }

        });
    }

    static void asyncFetchBlackListFromServer(){
        HXSDKHelper.getInstance().asyncFetchBlackListFromServer(new EMValueCallBack<List<String>>(){

            @Override
            public void onSuccess(List<String> value) {
                EMContactManager.getInstance().saveBlackList(value);
                HXSDKHelper.getInstance().notifyBlackListSyncListener(true);
            }

            @Override
            public void onError(int error, String errorMsg) {
                HXSDKHelper.getInstance().notifyBlackListSyncListener(false);
            }

        });
    }

    /**
     * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
     *
     * @param username
     * @param user
     */
    private static void setUserHearder(String username, HuanXinUser user) {
        String headerName = null;
        if (!TextUtils.isEmpty(user.getNick())) {
            headerName = user.getNick();
        } else {
            headerName = user.getUsername();
        }
        if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
            user.setHeader("");
        } else if (Character.isDigit(headerName.charAt(0))) {
            user.setHeader("#");
        } else {
            user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0, 1)
                    .toUpperCase());
            char header = user.getHeader().toLowerCase().charAt(0);
            if (header < 'a' || header > 'z') {
                user.setHeader("#");
            }
        }
    }

    /**
     * 监听事件
     */
    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage: // 普通消息
            {
                EMMessage message = (EMMessage) event.getData();

                // 提示新消息
                HXSDKHelper.getInstance().getNotifier().onNewMsg(message);

                refreshUI();
                break;
            }

            case EventOfflineMessage: {
                refreshUI();
                break;
            }

            case EventConversationListChanged: {
                refreshUI();
                break;
            }

            default:
                break;
        }
    }

    private void refreshUI() {
        runOnUiThread(new Runnable() {
            public void run() {
                // 刷新bottom bar消息未读数
                updateUnreadLabel();
                if (currentTabIndex ==1) {
                    // 当前页面如果为聊天历史页面，刷新此页面
                    if (messageFragment != null) {
                        messageFragment.refresh();
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (conflictBuilder != null) {
            conflictBuilder.create().dismiss();
            conflictBuilder = null;
        }

        if(connectionListener != null){
            EMChatManager.getInstance().removeConnectionListener(connectionListener);
        }

        if(groupChangeListener != null){
            EMGroupManager.getInstance().removeGroupChangeListener(groupChangeListener);
        }

        try {
            unregisterReceiver(internalDebugReceiver);
        } catch (Exception e) {
        }
    }

    /**
     * 刷新未读消息数
     */
    public void updateUnreadLabel() {
       // int count = getUnreadMsgCountTotal();
        int count = getUnreadMsgCountTotal()+getUnreadAddressCountTotal();
        if (count > 0) {
            //rb_message.setBackgroundResource(R.drawable.selector_unreader_message);

            Drawable drawable=getResources().getDrawable(R.drawable.selector_unreader_message);
            rb_message.setCompoundDrawablesRelativeWithIntrinsicBounds(null,drawable,null,null);
            //更新标签
            //unreadLabel.setText(String.valueOf(count));
           // unreadLabel.setVisibility(View.VISIBLE);
        } else {
            //rb_message.setBackgroundResource(R.drawable.selector_message);

            Drawable drawable=getResources().getDrawable(R.drawable.selector_message);
            rb_message.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawable, null, null);
            //rb_message.setCompoundDrawables( null,drawable, null, null);
           // unreadLabel.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 刷新申请与通知消息数
     */
    public void updateUnreadAddressLable() {
        runOnUiThread(new Runnable() {
            public void run() {
                int count = getUnreadAddressCountTotal();
                if (count > 0) {

//                    unreadAddressLable.setText(String.valueOf(count));
//                    unreadAddressLable.setVisibility(View.VISIBLE);
                } else {
 //                   unreadAddressLable.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    /**
     * 获取未读申请与通知消息
     *
     * @return
     */
    public int getUnreadAddressCountTotal() {
        int unreadAddressCountTotal = 0;
        if (((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList().get(Constant.NEW_FRIENDS_USERNAME) != null)
            unreadAddressCountTotal = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList().get(Constant.NEW_FRIENDS_USERNAME)
                    .getUnreadMsgCount();
        return unreadAddressCountTotal;
    }

    /**
     * 获取未读消息数
     *
     * @return
     */
    public int getUnreadMsgCountTotal() {
        int unreadMsgCountTotal = 0;
        int chatroomUnreadMsgCount = 0;
        unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
        for(EMConversation conversation: EMChatManager.getInstance().getAllConversations().values()){
            if(conversation.getType() == EMConversationType.ChatRoom)
                chatroomUnreadMsgCount=chatroomUnreadMsgCount+conversation.getUnreadMsgCount();
        }
        return unreadMsgCountTotal-chatroomUnreadMsgCount;
    }

    private InviteMessgeDao inviteMessgeDao;
    private UserDao userDao;

    /***
     * 好友变化listener
     *
     */
    public class MyContactListener implements EMContactListener {

        @Override
        public void onContactAdded(List<String> usernameList) {
            // 保存增加的联系人
            Map<String, HuanXinUser> localUsers = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList();
            Map<String, HuanXinUser> toAddUsers = new HashMap<String, HuanXinUser>();
            for (String username : usernameList) {
                HuanXinUser user = setUserHead(username);
                // 添加好友时可能会回调added方法两次
                if (!localUsers.containsKey(username)) {
                    userDao.saveContact(user);
                }
                toAddUsers.put(username, user);
            }
            localUsers.putAll(toAddUsers);
        }

        @Override
        public void onContactDeleted(final List<String> usernameList) {
            // 被删除
            Map<String, HuanXinUser> localUsers = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList();
            for (String username : usernameList) {
                localUsers.remove(username);
                userDao.deleteContact(username);
                inviteMessgeDao.deleteMessage(username);
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    // 如果正在与此用户的聊天页面
                    String st10 = getResources().getString(R.string.have_you_removed);
                    if (ChatActivity.activityInstance != null
                            && usernameList.contains(ChatActivity.activityInstance.getToChatUsername())) {
                        Toast.makeText(MainActivity.this, ChatActivity.activityInstance.getToChatUsername() + st10, Toast.LENGTH_SHORT)
                                .show();
                        ChatActivity.activityInstance.finish();
                    }
                    updateUnreadLabel();
                    // 刷新ui
                    messageFragment.refresh();
                }
            });

        }

        @Override
        public void onContactInvited(String username, String reason) {

            // 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();

            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username)) {
                    inviteMessgeDao.deleteMessage(username);
                }
            }
            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            msg.setReason(reason);
            Log.d(TAG, username + "请求加你为好友,reason: " + reason);
            // 设置相应status
            msg.setStatus(InviteMesageStatus.BEINVITEED);
            notifyNewIviteMessage(msg);

        }

        @Override
        public void onContactAgreed(String username) {
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getFrom().equals(username)) {
                    return;
                }
            }
            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            Log.d(TAG, username + "同意了你的好友请求");
            msg.setStatus(InviteMesageStatus.BEAGREED);
            notifyNewIviteMessage(msg);

        }

        @Override
        public void onContactRefused(String username) {

            // 参考同意，被邀请实现此功能,demo未实现
            Log.d(username, username + "拒绝了你的好友请求");
        }

    }

    /**
     * 连接监听listener
     *
     */
    public class MyConnectionListener implements EMConnectionListener {

        @Override
        public void onConnected() {
            boolean groupSynced = HXSDKHelper.getInstance().isGroupsSyncedWithServer();
            boolean contactSynced = HXSDKHelper.getInstance().isContactsSyncedWithServer();

            // in case group and contact were already synced, we supposed to notify sdk we are ready to receive the events
            if(groupSynced && contactSynced){
                new Thread(){
                    @Override
                    public void run(){
                        HXSDKHelper.getInstance().notifyForRecevingEvents();
                    }
                }.start();
            }else{
                if(!groupSynced){
                    asyncFetchGroupsFromServer();
                }

                if(!contactSynced){
                    asyncFetchContactsFromServer();
                }

                if(!HXSDKHelper.getInstance().isBlackListSyncedWithServer()){
                    asyncFetchBlackListFromServer();
                }
            }

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    messageFragment.errorItem.setVisibility(View.GONE);
                }

            });
        }

        @Override
        public void onDisconnected(final int error) {
            final String st1 = getResources().getString(R.string.can_not_connect_chat_server_connection);
            final String st2 = getResources().getString(R.string.the_current_network);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        // 显示帐号已经被移除
                        showAccountRemovedDialog();
                    } else if (error == EMError.CONNECTION_CONFLICT) {
                        // 显示帐号在其他设备登陆dialog
                        showConflictDialog();
                    } else {
                        messageFragment.errorItem.setVisibility(View.VISIBLE);
                        if (NetUtils.hasNetwork(MainActivity.this))
                            messageFragment.errorText.setText(st1);
                        else
                            messageFragment.errorText.setText(st2);

                    }
                }

            });
        }
    }

    /**
     * MyGroupChangeListener
     */
    public class MyGroupChangeListener implements EMGroupChangeListener {

        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {

            boolean hasGroup = false;
            for (EMGroup group : EMGroupManager.getInstance().getAllGroups()) {
                if (group.getGroupId().equals(groupId)) {
                    hasGroup = true;
                    break;
                }
            }
            if (!hasGroup)
                return;

            // 被邀请
            String st3 = getResources().getString(R.string.Invite_you_to_join_a_group_chat);
            EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
            msg.setChatType(ChatType.GroupChat);
            msg.setFrom(inviter);
            msg.setTo(groupId);
            msg.setMsgId(UUID.randomUUID().toString());
            msg.addBody(new TextMessageBody(inviter + " " +st3));
            // 保存邀请消息
            EMChatManager.getInstance().saveMessage(msg);
            // 提醒新消息
            HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(msg);

            runOnUiThread(new Runnable() {
                public void run() {
                    updateUnreadLabel();
                    // 刷新ui
                    if (currentTabIndex == 1)
                        messageFragment.refresh();
                    if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
                        GroupsActivity.instance.onResume();
                    }
                }
            });

        }

        @Override
        public void onInvitationAccpted(String groupId, String inviter, String reason) {

        }

        @Override
        public void onInvitationDeclined(String groupId, String invitee, String reason) {

        }

        @Override
        public void onUserRemoved(String groupId, String groupName) {

            // 提示用户被T了，demo省略此步骤
            // 刷新ui
            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        updateUnreadLabel();
                        if (currentTabIndex == 1)
                            messageFragment.refresh();
                        if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
                            GroupsActivity.instance.onResume();
                        }
                    } catch (Exception e) {
                        EMLog.e(TAG, "refresh exception " + e.getMessage());
                    }
                }
            });
        }

        @Override
        public void onGroupDestroy(String groupId, String groupName) {

            // 群被解散
            // 提示用户群被解散,demo省略
            // 刷新ui
            runOnUiThread(new Runnable() {
                public void run() {
                    updateUnreadLabel();
                    if (currentTabIndex == 1)
                        messageFragment.refresh();
                    if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
                        GroupsActivity.instance.onResume();
                    }
                }
            });

        }

        @Override
        public void onApplicationReceived(String groupId, String groupName, String applyer, String reason) {

            // 用户申请加入群聊
            InviteMessage msg = new InviteMessage();
            msg.setFrom(applyer);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setGroupName(groupName);
            msg.setReason(reason);
            Log.d(TAG, applyer + " 申请加入群聊：" + groupName);
            msg.setStatus(InviteMesageStatus.BEAPPLYED);
            notifyNewIviteMessage(msg);
        }

        @Override
        public void onApplicationAccept(String groupId, String groupName, String accepter) {

            String st4 = getResources().getString(R.string.Agreed_to_your_group_chat_application);
            // 加群申请被同意
            EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
            msg.setChatType(ChatType.GroupChat);
            msg.setFrom(accepter);
            msg.setTo(groupId);
            msg.setMsgId(UUID.randomUUID().toString());
            msg.addBody(new TextMessageBody(accepter + " " +st4));
            // 保存同意消息
            EMChatManager.getInstance().saveMessage(msg);
            // 提醒新消息
            HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(msg);

            runOnUiThread(new Runnable() {
                public void run() {
                    updateUnreadLabel();
                    // 刷新ui
                    if (currentTabIndex ==1)
                        messageFragment.refresh();
                    if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
                        GroupsActivity.instance.onResume();
                    }
                }
            });
        }

        @Override
        public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {
            // 加群申请被拒绝，demo未实现
        }
    }

    /**
     * 保存提示新消息
     *
     * @param msg
     */
    private void notifyNewIviteMessage(InviteMessage msg) {
        saveInviteMsg(msg);
        // 提示有新消息
        HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(null);

        // 刷新bottom bar消息未读数
        updateUnreadAddressLable();
    }

    /**
     * 保存邀请等msg
     *
     * @param msg
     */
    private void saveInviteMsg(InviteMessage msg) {
        // 保存msg
        inviteMessgeDao.saveMessage(msg);
        // 未读数加1
        HuanXinUser user = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList().get(Constant.NEW_FRIENDS_USERNAME);
        if (user.getUnreadMsgCount() == 0)
            user.setUnreadMsgCount(user.getUnreadMsgCount() + 1);
    }

    /**
     * set head
     *
     * @param username
     * @return
     */
    HuanXinUser setUserHead(String username) {
        HuanXinUser user = new HuanXinUser();
        user.setUsername(username);
        String headerName = null;
        if (!TextUtils.isEmpty(user.getNick())) {
            headerName = user.getNick();
        } else {
            headerName = user.getUsername();
        }
        if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
            user.setHeader("");
        } else if (Character.isDigit(headerName.charAt(0))) {
            user.setHeader("#");
        } else {
            user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0, 1)
                    .toUpperCase());
            char header = user.getHeader().toLowerCase().charAt(0);
            if (header < 'a' || header > 'z') {
                user.setHeader("#");
            }
        }
        return user;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isConflict && !isCurrentAccountRemoved) {
            updateUnreadLabel();
            updateUnreadAddressLable();
            EMChatManager.getInstance().activityResumed();
        }

        // unregister this event listener when this activity enters the
        // background
        DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();
        sdkHelper.pushActivity(this);

        // register the event listener when enter the foreground
        EMChatManager.getInstance().registerEventListener(this,
                new EMNotifierEvent.Event[] { EMNotifierEvent.Event.EventNewMessage , EMNotifierEvent.Event.EventOfflineMessage, EMNotifierEvent.Event.EventConversationListChanged});
    }

    @Override
    protected void onStop() {
        EMChatManager.getInstance().unregisterEventListener(this);
        DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();
        sdkHelper.popActivity(this);

        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isConflict", isConflict);
        outState.putBoolean(Constant.ACCOUNT_REMOVED, isCurrentAccountRemoved);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private android.app.AlertDialog.Builder conflictBuilder;
    private android.app.AlertDialog.Builder accountRemovedBuilder;
    private boolean isConflictDialogShow;
    private boolean isAccountRemovedDialogShow;
    private BroadcastReceiver internalDebugReceiver;

    /**
     * 显示帐号在别处登录dialog
     */
    private void showConflictDialog() {
        isConflictDialogShow = true;
        DemoHXSDKHelper.getInstance().logout(true,null);
        BmobUser.logOut(mContext);
        SharedPreferences setting = getSharedPreferences("setting", 0);
        setting.edit().putBoolean("FIRST", true).apply();
        String st = getResources().getString(R.string.Logoff_notification);
        if (!MainActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (conflictBuilder == null)
                    conflictBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
                conflictBuilder.setTitle(st);
                conflictBuilder.setMessage(R.string.connect_conflict);
                conflictBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        conflictBuilder = null;
                        finish();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                });
                conflictBuilder.setCancelable(false);
                conflictBuilder.create().show();
                isConflict = true;
            } catch (Exception e) {
                EMLog.e(TAG, "---------color conflictBuilder error" + e.getMessage());
            }

        }

    }

    /**
     * 帐号被移除的dialog
     */
    private void showAccountRemovedDialog() {
        isAccountRemovedDialogShow = true;
        DemoHXSDKHelper.getInstance().logout(true,null);
        BmobUser.logOut(mContext);
        SharedPreferences setting = getSharedPreferences("setting", 0);
        setting.edit().putBoolean("FIRST", true).apply();
        String st5 = getResources().getString(R.string.Remove_the_notification);
        if (!MainActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (accountRemovedBuilder == null)
                    accountRemovedBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
                accountRemovedBuilder.setTitle(st5);
                accountRemovedBuilder.setMessage(R.string.em_user_remove);
                accountRemovedBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        accountRemovedBuilder = null;
                        finish();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                });
                accountRemovedBuilder.setCancelable(false);
                accountRemovedBuilder.create().show();
                isCurrentAccountRemoved = true;
            } catch (Exception e) {
                EMLog.e(TAG, "---------color userRemovedBuilder error" + e.getMessage());
            }

        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (getIntent().getBooleanExtra("conflict", false) && !isConflictDialogShow) {
            showConflictDialog();
        } else if (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
            showAccountRemovedDialog();
        }
    }

    /**
     * 内部测试代码，开发者请忽略
     */
    private void registerInternalDebugReceiver() {
        internalDebugReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                DemoHXSDKHelper.getInstance().logout(true,new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                // 重新显示登陆页面
                                finish();
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));

                            }
                        });
                    }

                    @Override
                    public void onProgress(int progress, String status) {}

                    @Override
                    public void onError(int code, String message) {}
                });
            }
        };
        IntentFilter filter = new IntentFilter(getPackageName() + ".em_internal_debug");
        registerReceiver(internalDebugReceiver, filter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //getMenuInflater().inflate(R.menu.context_tab_contact, menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isNetConnected())
        {
            showToast("当前网络不可用，请检查网络");
            return;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case 12://创建帮派
                //gangsFragment.updatePage();
                getSupportFragmentManager().beginTransaction().hide(noGangsFragment).show(gangsFragment).commitAllowingStateLoss();
                fragments=fragments0;
                break;
//            case 13:
//                CurrentUser=BmobUser.getCurrentUser(mContext,User.class);
//                //meFragment.updateInfo();
//                break;
            case 14:
                getSupportFragmentManager().beginTransaction().hide(gangsFragment).show(noGangsFragment).commitAllowingStateLoss();
                fragments = fragments1;
//                BmobQuery<User> query=new BmobQuery<>();
//                query.addWhereEqualTo("objectId",CurrentUser.getObjectId());
//                query.findObjects(mContext, new FindListener<User>() {
//                    @Override
//                    public void onSuccess(List<User> list) {
//                        String name = list.get(0).getGangsName();
//                        if (name.equals("")) {
//                            getSupportFragmentManager().beginTransaction().hide(gangsFragment).show(noGangsFragment).commitAllowingStateLoss();
//                            fragments = fragments1;
//                        }
//                    }
//
//                    @Override
//                    public void onError(int i, String s) {
//
//                    }
//                });
                break;
            case 15://退出登录
                finish();
                break;
//            case 16://添加帮派
//                BmobQuery<User> query1=new BmobQuery<>();
//                query1.addWhereEqualTo("objectId",CurrentUser.getObjectId());
//                query1.findObjects(mContext, new FindListener<User>() {
//                    @Override
//                    public void onSuccess(List<User> list) {
//                        String name = list.get(0).getGangsName();
//                        if (name.equals("")) {
//                            getSupportFragmentManager().beginTransaction().hide(gangsFragment).show(noGangsFragment).commitAllowingStateLoss();
//                            fragments = fragments1;
//                        }
//                    }
//
//                    @Override
//                    public void onError(int i, String s) {
//
//                    }
//                });
//                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void  undateGangs(){
        BmobQuery<User> query=new BmobQuery<>();
        query.addWhereEqualTo("objectId",CurrentUser.getObjectId());
        query.findObjects(mContext, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                String name = list.get(0).getGangsName();
                if (name==null||name.equals("")) {
                    if(fragments==fragments0){
                        getSupportFragmentManager().beginTransaction().hide(gangsFragment).show(noGangsFragment).commitAllowingStateLoss();
                        fragments = fragments1;
                    }
                }else{
                    if(fragments==fragments1){
                        getSupportFragmentManager().beginTransaction().hide(noGangsFragment).show(gangsFragment).commitAllowingStateLoss();
                        fragments = fragments0;
                        gangsFragment.updatePage();
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                showToast("请检查网络设置");
            }
        });
    }
}
