package com.open.welinks.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnMapLoadedListener;
import com.amap.api.maps2d.AMap.OnMapScreenShotListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.ResponseHandler;
import com.open.welinks.BusinessCardActivity;
import com.open.welinks.ChatActivity;
import com.open.welinks.ExpressionManageActivity;
import com.open.welinks.GroupInfoActivity;
import com.open.welinks.GroupListActivity;
import com.open.welinks.GroupMemberManageActivity;
import com.open.welinks.ImageScanActivity;
import com.open.welinks.ImagesDirectoryActivity;
import com.open.welinks.LocationActivity;
import com.open.welinks.R;
import com.open.welinks.ShareMessageDetailActivity;
import com.open.welinks.customListener.AudioListener;
import com.open.welinks.customListener.MyOnClickListener;
import com.open.welinks.customListener.OnUploadLoadingListener;
import com.open.welinks.customView.ChatFaceView.OnFaceSeletedListener;
import com.open.welinks.customView.SmallBusinessCardPopView.OnUserCardListener;
import com.open.welinks.model.API;
import com.open.welinks.model.Constant;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Messages.Message;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.model.SubData;
import com.open.welinks.model.SubData.CardMessageContent;
import com.open.welinks.model.SubData.LocationMessageContent;
import com.open.welinks.model.SubData.SpecialGifMessageContent;
import com.open.welinks.model.SubData.VoiceMessageContent;
import com.open.welinks.model.TaskManageHolder;
import com.open.welinks.oss.UploadMultipart;
import com.open.welinks.utils.BaseDataUtils;
import com.open.welinks.utils.ExpressionUtil;
import com.open.welinks.utils.InputMethodManagerUtils;
import com.open.welinks.utils.SHA1;
import com.open.welinks.utils.StreamParser;
import com.open.welinks.view.ChatView;

public class ChatController {
	public Data data = Data.getInstance();
	public SubData subData = SubData.getInstance();
	public Parser parser = Parser.getInstance();
	public TaskManageHolder taskManageHolder = TaskManageHolder.getInstance();
	public InputMethodManagerUtils inputManager;
	public Gson gson = new Gson();
	public SHA1 sha1 = new SHA1();

	public SensorManager mSensorManager;
	public Sensor mSensor;

	public AMap mAMap;
	public LocationManagerProxy mLocationManagerProxy;
	public AMapLocationListener mAMapLocationListener;
	public OnMapScreenShotListener mOnMapScreenShotListener;

	public ChatView thisView;
	public ChatController thisController;
	public ChatActivity thisActivity;

	public MyOnClickListener mOnClickListener;
	public OnTouchListener mOnTouchListener;
	public OnItemClickListener mItemClickListener;
	public OnFaceSeletedListener mOnFaceSeletedListener;
	public OnUploadLoadingListener uploadLoadingListener;
	public OnFocusChangeListener mOnFocusChangeListener;
	public OnScrollListener mOnScrollListener;
	public AnimationListener mAnimationListener;
	public OnMapLoadedListener mOnMapLoadedListener;
	public TextWatcher mTextWatcher;
	public AudioListener mAudioListener;
	public MyGestureDetector mGestureDetector;
	public GestureDetector.SimpleOnGestureListener mSimpleOnGestureListener;
	public SensorEventListener mySensorListener;
	public OnUserCardListener mOnUserCardListener;

	public String key = "", type = "";
	public User user;

	public Map<String, Message> messagesMap;

	public VoiceTimerTask timerTask;
	public Timer voicePopTimer;
	public long voiceTime = 0, sensorLastTime = 0;
	public float sensorLastX, sensorLastY, sensorLastZ;

	public int showChatCounts;
	public int doubleTapCounts;

	public boolean sendRecording = true, continuePlay = false;

	public Handler handler;
	public Runnable chatContentRunnable;
	public Thread chatContentThread;

	public File tempPhotoFile;
	public String tempLocationKey = "";

	// handler
	public final int HANDLER_CHAT_NOTIFY = 0x11;
	public final int HANDLER_CHAT_HIDEVOICEPOP = 0x12;
	public final int HANDLER_CHAT_STARTPLAY = 0x13;
	public final int HANDLER_CHAT_STOPPLAY = 0x14;
	public final int HANDLER_CHAT_RECORDSTART = 0x15;
	// requestCode
	public int REQUESTCODE_ABLUM = 0x21;
	public int REQUESTCODE_TAKEPHONE = 0x22;
	public int REQUESTCODE_TAKEPHOTO = 0x23;
	public int REQUESTCODE_SHAREVIEW = 0x24;
	public int REQUESTCODE_EXPRESSIONMANAGE = 0x25;

	public ChatController(ChatActivity activity) {
		thisController = this;
		thisActivity = activity;
	}

	public void onCreate() {
		String key = thisActivity.getIntent().getStringExtra("id");
		if (key != null && !"".equals(key)) {
			this.key = key;
		}
		String type = thisActivity.getIntent().getStringExtra("type");
		if (type != null && !"".equals(type)) {
			this.type = type;
		}
	}

	public void initData() {
		showChatCounts = 20;
		user = data.userInformation.currentUser;
		messagesMap = new HashMap<String, Message>();
		inputManager = new InputMethodManagerUtils(thisActivity);
		mLocationManagerProxy = LocationManagerProxy.getInstance(thisActivity);
		mSensorManager = (SensorManager) thisActivity.getSystemService(Service.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		initListeners();
	}

	@SuppressLint("HandlerLeak")
	public void initListeners() {
		mOnClickListener = new MyOnClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onClickEffective(View view) {
				if (view.getTag(R.id.tag_first) != null) {
					String contentType = (String) view.getTag(R.id.tag_first);
					if ("voice".equals(contentType)) {
						thisView.changeVoice(view);
					} else if ("image".equals(contentType)) {
						data.tempData.selectedImageList = (ArrayList<String>) view.getTag(R.id.tag_second);
						Intent intent = new Intent(thisActivity, ImageScanActivity.class);
						intent.putExtra("position", String.valueOf(0));
						thisActivity.startActivity(intent);
					} else if ("share".equals(contentType)) {
						String gid = (String) view.getTag(R.id.tag_second);
						String gsid = (String) view.getTag(R.id.tag_third);
						String sid = (String) view.getTag(R.id.tag_fourth);
						if (gid.matches("[\\d]+") && gsid.matches("[\\d]+")) {
							Intent intent = new Intent(thisActivity, ShareMessageDetailActivity.class);
							intent.putExtra("gid", gid);
							intent.putExtra("sid", sid);
							intent.putExtra("gsid", gsid);
							thisActivity.startActivity(intent);
						} else {
							Toast.makeText(thisActivity, "群分享不存在", Toast.LENGTH_SHORT).show();
						}
					} else if ("location".equals(contentType)) {
						Intent intent = new Intent(thisActivity, LocationActivity.class);
						intent.putExtra("latitude", (String) view.getTag(R.id.tag_second));
						intent.putExtra("longitude", (String) view.getTag(R.id.tag_third));
						thisActivity.startActivity(intent);
					} else if ("card".equals(contentType)) {
						Intent intent = new Intent(thisActivity, BusinessCardActivity.class);
						intent.putExtra("type", (String) view.getTag(R.id.tag_second));
						intent.putExtra("key", (String) view.getTag(R.id.tag_third));
						thisActivity.startActivity(intent);
					} else if ("resend".equals(contentType)) {
						int position = (Integer) view.getTag(R.id.tag_second);
						List<Message> messages = thisView.mChatAdapter.messages;
						if (messages.size() > position) {
							Message message = messages.get(position);
							message.status = "sending";
							message.time = String.valueOf(System.currentTimeMillis());
							thisView.mChatAdapter.notifyDataSetChanged();
							sendMessage(message);
						}
					}
				} else if (thisView.backView.equals(view)) {
					thisActivity.finish();
				} else if (thisView.titleImage.equals(view)) {
					thisView.changeChatMenu();
				} else if (thisView.chatAdd.equals(view)) {
					thisView.changeChatAdd();
				} else if (thisView.chatSmily.equals(view)) {
					thisView.changeChatSmily();
				} else if (thisView.chatRecord.equals(view)) {
					thisView.changeChatRecord();
				} else if (thisView.chatInput.equals(view)) {
					thisView.changeChatInput();
				} else if (thisView.chatSend.equals(view)) {
					createTextMessage();
				} else if (thisView.takePhoto.equals(view)) {
					takePhoto();
				} else if (thisView.ablum.equals(view)) {
					data.tempData.selectedImageList = null;
					thisActivity.startActivityForResult(new Intent(thisActivity, ImagesDirectoryActivity.class), REQUESTCODE_ABLUM);
				} else if (thisView.location.equals(view)) {
					requestLocation();
				} else if (thisView.chatContent.equals(view)) {
					if (thisController.inputManager.isActive(thisView.chatInput))
						thisController.inputManager.hide(thisView.chatInput);
				} else if (thisView.chatMenuBackground.equals(view)) {
					thisView.changeChatMenu();
				} else if (thisView.chatContentHeaderView.equals(view)) {
					if (showChatCounts < thisView.mChatAdapter.messages.size()) {
						showChatCounts += 10;
						thisView.mChatAdapter.notifyDataSetChanged();
						if (showChatCounts < thisView.mChatAdapter.messages.size()) {
							thisView.chatContent.setSelection(11);
						} else {
							thisView.chatContent.setSelection(thisView.mChatAdapter.messages.size() - showChatCounts + 11);
							thisView.chatContentHeaderView.setVisibility(View.GONE);
						}
					}
				}
			}
		};
		mSimpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {

			@Override
			public boolean onDown(MotionEvent e) {
				return true;
			}

			@Override
			public boolean onDoubleTap(MotionEvent event) {
				View view = mGestureDetector.getTouchView();
				if (thisView.chatContent.equals(view)) {
					createGifMessage("poke");
				} else {
					String contentType = (String) view.getTag(R.id.tag_first);
					if (contentType != null) {
						String phone = (String) view.getTag(R.id.tag_second);
						if (phone != null && !phone.equals(data.userInformation.currentUser.phone))
							createSpecialGifMessage(phone, thisActivity.getString(R.string.specialGifMessageString));
					}
				}
				return true;
			}

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				View view = mGestureDetector.getTouchView();
				if (thisView.chatContent.equals(view)) {
					if (thisController.inputManager.isActive(thisView.chatInput))
						thisController.inputManager.hide(thisView.chatInput);
				} else {
					String contentType = (String) view.getTag(R.id.tag_first);
					if (contentType != null) {
						if ("head".equals(contentType)) {
							String phone = (String) view.getTag(R.id.tag_second);
							thisView.businessCardPopView.cardView.setSmallBusinessCardContent(thisView.businessCardPopView.cardView.TYPE_POINT, phone);
							thisView.businessCardPopView.cardView.setMenu(false);
							thisView.businessCardPopView.showUserCardDialogView();
							mSensorManager.registerListener(mySensorListener, mSensor, SensorManager.SENSOR_DELAY_GAME);
						}
					}
				}
				return true;
			}

		};
		mGestureDetector = new MyGestureDetector(thisActivity, mSimpleOnGestureListener);
		mOnTouchListener = new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (thisView.voiceLayout.equals(view)) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						taskManageHolder.audioHandler.startRecording();
					} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
						float x = event.getRawX(), y = event.getRawY(), x1 = thisView.voicePop.getX(), y1 = thisView.voicePop.getY(), x2 = x1 + thisView.voicePop.getWidth(), y2 = y1 + thisView.voicePop.getHeight();
						if (x > x1 && x < x2 && y < y2 && y > y1) {
							if (sendRecording) {
								sendRecording = false;
								thisView.changeVoice(sendRecording);
							}
						} else {
							if (!sendRecording) {
								sendRecording = true;
								thisView.changeVoice(sendRecording);
							}
						}

					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						completeVoiceRecording(sendRecording);
					} else if (event.getAction() == MotionEvent.ACTION_UP) {

					}
				} else if (thisView.chatContent.equals(view) || "head".equals(view.getTag(R.id.tag_first))) {
					return mGestureDetector.onTouchEvent(event, view);
				}
				return false;
			}
		};

		mySensorListener = new SensorEventListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onSensorChanged(SensorEvent event) {
				if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
					long currentTime = System.currentTimeMillis();
					long time = currentTime - sensorLastTime;
					float x, y, z, speed;
					if (time > 100) {
						sensorLastTime = currentTime;
						x = event.values[SensorManager.DATA_X];
						y = event.values[SensorManager.DATA_Y];
						z = event.values[SensorManager.DATA_Z];
						speed = Math.abs(x - sensorLastX) / time * 10000;
						sensorLastX = x;
						sensorLastY = y;
						sensorLastZ = z;
						if (speed > 1500) {
							thisView.businessCardPopView.showList();
						}
					}
				}
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {

			}
		};

		mOnUserCardListener = thisView.businessCardPopView.new OnUserCardListener() {
			@Override
			public void onDismiss() {
				mSensorManager.unregisterListener(mySensorListener, mSensor);
			}

			@Override
			public void onItemSeleted(String string, String phone) {
				createSpecialGifMessage(phone, string);
			}
		};

		mOnScrollListener = new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (thisView.chatContent.getFirstVisiblePosition() == 0) {
					if (showChatCounts >= thisView.mChatAdapter.messages.size()) {
						thisView.chatContentHeaderView.setVisibility(View.GONE);
					} else {
						thisView.chatContentHeaderView.setVisibility(View.VISIBLE);
					}
				} else if (thisView.chatContent.getLastVisiblePosition() == (thisView.chatContent.getCount() - 1)) {

				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

			}
		};
		mItemClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String tag = (String) view.getTag();
				if (tag.equals(thisActivity.getString(R.string.personalDetails))) {
					Intent intent = new Intent(thisActivity, BusinessCardActivity.class);
					intent.putExtra("key", key);
					intent.putExtra("type", type);
					thisActivity.startActivity(intent);
				} else if (tag.equals(thisActivity.getString(R.string.groupDetails))) {
					Intent intent = new Intent(thisActivity, BusinessCardActivity.class);
					intent.putExtra("key", key);
					intent.putExtra("type", type);
					thisActivity.startActivity(intent);
				} else if (tag.equals(thisActivity.getString(R.string.sendCard))) {
					Intent intent = new Intent(thisActivity, GroupListActivity.class);
					intent.putExtra("type", "sendCard");
					thisActivity.startActivityForResult(intent, REQUESTCODE_SHAREVIEW);
				} else if (tag.equals(thisActivity.getString(R.string.setting))) {
					Intent intent = new Intent(thisActivity, GroupInfoActivity.class);
					intent.putExtra("gid", key);
					thisActivity.startActivity(intent);
				} else if (tag.equals(thisActivity.getString(R.string.groupMembers))) {
					Intent intent = new Intent(thisActivity, GroupMemberManageActivity.class);
					intent.putExtra("gid", key);
					intent.putExtra("type", 2);
					thisActivity.startActivity(intent);
				}
				thisView.titleImage.performClick();
			}
		};
		mTextWatcher = new TextWatcher() {
			String content = "";

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				content = s.toString();
			}

			@Override
			public void afterTextChanged(Editable s) {
				if ("".equals(s.toString())) {
					thisView.chatSend.setVisibility(View.GONE);
					thisView.chatRecord.setVisibility(View.VISIBLE);
				} else {
					thisView.chatSend.setVisibility(View.VISIBLE);
					thisView.chatRecord.setVisibility(View.GONE);
					int selectionIndex = thisView.chatInput.getSelectionStart();
					String afterContent = s.toString();
					if (!afterContent.equals(content)) {
						SpannableString spannableString = ExpressionUtil.getExpressionString(thisActivity, afterContent, Constant.FACEREGX);
						thisView.chatInput.setText(spannableString);
						thisView.chatInput.setSelection(selectionIndex);
					}
				}
			}
		};
		mAnimationListener = new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				if (animation.equals(thisView.chatContentAddOutTranslateAnimation)) {

				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (animation.equals(thisView.samilyInTranslateAnimation)) {

				} else if (animation.equals(thisView.samilyOutTranslateAnimation)) {
					thisView.chatBottomLayout.clearAnimation();
					thisView.faceLayout.setVisibility(View.GONE);
				} else if (animation.equals(thisView.addInTranslateAnimation)) {

				} else if (animation.equals(thisView.addOutTranslateAnimation)) {
					thisView.chatBottomLayout.clearAnimation();
					thisView.chatAddLayout.setVisibility(View.GONE);
				} else if (animation.equals(thisView.chatContentAddInTranslateAnimation)) {
					// thisView.chatContent.getLayoutParams().height = (int) (data.baseData.appHeight - BaseDataUtils.dpToPx(206.5f));
					thisView.chatContent.setSelection(thisView.chatContent.getBottom());
					thisView.chatContent.clearAnimation();
				} else if (animation.equals(thisView.chatContentAddOutTranslateAnimation)) {
					thisView.chatContent.clearAnimation();
				} else if (animation.equals(thisView.chatContentSamilyInTranslateAnimation)) {
					thisView.chatContent.clearAnimation();

				} else if (animation.equals(thisView.chatContentSamilyOutTranslateAnimation)) {
					thisView.chatContent.clearAnimation();
					// chatContentAddInRotateAnimation thisView.chatContent.getLayoutParams().height = (int) (thisController.data.baseData.appHeight - BaseDataUtils.dpToPx(106));
				} else if (animation.equals(thisView.chatContentAddInRotateAnimation)) {
					thisView.chatAdd.clearAnimation();
					thisView.chatAdd.setImageDrawable(thisActivity.getResources().getDrawable(R.drawable.selector_chat_return));
					thisView.chatAddLayout.setVisibility(View.VISIBLE);
					thisView.changeChatList();
				} else if (animation.equals(thisView.chatContentAddOutRotateAnimation)) {
					thisView.chatAdd.clearAnimation();
					thisView.chatAdd.setImageDrawable(thisActivity.getResources().getDrawable(R.drawable.selector_chat_add));
					thisView.chatAddLayout.setVisibility(View.GONE);
				}
			}
		};

		mOnFaceSeletedListener = thisView.faceLayout.new OnFaceSeletedListener() {

			@Override
			public void onFaceSeleted(String faceName) {
				if ("delete".equals(faceName)) {
					int start2 = thisView.chatInput.getSelectionStart();
					String content2 = thisView.chatInput.getText().toString();
					if (start2 - 1 < 0)
						return;
					String faceEnd2 = content2.substring(start2 - 1, start2);
					if ("]".equals(faceEnd2)) {
						String str = content2.substring(0, start2);
						int index = str.lastIndexOf("[");
						if (index != -1) {
							String faceStr = content2.substring(index, start2);
							Pattern patten = Pattern.compile(Constant.FACEREGX, Pattern.CASE_INSENSITIVE);
							Matcher matcher = patten.matcher(faceStr);
							if (matcher.find()) {
								thisView.chatInput.setText(content2.substring(0, start2 - faceStr.length()) + content2.substring(start2));
								thisView.chatInput.setSelection(start2 - faceStr.length());
							} else {
								if (start2 - 1 >= 0) {
									thisView.chatInput.setText(content2.substring(0, start2 - 1) + content2.substring(start2));
									thisView.chatInput.setSelection(start2 - 1);
								}
							}
						}
					} else {
						if (start2 - 1 >= 0) {
							thisView.chatInput.setText(content2.substring(0, start2 - 1) + content2.substring(start2));
							thisView.chatInput.setSelection(start2 - 1);
						}
					}
				} else if (faceName.indexOf("[") != -1) {
					thisView.chatInput.setText(thisView.chatInput.getText() + faceName);
					thisView.chatInput.setSelection(thisView.chatInput.getText().length());
				} else {
					createGifMessage(faceName);
				}
			}

			@Override
			public void onFaceManagerSeleted() {
				Intent intent = new Intent(thisActivity, ExpressionManageActivity.class);
				thisActivity.startActivityForResult(intent, REQUESTCODE_EXPRESSIONMANAGE);
				thisView.chatSmily.performClick();
			}

		};
		mOnFocusChangeListener = new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				if (view.equals(thisView.chatInput) && hasFocus) {
					thisView.changeChatInput();
				}
			}
		};
		uploadLoadingListener = new OnUploadLoadingListener() {
			@Override
			public void onSuccess(UploadMultipart instance, int time) {
				Message message = null;
				String fileName = "";
				if (instance.view.getTag(R.id.tag_third) != null) {
					int current = 0, total = 0;
					fileName = (String) instance.view.getTag(R.id.tag_first);
					total = (Integer) instance.view.getTag(R.id.tag_second);
					current = (Integer) instance.view.getTag(R.id.tag_third);
					if (total == ++current) {
						message = messagesMap.remove(fileName);
					} else {
						instance.view.setTag(R.id.tag_third, current);
					}
				} else {
					fileName = (String) instance.view.getTag();
					message = messagesMap.remove(fileName);
				}
				if (message != null) {
					sendMessage(message);
				}
			}
		};
		mOnMapScreenShotListener = new OnMapScreenShotListener() {

			@Override
			public void onMapScreenShot(Bitmap bitmap) {
				try {
					View view = new View(thisActivity);
					Message message = messagesMap.remove(tempLocationKey);
					bitmap = Bitmap.createBitmap(bitmap, (int) BaseDataUtils.dpToPx(65), (int) BaseDataUtils.dpToPx(40), (int) BaseDataUtils.dpToPx(150), (int) BaseDataUtils.dpToPx(100));
					File toFile = new File(taskManageHolder.fileHandler.sdcardImageFolder, tempLocationKey + ".png");
					FileOutputStream fos;
					fos = new FileOutputStream(toFile);
					bitmap.compress(CompressFormat.PNG, 100, fos);
					Map<String, Object> map = processImagesInformation(toFile.getAbsolutePath());
					String fileName = (String) map.get("fileName");
					File fromFile = new File(taskManageHolder.fileHandler.sdcardImageFolder, fileName);
					if (message != null) {
						LocationMessageContent messageContent = gson.fromJson(message.content, LocationMessageContent.class);
						messageContent.imageFileName = fileName;
						message.content = gson.toJson(messageContent);
						data.messages.isModified = true;
						thisView.mChatAdapter.notifyDataSetChanged();
					}
					messagesMap.put(fileName, message);
					view.setTag(fileName);
					UploadMultipart multipart = uploadFile(fromFile.getAbsolutePath(), fileName, (byte[]) map.get("bytes"), view, UploadMultipart.UPLOAD_TYPE_IMAGE);
					taskManageHolder.uploadMultipartList.addMultipart(multipart);
					tempLocationKey = "";
					toFile.delete();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		};
		mOnMapLoadedListener = new OnMapLoadedListener() {

			@Override
			public void onMapLoaded() {

			}
		};
		mAMapLocationListener = new AMapLocationListener() {

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
			}

			@Override
			public void onProviderEnabled(String provider) {
			}

			@Override
			public void onProviderDisabled(String provider) {
			}

			@Override
			public void onLocationChanged(Location location) {
			}

			@Override
			public void onLocationChanged(AMapLocation mAMapLocation) {
				if (mAMapLocation != null && mAMapLocation.getAMapException().getErrorCode() == 0) {
					mLocationManagerProxy.removeUpdates(mAMapLocationListener);
					mLocationManagerProxy.destroy();
					Double geoLat = mAMapLocation.getLatitude();
					Double geoLng = mAMapLocation.getLongitude();
					LatLng latLonPoint = new LatLng(geoLat, geoLng);
					mAMap.clear();
					mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLonPoint, 17));
					mAMap.invalidate();
					MarkerOptions markOptions = new MarkerOptions();
					markOptions.position(latLonPoint);
					mAMap.addMarker(markOptions);
					mAMap.setOnMapLoadedListener(mOnMapLoadedListener);
					Message message = messagesMap.get(tempLocationKey);
					if (message != null) {
						LocationMessageContent messageContent = subData.new LocationMessageContent();
						messageContent.latitude = String.valueOf(geoLat);
						messageContent.longitude = String.valueOf(geoLng);
						message.content = gson.toJson(messageContent);
					}
					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					mAMap.getMapScreenShot(mOnMapScreenShotListener);
				} else {
				}
			}
		};
		mAudioListener = new AudioListener() {

			@Override
			public void onRecording(int volume) {
				if (sendRecording) {
					if (volume >= 0 && volume <= 10) {
						thisView.changeVoice(R.drawable.image_chat_voice_talk_1);
					} else if (volume > 10 && volume <= 20) {
						thisView.changeVoice(R.drawable.image_chat_voice_talk_2);
					} else if (volume > 20 && volume <= 30) {
						thisView.changeVoice(R.drawable.image_chat_voice_talk_3);
					} else {
						thisView.changeVoice(R.drawable.image_chat_voice_talk_4);
					}
				}
			}

			@Override
			public void onPlayFail() {
				thisView.showVoiceMoive(false);
			}

			@Override
			public void onPlayComplete() {
				if (continuePlay) {
					continuePlay = false;
					taskManageHolder.audioHandler.startPlay((String) thisView.currentVoiceView.getTag(R.id.tag_second), (String) thisView.currentVoiceView.getTag(R.id.tag_third));
					thisController.postHandler(HANDLER_CHAT_STARTPLAY);
					thisView.showVoiceMoive(true);
				} else {
					thisController.postHandler(HANDLER_CHAT_STOPPLAY);
					thisView.showVoiceMoive(false);
				}
			}

			@Override
			public void onPrepared() {
			}

			@Override
			public void onRecordStarted() {
				if (voicePopTimer == null) {
					voicePopTimer = new Timer();
					timerTask = new VoiceTimerTask();
					voiceTime = System.currentTimeMillis();
					voicePopTimer.schedule(timerTask, 0, 1000);
				}
				sendRecording = true;
				postHandler(HANDLER_CHAT_RECORDSTART);
			}

			@Override
			public void onRecorded(String filePath) {
				postHandler(HANDLER_CHAT_HIDEVOICEPOP);
				if (!"".equals(filePath)) {
					createVoiceMessage(filePath);
				}
			}

			@Override
			public void onRecordFail() {
				postHandler(HANDLER_CHAT_HIDEVOICEPOP);
			}
		};
		handler = new Handler() {
			@Override
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case HANDLER_CHAT_NOTIFY:
					thisView.mChatAdapter.notifyDataSetChanged();
					break;
				case HANDLER_CHAT_HIDEVOICEPOP:
					thisView.voicePop.setVisibility(View.GONE);
					thisView.voicePopImage.setImageResource(R.drawable.image_chat_voice_talk);
					if (voicePopTimer != null) {
						voicePopTimer.cancel();
						voicePopTimer.purge();
						voicePopTimer = null;
					}
					voiceTime = 0;
					break;
				case HANDLER_CHAT_STARTPLAY:
					thisView.currentVoiceView.findViewById(R.id.voiceGif).setVisibility(View.VISIBLE);
					break;
				case HANDLER_CHAT_STOPPLAY:
					thisView.currentVoiceView.findViewById(R.id.voiceGif).setVisibility(View.INVISIBLE);
					break;
				case HANDLER_CHAT_RECORDSTART:
					thisView.voicePopTime.setText(thisActivity.getText(R.string.seconds));
					thisView.voicePop.setVisibility(View.VISIBLE);
					break;
				}
				super.handleMessage(msg);
			}
		};
		bindEvent();
	}

	public void bindEvent() {
		thisView.backView.setOnClickListener(mOnClickListener);
		thisView.chatAdd.setOnClickListener(mOnClickListener);
		thisView.chatSend.setOnClickListener(mOnClickListener);
		thisView.chatSmily.setOnClickListener(mOnClickListener);
		thisView.chatRecord.setOnClickListener(mOnClickListener);
		thisView.titleImage.setOnClickListener(mOnClickListener);
		thisView.voiceLayout.setOnClickListener(mOnClickListener);
		thisView.takePhoto.setOnClickListener(mOnClickListener);
		thisView.ablum.setOnClickListener(mOnClickListener);
		thisView.location.setOnClickListener(mOnClickListener);
		thisView.chatInput.setOnClickListener(mOnClickListener);
		thisView.chatMenuBackground.setOnClickListener(mOnClickListener);

		thisView.chatContent.setOnScrollListener(mOnScrollListener);

		thisView.chatContent.setOnTouchListener(mOnTouchListener);
		thisView.voiceLayout.setOnTouchListener(mOnTouchListener);

		thisView.faceLayout.setOnFaceSeletedListener(mOnFaceSeletedListener);

		thisView.chatInput.addTextChangedListener(mTextWatcher);
		thisView.chatInput.setOnFocusChangeListener(mOnFocusChangeListener);
		thisView.chatMenu.setOnItemClickListener(mItemClickListener);

		taskManageHolder.audioHandler.setAudioListener(mAudioListener);
		mAMap.setOnMapLoadedListener(mOnMapLoadedListener);

		thisView.samilyInTranslateAnimation.setAnimationListener(mAnimationListener);
		thisView.samilyOutTranslateAnimation.setAnimationListener(mAnimationListener);
		thisView.addInTranslateAnimation.setAnimationListener(mAnimationListener);
		thisView.addOutTranslateAnimation.setAnimationListener(mAnimationListener);
		thisView.chatContentAddInTranslateAnimation.setAnimationListener(mAnimationListener);
		thisView.chatContentAddOutTranslateAnimation.setAnimationListener(mAnimationListener);
		thisView.chatContentSamilyInTranslateAnimation.setAnimationListener(mAnimationListener);
		thisView.chatContentSamilyOutTranslateAnimation.setAnimationListener(mAnimationListener);
		thisView.chatContentAddInRotateAnimation.setAnimationListener(mAnimationListener);
		thisView.chatContentAddOutRotateAnimation.setAnimationListener(mAnimationListener);

		thisView.businessCardPopView.setOnDismissListener(mOnUserCardListener);
	}

	private void completeVoiceRecording(boolean weather) {
		if (weather) {
			taskManageHolder.audioHandler.stopRecording();
		} else {
			postHandler(HANDLER_CHAT_HIDEVOICEPOP);
			taskManageHolder.audioHandler.releaseRecording();
		}
	}

	private void createTextMessage() {
		long time = new Date().getTime();
		String messageContent = thisView.chatInput.getText().toString().trim();
		thisView.chatInput.setText("");
		if ("".equals(messageContent))
			return;
		Message message = data.messages.new Message();
		message.content = messageContent;
		message.contentType = "text";
		message.phone = user.phone;
		message.nickName = user.nickName;
		message.time = String.valueOf(time);
		message.status = "sending";
		message.type = Constant.MESSAGE_TYPE_SEND;
		addMessageToLocation(message);
		sendMessage(message);
	}

	private void createImageMessage(ArrayList<String> selectedImageList) {
		long time = new Date().getTime();
		View view = new View(thisActivity);
		ArrayList<String> messageContent = new ArrayList<String>();
		view.setTag(R.id.tag_first, String.valueOf(time));
		view.setTag(R.id.tag_second, selectedImageList.size());
		view.setTag(R.id.tag_third, 0);
		Message message = data.messages.new Message();
		message.contentType = "image";
		message.phone = user.phone;
		message.nickName = user.nickName;
		message.time = String.valueOf(time);
		message.status = "sending";
		message.type = Constant.MESSAGE_TYPE_SEND;
		for (String filePath : selectedImageList) {
			Map<String, Object> map = processImagesInformation(filePath);
			String fileName = (String) map.get("fileName");
			messageContent.add(fileName);
			UploadMultipart multipart = uploadFile(filePath, fileName, (byte[]) map.get("bytes"), view, UploadMultipart.UPLOAD_TYPE_IMAGE);
			taskManageHolder.uploadMultipartList.addMultipart(multipart);
		}
		message.content = gson.toJson(messageContent);
		messagesMap.put(String.valueOf(time), message);
		addMessageToLocation(message);
	}

	private void createVoiceMessage(String filePath) {
		long time = new Date().getTime();
		View view = new View(thisActivity);
		String voiceTime = "", recordReadSize = "";
		String[] infomation = filePath.split("@");
		filePath = infomation[0];
		voiceTime = infomation[1];
		recordReadSize = infomation[2];
		Map<String, Object> map = processVoiceInformation(filePath);
		String fileName = (String) map.get("fileName");
		view.setTag(fileName);
		Message message = data.messages.new Message();
		VoiceMessageContent content = subData.new VoiceMessageContent();
		content.fileName = fileName;
		content.time = voiceTime;
		content.recordReadSize = recordReadSize;
		message.content = gson.toJson(content);
		message.contentType = "voice";
		message.phone = user.phone;
		message.nickName = user.nickName;
		message.time = String.valueOf(time);
		message.status = "sending";
		message.type = Constant.MESSAGE_TYPE_SEND;
		messagesMap.put(fileName, message);
		UploadMultipart multipart = uploadFile(filePath, fileName, (byte[]) map.get("bytes"), view, UploadMultipart.UPLOAD_TYPE_VOICE);
		taskManageHolder.uploadMultipartList.addMultipart(multipart);
		addMessageToLocation(message);
	}

	private void createGifMessage(String faceName) {
		long time = new Date().getTime();
		Message message = data.messages.new Message();
		message.content = faceName;
		message.contentType = "gif";
		message.phone = user.phone;
		message.nickName = user.nickName;
		message.time = String.valueOf(time);
		message.status = "sending";
		message.type = Constant.MESSAGE_TYPE_SEND;
		addMessageToLocation(message);
		sendMessage(message);
	}

	private void createLocationMessage() {
		long time = new Date().getTime();
		tempLocationKey = String.valueOf(time);
		Message message = data.messages.new Message();
		message.content = "";
		message.contentType = "location";
		message.phone = user.phone;
		message.nickName = user.nickName;
		message.time = String.valueOf(time);
		message.status = "sending";
		message.type = Constant.MESSAGE_TYPE_SEND;
		messagesMap.put(tempLocationKey, message);
		addMessageToLocation(message);
	}

	private void createSpecialGifMessage(String phone, String content) {
		long time = new Date().getTime();
		SpecialGifMessageContent messageContent = subData.new SpecialGifMessageContent();
		messageContent.phone = phone;
		messageContent.content = content;
		Message message = data.messages.new Message();
		message.content = gson.toJson(messageContent);
		message.contentType = "specialGif";
		message.phone = user.phone;
		message.nickName = user.nickName;
		message.time = String.valueOf(time);
		message.status = "sending";
		message.type = Constant.MESSAGE_TYPE_SEND;
		addMessageToLocation(message);
		sendMessage(message);
	}

	private void takePhoto() {
		tempPhotoFile = new File(taskManageHolder.fileHandler.sdcardImageFolder, "tempimage.jpg");
		int i = 1;
		while (tempPhotoFile.exists()) {
			tempPhotoFile = new File(taskManageHolder.fileHandler.sdcardImageFolder, "tempimage" + (i++) + ".jpg");
		}
		Uri uri = Uri.fromFile(tempPhotoFile);
		Intent tackPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		tackPicture.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
		tackPicture.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		thisActivity.startActivityForResult(tackPicture, REQUESTCODE_TAKEPHOTO);
	}

	@SuppressLint("ShowToast")
	private void requestLocation() {
		if ("".equals(tempLocationKey)) {
			createLocationMessage();
			mLocationManagerProxy.setGpsEnable(true);
			mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 10, mAMapLocationListener);
		} else {
			long lastTime = Long.valueOf(tempLocationKey);
			long time = System.currentTimeMillis();
			if ((time - lastTime) / 1000 > 30) {
				Message message = messagesMap.remove(tempLocationKey);
				tempLocationKey = String.valueOf(time);
				messagesMap.put(tempLocationKey, message);
				mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 10, mAMapLocationListener);
			} else {
				Toast.makeText(thisActivity, R.string.locationing, Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void addMessageToLocation(final Message message) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				List<String> messagesOrder = data.messages.messagesOrder;
				String orderKey = "";
				if ("point".equals(type)) {
					orderKey = "p" + key;
					if (messagesOrder.contains(orderKey)) {
						messagesOrder.remove(orderKey);
					}
					messagesOrder.add(0, orderKey);
					message.sendType = "point";
					message.phoneto = "[\"" + key + "\"]";
					Map<String, ArrayList<Message>> friendMessageMap = data.messages.friendMessageMap;
					if (friendMessageMap == null) {
						friendMessageMap = new HashMap<String, ArrayList<Message>>();
						data.messages.friendMessageMap = friendMessageMap;
					}
					ArrayList<Message> messages = friendMessageMap.get(orderKey);
					if (messages == null) {
						messages = new ArrayList<Message>();
						friendMessageMap.put(orderKey, messages);
					}
					messages.add(message);
				} else if ("group".equals(type)) {
					orderKey = "g" + key;
					if (messagesOrder.contains(orderKey)) {
						messagesOrder.remove(orderKey);
					}
					messagesOrder.add(0, orderKey);
					message.gid = key;
					message.sendType = "group";

					message.phoneto = data.relationship.groupsMap.get(key).members.toString();
					Map<String, ArrayList<Message>> groupMessageMap = data.messages.groupMessageMap;
					if (groupMessageMap == null) {
						groupMessageMap = new HashMap<String, ArrayList<Message>>();
						data.messages.groupMessageMap = groupMessageMap;
					}
					ArrayList<Message> messages = groupMessageMap.get(orderKey);
					if (messages == null) {
						messages = new ArrayList<Message>();
						groupMessageMap.put(orderKey, messages);
					}
					messages.add(message);
				}
				data.messages.isModified = true;
				postHandler(HANDLER_CHAT_NOTIFY);
			}
		}).start();

	}

	private void createCardMessage(final String key, final String type) {
		new Thread() {
			public void run() {
				List<String> messagesOrder = data.messages.messagesOrder;
				String orderKey = "";
				long time = new Date().getTime();
				Message message = data.messages.new Message();
				message.contentType = "card";
				message.phone = user.phone;
				message.nickName = user.nickName;
				message.time = String.valueOf(time);
				message.status = "sending";
				message.type = Constant.MESSAGE_TYPE_SEND;
				CardMessageContent messageContent = subData.new CardMessageContent();
				messageContent.key = thisController.key;
				messageContent.type = thisController.type;
				if ("point".equals(thisController.type)) {
					Friend friend = data.relationship.friendsMap.get(thisController.key);
					if (friend != null) {
						messageContent.head = friend.head;
						messageContent.name = friend.nickName;
						messageContent.mainBusiness = friend.mainBusiness;
					}
				} else if ("group".equals(thisController.type)) {
					Group group = data.relationship.groupsMap.get(thisController.key);
					if (group != null) {
						messageContent.head = group.icon;
						messageContent.name = group.name;
						messageContent.mainBusiness = group.description;
					}
				}
				message.content = gson.toJson(messageContent);
				if ("point".equals(type)) {
					orderKey = "p" + key;
					if (messagesOrder.contains(orderKey)) {
						messagesOrder.remove(orderKey);
					}
					messagesOrder.add(0, orderKey);
					message.sendType = "point";
					message.phoneto = "[\"" + key + "\"]";

					Map<String, ArrayList<Message>> friendMessageMap = data.messages.friendMessageMap;
					if (friendMessageMap == null) {
						friendMessageMap = new HashMap<String, ArrayList<Message>>();
						data.messages.friendMessageMap = friendMessageMap;
					}
					ArrayList<Message> messages = friendMessageMap.get(orderKey);
					if (messages == null) {
						messages = new ArrayList<Message>();
						friendMessageMap.put(orderKey, messages);
					}
					messages.add(message);
				} else if ("group".equals(type)) {
					orderKey = "g" + key;
					if (messagesOrder.contains(orderKey)) {
						messagesOrder.remove(orderKey);
					}
					messagesOrder.add(0, orderKey);
					message.gid = key;
					message.sendType = "group";

					message.phoneto = data.relationship.groupsMap.get(key).members.toString();
					Map<String, ArrayList<Message>> groupMessageMap = data.messages.groupMessageMap;
					if (groupMessageMap == null) {
						groupMessageMap = new HashMap<String, ArrayList<Message>>();
						data.messages.groupMessageMap = groupMessageMap;
					}
					ArrayList<Message> messages = groupMessageMap.get(orderKey);
					if (messages == null) {
						messages = new ArrayList<Message>();
						groupMessageMap.put(orderKey, messages);
					}
					messages.add(message);
				}
				data.messages.isModified = true;
				HttpUtils httpUtils = new HttpUtils();
				RequestParams params = new RequestParams();

				params.addBodyParameter("phone", user.phone);
				params.addBodyParameter("accessKey", user.accessKey);
				params.addBodyParameter("sendType", type);
				params.addBodyParameter("contentType", message.contentType);
				params.addBodyParameter("content", message.content);
				params.addBodyParameter("time", message.time);
				if ("group".equals(type)) {
					Group group = data.relationship.groupsMap.get(key);
					if (group == null) {
						group = data.relationship.new Group();
					}
					params.addBodyParameter("gid", key);
					params.addBodyParameter("phoneto", gson.toJson(group.members));
				} else if ("point".equals(type)) {
					List<String> phoneto = new ArrayList<String>();
					phoneto.add(key);
					params.addBodyParameter("phoneto", gson.toJson(phoneto));
					params.addBodyParameter("gid", "");
				}

				ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
				httpUtils.send(HttpMethod.POST, API.MESSAGE_SEND, params, responseHandlers.message_sendMessageCallBack);
			};
		}.start();
	}

	private void sendMessage(Message message) {
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();

		params.addBodyParameter("phone", user.phone);
		params.addBodyParameter("accessKey", user.accessKey);
		params.addBodyParameter("sendType", type);
		params.addBodyParameter("contentType", message.contentType);
		params.addBodyParameter("content", message.content);
		params.addBodyParameter("time", message.time);
		if ("group".equals(type)) {
			Group group = data.relationship.groupsMap.get(key);
			if (group == null) {
				group = data.relationship.new Group();
			}
			params.addBodyParameter("gid", key);
			params.addBodyParameter("phoneto", gson.toJson(group.members));
		} else if ("point".equals(type)) {
			List<String> phoneto = new ArrayList<String>();
			phoneto.add(key);
			params.addBodyParameter("phoneto", gson.toJson(phoneto));
			params.addBodyParameter("gid", "");
		}

		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
		httpUtils.send(HttpMethod.POST, API.MESSAGE_SEND, params, responseHandlers.message_sendMessageCallBack);
	}

	public Map<String, Object> processVoiceInformation(String filePath) {
		Map<String, Object> map = new HashMap<String, Object>();
		String suffixName = ".osa";
		String fileName = "";
		File fromFile = new File(filePath);
		byte[] bytes = StreamParser.parseToByteArray(fromFile);
		map.put("bytes", bytes);
		String sha1FileName = sha1.getDigestOfString(bytes);
		fileName = sha1FileName + suffixName;
		map.put("fileName", fileName);
		File toFile = new File(taskManageHolder.fileHandler.sdcardVoiceFolder, fileName);
		fromFile.renameTo(toFile);
		return map;
	}

	public Map<String, Object> processImagesInformation(String filePath) {
		Map<String, Object> map = new HashMap<String, Object>();
		String suffixName = filePath.substring(filePath.lastIndexOf("."));
		if (suffixName.equals(".jpg") || suffixName.equals(".jpeg")) {
			suffixName = ".osj";
		} else if (suffixName.equals(".png")) {
			suffixName = ".osp";
		}
		String fileName = "";
		File fromFile = new File(filePath);
		byte[] bytes = taskManageHolder.fileHandler.getImageFileBytes(null, fromFile, (int) data.baseData.screenWidth, (int) data.baseData.screenHeight);
		map.put("bytes", bytes);
		String sha1FileName = sha1.getDigestOfString(bytes);
		fileName = sha1FileName + suffixName;
		map.put("fileName", fileName);
		File toFile = new File(taskManageHolder.fileHandler.sdcardImageFolder, fileName);
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(toFile);
			StreamParser.parseToFile(bytes, fileOutputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		File toSnapFile = new File(taskManageHolder.fileHandler.sdcardThumbnailFolder, fileName);
		taskManageHolder.fileHandler.makeImageThumbnail(fromFile, (int) BaseDataUtils.dpToPx(178), (int) BaseDataUtils.dpToPx(146), toSnapFile, fileName);
		return map;
	}

	private UploadMultipart uploadFile(String filePath, String fileName, byte[] bytes, View view, int type) {
		UploadMultipart multipart = new UploadMultipart(filePath, fileName, bytes, type);
		multipart.view = view;
		multipart.setUploadLoadingListener(uploadLoadingListener);
		return multipart;
	}

	public void postHandler(int what) {
		android.os.Message msg = new android.os.Message();
		msg.what = what;
		handler.sendMessage(msg);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == REQUESTCODE_ABLUM && resultCode == Activity.RESULT_OK) {
			ArrayList<String> selectedImageList = data.tempData.selectedImageList;
			if (selectedImageList == null || selectedImageList.size() == 0) {
				return;
			}
			data.tempData.selectedImageList = null;
			createImageMessage(selectedImageList);
		} else if (requestCode == REQUESTCODE_TAKEPHOTO && resultCode == Activity.RESULT_OK) {
			String strRingPath = tempPhotoFile.getAbsolutePath();
			ArrayList<String> selectedImageList = new ArrayList<String>();
			selectedImageList.add(strRingPath);
			createImageMessage(selectedImageList);
			tempPhotoFile.delete();
		} else if (requestCode == REQUESTCODE_SHAREVIEW && resultCode == Activity.RESULT_OK) {
			String type = intent.getStringExtra("sendType");
			String key = intent.getStringExtra("key");
			if (!"".equals(key) && !"".equals(type)) {
				createCardMessage(key, type);
			}
		} else if (requestCode == REQUESTCODE_EXPRESSIONMANAGE) {
			thisView.faceLayout.nodifyChatFace();
		}
	}

	public void onDestroy() {
		taskManageHolder.audioHandler.releasePlyer();
		thisView.locationMapView.onDestroy();
	}

	public void onResume() {
		taskManageHolder.viewManage.chatView = thisView;
		thisView.locationMapView.onResume();

		if (thisView.businessCardPopView.isShowing()) {
			thisView.businessCardPopView.dismissUserCardDialogView();
		}
	}

	private class MyGestureDetector extends GestureDetector {
		private View view;

		public MyGestureDetector(Context context, OnGestureListener listener) {
			super(context, listener);
		}

		public boolean onTouchEvent(MotionEvent ev, View view) {
			this.view = view;
			return super.onTouchEvent(ev);
		}

		public View getTouchView() {
			return view;
		}
	}

	private class VoiceTimerTask extends TimerTask {

		@Override
		public void run() {
			thisView.changeVoice();
		}
	}

	public void finish() {
		String content = thisView.chatInput.getText().toString();
		if (!"".equals(content)) {
			Map<String, String> notSentMessagesMap = data.localStatus.localData.notSentMessagesMap;
			if (notSentMessagesMap == null) {
				notSentMessagesMap = new HashMap<String, String>();
				data.localStatus.localData.notSentMessagesMap = notSentMessagesMap;
			}
			notSentMessagesMap.put(type + key, content);
		}

		if ("point".equals(type)) {
			data.relationship.friendsMap.get(key).notReadMessagesCount = 0;
		} else if ("group".equals(type)) {
			data.relationship.groupsMap.get(key).notReadMessagesCount = 0;
		}
		data.relationship.isModified = true;
		taskManageHolder.viewManage.chatView = null;

		taskManageHolder.viewManage.messagesSubView.showMessagesSequence();
	}

	public void onPause() {
		thisView.locationMapView.onPause();
	}

	public void onBackPressed() {
		if (thisView.chatAddLayout.getVisibility() == View.VISIBLE) {
			thisView.chatAdd.performClick();
		} else if (thisView.faceLayout.getVisibility() == View.VISIBLE) {
			thisView.chatSmily.performClick();
		} else {
			thisActivity.finish();
		}

	}

	public void onSaveInstanceState(Bundle outState) {
		thisView.locationMapView.onSaveInstanceState(outState);
	}
}
