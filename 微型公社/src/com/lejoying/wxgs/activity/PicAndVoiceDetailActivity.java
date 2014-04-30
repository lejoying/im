package com.lejoying.wxgs.activity;


import java.util.ArrayList;
import java.util.List;

import com.lejoying.wxgs.R;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;

public class PicAndVoiceDetailActivity extends Activity implements
		OnClickListener {

	TextView tv_setcover, tv_number;
	ImageView PicAndVoiceDetailBack, iv_picandvoice_del, iv_picandvoice_cancel;
	ViewPager picandvoice_Pager;
	LayoutInflater mInflater;
	
	List<View> mainListViews;
	boolean iscover = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picandvoicedetail);
		mInflater = getLayoutInflater();
		getWindow().setBackgroundDrawableResource(R.drawable.square_background);
		initLayout();
		initData();
	}

	public void onBackPressed() {
		mFinish();
	}

	void initLayout() {
		tv_setcover = (TextView) findViewById(R.id.tv_setcover);
		tv_number = (TextView) findViewById(R.id.tv_number);
		PicAndVoiceDetailBack = (ImageView) findViewById(R.id.PicAndVoiceDetailBack);
		iv_picandvoice_del = (ImageView) findViewById(R.id.iv_picandvoice_del);
		iv_picandvoice_cancel = (ImageView) findViewById(R.id.iv_picandvoice_cancel);
		picandvoice_Pager = (ViewPager) findViewById(R.id.picandvoice_Pager);

		tv_setcover.setOnClickListener(this);
		PicAndVoiceDetailBack.setOnClickListener(this);
		iv_picandvoice_del.setOnClickListener(this);
		iv_picandvoice_cancel.setOnClickListener(this);
	}

	void initData() {
		mainListViews=new ArrayList<View>();
		for (int i = 0; i < ReleaseActivity.voice.size(); i++) {
			View addView = mInflater.inflate(R.layout.release_child_navigation,
					null);
			
			mainListViews.add(addView);
		}
		for (int i = 0; i < ReleaseActivity.image.size(); i++) {
			View addView = mInflater.inflate(R.layout.release_child_navigation,
					null);
			ImageView iv = (ImageView) addView
					.findViewById(R.id.iv_release_child);
			iv.setImageBitmap((Bitmap) ReleaseActivity.image.get(i).get(
					"bitmap"));
			mainListViews.add(addView);
		}
		picandvoice_Pager.setAdapter(new myPageAdapter(mainListViews));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.PicAndVoiceDetailBack:
			mFinish();
			break;
		case R.id.tv_setcover:
		case R.id.iv_picandvoice_cancel:
			if (iscover) {
				iv_picandvoice_cancel
						.setImageResource(R.drawable.picandvoice_cancel);
				iscover = false;
			} else {
				iv_picandvoice_cancel
						.setImageResource(R.drawable.picandvoice_affirm);
				iscover = true;
			}
			break;
		default:
			break;
		}

	}

	public void mFinish() {
		Intent intent = new Intent();
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

}
class myPageAdapter extends PagerAdapter {
	List<View> mListViews;

	public myPageAdapter(List<View> mListViews) {
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
