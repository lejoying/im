package com.lejoying.wxgs.activity.mode.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.ReleaseImageAndTextActivity;
import com.lejoying.wxgs.activity.ReleaseVoiceActivity;
import com.lejoying.wxgs.activity.ReleaseVoteActivity;
import com.lejoying.wxgs.activity.SquareMessageDetail;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.view.RefreshableView;
import com.lejoying.wxgs.activity.view.RefreshableView.PullToRefreshListener;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.data.entity.Group;
import com.lejoying.wxgs.app.data.entity.GroupShare;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileResult;
import com.lejoying.wxgs.app.parser.JSONParser;

public class GroupShareFragment extends BaseFragment implements OnClickListener {

	private GroupShareAdapter groupShareAdapter = null;
	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;
	LayoutInflater mInflater;
	private View mContent;
	HorizontalScrollView gshare_scroll;
	LinearLayout gshare_scroll_ll;
	RelativeLayout gshare_send;
	ListView gshare_lv;
	RefreshableView refreshableView;
	PopupWindow pop;
	View popView;
	int height, width, dip;
	float density;

	int showImageWidth;
	int showImageHeight;

	Bitmap bm;

	public static String mCurrentGroupShareID = "";

	Group mCurrentGroup;
	int nowpage = 0;
	int pagesize = 30;

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	@Override
	public void onResume() {
		getGroupShares();
		mMainModeManager.handleMenu(true);
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mContent == null) {
			mInflater = inflater;
			mContent = mInflater.inflate(R.layout.f_groupshare, null);
			DisplayMetrics dm = new DisplayMetrics();
			getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
			density = dm.density;
			dip = (int) (40 * density + 0.5f);
			height = dm.heightPixels;
			width = dm.widthPixels;
			showImageWidth = width - (int) (22 * density + 0.5f);
			showImageHeight = 410;
			initLayout();
			mCurrentGroupShareID = app.data.currentGroup;
			// mCurrentGroupShareID = app.data.groups.get(0) == null ? ""
			// : app.data.groups.get(0);
			if (!"".equals(mCurrentGroupShareID)) {
				groupShareAdapter = new GroupShareAdapter(
						app.data.groupsMap.get(mCurrentGroupShareID).groupShares);
				gshare_lv.setAdapter(groupShareAdapter);
				initData();
				mCurrentGroup = app.data.groupsMap.get(mCurrentGroupShareID);
				getGroupShares();
			}
		}
		return mContent;
	}

	public void setGroupShare() {
		mCurrentGroup = app.data.groupsMap.get(mCurrentGroupShareID);
		nowpage = 0;
		if (!"".equals(mCurrentGroupShareID)) {
			groupShareAdapter = new GroupShareAdapter(
					app.data.groupsMap.get(mCurrentGroupShareID).groupShares);
			gshare_lv.setAdapter(groupShareAdapter);
			getGroupShares();
		}
	}

	public void notifyGroupShareViews() {
		if (groupShareAdapter != null) {
			groupShareAdapter.notifyDataSetChanged();
		}
	}

	void initLayout() {
		gshare_scroll = (HorizontalScrollView) mContent
				.findViewById(R.id.gshare_scroll);
		gshare_scroll_ll = (LinearLayout) mContent
				.findViewById(R.id.gshare_scroll_ll);
		gshare_send = (RelativeLayout) mContent
				.findViewById(R.id.rl_gshare_send);
		gshare_lv = (ListView) mContent.findViewById(R.id.gshare_lv);
		refreshableView = (RefreshableView) mContent
				.findViewById(R.id.refreshable_view);

		LayoutParams scrollParams = gshare_scroll.getLayoutParams();
		LayoutParams lvlParams = gshare_lv.getLayoutParams();

		gshare_scroll.setLayoutParams(scrollParams);
		gshare_lv.setLayoutParams(lvlParams);

		refreshableView.setOnRefreshListener(new PullToRefreshListener() {
			@Override
			public void onRefresh() {
				if (!"".equals(mCurrentGroupShareID)) {
					getGroupShares();
				}
			}
		}, 0);
		gshare_send.setOnClickListener(this);
		gshare_scroll_ll.setOnClickListener(this);
		initPopupWindow();
	}

	private void initPopupWindow() {
		final View imageandtext, voice, vote, activity, commodity, service, rl_imageandtext, rl_voice, rl_vote, rl_activity, rl_commodity, rl_service;
		popView = mInflater.inflate(R.layout.activity_release_option, null);
		pop = new PopupWindow(popView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		imageandtext = popView.findViewById(R.id.reloption_rl_imageandtext);
		voice = popView.findViewById(R.id.reloption_rl_voice);
		vote = popView.findViewById(R.id.reloption_rl_vote);
		activity = popView.findViewById(R.id.reloption_rl_activity);
		commodity = popView.findViewById(R.id.reloption_rl_commodity);
		service = popView.findViewById(R.id.reloption_rl_service);
		rl_imageandtext = popView.findViewById(R.id.rl_imageandtext);
		rl_voice = popView.findViewById(R.id.rl_voice);
		rl_vote = popView.findViewById(R.id.rl_vote);
		rl_activity = popView.findViewById(R.id.rl_activity);
		rl_commodity = popView.findViewById(R.id.rl_commodity);
		rl_service = popView.findViewById(R.id.rl_service);

		imageandtext.setOnTouchListener(new OnTouchListener() {
			GestureDetector viewDetector = new GestureDetector(getActivity(),
					new GestureDetector.SimpleOnGestureListener() {
						@Override
						public boolean onDown(MotionEvent e) {
							return true;
						}

						@Override
						public boolean onSingleTapUp(MotionEvent e) {
							startActivity(new Intent(getActivity(),
									ReleaseImageAndTextActivity.class));
							pop.dismiss();
							return true;
						}

					});

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					rl_imageandtext
							.setBackgroundResource(R.drawable.reloption_bk_sel);
					break;
				case MotionEvent.ACTION_UP:
					rl_imageandtext
							.setBackgroundResource(R.drawable.reloption_bk_nor);
					break;
				}
				return viewDetector.onTouchEvent(event);
			}
		});
		voice.setOnTouchListener(new OnTouchListener() {
			GestureDetector viewDetector = new GestureDetector(getActivity(),
					new GestureDetector.SimpleOnGestureListener() {

						@Override
						public boolean onDown(MotionEvent e) {
							return true;
						}

						@Override
						public boolean onSingleTapUp(MotionEvent e) {
							startActivity(new Intent(getActivity(),
									ReleaseVoiceActivity.class));
							pop.dismiss();
							return true;
						}

					});

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					rl_voice.setBackgroundResource(R.drawable.reloption_bk_sel);
					break;
				case MotionEvent.ACTION_UP:
					rl_voice.setBackgroundResource(R.drawable.reloption_bk_nor);
					break;
				}
				return viewDetector.onTouchEvent(event);
			}
		});
		vote.setOnTouchListener(new OnTouchListener() {
			GestureDetector viewDetector = new GestureDetector(getActivity(),
					new GestureDetector.SimpleOnGestureListener() {

						@Override
						public boolean onDown(MotionEvent e) {
							return true;
						}

						@Override
						public boolean onSingleTapUp(MotionEvent e) {
							startActivity(new Intent(getActivity(),
									ReleaseVoteActivity.class));
							pop.dismiss();
							return true;
						}

					});

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					rl_vote.setBackgroundResource(R.drawable.reloption_bk_sel);
					break;
				case MotionEvent.ACTION_UP:
					rl_vote.setBackgroundResource(R.drawable.reloption_bk_nor);
					break;
				}
				return viewDetector.onTouchEvent(event);
			}
		});
		activity.setOnTouchListener(new OnTouchListener() {
			GestureDetector viewDetector = new GestureDetector(getActivity(),
					new GestureDetector.SimpleOnGestureListener() {

						@Override
						public boolean onDown(MotionEvent e) {
							return true;
						}

						@Override
						public boolean onSingleTapUp(MotionEvent e) {
							// TODO pop.dismiss();
							Alert.showMessage("更多功能，敬请期待");
							return true;
						}

					});

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					rl_activity
							.setBackgroundResource(R.drawable.reloption_bk_sel);
					break;
				case MotionEvent.ACTION_UP:
					rl_activity
							.setBackgroundResource(R.drawable.reloption_bk_nor);
					break;
				}
				return viewDetector.onTouchEvent(event);
			}
		});
		commodity.setOnTouchListener(new OnTouchListener() {
			GestureDetector viewDetector = new GestureDetector(getActivity(),
					new GestureDetector.SimpleOnGestureListener() {

						@Override
						public boolean onDown(MotionEvent e) {
							return true;
						}

						@Override
						public boolean onSingleTapUp(MotionEvent e) {
							// TODO pop.dismiss();
							Alert.showMessage("更多功能，敬请期待");
							return true;
						}

					});

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					rl_commodity
							.setBackgroundResource(R.drawable.reloption_bk_sel);
					break;
				case MotionEvent.ACTION_UP:
					rl_commodity
							.setBackgroundResource(R.drawable.reloption_bk_nor);
					break;
				}
				return viewDetector.onTouchEvent(event);
			}
		});
		service.setOnTouchListener(new OnTouchListener() {
			GestureDetector viewDetector = new GestureDetector(getActivity(),
					new GestureDetector.SimpleOnGestureListener() {

						@Override
						public boolean onDown(MotionEvent e) {
							return true;
						}

						@Override
						public boolean onSingleTapUp(MotionEvent e) {
							// TODO pop.dismiss();
							Alert.showMessage("更多功能，敬请期待");
							return true;
						}

					});

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					rl_service
							.setBackgroundResource(R.drawable.reloption_bk_sel);
					break;
				case MotionEvent.ACTION_UP:
					rl_service
							.setBackgroundResource(R.drawable.reloption_bk_nor);
					break;
				}
				return viewDetector.onTouchEvent(event);
			}
		});

		popView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pop.dismiss();

			}
		});

	}

	void initData() {
		if (!"".equals(mCurrentGroupShareID)) {
			List<String> members = app.data.groupsMap.get(mCurrentGroupShareID).members;
			Map<String, Friend> groupFriends = app.data.groupFriends;
			for (int i = 0; i < members.size(); i++) {
				Friend friend = groupFriends.get(members.get(i));
				View child = mInflater.inflate(R.layout.view_child, null);
				final ImageView iv = (ImageView) child
						.findViewById(R.id.iv_child);
				app.fileHandler.getHeadImage(friend.head, friend.sex,
						new FileResult() {
							public void onResult(String where, Bitmap bitmap) {
								iv.setImageBitmap(bitmap);
							}
						});
				LinearLayout.LayoutParams headParams = (android.widget.LinearLayout.LayoutParams) iv
						.getLayoutParams();
				headParams.height = (int) (height * 0.05078125f);
				headParams.width = (int) (width * 0.09027778f);
				headParams.rightMargin = (int) (width * 0.027777778f);
				iv.setLayoutParams(headParams);
				gshare_scroll_ll.addView(child);
			}
		}
		// bm = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeResource(
		// mInflater.getContext().getResources(), R.drawable.background1),
		// showImageWidth, showImageHeight);
		// ArrayList<GroupShare> groupShares = app.data.groupsMap
		// .get(mCurrentGroupShareID).groupShares;
		// if (groupShares != null)
		// groupShareAdapter = new GroupShareAdapter(groupShares);
		// gshare_lv.setAdapter(groupShareAdapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_gshare_send:
			pop.showAtLocation((View) mContent.getParent(), Gravity.CENTER, 0,
					0);
			break;
		case R.id.gshare_scroll_ll:
			mMainModeManager.mChatGroupFragment.mStatus = ChatFriendFragment.CHAT_GROUP;
			mMainModeManager.mChatGroupFragment.mNowChatGroup = mCurrentGroup;
			mMainModeManager.showNext(mMainModeManager.mChatGroupFragment);
			break;
		default:
			break;
		}

	}

	class GroupShareAdapter extends BaseAdapter {

		ArrayList<String> groupShares = new ArrayList<String>();
		HashMap<String, GroupShare> groupSharesMap = app.data.groupsMap
				.get(mCurrentGroupShareID).groupSharesMap;

		public GroupShareAdapter(ArrayList<String> groupShares) {
			this.groupShares = groupShares;
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return groupShares.size();
		}

		@Override
		public Object getItem(int position) {
			return mCurrentGroup.groupShares.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			return groupSharesMap.get((getItem(position))).mType;
		}

		@Override
		public int getViewTypeCount() {
			return GroupShare.MAXTYPE_COUNT;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final String groupShareGsid = groupShares.get(position);
			final GroupShare groupShare = groupSharesMap.get(groupShareGsid);
			GroupShare lastGroupShare = groupSharesMap.get(groupShares
					.get(position == 0 ? position : position - 1));
			Friend friend = app.data.groupFriends.get(groupShare.phone);
			final GroupShareHolder groupShareHolder;
			final int mType = getItemViewType(position);
			switch (mType) {
			case GroupShare.MESSAGE_TYPE_IMAGETEXT:

				break;
			case GroupShare.MESSAGE_TYPE_VOICETEXT:

				break;
			case GroupShare.MESSAGE_TYPE_VOTE:

				break;

			default:
				break;
			}
			if (convertView == null) {
				groupShareHolder = new GroupShareHolder();
				convertView = mInflater.inflate(
						R.layout.fragment_groupshare_show, null);
				groupShareHolder.gshare_ll = (LinearLayout) convertView
						.findViewById(R.id.gshare_ll);
				groupShareHolder.gshare_head = (ImageView) convertView
						.findViewById(R.id.gshare_head);
				groupShareHolder.gshare_time_iv = (ImageView) convertView
						.findViewById(R.id.gshare_time_iv);
				groupShareHolder.gshare_bigpic = (ImageView) convertView
						.findViewById(R.id.gshare_bigpic);
				LinearLayout.LayoutParams shareImageParams = new LinearLayout.LayoutParams(
						showImageWidth, showImageHeight);
				int margin = (int) dp2px(1);
				shareImageParams.setMargins(margin, margin, margin, margin);
				groupShareHolder.gshare_bigpic
						.setLayoutParams(shareImageParams);
				groupShareHolder.gshare_name = (TextView) convertView
						.findViewById(R.id.gshare_name);
				groupShareHolder.gshare_time_tv = (TextView) convertView
						.findViewById(R.id.gshare_time_tv);
				groupShareHolder.gshare_content = (TextView) convertView
						.findViewById(R.id.gshare_content);
				groupShareHolder.gshare_praise = (TextView) convertView
						.findViewById(R.id.gshare_praise);
				groupShareHolder.gshare_comment = (TextView) convertView
						.findViewById(R.id.gshare_comment);
				groupShareHolder.gshare_date_tv = (TextView) convertView
						.findViewById(R.id.gshare_date_tv);
				LayoutParams headParams = groupShareHolder.gshare_head
						.getLayoutParams();
				headParams.height = (int) (height * 0.05078125f);
				headParams.width = (int) (width * 0.09027778f);
				groupShareHolder.gshare_head.setLayoutParams(headParams);
				groupShareHolder.gshare_date_tv.setTextSize(
						TypedValue.COMPLEX_UNIT_PX, width * 0.06944444f);
				convertView.setTag(groupShareHolder);
			} else {
				groupShareHolder = (GroupShareHolder) convertView.getTag();
			}
			String now = formatYearMonthDay(groupShare.time);
			String lastTime = formatYearMonthDay(lastGroupShare.time);
			if (!now.equals(lastTime)) {
				groupShareHolder.gshare_date_tv.setVisibility(View.VISIBLE);
				groupShareHolder.gshare_date_tv.setText(now);
			} else {
				groupShareHolder.gshare_date_tv.setVisibility(View.GONE);
			}
			if (groupShare.content.images.size() > 0) {
				final String fileName = groupShare.content.images.get(0);
				app.fileHandler.getThumbnail(fileName, "", showImageWidth,
						showImageHeight, new FileResult() {

							@Override
							public void onResult(String where, Bitmap bitmap) {
								groupShareHolder.gshare_bigpic
										.setImageBitmap(app.fileHandler.bitmaps
												.get(fileName));
							}
						});
			}
			groupShareHolder.gshare_name.setText(friend.nickName);
			groupShareHolder.gshare_time_tv
					.setText(formatHourMinute(groupShare.time));
			groupShareHolder.gshare_praise.setText(groupShare.praiseusers
					.size() + "");
			groupShareHolder.gshare_comment.setText(groupShare.comments.size()
					+ "");
			groupShareHolder.gshare_content.setText(groupShare.content.text);
			app.fileHandler.getHeadImage(friend.head, friend.sex,
					new FileResult() {

						public void onResult(String where, Bitmap bitmap) {
							groupShareHolder.gshare_head.setImageBitmap(bitmap);
						}
					});

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(getActivity(),
							SquareMessageDetail.class);
					intent.putExtra("content", groupShare);
					intent.putExtra("type", "share");
					// startActivity(intent);
				}
			});
			return convertView;
		}

		class GroupShareHolder {
			LinearLayout gshare_ll;
			ImageView gshare_head, gshare_time_iv, gshare_bigpic;
			TextView gshare_name, gshare_time_tv, gshare_content,
					gshare_praise, gshare_comment, gshare_date_tv;
		}
	}

	private float dp2px(float dp) {
		float px = getResources().getDisplayMetrics().density * dp + 0.5f;
		return px;
	}

	@SuppressLint("SimpleDateFormat")
	public String formatYearMonthDay(long timeMillis) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
		Date date = new Date(timeMillis);
		String mTime = simpleDateFormat.format(date);
		return mTime;
	}

	@SuppressLint("SimpleDateFormat")
	public String formatHourMinute(long timeMillis) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
		Date date = new Date(timeMillis);
		String mTime = simpleDateFormat.format(date);
		return mTime;
	}

	public void getGroupShares() {
		app.networkHandler.connection(new CommonNetConnection() {

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.SHARE_GETSHARES;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("gid", mCurrentGroupShareID);
				params.put("nowpage", nowpage + "");
				params.put("pagesize", pagesize + "");
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
				try {
					final ArrayList<GroupShare> shares = JSONParser
							.generateSharesFromJSON(jData
									.getJSONArray("shares"));
					app.dataHandler.exclude(new Modification() {

						@Override
						public void modifyData(Data data) {
							ArrayList<String> groupShares = data.groupsMap
									.get(mCurrentGroupShareID).groupShares;
							HashMap<String, GroupShare> groupSharesMap = data.groupsMap
									.get(mCurrentGroupShareID).groupSharesMap;
							int index = 0;
							for (int i = 0; i < shares.size(); i++) {
								GroupShare shareGroup = shares.get(i);
								if (!groupShares.contains(shareGroup.gsid)) {
									if (nowpage == 0) {
										groupShares.add(index, shareGroup.gsid);
										index++;
									} else if (nowpage > 0) {
										groupShares.add(shareGroup.gsid);
									}
								}
								groupSharesMap.put(shareGroup.gsid, shareGroup);
							}
						}

						@Override
						public void modifyUI() {
							refreshableView.finishRefreshing();
							if (groupShareAdapter != null) {
								groupShareAdapter.notifyDataSetChanged();
							}
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
}