package com.lejoying.wxgs.activity;

import java.io.File;
import java.lang.ref.SoftReference;
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
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.utils.MCImageUtils;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.handler.AsyncHandler.Execution;

public class MapStorageDirectoryActivity extends Activity {

	MainApplication app = MainApplication.getMainApplication();
	LayoutInflater inflater;

	public static List<String> directorys = new ArrayList<String>();
	public static Map<String, List<String>> directoryToImages = new HashMap<String, List<String>>();

	public static String currentShowDirectory = "";
	MapStorageDirectoryAdapter mapStorageDirectoryAdapter;

	ListView imagesDirectory;
	TextView cancleSelect;
	Bitmap defaultImage;
	int listStatus;
	Map<String, SoftReference<Bitmap>> bitmaps;

	public static List<String> selectedImages = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_mapstoragedirectory);
		imagesDirectory = (ListView) findViewById(R.id.gv_imagesDirectory);
		cancleSelect = (TextView) findViewById(R.id.tv_cancle);
		inflater = this.getLayoutInflater();
		mapStorageDirectoryAdapter = new MapStorageDirectoryAdapter();
		imagesDirectory.setAdapter(mapStorageDirectoryAdapter);
		defaultImage = ThumbnailUtils.extractThumbnail(BitmapFactory
				.decodeResource(getResources(), R.drawable.defaultimage), 60,
				60, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		bitmaps = new HashMap<String, SoftReference<Bitmap>>();
		directorys = new ArrayList<String>();
		directoryToImages = new HashMap<String, List<String>>();
		getSDImages();
		initEvent();
		super.onCreate(savedInstanceState);
	}

	private void initEvent() {
		imagesDirectory.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				listStatus = arg1;
				if (arg1 == SCROLL_STATE_IDLE) {
					mapStorageDirectoryAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
			}
		});
		cancleSelect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MapStorageDirectoryActivity.this,
						MainActivity.class);
				startActivity(intent);

			}
		});
	}

	class MapStorageDirectoryAdapter extends BaseAdapter {

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
						R.layout.activity_mapstoragedirectory_item, null);
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
			SoftReference<Bitmap> softBitmap = bitmaps.get(path);
			if (softBitmap == null || softBitmap.get() == null) {
				loadImage(path);
				softBitmap = new SoftReference<Bitmap>(defaultImage);
			}
			imagesHolder.directoryImage.setImageBitmap(softBitmap.get());
			imagesHolder.directoryName.setText(directorys.get(position)
					.substring(directorys.get(position).lastIndexOf("/") + 1)
					+ "("
					+ directoryToImages.get(directorys.get(position)).size()
					+ ")");
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(
							MapStorageDirectoryActivity.this,
							MapStorageImagesActivity.class);
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

	private void loadImage(final String path) {
		if (listStatus == OnScrollListener.SCROLL_STATE_FLING) {
			return;
		}
		bitmaps.put(path, new SoftReference<Bitmap>(defaultImage));
		app.asyncHandler.execute(new Execution<Bitmap>() {
			@Override
			protected void onResult(Bitmap t) {
				mapStorageDirectoryAdapter.notifyDataSetChanged();
			}

			@Override
			protected Bitmap asyncExecute() {
				Bitmap bitmapZoom = MCImageUtils.getZoomBitmapFromFile(
						new File(path), 100, 100);
				Bitmap bitmap = ThumbnailUtils.extractThumbnail(bitmapZoom, 60,
						60, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
				SoftReference<Bitmap> bitmap0 = new SoftReference<Bitmap>(
						bitmap);
				bitmaps.put(path, bitmap0);
				return bitmap;
			}

		});

	}

	public void getSDImages() {
		sendBroadcast(new Intent(
				Intent.ACTION_MEDIA_MOUNTED,
				Uri.parse("file://" + Environment.getExternalStorageDirectory())));
		Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		ContentResolver contentResolver = MapStorageDirectoryActivity.this
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
			int index = 0;
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
				if (directorys.size() == 0) {
					List<String> imagesList = new ArrayList<String>();
					imagesList.add(path);
					directoryToImages.put("/最近照片", imagesList);
					directorys.add("/最近照片");
					index++;
				} else {
					if (index < 50) {
						directoryToImages.get("/最近照片").add(path);
						index++;
					}
				}
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
