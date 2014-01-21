package com.lejoying.mc.fragment;

import com.lejoying.mc.R;
import com.lejoying.mc.data.App;
import com.lejoying.mc.utils.MCImageUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;

public class ShareFragment extends BaseListFragment {

	App app = App.getInstance();
	
	private View mContent;
	private LayoutInflater mInflater;

	private Bitmap headman;
	private Bitmap headwoman;

	@Override
	protected EditText showSoftInputOnShow() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onResume() {
		app.mark = app.shareFragment;
		super.onResume();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContent = inflater.inflate(R.layout.f_share, null);
		mInflater = inflater;
		mMCFragmentManager.showCircleMenuToTop(false, false);
		mMCFragmentManager.setCircleMenuPageName("分享");
		headman = MCImageUtils.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.face_man), true, 10, Color.WHITE);

		headwoman = MCImageUtils.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.face_woman), true, 10, Color.WHITE);

		return mContent;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		setListAdapter(new ShareAdapter());
	}

	class ShareAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 10;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			arg1 = mInflater.inflate(R.layout.f_share_item, null);
			final ImageView iv = (ImageView) arg1.findViewById(R.id.imageView1);
			ImageView iv_fhead = (ImageView) arg1.findViewById(R.id.iv_fhead);
			ImageView iv_fhead1 = (ImageView) arg1.findViewById(R.id.iv_head);
			iv_fhead.setImageBitmap(headman);
			iv_fhead1.setImageBitmap(headman);
			int imgid = R.drawable.p1;
			if (arg0 % 2 == 0) {
				imgid = R.drawable.p2;
				iv_fhead.setImageBitmap(headwoman);
				iv_fhead1.setImageBitmap(headwoman);
			} else if (arg0 % 3 == 0) {
				imgid = R.drawable.p3;
			}
			iv.setImageResource(imgid);
			iv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					System.out.println(iv.getWidth() + ":" + iv.getHeight());
				}
			});

			return arg1;
		}
	}

}
