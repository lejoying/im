package com.lejoying.mc.view;

import android.widget.BaseAdapter;

public abstract class ScrollContentAdapter extends BaseAdapter {

	ScrollContent mScrollContent;

	public ScrollContentAdapter(ScrollContent scrollContent) {
		// TODO Auto-generated constructor stub
		this.mScrollContent = scrollContent;
	}

	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		mScrollContent.setAdapter(this);
		super.notifyDataSetChanged();
	}
}
