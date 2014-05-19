package com.lejoying.wxgs.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.lejoying.wxgs.R;

public class SDCardImagesSelected extends Activity {

	GridView mImagesContent;
	LayoutInflater inflater;
	MyAdapter myAdapter;

	List<HashMap<String, String>> mImages;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		inflater = this.getLayoutInflater();
		setContentView(R.layout.activity_sdcardimageselected);
		mImagesContent = (GridView) findViewById(R.id.gv_imagesContent);
		this.mImages = this.getImages();
		// Toast.makeText(SDCardImagesSelected.this, mImages.size() +
		// "---------",
		// Toast.LENGTH_LONG).show();
		myAdapter = new MyAdapter();
		mImagesContent.setAdapter(myAdapter);
		mImagesContent.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
			}
		});

	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mImages.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mImages.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			ImageView mImageView = null;
			if (view == null) {
				view = inflater.inflate(
						R.layout.activity_sdcardimageselected_item, null);
				mImageView = (ImageView) view
						.findViewById(R.id.iv_imageContent);
				view.setTag(mImageView);

			} else {
				mImageView = (ImageView) view.getTag();
			}
			String path = mImages.get(position).get("data");
			// Uri uri = Uri.parse(path);
			// mImageView.setImageURI(uri);
			Bitmap bitmap0 = BitmapFactory.decodeFile(path);
			Bitmap bitmap = ThumbnailUtils.extractThumbnail(bitmap0, 100, 100,
					ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
			if (bitmap0.isRecycled()) {
				bitmap0.recycle();
				System.gc();
			}
			mImageView.setImageBitmap(bitmap);
			if (bitmap.isRecycled()) {
				bitmap.recycle();
				System.gc();
			}
			return view;
		}
	}

	public List<HashMap<String, HashMap<String, String>>> getSDFileImages() {
		List<HashMap<String, HashMap<String, String>>> images = null;
		File sdFile = Environment.getExternalStorageDirectory();
		if (sdFile != null) {
			images = new ArrayList<HashMap<String, HashMap<String, String>>>();
			recursionImages(sdFile, images);
		}
		return images;
	}

	public void recursionImages(File parentFile,
			List<HashMap<String, HashMap<String, String>>> parentImages) {
		File[] files = parentFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isDirectory()) {
				recursionImages(file, parentImages);
			} else if (file.isFile()) {
				String fileName = file.getName();
				String lastName = fileName
						.substring(fileName.lastIndexOf(".") + 1);
				if ("jpg".equals(lastName) || "jpeg".equals(lastName)
						|| "gif".equals(lastName) || "png".equals(lastName)) {

				}
			}
		}
	}

	public List<HashMap<String, String>> getImages() {
		sendBroadcast(new Intent(
				Intent.ACTION_MEDIA_MOUNTED,
				Uri.parse("file://" + Environment.getExternalStorageDirectory())));
		// 指定要查询的uri资源
		Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		// 获取ContentResolver
		ContentResolver contentResolver = SDCardImagesSelected.this
				.getContentResolver();
		// 查询的字段
		String[] projection = { MediaStore.Images.Media._ID,
				MediaStore.Images.Media.DISPLAY_NAME,
				MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE };
		// 条件
		String selection = MediaStore.Images.Media.MIME_TYPE + "=?";
		// 条件值(這裡的参数不是图片的格式，而是标准，所有不要改动)
		String[] selectionArgs = { "image/jpeg" };
		// 排序
		String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " desc";
		// 查询sd卡上的图片
		Cursor cursor = contentResolver.query(uri, projection, selection,
				selectionArgs, sortOrder);
		List<HashMap<String, String>> imageList = new ArrayList<HashMap<String, String>>();
		if (cursor != null) {
			HashMap<String, String> imageMap = null;
			cursor.moveToFirst();
			while (cursor.moveToNext()) {
				imageMap = new HashMap<String, String>();
				// 获得图片的id
				imageMap.put("imageID", cursor.getString(cursor
						.getColumnIndex(MediaStore.Images.Media._ID)));
				// 获得图片显示的名称
				imageMap.put("imageName", cursor.getString(cursor
						.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
				// 获得图片的信息
				imageMap.put(
						"imageInfo",
						""
								+ cursor.getLong(cursor
										.getColumnIndex(MediaStore.Images.Media.SIZE) / 1024)
								+ "kb");
				// 获得图片所在的路径(可以使用路径构建URI)
				imageMap.put("data", cursor.getString(cursor
						.getColumnIndex(MediaStore.Images.Media.DATA)));
				imageList.add(imageMap);
			}
			// 关闭cursor
			cursor.close();
		}
		return imageList;
	}
}
