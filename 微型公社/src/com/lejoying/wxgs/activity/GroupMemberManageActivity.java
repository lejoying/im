package com.lejoying.wxgs.activity;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileResult;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GroupMemberManageActivity extends Activity implements
		OnClickListener {

	MainApplication app = MainApplication.getMainApplication();

	String[] backGrounds = new String[] { "bg1.png", "bg1.png", "bg1.png",
			"bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png",
			"bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png",
			"bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png",
			"bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png",
			"bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png",
			"bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png",
			"bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png",
			"bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png",
			"bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png",
			"bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png",
			"bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png",
			"bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png",
			"bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png",
			"bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png",
			"bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png",
			"bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png",
			"bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png",
			"bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png",
			"bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png",
			"bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png",
			"bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png",
			"bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png",
			"bg1.png", "bg1.png", "bg1.png", "bg1.png", "bg1.png", };

	LayoutInflater mInflater;

	float height, width, dip;
	float density;

	LinearLayout backView;

	GridView groupMembersView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mInflater = this.getLayoutInflater();
		setContentView(R.layout.activity_groupmembersmanage);
		backView = (LinearLayout) findViewById(R.id.ll_groupmanagebackview);
		groupMembersView = (GridView) findViewById(R.id.gridView_groupmembers);
		initData();
		groupMembersView.setAdapter(new BackGroundAdapter());
		backView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_groupmanagebackview:
			finish();
			break;

		default:
			break;
		}
	}

	private void initData() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		dip = (int) (40 * density + 0.5f);
		height = dm.heightPixels;
		width = dm.widthPixels;
	}

	class BackGroundAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return backGrounds.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ImageHolder imageHolder = null;
			if (convertView == null) {
				imageHolder = new ImageHolder();
				convertView = mInflater
						.inflate(
								R.layout.activity_groupinfomation_selectbackground_item,
								null);
				imageHolder.imageContent = (ImageView) convertView
						.findViewById(R.id.iv_image);
				imageHolder.imageContentStatus = (ImageView) convertView
						.findViewById(R.id.iv_imageContentStatus);
				convertView.setTag(imageHolder);
			} else {
				imageHolder = (ImageHolder) convertView.getTag();
			}
			final ImageHolder imageHolder0 = imageHolder;
			app.fileHandler.getThumbnail(backGrounds[position], "",
					(int) dp2px(90), (int) dp2px(90), new FileResult() {

						@Override
						public void onResult(String where, Bitmap bitmap) {
							imageHolder0.imageContent.setImageBitmap(bitmap);
						}
					});
			// app.fileHandler.getSquareDetailImage(backGrounds[position],
			// dp2px(50), new FileResult() {
			//
			// @Override
			// public void onResult(String where, Bitmap bitmap) {
			// imageContent.setImageBitmap(bitmap);
			// }
			// });

			return convertView;
		}
	}

	class ImageHolder {
		ImageView imageContent;
		ImageView imageContentStatus;
	}

	public float dp2px(float px) {
		float dp = density * px + 0.5f;
		return dp;
	}
}
