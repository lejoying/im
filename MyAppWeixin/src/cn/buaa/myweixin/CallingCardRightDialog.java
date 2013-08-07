package cn.buaa.myweixin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class CallingCardRightDialog extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.callingcard_right_dialog);
	}

	public void modifyDetails(View v){
		Intent intent = new Intent(this,CallingCardModifyActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		finish();
		return super.onTouchEvent(event);
	}
	
	
}
