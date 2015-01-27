package com.open.welinks;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.open.lib.MyLog;
import com.open.welinks.ExpressionManageActivity.ListAdapter;
import com.open.welinks.customView.Alert;
import com.open.welinks.customView.Alert.AlertInputDialog;
import com.open.welinks.customView.Alert.AlertInputDialog.OnDialogClickListener;
import com.open.welinks.customView.ThreeChoicesView;
import com.open.welinks.model.API;
import com.open.welinks.model.Constant;
import com.open.welinks.model.Data;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.Relationship.GroupCircle;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.model.Parser;
import com.open.welinks.utils.BaseDataUtils;
import com.open.welinks.utils.InputMethodManagerUtils;
import com.open.welinks.view.ViewManage;

public class GroupListActivity extends Activity {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();
	public ViewManage mViewManage = ViewManage.getInstance();
	public String tag = "GroupListActivity";
	public MyLog log = new MyLog(tag, true);

	public LayoutInflater mInflater;

	public RelativeLayout backView;
	public TextView backTitileView;
	public TextView titleView;
	public RelativeLayout rightContainer, maxView;
	public LinearLayout rightContainerLinearLayout;

	public TextView createGroupButton, sectionNameTextView;

	public ImageView moreView;

	public View dialogView;
	public PopupWindow popDialogView;

	public ListView groupListContainer;

	public ThreeChoicesView threeChoicesView;

	public DisplayMetrics displayMetrics;

	public OnClickListener mOnClickListener;
	public OnTouchListener mOnTouchListener;
	public OnItemClickListener mOnItemClickListener;
	public com.open.welinks.customView.ThreeChoicesView.OnItemClickListener mOnThreeChoiceItemClickListener;
	public ListController listController;

	public List<String> groups;
	public String[] friends;
	public Map<String, Group> groupsMap;
	public Map<String, Friend> friendsMap;
	public Map<String, GroupCircle> groupCirclesMap;
	public GroupListAdapter groupListAdapter;

	public DragSortListView groupCircleList;
	public View buttons, manage, buttonOne, buttonTwo, buttonThree, background;
	public TextView buttonOneText, buttonTwoText, buttonThreeText;
	public GroupCircleDialogAdapter dialogAdapter;

	public FileHandlers fileHandlers = FileHandlers.getInstance();

	public ViewManage viewManage = ViewManage.getInstance();

	public ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

	public GroupCircle currentGroupCircle, onTouchDownGroupCircle;
	public View onTouchDownView, onLongPressView;

	public Gson gson = new Gson();

	public Status status;
	public String gid;

	private enum Status {
		square, friend, share_group, message_group, list_group, card_friend, card_group, group_circle
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		viewManage.groupListActivity = this;
		String type = getIntent().getStringExtra("type");
		if ("list_group".equals(type)) {
			this.status = Status.list_group;
		} else if ("share".equals(type)) {
			this.status = Status.square;
		} else if ("message".equals(type)) {
			this.status = Status.friend;
		} else if ("sendCard".equals(type)) {
			this.status = Status.card_friend;
		} else if ("group_circle".equals(type)) {
			this.status = Status.group_circle;
			gid = getIntent().getStringExtra("gid");
		}
		initView();
		initializeListeners();
		bindEvent();
		initData();
	}

	@Override
	protected void onResume() {
		if (dialogAdapter != null)
			dialogAdapter.notifyDataSetChanged();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		viewManage.groupListActivity = null;
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		if (popDialogView.isShowing()) {
			changePopupWindow();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void finish() {
		if (mViewManage.shareSubView != null) {
			mViewManage.shareSubView.setGroupsDialogContent();
			mViewManage.shareSubView.showShareMessages();
		}
		super.finish();
	}

	private void initData() {
		friends = new String[] {};
		parser.check();
		if (status == Status.friend || status == Status.card_friend) {
			friendsMap = data.relationship.friendsMap;
			Set<String> friends = new HashSet<String>();
			for (String circles : data.relationship.circles) {
				friends.addAll(data.relationship.circlesMap.get(circles).friends);
			}
			if (data.relationship.circlesMap.get(Constant.DEFAULTCIRCLEID + "") != null) {
				friends.addAll(data.relationship.circlesMap.get(Constant.DEFAULTCIRCLEID + "").friends);
			}
			this.friends = friends.toArray(this.friends);
		} else if (status == Status.group_circle) {
			groups = data.relationship.groupCircles;
			groupCirclesMap = data.relationship.groupCirclesMap;
		} else {
			if (status == Status.square) {
				groups = data.relationship.squares;
			} else if (status == Status.list_group) {
				currentGroupCircle = data.relationship.groupCirclesMap.get(data.relationship.groupCircles.get(0));
				groups = currentGroupCircle.groups;
				showGroupCircles();
			} else {
				groups = data.relationship.groups;
			}
			groupsMap = data.relationship.groupsMap;
		}
		groupListAdapter = new GroupListAdapter();
		groupListContainer.setAdapter(groupListAdapter);
	}

	private void initView() {
		displayMetrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		mInflater = this.getLayoutInflater();

		setContentView(R.layout.activity_group_list);

		this.maxView = (RelativeLayout) findViewById(R.id.maxView);
		this.backView = (RelativeLayout) findViewById(R.id.backView);
		this.backTitileView = (TextView) findViewById(R.id.backTitleView);
		this.titleView = (TextView) findViewById(R.id.titleContent);
		this.rightContainer = (RelativeLayout) findViewById(R.id.rightContainer);

		this.groupListContainer = (ListView) findViewById(R.id.groupListContainer);
		this.createGroupButton = new TextView(this);
		this.threeChoicesView = new ThreeChoicesView(this);
		this.threeChoicesView.setTwoChoice();

		if (status == Status.list_group) {
			this.backTitileView.setText("群组列表");

			rightContainerLinearLayout = new LinearLayout(this);
			rightContainerLinearLayout.setPadding((int) (10 * displayMetrics.density), 0, (int) (20 * displayMetrics.density), 0);
			LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, (int) (48 * displayMetrics.density));
			rightContainerLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
			RelativeLayout.LayoutParams rightParams = (android.widget.RelativeLayout.LayoutParams) rightContainer.getLayoutParams();
			rightParams.rightMargin = 0;
			// rightContainer.setPadding(0, 0, (int) BaseDataUtils.dpToPx(20), 0);
			moreView = new ImageView(this);
			moreView.setTranslationY((int) (30 * displayMetrics.density));
			moreView.setImageResource(R.drawable.subscript_triangle);
			// moreView.setColorFilter(Color.parseColor("#0099cd"));
			RelativeLayout.LayoutParams infomationParams = new RelativeLayout.LayoutParams((int) (7 * displayMetrics.density), (int) (7 * displayMetrics.density));

			sectionNameTextView = new TextView(this);
			sectionNameTextView.setSingleLine();
			sectionNameTextView.setTextColor(Color.WHITE);
			sectionNameTextView.setTextSize(18);

			RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT, (int) (48 * displayMetrics.density));
			rightContainerLinearLayout.addView(sectionNameTextView, textViewParams);
			sectionNameTextView.setGravity(Gravity.CENTER_VERTICAL);
			rightContainerLinearLayout.addView(moreView, infomationParams);
			rightContainer.addView(rightContainerLinearLayout, lineParams);

			currentGroupCircle = data.relationship.groupCirclesMap.get(data.relationship.groupCircles.get(0));

			initializationGroupCirclesDialog();
			// int dp_5 = (int) (5 * displayMetrics.density);
			// this.createGroupButton.setGravity(Gravity.CENTER);
			// this.createGroupButton.setTextColor(Color.WHITE);
			// this.createGroupButton.setPadding(dp_5 * 2, dp_5, dp_5 * 2, dp_5);
			// this.createGroupButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			// this.createGroupButton.setText("创建群组");
			// this.createGroupButton.setBackgroundResource(R.drawable.textview_bg);
			// RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			// layoutParams.setMargins(0, dp_5, (int) 0, dp_5);
			// layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			// this.rightContainer.addView(this.createGroupButton, layoutParams);
		} else if (status == Status.card_friend) {
			this.backTitileView.setText("发送名片");
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			this.threeChoicesView.setButtonOneText("全部好友");
			this.threeChoicesView.setButtonThreeText("群组");
			this.rightContainer.addView(this.threeChoicesView, layoutParams);
		} else if (status == Status.group_circle) {
			this.backTitileView.setText("群组分组列表");
		} else {
			this.backTitileView.setText("分享");
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			if (status == Status.friend) {
				this.threeChoicesView.setButtonOneText("全部好友");
				this.threeChoicesView.setButtonThreeText("群组");
			} else if (status == Status.square) {
				this.threeChoicesView.setButtonOneText("社区");
				this.threeChoicesView.setButtonThreeText("群组");
			}
			this.rightContainer.addView(this.threeChoicesView, layoutParams);
		}
	}

	private void initializeListeners() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(createGroupButton)) {
					Intent intent = new Intent(GroupListActivity.this, CreateGroupStartActivity.class);
					startActivity(intent);
				} else if (view.equals(backView)) {
					finish();
				} else if (view.equals(rightContainerLinearLayout)) {
					changePopupWindow();
				} else if (view.equals(manage)) {
					if (buttons.getVisibility() == View.VISIBLE) {
						buttons.setVisibility(View.GONE);
					} else {
						buttons.setVisibility(View.VISIBLE);
					}
				} else if (view.equals(background)) {
					changePopupWindow();
				} else if (view.equals(buttonOne)) {
					createGroupCircle();
				}
			}

		};
		mOnThreeChoiceItemClickListener = this.threeChoicesView.new OnItemClickListener() {
			@Override
			public void onButtonCilck(int position) {
				if (position == 1) {
					if (status == Status.share_group) {
						status = Status.square;
					} else if (status == Status.message_group) {
						status = Status.friend;
					} else if (status == Status.card_group) {
						status = Status.card_friend;
					}
				} else if (position == 3) {
					if (status == Status.friend) {
						status = Status.message_group;
					} else if (status == Status.square) {
						status = Status.share_group;
					} else if (status == Status.card_friend) {
						status = Status.card_group;
					}
				}
				groupListAdapter.notifyDataSetChanged();
			}
		};

		mOnItemClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				if (status == Status.list_group) {
					Intent intent = new Intent(GroupListActivity.this, BusinessCardActivity.class);
					intent.putExtra("type", "group");
					intent.putExtra("orid", currentGroupCircle.rid);
					intent.putExtra("key", groups.get(position));
					startActivity(intent);
				} else {
					Alert.createDialog(GroupListActivity.this).setTitle(getDialogTitle(position)).setOnConfirmClickListener(new OnDialogClickListener() {

						@Override
						public void onClick(AlertInputDialog dialog) {
							Intent intent = new Intent();
							if (status == Status.friend || status == Status.card_friend) {
								intent.putExtra("key", friendsMap.get(friends[position]).phone);
								intent.putExtra("type", "message");
								intent.putExtra("sendType", "point");
							} else if (status == Status.group_circle) {
								intent.putExtra("rid", data.relationship.groupCirclesMap.get(groups.get(position)).rid);
							} else {
								intent.putExtra("key", String.valueOf(groupsMap.get(groups.get(position)).gid));
								if (status == Status.share_group || status == Status.square) {
									intent.putExtra("type", "share");
								} else if (status == Status.message_group || status == Status.card_group) {
									intent.putExtra("type", "message");
									intent.putExtra("sendType", "group");
								}
							}
							setResult(Activity.RESULT_OK, intent);
							finish();
						}
					}).show();
				}
			}
		};
	}

	private void bindEvent() {
		this.threeChoicesView.setOnItemClickListener(mOnThreeChoiceItemClickListener);
		this.groupListContainer.setOnItemClickListener(mOnItemClickListener);
		this.createGroupButton.setOnClickListener(mOnClickListener);
		this.backView.setOnClickListener(mOnClickListener);

		if (rightContainerLinearLayout != null)
			this.rightContainerLinearLayout.setOnClickListener(mOnClickListener);
		if (buttonOne != null)
			this.buttonOne.setOnClickListener(mOnClickListener);
		if (manage != null)
			this.manage.setOnClickListener(mOnClickListener);
		if (background != null)
			this.background.setOnClickListener(mOnClickListener);
	}

	public String getDialogTitle(int position) {
		String title = "是否";
		if (status == Status.friend) {
			title += "分享给好友：【" + friendsMap.get(friends[position]).nickName + "】?";
		} else if (status == Status.message_group) {
			title += "分享给房间：【" + groupsMap.get(groups.get(position)).name + "】?";
		} else if (status == Status.square) {
			title += "分享到社区：【" + groupsMap.get(groups.get(position)).name + "】?";
		} else if (status == Status.share_group) {
			title += "分享到房间：【" + groupsMap.get(groups.get(position)).name + "】?";
		} else if (status == Status.card_friend) {
			title += "发送名片给好友：【" + friendsMap.get(friends[position]).nickName + "】?";
		} else if (status == Status.card_group) {
			title += "发送名片到群组：【" + groupsMap.get(groups.get(position)).name + "】?";
		} else if (status == Status.group_circle) {
			Group group = data.relationship.groupsMap.get(gid);
			GroupCircle groupCircle = data.relationship.groupCirclesMap.get(groups.get(position));
			if (group != null && groupCircle != null) {
				title += "将群组【" + group.name + "】加入到群组分组【" + groupCircle.name + "】中？";
			} else {
				title = "";
			}
		}
		return title;
	}

	public void initializationGroupCirclesDialog() {
		dialogView = mInflater.inflate(R.layout.dialog_listview, null);
		groupCircleList = (DragSortListView) dialogView.findViewById(R.id.content);
		buttons = dialogView.findViewById(R.id.buttons);
		manage = dialogView.findViewById(R.id.manage);
		background = dialogView.findViewById(R.id.background);
		buttonOne = dialogView.findViewById(R.id.buttonOne);
		buttonTwo = dialogView.findViewById(R.id.buttonTwo);
		buttonThree = dialogView.findViewById(R.id.buttonThree);
		buttonOneText = (TextView) dialogView.findViewById(R.id.buttonOneText);
		buttonTwoText = (TextView) dialogView.findViewById(R.id.buttonTwoText);
		buttonThreeText = (TextView) dialogView.findViewById(R.id.buttonThreeText);
		buttonOneText.setText("新建分组");
		buttonTwo.setVisibility(View.GONE);
		buttonThree.setVisibility(View.GONE);

		popDialogView = new PopupWindow(dialogView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		popDialogView.setBackgroundDrawable(new BitmapDrawable());

	}

	public void changePopupWindow() {
		if (popDialogView.isShowing()) {
			popDialogView.dismiss();
		} else {
			if (buttons.getVisibility() == View.VISIBLE)
				buttons.setVisibility(View.GONE);
			popDialogView.showAtLocation(maxView, Gravity.CENTER, 0, 0);
		}
	}

	public class GroupCircleDialogAdapter extends BaseAdapter {
		private List<String> groupCircles;

		public GroupCircleDialogAdapter() {
			groupCircles = data.relationship.groupCircles;
		}

		@Override
		public void notifyDataSetChanged() {
			groupListAdapter.notifyDataSetChanged();
			super.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return groupCircles.size();
		}

		@Override
		public Object getItem(int position) {
			return data.relationship.groupCirclesMap.get(groupCircles.get(position));
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if (convertView == null) {
				holder = new Holder();
				convertView = mInflater.inflate(R.layout.share_group_select_dialog_item, null, false);
				holder.iconView = (ImageView) convertView.findViewById(R.id.groupIcon);
				holder.selectedStatusView = (ImageView) convertView.findViewById(R.id.groupSelectedStatus);
				holder.cardBackground = (ImageView) convertView.findViewById(R.id.grip_card_background);
				holder.nameView = (TextView) convertView.findViewById(R.id.groupName);
				holder.selectedStatusView.getLayoutParams().width = (int) BaseDataUtils.dpToPx(350);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			GroupCircle groupCircle = data.relationship.groupCirclesMap.get(groupCircles.get(position));
			if (groupCircle != null) {
				holder.nameView.setText(groupCircle.name);
				if (currentGroupCircle != null && currentGroupCircle.rid == groupCircle.rid) {
					holder.selectedStatusView.setVisibility(View.VISIBLE);
				} else {
					holder.selectedStatusView.setVisibility(View.INVISIBLE);
				}
			}
			return convertView;
		}

		class Holder {
			public ImageView iconView, selectedStatusView, cardBackground;
			public TextView nameView;
		}

	}

	public class ListController extends DragSortController implements DragSortListView.DropListener, DragSortListView.RemoveListener, android.widget.AdapterView.OnItemClickListener {
		private GroupCircleDialogAdapter adapter;
		private DragSortListView listView;

		public ListController(DragSortListView dslv, GroupCircleDialogAdapter dialogAdapter) {
			super(dslv);
			this.adapter = dialogAdapter;
			this.listView = dslv;
			setRemoveEnabled(true);
			setRemoveMode(DragSortController.FLING_REMOVE);
			setDragInitMode(DragSortController.ON_LONG_PRESS);
		}

		@Override
		public void drop(int from, int to) {
			if (from != to) {
				String groupCircle = data.relationship.groupCircles.remove(from);
				data.relationship.groupCircles.add(to, groupCircle);
				adapter.notifyDataSetChanged();
				modifyGroupCirclesSequence(gson.toJson(data.relationship.groupCircles));
			}
		}

		@Override
		public void remove(final int which) {
			GroupCircle groupCircle = (GroupCircle) adapter.getItem(which);
			removeGroupCircle(groupCircle);
		}

		@Override
		public boolean onDown(MotionEvent ev) {
			int res = super.dragHandleHitPosition(ev);
			GroupCircle groupCircle = (GroupCircle) adapter.getItem(res);
			if (groupCircle.rid == 8888888) {
				setRemoveEnabled(false);
			} else {
				setRemoveEnabled(true);
			}
			return super.onDown(ev);
		}

		@Override
		public int startDragPosition(MotionEvent ev) {
			return super.dragHandleHitPosition(ev);
		}

		@SuppressWarnings("deprecation")
		@Override
		public View onCreateFloatView(int position) {
			Vibrator vibrator = (Vibrator) GroupListActivity.this.getSystemService(Service.VIBRATOR_SERVICE);
			long[] pattern = { 100, 100, 300 };
			vibrator.vibrate(pattern, -1);

			View view = adapter.getView(position, null, groupCircleList);
			view.setBackgroundDrawable(getResources().getDrawable(R.drawable.card_login_background_press));
			return view;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			GroupCircle groupCircle = (GroupCircle) adapter.getItem(position);
			if (groupCircle != null) {
				currentGroupCircle = groupCircle;
				showGroupCircles();
			}
			changePopupWindow();
		}
	}

	public void showGroupCircles() {
		if (dialogAdapter == null) {
			dialogAdapter = new GroupCircleDialogAdapter();
			groupCircleList.setAdapter(dialogAdapter);
			listController = new ListController(groupCircleList, dialogAdapter);
			groupCircleList.setDropListener(listController);
			groupCircleList.setRemoveListener(listController);
			groupCircleList.setFloatViewManager(listController);
			groupCircleList.setOnTouchListener(listController);
			groupCircleList.setOnItemClickListener(listController);
		} else {
			dialogAdapter.notifyDataSetChanged();
		}
		if (currentGroupCircle != null) {
			sectionNameTextView.setText(currentGroupCircle.name);
		}
	}

	public void createGroupCircle() {
		Alert.createInputDialog(GroupListActivity.this).setInputHint("请输入分组名称").setTitle("创建群分组").setOnConfirmClickListener(new OnDialogClickListener() {

			@Override
			public void onClick(AlertInputDialog dialog) {
				String name = dialog.getInputText().trim();
				if (!"".equals(name)) {
					GroupCircle groupCircle = data.relationship.new GroupCircle();
					groupCircle.rid = (int) System.currentTimeMillis();
					groupCircle.name = name;
					data.relationship.groupCircles.add(String.valueOf(groupCircle.rid));
					data.relationship.groupCirclesMap.put(String.valueOf(groupCircle.rid), groupCircle);

					RequestParams params = new RequestParams();
					HttpUtils httpUtils = new HttpUtils();
					params.addBodyParameter("phone", data.userInformation.currentUser.phone);
					params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
					params.addBodyParameter("name", groupCircle.name);
					params.addBodyParameter("rid", String.valueOf(groupCircle.rid));

					httpUtils.send(HttpMethod.POST, API.GROUP_CREATEGROUPCIRCLE, params, responseHandlers.group_creategroupcircle);
					showGroupCircles();
				}
			}

		}).show();

	}

	public void removeGroupCircle(final GroupCircle groupCircle) {
		Alert.createDialog(this).setTitle("是否删除群组分组【" + groupCircle.name + "】？删除后此分组中群组将移动到【默认分组】").setOnConfirmClickListener(new OnDialogClickListener() {

			@Override
			public void onClick(AlertInputDialog dialog) {
				data.relationship.groupCircles.remove(String.valueOf(groupCircle.rid));
				if (currentGroupCircle.rid == groupCircle.rid) {
					currentGroupCircle = data.relationship.groupCirclesMap.get(data.relationship.groupCircles.get(0));
				}
				for (String gid : data.relationship.groupCirclesMap.get(String.valueOf(groupCircle.rid)).groups) {
					data.relationship.groupCirclesMap.get("8888888").groups.add(gid);
				}
				showGroupCircles();

				RequestParams params = new RequestParams();
				HttpUtils httpUtils = new HttpUtils();
				params.addBodyParameter("phone", data.userInformation.currentUser.phone);
				params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
				params.addBodyParameter("rid", String.valueOf(groupCircle.rid));
				httpUtils.send(HttpMethod.POST, API.GROUP_DELETEGROUPCIRCLE, params, responseHandlers.group_deletegroupcircle);
			}
		}).setOnCancelClickListener(new OnDialogClickListener() {

			@Override
			public void onClick(AlertInputDialog dialog) {
				showGroupCircles();

			}
		}).show();
	}

	public void modifyGroupCirclesSequence(String sequenceListString) {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		params.addBodyParameter("groupCircles", sequenceListString);

		httpUtils.send(HttpMethod.POST, API.GROUP_MODIFYGROUPCIRCLE, params, responseHandlers.group_modifygroupcircle);

	}

	public class GroupListAdapter extends BaseAdapter {

		@Override
		public void notifyDataSetChanged() {
			if (status == Status.friend) {
				friendsMap = data.relationship.friendsMap;
			} else if (status == Status.group_circle) {
				groups = data.relationship.groupCircles;
				groupCirclesMap = data.relationship.groupCirclesMap;
			} else {
				if (status == Status.square) {
					groups = data.relationship.squares;
				} else if (status == Status.list_group) {
					groups = currentGroupCircle.groups;
				} else {
					groups = data.relationship.groups;
				}
				groupsMap = data.relationship.groupsMap;
			}
			super.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			int size = 0;
			if (status == Status.friend || status == Status.card_friend) {
				size = friends.length;
			} else {
				size = groups.size();
			}
			return size;
		}

		@Override
		public Object getItem(int position) {
			Object item = null;
			if (status == Status.friend || status == Status.card_friend) {
				item = friends[position];
			} else {
				item = groups.get(position);
			}
			return item;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			GroupHolder holder = null;
			if (convertView == null) {
				holder = new GroupHolder();
				convertView = mInflater.inflate(R.layout.activity_group_list_item, null);
				holder.headView = (ImageView) convertView.findViewById(R.id.head);
				holder.nameView = (TextView) convertView.findViewById(R.id.title);
				holder.descriptionView = (TextView) convertView.findViewById(R.id.description);
				convertView.setTag(holder);
			} else {
				holder = (GroupHolder) convertView.getTag();
			}
			if (status == Status.friend || status == Status.card_friend) {
				Friend friend = friendsMap.get(friends[position]);
				// holder.headView.setImageBitmap(bitmap);
				fileHandlers.getHeadImage(friend.head, holder.headView, viewManage.options50);
				if (friend.alias != null && !friend.alias.equals("")) {
					holder.nameView.setText(friend.alias + "(" + friend.nickName + ")");
				} else {
					holder.nameView.setText(friend.nickName);
				}
				holder.descriptionView.setText(friend.mainBusiness);
			} else if (status == Status.group_circle) {
				Group group = data.relationship.groupsMap.get(gid);
				GroupCircle groupCircle = groupCirclesMap.get(groups.get(position));
				holder.nameView.setText(groupCircle.name);
				if (group != null)
					fileHandlers.getHeadImage(group.icon, holder.headView, viewManage.options50);
			} else {
				Group group = groupsMap.get(groups.get(position));
				// holder.headView.setImageBitmap(bitmap);
				fileHandlers.getHeadImage(group.icon, holder.headView, viewManage.options50);
				holder.nameView.setText(group.name);
				holder.descriptionView.setText(group.description);
			}
			return convertView;
		}
	}

	public class GroupHolder {
		public ImageView headView;
		public TextView nameView;
		public TextView descriptionView;
	}
}
