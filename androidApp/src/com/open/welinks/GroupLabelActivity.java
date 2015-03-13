package com.open.welinks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.HttpClient;
import com.open.lib.MyLog;
import com.open.welinks.customListener.MyOnClickListener;
import com.open.welinks.customView.Alert;
import com.open.welinks.customView.Alert.AlertInputDialog;
import com.open.welinks.customView.Alert.AlertInputDialog.OnDialogClickListener;
import com.open.welinks.model.API;
import com.open.welinks.model.Constant;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.utils.BaseDataUtils;

public class GroupLabelActivity extends Activity {

	private Data data = Data.getInstance();
	private ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
	private HttpClient httpClient = HttpClient.getInstance();

	private Gson gson = new Gson();
	public MyLog log = new MyLog("GroupLabelActivity", true);

	private LayoutInflater mInflater;

	private View backView, backMaxView;
	private TextView backTitleView, saveTextView, addLabelView;
	private ImageView backImageView;
	private GridView gridView;
	private ViewGroup labelsViewGroup, rightContainer;

	private GridAdapter mAdapter;

	private MyOnClickListener mOnClickListener;
	private OnItemClickListener mOnItemClickListener;
	private Group currentGroup;

	public List<String> seletedLabel;
	public List<View> labelViews;
	public Map<String, Integer> labelColorsMap;

	public Random random = new Random();

	public int labelCount = 4, nowpage = 0, pagesize = 10;

	public int[] colors = { Color.parseColor("#ff64c151"), Color.parseColor("#ff8982d3"), Color.parseColor("#fffd8963"), Color.parseColor("#ff4ed0c7"), Color.parseColor("#ff7eb9f1"), Color.parseColor("#fffdb859"), Color.parseColor("#fffd6b7b") };
	public int[] drawables = { R.drawable.selector_label_one, R.drawable.selector_label_two, R.drawable.selector_label_three, R.drawable.selector_label_four, R.drawable.selector_label_five, R.drawable.selector_label_six, R.drawable.selector_label_seven };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initViews();
		initData();
	}

	@Override
	public void onBackPressed() {
		mFinish();
	}

	public void mFinish() {
		if (saveTextView.getVisibility() == View.VISIBLE) {
			Alert.createDialog(this).setTitle("您还有编辑尚未提交，是否保存？").setOnConfirmClickListener(new OnDialogClickListener() {

				@Override
				public void onClick(AlertInputDialog dialog) {
					modifyGroupLabels();
					finish();
				}
			}).setOnCancelClickListener(new OnDialogClickListener() {

				@Override
				public void onClick(AlertInputDialog dialog) {
					finish();
				}
			}).show();
		} else {
			finish();
		}
	}

	private void initData() {
		mInflater = getLayoutInflater();
		String gid = getIntent().getStringExtra("key");
		currentGroup = data.relationship.groupsMap.get(gid);
		if (currentGroup == null) {
			finish();
		} else if (currentGroup.labels == null) {
			currentGroup.labels = new ArrayList<String>();
		}
		seletedLabel = new ArrayList<String>(currentGroup.labels);
		labelViews = new ArrayList<View>();
		labelColorsMap = new HashMap<String, Integer>();
		mAdapter = new GridAdapter();
		gridView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
		getGroupLabels();
		fillLabels();
	}

	@SuppressWarnings("deprecation")
	private void initViews() {
		setContentView(R.layout.activity_group_labels);
		backView = findViewById(R.id.backView);
		backMaxView = findViewById(R.id.backMaxView);
		labelsViewGroup = (ViewGroup) findViewById(R.id.labels);
		backTitleView = (TextView) findViewById(R.id.backTitleView);
		backImageView = (ImageView) findViewById(R.id.backImageView);
		gridView = (GridView) findViewById(R.id.grid);
		rightContainer = (ViewGroup) findViewById(R.id.rightContainer);

		saveTextView = new TextView(this);
		saveTextView.setText("保存");
		saveTextView.setTextColor(Color.WHITE);
		saveTextView.setTextSize(16);
		saveTextView.setGravity(Gravity.CENTER);
		saveTextView.setPadding(BaseDataUtils.dpToPxint(15), BaseDataUtils.dpToPxint(5), BaseDataUtils.dpToPxint(15), BaseDataUtils.dpToPxint(5));
		// save.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_white_stroke));
		saveTextView.setBackgroundResource(R.drawable.shape_white_stroke);
		saveTextView.setVisibility(View.GONE);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rightContainer.addView(saveTextView, params);
		backTitleView.setText("群组标签");
		// backView.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_back_white));

		addLabelView = new TextView(this);
		addLabelView.setText("+");
		addLabelView.setTextSize(14);
		addLabelView.setTextColor(Color.WHITE);
		addLabelView.setGravity(Gravity.CENTER);
		GradientDrawable drawable = (GradientDrawable) getResources().getDrawable(R.drawable.shape_grouplabel_item);
		drawable.setStroke((int) BaseDataUtils.dpToPx(0.75f), Color.TRANSPARENT);
		drawable.setColor(Color.parseColor("#0099cd"));
		addLabelView.setBackgroundDrawable(drawable);
		addLabelView.setPadding(BaseDataUtils.dpToPxint(20), BaseDataUtils.dpToPxint(5), BaseDataUtils.dpToPxint(20), BaseDataUtils.dpToPxint(5));
		LinearLayout.LayoutParams addLabelParams = new LinearLayout.LayoutParams(BaseDataUtils.dpToPxint((int) (data.baseData.screenWidth / 5) - 45), LayoutParams.WRAP_CONTENT);
		addLabelParams.leftMargin = BaseDataUtils.dpToPxint(10);
		addLabelView.setLayoutParams(addLabelParams);
		labelsViewGroup.addView(addLabelView, 0);

		initListener();
	}

	private void initListener() {
		mOnClickListener = new MyOnClickListener() {
			@Override
			public void onClickEffective(View view) {
				if (view.equals(backView)) {
					mFinish();
				} else if (view.equals(addLabelView)) {
					creataCustomLabel();
				} else if (view.equals(saveTextView)) {
					modifyGroupLabels();
				} else {
					String tag = (String) view.getTag(R.id.tag_class);
					if ("seleted".equals(tag)) {
						String label = ((TextView) view).getText().toString();
						int index = seletedLabel.indexOf(label);
						seletedLabel.remove(index);
						labelsViewGroup.removeViewAt(index + 1);
						mAdapter.notifyDataSetChanged();
						checkSave();
					}
				}
			}

		};
		mOnItemClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String label = (String) mAdapter.getItem(position);
				int color = colors[labelColorsMap.get(label)];
				if (label.equals("更多") && position == mAdapter.labels.size() - 1) {
					getHotLabels();
				} else {
					if (!seletedLabel.contains(label)) {
						labelsViewGroup.addView(createSeletedLabel(color, label), 1);
					} else {
						int index = seletedLabel.indexOf(label);
						seletedLabel.remove(index);
						labelsViewGroup.removeViewAt(index + 1);
						mAdapter.notifyDataSetChanged();
					}
					checkSave();
				}
			}

		};
		bindEvent();
	}

	private void bindEvent() {
		backView.setOnClickListener(mOnClickListener);
		addLabelView.setOnClickListener(mOnClickListener);
		saveTextView.setOnClickListener(mOnClickListener);
		gridView.setOnItemClickListener(mOnItemClickListener);
	}

	private void fillLabels() {
		labelsViewGroup.removeAllViews();
		labelsViewGroup.addView(addLabelView, 0);
		for (String label : seletedLabel) {
			int color = 0;
			if (labelColorsMap.containsKey(label)) {
				color = colors[labelColorsMap.get(label)];
			} else {
				int index = random.nextInt(colors.length);
				color = colors[index];
				labelColorsMap.put(label, index);
			}
			labelsViewGroup.addView(createSeletedLabel(color, label));
		}
	}

	@SuppressWarnings("deprecation")
	public TextView createSeletedLabel(int color, String label) {

		TextView text = new TextView(this);
		text.setText(label);
		text.setTextSize(14);
		text.setTextColor(Color.WHITE);
		GradientDrawable drawable = (GradientDrawable) getResources().getDrawable(R.drawable.shape_grouplabel_item);
		drawable.setStroke((int) BaseDataUtils.dpToPx(0.75f), Color.TRANSPARENT);
		drawable.setColor(color);
		text.setBackgroundDrawable(drawable);
		text.setPadding(BaseDataUtils.dpToPxint(18), BaseDataUtils.dpToPxint(5), BaseDataUtils.dpToPxint(18), BaseDataUtils.dpToPxint(5));
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.leftMargin = BaseDataUtils.dpToPxint(10);
		params.gravity = Gravity.CENTER;
		text.setLayoutParams(params);
		text.setTag(R.id.tag_class, "seleted");
		text.setOnClickListener(mOnClickListener);
		if (!seletedLabel.contains(label)) {
			seletedLabel.add(0, label);
		}
		if (seletedLabel.size() > labelCount) {
			seletedLabel.remove(labelCount);
			labelsViewGroup.removeViewAt(labelCount);
		}
		mAdapter.notifyDataSetChanged();
		return text;
	}

	class GridAdapter extends BaseAdapter {
		public List<String> labels;

		public GridAdapter() {
			labels = new ArrayList<String>(Arrays.asList(Constant.LABELS));
			labels.add("更多");
		}

		@Override
		public int getCount() {
			return labels.size();
		}

		@Override
		public Object getItem(int position) {
			return labels.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressWarnings("deprecation")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			String label = (String) getItem(position);
			Holder holder;
			int color = 0, index = 0;
			if (convertView == null) {
				holder = new Holder();
				convertView = mInflater.inflate(R.layout.group_label_item, null);
				holder.labelView = (TextView) convertView.findViewById(R.id.content);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			if (!labelColorsMap.containsKey(label)) {
				index = random.nextInt(colors.length);
				labelColorsMap.put(label, index);
			} else {
				index = labelColorsMap.get(label);
			}
			color = colors[index];
			if (seletedLabel.contains(label)) {
				GradientDrawable drawable = (GradientDrawable) getResources().getDrawable(R.drawable.shape_grouplabel_item);
				drawable.setStroke((int) BaseDataUtils.dpToPx(0.75f), Color.TRANSPARENT);
				drawable.setColor(color);
				holder.labelView.setBackgroundDrawable(drawable);
				color = Color.WHITE;
			} else {
				Drawable drawable = getResources().getDrawable(drawables[index]);
				holder.labelView.setBackgroundDrawable(drawable);
			}

			holder.labelView.setText(label);
			holder.labelView.setTextColor(createColorStateList(color));
			return convertView;
		}

		class Holder {
			TextView labelView;
		}
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

	private void creataCustomLabel() {
		Alert.createInputDialog(this).setInputHint("请输入少于20字标签").setTitle("添加标签").setOnConfirmClickListener(new OnDialogClickListener() {
			@Override
			public void onClick(AlertInputDialog dialog) {
				String label = dialog.getInputText().trim();
				if (!"".equals(label)) {
					if (label.length() <= 20) {
						if (!seletedLabel.contains(label)) {
							int color = 0;
							if (labelColorsMap.containsKey(label)) {
								color = colors[labelColorsMap.get(label)];
							} else {
								int index = random.nextInt(colors.length);
								color = colors[index];
								labelColorsMap.put(label, index);
							}
							labelsViewGroup.addView(createSeletedLabel(color, label), 1);
						}
						checkSave();
					} else {
						Toast.makeText(getApplicationContext(), "标签字数不能大于20", Toast.LENGTH_SHORT).show();
					}
				}
			}
		}).show();
	}

	public void checkSave() {
		boolean flag = true;
		if (seletedLabel.size() == currentGroup.labels.size()) {
			for (String label : seletedLabel) {
				if (!currentGroup.labels.contains(label)) {
					flag = false;
					break;
				}
			}
		} else {
			flag = false;
		}
		if (flag) {
			saveTextView.setVisibility(View.GONE);
		} else {
			saveTextView.setVisibility(View.VISIBLE);
		}
	}

	private void getHotLabels() {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		User currentUser = data.userInformation.currentUser;
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("nowpage", nowpage + "");
		params.addBodyParameter("pagesize", pagesize + "");
		httpUtils.send(HttpMethod.POST, API.GROUP_GETHOTLABELS, params, httpClient.new ResponseHandler<String>() {
			class Response {
				public String 提示信息;
				public String 失败原因;
				public List<String> labels;
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				Response response = gson.fromJson(responseInfo.result, Response.class);
				if (response.提示信息.equals("获取热门标签成功")) {
					for (String label : response.labels) {
						if (!mAdapter.labels.contains(label)) {
							mAdapter.labels.add(mAdapter.labels.size() - 2, label);
						}
					}
					nowpage++;
					mAdapter.notifyDataSetChanged();
				} else {
					log.e(response.失败原因);
				}
			}
		});
	}

	private void modifyGroupLabels() {
		currentGroup.labels.clear();
		currentGroup.labels.addAll(seletedLabel);
		data.relationship.isModified = true;
		checkSave();
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		User currentUser = data.userInformation.currentUser;
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("gid", currentGroup.gid + "");
		params.addBodyParameter("labels", gson.toJson(currentGroup.labels));
		httpUtils.send(HttpMethod.POST, API.GROUP_MODIFYGROUPLABEL, params, httpClient.new ResponseHandler<String>() {
			class Response {
				public String 提示信息;
				public String 失败原因;
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				Response response = gson.fromJson(responseInfo.result, Response.class);
				if (response.提示信息.equals("创建群组标签成功")) {
					log.e("创建群组标签成功");
				} else {
					log.e(response.失败原因);
				}
			}

		});
	}

	private void getGroupLabels() {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		User currentUser = data.userInformation.currentUser;
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("gid", currentGroup.gid + "");
		httpUtils.send(HttpMethod.POST, API.GROUP_GETGROUPLABELS, params, httpClient.new ResponseHandler<String>() {
			class Response {
				public String 提示信息;
				public String 失败原因;
				public String gid;
				public List<String> labels;
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				Response response = gson.fromJson(responseInfo.result, Response.class);
				if (response.提示信息.equals("获取群组标签成功")) {
					Group group = data.relationship.groupsMap.get(response.gid);
					if (group != null) {
						group.labels = response.labels;
						currentGroup.labels = response.labels;
						data.relationship.isModified = true;
						seletedLabel = new ArrayList<String>(response.labels);
					}
					fillLabels();
				} else {
					log.e(response.失败原因);
				}
			}
		});

	}
}
