package com.open.welinks;

import java.io.File;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.welinks.controller.DownloadFile;
import com.open.welinks.controller.DownloadFileList;
import com.open.welinks.controller.OnDownloadListener;
import com.open.welinks.controller.OnUploadLoadingListener;
import com.open.welinks.controller.UploadMultipart;
import com.open.welinks.controller.UploadMultipartList;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.utils.MCImageUtils;

public class ModifyInformationActivity extends Activity implements OnClickListener {
	public Data data = Data.getInstance();
	public UploadMultipartList uploadMultipartList = UploadMultipartList.getInstance();

	public Gson gson = new Gson();

	public int REQUESTCODE_SELECT = 0x1, REQUESTCODE_TAKE = 0x2, REQUESTCODE_CAT = 0x3, TAG_EXIT = 0x99, TAG_EDIT = 0x98;

	public String key, type, headFileName;
	public boolean modified = false;

	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DownloadFileList downloadFileList = DownloadFileList.getInstance();

	public DownloadFile downloadFile;
	public DisplayImageOptions options;

	public onDataChanged mOnDataChanged;
	public OnUploadLoadingListener uploadLoadingListener;
	public OnDownloadListener downloadListener;

	public File tempFile, sdFile;

	public User user;
	public Group group;

	public View backView, head_layout, name_layout, sex_layout, location_layout, business_layout, lable_layout, pic_layout, inputDialogContent;
	public TextView modify_title, name_title, name, sex_title, sex, location_title, location, business_title, business, lable_title, lable, modify, camera, album, input_title;
	public ImageView head;
	public EditText input;
	public Button confirm, cancel;

	public InputMethodManager inputMethodManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_information);
		key = getIntent().getStringExtra("key");
		type = getIntent().getStringExtra("type");
		inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).displayer(new RoundedBitmapDisplayer(40)).build();
		sdFile = new File(Environment.getExternalStorageDirectory(), "welinks/heads/");
		initView();
		initializeListeners();
	}

	@Override
	public void onBackPressed() {
		if (pic_layout.getVisibility() == View.VISIBLE) {
			pic_layout.setVisibility(View.GONE);
		} else if (inputDialogContent.getVisibility() == View.VISIBLE) {
			inputDialogContent.setVisibility(View.GONE);
		} else {
			mFinish();
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
			Map<String, Object> map = MCImageUtils.processImagesInformation(this.data.tempData.selectedImageList.get(0), sdFile);
			headFileName = (String) map.get("fileName");
			initHeadImage(headFileName, head);
			uploadFile("file://" + this.data.tempData.selectedImageList.get(0), (String) map.get("fileName"), (byte[]) map.get("bytes"));
			pic_layout.setVisibility(View.GONE);
		}
	}

	private void initView() {
		backView = findViewById(R.id.backView);
		head_layout = findViewById(R.id.head_layout);
		name_layout = findViewById(R.id.name_layout);
		sex_layout = findViewById(R.id.sex_layout);
		location_layout = findViewById(R.id.location_layout);
		business_layout = findViewById(R.id.business_layout);
		lable_layout = findViewById(R.id.lable_layout);
		pic_layout = findViewById(R.id.pic_layout);
		modify_title = (TextView) findViewById(R.id.backTitleView);
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
		input = (EditText) findViewById(R.id.input);
		inputDialogContent = findViewById(R.id.inputDialogContent);
		input_title = (TextView) findViewById(R.id.title);
		confirm = (Button) findViewById(R.id.confirm);
		cancel = (Button) findViewById(R.id.cancel);

		backView.setOnClickListener(this);
		head_layout.setOnClickListener(this);
		name_layout.setOnClickListener(this);
		sex_layout.setOnClickListener(this);
		location_layout.setOnClickListener(this);
		business_layout.setOnClickListener(this);
		lable_layout.setOnClickListener(this);
		modify.setOnClickListener(this);
		camera.setOnClickListener(this);
		album.setOnClickListener(this);
		confirm.setOnClickListener(this);
		cancel.setOnClickListener(this);
		fillData();
	}

	public void initializeListeners() {
		uploadLoadingListener = new OnUploadLoadingListener() {

			@Override
			public void onSuccess(UploadMultipart instance, int time) {

			}

			@Override
			public void onLoading(UploadMultipart instance, int precent, long time, int status) {

			}
		};
		downloadListener = new OnDownloadListener() {

			@Override
			public void onSuccess(DownloadFile instance, int status) {

			}

			@Override
			public void loading(DownloadFile instance, int precent, int status) {

			}

			@Override
			public void onFailure(DownloadFile instance, int status) {

			}
		};
	}

	public void fillData() {
		if ("point".equals(type)) {
			user = data.userInformation.currentUser;
			headFileName = user.head;
			location_layout.setVisibility(View.GONE);
			modify_title.setText("编辑个人资料");
			name_title.setText("昵称");
			business_title.setText("个人宣言");
			lable_title.setText("爱好");
			sex_title.setText("性别");
			name.setText(user.nickName);
			if ("male".equals(user.sex)) {
				sex.setText("男");
			} else {
				sex.setText("女");
			}
			business.setText(user.mainBusiness);
			lable.setText("");
			if (user.head.equals("Head") || "".equals(user.head)) {
				Bitmap bitmap = MCImageUtils.getCircleBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.face_man), true, 5, Color.WHITE);
				head.setImageBitmap(bitmap);
			} else {
				initHeadImage(user.head, head);
			}
		} else if ("group".equals(type)) {
			group = data.relationship.groupsMap.get(key);
			headFileName = group.icon;
			sex_layout.setVisibility(View.GONE);
			location_layout.setVisibility(View.GONE);
			modify_title.setText("修改群名片");
			name_title.setText("群名称");
			business_title.setText("主要业务");
			lable_title.setText("标签");
			name.setText(group.name);
			business.setText(group.description);
			lable.setText("");
			if ("".equals(group.icon)) {
				Bitmap bitmap = MCImageUtils.getCircleBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.face_man), true, 5, Color.WHITE);
				head.setImageBitmap(bitmap);
			} else {
				initHeadImage(group.icon, head);
			}
		}
	}

	@Override
	public void onClick(View view) {
		if (view.equals(backView)) {
			mFinish();
		} else if (view.equals(head_layout)) {
			selectPicture(REQUESTCODE_SELECT);
			// pic_layout.setVisibility(View.VISIBLE);
		} else if (view.equals(sex_layout)) {
			if ("男".equals(sex.getText().toString())) {
				sex.setText("女");
				user.sex = "female";
			} else {
				sex.setText("男");
				user.sex = "male";
			}
			modified = true;
		} else if (view.equals(name_layout)) {
			modifyData(name);
		} else if (view.equals(business_layout)) {
			modifyData(business);
		} else if (view.equals(lable_layout)) {

		} else if (view.equals(location_layout)) {

		} else if (view.equals(modify)) {
			sendData();
		} else if (view.equals(camera)) {
			// takePicture(REQUESTCODE_TAKE);
		} else if (view.equals(album)) {
			selectPicture(REQUESTCODE_SELECT);
		} else if (view.equals(confirm)) {
			if (view.getTag().equals(TAG_EDIT)) {
				mOnDataChanged.onDataChangedListener();
				if (inputMethodManager.isActive()) {
					inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
				}
				setFocus(true);
			} else if (view.getTag().equals(TAG_EXIT)) {
				sendData();
			}
		} else if (view.equals(cancel)) {
			if (view.getTag().equals(TAG_EDIT)) {
				inputDialogContent.setVisibility(View.GONE);
				if (inputMethodManager.isActive()) {
					inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
				}
				setFocus(true);
			} else if (view.getTag().equals(TAG_EXIT)) {
				finish();
			}
		}

	}

	public void sendData() {
		if ("point".equals(type)) {
			if (!"".equals(headFileName)) {
				user.head = headFileName;
			}
			if ("男".equals(sex.getText().toString())) {
				user.sex = "male";
			} else {
				user.sex = "female";
			}
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
			httpUtils.send(HttpMethod.POST, API.GROUP_MODIFY, params, responseHandlers.group_modify);
		}
		setResult(Activity.RESULT_OK);
		finish();
	}

	public void modifyData(final TextView view) {
		inputDialogContent.setVisibility(View.VISIBLE);
		input.setVisibility(View.VISIBLE);
		confirm.setText("确定");
		cancel.setText("取消");
		confirm.setTag(TAG_EDIT);
		cancel.setTag(TAG_EDIT);
		input_title.setText("请输入内容");
		setFocus(false);
		String content = view.getText().toString();
		input.setText(content);
		input.setSelection(content.length());
		input.requestFocus();
		inputMethodManager.showSoftInput(input, InputMethodManager.SHOW_FORCED);

		mOnDataChanged = new onDataChanged() {
			@Override
			public void onDataChangedListener() {
				view.setText(input.getText().toString());
				inputDialogContent.setVisibility(View.GONE);
				setFocus(true);
				modified = true;
			}
		};

	}

	void selectPicture(int requestCode) {
		data.tempData.selectedImageList = null;
		startActivityForResult(new Intent(this, ImagesDirectoryActivity.class), requestCode);
	}

	void takePicture(int requestCode) {
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
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(intent, REQUESTCODE_CAT);
	}

	public void uploadFile(final String filePath, final String fileName, final byte[] bytes) {
		UploadMultipart multipart = new UploadMultipart(filePath, fileName, bytes, UploadMultipart.UPLOAD_TYPE_HEAD);
		uploadMultipartList.addMultipart(multipart);
		multipart.setUploadLoadingListener(uploadLoadingListener);
	}

	public void mFinish() {
		if (modified) {
			inputDialogContent.setVisibility(View.VISIBLE);
			input.setVisibility(View.GONE);
			confirm.setText("保存");
			cancel.setText("取消");
			confirm.setTag(TAG_EXIT);
			cancel.setTag(TAG_EXIT);
			input_title.setText("您有修改尚未提交，是否保存？");
		} else {
			finish();
		}

	}

	public void initHeadImage(String fileName, ImageView view) {
		File sdFile = Environment.getExternalStorageDirectory();
		File file = new File(sdFile, "welinks/heads/" + fileName);
		final String url = API.DOMAIN_COMMONIMAGE + "heads/" + fileName;
		final String path = file.getAbsolutePath();
		imageLoader.displayImage("file://" + path, view, options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				downloadFile = new DownloadFile(url, path);
				downloadFile.view = view;
				downloadFile.setDownloadFileListener(downloadListener);
				downloadFileList.addDownloadFile(downloadFile);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

			}
		});
	}

	public void setFocus(boolean whether) {
		head_layout.setClickable(whether);
		name_layout.setClickable(whether);
		sex_layout.setClickable(whether);
		location_layout.setClickable(whether);
		business_layout.setClickable(whether);
		lable_layout.setClickable(whether);
		modify.setClickable(whether);
	}

	public interface onDataChanged {
		public void onDataChangedListener();
	}
}
