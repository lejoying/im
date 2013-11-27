package com.lejoying.mc;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;

public class MainActivity extends Activity {

	private ServiceConnection conn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		conn = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
			}
		};

		Intent service = new Intent(this, CircleMenuService.class);
		bindService(service, conn, Context.BIND_AUTO_CREATE);
		
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

}
