package com.open.welinks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.open.lib.MyLog;
import com.open.welinks.customListener.MyOnClickListener;
import com.open.welinks.customView.Alert;
import com.open.welinks.customView.Alert.AlertInputDialog;
import com.open.welinks.customView.Alert.AlertInputDialog.OnDialogClickListener;
import com.open.welinks.model.Constant;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.utils.BaseDataUtils;
import com.open.welinks.view.ViewManage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class ClassificationRecommendationActivity extends Activity {

	public MyLog log = new MyLog("ClassificationRecommendationActivity", true);
	public Data data = Data.getInstance();
	public FileHandlers mFileHandlers = FileHandlers.getInstance();
	public ViewManage mViewManage = ViewManage.getInstance();

	public LayoutInflater mInflater;

	public Random random;

	public ViewGroup seletedLabelParentView, labelParents, lineOne, lineTwo, lineThree, lineFour;
	public HorizontalScrollView seletedScrollView;
	public View backView, backMaxView;
	public ImageView backImageView, searchImage;
	public TextView backTitleView;
	public ListView content;

	public int measure;

	public List<String> seletedLabel;
	public List<String> labels;
	public List<View> labelViews;
	public Map<String, View> labelViewsMap;

	public MyOnClickListener mOnClickListener;
	public OnScrollListener mOnScrollListener;
	public OnTouchListener OnTouchListener;

	public GroupAdapter mAdapter;
	public List<String> groups;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initViews();
		initData();
	}

	@SuppressWarnings("deprecation")
	private void initViews() {
		setContentView(R.layout.activity_classificationrecommendation);
		mInflater = getLayoutInflater();
		labelParents = (ViewGroup) findViewById(R.id.labelParents);
		seletedLabelParentView = (ViewGroup) findViewById(R.id.seletedLabels);
		lineOne = (ViewGroup) findViewById(R.id.lineOne);
		lineTwo = (ViewGroup) findViewById(R.id.lineTwo);
		lineThree = (ViewGroup) findViewById(R.id.lineThree);
		lineFour = (ViewGroup) findViewById(R.id.lineFour);
		backView = findViewById(R.id.backView);
		backMaxView = findViewById(R.id.backMaxView);
		seletedScrollView = (HorizontalScrollView) findViewById(R.id.ScrollViewOne);
		backImageView = (ImageView) findViewById(R.id.backImageView);
		backTitleView = (TextView) findViewById(R.id.backTitleView);
		content = (ListView) findViewById(R.id.content);
		// backMaxView.setBackgroundColor(Color.WHITE);
		// backTitleView.setTextColor(Color.parseColor("#0099cd"));
		// backImageView.setColorFilter(Color.parseColor("#0099cd"));

		searchImage = new ImageView(this);
		searchImage.setImageResource(R.drawable.dialog_search);
		GradientDrawable drawable = (GradientDrawable) getResources().getDrawable(R.drawable.shape_grouplabel_item);
		drawable.setStroke((int) BaseDataUtils.dpToPx(0.75f), Color.TRANSPARENT);
		drawable.setColor(Color.parseColor("#0099cd"));
		searchImage.setBackgroundDrawable(drawable);
		searchImage.setPadding(BaseDataUtils.dpToPxint(22), BaseDataUtils.dpToPxint(5), BaseDataUtils.dpToPxint(22), BaseDataUtils.dpToPxint(5));
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, BaseDataUtils.dpToPxint(29));
		params.bottomMargin = BaseDataUtils.dpToPxint(10);
		params.topMargin = BaseDataUtils.dpToPxint(7);
		params.leftMargin = BaseDataUtils.dpToPxint(7);
		params.rightMargin = BaseDataUtils.dpToPxint(10);
		searchImage.setLayoutParams(params);
		seletedLabelParentView.addView(searchImage, 0);
		backTitleView.setText("分类推荐");
		initListener();
	}

	private void initListener() {
		mOnClickListener = new MyOnClickListener() {
			@Override
			public void onClickEffective(View view) {
				if (view.equals(backView)) {
					finish();
				} else if (view.equals(searchImage)) {
					labelViews.add(0, null);
					addCustomlabel(view);
				} else if (view.equals(seletedScrollView)) {
					if (labelParents.getVisibility() == View.GONE) {
						labelParents.setVisibility(View.VISIBLE);
					}
				} else {
					String tag = (String) view.getTag(R.id.tag_class);
					if ("label".equals(tag)) {
						String label = ((TextView) view).getText().toString();
						if (!seletedLabel.contains(label)) {
							int color = (Integer) view.getTag(R.id.tag_first);
							setTextAttribute((TextView) view, "", color, "seleted");
							labelViews.add(0, view);
							createSeletedLabel(label, color);
						}
					} else if ("seleted".equals(tag)) {
						String label = ((TextView) view).getText().toString();
						if (seletedLabel.contains(label)) {
							int index = seletedLabel.indexOf(label);
							seletedLabelParentView.removeViewAt(index + 1);
							seletedLabel.remove(label);
							View normalView = labelViews.get(index);
							if (normalView != null)
								setTextAttribute((TextView) normalView, "", (Integer) normalView.getTag(R.id.tag_first), "normal");
							labelViews.remove(index);
							if (labelParents.getVisibility() == View.GONE) {
								labelParents.setVisibility(View.VISIBLE);
							}
						}
					}
				}
			}

		};
		OnTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (view.equals(seletedScrollView)) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						if (labelParents.getVisibility() == View.GONE) {
							labelParents.setVisibility(View.VISIBLE);
						}
					}
				}
				return false;
			}
		};

		mOnScrollListener = new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (labelParents.getVisibility() == View.VISIBLE) {
					labelParents.setVisibility(View.GONE);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

			}
		};
		bindEvent();
	}

	private void bindEvent() {
		backView.setOnClickListener(mOnClickListener);
		searchImage.setOnClickListener(mOnClickListener);

		seletedScrollView.setOnTouchListener(OnTouchListener);

		content.setOnScrollListener(mOnScrollListener);
	}

	private void initData() {
		measure = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		random = new Random();
		seletedLabel = new ArrayList<String>();
		labelViews = new ArrayList<View>();
		labelViewsMap = new HashMap<String, View>();
		labels = new ArrayList<String>(Arrays.asList(Constant.LABELS));
		labels.remove(0);
		fillData(labels);
		mAdapter = new GroupAdapter();
		content.setAdapter(mAdapter);
	}

	private void fillData(List<String> labels) {
		for (String label : labels) {
			int color = colors[random.nextInt(colors.length)];
			TextView textView = new TextView(this);
			setTextAttribute(textView, label, color, "normal");
			textView.setTag(R.id.tag_class, "label");
			textView.setTag(R.id.tag_first, color);
			labelViewsMap.put(label, textView);
			ViewGroup parentView = (ViewGroup) compareViews(lineOne, lineTwo, lineThree, lineFour);
			parentView.addView(textView);
		}
	}

	@SuppressWarnings("deprecation")
	public TextView setTextAttribute(TextView textView, String content, int color, String status) {
		if ("seleted".equals(status)) {
			GradientDrawable drawableDown = (GradientDrawable) getResources().getDrawable(R.drawable.shape_grouplabel_item);
			drawableDown.setStroke((int) BaseDataUtils.dpToPx(0.75f), color);
			drawableDown.setColor(color);
			textView.setBackgroundDrawable(drawableDown);
			textView.setTextColor(Color.WHITE);
		} else if ("normal".equals(status)) {
			// GradientDrawable drawableUp = (GradientDrawable) getResources().getDrawable(R.drawable.selector_label);
			// drawableUp.setStroke((int) BaseDataUtils.dpToPx(0.75f), color);
			// drawableUp.setColor(Color.TRANSPARENT);
			textView.setBackgroundDrawable(createDrawable(color));
			textView.setTextColor(createColorStateList(color));
		}
		if (!"".equals(content)) {
			textView.setText(content);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			textView.setPadding(BaseDataUtils.dpToPxint(22), BaseDataUtils.dpToPxint(5), BaseDataUtils.dpToPxint(22), BaseDataUtils.dpToPxint(5));
			textView.setGravity(Gravity.CENTER);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.bottomMargin = BaseDataUtils.dpToPxint(10);
			params.topMargin = BaseDataUtils.dpToPxint(7);
			params.leftMargin = BaseDataUtils.dpToPxint(7);
			params.rightMargin = BaseDataUtils.dpToPxint(10);
			textView.setLayoutParams(params);
			textView.setOnClickListener(mOnClickListener);
		}
		return textView;
	}

	public Drawable createDrawable(int color) {
		Drawable drawable = null;
		if (color == colors[0]) {
			log.e("0:::::::::::");
			drawable = getResources().getDrawable(R.drawable.selector_label_one);
		} else if (color == colors[1]) {
			log.e("1:::::::::::");
			drawable = getResources().getDrawable(R.drawable.selector_label_two);
		} else if (color == colors[2]) {
			log.e("2:::::::::::");
			drawable = getResources().getDrawable(R.drawable.selector_label_three);
		} else if (color == colors[3]) {
			log.e("3:::::::::::");
			drawable = getResources().getDrawable(R.drawable.selector_label_four);
		} else if (color == colors[4]) {
			log.e("4:::::::::::");
			drawable = getResources().getDrawable(R.drawable.selector_label_five);
		} else if (color == colors[5]) {
			log.e("5:::::::::::");
			drawable = getResources().getDrawable(R.drawable.selector_label_six);
		} else if (color == colors[6]) {
			log.e("6:::::::::::");
			drawable = getResources().getDrawable(R.drawable.selector_label_seven);
		}
		return drawable;
	}

	public ColorStateList createColorStateList(int color) {
		int[] colors = new int[] { Color.WHITE, Color.WHITE, color, Color.WHITE, Color.WHITE, color };
		int[][] states = new int[6][];
		states[0] = new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled };
		states[1] = new int[] { android.R.attr.state_enabled, android.R.attr.state_focused };
		states[2] = new int[] { android.R.attr.state_enabled };
		states[3] = new int[] { android.R.attr.state_focused };
		states[4] = new int[] { android.R.attr.state_window_focused };
		states[5] = new int[] {};
		ColorStateList colorList = new ColorStateList(states, colors);
		return colorList;
	}

	public void addCustomlabel(View view) {
		Alert.createInputDialog(this).setInputHint("请输入要搜索的标签").setTitle("标签搜索").setOnConfirmClickListener(new OnDialogClickListener() {

			@Override
			public void onClick(AlertInputDialog dialog) {
				String label = dialog.getInputText().trim();
				if (!"".equals(label) && !seletedLabel.contains(label)) {
					if (labelViewsMap.containsKey(label)) {
						View seleteView = labelViewsMap.get(label);
						int color = (Integer) seleteView.getTag(R.id.tag_first);
						setTextAttribute((TextView) seleteView, "", color, "seleted");
						labelViews.add(0, seleteView);
						createSeletedLabel(label, color);
					} else {
						createSeletedLabel(label, colors[random.nextInt(colors.length)]);
					}
				}
			}
		}).show();

	}

	public void createSeletedLabel(String label, int color) {
		TextView textView = new TextView(getBaseContext());
		setTextAttribute(textView, label, color, "seleted");
		textView.setTag(R.id.tag_class, "seleted");
		textView.setTag(R.id.tag_first, color);
		seletedLabel.add(0, label);
		seletedLabelParentView.addView(textView, 1);
		if (seletedLabelParentView.getChildCount() > 5) {
			seletedLabel.remove(4);
			seletedLabelParentView.removeViewAt(5);
			View normalView = labelViews.get(4);
			if (normalView != null)
				setTextAttribute((TextView) normalView, "", (Integer) normalView.getTag(R.id.tag_first), "normal");
			labelViews.remove(4);
		}
	}

	public View compareViews(View... views) {
		View minWidthView = null;
		for (View view : views) {
			view.measure(measure, measure);
			if (minWidthView == null) {
				minWidthView = view;
			} else {
				if (minWidthView.getMeasuredWidth() > view.getMeasuredWidth()) {
					minWidthView = view;
				}
			}
		}
		return minWidthView;
	}

	class GroupAdapter extends BaseAdapter {

		public GroupAdapter() {
			groups = data.relationship.groups;
		}

		@Override
		public int getCount() {
			return groups.size();
		}

		@Override
		public Object getItem(int position) {
			return groups.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if (convertView == null) {
				holder = new Holder();
				convertView = mInflater.inflate(R.layout.chat_message_item, null);
				holder.headView = (ImageView) convertView.findViewById(R.id.userHeadView);
				holder.nickNameView = (TextView) convertView.findViewById(R.id.tv_nickname);
				holder.lastChatTimeView = (TextView) convertView.findViewById(R.id.tv_time);
				holder.lastChatMessageView = (TextView) convertView.findViewById(R.id.tv_lastchatcontent);
				holder.notReadNumberView = (TextView) convertView.findViewById(R.id.tv_notread);
				holder.groupIconView = (TextView) convertView.findViewById(R.id.groupIcon);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			holder.notReadNumberView.setVisibility(View.GONE);
			holder.groupIconView.setVisibility(View.GONE);
			Group group = data.relationship.groupsMap.get(getItem(position));
			if (group != null) {
				mFileHandlers.getHeadImage(group.icon, holder.headView, mViewManage.options50);
				holder.nickNameView.setText(group.name);
				holder.lastChatMessageView.setText(group.description);
			}

			return convertView;
		}

		class Holder {
			public ImageView headView;
			public TextView nickNameView, lastChatTimeView, lastChatMessageView, notReadNumberView, groupIconView;
		}
	}

	public int[] colors = { Color.parseColor("#ff64c151"), Color.parseColor("#ff8982d3"), Color.parseColor("#fffd8963"), Color.parseColor("#ff4ed0c7"), Color.parseColor("#ff7eb9f1"), Color.parseColor("#fffdb859"), Color.parseColor("#fffd6b7b") };
}
