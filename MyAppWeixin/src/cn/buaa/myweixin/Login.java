package cn.buaa.myweixin;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;

public class Login extends Activity {
	private EditText mUser; // ’ ∫≈±‡º≠øÚ
	private EditText mPassword; // √‹¬Î±‡º≠øÚ

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        mUser = (EditText)findViewById(R.id.login_user_edit);
        mPassword = (EditText)findViewById(R.id.tv_password);
        
    }

    public void login_mainweixin(View v) {
    	
      }  
    public void login_back(View v) {     //±ÍÃ‚¿∏ ∑µªÿ∞¥≈•
      	this.finish();
      }  
    public void login_pw(View v) {     //Õ¸º«√‹¬Î∞¥≈•
    	Uri uri = Uri.parse("http://3g.qq.com"); 
    	Intent intent = new Intent(Intent.ACTION_VIEW, uri); 
    	startActivity(intent);
    	//Intent intent = new Intent();
    	//intent.setClass(Login.this,Whatsnew.class);
        //startActivity(intent);
      }  
}
