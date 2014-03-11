package com.lejoying.wxgs.activity.page;

import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.BaseActivity;

public abstract class BasePage {

	public static final int ANIMATION_OPERATION_SHOW = 0xb;
	public static final int ANIMATION_OPERATION_HIDE = 0x15;
	public static final int ANIMATION_DIRECTION_TOP = 0xd;
	public static final int ANIMATION_DIRECTION_BOTTOM = 0x13;

	BaseActivity mBaseActivity;
	LayoutInflater mInflater;
	View mContentView;

	public abstract View initView(View content, LayoutInflater inflater);

	public abstract void initEvent(View content);

	public BasePage(BaseActivity baseActivity) {
		this.mBaseActivity = baseActivity;
		this.mInflater = baseActivity.getLayoutInflater();
		mContentView = initView(mContentView, mInflater);
		initEvent(mContentView);
	}

	public void show(int direction) {

	}

	public void show(int direction, AnimationListener listener) {

	}

	public void hide(int direction) {

	}

	public void hide(int direction, AnimationListener listener) {

	}

	public boolean excludeAnimation(int animOperation, int animDirection,
			AnimationListener listener) {
		Animation animation = null;

		switch (animOperation & animDirection) {
		case ANIMATION_OPERATION_SHOW & ANIMATION_DIRECTION_TOP:
			animation = AnimationUtils.loadAnimation(mBaseActivity,
					R.anim.translate_in_top);
			break;
		case ANIMATION_OPERATION_SHOW & ANIMATION_DIRECTION_BOTTOM:
			animation = AnimationUtils.loadAnimation(mBaseActivity,
					R.anim.translate_in_bottom);
			break;
		case ANIMATION_OPERATION_HIDE & ANIMATION_DIRECTION_TOP:
			animation = AnimationUtils.loadAnimation(mBaseActivity,
					R.anim.translate_out_top);
			break;
		case ANIMATION_OPERATION_HIDE & ANIMATION_DIRECTION_BOTTOM:
			animation = AnimationUtils.loadAnimation(mBaseActivity,
					R.anim.translate_out_bottom);
			break;

		default:
			break;
		}

		if (animation != null) {
			if (listener != null) {
				animation.setAnimationListener(listener);
			}
			mContentView.startAnimation(animation);
		}

		return animation == null;

	}

}
