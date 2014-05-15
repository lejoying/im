package com.lejoying.wxgs.activity;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.view.BackgroundView;
import com.lejoying.wxgs.activity.view.RecordView;
import com.lejoying.wxgs.activity.view.RecordView.PlayButtonClickListener;
import com.lejoying.wxgs.activity.view.RecordView.ProgressListener;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;

public class SendVoiceActivity extends BaseActivity implements OnClickListener {
	BackgroundView mBackground;
	MainApplication app = MainApplication.getMainApplication();
	LayoutInflater mInflater;

	RecordView recordView;
	TextView sendvoice_tv;

	int height, width, dip;
	float density;

	RelativeLayout sendvoice_rl_navigation;

	MediaRecorder recorder;
	MediaPlayer player;
	boolean isStop = true;
	boolean isPlayEnd = true;
	String voiceFileName = "";

	int RECORD_START = 0x001;
	int RECORD_STOP = 0x002;
	int recordStatus = RECORD_STOP;

	Boolean backStatus = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_voice);
		mInflater = getLayoutInflater();
		getWindow().setBackgroundDrawableResource(R.drawable.square_background);
		initData();
		initEvent();
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(0, 0);
	}

	protected void onResume() {
		// CircleMenu.hide();
		super.onResume();
	}

	@Override
	public void finish() {
		if (player != null) {
			player.release();
		}
		if (!backStatus) {
			initMediaRecord();
		}
		super.finish();
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
		sendvoice_tv = (TextView) findViewById(R.id.sendvoice_tv);
		View sendvoice_button = findViewById(R.id.sendvoice_button);
		View sendvoice_iv_commit = findViewById(R.id.sendvoice_iv_commit);
		View sendvoice_iv_del = findViewById(R.id.sendvoice_iv_del);
		sendvoice_rl_navigation = (RelativeLayout) findViewById(R.id.sendvoice_rl_navigation);

		RelativeLayout.LayoutParams buttonrelativeParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		buttonrelativeParams.width = width / 3 * 2;
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
		commitrelativeParams.leftMargin = width / 25;
		commitrelativeParams.addRule(RelativeLayout.CENTER_VERTICAL);
		sendvoice_iv_commit.setLayoutParams(commitrelativeParams);

		RelativeLayout.LayoutParams delrelativeParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		delrelativeParams
				.addRule(RelativeLayout.LEFT_OF, R.id.sendvoice_button);
		delrelativeParams.rightMargin = width / 25;
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
					initMediaRecord();
					sendvoice_tv.setText("放开停止");
					recordView.startTimer();
					recordView.setMode(RecordView.MODE_TIMER);
					startMediaRecord();
					recordStatus = RECORD_START;
					break;
				case MotionEvent.ACTION_UP:
					initRecorder();
					break;
				default:
					break;
				}
				return true;
			}
		});
		recordView.setPlayButtonClickListener(new PlayButtonClickListener() {

			@Override
			public void onPlay() {
				if (isPlayEnd) {
					initMediaPlay();
				}
				if (!player.isPlaying()) {
					player.start();
				}
				isStop = false;
				isPlayEnd = false;
			}

			@Override
			public void onPause() {
				if (player.isPlaying()) {
					player.pause();
				}
				isStop = true;
			}
		});
		recordView.setProgressListener(new ProgressListener() {

			@Override
			public void onProgressEnd() {
				if (recordView.getMode() == RecordView.MODE_PROGRESS) {
					player.stop();
					isStop = false;
					isPlayEnd = true;
				} else {
					initRecorder();
				}
			}

			@Override
			public void onDrag(float percent) {
				if (isPlayEnd) {
					initMediaPlay();
				}
				player.seekTo((int) (player.getDuration() * percent));
				recordView.seekTo(percent);
				recordView.setProgressTime(player.getDuration());
				recordView.setMode(RecordView.MODE_PROGRESS);
				if (!player.isPlaying()) {
					recordView.startProgress();
					player.start();
				}
				isStop = false;
				isPlayEnd = false;
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.sendvoice_iv_commit:
			if (player != null) {
				player.release();
			}
			intent.putExtra("fileName", voiceFileName);
			setResult(Activity.RESULT_OK, intent);
			backStatus = true;
			finish();
			break;
		case R.id.sendvoice_iv_del:
			setResult(400, intent);
			backStatus = false;
			finish();
			break;
		case R.id.sendvoice_rl_navigation:

			break;
		default:
			break;
		}
	}

	@SuppressLint("InlinedApi")
	void startMediaRecord() {
		try {
			voiceFileName = new Date().getTime() + ".aac";
			recorder = new MediaRecorder();
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
			recorder.setOutputFile((new File(app.sdcardVoiceFolder,
					voiceFileName)).getAbsolutePath());
			recorder.prepare();
			recorder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initRecorder() {
		if (recordView.getMode() == RecordView.MODE_TIMER) {
			sendvoice_tv.setText("开始录音");
			recordView.stopTimer();
			recorder.stop();
			recorder.release();
			recorder = null;
			recordStatus = RECORD_STOP;
			recordView.setDragEnable(true);
			initMediaPlay();
			if (player.getDuration() >= 1000) {
				recordView.setProgressTime(player.getDuration());
				recordView.setMode(RecordView.MODE_PROGRESS);
			} else {
				initMediaRecord();
				Alert.showMessage("录音时长不能少于1s");
			}
		}
	}

	public void initMediaPlay() {
		player = MediaPlayer.create(SendVoiceActivity.this, Uri
				.parse((new File(app.sdcardVoiceFolder, voiceFileName))
						.getAbsolutePath()));
		player.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				isStop = false;
			}
		});
		try {
			player.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initMediaRecord() {
		if (player != null) {
			player.release();
			player = null;
		}
		recordView.setDragEnable(false);
		recordView.setMode(RecordView.MODE_TIMER);
		recordView.resetTimer();
		if (voiceFileName != "") {
			File file = new File(app.sdcardVoiceFolder, voiceFileName);
			if (file.exists()) {
				file.delete();
			}
		}
	}
}
