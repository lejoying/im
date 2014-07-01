package com.lejoying.wxgs.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileResult;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class ReleaseImageAndTextActivity extends Activity implements
		OnClickListener, OnTouchListener {
	MainApplication app = MainApplication.getMainApplication();
	LayoutInflater mInflater;
	GridView gridView;

	int height, width, dip;
	float density;
	int RESULT_TAKEPICTURE = 0x2;

	View sl_content, bottom_bar, rl_back, rl_send, rl_sync;
	EditText release_et;

	PopupWindow pop;
	View popView;
	LinearLayout ll_releaselocal, ll_releasecamera;
	File tempFile;

	GestureDetector backViewDetector;

	List<String> photoList;
	MyGridViewAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.release_imageandtext);
		mInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		dip = (int) (40 * density + 0.5f);
		height = dm.heightPixels;
		width = dm.widthPixels;
		initLayout();
		initData();
		initEvent();
	}

	@Override
	public void onBackPressed() {
		if (pop.isShowing()) {
			pop.dismiss();
		} else {
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		pop.dismiss();
		if (requestCode == MapStorageDirectoryActivity.RESULT_SELECTPIC
				&& resultCode == Activity.RESULT_OK) {
			for (int i = 0; i < MapStorageDirectoryActivity.selectedImages
					.size(); i++) {
				photoList
						.add(MapStorageDirectoryActivity.selectedImages.get(i));
				mAdapter.notifyDataSetChanged();
			}
		} else if (requestCode == RESULT_TAKEPICTURE
				&& resultCode == Activity.RESULT_OK) {
			photoList.add(tempFile.getAbsolutePath());
			mAdapter.notifyDataSetChanged();
		}
	}

	void initData() {
		photoList = new ArrayList<String>();
		mAdapter = new MyGridViewAdapter();
		gridView.setAdapter(mAdapter);
	}

	void initLayout() {
		rl_back = findViewById(R.id.rl_back);
		rl_send = findViewById(R.id.rl_send);
		rl_sync = findViewById(R.id.rl_sync);
		release_et = (EditText) findViewById(R.id.release_et);
		gridView = (GridView) findViewById(R.id.release_gv);
		sl_content = findViewById(R.id.sl_content);
		popView = mInflater.inflate(R.layout.f_release_sel, null);
		pop = new PopupWindow(popView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		ll_releaselocal = (LinearLayout) popView
				.findViewById(R.id.ll_releaselocal);
		ll_releasecamera = (LinearLayout) popView
				.findViewById(R.id.ll_releasecamera);
		ll_releaselocal.setOnClickListener(this);
		ll_releasecamera.setOnClickListener(this);
	}

	void initEvent() {
		rl_back.setOnTouchListener(this);
		rl_send.setOnClickListener(this);
		rl_sync.setOnClickListener(this);
		backViewDetector = new GestureDetector(
				ReleaseImageAndTextActivity.this,
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onDown(MotionEvent e) {
						return true;
					}

					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						finish();
						return true;
					}
				});
		popView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pop.dismiss();
			}
		});
	}

	void Send() {

	}

	void Sync() {

	}

	public class MyGridViewAdapter extends BaseAdapter {

		public MyGridViewAdapter() {
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return photoList.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			if (position < photoList.size()) {
				return photoList.get(position);
			} else {
				return null;
			}

		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView,
				final ViewGroup parent) {
			final GridViewHolder gridViewHolder;
			if (convertView == null) {
				gridViewHolder = new GridViewHolder();
				convertView = mInflater.inflate(R.layout.view_child, null);
				gridViewHolder.iv_child = (ImageView) convertView
						.findViewById(R.id.iv_child);
				LayoutParams childParams = gridViewHolder.iv_child
						.getLayoutParams();
				childParams.height = (int) (height * 0.165625f);
				childParams.width = (int) (width * 0.29444444f);
				gridViewHolder.iv_child.setLayoutParams(childParams);
				convertView.setTag(gridViewHolder);
			} else {
				gridViewHolder = (GridViewHolder) convertView.getTag();
			}
			if (position < photoList.size()) {
				app.fileHandler.getImage(photoList.get(position),
						new FileResult() {
							@Override
							public void onResult(String where, Bitmap bitmap) {
								gridViewHolder.iv_child.setImageBitmap(ThumbnailUtils
										.extractThumbnail(bitmap,
												(int) (width * 0.29444444f),
												(int) (height * 0.165625f)));
							}
						});
			} else {
				gridViewHolder.iv_child
						.setImageResource(R.drawable.release_imgandtext_add);
				gridViewHolder.iv_child
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								pop.showAtLocation((View) parent.getParent(),
										Gravity.CENTER, 0, 0);
							}
						});
			}
			return convertView;
		}

		class GridViewHolder {
			ImageView iv_child;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_send:
			Send();
			break;
		case R.id.rl_sync:
			Sync();
			break;
		case R.id.ll_releaselocal:
			Intent selectFromGallery = new Intent(this,
					MapStorageDirectoryActivity.class);
			startActivityForResult(selectFromGallery,
					MapStorageDirectoryActivity.RESULT_SELECTPIC);
			break;
		case R.id.ll_releasecamera:
			tempFile = new File(app.sdcardImageFolder, "tempimage.jpg");
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
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		switch (v.getId()) {
		case R.id.rl_back:
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				rl_back.setBackgroundColor(Color.argb(143, 0, 0, 0));
				break;
			case MotionEvent.ACTION_UP:
				rl_back.setBackgroundColor(Color.argb(0, 0, 0, 0));
				break;
			}
			break;
		}
		return backViewDetector.onTouchEvent(event);
	}

}
