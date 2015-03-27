package com.open.welinks.model;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.gson.Gson;
import com.lidroid.xutils.http.ResponseInfo;
import com.open.lib.MyLog;
import com.open.welinks.NearbyActivity;
import com.open.welinks.model.Data.Boards.Board;
import com.open.welinks.model.Data.Boards.ShareMessage;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.SubData.SendShareMessage;
import com.open.welinks.model.SubData.ShareContentItem;
import com.open.welinks.utils.SHA1;
import com.open.welinks.utils.StreamParser;
import com.open.welinks.view.ViewManage;

public class TaskContainer_Share {
	public String tag = "TaskContainer_Share";
	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();
	public Gson gson = new Gson();
	public MyLog log = new MyLog(tag, true);
	public SHA1 sha1 = new SHA1();

	public SubData subData = SubData.getInstance();

	public TaskManageHolder taskManageHolder = TaskManageHolder.getInstance();

	public class GetShares extends Task {

		public String gid;
		public String sid;
		public int nowpage = 0;
		public int pagesize = 10;

		@Override
		public void sendRequest() {
			parser.check();
			User currentUser = data.userInformation.currentUser;
			params.addBodyParameter("phone", currentUser.phone);
			params.addBodyParameter("accessKey", currentUser.accessKey);
			params.addBodyParameter("gid", gid);
			params.addBodyParameter("sid", sid);
			params.addBodyParameter("nowpage", nowpage + "");
			params.addBodyParameter("pagesize", pagesize + "");

			// httpUtils.send(HttpMethod.POST, API.SHARE_GETSHARES, params, responseHandlers.share_getSharesCallBack2);
		}

		class Response {
			public String 提示信息;
			public String 失败原因;
			public String gid;
			public String sid;
			public int nowpage;
			public SubBoard shares;
		}

		class SubBoard {
			public List<String> shareMessagesOrder;
			public Map<String, ShareMessage> shareMessagesMap;
		}

		Response response;

		@Override
		public boolean onResponseReceived(ResponseInfo<String> responseInfo) {
			response = gson.fromJson(responseInfo.result, Response.class);
			if (response == null) {
				log.e(ViewManage.getErrorLineNumber() + "gson Exception");
			} else if (response.提示信息.equals("获取群分享成功")) {
				log.e("获取群分享成功:::::::::::::::::");
			} else {
				log.e(ViewManage.getErrorLineNumber() + response.失败原因);
			}
			return true;
		}

		public void updateData() {// 主UI线程
			log.e("updateData:::::::::::::::::");
			SubBoard responsesShare = response.shares;
			parser.check();
			// String gid = response.gid;
			String sid = response.sid;
			Board board = data.boards.boardsMap.get(sid);
			if (board == null) {
				board = data.boards.new Board();
				data.boards.boardsMap.put(sid, board);
			}
			List<String> sharesOrder = responsesShare.shareMessagesOrder;
			if (response.nowpage == 0) {
				for (int i = sharesOrder.size() - 1; i >= 0; i--) {
					String key = sharesOrder.get(i);
					if (!board.shareMessagesOrder.contains(key)) {
						board.shareMessagesOrder.add(0, key);
					}
				}
			} else {
				for (int i = 0; i < sharesOrder.size(); i++) {
					String key = sharesOrder.get(i);
					if (!board.shareMessagesOrder.contains(key)) {
						board.shareMessagesOrder.add(key);
					}
				}
			}
			data.boards.shareMessagesMap.putAll(responsesShare.shareMessagesMap);
			board.updateTime = new Date().getTime();
			data.boards.isModified = true;
		}

		public void updateView() {// 主UI线程
			log.e("updateView:::::::::::::::::");
			if (data.relationship.groups.contains(gid)) {
				if (response.shares.shareMessagesOrder.size() == 0) {
					taskManageHolder.viewManage.shareSectionView.thisController.nowpage--;
				}
				taskManageHolder.viewManage.shareSectionView.thisController.reflashStatus.state = taskManageHolder.viewManage.shareSectionView.thisController.reflashStatus.Normal;
				taskManageHolder.viewManage.postNotifyView("ShareSectionNotifyShares");
			} else {
				if (response.shares.shareMessagesOrder.size() == 0) {
					taskManageHolder.viewManage.shareSectionView.thisController.nowpage--;
				}
				taskManageHolder.viewManage.shareSectionView.thisController.reflashStatus.state = taskManageHolder.viewManage.shareSectionView.thisController.reflashStatus.Normal;
				taskManageHolder.viewManage.postNotifyView("ShareSectionNotifyShares");
			}
		}
	}

	public class PostTask extends Task {

		public String mode;

		public String gid;
		public String gtype;
		public String sid;
		public String chatTextContent;
		public String address;
		public double longitude, latitude;

		public String imageListString = "";

		ShareMessage shareMessage;

		public List<ShareContentItem> contentItems;

		public int source;// 1 group 2 square

		@Override
		public void modifyData() {
			contentItems = new ArrayList<ShareContentItem>();
			parser.check();
			User currentUser = data.userInformation.currentUser;
			long time = new Date().getTime();
			parser.check();
			// ShareDraft shareDraft = data.localStatus.localData.new ShareDraft();
			// shareDraft.gid = gid;
			// shareDraft.sid = sid;
			// shareDraft.gsid = currentUser.phone + "_" + time;
			// shareDraft.gtype = gtype;
			// shareDraft.content = chatTextContent;
			// shareDraft.imagesContent = imageListString;
			// if (data.localStatus.localData.shareReleaseSequece == null) {
			// data.localStatus.localData.shareReleaseSequece = new ArrayList<String>();
			// }
			// if (data.localStatus.localData.shareReleaseSequeceMap == null) {
			// data.localStatus.localData.shareReleaseSequeceMap = new HashMap<String, ShareDraft>();
			// }
			// data.localStatus.localData.shareReleaseSequece.add(shareDraft.gsid);
			// data.localStatus.localData.shareReleaseSequeceMap.put(shareDraft.gsid, shareDraft);

			if (data.boards == null) {
				data.boards = data.new Boards();
			}
			if (data.boards.boardsMap.get(sid) == null) {
				Board board = data.boards.new Board();
				data.boards.boardsMap.put(sid, board);
			}
			Board board = data.boards.boardsMap.get(sid);
			shareMessage = data.boards.new ShareMessage();
			// shareMessage.mType = shareMessage.MESSAGE_TYPE_IMAGETEXT;
			shareMessage.gsid = currentUser.phone + "_" + time;
			shareMessage.type = "imagetext";
			shareMessage.sid = sid;
			shareMessage.gid = gid;
			shareMessage.phone = currentUser.phone;
			shareMessage.nickName = currentUser.nickName;
			shareMessage.time = time;
			shareMessage.status = "sending";

			ShareContentItem shareContentItem = subData.new ShareContentItem();
			shareContentItem.type = "text";
			shareContentItem.detail = chatTextContent;
			contentItems.add(shareContentItem);

			// remove
			// board.shareMessagesOrder.remove(shareMessage.gsid);
			// add
			board.shareMessagesOrder.add(0, shareMessage.gsid);
			data.boards.shareMessagesMap.put(shareMessage.gsid, shareMessage);
			data.boards.isModified = true;
			this.initProgress(taskManageHolder.viewManage.screenWidth / 10 * 1);
		}

		public void initProgress(int percent) {
			if (source == 1) {
			} else if (source == 2) {
				NearbyActivity.instance.thisView.initShareMessage(shareMessage, percent);
			}
		}

		public void updateProgress(int percent) {
			if (source == 1) {
			} else if (source == 2) {
				NearbyActivity.instance.thisView.updateProgress(shareMessage, percent);
			}
		}

		@Override
		public void modifyView() {
			if (this.myFileList != null && this.myFileList.size() > 0) {
				currentResolveFileCount = 0;
				resolveFileTotal = this.myFileList.size();
				MyFile myFile = this.myFileList.get(0);
				copyFileToSprecifiedDirecytory(myFile, true);
				ShareContentItem contentItem = subData.new ShareContentItem();
				contentItem.type = "image";
				contentItem.detail = myFile.fileName;
				contentItems.add(contentItem);
				shareMessage.content = gson.toJson(contentItems);
				// taskManageHolder.viewManage.postNotifyView("ShareSectionNotifyShares");
			} else {
				shareMessage.content = gson.toJson(contentItems);

				// taskManageHolder.viewManage.postNotifyView("ShareSectionNotifyShares");
			}
			taskManageHolder.viewManage.postNotifyView(this.mode);// ShareSectionNotifyShares
			this.updateProgress(taskManageHolder.viewManage.screenWidth / 10 * 3);
		}

		@Override
		public void onLocalFilesResolved() {
			for (int i = 1; i < this.myFileList.size(); i++) {
				MyFile myFile = this.myFileList.get(i);
				ShareContentItem contentItem = subData.new ShareContentItem();
				contentItem.type = "image";
				contentItem.detail = myFile.fileName;
				contentItems.add(contentItem);
			}
			shareMessage.content = gson.toJson(contentItems);
			this.updateProgress(taskManageHolder.viewManage.screenWidth / 10 * 5);
		}

		@Override
		public void uploadFiles() {

		}

		@Override
		public void sendRequest() {
			User currentUser = data.userInformation.currentUser;
			params.addBodyParameter("phone", currentUser.phone);
			params.addBodyParameter("nickName", currentUser.nickName);
			params.addBodyParameter("accessKey", currentUser.accessKey);
			params.addBodyParameter("head", currentUser.head);
			params.addBodyParameter("gid", gid);
			params.addBodyParameter("ogsid", shareMessage.gsid);
			params.addBodyParameter("sid", sid);
			if (longitude != 0 && latitude != 0) {
				params.addBodyParameter("location", "[" + longitude + "," + latitude + "]");
				params.addBodyParameter("address", address);
			}
			SendShareMessage sendShareMessage = subData.new SendShareMessage();
			sendShareMessage.type = "imagetext";
			sendShareMessage.content = shareMessage.content;
			params.addBodyParameter("message", gson.toJson(sendShareMessage));
			this.updateProgress(taskManageHolder.viewManage.screenWidth / 10 * 7);
		}

		class Response {
			public String 提示信息;
			public String 失败原因;
			public long time;
			public String gid;
			public String sid;
			public String gsid;
			public String ogsid;
		}

		public Response response;

		@Override
		public boolean onResponseReceived(ResponseInfo<String> responseInfo) {
			response = gson.fromJson(responseInfo.result, Response.class);
			return true;
		}

		@Override
		public void updateData() {
			this.updateProgress(taskManageHolder.viewManage.screenWidth / 10 * 8);
			// id time state
			if (response.提示信息.equals("发布群分享成功")) {
				parser.check();
				// String gid = response.gid;
				String sid = response.sid;
				String gsid = response.gsid;
				String ogsid = response.ogsid;
				Board board = data.boards.boardsMap.get(sid);
				ShareMessage shareMessage = data.boards.shareMessagesMap.get(ogsid);
				if (shareMessage != null) {
					shareMessage.gsid = response.gsid;
					shareMessage.time = response.time;
					shareMessage.status = "sent";
				}
				int index = board.shareMessagesOrder.indexOf(ogsid);
				if (index != -1 && shareMessage != null) {
					board.shareMessagesOrder.remove(index);
					board.shareMessagesOrder.add(index, gsid);
					data.boards.shareMessagesMap.remove(ogsid);
					data.boards.shareMessagesMap.put(shareMessage.gsid, shareMessage);
				}
				data.boards.isModified = true;

				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------发送成功");
			} else if (response.提示信息.equals("发布群分享失败")) {
				parser.check();
				// String gid = response.gid;
				String sid = response.sid;
				String ogsid = response.ogsid;
				Board board = data.boards.boardsMap.get(sid);
				ShareMessage shareMessage = null;
				if (board != null) {
					shareMessage = data.boards.shareMessagesMap.get(ogsid);
				}
				if (shareMessage != null) {
					shareMessage.status = "failed";
				}
				log.e(ViewManage.getErrorLineNumber() + response.失败原因);
			} else {
				log.e(ViewManage.getErrorLineNumber() + response.失败原因);
			}
			this.updateProgress(taskManageHolder.viewManage.screenWidth / 10 * 9);
		}

		@Override
		public void updateView() {
			taskManageHolder.viewManage.postNotifyView(this.mode);// "ShareSectionNotifyShares"
			this.updateProgress(taskManageHolder.viewManage.screenWidth);
		}

		public void copyFileToSprecifiedDirecytory(MyFile myFile, boolean isCompression) {
			String key = myFile.path;
			String suffixName = key.substring(key.lastIndexOf("."));
			suffixName = suffixName.toLowerCase(Locale.getDefault());
			if (suffixName.equals(".jpg") || suffixName.equals(".jpeg")) {
				suffixName = ".osj";
			} else if (suffixName.equals(".png")) {
				suffixName = ".osp";
			}
			try {
				String fileName = "";
				File fromFile = new File(key);
				byte[] bytes = null;
				bytes = taskManageHolder.fileHandler.getImageFileBytes(null, fromFile, taskManageHolder.viewManage.screenWidth, taskManageHolder.viewManage.screenHeight);
				int fileLength = bytes.length;
				myFile.length = fileLength;
				String sha1FileName = sha1.getDigestOfString(bytes);
				fileName = sha1FileName + suffixName;
				myFile.fileName = fileName;
				File toFile = new File(taskManageHolder.fileHandler.sdcardImageFolder, fileName);
				FileOutputStream fileOutputStream = new FileOutputStream(toFile);
				StreamParser.parseToFile(bytes, fileOutputStream);

				// if (isThumbnail) {
				// int showImageWidth = (int) (viewManage.screenWidth- 20 * viewManage.displayMetrics.density - 0.5f);
				// File toSnapFile = new File(fileHandlers.sdcardThumbnailFolder, fileName);
				// fileHandlers.makeImageThumbnail(fromFile, showImageWidth, viewManage.screenHeight, toSnapFile, fileName);
				// }
				bytes = null;
				Thread.sleep(100);
				System.gc();
				Thread.sleep(100);
				// UploadMultipart multipart = new UploadMultipart(key, fileName, bytes, UploadMultipart.UPLOAD_TYPE_IMAGE);

				// multipart.path = key;
				// uploadMultipartList.addMultipart(multipart);
				// multipart.setUploadLoadingListener(uploadLoadingListener);
			} catch (Exception e) {
				e.printStackTrace();
				log.e(e.toString());
				StackTraceElement ste = new Throwable().getStackTrace()[1];
				log.e("Exception@" + ste.getLineNumber());
			}
		}
	}
}
