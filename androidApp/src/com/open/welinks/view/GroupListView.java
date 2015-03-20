package com.open.welinks.view;

import java.util.List;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import com.mobeta.android.dslv.DragSortListView;
import com.open.welinks.GroupListActivity;
import com.open.welink.R;
import com.open.welinks.controller.GroupListController;
import com.open.welinks.controller.GroupListController.Status;
import com.open.welinks.customView.SmallBusinessCardPopView;
import com.open.welinks.customView.ThreeChoicesView;
import com.open.welinks.model.Data;
import com.open.welinks.model.Parser;
import com.open.welinks.model.TaskManageHolder;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.Relationship.GroupCircle;
import com.open.welinks.utils.BaseDataUtils;

public class GroupListView {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();

	public GroupListActivity thisActivity;
	public GroupListController thisController;

	public TaskManageHolder taskManageHolder = TaskManageHolder.getInstance();

	public LayoutInflater mInflater;

	public RelativeLayout backView, rightContainer, maxView, backMaxView;
	public TextView dialogGroupEditorConfirm, dialogGroupEditorCancel, groupEditorConfirmView, groupEditorCancelView, backTitileView, titleView, sectionNameTextView, buttonOneText, buttonTwoText, buttonThreeText;
	public LinearLayout rightContainerLinearLayout;
	public ImageView moreView, rditorLine, backImageView;
	public View groupEditor, dialogGroupEditor, dialogView, buttomBarView, settingView, buttonOne, buttonTwo, buttonThree, background, onTouchDownView, onLongPressView;
	public PopupWindow popDialogView;
	public ListView groupListContainer;
	public ThreeChoicesView threeChoicesView;

	public DragSortListView groupCircleList;
	public GroupCircleDialogAdapter dialogAdapter;

	public GroupListAdapter groupListAdapter;

	public DisplayMetrics displayMetrics;

	public SmallBusinessCardPopView businessCardPopView;

	public GroupListView(GroupListActivity thisActivity) {
		this.thisActivity = thisActivity;
	}

	public void initViews() {
		displayMetrics = new DisplayMetrics();
		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		mInflater = thisActivity.getLayoutInflater();

		thisActivity.setContentView(R.layout.activity_group_list);

		this.groupEditor = thisActivity.findViewById(R.id.groupEditor);

		this.maxView = (RelativeLayout) thisActivity.findViewById(R.id.maxView);
		this.backView = (RelativeLayout) thisActivity.findViewById(R.id.backView);
		this.backTitileView = (TextView) thisActivity.findViewById(R.id.backTitleView);
		this.titleView = (TextView) thisActivity.findViewById(R.id.titleContent);

		this.backImageView = (ImageView) thisActivity.findViewById(R.id.backImageView);

		this.groupEditorConfirmView = (TextView) thisActivity.findViewById(R.id.confirm);
		this.groupEditorCancelView = (TextView) thisActivity.findViewById(R.id.cancel);
		this.rightContainer = (RelativeLayout) thisActivity.findViewById(R.id.rightContainer);
		this.backMaxView = (RelativeLayout) thisActivity.findViewById(R.id.backMaxView);

		this.groupListContainer = (ListView) thisActivity.findViewById(R.id.groupListContainer);
		// this.createGroupButton = new TextView(this);
		this.threeChoicesView = new ThreeChoicesView(thisActivity);
		this.threeChoicesView.setTwoChoice();

		businessCardPopView = new SmallBusinessCardPopView(thisActivity, maxView);
		businessCardPopView.cardView.setHot(false);

		backMaxView.setBackgroundColor(thisActivity.getResources().getColor(R.color.card_color));
		backImageView.setColorFilter(Color.WHITE);
		backTitileView.setTextColor(Color.WHITE);

		if (thisController.status == Status.list_group) {
			this.backTitileView.setText("群组列表");

			rightContainerLinearLayout = new LinearLayout(thisActivity);
			rightContainerLinearLayout.setPadding((int) (10 * displayMetrics.density), 0, (int) (20 * displayMetrics.density), 0);
			LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, (int) (48 * displayMetrics.density));
			rightContainerLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
			RelativeLayout.LayoutParams rightParams = (android.widget.RelativeLayout.LayoutParams) rightContainer.getLayoutParams();
			rightParams.rightMargin = 0;
			moreView = new ImageView(thisActivity);
			moreView.setTranslationY((int) (30 * displayMetrics.density));
			moreView.setImageResource(R.drawable.subscript_triangle);
			RelativeLayout.LayoutParams infomationParams = new RelativeLayout.LayoutParams((int) (7 * displayMetrics.density), (int) (7 * displayMetrics.density));

			sectionNameTextView = new TextView(thisActivity);
			sectionNameTextView.setSingleLine();
			sectionNameTextView.setTextColor(Color.WHITE);
			sectionNameTextView.setTextSize(18);

			RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT, (int) (48 * displayMetrics.density));
			rightContainerLinearLayout.addView(sectionNameTextView, textViewParams);
			sectionNameTextView.setGravity(Gravity.CENTER_VERTICAL);
			rightContainerLinearLayout.addView(moreView, infomationParams);
			rightContainer.addView(rightContainerLinearLayout, lineParams);

			if (data.relationship != null && data.relationship.groupCirclesMap != null && data.relationship.groupCircles != null) {
				thisController.currentGroupCircle = data.relationship.groupCirclesMap.get(data.relationship.groupCircles.get(0));
			} else {
				thisActivity.finish();
			}

			initializationGroupCirclesDialog();
		} else if (thisController.status == Status.card_friend) {
			this.backTitileView.setText("发送名片");
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			this.threeChoicesView.setButtonOneText("全部好友");
			this.threeChoicesView.setButtonThreeText("群组");
			this.rightContainer.addView(this.threeChoicesView, layoutParams);
		} else {
			this.backTitileView.setText("分享");
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			if (thisController.status == Status.friend) {
				this.threeChoicesView.setButtonOneText("全部好友");
				this.threeChoicesView.setButtonThreeText("群组");
			} else if (thisController.status == Status.square) {
				this.threeChoicesView.setButtonOneText("社区");
				this.threeChoicesView.setButtonThreeText("群组");
			}
			this.rightContainer.addView(this.threeChoicesView, layoutParams);
		}
	}

	@SuppressWarnings("deprecation")
	public void initializationGroupCirclesDialog() {
		dialogView = mInflater.inflate(R.layout.dialog_listview, null);
		groupCircleList = (DragSortListView) dialogView.findViewById(R.id.content);
		buttomBarView = dialogView.findViewById(R.id.buttons);
		settingView = dialogView.findViewById(R.id.manage);
		background = dialogView.findViewById(R.id.background);
		buttonOne = dialogView.findViewById(R.id.buttonOne);
		buttonTwo = dialogView.findViewById(R.id.buttonTwo);
		buttonThree = dialogView.findViewById(R.id.buttonThree);
		dialogGroupEditor = dialogView.findViewById(R.id.groupEditor);
		rditorLine = (ImageView) dialogView.findViewById(R.id.rditorLine);
		buttonOneText = (TextView) dialogView.findViewById(R.id.buttonOneText);
		buttonTwoText = (TextView) dialogView.findViewById(R.id.buttonTwoText);
		buttonThreeText = (TextView) dialogView.findViewById(R.id.buttonThreeText);
		dialogGroupEditorConfirm = (TextView) dialogView.findViewById(R.id.confirm);
		dialogGroupEditorCancel = (TextView) dialogView.findViewById(R.id.cancel);
		buttonOneText.setText("新建分组");
		buttonTwoText.setText("修改组名");
		buttonThreeText.setText("删除分组");

		popDialogView = new PopupWindow(dialogView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		popDialogView.setBackgroundDrawable(new BitmapDrawable());

	}

	public void changePopupWindow(boolean isEditor) {
		if (popDialogView.isShowing()) {
			popDialogView.dismiss();
		} else {
			if (buttomBarView.getVisibility() == View.VISIBLE)
				buttomBarView.setVisibility(View.GONE);
			if (isEditor) {
				dialogGroupEditor.setVisibility(View.VISIBLE);
				rditorLine.setVisibility(View.VISIBLE);
				settingView.setVisibility(View.GONE);
			} else {
				dialogGroupEditor.setVisibility(View.GONE);
				rditorLine.setVisibility(View.GONE);
				settingView.setVisibility(View.VISIBLE);
			}
			if (thisController.currentGroupCircle.rid == 8888888) {
				buttonTwo.setVisibility(View.GONE);
				buttonThree.setVisibility(View.GONE);
			} else {
				buttonTwo.setVisibility(View.VISIBLE);
				buttonThree.setVisibility(View.VISIBLE);
			}
			popDialogView.showAtLocation(maxView, Gravity.CENTER, 0, 0);
		}
	}

	public void showGroupCircles() {
		if (dialogAdapter == null) {
			dialogAdapter = new GroupCircleDialogAdapter();
			groupCircleList.setAdapter(dialogAdapter);
			thisController.listController = thisController.new ListController(groupCircleList, dialogAdapter);
			groupCircleList.setDropListener(thisController.listController);
			groupCircleList.setRemoveListener(thisController.listController);
			groupCircleList.setFloatViewManager(thisController.listController);
			groupCircleList.setOnTouchListener(thisController.listController);
			groupCircleList.setOnItemClickListener(thisController.listController);
		} else {
			dialogAdapter.notifyDataSetChanged();
		}
		if (thisController.currentGroupCircle != null) {
			sectionNameTextView.setText(thisController.currentGroupCircle.name);
		}
	}

	public class GroupListAdapter extends BaseAdapter {

		@Override
		public void notifyDataSetChanged() {
			if (thisController.status == Status.friend) {
				thisController.friendsMap = data.relationship.friendsMap;
			} else {
				if (thisController.status == Status.square) {
					thisController.groups = data.relationship.squares;
				} else if (thisController.status == Status.list_group) {
					thisController.groups = thisController.currentGroupCircle.groups;
				} else {
					thisController.groups = data.relationship.groups;
				}
				thisController.groupsMap = data.relationship.groupsMap;
			}
			super.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			int size = 0;
			if (thisController.status == Status.friend || thisController.status == Status.card_friend) {
				size = thisController.friends.length;
			} else {
				size = thisController.groups.size();
			}
			return size;
		}

		@Override
		public Object getItem(int position) {
			Object item = null;
			if (thisController.status == Status.friend || thisController.status == Status.card_friend) {
				item = thisController.friends[position];
			} else {
				item = thisController.groups.get(position);
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
				convertView = mInflater.inflate(R.layout.group_list_item, null);
				holder.headView = (ImageView) convertView.findViewById(R.id.head);
				holder.checkBoxView = (ImageView) convertView.findViewById(R.id.checkBox);
				holder.nameView = (TextView) convertView.findViewById(R.id.title);
				holder.descriptionView = (TextView) convertView.findViewById(R.id.description);
				((RelativeLayout.LayoutParams) holder.checkBoxView.getLayoutParams()).rightMargin = BaseDataUtils.dpToPxint(25);
				convertView.setTag(holder);
			} else {
				holder = (GroupHolder) convertView.getTag();
			}
			if (thisController.status == Status.friend || thisController.status == Status.card_friend) {
				Friend friend = thisController.friendsMap.get(thisController.friends[position]);
				taskManageHolder.fileHandler.getHeadImage(friend.head, holder.headView, taskManageHolder.viewManage.options50);
				if (friend.alias != null && !friend.alias.equals("")) {
					holder.nameView.setText(friend.alias + "(" + friend.nickName + ")");
				} else {
					holder.nameView.setText(friend.nickName);
				}
				holder.descriptionView.setText(friend.mainBusiness);
			} else {
				String gid = thisController.groups.get(position);
				Group group = thisController.groupsMap.get(gid);
				if (group != null) {
					taskManageHolder.fileHandler.getHeadImage(group.icon, holder.headView, taskManageHolder.viewManage.options50);
					holder.nameView.setText(group.name);
					if ("请输入群组描述信息".equals(group.description)) {
						holder.descriptionView.setText("");
					} else {
						holder.descriptionView.setText(group.description);
					}
					if (thisController.isGroupEditor) {
						holder.checkBoxView.setVisibility(View.VISIBLE);
						if (thisController.editorGroups.contains(gid)) {
							holder.checkBoxView.setImageResource(R.drawable.icon_checkbox_checked);
						} else {
							holder.checkBoxView.setImageResource(R.drawable.icon_checkbox);
						}
					} else {
						holder.checkBoxView.setVisibility(View.GONE);
					}
				}
			}
			return convertView;
		}
	}

	public class GroupHolder {
		public ImageView headView, checkBoxView;
		public TextView nameView, descriptionView;
	}

	public class GroupCircleDialogAdapter extends BaseAdapter {
		private List<String> groupCircles;

		public GroupCircleDialogAdapter() {
			groupCircles = data.relationship.groupCircles;
		}

		@Override
		public void notifyDataSetChanged() {
			groupCircles = data.relationship.groupCircles;
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
				convertView = mInflater.inflate(R.layout.group_list_dialog_item, null, false);
				holder.selectedStatusView = (ImageView) convertView.findViewById(R.id.selectedStatus);
				holder.statusView = (ImageView) convertView.findViewById(R.id.status);
				holder.nameView = (TextView) convertView.findViewById(R.id.name);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			GroupCircle groupCircle = (GroupCircle) getItem(position);
			if (groupCircle != null) {
				holder.nameView.setText(groupCircle.name);
				if (thisController.currentGroupCircle != null && thisController.currentGroupCircle.rid == groupCircle.rid) {
					holder.selectedStatusView.setVisibility(View.VISIBLE);
				} else {
					holder.selectedStatusView.setVisibility(View.INVISIBLE);
				}
				if (thisController.isGroupEditor && settingView.getVisibility() == View.GONE) {
					holder.statusView.setVisibility(View.VISIBLE);
					if (groupCircle.rid == thisController.seletedRid) {
						holder.statusView.setImageResource(R.drawable.icon_checkbox_checked);
					} else {
						holder.statusView.setImageResource(R.drawable.icon_checkbox);
					}
				} else {
					holder.statusView.setVisibility(View.GONE);
				}
			}
			return convertView;
		}

		class Holder {
			public ImageView statusView, selectedStatusView;
			public TextView nameView;
		}
	}
}
