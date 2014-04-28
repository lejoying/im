package com.lejoying.wxgs.activity;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.view.BackgroundView;
import com.lejoying.wxgs.activity.view.RecordView;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class SendVoiceActivity extends BaseActivity implements OnClickListener {
	BackgroundView mBackground;
	MainApplication app = MainApplication.getMainApplication();
	LayoutInflater mInflater;

	RecordView recordView;

	int height, width, dip;
	float density;

	RelativeLayout sendvoice_rl_navigation;

	int RECORD_START = 0x001;
	int RECORD_STOP = 0x002;
	int recordStatus = RECORD_STOP;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_voice);
		mInflater = getLayoutInflater();
		getWindow().setBackgroundDrawableResource(R.drawable.square_background);
		initData();
		initEvent();
	}

	protected void onResume() {
		CircleMenu.hide();
		super.onResume();
	}

	public void initData() {
		// TODO Auto-generated method stub
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		dip = (int) (40 * density + 0.5f);
		height = dm.heightPixels;
		width = dm.widthPixels;
	}

	public void initEvent() {
		// TODO Auto-generated method stub
		recordView = (RecordView) findViewById(R.id.recordView);
		recordView.setMode(RecordView.MODE_TIMER);
		// recordView.startProgress();
		final TextView sendvoice_tv = (TextView) findViewById(R.id.sendvoice_tv);
		View sendvoice_button = findViewById(R.id.sendvoice_button);
		View sendvoice_iv_commit = findViewById(R.id.sendvoice_iv_commit);
		View sendvoice_iv_del = findViewById(R.id.sendvoice_iv_del);
		sendvoice_rl_navigation = (RelativeLayout) findViewById(R.id.sendvoice_rl_navigation);

		RelativeLayout.LayoutParams buttonrelativeParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		buttonrelativeParams.width = width / 3 * 2;
		// buttonrelativeParams.height=60;
		buttonrelativeParams.topMargin = 20;
		buttonrelativeParams.bottomMargin = 20;
		buttonrelativeParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		sendvoice_button.setLayoutParams(buttonrelativeParams);

		RelativeLayout.LayoutParams tvrelativeParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		tvrelativeParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		sendvoice_tv.setLayoutParams(tvrelativeParams);

		RelativeLayout.LayoutParams commitrelativeParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		commitrelativeParams.addRule(RelativeLayout.RIGHT_OF,
				R.id.sendvoice_button);
		// commitrelativeParams.rightMargin = width / 25;
		commitrelativeParams.leftMargin = width / 25;
		commitrelativeParams.addRule(RelativeLayout.CENTER_VERTICAL);
		sendvoice_iv_commit.setLayoutParams(commitrelativeParams);

		RelativeLayout.LayoutParams delrelativeParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		delrelativeParams
				.addRule(RelativeLayout.LEFT_OF, R.id.sendvoice_button);
		// delrelativeParams.leftMargin = width / 25;
		commitrelativeParams.rightMargin = width / 25;
		delrelativeParams.addRule(RelativeLayout.CENTER_VERTICAL);
		sendvoice_iv_del.setLayoutParams(delrelativeParams);

		sendvoice_iv_commit.setOnClickListener(this);
		sendvoice_iv_del.setOnClickListener(this);
		sendvoice_rl_navigation.setOnClickListener(this);

		sendvoice_button.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					sendvoice_tv.setText("放开停止");
					recordView.startTimer();
					recordStatus = RECORD_START;
					break;
				case MotionEvent.ACTION_UP:
					sendvoice_tv.setText("开始录音");
					recordView.stopTimer();
					recordStatus = RECORD_STOP;
					recordView.setProgressTime(40000);
					recordView.setMode(RecordView.MODE_PROGRESS);
					recordView.startProgress();
					break;
				default:
					break;
				}
				return true;
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		switch (v.getId()) {
		case R.id.sendvoice_iv_commit:
			intent.putExtra("path", "");
			setResult(200, intent);
			finish();
			break;
		case R.id.sendvoice_iv_del:
			setResult(400, intent);
			finish();
			break;
		case R.id.sendvoice_rl_navigation:

			break;
		default:
			break;
		}
	}

	void startMediaRecord() {
		String fileName = new Date().getTime() + ".aac";
		// AudioRecord audioRecord = new AudioRecord(audioSource,
		// sampleRateInHz, channelConfig, audioFormat,
		// bufferSizeInBytes)
		MediaRecorder recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);//
		// recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
		// recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		// recorder.setAudioSamplingRate(3000);
		// recorder.setAudioEncodingBitRate(10000);
		recorder.setOutputFile((new File(app.sdcardVoiceFolder, fileName))
				.getAbsolutePath());
		try {
			recorder.prepare();
		} catch (IOException e) {
			e.printStackTrace();
		}

		recorder.start();
		System.out.println("start------------------------------");
		// };
		// }.start();
	}
}
