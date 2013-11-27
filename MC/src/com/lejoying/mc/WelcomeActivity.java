package com.lejoying.mc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

public class WelcomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		
		// Intent service = new Intent(this, CircleMenuService.class);
		// startService(service);
		
		// new Thread() {
		// @Override
		// public void run() {
		// try {
		// Thread.sleep(1500);
		// Intent intent = new Intent(WelcomeActivity.this,
		// MessagesActivity.class);
		// WelcomeActivity.this.startActivity(intent);
		// WelcomeActivity.this.finish();
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// }.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
