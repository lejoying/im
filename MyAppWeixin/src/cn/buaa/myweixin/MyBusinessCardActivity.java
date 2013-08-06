package cn.buaa.myweixin;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MyBusinessCardActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mybusinesscard);
	}

	public void back(View v){
		finish();
	}
}
