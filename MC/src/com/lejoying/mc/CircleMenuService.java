package com.lejoying.mc;

import com.lejoying.listener.ToTryListener;
import com.lejoying.mcutils.ToTry;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

public class CircleMenuService extends Service {

	@Override
	public void onCreate() {
		super.onCreate();
		createMenu();
	}

	private void createMenu() {

		WindowManager wm = (WindowManager) getApplicationContext()
				.getSystemService(WINDOW_SERVICE);

		WindowManager.LayoutParams params = new WindowManager.LayoutParams();

		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;

		params.width = WindowManager.LayoutParams.WRAP_CONTENT;

		params.height = WindowManager.LayoutParams.WRAP_CONTENT;

		final TextView tv = new TextView(this);
		tv.setText("aaaaaaaaaa");
		wm.addView(tv, params);
		System.out.println(tv.getHeight() + "<<<<<<<<<<<<<<<<");
		ToTry.tryDoing(10, 500, new ToTryListener() {

			@Override
			public void successed(long time) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean isSuccess() {
				// TODO Auto-generated method stub
				System.out.println(tv.getHeight());
				return false;
			}

			@Override
			public void failed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeDoing() {
				// TODO Auto-generated method stub

			}
		});

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
