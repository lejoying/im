package cn.buaa.myweixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class CallingCardModifyYeWuActivity extends Activity {

	private EditText modifyyw;
	private TextView yewulength;
	private List<String> yewu;
	private int position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.callingcard_modify_yewu);
		initView();
		initData();
		// getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}

	public void initView() {
		modifyyw = (EditText) findViewById(R.id.modifyyewu);
		yewulength = (TextView) findViewById(R.id.yewulength);
		modifyyw.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				yewulength.setText(s.length() + "/240");
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void initData() {
		yewu = new ArrayList<String>();
		yewu.add("学校治安不好，经常有社会闲杂人员出入，一天晚自习下课后，一对情侣在学校的小湖边幽会时被被抢，男生受伤学生家长到学校理论，指责校方没有装路灯什么的。 校方回复到：有路灯的地方他们也不去啊！");
		yewu.add("放假时，学校领导给我们开会说“同学们，你们千万要注意生命安全，生命安全无非两种情况，一种是生，一种是死，哪一种都非常不好”。");
		yewu.add("我们班有个很不招人待见的同学，有一次他突然摔伤了要叫救护车，大家看他确实挺惨只好立刻写信给急救中心。");
		yewu.add("为了节省家里的水费，我每天到单位之后再洗脸、刷牙、刮胡子、如厕。");
		yewu.add("我从家里带来一个脸盆，晚上没人的时候，就用饮水机里的热水泡脚。");
	}

	public void chat_back(View v) {
		finish();
	}

	public void save(View v) {
		if (modifyyw.getText().toString().equals("")) {
			return;
		}
		Intent intent = new Intent(this, CallingCardModifyActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("item", CallingCardModifyActivity.MODIFY_YEWU);
		bundle.putString("value", modifyyw.getText().toString());
		intent.putExtras(bundle);
		startActivity(intent);
	}

	public void random(View v) {
		Random random = new Random();
		int temp=random.nextInt(5);
		while(position==temp){
			temp = random.nextInt(5);
		}
		position = temp;
		modifyyw.setText(yewu.get(position));
	}
}
