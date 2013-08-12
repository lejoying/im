package cn.buaa.myweixin;

import java.util.ArrayList;
import java.util.Calendar;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import cn.buaa.myweixin.utils.ImageTools;

/**
 * 
 * @author geniuseoe2012 更多精彩，请关注我的CSDN博客http://blog.csdn.net/geniuseoe2012
 *         android开发交流群：200102476
 */
@SuppressLint("HandlerLeak")
public class ChatActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */

	private final static int SEND_MESSAGE = 0x11;
	private Handler handler;
	private Button mBtnSend;
	private Button mBtnBack;
	private EditText mEditTextContent;
	private ListView mListView;
	private ChatMsgViewAdapter mAdapter;
	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
	private RelativeLayout rl_bottom;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_xiaohei);
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

	private String[] msgArray = new String[] { "有大吗", "有！你呢？", "我也有", "那上吧",
			"打啊！你放大啊", "你tm咋不放大呢？留大抢人头那！Cao的。你个菜b", "2B不解释", "尼滚....", };

	private String[] dataArray = new String[] { "2012-09-01 18:00",
			"2012-09-01 18:10", "2012-09-01 18:11", "2012-09-01 18:20",
			"2012-09-01 18:30", "2012-09-01 18:35", "2012-09-01 18:40",
			"2012-09-01 18:50" };
	private final static int COUNT = 8;

	public void initData() {
		Bitmap headfrom = ImageTools.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.renma));
		Bitmap headto = ImageTools.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.xiaohei));
		for (int i = 0; i < COUNT; i++) {
			ChatMsgEntity entity = new ChatMsgEntity();

			entity.setDate(dataArray[i]);
			if (i % 2 == 0) {
				entity.setName("小黑");
				entity.setHead(headto);
				entity.setMsgType(true);
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
			entity.setHead(ImageTools.getCircleBitmap(BitmapFactory
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
		Intent intent = new Intent(ChatActivity.this, InfoXiaohei.class);
		startActivity(intent);
	}
	
	public void showCC(View v){ //标题栏显示对方名片
		Intent intent = new Intent(ChatActivity.this,CallingCardActivity.class);
		startActivity(intent);
	}

}