package com.lejoying.wxgs.activity;

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.app.MainApplication;

public class SDCardImagesDirectoryActivity extends Activity {

	MainApplication app = MainApplication.getMainApplication();
	LayoutInflater inflater;

	public static List<String> directorys = new ArrayList<String>();
	public static Map<String, List<String>> directoryToImages = new HashMap<String, List<String>>();

	public static String currentShowDirectory = "";

	ListView imagesDirectory;
	TextView cancleSelect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_selectimagedirectory);
		imagesDirectory = (ListView) findViewById(R.id.gv_imagesDirectory);
		cancleSelect = (TextView) findViewById(R.id.tv_cancle);
		inflater = this.getLayoutInflater();
		getSDImages();// count 35
		MyImageDirectoryAdapter myImageDirectoryAdapter = new MyImageDirectoryAdapter();
		imagesDirectory.setAdapter(myImageDirectoryAdapter);
		initEvent();
		super.onCreate(savedInstanceState);
	}

	private void initEvent() {
		cancleSelect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(SDCardImagesDirectoryActivity.this,
						MainActivity.class);
				startActivity(intent);

			}
		});
	}

	class MyImageDirectoryAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return directorys.size();
		}

		@Override
		public Object getItem(int arg0) {
			return directorys.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ImagesHolder imagesHolder = null;
			if (convertView == null) {
				imagesHolder = new ImagesHolder();
				convertView = inflater.inflate(
						R.layout.activity_sdcardimagedirectory_item, null);
				imagesHolder.directoryImage = (ImageView) convertView
						.findViewById(R.id.iv_directoryImage);
				imagesHolder.directoryName = (TextView) convertView
						.findViewById(R.id.tv_directoryName);
				imagesHolder.directoryOff = (ImageView) convertView
						.findViewById(R.id.iv_directoryOff);
				convertView.setTag(imagesHolder);
			} else {
				imagesHolder = (ImagesHolder) convertView.getTag();
			}
			final String path = directoryToImages.get(directorys.get(position))
					.get(0);
			final ImageView image = imagesHolder.directoryImage;
			new Thread(new Runnable() {

				@Override
				public void run() {
					Bitmap bitmap0 = BitmapFactory.decodeFile(path);
					final Bitmap bitmap = ThumbnailUtils.extractThumbnail(
							bitmap0, 60, 60,
							ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
					if (!bitmap0.isRecycled()) {
						bitmap0.recycle();
					}
					app.UIHandler.post(new Runnable() {

						@Override
						public void run() {
							image.setImageBitmap(bitmap);
						}
					});
				}
			}).start();
			imagesHolder.directoryName.setText(directorys.get(position)
					.substring(0, 4));
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(
							SDCardImagesDirectoryActivity.this,
							SDCardImagesSelected.class);
					currentShowDirectory = directorys.get(position);
					startActivity(intent);
				}
			});
			return convertView;
		}
	}

	class ImagesHolder {
		ImageView directoryImage;
		TextView directoryName;
		ImageView directoryOff;
	}

	public void getSDImages() {
		sendBroadcast(new Intent(
				Intent.ACTION_MEDIA_MOUNTED,
				Uri.parse("file://" + Environment.getExternalStorageDirectory())));
		Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		ContentResolver contentResolver = SDCardImagesDirectoryActivity.this
				.getContentResolver();
		String[] projection = { MediaStore.Images.Media._ID,
				MediaStore.Images.Media.DISPLAY_NAME,
				MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE };
		String selection = MediaStore.Images.Media.MIME_TYPE + "=?";
		String[] selectionArgs = { "image/jpeg" };
		String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " desc";
		Cursor cursor = contentResolver.query(uri, projection, selection,
				selectionArgs, sortOrder);
		List<HashMap<String, String>> imageList = new ArrayList<HashMap<String, String>>();
		if (cursor != null) {
			HashMap<String, String> imageMap = null;
			cursor.moveToFirst();
			while (cursor.moveToNext()) {
				imageMap = new HashMap<String, String>();
				imageMap.put("imageID", cursor.getString(cursor
						.getColumnIndex(MediaStore.Images.Media._ID)));
				imageMap.put("imageName", cursor.getString(cursor
						.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
				imageMap.put(
						"imageInfo",
						""
								+ cursor.getLong(cursor
										.getColumnIndex(MediaStore.Images.Media.SIZE) / 1024)
								+ "kb");
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
			}
			cursor.close();
		}
	}
}
