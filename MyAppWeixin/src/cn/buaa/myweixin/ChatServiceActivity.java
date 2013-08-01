package cn.buaa.myweixin;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TooManyListenersException;

import cn.buaa.myweixin.utils.HeadImageUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * 
 * @author geniuseoe2012 更多精彩，请关注我的CSDN博客http://blog.csdn.net/geniuseoe2012
 *         android开发交流群：200102476
 */
public class ChatServiceActivity extends Activity implements OnClickListener {
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

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_service);
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
		mListView = (ListView) findViewById(R.id.listview);
		mBtnSend = (Button) findViewById(R.id.btn_send);
		mBtnSend.setOnClickListener(this);
		mBtnBack = (Button) findViewById(R.id.btn_back);
		mBtnBack.setOnClickListener(this);

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

	}

	private String[] msgArray = new String[] { "欢迎您使用乐家品质生活服务，请选择服务类型：",
			"....", "欢迎您使用乐家品质生活服务，请选择服务类型：", "....", "欢迎您使用乐家品质生活服务，请选择服务类型：",
			"....", "欢迎您使用乐家品质生活服务，请选择服务类型：" };

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private String[] dataArray = new String[] { sdf.format(new Date()),
			sdf.format(new Date()), sdf.format(new Date()),
			sdf.format(new Date()), sdf.format(new Date()),
			sdf.format(new Date()), sdf.format(new Date()) };
	private final static int COUNT = 7;

	public void initData() {
		for (int i = 0; i < COUNT; i++) {
			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setDate(dataArray[i]);
			HeadImageUtils hiu = new HeadImageUtils();
			if (i % 2 == 0) {
				entity.setName("客服MM");
				entity.setMsgType(true);
				entity.setService(true);
				entity.setHead(hiu.returnHeadBitmap(BitmapFactory
						.decodeResource(getResources(), R.drawable.service_head)));
			} else {
				entity.setName("人马");
				entity.setHead(hiu.returnHeadBitmap(BitmapFactory
						.decodeResource(getResources(), R.drawable.renma)));
				entity.setMsgType(false);
			}

			entity.setText(msgArray[i]);
			mDataArrays.add(entity);
		}

		mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_send:
			send();
			break;
		case R.id.btn_back:
			finish();
			break;
		}
	}

	private void send() {
		String contString = mEditTextContent.getText().toString();
		if (contString.length() > 0) {
			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setDate(getDate());
			entity.setName("人马");
			entity.setHead(new HeadImageUtils().returnHeadBitmap(BitmapFactory
					.decodeResource(getResources(), R.drawable.renma)));
			entity.setMsgType(false);
			entity.setText(contString);
			mDataArrays.add(entity);
			mAdapter.notifyDataSetChanged();
			mEditTextContent.setText("");
			handler.sendEmptyMessage(SEND_MESSAGE);
		}
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
		Intent intent = new Intent(ChatServiceActivity.this, InfoXiaohei.class);
		startActivity(intent);
	}

	@SuppressLint("NewApi")
	public void show_service(View v) { // 显示服务按钮
		int[] aa = new int[2];
		v.getLocationInWindow(aa);
		System.out.println(aa);
		int noTopHeight = getWindow().findViewById(Window.ID_ANDROID_CONTENT)
				.getHeight();
		int titleHeight = findViewById(R.id.rl_layout).getHeight();
		Intent showServiceMenu = new Intent(ChatServiceActivity.this,
				ServiceMenu.class);
		Bundle bundle = new Bundle();
		bundle.putFloat("fromX", v.getX());
		bundle.putFloat("fromY", v.getY());
		bundle.putInt("noTopHeight", noTopHeight);
		bundle.putInt("titleHeight", titleHeight);
		showServiceMenu.putExtras(bundle);
		startActivityForResult(showServiceMenu, SERVICE_GETSERVICE);
	}

	// 获取点击Activity返回值
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SERVICE_GETSERVICE && data != null) {
			int service = data.getExtras().getInt("service");
			if (service == SERVICE_WATER) {
				Toast.makeText(ChatServiceActivity.this, "送水服务",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

}
