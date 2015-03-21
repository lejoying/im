package com.open.welinks;

import java.lang.reflect.Field;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.open.lib.MyLog;
import com.open.welinks.R;
import com.open.welinks.model.Data;
import com.open.welinks.model.Parser;

@SuppressWarnings("deprecation")
public class WebViewActivity extends Activity {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();
	public String tag = "WebViewActivity";
	public MyLog log = new MyLog(tag, true);

	public WebView webView;
	public View backView;
	public TextView backTitle;
	public TextView titleContent;
	public RelativeLayout rightContainer;
	public ImageView moreView;
	public View selectMenuView;
	public View selectMenuBGView;
	public View progressView;
	public View browserView;
	public View copyView;

	public LinearLayout container;

	public String url = "http://www.we-links.com";

	public DisplayMetrics displayMetrics;

	public OnClickListener mOnClickListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setConfigCallback((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
		String url = this.getIntent().getStringExtra("url");
		if (url != null && !url.equals("")) {
			this.url = url;
		}
		initView();
		initializeListeners();
		bindEvent();
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
		webView.setDownloadListener(new DownloadListener() {

			@Override
			public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
		});
		webView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
			}

			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				titleContent.setText(title);
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
		titleContent = (TextView) findViewById(R.id.titleContent);

		selectMenuView = findViewById(R.id.selectMenu);
		selectMenuBGView = findViewById(R.id.selectMenuBG);

		browserView = findViewById(R.id.browser);
		copyView = findViewById(R.id.copy);

		progressView = findViewById(R.id.title_control_progress_container);
		progressView.setVisibility(View.GONE);

		rightContainer = (RelativeLayout) findViewById(R.id.rightContainer);
		RelativeLayout.LayoutParams rightParams = (android.widget.RelativeLayout.LayoutParams) rightContainer.getLayoutParams();
		rightParams.rightMargin = 0;
		moreView = new ImageView(this);
		moreView.setImageResource(R.drawable.chat_more);
		RelativeLayout.LayoutParams infomationParams = new RelativeLayout.LayoutParams((int) (48 * displayMetrics.density), (int) (48 * displayMetrics.density));
		infomationParams.addRule(RelativeLayout.CENTER_VERTICAL);
		int padding = (int) (10 * displayMetrics.density);
		moreView.setPadding(padding, padding, padding, padding);
		rightContainer.addView(moreView, infomationParams);
		dialogSpring.addListener(dialogSpringListener);
	}

	public BaseSpringSystem mSpringSystem = SpringSystem.create();
	public SpringConfig IMAGE_SPRING_CONFIG = SpringConfig.fromOrigamiTensionAndFriction(40, 7);
	public Spring dialogSpring = mSpringSystem.createSpring().setSpringConfig(IMAGE_SPRING_CONFIG);
	public DialogShowSpringListener dialogSpringListener = new DialogShowSpringListener();

	private class DialogShowSpringListener extends SimpleSpringListener {
		@Override
		public void onSpringUpdate(Spring spring) {
			float mappedValue = (float) spring.getCurrentValue();
			selectMenuView.setScaleX(mappedValue);
			selectMenuView.setScaleY(mappedValue);
			selectMenuBGView.setScaleX(mappedValue);
			selectMenuBGView.setScaleY(mappedValue);
		}
	}

	public void initializeListeners() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(backView)) {
					finish();
				} else if (view.equals(rightContainer)) {
					if (selectMenuView.getVisibility() == View.VISIBLE) {
						selectMenuView.setVisibility(View.GONE);
						selectMenuBGView.setVisibility(View.GONE);
					} else {
						selectMenuView.setVisibility(View.VISIBLE);
						selectMenuBGView.setVisibility(View.VISIBLE);
						dialogSpring.setCurrentValue(0);
						dialogSpring.setEndValue(1);
					}
				} else if (view.equals(browserView)) {
					selectMenuView.setVisibility(View.GONE);
					selectMenuBGView.setVisibility(View.GONE);
					Intent intent = new Intent();
					intent.setAction("android.intent.action.VIEW");
					Uri content_url = Uri.parse(url);
					intent.setData(content_url);
					startActivity(intent);
				} else if (view.equals(copyView)) {
					selectMenuView.setVisibility(View.GONE);
					selectMenuBGView.setVisibility(View.GONE);
					@SuppressWarnings("static-access")
					ClipboardManager clipboard = (ClipboardManager) WebViewActivity.this.getSystemService(WebViewActivity.this.CLIPBOARD_SERVICE);
					clipboard.setText(url);
					Toast.makeText(WebViewActivity.this, "复制链接成功", Toast.LENGTH_SHORT).show();
				}
			}
		};
	}

	public void bindEvent() {
		this.backView.setOnClickListener(mOnClickListener);
		this.rightContainer.setOnClickListener(mOnClickListener);
		this.browserView.setOnClickListener(mOnClickListener);
		this.copyView.setOnClickListener(mOnClickListener);
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
