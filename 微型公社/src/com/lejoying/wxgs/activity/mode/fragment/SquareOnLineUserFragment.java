package com.lejoying.wxgs.activity.mode.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.BusinessCardActivity;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileResult;
import com.lejoying.wxgs.app.parser.JSONParser;

public class SquareOnLineUserFragment extends BaseFragment {

	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;
	LayoutInflater mInflater;
	RelativeLayout mBackView;
	ListView mSquareOnLineUserView;

	SquareOnLineAdapter onLineAdapter;
	public List<Friend> users = new ArrayList<Friend>();

	View mContent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		mContent = mInflater.inflate(R.layout.f_square_online_user, null);
		mBackView = (RelativeLayout) mContent.findViewById(R.id.backview);
		mSquareOnLineUserView = (ListView) mContent
				.findViewById(R.id.lv_square_online_user);
		// if (!"".equals(SquareInfomationFragment.mSquareID)) {
		// getSquareOnLineUsers();
		// }
		initEvent();
		initData();
		return mContent;
	}

	private void initData() {
		onLineAdapter = new SquareOnLineAdapter(users);
		mSquareOnLineUserView.setAdapter(onLineAdapter);
	}

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	private void initEvent() {
		mBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mMainModeManager.back();
			}
		});
	}

	@Override
	public void onResume() {
		mMainModeManager.handleMenu(false);
		super.onResume();
	}

	class SquareOnLineAdapter extends BaseAdapter {

		List<Friend> users;

		public SquareOnLineAdapter(List<Friend> users) {
			this.users = users;
		}

		@Override
		public int getCount() {
			return users.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup viewGroup) {
			SquareOnLineUserHolder userHolder;
			if (convertView == null) {
				userHolder = new SquareOnLineUserHolder();
				convertView = mInflater.inflate(
						R.layout.f_square_online_user_item, null);
				userHolder.uHead = (ImageView) convertView
						.findViewById(R.id.iv_user_head);
				userHolder.uNickName = (TextView) convertView
						.findViewById(R.id.tv_nickName);
				userHolder.uSex = (ImageView) convertView
						.findViewById(R.id.iv_sex);
				userHolder.uAge = (TextView) convertView
						.findViewById(R.id.tv_age);
				userHolder.uDistance = (TextView) convertView
						.findViewById(R.id.tv_distance);
				userHolder.uDiscription = (TextView) convertView
						.findViewById(R.id.tv_desceiption);
				convertView.setTag(userHolder);
			} else {
				userHolder = (SquareOnLineUserHolder) convertView.getTag();
			}
			final SquareOnLineUserHolder userHolder0 = userHolder;
			final Friend friend = users.get(position);
			app.fileHandler.getHeadImage(friend.head, "男", new FileResult() {

				@Override
				public void onResult(String where, Bitmap bitmap) {
					userHolder0.uHead.setImageBitmap(app.fileHandler.bitmaps
							.get(friend.head));
				}
			});
			userHolder0.uNickName.setText(friend.nickName);
			if ("男".equals(friend.sex)) {
				userHolder0.uSex.setImageResource(R.drawable.sex_box);
			} else {
				userHolder0.uSex.setImageResource(R.drawable.sex_girl);
			}
			userHolder0.uDiscription.setText(friend.mainBusiness);
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(getActivity(),
							BusinessCardActivity.class);
					if (friend.phone.equals(app.data.user.phone)) {
						intent.putExtra("type", BusinessCardActivity.TYPE_SELF);
					} else if (app.data.friends.get(friend.phone) != null) {
						intent.putExtra("type",
								BusinessCardActivity.TYPE_FRIEND);
					} else {
						intent.putExtra("type",
								BusinessCardActivity.TYPE_TEMPFRIEND);
						intent.putExtra("friend", friend);
					}
					intent.putExtra("phone", friend.phone);
					startActivity(intent);
					//
					// if (app.data.friends.get(friend.phone) != null) {
					// mMainModeManager.mBusinessCardFragment.mStatus =
					// BusinessCardFragment.SHOW_FRIEND;
					// mMainModeManager.mBusinessCardFragment.mShowFriend =
					// friend;
					// mMainModeManager
					// .showNext(mMainModeManager.mBusinessCardFragment);
					// } else if (friend.phone.equals(app.data.user.phone)) {
					// mMainModeManager.mBusinessCardFragment.mStatus =
					// BusinessCardFragment.SHOW_SELF;
					// mMainModeManager.mBusinessCardFragment.mShowFriend =
					// friend;
					// mMainModeManager
					// .showNext(mMainModeManager.mBusinessCardFragment);
					// } else {
					// mMainModeManager.mBusinessCardFragment.mStatus =
					// BusinessCardFragment.SHOW_TEMPFRIEND;
					// mMainModeManager.mBusinessCardFragment.mShowFriend =
					// friend;
					// mMainModeManager
					// .showNext(mMainModeManager.mBusinessCardFragment);
					// }
				}
			});
			return convertView;
		}
	}

	class SquareOnLineUserHolder {
		ImageView uHead;
		TextView uNickName;
		ImageView uSex;
		TextView uAge;
		TextView uDistance;
		TextView uDiscription;
	}

	// public void getSquareOnLineUsers() {
	// app.networkHandler.connection(new CommonNetConnection() {
	// @Override
	// protected void settings(Settings settings) {
	// settings.url = API.DOMAIN + API.SQUARE_GETSQUAREUSERS;
	// Map<String, String> params = new HashMap<String, String>();
	// params.put("phone", app.data.user.phone);
	// params.put("accessKey", app.data.user.accessKey);
	// params.put("gid", SquareInfomationFragment.mSquareID);
	// settings.params = params;
	// }
	//
	// @Override
	// public void success(JSONObject jData) {
	// try {
	// users = JSONParser.generateFriendsFromJSON(jData
	// .getJSONArray("users"));
	// app.UIHandler.post(new Runnable() {
	//
	// @Override
	// public void run() {
	// onLineAdapter = new SquareOnLineAdapter(users);
	// mSquareOnLineUserView.setAdapter(onLineAdapter);
	// }
	// });
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }
	// });
	// }
}
