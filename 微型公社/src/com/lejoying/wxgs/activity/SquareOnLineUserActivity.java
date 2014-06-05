package com.lejoying.wxgs.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
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
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.handler.FileHandler.FileResult;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.parser.JSONParser;

public class SquareOnLineUserActivity extends Activity {

	MainApplication app = MainApplication.getMainApplication();
	LayoutInflater mInflater;
	RelativeLayout mBackView;
	ListView mSquareOnLineUserView;

	SquareOnLineAdapter onLineAdapter;
	List<Friend> users;
	String mSquareID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		mSquareID = intent.getStringExtra("mSquareID");
		setContentView(R.layout.activity_square_online_user);
		mInflater = getLayoutInflater();
		mBackView = (RelativeLayout) findViewById(R.id.backview);
		mSquareOnLineUserView = (ListView) findViewById(R.id.lv_square_online_user);

		// users = new ArrayList<Friend>();
		// for (int i = 0; i < 20; i++) {
		// Friend friend = new Friend();
		// friend.phone = app.data.user.phone;
		// friend.nickName = app.data.user.nickName;
		// friend.sex = app.data.user.sex;
		// friend.mainBusiness = app.data.user.mainBusiness;
		// friend.head = app.data.user.head;
		// users.add(friend);
		// }
		getSquareOnLineUsers();
		initEvent();
		super.onCreate(savedInstanceState);
	}

	private void initEvent() {
		mBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
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
						R.layout.activity_square_online_user_item, null);
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
			app.fileHandler.getSquareOnLineHeadImage(friend.head,
					new FileResult() {

						@Override
						public void onResult(String where, Bitmap bitmap) {
							userHolder0.uHead
									.setImageBitmap(app.fileHandler.bitmaps
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
					Intent intent = new Intent(SquareOnLineUserActivity.this,
							BusinessCardActivity.class);
					intent.putExtra("friend", friend);

					if (app.data.friends.get(friend.phone) != null) {
						intent.putExtra("mStatus",
								BusinessCardActivity.SHOW_FRIEND);
					} else if (friend.phone.equals(app.data.user.phone)) {
						intent.putExtra("mStatus",
								BusinessCardActivity.SHOW_SELF);
					} else {
						intent.putExtra("mStatus",
								BusinessCardActivity.SHOW_TEMPFRIEND);
					}
					startActivity(intent);
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

	public void getSquareOnLineUsers() {
		app.networkHandler.connection(new CommonNetConnection() {
			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.SQUARE_GETSQUAREUSERS;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("gid", mSquareID);
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
				try {
					users = JSONParser.generateFriendsFromJSON(jData
							.getJSONArray("users"));
					app.UIHandler.post(new Runnable() {

						@Override
						public void run() {
							onLineAdapter = new SquareOnLineAdapter(users);
							mSquareOnLineUserView.setAdapter(onLineAdapter);
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
