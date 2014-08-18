package com.open.welinks.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.open.welinks.Debug1Activity;
import com.open.welinks.ImageGridActivity;
import com.open.welinks.view.ImagesDirectoryView;
import com.open.welinks.view.ImagesDirectoryView.MyGridViewAdapter;

public class ImagesDirectoryController {

	public String tag = "ImagesDirectoryController";

	public final static int SCAN_OK = 1;

	public Context context;
	public Activity thisActivity;
	public ImagesDirectoryController thisController;
	public ImagesDirectoryView thisView;

	public ArrayList<String> imageDirectorys = new ArrayList<String>();
	public HashMap<String, ArrayList<String>> mGruopMap = new HashMap<String, ArrayList<String>>();
	public HashMap<String, HashMap<String, String>> imagesDescription = new HashMap<String, HashMap<String, String>>();

	public static ArrayList<String> selectedImage = new ArrayList<String>();

	public OnClickListener onClickListener;
	OnItemClickListener onItemClickListener;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == SCAN_OK) {
				MyGridViewAdapter myGridViewAdapter = thisView.new MyGridViewAdapter();
				thisView.mGridView.setAdapter(myGridViewAdapter);
			}
		}
	};

	public ImagesDirectoryController(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;
	}

	public void initializeListeners() {
		onClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(thisView.mCancle)) {
					thisActivity.finish();
				}
			}
		};

		onItemClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(thisActivity,
						ImageGridActivity.class);
				intent.putStringArrayListExtra("images",
						mGruopMap.get(imageDirectorys.get(position)));
				thisActivity.startActivityForResult(intent, 100);
			}
		};
	}

	public void bindEvent() {
		thisView.mGridView.setOnItemClickListener(onItemClickListener);
		thisView.mCancle.setOnClickListener(onClickListener);
	}

	public void setDate() {
		getImages();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
			if (selectedImage.size() > 0) {
				Intent intent = new Intent(thisActivity, Debug1Activity.class);
				ArrayList<HashMap<String, String>> images = new ArrayList<HashMap<String, String>>();
				for (int i = 0; i < selectedImage.size(); i++) {
					images.add(imagesDescription.get(selectedImage.get(i)));
				}
				selectedImage.clear();
				intent.putExtra("images", images);
				thisActivity.startActivity(intent);
			}
			System.out.println("OKOKOKO");
		} else {
			System.out.println("error---fail");
		}
	}

	public class ImageBean {
		public String parentName;
		public String path;

		public String contentType;
		public long size;

		public ImageBean() {
		}

		public ImageBean(String parentName, String path, String contentType,
				long size) {
			super();
			this.parentName = parentName;
			this.path = path;
			this.contentType = contentType;
			this.size = size;
		}
	}

	private void getImages() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = thisActivity
						.getContentResolver();
				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" },
						MediaStore.Images.Media.DATE_MODIFIED);
				while (mCursor.moveToNext()) {
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					long size = mCursor.getLong(mCursor
							.getColumnIndex(MediaStore.Images.Media.SIZE)) / 1024;
					String type = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
					String parentName = new File(path).getParentFile()
							.getName();
					if (!mGruopMap.containsKey(parentName)) {
						imageDirectorys.add(parentName);
						ArrayList<String> chileList = new ArrayList<String>();
						chileList.add(path);
						mGruopMap.put(parentName, chileList);
					} else {
						mGruopMap.get(parentName).add(path);
					}

					
					ImageBean imageBean = new ImageBean();
					
					HashMap<String, String> imageDescription = new HashMap<String, String>();
					imageDescription.put("size", size + "");
					imageDescription.put("path", path);
					imageDescription.put("parentName", parentName);
					imageDescription.put("Content-Type", type);
					imagesDescription.put(path, imageDescription);
				}
				mCursor.close();
				mHandler.sendEmptyMessage(SCAN_OK);
			}
		}).start();
	}
}
