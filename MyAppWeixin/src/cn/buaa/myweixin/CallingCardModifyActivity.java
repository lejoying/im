package cn.buaa.myweixin;

import cn.buaa.myweixin.apiutils.Account;
import cn.buaa.myweixin.apiutils.MCTools;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class CallingCardModifyActivity extends Activity {

	public static final int MODIFY_NAME = 0x31;
	public static final int MODIFY_PHONE = 0x32;
	public static final int MODIFY_YEWU = 0x33;

	private TextView cc_name;
	private TextView cc_phone;
	private TextView cc_yewu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.callingcard_modify);
		initView();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			int item = bundle.getInt("item");
			String value = bundle.getString("value");
			if (item == MODIFY_NAME) {
				Account account = MCTools.getLoginedAccount(this);
				account.setNickName(value);
				MCTools.saveAccount(this, account);
				cc_name.setText(value);
			}
			if (item == MODIFY_YEWU) {
				Account account = MCTools.getLoginedAccount(this);
				account.setMainBusiness(value);
				MCTools.saveAccount(this, account);
				cc_yewu.setText(value);
			}
		}
	}

	public void initView() {
		cc_name = (TextView) findViewById(R.id.cc_name);
		cc_phone = (TextView) findViewById(R.id.cc_phone);
		cc_yewu = (TextView) findViewById(R.id.cc_yewu);
		Account account = MCTools.getLoginedAccount(this);
		System.out.println(account.toString());
		cc_name.setText(account.getNickName());
		cc_phone.setText(account.getPhone());
		cc_yewu.setText(account.getMainBusiness());
	}

	public void chat_back(View v) {
		finish();
	}

	public void modifyname(View v) {
		Intent intent = new Intent(this, CallingCardModifyNameActivity.class);
		startActivity(intent);
	}

	public void modifyphone(View v) {
		Intent intent = new Intent(this, CallingCardModifyPhoneActivity.class);
		startActivity(intent);
	}

	public void modifyyewu(View v) {
		Intent intent = new Intent(this, CallingCardModifyYeWuActivity.class);
		startActivity(intent);
	}

	public void modifyheadmenu(View v) {
		Intent intent = new Intent(this, CCModifyMenuDialog.class);
		startActivity(intent);
	}

}
