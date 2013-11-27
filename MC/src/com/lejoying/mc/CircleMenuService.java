package com.lejoying.mc;

import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;

import com.lejoying.adapter.ToTryAdapter;
import com.lejoying.mcutils.MenuEntity;
import com.lejoying.mcutils.ToTry;

public class CircleMenuService extends Service {

	private WindowManager wm;
	private WindowManager.LayoutParams params;

	private LayoutInflater inflater;
	private ImageView iv_disk;

	private boolean init;

	private int diskRadius;

	private CircleMenuBinder circleMenuBinder;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return circleMenuBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		circleMenuBinder = new CircleMenuBinder();
		createMenu(null);
	}

	public class CircleMenuBinder extends Binder {

		public CircleMenuService getService() {
			return CircleMenuService.this;
		}
	}

	private void createMenu(List<MenuEntity> menuList) {

		wm = (WindowManager) getApplicationContext().getSystemService(
				WINDOW_SERVICE);
		
		params = new WindowManager.LayoutParams();
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		params.token = circleMenuBinder;

		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;

		params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

		params.format = PixelFormat.TRANSPARENT;

		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		iv_disk = (ImageView) inflater.inflate(R.layout.service_circlemenu,
				null);

		params.token = iv_disk.getWindowToken();

		iv_disk.setVisibility(View.INVISIBLE);

		iv_disk.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				System.out.println("aaaa");
				return true;
			}
		});

		wm.addView(iv_disk, params);
		showMenu();
	}

	public void showMenu() {
		if (!init) {
			ToTry.tryDoing(10, 1000, new ToTryAdapter() {
				@Override
				public boolean isSuccess() {
					return iv_disk.getWidth() != 0;
				}

				@Override
				public void successed(long time) {
					init = true;
					diskRadius = iv_disk.getWidth() / 2;
					System.out.println(diskRadius);
					System.out.println(params.y);
					params.y = -5 * diskRadius;
					System.out.println(params.y);
					wm.updateViewLayout(iv_disk, params);
					iv_disk.setVisibility(View.VISIBLE);
				}
			});
		} else {

		}
	}

	public void hideMenu() {

	}

}
