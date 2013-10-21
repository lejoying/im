package cn.buaa.myweixin;


import cn.buaa.myweixin.tdcode.EncodeActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainTopRightDialog extends Activity {
	//private MyDialog dialog;
	private LinearLayout layout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_top_right_dialog);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event){
		finish();
		return true;
	}
	
	//扫一扫
	public void richScan(View v){
		Intent intent = new Intent(this,CaptureActivity.class);
		startActivity(intent);
		finish();
	}
	
	//打开我的名片
	public void showMyBusinessCard(View v){
		Intent intent = new Intent(MainTopRightDialog.this,CallingCardActivity.class);
		startActivity(intent);
		this.finish();
	}
}
