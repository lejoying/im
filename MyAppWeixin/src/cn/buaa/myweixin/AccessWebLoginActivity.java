package cn.buaa.myweixin;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class AccessWebLoginActivity extends Activity {

	private String accessKey;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accessweblogin);
		accessKey = getIntent().getExtras().getString("accessKey");
	}

	public void access(View v){
		
	}
	
	public void cancel(View v){
		
	}
	
	public void back(View v){
		finish();
	}
}
