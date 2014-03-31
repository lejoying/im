package com.lejoying.wxgs.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.SysApplication;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.app.Activity;
import android.content.Intent;

public class LoadingActivity extends Activity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_loading);
		SysApplication.getInstance().addActivity(this);
		getWindow().setBackgroundDrawableResource(R.drawable.bg);
		final Intent localIntent=new Intent(LoadingActivity.this,LoginActivity.class);  
        Timer timer=new Timer();  
        TimerTask task=new TimerTask(){  
             @Override  
             public void run(){  
              startActivity(localIntent); 
              finish();
             }  
        };  
        timer.schedule(task,2000);
		
	}

}
