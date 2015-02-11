package com.open.welinks.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class MyHorizontalScrollView extends HorizontalScrollView {

	private ScrollEndListener scrollEndListener;
	private boolean isEnd = false;

	public MyHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {

		if (getChildAt(0).getMeasuredWidth() <= getScrollX() + getWidth()) {
			if (!isEnd) {
				isEnd = true;
				if (scrollEndListener != null)
					scrollEndListener.scrollEnd();
			}
		} else {
			isEnd = false;
		}
		super.onScrollChanged(l, t, oldl, oldt);
	}

	public void setOnScrollEndListener(ScrollEndListener scrollEndListener) {
		this.scrollEndListener = scrollEndListener;
	}

	public interface ScrollEndListener {
		public void scrollEnd();
	}
}
