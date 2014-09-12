package com.open.welinks;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.MyLog;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.view.ViewManage;

public class CreateGroupStartActivity extends Activity {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();
	public String tag = "CreateGroupStartActivity";
	public MyLog log = new MyLog(tag, false);

	public ViewManage viewManage = ViewManage.getInstance();

	public RelativeLayout backView;
	public TextView backTitileView;
	public TextView titleView;

	public EditText groupNameView;
	public TextView groupPositionView;

	public TextView okButtonView;

	public OnClickListener mOnClickListener;

	public String address = "", latitude = "", longitude = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initializeListeners();
		bindEvent();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == R.id.tag_first && resultCode == Activity.RESULT_OK && data != null) {
			longitude = data.getStringExtra("longitude");
			latitude = data.getStringExtra("latitude");
			address = data.getStringExtra("address");
			groupPositionView.setText(address);
		}
	}

	private void initView() {
		setContentView(R.layout.activity_creategroupstart);
		this.backView = (RelativeLayout) findViewById(R.id.backView);
		this.backTitileView = (TextView) findViewById(R.id.backTitleView);
		this.backTitileView.setText("创建群组");
		this.titleView = (TextView) findViewById(R.id.titleContent);

		this.groupNameView = (EditText) findViewById(R.id.groupName);
		this.groupPositionView = (TextView) findViewById(R.id.groupPosition);

		this.okButtonView = (TextView) findViewById(R.id.okButton);
	}

	private void initializeListeners() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(backView)) {
					finish();
				} else if (view.equals(okButtonView)) {
					createGroup();
					Intent intent = new Intent(CreateGroupStartActivity.this, CreateGroupOkActivity.class);
					startActivity(intent);
				} else if (view.equals(groupPositionView)) {
					Intent intent = new Intent(CreateGroupStartActivity.this, CreateGroupLocationActivity.class);
					intent.putExtra("latitude", latitude);
					intent.putExtra("longitude", longitude);
					intent.putExtra("address", address);
					startActivityForResult(intent, R.id.tag_first);
				}
			}
		};
	}

	private void bindEvent() {
		this.backView.setOnClickListener(mOnClickListener);
		this.okButtonView.setOnClickListener(mOnClickListener);
		this.groupPositionView.setOnClickListener(mOnClickListener);
	}

	public void createGroup() {
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		data = parser.check();
		String name = groupNameView.getText().toString();
		String key = String.valueOf(new Date().getTime() % 100000);

		params.addBodyParameter("address", address);
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		params.addBodyParameter("type", "createGroup");
		params.addBodyParameter("gtype", "group");
		params.addBodyParameter("members", "[\"" + data.userInformation.currentUser.phone + "\"]");
		params.addBodyParameter("tempGid", key);
		params.addBodyParameter("name", groupNameView.getText().toString());
		String location = "{\"longitude\":\"" + longitude + "\",\"latitude\":\"" + latitude + "\"}";
		params.addBodyParameter("location", location);

		Group group = data.relationship.new Group();
		group.name = name;
		group.gid = Integer.valueOf(key);
		group.latitude = latitude;
		group.longitude = longitude;
		group.description = "";
		data.relationship.groups.add(key);
		data.relationship.groupsMap.put(key, group);
		data.relationship.isModified = true;

		viewManage.mainView.shareSubView.setGroupsDialogContent();

		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
		httpUtils.send(HttpMethod.POST, API.GROUP_CREATE, params, responseHandlers.group_create);
	}
}
