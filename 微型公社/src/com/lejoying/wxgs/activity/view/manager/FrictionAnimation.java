package com.lejoying.wxgs.activity.view.manager;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class FrictionAnimation extends Animation {
	float factor_y = 1.0f;

	long lastMillis = 0;
	long currentMillis = 0;

	boolean isRunning = false;

	public static class AnimatingView {
		public View view;
		public float vx = 0;
		public float vy = 0;

	}

	public Map<String, AnimatingView> animatingViews = new Hashtable<String, AnimatingView>();

	public FrictionAnimation() {
		isRunning = true;
	}

	public void addView(String key, AnimatingView animatingView) {
		isRunning = true;
		animatingViews.put(key, animatingView);
	}

	int Y = 0;

	int stepCount = 50;

	void step() {
		if (lastMillis != 0) {
			long deltaMillis = currentMillis - lastMillis;
			TransformViews(deltaMillis);
		}
	}

	void TransformViews(long deltaMillis) {
		Set<String> keys = animatingViews.keySet();
		isRunning = false;
		for (String key : keys) {
			isRunning = true;
			AnimatingView animatingView = animatingViews.get(key);

			float dy = animatingView.vy * deltaMillis * factor_y;
			animatingView.view.scrollBy(0, -(int) (dy));

			animatingView.vy = animatingView.vy * (1.0f - 0.003f * deltaMillis);

			if (Math.abs(animatingView.vy) < 0.01f) {
				animatingView.vy = 0.0f;
			}
			if (animatingView.vy == 0.0f) {
				animatingViews.remove(key);
			}
		}
	}

	@Override
	public boolean getTransformation(long currentTime, Transformation outTransformation, float scale) {
		currentMillis = currentTime;
		step();
		// outTransformation.getMatrix().setTranslate(0, Y);
		lastMillis = currentMillis;
		return isRunning;
	}

}
