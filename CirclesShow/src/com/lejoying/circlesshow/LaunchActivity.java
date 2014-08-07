package com.lejoying.circlesshow;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lejoying.utils.CommonNetConnection;
import com.lejoying.utils.NetworkHandler;
import com.lejoying.utils.NetworkHandler.Settings;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship;

public class LaunchActivity extends Activity implements OnClickListener {

	String TAG = "LaunchActivity";

	NetworkHandler mNetworkHandler;
	Handler handler = new Handler();
	String url = "http://www.we-links.com/api2/relation/intimatefriends";

	public static Data data = Data.getInstance();
	Gson gson = new Gson();

	EditText showPhoneView;
	RelativeLayout getDataView;
	RelativeLayout showUIView;

	boolean isInit = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		mNetworkHandler = new NetworkHandler(5);
		initViews();
		initEvent();
	}

	private void initEvent() {
		getDataView.setOnClickListener(this);
		showUIView.setOnClickListener(this);
	}

	private void initViews() {
		showPhoneView = (EditText) findViewById(R.id.et_showPhone);
		getDataView = (RelativeLayout) findViewById(R.id.rl_getData);
		showUIView = (RelativeLayout) findViewById(R.id.rl_showUI);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.rl_getData) {
			String showPhone = showPhoneView.getText().toString().trim();
			if ("".equals(showPhone)) {
				Toast.makeText(LaunchActivity.this, "帐号不能为空",
						Toast.LENGTH_SHORT).show();
				return;
			}
			getData(showPhone);
		} else if (id == R.id.rl_showUI) {

			if (!isInit) {
				Toast.makeText(LaunchActivity.this, "请获取数据后点击",
						Toast.LENGTH_SHORT).show();
				return;
			}
			Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
			startActivity(intent);
		}
	}

	void getData(final String phone) {
		mNetworkHandler.connection(new CommonNetConnection() {

			@Override
			public void success(JSONObject jData) {
				try {
					data.relationship = gson.fromJson(jData.getString("data"),
							Relationship.class);
					isInit = true;
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(LaunchActivity.this, "获取数据成功",
									Toast.LENGTH_SHORT).show();
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			protected void settings(Settings settings) {
				settings.url = url;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", phone);
				params.put("accessKey", "lejoying");
				settings.params = params;
			}
		});
	}
}
