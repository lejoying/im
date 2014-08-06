package com.open.welinks;

import com.open.welinks.model.Parser;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

	String tag = "MainActivity";
	public Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		Log.d(tag, "hello world!");

		Parser parser = Parser.getInstance();
		parser.initialize(context);
		
		parser.parse();
	}

}
