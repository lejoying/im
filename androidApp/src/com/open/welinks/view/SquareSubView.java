package com.open.welinks.view;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.open.lib.MyLog;
import com.open.welinks.R;
import com.open.welinks.controller.SquareSubController;
import com.open.welinks.model.Data;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.model.Parser;
import com.open.welinks.utils.BaseDataUtils;
import com.open.welinks.oss.DownloadFileList;

public class SquareSubView {

	public Data data = Data.getInstance();

	public String tag = "SquareSubView";

	public SquareSubController thisController;

	public MyLog log = new MyLog(tag, true);

	public FileHandlers fileHandlers = FileHandlers.getInstance();

	public DisplayMetrics displayMetrics;

	public MainView mainView;

	// share
	public RelativeLayout squareView;
	public ViewGroup squareMessageView;

	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DownloadFileList downloadFileList = DownloadFileList.getInstance();
	public Gson gson = new Gson();
	public Parser parser = Parser.getInstance();

	public ViewManage viewManage = ViewManage.getInstance();

	public LayoutInflater mInflater;

	public Button button1, button2;

	public SquareSubView(MainView mainView) {
		this.mainView = mainView;
		viewManage.squareSubView = this;
	}

	TextView titleName;

	public TextView roomTextView;

	public float textSize;
	public ImageView botton;

	public void initViews() {
		this.squareView = mainView.squareView;
		this.displayMetrics = mainView.displayMetrics;
		this.mInflater = mainView.mInflater;

		squareMessageView = (ViewGroup) squareView.findViewById(R.id.squareContainer);

		button1 = new Button(mainView.thisActivity);
		button2 = new Button(mainView.thisActivity);
		button1.setText("分类推荐");
		button2.setText("附近分享");
		RelativeLayout.LayoutParams button1Params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		button1Params.topMargin = 20;
		RelativeLayout.LayoutParams button2Params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		button2Params.topMargin = 20;
		button2Params.leftMargin = BaseDataUtils.dpToPxint(120);
		squareMessageView.addView(button1, button1Params);
		squareMessageView.addView(button2, button2Params);

		thisController.bindEvent();
	}

}
