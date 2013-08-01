package cn.buaa.myweixin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class ServiceMenu extends Activity {

	private int noTopHeight;
	private int titleHeight;
	private int screenWidth;
	private int screenHeight;
	private float fromX;
	private float fromY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_menu);
		initView();
		initAnimation();
	}

	public void initView() {
		// 获取屏幕高度宽度。
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenHeight = dm.heightPixels;
		screenWidth = dm.widthPixels;

		Window window = getWindow();
		window.setWindowAnimations(R.anim.none_animation);
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.width = (int) screenWidth;
		lp.height = (int) screenHeight;
		Bundle bundle = getIntent().getExtras();
		fromX = bundle.getFloat("fromX");
		fromY = bundle.getFloat("fromY");
		noTopHeight = bundle.getInt("noTopHeight");
		titleHeight = bundle.getInt("titleHeight");
		this.getWindow().setAttributes(lp);
	}

	public void initAnimation() {
		
		int y = screenHeight-noTopHeight+titleHeight;
			
		ImageView iv_life = (ImageView) findViewById(R.id.service_life);
		
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		iv_life.measure(w, h);
		int iv_height = iv_life.getMeasuredHeight();
		int iv_width = iv_life.getMeasuredWidth();
		
		iv_life.setImageResource(R.drawable.service_menu_forlife_normal);
		TranslateAnimation am = new TranslateAnimation(fromX, screenWidth / 2
				- iv_width/2, fromY + y, screenHeight / 2
				- iv_height/2 -y/2);
		am.setDuration(500);
		am.setFillAfter(true);
		iv_life.setAnimation(am);
		am.start();
	}

	public void btn_water(View v) {
		Intent intent = new Intent();
		intent.setClass(ServiceMenu.this, ChatServiceActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("service", ChatServiceActivity.SERVICE_WATER);
		intent.putExtras(bundle);
		setResult(ChatServiceActivity.SERVICE_GETSERVICE, intent);
		finish();
	}

	// 点击其他位置关闭服务菜单
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return super.onTouchEvent(event);
	}

}
