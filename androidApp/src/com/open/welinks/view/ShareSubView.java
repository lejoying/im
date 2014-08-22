package com.open.welinks.view;

import java.io.File;
import java.util.List;
import java.util.Map;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.lib.viewbody.ListBody;
import com.open.lib.viewbody.ListBody.MyListItemBody;
import com.open.welinks.R;
import com.open.welinks.controller.DownloadFile;
import com.open.welinks.controller.DownloadFileList;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.Shares.Share;
import com.open.welinks.model.Data.Shares.Share.Comment;
import com.open.welinks.model.Data.Shares.Share.ShareContent;
import com.open.welinks.model.Data.Shares.Share.ShareContent.ShareContentItem;
import com.open.welinks.model.Data.Shares.Share.ShareMessage;
import com.open.welinks.utils.DateUtil;
import com.open.welinks.utils.MCImageUtils;

public class ShareSubView {

	public Data data = Data.getInstance();

	public String tag = "ShareSubView";

	public DisplayMetrics displayMetrics;

	public RelativeLayout shareView;

	// share
	public RelativeLayout shareMessageView;
	public ListBody shareMessageListBody;

	public RelativeLayout shareTopMenuGroupNameParent;
	public TextView shareTopMenuGroupName;

	public PopupWindow groupPopWindow;
	public View groupDialogView;

	public MainView mainView;

	public ShareSubView(MainView mainView) {
		this.mainView = mainView;
	}

	public void initViews() {
		this.shareView = mainView.shareView;
		this.displayMetrics = mainView.displayMetrics;

		shareMessageView = (RelativeLayout) shareView.findViewById(R.id.groupShareMessageContent);
		shareMessageListBody = new ListBody();
		shareMessageListBody.initialize(displayMetrics, shareMessageView);

		groupDialogView = mainView.mInflater.inflate(R.layout.share_group_select_dialog, null, false);

		shareTopMenuGroupNameParent = (RelativeLayout) shareView.findViewById(R.id.shareTopMenuGroupNameParent);
		shareTopMenuGroupName = (TextView) shareView.findViewById(R.id.shareTopMenuGroupName);

	}

	public void showShareMessages() {
		Share share = data.shares.shareMap.get("1001");
		List<String> sharesOrder = share.sharesOrder;
		Map<String, ShareMessage> sharesMap = share.sharesMap;
		this.shareMessageListBody.listItemsSequence.clear();
		SharesMessageBody sharesMessageBody0 = null;
		sharesMessageBody0 = new SharesMessageBody(this.shareMessageListBody);
		sharesMessageBody0.initialize(-1);
		sharesMessageBody0.setContent(null);
		this.shareMessageListBody.listItemsSequence.add("message#" + "topBar");
		this.shareMessageListBody.listItemBodiesMap.put("message#" + "topBar", sharesMessageBody0);
		RelativeLayout.LayoutParams layoutParams0 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, (int) (50 * displayMetrics.density));
		sharesMessageBody0.y = 60 * displayMetrics.density * 0 + 2 * displayMetrics.density;
		sharesMessageBody0.cardView.setY(sharesMessageBody0.y);
		sharesMessageBody0.cardView.setX(0);
		this.shareMessageListBody.height = this.shareMessageListBody.height + 60 * displayMetrics.density;
		this.shareMessageListBody.containerView.addView(sharesMessageBody0.cardView, layoutParams0);
		for (int i = 0; i < 3; i++) {
			String key = sharesOrder.get(i);
			ShareMessage shareMessage = null;
			shareMessage = sharesMap.get(key);
			SharesMessageBody sharesMessageBody = null;
			sharesMessageBody = new SharesMessageBody(this.shareMessageListBody);
			sharesMessageBody.initialize(i);
			sharesMessageBody.setContent(shareMessage);

			this.shareMessageListBody.listItemsSequence.add("message#" + shareMessage.phone + "_" + shareMessage.time);
			this.shareMessageListBody.listItemBodiesMap.put("message#" + shareMessage.phone + "_" + shareMessage.time, sharesMessageBody);

			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, (int) (340 * displayMetrics.density));
			sharesMessageBody.y = 350 * displayMetrics.density * i + 2 * displayMetrics.density + 50 * displayMetrics.density;
			sharesMessageBody.cardView.setY(sharesMessageBody.y);
			sharesMessageBody.cardView.setX(0);
			this.shareMessageListBody.height = this.shareMessageListBody.height + 350 * displayMetrics.density;
			this.shareMessageListBody.containerView.addView(sharesMessageBody.cardView, layoutParams);
		}
	}

	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DisplayImageOptions options;
	public DownloadFileList downloadFileList = DownloadFileList.getInstance();
	public Gson gson = new Gson();

	public class SharesMessageBody extends MyListItemBody {

		SharesMessageBody(ListBody listBody) {
			listBody.super();
		}

		public View cardView = null;

		public ImageView headView;
		public TextView nickNameView;
		public TextView releaseTimeView;
		public TextView shareTextContentView;
		public ImageView shareImageContentView;
		public TextView sharePraiseNumberView;
		public ImageView sharePraiseIconView;
		public TextView shareCommentNumberView;
		public ImageView shareCommentIconView;

		public DownloadFile downloadFile = null;

		public int i;
		public RelativeLayout groupMembersListContentView;

		public View initialize(int i) {
			this.i = i;
			if (i == -1) {
				this.cardView = mainView.mInflater.inflate(R.layout.share_group_members_show, null);
				this.groupMembersListContentView = (RelativeLayout) this.cardView.findViewById(R.id.groupMembersListContent);
			} else {
				this.cardView = mainView.mInflater.inflate(R.layout.share_message_item, null);
				this.headView = (ImageView) this.cardView.findViewById(R.id.share_head);
				this.nickNameView = (TextView) this.cardView.findViewById(R.id.share_nickName);
				this.releaseTimeView = (TextView) this.cardView.findViewById(R.id.share_releaseTime);
				this.shareTextContentView = (TextView) this.cardView.findViewById(R.id.share_textContent);
				this.shareImageContentView = (ImageView) this.cardView.findViewById(R.id.share_imageContent);
				this.sharePraiseNumberView = (TextView) this.cardView.findViewById(R.id.share_praise);
				this.sharePraiseIconView = (ImageView) this.cardView.findViewById(R.id.share_praise_icon);
				this.shareCommentNumberView = (TextView) this.cardView.findViewById(R.id.share_comment);
				this.shareCommentIconView = (ImageView) this.cardView.findViewById(R.id.share_comment_icon);
			}
			super.initialize(cardView);
			return cardView;
		}

		public void setContent(ShareMessage shareMessage) {
			if (i == -1) {
				showGroupMembers(this.groupMembersListContentView);
			} else {
				Resources resources = mainView.thisActivity.getResources();
				Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.face_man);
				bitmap = MCImageUtils.getCircleBitmap(bitmap, true, 5, Color.WHITE);
				this.headView.setImageBitmap(bitmap);
				this.nickNameView.setText(data.relationship.friendsMap.get(shareMessage.phone).nickName);
				this.releaseTimeView.setText(DateUtil.formatHourMinute(shareMessage.time));
				ShareContent shareContent = gson.fromJson("{shareContentItems:" + shareMessage.content + "}", ShareContent.class);
				String textContent = "";
				String imageContent = "";
				List<ShareContentItem> shareContentItems = shareContent.shareContentItems;
				for (int i = 0; i < shareContentItems.size(); i++) {
					ShareContentItem shareContentItem = shareContentItems.get(i);
					if (shareContentItem.type.equals("image")) {
						imageContent = shareContentItem.detail;
						if (!"".equals(textContent))
							break;
					} else if (shareContentItem.type.equals("text")) {
						textContent = shareContentItem.detail;
						if (!"".equals(imageContent))
							break;
					}
				}

				this.shareTextContentView.setText(textContent);
				File sdFile = Environment.getExternalStorageDirectory();
				File file = new File(sdFile, "wxgs/" + imageContent);
				int showImageWidth = displayMetrics.widthPixels - (int) (22 * displayMetrics.density + 0.5f);
				int showImageHeight = (int) (displayMetrics.density * 200 + 0.5f);
				RelativeLayout.LayoutParams shareImageParams = new RelativeLayout.LayoutParams(showImageWidth, showImageHeight);
				// int margin = (int) ((int) displayMetrics.density * 1 + 0.5f);
				shareImageContentView.setLayoutParams(shareImageParams);
				final String url = "http://images3.we-links.com/images/" + imageContent + "@" + showImageWidth / 2 + "w_" + showImageHeight / 2 + "h_1c_1e_100q";
				final String path = file.getAbsolutePath();
				if (file.exists()) {
					imageLoader.displayImage("file://" + path, shareImageContentView, options, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
						}

						@Override
						public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
							downloadFile = new DownloadFile(url, path);
							downloadFile.view = shareImageContentView;
							downloadFileList.addDownloadFile(downloadFile);
						}

						@Override
						public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

						}
					});
				} else {
					downloadFile = new DownloadFile(url, path);
					downloadFile.view = shareImageContentView;
					downloadFileList.addDownloadFile(downloadFile);
				}

				this.sharePraiseNumberView.setText(shareMessage.praiseusers.size() + "");
				this.shareCommentNumberView.setText(shareMessage.comments.size() + "");
				String userPhone = data.userInformation.currentUser.phone;
				if (shareMessage.praiseusers.contains(userPhone)) {
					this.sharePraiseIconView.setImageResource(R.drawable.praised_icon);
				} else {
					this.sharePraiseIconView.setImageResource(R.drawable.praise_icon);
				}
				List<Comment> comments = shareMessage.comments;
				this.shareCommentIconView.setImageResource(R.drawable.comment_icon);
				for (int i = 0; i < comments.size(); i++) {
					Comment comment = comments.get(i);
					if (comment.phone.equals(userPhone)) {
						this.shareCommentIconView.setImageResource(R.drawable.commented_icon);
						break;
					}
				}
			}
		}
	}

	public RelativeLayout groupsDialogContent;

	@SuppressWarnings("deprecation")
	public void showGroupsDialog() {
		groupPopWindow = new PopupWindow(groupDialogView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		groupPopWindow.setBackgroundDrawable(new BitmapDrawable());
		RelativeLayout mainContentView = (RelativeLayout) groupDialogView.findViewById(R.id.mainContent);

		groupsDialogContent = (RelativeLayout) groupDialogView.findViewById(R.id.groupsContent);
		setGroupsDialogContent();
		RelativeLayout.LayoutParams mainContentParams = (RelativeLayout.LayoutParams) mainContentView.getLayoutParams();
		mainContentParams.height = (int) (displayMetrics.heightPixels * 0.7578125f);
		mainContentParams.leftMargin = (int) (20 / displayMetrics.density + 0.5f);
		mainContentParams.rightMargin = (int) (20 / displayMetrics.density + 0.5f);
		mainContentView.setLayoutParams(mainContentParams);
		groupPopWindow.showAtLocation(mainView.main_container, Gravity.CENTER, 0, 0);
	}

	public void dismissGroupDialog() {
		groupPopWindow.dismiss();
	}

	public void setGroupsDialogContent() {
		List<String> groups = data.relationship.groups;
		Map<String, Group> groupsMap = data.relationship.groupsMap;
		groupsDialogContent.removeAllViews();
		for (int i = 0; i < groups.size(); i++) {
			GroupDialogItem groupDialogItem = new GroupDialogItem();
			View view = groupDialogItem.initialize();
			groupDialogItem.setContent(groupsMap.get(groups.get(i)));
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (60 * displayMetrics.density));
			layoutParams.topMargin = (int) (60 * displayMetrics.density * i);
			groupsDialogContent.addView(view, layoutParams);
		}
	}

	public class GroupDialogItem {

		public View cardView;

		public ImageView groupIconView;
		public TextView groupNameView;

		public View initialize() {
			this.cardView = mainView.mInflater.inflate(R.layout.share_group_select_dialog_item, null);
			this.groupIconView = (ImageView) this.cardView.findViewById(R.id.groupIcon);
			this.groupNameView = (TextView) this.cardView.findViewById(R.id.groupName);
			return cardView;
		}

		public void setContent(Group group) {
			Resources resources = mainView.thisActivity.getResources();
			Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.face_man);
			bitmap = MCImageUtils.getCircleBitmap(bitmap, true, 5, Color.WHITE);
			this.groupIconView.setImageBitmap(bitmap);
			this.groupNameView.setText(group.name);
		}
	}

	public void showGroupMembers(RelativeLayout groupMembersListContentView) {
		groupMembersListContentView.removeAllViews();
		List<String> groupMembers = data.relationship.groupsMap.get("1001").members;
		// Map<String, Friend> friendsMap = data.relationship.friendsMap;
		Resources resources = mainView.thisActivity.getResources();
		Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.face_man);
		bitmap = MCImageUtils.getCircleBitmap(bitmap, true, 5, Color.WHITE);
		for (int i = 0; i < groupMembers.size(); i++) {
			// String key = groupMembers.get(i);
			// Friend friend = friendsMap.get(key);
			ImageView imageView = new ImageView(mainView.thisActivity);
			// imageView.setBackgroundColor(Color.BLACK);
			imageView.setImageBitmap(bitmap);
			int height = (int) (50 * displayMetrics.density);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(height, height);
			layoutParams.leftMargin = (int) (50 * displayMetrics.density * i);
			groupMembersListContentView.addView(imageView, layoutParams);
		}
	}
}
