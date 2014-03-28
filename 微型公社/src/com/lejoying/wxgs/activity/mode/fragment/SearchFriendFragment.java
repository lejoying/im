package com.lejoying.wxgs.activity.mode.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.utils.LocationUtils;
import com.lejoying.wxgs.activity.view.ScrollContent;
import com.lejoying.wxgs.activity.view.ScrollContentAdapter;
import com.lejoying.wxgs.activity.view.ScrollRelativeLayout;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.handler.FileHandler.FileResult;
import com.lejoying.wxgs.app.handler.LocationHandler.LocationListener;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.parser.JSONParser;

public class SearchFriendFragment extends BaseFragment {

	MainApplication app = MainApplication.getMainApplication();
	LayoutInflater mInflater;
	MainModeManager mMainModeManager;

	boolean isReceiveLocation;

	public static SearchFriendFragment instance;

	@Override
	public void onResume() {
		CircleMenu.showBack();
		super.onResume();
	}

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	ScrollView mContent;
	ScrollRelativeLayout viewContainer;
	CircleHolder circleHolder;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		instance = this;
		mInflater = inflater;

		mContent = (ScrollView) inflater.inflate(R.layout.f_vertical_scroll,
				null);
		viewContainer = (ScrollRelativeLayout) mContent
				.findViewById(R.id.viewContainer);
		circleHolder = new CircleHolder();
		notifyCircleView(viewContainer, app.data.nearByFriends, circleHolder);

		final EditText mView_phone = (EditText) mContent
				.findViewById(R.id.et_phone);
		View mView_search = mContent.findViewById(R.id.btn_search);
		mView_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String phone = mView_phone.getText().toString();
				if (phone == null || phone.equals("")) {
					Alert.showMessage(getString(R.string.alert_text_phonenotnull));
					return;
				}
				hideSoftInput();
				search(phone);
			}
		});

		View scanBusinessCard = mContent.findViewById(R.id.scanBusinessCard);
		scanBusinessCard.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mMainModeManager.showNext(mMainModeManager.mScanQRCodeFragment);
			}
		});

		app.locationHandler.requestLocation(new LocationListener() {

			@Override
			public void onReceiveLocation(BDLocation location) {
				LocationUtils.updateLocation(location.getLongitude(),
						location.getLatitude());
				getNearByFriend(location.getLongitude(), location.getLatitude());
			}
		});

		return mContent;
	}

	void getNearByFriend(final double longitude, final double latitude) {
		app.networkHandler.connection(new CommonNetConnection() {

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.LBS_NEARBYACCOUNTS;
				settings.params = new HashMap<String, String>();
				settings.params.put("phone", app.data.user.phone);
				settings.params.put("accessKey", app.data.user.accessKey);
				settings.params.put("area", "{\"longitude\":\"" + longitude
						+ "\",\"latitude\":\"" + latitude + "\",\"radius\":\""
						+ 2000 + "\"}");
			}

			@Override
			public void success(final JSONObject jData) {
				System.out.println(jData);
				app.dataHandler.exclude(new Modification() {
					@Override
					public void modifyData(Data data) {
						try {
							data.nearByFriends = JSONParser
									.generateFriendsFromJSON(jData
											.getJSONArray("accounts"));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void modifyUI() {
						notifyCircleView(viewContainer, app.data.nearByFriends,
								circleHolder);
					}
				});
			}
		});
	}

	class SearchFriendAdapter extends ScrollContentAdapter {

		public SearchFriendAdapter(ScrollContent scrollContent) {
			super(scrollContent);
			// TODO Auto-generated constructor stub
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			switch (position) {
			case 0:
				convertView = mInflater.inflate(R.layout.fragment_panel, null);
				TextView tv_groupname = (TextView) convertView
						.findViewById(R.id.panel_name);
				tv_groupname.setText("附近好友");

				ScrollRelativeLayout viewContainer = (ScrollRelativeLayout) convertView
						.findViewById(R.id.viewContainer);
				CircleHolder circleHolder = new CircleHolder();
				notifyCircleView(viewContainer, app.data.nearByFriends,
						circleHolder);

				break;
			case 1:
				convertView = mInflater.inflate(R.layout.f_searchfriend, null);
				final EditText mView_phone = (EditText) convertView
						.findViewById(R.id.et_phone);
				View mView_search = convertView.findViewById(R.id.btn_search);
				mView_search.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						final String phone = mView_phone.getText().toString();
						if (phone == null || phone.equals("")) {
							Alert.showMessage(getString(R.string.alert_text_phonenotnull));
							return;
						}
						hideSoftInput();
						search(phone);
					}
				});

				break;
			case 2:
				convertView = mInflater.inflate(R.layout.fragment_item_button,
						null);
				Button button = (Button) convertView.findViewById(R.id.button);
				button.setText(getString(R.string.button_scanbusinesscard));
				button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						mMainModeManager
								.showNext(mMainModeManager.mScanQRCodeFragment);
					}
				});
				break;

			default:
				break;
			}
			return convertView;
		}
	}

	public void search(final String phone) {

		app.networkHandler.connection(new CommonNetConnection() {
			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.ACCOUNT_GET;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("target", "[\"" + phone + "\"]");
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
				try {
					final Friend friend = JSONParser
							.generateFriendFromJSON(jData.getJSONArray(
									"accounts").getJSONObject(0));

					if (phone.equals(app.data.user.phone)) {
						mMainModeManager.mBusinessCardFragment.mStatus = BusinessCardFragment.SHOW_SELF;
						app.dataHandler.exclude(new Modification() {
							@Override
							public void modifyData(Data data) {
								data.user.nickName = friend.nickName;
								data.user.mainBusiness = friend.mainBusiness;
								data.user.head = friend.head;
							}
						});
					} else if (app.data.friends.get(phone) != null) {
						mMainModeManager.mBusinessCardFragment.mStatus = BusinessCardFragment.SHOW_FRIEND;
						app.dataHandler.exclude(new Modification() {

							@Override
							public void modifyData(Data data) {
								friend.messages = data.friends.get(phone).messages;
								data.friends.put(phone, friend);
							}
						});
					} else {
						mMainModeManager.mBusinessCardFragment.mStatus = BusinessCardFragment.SHOW_TEMPFRIEND;
					}
					mMainModeManager.mBusinessCardFragment.mShowFriend = friend;
					mMainModeManager
							.showNext(mMainModeManager.mBusinessCardFragment);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	public int getScreenWidth() {
		return getActivity().getResources().getDisplayMetrics().widthPixels;
	}

	public int getScreenHeight() {
		return getActivity().getResources().getDisplayMetrics().heightPixels;
	}

	public float dp2px(float px) {
		float dp = getActivity().getResources().getDisplayMetrics().density
				* px + 0.5f;
		return dp;
	}

	class Position {
		int x = 0;
		int y = 0;
	}

	Position switchPosition(int i) {
		Position position = new Position();
		if ((i + 1) % 6 == 1) {
			position.y = (int) dp2px(11);
			position.x = (int) dp2px(26 + i / 6 * 326);
		} else if ((i + 1) % 6 == 2) {
			position.y = (int) dp2px(11);
			position.x = (int) dp2px(26 + 55 + 48 + i / 6 * 326);
		} else if ((i + 1) % 6 == 3) {
			position.y = (int) dp2px(11);
			position.x = (int) dp2px(26 + 55 + 48 + 55 + 48 + i / 6 * 326);
		} else if ((i + 1) % 6 == 4) {
			position.y = (int) dp2px(11 + 73 + 27);
			position.x = (int) dp2px(26 + i / 6 * 326);
		} else if ((i + 1) % 6 == 5) {
			position.y = (int) dp2px(11 + 73 + 27);
			position.x = (int) dp2px(26 + 55 + 48 + i / 6 * 326);
		} else if ((i + 1) % 6 == 0) {
			position.y = (int) dp2px(11 + 73 + 27);
			position.x = (int) dp2px(26 + 55 + 48 + 55 + 48 + i / 6 * 326);
		}
		return position;
	}

	class FriendHolder {
		Position position;
		View view;
		String phone = "";
		int index;

		@Override
		public boolean equals(Object o) {
			boolean flag = false;
			if (o != null) {
				if (o instanceof FriendHolder) {
					FriendHolder h = (FriendHolder) o;
					if (phone.equals(h.phone)) {
						flag = true;
					}
				} else if (o instanceof String) {
					String s = (String) o;
					if (phone.equals(s)) {
						flag = true;
					}
				}
			}
			return flag;
		}
	}

	class CircleHolder {
		public List<FriendHolder> friendHolders = new ArrayList<FriendHolder>();
	}

	public void resolveFriendsPositions(CircleHolder circleHolder) {
		for (int i = 0; i < circleHolder.friendHolders.size(); i++) {
			FriendHolder friendHolder = circleHolder.friendHolders.get(i);
			friendHolder.position = switchPosition(i);
			friendHolder.index = i;
		}
	}

	public void setFriendsPositions(CircleHolder circleHolder) {
		for (int i = 0; i < circleHolder.friendHolders.size(); i++) {
			FriendHolder friendHolder = circleHolder.friendHolders.get(i);

			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					(int) dp2px(55f),
					android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.rightMargin = -Integer.MAX_VALUE;

			params.topMargin = friendHolder.position.y;
			params.leftMargin = friendHolder.position.x;
			friendHolder.view.setLayoutParams(params);
		}
	}

	void notifyCircleView(final ScrollRelativeLayout container,
			List<Friend> friends, CircleHolder circleHolder) {
		for (int i = 0; i < friends.size(); i++) {
			final Friend friend = friends.get(i);
			FriendHolder friendHolder = new FriendHolder();
			friendHolder.phone = friend.phone;
			int index = circleHolder.friendHolders.indexOf(friendHolder);
			friendHolder = (index != -1 ? circleHolder.friendHolders
					.remove(index) : null);
			View convertView;
			if (friendHolder == null) {
				convertView = generateFriendView(friend);
				friendHolder = new FriendHolder();
				friendHolder.phone = friend.phone;
				friendHolder.view = convertView;
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(final View holderView) {
						if (app.data.friends.get(friend.phone) != null) {
							mMainModeManager.mBusinessCardFragment.mStatus = BusinessCardFragment.SHOW_FRIEND;
							mMainModeManager.mBusinessCardFragment.mShowFriend = friend;
							mMainModeManager
									.showNext(mMainModeManager.mBusinessCardFragment);
						} else if (friend.phone.equals(app.data.user.phone)) {
							mMainModeManager.mBusinessCardFragment.mStatus = BusinessCardFragment.SHOW_SELF;
							mMainModeManager.mBusinessCardFragment.mShowFriend = friend;
							mMainModeManager
									.showNext(mMainModeManager.mBusinessCardFragment);
						} else {
							mMainModeManager.mBusinessCardFragment.mStatus = BusinessCardFragment.SHOW_TEMPFRIEND;
							mMainModeManager.mBusinessCardFragment.mShowFriend = friend;
							mMainModeManager
									.showNext(mMainModeManager.mBusinessCardFragment);
						}
					}
				});
				container.addView(convertView);
			}

			circleHolder.friendHolders.add(i, friendHolder);

		}

		final GestureDetector detector = new GestureDetector(getActivity(),
				new SimpleOnGestureListener() {
					float x0 = 0;
					float dx = 0;

					@Override
					public boolean onDown(MotionEvent e) {
						x0 = e.getRawX();
						return true;
					}

					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {
						dx = e2.getRawX() - x0;
						container.scrollBy(-(int) (dx), 0);
						x0 = e2.getRawX();
						return true;
					}
				});

		container.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mContent.requestDisallowInterceptTouchEvent(true);
				return detector.onTouchEvent(event);
			}
		});

		resolveFriendsPositions(circleHolder);
		setFriendsPositions(circleHolder);

	}

	View generateFriendView(Friend friend) {
		View convertView = mInflater.inflate(
				R.layout.fragment_circles_gridpage_item, null);
		final ImageView head = (ImageView) convertView
				.findViewById(R.id.iv_head);
		TextView nickname = (TextView) convertView
				.findViewById(R.id.tv_nickname);
		nickname.setText(friend.nickName);
		final String headFileName = friend.head;
		app.fileHandler.getHeadImage(headFileName, new FileResult() {
			@Override
			public void onResult(String where) {
				head.setImageBitmap(app.fileHandler.bitmaps.get(headFileName));
			}
		});
		return convertView;
	}

}
