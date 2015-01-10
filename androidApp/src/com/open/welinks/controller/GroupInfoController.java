package com.open.welinks.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.MyLog;
import com.open.welinks.BusinessCardActivity;
import com.open.welinks.CreateGroupLocationActivity;
import com.open.welinks.CropActivity;
import com.open.welinks.GroupMemberManageActivity;
import com.open.welinks.ImagesDirectoryActivity;
import com.open.welinks.LocationActivity;
import com.open.welinks.customListener.OnDownloadListener;
import com.open.welinks.customListener.OnUploadLoadingListener;
import com.open.welinks.customView.Alert;
import com.open.welinks.customView.Alert.AlertInputDialog;
import com.open.welinks.customView.Alert.AlertInputDialog.OnDialogClickListener;
import com.open.welinks.customView.Alert.AlertSelectDialog;
import com.open.welinks.customView.Alert.AlertSelectDialog.OnDialogClickListener2;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.utils.MCImageUtils;
import com.open.welinks.utils.SHA1;
import com.open.welinks.utils.StreamParser;
import com.open.welinks.view.GroupInfoView;
import com.open.welinks.view.ViewManage;

public class GroupInfoController {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();
	public String tag = "GroupInfoController";
	public MyLog log = new MyLog(tag, true);

	public Context context;
	public GroupInfoView thisView;
	public GroupInfoController thisController;
	public Activity thisActivity;

	public OnClickListener mOnClickListener;
	public OnUploadLoadingListener uploadLoadingListener;
	public OnSeekBarChangeListener mOnSeekBarChangeListener;
	public OnDownloadListener downloadListener;

	public Group currentGroup;

	public UploadMultipartList uploadMultipartList = UploadMultipartList.getInstance();
	public File tempFile;

	public ViewManage viewManage = ViewManage.getInstance();

	public GroupInfoController(Activity thisActivity) {
		this.context = thisActivity;
		this.thisController = this;
		this.thisActivity = thisActivity;
	}

	public void onCreate() {
		String gid = thisActivity.getIntent().getStringExtra("gid");
		if (gid != null && !"".equals(gid)) {
			currentGroup = data.relationship.groupsMap.get(gid);
			if (currentGroup == null) {
				thisActivity.finish();
			} else {
				thisView.setData();
				// thisView.showGroupMembers();
			}
		} else {
			thisActivity.finish();
		}
	}

	public int REQUESTCODE_SELECT = 0x1, REQUESTCODE_TAKE = 0x2, REQUESTCODE_CAT = 0x3, TAG_EXIT = 0x99, TAG_EDIT = 0x98;

	public void initializeListeners() {
		downloadListener = new OnDownloadListener() {

			@Override
			public void onLoading(DownloadFile instance, int precent, int status) {
			}

			@Override
			public void onSuccess(final DownloadFile instance, int status) {
				thisView.imageLoader.displayImage("file://" + instance.path, (ImageView) instance.view);
			}

			@Override
			public void onFailure(DownloadFile instance, int status) {
			}
		};
		mOnSeekBarChangeListener = new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				String tag = (String) seekBar.getTag();
				boolean flag = false;
				if (seekBar.getProgress() > 50) {
					flag = true;
					seekBar.setProgress(100);
				} else {
					flag = false;
					seekBar.setProgress(0);
				}
				if (tag.equals("newMessageSettingBar")) {
					if (data.localStatus.localData != null) {
						if (data.localStatus.localData.newMessagePowerMap != null) {
							data.localStatus.localData.newMessagePowerMap.put(currentGroup.gid + "", flag);
						} else {
							data.localStatus.localData.newMessagePowerMap = new HashMap<String, Boolean>();
							data.localStatus.localData.newMessagePowerMap.put(currentGroup.gid + "", flag);
						}
						data.localStatus.localData.isModified = true;
					} else {
						data.localStatus.localData = data.localStatus.new LocalData();
						data.localStatus.localData.newMessagePowerMap.put(currentGroup.gid + "", flag);
					}
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			}
		};
		uploadLoadingListener = new OnUploadLoadingListener() {

			@Override
			public void onSuccess(UploadMultipart instance, int time) {
				super.onSuccess(instance, time);
				if (instance.path.indexOf("heads") != -1) {
					RequestParams params = new RequestParams();
					params.addBodyParameter("icon", instance.fileName.substring(instance.fileName.indexOf("/") + 1));
					modifyGroupData(params);
				} else if (instance.path.indexOf("backgrounds") != -1) {
					RequestParams params = new RequestParams();
					params.addBodyParameter("cover", instance.fileName.substring(instance.fileName.indexOf("/") + 1));
					modifyGroupData(params);
				}
			}
		};
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(thisView.backView)) {
					thisActivity.finish();
				} else if (view.equals(thisView.headOptionView)) {
					selectPicture(REQUESTCODE_SELECT);
				} else if (view.equals(thisView.nickNameOptionView)) {
					displayDialog("nickName");
				} else if (view.equals(thisView.businessOptionView)) {
					displayDialog("business");
				} else if (view.equals(thisView.coverOptionView)) {
					selectPicture(CONVER_SET);
				} else if (view.equals(thisView.addressOptionView)) {
					Intent intent = new Intent(thisActivity, LocationActivity.class);
					intent.putExtra("latitude", currentGroup.latitude);
					intent.putExtra("longitude", currentGroup.longitude);
					thisActivity.startActivity(intent);
				} else if (view.equals(thisView.permissionOptionView)) {
					String permission = currentGroup.permission;
					log.e(permission + "----");
					Alert.createSelectDialog(thisActivity).setCurrentItem(permission).setOnConfirmClickListener(new OnDialogClickListener2() {

						@Override
						public void onClick(AlertSelectDialog dialog) {
							String type = dialog.currentItem;
							if (currentGroup.permission != null) {
								if (!currentGroup.permission.equals(type)) {
									data.relationship.groupsMap.get(currentGroup.gid + "").permission = type;
									data.relationship.isModified = true;
									RequestParams params = new RequestParams();
									params.addBodyParameter("permission", type);
									modifyGroupData(params);
								}
							} else {
								data.relationship.groupsMap.get(currentGroup.gid + "").permission = type;
								data.relationship.isModified = true;
								RequestParams params = new RequestParams();
								params.addBodyParameter("permission", type);
								modifyGroupData(params);
							}
						}
					}).show();
				} else if (view.equals(thisView.exit2DeleteGroup)) {
					Alert.createDialog(thisActivity).setTitle("您确定要删除并退出该房间？").setOnConfirmClickListener(new OnDialogClickListener() {

						@Override
						public void onClick(AlertInputDialog dialog) {
							thisActivity.finish();
							resetShareGroup();
						}
					}).show();
				} else if (view.equals(thisView.memberListTopView) || view.equals(thisView.memberListView)) {
					Intent intent = new Intent(thisActivity, GroupMemberManageActivity.class);// GroupMemberManageActivity
					intent.putExtra("gid", currentGroup.gid + "");
					intent.putExtra("type", 2);
					thisActivity.startActivity(intent);
				} else if (view.equals(thisView.cardOptionView)) {
					Intent intent = new Intent(thisActivity, BusinessCardActivity.class);
					intent.putExtra("type", "group");
					intent.putExtra("key", currentGroup.gid + "");
					thisActivity.startActivity(intent);
				}
			}
		};
	}

	public int CONVER_SET = 0x91;
	public int CONVER_SET_OK = 0x92;

	public void displayDialog(final String type) {
		data = parser.check();
		String title = "";
		String text = "";
		if ("nickName".equals(type)) {
			title = "请输入房间名称";
			text = currentGroup.name;
		} else if ("business".equals(type)) {
			title = "请输入房间描述";
			text = currentGroup.description;
		}
		Alert.createInputDialog(context).setTitle(title).setInputText(text).setOnConfirmClickListener(new OnDialogClickListener() {

			@Override
			public void onClick(AlertInputDialog dialog) {
				String content = dialog.getInputText().trim();
				if (!content.equals("")) {
					RequestParams params = new RequestParams();
					boolean flag = true;
					if ("nickName".equals(type)) {
						if (!content.equals(currentGroup.name)) {
							currentGroup.name = content;
							params.addBodyParameter("name", content);
						} else {
							flag = false;
						}
					} else if ("business".equals(type)) {
						if (!content.equals(currentGroup.description)) {
							currentGroup.description = content;
							params.addBodyParameter("description", content);
						} else {
							flag = false;
						}
					}
					if (flag) {
						thisView.fileHandlers.handler.post(new Runnable() {

							@Override
							public void run() {
								thisView.setData();
							}
						});
						modifyGroupData(params);
					}
				} else {
					thisView.fileHandlers.handler.post(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(context, "内容不能为空", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		}).show();
	}

	public Gson gson = new Gson();

	public void resetShareGroup() {
		parser.check();
		String gid = data.localStatus.localData.currentSelectedGroup;
		data.relationship.groups.remove(gid);
		data.localStatus.localData.currentSelectedGroup = "";
		viewManage.shareSubView.setGroupsDialogContent();
		if (data.relationship.groups.size() != 0) {
			data.localStatus.localData.currentSelectedGroup = data.relationship.groups.get(0);
			viewManage.shareSubView.shareTopMenuGroupName.setText(data.relationship.groupsMap.get(data.localStatus.localData.currentSelectedGroup).name);
			viewManage.shareSubView.setMenuNameBotton(data.relationship.groupsMap.get(data.localStatus.localData.currentSelectedGroup).name);
		} else {
			viewManage.shareSubView.shareTopMenuGroupName.setText("暂无房间");
			viewManage.shareSubView.setMenuNameBotton("暂无房间");
		}
		viewManage.shareSubView.shareMessageListBody.y = 0;
		data.relationship.isModified = true;
		viewManage.shareSubView.showShareMessages();
		List<String> subtractMembers = new ArrayList<String>();
		subtractMembers.add(data.userInformation.currentUser.phone);
		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
		String subtractMembersStr = gson.toJson(subtractMembers);
		modifyGroupMembers(API.GROUP_REMOVEMEMBERS, subtractMembersStr, responseHandlers.removeMembersCallBack);
	}

	public void modifyGroupMembers(String url, String membersContentString, RequestCallBack<String> callBack) {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		params.addBodyParameter("gid", currentGroup.gid + "");
		params.addBodyParameter("members", membersContentString);

		httpUtils.send(HttpMethod.POST, url, params, callBack);
	}

	public void bindEvent() {
		thisView.backView.setOnClickListener(mOnClickListener);
		thisView.headOptionView.setOnClickListener(mOnClickListener);
		thisView.nickNameOptionView.setOnClickListener(mOnClickListener);
		thisView.businessOptionView.setOnClickListener(mOnClickListener);
		thisView.coverOptionView.setOnClickListener(mOnClickListener);
		thisView.addressOptionView.setOnClickListener(mOnClickListener);
		thisView.permissionOptionView.setOnClickListener(mOnClickListener);
		thisView.exit2DeleteGroup.setOnClickListener(mOnClickListener);
		thisView.newMessageSettingBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		thisView.inCardBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		thisView.memberListTopView.setOnClickListener(mOnClickListener);
		thisView.memberListView.setOnClickListener(mOnClickListener);
		thisView.cardOptionView.setOnClickListener(mOnClickListener);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data2) {
		if (requestCode == REQUESTCODE_SELECT && resultCode == Activity.RESULT_OK) {
			if (this.data.tempData.selectedImageList != null && this.data.tempData.selectedImageList.size() != 0) {
				Uri selectedImage = Uri.parse("file://" + this.data.tempData.selectedImageList.get(0));
				startPhotoZoom(selectedImage);
			}
		} else if (requestCode == REQUESTCODE_TAKE && resultCode == Activity.RESULT_OK) {
			// Uri uri = Uri.fromFile(tempFile);
			// startPhotoZoom(uri);
		} else if (requestCode == REQUESTCODE_CAT && resultCode == Activity.RESULT_OK) {
			Map<String, Object> map = MCImageUtils.processImagesInformation(tempFile.getAbsolutePath(), thisView.fileHandlers.sdcardHeadImageFolder);
			String headFileName = (String) map.get("fileName");
			currentGroup.icon = headFileName;
			thisView.fileHandlers.getHeadImage(headFileName, thisView.headIvView, viewManage.options70);
			System.out.println((String) map.get("fileName"));
			uploadFile(tempFile.getAbsolutePath(), (String) map.get("fileName"), (byte[]) map.get("bytes"), UploadMultipart.UPLOAD_TYPE_HEAD);
		} else if (requestCode == CONVER_SET && resultCode == Activity.RESULT_OK) {
			if (this.data.tempData.selectedImageList != null && this.data.tempData.selectedImageList.size() != 0) {
				Intent intent = new Intent(thisActivity, CropActivity.class);
				intent.putExtra("path", data.tempData.selectedImageList.get(0));
				thisActivity.startActivityForResult(intent, CONVER_SET_OK);
			}
		} else if (requestCode == CONVER_SET_OK && resultCode == Activity.RESULT_OK) {
			byte[] bytes = data2.getByteArrayExtra("bitmap");
			String fileName = new SHA1().getDigestOfString(bytes) + ".osp";
			File file = new File(thisView.fileHandlers.sdcardBackImageFolder, fileName);
			try {
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				StreamParser.parseToFile(bytes, fileOutputStream);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			currentGroup.cover = fileName;
			viewManage.postNotifyView("ShareSubViewConver");
			uploadFile(file.getAbsolutePath(), fileName, bytes, UploadMultipart.UPLOAD_TYPE_BACKGROUND);
		}
	}

	public void modifyGroupData(RequestParams params) {
		class Location {
			String longitude, latitude;
		}
		Location location = new Location();
		location.longitude = currentGroup.longitude;
		location.latitude = currentGroup.latitude;
		log.e(location.longitude + "::::::::::::::::::::::::::::::::::::::::");
		HttpUtils httpUtils = new HttpUtils();
		User user = data.userInformation.currentUser;
		params.addBodyParameter("phone", user.phone);
		params.addBodyParameter("accessKey", user.accessKey);
		params.addBodyParameter("gid", currentGroup.gid + "");
//		params.addBodyParameter("location", gson.toJson(location));
		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
		httpUtils.send(HttpMethod.POST, API.GROUP_MODIFY, params, responseHandlers.group_modify);
	}

	public void uploadFile(final String filePath, final String fileName, final byte[] bytes, int uploadType) {
		UploadMultipart multipart = new UploadMultipart(filePath, fileName, bytes, uploadType);
		uploadMultipartList.addMultipart(multipart);
		multipart.setUploadLoadingListener(uploadLoadingListener);
	}

	void selectPicture(int requestCode) {
		data.tempData.selectedImageList = null;
		Intent intent = new Intent(thisActivity, ImagesDirectoryActivity.class);
		intent.putExtra("max", 1);
		thisActivity.startActivityForResult(intent, requestCode);
	}

	public void startPhotoZoom(Uri uri) {
		File sdFile = thisView.fileHandlers.sdcardHeadImageFolder;
		tempFile = new File(sdFile, "tempimage.png");
		int i = 1;
		while (tempFile.exists()) {
			tempFile = new File(sdFile, "tempimage" + (i++) + ".png");
		}
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 300);// 100
		intent.putExtra("outputY", 300);// 100
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
		thisActivity.startActivityForResult(intent, REQUESTCODE_CAT);
	}
}
