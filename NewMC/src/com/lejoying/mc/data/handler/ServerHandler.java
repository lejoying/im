package com.lejoying.mc.data.handler;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import com.lejoying.mc.data.App;
import com.lejoying.mc.data.Friend;
import com.lejoying.mc.data.StaticData;
import com.lejoying.mc.data.handler.DataHandler1.Modification;
import com.lejoying.mc.data.handler.DataHandler1.UIModification;
import com.lejoying.mc.fragment.FriendsFragment;
import com.lejoying.mc.fragment.NewFriendsFragment;
import com.lejoying.mc.network.API;
import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.mc.utils.MCNetTools.AjaxInterface;
import com.lejoying.mc.utils.MCNetTools.Settings;

public class ServerHandler {
	App app;

	public void initailize(App app) {
		this.app = app;
	}

	public void getAskFriends() {
		final Bundle params = new Bundle();
		params.putString("phone", app.data.user.phone);
		params.putString("accessKey", app.data.user.accessKey);

		MCNetTools.ajaxAPI(new AjaxInterface() {

			@Override
			public void setParams(Settings settings) {
				settings.url = API.RELATION_GETASKFRIENDS;
				settings.params = params;
			}

			@Override
			public void onSuccess(JSONObject data) {
				try {
					final JSONArray jFriends = data.getJSONArray("accounts");
					app.dataHandler1.modifyData(new Modification() {
						public void modify(StaticData data) {
							List<Friend> newFriends = app.mJSONHandler.generateFriendsFromJSON(jFriends);
							for (Friend friend : newFriends) {
								if (!app.data.newFriends.contains(friend)) {
									app.data.newFriends.add(0, friend);
								}
							}
						}
					}, new UIModification() {
						public void modifyUI() {
							if (FriendsFragment.instance != null) {
								FriendsFragment.instance.initData(true);
								FriendsFragment.instance.mFriendsAdapter.notifyDataSetChanged();
							}else if(NewFriendsFragment.instance!=null){
								NewFriendsFragment.instance.newFriendsAdapter.notifyDataSetChanged();
							}
						}
					});
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void failed() {
				// TODO Auto-generated method stub

			}
		});

	}
}
