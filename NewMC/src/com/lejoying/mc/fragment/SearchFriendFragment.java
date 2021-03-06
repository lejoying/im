package com.lejoying.mc.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
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

import com.baidu.location.BDLocation;
import com.lejoying.mc.R;
import com.lejoying.mc.data.App;
import com.lejoying.mc.data.Data;
import com.lejoying.mc.data.Friend;
import com.lejoying.mc.data.handler.DataHandler.Modification;
import com.lejoying.mc.data.handler.DataHandler.UIModification;
import com.lejoying.mc.data.handler.FileHandler.FileResult;
import com.lejoying.mc.data.handler.LocationHandler.LocationListener;
import com.lejoying.mc.network.API;
import com.lejoying.mc.utils.AjaxAdapter;
import com.lejoying.mc.utils.MCNetUtils;
import com.lejoying.mc.utils.MCNetUtils.Settings;
import com.lejoying.mc.view.FriendViewPager;
import com.lejoying.mc.view.ScrollContent;
import com.lejoying.mc.view.ScrollContentAdapter;

public class SearchFriendFragment extends BaseFragment {

	App app = App.getInstance();
	LayoutInflater mInflater;

	View mContent;

	ScrollContent mScrollContent;
	ScrollContentAdapter mAdapter;

	boolean isReceiveLocation;

	public static SearchFriendFragment instance;

	@Override
	protected EditText showSoftInputOnShow() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		instance = this;
		mInflater = inflater;

		mContent = inflater.inflate(R.layout.f_vertical_scroll, null);

		mScrollContent = (ScrollContent) mContent.findViewById(R.id.content);

		app.locationHandler.requestLocation(new LocationListener() {

			@Override
			public void onReceivePoi(BDLocation poiLocation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onReceiveLocation(final BDLocation location) {
				if (isReceiveLocation) {
					return;
				}
				isReceiveLocation = true;
				MCNetUtils.ajax(new AjaxAdapter() {

					@Override
					public void setParams(Settings settings) {
						try {
							settings.url = API.LBS_NEARBYACCOUNTS;
							JSONObject jArea = new JSONObject();
							jArea.put("latitude", location.getLatitude());
							jArea.put("longitude", location.getLongitude());
							jArea.put("radius", 500000);
							Bundle params = new Bundle();
							params.putString("phone", app.data.user.phone);
							params.putString("accessKey",
									app.data.user.accessKey);
							params.putString("area", jArea.toString());
							settings.params = params;
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void onSuccess(final JSONObject jData) {
						app.dataHandler.modifyData(new Modification() {
							@Override
							public void modify(Data data) {
								try {
									app.mJSONHandler.saveNearByFriends(
											jData.getJSONArray("accounts"),
											data);
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}, new UIModification() {

							@Override
							public void modifyUI() {
								mAdapter.notifyDataSetChanged();
							}
						});
					}
				});
			}
		});

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
			return 5;
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
			case 1:
				convertView = mInflater.inflate(R.layout.f_friend_panel, null);
				TextView tv_groupname = (TextView) convertView
						.findViewById(R.id.textView_groupNameAndMemberCount);
				tv_groupname.setText("附近好友");

				FriendViewPager friendViewPager = (FriendViewPager) convertView
						.findViewById(R.id.vp_content);

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
								convertView = app.inflater
										.inflate(
												R.layout.f_friend_panelitem_gridpage_item,
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
					GridView gridpage = (GridView) app.inflater.inflate(
							R.layout.f_friend_panelitem_gridpage, null);
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
			case 2:
				convertView = mInflater.inflate(R.layout.f_searchfriend, null);
				final EditText mView_phone = (EditText) convertView
						.findViewById(R.id.et_phone);
				View mView_search = convertView.findViewById(R.id.btn_search);
				mView_search.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						final String phone = mView_phone.getText().toString();
						if (phone.equals("")) {
							// showMsg("请输入好友手机号");
							return;
						}
						search(phone);
					}
				});

				break;
			case 3:
				convertView = mInflater.inflate(R.layout.f_button, null);
				((Button) convertView.findViewById(R.id.button))
						.setText("扫描名片");
				break;

			default:
				convertView = mInflater.inflate(R.layout.f_margin, null);
				break;
			}
			return convertView;
		}
	}

	public void search(final String phone) {
		final Bundle params = new Bundle();
		params.putString("phone", app.data.user.phone);
		params.putString("accessKey", app.data.user.accessKey);
		JSONArray jTarget = new JSONArray();
		jTarget.put(phone);
		params.putString("target", jTarget.toString());

		MCNetUtils.ajax(new AjaxAdapter() {

			@Override
			public void setParams(Settings settings) {
				settings.url = API.ACCOUNT_GET;
				settings.params = params;
			}

			@Override
			public void onSuccess(final JSONObject jData) {
				app.dataHandler.modifyData(new Modification() {
					@Override
					public void modify(Data data) {
						try {
							jData.getString(getString(R.string.app_reason));
							return;
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (phone.equals(data.user.phone)) {
							try {
								app.mJSONHandler.updateUser(
										jData.getJSONArray("accounts")
												.getJSONObject(0), data);
								app.businessCardStatus = app.SHOW_SELF;
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							return;
						}
						if (data.friends.get(phone) != null) {
							data.tempFriend = data.friends.get(phone);
							app.businessCardStatus = app.SHOW_FRIEND;
							return;
						}

						try {
							data.tempFriend = app.mJSONHandler
									.generateFriendFromJSON(jData.getJSONArray(
											"accounts").getJSONObject(0));
							data.tempFriend.temp = true;
							app.businessCardStatus = app.SHOW_TEMPFRIEND;
							mMCFragmentManager.replaceToContent(
									new BusinessCardFragment(), true);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new UIModification() {

					@Override
					public void modifyUI() {
						try {
							jData.getString(getString(R.string.app_reason));
							mMCFragmentManager.replaceToContent(
									new FriendNotFoundFragment(), true);
							return;
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						mMCFragmentManager.replaceToContent(
								new BusinessCardFragment(), true);
					}
				});
			}
		});

	}

	@Override
	public String setMark() {
		// TODO Auto-generated method stub
		return app.searchFriendFragment;
	}

}
