package com.lejoying.wxgs.activity.mode.fragment;

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.MapStorageDirectoryActivity;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.app.MainApplication;

public class ReleaseSelectImageFragment extends BaseFragment implements
		OnClickListener {

	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;
	LayoutInflater mInflater;
	private View mContent;
	int height, width, dip;
	float density;
	File tempFile;

	LinearLayout ll_releaselocal, ll_releasecamera;

	int RESULT_TAKEPICTURE = 0x1;

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	@Override
	public void onResume() {
		mMainModeManager.handleMenu(false);
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mInflater = inflater;
		mContent = mInflater.inflate(R.layout.f_release_sel, null);
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		dip = (int) (40 * density + 0.5f);
		height = dm.heightPixels;
		width = dm.widthPixels;
		initLayout();
		initData();

		return mContent;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

	void initLayout() {
		ll_releaselocal = (LinearLayout) mContent
				.findViewById(R.id.ll_releaselocal);
		ll_releasecamera = (LinearLayout) mContent
				.findViewById(R.id.ll_releasecamera);
		ll_releaselocal.setOnClickListener(this);
		ll_releasecamera.setOnClickListener(this);
	}

	void initData() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_releaselocal:
			Intent selectFromGallery = new Intent(getActivity(),
					MapStorageDirectoryActivity.class);
			startActivity(selectFromGallery);
			break;
		case R.id.ll_releasecamera:
			tempFile = new File(app.sdcardImageFolder, "tempimage.jpg");
			int i = 1;
			while (tempFile.exists()) {
				tempFile = new File(app.sdcardImageFolder, "tempimage" + (i++)
						+ ".jpg");
			}
			Uri uri = Uri.fromFile(tempFile);
			Intent tackPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			tackPicture.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
			tackPicture.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			startActivityForResult(tackPicture, RESULT_TAKEPICTURE);
			break;

		default:
			break;
		}

	}

}
