package com.open.welinks;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.MyLog;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseHandlers;

public class AddFriendActivity extends Activity implements OnClickListener {

	public String tag = "AddFriendActivity";
	public MyLog log = new MyLog(tag, true);

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();

	public View backview;
	public EditText message;
	public Button send;

	public String phoneto;

	public String addFriendMessage = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_friend);
		phoneto = getIntent().getStringExtra("key");
		parser.check();
		if (data.localStatus.localData != null) {
			if (data.localStatus.localData.addFriendMessage != null) {
				this.addFriendMessage = data.localStatus.localData.addFriendMessage;
			}
		}

		initView();
	}

	private void initView() {
		backview = findViewById(R.id.backview);
		message = (EditText) findViewById(R.id.message);
		send = (Button) findViewById(R.id.send);

		message.setText(this.addFriendMessage);

		backview.setOnClickListener(this);
		send.setOnClickListener(this);

	}

	@Override
	public void onClick(View view) {
		if (view.equals(backview)) {
			finish();
		} else if (view.equals(send)) {
			String addMessage = message.getText().toString();
			// if (!"".equals(addMessage)) {
			addFriend(addMessage);
			setResult(Activity.RESULT_OK);
			finish();
			// } else {
			// Alert.showMessage("请输入验证信息");
			// }
		}

	}

	public void addFriend(String addMessage) {
		parser.check();
		if (data.localStatus.localData != null) {
			data.localStatus.localData.addFriendMessage = addMessage;
			data.localStatus.localData.isModified = true;
		}
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		params.addBodyParameter("target", phoneto);
		params.addBodyParameter("message", addMessage);
		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
		httpUtils.send(HttpMethod.POST, API.RELATION_FOLLOW, params, responseHandlers.relation_addfriend);
	}
}
