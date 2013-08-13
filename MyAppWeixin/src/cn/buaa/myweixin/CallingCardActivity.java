package cn.buaa.myweixin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

public class CallingCardActivity extends Activity {

	private TextView tv_spacing;
	private TextView tv_spacing2;
	private TextView tv_spacing3;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.callingcarddemo);
		initView();
	}
	
	public void initView(){
		tv_spacing = (TextView) findViewById(R.id.tv_spacing);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		tv_spacing.setHeight((int)(dm.heightPixels*0.7));
		
		tv_spacing2 = (TextView) findViewById(R.id.tv_spacing2);
		tv_spacing2.setHeight((int)(dm.heightPixels*0.2));
		
		tv_spacing3 = (TextView) findViewById(R.id.tv_spacing3);
		tv_spacing3.setHeight((int)(dm.heightPixels*0.2));
		
		
	}

	public void back(View v){
		finish();
	}
	public void callcardRightDialog(View v){
		Intent intent = new Intent(this,CallingCardRightDialog.class);
		startActivity(intent);
	}
}
