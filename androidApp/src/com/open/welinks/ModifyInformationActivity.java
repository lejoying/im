package com.open.welinks;

import java.io.File;
import java.util.Map;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.welinks.controller.UploadMultipart;
import com.open.welinks.controller.UploadMultipartList;
import com.open.welinks.controller.UploadMultipart.UploadLoadingListener;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.utils.MCImageUtils;
import com.open.welinks.view.Alert;
import com.open.welinks.view.Alert.AlertInputDialog;
import com.open.welinks.view.Alert.AlertInputDialog.OnDialogClickListener;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;

public class ModifyInformationActivity extends Activity implements OnClickListener {
	public Data data = Data.getInstance();
	public UploadMultipartList uploadMultipartList = UploadMultipartList.getInstance();

	public Gson gson = new Gson();

	public static final int REQUESTCODE_SELECT = 0x1, REQUESTCODE_TAKE = 0x2, REQUESTCODE_CAT = 0x3;

	public String key, type, headFileName;
	public boolean modified = false;

	public onDataChanged mOnDataChanged;
	public UploadLoadingListener uploadLoadingListener;

	public File tempFile, sdFile;

	public User user;
	public Group group;

	public View backview, complete, head_layout, name_layout, sex_layout, location_layout, business_layout, lable_layout, input_layout, pic_layout;
	public TextView modify_title, name_title, name, sex_title, sex, location_title, location, business_title, business, lable_title, lable, modify, camera, album;
	public ImageView head, del;
	public EditText input;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_information);
		key = getIntent().getStringExtra("key");
		type = getIntent().getStringExtra("type");

		initView();
		initializeListeners();
	}

	@Override
	public void onBackPressed() {
		if (pic_layout.getVisibility() == View.VISIBLE) {
			pic_layout.setVisibility(View.GONE);
		} else if (input_layout.getVisibility() == View.VISIBLE) {
			input_layout.setVisibility(View.GONE);
		} else {
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUESTCODE_SELECT && resultCode == Activity.RESULT_OK) {
			Uri selectedImage = Uri.parse("file://" + this.data.tempData.selectedImageList.get(0));
			startPhotoZoom(selectedImage);
		} else if (requestCode == REQUESTCODE_TAKE && resultCode == Activity.RESULT_OK) {
			Uri uri = Uri.fromFile(tempFile);
			startPhotoZoom(uri);
		} else if (requestCode == REQUESTCODE_CAT && resultCode == Activity.RESULT_OK) {
			Bitmap headBitmap = MCImageUtils.getCircleBitmap((Bitmap) data.getExtras().get("data"), true, 5, Color.WHITE);
			head.setImageBitmap(headBitmap);
			Map<String, Object> map = MCImageUtils.processImagesInformation(tempFile.getAbsolutePath(), sdFile);
			headFileName = (String) map.get("fileName");
			uploadFile(tempFile.getAbsolutePath(), (String) map.get("fileName"), (byte[]) map.get("bytes"));
		}
	}

	private void initView() {
		backview = findViewById(R.id.backview);
		complete = findViewById(R.id.complete);
		head_layout = findViewById(R.id.head_layout);
		name_layout = findViewById(R.id.name_layout);
		sex_layout = findViewById(R.id.sex_layout);
		location_layout = findViewById(R.id.location_layout);
		business_layout = findViewById(R.id.business_layout);
		lable_layout = findViewById(R.id.lable_layout);
		input_layout = findViewById(R.id.input_layout);
		pic_layout = findViewById(R.id.pic_layout);
		modify_title = (TextView) findViewById(R.id.modify_title);
		name_title = (TextView) findViewById(R.id.name_title);
		name = (TextView) findViewById(R.id.name);
		sex_title = (TextView) findViewById(R.id.sex_title);
		sex = (TextView) findViewById(R.id.sex);
		location_title = (TextView) findViewById(R.id.location_title);
		location = (TextView) findViewById(R.id.location);
		business_title = (TextView) findViewById(R.id.business_title);
		business = (TextView) findViewById(R.id.business);
		lable_title = (TextView) findViewById(R.id.lable_title);
		lable = (TextView) findViewById(R.id.lable);
		modify = (TextView) findViewById(R.id.modify);
		camera = (TextView) findViewById(R.id.camera);
		album = (TextView) findViewById(R.id.album);
		head = (ImageView) findViewById(R.id.head);
		del = (ImageView) findViewById(R.id.del);
		input = (EditText) findViewById(R.id.input);

		backview.setOnClickListener(this);
		complete.setOnClickListener(this);
		head_layout.setOnClickListener(this);
		name_layout.setOnClickListener(this);
		sex_layout.setOnClickListener(this);
		location_layout.setOnClickListener(this);
		business_layout.setOnClickListener(this);
		lable_layout.setOnClickListener(this);
		modify.setOnClickListener(this);
		camera.setOnClickListener(this);
		album.setOnClickListener(this);
		del.setOnClickListener(this);
		fillData();
	}

	public void initializeListeners() {
		uploadLoadingListener = new UploadLoadingListener() {

			@Override
			public void success(UploadMultipart instance, int time) {

			}

			@Override
			public void loading(UploadMultipart instance, int precent, long time, int status) {

			}
		};

	}

	public void fillData() {
		if ("point".equals(type)) {
			user = data.userInformation.currentUser;
			location_layout.setVisibility(View.GONE);
			modify_title.setText("编辑个人资料");
			name_title.setText("昵称");
			business_title.setText("个人宣言");
			lable_title.setText("爱好");
			sex_title.setText("性别");
			name.setText(user.nickName);
			sex.setText(user.sex);
			business.setText(user.mainBusiness);
			lable.setText("");
		} else if ("group".equals(type)) {
			group = data.relationship.groupsMap.get(key);
			sex_layout.setVisibility(View.GONE);
			location_layout.setVisibility(View.GONE);
			modify_title.setText("修改群名片");
			name_title.setText("群名称");
			business_title.setText("主要业务");
			lable_title.setText("标签");
			name.setText(group.name);
			business.setText(group.description);
			lable.setText("");
		}
	}

	@Override
	public void onClick(View view) {
		if (view.equals(backview)) {
			mFinish();
		} else if (view.equals(complete)) {
			sendData();
		} else if (view.equals(head_layout)) {
			pic_layout.setVisibility(View.VISIBLE);
		} else if (view.equals(sex_layout)) {
			if ("男".equals(sex.getText().toString())) {
				sex.setText("女");
			} else {
				sex.setText("男");
			}
		} else if (view.equals(name_layout)) {
			modifyData(name);
		} else if (view.equals(business_layout)) {
			modifyData(business);
		} else if (view.equals(lable_layout)) {

		} else if (view.equals(location_layout)) {

		} else if (view.equals(modify)) {
			mOnDataChanged.onDataChangedListener();
		} else if (view.equals(camera)) {
			takePicture(REQUESTCODE_TAKE);
		} else if (view.equals(album)) {
			selectPicture(REQUESTCODE_SELECT);
		} else if (view.equals(del)) {
			input.setText("");
		}

	}

	public void sendData() {
		if ("point".equals(type)) {
			if (!"".equals(headFileName)) {
				user.head = headFileName;
			}
			user.sex = sex.getText().toString();
			user.nickName = name.getText().toString();
			user.mainBusiness = business.getText().toString();
			HttpUtils httpUtils = new HttpUtils();
			RequestParams params = new RequestParams();
			params.addBodyParameter("phone", data.userInformation.currentUser.phone);
			params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
			params.addBodyParameter("account", gson.toJson(user));
			ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
			httpUtils.send(HttpMethod.POST, API.ACCOUNT_MODIFY, params, responseHandlers.account_modify);
		} else if ("group".equals(type)) {
			if (!"".equals(headFileName)) {
				group.icon = headFileName;
			}
			group.name = name.getText().toString();
			group.description = business.getText().toString();
			HttpUtils httpUtils = new HttpUtils();
			RequestParams params = new RequestParams();
			params.addBodyParameter("phone", data.userInformation.currentUser.phone);
			params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
			params.addBodyParameter("gid", key);
			params.addBodyParameter("icon", headFileName);
			params.addBodyParameter("description", group.description);
			params.addBodyParameter("name", group.name);
			ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
			httpUtils.send(HttpMethod.POST, API.ACCOUNT_MODIFY, params, responseHandlers.group_modify);
		}
		setResult(Activity.RESULT_OK);
		finish();
	}

	public void modifyData(final TextView view) {
		input_layout.setVisibility(View.VISIBLE);
		input.setText(view.getText().toString());
		mOnDataChanged = new onDataChanged() {
			@Override
			public void onDataChangedListener() {
				view.setText(input.getText().toString());
				input_layout.setVisibility(View.GONE);
				modified = true;
			}
		};

	}

	void selectPicture(int requestCode) {
		data.tempData.selectedImageList = null;
		startActivityForResult(new Intent(this, ImagesDirectoryActivity.class), requestCode);
	}

	void takePicture(int requestCode) {
		sdFile = new File(Environment.getExternalStorageDirectory(), "welinks/images/");
		tempFile = new File(sdFile, "tempimage");
		int i = 1;
		while (tempFile.exists()) {
			tempFile = new File(sdFile, "tempimage" + (i++));
		}
		Uri uri = Uri.fromFile(tempFile);
		Intent tackPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		tackPicture.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
		tackPicture.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(tackPicture, requestCode);
	}

	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 100);
		intent.putExtra("outputY", 100);
		intent.putExtra("return-data", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
		startActivityForResult(intent, REQUESTCODE_CAT);
	}

	public void uploadFile(final String filePath, final String fileName, final byte[] bytes) {
		new Thread() {
			@Override
			public void run() {
				UploadMultipart multipart = new UploadMultipart(filePath, fileName, bytes);
				uploadMultipartList.addMultipart(multipart);
				multipart.setUploadLoadingListener(uploadLoadingListener);
			}
		}.start();
	}

	public void mFinish() {
		if (modified) {
			Alert.createDialog(this).setTitle("您有修改尚未提交，是否退出？").setOnConfirmClickListener(new OnDialogClickListener() {

				@Override
				public void onClick(AlertInputDialog dialog) {
					finish();
				}
			}).show();
		} else {
			finish();
		}

	}

	public interface onDataChanged {
		public void onDataChangedListener();
	}
}
