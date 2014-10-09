package com.open.welinks.customView;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.open.lib.MyLog;
import com.open.welinks.BusinessCardActivity;
import com.open.welinks.ChatActivity;
import com.open.welinks.GroupInfomationActivity;
import com.open.welinks.R;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.model.LBSHandlers;
import com.open.welinks.model.Parser;
import com.open.welinks.utils.DateUtil;

public class SmallBusinessCardPopView {

	public String tag = "SmallBusinessCardPopView";
	public MyLog log = new MyLog(tag, true);
	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();

	public SmallBusinessCardPopView instance;
	public Activity thisActivity;
	public DisplayMetrics displayMetrics;

	public View view;

	public CardView cardView;
	// public CardHolder cardHolder;

	public PopupWindow userCardPopWindow;

	public SmallBusinessCardPopView(Activity thisActivity, View view) {
		this.instance = this;
		this.view = view;
		this.thisActivity = thisActivity;
		this.cardView = new CardView(thisActivity);
	}

	public void showUserCardDialogView() {
		if (userCardPopWindow != null && !userCardPopWindow.isShowing()) {
			userCardPopWindow.showAtLocation(this.view, Gravity.CENTER, 0, 0);
		}
	}

	public void dismissUserCardDialogView() {
		if (userCardPopWindow != null && userCardPopWindow.isShowing()) {
			userCardPopWindow.dismiss();
		}
	}

	public class CardView extends FrameLayout {

		public Context context;

		public DisplayImageOptions smallBusinessCardOptions;
		public View userCardMainView;
		public RelativeLayout userBusinessContainer;
		public TextView goInfomationView;
		public TextView goChatView;
		public ImageView userHeadView;
		public TextView userNickNameView;
		public TextView userAgeView;
		public TextView distanceView;
		public TextView lastLoginTimeView;
		public LinearLayout optionTwoView;
		public TextView singleButtonView;
		public TextView cardStatusView;
		public TextView vLineView;

		public String TYPE_POINT = "point";
		public String TYPE_GROUP = "group";
		public String TYPE_SQUARE = "square";

		public OnClickListener mOnClickListener;

		public String type;
		public String key;

		public CardView(Context context) {
			super(context);
			this.context = context;
			init();
		}

		public CardView(Context context, AttributeSet attrs) {
			super(context, attrs);
			this.context = context;
			init();
		}

		public CardView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			this.context = context;
			init();
		}

		public void init() {
			this.initView();
			this.initializeListeners();
			this.bindEvent();
		}

		public void setSmallBusinessCardContent(String type, String key) {
			this.type = type;
			this.key = key;
			if (type == null || "".equals(type) || key == null || "".equals(key)) {
				return;
			}
			parser.check();
			User user = data.userInformation.currentUser;
			String relation = "";
			if (type.equals(TYPE_POINT)) {
				if (data.relationship.friends != null) {
					if (key.equals(user.phone)) {
						relation = "自己";
						setContent(false, user.sex, user.age, user.head, user.nickName, relation, type, key, user.longitude, user.latitude, user.lastLoginTime);
					} else if (data.relationship.friends.contains(key)) {
						relation = "已是好友";
						Friend friend = data.relationship.friendsMap.get(key);
						if (friend != null) {
							setContent(true, friend.sex, friend.age + "", friend.head, friend.nickName, relation, type, key, friend.longitude, friend.latitude, friend.lastLoginTime);
						}
					} else {
						relation = "不是好友";
						Friend friend = data.relationship.friendsMap.get(key);
						if (friend != null) {
							setContent(false, friend.sex, friend.age + "", friend.head, friend.nickName, relation, type, key, friend.longitude, friend.latitude, friend.lastLoginTime);
						} else {
							setContent(false, "", "", "", "", relation, type, key, "0", "0", "0");
						}
					}
				} else {
					relation = "不是好友";
					setContent(false, "", "", "", "", relation, type, key, "0", "0", "0");
				}
			} else if (type.equals(TYPE_GROUP)) {
				if (data.relationship.groupsMap == null) {
					return;
				}
				if (data.relationship.groups != null) {
					if (data.relationship.groups.contains(key)) {
						relation = "已加入该群组";
						Group group = data.relationship.groupsMap.get(key);
						if (group != null) {
							setContent(true, "", "", group.icon, group.name, relation, type, key, group.longitude, group.latitude, 0 + "");
						}
					} else {
						relation = "未加入该群组";
						Group group = data.relationship.groupsMap.get(key);
						if (group != null) {
							setContent(false, "", "", group.icon, group.name, relation, type, key, group.longitude, group.latitude, 0 + "");
						}
					}
				} else {
					relation = "未加入该群组";
					Group group = data.relationship.groupsMap.get(key);
					if (group != null) {
						setContent(false, "", "", group.icon, group.name, relation, type, key, group.longitude, group.latitude, 0 + "");
					}
				}
			} else if (type.equals(TYPE_SQUARE)) {
				if (data.relationship.groups != null) {
					if (data.relationship.groups.contains(key)) {
						relation = "已加入该广场";
						Group group = data.relationship.groupsMap.get(key);
						if (group != null) {
							setContent(false, "", "", group.icon, group.name, relation, type, key, group.longitude, group.latitude, 0 + "");
						}
					} else {
						relation = "未加入该广场";
						Group group = data.relationship.groupsMap.get(key);
						if (group != null) {
							setContent(false, "", "", group.icon, group.name, relation, type, key, group.longitude, group.latitude, 0 + "");
						}
					}
				} else {
					relation = "未加入该广场";
					Group group = data.relationship.groupsMap.get(key);
					if (group != null) {
						setContent(false, "", "", group.icon, group.name, relation, type, key, group.longitude, group.latitude, 0 + "");
					}
				}
			}
		}

		public void setMenu(boolean flag) {
			if (flag) {
				optionTwoView.setVisibility(View.VISIBLE);
				singleButtonView.setVisibility(View.GONE);
			} else {
				optionTwoView.setVisibility(View.GONE);
				singleButtonView.setVisibility(View.VISIBLE);
			}
		}

		public FileHandlers fileHandlers = FileHandlers.getInstance();
		public LBSHandlers lbsHandlers = LBSHandlers.getInstance();

		public void setContent(boolean isChat, String sex, String age, String fileName, String nickName, String relation, String type, String key, String longitude, String latitude, String lastLoginTime) {
			parser.check();
			User user = data.userInformation.currentUser;
			fileHandlers.getHeadImage(fileName, userHeadView, smallBusinessCardOptions);
			userNickNameView.setText(nickName);
			cardStatusView.setText(relation);
			userAgeView.setText(age);
			if (!"".equals(sex) && ("male".equals(sex) || "男".equals(sex))) {
				userAgeView.setBackgroundResource(R.drawable.personalinfo_male);
			} else {
				userAgeView.setBackgroundResource(R.drawable.personalinfo_female);
			}
			distanceView.setText(lbsHandlers.pointDistance(user.longitude, user.latitude, longitude, latitude) + "km");
			if (lastLoginTime != null && !"".equals(lastLoginTime)) {
				lastLoginTimeView.setText(DateUtil.getTime(Long.valueOf(lastLoginTime)));
			} else {
				lastLoginTimeView.setText("");
			}
			if (isChat) {
				optionTwoView.setVisibility(View.VISIBLE);
				singleButtonView.setVisibility(View.GONE);
			} else {
				optionTwoView.setVisibility(View.GONE);
				singleButtonView.setVisibility(View.VISIBLE);
			}
			if (type.equals(TYPE_POINT)) {
				if (user.phone.equals(key)) {
					singleButtonView.setText("个人资料");
				} else if (data.relationship.friends.contains(key)) {
					goInfomationView.setText("好友资料");
				} else {
					singleButtonView.setText("用户资料");
				}
				vLineView.setVisibility(View.VISIBLE);
				userAgeView.setVisibility(View.VISIBLE);
			} else if (type.equals(TYPE_GROUP)) {
				if (data.relationship.groups.contains(key)) {
					goInfomationView.setText("群组信息");
				} else {
					singleButtonView.setText("群组信息");
				}
				userAgeView.setVisibility(View.GONE);
				lastLoginTimeView.setText("");
				vLineView.setVisibility(View.GONE);
			} else if (type.equals(TYPE_SQUARE)) {
				goInfomationView.setText("广场资料");
				userAgeView.setVisibility(View.GONE);
				lastLoginTimeView.setText("");
				vLineView.setVisibility(View.GONE);
			}
			if (lastLoginTimeView.getText().toString().equals("")) {
				vLineView.setVisibility(View.GONE);
			}
		}

		@SuppressWarnings("deprecation")
		private void initView() {
			displayMetrics = new DisplayMetrics();
			thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

			userCardMainView = LayoutInflater.from(context).inflate(R.layout.view_dialog_small_businesscard, this);
			optionTwoView = (LinearLayout) userCardMainView.findViewById(R.id.optionTwo);
			userNickNameView = (TextView) userCardMainView.findViewById(R.id.userNickName);
			userAgeView = (TextView) userCardMainView.findViewById(R.id.userAge);
			distanceView = (TextView) userCardMainView.findViewById(R.id.userDistance);
			lastLoginTimeView = (TextView) userCardMainView.findViewById(R.id.lastLoginTime);
			userBusinessContainer = (RelativeLayout) userCardMainView.findViewById(R.id.userBusinessView);
			int height = (int) (displayMetrics.heightPixels * 0.5f - 50 * displayMetrics.density) + getStatusBarHeight(thisActivity);
			userBusinessContainer.getLayoutParams().height = height;
			goInfomationView = (TextView) userCardMainView.findViewById(R.id.goInfomation);
			goChatView = (TextView) userCardMainView.findViewById(R.id.goChat);
			singleButtonView = (TextView) userCardMainView.findViewById(R.id.singleButton);
			cardStatusView = (TextView) userCardMainView.findViewById(R.id.cardStatus);
			vLineView = (TextView) userCardMainView.findViewById(R.id.vLine);
			// singleButtonView.setVisibility(View.GONE);
			userHeadView = (ImageView) userCardMainView.findViewById(R.id.userHead);
			userHeadView.getLayoutParams().height = height;
			userCardPopWindow = new PopupWindow(userCardMainView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
			userCardPopWindow.setBackgroundDrawable(new BitmapDrawable());
			smallBusinessCardOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(10)).build();
		}

		public void bindEvent() {
			this.userCardMainView.setOnClickListener(mOnClickListener);
			this.goInfomationView.setOnClickListener(mOnClickListener);
			this.singleButtonView.setOnClickListener(mOnClickListener);
			this.goChatView.setOnClickListener(mOnClickListener);
		}

		public void initializeListeners() {
			mOnClickListener = new OnClickListener() {

				@Override
				public void onClick(View view) {
					if (view.equals(userCardMainView)) {
						dismissUserCardDialogView();
					} else if (view.equals(goInfomationView) || view.equals(singleButtonView)) {
						// String phone = (String) view.getTag(R.id.tag_first);
						if (type.equals(TYPE_POINT)) {
							Intent intent = new Intent(thisActivity, BusinessCardActivity.class);
							intent.putExtra("key", key);
							intent.putExtra("type", type);
							thisActivity.startActivity(intent);
						} else if (type.equals(TYPE_GROUP)) {
							Intent intent = new Intent(thisActivity, GroupInfomationActivity.class);
							intent.putExtra("gid", key);
							thisActivity.startActivity(intent);
						}

					} else if (view.equals(goChatView)) {
						// String phone = (String) view.getTag(R.id.tag_first);
						Intent intent = new Intent(thisActivity, ChatActivity.class);
						intent.putExtra("id", key);
						intent.putExtra("type", type);
						thisActivity.startActivityForResult(intent, R.id.tag_second);
					}
				}
			};
		}
	}

	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}
}
