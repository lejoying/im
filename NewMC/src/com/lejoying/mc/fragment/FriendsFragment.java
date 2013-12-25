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
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lejoying.mc.R;
import com.lejoying.mc.api.API;
import com.lejoying.mc.data.App;
import com.lejoying.mc.data.Circle;
import com.lejoying.mc.utils.MCDataTools;
import com.lejoying.mc.utils.MCHttpTools;
import com.lejoying.mc.utils.MCImageTools;
import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.mc.utils.MCNetTools.ResponseListener;

public class FriendsFragment extends BaseListFragment {

	private final int TYPE_MAX_COUNT = 5;
	private final int TYPE_MESSAGE = 1;
	private final int TYPE_CIRCLE = 2;
	private final int TYPE_BUTTON = 3;
	private final int TYPE_MARGIN = 4;

	public final int NOTIFYDATASETCHANGED = 10;

	public static FriendsFragment instance;

	App app = App.getInstance();

	FriendsAdapter mFriendsAdapter;
	FriendsHandler mFriendsHandler;

	private View mContent;
	private LayoutInflater mInflater;

	int showMessageCount;
	int buttonCount;
	boolean showNewFriends;
	int messageFirstPosition;
	int circleFirstPosition;

	Bitmap head;
	Map<Integer, List<View>> circlePageViews;

	void initPosition() {
		buttonCount = showNewFriends ? 4 : 3;
		messageFirstPosition = showNewFriends ? 2 : 1;
		circleFirstPosition = messageFirstPosition + showMessageCount + 1;
		showMessageCount = app.data.lastChatFriends.size() > 5 ? 5
				: app.data.lastChatFriends.size();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mInflater = getActivity().getLayoutInflater();
		head = MCImageTools.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.face_man), true, 10, Color.WHITE);

		MCDataTools.getUserData(getActivity(), app.data.user);

		instance = this;
		initPosition();
		getUser();
		mFriendsAdapter = new FriendsAdapter();
		mFriendsHandler = new FriendsHandler();
		circlePageViews = new Hashtable<Integer, List<View>>();
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
		setListAdapter(mFriendsAdapter);
	}

	public void onResume() {
		super.onResume();
		mFriendsAdapter.notifyDataSetChanged();
	};

	class FriendsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return showMessageCount + app.data.circles.size() + buttonCount + 2;
		}

		@Override
		public int getViewTypeCount() {
			return TYPE_MAX_COUNT;
		}

		@Override
		public int getItemViewType(int position) {
			int type = 0;
			if (position == 0 || position == getCount() - 1) {
				type = TYPE_MARGIN;
			} else if (position >= messageFirstPosition
					&& position < messageFirstPosition + showMessageCount) {
				type = TYPE_MESSAGE;
			} else if (position >= circleFirstPosition
					&& position < circleFirstPosition + app.data.circles.size()) {
				type = TYPE_CIRCLE;
			} else {
				type = TYPE_BUTTON;
			}
			return type;
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
			int type = getItemViewType(arg0);
			MessageHolder messageHolder = null;
			FriendHolder friendHolder = null;
			ButtonHolder bHolder = null;
			if (arg1 == null) {
				switch (type) {
				case TYPE_MESSAGE:
					arg1 = mInflater.inflate(R.layout.f_messages_item, null);
					messageHolder = new MessageHolder();
					messageHolder.iv_head = (ImageView) arg1
							.findViewById(R.id.iv_head);
					messageHolder.tv_nickname = (TextView) arg1
							.findViewById(R.id.tv_nickname);
					messageHolder.tv_lastchat = (TextView) arg1
							.findViewById(R.id.tv_lastchat);
					messageHolder.tv_notread = (TextView) arg1
							.findViewById(R.id.tv_notread);
					arg1.setTag(messageHolder);
					break;
				case TYPE_CIRCLE:
					arg1 = mInflater.inflate(R.layout.f_group_panel, null);
					friendHolder = new FriendHolder();
					friendHolder.tv_groupname = (TextView) arg1
							.findViewById(R.id.tv_groupname);
					friendHolder.vp_content = (ViewPager) arg1
							.findViewById(R.id.vp_content);
					arg1.setTag(friendHolder);
					break;
				case TYPE_BUTTON:
					arg1 = mInflater.inflate(R.layout.f_button, null);
					bHolder = new ButtonHolder();
					bHolder.button = (Button) arg1;
					arg1.setTag(bHolder);
					break;

				case TYPE_MARGIN:
					arg1 = mInflater.inflate(R.layout.f_margin, null);
					break;
				default:
					break;
				}
			} else {
				switch (type) {
				case TYPE_MESSAGE:
					messageHolder = (MessageHolder) arg1.getTag();
					break;
				case TYPE_CIRCLE:
					friendHolder = (FriendHolder) arg1.getTag();
					break;
				case TYPE_BUTTON:
					bHolder = (ButtonHolder) arg1.getTag();
					break;
				default:
					break;
				}
			}

			switch (type) {
			case TYPE_MESSAGE:
				messageHolder.tv_nickname.setText(app.data.friends
						.get(app.data.lastChatFriends.get(arg0
								- messageFirstPosition)).nickName);
				messageHolder.tv_lastchat
						.setText(app.data.friends.get(app.data.lastChatFriends
								.get(arg0 - messageFirstPosition)).messages
								.get(0).content);
				messageHolder.iv_head.setImageBitmap(head);
				Integer notread = app.data.friends.get(app.data.lastChatFriends
						.get(arg0 - messageFirstPosition)).notReadMessagesCount;
				if (notread != null) {
					messageHolder.tv_notread.setText(notread.toString());
				}
				break;
			case TYPE_CIRCLE:
				Circle circle = app.data.circles
						.get(arg0 - circleFirstPosition);
				friendHolder.tv_groupname.setText(circle.name);
				friendHolder.setCircle(circle);
				break;
			case TYPE_BUTTON:
				if (showNewFriends && arg0 == 1) {
					bHolder.button.setText(getActivity().getString(
							R.string.btn_newfriends));
					bHolder.button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mMCFragmentManager.replaceToContent(
									new NewFriendsFragment(), true);
						}
					});
				} else if (arg0 == showMessageCount + messageFirstPosition) {
					bHolder.button.setText(getActivity().getString(
							R.string.btn_moremessages));
					bHolder.button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							System.out.println("bbb");
						}
					});
				} else if (arg0 == getCount() - 3) {
					bHolder.button.setText(getActivity().getString(
							R.string.btn_newgroup));
					bHolder.button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							System.out.println("cccc");
						}
					});
				} else if (arg0 == getCount() - 2) {
					bHolder.button.setText(getActivity().getString(
							R.string.btn_findmorefriend));
					bHolder.button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mMCFragmentManager.replaceToContent(
									new SearchFriendFragment(), true);
						}
					});
				}
				break;
			default:
				break;
			}

			return arg1;
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			super.unregisterDataSetObserver(observer);
		}

	}

	class MessageHolder {
		ImageView iv_head;
		TextView tv_nickname;
		TextView tv_lastchat;
		TextView tv_notread;
	}

	class FriendHolder {
		TextView tv_groupname;
		ViewPager vp_content;
		PagerAdapter vp_contentAdapter;

		Circle circle;

		class ItemHolder {
			ImageView iv_head;
			TextView tv_nickname;
		}

		public void setCircle(Circle c) {
			this.circle = c;
			if (circlePageViews.get(circle.rid) == null) {
				final List<String> phones = circle.phones;
				final int pagecount = phones.size() % 6 == 0 ? phones.size() / 6
						: phones.size() / 6 + 1;
				List<View> pageviews = new ArrayList<View>();
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
												R.layout.f_group_panelitem_gridpageitem_user,
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

							itemHolder.iv_head.setImageBitmap(head);
							itemHolder.tv_nickname
									.setText(app.data.friends.get(phones.get(a
											* 6 + position)).nickName);
							convertView
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
										}
									});
							return convertView;
						}

						@Override
						public long getItemId(int position) {
							return position;
						}

						@Override
						public Object getItem(int position) {
							return app.data.friends.get(phones.get(a * 6
									+ position));
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
				circlePageViews.put(circle.rid, pageviews);
			}
			if (circlePageViews.get(circle.rid).size() == 0) {
				vp_content.setAdapter(null);
				return;
			}
			if (vp_contentAdapter == null) {
				vp_contentAdapter = new PagerAdapter() {
					@Override
					public boolean isViewFromObject(View arg0, Object arg1) {
						return arg0 == arg1;
					}

					@Override
					public int getCount() {
						return circlePageViews.get(circle.rid).size();
					}

					@Override
					public void destroyItem(View container, int position,
							Object object) {
						if (circlePageViews.get(circle.rid).size() > position)
							((ViewPager) container).removeView(circlePageViews
									.get(circle.rid).get(position));
					}

					@Override
					public Object instantiateItem(View container, int position) {
						if (circlePageViews.get(circle.rid).get(position)
								.getParent() != null) {
							((ViewGroup) (circlePageViews.get(circle.rid).get(
									position).getParent())).removeAllViews();
						}
						((ViewPager) container).addView(circlePageViews.get(
								circle.rid).get(position));
						return circlePageViews.get(circle.rid).get(position);
					}

					@Override
					public void unregisterDataSetObserver(
							DataSetObserver observer) {
						if (observer != null) {
							super.unregisterDataSetObserver(observer);
						}
					}
				};
			}
			vp_content.setAdapter(vp_contentAdapter);
			vp_content.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int action = event.getAction();
					boolean flag = false;
					switch (action) {
					case MotionEvent.ACTION_MOVE:
						getListView().requestDisallowInterceptTouchEvent(true);
						flag = true;
						break;
					default:
						break;
					}
					return flag;
				}
			});
		}
	}

	class ButtonHolder {
		Button button;
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
		String flag = app.data.user.flag;
		params.putString("flag", flag);
		MCNetTools.ajax(getActivity(), API.MESSAGE_GET, params,
				MCHttpTools.SEND_POST, 5000, new ResponseListener() {

					@Override
					public void success(JSONObject data) {
						try {
							getAskFriends();
							System.out.println(data);
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

	private void getAskFriends() {
		Bundle params = new Bundle();
		params.putString("phone", app.data.user.phone);
		params.putString("accessKey", app.data.user.accessKey);
		MCNetTools.ajax(getActivity(), API.RELATION_GETASKFRIENDS, params,
				MCHttpTools.SEND_POST, 5000, new ResponseListener() {

					@Override
					public void success(JSONObject data) {
						System.out.println(data);
						try {
							showMsg(data.getString("失败原因"));
							return;
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							MCDataTools.saveNewFriends(data
									.getJSONArray("accounts"));
							if (app.data.newFriends.size() != 0) {
								showNewFriends = true;
								mFriendsHandler
										.sendEmptyMessage(NOTIFYDATASETCHANGED);
							}
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

	public class FriendsHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case NOTIFYDATASETCHANGED:
				initPosition();
				mFriendsAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
		}
	}
}
