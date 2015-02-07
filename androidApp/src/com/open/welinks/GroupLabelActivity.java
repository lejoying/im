package com.open.welinks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.HttpClient;
import com.open.welinks.customListener.MyOnClickListener;
import com.open.welinks.customView.Alert;
import com.open.welinks.customView.Alert.AlertInputDialog;
import com.open.welinks.customView.Alert.AlertInputDialog.OnDialogClickListener;
import com.open.welinks.model.API;
import com.open.welinks.model.Constant;
import com.open.welinks.model.Data;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.model.Data.Relationship.Group;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class GroupLabelActivity extends Activity {

	private Data data = Data.getInstance();
	private ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
	private HttpClient httpClient = HttpClient.getInstance();

	private Gson gson = new Gson();

	private LayoutInflater mInflater;

	private View backView, backMaxView;
	private TextView backTitleView;
	private ImageView backImageView;
	private GridView grid;
	private ViewGroup labels;

	private GridAdapter mAdapter;

	private MyOnClickListener mOnClickListener;
	private OnItemLongClickListener mOnItemLongClickListener;
	private OnItemClickListener mOnItemClickListener;
	private Group currentGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initViews();
		initData();
		fillLabels();
	}

	private void initData() {
		mInflater = getLayoutInflater();
		String gid = getIntent().getStringExtra("key");
		currentGroup = data.relationship.groupsMap.get(gid);
		if (currentGroup == null) {
			finish();
		}
		mAdapter = new GridAdapter();
		grid.setAdapter(mAdapter);
		getGroupLabels();
	}

	@SuppressWarnings("deprecation")
	private void initViews() {
		setContentView(R.layout.activity_group_labels);
		backView = findViewById(R.id.backView);
		backMaxView = findViewById(R.id.backMaxView);
		labels = (ViewGroup) findViewById(R.id.labels);
		backTitleView = (TextView) findViewById(R.id.backTitleView);
		backImageView = (ImageView) findViewById(R.id.backImageView);
		grid = (GridView) findViewById(R.id.grid);

		backMaxView.setBackgroundColor(Color.WHITE);
		backTitleView.setTextColor(Color.parseColor("#0099cd"));
		backImageView.setColorFilter(Color.parseColor("#0099cd"));
		backTitleView.setText("群组标签");
		backView.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_back_white));
		initListener();
	}

	private void initListener() {
		mOnClickListener = new MyOnClickListener() {
			@Override
			public void onClickEffective(View view) {
				if (view.equals(backView)) {
					finish();
				}
			}

		};
		mOnItemClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub

			}
		};
		bindEvent();
	}

	private void bindEvent() {
		backView.setOnClickListener(mOnClickListener);
		grid.setOnItemClickListener(mOnItemClickListener);
	}

	private void fillLabels() {
		// TODO Auto-generated method stub

	}

	class GridAdapter extends BaseAdapter {
		private List<String> labels;

		public GridAdapter() {
			labels = new ArrayList<String>(Arrays.asList(Constant.LABELS));
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			String label = (String) getItem(position);
			Holder holder;
			if (convertView == null) {
				holder = new Holder();
				convertView = mInflater.inflate(R.layout.group_label_item, null);
				holder.label = (TextView) convertView.findViewById(R.id.content);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			holder.label.setText(label);
			return convertView;
		}

		class Holder {
			TextView label;
		}
	}

	private void creataCustomLabel() {
		Alert.createInputDialog(this).setInputHint("请输入少于20字标签").setTitle("添加标签").setOnConfirmClickListener(new OnDialogClickListener() {
			@Override
			public void onClick(AlertInputDialog dialog) {
				String label = dialog.getInputText().trim();
				if (!"".equals(label)) {
					if (label.length() <= 20) {
						if (!currentGroup.labels.contains(label)) {
							currentGroup.labels.add(label);
							data.relationship.isModified = true;
							RequestParams params = new RequestParams();
							HttpUtils httpUtils = new HttpUtils();
							params.addBodyParameter("phone", data.userInformation.currentUser.phone);
							params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
							params.addBodyParameter("gid", currentGroup.gid + "");
							params.addBodyParameter("label", label);
							httpUtils.send(HttpMethod.POST, API.GROUP_CREATEGROUPLABEL, params, responseHandlers.group_creategrouplabel);
							mAdapter.notifyDataSetChanged();
						}
					}
				}
			}
		}).show();
	}

	private void deleteLabel(final String label) {
		Alert.createDialog(this).setTitle("是否删除标签【" + label + "】").setOnConfirmClickListener(new OnDialogClickListener() {

			@Override
			public void onClick(AlertInputDialog dialog) {
				currentGroup.labels.remove(label);
				data.relationship.isModified = true;
				RequestParams params = new RequestParams();
				HttpUtils httpUtils = new HttpUtils();
				params.addBodyParameter("phone", data.userInformation.currentUser.phone);
				params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
				params.addBodyParameter("gid", currentGroup.gid + "");
				params.addBodyParameter("label", label);
				httpUtils.send(HttpMethod.POST, API.GROUP_DELETEGROUPLABEL, params, responseHandlers.group_deletegrouplabel);
				mAdapter.notifyDataSetChanged();
			}
		}).show();

	}

	private void getGroupLabels() {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		params.addBodyParameter("gid", currentGroup.gid + "");
		httpUtils.send(HttpMethod.POST, API.GROUP_DELETEGROUPLABEL, params, httpClient.new ResponseHandler<String>() {
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
					}
					mAdapter.notifyDataSetChanged();
				} else {

				}
			}
		});

	}
}
