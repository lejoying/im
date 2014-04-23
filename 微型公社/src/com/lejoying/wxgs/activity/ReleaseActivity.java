package com.lejoying.wxgs.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.view.BackgroundView;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.handler.FileHandler.BigFaceImgInterface;
import com.lejoying.wxgs.app.handler.FileHandler.BigFaceImgSettings;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ReleaseActivity extends BaseActivity implements OnClickListener {
	BackgroundView mBackground;
	MainApplication app = MainApplication.getMainApplication();

	int height, width, dip;
	int chat_vPager_now = 0;
	float density;

	List<String[]> faceNamesList;
	List<List<String>> faceNameList;
	List<ImageView> faceMenuShowList;
	static Map<String, String> expressionFaceMap = new HashMap<String, String>();

	LinearLayout ll_facemenu;
	RelativeLayout rl_face;
	ViewPager chat_vPager;
	LayoutInflater mInflater;

	EditText et_release;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_post);
		mInflater = getLayoutInflater();
		getWindow().setBackgroundDrawableResource(R.drawable.square_background);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		dip = (int) (45 * density + 0.5f);
		height = dm.heightPixels;
		width = dm.widthPixels;
		initData();
		initEvent();
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		CircleMenu.hide();
		super.onResume();
	}

	public void initEvent() {
		LinearLayout ll_navigation = (LinearLayout) findViewById(R.id.release_ll_navigation);
		ImageView iv_selectpicture = (ImageView) findViewById(R.id.release_iv_selectpicture);
		ImageView iv_emoji = (ImageView) findViewById(R.id.release_iv_emoji);
		ImageView iv_voice = (ImageView) findViewById(R.id.release_iv_voice);
		TextView tv_cancel = (TextView) findViewById(R.id.release_tv_cancel);
		TextView tv_commit = (TextView) findViewById(R.id.release_tv_commit);

		et_release = (EditText) findViewById(R.id.release_et_release);

		ll_facemenu = (LinearLayout)findViewById(R.id.release_ll_facemenu);
		rl_face = (RelativeLayout) findViewById(R.id.release_rl_face);

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

		iv_selectpicture.setOnClickListener(this);
		iv_emoji.setOnClickListener(this);
		iv_voice.setOnClickListener(this);
		tv_cancel.setOnClickListener(this);
		tv_commit.setOnClickListener(this);

	}

	public void initData() {
		initFace();
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
				ImageView iv = new ImageView(getBaseContext());
				iv.setImageBitmap(BitmapFactory.decodeStream(this.getAssets()
						.open("images/" + faceNameList.get(i).get(0))));
				iv.setLayoutParams(lp);
				if (i == 0) {
					// iv.setBackgroundColor(Color.RED);
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

			break;
		case R.id.release_iv_emoji:

			break;
		case R.id.release_iv_voice:

			break;
		case R.id.release_tv_cancel:
			finish();
			break;
		case R.id.release_tv_commit:

			break;
		default:
			break;
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
					}
					// else {
					// rl_face.setVisibility(View.GONE);
					// app.fileHandler.getBigFaceImgBASE64(getActivity(),
					// new BigFaceImgInterface() {
					//
					// @Override
					// public void setParams(
					// BigFaceImgSettings settings) {
					// settings.format = ".gif";
					// settings.assetsPath = "images/";
					// settings.fileName = "tusiji_"
					// + (position + 1) + ".gif";
					// }
					//
					// @Override
					// public void onSuccess(String fileName,
					// String base64) {
					// checkImage(fileName, base64);
					// }
					// });
					// }
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
				// Log.d("parent=", "" + mListViews.get(arg1).getParent());
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
