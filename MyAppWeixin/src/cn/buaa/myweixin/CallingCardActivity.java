package cn.buaa.myweixin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class CallingCardActivity extends Activity {

	private static final int SCROLL = 0x51;

	private TextView tv_spacing;
	private TextView tv_spacing2;
	private TextView tv_spacing3;
	private RelativeLayout rl_show;
	private RelativeLayout relativeLayout1;

	private TextView textView4;

	private ScrollView scrollView1;

	private Handler handler;

	private boolean stopSend;

	private int startHight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.callingcarddemo);

		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				int what = msg.what;
				switch (what) {
				case SCROLL:
					if (scrollView1.getScrollY() > 10) {
						textView4.setMaxLines(100);
					}
					if (scrollView1.getScrollY() < 10) {
						textView4.setMaxLines(3);
					}
					break;
				}
				super.handleMessage(msg);
			}
		};
	}

	public void initView() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		relativeLayout1 = (RelativeLayout) findViewById(R.id.relativeLayout1);
		rl_show = (RelativeLayout) findViewById(R.id.rl_show);

		Rect frame = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;

		tv_spacing2 = (TextView) findViewById(R.id.tv_spacing2);

		tv_spacing.setHeight((int) (dm.heightPixels
				- relativeLayout1.getHeight() - rl_show.getHeight()
				- statusBarHeight - tv_spacing2.getHeight()));

		tv_spacing3 = (TextView) findViewById(R.id.tv_spacing3);
		tv_spacing3.setHeight((int) (dm.heightPixels * 0.2));

		textView4 = (TextView) findViewById(R.id.textView4);

		scrollView1 = (ScrollView) findViewById(R.id.scrollView1);

		scrollView1.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				stopSend = true;
				new Thread() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						while (stopSend) {
							handler.sendEmptyMessage(SCROLL);
							int start = scrollView1.getScrollY();
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							int stop = scrollView1.getScrollY();
							if(start == stop){
								stopSend = false;
							}
							System.out.println("aaa");
						}
						
						super.run();
					}

				}.start();
				return false;
			}
		});

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {

		tv_spacing = (TextView) findViewById(R.id.tv_spacing);
		if (startHight == 0) {
			startHight = tv_spacing.getHeight();
		}
		if (tv_spacing.getHeight() == startHight) {
			initView();
		}
		super.onWindowFocusChanged(hasFocus);
	}

	public void back(View v) {
		finish();
	}

	public void callcardRightDialog(View v) {
		Intent intent = new Intent(this, CallingCardRightDialog.class);
		startActivity(intent);
	}
}
