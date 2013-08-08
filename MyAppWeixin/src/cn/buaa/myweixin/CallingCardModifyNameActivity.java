package cn.buaa.myweixin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

public class CallingCardModifyNameActivity extends Activity {

	private EditText modifyname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.callingcard_modify_name);
		initView();
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}

	public void initView() {
		modifyname = (EditText) findViewById(R.id.modifyname);
	}

	public void top_back(View v) {
		finish();
	}

	public void save(View v) {
		if (modifyname.getText().toString().equals("")) {
			return;
		}
		Intent intent = new Intent(this, CallingCardModifyActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("item", CallingCardModifyActivity.MODIFY_NAME);
		bundle.putString("value", modifyname.getText().toString());
		intent.putExtras(bundle);
		startActivity(intent);
	}

}
