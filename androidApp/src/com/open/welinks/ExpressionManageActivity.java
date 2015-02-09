package com.open.welinks;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.welinks.customListener.MyOnClickListener;
import com.open.welinks.customListener.OnDownloadListener;
import com.open.welinks.customView.Alert;
import com.open.welinks.customView.Alert.AlertInputDialog;
import com.open.welinks.customView.Alert.AlertInputDialog.OnDialogClickListener;
import com.open.welinks.model.API;
import com.open.welinks.model.Constant;
import com.open.welinks.model.Data;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.oss.DownloadFile;
import com.open.welinks.oss.DownloadFileList;
import com.open.welinks.view.ViewManage;

public class ExpressionManageActivity extends Activity {

	private Data data = Data.getInstance();
	private FileHandlers fileHandlers = FileHandlers.getInstance();
	private ViewManage mViewManage = ViewManage.getInstance();
	private DownloadFileList downloadFileList = DownloadFileList.getInstance();

	private View backView;
	private TextView titleTextView;
	private DragSortListView expressionListView;

	private ListController expressionListController;
	private MyOnClickListener mOnClickListener;
	private OnDownloadListener mOnDownloadListener;
	private OnDialogClickListener mConfirmOnDialogClickListener, mCancelOnDialogClickListener;
	private SimpleImageLoadingListener mSimpleImageLoadingListener;

	private ListAdapter listAdapter;
	private LayoutInflater mInflater;
	private List<String> allExpression, ownedExpression, unownedExpression;

	private Handler handler;
	private AlertInputDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initViews();
		initData();
	}

	@Override
	protected void onResume() {
		Toast.makeText(this, "长按调整顺序，滑动删除", Toast.LENGTH_LONG).show();
		super.onResume();
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
		titleTextView = (TextView) this.findViewById(R.id.titleText);
		expressionListView = (DragSortListView) this.findViewById(R.id.expressionList);
		initListener();
	}

	private void initListener() {
		mOnClickListener = new MyOnClickListener() {
			@Override
			public void onClickEffective(View view) {
				if (view.getTag(R.id.tag_first) != null) {
					String expressionName = (String) view.getTag(R.id.tag_first);
					if (unownedExpression.contains(expressionName)) {
						View convertView = (View) view.getParent();
						String[] faceResources = Constant.FACE_RESOURCES_MAP.get(expressionName);
						if (faceResources != null) {
							List<String> expressionNames = Arrays.asList(faceResources);
							downLoadExpressions(convertView, expressionNames);
						}
					}
				} else if (view.equals(backView)) {
					finish();
				}
			}
		};
		mOnDownloadListener = new OnDownloadListener() {
			@Override
			public void onSuccess(DownloadFile instance, int status) {
				if (instance.view.getTag(R.id.tag_first) == null) {
					fileHandlers.imageLoader.displayImage("file://" + instance.path, (ImageView) instance.view, mViewManage.options, mSimpleImageLoadingListener);
				} else {
					int current = (Integer) (instance.view.getTag(R.id.tag_second));
					instance.view.setTag(R.id.tag_second, ++current);
					changePercent(instance.view);
				}
			}

			@Override
			public void onFailure(DownloadFile instance, int status) {
				downloadFileList.addDownloadFile(instance);
			}
		};
		mConfirmOnDialogClickListener = new OnDialogClickListener() {

			@Override
			public void onClick(AlertInputDialog dialog) {
				String name = dialog.getInputText();
				if (ownedExpression.contains(name)) {
					ownedExpression.remove(name);
					unownedExpression.add(name);
				}
				expressionListController.mDivPos = ownedExpression.size();
				listAdapter.notifyDataSetChanged();
			}
		};
		mCancelOnDialogClickListener = new OnDialogClickListener() {

			@Override
			public void onClick(AlertInputDialog dialog) {
				listAdapter.notifyDataSetChanged();

			}
		};
		mSimpleImageLoadingListener = new SimpleImageLoadingListener() {

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				super.onLoadingFailed(imageUri, view, failReason);
				String fileName = imageUri.substring(imageUri.lastIndexOf("/") + 1);
				File file = new File(fileHandlers.sdcardGifImageFolder, fileName);
				downLoadCoverImage(file.getAbsolutePath(), imageUri, (ImageView) view);
			}
		};
	}

	private void bindEvent() {
		backView.setOnClickListener(mOnClickListener);
		dialog.setOnConfirmClickListener(mConfirmOnDialogClickListener);
		dialog.setOnCancelClickListener(mCancelOnDialogClickListener);

		expressionListView.setDropListener(expressionListController);
		expressionListView.setRemoveListener(expressionListController);
		expressionListView.setFloatViewManager(expressionListController);
		expressionListView.setOnTouchListener(expressionListController);
	}

	private void initData() {
		mInflater = this.getLayoutInflater();
		handler = new Handler();
		dialog = Alert.createDialog(ExpressionManageActivity.this);
		allExpression = Arrays.asList(Constant.FACES);
		ownedExpression = data.userInformation.currentUser.faceList;
		if (ownedExpression == null)
			ownedExpression = new ArrayList<String>();
		unownedExpression = new ArrayList<String>();
		for (String face : allExpression) {
			if (!ownedExpression.contains(face))
				unownedExpression.add(face);
		}
		titleTextView.setText(getString(R.string.expressionManage));
		listAdapter = new ListAdapter();
		expressionListController = new ListController(expressionListView, listAdapter);
		expressionListView.setAdapter(listAdapter);
		bindEvent();
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
			Holder holder = new Holder();
			String exressionName = "";
			int type = getItemViewType(position);
			convertView = mInflater.inflate(R.layout.expression_manage_item, null);
			holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
			holder.imageView = (ImageView) convertView.findViewById(R.id.image);
			holder.nameView = (TextView) convertView.findViewById(R.id.name);
			holder.statusView = (TextView) convertView.findViewById(R.id.status);
			convertView.setTag(holder);
			if (type == OWNED) {
				exressionName = ownedExpression.get(position);
				holder.statusView.setBackgroundResource(R.drawable.expression_manager_button_off);
				holder.statusView.setText("已下载");
			} else if (type == UNOWNED) {
				exressionName = unownedExpression.get(position - ownedExpression.size());
				holder.statusView.setBackgroundResource(R.drawable.selector_exprission_item_status);
				holder.statusView.setText("下载");
				holder.statusView.setTag(R.id.tag_first, exressionName);
				holder.statusView.setOnClickListener(mOnClickListener);
			}
			getCoverImage(Constant.FACE_RESOURCES_MAP.get(exressionName)[0], holder.imageView);
			holder.nameView.setText(getExpressionName(exressionName));
			return convertView;
		}

		class Holder {
			ImageView imageView;
			TextView nameView, statusView;
			ProgressBar progressBar;
		}
	}

	private class ListController extends DragSortController implements DragSortListView.DropListener, DragSortListView.RemoveListener {
		private ListAdapter listAdapter;
		private int mPos;
		private int mDivPos;

		public ListController(DragSortListView dslv, ListAdapter adapter) {
			super(dslv);
			this.listAdapter = adapter;
			setRemoveMode(DragSortController.FLING_REMOVE);
			setDragInitMode(DragSortController.ON_LONG_PRESS);
		}

		@Override
		public void drop(int from, int to) {
			if (from != to) {
				String data = ownedExpression.remove(from);
				ownedExpression.add(to, data);
				listAdapter.notifyDataSetChanged();
			}
		}

		@Override
		public void remove(final int which) {
			String name = (String) listAdapter.getItem(which);
			removeExpression(name);
		}

		@Override
		public boolean onDown(MotionEvent ev) {
			int res = super.dragHandleHitPosition(ev);
			if (res >= mDivPos) {
				setRemoveEnabled(false);
			} else {
				setRemoveEnabled(true);
			}
			return super.onDown(ev);
		}

		@Override
		public int startDragPosition(MotionEvent ev) {
			int res = super.dragHandleHitPosition(ev);
			mDivPos = ownedExpression.size();
			if (res >= mDivPos) {
				return DragSortController.MISS;
			} else {
				return res;
			}
		}

		@Override
		public View onCreateFloatView(int position) {
			mPos = position;
			View view = listAdapter.getView(position, null, expressionListView);
			view.setBackgroundResource(R.drawable.chat_expression_float_view);
			return view;
		}

		@Override
		public void onDragFloatView(View floatView, Point position, Point touch) {
			final int first = expressionListView.getFirstVisiblePosition();
			final int lvDivHeight = expressionListView.getDividerHeight();
			View div = expressionListView.getChildAt(mDivPos - first);
			if (div != null) {
				if (mPos > mDivPos) {
					final int limit = div.getBottom() + lvDivHeight;
					if (position.y < limit) {
						position.y = limit;
					}
				} else {
					final int limit = div.getTop() - lvDivHeight - floatView.getHeight();
					if (position.y > limit) {
						position.y = limit;
					}
				}
			} else {
			}
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
			view.findViewById(R.id.progressBar).setVisibility(View.GONE);
			if (current == total) {
				String expressionName = (String) view.findViewById(R.id.status).getTag(R.id.tag_first);
				if (unownedExpression.remove(expressionName)) {
					ownedExpression.add(expressionName);
					data.userInformation.isModified = true;
					handler.post(new Runnable() {
						@Override
						public void run() {
							listAdapter.notifyDataSetChanged();
						}
					});
				}
			}
		} else {
			ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
			progressBar.setVisibility(View.VISIBLE);
			int percent = 100 / total;
			progressBar.incrementProgressBy(percent);
		}

	}

	private void getCoverImage(String fileName, ImageView imageView) {
		File file = new File(fileHandlers.sdcardGifImageFolder, fileName);
		String excessivePath = file.getAbsolutePath();
		String excessiveUrl = API.DOMAIN_COMMONIMAGE + "gifs/" + fileName;
		if (file.exists()) {
			fileHandlers.imageLoader.displayImage("file://" + excessivePath, imageView, mViewManage.options, mSimpleImageLoadingListener);
		} else {
			downLoadCoverImage(excessiveUrl, excessivePath, imageView);
		}
	}

	private void downLoadCoverImage(String url, String path, ImageView imageView) {
		DownloadFile downloadFile = new DownloadFile(url, path);
		downloadFile.view = imageView;
		downloadFile.options = mViewManage.options;
		downloadFile.type = DownloadFile.TYPE_GIF_IMAGE;
		downloadFile.setDownloadFileListener(mOnDownloadListener);
		downloadFileList.addDownloadFile(downloadFile);
	}

	private void removeExpression(final String name) {
		dialog.setTitle("是否删除表情[" + getExpressionName(name) + "]").setInputText(name).show();
	}

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
