package com.lejoying.wxgs.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.app.MainApplication;

public class SDCardImagesSelected extends Activity {

	MainApplication app = MainApplication.getMainApplication();
	ListView mImagesContent;
	LayoutInflater inflater;
	MyAdapter myAdapter;

	List<HashMap<String, String>> mImages;

	List<String> directorys = new ArrayList<String>();
	Map<String, List<String>> directoryToImages = new HashMap<String, List<String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		inflater = this.getLayoutInflater();
		setContentView(R.layout.activity_sdcardimageselected);
		mImagesContent = (ListView) findViewById(R.id.gv_imagesContent);
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
			return mImages.size() / 3 == 0 ? mImages.size()
					: mImages.size() + 1;
		}

		@Override
		public Object getItem(int arg0) {
			return mImages.get(arg0 / 3 == 0 ? arg0 / 3 : arg0 + 1);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			MessageHolder messageHolder;
			if (view == null) {
				messageHolder = new MessageHolder();
				view = inflater.inflate(
						R.layout.activity_sdcardimageselected_item, null);
				messageHolder.iv1 = (ImageView) view
						.findViewById(R.id.iv_imageContent1);
				messageHolder.iv2 = (ImageView) view
						.findViewById(R.id.iv_imageContent2);
				messageHolder.iv3 = (ImageView) view
						.findViewById(R.id.iv_imageContent3);
				view.setTag(messageHolder);

			} else {
				messageHolder = (MessageHolder) view.getTag();
			}
			final String path = mImages.get(position * 3).get("data");
			// Uri uri = Uri.parse(path);
			// mImageView.setImageURI(uri);
			final MessageHolder messageHolder0 = messageHolder;
			new Thread(new Runnable() {

				@Override
				public void run() {
					Bitmap bitmap0 = BitmapFactory.decodeFile(path);
					final Bitmap bitmap = ThumbnailUtils.extractThumbnail(
							bitmap0, 100, 100,
							ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
					app.UIHandler.post(new Runnable() {

						@Override
						public void run() {
							messageHolder0.iv1.setImageBitmap(bitmap);
						}
					});
				}
			}).start();
			if (position * 3 + 1 < mImages.size()) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						String path1 = mImages.get(position * 3 + 1)
								.get("data");
						Bitmap bitmap01 = BitmapFactory.decodeFile(path1);
						final Bitmap bitmap1 = ThumbnailUtils.extractThumbnail(
								bitmap01, 100, 100,
								ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
						app.UIHandler.post(new Runnable() {

							@Override
							public void run() {
								messageHolder0.iv2.setImageBitmap(bitmap1);
							}
						});
					}
				}).start();
			}
			if (position * 3 + 2 < mImages.size()) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						String path1 = mImages.get(position * 3 + 2)
								.get("data");
						Bitmap bitmap01 = BitmapFactory.decodeFile(path1);
						final Bitmap bitmap1 = ThumbnailUtils.extractThumbnail(
								bitmap01, 100, 100,
								ThumbnailUtils.OPTIONS_RECYCLE_INPUT);// OPTIONS_RECYCLE_INPUT
						app.UIHandler.post(new Runnable() {

							@Override
							public void run() {
								messageHolder0.iv3.setImageBitmap(bitmap1);
							}
						});
					}
				}).start();
			}

			return view;
		}
	}

	class MessageHolder {
		ImageView iv1;
		ImageView iv2;
		ImageView iv3;
	}

	// public List<HashMap<String, HashMap<String, String>>> getSDFileImages() {
	// List<HashMap<String, HashMap<String, String>>> images = null;
	// File sdFile = Environment.getExternalStorageDirectory();
	// if (sdFile != null) {
	// images = new ArrayList<HashMap<String, HashMap<String, String>>>();
	// recursionImages(sdFile, images);
	// }
	// return images;
	// }
	//
	// public void recursionImages(File parentFile,
	// List<HashMap<String, HashMap<String, String>>> parentImages) {
	// File[] files = parentFile.listFiles();
	// for (int i = 0; i < files.length; i++) {
	// File file = files[i];
	// if (file.isDirectory()) {
	// recursionImages(file, parentImages);
	// } else if (file.isFile()) {
	// String fileName = file.getName();
	// String lastName = fileName
	// .substring(fileName.lastIndexOf(".") + 1);
	// if ("jpg".equals(lastName) || "jpeg".equals(lastName)
	// || "gif".equals(lastName) || "png".equals(lastName)) {
	//
	// }
	// }
	// }
	// }

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
				// 获得图片的信息
				imageMap.put("imageName", cursor.getString(cursor
						.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
				imageMap.put(
						"imageInfo",
						""
								+ cursor.getLong(cursor
										.getColumnIndex(MediaStore.Images.Media.SIZE) / 1024)
								+ "kb");
				// 获得图片所在的路径(可以使用路径构建URI)
				String path = cursor.getString(cursor
						.getColumnIndex(MediaStore.Images.Media.DATA));
				imageMap.put("data", path);
				imageList.add(imageMap);
				String directory = path.substring(0, path.lastIndexOf("/"));
				if (directoryToImages.get(directory) == null) {
					List<String> imagesList = new ArrayList<String>();
					imagesList.add(path);
					directoryToImages.put(directory, imagesList);
					directorys.add(directory);
				} else {
					directoryToImages.get(directory).add(path);
				}
				// System.out.println(path.substring(path.lastIndexOf("/") +
				// 1));
				// for (int i = 0; i < cursor.getColumnCount(); i++) {
				// System.out.println("=======" + cursor.getColumnName(i)
				// + "=======" + cursor.getString(i));
				// }
				// break;
			}
			System.out.println(directorys.size() + "--------------"
					+ directoryToImages.keySet().size());
			// 关闭cursor
			cursor.close();
		}
		return imageList;
	}
}
