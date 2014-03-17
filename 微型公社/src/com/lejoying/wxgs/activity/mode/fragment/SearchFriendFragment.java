package com.lejoying.wxgs.activity.mode.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.view.CommonViewPager;
import com.lejoying.wxgs.activity.view.ScrollContent;
import com.lejoying.wxgs.activity.view.ScrollContentAdapter;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.handler.FileHandler.FileResult;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.parser.JSONParser;

public class SearchFriendFragment extends BaseFragment {

	MainApplication app = MainApplication.getMainApplication();
	LayoutInflater mInflater;
	MainModeManager mMainModeManager;

	View mContent;

	ScrollContent mScrollContent;
	ScrollContentAdapter mAdapter;

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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		instance = this;
		mInflater = inflater;

		mContent = inflater.inflate(R.layout.f_vertical_scroll, null);

		mScrollContent = (ScrollContent) mContent.findViewById(R.id.content);

		// app.locationHandler.requestLocation(new LocationListener() {
		//
		// @Override
		// public void onReceivePoi(BDLocation poiLocation) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onReceiveLocation(final BDLocation location) {
		// if (isReceiveLocation) {
		// return;
		// }
		// isReceiveLocation = true;
		// MCNetUtils.ajax(new AjaxAdapter() {
		//
		// @Override
		// public void setParams(Settings settings) {
		// try {
		// settings.url = API.LBS_NEARBYACCOUNTS;
		// JSONObject jArea = new JSONObject();
		// jArea.put("latitude", location.getLatitude());
		// jArea.put("longitude", location.getLongitude());
		// jArea.put("radius", 500000);
		// Bundle params = new Bundle();
		// params.putString("phone", app.data.user.phone);
		// params.putString("accessKey",
		// app.data.user.accessKey);
		// params.putString("area", jArea.toString());
		// settings.params = params;
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		//
		// @Override
		// public void onSuccess(final JSONObject jData) {
		// app.dataHandler.modifyData(new Modification() {
		// @Override
		// public void modify(Data data) {
		// try {
		// app.mJSONHandler.saveNearByFriends(
		// jData.getJSONArray("accounts"),
		// data);
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// }, new UIModification() {
		//
		// @Override
		// public void modifyUI() {
		// mAdapter.notifyDataSetChanged();
		// }
		// });
		// }
		// });
		// }
		// });

		return mContent;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mAdapter = new SearchFriendAdapter(mScrollContent);
		mScrollContent.setAdapter(mAdapter);
	}

	class ItemHolder {
		ImageView iv_head;
		TextView tv_nickname;
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

				CommonViewPager friendViewPager = (CommonViewPager) convertView
						.findViewById(R.id.commonViewPager);

				PagerAdapter vp_contentAdapter;

				final List<Friend> friends = app.data.nearByFriends;
				final int pagecount = friends.size() % 6 == 0 ? friends.size() / 6
						: friends.size() / 6 + 1;
				final List<View> pageviews = new ArrayList<View>();
				for (int i = 0; i < pagecount; i++) {
					final int a = i;
					BaseAdapter gridpageAdapter = new BaseAdapter() {
						@Override
						public View getView(final int position,
								View convertView, final ViewGroup parent) {
							ItemHolder itemHolder = null;
							if (convertView == null) {
								convertView = mInflater
										.inflate(
												R.layout.fragment_circles_gridpage_item,
												null);
								itemHolder = new ItemHolder();
								itemHolder.iv_head = (ImageView) convertView
										.findViewById(R.id.iv_head);
								itemHolder.tv_nickname = (TextView) convertView
										.findViewById(R.id.tv_nickname);
								convertView.setTag(itemHolder);
							} else {
								itemHolder = (ItemHolder) convertView.getTag();
							}
							final String headFileName = friends.get(a * 6
									+ position).head;
							final ImageView iv_head = itemHolder.iv_head;
							app.fileHandler.getHeadImage(headFileName,
									new FileResult() {
										@Override
										public void onResult(String where) {
											iv_head.setImageBitmap(app.fileHandler.bitmaps
													.get(headFileName));
										}
									});
							itemHolder.tv_nickname.setText(friends.get(a * 6
									+ position).nickName);

							return convertView;
						}

						@Override
						public long getItemId(int position) {
							return position;
						}

						@Override
						public Object getItem(int position) {
							return friends.get(a * 6 + position);
						}

						@Override
						public int getCount() {
							int nowcount = 0;
							if (a < pagecount - 1) {
								nowcount = 6;
							} else {
								nowcount = friends.size() - a * 6;
							}
							return nowcount;
						}

						@Override
						public void unregisterDataSetObserver(
								DataSetObserver observer) {
							if (observer != null) {
								super.unregisterDataSetObserver(observer);
							}
						}

					};
					GridView gridpage = (GridView) mInflater.inflate(
							R.layout.fragment_circles_gridpage, null);
					gridpage.setAdapter(gridpageAdapter);
					pageviews.add(gridpage);
				}

				vp_contentAdapter = new PagerAdapter() {
					@Override
					public boolean isViewFromObject(View arg0, Object arg1) {
						return arg0 == arg1;
					}

					@Override
					public int getCount() {
						return pageviews.size();
					}

					@Override
					public void destroyItem(View container, int position,
							Object object) {
						((ViewPager) container).removeView(pageviews
								.get(position));
					}

					@Override
					public Object instantiateItem(View container, int position) {
						((ViewPager) container)
								.addView(pageviews.get(position));
						return pageviews.get(position);
					}

					@Override
					public void unregisterDataSetObserver(
							DataSetObserver observer) {
						if (observer != null) {
							super.unregisterDataSetObserver(observer);
						}
					}
				};
				friendViewPager.setAdapter(vp_contentAdapter);

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
				convertView = mInflater.inflate(R.layout.fragment_item_buttom,
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

}
