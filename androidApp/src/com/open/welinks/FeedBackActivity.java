package com.open.welinks;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class FeedBackActivity extends Activity implements OnClickListener, TextWatcher {
	public View backView, send;
	public TextView num;
	public EditText opinion;

	public String content = "";
	public InputMethodManager inputMethodManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		backView = findViewById(R.id.backView);
		send = findViewById(R.id.send);
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
