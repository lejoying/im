package com.open.welinks.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.open.welinks.ImageGridActivity;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.TempData.ImageBean;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.view.ImagesDirectoryView;
import com.open.welinks.view.ImagesDirectoryView.MyGridViewAdapter;

public class ImagesDirectoryController {

	public Data data = Data.getInstance();
	public String tag = "ImagesDirectoryController";

	public final static int SCAN_OK = 1;

	public Context context;
	public Activity thisActivity;
	public ImagesDirectoryController thisController;
	public ImagesDirectoryView thisView;

	public ArrayList<String> imageDirectorys = new ArrayList<String>();
	public HashMap<String, ArrayList<String>> mImageDirectorysMap = new HashMap<String, ArrayList<String>>();
	public HashMap<String, ImageBean> mImagesDescription = new HashMap<String, ImageBean>();

	public ArrayList<String> selectedImage = new ArrayList<String>();

	public OnClickListener onClickListener;
	public OnItemClickListener onItemClickListener;

	public int RESULT_REQUESTCODE_SELECTIMAGE = 0x01;

	public static ImagesDirectoryController instance;

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
		instance = this;
	}

	public void initializeListeners() {
		onClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(thisView.backView)) {
					thisActivity.finish();
				}
			}
		};

		onItemClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(thisActivity, ImageGridActivity.class);
				intent.putExtra("parentName", imageDirectorys.get(position));
				intent.putStringArrayListExtra("images", mImageDirectorysMap.get(imageDirectorys.get(position)));
				thisActivity.startActivityForResult(intent, RESULT_REQUESTCODE_SELECTIMAGE);
			}
		};
	}

	public void bindEvent() {
		thisView.mGridView.setOnItemClickListener(onItemClickListener);
		thisView.backView.setOnClickListener(onClickListener);
	}

	public void setDate() {
		if (data.tempData.selectedImageList != null) {
			selectedImage = data.tempData.selectedImageList;
		} else {
			selectedImage = new ArrayList<String>();
		}
		getImages();
		getCloudFileAccess();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_REQUESTCODE_SELECTIMAGE && resultCode == Activity.RESULT_OK) {
			ArrayList<ImageBean> images = new ArrayList<ImageBean>();
			for (int i = 0; i < selectedImage.size(); i++) {
				images.add(mImagesDescription.get(selectedImage.get(i)));
			}
			this.data.tempData.selectedImageList = selectedImage;
			selectedImage = new ArrayList<String>();
			thisActivity.setResult(Activity.RESULT_OK);
			thisActivity.finish();
			System.out.println("confirm selected image");
		} else {
			System.out.println("cancle selected image");
		}
	}

	public FileHandlers fileHandlers = FileHandlers.getInstance();
	public List<String> cloudAccessFile = new ArrayList<String>();

	public void getCloudFileAccess() {
		File imageFile = fileHandlers.sdcardImageFolder;
		if (imageFile.exists()) {
			if (imageFile.isDirectory()) {
				File[] files = imageFile.listFiles();
				for (int i = 0; i < files.length; i++) {
					File file = files[i];
					if (file.isFile()) {
						String fileName = file.getName();
						if (fileName.indexOf(".osj") != -1 || fileName.indexOf(".osp") != -1) {
							cloudAccessFile.add(file.getAbsolutePath());
							// Log.e(tag, file.getAbsolutePath());
						}
					}
				}
			}
		}
	}

	private void getImages() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				File file = Environment.getExternalStorageDirectory();
				thisActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = thisActivity.getContentResolver();
				Cursor mCursor = mContentResolver.query(mImageUri, null, MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?", new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);
				while (mCursor.moveToNext()) {
					String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
					long size = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Images.Media.SIZE)) / 1024;
					String contentType = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
					String parentName = new File(path).getParentFile().getName();
					if (!mImageDirectorysMap.containsKey(parentName)) {
						imageDirectorys.add(parentName);
						ArrayList<String> chileList = new ArrayList<String>();
						chileList.add(path);
						mImageDirectorysMap.put(parentName, chileList);
					} else {
						mImageDirectorysMap.get(parentName).add(path);
					}

					ImageBean imageBean = data.tempData.new ImageBean();

					imageBean.parentName = parentName;
					imageBean.path = path;

					imageBean.contentType = contentType;
					imageBean.size = size;
					mImagesDescription.put(path, imageBean);
				}
				mCursor.close();
				mHandler.sendEmptyMessage(SCAN_OK);
			}
		}).start();
	}
}
