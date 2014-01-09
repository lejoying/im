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
import com.lejoying.mc.utils.AjaxAdapter;
import com.lejoying.mc.utils.MCNetTools;
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

		MCNetTools.ajaxAPI(new AjaxAdapter() {

			@Override
			public void setParams(Settings settings) {
				settings.url = API.RELATION_GETASKFRIENDS;
				settings.params = params;
			}

			@Override
			public void onSuccess(final JSONObject jData) {
				this.jData = jData;
				app.dataHandler1.modifyData(modification, mUIModification);
			}

			Modification modification = new Modification() {
				public void modify(StaticData data) {
					try {
						JSONArray jFriends = jData.getJSONArray("accounts");
						List<Friend> newFriends = app.mJSONHandler.generateFriendsFromJSON(jFriends);
						for (Friend friend : newFriends) {
							if (!app.data.newFriends.contains(friend)) {
								app.data.newFriends.add(0, friend);
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			};
			
			UIModification mUIModification = new UIModification() {
				public void modifyUI() {
					if (FriendsFragment.instance != null) {
						FriendsFragment.instance.initData(true);
						FriendsFragment.instance.mFriendsAdapter.notifyDataSetChanged();
					} else if (NewFriendsFragment.instance != null) {
						NewFriendsFragment.instance.newFriendsAdapter.notifyDataSetChanged();
					}
				}
			};
		});

	}
}
