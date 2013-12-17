package com.lejoying.mc;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.transition.ChangeBounds;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.mc.api.API;
import com.lejoying.mc.entity.Circle;
import com.lejoying.mc.entity.Friend;
import com.lejoying.mc.entity.Message;
import com.lejoying.mc.fragment.ChatFragment;
import com.lejoying.mc.fragment.FriendsFragment;
import com.lejoying.mc.fragment.ShareFragment;
import com.lejoying.mc.service.handler.MainServiceHandler;
import com.lejoying.mc.service.handler.NetworkHandler.NetworkStatusListener;
import com.lejoying.mc.utils.MCDataTools;
import com.lejoying.mc.utils.MCImageTools;
import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.mc.utils.MCStaticData;

public class MainActivity extends BaseFragmentActivity {

	private final int START_CHAT = 0x01;

	private Context mContext;
	private LayoutInflater mInflater;

	private boolean isStartToChat;

	private Handler mHandler;

	private ChatFragment mChatFragment;

	private Bitmap head;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout._main);
		getUser();
		mContext = this;
		mInflater = getLayoutInflater();

		head = MCImageTools.getCircleBitmap(BitmapFactory.decodeResource(
				mContext.getResources(), R.drawable.xiaohei), true, 5,
				Color.WHITE);
		mHandler = new MainHandler();
		mChatFragment = new ChatFragment();

	}

	@Override
	public Fragment setFirstPreview() {
		return new FriendsFragment();
	}

	@Override
	protected int setBackground() {
		// TODO Auto-generated method stub
		return R.drawable.card_background;
	}

	private void getUser() {
		Bundle params = new Bundle();
		params.putString("phone", MCDataTools.getLoginedUser(this).getPhone());
		params.putString("accessKey", MCDataTools.getLoginedUser(this)
				.getAccessKey());
		params.putString("target", MCDataTools.getLoginedUser(this).getPhone());
		startNetwork(API.ACCOUNT_GET, params, false,
				new NetworkStatusListener() {
					@Override
					public void onReceive(int STATUS, String log) {
						switch (STATUS) {
						case MainServiceHandler.STATUS_NETWORK_SUCCESS:
							getCirclesAndFriends();
							getMessages();
							break;
						case MainServiceHandler.STATUS_NETWORK_UNSUCCESS:
							System.out.println("获取用户失败");
							Intent intent = new Intent(MainActivity.this,
									LoginActivity.class);
							startActivity(intent);
							MainActivity.this.finish();
							break;
						case MainServiceHandler.STATUS_NETWORK_NOINTERNET:
							MCNetTools.showMsg(MainActivity.this,
									getString(R.string.app_nointernet));
							break;
						case MainServiceHandler.STATUS_NETWORK_FAILED:
							getUser();
							break;

						default:
							break;
						}
					}
				});
	}

	private void getCirclesAndFriends() {
		Bundle params = new Bundle();
		params.putString("phone", MCDataTools.getLoginedUser(this).getPhone());
		params.putString("accessKey", MCDataTools.getLoginedUser(this)
				.getAccessKey());
		startNetwork(API.RELATION_GETCIRCLESANDFRIENDS, params, false,
				new NetworkStatusListener() {
					@Override
					public void onReceive(int STATUS, String log) {
						switch (STATUS) {
						case MainServiceHandler.STATUS_NETWORK_SUCCESS:
							createFriendView();
							break;
						case MainServiceHandler.STATUS_NETWORK_UNSUCCESS:
							System.out.println("获取好友圈失败");
							Intent intent = new Intent(MainActivity.this,
									LoginActivity.class);
							startActivity(intent);
							MainActivity.this.finish();
							break;
						case MainServiceHandler.STATUS_NETWORK_NOINTERNET:
							MCNetTools.showMsg(MainActivity.this,
									getString(R.string.app_nointernet));
							break;
						case MainServiceHandler.STATUS_NETWORK_FAILED:
							getCirclesAndFriends();
							break;

						default:
							break;
						}
					}
				});

	}

	private void getMessages() {
		Bundle params = new Bundle();
		params.putString("phone", MCDataTools.getLoginedUser(this).getPhone());
		params.putString("accessKey", MCDataTools.getLoginedUser(this)
				.getAccessKey());
		String flag = MCDataTools.getLoginedUser(this).getFlag();
		params.putString("flag", "0");
		startNetwork(API.MESSAGE_GET, params, false,
				new NetworkStatusListener() {
					@Override
					public void onReceive(int STATUS, String log) {
						switch (STATUS) {
						case MainServiceHandler.STATUS_NETWORK_SUCCESS:
							createMessageView();
							break;
						case MainServiceHandler.STATUS_NETWORK_UNSUCCESS:
							System.out.println("获取消息失败");
							Intent intent = new Intent(MainActivity.this,
									LoginActivity.class);
							startActivity(intent);
							MainActivity.this.finish();
							break;
						case MainServiceHandler.STATUS_NETWORK_NOINTERNET:
							MCNetTools.showMsg(MainActivity.this,
									getString(R.string.app_nointernet));
							break;
						case MainServiceHandler.STATUS_NETWORK_FAILED:
							getMessages();
							break;

						default:
							break;
						}
					}
				});
	}

	private void createMessageView() {
		new Thread() {
			public void run() {
				List<Message> messages = MCDataTools.getMessages(mContext, 5);
				List<View> messagesView = new ArrayList<View>();
				messagesView.add(mInflater.inflate(R.layout.f_margin, null));
				for (final Message message : messages) {
					final Friend friend = message.getFriend();
					View messageItem = mInflater.inflate(
							R.layout.f_messages_item, null);
					ImageView iv_head = (ImageView) messageItem
							.findViewById(R.id.iv_head);
					TextView tv_lastchat = (TextView) messageItem
							.findViewById(R.id.tv_lastchat);
					TextView tv_nickname = (TextView) messageItem
							.findViewById(R.id.tv_nickname);
					iv_head.setImageBitmap(head);
					tv_lastchat.setText(message.getContent());
					tv_nickname.setText(message.getFriend().getNickName());
					messageItem.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							if (!isStartToChat) {
								isStartToChat = true;
								mHandler.obtainMessage(START_CHAT, friend)
										.sendToTarget();
							}
						}
					});
					messagesView.add(messageItem);
				}
				Button btn_more = (Button) mInflater.inflate(R.layout.f_button,
						null);
				btn_more.setText(getString(R.string.btn_moremessages));
				messagesView.add(btn_more);
				MCStaticData.messagesViewList = messagesView;
				getNotifyListener().notifyDataChanged(
						NotifyListener.NOTIFY_MESSAGEANDFRIEND);
			}
		}.start();
	}

	private void createFriendView() {
		new Thread() {
			public void run() {
				List<Circle> circles = MCDataTools.getCircles(mContext);
				List<View> circlesView = new ArrayList<View>();
				for (Circle circle : circles) {
					final View group = (RelativeLayout) mInflater.inflate(
							R.layout.f_group_panel, null);
					TextView tv_groupname = (TextView) group
							.findViewById(R.id.tv_groupname);
					tv_groupname.setText(circle.getName());

					final List<Friend> friends = circle.getFriends();
					final int pagecount = friends.size() % 6 == 0 ? friends
							.size() / 6 : friends.size() / 6 + 1;
					final List<View> pageviews = new ArrayList<View>();

					for (int i = 0; i < pagecount; i++) {
						final int a = i;
						BaseAdapter gridpageAdapter = new BaseAdapter() {
							@Override
							public View getView(final int position,
									View convertView, final ViewGroup parent) {
								RelativeLayout rl_gridpage_item = (RelativeLayout) mInflater
										.inflate(
												R.layout.f_group_panelitem_gridpageitem_user,
												null);
								ImageView iv_head = (ImageView) rl_gridpage_item
										.findViewById(R.id.iv_head);
								TextView tv_nickname = (TextView) rl_gridpage_item
										.findViewById(R.id.tv_nickname);

								iv_head.setImageBitmap(head);
								tv_nickname.setText(friends.get(
										a * 6 + position).getNickName());

								rl_gridpage_item
										.setOnClickListener(new OnClickListener() {

											@Override
											public void onClick(View v) {
												mHandler.obtainMessage(
														START_CHAT,
														friends.get(a * 6
																+ position))
														.sendToTarget();
											}
										});

								return rl_gridpage_item;
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
								R.layout.f_group_panelitem_gridpage, null);
						gridpage.setAdapter(gridpageAdapter);
						pageviews.add(gridpage);
					}

					ViewPager vp_content = (ViewPager) group
							.findViewById(R.id.vp_content);
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
							((ViewPager) container).removeView(pageviews
									.get(position));
						}

						@Override
						public Object instantiateItem(View container,
								int position) {
							((ViewPager) container).addView(pageviews
									.get(position));
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

					LinearLayout ll_bottom = (LinearLayout) group
							.findViewById(R.id.ll_bottom);
					final List<ImageView> page_ivs = new ArrayList<ImageView>();

					for (int i = 0; i < pageviews.size(); i++) {
						ImageView iv = new ImageView(mContext);
						if (i == 0) {
							iv.setImageResource(R.drawable.point_white);
						} else {
							iv.setImageResource(R.drawable.point_blank);
						}

						page_ivs.add(iv);
						ll_bottom.addView(iv);
					}

					vp_content
							.setOnPageChangeListener(new OnPageChangeListener() {

								@Override
								public void onPageSelected(int arg0) {
									page_ivs.get(arg0).setImageResource(
											R.drawable.point_white);
									if (arg0 - 1 >= 0) {
										page_ivs.get(arg0 - 1)
												.setImageResource(
														R.drawable.point_blank);
									}
									if (arg0 + 1 < page_ivs.size()) {
										page_ivs.get(arg0 + 1)
												.setImageResource(
														R.drawable.point_blank);
									}
								}

								@Override
								public void onPageScrolled(int arg0,
										float arg1, int arg2) {
								}

								@Override
								public void onPageScrollStateChanged(int arg0) {
								}
							});

					vp_content.setAdapter(vp_contentAdapter);
					circlesView.add(group);
				}

				Button btn_newGroup = (Button) mInflater.inflate(
						R.layout.f_button, null);
				btn_newGroup.setText(getString(R.string.btn_newgroup));
				Button btn_findMore = (Button) mInflater.inflate(
						R.layout.f_button, null);
				btn_findMore.setText(getString(R.string.btn_findmorefriend));
				circlesView.add(btn_newGroup);
				circlesView.add(btn_findMore);
				circlesView.add(mInflater.inflate(R.layout.f_margin, null));
				MCStaticData.circlesViewList = circlesView;
				getNotifyListener().notifyDataChanged(
						NotifyListener.NOTIFY_MESSAGEANDFRIEND);
			}
		}.start();

	}

	private void startToChat(Friend friend) {
		MCStaticData.chatMessagesViewList.clear();
		replaceToContent(mChatFragment, true);
		for (int i = 0; i < 100; i++) {
			TextView tv = new TextView(mContext);
			tv.setText("测试条目" + i);
			MCStaticData.chatMessagesViewList.add(tv);
		}
		getNotifyListener().notifyDataChanged(
				NotifyListener.NOTIFY_MESSAGEANDFRIEND);
	}

	class MainHandler extends Handler {
		@Override
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case START_CHAT:
				if (msg.obj != null) {
					startToChat((Friend) msg.obj);
					isStartToChat = false;
				}
				break;

			default:
				break;
			}
		}
	}

}
