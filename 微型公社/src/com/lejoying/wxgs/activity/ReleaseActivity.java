package com.lejoying.wxgs.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.utils.ExpressionUtil;
import com.lejoying.wxgs.activity.utils.MCImageUtils;
import com.lejoying.wxgs.activity.view.BackgroundView;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.activity.view.widget.Alert.AlertInputDialog.OnDialogClickListener;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.activity.view.widget.Alert.AlertInputDialog;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.handler.FileHandler.SaveBitmapInterface;
import com.lejoying.wxgs.app.handler.FileHandler.SaveSettings;
import com.lejoying.wxgs.app.handler.FileHandler.VoiceInterface;
import com.lejoying.wxgs.app.handler.FileHandler.VoiceSettings;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ReleaseActivity extends BaseActivity implements OnClickListener {
	BackgroundView mBackground;
	MainApplication app = MainApplication.getMainApplication();

	int height, width, dip, picwidth;
	int chat_vPager_now = 0;
	static int RESULT_SELECTPICTURE = 0x34, RESULT_TAKEPICTURE = 0x54,
			RESULT_MAKEVOICE = 0x64;
	float density;
	boolean isEditText = true, faceVisible = false, seletePic = false;
	List<Map<String, Object>> voice;
	List<Map<String, Object>> image;
	List<String[]> faceNamesList;
	List<List<String>> faceNameList;
	List<ImageView> faceMenuShowList;
	static Map<String, String> expressionFaceMap = new HashMap<String, String>();
	File tempFile;

	RelativeLayout rl_face;
	RelativeLayout rl_releasepic;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_post);
		mInflater = getLayoutInflater();
		getWindow().setBackgroundDrawableResource(R.drawable.square_background);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		dip = (int) (40 * density + 0.5f);
		height = dm.heightPixels;
		width = dm.widthPixels;
		initEvent();
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
		}
	}

	public void initEvent() {
		ll_navigation = (LinearLayout) findViewById(R.id.release_ll_navigation);
		ImageView iv_selectpicture = (ImageView) findViewById(R.id.release_iv_selectpicture);
		ImageView iv_emoji = (ImageView) findViewById(R.id.release_iv_emoji);
		ImageView iv_voice = (ImageView) findViewById(R.id.release_iv_voice);
		TextView tv_cancel = (TextView) findViewById(R.id.release_tv_cancel);
		TextView tv_commit = (TextView) findViewById(R.id.release_tv_commit);

		release_iv_face_left = findViewById(R.id.release_iv_face_left);
		release_iv_face_right = findViewById(R.id.release_iv_face_right);
		;
		release_iv_face_delete = findViewById(R.id.release_iv_face_delete);
		;
		et_release = (EditText) findViewById(R.id.release_et_release);

		ll_et_release = (LinearLayout) findViewById(R.id.ll_et_release);
		rl_releasepic = (RelativeLayout) findViewById(R.id.rl_releasepic);
		ll_releasecamera = (LinearLayout) findViewById(R.id.ll_releasecamera);
		ll_releaselocal = (LinearLayout) findViewById(R.id.ll_releaselocal);
		ll_facemenu = (LinearLayout) findViewById(R.id.release_ll_facemenu);
		rl_face = (RelativeLayout) findViewById(R.id.release_rl_face);
		horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
		ll_release_picandvoice = (LinearLayout) findViewById(R.id.ll_release_picandvoice);
		chat_vPager = (ViewPager) findViewById(R.id.release_chat_vPager);

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				dip, dip);
		layoutParams.leftMargin = (width - (dip * 5)) / 10;
		layoutParams.rightMargin = (width - (dip * 5)) / 10;
		layoutParams.bottomMargin = 20;
		layoutParams.topMargin = 20;
		iv_selectpicture.setLayoutParams(layoutParams);
		iv_emoji.setLayoutParams(layoutParams);
		iv_voice.setLayoutParams(layoutParams);
		tv_cancel.setLayoutParams(layoutParams);
		tv_commit.setLayoutParams(layoutParams);

		RelativeLayout.LayoutParams relativeParams1 = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		relativeParams1.width = width / 2;
		RelativeLayout.LayoutParams relativeParams2 = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		relativeParams2.width = width / 2;
		relativeParams2.addRule(RelativeLayout.RIGHT_OF, R.id.ll_releasecamera);
		ll_releasecamera.setLayoutParams(relativeParams1);
		ll_releaselocal.setLayoutParams(relativeParams2);

		RelativeLayout.LayoutParams relativeParams3 = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		relativeParams3.addRule(RelativeLayout.ABOVE,
				R.id.release_ll_navigation);
		horizontalScrollView.setLayoutParams(relativeParams3);

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
		image = new ArrayList<Map<String, Object>>();
		voice = new ArrayList<Map<String, Object>>();
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
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.release_iv_selectpicture:
			if (faceVisible) {
				rl_face.setVisibility(View.GONE);
				faceVisible = false;
			}
			horizontalScrollView.setVisibility(View.GONE);
			rl_releasepic.setVisibility(View.VISIBLE);
			et_release.setVisibility(View.GONE);
			ll_navigation.setVisibility(View.GONE);
			seletePic = true;
			break;
		case R.id.release_iv_emoji:
			if (faceVisible) {
				faceEndAnimation();
			} else {
				faceStartAnimation();
			}
			break;
		case R.id.release_iv_voice:
			Intent intent = new Intent(ReleaseActivity.this,
					SendVoiceActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
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
					rl_face.setVisibility(View.GONE);
					RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT);
					layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					ll_navigation.setLayoutParams(layoutParams);
					LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.MATCH_PARENT);
					et_release.setLayoutParams(layoutParams2);
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
			rl_releasepic.setVisibility(View.GONE);
			horizontalScrollView.setVisibility(View.VISIBLE);
			et_release.setVisibility(View.VISIBLE);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			ll_navigation.setLayoutParams(layoutParams);
			ll_navigation.setVisibility(View.VISIBLE);
			seletePic = false;
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
		Animation navigationanimation = new TranslateAnimation(0, 0, 0, -240
				* density + 0.5f);
		navigationanimation.setDuration(220);
		navigationanimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				ll_navigation.clearAnimation();
				horizontalScrollView.clearAnimation();
				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				layoutParams
						.addRule(RelativeLayout.ABOVE, R.id.release_rl_face);
				ll_navigation.setLayoutParams(layoutParams);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				layoutParams.height = (int) (height - ll_navigation.getHeight()
						- (240 * density + 0.5f) - 40
						- ((width - (7 * 10)) / 6) - 10);
				et_release.setLayoutParams(layoutParams);
			}
		});
		Animation faceanimation = new TranslateAnimation(0, 0,
				240 * density + 0.5f, 0);
		faceanimation.setDuration(220);
		Animation ScrollViewanimation = new TranslateAnimation(0, 0, 0, -240
				* density + 0.5f);
		ScrollViewanimation.setDuration(220);

		ll_navigation.startAnimation(navigationanimation);
		rl_face.startAnimation(faceanimation);
		horizontalScrollView.startAnimation(ScrollViewanimation);

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
				ll_navigation.clearAnimation();
				horizontalScrollView.clearAnimation();
				RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				ll_navigation.setLayoutParams(layoutParams1);
				LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				layoutParams2.height = (int) (height
						- ll_navigation.getHeight() - 40
						- ((width - (7 * 10)) / 6) - 10);
				et_release.setLayoutParams(layoutParams2);
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

	public void Send() {
		jsonArray = new JSONArray();
		mCurrentSquareID = "98";
		messageType = "精华";
		broadcast = et_release.getText().toString();
		if ((broadcast == null || broadcast.equals("")) && image.size() == 0
				&& voice.size() == 0) {
			Alert.showMessage("发送内容不能为空");
			return;
		}
		if (broadcast == null || broadcast.equals("")) {
			if (image.size() == 0) {
				addVoiceToJson();
				contentType = "voice";
			} else if (voice.size() == 0) {
				addImageToJson();
				contentType = "image";
			} else {
				addVoiceToJson();
				addImageToJson();
				contentType = "voiceandimage";
			}
		} else if (image.size() == 0) {
			if (voice.size() == 0) {
				addTextToJson(broadcast);
				contentType = "text";
			} else {
				addVoiceToJson();
				addTextToJson(broadcast);
				contentType = "textandvoice";
			}
		} else if (voice.size() == 0) {
			addTextToJson(broadcast);
			addImageToJson();
			contentType = "textandimage";

		} else {
			addTextToJson(broadcast);
			addImageToJson();
			addVoiceToJson();
		}

		app.networkHandler.connection(new CommonNetConnection() {
			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.SQUARE_SENDSQUAREMESSAGE;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("head", app.data.user.head);
				params.put("nickName", app.data.user.nickName);
				params.put("gid", mCurrentSquareID);
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
		for (int i = 0; i < image.size(); i++) {
			try {
				String fileName = (String) image.get(i).get("fileName");
				checkImageOrVoice(fileName, (String) image.get(i).get("base64"));
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("type", "image");
				jsonObject.put("details", fileName);
				jsonArray.put(jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public void addVoiceToJson() {
		for (int i = 0; i < voice.size(); i++) {
			try {
				String fileName = (String) voice.get(i).get("fileName");
				checkImageOrVoice(fileName, (String) voice.get(i).get("base64"));
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("type", "voice");
				jsonObject.put("details", voice.get(i).get("fileName"));
				jsonArray.put(jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public void addImageFromLocal(){
		Intent selectFromGallery = new Intent(Intent.ACTION_PICK,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(selectFromGallery, RESULT_SELECTPICTURE);

	}

	public void addImageFromCamera() {
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

	public void onActivityResult(final int requestCode, int resultCode,
			Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT_MAKEVOICE && resultCode == Activity.RESULT_OK
				&& data != null) {
			final String voiceName = data.getExtras().getString("fileName");
			app.fileHandler.getVoice(new VoiceInterface() {

				@Override
				public void setParams(VoiceSettings settings) {
					settings.fileName = voiceName;
					settings.format = ".aac";
				}

				@Override
				public void onSuccess(String filename, String base64,
						Boolean flag) {
					final Map<String, Object> map = new HashMap<String, Object>();
					map.put("fileName", filename);
					map.put("base64", base64);
					File voiceFile = new File(app.sdcardVoiceFolder, voiceName);
					if (voiceFile.exists()) {
						voiceFile.renameTo(new File(app.sdcardVoiceFolder,
								filename));
					}
					voice.add(map);
					nodifyViews();
				}
			});

		} else {
			String picturePath = "";
			String format = "";
			if (requestCode == RESULT_SELECTPICTURE
					&& resultCode == Activity.RESULT_OK && data != null) {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				picturePath = cursor.getString(columnIndex).toLowerCase(
						Locale.getDefault());
				format = picturePath.substring(picturePath.lastIndexOf("."));
				cursor.close();

			} else if (requestCode == RESULT_TAKEPICTURE
					&& resultCode == Activity.RESULT_OK) {
				Uri uri = Uri.fromFile(tempFile);
				picturePath = uri.getPath();
				format = "";
			}

			final Map<String, Object> map = new HashMap<String, Object>();
			final Bitmap bitmap = MCImageUtils.getZoomBitmapFromFile(new File(
					picturePath), 960, 540);
			final String newformat = format;
			final String newpicturePath = picturePath;
			map.put("bitmap", bitmap);

			if (bitmap != null) {
				app.fileHandler.saveBitmap(new SaveBitmapInterface() {

					@Override
					public void setParams(SaveSettings settings) {
						settings.compressFormat = newformat.equals(".jpg") ? settings.JPG
								: settings.PNG;
						settings.source = bitmap;
					}

					@Override
					public void onSuccess(String fileName, String base64) {
						map.put("fileName", fileName);
						map.put("base64", base64);
						image.add(map);
						nodifyViews();
						if (requestCode == RESULT_TAKEPICTURE) {
							File file = new File(newpicturePath);
							if (file.isFile() && file.exists()) {
								file.delete();
							}
						}
					}
				});
			}
		}
	}

	public void checkImageOrVoice(final String fileName, final String base64) {
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
						// JSONObject jsonObject = new JSONObject();
						// try {
						// jsonObject.put("type", type);
						// jsonObject.put("details", fileName);
						// } catch (JSONException e) {
						// e.printStackTrace();
						// }
						// jsonArray.put(jsonObject);

					} else {
						uploadImageOrVoice("image", fileName, base64);
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
				ll_release_picandvoice.removeAllViews();
				for (int i = 0; i < voice.size(); i++) {
					addView(voice.get(i));
				}
				for (int i = 0; i < image.size(); i++) {
					addView(image.get(i));
				}
			}
		});
	}

	public void addView(Map<String, Object> map) {
		View addView = mInflater.inflate(R.layout.release_child_navigation,
				null);
		ImageView iv = (ImageView) addView.findViewById(R.id.iv_release_child);
		LinearLayout.LayoutParams ivParems = new LinearLayout.LayoutParams(
				(width - (7 * 10)) / 6, (width - (7 * 10)) / 6);
		ivParems.bottomMargin = 10;
		ivParems.leftMargin = 10;
		iv.setLayoutParams(ivParems);
		if (map.get("bitmap") != null)
			iv.setImageBitmap((Bitmap) map.get("bitmap"));
		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(ReleaseActivity.this,
						PicAndVoiceDetailActivity.class);
				// intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
			}
		});
		ll_release_picandvoice.addView(addView);
	}

	public void checkBack() {
		String msg = et_release.getText().toString();
		if (!msg.equals("") || voice.size() != 0 || image.size() != 0) {
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
