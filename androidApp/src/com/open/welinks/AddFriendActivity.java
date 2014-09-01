package com.open.welinks;

import android.os.Bundle;
import android.app.Activity;

public class AddFriendActivity extends Activity {
	public String phoneto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_friend);
		phoneto = getIntent().getStringExtra("key");
	}

}
