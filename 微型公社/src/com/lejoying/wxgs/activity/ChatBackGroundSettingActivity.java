package com.lejoying.wxgs.activity;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileResult;

public class ChatBackGroundSettingActivity extends Activity implements
		OnClickListener {

	MainApplication app = MainApplication.getMainApplication();

	String[] backGrounds = new String[] { "bg1.png", "bg1.png", "bg1.png",
			"bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png",
			"bg1.png", };

	ImageView currentSelectedImageView;

	static int SHOW_BACKGROUNDSETTING = 0x01;
	static int SHOW_BACKGROUNDSELECTING = 0x02;
	int isShowStatus = SHOW_BACKGROUNDSETTING;

	int RESULT_SELECTPICTURE = 0x11;
	int RESULT_TAKEPICTURE = 0x12;
	int RESULT_CATPICTURE = 0x13;

	float height, width, dip;
	float density;

	LayoutInflater mInflater;

	LinearLayout backView;
	TextView backTextView;

	LinearLayout backgroundOption;
	RelativeLayout selectBackGround;
	RelativeLayout backGroundFromPhotos;
	RelativeLayout backGroundFromCamera;

	LinearLayout innerBackGround;
	GridView backGroundGridView;

	BackGroundAdapter backGroundAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_groupinfomation_setbackground);

		mInflater = this.getLayoutInflater();

		backView = (LinearLayout) findViewById(R.id.ll_setbackgroundbackview);
		backTextView = (TextView) findViewById(R.id.tv_backText);

		backgroundOption = (LinearLayout) findViewById(R.id.ll_backgroundOption);
		selectBackGround = (RelativeLayout) findViewById(R.id.rl_selectBackGround);
		backGroundFromPhotos = (RelativeLayout) findViewById(R.id.rl_backGroundFromPhotos);
		backGroundFromCamera = (RelativeLayout) findViewById(R.id.rl_backGroundFromCamera);

		innerBackGround = (LinearLayout) findViewById(R.id.ll_innerBackGround);
		backGroundGridView = (GridView) findViewById(R.id.gridView_background);

		backView.setOnClickListener(this);
		selectBackGround.setOnClickListener(this);
		backGroundFromPhotos.setOnClickListener(this);
		backGroundFromCamera.setOnClickListener(this);
		initData();
		backGroundAdapter = new BackGroundAdapter();
		backGroundGridView.setAdapter(backGroundAdapter);
	}

	private void initData() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		dip = (int) (40 * density + 0.5f);
		height = dm.heightPixels;
		width = dm.widthPixels;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_setbackgroundbackview:
			if (isShowStatus == SHOW_BACKGROUNDSETTING) {
				finish();
			} else {
				isShowStatus = SHOW_BACKGROUNDSETTING;
				backTextView.setText("设置聊天背景");
				backgroundOption.setVisibility(View.VISIBLE);
				innerBackGround.setVisibility(View.GONE);
			}
			break;
		case R.id.rl_selectBackGround:
			isShowStatus = SHOW_BACKGROUNDSELECTING;
			backTextView.setText("选择背景图");
			backgroundOption.setVisibility(View.GONE);
			innerBackGround.setVisibility(View.VISIBLE);
			break;
		case R.id.rl_backGroundFromPhotos:
			Intent intentPhotos = new Intent(
					ChatBackGroundSettingActivity.this,
					MapStorageDirectoryActivity.class);
			intentPhotos.putExtra("max", 1);
			startActivityForResult(intentPhotos, RESULT_SELECTPICTURE);
			break;
		case R.id.rl_backGroundFromCamera:
			takeCameraPicture();
			break;

		default:
			break;
		}
	}

	void takeCameraPicture() {
		File tempFile = new File(app.sdcardImageFolder, "tempimage.jpg");
		int i = 1;
		while (tempFile.exists()) {
			tempFile = new File(app.sdcardImageFolder, "tempimage" + (i++)
					+ ".jpg");
		}
		Uri uri = Uri.fromFile(tempFile);
		Intent tackPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		tackPicture.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
		tackPicture.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(tackPicture, RESULT_TAKEPICTURE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT_SELECTPICTURE
				&& resultCode == Activity.RESULT_OK) {
			Toast.makeText(ChatBackGroundSettingActivity.this,
					"RESULT_SELECTPICTURE", Toast.LENGTH_LONG).show();
		} else if (requestCode == RESULT_CATPICTURE
				&& resultCode == Activity.RESULT_OK) {
			// takeCameraPicture();
		} else if (requestCode == RESULT_TAKEPICTURE
				&& resultCode == Activity.RESULT_OK) {
			Toast.makeText(ChatBackGroundSettingActivity.this,
					"RESULT_TAKEPICTURE", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& isShowStatus == SHOW_BACKGROUNDSELECTING) {
			isShowStatus = SHOW_BACKGROUNDSETTING;
			backTextView.setText("设置聊天背景");
			backgroundOption.setVisibility(View.VISIBLE);
			innerBackGround.setVisibility(View.GONE);
		} else {
			finish();
		}
		return true;
	}

	class BackGroundAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return backGrounds.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ImageHolder imageHolder = null;
			if (convertView == null) {
				imageHolder = new ImageHolder();
				convertView = mInflater
						.inflate(
								R.layout.activity_groupinfomation_selectbackground_item,
								null);
				imageHolder.imageContent = (ImageView) convertView
						.findViewById(R.id.iv_image);
				imageHolder.imageContentStatus = (ImageView) convertView
						.findViewById(R.id.iv_imageContentStatus);
				convertView.setTag(imageHolder);
			} else {
				imageHolder = (ImageHolder) convertView.getTag();
			}
			final ImageHolder imageHolder0 = imageHolder;
			app.fileHandler.getThumbnail(backGrounds[position], "",
					(int) dp2px(90), (int) dp2px(90), new FileResult() {

						@Override
						public void onResult(String where, Bitmap bitmap) {
							imageHolder0.imageContent.setImageBitmap(bitmap);
						}
					});
			// app.fileHandler.getSquareDetailImage(backGrounds[position],
			// dp2px(50), new FileResult() {
			//
			// @Override
			// public void onResult(String where, Bitmap bitmap) {
			// imageContent.setImageBitmap(bitmap);
			// }
			// });
			imageHolder0.imageContent.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (currentSelectedImageView != null
							&& currentSelectedImageView != imageHolder0.imageContentStatus) {
						currentSelectedImageView.setVisibility(View.GONE);
					}
					if (imageHolder0.imageContentStatus.getVisibility() == View.GONE) {
						imageHolder0.imageContentStatus
								.setVisibility(View.VISIBLE);
						currentSelectedImageView = imageHolder0.imageContentStatus;
						currentSelectedImageView.setTag(position);
					} else {
						imageHolder0.imageContentStatus
								.setVisibility(View.GONE);
						currentSelectedImageView = null;
					}
				}
			});
			return convertView;
		}
	}

	class ImageHolder {
		ImageView imageContent;
		ImageView imageContentStatus;
	}

	public float dp2px(float px) {
		float dp = density * px + 0.5f;
		return dp;
	}
}
