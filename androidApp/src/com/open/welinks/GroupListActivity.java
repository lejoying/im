package com.open.welinks;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.open.lib.MyLog;
import com.open.welinks.controller.GroupListController;
import com.open.welinks.customView.Alert;
import com.open.welinks.customView.Alert.AlertInputDialog;
import com.open.welinks.customView.Alert.AlertInputDialog.OnDialogClickListener;
import com.open.welinks.customView.ThreeChoicesView;
import com.open.welinks.model.API;
import com.open.welinks.model.Constant;
import com.open.welinks.model.Data;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.Relationship.GroupCircle;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.model.Parser;
import com.open.welinks.utils.BaseDataUtils;
import com.open.welinks.view.GroupListView;
import com.open.welinks.view.ViewManage;

public class GroupListActivity extends Activity {

	public GroupListView thisView;
	public GroupListController thisController;

	public String tag = "GroupListActivity";
	public MyLog log = new MyLog(tag, true);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		thisView = new GroupListView(this);
		thisController = new GroupListController(this);
		thisView.thisController = thisController;
		thisController.thisView = thisView;

		thisController.onCreate();
		thisView.initViews();
		thisController.initializeListeners();
		thisController.initData();
	}

	@Override
	protected void onResume() {
		thisController.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		thisController.onDestroy();
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		thisController.onBackPressed();
	}

	@Override
	public void finish() {
		thisController.finish();
		super.finish();
	}

}
