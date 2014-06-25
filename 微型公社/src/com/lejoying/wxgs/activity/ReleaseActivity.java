package com.lejoying.wxgs.activity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.mode.fragment.SquareFragment;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.utils.ExpressionUtil;
import com.lejoying.wxgs.activity.utils.MCImageUtils;
import com.lejoying.wxgs.activity.view.BackgroundView;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.activity.view.widget.Alert.AlertInputDialog;
import com.lejoying.wxgs.activity.view.widget.Alert.AlertInputDialog.OnDialogClickListener;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.handler.OSSFileHandler;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileMessageInfoInterface;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileMessageInfoSettings;
import com.lejoying.wxgs.app.handler.OSSFileHandler.ImageMessageInfo;
import com.lejoying.wxgs.app.handler.OSSFileHandler.UploadFileInterface;
import com.lejoying.wxgs.app.handler.OSSFileHandler.UploadFileSettings;

@SuppressLint("DefaultLocale")
public class ReleaseActivity extends BaseActivity implements OnClickListener {
	BackgroundView mBackground;
	MainApplication app = MainApplication.getMainApplication();
	InputMethodManager imm;

	int height, width, dip, picwidth, statusBarHeight;
	int chat_vPager_now = 0;
	int RESULT_SELECTPICTURE = 0x34, RESULT_TAKEPICTURE = 0x54,
			RESULT_MAKEVOICE = 0x64, RESULT_PICANDVOICE = 0x74;
	float density;
	boolean isEditText = true, faceVisible = false, seletePic = false;
	static List<Map<String, Object>> voices;
	static List<Map<String, Object>> images;
	List<String[]> faceNamesList;
	List<List<String>> faceNameList;
	List<ImageView> faceMenuShowList;
	static Map<String, String> expressionFaceMap = new HashMap<String, String>();
	File tempFile;

	RelativeLayout rl_face;
	RelativeLayout rl_releasepic;
	ScrollView sl_et_release;
	LinearLayout ll_et_release;
	LinearLayout ll_facemenu;
	LinearLayout ll_navigation;
	LinearLayout ll_releasecamera;
	LinearLayout ll_releaselocal;
	LinearLayout ll_release_picandvoice;
	HorizontalScrollView horizontalScrollView;
	ViewPager chat_vPager;
	LayoutInflater mInflater;

	EditText et_release;
	View release_iv_face_left;
	View release_iv_face_right;
	View release_iv_face_delete;

	String faceRegx = "[\\[,<]{1}[\u4E00-\u9FFF]{1,5}[\\],>]{1}|[\\[,<]{1}[a-zA-Z0-9]{1,5}[\\],>]{1}";
	String mCurrentSquareID = "";
	String messageType = "";
	String broadcast = "";
	String contentType = "vit";
	JSONArray jsonArray;

	public static String cover = "";
	public static int currentCoverIndex = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_post);
		mInflater = getLayoutInflater();
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		dip = (int) (40 * density + 0.5f);
		height = dm.heightPixels;
		width = dm.widthPixels;
		statusBarHeight = getStatusBarHeight(this);
		initLayout();
		initData();
		super.onCreate(savedInstanceState);
	}

	protected void onResume() {
		CircleMenu.hide();
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		if (!seletePic) {
			checkBack();
		} else {
			rl_releasepic.setVisibility(View.GONE);
			if (voices.size() != 0 || images.size() != 0) {
				horizontalScrollView.setVisibility(View.VISIBLE);
			}
			sl_et_release.setVisibility(View.VISIBLE);
			ll_navigation.setVisibility(View.VISIBLE);
			seletePic = false;
		}
	}

	public void initLayout() {
		ll_navigation = (LinearLayout) findViewById(R.id.release_ll_navigation);
		ImageView iv_selectpicture = (ImageView) findViewById(R.id.release_iv_selectpicture);
		ImageView iv_emoji = (ImageView) findViewById(R.id.release_iv_emoji);
		ImageView iv_voice = (ImageView) findViewById(R.id.release_iv_voice);
		TextView tv_cancel = (TextView) findViewById(R.id.release_tv_cancel);
		TextView tv_commit = (TextView) findViewById(R.id.release_tv_commit);

		et_release = (EditText) findViewById(R.id.release_et_release);
		sl_et_release = (ScrollView) findViewById(R.id.sl_et_release);

		rl_releasepic = (RelativeLayout) findViewById(R.id.rl_releasepic);
		ll_releasecamera = (LinearLayout) findViewById(R.id.ll_releasecamera);
		ll_releaselocal = (LinearLayout) findViewById(R.id.ll_releaselocal);

		ll_facemenu = (LinearLayout) findViewById(R.id.release_ll_facemenu);
		rl_face = (RelativeLayout) findViewById(R.id.release_rl_face);
		horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
		chat_vPager = (ViewPager) findViewById(R.id.release_chat_vPager);
		release_iv_face_left = findViewById(R.id.release_iv_face_left);
		release_iv_face_right = findViewById(R.id.release_iv_face_right);
		release_iv_face_delete = findViewById(R.id.release_iv_face_delete);

		ll_release_picandvoice = (LinearLayout) findViewById(R.id.ll_release_picandvoice);

//		et_release
//				.setHeight((int) (height - (71 * density + 0.5f) - statusBarHeight));

		iv_selectpicture.setOnClickListener(this);
		iv_emoji.setOnClickListener(this);
		iv_voice.setOnClickListener(this);
		tv_cancel.setOnClickListener(this);
		tv_commit.setOnClickListener(this);
		et_release.setOnClickListener(this);
		ll_releasecamera.setOnClickListener(this);
		ll_releaselocal.setOnClickListener(this);
		rl_releasepic.setOnClickListener(this);
		release_iv_face_left.setOnClickListener(this);
		release_iv_face_right.setOnClickListener(this);
		release_iv_face_delete.setOnClickListener(this);

	}

	public void initData() {
		initFace();
		images = new ArrayList<Map<String, Object>>();
		voices = new ArrayList<Map<String, Object>>();
		et_release.addTextChangedListener(new TextWatcher() {
			String content = "";

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				content = s.toString();
			}

			@Override
			public void afterTextChanged(Editable s) {
				int selectionIndex = et_release.getSelectionStart();
				if (!(s.toString()).equals(content)) {
					SpannableString spannableString = ExpressionUtil
							.getExpressionString(getBaseContext(),
									s.toString(), faceRegx, expressionFaceMap);
					et_release.setText(spannableString);
					et_release.setSelection(selectionIndex);
				}
			}
		});
		chat_vPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				chat_vPager_now = arg0;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	public void initFace() {
		faceMenuShowList = new ArrayList<ImageView>();
		faceNamesList = new ArrayList<String[]>();
		faceNamesList = MainModeManager.faceNamesList;
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
				ImageView iv = new ImageView(this);
				iv.setImageBitmap(BitmapFactory.decodeStream(this.getAssets()
						.open("images/" + faceNameList.get(i).get(0))));
				iv.setLayoutParams(lp);
				if (i == 0) {
				}
				iv.setTag(i);
				ll_facemenu.addView(iv);
				faceMenuShowList.add(iv);
				ImageView iv_1 = new ImageView(this);
				iv_1.setBackgroundColor(Color.WHITE);
				iv_1.setMinimumWidth(1);
				iv_1.setMinimumHeight(80);
				iv_1.setMaxWidth(1);
				ll_facemenu.addView(iv_1);
				iv.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						int position = (Integer) v.getTag();
						chat_vPager_now = position;
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
				chat_base_gv.setAdapter(new MyGridAdapter(faceNameList.get(i)));
				mListViews.add(chat_base_gv);
			} else {
				View v = mInflater.inflate(R.layout.f_chat_bigimg_gridview,
						null);
				GridView chat_base_gv = (GridView) v
						.findViewById(R.id.chat_base_gv);
				chat_base_gv.setAdapter(new MyGridAdapter(faceNameList.get(i)));
				mListViews.add(chat_base_gv);
				chat_vPager.setAdapter(new MyPageAdapter(mListViews));
			}
		}
		LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(100,
				LayoutParams.WRAP_CONTENT);
		lp1.gravity = Gravity.CENTER;
		ImageView iv = new ImageView(this);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.face_add);
		iv.setImageBitmap(bitmap);
		iv.setLayoutParams(lp1);
		ll_facemenu.addView(iv);
		ImageView iv_1 = new ImageView(this);
		iv_1.setBackgroundColor(Color.WHITE);
		iv_1.setMinimumWidth(1);
		iv_1.setMinimumHeight(80);
		iv_1.setMaxWidth(1);
		ll_facemenu.addView(iv_1);
		faceMenuShowList.add(iv);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.release_iv_selectpicture:
			if (faceVisible) {
				rl_face.setVisibility(View.GONE);
				faceVisible = false;
			}
			if (imm.isActive()) {
				imm.hideSoftInputFromWindow(et_release.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
			horizontalScrollView.setVisibility(View.GONE);
			sl_et_release.setVisibility(View.GONE);
			ll_navigation.setVisibility(View.GONE);
			rl_releasepic.setVisibility(View.VISIBLE);
			seletePic = true;
			break;
		case R.id.release_iv_emoji:
			if (isEditText) {
				if (imm.isActive()) {
					imm.hideSoftInputFromWindow(et_release.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
				faceStartAnimation();
				isEditText = false;
			} else {
				if (faceVisible) {
					faceEndAnimation();
				} else {
					faceStartAnimation();
				}
			}
			break;
		case R.id.release_iv_voice:
			Intent intent = new Intent(ReleaseActivity.this,
					SendVoiceActivity.class);
			startActivityForResult(intent, RESULT_MAKEVOICE);
			break;
		case R.id.release_tv_cancel:
			checkBack();
			break;
		case R.id.release_tv_commit:
			Send();
			break;
		case R.id.release_et_release:
			if (!isEditText) {
				if (faceVisible) {
					faceEndAnimation();
					faceVisible = false;
				}
				isEditText = true;
			}
			break;
		case R.id.ll_releasecamera:
			addImageFromCamera();
			break;
		case R.id.ll_releaselocal:
			addImageFromLocal();
			break;
		case R.id.rl_releasepic:
			// rl_releasepic.setVisibility(View.GONE);
			// horizontalScrollView.setVisibility(View.VISIBLE);
			// et_release.setVisibility(View.VISIBLE);
			// ll_navigation.setVisibility(View.VISIBLE);
			// seletePic = false;
			break;
		case R.id.release_iv_face_left:
			int start1 = et_release.getSelectionStart();
			String content1 = et_release.getText().toString();
			if (start1 - 1 < 0)
				return;
			String faceEnd1 = content1.substring(start1 - 1, start1);
			if ("]".equals(faceEnd1) || ">".equals(faceEnd1)) {
				String str = content1.substring(0, start1);
				int index = "]".equals(faceEnd1) ? str.lastIndexOf("[") : str
						.lastIndexOf("<");
				if (index != -1) {
					String faceStr = content1.substring(index, start1);
					Pattern patten = Pattern.compile(faceRegx,
							Pattern.CASE_INSENSITIVE);
					Matcher matcher = patten.matcher(faceStr);
					if (matcher.find()) {
						et_release.setSelection(start1 - faceStr.length());
					} else {
						if (start1 - 1 >= 0) {
							et_release.setSelection(start1 - 1);
						}
					}
				}
			} else {
				if (start1 - 1 >= 0) {
					et_release.setSelection(start1 - 1);
				}
			}

			break;
		case R.id.release_iv_face_right:

			int start2 = et_release.getSelectionStart();
			String content2 = et_release.getText().toString();
			if (start2 + 1 > content2.length())
				return;
			String faceEnd2 = content2.substring(start2, start2 + 1);
			if ("[".equals(faceEnd2) || "<".equals(faceEnd2)) {
				String str = content2.substring(start2);
				int index = "[".equals(faceEnd2) ? str.indexOf("]") : str
						.indexOf(">");
				if (index != -1) {
					String faceStr = content2.substring(start2, index + start2
							+ 1);
					Pattern patten = Pattern.compile(faceRegx,
							Pattern.CASE_INSENSITIVE);
					Matcher matcher = patten.matcher(faceStr);
					if (matcher.find()) {
						et_release.setSelection(start2 + faceStr.length());
					} else {
						if (start2 + 1 <= content2.length()) {
							et_release.setSelection(start2 + 1);
						}
					}
				}
			} else {
				if (start2 + 1 <= content2.length()) {
					et_release.setSelection(start2 + 1);
				}
			}

			break;
		case R.id.release_iv_face_delete:

			int start = et_release.getSelectionStart();
			String content = et_release.getText().toString();
			if (start - 1 < 0)
				return;
			String faceEnd = content.substring(start - 1, start);
			if ("]".equals(faceEnd) || ">".equals(faceEnd)) {
				String str = content.substring(0, start);
				int index = "]".equals(faceEnd) ? str.lastIndexOf("[") : str
						.lastIndexOf("<");
				if (index != -1) {
					String faceStr = content.substring(index, start);
					Pattern patten = Pattern.compile(faceRegx,
							Pattern.CASE_INSENSITIVE);
					Matcher matcher = patten.matcher(faceStr);
					if (matcher.find()) {
						et_release.setText(content.substring(0,
								start - faceStr.length())
								+ content.substring(start));
						et_release.setSelection(start - faceStr.length());
					} else {
						if (start - 1 >= 0) {
							et_release.setText(content.substring(0, start - 1)
									+ content.substring(start));
							et_release.setSelection(start - 1);
						}
					}
				}
			} else {
				if (start - 1 >= 0) {
					et_release.setText(content.substring(0, start - 1)
							+ content.substring(start));
					et_release.setSelection(start - 1);
				}
			}

			break;
		default:
			break;
		}

	}

	public void faceStartAnimation() {
		rl_face.setVisibility(View.VISIBLE);
		Animation faceanimation = new TranslateAnimation(0, 0,
				240 * density + 0.5f, 0);
		faceanimation.setDuration(220);

		Animation navigationanimation = new TranslateAnimation(0, 0,
				240 * density + 0.5f, 0);
		navigationanimation.setDuration(220);
		navigationanimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				ll_navigation.clearAnimation();
				horizontalScrollView.clearAnimation();
				rl_face.clearAnimation();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}
		});

		Animation ScrollViewanimation = new TranslateAnimation(0, 0,
				240 * density + 0.5f, 0);
		ScrollViewanimation.setDuration(220);

		ll_navigation.startAnimation(navigationanimation);
		horizontalScrollView.startAnimation(ScrollViewanimation);
		rl_face.startAnimation(faceanimation);

		faceVisible = true;
		isEditText = false;
	}

	public void faceEndAnimation() {
		Animation navigationanimation = new TranslateAnimation(0, 0, 0,
				240 * density + 0.5f);
		navigationanimation.setDuration(220);
		navigationanimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				rl_face.setVisibility(View.GONE);
				rl_face.clearAnimation();
				ll_navigation.clearAnimation();
				horizontalScrollView.clearAnimation();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}
		});
		Animation faceanimation = new TranslateAnimation(0, 0, 0,
				240 * density + 0.5f);
		faceanimation.setDuration(220);

		Animation ScrollViewanimation = new TranslateAnimation(0, 0, 0,
				240 * density + 0.5f);
		ScrollViewanimation.setDuration(220);

		ll_navigation.startAnimation(navigationanimation);
		rl_face.startAnimation(faceanimation);
		horizontalScrollView.startAnimation(ScrollViewanimation);

		faceVisible = false;
		isEditText = true;
	}

	@SuppressLint("DefaultLocale")
	public void Send() {
		jsonArray = new JSONArray();
		mCurrentSquareID = SquareFragment.mCurrentSquareID;
		// long time = System.currentTimeMillis();
		// if (time % 3 == 0) {
		// messageType = "精华";
		// } else if (time % 3 == 1) {
		// messageType = "活动";
		// } else if (time % 3 == 2) {
		// messageType = "吐槽";
		// }
		messageType = new JSONArray().toString();
		broadcast = et_release.getText().toString();
		if ((broadcast == null || broadcast.equals("")) && images.size() == 0
				&& voices.size() == 0) {
			Alert.showMessage("发送内容不能为空");
			return;
		}
		if (images.size() != 0) {
			for (int i = 0; i < images.size(); i++) {
				if (images.get(i).get("bitmap") != null) {
					final int j = i;
					// app.fileHandler.saveBitmap(new SaveBitmapInterface() {
					// @Override
					// public void setParams(SaveSettings settings) {
					// settings.source = (Bitmap) images.get(j).get(
					// "bitmap");
					// settings.compressFormat = settings.PNG;
					// settings.folder = app.sdcardImageFolder;
					// }
					//
					// @Override
					// public void onSuccess(String fileName, String base64) {
					//
					// }
					// });
				}
			}
		}
		if (broadcast == null || broadcast.equals("")) {
			if (images.size() == 0) {
				addVoiceToJson();
				contentType = "voice";
			} else if (voices.size() == 0) {
				addImageToJson();
				contentType = "image";
			} else {
				addVoiceToJson();
				addImageToJson();
				contentType = "voiceandimage";
			}
		} else if (images.size() == 0) {
			if (voices.size() == 0) {
				addTextToJson(broadcast);
				contentType = "text";
			} else {
				addVoiceToJson();
				addTextToJson(broadcast);
				contentType = "textandvoice";
			}
		} else if (voices.size() == 0) {
			addTextToJson(broadcast);
			addImageToJson();
			contentType = "textandimage";

		} else {
			addTextToJson(broadcast);
			addImageToJson();
			addVoiceToJson();
		}

		app.networkHandler.connection(new CommonNetConnection() {
			@SuppressLint("DefaultLocale")
			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.SQUARE_SENDSQUAREMESSAGE;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("head", app.data.user.head);
				params.put("nickName", app.data.user.nickName);
				params.put("gid", mCurrentSquareID);
				// Log.e("Coolspan", cover + "------" + currentCoverIndex);
				if (currentCoverIndex != -1) {
					String lastName = cover.substring(
							cover.lastIndexOf(".") + 1).toLowerCase();
					if ("aac".equals(lastName)) {
						params.put("cover", "voice");
					} else {
						params.put("cover", cover);
					}
				} else {
					params.put("cover", "none");
				}
				currentCoverIndex = -1;
				cover = "";
				try {
					JSONObject messageJSONObject = new JSONObject();
					messageJSONObject.put("messageType", messageType);
					messageJSONObject.put("contentType", contentType);
					messageJSONObject.put("content", jsonArray);
					params.put("message", messageJSONObject.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
				finish();
			}
		});
	}

	public void addTextToJson(String broadcast) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("type", "text");
			jsonObject.put("details", broadcast);
			jsonArray.put(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void addImageToJson() {
		for (int i = 0; i < images.size(); i++) {
			try {
				ImageMessageInfo imageMessageInfo = (ImageMessageInfo) images
						.get(i).get("file");
				checkImageOrVoice(imageMessageInfo,
						(String) images.get(i).get("contentType"),
						(String) images.get(i).get("path"), "image");
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("type", "image");
				jsonObject.put("details", imageMessageInfo.fileName);
				jsonArray.put(jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public void addVoiceToJson() {
		for (int i = 0; i < voices.size(); i++) {
			try {
				ImageMessageInfo imageMessageInfo = (ImageMessageInfo) voices
						.get(i).get("file");
				checkImageOrVoice(imageMessageInfo, "audio/x-mei-aac",
						(String) voices.get(i).get("path"), "voice");
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("type", "voice");
				jsonObject.put("details", imageMessageInfo.fileName);
				jsonArray.put(jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public void addImageFromLocal() {
		Intent selectFromGallery = new Intent(ReleaseActivity.this,
				MapStorageDirectoryActivity.class);
		startActivityForResult(selectFromGallery, RESULT_SELECTPICTURE);

	}

	public void addImageFromCamera() {
		tempFile = new File(app.sdcardImageFolder, "tempimage.jpg");
		int i = 1;
		while (tempFile.exists()) {
			tempFile = new File(app.sdcardImageFolder, "tempimage" + (i++)
					+ ".jpg");
		}
		Uri uri = Uri.fromFile(tempFile);
		Intent tackPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		tackPicture.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
		tackPicture.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(tackPicture, RESULT_TAKEPICTURE);

	}

	public void onActivityResult(final int requestCode, int resultCode,
			Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT_MAKEVOICE && resultCode == Activity.RESULT_OK
				&& data != null) {
			final String voiceName = data.getExtras().getString("fileName");
			app.fileHandler.getFileMessageInfo(new FileMessageInfoInterface() {

				@Override
				public void setParams(FileMessageInfoSettings settings) {
					settings.fileName = voiceName;
					settings.folder = app.sdcardVoiceFolder;
					settings.FILE_TYPE = OSSFileHandler.FILE_TYPE_SDVOICE;
				}

				@Override
				public void onSuccess(ImageMessageInfo imageMessageInfo) {
					final Map<String, Object> map = new HashMap<String, Object>();
					map.put("file", imageMessageInfo);
					File voiceFile = new File(app.sdcardVoiceFolder, voiceName);
					File toFile = new File(app.sdcardVoiceFolder,
							imageMessageInfo.fileName);
					if (voiceFile.exists()) {
						voiceFile.renameTo(toFile);
						map.put("path", toFile.getAbsolutePath());
					}
					voices.add(map);
					nodifyViews();
				}

			});

		} else if (requestCode == RESULT_PICANDVOICE
				&& resultCode == Activity.RESULT_OK) {
			nodifyViews();
		} else if (requestCode == RESULT_SELECTPICTURE
				&& resultCode == Activity.RESULT_OK) {
			// String picturePath = "";
			String format = "";
			// Uri selectedImage = data.getData();
			// String[] filePathColumn = { MediaStore.Images.Media.DATA };
			// Cursor cursor = getContentResolver().query(selectedImage,
			// filePathColumn, null, null, null);
			// cursor.moveToFirst();
			// int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			// picturePath = cursor.getString(columnIndex).toLowerCase(
			// Locale.getDefault());
			// format = picturePath.substring(picturePath.lastIndexOf("."));
			// cursor.close();
			// System.out.println(MapStorageDirectoryActivity.selectedImages
			// .size() + "...||||||||||||||||||||||");
			for (int i = 0; i < MapStorageDirectoryActivity.selectedImages
					.size(); i++) {
				final String filePath = MapStorageDirectoryActivity.selectedImages
						.get(i);
				format = filePath.substring(filePath.lastIndexOf("."));
				final Bitmap bitmap = MCImageUtils.getZoomBitmapFromFile(
						new File(filePath), 960, 540);

				Map<String, Object> map = MapStorageDirectoryActivity.selectedImagesMap
						.get(filePath);
				if (map == null) {
					map = new HashMap<String, Object>();
				}
				// final String newformat = format;
				// final String newpicturePath = filePath;
				if (bitmap != null) {
					map.put("bitmap", bitmap);
				}
				final Map<String, Object> map0 = map;
				app.fileHandler
						.getFileMessageInfo(new FileMessageInfoInterface() {

							@Override
							public void setParams(
									FileMessageInfoSettings settings) {
								settings.FILE_TYPE = OSSFileHandler.FILE_TYPE_SDSELECTIMAGE;
								settings.path = filePath;
								settings.fileName = filePath.substring(filePath
										.lastIndexOf("/"));
							}

							@Override
							public void onSuccess(
									ImageMessageInfo imageMessageInfo) {
								map0.put("file", imageMessageInfo);
								map0.put("path", filePath);
								images.add(map0);
								nodifyViews();
							}
						});
			}
			MapStorageDirectoryActivity.selectedImages.clear();

		} else if (requestCode == RESULT_TAKEPICTURE
				&& resultCode == Activity.RESULT_OK) {
			String format = "";
			Uri uri = Uri.fromFile(tempFile);
			final String picturePath = uri.getPath();
			format = "";
			final Bitmap bitmap = MCImageUtils.getZoomBitmapFromFile(new File(
					picturePath), 960, 540);
			Map<String, Object> map = null;
			if (map == null) {
				map = new HashMap<String, Object>();
			}
			// final String newformat = format;
			// final String newpicturePath = filePath;
			if (bitmap != null) {
				map.put("bitmap", bitmap);
			}
			// Toast.makeText(ReleaseActivity.this, picturePath + "--",
			// Toast.LENGTH_LONG).show();
			final Map<String, Object> map0 = map;
			app.fileHandler.getFileMessageInfo(new FileMessageInfoInterface() {

				@Override
				public void setParams(FileMessageInfoSettings settings) {
					settings.FILE_TYPE = OSSFileHandler.FILE_TYPE_SDSELECTIMAGE;
					settings.path = picturePath;
					settings.fileName = picturePath.substring(picturePath
							.lastIndexOf("/"));
				}

				@Override
				public void onSuccess(ImageMessageInfo imageMessageInfo) {
					map0.put("file", imageMessageInfo);
					map0.put("path", picturePath);
					map0.put("contentType", "image/jpeg");
					File toFile = new File(app.sdcardImageFolder,
							imageMessageInfo.fileName);
					tempFile.renameTo(toFile);
					images.add(map0);
					nodifyViews();
				}
			});
		}
		rl_releasepic.setVisibility(View.GONE);
		sl_et_release.setVisibility(View.VISIBLE);
		ll_navigation.setVisibility(View.VISIBLE);
		seletePic = false;

	}

	public void checkImageOrVoice(final ImageMessageInfo imageMessageInfo,
			final String contentType, final String path, final String fileType) {
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
						// JSONObject jsonObject = new JSONObject();
						// try {
						// jsonObject.put("type", type);
						// jsonObject.put("details", fileName);
						// } catch (JSONException e) {
						// e.printStackTrace();
						// }
						// jsonArray.put(jsonObject);

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
								System.out.println(flag + "-+-" + fileName);
							}
						});
					}
				} catch (JSONException e) {
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
				// JSONObject jsonObject = new JSONObject();
				// try {
				// jsonObject.put("type", type);
				// jsonObject.put("details", fileName);
				// } catch (JSONException e) {
				// e.printStackTrace();
				// }
				// jsonArray.put(jsonObject);
			}

		});
	}

	private void nodifyViews() {
		app.UIHandler.post(new Runnable() {
			@Override
			public void run() {
				if (images.size() != 0 || voices.size() != 0) {
					horizontalScrollView.setVisibility(View.VISIBLE);
				}else{
					horizontalScrollView.setVisibility(View.GONE);
				}
				int index = 0;
				ll_release_picandvoice.removeAllViews();
				for (int i = 0; i < voices.size(); i++) {
					index++;
					generateView(voices.get(i), "voice", index);
				}
				for (int i = 0; i < images.size(); i++) {
					index++;
					generateView(images.get(i), "image", index);
				}
			}
		});
	}

	public void generateView(Map<String, Object> map, String type,
			final int index) {
		View addView = mInflater.inflate(R.layout.view_child, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				(int) (width * 0.138888889f), (int) (height * 0.078125f));
		params.rightMargin = (int) (width * 0.01388889f);

		if ("image".equals(type)) {
			ImageView iv = (ImageView) addView.findViewById(R.id.iv_child);

			iv.setLayoutParams(params);
			if (map.get("bitmap") != null)
				iv.setImageBitmap(MCImageUtils.getCutBitmap(
						(Bitmap) map.get("bitmap"),
						(int) (width * 0.138888889f),
						(int) (height * 0.078125f), 0));

			iv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(ReleaseActivity.this,
							PicAndVoiceDetailActivity.class);
					intent.putExtra("currentIndex", index);
					intent.putExtra("Activity", "ReleaseActivity");
					startActivityForResult(intent, RESULT_PICANDVOICE);
				}
			});
			ll_release_picandvoice.addView(addView);
		} else if ("voice".equals(type)) {
			ImageView iv = (ImageView) addView.findViewById(R.id.iv_child);

			iv.setLayoutParams(params);
			if (map.get("bitmap") == null)
				iv.setImageResource(R.drawable.selected_voice);

			iv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(ReleaseActivity.this,
							PicAndVoiceDetailActivity.class);
					intent.putExtra("currentIndex", index);
					intent.putExtra("Activity", "ReleaseActivity");
					startActivityForResult(intent, RESULT_PICANDVOICE);
				}
			});
			ll_release_picandvoice.addView(addView);
		}
	}

	public void checkBack() {
		String msg = et_release.getText().toString();
		if (!msg.equals("") || voices.size() != 0 || images.size() != 0) {
			Alert.createDialog(this).setTitle("您尚有编辑未提交,是否退出?")
					.setOnConfirmClickListener(new OnDialogClickListener() {
						@Override
						public void onClick(AlertInputDialog dialog) {
							finish();
						}
					}).show();
		} else {
			finish();
		}
	}

	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}

	class MyGridAdapter extends BaseAdapter {
		List<String> list;

		public MyGridAdapter(List<String> list) {
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
				iv.setImageBitmap(BitmapFactory.decodeStream(getBaseContext()
						.getAssets().open("images/" + list.get(position))));
			} catch (IOException e) {
				e.printStackTrace();
			}
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (chat_vPager_now < 2) {
						et_release.getText().insert(
								et_release.getSelectionStart(),
								faceNamesList.get(chat_vPager_now)[position]);
					} else {
						// TODO
						addGif(position);
					}
				}
			});
			return convertView;
		}
	}

	public void addGif(final int position) {
		InputStream is = null;
		try {
			is = getResources().getAssets().open(
					"images/" + faceNameList.get(2).get(position));
			final Map<String, Object> map = new HashMap<String, Object>();
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			map.put("bitmap", bitmap);
			map.put("type", "gif");

			app.fileHandler.getFileMessageInfo(new FileMessageInfoInterface() {

				@Override
				public void setParams(FileMessageInfoSettings settings) {
					settings.assetsPath = "images/";
					settings.FILE_TYPE = OSSFileHandler.FILE_TYPE_ASSETS;
					settings.fileName = faceNameList.get(2).get(position);
				}

				@Override
				public void onSuccess(ImageMessageInfo imageMessageInfo) {
					map.put("fileName", imageMessageInfo.fileName);
					map.put("data", imageMessageInfo.data);
					images.add(map);
					nodifyViews();
				}
			});

			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	class MyPageAdapter extends PagerAdapter {
		List<View> mListViews;

		public MyPageAdapter(List<View> mListViews) {
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
					((ViewGroup) mListViews.get(arg1).getParent())
							.removeView(mListViews.get(arg1));
					((ViewPager) arg0).addView(mListViews.get(arg1), 0);
				}
			} catch (Exception e) {
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
