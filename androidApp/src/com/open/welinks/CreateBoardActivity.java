package com.open.welinks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.welinks.controller.UploadMultipart;
import com.open.welinks.controller.UploadMultipartList;
import com.open.welinks.customListener.OnUploadLoadingListener;
import com.open.welinks.customView.Alert;
import com.open.welinks.customView.Alert.AlertInputDialog;
import com.open.welinks.customView.Alert.AlertInputDialog.OnDialogClickListener;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Boards.Board;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.utils.MCImageUtils;
import com.open.welinks.utils.SHA1;
import com.open.welinks.utils.StreamParser;
import com.open.welinks.view.ViewManage;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class CreateBoardActivity extends Activity implements OnClickListener {
	private Data data = Data.getInstance();
	private FileHandlers fileHandlers = FileHandlers.getInstance();
	private ViewManage viewManage = ViewManage.getInstance();
	private UploadMultipartList uploadMultipartList = UploadMultipartList.getInstance();

	private Gson gson = new Gson();

	private OnUploadLoadingListener uploadLoadingListener;

	private View backView;
	private ImageView boardHead, boardCover;
	private EditText boardName, boardDescribe;
	private TextView okButton, backTitleView;
	private String imageHeadPath = "", imageCoverPath = "";

	public File tempFile;

	private int REQUESTCODE_ABLUM_HEAD = 0x1, REQUESTCODE_ABLUM_COVER = 0x2, REQUESTCODE_CAT_HEAD = 0x3, REQUESTCODE_CAT_COVER = 0x4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initViews();
		initListener();
	}

	private void initListener() {
		uploadLoadingListener = new OnUploadLoadingListener() {
		};
		bindEvent();
	}

	private void initViews() {
		setContentView(R.layout.activity_create_board);
		backView = this.findViewById(R.id.backView);
		boardHead = (ImageView) this.findViewById(R.id.boardHead);
		boardCover = (ImageView) this.findViewById(R.id.boardCover);
		boardName = (EditText) this.findViewById(R.id.boardName);
		boardDescribe = (EditText) this.findViewById(R.id.boardDescribe);
		okButton = (TextView) this.findViewById(R.id.okButton);
		backTitleView = (TextView) this.findViewById(R.id.backTitleView);

		backTitleView.setText("创建版块");
	}

	private void bindEvent() {
		boardHead.setOnClickListener(this);
		boardCover.setOnClickListener(this);
		okButton.setOnClickListener(this);
		backView.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		if (view.equals(boardHead)) {
			data.tempData.selectedImageList = null;
			Intent intent = new Intent(this, ImagesDirectoryActivity.class);
			intent.putExtra("max", 1);
			startActivityForResult(intent, REQUESTCODE_ABLUM_HEAD);
		} else if (view.equals(boardCover)) {
			data.tempData.selectedImageList = null;
			Intent intent = new Intent(this, ImagesDirectoryActivity.class);
			intent.putExtra("max", 1);
			startActivityForResult(intent, REQUESTCODE_ABLUM_COVER);
		} else if (view.equals(okButton)) {
			createBoard();
			if (viewManage.shareSectionView != null) {
				viewManage.shareSectionView.showGroupBoards();
			}
			setResult(Activity.RESULT_OK);
			finish();
		} else if (view.equals(backView)) {
			mFinish();
		}
	}

	private void createBoard() {
		Board board = data.boards.new Board();
		board.gid = data.localStatus.localData.currentSelectedGroup;
		board.updateTime = System.currentTimeMillis();
		board.sid = String.valueOf(board.updateTime);
		board.head = imageHeadPath;
		board.cover = imageCoverPath;
		board.name = boardName.getText().toString().trim();
		board.description = boardDescribe.getText().toString().trim();

		data.boards.boardsMap.put(board.sid, board);
		List<String> boards = data.relationship.groupsMap.get(board.gid).boards;
		if (boards == null)
			boards = new ArrayList<String>();
		boards.add(board.sid);

		String targetphones = "";
		Group group = data.relationship.groupsMap.get(board.gid);
		if (group != null)
			targetphones = gson.toJson(group.members);

		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		params.addBodyParameter("gid", board.gid);
		params.addBodyParameter("name", board.name);
		params.addBodyParameter("osid", board.sid);
		params.addBodyParameter("targetphones", targetphones);
		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
		httpUtils.send(HttpMethod.POST, API.SHARE_ADDBOARD, params, responseHandlers.share_addBoard);
	}

	private void mFinish() {
		if ("".equals(imageCoverPath) && "".equals(imageHeadPath) && "".equals(boardName.getText().toString().trim()) && "".equals(boardDescribe.getText().toString().trim())) {
			this.finish();
		} else {
			Alert.createDialog(this).setTitle("您还有信息尚未提交，是否退出？").setOnConfirmClickListener(new OnDialogClickListener() {
				@Override
				public void onClick(AlertInputDialog dialog) {
					finish();
				}
			}).show();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if ((requestCode == REQUESTCODE_ABLUM_HEAD || requestCode == REQUESTCODE_ABLUM_COVER) && resultCode == Activity.RESULT_OK && data.tempData.selectedImageList.size() > 0) {
			startPhotoZoom(this.data.tempData.selectedImageList.get(0), requestCode);
			data.tempData.selectedImageList = null;
		} else if (requestCode == REQUESTCODE_CAT_HEAD && resultCode == Activity.RESULT_OK) {
			Map<String, Object> map = MCImageUtils.processImagesInformation(tempFile.getAbsolutePath(), fileHandlers.sdcardHeadImageFolder);
			imageHeadPath = (String) map.get("fileName");
			fileHandlers.getHeadImage(imageHeadPath, boardHead, viewManage.options45);
			uploadFile(tempFile.getAbsolutePath(), imageHeadPath, (byte[]) map.get("bytes"), UploadMultipart.UPLOAD_TYPE_HEAD);
		} else if (requestCode == REQUESTCODE_CAT_COVER) {
			byte[] bytes = intent.getByteArrayExtra("bitmap");
			imageCoverPath = new SHA1().getDigestOfString(bytes) + ".osp";
			File file = new File(fileHandlers.sdcardBackImageFolder, imageCoverPath);
			try {
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				StreamParser.parseToFile(bytes, fileOutputStream);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			uploadFile(file.getAbsolutePath(), imageCoverPath, bytes, UploadMultipart.UPLOAD_TYPE_BACKGROUND);
			fileHandlers.getBackImage(imageCoverPath, boardCover, viewManage.options);
		}
	}

	public void startPhotoZoom(String path, int requestCode) {
		Intent intent = null;
		if (requestCode == REQUESTCODE_ABLUM_COVER) {
			intent = new Intent(this, CropActivity.class);
			intent.putExtra("path", path);
			startActivityForResult(intent, REQUESTCODE_CAT_COVER);
		} else if (requestCode == REQUESTCODE_ABLUM_HEAD) {
			tempFile = new File(fileHandlers.sdcardHeadImageFolder, "tempimage.png");
			int i = 1;
			while (tempFile.exists()) {
				tempFile = new File(fileHandlers.sdcardHeadImageFolder, "tempimage" + (i++) + ".png");
			}
			Uri uri = Uri.parse("file://" + path);
			intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(uri, "image/*");
			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", 300);// 100
			intent.putExtra("outputY", 300);// 100
			intent.putExtra("return-data", false);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
			startActivityForResult(intent, REQUESTCODE_CAT_HEAD);
		}
	}

	public void uploadFile(final String filePath, final String fileName, final byte[] bytes, int type) {
		UploadMultipart multipart = new UploadMultipart(filePath, fileName, bytes, type);
		uploadMultipartList.addMultipart(multipart);
		multipart.setUploadLoadingListener(uploadLoadingListener);
	}
}
