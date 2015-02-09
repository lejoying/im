package com.open.welinks;

import java.util.ArrayList;
import java.util.List;

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

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.MyLog;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.ResponseHandlers;

public class FeedBackActivity extends Activity implements OnClickListener, TextWatcher {
	public Data data = Data.getInstance();
	public String tag = "FeedBackActivity";
	public MyLog log = new MyLog(tag, true);

	public View backView;
	public ImageView sendButton;
	public TextView titleContentView;
	public RelativeLayout rightContainer;

	public TextView textNumberView;
	public EditText opinionView;

	public String content = "";
	public InputMethodManager inputMethodManager;

	public Gson gson = new Gson();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);

		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		backView = findViewById(R.id.backView);
		titleContentView = (TextView) findViewById(R.id.backTitleView);
		titleContentView.setText("意见反馈");
		rightContainer = (RelativeLayout) findViewById(R.id.rightContainer);
		sendButton = new ImageView(this);
		sendButton.setImageResource(R.drawable.mark_stone);
		sendButton.setPadding((int) (10 * dm.density), (int) (5 * dm.density), (int) (10 * dm.density), (int) (5 * dm.density));
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);

		rightContainer.addView(sendButton, layoutParams);
		RelativeLayout.LayoutParams rightContainerParams = (LayoutParams) rightContainer.getLayoutParams();
		rightContainerParams.rightMargin = 0;

		textNumberView = (TextView) findViewById(R.id.num);
		opinionView = (EditText) findViewById(R.id.opinion);

		inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

		backView.setOnClickListener(this);
		sendButton.setOnClickListener(this);
		opinionView.addTextChangedListener(this);

		inputMethodManager.showSoftInput(opinionView, InputMethodManager.SHOW_FORCED);
	}

	@Override
	public void onClick(View view) {
		if (view.equals(sendButton)) {
			String content = opinionView.getText().toString().trim();
			if ("".equals(content)) {
				return;
			}
			sendMessageToServer(content);
			finish();
		} else if (view.equals(backView)) {
			finish();
		}
	}

	public void sendMessageToServer(String content) {
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		User currentUser = data.userInformation.currentUser;
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("sendType", "point");
		params.addBodyParameter("contentType", "text");
		params.addBodyParameter("content", content);
		List<String> phoneto = new ArrayList<String>();
		phoneto.add("151");
		params.addBodyParameter("phoneto", gson.toJson(phoneto));
		params.addBodyParameter("gid", "");

		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
		httpUtils.send(HttpMethod.POST, API.MESSAGE_SEND, params, responseHandlers.message_sendMessageCallBack);
	}

	@Override
	public void afterTextChanged(Editable s) {
		textNumberView.setText(String.valueOf(300 - s.toString().length()));
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int before, int count) {
		content = s.toString();
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int count, int after) {
	}
}
