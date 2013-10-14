package cn.buaa.myweixin;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import cn.buaa.myweixin.utils.Account;
import cn.buaa.myweixin.utils.Community;

public class Welcome extends Activity {
	

	public static Welcome instance = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
			InputStream is = openFileInput("account");
			ObjectInputStream ois = new ObjectInputStream(is);
			Account account = (Account) ois.readObject();
			if(account!=null){
				Intent intent = new Intent(this,MainWeixin.class);
				startActivity(intent);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block         
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
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
