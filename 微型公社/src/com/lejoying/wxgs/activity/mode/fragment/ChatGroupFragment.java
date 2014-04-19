package com.lejoying.wxgs.activity.mode.fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.MainActivity;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.utils.ExpressionUtil;
import com.lejoying.wxgs.activity.utils.MCImageUtils;
import com.lejoying.wxgs.activity.view.SampleView;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.activity.view.widget.Alert.AlertInputDialog;
import com.lejoying.wxgs.activity.view.widget.Alert.AlertInputDialog.OnDialogClickListener;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.adapter.AnimationAdapter;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.data.entity.Group;
import com.lejoying.wxgs.app.data.entity.Message;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.handler.FileHandler.BigFaceImgInterface;
import com.lejoying.wxgs.app.handler.FileHandler.BigFaceImgSettings;
import com.lejoying.wxgs.app.handler.FileHandler.FileResult;
import com.lejoying.wxgs.app.handler.FileHandler.SaveBitmapInterface;
import com.lejoying.wxgs.app.handler.FileHandler.SaveSettings;
import com.lejoying.wxgs.app.handler.FileHandler.VoiceInterface;
import com.lejoying.wxgs.app.handler.FileHandler.VoiceSettings;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;

public class ChatGroupFragment extends BaseFragment {

	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;

	public static final int CHAT_FRIEND = 1;
	public static final int CHAT_GROUP = 2;

	public int mStatus;

	public Friend mNowChatFriend;
	public Group mNowChatGroup;

	private View mContent;
	public BaseAdapter mAdapter;

	public MediaRecorder recorder;
	public MediaPlayer mPlayer;
	public List<String> voice_list;
	public int play_order = 0;
	public double voice_length = 0;
	public long startTime = 0;

	int RESULT_SELECTPICTURE = 0x124;
	int RESULT_TAKEPICTURE = 0xa3;
	int RESULT_CATPICTURE = 0x3d;

	boolean VOICE_PLAYSTATUS = false;
	boolean VOICE_SAVESTATUS = false;

	LayoutInflater mInflater;

	Map<String, Bitmap> tempImages = new Hashtable<String, Bitmap>();

	View iv_send;
	View iv_more;
	View iv_more_select;
	EditText editText_message;
	RelativeLayout rl_chatbottom;
	RelativeLayout rl_message;
	RelativeLayout rl_select;
	RelativeLayout rl_audiopanel;
	View rl_selectpicture;
	View rl_makeaudio;
	TextView tv_voice;
	TextView tv_voice_start;
	ImageView iv_voice_send;
	ImageView iv_voice_play;
	TextView tv_voice_timelength;
	OnTouchListener mOnTouchListener;

	RelativeLayout rl_face;
	LinearLayout ll_facepanel;
	LinearLayout ll_facemenu;
	RelativeLayout rl_selectedface;
	ViewPager chat_vPager;
	int chat_vPager_now = 0;
	ImageView iv_face_left;
	ImageView iv_face_right;
	ImageView iv_face_delete;
	List<ImageView> faceMenuShowList;
	List<List<String>> faceNameList;
	static Map<String, String> expressionFaceMap = new HashMap<String, String>();
	List<String[]> faceNamesList;
	String faceRegx = "[\\[,<]{1}[\u4E00-\u9FFF]{1,5}[\\],>]{1}|[\\[,<]{1}[a-zA-Z0-9]{1,5}[\\],>]{1}";

	View groupTopBar;
	TextView textView_groupName;
	TextView textView_memberCount;
	LinearLayout linearlayout_members;
	View groupSetting;

	View groupCenterBar;
	TextView textView_groupNameAndMemberCount;
	LinearLayout linearlayout;

	int beforeHeight;
	int beforeLineHeight;

	final int MAXTYPE_COUNT = 3;

	public int showFirstPosition;

	public ListView chatContent;

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	public void initShowFirstPosition() {
		int initShowCount = 10;
		if (mNowChatFriend.notReadMessagesCount > 10) {
			initShowCount = mNowChatFriend.notReadMessagesCount;
		}
		int messagesTotalCount = mNowChatFriend.messages.size();
		if (messagesTotalCount < 10) {
			initShowCount = messagesTotalCount;
		}
		showFirstPosition = messagesTotalCount - initShowCount;
	}

	@Override
	public void onResume() {
		CircleMenu.showBack();
		LinearLayout ll_menu_app = mMainModeManager.ll_menu_app;
		if (ll_menu_app.getVisibility() == View.VISIBLE) {
			ll_menu_app.setVisibility(View.GONE);
		}
		super.onResume();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	View headView;

	public float dp2px(float px) {
		float dp = getActivity().getResources().getDisplayMetrics().density
				* px + 0.5f;
		return dp;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		// voice
		voice_list = new ArrayList<String>();
		faceMenuShowList = new ArrayList<ImageView>();
		faceNamesList = new ArrayList<String[]>();
		mContent = inflater.inflate(R.layout.f_chat, null);
		chatContent = (ListView) mContent.findViewById(R.id.chatContent);

		if (headView == null) {
			AbsListView.LayoutParams params = new AbsListView.LayoutParams(
					android.widget.AbsListView.LayoutParams.WRAP_CONTENT,
					(int) dp2px(35));
			headView = new View(getActivity());
			headView.setLayoutParams(params);
		}
		chatContent.addHeaderView(headView);

		iv_send = mContent.findViewById(R.id.iv_send);
		iv_more = mContent.findViewById(R.id.iv_more);
		iv_more_select = mContent.findViewById(R.id.iv_more_select);
		editText_message = (EditText) mContent.findViewById(R.id.et_message);
		rl_chatbottom = (RelativeLayout) mContent
				.findViewById(R.id.chat_bottom_bar);
		rl_message = (RelativeLayout) mContent.findViewById(R.id.rl_message);
		rl_select = (RelativeLayout) mContent.findViewById(R.id.rl_select);
		rl_audiopanel = (RelativeLayout) mContent
				.findViewById(R.id.rl_audiopanel);
		rl_selectpicture = mContent.findViewById(R.id.rl_selectpicture);
		rl_makeaudio = mContent.findViewById(R.id.rl_makeaudio);
		tv_voice = (TextView) mContent.findViewById(R.id.tv_voice);
		tv_voice_start = (TextView) mContent.findViewById(R.id.tv_voice_start);
		iv_voice_send = (ImageView) mContent.findViewById(R.id.iv_voice_send);
		iv_voice_play = (ImageView) mContent.findViewById(R.id.iv_voice_play);
		tv_voice_timelength = (TextView) mContent
				.findViewById(R.id.tv_voice_timelength);

		rl_face = (RelativeLayout) mContent.findViewById(R.id.rl_face);
		ll_facepanel = (LinearLayout) mContent.findViewById(R.id.ll_facepanel);
		ll_facemenu = (LinearLayout) mContent.findViewById(R.id.ll_facemenu);
		rl_selectedface = (RelativeLayout) mContent
				.findViewById(R.id.rl_selectedface);
		chat_vPager = (ViewPager) mContent.findViewById(R.id.chat_vPager);
		iv_face_left = (ImageView) mContent.findViewById(R.id.iv_face_left);
		iv_face_right = (ImageView) mContent.findViewById(R.id.iv_face_right);
		iv_face_delete = (ImageView) mContent.findViewById(R.id.iv_face_delete);

		groupTopBar = mContent.findViewById(R.id.relativeLayout_topbar);
		textView_groupName = (TextView) mContent
				.findViewById(R.id.textView_groupName);
		textView_memberCount = (TextView) mContent
				.findViewById(R.id.textView_memberCount);

		groupCenterBar = mContent.findViewById(R.id.relativeLayout_group);
		textView_groupNameAndMemberCount = (TextView) mContent
				.findViewById(R.id.textView_groupNameAndMemberCount);
		linearlayout = (LinearLayout) groupCenterBar
				.findViewById(R.id.linearlayout_user);
		groupSetting = groupCenterBar.findViewById(R.id.groupSetting);

		linearlayout_members = (LinearLayout) mContent
				.findViewById(R.id.linearlayout_members);

		if (mStatus == CHAT_FRIEND) {
			groupTopBar.setVisibility(View.GONE);
			initShowFirstPosition();
			if (mNowChatFriend.notReadMessagesCount != 0) {
				app.dataHandler.exclude(new Modification() {

					@Override
					public void modifyData(Data data) {
						data.friends.get(mNowChatFriend.phone).notReadMessagesCount = 0;
					}

					@Override
					public void modifyUI() {
						if (MainActivity.instance.mMainMode.mCirclesFragment
								.isAdded()) {
							mMainModeManager.mCirclesFragment.notifyViews();
						}
					}
				});
			}
		} else if (mStatus == CHAT_GROUP) {
			groupTopBar.setVisibility(View.VISIBLE);

		}
		LinearLayout ll_menu_app = mMainModeManager.ll_menu_app;
		if (ll_menu_app.getVisibility() == View.VISIBLE) {
			ll_menu_app.setVisibility(View.GONE);
		}
		initEvent();
		initBaseFaces();
		return mContent;
	}

	void initEvent() {
		chat_vPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// faceMenuShowList.get(chat_vPager_now).setBackgroundColor(
				// Color.WHITE);
				chat_vPager_now = arg0;
				// faceMenuShowList.get(chat_vPager_now).setBackgroundColor(
				// Color.RED);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		ll_facepanel.setOnClickListener(null);
		iv_face_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int start = editText_message.getSelectionStart();
				String content = editText_message.getText().toString();
				if (start - 1 < 0)
					return;
				String faceEnd = content.substring(start - 1, start);
				if ("]".equals(faceEnd) || ">".equals(faceEnd)) {
					String str = content.substring(0, start);
					int index = "]".equals(faceEnd) ? str.lastIndexOf("[")
							: str.lastIndexOf("<");
					if (index != -1) {
						String faceStr = content.substring(index, start);
						Pattern patten = Pattern.compile(faceRegx,
								Pattern.CASE_INSENSITIVE);
						Matcher matcher = patten.matcher(faceStr);
						if (matcher.find()) {
							editText_message.setSelection(start
									- faceStr.length());
						} else {
							if (start - 1 >= 0) {
								editText_message.setSelection(start - 1);
							}
						}
					}
				} else {
					if (start - 1 >= 0) {
						editText_message.setSelection(start - 1);
					}
				}
			}
		});
		iv_face_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int start = editText_message.getSelectionStart();
				String content = editText_message.getText().toString();
				if (start + 1 > content.length())
					return;
				String faceEnd = content.substring(start, start + 1);
				if ("[".equals(faceEnd) || "<".equals(faceEnd)) {
					String str = content.substring(start);
					int index = "[".equals(faceEnd) ? str.indexOf("]") : str
							.indexOf(">");
					if (index != -1) {
						String faceStr = content.substring(start, index + start
								+ 1);
						Pattern patten = Pattern.compile(faceRegx,
								Pattern.CASE_INSENSITIVE);
						Matcher matcher = patten.matcher(faceStr);
						if (matcher.find()) {
							editText_message.setSelection(start
									+ faceStr.length());
						} else {
							if (start + 1 <= content.length()) {
								editText_message.setSelection(start + 1);
							}
						}
					}
				} else {
					if (start + 1 <= content.length()) {
						editText_message.setSelection(start + 1);
					}
				}
			}
		});
		iv_face_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int start = editText_message.getSelectionStart();
				String content = editText_message.getText().toString();
				if (start - 1 < 0)
					return;
				String faceEnd = content.substring(start - 1, start);
				if ("]".equals(faceEnd) || ">".equals(faceEnd)) {
					String str = content.substring(0, start);
					int index = "]".equals(faceEnd) ? str.lastIndexOf("[")
							: str.lastIndexOf("<");
					if (index != -1) {
						String faceStr = content.substring(index, start);
						Pattern patten = Pattern.compile(faceRegx,
								Pattern.CASE_INSENSITIVE);
						Matcher matcher = patten.matcher(faceStr);
						if (matcher.find()) {
							editText_message.setText(content.substring(0, start
									- faceStr.length())
									+ content.substring(start));
							editText_message.setSelection(start
									- faceStr.length());
						} else {
							if (start - 1 >= 0) {
								editText_message.setText(content.substring(0,
										start - 1) + content.substring(start));
								editText_message.setSelection(start - 1);
							}
						}
					}
				} else {
					if (start - 1 >= 0) {
						editText_message.setText(content
								.substring(0, start - 1)
								+ content.substring(start));
						editText_message.setSelection(start - 1);
					}
				}
			}
		});
		rl_selectedface.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int show_status = rl_face.getVisibility();
				if (show_status == View.VISIBLE) {
					rl_face.setVisibility(View.GONE);
				} else {
					rl_face.setVisibility(View.VISIBLE);
					hideSelectTab();
				}
			}
		});
		groupTopBar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TranslateAnimation translateIn = new TranslateAnimation(0, 0,
						-dp2px(255), 0);
				translateIn.setDuration(150);
				groupCenterBar.setVisibility(View.VISIBLE);
				((ViewGroup) groupCenterBar).getChildAt(0).startAnimation(
						translateIn);
			}
		});

		groupCenterBar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				groupCenterBar.setVisibility(View.GONE);
			}
		});

		groupSetting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mMainModeManager.mGroupManagerFragment.status = GroupManagerFragment.MODE_MANAGER;
				mMainModeManager.mGroupManagerFragment.mCurrentManagerGroup = mNowChatGroup;
				mMainModeManager
						.showNext(mMainModeManager.mGroupManagerFragment);
			}
		});

		rl_selectpicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectPicture();
			}
		});

		rl_makeaudio.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int show_status = rl_audiopanel.getVisibility();
				if (show_status == View.VISIBLE) {
					if (voice_list.size() != 0) {
						Alert.createDialog(getActivity())
								.setTitle("语音尚未发送，是否取消？")
								.setOnConfirmClickListener(
										new OnDialogClickListener() {
											@Override
											public void onClick(
													AlertInputDialog dialog) {
												tv_voice.setText("语音");
												rl_audiopanel
														.setVisibility(View.GONE);
												for (int i = 0; i < voice_list
														.size(); i++) {
													File file = new File(
															voice_list.get(i));
													file.delete();
												}
												voice_list.clear();
												voice_length = 0;
											}
										}).show();
					} else {
						tv_voice.setText("语音");
						rl_audiopanel.setVisibility(View.GONE);
					}
				} else {
					initVoice();
				}
			}
		});
		rl_audiopanel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// not to do
			}
		});
		iv_voice_play.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!VOICE_PLAYSTATUS) {
					iv_voice_play.setImageBitmap(BitmapFactory.decodeResource(
							getResources(), R.drawable.voice_stop));
					VOICE_PLAYSTATUS = true;
					if (voice_list.size() != 0) {
						play_order = 0;
						play(play_order);
					} else {
						Toast.makeText(getActivity(), "voice not exist",
								Toast.LENGTH_SHORT).show();
						iv_voice_play.setImageBitmap(BitmapFactory
								.decodeResource(getResources(),
										R.drawable.voice_start));
						VOICE_PLAYSTATUS = false;
					}
				} else {
					iv_voice_play.setImageBitmap(BitmapFactory.decodeResource(
							getResources(), R.drawable.voice_start));
					VOICE_PLAYSTATUS = false;
					mPlayer.stop();
				}
			}
		});
		mOnTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();

				switch (action) {
				case MotionEvent.ACTION_DOWN:
					startTime = System.currentTimeMillis();
					start();
					tv_voice_start.setText("正在录音");
					break;
				case MotionEvent.ACTION_UP:
					long currentTime = System.currentTimeMillis();
					if (currentTime - startTime > 1000) {
						finish();
						File file = new File(app.sdcardVoiceFolder,
								voice_list.get(0));
						if (file.exists()) {
							mPlayer = MediaPlayer
									.create(getActivity(),
											Uri.parse((new File(
													app.sdcardVoiceFolder,
													voice_list.get(voice_list
															.size() - 1)))
													.getAbsolutePath()));
							if (mPlayer != null) {
								voice_length += mPlayer.getDuration();
							}
							tv_voice_timelength.setText((int) Math
									.ceil(voice_length / 1000) + "\"");
						}
					} else {
						File file = new File(app.sdcardVoiceFolder,
								voice_list.remove(voice_list.size() - 1));
						if (file.exists()) {
							file.delete();
						}
						Toast.makeText(getActivity(), "录音时间太短",
								Toast.LENGTH_SHORT).show();
						recorder.reset();
						recorder = null;
					}
					tv_voice_start.setText("继续录音");
					break;
				case MotionEvent.ACTION_CANCEL:
					Toast.makeText(getActivity(), "ACTION_CANCEL",
							Toast.LENGTH_SHORT).show();
					break;
				}
				return true;
			}
		};
		tv_voice_start.setOnTouchListener(mOnTouchListener);
		iv_voice_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (voice_length == 0) {
					Toast.makeText(getActivity(), "尚未录制语音", Toast.LENGTH_SHORT)
							.show();
				} else {
					// mergeAACAudioFiles();
					getRecordVoice();
					app.UIHandler.post(new Runnable() {
						public void run() {
							initVoice();
						}
					});
				}

			}
		});
		iv_more_select.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hideSelectTab();
			}
		});

		final GestureDetector gestureDetector = new GestureDetector(
				getActivity(), new OnGestureListener() {

					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						return false;
					}

					@Override
					public void onShowPress(MotionEvent e) {
						// TODO Auto-generated method stub

					}

					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public void onLongPress(MotionEvent e) {
						// TODO Auto-generated method stub

					}

					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						boolean flag = false;
						if (e2.getX() - e1.getX() > 0 && velocityX > 2000) {
							showSelectTab();
							flag = true;
						}
						return flag;
					}

					@Override
					public boolean onDown(MotionEvent e) {
						return false;
					}
				});

		editText_message.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});

		editText_message.setVisibility(View.GONE);
		editText_message.setVisibility(View.VISIBLE);
		// editText_message.requestFocus();

		iv_more.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});

		iv_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showSelectTab();
			}
		});
		editText_message.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				rl_face.setVisibility(View.GONE);
			}
		});
		editText_message.addTextChangedListener(new TextWatcher() {
			String content = "";

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (beforeHeight == 0) {
					beforeHeight = editText_message.getHeight();
				}
				if (beforeLineHeight == 0) {
					beforeLineHeight = editText_message.getLineHeight();
				}

				if (beforeHeight == 0 || beforeLineHeight == 0) {
					return;
				}

				LayoutParams etparams = editText_message.getLayoutParams();
				LayoutParams rlparams = rl_chatbottom.getLayoutParams();

				int lineCount = editText_message.getLineCount();

				switch (lineCount) {
				case 4:
					etparams.height = beforeHeight + beforeLineHeight;
					rlparams.height = beforeHeight + beforeLineHeight;
					break;
				case 5:
					etparams.height = beforeHeight + beforeLineHeight * 2;
					rlparams.height = beforeHeight + beforeLineHeight * 2;
					break;

				default:
					if (lineCount <= 3) {
						etparams.height = beforeHeight;
						rlparams.height = beforeHeight;
					}
					break;
				}
				if (lineCount > 5) {
					etparams.height = beforeHeight + beforeLineHeight * 2;
					rlparams.height = beforeHeight + beforeLineHeight * 2;
				}
				editText_message.setLayoutParams(etparams);
				rl_chatbottom.setLayoutParams(rlparams);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				content = s.toString();
			}

			@Override
			public void afterTextChanged(Editable s) {
				int selectionIndex = editText_message.getSelectionStart();
				if (!(s.toString()).equals(content)) {
					SpannableString spannableString = ExpressionUtil
							.getExpressionString(getActivity(), s.toString(),
									faceRegx, expressionFaceMap);
					editText_message.setText(spannableString);
					Log.v("Coolspan", selectionIndex + "");
					editText_message.setSelection(selectionIndex);
				}
			}
		});

		iv_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String message = editText_message.getText().toString();
				editText_message.setText("");
				if (message != null && !message.equals("")) {
					sendMessage("text", message);
					rl_face.setVisibility(View.GONE);
				}
			}
		});
	}

	void mergeAACAudioFiles() {
		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/wxgs/";
		File mergeACC = new File(path + new Date().getTime() + ".aac");
		FileOutputStream mergerAACFos = null;
		if (!mergeACC.exists()) {
			try {
				mergeACC.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			mergerAACFos = new FileOutputStream(mergeACC);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < voice_list.size(); i++) {
			File file = new File(path + voice_list.get(i));
			if (file.exists()) {
				try {
					FileInputStream fis = new FileInputStream(file);
					// byte[] buffer = new byte[1024];
					// int len = 0;
					// if (i == 0) {
					// while ((len = fis.read(buffer)) > 0) {
					// mergerAACFos.write(buffer, 0, len);
					// }
					// }
					//
					// else {
					// while ((len = fis.read(buffer)) > 0) {
					// mergerAACFos.write(buffer, 0, len);
					// }
					// }
					byte[] myByte = new byte[fis.available()];
					int length = myByte.length;

					if (i == 0) {
						while (fis.read(myByte) != -1) {
							mergerAACFos.write(myByte, 0, length);
						}
					} else {
						while (fis.read(myByte) != -1) {
							mergerAACFos.write(myByte, 6, length - 6);
						}
					}
					mergerAACFos.flush();
					fis.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				continue;
			}
		}
		try {
			mergerAACFos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		deleteRecordFile(path);
	}

	void deleteRecordFile(String path) {
		for (int i = 0; i < voice_list.size(); i++) {
			File file = new File(path + voice_list.get(i));
			if (file.exists()) {
				file.delete();
			}
		}
		voice_list.clear();
		voice_length = 0;
		tv_voice_timelength.setText("0\"");
		tv_voice_start.setText("按住录音");
	}

	void initVoice() {
		tv_voice_timelength.setText("0\"");
		tv_voice.setText("取消");
		tv_voice_start.setText("按住录音");
		rl_audiopanel.setVisibility(View.VISIBLE);
		int show_status = rl_face.getVisibility();
		if (show_status == View.VISIBLE) {
			rl_face.setVisibility(View.GONE);
		}
	}

	void play(int i) {
		play_order = i;
		playAudio(play_order).setOnCompletionListener(
				new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						// TODO Auto-generated method stub
						mp.reset();
						play_order++;
						if (play_order < voice_list.size()) {
							playAudio(play_order);
						} else {
							iv_voice_play.setImageBitmap(BitmapFactory
									.decodeResource(getResources(),
											R.drawable.voice_start));
							VOICE_PLAYSTATUS = false;
							mp.stop();
							mp.release();
							mp = null;
						}
					}
				});
	}

	MediaPlayer playAudio(int i) {
		mPlayer = MediaPlayer.create(getActivity(), Uri.parse((new File(
				app.sdcardVoiceFolder, voice_list.get(i))).getAbsolutePath()));
		mPlayer.start();
		return mPlayer;
	}

	@SuppressLint("InlinedApi")
	void start() {
		String fileName = new Date().getTime() + ".aac";
		// AudioRecord audioRecord = new AudioRecord(audioSource,
		// sampleRateInHz, channelConfig, audioFormat,
		// bufferSizeInBytes)
		recorder = new MediaRecorder();
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
		voice_list.add(fileName);
		// System.out.println("start------------------------------");
		// };
		// }.start();
	}

	void finish() {
		if (recorder != null) {
			recorder.stop();
			recorder.reset();
			recorder.release();
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (mStatus == CHAT_FRIEND) {
		} else if (mStatus == CHAT_GROUP) {
			mAdapter = new GroupChatAdapter();
		}
		chatContent.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();

		chatContent.setSelection(mAdapter.getCount() - 1);
	}

	public void showSelectTab() {
		hideSoftInput();
		Animation outAnimation = new TranslateAnimation(0,
				rl_chatbottom.getWidth(), 0, 0);
		outAnimation.setDuration(150);
		outAnimation.setAnimationListener(new AnimationAdapter() {
			@Override
			public void onAnimationEnd(Animation animation) {
				rl_message.setVisibility(View.GONE);
				rl_message.clearAnimation();
			}
		});
		rl_message.startAnimation(outAnimation);

		Animation inAnimation = new TranslateAnimation(
				-rl_chatbottom.getWidth(), 0, 0, 0);
		inAnimation.setDuration(150);
		rl_select.setVisibility(View.VISIBLE);
		rl_select.startAnimation(inAnimation);
	}

	public void hideSelectTab() {
		Animation outAnimation = new TranslateAnimation(0,
				-rl_chatbottom.getWidth(), 0, 0);
		outAnimation.setDuration(150);
		outAnimation.setAnimationListener(new AnimationAdapter() {
			@Override
			public void onAnimationEnd(Animation animation) {
				rl_select.setVisibility(View.GONE);
				rl_message.clearAnimation();
			}
		});
		rl_select.startAnimation(outAnimation);

		Animation inAnimation = new TranslateAnimation(
				rl_chatbottom.getWidth(), 0, 0, 0);
		inAnimation.setDuration(150);
		rl_message.setVisibility(View.VISIBLE);
		editText_message.requestFocus();
		rl_message.startAnimation(inAnimation);
		if (rl_audiopanel.getVisibility() == View.VISIBLE) {
			rl_audiopanel.setVisibility(View.GONE);
			tv_voice.setText("语音");
		}
	}

	public class GroupChatAdapter extends BaseAdapter {

		@Override
		public void notifyDataSetChanged() {
			textView_groupName.setText(mNowChatGroup.name);
			textView_memberCount.setText("(" + mNowChatGroup.members.size()
					+ "人)");
			int topShowCount = mNowChatGroup.members.size() < 4 ? mNowChatGroup.members
					.size() : 4;
			linearlayout_members.removeAllViews();
			for (int i = 0; i < topShowCount; i++) {
				final ImageView iv_head = new ImageView(getActivity());
				final String headFileName = app.data.groupFriends
						.get(mNowChatGroup.members.get(i)).head;
				app.fileHandler.getHeadImage(headFileName, new FileResult() {
					@Override
					public void onResult(String where) {
						iv_head.setImageBitmap(app.fileHandler.bitmaps
								.get(headFileName));
					}
				});
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						40, 40);
				if (i != 3)
					params.setMargins(0, 0, 10, 0);
				iv_head.setLayoutParams(params);
				linearlayout_members.addView(iv_head);
			}

			textView_groupNameAndMemberCount.setText(mNowChatGroup.name + "("
					+ mNowChatGroup.members.size() + "人)");
			linearlayout.removeAllViews();
			for (int i = 0; i < mNowChatGroup.members.size(); i++) {
				final Friend friend = app.data.groupFriends
						.get(mNowChatGroup.members.get(i));
				View userView = mInflater.inflate(
						R.layout.fragment_circles_gridpage_item, null);
				final ImageView iv_head = (ImageView) userView
						.findViewById(R.id.iv_head);
				TextView tv_nickname = (TextView) userView
						.findViewById(R.id.tv_nickname);
				tv_nickname.setText(friend.nickName);
				final String headFileName = friend.head;
				app.fileHandler.getHeadImage(headFileName, new FileResult() {
					@Override
					public void onResult(String where) {
						iv_head.setImageBitmap(app.fileHandler.bitmaps
								.get(headFileName));
					}
				});
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						(int) dp2px(55), LinearLayout.LayoutParams.WRAP_CONTENT);

				params.setMargins(40, 0, 0, 0);

				if (i == mNowChatGroup.members.size() - 1) {
					params.setMargins(40, 0, 40, 0);
				}
				userView.setLayoutParams(params);

				userView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (app.data.friends.get(friend.phone) != null) {
							mMainModeManager.mBusinessCardFragment.mStatus = BusinessCardFragment.SHOW_FRIEND;
							mMainModeManager.mBusinessCardFragment.mShowFriend = friend;
							mMainModeManager
									.showNext(mMainModeManager.mBusinessCardFragment);
						} else if (friend.phone.equals(app.data.user.phone)) {
							mMainModeManager.mBusinessCardFragment.mStatus = BusinessCardFragment.SHOW_SELF;
							mMainModeManager.mBusinessCardFragment.mShowFriend = friend;
							mMainModeManager
									.showNext(mMainModeManager.mBusinessCardFragment);
						} else {
							mMainModeManager.mBusinessCardFragment.mStatus = BusinessCardFragment.SHOW_TEMPFRIEND;
							mMainModeManager.mBusinessCardFragment.mShowFriend = friend;
							mMainModeManager
									.showNext(mMainModeManager.mBusinessCardFragment);
						}
					}
				});

				linearlayout.addView(userView);
			}

			if (mNowChatGroup.notReadMessagesCount != 0) {
				app.dataHandler.exclude(new Modification() {

					@Override
					public void modifyData(Data data) {
						data.groupsMap.get(String.valueOf(mNowChatGroup.gid)).notReadMessagesCount = 0;
					}

					@Override
					public void modifyUI() {
						if (MainActivity.instance.mMainMode.mGroupFragment
								.isAdded()) {
							mMainModeManager.mGroupFragment.notifyViews();
						}
					}
				});
			}

			super.notifyDataSetChanged();

			chatContent.setSelection(mAdapter.getCount() - 1);
		}

		@Override
		public int getCount() {
			return mNowChatGroup.messages.size() - showFirstPosition;
		}

		@Override
		public Object getItem(int position) {
			return mNowChatGroup.messages.get(showFirstPosition + position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			return ((Message) getItem(position)).type;
		}

		@Override
		public int getViewTypeCount() {
			return MAXTYPE_COUNT;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			final MessageHolder messageHolder;
			final int type = getItemViewType(position);
			if (convertView == null) {
				messageHolder = new MessageHolder();
				switch (type) {
				case Message.MESSAGE_TYPE_SEND:
					convertView = mInflater.inflate(R.layout.f_chat_item_right,
							null);
					messageHolder.text = convertView
							.findViewById(R.id.rl_chatright);
					break;
				case Message.MESSAGE_TYPE_RECEIVE:
					convertView = mInflater.inflate(R.layout.f_chat_item_left,
							null);
					messageHolder.text = convertView
							.findViewById(R.id.rl_chatleft);
					break;
				default:
					break;
				}
				messageHolder.image = convertView
						.findViewById(R.id.rl_chatleft_image);
				messageHolder.iv_image = (ImageView) convertView
						.findViewById(R.id.iv_image);
				messageHolder.iv_image_gif = (RelativeLayout) convertView
						.findViewById(R.id.iv_image_gif);
				messageHolder.tv_nickname = (TextView) convertView
						.findViewById(R.id.tv_nickname);
				messageHolder.iv_head = (ImageView) convertView
						.findViewById(R.id.iv_head);
				messageHolder.tv_chat = (TextView) convertView
						.findViewById(R.id.tv_chat);
				messageHolder.voice = convertView
						.findViewById(R.id.rl_chatleft_voice);
				messageHolder.iv_voicehead_status = (ImageView) convertView
						.findViewById(R.id.iv_voicehead_status);
				messageHolder.iv_voicehead = (ImageView) convertView
						.findViewById(R.id.iv_voicehead);
				messageHolder.tv_voicetime = (TextView) convertView
						.findViewById(R.id.tv_voicetime);
				messageHolder.sk_voice = (SeekBar) convertView
						.findViewById(R.id.sk_voice);

				convertView.setTag(messageHolder);
			} else {
				messageHolder = (MessageHolder) convertView.getTag();
			}
			final Message message = (Message) getItem(position);
			if (message.contentType.equals("text")) {
				messageHolder.text.setVisibility(View.VISIBLE);
				messageHolder.image.setVisibility(View.GONE);
				messageHolder.voice.setVisibility(View.GONE);
				// messageHolder.tv_chat.setText(message.content);
				String content = message.content;
				SpannableString spannableString = ExpressionUtil
						.getExpressionString(getActivity(), content, faceRegx,
								expressionFaceMap);
				messageHolder.tv_chat.setText(spannableString);
				String fileName = app.data.user.head;
				switch (type) {
				case Message.MESSAGE_TYPE_SEND:
					fileName = app.data.user.head;
					break;
				case Message.MESSAGE_TYPE_RECEIVE:
					fileName = app.data.groupFriends.get(message.phone).head;
					break;
				default:
					break;
				}
				final String headFileName = fileName;
				final ImageView iv_head = messageHolder.iv_head;
				app.fileHandler.getHeadImage(headFileName, new FileResult() {
					@Override
					public void onResult(String where) {
						iv_head.setImageBitmap(app.fileHandler.bitmaps
								.get(headFileName));
					}
				});
				iv_head.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						switch (type) {
						case Message.MESSAGE_TYPE_SEND:
							mMainModeManager.mBusinessCardFragment.mStatus = BusinessCardFragment.SHOW_SELF;
							mMainModeManager
									.showNext(mMainModeManager.mBusinessCardFragment);
							break;
						case Message.MESSAGE_TYPE_RECEIVE:
							mMainModeManager.mBusinessCardFragment.mStatus = BusinessCardFragment.SHOW_FRIEND;
							mMainModeManager.mBusinessCardFragment.mShowFriend = app.data.groupFriends
									.get(message.phone);
							mMainModeManager
									.showNext(mMainModeManager.mBusinessCardFragment);
							break;

						default:
							break;
						}
					}
				});
				messageHolder.text
						.setOnLongClickListener(new OnLongClickListener() {

							@SuppressWarnings("deprecation")
							@TargetApi(Build.VERSION_CODES.HONEYCOMB)
							@Override
							public boolean onLongClick(View v) {
								ClipboardManager clip = (ClipboardManager) getActivity()
										.getSystemService(
												Context.CLIPBOARD_SERVICE);
								// clip.setPrimaryClip()
								clip.setText(message.content);
								Toast.makeText(getActivity(), "复制成功!",
										Toast.LENGTH_SHORT).show();
								return true;
							}
						});
			} else if (message.contentType.equals("image")) {
				messageHolder.text.setVisibility(View.GONE);
				messageHolder.image.setVisibility(View.VISIBLE);
				messageHolder.voice.setVisibility(View.GONE);
				final String imageFileName = message.content;
				final ImageView iv_image = messageHolder.iv_image;
				String content = message.content;
				final String imgLastName = content.substring(content
						.lastIndexOf(".") + 1);
				if ("gif".equals(imgLastName)) {
					messageHolder.iv_image.setVisibility(View.GONE);
					messageHolder.iv_image_gif.setVisibility(View.VISIBLE);
					messageHolder.iv_image_gif.removeAllViews();
					app.fileHandler.getGifImgFromWebOrSdCard(imageFileName,
							new FileResult() {

								@Override
								public void onResult(final String where) {
									app.UIHandler.post(new Runnable() {
										public void run() {
											SampleView sampleView = new SampleView(
													getActivity(),
													app.fileHandler.gifs
															.get(imageFileName));
											messageHolder.iv_image_gif
													.addView(sampleView);
											if (where == app.fileHandler.FROM_WEB) {
												mAdapter.notifyDataSetChanged();
											}
										}
									});
								}
							});
				} else {
					app.fileHandler.getImage(imageFileName, new FileResult() {
						@Override
						public void onResult(String where) {
							messageHolder.iv_image_gif.setVisibility(View.GONE);
							messageHolder.iv_image.setVisibility(View.VISIBLE);
							iv_image.setImageBitmap(app.fileHandler.bitmaps
									.get(imageFileName));
							// Movie.decodeFile((new File(app.sdcardImageFolder,
							// imageFileName)).getAbsolutePath());
							// if (where == app.fileHandler.FROM_WEB) {
							// mAdapter.notifyDataSetChanged();
							// }
						}
					});
				}
				switch (type) {
				case Message.MESSAGE_TYPE_SEND:
					break;
				case Message.MESSAGE_TYPE_RECEIVE:
					messageHolder.tv_nickname.setText(app.data.groupFriends
							.get(message.phone).nickName);
					break;
				default:
					break;
				}
			} else if (message.contentType.equals("voice")) {
				messageHolder.text.setVisibility(View.GONE);
				messageHolder.image.setVisibility(View.GONE);
				messageHolder.voice.setVisibility(View.VISIBLE);
				String fileName = app.data.user.head;
				switch (type) {
				case Message.MESSAGE_TYPE_SEND:
					fileName = app.data.user.head;
					break;
				case Message.MESSAGE_TYPE_RECEIVE:
					fileName = app.data.groupFriends.get(message.phone).head;
					break;
				default:
					break;
				}
				final String voiceContent = message.content;
				final String headFileName = fileName;
				final ImageView iv_head = messageHolder.iv_voicehead;
				final ImageView iv_voicehead_status = messageHolder.iv_voicehead_status;
				app.fileHandler.getHeadImage(headFileName, new FileResult() {
					@Override
					public void onResult(String where) {
						iv_head.setImageBitmap(app.fileHandler.bitmaps
								.get(headFileName));
						// iv_head.setBackgroundDrawable(new BitmapDrawable(
						// app.fileHandler.bitmaps.get(headFileName)));
						Bitmap bitmap = BitmapFactory.decodeResource(
								getResources(), R.drawable.head_voice_start);

						iv_voicehead_status.setImageBitmap(bitmap);
						iv_voicehead_status.setTag("start");
					}
				});
				app.fileHandler.saveVoice(new VoiceInterface() {

					@Override
					public void setParams(VoiceSettings settings) {
						settings.fileName = voiceContent;
					}

					@Override
					public void onSuccess(String filename, String base64,
							Boolean flag) {
						VOICE_SAVESTATUS = flag;
						if (flag) {
							// MediaPlayer mpPlayer = null;
							try {
								final MediaPlayer mpPlayer = MediaPlayer
										.create(getActivity(), Uri
												.parse((new File(
														app.sdcardVoiceFolder,
														voiceContent))
														.getAbsolutePath()));
								messageHolder.mpPlayer = mpPlayer;
								app.UIHandler.post(new Runnable() {

									@Override
									public void run() {
										messageHolder.tv_voicetime.setText((int) Math
												.ceil((double) (mpPlayer
														.getDuration()) / 1000)
												+ "\"");
										// messageHolder.tv_voicetime.setText((int)
										// Math
										// .ceil(mpPlayer.getDuration() / 1000)
										// + "\"");
									}
								});
								messageHolder.sk_voice.setMax(mpPlayer
										.getDuration());
							} catch (SecurityException e) {
								e.printStackTrace();
							} catch (IllegalStateException e) {
								e.printStackTrace();
							}
						} else {
							// to do loading voice failed
						}

					}
				});

				messageHolder.sk_voice.setProgress(0);
				messageHolder.sk_voice
						.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

							@Override
							public void onStopTrackingTouch(SeekBar seekBar) {
								// TODO Auto-generated method stub
								messageHolder.sk_voice.setProgress(seekBar
										.getProgress());
								messageHolder.mpPlayer.seekTo(seekBar
										.getProgress());
								if (!messageHolder.mpPlayer.isPlaying()) {
									messageHolder.mpPlayer.start();
									iv_head.performClick();
								}
							}

							@Override
							public void onStartTrackingTouch(SeekBar seekBar) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onProgressChanged(SeekBar seekBar,
									int arg1, boolean arg2) {
								// TODO Auto-generated method stub

							}
						});
				iv_head.setOnClickListener(new OnClickListener() {
					Thread thread = null;

					@Override
					public void onClick(View v) {
						Bitmap bitmap = null;

						final MediaPlayer mpPlayer = messageHolder.mpPlayer;
						if (iv_voicehead_status.getTag() == "start") {
							bitmap = BitmapFactory.decodeResource(
									getResources(), R.drawable.head_voice_stop);
							iv_voicehead_status.setTag("stop");
							int playTime = messageHolder.sk_voice.getProgress();
							if (mpPlayer.getDuration() - playTime > 10) {
								mpPlayer.seekTo(playTime);
							} else {
								mpPlayer.seekTo(0);
							}

							mpPlayer.start();
							thread = new Thread() {
								public void run() {
									while (true) {
										try {
											if (getActivity() == null) {
												mpPlayer.stop();
												mpPlayer.release();
												thread.interrupt();
												break;
											}
											Thread.sleep(50);
											messageHolder.sk_voice.setProgress(mpPlayer
													.getCurrentPosition());
											if (mpPlayer.getDuration()
													- mpPlayer
															.getCurrentPosition() < 50) {
												messageHolder.sk_voice
														.setProgress(messageHolder.sk_voice
																.getMax());
												Thread.currentThread()
														.interrupt();
												break;
											}
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
									}
								};
							};
							thread.start();
							mpPlayer.setOnCompletionListener(new OnCompletionListener() {

								@Override
								public void onCompletion(MediaPlayer arg0) {
									if (getActivity() == null) {
										thread.interrupt();
										return;
									}
									Bitmap bitmap = BitmapFactory
											.decodeResource(getResources(),
													R.drawable.head_voice_start);
									iv_voicehead_status.setImageBitmap(bitmap);
									iv_voicehead_status.setTag("start");
								}
							});
						} else {
							bitmap = BitmapFactory
									.decodeResource(getResources(),
											R.drawable.head_voice_start);
							iv_voicehead_status.setTag("start");
							mpPlayer.pause();
						}
						iv_voicehead_status.setImageBitmap(bitmap);
					}
				});
			}
			return convertView;
		}
	}

	class MessageHolder {
		View text;
		ImageView iv_head;
		TextView tv_chat;

		View image;
		ImageView iv_image;
		TextView tv_nickname;
		RelativeLayout iv_image_gif;

		View voice;
		ImageView iv_voicehead;
		ImageView iv_voicehead_status;
		TextView tv_voicetime;
		SeekBar sk_voice;
		MediaPlayer mpPlayer;
	}

	public void sendMessage(final String type, final String content) {
		final Message message = new Message();
		message.type = Message.MESSAGE_TYPE_SEND;
		if (mStatus == CHAT_FRIEND) {
			message.sendType = "point";
			message.phone = mNowChatFriend.phone;
		} else if (mStatus == CHAT_GROUP) {
			message.sendType = "group";
			message.gid = String.valueOf(mNowChatGroup.gid);
			message.phone = app.data.user.phone;
		}
		message.content = content;
		message.contentType = type;
		message.status = "sending";
		message.time = String.valueOf(new Date().getTime());

		app.dataHandler.exclude(new Modification() {
			@Override
			public void modifyData(Data data) {
				mNowChatGroup.messages.add(message);
				data.lastChatFriends.remove("g" + mNowChatGroup.gid);
				data.lastChatFriends.add(0, "g" + mNowChatGroup.gid);
				Log.e("Coolspan", data.lastChatFriends.size()
						+ "---------------chat length");
				mMainModeManager.mChatMessagesFragment.notifyViews();
			}

			@Override
			public void modifyUI() {
				mAdapter.notifyDataSetChanged();
				chatContent.setSelection(mAdapter.getCount() - 1);
				if (mMainModeManager.mCirclesFragment.isAdded()) {
					// mMainModeManager.mCirclesFragment.mAdapter
					// .notifyDataSetChanged();
				}
			}
		});

		app.networkHandler.connection(new CommonNetConnection() {

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.MESSAGE_SEND;
				settings.params = generateMessageParams(type, content);
			}

			@Override
			public void success(JSONObject jData) {
				try {
					String time = jData.getString("time");
					message.time = time;
					message.status = "sent";
					if (app.data.lastChatFriends.indexOf("g"
							+ mNowChatGroup.gid) != 0) {
						app.data.lastChatFriends
								.remove("g" + mNowChatGroup.gid);
						app.data.lastChatFriends
								.add(0, "g" + mNowChatGroup.gid);
						mMainModeManager.mChatMessagesFragment.notifyViews();
					}
				} catch (JSONException e) {
					message.status = "failed";
				}
			}

			@Override
			protected void unSuccess(JSONObject jData) {
				message.status = "failed";
				super.unSuccess(jData);
			}
		});

	}

	public Map<String, String> generateMessageParams(String type, String content) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", app.data.user.phone);
		params.put("accessKey", app.data.user.accessKey);
		JSONArray jFriends = new JSONArray();
		if (mStatus == CHAT_FRIEND) {
			params.put("sendType", "point");
			jFriends.put(mNowChatFriend.phone);
			params.put("phoneto", jFriends.toString());
		} else if (mStatus == CHAT_GROUP) {
			params.put("sendType", "group");
			params.put("gid", String.valueOf(mNowChatGroup.gid));
			for (String phone : mNowChatGroup.members) {
				jFriends.put(phone);
			}
			params.put("phoneto", jFriends.toString());
		}
		JSONObject jMessage = new JSONObject();
		try {
			jMessage.put("contentType", type);
			jMessage.put("content", content);
			params.put("message", jMessage.toString());
		} catch (JSONException e) {
		}

		return params;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT_SELECTPICTURE
				&& resultCode == Activity.RESULT_OK && data != null) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getActivity().getContentResolver().query(
					selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			final String picturePath = cursor.getString(columnIndex)
					.toLowerCase(Locale.getDefault());
			final String format = picturePath.substring(picturePath
					.lastIndexOf("."));
			cursor.close();

			final Bitmap bitmap = MCImageUtils.getZoomBitmapFromFile(new File(
					picturePath), 960, 540);
			if (bitmap != null) {
				app.fileHandler.saveBitmap(new SaveBitmapInterface() {

					@Override
					public void setParams(SaveSettings settings) {
						settings.compressFormat = format.equals(".jpg") ? settings.JPG
								: settings.PNG;
						settings.source = bitmap;
					}

					@Override
					public void onSuccess(String fileName, String base64) {
						checkImage(fileName, base64);
					}
				});
			}

		} else if (requestCode == RESULT_TAKEPICTURE
				&& resultCode == Activity.RESULT_OK) {

		} else if (requestCode == RESULT_CATPICTURE
				&& resultCode == Activity.RESULT_OK && data != null) {

		}
	}

	void selectPicture() {
		Intent selectFromGallery = new Intent(Intent.ACTION_PICK,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(selectFromGallery, RESULT_SELECTPICTURE);
	}

	File tempFile;

	void takePicture() {
		tempFile = new File(app.sdcardImageFolder, "tempimage");
		int i = 1;
		while (tempFile.exists()) {
			tempFile = new File(app.sdcardImageFolder, "tempimage" + (i++));
		}
		Uri uri = Uri.fromFile(tempFile);
		Intent tackPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		tackPicture.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
		tackPicture.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(tackPicture, RESULT_TAKEPICTURE);
	}

	public void checkImage(final String fileName, final String base64) {
		app.networkHandler.connection(new CommonNetConnection() {

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.IMAGE_CHECK;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("filename", fileName);
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
				try {
					if (jData.getBoolean("exists")) {
						sendMessage("image", fileName);
					} else {
						uploadImageOrVoice("image", fileName, base64);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
	}

	public void uploadImageOrVoice(final String type, final String fileName,
			final String base64) {
		app.networkHandler.connection(new CommonNetConnection() {

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.IMAGE_UPLOAD;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("filename", fileName);
				params.put("imagedata", base64);
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
				sendMessage(type, fileName);
			}

		});
	}

	public void getRecordVoice() {
		app.fileHandler.getVoice(new VoiceInterface() {

			@Override
			public void setParams(VoiceSettings settings) {
				settings.format = ".aac";
				settings.fileName = voice_list.get(0);
			}

			@Override
			public void onSuccess(String filename, String base64, Boolean flag) {
				File from = new File(app.sdcardVoiceFolder, voice_list.get(0));
				File to = new File(app.sdcardVoiceFolder, filename);
				from.renameTo(to);
				voice_list.clear();
				voice_length = 0;
				uploadImageOrVoice("voice", filename, base64);
			}
		});
	}

	void initBaseFaces() {
		String[] faceNames1 = new String[] { "[微笑]", "[撇嘴]", "[色]", "[发呆]",
				"[得意]", "[流泪]", "[害羞]", "[闭嘴]", "[睡]", "[大哭]", "[尴尬]", "[发怒]",
				"[调皮]", "[呲牙]", "[惊讶]", "[难过]", "[酷]", "[冷汗]", "[抓狂]", "[吐]",
				"[偷笑]", "[可爱]", "[白眼]", "[傲慢]", "[饥饿]", "[困]", "[惊恐]", "[流汗",
				"[憨笑]", "[大兵]", "[奋斗]", "[咒骂]", "[疑问]", "[嘘]", "[晕]", "折磨]",
				"[衰]", "[骷髅]", "[敲打]", "[再见]", "[擦汗]", "[抠鼻]", "[鼓掌]", "[糗大了]",
				"[坏笑]", "[左哼哼]", "[右哼哼]", "[哈欠]", "[鄙视]", "[委屈]", "[快哭了]",
				"[阴险]", "[亲亲]", "[吓]", "[可怜]", "[菜刀]", "[西瓜]", "[啤酒]", "[篮球]",
				"[乒乓]", "[咖啡]", "[饭]", "[猪头]", "[玫瑰]", "[凋谢]", "[示爱]", "[爱心]",
				"[心碎]", "[蛋糕]", "[闪电]", "[炸弹]", "[刀]", "[足球]", "[瓢虫]", "[便便]",
				"[月亮]", "[太阳]", "[礼物]", "[拥抱]", "[强", "[弱]", "[握手]", "[胜利]",
				"[抱拳]", "[勾引]", "[拳头]", "[差劲]", "[爱你]", "[NO]", "[OK]", "[爱情]",
				"[飞吻]", "[跳跳]", "[发抖]", "[怄火]", "[转圈]", "[磕头]", "[回头]", "[跳绳]",
				"[挥手]", "[激动]", "[街舞]", "[献吻]", "[左太极]", "[右太极]" };
		String[] faceNames2 = new String[] { "<笑脸>", "<开心>", "<大笑>", "<热情>",
				"<眨眼>", "<色>", "<接吻>", "<亲吻>", "<脸红>", "<露齿笑>", "<满意>", "<戏弄>",
				"<吐舌>", "<无语>", "<得意>", "<汗>", "<失望>", "<低落>", "<呸>", "<焦虑>",
				"<担心>", "<震惊>", "<悔恨>", "<眼泪>", "<哭>", "<破涕为笑>", "<晕>", "<恐惧>",
				"<心烦>", "<生气>", "<睡觉>", "<生病>", "<恶魔>", "<外星人>", "<心>", "<心碎>",
				"<丘比特>", "<闪烁>", "<星星>", "<叹号>", "<问号>", "<睡着>", "<水滴>",
				"<音乐>", "<火>", "<便便>", "<强>", "<弱>", "<拳头>", "<胜利>", "<上>",
				"<下>", "<右>", "<左>", "<第一>", "<强壮>", "<吻>", "<热恋>", "<男孩>",
				"<女孩>", "<女士>", "<男士>", "<天使>", "<骷髅>", "<红唇>", "<太阳>", "<下雨>",
				"<多云>", "<雪人>", "<月亮>", "<闪电>", "<海浪>", "<猫>", "<小狗>", "<老鼠>",
				"<仓鼠>", "<兔子>" };
		faceNamesList.add(faceNames1);
		faceNamesList.add(faceNames2);
		List<View> mListViews = new ArrayList<View>();
		List<String> images1 = new ArrayList<String>();
		for (int i = 0; i < 105; i++) {
			expressionFaceMap.put(faceNamesList.get(0)[i], "smiley_" + i
					+ ".png");
			images1.add("smiley_" + i + ".png");
		}
		List<String> images2 = new ArrayList<String>();
		for (int i = 0; i < 77; i++) {
			expressionFaceMap.put(faceNamesList.get(1)[i], "emoji_" + i
					+ ".png");
			images2.add("emoji_" + i + ".png");
		}
		List<String> images3 = new ArrayList<String>();
		for (int i = 1; i < 17; i++) {
			images3.add("tusiji_" + i + ".gif");
		}
		faceNameList = new ArrayList<List<String>>();
		faceNameList.add(images1);
		faceNameList.add(images2);
		faceNameList.add(images3);

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(100,
				LayoutParams.MATCH_PARENT);
		lp.gravity = Gravity.CENTER;
		for (int i = 0; i < 3; i++) {
			try {
				ImageView iv = new ImageView(getActivity());
				iv.setImageBitmap(BitmapFactory.decodeStream(getActivity()
						.getAssets().open(
								"images/" + faceNameList.get(i).get(0))));
				iv.setLayoutParams(lp);
				if (i == 0) {
					// iv.setBackgroundColor(Color.RED);
				}
				iv.setTag(i);
				ll_facemenu.addView(iv);
				faceMenuShowList.add(iv);
				ImageView iv_1 = new ImageView(getActivity());
				iv_1.setBackgroundColor(Color.WHITE);
				iv_1.setMinimumWidth(1);
				iv_1.setMinimumHeight(80);
				iv_1.setMaxWidth(1);
				ll_facemenu.addView(iv_1);
				iv.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						int position = (Integer) v.getTag();
						// faceMenuShowList.get(chat_vPager_now)
						// .setBackgroundColor(Color.WHITE);
						chat_vPager_now = position;
						// faceMenuShowList.get(chat_vPager_now)
						// .setBackgroundColor(Color.RED);
						chat_vPager.setCurrentItem(chat_vPager_now);
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (i != 2) {
				View v = mInflater.inflate(R.layout.f_chat_face_base_gridview,
						null);
				GridView chat_base_gv = (GridView) v
						.findViewById(R.id.chat_base_gv);
				chat_base_gv.setAdapter(new myGridAdapter(faceNameList.get(i)));
				mListViews.add(chat_base_gv);
			} else {
				View v = mInflater.inflate(R.layout.f_chat_bigimg_gridview,
						null);
				GridView chat_base_gv = (GridView) v
						.findViewById(R.id.chat_base_gv);
				chat_base_gv.setAdapter(new myGridAdapter(faceNameList.get(i)));
				mListViews.add(chat_base_gv);
				chat_vPager.setAdapter(new myPageAdapter(mListViews));
			}
		}
		LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(100,
				LayoutParams.WRAP_CONTENT);
		lp1.gravity = Gravity.CENTER;
		ImageView iv = new ImageView(getActivity());
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.face_add);
		iv.setImageBitmap(bitmap);
		iv.setLayoutParams(lp1);
		ll_facemenu.addView(iv);
		ImageView iv_1 = new ImageView(getActivity());
		iv_1.setBackgroundColor(Color.WHITE);
		iv_1.setMinimumWidth(1);
		iv_1.setMinimumHeight(80);
		iv_1.setMaxWidth(1);
		ll_facemenu.addView(iv_1);
		faceMenuShowList.add(iv);
	}

	class myGridAdapter extends BaseAdapter {
		List<String> list;

		public myGridAdapter(List<String> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (getCount() < 30) {
				convertView = mInflater.inflate(
						R.layout.f_chat_bigimg_gridview_item, null);
			} else {
				convertView = mInflater.inflate(
						R.layout.f_chat_base_gridview_item, null);
			}
			ImageView iv = (ImageView) convertView
					.findViewById(R.id.chat_base_iv);
			try {
				iv.setImageBitmap(BitmapFactory.decodeStream(getActivity()
						.getAssets().open("images/" + list.get(position))));
			} catch (IOException e) {
				e.printStackTrace();
			}
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (chat_vPager_now < 2) {
						editText_message.getText().insert(
								editText_message.getSelectionStart(),
								faceNamesList.get(chat_vPager_now)[position]);
					} else {
						rl_face.setVisibility(View.GONE);
						app.fileHandler.getBigFaceImgBASE64(getActivity(),
								new BigFaceImgInterface() {

									@Override
									public void setParams(
											BigFaceImgSettings settings) {
										settings.format = ".gif";
										settings.assetsPath = "images/";
										settings.fileName = "tusiji_"
												+ (position + 1) + ".gif";
									}

									@Override
									public void onSuccess(String fileName,
											String base64) {
										checkImage(fileName, base64);
									}
								});
					}
				}
			});
			return convertView;
		}
	}

	class myPageAdapter extends PagerAdapter {
		List<View> mListViews;

		public myPageAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return (arg0 == arg1);
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			try {
				if (mListViews.get(arg1).getParent() == null)
					((ViewPager) arg0).addView(mListViews.get(arg1), 0);
				else {
					// 很难理解新添加进来的view会自动绑定一个父类，由于一个儿子view不能与两个父类相关，所以得解绑
					// 不这样做否则会产生 viewpager java.lang.IllegalStateException: The
					// specified child already has a parent. You must call
					// removeView() on the child's parent first.
					// 还有一种方法是viewPager.setOffscreenPageLimit(3); 这种方法不用判断parent
					// 是不是已经存在，但多余的listview不能被destroy
					((ViewGroup) mListViews.get(arg1).getParent())
							.removeView(mListViews.get(arg1));
					((ViewPager) arg0).addView(mListViews.get(arg1), 0);
				}
			} catch (Exception e) {
				Log.d("parent=", "" + mListViews.get(arg1).getParent());
				e.printStackTrace();
			}
			return mListViews.get(arg1);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}
	}
}
