package com.xpple.jahoqy.BaseClass;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.easemob.EMValueCallBack;
import com.xpple.jahoqy.BaseClass.HXSDKHelper.HXSyncListener;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.xpple.jahoqy.bean.HuanXinUser;
import com.xpple.jahoqy.parse.ParseManager;

public class UserProfileManager {

	/**
	 * application context
	 */
	protected Context appContext = null;

	/**
	 * init flag: test if the sdk has been inited before, we don't need to init
	 * again
	 */
	private boolean sdkInited = false;

	/**
	 * HuanXin sync contact nick and avatar listener
	 */
	private List<HXSyncListener> syncContactInfosListeners;

	private boolean isSyncingContactInfosWithServer = false;

	private HuanXinUser currentUser;

	public UserProfileManager() {
	}

	public synchronized boolean onInit(Context context) {
		if (sdkInited) {
			return true;
		}
		ParseManager.getInstance().onInit(context);
		syncContactInfosListeners = new ArrayList<HXSyncListener>();
		sdkInited = true;
		return true;
	}

	public void addSyncContactInfoListener(HXSyncListener listener) {
		if (listener == null) {
			return;
		}
		if (!syncContactInfosListeners.contains(listener)) {
			syncContactInfosListeners.add(listener);
		}
	}

	public void removeSyncContactInfoListener(HXSyncListener listener) {
		if (listener == null) {
			return;
		}
		if (syncContactInfosListeners.contains(listener)) {
			syncContactInfosListeners.remove(listener);
		}
	}

	public void asyncFetchContactInfosFromServer(List<String> usernames, final EMValueCallBack<List<HuanXinUser>> callback) {
		if (isSyncingContactInfosWithServer) {
			return;
		}
		isSyncingContactInfosWithServer = true;
		ParseManager.getInstance().getContactInfos(usernames, new EMValueCallBack<List<HuanXinUser>>() {

			@Override
			public void onSuccess(List<HuanXinUser> value) {
				isSyncingContactInfosWithServer = false;
				// in case that logout already before server returns,we should
				// return immediately
				if (!EMChat.getInstance().isLoggedIn()) {
					return;
				}
				if (callback != null) {
					callback.onSuccess(value);
				}
			}

			@Override
			public void onError(int error, String errorMsg) {
				isSyncingContactInfosWithServer = false;
				if (callback != null) {
					callback.onError(error, errorMsg);
				}
			}

		});

	}

	public void notifyContactInfosSyncListener(boolean success) {
		for (HXSyncListener listener : syncContactInfosListeners) {
			listener.onSyncSucess(success);
		}
	}

	public boolean isSyncingContactInfoWithServer() {
		return isSyncingContactInfosWithServer;
	}

	synchronized void reset() {
		isSyncingContactInfosWithServer = false;
		currentUser = null;
		HXPreferenceUtils.getInstance().removeCurrentUserInfo();
	}

	public synchronized HuanXinUser getCurrentUserInfo() {
		if (currentUser == null) {
			String username = EMChatManager.getInstance().getCurrentUser();
			currentUser = new HuanXinUser(username);
			String nick = getCurrentUserNick();
			currentUser.setNick((nick != null) ? nick : username);
			currentUser.setAvatar(getCurrentUserAvatar());
		}
		return currentUser;
	}

	public boolean updateParseNickName(final String nickname) {
		boolean isSuccess = ParseManager.getInstance().updateParseNickName(nickname);
		if (isSuccess) {
			setCurrentUserNick(nickname);
		}
		return isSuccess;
	}

	public String uploadUserAvatar(byte[] data) {
		String avatarUrl = ParseManager.getInstance().uploadParseAvatar(data);
		if (avatarUrl != null) {
			setCurrentUserAvatar(avatarUrl);
		}
		return avatarUrl;
	}

	public void asyncGetCurrentUserInfo() {
		ParseManager.getInstance().asyncGetCurrentUserInfo(new EMValueCallBack<HuanXinUser>() {

			@Override
			public void onSuccess(HuanXinUser value) {
				setCurrentUserNick(value.getNick());
				setCurrentUserAvatar(value.getAvatar());
			}

			@Override
			public void onError(int error, String errorMsg) {

			}
		});

	}
	public void asyncGetUserInfo(final String username,final EMValueCallBack<HuanXinUser> callback){
		ParseManager.getInstance().asyncGetUserInfo(username, callback);
	}
	private void setCurrentUserNick(String nickname) {
		getCurrentUserInfo().setNick(nickname);
		HXPreferenceUtils.getInstance().setCurrentUserNick(nickname);
	}

	private void setCurrentUserAvatar(String avatar) {
		getCurrentUserInfo().setAvatar(avatar);
		HXPreferenceUtils.getInstance().setCurrentUserAvatar(avatar);
	}

	private String getCurrentUserNick() {
		return HXPreferenceUtils.getInstance().getCurrentUserNick();
	}

	private String getCurrentUserAvatar() {
		return HXPreferenceUtils.getInstance().getCurrentUserAvatar();
	}

}
