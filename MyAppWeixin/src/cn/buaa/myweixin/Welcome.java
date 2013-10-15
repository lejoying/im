package cn.buaa.myweixin;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import cn.buaa.myweixin.utils.Account;
import cn.buaa.myweixin.utils.MCNowUser;
import cn.buaa.myweixin.utils.MCTools;
public class Welcome extends Activity {
	

	public static Welcome instance = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Account account = MCTools.getLoginedAccount(Welcome.this);
        if(account!=null){
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
