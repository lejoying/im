package com.lejoying.mc.fragment;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lejoying.mc.R;
import com.lejoying.mc.api.API;
import com.lejoying.mc.data.App;
import com.lejoying.mc.data.Circle;
import com.lejoying.mc.data.Friend;
import com.lejoying.mc.utils.MCDataTools;
import com.lejoying.mc.utils.MCHttpTools;
import com.lejoying.mc.utils.MCImageTools;
import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.mc.utils.MCNetTools.ResponseListener;

public class Copy_2_of_FriendsFragment extends BaseFragment {
	public final int NOTIFYDATASETCHANGED = 10;

	public static Copy_2_of_FriendsFragment instance;

	App app = App.getInstance();

	FriendsAdapter mFriendsAdapter;
	FriendsHandler mFriendsHandler;

	private View mContent;
	private LayoutInflater mInflater;

	int showMessageCount;
	Bitmap head;
	List<View> messageViews;
	List<View> circleViews;
	List<View> showViews;
	Map<Integer, List<View>> circlePageViews;

	ListView listView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mInflater = getActivity().getLayoutInflater();
		head = MCImageTools.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.face_man), true, 5, Color.WHITE);

		instance = this;

		showMessageCount = app.data.lastChatFriends.size() > 5 ? 5
				: app.data.lastChatFriends.size();
		getUser();
		mFriendsAdapter = new FriendsAdapter();
		mFriendsHandler = new FriendsHandler();
		circlePageViews = new Hashtable<Integer, List<View>>();
		showViews = new ArrayList<View>();
		messageViews = new ArrayList<View>();
		circleViews = new ArrayList<View>();
		changeContentFragment(new CircleMenuFragment());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMCFragmentManager.showCircleMenuToTop(false, false);
		mMCFragmentManager
				.setCircleMenuPageName(getString(R.string.page_friend));
		mContent = inflater.inflate(R.layout.f_friends, null);

		return mContent;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		listView = (ListView) getActivity().findViewById(android.R.id.list);
		listView.setAdapter(mFriendsAdapter);
	}

	class FriendsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return messageViews.size() + circleViews.size();
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if (arg0 < messageViews.size()) {
				return messageViews.get(arg0);
			} else {
				return circleViews.get(arg0 - messageViews.size());
			}
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			super.unregisterDataSetObserver(observer);
		}

	}

	@Override
	protected EditText showSoftInputOnShow() {
		// TODO Auto-generated method stub
		return null;
	}

	private void getUser() {
		Bundle params = new Bundle();
		params.putString("phone", app.data.user.phone);
		params.putString("accessKey", app.data.user.accessKey);
		params.putString("target", app.data.user.phone);

		MCNetTools.ajax(getActivity(), API.ACCOUNT_GET, params,
				MCHttpTools.SEND_POST, 5000, new ResponseListener() {

					@Override
					public void success(JSONObject data) {
						try {
							MCDataTools.updateUser(data
									.getJSONObject("account"));
							getCirclesAndFriends();
						} catch (JSONException e) {
						}
					}

					@Override
					public void noInternet() {
						// TODO Auto-generated method stub
					}

					@Override
					public void failed() {
						// TODO Auto-generated method stub
					}

					@Override
					public void connectionCreated(
							HttpURLConnection httpURLConnection) {
						// TODO Auto-generated method stub

					}
				});
	}

	private void getCirclesAndFriends() {
		Bundle params = new Bundle();
		params.putString("phone", app.data.user.phone);
		params.putString("accessKey", app.data.user.accessKey);
		MCNetTools.ajax(getActivity(), API.RELATION_GETCIRCLESANDFRIENDS,
				params, MCHttpTools.SEND_POST, 5000, new ResponseListener() {
					@Override
					public void success(JSONObject data) {
						try {
							MCDataTools.saveCircles(data
									.getJSONArray("circles"));
							getMessages();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void noInternet() {
						// TODO Auto-generated method stub

					}

					@Override
					public void failed() {
						// TODO Auto-generated method stub

					}

					@Override
					public void connectionCreated(
							HttpURLConnection httpURLConnection) {
						// TODO Auto-generated method stub

					}
				});
	}

	private void getMessages() {
		Bundle params = new Bundle();
		params.putString("phone", app.data.user.phone);
		params.putString("accessKey", app.data.user.accessKey);
		// String flag = app.data.user.flag;
		params.putString("flag", "0");
		MCNetTools.ajax(getActivity(), API.MESSAGE_GET, params,
				MCHttpTools.SEND_POST, 5000, new ResponseListener() {

					@Override
					public void success(JSONObject data) {
						try {
							MCDataTools.saveMessages(data
									.getJSONArray("messages"));
							app.data.user.flag = String.valueOf(data
									.getInt("flag"));
							mFriendsHandler
									.sendEmptyMessage(NOTIFYDATASETCHANGED);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void noInternet() {
						// TODO Auto-generated method stub

					}

					@Override
					public void failed() {
						// TODO Auto-generated method stub

					}

					@Override
					public void connectionCreated(
							HttpURLConnection httpURLConnection) {
						// TODO Auto-generated method stub

					}
				});
	}

	void createShowViews() {

	}

	void createMessageViews() {
		Map<String, Friend> friends = app.data.friends;
		List<String> lastChatFriends = app.data.lastChatFriends;
		List<View> views = new ArrayList<View>();
		for (int i = 0; i < showMessageCount; i++) {
			View content = mInflater.inflate(R.layout.f_messages_item, null);
			TextView tv_nickname = (TextView) content
					.findViewById(R.id.tv_nickname);
			TextView tv_lastchat = (TextView) content
					.findViewById(R.id.tv_lastchat);
			ImageView iv_head = (ImageView) content.findViewById(R.id.iv_head);
			TextView tv_notread = (TextView) content
					.findViewById(R.id.tv_notread);
			tv_nickname.setText(friends.get(lastChatFriends.get(i)).nickName);
			tv_lastchat.setText(friends.get(lastChatFriends.get(i)).messages
					.get(0).content);
			iv_head.setImageBitmap(head);
			Integer notread = friends.get(lastChatFriends.get(i)).notReadMessagesCount;
			if (notread != null) {
				tv_notread.setText(notread.toString());
			}
			views.add(content);
		}
		messageViews = views;
	}

	void createCircleViews() {
		List<Circle> circles = app.data.circles;
		final Map<String, Friend> friends = app.data.friends;
		List<View> views = new ArrayList<View>();
		for (int i = circles.size() - 1; i > -1; i--) {
			View content = mInflater.inflate(R.layout.f_group_panel, null);
			TextView tv_groupname = (TextView) content
					.findViewById(R.id.tv_groupname);
			tv_groupname.setText(circles.get(i).name);
			ViewPager vp_content = (ViewPager) content
					.findViewById(R.id.vp_content);
			final List<String> phones = circles.get(i).phones;
			final int pagecount = phones.size() % 6 == 0 ? phones.size() / 6
					: phones.size() / 6 + 1;
			System.out.println(phones.size());
			final List<View> pageviews = new ArrayList<View>();
			for (int j = 0; j < pagecount; j++) {
				final int a = j;
				System.out.println(friends.get(phones.get(a * 6 + 0)).nickName);
				BaseAdapter gridpageAdapter = new BaseAdapter() {
					@Override
					public View getView(final int position, View convertView,
							final ViewGroup parent) {
						View v = mInflater.inflate(
								R.layout.f_group_panelitem_gridpageitem_user,
								null);
						ImageView iv_head = (ImageView) v
								.findViewById(R.id.iv_head);
						TextView tv_nickname = (TextView) v
								.findViewById(R.id.tv_nickname);

						iv_head.setImageBitmap(head);
						tv_nickname.setText(friends.get(phones.get(a * 6
								+ position)).nickName);
						v.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
							}
						});
						return v;
					}

					@Override
					public long getItemId(int position) {
						return position;
					}

					@Override
					public Object getItem(int position) {
						return friends.get(phones.get(a * 6 + position));
					}

					@Override
					public int getCount() {
						int nowcount = 0;
						if (a < pagecount - 1) {
							nowcount = 6;
						} else {
							nowcount = phones.size() - a * 6;
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
						R.layout.f_group_panelitem_gridpage, null);
				gridpage.setAdapter(gridpageAdapter);
				pageviews.add(gridpage);
			}
			PagerAdapter vp_contentAdapter = new PagerAdapter() {
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
					((ViewPager) container).removeView(pageviews.get(position));
				}

				@Override
				public Object instantiateItem(View container, int position) {
					((ViewPager) container).addView(pageviews.get(position));
					return pageviews.get(position);
				}

				@Override
				public void unregisterDataSetObserver(DataSetObserver observer) {
					if (observer != null) {
						super.unregisterDataSetObserver(observer);
					}
				}
			};

			vp_content.setAdapter(vp_contentAdapter);
			vp_content.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int action = event.getAction();
					switch (action) {
					case MotionEvent.ACTION_MOVE:
						listView.requestDisallowInterceptTouchEvent(true);
						break;
					default:
						break;
					}
					return false;
				}
			});
			views.add(content);
		}
		circleViews = views;
	}

	class FriendsHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case NOTIFYDATASETCHANGED:
				showMessageCount = app.data.lastChatFriends.size() > 5 ? 5
						: app.data.lastChatFriends.size();
				createMessageViews();
				createCircleViews();
				mFriendsAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
		}
	}
}
