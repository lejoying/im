package com.open.welinks.customView;

import com.open.welinks.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PageControlView extends LinearLayout {
	private Context context;
	private PageControlView thisView;
	private Bitmap defaultImage, seleteImage;

	// private LinearLayout.LayoutParams params;

	public PageControlView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		this.thisView = this;
		thisView.setOrientation(LinearLayout.HORIZONTAL);
		defaultImage = BitmapFactory.decodeResource(getResources(), R.drawable.dot_off);
		seleteImage = BitmapFactory.decodeResource(getResources(), R.drawable.dot_on);
		// params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	public void setCount(int count, int defaultItem) {
		thisView.removeAllViews();
		for (int i = 0; i < count; i++) {
			ImageView view = new ImageView(context);
			view.setPadding(5, 5, 5, 5);
			if (i == defaultItem - 1) {
				view.setImageBitmap(seleteImage);
			} else {
				view.setImageBitmap(defaultImage);
			}
			thisView.addView(view);
		}
	}

	public void setSeleteItem(int position) {
		for (int i = 0; i < thisView.getChildCount(); i++) {
			if (i == (position)) {
				((ImageView) thisView.getChildAt(i)).setImageBitmap(seleteImage);
			} else {
				((ImageView) thisView.getChildAt(i)).setImageBitmap(defaultImage);
			}
		}
	}

	public void setImage(int defaultImageId, int seleteImageId) {
		defaultImage = BitmapFactory.decodeResource(getResources(), defaultImageId);
		seleteImage = BitmapFactory.decodeResource(getResources(), seleteImageId);
	}
}
