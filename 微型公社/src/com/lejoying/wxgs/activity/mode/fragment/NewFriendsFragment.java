package com.lejoying.wxgs.activity.mode.fragment;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.utils.DataUtil;
import com.lejoying.wxgs.activity.utils.DataUtil.GetDataListener;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.handler.FileHandler.FileResult;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;

public class NewFriendsFragment extends BaseFragment {

	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;

	ListView mContent;

	LayoutInflater mInflater;

	public NewFriendsAdapter mAdapter;

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContent = (ListView) inflater.inflate(R.layout.f_newfriends, null);
		mInflater = inflater;
		return mContent;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mAdapter = new NewFriendsAdapter();
		mContent.setAdapter(mAdapter);
	}

	public class NewFriendsAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return app.data.newFriends.size();
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
			NewFriendsHolder newFriendsHolder = null;
			if (arg1 == null || position == 7 || position == getCount() - 8) {
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
				newFriendsHolder.position = position;
				arg1.setTag(newFriendsHolder);
			} else {
				newFriendsHolder = (NewFriendsHolder) arg1.getTag();
			}
			newFriendsHolder.tv_nickname.setText(app.data.newFriends
					.get(position).nickName);
			newFriendsHolder.tv_message.setText(app.data.newFriends
					.get(position).addMessage);
			final String headFileName = app.data.newFriends.get(position).head;
			final ImageView iv_head = newFriendsHolder.iv_head;
			app.fileHandler.getHeadImage(headFileName, new FileResult() {
				@Override
				public void onResult(String where) {
					iv_head.setImageBitmap(app.fileHandler.bitmaps
							.get(headFileName));
				}
			});

			if (app.data.friends.get(app.data.newFriends.get(position).phone) != null) {
				newFriendsHolder.btn_agree.setVisibility(View.GONE);
				newFriendsHolder.tv_waitagree.setVisibility(View.GONE);
				newFriendsHolder.tv_added.setVisibility(View.VISIBLE);
			} else {
				if (app.data.newFriends.get(position).temp) {
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
									addFriend(app.data.newFriends.get(position));
								}
							});
				}
			}
			return arg1;
		}
	}

	private void addFriend(final Friend friend) {
		app.networkHandler.connection(new CommonNetConnection() {

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.RELATION_ADDFRIENDAGREE;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("phoneask", friend.phone);
				params.put("status", "true");
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
				DataUtil.getCircles(new GetDataListener() {

					@Override
					public void getSuccess() {
						mAdapter.notifyDataSetChanged();
						// TODO refresh
						mMainModeManager.mCirclesFragment.notifyViews();
					}

					@Override
					public void getFailed() {
						// TODO Auto-generated method stub

					}
				});
			}
		});
		app.dataHandler.exclude(new Modification() {

			@Override
			public void modifyData(Data data) {
				if (data.friends.get(friend.phone) == null) {
					data.friends.put(friend.phone, friend);
				}
				if (!data.circlesMap.get("-1").phones.contains(friend.phone)) {
					data.circlesMap.get("-1").phones.add(friend.phone);
				}
			}

			@Override
			public void modifyUI() {
				mAdapter.notifyDataSetChanged();
				// TODO refresh
				mMainModeManager.mCirclesFragment.notifyViews();
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
		Integer position;
	}

	@Override
	public void onResume() {
		CircleMenu.showBack();
		super.onResume();
	}

}
