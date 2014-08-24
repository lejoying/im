package com.open.lib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class TouchImageView extends ImageView {


	
    public TouchImageView(Context context) {
        super(context);
    }

    public TouchImageView(Context context, AttributeSet attrs) {
    	super(context, attrs);
    }

    public TouchImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    @Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		return false;
	}

}