package com.open.welinks;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.open.welinks.model.Data;

public class ImageScanActivity extends FragmentActivity {

	public Data data = Data.getInstance();
	public String tag = "ImageScanActivity";

	public static final String EXTRA_IMAGE = "extra_image";

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
	public TextView titleView;
	// public ImageView choiceCoverView;
	public ImageView deleteButtonView;

	public RelativeLayout rightContainer;

	public DisplayImageOptions options;
	public ImageLoader imageLoader = ImageLoader.getInstance();

	public OnClickListener mOnClickListener;
	public OnPageChangeListener mOnPageChangeListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_detail_pager);

		currentPosition = Integer.valueOf(getIntent().getStringExtra("position"));
		currentType = getIntent().getIntExtra("type", IMAGEBROWSE_COMMON);
		if (data.tempData.selectedImageList != null)
			imagesBrowseList = data.tempData.selectedImageList;
		else
			imagesBrowseList = new ArrayList<String>();

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

		backView = (RelativeLayout) findViewById(R.id.backView);
		imageNumberView = (TextView) findViewById(R.id.backTitleView);
		titleView = (TextView) findViewById(R.id.titleContent);
		rightContainer = (RelativeLayout) findViewById(R.id.rightContainer);
		options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).resetViewBeforeLoading(true).cacheOnDisk(true).imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true).displayer(new FadeInBitmapDisplayer(300)).build();

		imageNumberView.setText((currentPosition + 1) + "/" + imagesBrowseList.size());
		titleView.setText("浏览");
		deleteButtonView = new ImageView(this);
		deleteButtonView.setImageResource(R.drawable.image_delete);
		deleteButtonView.setPadding((int) (30 * displaymetrics.density), (int) (15 * displaymetrics.density), (int) (30 * displaymetrics.density), (int) (15 * displaymetrics.density));
		rightContainer.addView(deleteButtonView);
		if (currentType == IMAGEBROWSE_COMMON) {
			deleteButtonView.setVisibility(View.GONE);
		} else if (currentType == IMAGEBROWSE_OPTION) {
			deleteButtonView.setVisibility(View.VISIBLE);
		}

		mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), imagesBrowseList.size());
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
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
				imageNumberView.setText(position + 1 + "/" + imagesBrowseList.size());
			}
		};
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(backView)) {
					setResult(Activity.RESULT_CANCELED);
					finish();
				} else if (view.equals(deleteButtonView)) {
					imagesBrowseList.remove(currentPosition);
					if (imagesBrowseList.size() == 0) {
						finish();
					} else if (currentPosition > imagesBrowseList.size() - 1) {
						currentPosition = imagesBrowseList.size() - 1;
						// thisView.imageViewPageContent.setCurrentItem(currentPosition);
					}
					mAdapter.notifyDataSetChanged();
					imageNumberView.setText(currentPosition + 1 + "/" + imagesBrowseList.size());
					Toast.makeText(ImageScanActivity.this, "deleteButtonView", Toast.LENGTH_SHORT).show();
				}
			}
		};
	}

	public void bindEvent() {
		backView.setOnClickListener(mOnClickListener);
		deleteButtonView.setOnClickListener(mOnClickListener);
		mPager.setOnPageChangeListener(mOnPageChangeListener);
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
