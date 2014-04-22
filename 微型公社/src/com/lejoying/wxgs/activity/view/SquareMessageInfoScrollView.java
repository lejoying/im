package com.lejoying.wxgs.activity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class SquareMessageInfoScrollView extends ScrollView {

	private SizeChangedListener sizeChangedListener;

	public SquareMessageInfoScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (sizeChangedListener != null) {
			sizeChangedListener.sizeChanged(w, h, oldw, oldh);
		}
	}

	public void setSizeChangedListener(SizeChangedListener sizeChangedListener) {
		this.sizeChangedListener = sizeChangedListener;
	}

	public interface SizeChangedListener {
		public void sizeChanged(int w, int h, int oldw, int oldh);
	}

}
