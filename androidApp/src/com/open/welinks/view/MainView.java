package com.open.welinks.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.open.lib.MyLog;
import com.open.lib.TouchView;
import com.open.lib.viewbody.BodyCallback;
import com.open.lib.viewbody.PagerBody;
import com.open.welinks.NearbyActivity;
import com.open.welinks.R;
import com.open.welinks.controller.MainController;
import com.open.welinks.customView.ControlProgress;
import com.open.welinks.model.Data;

public class MainView {

	public String tag = "MainView";
	public MyLog log = new MyLog(tag, true);

	public Data data = Data.getInstance();

	public ImageLoader imageLoader = ImageLoader.getInstance();

	public DisplayMetrics displayMetrics;

	public MainController thisController;
	public MainView thisView;
	public Context context;
	public Activity thisActivity;

	public SquareSubView squareSubView;
	public MessagesSubView messagesSubView;
	public FriendsSubView friendsSubView;
	public MeSubView meSubView;

	public LayoutInflater mInflater;

	public RelativeLayout friendsMenuView;
	public RelativeLayout messagesMenuView;
	public RelativeLayout meMenuView;

	public RelativeLayout squareMenuView;
	public RelativeLayout shareMenuView;
	public RelativeLayout messages_friends_me_menuView;

	public ImageView squareMenuImageView;
	public ImageView shareMenuImageView;
	public ImageView messages_friends_me_menuImageView;

	public ImageView messages_friends_me_pager_indicator;
	public ImageView main_pager_indicator;

	public TouchView friendsView;
	public TouchView messagesView;
	public TouchView meView;

	public RelativeLayout messages_friends_me_View;
	public RelativeLayout shareView;
	public RelativeLayout squareView;

	public RelativeLayout title_messages_friends_me;
	public RelativeLayout title_share;
	public RelativeLayout title_square;

	public RelativeLayout main_container;

	public PagerBody messages_friends_me_PagerBody;

	public PagerBody mainPagerBody;

	public ImageView scannerCodeView;
	public TextView userTopbarNameView;

	public RelativeLayout userTopbarNameParentView;

	public ViewManage viewManage = ViewManage.getInstance();

	public ControlProgress controlProgress;
	public View controlProgressView;

	public float textSize;

	public class ActivityStatus {
		public float SHARE = 0, MESSAGES = 1.0f, FRIENDS = 1.1f, ME = 1.2f;
		public float subState = MESSAGES;
		public float state = SHARE;
	}

	public ActivityStatus activityStatus = new ActivityStatus();

	public MainView(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;
		this.thisView = this;

	}

	public ImageView botton;

	public void initViews() {
		viewManage.initialize(thisActivity);
		mInflater = thisActivity.getLayoutInflater();
		displayMetrics = new DisplayMetrics();

		MyBodyCallback myBodyCallback = new MyBodyCallback();

		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		textSize = displayMetrics.scaledDensity * 18 + 0.5f;

		thisActivity.setContentView(R.layout.activity_welinks);

		main_container = (RelativeLayout) thisActivity.findViewById(R.id.main_container);

		squareView = (RelativeLayout) mInflater.inflate(R.layout.view_main_square, null);
		shareView = (RelativeLayout) mInflater.inflate(R.layout.view_main_share, null);
		messages_friends_me_View = (RelativeLayout) mInflater.inflate(R.layout.view_main_messages_friends_me, null);

		title_square = (RelativeLayout) squareView.findViewById(R.id.title_square);
		title_share = (RelativeLayout) shareView.findViewById(R.id.title_share);
		title_messages_friends_me = (RelativeLayout) messages_friends_me_View.findViewById(R.id.title_messages_friends_me);

		scannerCodeView = (ImageView) title_messages_friends_me.findViewById(R.id.scanner_code);
		userTopbarNameView = (TextView) title_messages_friends_me.findViewById(R.id.userTopbarName);
		userTopbarNameParentView = (RelativeLayout) title_messages_friends_me.findViewById(R.id.userTopbarNameParent);
		// userTopbarNameParentView.setVisibility(View.GONE);

		botton = (ImageView) title_messages_friends_me.findViewById(R.id.botton);

		main_pager_indicator = (ImageView) thisActivity.findViewById(R.id.main_pager_indicator);
		int main_pager_indicator_trip = (int) (48 * displayMetrics.density);

		mainPagerBody = new PagerBody();
		mainPagerBody.tag = "mainPagerBody";
		mainPagerBody.pager_indicator = main_pager_indicator;
		mainPagerBody.pager_indicator_trip = main_pager_indicator_trip;
		mainPagerBody.initialize(displayMetrics, myBodyCallback);

		main_container.addView(squareView);
		mainPagerBody.addChildView(squareView);
		mainPagerBody.setTitleView(title_square, 0);

		main_container.addView(messages_friends_me_View);
		mainPagerBody.addChildView(messages_friends_me_View);
		mainPagerBody.setTitleView(title_messages_friends_me, 1);

		TextView textView = new TextView(thisActivity);
		textView.setText("广场");
		textView.setGravity(Gravity.CENTER_VERTICAL);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		int padding = (int) (20 * displayMetrics.density);
		textView.setPadding(padding, 0, padding, 0);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, (int) (48 * displayMetrics.density));
		textView.setTextColor(Color.parseColor("#0099cd"));
		main_container.addView(textView, layoutParams);
		// TODO temp
		textView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent intent = new Intent(thisActivity, NearbyActivity.class);
				intent.putExtra("type", "newest");
				thisActivity.startActivity(intent);
				thisActivity.finish();
			}
		});

		friendsMenuView = (RelativeLayout) messages_friends_me_View.findViewById(R.id.rl_intimatefriends);
		messagesMenuView = (RelativeLayout) messages_friends_me_View.findViewById(R.id.rl_chatMessagesList);
		meMenuView = (RelativeLayout) messages_friends_me_View.findViewById(R.id.rl_userInfomation);

		squareMenuView = (RelativeLayout) thisActivity.findViewById(R.id.square_menu_view);
		shareMenuView = (RelativeLayout) thisActivity.findViewById(R.id.share_menu_view);
		messages_friends_me_menuView = (RelativeLayout) thisActivity.findViewById(R.id.messages_friends_me_menu_view);

		squareMenuImageView = (ImageView) squareMenuView.getChildAt(0);
		shareMenuImageView = (ImageView) shareMenuView.getChildAt(0);
		messages_friends_me_menuImageView = (ImageView) messages_friends_me_menuView.getChildAt(0);

		messagesView = (TouchView) messages_friends_me_View.findViewById(R.id.messagesContainer);

		friendsView = (TouchView) messages_friends_me_View.findViewById(R.id.friendsContainer);

		meView = (TouchView) messages_friends_me_View.findViewById(R.id.meContainer);

		messages_friends_me_pager_indicator = (ImageView) messages_friends_me_View.findViewById(R.id.messages_friends_me_pager_indicator);
		int messages_friends_me_pager_indicator_trip = (int) (displayMetrics.widthPixels - (20 * displayMetrics.density)) / 3;
		ViewGroup.LayoutParams params2 = messages_friends_me_pager_indicator.getLayoutParams();
		params2.height = (int) (32 * displayMetrics.density);
		params2.width = messages_friends_me_pager_indicator_trip;
		messages_friends_me_pager_indicator.setLayoutParams(params2);

		ViewGroup.LayoutParams friendsMenuViewParams = friendsMenuView.getLayoutParams();
		friendsMenuViewParams.height = (int) (32 * displayMetrics.density);
		friendsMenuViewParams.width = messages_friends_me_pager_indicator_trip;

		ViewGroup.LayoutParams messagesMenuViewParams = messagesMenuView.getLayoutParams();
		messagesMenuViewParams.height = (int) (32 * displayMetrics.density);
		messagesMenuViewParams.width = messages_friends_me_pager_indicator_trip;

		ViewGroup.LayoutParams meMenuViewParams = meMenuView.getLayoutParams();
		meMenuViewParams.height = (int) (32 * displayMetrics.density);
		meMenuViewParams.width = messages_friends_me_pager_indicator_trip;

		// progress
		this.controlProgressView = squareView.findViewById(R.id.title_control_progress_container);
		this.controlProgress = new ControlProgress();
		this.controlProgress.initialize(this.controlProgressView, displayMetrics);
		this.controlProgress.moveTo(0);

		messages_friends_me_PagerBody = new PagerBody();
		messages_friends_me_PagerBody.tag = "messages_friends_me_PagerBody";
		messages_friends_me_PagerBody.pager_indicator = messages_friends_me_pager_indicator;
		messages_friends_me_PagerBody.pager_indicator_trip = messages_friends_me_pager_indicator_trip;
		messages_friends_me_PagerBody.initialize(displayMetrics, myBodyCallback);

		messages_friends_me_PagerBody.addChildView(messagesView);
		messages_friends_me_PagerBody.addChildView(friendsView);
		messages_friends_me_PagerBody.addChildView(meView);
		messages_friends_me_PagerBody.inActive();

		this.thisView.squareSubView.initViews();
		this.thisView.messagesSubView.initViews();
		this.thisView.friendsSubView.initViews();
		this.thisView.meSubView.initViews();
	}

	public void recordPage() {
		String currentFunctionPage = data.localStatus.localData.currentFunctionPage;

		if (currentFunctionPage != null && !"".equals(currentFunctionPage)) {
			if ("share".equals(currentFunctionPage)) {
				thisView.mainPagerBody.active();
				thisView.messages_friends_me_PagerBody.inActive();
				thisView.mainPagerBody.flipTo(0);
				// Log.e("AAA", "share");
				thisView.activityStatus.state = thisView.activityStatus.SHARE;
			} else {
				// Log.e("AAA", "Me");
				thisView.mainPagerBody.inActive();
				thisView.messages_friends_me_PagerBody.active();
				thisView.mainPagerBody.flipTo(1);
				if ("message".equals(currentFunctionPage)) {
					thisView.messagesMenuView.performClick();
					thisView.messagesSubView.messageListBody.active();
					thisView.activityStatus.state = thisView.activityStatus.MESSAGES;
				} else if ("friend".equals(currentFunctionPage)) {
					thisView.friendsMenuView.performClick();
					// thisView.messages_friends_me_PagerBody.flipTo(1);
					thisView.friendsSubView.friendListBody.active();
					thisView.activityStatus.state = thisView.activityStatus.FRIENDS;
				} else if ("me".equals(currentFunctionPage)) {
					thisView.meMenuView.performClick();
					thisView.activityStatus.state = thisView.activityStatus.ME;
					// thisView.messages_friends_me_PagerBody.flipTo(2);
				}
			}
		}
	}

	class MyBodyCallback extends BodyCallback {
		@Override
		public void onStart(String bodyTag, float variable) {
			if (bodyTag.equals("messages_friends_me_PagerBody")) {

				thisView.friendsSubView.friendListBody.inActive();
				if (variable == 2) {
					thisView.meSubView.mMePageAppIconScaleSpring.setEndValue(1);
				}
			}
		}

		int lastOnFlipingIndex = -1;

		@Override
		public void onFlipping(String bodyTag, float variable) {
			// main_container.playSoundEffect(SoundEffectConstants.CLICK);
			if (bodyTag.equals("messages_friends_me_PagerBody")) {
				if (variable == 0) {
				} else if (variable == 1) {
				} else if (variable == 2) {
				}
				lastOnFlipingIndex = -1;
			} else if (bodyTag.equals("mainPagerBody")) {
				if (variable == 0) {
					lastOnFlipingIndex = -1;
					squareMenuImageView.setImageResource(R.drawable.square1);
					shareMenuImageView.setImageResource(R.drawable.group);
					messages_friends_me_menuImageView.setImageResource(R.drawable.me);
				}
				// else if (variable == 1) {
				// lastOnFlipingIndex = 1;
				// squareMenuImageView.setImageResource(R.drawable.square1);
				// shareMenuImageView.setImageResource(R.drawable.group);
				// messages_friends_me_menuImageView.setImageResource(R.drawable.me);
				// }
				// else if (variable == 1) {
				// squareMenuImageView.setImageResource(R.drawable.square);
				// shareMenuImageView.setImageResource(R.drawable.group);
				// messages_friends_me_menuImageView.setImageResource(R.drawable.me1);
				// if (lastOnFlipingIndex == 1) {
				// Log.d(tag, "bodyTag onFliping:" + variable + "  lastOnFlipingIndex: " + lastOnFlipingIndex);
				// switchTo2();
				// }
				// lastOnFlipingIndex = -1;
				// }

			}
		}

		public void switchTo2() {

			thisView.activityStatus.state = thisView.activityStatus.subState;
			thisView.mainPagerBody.inActive();
			thisView.messages_friends_me_PagerBody.active();
			if (thisView.activityStatus.state == thisView.activityStatus.MESSAGES) {

				thisView.messagesSubView.messageListBody.active();
				thisView.friendsSubView.friendListBody.inActive();
				thisView.squareSubView.locationListBody.inActive();
				data.localStatus.localData.currentFunctionPage = "message";
				log.e("-message-111");
			} else if (thisView.activityStatus.state == thisView.activityStatus.FRIENDS) {

				thisView.messagesSubView.messageListBody.inActive();
				thisView.friendsSubView.friendListBody.active();
				thisView.squareSubView.locationListBody.inActive();
				data.localStatus.localData.currentFunctionPage = "friend";
				log.e("-friend-111");
			} else if (thisView.activityStatus.state == thisView.activityStatus.ME) {
				thisView.meSubView.mMePageAppIconScaleSpring.setEndValue(0);

				thisView.messagesSubView.messageListBody.inActive();
				thisView.friendsSubView.friendListBody.inActive();
				thisView.squareSubView.locationListBody.inActive();
				data.localStatus.localData.currentFunctionPage = "me";
				log.e("-me-111");
			}
			squareMenuImageView.setImageResource(R.drawable.square);
			shareMenuImageView.setImageResource(R.drawable.group);
			messages_friends_me_menuImageView.setImageResource(R.drawable.me1);
		}

		@Override
		public void onFixed(String bodyTag, float variable) {
			log.e(bodyTag + "---" + variable);
			if (bodyTag.equals("messages_friends_me_PagerBody")) {
				if (variable == 0) {
					thisView.activityStatus.state = thisView.activityStatus.MESSAGES;
					thisView.activityStatus.subState = thisView.activityStatus.MESSAGES;

					thisView.messagesSubView.messageListBody.active();
					thisView.friendsSubView.friendListBody.inActive();
					thisView.squareSubView.locationListBody.inActive();
					data.localStatus.localData.currentFunctionPage = "message";
					log.e("-message-");
				} else if (variable == 1) {
					thisView.activityStatus.state = thisView.activityStatus.FRIENDS;
					thisView.activityStatus.subState = thisView.activityStatus.FRIENDS;

					thisView.messagesSubView.messageListBody.inActive();
					thisView.friendsSubView.friendListBody.active();
					thisView.squareSubView.locationListBody.inActive();
					data.localStatus.localData.currentFunctionPage = "friend";
					log.e("-friend-");
				} else if (variable == 2) {
					thisView.activityStatus.state = thisView.activityStatus.ME;
					thisView.activityStatus.subState = thisView.activityStatus.ME;

					thisView.messagesSubView.messageListBody.inActive();
					thisView.friendsSubView.friendListBody.inActive();
					thisView.squareSubView.locationListBody.inActive();
					data.localStatus.localData.currentFunctionPage = "me";
					log.e("-me-");
				}
			} else if (bodyTag.equals("mainPagerBody")) {
				if (variable == 0) {
					thisView.activityStatus.state = thisView.activityStatus.SHARE;

					thisView.squareSubView.locationListBody.active();
					thisView.messagesSubView.messageListBody.inActive();
					thisView.friendsSubView.friendListBody.inActive();
					thisView.squareSubView.locationListBody.inActive();

					squareMenuImageView.setImageResource(R.drawable.square1);
					shareMenuImageView.setImageResource(R.drawable.group);
					messages_friends_me_menuImageView.setImageResource(R.drawable.me);
					data.localStatus.localData.currentFunctionPage = "share";
					log.e("-share-");
				} else if (variable == 1) {
					switchTo2();
				}
			}
			Log.i(bodyTag, "thisView.activityStatus.state: " + thisView.activityStatus.state);
		}

		@Override
		public boolean onOverRange(String bodyTag, float variable) {
			if (bodyTag.equals("messages_friends_me_PagerBody")) {
				if (variable == -1) {
					Log.d(tag, "messages_friends_me_PagerBody onOverRange");

					thisView.mainPagerBody.active();
					thisView.messages_friends_me_PagerBody.inActive();
					return true;
				}
			}
			return false;
		}
	}
}
