package com.lejoying.wxgs.activity;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
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

public class MapStorageImagesActivity extends Activity {

	MainApplication app = MainApplication.getMainApplication();

	ListView mImagesContent;
	ImageView mBack;
	TextView mCancel;
	TextView directoryName;
	TextView mPreview;
	TextView mConfirm;

	LayoutInflater inflater;
	MapStorageImagesAdapter mapStorageImagesAdapter;

	List<String> mImages;

	Map<String, SoftReference<Bitmap>> bitmaps;

	int listStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bitmaps = new HashMap<String, SoftReference<Bitmap>>();
		inflater = this.getLayoutInflater();
		setContentView(R.layout.activity_mapstorageimages);
		mImagesContent = (ListView) findViewById(R.id.gv_imagesContent);
		mBack = (ImageView) findViewById(R.id.iv_back);
		mCancel = (TextView) findViewById(R.id.tv_cancel);
		directoryName = (TextView) findViewById(R.id.tv_directoryName);
		mPreview = (TextView) findViewById(R.id.tv_preview);
		mConfirm = (TextView) findViewById(R.id.tv_confirm);
		mImages = MapStorageDirectoryActivity.directoryToImages
				.get(MapStorageDirectoryActivity.currentShowDirectory);
		mapStorageImagesAdapter = new MapStorageImagesAdapter();
		mImagesContent.setAdapter(mapStorageImagesAdapter);
		mImagesContent.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				listStatus = arg1;
				if (arg1 == SCROLL_STATE_IDLE) {
					mapStorageImagesAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
			}
		});
		defaultImage = ThumbnailUtils.extractThumbnail(BitmapFactory
				.decodeResource(getResources(), R.drawable.defaultimage), 100,
				100, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		modifyConfirmStyle();
		initData();
		initEvent();
	}

	private void initEvent() {
		mConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MapStorageDirectoryActivity.selectedImages.clear();

			}
		});
		mPreview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		mCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void initData() {
		String directory = MapStorageDirectoryActivity.currentShowDirectory;
		directory = directory.substring(directory.lastIndexOf("/") + 1);
		directoryName.setText(directory);
	}

	Bitmap defaultImage;

	class MapStorageImagesAdapter extends BaseAdapter {

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
			if (view == null) {
				messageHolder = new MessageHolder();
				view = inflater.inflate(
						R.layout.activity_mapstorageimages_item, null);
				messageHolder.iv1 = (MyImageView) view
						.findViewById(R.id.iv_imageContent1);
				messageHolder.ivStatus1 = (ImageView) view
						.findViewById(R.id.iv_imageContentStatus1);
				messageHolder.iv2 = (MyImageView) view
						.findViewById(R.id.iv_imageContent2);
				messageHolder.ivStatus2 = (ImageView) view
						.findViewById(R.id.iv_imageContentStatus2);
				messageHolder.iv3 = (MyImageView) view
						.findViewById(R.id.iv_imageContent3);
				messageHolder.ivStatus3 = (ImageView) view
						.findViewById(R.id.iv_imageContentStatus3);
				view.setTag(messageHolder);

			} else {
				messageHolder = (MessageHolder) view.getTag();
			}
			final MessageHolder messageHolder0 = messageHolder;
			if (position * 3 < mImages.size()) {
				messageHolder0.iv1.setVisibility(View.VISIBLE);
				final String path = mImages.get(position * 3);
				boolean flag = MapStorageDirectoryActivity.selectedImages
						.contains(path);
				if (flag) {
					messageHolder0.ivStatus1.setVisibility(View.VISIBLE);
				} else {
					messageHolder0.ivStatus1.setVisibility(View.GONE);
				}
				SoftReference<Bitmap> softBitmap = bitmaps.get(path);
				if (softBitmap == null || softBitmap.get() == null) {
					loadImage(path);
					softBitmap = new SoftReference<Bitmap>(defaultImage);
				}
				messageHolder0.iv1.setImageBitmap(softBitmap.get());
				messageHolder0.iv1.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (messageHolder0.ivStatus1.getVisibility() == View.VISIBLE) {
							messageHolder0.ivStatus1.setVisibility(View.GONE);
							MapStorageDirectoryActivity.selectedImages
									.remove(path);
							modifyConfirmStyle();
						} else {
							messageHolder0.ivStatus1
									.setVisibility(View.VISIBLE);
							MapStorageDirectoryActivity.selectedImages
									.add(path);
							modifyConfirmStyle();
						}
					}
				});
			}
			if (position * 3 + 1 < mImages.size()) {
				messageHolder0.iv2.setVisibility(View.VISIBLE);
				final String path = mImages.get(position * 3 + 1);
				boolean flag = MapStorageDirectoryActivity.selectedImages
						.contains(path);
				if (flag) {
					messageHolder0.ivStatus2.setVisibility(View.VISIBLE);
				} else {
					messageHolder0.ivStatus2.setVisibility(View.GONE);
				}
				SoftReference<Bitmap> softBitmap = bitmaps.get(path);
				if (softBitmap == null || softBitmap.get() == null) {
					loadImage(path);
					softBitmap = new SoftReference<Bitmap>(defaultImage);
				}
				messageHolder0.iv2.setImageBitmap(softBitmap.get());
				messageHolder0.iv2.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (messageHolder0.ivStatus2.getVisibility() == View.VISIBLE) {
							messageHolder0.ivStatus2.setVisibility(View.GONE);
							MapStorageDirectoryActivity.selectedImages
									.remove(path);
							modifyConfirmStyle();
						} else {
							messageHolder0.ivStatus2
									.setVisibility(View.VISIBLE);
							MapStorageDirectoryActivity.selectedImages
									.add(path);
							modifyConfirmStyle();
						}
					}
				});
			} else {
				messageHolder0.iv2.setVisibility(View.GONE);
				messageHolder0.ivStatus1.setVisibility(View.GONE);
			}
			if (position * 3 + 2 < mImages.size()) {
				messageHolder0.iv3.setVisibility(View.VISIBLE);
				messageHolder0.iv3.setImageDrawable(getResources().getDrawable(
						R.drawable.defaultimage));
				final String path = mImages.get(position * 3 + 2);
				boolean flag = MapStorageDirectoryActivity.selectedImages
						.contains(path);
				if (flag) {
					messageHolder0.ivStatus3.setVisibility(View.VISIBLE);
				} else {
					messageHolder0.ivStatus3.setVisibility(View.GONE);
				}
				SoftReference<Bitmap> softBitmap = bitmaps.get(path);
				if (softBitmap == null || softBitmap.get() == null) {
					loadImage(path);
					softBitmap = new SoftReference<Bitmap>(defaultImage);
				}
				messageHolder0.iv3.setImageBitmap(softBitmap.get());
				messageHolder0.iv3.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (messageHolder0.ivStatus3.getVisibility() == View.VISIBLE) {
							messageHolder0.ivStatus3.setVisibility(View.GONE);
							MapStorageDirectoryActivity.selectedImages
									.remove(path);
							modifyConfirmStyle();
						} else {
							messageHolder0.ivStatus3
									.setVisibility(View.VISIBLE);
							MapStorageDirectoryActivity.selectedImages
									.add(path);
							modifyConfirmStyle();
						}
					}
				});
			} else {
				messageHolder0.iv3.setVisibility(View.GONE);
				messageHolder0.ivStatus1.setVisibility(View.GONE);
			}

			return view;
		}
	}

	void modifyConfirmStyle() {
		int count = MapStorageDirectoryActivity.selectedImages.size();
		mConfirm.setText("确定(" + count + ")");
		if (count == 0) {
			mPreview.setBackgroundResource(R.drawable.noselectimage_preview);
			mConfirm.setBackgroundResource(R.drawable.noselectimage_preview);
		} else {
			mPreview.setBackgroundResource(R.drawable.selectimage_preview);
			mConfirm.setBackgroundResource(R.drawable.selectimage_preview);
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
				mapStorageImagesAdapter.notifyDataSetChanged();
			}

			@Override
			protected Bitmap asyncExecute() {
				Bitmap bitmapZoom = MCImageUtils.getZoomBitmapFromFile(
						new File(path), 200, 200);
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
		ImageView ivStatus1;
		MyImageView iv2;
		ImageView ivStatus2;
		MyImageView iv3;
		ImageView ivStatus3;
	}
}
