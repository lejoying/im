package com.open.welinks;

import java.lang.reflect.Field;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.open.lib.MyLog;
import com.open.welinks.model.Data;
import com.open.welinks.model.Parser;

public class WebViewActivity extends Activity {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();
	public String tag = "WebViewActivity";
	public MyLog log = new MyLog(tag, true);

	public WebView webView;
	public View backView;
	public TextView backTitle;
	public TextView centerTv;

	public LinearLayout container;

	public String url = "http://www.we-links.com";

	public DisplayMetrics displayMetrics;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setConfigCallback((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
		String url = this.getIntent().getStringExtra("url");
		if (url != null && !url.equals("")) {
			this.url = url;
		}
		initView();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		displayMetrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		setContentView(R.layout.activity_webview);
		container = (LinearLayout) findViewById(R.id.container);
		webView = new WebView(getApplicationContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		container.addView(webView, params);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
			}

			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				centerTv.setText(title);
			}

			@Override
			public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
				// super.onConsoleMessage(consoleMessage)
				return true;
			}

			@Override
			@Deprecated
			public void onConsoleMessage(String message, int lineNumber, String sourceID) {
				// onConsoleMessage(message, lineNumber, sourceID)
			}
		});
		webView.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				log.e("onReceivedError:Oh no! " + description);
			}
		});
		webView.loadUrl(url);

		backView = findViewById(R.id.backView);
		backTitle = (TextView) findViewById(R.id.backTitleView);
		backTitle.setText("返回");
		centerTv = (TextView) findViewById(R.id.titleContent);
		backView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		setConfigCallback(null);
		super.onDestroy();
		webView.setVisibility(View.GONE);
		webView.removeAllViews();
		webView.destroy();

	}

	public void setConfigCallback(WindowManager windowManager) {
		try {
			Field field = WebView.class.getDeclaredField("mWebViewCore");
			field = field.getType().getDeclaredField("mBrowserFrame");
			field = field.getType().getDeclaredField("sConfigCallback");
			field.setAccessible(true);
			Object configCallback = field.get(null);

			if (null == configCallback) {
				return;
			}

			field = field.getType().getDeclaredField("mWindowManager");
			field.setAccessible(true);
			field.set(configCallback, windowManager);
		} catch (Exception e) {
		}
	}
}
