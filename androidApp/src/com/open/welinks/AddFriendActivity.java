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
import com.open.lib.ResponseHandler;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseHandlers;

public class AddFriendActivity extends Activity implements OnClickListener {

	public String tag = "AddFriendActivity";
	public MyLog log = new MyLog(tag, true);

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();

	public View backView;
	public EditText messageEditText;
	public Button sendButton;

	public String phoneTo;

	public String addFriendMessage = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_friend);
		phoneTo = getIntent().getStringExtra("key");
		parser.check();
		if (data.localStatus.localData != null) {
			if (data.localStatus.localData.addFriendMessage != null) {
				this.addFriendMessage = data.localStatus.localData.addFriendMessage;
			}
		}

		initView();
	}

	private void initView() {
		backView = findViewById(R.id.backview);
		messageEditText = (EditText) findViewById(R.id.message);
		sendButton = (Button) findViewById(R.id.send);

		messageEditText.setText(this.addFriendMessage);

		backView.setOnClickListener(this);
		sendButton.setOnClickListener(this);

	}

	@Override
	public void onClick(View view) {
		if (view.equals(backView)) {
			finish();
		} else if (view.equals(sendButton)) {
			String addMessage = messageEditText.getText().toString();
			addFriend(addMessage);
			setResult(Activity.RESULT_OK);
			finish();
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
		User currentUser = data.userInformation.currentUser;
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("target", phoneTo);
		params.addBodyParameter("message", addMessage);
		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
		httpUtils.send(HttpMethod.POST, API.RELATION_FOLLOW, params, responseHandlers.relation_addfriend);
	}
}
