package com.open.welinks.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.open.welink.R;
import com.open.welink.R.color;
import com.open.welinks.GroupListActivity;
import com.open.welinks.customView.Alert;
import com.open.welinks.customView.Alert.AlertInputDialog;
import com.open.welinks.customView.Alert.AlertInputDialog.OnDialogClickListener;
import com.open.welinks.model.API;
import com.open.welinks.model.Constant;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.Relationship.GroupCircle;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.DataHandler;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.model.TaskManageHolder;
import com.open.welinks.utils.InputMethodManagerUtils;
import com.open.welinks.view.GroupListView;
import com.open.welinks.view.GroupListView.GroupCircleDialogAdapter;

public class GroupListController {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();

	public TaskManageHolder taskManageHolder = TaskManageHolder.getInstance();

	public GroupListActivity thisActivity;
	public GroupListView thisView;

	public OnClickListener mOnClickListener;
	public OnTouchListener mOnTouchListener;
	public OnItemClickListener mOnItemClickListener;
	public OnItemLongClickListener mOnItemLongClickListener;
	public com.open.welinks.customView.ThreeChoicesView.OnItemClickListener mOnThreeChoiceItemClickListener;
	public ListController listController;

	public ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
	public Gson gson = new Gson();

	public List<String> groups;
	public List<String> editorGroups;
	public String[] friends;
	public Map<String, Group> groupsMap;
	public Map<String, Friend> friendsMap;
	public Map<String, GroupCircle> groupCirclesMap;

	public Status status;
	public String gid;

	public boolean isGroupEditor = false;
	public int seletedRid = 0;

	public GroupCircle currentGroupCircle, onTouchDownGroupCircle;

	public static enum Status {
		square, friend, share_group, message_group, list_group, card_friend, card_group
	}

	public GroupListController(GroupListActivity thisActivity) {
		this.thisActivity = thisActivity;
	}

	public void onCreate() {
		taskManageHolder.viewManage.groupListActivity = thisActivity;
		String type = thisActivity.getIntent().getStringExtra("type");
		if ("list_group".equals(type)) {
			this.status = Status.list_group;
		} else if ("share".equals(type)) {
			this.status = Status.square;
		} else if ("message".equals(type)) {
			this.status = Status.friend;
		} else if ("sendCard".equals(type)) {
			this.status = Status.card_friend;
		}
	}

	public void initData() {
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
		} else {
			if (status == Status.square) {
				groups = data.relationship.squares;
			} else if (status == Status.list_group) {
				if (data.relationship != null && data.relationship.groupCirclesMap != null && data.relationship.groupCircles != null) {
					if (taskManageHolder.viewManage.shareSubView.currentGroupCircle != null) {
						currentGroupCircle = data.relationship.groupCirclesMap.get(taskManageHolder.viewManage.shareSubView.currentGroupCircle.rid + "");

					} else {
						currentGroupCircle = data.relationship.groupCirclesMap.get(data.relationship.groupCircles.get(0));
					}
					if (currentGroupCircle != null && currentGroupCircle.groups != null) {
						groups = currentGroupCircle.groups;
						thisView.showGroupCircles();
					} else {
						thisActivity.finish();
					}
				} else {
					thisActivity.finish();
				}
			} else {
				groups = data.relationship.groups;
			}
			groupsMap = data.relationship.groupsMap;
		}
		thisView.groupListAdapter = thisView.new GroupListAdapter();
		thisView.groupListContainer.setAdapter(thisView.groupListAdapter);
	}

	public void initializeListeners() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(thisView.backView)) {
					if (isGroupEditor) {
						cancelEditor();
					} else {
						thisActivity.finish();
					}
				} else if (view.equals(thisView.rightContainerLinearLayout)) {
					thisView.changePopupWindow(false);
				} else if (view.equals(thisView.settingView)) {
					if (thisView.buttomBarView.getVisibility() == View.VISIBLE) {
						thisView.buttomBarView.setVisibility(View.GONE);
					} else {
						thisView.buttomBarView.setVisibility(View.VISIBLE);
					}
				} else if (view.equals(thisView.background)) {
					thisView.changePopupWindow(false);
				} else if (view.equals(thisView.groupEditorConfirmView)) {
					if (editorGroups.size() > 0) {
						seletedRid = 0;
						thisView.changePopupWindow(true);
					}
				} else if (view.equals(thisView.dialogGroupEditorConfirm)) {
					moveGroupsToCircle();
				} else if (view.equals(thisView.groupEditorCancelView)) {
					cancelEditor();
				} else if (view.equals(thisView.buttonOne)) {
					createGroupCircle();
				} else if (view.equals(thisView.buttonTwo)) {
					modifyGroupCircleName();
				} else if (view.equals(thisView.buttonThree)) {
					removeGroupCircle(currentGroupCircle);
				}
			}

		};
		mOnThreeChoiceItemClickListener = thisView.threeChoicesView.new OnItemClickListener() {
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
				thisView.groupListAdapter.notifyDataSetChanged();
			}
		};

		mOnItemClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				if (status == Status.list_group) {
					if (isGroupEditor) {
						String gid = groups.get(position);
						if (editorGroups.contains(gid)) {
							editorGroups.remove(gid);
						} else {
							editorGroups.add(gid);
						}
						if (editorGroups.size() > 0) {
							thisView.groupEditorConfirmView.setText("移动" + editorGroups.size() + "个群组到...");
							thisView.groupEditorConfirmView.setTextColor(Color.BLACK);
						} else {
							thisView.groupEditorConfirmView.setText("移动到...");
							thisView.groupEditorConfirmView.setTextColor(color.gray80);
						}
						thisView.groupListAdapter.notifyDataSetChanged();
					} else {
						String gid = groups.get(position);
						thisView.businessCardPopView.cardView.setSmallBusinessCardContent(thisView.businessCardPopView.cardView.TYPE_GROUP, gid);
						thisView.businessCardPopView.showUserCardDialogView();
						DataHandler.getGroupBoards(gid);
					}
				} else {
					Alert.createDialog(thisActivity).setTitle(getDialogTitle(position)).setOnConfirmClickListener(new OnDialogClickListener() {

						@Override
						public void onClick(AlertInputDialog dialog) {
							Intent intent = new Intent();
							if (status == Status.friend || status == Status.card_friend) {
								intent.putExtra("key", friendsMap.get(friends[position]).phone);
								intent.putExtra("type", "message");
								intent.putExtra("sendType", "point");
							} else {
								intent.putExtra("key", String.valueOf(groupsMap.get(groups.get(position)).gid));
								if (status == Status.share_group || status == Status.square) {
									intent.putExtra("type", "share");
								} else if (status == Status.message_group || status == Status.card_group) {
									intent.putExtra("type", "message");
									intent.putExtra("sendType", "group");
								}
							}
							thisActivity.setResult(Activity.RESULT_OK, intent);
							thisActivity.finish();
						}
					}).show();
				}
			}
		};
		mOnItemLongClickListener = new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				if (status == Status.list_group && !isGroupEditor) {
					thisView.backTitileView.setText("分组管理");
					Vibrator vibrator = (Vibrator) thisActivity.getSystemService(Service.VIBRATOR_SERVICE);
					long[] pattern = { 100, 100, 300 };
					vibrator.vibrate(pattern, -1);
					isGroupEditor = true;
					editorGroups = new ArrayList<String>();
					thisView.groupListAdapter.notifyDataSetChanged();
					thisView.groupEditorConfirmView.setText("移动到...");
					thisView.groupEditorConfirmView.setTextColor(color.gray80);
					thisView.groupEditor.setVisibility(View.VISIBLE);
				}
				return true;
			}
		};
		bindEvent();
	}

	public void bindEvent() {
		thisView.threeChoicesView.setOnItemClickListener(mOnThreeChoiceItemClickListener);
		thisView.groupListContainer.setOnItemClickListener(mOnItemClickListener);
		thisView.groupListContainer.setOnItemLongClickListener(mOnItemLongClickListener);
		thisView.backView.setOnClickListener(mOnClickListener);
		thisView.groupEditorConfirmView.setOnClickListener(mOnClickListener);
		thisView.groupEditorCancelView.setOnClickListener(mOnClickListener);

		if (thisView.dialogGroupEditorConfirm != null) {
			thisView.dialogGroupEditorConfirm.setOnClickListener(mOnClickListener);
		}
		if (thisView.rightContainerLinearLayout != null)
			thisView.rightContainerLinearLayout.setOnClickListener(mOnClickListener);
		if (thisView.buttonOne != null)
			thisView.buttonOne.setOnClickListener(mOnClickListener);
		if (thisView.buttonTwo != null)
			thisView.buttonTwo.setOnClickListener(mOnClickListener);
		if (thisView.buttonThree != null)
			thisView.buttonThree.setOnClickListener(mOnClickListener);
		if (thisView.settingView != null)
			thisView.settingView.setOnClickListener(mOnClickListener);
		if (thisView.background != null)
			thisView.background.setOnClickListener(mOnClickListener);
	}

	public class ListController extends DragSortController implements DragSortListView.DropListener, DragSortListView.RemoveListener, android.widget.AdapterView.OnItemClickListener {
		private GroupCircleDialogAdapter adapter;

		// private DragSortListView listView;

		public ListController(DragSortListView dslv, GroupCircleDialogAdapter dialogAdapter) {
			super(dslv);
			this.adapter = dialogAdapter;
			// this.listView = dslv;
			setRemoveEnabled(true);
			setRemoveMode(DragSortController.FLING_REMOVE);
			setDragInitMode(DragSortController.ON_LONG_PRESS);
		}

		@Override
		public void drop(int from, int to) {
			if (from != to) {
				String groupCircle = data.relationship.groupCircles.remove(from);
				data.relationship.groupCircles.add(to, groupCircle);
				data.relationship.isModified = true;
				adapter.notifyDataSetChanged();
				modifyGroupCirclesSequence(gson.toJson(data.relationship.groupCircles), 0, "");
			}
		}

		@Override
		public void remove(final int which) {
			GroupCircle groupCircle = (GroupCircle) adapter.getItem(which);
			removeGroupCircle(groupCircle);
		}

		@Override
		public boolean onDown(MotionEvent ev) {
			if (isGroupEditor)
				return false;
			int res = super.dragHandleHitPosition(ev);
			if (res != -1) {
				GroupCircle groupCircle = (GroupCircle) adapter.getItem(res);
				if (groupCircle.rid == 8888888) {
					setRemoveEnabled(false);
				} else {
					setRemoveEnabled(true);
				}
			}
			return super.onDown(ev);
		}

		@Override
		public int startDragPosition(MotionEvent ev) {
			return super.dragHandleHitPosition(ev);
		}

		@Override
		public View onCreateFloatView(int position) {
			Vibrator vibrator = (Vibrator) thisActivity.getSystemService(Service.VIBRATOR_SERVICE);
			long[] pattern = { 100, 100, 300 };
			vibrator.vibrate(pattern, -1);

			View view = adapter.getView(position, null, thisView.groupCircleList);
			view.setBackgroundResource(R.drawable.card_login_background_press);
			return view;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			GroupCircle groupCircle = (GroupCircle) adapter.getItem(position);
			if (groupCircle != null) {
				if (isGroupEditor && thisView.settingView.getVisibility() == View.GONE) {
					if (seletedRid != groupCircle.rid) {
						seletedRid = groupCircle.rid;
						thisView.dialogAdapter.notifyDataSetChanged();
					}
				} else {
					currentGroupCircle = groupCircle;
					thisView.showGroupCircles();
					thisView.changePopupWindow(false);
				}
			}
		}
	}

	public String getDialogTitle(int position) {
		String title = "是否";
		if (status == Status.friend) {
			title += "分享给好友：【" + friendsMap.get(friends[position]).nickName + "】?";
		} else if (status == Status.message_group) {
			title += "分享给群组：【" + groupsMap.get(groups.get(position)).name + "】?";
		} else if (status == Status.square) {
			title += "分享到社区：【" + groupsMap.get(groups.get(position)).name + "】?";
		} else if (status == Status.share_group) {
			title += "分享到群组：【" + groupsMap.get(groups.get(position)).name + "】?";
		} else if (status == Status.card_friend) {
			title += "发送名片给好友：【" + friendsMap.get(friends[position]).nickName + "】?";
		} else if (status == Status.card_group) {
			title += "发送名片到群组：【" + groupsMap.get(groups.get(position)).name + "】?";
		}
		return title;
	}

	public void cancelEditor() {
		thisView.backTitileView.setText("群组列表");
		isGroupEditor = false;
		editorGroups.clear();
		thisView.groupEditor.setVisibility(View.GONE);
		thisView.showGroupCircles();
	}

	public void createGroupCircle() {
		Alert.createInputDialog(thisActivity).setInputHint("请输入分组名称").setTitle("创建群分组").setOnConfirmClickListener(new OnDialogClickListener() {

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
					User currentUser = data.userInformation.currentUser;
					params.addBodyParameter("phone", currentUser.phone);
					params.addBodyParameter("accessKey", currentUser.accessKey);
					params.addBodyParameter("name", groupCircle.name);
					params.addBodyParameter("rid", String.valueOf(groupCircle.rid));

					httpUtils.send(HttpMethod.POST, API.GROUP_CREATEGROUPCIRCLE, params, responseHandlers.group_creategroupcircle);
					thisView.showGroupCircles();
					InputMethodManagerUtils mInputMethodManagerUtils = new InputMethodManagerUtils(thisActivity);
					mInputMethodManagerUtils.toggleSoftInput();
				}
			}

		}).show();
	}

	public void modifyGroupCircleName() {
		Alert.createInputDialog(thisActivity).setTitle("修改分组名称").setInputHint("请输入名称").setInputText(currentGroupCircle.name).setOnConfirmClickListener(new OnDialogClickListener() {

			@Override
			public void onClick(AlertInputDialog dialog) {
				String name = dialog.getInputText().trim();
				if (!"".equals(name) && !currentGroupCircle.name.equals(name)) {
					GroupCircle circle = data.relationship.groupCirclesMap.get(currentGroupCircle.rid + "");
					circle.name = name;
					currentGroupCircle = circle;
					thisView.showGroupCircles();
					data.relationship.isModified = true;
					modifyGroupCirclesSequence("", circle.rid, name);
				}

			}
		}).show();

	}

	public void removeGroupCircle(final GroupCircle groupCircle) {
		Alert.createDialog(thisActivity).setTitle("是否删除群组分组【" + groupCircle.name + "】？删除后此分组中群组将移动到【默认分组】").setOnConfirmClickListener(new OnDialogClickListener() {

			@Override
			public void onClick(AlertInputDialog dialog) {
				data.relationship.groupCircles.remove(String.valueOf(groupCircle.rid));
				if (currentGroupCircle.rid == groupCircle.rid) {
					currentGroupCircle = data.relationship.groupCirclesMap.get(data.relationship.groupCircles.get(0));
				}
				for (String gid : data.relationship.groupCirclesMap.get(String.valueOf(groupCircle.rid)).groups) {
					data.relationship.groupCirclesMap.get(Constant.DEFAULTCIRCLEID + "").groups.add(gid);
				}
				thisView.showGroupCircles();

				RequestParams params = new RequestParams();
				HttpUtils httpUtils = new HttpUtils();
				User currentUser = data.userInformation.currentUser;
				params.addBodyParameter("phone", currentUser.phone);
				params.addBodyParameter("accessKey", currentUser.accessKey);
				params.addBodyParameter("rid", String.valueOf(groupCircle.rid));
				httpUtils.send(HttpMethod.POST, API.GROUP_DELETEGROUPCIRCLE, params, responseHandlers.group_deletegroupcircle);
			}
		}).setOnCancelClickListener(new OnDialogClickListener() {

			@Override
			public void onClick(AlertInputDialog dialog) {
				thisView.showGroupCircles();
			}
		}).show();
	}

	public void moveGroupsToCircle() {
		for (GroupCircle circle : data.relationship.groupCirclesMap.values()) {
			if (circle.rid == seletedRid) {
				for (String gid : editorGroups) {
					if (!circle.groups.contains(gid))
						circle.groups.add(gid);
				}
			} else {
				for (String gid : editorGroups) {
					circle.groups.remove(gid);
				}
			}
		}
		data.relationship.isModified = true;
		thisView.changePopupWindow(false);

		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		User currentUser = data.userInformation.currentUser;
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("groups", gson.toJson(editorGroups));
		params.addBodyParameter("groupCircles", gson.toJson(data.relationship.groupCircles));
		params.addBodyParameter("groupCirclesMap", gson.toJson(data.relationship.groupCirclesMap));
		params.addBodyParameter("rid", seletedRid + "");
		thisActivity.log.e(gson.toJson(data.relationship.groupCirclesMap));
		httpUtils.send(HttpMethod.POST, API.GROUP_MOVEGROUPSTOCIRCLE, params, responseHandlers.group_movegroupstocircle);

		cancelEditor();
	}

	public void moveGroupsToCircle(List<String> groups, int fromRid, int toRid, List<String> fromGroups, List<String> toGroups) {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		User currentUser = data.userInformation.currentUser;
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("groups", gson.toJson(groups));
		params.addBodyParameter("fromGroups", gson.toJson(fromGroups));
		params.addBodyParameter("toGroups", gson.toJson(toGroups));
		params.addBodyParameter("fromRid", fromRid + "");
		params.addBodyParameter("toRid", toRid + "");

		httpUtils.send(HttpMethod.POST, API.GROUP_MOVEGROUPSTOCIRCLE, params, responseHandlers.group_movegroupstocircle);
	}

	public void modifyGroupCirclesSequence(String sequenceListString, int rid, String name) {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		User currentUser = data.userInformation.currentUser;
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("groupCircles", sequenceListString);
		params.addBodyParameter("name", name);
		params.addBodyParameter("rid", rid + "");

		httpUtils.send(HttpMethod.POST, API.GROUP_MODIFYGROUPCIRCLE, params, responseHandlers.group_modifygroupcircle);

	}

	public void onResume() {
		if (thisView.dialogAdapter != null) {
			thisView.dialogAdapter.notifyDataSetChanged();
		}
		thisView.businessCardPopView.dismissUserCardDialogView();
	}

	public void onDestroy() {
		taskManageHolder.viewManage.groupListActivity = null;
	}

	public void onBackPressed() {
		if (thisView.popDialogView.isShowing()) {
			thisView.changePopupWindow(false);
		} else if (isGroupEditor) {
			cancelEditor();
		} else {
			thisActivity.finish();
		}
	}

	public void finish() {
		if (taskManageHolder.viewManage.shareSubView != null) {
			taskManageHolder.viewManage.shareSubView.setGroupsDialogContent(currentGroupCircle);
			taskManageHolder.viewManage.shareSubView.showShareMessages();
		}
	}
}
