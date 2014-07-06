package com.lejoying.wxgs.activity;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.utils.MCImageUtils;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.handler.AsyncHandler.Execution;

public class MapStorageImagesActivity extends Activity {

	MainApplication app = MainApplication.getMainApplication();

	GridView mImagesContent;
	ImageView mBack;
	TextView mCancel;
	TextView directoryName;
	TextView mPreview;
	TextView mConfirm;
	RelativeLayout mBottomBar;

	LayoutInflater inflater;

	MapStorageImagesAdapter mapStorageImagesAdapter;

	List<HashMap<String, Object>> mImages;

	float height, width, dip;
	float density;
	int columnSide;

	int RESULT_PICANDVOICE = 0x15;

	Map<String, SoftReference<Bitmap>> bitmaps;

	int listStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bitmaps = new HashMap<String, SoftReference<Bitmap>>();
		inflater = this.getLayoutInflater();
		setContentView(R.layout.activity_mapstorageimages);
		mImagesContent = (GridView) findViewById(R.id.gv_imagesContent);
		mBack = (ImageView) findViewById(R.id.iv_back);
		mCancel = (TextView) findViewById(R.id.tv_cancel);
		directoryName = (TextView) findViewById(R.id.tv_directoryName);
		mPreview = (TextView) findViewById(R.id.tv_preview);
		mConfirm = (TextView) findViewById(R.id.tv_confirm);
		mBottomBar = (RelativeLayout) findViewById(R.id.rl_bottomBar);
		mImages = MapStorageDirectoryActivity.directoryToImages
				.get(MapStorageDirectoryActivity.currentShowDirectory);

		initData();
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
		initEvent();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT_PICANDVOICE
				&& resultCode == Activity.RESULT_OK && data != null) {
			mapStorageImagesAdapter.notifyDataSetChanged();
			modifyConfirmStyle();
		}
	}

	private void initEvent() {
		mBottomBar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
			}
		});
		mConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(Activity.RESULT_OK);
				finish();
			}
		});
		mPreview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (MapStorageDirectoryActivity.selectedImages.size() != 0) {
					Intent intent = new Intent(MapStorageImagesActivity.this,
							PicAndVoiceDetailActivity.class);
					intent.putExtra("Activity", "MapStrage");
					startActivityForResult(intent, RESULT_PICANDVOICE);
				}
			}
		});
		mCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(Activity.RESULT_OK);
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

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		dip = (int) (40 * density + 0.5f);
		height = dm.heightPixels;
		width = dm.widthPixels;
		columnSide = (int) ((width - dp2px(20)) / 3);
	}

	public float dp2px(float px) {
		float dp = density * px + 0.5f;
		return dp;
	}

	Bitmap defaultImage;

	class MapStorageImagesAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mImages.size();
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
		public View getView(final int arg0, View arg1, ViewGroup arg2) {
			ImagesHolder imagesHolder = null;
			if (arg1 == null) {
				arg1 = inflater.inflate(
						R.layout.activity_mapstorageimages_item, null);
				ImageView iv = (ImageView) arg1.findViewById(R.id.iv_image);
				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
						columnSide, columnSide);
				iv.setLayoutParams(layoutParams);
				ImageView ivStatus = (ImageView) arg1
						.findViewById(R.id.iv_imageContentStatus);
				RelativeLayout.LayoutParams ivStatusParams = new RelativeLayout.LayoutParams(
						ivStatus.getLayoutParams());
				ivStatusParams.leftMargin = (int) (columnSide * 0.75f) - 4;
				ivStatusParams.topMargin = (int) (columnSide * 0.04f) + 3;
				ivStatus.setLayoutParams(ivStatusParams);
				imagesHolder = new ImagesHolder();
				imagesHolder.iv = iv;
				imagesHolder.ivStatus = ivStatus;
				arg1.setTag(imagesHolder);
			} else {
				imagesHolder = (ImagesHolder) arg1.getTag();
			}
			final String path = (String) mImages.get(arg0).get("path");
			boolean flag = MapStorageDirectoryActivity.selectedImages
					.contains(path);
			if (flag) {
				imagesHolder.ivStatus.setVisibility(View.VISIBLE);
			} else {
				imagesHolder.ivStatus.setVisibility(View.GONE);
			}
			SoftReference<Bitmap> softBitmap = bitmaps.get(path);
			if (softBitmap == null || softBitmap.get() == null) {
				loadImage(path);
				softBitmap = new SoftReference<Bitmap>(defaultImage);
			}
			imagesHolder.iv.setImageBitmap(softBitmap.get());
			final ImagesHolder imagesHolder0 = imagesHolder;
			imagesHolder.iv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg) {
					if (MapStorageDirectoryActivity.max == 0) {
						if (imagesHolder0.ivStatus.getVisibility() == View.VISIBLE) {
							imagesHolder0.ivStatus.setVisibility(View.GONE);
							MapStorageDirectoryActivity.selectedImages
									.remove(path);
							MapStorageDirectoryActivity.selectedImagesMap
									.remove(mImages.get(arg0));
							modifyConfirmStyle();
						} else {
							imagesHolder0.ivStatus.setVisibility(View.VISIBLE);
							MapStorageDirectoryActivity.selectedImages
									.add(path);
							MapStorageDirectoryActivity.selectedImagesMap.put(
									path, mImages.get(arg0));
							modifyConfirmStyle();
						}
					} else if (MapStorageDirectoryActivity.max == 1) {
						imagesHolder0.ivStatus.setVisibility(View.VISIBLE);
						MapStorageDirectoryActivity.selectedImages.add(path);
						MapStorageDirectoryActivity.selectedImagesMap.put(path,
								mImages.get(arg0));
						modifyConfirmStyle();
						Intent intent = new Intent();
						setResult(Activity.RESULT_OK, intent);
						finish();
					} else {
						if (MapStorageDirectoryActivity.selectedImages.size() < MapStorageDirectoryActivity.max) {
							if (imagesHolder0.ivStatus.getVisibility() == View.VISIBLE) {
								imagesHolder0.ivStatus.setVisibility(View.GONE);
								MapStorageDirectoryActivity.selectedImages
										.remove(path);
								MapStorageDirectoryActivity.selectedImagesMap
										.remove(mImages.get(arg0));
								modifyConfirmStyle();
							} else {
								imagesHolder0.ivStatus
										.setVisibility(View.VISIBLE);
								MapStorageDirectoryActivity.selectedImages
										.add(path);
								MapStorageDirectoryActivity.selectedImagesMap
										.put(path, mImages.get(arg0));
								modifyConfirmStyle();
							}
						} else {
							if (imagesHolder0.ivStatus.getVisibility() == View.VISIBLE) {
								imagesHolder0.ivStatus.setVisibility(View.GONE);
								MapStorageDirectoryActivity.selectedImages
										.remove(path);
								MapStorageDirectoryActivity.selectedImagesMap
										.remove(mImages.get(arg0));
								modifyConfirmStyle();
							} else {
								Alert.showMessage("您最多能选择"
										+ MapStorageDirectoryActivity.max
										+ "张图片。");
							}

						}
					}
				}
			});
			return arg1;
		}
	}

	class ImagesHolder {
		ImageView iv;
		ImageView ivStatus;
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

}
