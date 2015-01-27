package com.open.welinks;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class StatementActivity extends Activity implements OnClickListener {

	public View backView;
	public TextView title, content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statement);
		backView = findViewById(R.id.backView);
		title = (TextView) findViewById(R.id.backTitleView);
		content = (TextView) findViewById(R.id.content);
		backView.setOnClickListener(this);
		String type = getIntent().getStringExtra("type");
		if ("disclaimer".equals(type)) {
			title.setText("免责条款");
		} else if ("about".equals(type)) {
			title.setText("关于“" + getString(R.string.app_name) + "”");
		}
		getFromAssets(type + ".txt");
	}

	public void getFromAssets(String fileName) {
		try {
			InputStreamReader inputReader = new InputStreamReader(getResources().getAssets().open(fileName));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = "";
			String Result = "";
			while ((line = bufReader.readLine()) != null)
				Result += line + "\n";
			content.setText(Result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View view) {
		if (view.equals(backView)) {
			finish();
		}
	}
}
