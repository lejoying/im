package com.open.welinks.customView;

import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.open.welink.R;

public class ControlProgress {

	public View controlProgressView;

	public ImageView progress_line1;

	public ImageView progress_line2;
	public TranslateAnimation move_progress_line1;

	public float percentage = 0;
	public int width = 0;

	public void initialize(View container, DisplayMetrics displayMetrics) {
		move_progress_line1 = new TranslateAnimation(103, 0, 0, 0);

		progress_line1 = (ImageView) container.findViewById(R.id.progress_line1);
		progress_line2 = (ImageView) container.findViewById(R.id.progress_line2);
		controlProgressView = container;

		width = displayMetrics.widthPixels;
	}

	public void moveTo(float targetPercentage) {
		float position = targetPercentage / 100.0f * this.width;
		move_progress_line1 = new TranslateAnimation((percentage - targetPercentage) / 100.0f * width, 0, 0, 0);
		// TODO old animation becomes memory fragment
		move_progress_line1.setStartOffset(0);
		move_progress_line1.setDuration(200);

		progress_line1.startAnimation(move_progress_line1);

		progress_line1.setX(position);
		percentage = targetPercentage;
	}

	public void setTo(int targetPercentage) {
		float position = targetPercentage / 100.0f * this.width;
		progress_line1.setX(position);
		percentage = targetPercentage;
	}
}
