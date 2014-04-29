package com.lejoying.wxgs.activity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class SquareMessageInfoScrollView extends ScrollView {

	private SizeChangedListener sizeChangedListener;
	private onScrollChanged onScrollChanged;
	boolean scrollEnable;

	public SquareMessageInfoScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		scrollEnable = true;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (sizeChangedListener != null) {
			sizeChangedListener.sizeChanged(l, t, oldl, oldt);
		}
		// requestDisallowInterceptTouchEvent(disallowIntercept);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (sizeChangedListener != null) {
			sizeChangedListener.sizeChanged(w, h, oldw, oldh);
		}
	}

	OnTouchListener mOnTouchListener;

	@Override
	public void setOnTouchListener(OnTouchListener l) {
		mOnTouchListener = l;
	}

	public void setScrollEnable(boolean enable) {
		this.scrollEnable = enable;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mOnTouchListener != null) {
			mOnTouchListener.onTouch(this, ev);
		}
		if (scrollEnable) {
			super.onTouchEvent(ev);
		}
		return true;
	}

	boolean isintercept;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		isintercept = true;
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			isintercept = false;
		}
		return super.onInterceptTouchEvent(ev) && isintercept;
	}

	public void setSizeChangedListener(SizeChangedListener sizeChangedListener) {
		this.sizeChangedListener = sizeChangedListener;
	}

	public interface SizeChangedListener {
		public void sizeChanged(int w, int h, int oldw, int oldh);
	}

	public void onScrollChanged(onScrollChanged onScrollChanged) {
		this.onScrollChanged = onScrollChanged;
	}

	public interface onScrollChanged {
		public void ScrollChanged(int l, int t, int oldl, int oldt);
	}

}
