package cn.buaa.myweixin;


import cn.buaa.myweixin.utils.LocationTools;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class Welcome extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
    }
    public void welcome_login(View v) {  
      	Intent intent = new Intent();
		intent.setClass(Welcome.this,Login.class);//启动到登录界面
//      	intent.setClass(Welcome.this,MainWeixin.class);//直接登录到主界面
		startActivity(intent);
      }  
    public void welcome_register(View v) {  
      	Intent intent = new Intent();
      	intent.setClass(Welcome.this, RegisterActivity.class);
		startActivity(intent);
		//this.finish();
      }  
   
}
