package cn.buaa.myweixin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class RegisterCheckingActivity extends Activity {

	private int checkingCode;
	private String registerNumber;
	private TextView tv_registernumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_checking);
		// 启动activity时自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		Bundle bundle = getIntent().getExtras();
		//checkingCode = bundle.getInt("checkingcode");
		registerNumber = bundle.getString("number");
		initView();
	}

	public void initView() {
		tv_registernumber = (TextView) findViewById(R.id.tv_registernumber);
		tv_registernumber.setText("+86" + registerNumber);
		System.out.println(checkingCode);
	}
	
	public void registerCheckingNext(View v){
		Intent intent = new Intent(this,CCommunityActivity.class);
		startActivity(intent);
	}

}
