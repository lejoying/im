package com.lejoying.wxgs.activity;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.utils.MCImageUtils;
import com.lejoying.wxgs.activity.view.RecordView;
import com.lejoying.wxgs.activity.view.RecordView.PlayButtonClickListener;
import com.lejoying.wxgs.activity.view.RecordView.ProgressListener;
import com.lejoying.wxgs.activity.view.SampleView;
import com.lejoying.wxgs.app.MainApplication;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;

public class PicAndVoiceDetailActivity extends Activity implements
		OnClickListener {

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

	int height, width, dip;
	float density;

	List<View> mainListViews;
	boolean iscover = false;

	int currentPageSize = 0;
	MediaPlayer currentPlayVoice;
	MyPageAdapter myPageAdapter;

	List<MediaPlayer> players = new ArrayList<MediaPlayer>();

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
						ReleaseActivity.cover = "";
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
					MapStorageDirectoryActivity.selectedImages
							.remove(currentPageSize - 1);
					if (MapStorageDirectoryActivity.selectedImages.size() == 0) {
						mFinish();
					} else {
						initData();
						myPageAdapter = new MyPageAdapter(mainListViews);
						picandvoice_Pager.setAdapter(myPageAdapter);
					}
					if (MapStorageDirectoryActivity.selectedImages.size() > currentPageSize) {
						picandvoice_Pager.setCurrentItem(currentPageSize - 1);
						mediaTotalView.setText(currentPageSize + "/"
								+ mediaTotal);
					} else {
						currentPageSize = MapStorageDirectoryActivity.selectedImages
								.size();
						picandvoice_Pager.setCurrentItem(currentPageSize - 1);
						mediaTotalView.setText(currentPageSize + "/"
								+ mediaTotal);
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

		tv_setcover
				.setTextSize(TypedValue.COMPLEX_UNIT_PX, width * 0.04861111f);
		mediaTotalView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				width * 0.04861111f);

		RelativeLayout.LayoutParams navigationLayoutParam = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, (int) (height * 0.078125f));
		navigationLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		ll_picandvoice_navigation.setLayoutParams(navigationLayoutParam);

		if (activity.equals("ReleaseActivity")) {
			RelativeLayout.LayoutParams PicAndVoiceDetailBackLayoutParam = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, (int) (25 * density + 0.5f));
			PicAndVoiceDetailBackLayoutParam
					.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			PicAndVoiceDetailBackLayoutParam.leftMargin = (int) (width * 0.06944444f);
			PicAndVoiceDetailBack
					.setLayoutParams(PicAndVoiceDetailBackLayoutParam);

			RelativeLayout.LayoutParams mediaTotalViewLayoutParam = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mediaTotalViewLayoutParam.addRule(RelativeLayout.ALIGN_LEFT);
			mediaTotalViewLayoutParam.addRule(RelativeLayout.RIGHT_OF,
					R.id.PicAndVoiceDetailBack);
			mediaTotalViewLayoutParam.leftMargin = (int) (width * 0.02777778f);
			mediaTotalView.setLayoutParams(mediaTotalViewLayoutParam);

			RelativeLayout.LayoutParams tv_setcoverLayoutParam = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			tv_setcoverLayoutParam.addRule(RelativeLayout.ALIGN_RIGHT);
			tv_setcoverLayoutParam.addRule(RelativeLayout.LEFT_OF,
					R.id.iv_picandvoice_cancel);
			tv_setcoverLayoutParam.rightMargin = (int) (width * 0.013888889f);
			tv_setcover.setLayoutParams(tv_setcoverLayoutParam);

			RelativeLayout.LayoutParams selectedCoverViewLayoutParam = new RelativeLayout.LayoutParams(
					(int) (width * 0.06944444f), (int) (height * 0.0390625f));
			selectedCoverViewLayoutParam.addRule(RelativeLayout.ALIGN_RIGHT);
			selectedCoverViewLayoutParam.addRule(RelativeLayout.LEFT_OF,
					R.id.iv_picandvoice_del);
			selectedCoverViewLayoutParam.rightMargin = (int) (width * 0.20138889f);
			selectedCoverView.setLayoutParams(selectedCoverViewLayoutParam);

			RelativeLayout.LayoutParams deleteMediaViewLayoutParam = new RelativeLayout.LayoutParams(
					(int) (width * 0.06944444f), (int) (height * 0.0390625f));
			deleteMediaViewLayoutParam
					.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			deleteMediaViewLayoutParam.rightMargin = (int) (width * 0.0833333f);
			deleteMediaView.setLayoutParams(deleteMediaViewLayoutParam);

		} else if (activity.equals("MapStrage")) {
			tv_setcover.setText("预览");
			selectedCoverView.setVisibility(View.GONE);

			RelativeLayout.LayoutParams PicAndVoiceDetailBackLayoutParam = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, (int) (25 * density + 0.5f));
			PicAndVoiceDetailBackLayoutParam
					.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			PicAndVoiceDetailBackLayoutParam.leftMargin = (int) (width * 0.06944444f);
			PicAndVoiceDetailBack
					.setLayoutParams(PicAndVoiceDetailBackLayoutParam);

			RelativeLayout.LayoutParams mediaTotalViewLayoutParam = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mediaTotalViewLayoutParam.addRule(RelativeLayout.ALIGN_LEFT);
			mediaTotalViewLayoutParam.addRule(RelativeLayout.RIGHT_OF,
					R.id.PicAndVoiceDetailBack);
			mediaTotalViewLayoutParam.leftMargin = (int) (width * 0.02777778f);
			mediaTotalView.setLayoutParams(mediaTotalViewLayoutParam);

			RelativeLayout.LayoutParams tv_setcoverLayoutParam = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			tv_setcoverLayoutParam.addRule(RelativeLayout.LEFT_OF,
					R.id.iv_picandvoice_del);
			tv_setcoverLayoutParam.rightMargin = (int) (width * 0.326388889f);
			tv_setcover.setLayoutParams(tv_setcoverLayoutParam);

			RelativeLayout.LayoutParams deleteMediaViewLayoutParam = new RelativeLayout.LayoutParams(
					(int) (width * 0.06944444f), (int) (height * 0.0390625f));
			deleteMediaViewLayoutParam
					.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			deleteMediaViewLayoutParam.rightMargin = (int) (width * 0.0833333f);
			deleteMediaView.setLayoutParams(deleteMediaViewLayoutParam);

		} else if (activity.equals("Browse")) {
			tv_setcover.setText("预览");
			selectedCoverView.setVisibility(View.GONE);
			deleteMediaView.setVisibility(View.GONE);

			RelativeLayout.LayoutParams PicAndVoiceDetailBackLayoutParam = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, (int) (25 * density + 0.5f));
			PicAndVoiceDetailBackLayoutParam
					.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			PicAndVoiceDetailBackLayoutParam.leftMargin = (int) (width * 0.06944444f);
			PicAndVoiceDetailBack
					.setLayoutParams(PicAndVoiceDetailBackLayoutParam);

			RelativeLayout.LayoutParams mediaTotalViewLayoutParam = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mediaTotalViewLayoutParam.addRule(RelativeLayout.ALIGN_LEFT);
			mediaTotalViewLayoutParam.addRule(RelativeLayout.RIGHT_OF,
					R.id.PicAndVoiceDetailBack);
			mediaTotalViewLayoutParam.leftMargin = (int) (width * 0.02777778f);
			mediaTotalView.setLayoutParams(mediaTotalViewLayoutParam);

			RelativeLayout.LayoutParams tv_setcoverLayoutParam = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			tv_setcoverLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			tv_setcoverLayoutParam.rightMargin = (int) (width * 0.47916667f);
			tv_setcover.setLayoutParams(tv_setcoverLayoutParam);

		}

		tv_setcover.setOnClickListener(this);
		PicAndVoiceDetailBack.setOnClickListener(this);
		deleteMediaView.setOnClickListener(this);
		selectedCoverView.setOnClickListener(this);
	}

	void initData() {
		mediaTotal = 0;
		mainListViews = new ArrayList<View>();
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
						R.layout.release_child_navigation, null);
				ImageView iv = (ImageView) superView
						.findViewById(R.id.iv_release_child);
				Map<String, Object> map = ReleaseActivity.images.get(i);
				if (map.get("gifMovie") != null) {
					GifMovie gifMovie = (GifMovie) map.get("gifMovie");
					SampleView sampleview = new SampleView(this, gifMovie,
							width, height);
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
			for (int i = 0; i < MapStorageDirectoryActivity.selectedImages
					.size(); i++) {
				mediaTotal++;
				LinearLayout superView = (LinearLayout) mInflater.inflate(
						R.layout.release_child_navigation, null);
				ImageView iv = (ImageView) superView
						.findViewById(R.id.iv_release_child);
				SoftReference<Bitmap> bm = new SoftReference<Bitmap>(
						MCImageUtils
								.getZoomBitmapFromFile(
										new File(
												MapStorageDirectoryActivity.selectedImages
														.get(i)), width, height));
				iv.setImageBitmap(bm.get());
				mainListViews.add(superView);
			}
		} else if (activity.equals("Browse")) {
			for (int i = 0; i < content.size(); i++) {
				mediaTotal++;
				LinearLayout superView = (LinearLayout) mInflater.inflate(
						R.layout.release_child_navigation, null);
				ImageView iv = (ImageView) superView
						.findViewById(R.id.iv_release_child);
				SoftReference<Bitmap> bm = new SoftReference<Bitmap>(
						app.fileHandler.bitmaps.get(content.get(i)));
				iv.setImageBitmap(bm.get());
				mainListViews.add(superView);
			}
		}
		mediaTotalView.setText(1 + "/" + mediaTotal);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.PicAndVoiceDetailBack:
			mFinish();
			break;
		case R.id.tv_setcover:
		case R.id.iv_picandvoice_cancel:
			if (activity.equals("ReleaseActivity")) {
				if (iscover) {
					selectedCoverView
							.setImageResource(R.drawable.picandvoice_cancel);
					iscover = false;
					ReleaseActivity.cover = "";
					currentCoverIndex = -1;
				} else {
					selectedCoverView
							.setImageResource(R.drawable.picandvoice_affirm);
					iscover = true;
					currentCoverIndex = currentPageSize;
					if (currentPageSize <= ReleaseActivity.voices.size()) {
						ReleaseActivity.cover = (String) ReleaseActivity.voices
								.get(currentPageSize - 1).get("fileName");
					} else {
						ReleaseActivity.cover = (String) ReleaseActivity.images
								.get(currentPageSize
										- ReleaseActivity.voices.size() - 1)
								.get("fileName");
					}
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
			setResult(Activity.RESULT_OK, intent);
		}
		finish();
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
	public boolean isViewFromObject(View arg0, Object arg1) {
		return (arg0 == arg1);
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		try {
			if (mListViews.get(arg1).getParent() == null)
				((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			else {
				((ViewGroup) mListViews.get(arg1).getParent())
						.removeView(mListViews.get(arg1));
				((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mListViews.get(arg1);
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView(mListViews.get(arg1));
	}
}
