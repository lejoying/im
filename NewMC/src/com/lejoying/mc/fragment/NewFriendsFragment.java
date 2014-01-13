package com.lejoying.mc.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lejoying.mc.R;
import com.lejoying.mc.data.App;
import com.lejoying.mc.data.Data;
import com.lejoying.mc.data.handler.DataHandler.Modification;
import com.lejoying.mc.data.handler.FileHandler.FileResult;
import com.lejoying.mc.network.API;
import com.lejoying.mc.utils.AjaxAdapter;
import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.mc.utils.MCNetTools.Settings;

public class NewFriendsFragment extends BaseListFragment {

	App app = App.getInstance();
	View mContent;

	LayoutInflater mInflater;

	public static NewFriendsFragment instance;

	public NewFriendsAdapter mAdapter;

	@Override
	protected EditText showSoftInputOnShow() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		instance = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMCFragmentManager.showCircleMenuToTop(true, true);
		mContent = inflater.inflate(R.layout.android_list, null);
		mInflater = inflater;
		return mContent;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mAdapter = new NewFriendsAdapter();
		setListAdapter(mAdapter);
	}

	public class NewFriendsAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return app.data.newFriends.size() + 2;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View arg1, ViewGroup arg2) {
			if (position == 0 || position == getCount() - 1) {
				return mInflater.inflate(R.layout.f_margin, null);
			}
			NewFriendsHolder newFriendsHolder = null;
			if (arg1 == null) {
				newFriendsHolder = new NewFriendsHolder();
				arg1 = mInflater.inflate(R.layout.f_newfriends_item, null);
				newFriendsHolder.btn_agree = (Button) arg1
						.findViewById(R.id.btn_agreeadd);
				newFriendsHolder.tv_added = (TextView) arg1
						.findViewById(R.id.tv_added);
				newFriendsHolder.iv_head = (ImageView) arg1
						.findViewById(R.id.iv_head);
				newFriendsHolder.tv_nickname = (TextView) arg1
						.findViewById(R.id.tv_nickname);
				newFriendsHolder.tv_message = (TextView) arg1
						.findViewById(R.id.tv_message);
				newFriendsHolder.tv_waitagree = (TextView) arg1
						.findViewById(R.id.tv_waitagree);
				arg1.setTag(newFriendsHolder);
			} else {
				newFriendsHolder = (NewFriendsHolder) arg1.getTag();
			}
			newFriendsHolder.tv_nickname.setText(app.data.newFriends
					.get(position - 1).nickName);
			newFriendsHolder.tv_message.setText(app.data.newFriends
					.get(position - 1).addMessage);
			final String headFileName = app.data.newFriends.get(position - 1).head;
			final ImageView iv_head = newFriendsHolder.iv_head;
			app.fileHandler.getHeadImage(headFileName, new FileResult() {
				@Override
				public void onResult(String where) {
					iv_head.setImageBitmap(app.fileHandler.bitmaps
							.get(headFileName));
				}
			});

			if (app.data.friends
					.get(app.data.newFriends.get(position - 1).phone) != null) {
				newFriendsHolder.btn_agree.setVisibility(View.GONE);
				newFriendsHolder.tv_waitagree.setVisibility(View.GONE);
				newFriendsHolder.tv_added.setVisibility(View.VISIBLE);
			} else {
				if (app.data.newFriends.get(position - 1).temp) {
					newFriendsHolder.btn_agree.setVisibility(View.GONE);
					newFriendsHolder.tv_waitagree.setVisibility(View.VISIBLE);
					newFriendsHolder.tv_added.setVisibility(View.GONE);

				} else {
					newFriendsHolder.btn_agree.setVisibility(View.VISIBLE);
					newFriendsHolder.tv_added.setVisibility(View.GONE);
					newFriendsHolder.btn_agree
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									System.out.println("加好友");
									addFriend(app.data.newFriends
											.get(position - 1).phone);
								}
							});
				}
			}
			return arg1;
		}
	}

	private void addFriend(String phoneask) {
		final Bundle params = new Bundle();
		params.putString("phone", app.data.user.phone);
		params.putString("accessKey", app.data.user.accessKey);
		params.putString("phoneask", phoneask);
		params.putString("status", "true");
		MCNetTools.ajax(new AjaxAdapter() {
			@Override
			public void setParams(Settings settings) {
				settings.url = API.RELATION_ADDFRIENDAGREE;
				settings.params = params;
			}

			@Override
			public void onSuccess(JSONObject jData) {
				try {
					jData.getString("失败原因");
					return;
				} catch (JSONException e) {
				}
				getCirclesAndFriends();
			}
		});
	}

	private void getCirclesAndFriends() {
		final Bundle params = new Bundle();
		params.putString("phone", app.data.user.phone);
		params.putString("accessKey", app.data.user.accessKey);
		MCNetTools.ajax(new AjaxAdapter() {

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
					}
				});
			}
		});
	}

	class NewFriendsHolder {
		ImageView iv_head;
		TextView tv_nickname;
		TextView tv_message;
		TextView tv_waitagree;
		Button btn_agree;
		TextView tv_added;
	}

	@Override
	public void onResume() {
		app.mark = app.newFriendsFragment;
		super.onResume();
	}

}
