package com.open.welinks;

import com.open.lib.MyLog;
import com.open.welinks.model.Data;

import android.app.Activity;
import android.os.Bundle;

public class ShareListActivity extends Activity {
	public Data data = Data.getInstance();
	public String tag = "ShareListActivity";
	public MyLog log = new MyLog(tag, true);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
}
