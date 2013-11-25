package com.lejoying.mc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Intent service = new Intent(this, CircleMenuService.class);
		startService(service);

		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(500);
					Intent intent = new Intent(MainActivity.this,
							MessagesActivity.class);
					MainActivity.this.startActivity(intent);
					MainActivity.this.finish();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		System.out.println("aaaaaaaaaaaaa");
		return super.onTouchEvent(event);
	}

}
