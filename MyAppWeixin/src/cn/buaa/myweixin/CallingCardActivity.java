package cn.buaa.myweixin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class CallingCardActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.callingcard);
	}

	public void back(View v){
		finish();
	}
	public void callcardRightDialog(View v){
		Intent intent = new Intent(this,CallingCardRightDialog.class);
		startActivity(intent);
	}
}
