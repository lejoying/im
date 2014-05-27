package com.lejoying.wxgs.activity;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.utils.MCImageUtils;
import com.lejoying.wxgs.activity.view.MyImageView;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.handler.AsyncHandler.Execution;

public class SDCardImagesSelectedActivity extends Activity {

	MainApplication app = MainApplication.getMainApplication();

	ListView mImagesContent;
	ImageView mBack;
	TextView directoryName;

	LayoutInflater inflater;
	MyAdapter myAdapter;

	List<String> mImages;

	Map<String, SoftReference<Bitmap>> bitmaps;

	int listStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		bitmaps = new HashMap<String, SoftReference<Bitmap>>();
		inflater = this.getLayoutInflater();
		setContentView(R.layout.activity_sdcardimageselected);
		mImagesContent = (ListView) findViewById(R.id.gv_imagesContent);
		mBack = (ImageView) findViewById(R.id.iv_back);
		directoryName = (TextView) findViewById(R.id.tv_directoryName);
		mImages = SDCardImagesDirectoryActivity.directoryToImages
				.get(SDCardImagesDirectoryActivity.currentShowDirectory);
		myAdapter = new MyAdapter();
		mImagesContent.setAdapter(myAdapter);
		mImagesContent.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				listStatus = arg1;
				if (arg1 == SCROLL_STATE_IDLE) {
					myAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
			}
		});
		defaultImage = BitmapFactory.decodeResource(getResources(),
				R.drawable.defaultimage);
		initData();
		initEvent();

	}

	private void initEvent() {
		mBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void initData() {
		String directory = SDCardImagesDirectoryActivity.currentShowDirectory;
		directory = directory.substring(directory.lastIndexOf("/") + 1);
		directoryName.setText(directory);
	}

	Bitmap defaultImage;

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mImages.size() % 3 == 0 ? mImages.size() / 3 : mImages
					.size() / 3 + 1;
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			MessageHolder messageHolder;
			// System.out.println(position + "======" + mImages.size());
			if (view == null) {
				messageHolder = new MessageHolder();
				view = inflater.inflate(
						R.layout.activity_sdcardimageselected_item, null);
				messageHolder.iv1 = (MyImageView) view
						.findViewById(R.id.iv_imageContent1);
				messageHolder.iv2 = (MyImageView) view
						.findViewById(R.id.iv_imageContent2);
				messageHolder.iv3 = (MyImageView) view
						.findViewById(R.id.iv_imageContent3);
				view.setTag(messageHolder);

			} else {
				messageHolder = (MessageHolder) view.getTag();
			}
			final MessageHolder messageHolder0 = messageHolder;
			if (position * 3 < mImages.size()) {
				messageHolder0.iv1.setVisibility(View.VISIBLE);
				final String path = mImages.get(position * 3);
				SoftReference<Bitmap> softBitmap = bitmaps.get(path);
				if (softBitmap == null || softBitmap.get() == null) {
					loadImage(path);
					softBitmap = new SoftReference<Bitmap>(defaultImage);
				}
				messageHolder0.iv1.setImageBitmap(softBitmap.get());
			}
			if (position * 3 + 1 < mImages.size()) {
				messageHolder0.iv2.setVisibility(View.VISIBLE);
				final String path = mImages.get(position * 3 + 1);
				SoftReference<Bitmap> softBitmap = bitmaps.get(path);
				if (softBitmap == null || softBitmap.get() == null) {
					loadImage(path);
					softBitmap = new SoftReference<Bitmap>(defaultImage);
				}
				messageHolder0.iv2.setImageBitmap(softBitmap.get());
			} else {
				messageHolder0.iv2.setVisibility(View.GONE);
			}
			if (position * 3 + 2 < mImages.size()) {
				messageHolder0.iv3.setVisibility(View.VISIBLE);
				messageHolder0.iv3.setImageDrawable(getResources().getDrawable(
						R.drawable.defaultimage));
				final String path = mImages.get(position * 3 + 2);
				SoftReference<Bitmap> softBitmap = bitmaps.get(path);
				if (softBitmap == null || softBitmap.get() == null) {
					loadImage(path);
					softBitmap = new SoftReference<Bitmap>(defaultImage);
				}
				messageHolder0.iv3.setImageBitmap(softBitmap.get());
			} else {
				messageHolder0.iv3.setVisibility(View.GONE);
			}

			return view;
		}
	}

	private void loadImage(final String path) {
		if (listStatus == OnScrollListener.SCROLL_STATE_FLING) {
			return;
		}
		bitmaps.put(path, new SoftReference<Bitmap>(defaultImage));
		app.asyncHandler.execute(new Execution<Bitmap>() {
			@Override
			protected void onResult(Bitmap t) {
				myAdapter.notifyDataSetChanged();
			}

			@Override
			protected Bitmap asyncExecute() {
				Bitmap bitmapZoom = MCImageUtils.getZoomBitmapFromFile(
						new File(path), 200, 200);
				// Bitmap bitmap0 = BitmapFactory.decodeFile(path);
				Bitmap bitmap = ThumbnailUtils.extractThumbnail(bitmapZoom,
						100, 100, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
				SoftReference<Bitmap> bitmap0 = new SoftReference<Bitmap>(
						bitmap);
				bitmaps.put(path, bitmap0);
				return bitmap;
			}

		});

	}

	class MessageHolder {
		MyImageView iv1;
		MyImageView iv2;
		MyImageView iv3;
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

}
