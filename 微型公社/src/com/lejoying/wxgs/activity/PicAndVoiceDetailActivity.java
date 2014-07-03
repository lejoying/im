package com.lejoying.wxgs.activity;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.utils.MCImageUtils;
import com.lejoying.wxgs.activity.view.RecordView;
import com.lejoying.wxgs.activity.view.RecordView.PlayButtonClickListener;
import com.lejoying.wxgs.activity.view.RecordView.ProgressListener;
import com.lejoying.wxgs.activity.view.SampleView;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileResult;

public class PicAndVoiceDetailActivity extends Activity implements
		OnClickListener, OnTouchListener {

	MainApplication app = MainApplication.getMainApplication();
	RelativeLayout ll_picandvoice_navigation;
	TextView tv_setcover, mediaTotalView;
	ImageView PicAndVoiceDetailBack, deleteMediaView, selectedCoverView;
	ViewPager picandvoice_Pager;
	LayoutInflater mInflater;
	String activity;
	ArrayList<String> content;

	int mediaTotal = 0;
	int currentCoverIndex = -1;

	public static int height, width;
	int dip;
	float density;

	List<View> mainListViews;
	boolean iscover = false;

	int currentPageSize = 0;
	MediaPlayer currentPlayVoice;
	MyPageAdapter myPageAdapter;
	Bitmaps bitmaps;

	List<MediaPlayer> players = new ArrayList<MediaPlayer>();

	GestureDetector backViewDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int currentIndex = getIntent().getExtras().getInt("currentIndex");
		activity = getIntent().getExtras().getString("Activity");
		content = getIntent().getStringArrayListExtra("content");
		mInflater = getLayoutInflater();
		setContentView(R.layout.activity_picandvoicedetail);
		getWindow().setBackgroundDrawableResource(R.drawable.square_background);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		dip = (int) (40 * density + 0.5f);
		height = dm.heightPixels;
		width = dm.widthPixels;
		initLayout();
		initData();
		initEvent();
		if (activity.equals("ReleaseActivity")) {
			currentCoverIndex = ReleaseActivity.currentCoverIndex;
		}
		if (currentCoverIndex == 1) {
			selectedCoverView.setImageResource(R.drawable.picandvoice_affirm);
		}
		currentPageSize = 1;
		myPageAdapter = new MyPageAdapter(mainListViews);
		picandvoice_Pager.setAdapter(myPageAdapter);
		if (activity.equals("Browse")) {
			picandvoice_Pager.setCurrentItem(currentIndex, true);
		} else {
			picandvoice_Pager.setCurrentItem(currentIndex - 1, true);
		}

	}

	private void initEvent() {
		deleteMediaView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (activity.equals("ReleaseActivity")) {
					if (currentCoverIndex == currentPageSize) {
						iscover = false;
						currentCoverIndex = -1;
						selectedCoverView
								.setImageResource(R.drawable.picandvoice_cancel);
					}
					if (currentPageSize <= ReleaseActivity.voices.size()) {
						ReleaseActivity.voices.remove(currentPageSize - 1);
					} else {
						ReleaseActivity.images.remove(currentPageSize
								- ReleaseActivity.voices.size() - 1);
					}
					if (ReleaseActivity.voices.size()
							+ ReleaseActivity.images.size() == 0) {
						mFinish();
					} else {
						initData();
						myPageAdapter = new MyPageAdapter(mainListViews);
						picandvoice_Pager.setAdapter(myPageAdapter);
					}
					if (ReleaseActivity.voices.size()
							+ ReleaseActivity.images.size() > currentPageSize) {
						picandvoice_Pager.setCurrentItem(currentPageSize - 1);
						mediaTotalView.setText(currentPageSize + "/"
								+ mediaTotal);
					} else {
						currentPageSize = ReleaseActivity.voices.size()
								+ ReleaseActivity.images.size();
						picandvoice_Pager.setCurrentItem(currentPageSize - 1);
						mediaTotalView.setText(currentPageSize + "/"
								+ mediaTotal);
					}

				} else if (activity.equals("MapStrage")) {
					if (content != null && content.size() != 0) {
						content.remove(currentPageSize - 1);
						mainListViews.remove(currentPageSize - 1);
						mediaTotal--;
						if (content.size() == 0) {
							mFinish();
						} else {
							myPageAdapter.notifyDataSetChanged();
							// initData();
							// myPageAdapter = new MyPageAdapter(mainListViews);
							// picandvoice_Pager.setAdapter(myPageAdapter);
						}
						if (content.size() > currentPageSize) {
							picandvoice_Pager
									.setCurrentItem(currentPageSize - 1);
							mediaTotalView.setText(currentPageSize + "/"
									+ mediaTotal);
						} else {
							currentPageSize = content.size();
							picandvoice_Pager
									.setCurrentItem(currentPageSize - 1);
							mediaTotalView.setText(currentPageSize + "/"
									+ mediaTotal);
						}
					} else {
						MapStorageDirectoryActivity.selectedImages
								.remove(currentPageSize - 1);
						mainListViews.remove(currentPageSize - 1);
						mediaTotal--;
						if (MapStorageDirectoryActivity.selectedImages.size() == 0) {
							mFinish();
						} else {
							myPageAdapter.notifyDataSetChanged();
							// initData();
							// myPageAdapter = new MyPageAdapter(mainListViews);
							// picandvoice_Pager.setAdapter(myPageAdapter);
						}
						if (MapStorageDirectoryActivity.selectedImages.size() > currentPageSize) {
							picandvoice_Pager
									.setCurrentItem(currentPageSize - 1);
							mediaTotalView.setText(currentPageSize + "/"
									+ mediaTotal);
						} else {
							currentPageSize = MapStorageDirectoryActivity.selectedImages
									.size();
							picandvoice_Pager
									.setCurrentItem(currentPageSize - 1);
							mediaTotalView.setText(currentPageSize + "/"
									+ mediaTotal);
						}
					}
				} else {

				}
			}
		});
		picandvoice_Pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				currentPageSize = arg0 + 1;
				iscover = false;
				mediaTotalView.setText(currentPageSize + "/" + mediaTotal);
				if (currentPlayVoice != null) {
					if (currentPlayVoice.isPlaying()) {
						currentPlayVoice.pause();
					}
				}
				if (currentCoverIndex == currentPageSize) {
					iscover = true;
					selectedCoverView
							.setImageResource(R.drawable.picandvoice_affirm);
				} else if (currentCoverIndex > 0) {
					selectedCoverView
							.setImageResource(R.drawable.picandvoice_cancel);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		backViewDetector = new GestureDetector(PicAndVoiceDetailActivity.this,
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onDown(MotionEvent e) {
						return true;
					}

					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						mFinish();
						return true;
					}
				});
		tv_setcover.setOnClickListener(this);
		selectedCoverView.setOnClickListener(this);

		mediaTotalView.setOnTouchListener(this);
		PicAndVoiceDetailBack.setOnTouchListener(this);

	}

	public void onBackPressed() {
		mFinish();
	}

	void initLayout() {
		ll_picandvoice_navigation = (RelativeLayout) findViewById(R.id.rl_picandvoice_navigation);
		tv_setcover = (TextView) findViewById(R.id.tv_setcover);
		mediaTotalView = (TextView) findViewById(R.id.tv_number);
		PicAndVoiceDetailBack = (ImageView) findViewById(R.id.PicAndVoiceDetailBack);
		deleteMediaView = (ImageView) findViewById(R.id.iv_picandvoice_del);
		selectedCoverView = (ImageView) findViewById(R.id.iv_picandvoice_cancel);
		picandvoice_Pager = (ViewPager) findViewById(R.id.picandvoice_Pager);

		if (activity.equals("ReleaseActivity")) {

		} else if (activity.equals("MapStrage")) {
			tv_setcover.setText("预览");
			selectedCoverView.setVisibility(View.GONE);

		} else if (activity.equals("Browse")) {
			tv_setcover.setText("浏览");
			selectedCoverView.setVisibility(View.GONE);
			deleteMediaView.setVisibility(View.GONE);

		}

	}

	void initData() {
		mediaTotal = 0;
		mainListViews = new ArrayList<View>();
		bitmaps = new Bitmaps();
		if (activity.equals("ReleaseActivity")) {
			for (int i = 0; i < ReleaseActivity.voices.size(); i++) {
				mediaTotal++;
				final RecordView recordView = new RecordView(
						PicAndVoiceDetailActivity.this);
				recordView.setMode(RecordView.MODE_PROGRESS);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						width, height);
				recordView.setLayoutParams(params);
				String voiceFileName = (String) ReleaseActivity.voices.get(i)
						.get("fileName");
				File voiceFile = new File(app.sdcardVoiceFolder, voiceFileName);
				if (voiceFile.exists()) {
					final MediaPlayer player = MediaPlayer.create(
							PicAndVoiceDetailActivity.this,
							Uri.parse(voiceFile.getAbsolutePath()));
					try {
						player.prepare();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					players.add(player);
					recordView.setProgressTime(player.getDuration());
					recordView
							.setPlayButtonClickListener(new PlayButtonClickListener() {

								@Override
								public void onPlay() {
									if (!player.isPlaying()) {
										currentPlayVoice = player;
										player.start();
										recordView.startProgress();
									}

								}

								@Override
								public void onPause() {
									if (player.isPlaying()) {
										player.pause();
										recordView.stopProgress();
									}
								}
							});
					recordView.setProgressListener(new ProgressListener() {

						@Override
						public void onProgressEnd() {
							if (player.isPlaying()) {
								player.stop();
								recordView.stopProgress();
							}

						}

						@Override
						public void onDrag(float percent) {
							player.seekTo((int) (player.getDuration() * percent));

						}
					});
				}
				mainListViews.add(recordView);
			}
			for (int i = 0; i < ReleaseActivity.images.size(); i++) {
				mediaTotal++;
				LinearLayout superView = (LinearLayout) mInflater.inflate(
						R.layout.view_child, null);
				ImageView iv = (ImageView) superView
						.findViewById(R.id.iv_child);
				Map<String, Object> map = ReleaseActivity.images.get(i);
				if (map.get("type") != null) {
					SampleView sampleview = new SampleView(this,
							(byte[]) map.get("data"), width, height);
					superView.addView(sampleview);
					iv.setVisibility(View.GONE);
				} else {
					iv.setImageBitmap(new SoftReference<Bitmap>(
							(Bitmap) ReleaseActivity.images.get(i)
									.get("bitmap")).get());
				}
				mainListViews.add(superView);
			}
		} else if (activity.equals("MapStrage")) {
			if (content != null && content.size() != 0) {
				for (int i = 0; i < content.size(); i++) {
					mediaTotal++;
					LinearLayout superView = (LinearLayout) mInflater.inflate(
							R.layout.view_child, null);
					ImageView iv = (ImageView) superView
							.findViewById(R.id.iv_child);
					bitmaps.put(
							content.get(i),
							MCImageUtils.getZoomBitmapFromFile(
									new File(content.get(i)), width, height));
					iv.setImageBitmap(bitmaps.get(content.get(i)));
					mainListViews.add(superView);
				}
			} else {
				for (int i = 0; i < MapStorageDirectoryActivity.selectedImages
						.size(); i++) {
					mediaTotal++;
					LinearLayout superView = (LinearLayout) mInflater.inflate(
							R.layout.view_child, null);
					ImageView iv = (ImageView) superView
							.findViewById(R.id.iv_child);
					bitmaps.put(MapStorageDirectoryActivity.selectedImages
							.get(i), MCImageUtils.getZoomBitmapFromFile(
							new File(MapStorageDirectoryActivity.selectedImages
									.get(i)), width, height));
					iv.setImageBitmap(bitmaps
							.get(MapStorageDirectoryActivity.selectedImages
									.get(i)));
					mainListViews.add(superView);
				}
			}
		} else if (activity.equals("Browse")) {
			for (int i = 0; i < content.size(); i++) {
				final int location = i;
				mediaTotal++;
				LinearLayout superView = (LinearLayout) mInflater.inflate(
						R.layout.view_child, null);
				final ImageView iv = (ImageView) superView
						.findViewById(R.id.iv_child);
				app.fileHandler.getImage(content.get(i), new FileResult() {
					@Override
					public void onResult(String where, Bitmap bitmap) {
						bitmaps.put(content.get(location), bitmap);
						iv.setImageBitmap(bitmaps.get(content.get(location)));
					}
				});
				mainListViews.add(superView);
			}
		}
		mediaTotalView.setText(1 + "/" + mediaTotal);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// case R.id.PicAndVoiceDetailBack:
		// case R.id.tv_number:
		// mFinish();
		// break;
		case R.id.tv_setcover:
		case R.id.iv_picandvoice_cancel:
			if (activity.equals("ReleaseActivity")) {
				if (iscover) {
					selectedCoverView
							.setImageResource(R.drawable.picandvoice_cancel);
					iscover = false;
					// ReleaseActivity.cover = "";
					currentCoverIndex = -1;
				} else {
					selectedCoverView
							.setImageResource(R.drawable.picandvoice_affirm);
					iscover = true;
					currentCoverIndex = currentPageSize;
					// if (currentPageSize <= ReleaseActivity.voices.size()) {
					// ReleaseActivity.cover = (String) ReleaseActivity.voices
					// .get(currentPageSize - 1).get("fileName");
					// } else {
					// ReleaseActivity.cover = (String) ReleaseActivity.images
					// .get(currentPageSize
					// - ReleaseActivity.voices.size() - 1)
					// .get("fileName");
					// }
				}
			}
			break;
		default:
			break;
		}

	}

	public void mFinish() {
		if (activity.equals("ReleaseActivity")) {
			for (int i = 0; i < players.size(); i++) {
				if (players.get(i) != null) {
					players.get(i).release();
				}
			}
			ReleaseActivity.currentCoverIndex = currentCoverIndex;
			Intent intent = new Intent();
			setResult(Activity.RESULT_OK, intent);
		} else if (activity.equals("MapStrage")) {
			Intent intent = new Intent();
			if (content != null) {
				intent.putStringArrayListExtra("photoList", content);
			}
			setResult(Activity.RESULT_OK, intent);
		}
		finish();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		switch (v.getId()) {
		case R.id.PicAndVoiceDetailBack:
		case R.id.tv_number:
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mediaTotalView.setBackgroundColor(Color.argb(143, 0, 0, 0));
				PicAndVoiceDetailBack.setBackgroundColor(Color.argb(143, 0, 0,
						0));
				break;
			case MotionEvent.ACTION_UP:
				mediaTotalView.setBackgroundColor(Color.argb(0, 0, 0, 0));
				PicAndVoiceDetailBack
						.setBackgroundColor(Color.argb(0, 0, 0, 0));
				break;
			}
			break;
		}
		return backViewDetector.onTouchEvent(event);
	}

	public class Bitmaps {
		public Map<String, SoftReference<Bitmap>> softBitmaps = new Hashtable<String, SoftReference<Bitmap>>();

		public void put(String key, Bitmap bitmap) {
			softBitmaps.put(key, new SoftReference<Bitmap>(bitmap));
		}

		public Bitmap get(String key) {
			if (softBitmaps.get(key) == null) {
				return null;
			}
			return softBitmaps.get(key).get();
		}
	}

}

class MyPageAdapter extends PagerAdapter {
	List<View> mListViews;

	public MyPageAdapter(List<View> mListViews) {
		this.mListViews = mListViews;
	}

	@Override
	public int getCount() {
		return mListViews.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object obj) {
		return (view == obj);
	}

	@Override
	public Object instantiateItem(ViewGroup view, int position) {
		view.addView(mListViews.get(position));
		return mListViews.get(position);
	}

	@Override
	public void destroyItem(View view, int position, Object obj) {
		((ViewPager) view).removeView((View)obj);
	}

	@Override
	public int getItemPosition(Object object) {

		return POSITION_NONE;
	}

	@Override
	public Parcelable saveState() {

		return null;
	}

	@Override
	public void startUpdate(View container) {

	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {

	}

	@Override
	public void finishUpdate(ViewGroup container) {

	}
}
