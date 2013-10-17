package cn.buaa.myweixin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import cn.buaa.myweixin.apiutils.ImageTools;

/**
 * 
 * @author geniuseoe2012 更多精彩，请关注我的CSDN博客http://blog.csdn.net/geniuseoe2012
 *         android开发交流群：200102476
 */
public class ChatServiceWaterActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */

	public static final int SERVICE_GETSERVICE = 0x01;
	public static final int SERVICE_WATER = 0x02;

	private final static int SEND_MESSAGE = 0x11;
	private Handler handler;
	private Button mBtnSend;
	private Button mBtnBack;
	private EditText mEditTextContent;
	private ListView mListView;
	private ChatMsgViewAdapter mAdapter;
	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
	private RelativeLayout rl_bottom;
	private int screenWidth;
	private int screenHeight;

	private RelativeLayout rl_hide;
	// service menu
	// 遮罩层
	private RelativeLayout rl_shade;
	// service menu button
	private RelativeLayout rl_service_life;
	private RelativeLayout rl_service_history;
	private RelativeLayout rl_service_kuaidi;
	private RelativeLayout rl_service_jiazheng;
	private RelativeLayout rl_service_anmo;
	private RelativeLayout rl_service_more;
	private RelativeLayout rl_service_water;

	private RelativeLayout rl_service_scale;

	private RelativeLayout[] serviceMainBtnArray;

	private boolean isServiceMenuShow;

	private Bitmap headfrom;
	private Bitmap headservice;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_service_water);
		// 启动activity时不自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		initView();
		initData();

		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				int what = msg.what;
				switch (what) {
				case SEND_MESSAGE:
					mListView.setSelection(mListView.getCount() - 1);
					break;
				default:
					break;
				}
			}

		};
	}

	public void initView() {
		// 获取屏幕的大小和状态栏的高度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenHeight = dm.heightPixels;
		screenWidth = dm.widthPixels;

		mListView = (ListView) findViewById(R.id.listview);
		mBtnSend = (Button) findViewById(R.id.btn_send);
		mBtnSend.setOnClickListener(this);

		// service menu
		rl_shade = (RelativeLayout) findViewById(R.id.rl_shade);
		rl_shade.setOnClickListener(this);
		rl_service_history = (RelativeLayout) findViewById(R.id.rl_service_history);
		rl_service_history.setOnClickListener(this);
		rl_service_kuaidi = (RelativeLayout) findViewById(R.id.rl_service_kuaidi);
		rl_service_kuaidi.setOnClickListener(this);
		rl_service_jiazheng = (RelativeLayout) findViewById(R.id.rl_service_jiazheng);
		rl_service_jiazheng.setOnClickListener(this);
		rl_service_anmo = (RelativeLayout) findViewById(R.id.rl_service_anmo);
		rl_service_anmo.setOnClickListener(this);
		rl_service_more = (RelativeLayout) findViewById(R.id.rl_service_more);
		rl_service_more.setOnClickListener(this);
		rl_service_water = (RelativeLayout) findViewById(R.id.rl_service_water);
		rl_service_water.setOnClickListener(this);
		rl_service_life = (RelativeLayout) findViewById(R.id.rl_service_life);
		rl_service_life.setOnClickListener(this);

		serviceMainBtnArray = new RelativeLayout[] { rl_service_history,
				rl_service_kuaidi, rl_service_jiazheng, rl_service_anmo,
				rl_service_more, rl_service_water, rl_service_life };

		rl_service_scale = (RelativeLayout) findViewById(R.id.rl_service_scale);

		isServiceMenuShow = false;

		mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
		rl_bottom = (RelativeLayout) findViewById(R.id.rl_bottom);

		// 实现自动添加行

		final int lineHeight = mEditTextContent.getLayoutParams().height;
		final int rlHeight = rl_bottom.getLayoutParams().height;
		mEditTextContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				int linecount = mEditTextContent.getLineCount();
				LayoutParams params = mEditTextContent.getLayoutParams();
				LayoutParams rl_params = rl_bottom.getLayoutParams();
				if (linecount == 1) {
					params.height = lineHeight;
					rl_params.height = rlHeight;
				}
				if (linecount == 2) {
					params.height = (int) (lineHeight + lineHeight / 1.8);
					rl_params.height = (int) (rlHeight + lineHeight / 1.8);
				}
				if (linecount >= 3) {
					params.height = (int) (lineHeight + lineHeight * 2 / 1.8);
					rl_params.height = (int) (rlHeight + lineHeight * 2 / 1.8);
				}
				rl_bottom.setLayoutParams(rl_params);
				mEditTextContent.setLayoutParams(params);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});
		mListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				mListView.requestFocus();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mEditTextContent.getWindowToken(),
						0);
				return false;
			}
		});

	}

	private String[] msgArray = new String[] { "欢迎使用乐家生活送水服务，请选择：" };

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private String[] dataArray = new String[] { sdf.format(new Date())};
	private final static int COUNT = 1;

	public void initData() {
		headfrom = ImageTools.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.renma));
		headservice = ImageTools.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.ss_water));
		for (int i = 0; i < COUNT; i++) {
			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setDate(dataArray[i]);

			if (i % 2 == 0) {
				entity.setName("送水服务");
				entity.setMsgType(true);
				entity.setService(true);
				entity.setHead(headservice);
			} else {
				entity.setName("人马");
				entity.setHead(headfrom);
				entity.setMsgType(false);
			}

			entity.setText(msgArray[i]);
			mDataArrays.add(entity);
		}

		mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
		mListView.setAdapter(mAdapter);
		mListView.setSelection(mListView.getCount() - 1);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_send:
			String contString = mEditTextContent.getText().toString();
			send(contString);
			break;
		case R.id.btn_back:
			finish();
			break;
		case R.id.rl_shade:
			hideServiceMenu();
			break;
		case R.id.rl_service_history:
			hideServiceMenu();
			send("历史记录");
			serviceSend("历史记录：\n无");
			break;
		case R.id.rl_service_kuaidi:
			hideServiceMenu();
			send("服务介绍");
			serviceSend("服务介绍：\n送水上门");
			break;
		case R.id.rl_service_jiazheng:
			hideServiceMenu();
			send("质量保障");
			serviceSend("质量保障：\n绝对保障");
			break;
		case R.id.rl_service_anmo:
			hideServiceMenu();
			send("服务流程");
			serviceSend("服务流程：\n现在预订-送水上门");
			break;
		case R.id.rl_service_more:
			hideServiceMenu();
			send("投诉电话");
			serviceSend("投诉电话：\n110");
			break;
		case R.id.rl_service_water:
			hideServiceMenu();
			send("产品价格");
			serviceSend("产品价格：\n小桶:xx元\n中桶:xx元\n大桶:xx元");
			break;
		case R.id.rl_service_life:
			hideServiceMenu();
			send("现在预订");
			serviceSend("请选择您的位置：");
			break;
		}
	}

	public void hideServiceMenu() {
		isServiceMenuShow = false;
		// 清除动画
		rl_service_life.clearAnimation();
		hideServiceButton();
		// 隐藏遮罩层
		rl_shade.setVisibility(View.INVISIBLE);
		// 显示被隐藏的按钮
		rl_hide.setVisibility(View.VISIBLE);
	}

	public void hideServiceButton() {
		// 隐藏菜单按钮
		rl_service_life.setVisibility(View.INVISIBLE);
		for (RelativeLayout rl : serviceMainBtnArray) {
			rl.clearAnimation();
			rl.setVisibility(View.INVISIBLE);
		}

	}

	private void send(String msg) {
		if (msg.length() > 0) {
			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setDate(getDate());
			entity.setName("人马");
			entity.setHead(headfrom);
			entity.setMsgType(false);
			entity.setText(msg);
			mDataArrays.add(entity);
			mAdapter.notifyDataSetChanged();
			mEditTextContent.setText("");
			handler.sendEmptyMessage(SEND_MESSAGE);
		}
	}
	
	private void serviceSend(String msg) {
			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setDate(getDate());
			entity.setName("送水服务");
			entity.setHead(headservice);
			entity.setMsgType(true);
			entity.setService(true);
			entity.setText(msg);
			mDataArrays.add(entity);
			mAdapter.notifyDataSetChanged();
			handler.sendEmptyMessage(SEND_MESSAGE);
	}

	private String getDate() {
		Calendar c = Calendar.getInstance();

		String year = String.valueOf(c.get(Calendar.YEAR));
		String month = String.valueOf(c.get(Calendar.MONTH));
		String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + 1);
		String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
		String mins = String.valueOf(c.get(Calendar.MINUTE));
		StringBuffer sbBuffer = new StringBuffer();
		sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":"
				+ mins);

		return sbBuffer.toString();
	}

	public void head_xiaohei(View v) { // 标题栏 返回按钮
		Intent intent = new Intent(ChatServiceWaterActivity.this, InfoXiaohei.class);
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isServiceMenuShow) {
				hideServiceMenu();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@SuppressLint("NewApi")
	public void show_service(final View v) { // 显示服务按钮
		if (isServiceMenuShow) {
			return;
		}
		isServiceMenuShow = true;
		hideServiceButton();
		// 获取点击View在屏幕上的绝对位置
		int[] location = new int[2];
		v.getLocationOnScreen(location);
		int viewX = location[0];
		int viewY = location[1];

		// 每百毫秒移动像素
		int moveSpeed = 120;
		// 计算移动距离和所需时间
		int moveLength = (int) Math.hypot(viewX - rl_service_life.getX(), viewY
				- rl_service_life.getY());
		int moveTime = moveLength * 100 / moveSpeed;

		// 显示遮罩层
		rl_shade.setVisibility(View.VISIBLE);
		
		// 隐藏被点击的按钮
		rl_hide = (RelativeLayout) v;
		rl_hide.setVisibility(View.INVISIBLE);

		// 获取状态栏高度
		final int topHeight = screenHeight
				- getWindow().findViewById(Window.ID_ANDROID_CONTENT)
						.getHeight();
		// 设置service menu按钮可见
		rl_service_life.setVisibility(View.VISIBLE);
		rl_service_life.bringToFront();
		// TranslateAnimation
		float fromX = viewX - rl_service_life.getX()
				- ((rl_service_life.getWidth() - v.getWidth()) / 2);
		float fromY = viewY - rl_service_life.getY() - topHeight
				- ((rl_service_life.getHeight() - v.getHeight()) / 2);
		TranslateAnimation serviceShowTranslate = new TranslateAnimation(fromX,
				0, fromY, 0);
		serviceShowTranslate.setDuration(moveTime);
		serviceShowTranslate.setInterpolator(new AccelerateDecelerateInterpolator());
		serviceShowTranslate.setFillAfter(true);
		rl_service_life.startAnimation(serviceShowTranslate);

		// ScaleAnimation
		float resizeX = (float) rl_service_life.getWidth()
				/ (float) rl_service_scale.getWidth();
		float resizeY = (float) rl_service_life.getHeight()
				/ (float) rl_service_scale.getWidth();
		ScaleAnimation serviceShowScale = new ScaleAnimation(1, resizeX, 1,
				resizeY, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		serviceShowScale.setDuration(moveTime);
		serviceShowScale.setFillAfter(true);
		rl_service_scale.startAnimation(serviceShowScale);

		serviceShowTranslate.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				for (RelativeLayout rl : serviceMainBtnArray) {
					rl.setVisibility(View.VISIBLE);
				}
				setAnimation(serviceMainBtnArray);
			}
		});

	}

	@SuppressLint("NewApi")
	public void setAnimation(RelativeLayout[] rls) {
		for (RelativeLayout rl : rls) {
			TranslateAnimation translateAnimation = new TranslateAnimation(
					rl_service_life.getX() - rl.getX(), 1,
					rl_service_life.getY() - rl.getY(), 1);
			translateAnimation.setDuration(70);
			translateAnimation.setFillAfter(true);
			rl.startAnimation(translateAnimation);
		}
	}
	//返回按钮
	public void chat_back(View v){
		finish();
	}
}
