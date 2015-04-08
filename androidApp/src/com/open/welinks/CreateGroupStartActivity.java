package com.open.welinks;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.MyLog;
import com.open.welinks.R;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.view.ViewManage;

public class CreateGroupStartActivity extends Activity {

	public String tag = "CreateGroupStartActivity";
	public MyLog log = new MyLog(tag, false);

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();

	public ViewManage viewManage = ViewManage.getInstance();

	public RelativeLayout backView;
	public TextView backTitileView;

	public EditText groupNameView;
	public TextView groupPositionView;

	public TextView okButtonView;

	public ImageView groupNameWarnView;
	public ImageView groupPositionWarnView;

	public OnClickListener mOnClickListener;
	public TextWatcher mTextWatcher;

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
			if (!address.equals("")) {
				if (groupPositionWarnView.getVisibility() == View.VISIBLE) {
					groupPositionWarnView.setVisibility(View.GONE);
				}
			}
		}
	}

	private void initView() {
		setContentView(R.layout.activity_creategroupstart);
		this.backView = (RelativeLayout) findViewById(R.id.backView);
		this.backTitileView = (TextView) findViewById(R.id.backTitleView);
		this.backTitileView.setText("创建群组");

		this.groupNameView = (EditText) findViewById(R.id.groupName);
		this.groupPositionView = (TextView) findViewById(R.id.groupPosition);

		this.okButtonView = (TextView) findViewById(R.id.okButton);

		this.groupNameWarnView = (ImageView) findViewById(R.id.groupNameWarn);
		this.groupPositionWarnView = (ImageView) findViewById(R.id.groupPositionWarn);
	}

	private void initializeListeners() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(backView)) {
					finish();
				} else if (view.equals(okButtonView)) {
					String groupName = groupNameView.getText().toString().trim();
					if (groupName.equals("")) {
						groupNameWarnView.setVisibility(View.VISIBLE);
						return;
					}
					if (address.equals("") || longitude.equals("") || latitude.equals("")) {
						groupPositionWarnView.setVisibility(View.VISIBLE);
						return;
					}
					createGroup();
					Intent intent = new Intent(CreateGroupStartActivity.this, CreateGroupOkActivity.class);
					startActivity(intent);
					finish();
				} else if (view.equals(groupPositionView)) {
					Intent intent = new Intent(CreateGroupStartActivity.this, CreateGroupLocationActivity.class);
					intent.putExtra("latitude", latitude);
					intent.putExtra("longitude", longitude);
					intent.putExtra("address", address);
					startActivityForResult(intent, R.id.tag_first);
				}
			}
		};
		mTextWatcher = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String content = s.toString().trim();
				if (!content.equals("")) {
					if (groupNameWarnView.getVisibility() == View.VISIBLE) {
						groupNameWarnView.setVisibility(View.GONE);
					}
				}
			}
		};
	}

	private void bindEvent() {
		this.groupNameView.addTextChangedListener(mTextWatcher);
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

		User user = data.userInformation.currentUser;
		params.addBodyParameter("address", address);
		params.addBodyParameter("phone", user.phone);
		params.addBodyParameter("accessKey", user.accessKey);
		params.addBodyParameter("type", "createGroup");
		params.addBodyParameter("gtype", "group");
		params.addBodyParameter("members", "[\"" + user.phone + "\"]");
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
		group.members.add(user.phone);
		group.relation = "join";
		data.relationship.groups.add(key);
		data.relationship.groupsMap.put(key, group);
		data.relationship.isModified = true;

		viewManage.mainView1.shareSubView.setGroupsDialogContent(null);

		String currentGroupCircle = data.localStatus.localData.currentGroupCircle;
		if (!"".equals(currentGroupCircle) && data.relationship.groupCirclesMap.get(currentGroupCircle) != null) {
			data.relationship.groupCirclesMap.get(currentGroupCircle).groups.add(key);
		} else {
			data.relationship.groupCirclesMap.get(data.localStatus.localData.currentGroupCircle).groups.add(key);
		}

		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
		httpUtils.send(HttpMethod.POST, API.GROUP_CREATE, params, responseHandlers.group_create);
	}
}
