package cn.buaa.myweixin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class CCModifyMenuDialog extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.callingcard_modify_menu);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		finish();
		return super.onTouchEvent(event);
	}
	
	public void takePhoto(View v){
		Intent intent = new Intent(this,CallingCardModifyHeadActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("way", CallingCardModifyHeadActivity.TAKEPHOTO);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
	}

	public void choiceFromNative(View v){
		Intent intent = new Intent(this,CallingCardModifyHeadActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("way", CallingCardModifyHeadActivity.CHOICEFROMNATIVE);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
	}
	
	public void cancel(View v){
		finish();
	}
	
}
