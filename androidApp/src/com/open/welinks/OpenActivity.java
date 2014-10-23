package com.open.welinks;

import com.open.lib.MyLog;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class OpenActivity extends Activity {

	public String tag = "OpenActivity";
	public MyLog log = new MyLog(tag, true);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		if (intent != null) {
			Uri uri = intent.getData();
			String phone = uri.getQueryParameter("phone");
			String option = uri.getQueryParameter("option");
			log.e(phone + "---" + option);
			finish();
			Intent goIntent = new Intent(OpenActivity.this, LaunchActivity.class);
			goIntent.putExtra("phone", phone);
			goIntent.putExtra("option", option);
			startActivity(goIntent);
		}
	}
}
