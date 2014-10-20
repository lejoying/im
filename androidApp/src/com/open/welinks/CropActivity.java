package com.open.welinks;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.open.welinks.customView.ClipImageLayout;

public class CropActivity extends Activity {

	public ClipImageLayout mClipImageLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activty_crop);

		initView();

		String path = this.getIntent().getStringExtra("path");
		mClipImageLayout = (ClipImageLayout) findViewById(R.id.id_clipImageLayout);
		mClipImageLayout.setImage(path);
	}

	public RelativeLayout rightContainerView;
	public View backView;
	public TextView backTitleView;

	private void initView() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		backView = findViewById(R.id.backView);
		backTitleView = (TextView) findViewById(R.id.backTitleView);
		rightContainerView = (RelativeLayout) findViewById(R.id.rightContainer);

		int dp_5 = (int) (5 * displayMetrics.density);
		TextView mConfirm = new TextView(this);
		mConfirm.setGravity(Gravity.CENTER);
		mConfirm.setPadding(dp_5 * 2, dp_5, dp_5 * 2, dp_5);
		mConfirm.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		mConfirm.setText("完成");
		mConfirm.setBackgroundResource(R.drawable.textview_bg);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0, dp_5, (int) 0, dp_5);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		rightContainerView.addView(mConfirm, layoutParams);
		rightContainerView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Bitmap bitmap = mClipImageLayout.clip();

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				byte[] datas = baos.toByteArray();

				Intent intent = new Intent();
				intent.putExtra("bitmap", datas);
				setResult(Activity.RESULT_OK,intent);
				finish();
			}
		});
	}
}
