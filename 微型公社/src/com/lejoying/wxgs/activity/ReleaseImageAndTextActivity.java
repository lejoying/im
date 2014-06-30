package com.lejoying.wxgs.activity;


import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.MainModeManager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

public class ReleaseImageAndTextActivity extends Activity implements
		OnClickListener {
	MainModeManager mMainModeManager;
	LayoutInflater mInflater;
	GridView gridView;

	int height, width, dip;
	float density;
	View sl_content, bottom_bar, rl_back, rl_send, rl_sync;
	EditText release_et;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.release_imageandtext);
		mInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		dip = (int) (40 * density + 0.5f);
		height = dm.heightPixels;
		width = dm.widthPixels;
		initLayout();
		initData();
		initEvent();
	}

	void initData() {
		MyGridViewAdapter mAdapter = new MyGridViewAdapter();
		gridView.setAdapter(mAdapter);
	}

	void initLayout() {
		rl_back = findViewById(R.id.rl_back);
		rl_send = findViewById(R.id.rl_send);
		rl_sync = findViewById(R.id.rl_sync);
		release_et = (EditText) findViewById(R.id.release_et);
		gridView = (GridView) findViewById(R.id.release_gv);
		sl_content = findViewById(R.id.sl_content);
		// LayoutParams params = sl_content.getLayoutParams();
		// params.height = height - MainActivity.statusBarHeight
		// - (int) (157 * density + 0.5f);
		// sl_content.setLayoutParams(params);
	}

	void initEvent() {
		rl_back.setOnClickListener(this);
		rl_send.setOnClickListener(this);
		rl_sync.setOnClickListener(this);
	}

	void Send() {

	}

	void Sync() {

	}

	public class MyGridViewAdapter extends BaseAdapter {

		public MyGridViewAdapter() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 1;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final GridViewHolder gridViewHolder;
			if (convertView == null) {
				gridViewHolder = new GridViewHolder();
				convertView = mInflater.inflate(R.layout.view_child, null);
				gridViewHolder.iv_child = (ImageView) convertView
						.findViewById(R.id.iv_child);
				LayoutParams childParams = gridViewHolder.iv_child
						.getLayoutParams();
				childParams.height = (int) (height * 0.165625f);
				childParams.width = (int) (width * 0.29444444f);
				gridViewHolder.iv_child.setLayoutParams(childParams);
				convertView.setTag(gridViewHolder);
			} else {
				gridViewHolder = (GridViewHolder) convertView.getTag();
			}
			if (false) {

			} else {
				gridViewHolder.iv_child
						.setImageResource(R.drawable.release_imgandtext_add);
				gridViewHolder.iv_child
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								mMainModeManager
										.showNext(mMainModeManager.mReleaseSelectImageFragment);

							}
						});
			}
			return convertView;
		}

		class GridViewHolder {
			ImageView iv_child;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_back:
			finish();
			break;
		case R.id.rl_send:
			Send();
			break;
		case R.id.rl_sync:
			Sync();
			break;

		default:
			break;
		}
	}

}
