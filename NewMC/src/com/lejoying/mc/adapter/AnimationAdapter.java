package com.lejoying.mc.adapter;

import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public abstract class AnimationAdapter implements AnimationListener {

	@Override
	public abstract void onAnimationEnd(Animation animation);

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}

}
