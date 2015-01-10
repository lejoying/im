package com.open.welinks;

import java.io.File;
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
import com.open.welinks.view.ViewManage;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
	private ImageView boardHead;
	private EditText boardName, boardDescribe;
	private TextView okButton, backTitleView;
	private String imagePath = "";

	public File tempFile;

	private int REQUESTCODE_ABLUM = 0x1, REQUESTCODE_TAKE = 0x2, REQUESTCODE_CAT = 0x3;

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
		boardName = (EditText) this.findViewById(R.id.boardName);
		boardDescribe = (EditText) this.findViewById(R.id.boardDescribe);
		okButton = (TextView) this.findViewById(R.id.okButton);
		backTitleView = (TextView) this.findViewById(R.id.backTitleView);

		backTitleView.setText("创建板块");
	}

	private void bindEvent() {
		boardHead.setOnClickListener(this);
		okButton.setOnClickListener(this);
		backView.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		if (view.equals(boardHead)) {
			data.tempData.selectedImageList = null;
			startActivityForResult(new Intent(this, ImagesDirectoryActivity.class), REQUESTCODE_ABLUM);
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
		board.head = imagePath;
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
		if ("".equals(imagePath) && "".equals(boardName.getText().toString().trim()) && "".equals(boardDescribe.getText().toString().trim())) {
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
		if (requestCode == REQUESTCODE_ABLUM && resultCode == Activity.RESULT_OK && data.tempData.selectedImageList.size() > 0) {
			Uri selectedImage = Uri.parse("file://" + this.data.tempData.selectedImageList.get(0));
			startPhotoZoom(selectedImage);
			data.tempData.selectedImageList = null;
		} else if (requestCode == REQUESTCODE_CAT && resultCode == Activity.RESULT_OK) {
			Map<String, Object> map = MCImageUtils.processImagesInformation(tempFile.getAbsolutePath(), fileHandlers.sdcardHeadImageFolder);
			imagePath = (String) map.get("fileName");
			fileHandlers.getHeadImage(imagePath, boardHead, viewManage.options45);
			uploadFile(tempFile.getAbsolutePath(), (String) map.get("fileName"), (byte[]) map.get("bytes"));
		}
	}

	public void startPhotoZoom(Uri uri) {
		tempFile = new File(fileHandlers.sdcardHeadImageFolder, "tempimage.png");
		int i = 1;
		while (tempFile.exists()) {
			tempFile = new File(fileHandlers.sdcardHeadImageFolder, "tempimage" + (i++) + ".png");
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
		startActivityForResult(intent, REQUESTCODE_CAT);
	}

	public void uploadFile(final String filePath, final String fileName, final byte[] bytes) {
		UploadMultipart multipart = new UploadMultipart(filePath, fileName, bytes, UploadMultipart.UPLOAD_TYPE_HEAD);
		uploadMultipartList.addMultipart(multipart);
		multipart.setUploadLoadingListener(uploadLoadingListener);
	}
}
