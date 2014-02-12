package com.lejoying.mc.data.handler;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import com.lejoying.mc.data.App;
import com.lejoying.mc.data.Data;
import com.lejoying.mc.data.Friend;
import com.lejoying.mc.data.handler.DataHandler.Modification;
import com.lejoying.mc.data.handler.DataHandler.UIModification;
import com.lejoying.mc.fragment.ChatFragment;
import com.lejoying.mc.fragment.FriendsFragment;
import com.lejoying.mc.fragment.NewFriendsFragment;
import com.lejoying.mc.network.API;
import com.lejoying.mc.utils.AjaxAdapter;
import com.lejoying.mc.utils.MCNetUtils;
import com.lejoying.mc.utils.MCNetUtils.Settings;

public class ServerHandler {
	App app;

	public void initialize(App app) {
		this.app = app;
	}

	public void getUser(final Modification modification,
			final UIModification uiModification) {
		final Bundle params = new Bundle();
		params.putString("phone", app.data.user.phone);
		params.putString("accessKey", app.data.user.accessKey);
		params.putString("target", app.data.user.phone);

		MCNetUtils.ajax(new AjaxAdapter() {

			@Override
			public void setParams(Settings settings) {
				settings.url = API.ACCOUNT_GET;
				settings.params = params;
			}

			@Override
			public void onSuccess(JSONObject jData) {
				try {
					final JSONObject jUser = jData.getJSONObject("account");
					app.dataHandler.modifyData(new Modification() {
						public void modify(Data data) {
							app.mJSONHandler.updateUser(jUser, data);
							if (modification != null) {
								modification.modify(data);
							}
						}
					}, new UIModification() {

						@Override
						public void modifyUI() {
							// TODO Auto-generated method stub

							if (uiModification != null) {
								uiModification.modifyUI();
							}
						}
					});
				} catch (JSONException e) {
				}
			}
		});
	}

	public void getAskFriends(final Modification modification,
			final UIModification uiModification) {
		final Bundle params = new Bundle();
		params.putString("phone", app.data.user.phone);
		params.putString("accessKey", app.data.user.accessKey);

		MCNetUtils.ajax(new AjaxAdapter() {

			@Override
			public void setParams(Settings settings) {
				settings.url = API.RELATION_GETASKFRIENDS;
				settings.params = params;
			}

			@Override
			public void onSuccess(final JSONObject jData) {
				this.jData = jData;
				app.dataHandler.modifyData(new Modification() {
					public void modify(Data data) {
						try {
							JSONArray jFriends = jData.getJSONArray("accounts");
							List<Friend> newFriends = app.mJSONHandler
									.generateFriendsFromJSON(jFriends);
							for (Friend friend : newFriends) {
								if (!app.data.newFriends.contains(friend)) {
									app.data.newFriends.add(0, friend);
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						if (modification != null) {
							modification.modify(data);
						}
					}
				}, new UIModification() {
					public void modifyUI() {
						if (app.mark.equals(app.friendsFragment)) {
							if (FriendsFragment.instance != null) {
								FriendsFragment.instance.mAdapter
										.notifyDataSetChanged();
							}
						} else if (app.mark.equals(app.newFriendsFragment)) {
							if (NewFriendsFragment.instance != null) {
								NewFriendsFragment.instance.mAdapter
										.notifyDataSetChanged();
							}
						}
						if (uiModification != null) {
							uiModification.modifyUI();
						}
					}
				});
			}
		});

	}

	public void getCirclesAndFriends(final Modification modification,
			final UIModification uiModification) {
		final Bundle params = new Bundle();
		params.putString("phone", app.data.user.phone);
		params.putString("accessKey", app.data.user.accessKey);

		MCNetUtils.ajax(new AjaxAdapter() {

			@Override
			public void setParams(Settings settings) {
				settings.url = API.RELATION_GETCIRCLESANDFRIENDS;
				settings.params = params;
			}

			@Override
			public void onSuccess(final JSONObject jData) {
				app.dataHandler.modifyData(new Modification() {
					@Override
					public void modify(Data data) {
						try {
							app.mJSONHandler.saveCircles(
									jData.getJSONArray("circles"), data);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (modification != null) {
							modification.modify(data);
						}
					}
				}, new UIModification() {

					@Override
					public void modifyUI() {
						// TODO Auto-generated method stub
						if (app.mark.equals(app.friendsFragment)) {
							if (FriendsFragment.instance != null) {
								FriendsFragment.instance.mAdapter
										.notifyDataSetChanged();
							}
						}
						if (uiModification != null) {
							uiModification.modifyUI();
						}
					}
				});
			}
		});
	}

	public void getMessages(final Modification modification,
			final UIModification uiModification) {
		final Bundle params = new Bundle();
		params.putString("phone", app.data.user.phone);
		params.putString("accessKey", app.data.user.accessKey);
		String flag = app.data.user.flag;
		params.putString("flag", flag);
		MCNetUtils.ajax(new AjaxAdapter() {

			@Override
			public void setParams(Settings settings) {
				settings.url = API.MESSAGE_GET;
				settings.params = params;
			}

			@Override
			public void onSuccess(final JSONObject jData) {
				System.out.println(jData);
				app.dataHandler.modifyData(new Modification() {
					@Override
					public void modify(Data data) {
						try {
							app.mJSONHandler.saveMessages(
									jData.getJSONArray("messages"), data);
							data.user.flag = jData.getString("flag");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (modification != null) {
							modification.modify(data);
						}
					}
				}, new UIModification() {
					@Override
					public void modifyUI() {
						if (app.mark.equals(app.friendsFragment)) {
							if (FriendsFragment.instance != null) {
								FriendsFragment.instance.mAdapter
										.notifyDataSetChanged();
							}
						} else if (app.mark.equals(app.chatFragment)) {
							if (ChatFragment.instance != null) {
								ChatFragment.instance.mAdapter
										.notifyDataSetChanged();
							}
						}
						if (uiModification != null) {
							uiModification.modifyUI();
						}
					}
				});
			}
		});
	}

	public void getAllData() {
		getUser(new Modification() {
			@Override
			public void modify(Data data) {
				getCirclesAndFriends(new Modification() {
					@Override
					public void modify(Data data) {
						getMessages(new Modification() {
							@Override
							public void modify(Data data) {
								getAskFriends(null, null);
							}
						}, null);
					}
				}, null);
			}
		}, null);
	}

}
