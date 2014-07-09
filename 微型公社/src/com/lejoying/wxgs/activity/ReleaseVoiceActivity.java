package com.lejoying.wxgs.activity;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.fragment.GroupShareFragment;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.view.RecoderVoiceView;
import com.lejoying.wxgs.activity.view.RecoderVoiceView.DeleteRecorderClickListener;
import com.lejoying.wxgs.activity.view.RecoderVoiceView.PlayButtonClickListener;
import com.lejoying.wxgs.activity.view.RecoderVoiceView.ProgressListener;
import com.lejoying.wxgs.activity.view.ReleaseEditText;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.activity.view.widget.Alert.OnLoadingCancelListener;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.handler.OSSFileHandler;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileMessageInfoInterface;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileMessageInfoSettings;
import com.lejoying.wxgs.app.handler.OSSFileHandler.ImageMessageInfo;
import com.lejoying.wxgs.app.handler.OSSFileHandler.UploadFileInterface;
import com.lejoying.wxgs.app.handler.OSSFileHandler.UploadFileSettings;

public class ReleaseVoiceActivity extends Activity implements OnClickListener,
		OnTouchListener {

	MainApplication app = MainApplication.getMainApplication();

	LayoutInflater mInflater;

	View rl_back, rl_send, rl_sync;
	GestureDetector backViewDetector;
	ReleaseEditText etContent;

	static int ISRECODER = 0x001;
	static int ISPALY = 0x002;
	int recoderStatus = ISRECODER;

	RecoderVoiceView recoderVoiceView;

	MediaRecorder mediaRecorder;
	MediaPlayer mediaPlayer;

	String fileName = "";
	long voiceTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.release_voice);
		mInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		initData();
		initLayout();
		initEvent();

	}

	void initData() {

	}

	void initLayout() {
		etContent = (ReleaseEditText) findViewById(R.id.release_et);
		rl_back = findViewById(R.id.rl_back);
		rl_send = findViewById(R.id.rl_send);
		rl_sync = findViewById(R.id.rl_sync);
		recoderVoiceView = (RecoderVoiceView) findViewById(R.id.recordVoiceView);
	}

	void initEvent() {
		recoderVoiceView.setMode(RecoderVoiceView.MODE_TIMER);
		recoderVoiceView
				.setPlayButtonClickListener(new PlayButtonClickListener() {

					@SuppressLint("InlinedApi")
					@Override
					public void onPlay() {
						if (recoderVoiceView.getMode() == RecoderVoiceView.MODE_TIMER) {
							// recoderVoiceView.resetTimer();
							recoderVoiceView.setDragEnable(false);
							recoderVoiceView.startTimer();
							File file = new File(app.sdcardVoiceFolder,
									fileName);
							if (file.exists()) {
								file.delete();
							}
							fileName = new Date().getTime() + ".aac";
							try {
								mediaRecorder = new MediaRecorder();
								mediaRecorder
										.setAudioSource(MediaRecorder.AudioSource.MIC);
								mediaRecorder
										.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
								mediaRecorder
										.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
								mediaRecorder.setOutputFile((new File(
										app.sdcardVoiceFolder, fileName))
										.getAbsolutePath());
								mediaRecorder.prepare();
								mediaRecorder.start();
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							recoderVoiceView.setDragEnable(true);
							// recoderVoiceView.setProgressTime(10000);
							recoderVoiceView.startProgress();
							mediaPlayer.start();
						}
					}

					@Override
					public void onPause() {
						if (recoderVoiceView.getMode() == RecoderVoiceView.MODE_TIMER) {
							recoderVoiceView
									.setMode(RecoderVoiceView.MODE_PROGRESS);
							recoderVoiceView.stopTimer();
							if (mediaRecorder != null) {
								mediaRecorder.stop();
							}
							File file = new File(app.sdcardVoiceFolder,
									fileName);
							if (file.exists()) {
								mediaPlayer = MediaPlayer.create(
										ReleaseVoiceActivity.this,
										Uri.parse(file.getAbsolutePath()));
								try {
									mediaPlayer.prepare();
								} catch (IllegalStateException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
								voiceTime = mediaPlayer.getDuration();
								recoderVoiceView.setProgressTime(voiceTime);
							}
						} else {
							recoderVoiceView.pauseProgress();
							if (mediaPlayer != null) {
								mediaPlayer.pause();
							}
						}
					}
				});
		recoderVoiceView.setProgressListener(new ProgressListener() {

			@Override
			public void onProgressEnd() {
				if (recoderVoiceView.getMode() == RecoderVoiceView.MODE_TIMER) {
					recoderVoiceView.stopTimer();
					recoderVoiceView.setMode(RecoderVoiceView.MODE_PROGRESS);
				} else {
					recoderVoiceView.stopProgress();
				}
			}

			@Override
			public void onDrag(float percent) {
				if (mediaPlayer != null
						&& recoderVoiceView.getMode() == RecoderVoiceView.MODE_PROGRESS) {
					mediaPlayer.seekTo((int) (mediaPlayer.getDuration() * percent));
				}

			}
		});
		recoderVoiceView
				.setDeleteRecorderClickListener(new DeleteRecorderClickListener() {

					@Override
					public void onDeleteRecoder() {
						fileName = "";
						if (recoderVoiceView.getMode() == RecoderVoiceView.MODE_TIMER) {
							if (mediaRecorder != null) {
								mediaRecorder.release();
							}
							recoderVoiceView.stopTimer();
							recoderVoiceView.setShowPlay(true);
						} else {
							if (recoderVoiceView.isStartProgress()) {
								recoderVoiceView.stopProgress();
								mediaPlayer.stop();
								mediaPlayer.release();
							}
						}
						File file = new File(app.sdcardVoiceFolder, fileName);
						if (file.exists()) {
							file.delete();
						}
						recoderVoiceView.setMode(RecoderVoiceView.MODE_TIMER);
						recoderVoiceView.setTimerTime(60000);
						recoderVoiceView.resetProgress();
						recoderVoiceView.resetTimer();
					}
				});
		rl_back.setOnTouchListener(this);
		rl_send.setOnClickListener(this);
		rl_sync.setOnClickListener(this);
		backViewDetector = new GestureDetector(ReleaseVoiceActivity.this,
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onDown(MotionEvent e) {
						return true;
					}

					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						finish();
						return true;
					}
				});
	}

	void Send() {
		sendVoiceTextMessage();
	}

	void sendVoiceTextMessage() {
		String messageContent = etContent.getText().toString().trim();
		if ("".equals(fileName) || "".equals(messageContent)) {
			Alert.showMessage("声文分享内容不完整");
			return;
		}
		Alert.showLoading(new OnLoadingCancelListener() {
			@Override
			public void loadingCancel() {
				System.out.println("loading ...send message");
			}
		});
		final JSONArray messageJsonArray = new JSONArray();
		JSONObject contentObject = new JSONObject();
		try {
			contentObject.put("type", "text");
			contentObject.put("detail", messageContent);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		messageJsonArray.put(contentObject);
		app.fileHandler.getFileMessageInfo(new FileMessageInfoInterface() {

			@Override
			public void setParams(FileMessageInfoSettings settings) {
				settings.FILE_TYPE = OSSFileHandler.FILE_TYPE_SDVOICE;
				settings.fileName = fileName;
				settings.folder = app.sdcardVoiceFolder;
			}

			@Override
			public void onSuccess(ImageMessageInfo imageMessageInfo) {
				File fromFile = new File(app.sdcardVoiceFolder, fileName);
				File toFile = new File(app.sdcardVoiceFolder,
						imageMessageInfo.fileName);
				if (fromFile.exists()) {
					fromFile.renameTo(toFile);
				}
				checkImage(imageMessageInfo, "audio/x-mei-aac",
						app.sdcardVoiceFolder.getAbsolutePath(), "voice",
						messageJsonArray);
			}
		});
	}

	public void checkImage(final ImageMessageInfo imageMessageInfo,
			final String contentType, final String path, final String fileType,
			final JSONArray selectedImages) {
		app.networkHandler.connection(new CommonNetConnection() {

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.IMAGE_CHECK;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("filename", imageMessageInfo.fileName);
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
				try {
					if (jData.getBoolean("exists")) {
						if ("image".equals(fileType)) {

						} else {
							JSONObject imageObject = new JSONObject();
							imageObject.put("type", fileType);
							imageObject
									.put("detail", imageMessageInfo.fileName);
							imageObject.put("time", voiceTime);
							selectedImages.put(imageObject);
							sendMessage("voicetext", selectedImages.toString());
						}
					} else {

						app.fileHandler.uploadFile(new UploadFileInterface() {

							@Override
							public void setParams(UploadFileSettings settings) {
								settings.imageMessageInfo = imageMessageInfo;
								settings.contentType = contentType;
								settings.fileName = imageMessageInfo.fileName;
								settings.path = path;
								if ("image".equals(fileType)) {
									settings.uploadFileType = OSSFileHandler.UPLOAD_FILE_TYPE_IMAGES;
								} else if ("voice".equals(fileType)) {
									settings.uploadFileType = OSSFileHandler.UPLOAD_FILE_TYPE_VOICES;
								}
							}

							@Override
							public void onSuccess(Boolean flag, String fileName) {
								if ("image".equals(fileType)) {

								} else {
									sendMessage("voicetext",
											selectedImages.toString());
								}
							}
						});
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	void sendMessage(final String contentType, final String content) {
		app.networkHandler.connection(new CommonNetConnection() {

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.SHARE_SEND;
				settings.params = generateMessageParams(contentType, content);
			}

			@Override
			public void success(JSONObject jData) {
				Alert.removeLoading();
				finish();
			}

			@Override
			protected void unSuccess(JSONObject jData) {
				Alert.removeLoading();
				super.unSuccess(jData);
			}
		});
	}

	public Map<String, String> generateMessageParams(String type, String content) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", app.data.user.phone);
		params.put("accessKey", app.data.user.accessKey);
		params.put("gid", GroupShareFragment.mCurrentGroupShareID);
		JSONObject messageObject = new JSONObject();
		try {
			messageObject.put("type", type);
			messageObject.put("content", content);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		params.put("message", messageObject.toString());
		return params;
	}

	void Sync() {

	}

	@Override
	protected void onPause() {
		if (mediaRecorder != null) {
			mediaRecorder.stop();
			recoderVoiceView.setShowPlay(true);
			recoderVoiceView.stopTimer();
			recoderVoiceView.resetTimer();
		}
		if (mediaPlayer != null) {
			mediaPlayer.pause();
		}
		super.onPause();
	}

	@Override
	public void finish() {
		if (mediaRecorder != null) {
			mediaRecorder.release();
		}
		if (mediaPlayer != null) {
			mediaPlayer.release();
		}

		super.finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_send:
			Send();
			break;
		case R.id.rl_sync:
			Sync();
			break;

		default:
			break;
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.rl_back:
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				rl_back.setBackgroundColor(Color.argb(143, 0, 0, 0));
				break;
			case MotionEvent.ACTION_UP:
				rl_back.setBackgroundColor(Color.argb(0, 0, 0, 0));
				break;
			}
			break;
		}
		return backViewDetector.onTouchEvent(event);
	}

}
