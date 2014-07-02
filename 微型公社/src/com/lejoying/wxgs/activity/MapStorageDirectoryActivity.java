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
	public static HashMap<String, List<HashMap<String, Object>>> directoryToImages = new HashMap<String, List<HashMap<String, Object>>>();

	public static String currentShowDirectory = "";
	public static int max = 0;
	MapStorageDirectoryAdapter mapStorageDirectoryAdapter;

	ListView imagesDirectory;
	TextView cancleSelect;
	Bitmap defaultImage;
	int listStatus;
	public static int RESULT_SELECTPIC = 0x1;
	Map<String, SoftReference<Bitmap>> bitmaps;

	public static ArrayList<String> selectedImages = new ArrayList<String>();
	public static HashMap<String, HashMap<String, Object>> selectedImagesMap = new HashMap<String, HashMap<String, Object>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_mapstoragedirectory);
		boolean init = true;
		if (getIntent().getExtras() != null) {
			init = getIntent().getExtras().getBoolean("init");
		}
		imagesDirectory = (ListView) findViewById(R.id.gv_imagesDirectory);
		cancleSelect = (TextView) findViewById(R.id.tv_cancle);
		inflater = this.getLayoutInflater();
		bitmaps = new HashMap<String, SoftReference<Bitmap>>();
		directorys = new ArrayList<String>();
		directoryToImages = new HashMap<String, List<HashMap<String, Object>>>();
		if (init) {
			selectedImagesMap=new HashMap<String, HashMap<String,Object>>();
			selectedImages = new ArrayList<String>();
		}
		getSDImages();
		mapStorageDirectoryAdapter = new MapStorageDirectoryAdapter();
		imagesDirectory.setAdapter(mapStorageDirectoryAdapter);
		defaultImage = ThumbnailUtils.extractThumbnail(BitmapFactory
				.decodeResource(getResources(), R.drawable.defaultimage), 60,
				60, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		initEvent();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onBackPressed() {
		max = 0;
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			Intent intent = new Intent();
			intent.putStringArrayListExtra("photoList", selectedImages);
			intent.putExtra("photoListMap", selectedImagesMap);
			setResult(Activity.RESULT_OK, intent);
			max = 0;
			finish();
		}
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
//				Intent intent = new Intent(MapStorageDirectoryActivity.this,
//						MainActivity.class);
//				startActivityForResult(intent, RESULT_SELECTPIC);
				finish();

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

			final String path = (String) directoryToImages
					.get(directorys.get(position)).get(0).get("path");
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
					startActivityForResult(intent, RESULT_SELECTPIC);
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
				MediaStore.Images.Media.MIME_TYPE,
				MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE };
		String selection = MediaStore.Images.Media.MIME_TYPE + " = ?";
		// + MediaStore.Images.Media.MIME_TYPE + " = ? ";
		// + MediaStore.Images.Media.MIME_TYPE + " = ?";
		String[] selectionArgs = { "image/jpeg" };// , "image/gif",, "image/png"
		String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " desc";
		Cursor cursor = contentResolver.query(uri, projection, selection,
				selectionArgs, sortOrder);
		List<Map<String, Object>> imageList = new ArrayList<Map<String, Object>>();
		if (cursor != null) {
			HashMap<String, Object> imageMap = null;
			cursor.moveToFirst();
			int index = 0;
			while (cursor.moveToNext()) {
				imageMap = new HashMap<String, Object>();
				imageMap.put("ID", cursor.getString(cursor
						.getColumnIndex(MediaStore.Images.Media._ID)));
				imageMap.put("fileName", cursor.getString(cursor
						.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
				imageMap.put("contentType", cursor.getString(cursor
						.getColumnIndex(MediaStore.Images.Media.MIME_TYPE)));
				// System.out.println(imageMap.get("contentType"));
				imageMap.put(
						"fileSize",
						""
								+ cursor.getLong(cursor
										.getColumnIndex(MediaStore.Images.Media.SIZE) / 1024)
								+ "kb");
				String path = cursor.getString(cursor
						.getColumnIndex(MediaStore.Images.Media.DATA));
				imageMap.put("path", path);
				imageList.add(imageMap);
				String directory = path.substring(0, path.lastIndexOf("/"));
				if (directorys.size() == 0) {
					List<HashMap<String, Object>> imagesList = new ArrayList<HashMap<String, Object>>();
					imagesList.add(imageMap);
					directoryToImages.put("/最近照片", imagesList);
					directorys.add("/最近照片");
					index++;
				} else {
					if (index < 50) {
						directoryToImages.get("/最近照片").add(imageMap);
						index++;
					}
				}
				if (directoryToImages.get(directory) == null) {
					List<HashMap<String, Object>> imagesList = new ArrayList<HashMap<String, Object>>();
					imagesList.add(imageMap);
					directoryToImages.put(directory, imagesList);
					directorys.add(directory);
				} else {
					directoryToImages.get(directory).add(imageMap);
				}
			}
			cursor.close();
		}
	}
}
