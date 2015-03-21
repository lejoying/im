package com.open.welinks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.open.welinks.R;
import com.open.welinks.customView.ImageDetailFragment;
import com.open.welinks.model.Data;
import com.open.welinks.model.TaskManageHolder;
import com.open.welinks.utils.StreamParser;

public class ImageScanActivity extends FragmentActivity {

	public Data data = Data.getInstance();
	public String tag = "ImageScanActivity";

	public static final String EXTRA_IMAGE = "extra_image";

	public TaskManageHolder taskManageHolder = TaskManageHolder.getInstance();

	private ImagePagerAdapter mAdapter;
	private ViewPager mPager;

	public static int IMAGEBROWSE_COMMON = 0x01;
	public static int IMAGEBROWSE_OPTION = 0x02;

	public ArrayList<String> imagesBrowseList;

	public int currentPosition;
	public int currentType;

	// top bar view
	public RelativeLayout backView;
	public TextView imageNumberView;
	// public ImageView choiceCoverView;
	public ImageView deleteButtonView;

	public RelativeLayout rightContainer;

	public RelativeLayout menuOptionsView;
	public RelativeLayout saveOptionsView;
	public RelativeLayout shareOptionsView;

	public DisplayImageOptions options;

	public OnClickListener mOnClickListener;
	public OnPageChangeListener mOnPageChangeListener;

	public View backMaxView;
	public ImageView backImageView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		currentPosition = Integer.valueOf(getIntent().getStringExtra("position"));
		currentType = getIntent().getIntExtra("type", IMAGEBROWSE_COMMON);
		if (data.tempData.selectedImageList != null) {
			imagesBrowseList = data.tempData.selectedImageList;
		} else {
			imagesBrowseList = new ArrayList<String>();
		}

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

		setContentView(R.layout.image_detail_pager);
		backMaxView = findViewById(R.id.backMaxView);
		menuOptionsView = (RelativeLayout) findViewById(R.id.menuOptions);
		saveOptionsView = (RelativeLayout) findViewById(R.id.saveOption);
		shareOptionsView = (RelativeLayout) findViewById(R.id.shareOption);
		backView = (RelativeLayout) findViewById(R.id.backView);
		imageNumberView = (TextView) findViewById(R.id.backTitleView);
		rightContainer = (RelativeLayout) findViewById(R.id.rightContainer);
		backImageView = (ImageView) findViewById(R.id.backImageView);
		options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).resetViewBeforeLoading(true).cacheOnDisk(true).imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true).displayer(new FadeInBitmapDisplayer(300)).build();

		imageNumberView.setText("浏览  (" + (currentPosition + 1) + "/" + imagesBrowseList.size() + ")");
		backView.setBackgroundResource(R.drawable.selector_back_transparent);
		// backView.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_back_transparent));
		backMaxView.setBackgroundColor(Color.TRANSPARENT);
		backImageView.setColorFilter(Color.WHITE);
		imageNumberView.setTextColor(Color.WHITE);
		deleteButtonView = new ImageView(this);
		int padding = (int) (10 * displaymetrics.density);

		RelativeLayout.LayoutParams layoutParams = (LayoutParams) rightContainer.getLayoutParams();
		layoutParams.rightMargin = 0;

		deleteButtonView.setBackgroundResource(R.drawable.backview_background);
		rightContainer.addView(deleteButtonView);

		menuOptionsView.setVisibility(View.GONE);

		if (currentType == IMAGEBROWSE_COMMON) {
			deleteButtonView.setImageResource(R.drawable.share_to_group_icon);
		} else if (currentType == IMAGEBROWSE_OPTION) {
			deleteButtonView.setImageResource(R.drawable.image_delete);
			deleteButtonView.setPadding(padding, padding, padding, padding);
		}

		mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), imagesBrowseList.size());
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		mPager.setCurrentItem(currentPosition);
		mPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int position) {
				Log.i("Icache", "onPageSelected = " + position);
			}
		});
		final int extraCurrentItem = getIntent().getIntExtra(EXTRA_IMAGE, -1);
		if (extraCurrentItem != -1) {
			mPager.setCurrentItem(extraCurrentItem);
		}

		initializeListeners();
		bindEvent();
	}

	public void initializeListeners() {
		mOnPageChangeListener = new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int state) {
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				currentPosition = position;
				imageNumberView.setText("浏览  (" + (position + 1) + "/" + imagesBrowseList.size() + ")");
			}
		};
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(backView)) {
					setResult(Activity.RESULT_CANCELED);
					finish();
				} else if (view.equals(deleteButtonView)) {
					if (currentType == IMAGEBROWSE_COMMON) {
						if (menuOptionsView.getVisibility() == View.GONE) {
							menuOptionsView.setVisibility(View.VISIBLE);
						} else {
							menuOptionsView.setVisibility(View.GONE);
						}
					} else if (currentType == IMAGEBROWSE_OPTION) {
						imagesBrowseList.remove(currentPosition);
						if (imagesBrowseList.size() == 0) {
							finish();
						} else if (currentPosition > imagesBrowseList.size() - 1) {
							currentPosition = imagesBrowseList.size() - 1;
							// thisView.imageViewPageContent.setCurrentItem(currentPosition);
						}
						mAdapter.notifyDataSetChanged();
						imageNumberView.setText("浏览  (" + (currentPosition + 1) + "/" + imagesBrowseList.size() + ")");
						// Toast.makeText(ImageScanActivity.this, "deleteButtonView", Toast.LENGTH_SHORT).show();
					}
				} else if (view.equals(saveOptionsView)) {
					menuOptionsView.setVisibility(View.GONE);
					saveCloudFile();
				} else if (view.equals(shareOptionsView)) {
					menuOptionsView.setVisibility(View.GONE);
				}
			}
		};
	}

	public void saveCloudFile() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String path = imagesBrowseList.get(currentPosition);
				if (path.lastIndexOf("/") == -1) {
					File file = new File(taskManageHolder.fileHandler.sdcardImageFolder, path);
					path = file.getAbsolutePath();
				}
				String fileName = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
				String suffixName = path.substring(path.lastIndexOf("."));
				if (".osj".equals(suffixName)) {
					suffixName = ".jpg";
				} else if (".osp".equals(suffixName)) {
					suffixName = ".png";
				}
				fileName = fileName + suffixName;
				try {
					FileInputStream fileInputStream = new FileInputStream(new File(path));
					final File saveFile = new File(taskManageHolder.fileHandler.sdcardSaveImageFolder, fileName);
					FileOutputStream fileOutputStream = new FileOutputStream(saveFile);
					StreamParser.parseToFile(fileInputStream, fileOutputStream);
					taskManageHolder.fileHandler.handler.post(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(ImageScanActivity.this, "文件位置:" + saveFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
						}
					});
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void bindEvent() {
		this.backView.setOnClickListener(mOnClickListener);
		this.deleteButtonView.setOnClickListener(mOnClickListener);
		this.mPager.setOnPageChangeListener(mOnPageChangeListener);
		this.saveOptionsView.setOnClickListener(mOnClickListener);
		this.shareOptionsView.setOnClickListener(mOnClickListener);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (backMaxView.getVisibility() == View.VISIBLE) {
				backMaxView.setVisibility(View.GONE);
			} else {
				backMaxView.setVisibility(View.VISIBLE);
			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void onResume() {
		super.onResume();
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onBackPressed() {
		setResult(Activity.RESULT_CANCELED);
		finish();
		super.onBackPressed();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	private class ImagePagerAdapter extends FragmentStatePagerAdapter {

		public ImagePagerAdapter(FragmentManager fm, int size) {
			super(fm);
		}

		@Override
		public int getCount() {
			return imagesBrowseList.size();
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public Fragment getItem(int position) {
			String fileName = imagesBrowseList.get(position);
			return ImageDetailFragment.newInstance(position, fileName);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			final ImageDetailFragment fragment = (ImageDetailFragment) object;
			fragment.cancelWork();
			super.destroyItem(container, position, object);
		}
	}
}
