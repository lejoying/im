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
import android.graphics.Color;
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
import com.lejoying.wxgs.R.id;
import com.lejoying.wxgs.activity.BusinessCardActivity;
import com.lejoying.wxgs.activity.ChatActivity;
import com.lejoying.wxgs.activity.DetailsActivity;
import com.lejoying.wxgs.activity.GroupInformationActivity;
import com.lejoying.wxgs.activity.ReleaseImageAndTextActivity;
import com.lejoying.wxgs.activity.ReleaseVoiceActivity;
import com.lejoying.wxgs.activity.ReleaseVoteActivity;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.view.RefreshableView;
import com.lejoying.wxgs.activity.view.RefreshableView.PullToRefreshListener;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.data.entity.Comment;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.data.entity.Group;
import com.lejoying.wxgs.app.data.entity.GroupShare;
import com.lejoying.wxgs.app.data.entity.GroupShare.VoteContent;
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
		if (!"".equals(mCurrentGroupShareID)) {
			getGroupShares();
		}
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
				if (app.data.groupsMap.get(mCurrentGroupShareID).groupShares == null) {
					app.dataHandler.exclude(new Modification() {

						@Override
						public void modifyData(Data data) {
							data.groupsMap.get(mCurrentGroupShareID).groupShares = new ArrayList<String>();

						}
					});
				}
				groupShareAdapter = new GroupShareAdapter(
						app.data.groupsMap.get(mCurrentGroupShareID).groupShares);
				gshare_lv.setAdapter(groupShareAdapter);
				showGroupMembers();
				mCurrentGroup = app.data.groupsMap.get(mCurrentGroupShareID);
				getGroupShares();
			}
		}
		return mContent;
	}

	public void setGroupShare() {
		mCurrentGroup = app.data.groupsMap.get(mCurrentGroupShareID);
		nowpage = 0;
		showGroupMembers();
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

	void showGroupMembers() {
		gshare_scroll_ll.removeAllViews();
		if (!"".equals(mCurrentGroupShareID)) {
			List<String> members = app.data.groupsMap.get(mCurrentGroupShareID).members;
			if (members.size() > 0) {
				Map<String, Friend> groupFriends = app.data.groupFriends;
				for (int i = 0; i < members.size(); i++) {
					Friend friend = groupFriends.get(members.get(i));
					if (friend != null) {
						View child = mInflater.inflate(R.layout.view_child,
								null);
						final ImageView iv = (ImageView) child
								.findViewById(R.id.iv_child);
						app.fileHandler.getHeadImage(friend.head, friend.sex,
								new FileResult() {
									public void onResult(String where,
											Bitmap bitmap) {
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
			Intent intent = new Intent(getActivity(), ChatActivity.class);
			intent.putExtra("status", ChatActivity.CHAT_GROUP);
			intent.putExtra("gid", mCurrentGroupShareID);
			startActivity(intent);
			// mMainModeManager.mChatGroupFragment.mStatus =
			// ChatFriendFragment.CHAT_GROUP;
			// mMainModeManager.mChatGroupFragment.mNowChatGroup =
			// mCurrentGroup;
			// mMainModeManager.showNext(mMainModeManager.mChatGroupFragment);
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
			groupSharesMap = app.data.groupsMap.get(mCurrentGroupShareID).groupSharesMap;
			if (groupSharesMap == null) {
				groupSharesMap = new HashMap<String, GroupShare>();
				app.dataHandler.exclude(new Modification() {

					@Override
					public void modifyData(Data data) {
						data.groupsMap.get(mCurrentGroupShareID).groupSharesMap = groupSharesMap;
					}
				});
			}
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
				shareImageParams.setMargins(0, margin, 0, margin);
				groupShareHolder.gshare_bigpic
						.setLayoutParams(shareImageParams);
				groupShareHolder.gshare_name = (TextView) convertView
						.findViewById(R.id.gshare_name);
				groupShareHolder.gshare_time_tv = (TextView) convertView
						.findViewById(R.id.gshare_time_tv);
				groupShareHolder.llImageTextView = (LinearLayout) convertView
						.findViewById(R.id.ll_imagetext);
				groupShareHolder.gshare_content = (TextView) convertView
						.findViewById(R.id.gshare_imageContent);

				groupShareHolder.llVoiceTextView = (LinearLayout) convertView
						.findViewById(R.id.ll_voicetext);
				groupShareHolder.rlShowVoice = (RelativeLayout) convertView
						.findViewById(R.id.rl_showVoice);
				groupShareHolder.rlShowVoice.setLayoutParams(shareImageParams);
				groupShareHolder.voiceContent = (TextView) convertView
						.findViewById(R.id.gshare_voiceContent);

				// 0.7458333333333333
				int voiceRadius = (int) (showImageHeight * 0.7458333333333333f / 2);
				groupShareHolder.showVoice = (ImageView) convertView
						.findViewById(R.id.iv_voice);
				RelativeLayout.LayoutParams showVoiceParams = new RelativeLayout.LayoutParams(
						voiceRadius * 2, voiceRadius * 2);
				showVoiceParams.addRule(RelativeLayout.CENTER_IN_PARENT);
				groupShareHolder.showVoice.setLayoutParams(showVoiceParams);

				// 0.6458333333333333
				groupShareHolder.showVoiceTime = (TextView) convertView
						.findViewById(R.id.tv_voiceTime);
				RelativeLayout.LayoutParams showVoiceTimeParams = new RelativeLayout.LayoutParams(
						groupShareHolder.showVoiceTime.getLayoutParams());
				showVoiceTimeParams.topMargin = (int) (showImageHeight * 0.6458333333333333f);
				showVoiceTimeParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
				groupShareHolder.showVoiceTime
						.setLayoutParams(showVoiceTimeParams);
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

				groupShareHolder.praiseIcon = (ImageView) convertView
						.findViewById(R.id.gshare_praise_icon);

				groupShareHolder.commentIcon = (ImageView) convertView
						.findViewById(R.id.gshare_comment_icon);

				groupShareHolder.llVoteView = (LinearLayout) convertView
						.findViewById(R.id.ll_vote);
				groupShareHolder.voteTitle = (TextView) convertView
						.findViewById(R.id.voteTitle);
				groupShareHolder.llVoteOptions = (LinearLayout) convertView
						.findViewById(R.id.ll_voteOptions);
				// 0.1552967276760954
				RelativeLayout rl_voteTitile = (RelativeLayout) convertView
						.findViewById(R.id.rl_voteTitile);
				rl_voteTitile.setBackgroundColor(Color.WHITE);
				LinearLayout.LayoutParams rl_voteTitileParams = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT,
						(int) (showImageWidth * 0.1552967276760954f));
				rl_voteTitile.setLayoutParams(rl_voteTitileParams);
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
				String currentTime = formatYearMonthDay(System
						.currentTimeMillis());
				if (!lastTime.equals(currentTime) && position == 0) {
					groupShareHolder.gshare_date_tv.setVisibility(View.VISIBLE);
					groupShareHolder.gshare_date_tv.setText(lastTime);
				} else {
					groupShareHolder.gshare_date_tv.setVisibility(View.GONE);
				}
			}
			final int mType = getItemViewType(position);
			switch (mType) {
			case GroupShare.MESSAGE_TYPE_IMAGETEXT:
				groupShareHolder.llImageTextView.setVisibility(View.VISIBLE);
				groupShareHolder.llVoiceTextView.setVisibility(View.GONE);
				groupShareHolder.llVoteView.setVisibility(View.GONE);
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
				groupShareHolder.gshare_content
						.setText(groupShare.content.text);
				break;
			case GroupShare.MESSAGE_TYPE_VOICETEXT:
				groupShareHolder.llImageTextView.setVisibility(View.GONE);
				groupShareHolder.llVoiceTextView.setVisibility(View.VISIBLE);
				groupShareHolder.llVoteView.setVisibility(View.GONE);
				groupShareHolder.voiceContent.setText(groupShare.content.text);
				int second = (int) (groupShare.content.voices.get(0).time / 1000);
				String showVoiceTime = second > 9 ? second + "" : "0" + second;
				groupShareHolder.showVoiceTime.setText("00:" + showVoiceTime);
				break;
			case GroupShare.MESSAGE_TYPE_VOTE:
				groupShareHolder.llImageTextView.setVisibility(View.GONE);
				groupShareHolder.llVoiceTextView.setVisibility(View.GONE);
				groupShareHolder.llVoteView.setVisibility(View.VISIBLE);
				groupShareHolder.voteTitle.setText("投票主题 :"
						+ groupShare.content.title);
				generateVoteOptionsViews(groupShareHolder.llVoteOptions,
						groupShare.content.voteoptions);
				break;

			default:
				break;
			}

			groupShareHolder.gshare_name.setText(friend.nickName);
			groupShareHolder.gshare_time_tv
					.setText(formatHourMinute(groupShare.time));
			groupShareHolder.praiseIcon
					.setImageResource(R.drawable.gshare_praise);
			int praiseCount = groupShare.praiseusers.size();
			groupShareHolder.gshare_praise.setText(praiseCount + "");
			groupShareHolder.praiseIcon.setTag(false);
			if (praiseCount != 0) {
				ArrayList<String> praiseUsers = groupShare.praiseusers;
				for (int i = 0; i < praiseUsers.size(); i++) {
					if (app.data.user.phone.equals(praiseUsers.get(i))) {
						groupShareHolder.praiseIcon
								.setImageResource(R.drawable.gshare_praised);
						groupShareHolder.praiseIcon.setTag(true);
						break;
					}
				}
			}
			groupShareHolder.praiseIcon
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							boolean praiseFlag = (Boolean) groupShareHolder.praiseIcon
									.getTag();
							if (praiseFlag) {
								addPraise(false, groupShareGsid,
										groupShareHolder.praiseIcon,
										groupShareHolder.gshare_praise);
							} else {
								addPraise(true, groupShareGsid,
										groupShareHolder.praiseIcon,
										groupShareHolder.gshare_praise);
							}
							// TODO
						}
					});
			groupShareHolder.commentIcon
					.setImageResource(R.drawable.gshare_comment);
			int commentCount = groupShare.comments.size();
			if (commentCount != 0) {
				ArrayList<Comment> comments = groupShare.comments;
				for (int i = 0; i < comments.size(); i++) {
					if (comments.get(i).phone.equals(app.data.user.phone)) {
						groupShareHolder.commentIcon
								.setImageResource(R.drawable.gshare_commented);
						break;
					}
				}
			}
			groupShareHolder.gshare_comment.setText(commentCount + "");
			app.fileHandler.getHeadImage(friend.head, friend.sex,
					new FileResult() {

						public void onResult(String where, Bitmap bitmap) {
							groupShareHolder.gshare_head.setImageBitmap(bitmap);
						}
					});
			groupShareHolder.gshare_head
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							Intent intent = new Intent(getActivity(),
									BusinessCardActivity.class);
							if (groupShare.phone.equals(app.data.user.phone)) {
								intent.putExtra("type",
										BusinessCardActivity.TYPE_SELF);
							} else if (app.data.friends.get(groupShare.phone) != null) {
								intent.putExtra("type",
										BusinessCardActivity.TYPE_FRIEND);
							} else {
								intent.putExtra("type",
										BusinessCardActivity.TYPE_TEMPFRIEND);
								intent.putExtra("friend", app.data.groupFriends
										.get(groupShare.phone));
							}
							intent.putExtra("phone", groupShare.phone);
							startActivity(intent);
							// if (app.data.friends.get(groupShare.phone) !=
							// null) {
							// mMainModeManager.mBusinessCardFragment.mStatus =
							// BusinessCardFragment.SHOW_FRIEND;
							// mMainModeManager.mBusinessCardFragment.mShowFriend
							// = app.data.friends
							// .get(groupShare.phone);
							// mMainModeManager
							// .showNext(mMainModeManager.mBusinessCardFragment);
							// } else if (groupShare.phone
							// .equals(app.data.user.phone)) {
							// mMainModeManager.mBusinessCardFragment.mStatus =
							// BusinessCardFragment.SHOW_SELF;
							// mMainModeManager
							// .showNext(mMainModeManager.mBusinessCardFragment);
							// } else {
							// mMainModeManager.mBusinessCardFragment.mStatus =
							// BusinessCardFragment.SHOW_TEMPFRIEND;
							// mMainModeManager.mBusinessCardFragment.mShowFriend
							// = app.data.groupFriends
							// .get(groupShare.phone);
							// mMainModeManager
							// .showNext(mMainModeManager.mBusinessCardFragment);
							// }
						}
					});
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(getActivity(),
							DetailsActivity.class);
					intent.putExtra("gsid", groupShare.gsid);
					intent.putExtra("type", "share");
					startActivity(intent);
				}
			});
			return convertView;
		}

		class GroupShareHolder {
			LinearLayout gshare_ll;
			ImageView gshare_head, gshare_time_iv, gshare_bigpic;
			TextView gshare_name, gshare_time_tv, gshare_content,
					gshare_praise, gshare_comment, gshare_date_tv;

			LinearLayout llImageTextView;
			LinearLayout llVoiceTextView;
			RelativeLayout rlShowVoice;
			TextView voiceContent;
			ImageView showVoice;
			TextView showVoiceTime;

			ImageView praiseIcon;
			ImageView commentIcon;

			LinearLayout llVoteView;
			TextView voteTitle;
			LinearLayout llVoteOptions;
		}
	}

	private void addPraise(final boolean flag, final String gsid,
			final ImageView praiseView, final TextView praiseCountView) {

		app.networkHandler.connection(new CommonNetConnection() {
			@Override
			public void success(JSONObject jData) {
				app.dataHandler.exclude(new Modification() {

					@Override
					public void modifyData(Data data) {
						GroupShare groupShare = data.groupsMap
								.get(mCurrentGroupShareID).groupSharesMap
								.get(gsid);
						if (flag) {
							groupShare.praiseusers.add(app.data.user.phone);
						} else {
							groupShare.praiseusers.remove(app.data.user.phone);
						}
					}

					@Override
					public void modifyUI() {
						if (flag) {
							praiseView.setTag(true);
							praiseView
									.setImageResource(R.drawable.gshare_praised);
							praiseCountView.setText((Integer
									.valueOf(praiseCountView.getText()
											.toString()) + 1)
									+ "");
						} else {
							praiseView.setTag(false);
							praiseView
									.setImageResource(R.drawable.gshare_praise);
							praiseCountView.setText((Integer
									.valueOf(praiseCountView.getText()
											.toString()) - 1)
									+ "");
						}
					}
				});
			}

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.SHARE_ADDPRAISE;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("gid", mCurrentGroupShareID);
				params.put("gsid", gsid);
				params.put("option", flag + "");
				settings.params = params;
			}
		});
	}

	private void generateVoteOptionsViews(LinearLayout llVoteOptions,
			ArrayList<VoteContent> voteOptions) {
		// showImageWidth
		// option height scale 0.1277777777777778
		llVoteOptions.removeAllViews();
		float voteCount = 0;
		for (int i = 0; i < voteOptions.size(); i++) {
			voteCount += voteOptions.get(i).voteUsers.size();
		}
		for (int i = 0; i < voteOptions.size(); i++) {
			View v = mInflater.inflate(R.layout.fragment_groupshare_vote_item,
					null);
			RelativeLayout voteOptionContent = (RelativeLayout) v
					.findViewById(R.id.rl_voteOptionContent);
			LinearLayout.LayoutParams voteOptionContentParams = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT,
					(int) (showImageWidth * 0.1277777777777778f));
			voteOptionContentParams.topMargin = (int) (0.0233983286908078f * showImageWidth);
			voteOptionContent.setLayoutParams(voteOptionContentParams);
			voteOptionContent.setBackgroundColor(Color.argb(26, 255, 255, 255));
			TextView voteOption = (TextView) v
					.findViewById(id.tv_voteOptionContent);
			voteOption.setText(voteOptions.get(i).content);
			TextView voteOptionNumber = (TextView) v
					.findViewById(R.id.tv_voteOptionNumber);
			voteOptionNumber.setText(voteOptions.get(i).voteUsers.size() + "票");
			TextView voteOptionNumberPlan = (TextView) v
					.findViewById(R.id.tv_voteOptionNumPlan);
			if (voteOptions.get(i).voteUsers.size() == 0) {
				voteOptionNumberPlan.setVisibility(View.GONE);
			} else {
				RelativeLayout.LayoutParams voteNumberParams = new RelativeLayout.LayoutParams(
						(int) ((voteOptions.get(i).voteUsers.size() / voteCount) * showImageWidth),
						(int) (0.025f * showImageWidth));
				voteNumberParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				voteOptionNumberPlan.setLayoutParams(voteNumberParams);
				voteOptionNumberPlan.setBackgroundColor(Color.WHITE);
			}
			llVoteOptions.addView(v);
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
							if (groupShares == null) {
								data.groupsMap.get(mCurrentGroupShareID).groupShares = new ArrayList<String>();
								groupShares = data.groupsMap
										.get(mCurrentGroupShareID).groupShares;
							}
							if (groupSharesMap == null) {
								data.groupsMap.get(mCurrentGroupShareID).groupSharesMap = new HashMap<String, GroupShare>();
								groupSharesMap = data.groupsMap
										.get(mCurrentGroupShareID).groupSharesMap;
							}
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
							shares.clear();
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