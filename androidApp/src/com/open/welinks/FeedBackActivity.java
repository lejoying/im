package com.open.welinks;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class FeedBackActivity extends Activity implements OnClickListener, TextWatcher {

	public View backView;
	public ImageView send;
	public TextView titleContentView;
	public RelativeLayout rightContainer;

	public TextView num;
	public EditText opinion;

	public String content = "";
	public InputMethodManager inputMethodManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);

		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		backView = findViewById(R.id.backView);
		titleContentView = (TextView) findViewById(R.id.titleContent);
		titleContentView.setText("意见反馈");
		rightContainer = (RelativeLayout) findViewById(R.id.rightContainer);
		send = new ImageView(this);
		send.setImageResource(R.drawable.mark_stone);
		send.setPadding((int) (20 * dm.density), (int) (13 * dm.density), (int) (20 * dm.density), (int) (13 * dm.density));
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
		rightContainer.addView(send, layoutParams);

		num = (TextView) findViewById(R.id.num);
		opinion = (EditText) findViewById(R.id.opinion);

		inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

		backView.setOnClickListener(this);
		send.setOnClickListener(this);
		opinion.addTextChangedListener(this);

		inputMethodManager.showSoftInput(opinion, InputMethodManager.SHOW_FORCED);
	}

	@Override
	public void onClick(View view) {
		if (view.equals(send)) {

		} else if (view.equals(backView)) {
			finish();
		}

	}

	@Override
	public void afterTextChanged(Editable s) {
		num.setText(String.valueOf(300 - s.toString().length()));
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int before, int count) {
		content = s.toString();
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int count, int after) {

	}
}
