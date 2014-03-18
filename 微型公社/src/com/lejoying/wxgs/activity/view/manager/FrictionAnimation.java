package com.lejoying.wxgs.activity.view.manager;

import android.view.animation.Animation;
import android.view.animation.Transformation;

public class FrictionAnimation extends Animation {
	float factor_y = 1.0f;
	float vx = 0;
	float vy = 0;

	long lastMillis = 0;
	long currentMillis = 0;

	boolean isRunning = false;

	public FrictionAnimation(float vx, float vy) {
		this.vx = vx;
		this.vy = vy;
		isRunning = true;
	}

	int Y = 0;

	int stepCount = 50;

	void step() {
		if (lastMillis != 0) {
			long deltaMillis = currentMillis - lastMillis;
			// stepCount--;
			// System.out.println("step:    " + stepCount);

			Y += vy * deltaMillis * factor_y;

			vy *= (1.0f - 0.003f * deltaMillis);

			if (Math.abs(vy) < 0.001f) {
				vy = 0.0f;
			}
			if (vy == 0.0f) {
				isRunning = false;
			}

			// if (stepCount < 0) {
			// isRunning = false;
			// }
		}
	}

	@Override
	public boolean getTransformation(long currentTime, Transformation outTransformation, float scale) {
		currentMillis = currentTime;
		step();
		outTransformation.getMatrix().setTranslate(0, Y);
		lastMillis = currentMillis;
		return isRunning;
	}

}
