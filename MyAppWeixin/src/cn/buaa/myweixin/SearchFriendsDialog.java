package cn.buaa.myweixin;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SearchFriendsDialog extends Activity {

	private LinearLayout layout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchfriends_dialog);
		
		layout=(LinearLayout)findViewById(R.id.searchfriends_dialog_layout);
		layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！", 
						Toast.LENGTH_SHORT).show();	
			}
		});
	}
	//点击其他位置收起菜单
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	
}
