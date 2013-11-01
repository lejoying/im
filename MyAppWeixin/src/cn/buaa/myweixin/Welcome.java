package cn.buaa.myweixin;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import cn.buaa.myweixin.apiutils.Account;
import cn.buaa.myweixin.apiutils.MCTools;
public class Welcome extends Activity {
	

	public static Welcome instance = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Account account = MCTools.getLoginedAccount(Welcome.this);
        MCTools.saveAccount(Welcome.this, account);
        if(account!=null){
			Intent intent = new Intent(this,MainWeixin.class);
			startActivity(intent);
		}
        SharedPreferences config = getSharedPreferences("config", MODE_PRIVATE);        
        if(config.getString("first", "none").equals("none")){
        	Editor edit = config.edit();
        	edit.putString("first", "true");
        };
        
        setContentView(R.layout.welcome);
        
        instance = this;
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
