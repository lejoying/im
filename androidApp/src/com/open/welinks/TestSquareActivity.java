package com.open.welinks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.open.lib.TouchImageView;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.utils.MCImageUtils;

public class TestSquareActivity extends Activity {

	public FileHandlers fileHandlers = FileHandlers.getInstance();

	Bitmap defaultHeadBoy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		defaultHeadBoy = MCImageUtils.getCircleBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.face_man), true, 10, Color.WHITE);
		initData();
		initView();
		// setLayout();
	}

	private void setLayout() {
		// FrameLayout.LayoutParams maxViewpParams = (LayoutParams) maxView.getLayoutParams();
		// maxViewpParams.width = width;
		// maxViewpParams.gravity = Gravity.CENTER_HORIZONTAL;
	}

	public DisplayMetrics displayMetrics;

	int screenHeight;
	int screenWidth;
	public int width;

	public float imageHeightScale = 0.7586206896551724f;
	public int imageHeight;

	private void initData() {
		displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		screenHeight = displayMetrics.heightPixels;
		screenWidth = displayMetrics.widthPixels;
		width = (int) (screenWidth - (displayMetrics.density * 20 + 0.5f));
		imageHeight = (int) (width * imageHeightScale);
	}

	private void initView() {
		setContentView(R.layout.square_message_line);
		LinearLayout main = (LinearLayout) findViewById(R.id.main);
		LayoutInflater mInflater = this.getLayoutInflater();
		for (int i = 0; i < 10; i++) {
			View view = mInflater.inflate(R.layout.square_message_item, null);

			View maxView = view.findViewById(R.id.maxView);

			View mainContainer = view.findViewById(R.id.mainContainer);
			FrameLayout.LayoutParams params = (LayoutParams) mainContainer.getLayoutParams();
			params.topMargin = (int) (displayMetrics.density * 10 + 0.5f);
			params.bottomMargin = (int) (displayMetrics.density * 10 + 0.5f);

			final View buttonBar = view.findViewById(R.id.buttonBar);
			final TextView contentView = (TextView) view.findViewById(R.id.contentText);
			contentView.setBackgroundColor(Color.parseColor("#38000000"));
			contentView.setTextColor(Color.WHITE);
			ImageView contentImageView = (ImageView) view.findViewById(R.id.contentImage);

			FrameLayout.LayoutParams imageParams = (LayoutParams) contentImageView.getLayoutParams();
			imageParams.topMargin = (int) (displayMetrics.density * 30 + 0.5f);
			imageParams.height = imageHeight;

			ViewTreeObserver vto = contentView.getViewTreeObserver();
			vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				public void onGlobalLayout() {
					Log.e("aaa", contentView.getHeight() + "::::::::::::::::;;");
					FrameLayout.LayoutParams textParams = (LayoutParams) contentView.getLayoutParams();
					textParams.topMargin = (int) (displayMetrics.density * 30 + 0.5f + imageHeight - contentView.getHeight());
					FrameLayout.LayoutParams buttonbarpParams = (LayoutParams) buttonBar.getLayoutParams();
					buttonbarpParams.topMargin = (int) (displayMetrics.density * 30 + 0.5f ) + imageHeight;
					contentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					buttonBar.setVisibility(View.GONE);
				}
			});
			TouchImageView squareHeadView = (TouchImageView) view.findViewById(R.id.squareHead);
			squareHeadView.setImageBitmap(defaultHeadBoy);
			main.addView(view);
		}
	}
}
