package com.lejoying.wxgs.activity;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
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

	List<String> mImages;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		inflater = this.getLayoutInflater();
		setContentView(R.layout.activity_sdcardimageselected);
		mImagesContent = (ListView) findViewById(R.id.gv_imagesContent);
		mImages = SDCardImagesDirectoryActivity.directoryToImages
				.get(SDCardImagesDirectoryActivity.currentShowDirectory);
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
			final String path = mImages.get(position * 3);
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
					if (!bitmap0.isRecycled()) {
						bitmap0.recycle();
					}
					app.UIHandler.post(new Runnable() {

						@Override
						public void run() {
							messageHolder0.iv1.setImageBitmap(bitmap);
							if (!bitmap.isRecycled()) {
								// bitmap.recycle();
							}
						}
					});
				}
			}).start();
			if (position * 3 + 1 < mImages.size()) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						String path1 = mImages.get(position * 3 + 1);
						Bitmap bitmap01 = BitmapFactory.decodeFile(path1);
						final Bitmap bitmap1 = ThumbnailUtils.extractThumbnail(
								bitmap01, 100, 100,
								ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
						if (!bitmap01.isRecycled()) {
							bitmap01.recycle();
						}
						app.UIHandler.post(new Runnable() {

							@Override
							public void run() {
								messageHolder0.iv2.setImageBitmap(bitmap1);
								if (!bitmap1.isRecycled()) {
									// bitmap1.recycle();
								}
							}
						});
					}
				}).start();
			} else {
//				messageHolder0.iv2.setVisibility(View.GONE);
			}
			if (position * 3 + 2 < mImages.size()) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						String path1 = mImages.get(position * 3 + 2);
						Bitmap bitmap01 = BitmapFactory.decodeFile(path1);
						final Bitmap bitmap1 = ThumbnailUtils.extractThumbnail(
								bitmap01, 100, 100,
								ThumbnailUtils.OPTIONS_RECYCLE_INPUT);// OPTIONS_RECYCLE_INPUT
						if (!bitmap01.isRecycled()) {
							bitmap01.recycle();
						}
						app.UIHandler.post(new Runnable() {

							@Override
							public void run() {
								messageHolder0.iv3.setImageBitmap(bitmap1);
								if (!bitmap1.isRecycled()) {
									// bitmap1.recycle();
								}
							}
						});
					}
				}).start();
			} else {
//				messageHolder0.iv3.setVisibility(View.GONE);
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

}
