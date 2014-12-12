package com.open.welinks;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.open.welinks.controller.DownloadFile;
import com.open.welinks.controller.DownloadFileList;
import com.open.welinks.customListener.MyOnClickListener;
import com.open.welinks.customListener.OnDownloadListener;
import com.open.welinks.model.API;
import com.open.welinks.model.Constant;
import com.open.welinks.model.Data;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.utils.BaseDataUtils;
import com.open.welinks.view.ViewManage;

public class ExpressionManageActivity extends Activity {
	Data data = Data.getInstance();
	FileHandlers fileHandlers = FileHandlers.getInstance();
	ViewManage mViewManage = ViewManage.getInstance();
	DownloadFileList downloadFileList = DownloadFileList.getInstance();

	private View backView;
	private RelativeLayout rightContainer;
	private TextView titleText, delete;
	private ListView expressionList;
	private ListAdapter adapter;
	private LayoutInflater mInflater;
	private List<String> allExpression, ownedExpression, unownedExpression;
	private MyOnClickListener mOnClickListener;
	private OnDownloadListener mOnDownloadListener;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initViews();
		initData();
	}

	@Override
	protected void onPause() {
		data.userInformation.currentUser.faceList = ownedExpression;
		data.userInformation.isModified = true;
		super.onPause();
	}

	private void initViews() {
		setContentView(R.layout.activity_manage_expression);
		backView = this.findViewById(R.id.backView);
		rightContainer = (RelativeLayout) this.findViewById(R.id.rightContainer);
		titleText = (TextView) this.findViewById(R.id.titleText);
		expressionList = (ListView) this.findViewById(R.id.expressionList);
		delete = new TextView(this);
		initListener();
	}

	private void initListener() {
		mOnClickListener = new MyOnClickListener() {
			@Override
			public void onClickEffective(View view) {
				if (view.getTag(R.id.tag_first) != null) {
					String expressionName = (String) view.getTag(R.id.tag_first);
					if (!ownedExpression.contains(expressionName)) {
						View convertView = (View) view.getParent();
						String[] faceResources = Constant.FACE_RESOURCES_MAP.get(expressionName);
						if (faceResources != null) {
							List<String> expressionNames = Arrays.asList(faceResources);
							downLoadExpressions(convertView, expressionNames);
						}
					}
				} else if (view.equals(backView)) {
					finish();
				} else if (view.equals(delete)) {

				}
			}
		};
		mOnDownloadListener = new OnDownloadListener() {
			@Override
			public void onSuccess(DownloadFile instance, int status) {
				super.onSuccess(instance, status);
				int current = (Integer) (instance.view.getTag(R.id.tag_second)), total = (Integer) instance.view.getTag(R.id.tag_first);
				instance.view.setTag(R.id.tag_second, ++current);
				changePercent(instance.view);
			}

			@Override
			public void onFailure(DownloadFile instance, int status) {
				super.onFailure(instance, status);
			}
		};
		bindEvent();
	}

	private void bindEvent() {
		backView.setOnClickListener(mOnClickListener);
		delete.setOnClickListener(mOnClickListener);
	}

	private void initData() {
		mInflater = this.getLayoutInflater();
		handler = new Handler();
		allExpression = Arrays.asList(Constant.FACES);
		ownedExpression = data.userInformation.currentUser.faceList;
		if (ownedExpression == null)
			ownedExpression = new ArrayList<String>();
		unownedExpression = new ArrayList<String>();
		for (String face : allExpression) {
			if (!ownedExpression.contains(face))
				unownedExpression.add(face);
		}
		titleText.setText(getString(R.string.expressionManage));
		adapter = new ListAdapter();
		expressionList.setAdapter(adapter);
	}

	public class ListAdapter extends BaseAdapter {
		private int OWNED = 0x1, UNOWNED = 0x2, TYPECOUNT = 0x3;

		public ListAdapter() {

		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return allExpression.size();
		}

		@Override
		public Object getItem(int position) {
			if (position < ownedExpression.size()) {
				return ownedExpression.get(position);
			} else {
				return unownedExpression.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			if (position < ownedExpression.size()) {
				return OWNED;
			} else {
				return UNOWNED;
			}
		}

		@Override
		public int getViewTypeCount() {
			return TYPECOUNT;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder;
			String exressionName = "";
			int type = getItemViewType(position);
			// if (convertView == null) {
			holder = new Holder();
			convertView = mInflater.inflate(R.layout.expression_manage_item, null);
			holder.percentLayout = convertView.findViewById(R.id.percentLayout);
			holder.image = (ImageView) convertView.findViewById(R.id.image);
			holder.percent = (ImageView) convertView.findViewById(R.id.percent);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.status = (TextView) convertView.findViewById(R.id.status);
			convertView.setTag(holder);
			// } else {
			// holder = (Holder) convertView.getTag();
			// }
			if (type == OWNED) {
				exressionName = ownedExpression.get(position);
				holder.status.setText("已下载");
			} else if (type == UNOWNED) {
				exressionName = unownedExpression.get(position - ownedExpression.size());
				holder.status.setText("下载");
			}
			fileHandlers.getImage(Constant.FACE_RESOURCES_MAP.get(exressionName)[0], holder.image, holder.image.getLayoutParams(), DownloadFile.TYPE_GIF_IMAGE, mViewManage.options);
			holder.name.setText(getExpressionName(exressionName));
			holder.status.setTag(R.id.tag_first, exressionName);
			holder.status.setOnClickListener(mOnClickListener);
			return convertView;
		}

		class Holder {
			View percentLayout;
			ImageView image, percent;
			TextView name, status;
		}
	}

	private void downLoadExpressions(View view, List<String> list) {
		view.setTag(R.id.tag_first, list.size());
		view.setTag(R.id.tag_second, 0);
		for (String expressionName : list) {
			File file = new File(fileHandlers.sdcardGifImageFolder, expressionName);
			if (file.exists()) {
				int current = (Integer) (view.getTag(R.id.tag_second));
				view.setTag(R.id.tag_second, ++current);
				changePercent(view);
			} else {
				String url = API.DOMAIN_COMMONIMAGE + "gifs/" + expressionName;
				String path = file.getAbsolutePath();
				DownloadFile downloadFile = new DownloadFile(url, path);
				downloadFile.path = path;
				downloadFile.view = view;
				downloadFile.setDownloadFileListener(mOnDownloadListener);
				downloadFileList.addDownloadFile(downloadFile);
			}
		}
	}

	private void changePercent(View view) {
		int total = (Integer) view.getTag(R.id.tag_first), current = (Integer) (view.getTag(R.id.tag_second));
		if (total == current) {
			view.findViewById(R.id.percentLayout).setVisibility(View.GONE);
			if (current == total) {
				String expressionName = (String) view.findViewById(R.id.status).getTag(R.id.tag_first);
				if (unownedExpression.remove(expressionName)) {
					ownedExpression.add(expressionName);
					data.userInformation.isModified = true;
					handler.post(new Runnable() {
						@Override
						public void run() {
							adapter.notifyDataSetChanged();
						}
					});
				}
			}
		} else {
			view.findViewById(R.id.percentLayout).setVisibility(View.VISIBLE);
			ImageView percent = (ImageView) view.findViewById(R.id.percent);
			percent.getLayoutParams().width = (int) BaseDataUtils.dpToPx(50 / (float) (total / current));
		}

	}

	// "tosiji", "lengtu", "ninimao", "feiniaobulu", "donki", "xiaotumei", "chouerguang", "xiaoan", "oujisang", "tudandan",
	// "mengleyuan", "chouchoumao", "malimalihong", "yangxiaojian", "xiongnini"
	private String getExpressionName(String name) {
		String expressionName = "";
		if ("tosiji".equals(name)) {
			expressionName = "兔斯基";
		} else if ("lengtu".equals(name)) {
			expressionName = "冷兔";
		} else if ("ninimao".equals(name)) {
			expressionName = "妮妮猫";
		} else if ("feiniaobulu".equals(name)) {
			expressionName = "肥鸟布鲁";
		} else if ("donki".equals(name)) {
			expressionName = "Donki";
		} else if ("xiaotumei".equals(name)) {
			expressionName = "小兔妹";
		} else if ("chouerguang".equals(name)) {
			expressionName = "抽耳光";
		} else if ("xiaoan".equals(name)) {
			expressionName = "小安";
		} else if ("oujisang".equals(name)) {
			expressionName = "欧吉桑";
		} else if ("tudandan".equals(name)) {
			expressionName = "秃蛋蛋";
		} else if ("mengleyuan".equals(name)) {
			expressionName = "萌乐园";
		} else if ("chouchoumao".equals(name)) {
			expressionName = "臭臭猫";
		} else if ("malimalihong".equals(name)) {
			expressionName = "唛哩唛哩轰";
		} else if ("yangxiaojian".equals(name)) {
			expressionName = "羊小贱";
		} else if ("xiongnini".equals(name)) {
			expressionName = "熊泥泥";
		} else {
			expressionName = name;
		}
		return expressionName;
	}
}
