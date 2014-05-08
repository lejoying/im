package com.lejoying.wxgs.activity.mode.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.SquareMessageDetail;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.view.SquareContentView;
import com.lejoying.wxgs.activity.view.SquareContentView.OnItemClickListener;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.data.entity.SquareMessage;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.parser.JSONParser;
import com.lejoying.wxgs.app.service.PushService;

public class SquareFragment extends BaseFragment {

	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;
	View mContentView;

	LayoutInflater mInflater;

	ListView mSqureMessageView;

	public SquareContentView squareContentView;

	RelativeLayout currentSquareEssenceMessage;
	RelativeLayout currentSquareAllMessage;
	RelativeLayout currentSquareActivityMessage;
	RelativeLayout currentSquareShitsMessage;
	TextView currentSquareNoReadEssence;
	TextView currentSquareNoReadAll;
	TextView currentSquareNoReadActivity;
	TextView currentSquareNoReadShits;
	ImageView currentSquareStatusEssence;
	ImageView currentSquareStatusAll;
	ImageView currentSquareStatusActivity;
	ImageView currentSquareStatusShits;

	ImageView currentSquareMessageClassify;

	EditText mViewBroadcast;
	View mButtonSend;

	List<SquareMessage> mSquareMessages;
	public static Map<String, String> expressionFaceMap = new HashMap<String, String>();
	public static String mCurrentSquareID = "98";

	private HorizontalScrollView horizontalScrollView;
	private LinearLayout linearLayout;
	// 滚动条的宽度
	private int hsv_width;
	// 总共有多少个view
	private int child_count;
	// 每一个view的宽度
	private int child_width;
	// 预计显示在屏幕上的view的个数
	private int child_show_count;
	// 一开始居中选中的view
	private int child_start;

	int height, width, dip;
	float density;

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		String flag = app.data.squareFlags.get(mCurrentSquareID);
		flag = flag == null ? "0" : flag;
		PushService.startSquareLongPull(getActivity(), mCurrentSquareID, flag);
	}

	@Override
	public void onResume() {
		// CircleMenu.show();
		// CircleMenu.setPageName(getString(R.string.circlemenu_page_square));
		notifyViews();
		super.onResume();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		mContentView = inflater.inflate(R.layout.fragment_square, null);
		squareContentView = (SquareContentView) mContentView
				.findViewById(R.id.stv_squrecontentview);
		// currentSquareEssenceMessage = (RelativeLayout) mContentView
		// .findViewById(R.id.current_square_essence_message);
		// currentSquareAllMessage = (RelativeLayout) mContentView
		// .findViewById(R.id.current_square_all_message);
		// currentSquareActivityMessage = (RelativeLayout) mContentView
		// .findViewById(R.id.current_square_activity_message);
		// currentSquareShitsMessage = (RelativeLayout) mContentView
		// .findViewById(R.id.current_square_shits_message);
		// currentSquareStatusEssence = (ImageView) mContentView
		// .findViewById(R.id.current_square_status_essence);
		// currentSquareStatusAll = (ImageView) mContentView
		// .findViewById(R.id.current_square_status_all);
		// currentSquareStatusActivity = (ImageView) mContentView
		// .findViewById(R.id.current_square_status_activity);
		// currentSquareStatusShits = (ImageView) mContentView
		// .findViewById(R.id.current_square_status_shits);
		// currentSquareNoReadEssence = (TextView) mContentView
		// .findViewById(R.id.current_square_noread_essence);
		// currentSquareNoReadAll = (TextView) mContentView
		// .findViewById(R.id.current_square_noread_all);
		// currentSquareNoReadActivity = (TextView) mContentView
		// .findViewById(R.id.current_square_noread_activity);
		// currentSquareNoReadShits = (TextView) mContentView
		// .findViewById(R.id.current_square_noread_shits);
		currentSquareMessageClassify = currentSquareStatusAll;
		final List<String> messages = app.data.squareMessages
				.get(mCurrentSquareID);
		final Map<String, SquareMessage> squareMessageMap = app.data.squareMessagesMap
				.get(mCurrentSquareID);
		if (messages != null) {
			app.UIHandler.post(new Runnable() {

				@Override
				public void run() {
					squareContentView.setSquareMessageList(messages,
							squareMessageMap);
					squareContentView.notifyDataSetChanged();
				}
			});
		}
		initEvent();
		initFaceMap();
		app.UIHandler.post(new Runnable() {

			@Override
			public void run() {
				init();
			}
		});
		return mContentView;
	}

	private void initEvent() {
//		currentSquareShitsMessage.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (currentSquareMessageClassify != currentSquareStatusShits) {
//					currentSquareMessageClassify.setVisibility(View.GONE);
//					currentSquareStatusShits.setVisibility(View.VISIBLE);
//					currentSquareMessageClassify = currentSquareStatusShits;
//					// squareContentView.removeAllViews();
//					List<String> ShitsMessageClassify = app.data.squareMessagesClassify
//							.get(mCurrentSquareID).get("吐槽");
//					Map<String, SquareMessage> squareMessageMap = app.data.squareMessagesMap
//							.get(mCurrentSquareID);
//					if (ShitsMessageClassify == null) {
//						ShitsMessageClassify = new ArrayList<String>();
//					}
//					if (squareMessageMap == null) {
//						squareMessageMap = new HashMap<String, SquareMessage>();
//					}
//					// squareContentView.justSetSquareMessageList(
//					// ShitsMessageClassify, squareMessageMap);
//					squareContentView.setSquareMessageList(
//							ShitsMessageClassify, squareMessageMap);
//					app.UIHandler.post(new Runnable() {
//
//						@Override
//						public void run() {
//							squareContentView.notifyDataSetChanged();
//						}
//					});
//				}
//			}
//		});
//		currentSquareActivityMessage.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (currentSquareMessageClassify != currentSquareStatusActivity) {
//					currentSquareMessageClassify.setVisibility(View.GONE);
//					currentSquareStatusActivity.setVisibility(View.VISIBLE);
//					currentSquareMessageClassify = currentSquareStatusActivity;
//					// squareContentView.removeAllViews();
//					List<String> ActivityMessageClassify = app.data.squareMessagesClassify
//							.get(mCurrentSquareID).get("活动");
//					Map<String, SquareMessage> squareMessageMap = app.data.squareMessagesMap
//							.get(mCurrentSquareID);
//					if (ActivityMessageClassify == null) {
//						ActivityMessageClassify = new ArrayList<String>();
//					}
//					if (squareMessageMap == null) {
//						squareMessageMap = new HashMap<String, SquareMessage>();
//					}
//					// squareContentView.justSetSquareMessageList(
//					// ActivityMessageClassify, squareMessageMap);
//					squareContentView.setSquareMessageList(
//							ActivityMessageClassify, squareMessageMap);
//					app.UIHandler.post(new Runnable() {
//
//						@Override
//						public void run() {
//							squareContentView.notifyDataSetChanged();
//						}
//					});
//				}
//			}
//		});
//		currentSquareAllMessage.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (currentSquareMessageClassify != currentSquareStatusAll) {
//					currentSquareMessageClassify.setVisibility(View.GONE);
//					currentSquareStatusAll.setVisibility(View.VISIBLE);
//					currentSquareMessageClassify = currentSquareStatusAll;
//					// squareContentView.removeAllViews();
//					List<String> AllMessages = app.data.squareMessages
//							.get(mCurrentSquareID);
//					Map<String, SquareMessage> squareMessageMap = app.data.squareMessagesMap
//							.get(mCurrentSquareID);
//					if (AllMessages == null) {
//						AllMessages = new ArrayList<String>();
//					}
//					if (squareMessageMap == null) {
//						squareMessageMap = new HashMap<String, SquareMessage>();
//					}
//					squareContentView.setSquareMessageList(AllMessages,
//							squareMessageMap);
//					// squareContentView.justSetSquareMessageList(AllMessages,
//					// squareMessageMap);
//					app.UIHandler.post(new Runnable() {
//
//						@Override
//						public void run() {
//							squareContentView.notifyDataSetChanged();
//						}
//					});
//				}
//			}
//		});
//		currentSquareEssenceMessage.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (currentSquareMessageClassify != currentSquareStatusEssence) {
//					currentSquareMessageClassify.setVisibility(View.GONE);
//					currentSquareStatusEssence.setVisibility(View.VISIBLE);
//					currentSquareMessageClassify = currentSquareStatusEssence;
//					// squareContentView.removeAllViews();
//					List<String> EssenceMessageClassify = app.data.squareMessagesClassify
//							.get(mCurrentSquareID).get("精华");
//					Map<String, SquareMessage> squareMessageMap = app.data.squareMessagesMap
//							.get(mCurrentSquareID);
//					if (EssenceMessageClassify == null) {
//						EssenceMessageClassify = new ArrayList<String>();
//					}
//					if (squareMessageMap == null) {
//						squareMessageMap = new HashMap<String, SquareMessage>();
//					}
//					squareContentView.setSquareMessageList(
//							EssenceMessageClassify, squareMessageMap);
//					// squareContentView.justSetSquareMessageList(
//					// EssenceMessageClassify, squareMessageMap);
//					app.UIHandler.post(new Runnable() {
//
//						@Override
//						public void run() {
//							squareContentView.notifyDataSetChanged();
//						}
//					});
//				}
//			}
//		});
		squareContentView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(SquareMessage message) {
				Intent intent = new Intent(getActivity(),
						SquareMessageDetail.class);
				intent.putExtra("mCurrentSquareID", mCurrentSquareID);
				intent.putExtra("gmid", message.gmid);
				startActivity(intent);
			}
		});
	}

	public void notifyViews() {
		squareContentView.notifyDataSetChanged();
	}

	public void initFaceMap() {
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
		List<String[]> faceNamesList = new ArrayList<String[]>();
		faceNamesList.add(faceNames1);
		faceNamesList.add(faceNames2);
		for (int i = 0; i < 105; i++) {
			expressionFaceMap.put(faceNamesList.get(0)[i], "smiley_" + i
					+ ".png");
		}
		for (int i = 0; i < 77; i++) {
			expressionFaceMap.put(faceNamesList.get(1)[i], "emoji_" + i
					+ ".png");
		}
	}

	private void init() {
		horizontalScrollView = (HorizontalScrollView) mContentView
				.findViewById(R.id.horizontalScrollViewMenu);
		linearLayout = (LinearLayout) mContentView
				.findViewById(R.id.ll_horizontalScrollViewMenu);
		child_count = 10;
		child_show_count = 5;
		child_start = 2;
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		dip = (int) (40 * density + 0.5f);
		height = dm.heightPixels;
		width = dm.widthPixels;

		hsv_width = width;
		int child_width_temp = hsv_width / child_show_count;
		if (child_width_temp % 2 != 0) {
			child_width_temp++;
		}
		child_width = child_width_temp;
		initData();
		initHsvTouch();
		// initStart();
	}

	public int currentClassify = 2;

	/**
	 * 给滚动控件添加view，只有重复两个列表才能实现循环滚动
	 */
	private void initData() {
		String[] strs = { "吐槽", "精选", "全部", "活动", "服务", "吐槽", "精选", "全部", "活动",
				"服务" };
		for (int i = 0; i < child_count; i++) {
			View v = mInflater
					.inflate(R.layout.fragment_square_menu_item, null);
			TextView tv = (TextView) v.findViewById(R.id.tv_menu);
			TextView noread = (TextView) v.findViewById(R.id.tv_noread);
			// TextView textView = new TextView(getActivity());
			v.setLayoutParams(new ViewGroup.LayoutParams(child_width,
					ViewGroup.LayoutParams.MATCH_PARENT));
			tv.setText(strs[i]);
			tv.setTextSize(16);
			v.setTag(i % 5);
			// v.setGravity(Gravity.CENTER);
			linearLayout.addView(v);
			v.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int distance = 0;
					Toast.makeText(getActivity(), "" + v.getTag(),
							Toast.LENGTH_LONG).show();
					int id = (Integer) (v.getTag());
					if (currentClassify != id) {
						if (currentClassify > id) {
							distance = (id + 3) * child_width;
						} else {
							distance = (id - 2) * child_width;
						}
					}
					horizontalScrollView.smoothScrollTo(distance,
							horizontalScrollView.getScrollY());
					// Toast.makeText(getActivity(),
					// "" + (current_item % child_count),
					// Toast.LENGTH_LONG).show();
				}
			});
		}
		for (int i = 0; i < child_count; i++) {
			View v = mInflater
					.inflate(R.layout.fragment_square_menu_item, null);
			TextView tv = (TextView) v.findViewById(R.id.tv_menu);
			TextView noread = (TextView) v.findViewById(R.id.tv_noread);
			// TextView textView = new TextView(getActivity());
			v.setLayoutParams(new ViewGroup.LayoutParams(child_width,
					ViewGroup.LayoutParams.MATCH_PARENT));
			tv.setText(strs[i]);
			tv.setTextSize(16);
			v.setTag(i % 5);
			// tv.setGravity(Gravity.CENTER);
			linearLayout.addView(v);
			v.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int distance = 0;
					Toast.makeText(getActivity(), "" + v.getTag(),
							Toast.LENGTH_LONG).show();
					int id = (Integer) (v.getTag());
					if (currentClassify != id) {
						if (currentClassify > id) {
							distance = (id + 3) * child_width;
						} else {
							distance = (id - 2) * child_width;
						}
					}
					horizontalScrollView.smoothScrollTo(distance,
							horizontalScrollView.getScrollY());
				}
			});
		}
	}

	/**
	 * 实现滚动的循环处理，及停止触摸时的处理
	 */
	private void initHsvTouch() {
		horizontalScrollView.setOnTouchListener(new View.OnTouchListener() {

			private int pre_item;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				boolean flag = false;
				int x = horizontalScrollView.getScrollX();
				int current_item = (x + hsv_width / 2) / child_width + 1;
				switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE:
					flag = false;
					if (x <= child_width) {
						horizontalScrollView.scrollBy(
								child_width * child_count, 0);
						current_item += child_count;
					} else if (x >= (child_width * child_count * 2 - hsv_width - child_width)) {
						horizontalScrollView.scrollBy(-child_width
								* child_count, 0);
						current_item -= child_count;
					}
					break;
				case MotionEvent.ACTION_UP:
					flag = true;
					horizontalScrollView.smoothScrollTo(child_width
							* current_item - child_width / 2 - hsv_width / 2,
							horizontalScrollView.getScrollY());
					break;
				}
				if (pre_item == 0) {
					isChecked(current_item, true);
				} else if (pre_item != current_item) {
					isChecked(pre_item, false);
					isChecked(current_item, true);
				}
				pre_item = current_item;
				return flag;
			}
		});
	}

	/**
	 * 设置指定位置的状态
	 * 
	 * @param item
	 * @param isChecked
	 */
	private void isChecked(int item, boolean isChecked) {
		LinearLayout ll = (LinearLayout) linearLayout.getChildAt(item - 1);
		TextView textView = (TextView) ll.findViewById(R.id.tv_menu);
		if (isChecked) {
			textView.setTextSize(22);
		} else {
			textView.setTextSize(16);
		}
	}

	/**
	 * 刚开始进入界面时的初始选中项的处理
	 */
	private void initStart() {
		final ViewTreeObserver observer = horizontalScrollView
				.getViewTreeObserver();
		observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

			@Override
			public boolean onPreDraw() {
				// TODO Auto-generated method stub
				observer.removeOnPreDrawListener(this);
				int child_start_item = child_start;
				if ((child_start * child_width - child_width / 2 - hsv_width / 2) <= child_width) {
					child_start_item += child_count;
				}
				horizontalScrollView.scrollTo(child_width * child_start_item
						- child_width / 2 - hsv_width / 2,
						horizontalScrollView.getScrollY());
				isChecked(child_start_item, true);
				return false;
			}
		});
	}

	public void search(final String phone) {

		app.networkHandler.connection(new CommonNetConnection() {
			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.ACCOUNT_GET;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("target", "[\"" + phone + "\"]");
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
				try {
					final Friend friend = JSONParser
							.generateFriendFromJSON(jData.getJSONArray(
									"accounts").getJSONObject(0));

					if (phone.equals(app.data.user.phone)) {
						mMainModeManager.mBusinessCardFragment.mStatus = BusinessCardFragment.SHOW_SELF;
						app.dataHandler.exclude(new Modification() {
							@Override
							public void modifyData(Data data) {
								data.user.nickName = friend.nickName;
								data.user.mainBusiness = friend.mainBusiness;
								data.user.head = friend.head;
							}
						});
					} else if (app.data.friends.get(phone) != null) {
						mMainModeManager.mBusinessCardFragment.mStatus = BusinessCardFragment.SHOW_FRIEND;
						app.dataHandler.exclude(new Modification() {

							@Override
							public void modifyData(Data data) {
								friend.messages = data.friends.get(phone).messages;
								data.friends.put(phone, friend);
							}
						});
					} else {
						mMainModeManager.mBusinessCardFragment.mStatus = BusinessCardFragment.SHOW_TEMPFRIEND;
					}
					mMainModeManager.mBusinessCardFragment.mShowFriend = friend;
					mMainModeManager
							.showNext(mMainModeManager.mBusinessCardFragment);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	class MessageHolder {
		TextView nickName;
		TextView message;
	}

}
