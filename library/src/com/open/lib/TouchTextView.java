package com.open.lib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

public class TouchTextView extends TextView {

	public TouchTextView(Context context) {
		super(context);
	}

	public TouchTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TouchTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		return false;
	}

}