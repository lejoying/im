package com.lejoying.wxgs.activity.view.manager;

import java.util.HashMap;
import java.util.Map;

import android.view.View;
import android.widget.BaseAdapter;

public class ViewManager {

	BaseAdapter adapter;

	Map<String, View> views = new HashMap<String, View>();

	public void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;
		
	}

}
