package com.open.welinks.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.open.lib.MyLog;
import com.open.welinks.R;
import com.open.welinks.controller.DownloadFile;
import com.open.welinks.controller.DownloadFileList;
import com.open.welinks.controller.ShareListController;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Shares.Share.ShareMessage;
import com.open.welinks.model.SubData.ShareContent;
import com.open.welinks.model.SubData.ShareContent.ShareContentItem;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.utils.DateUtil;

public class ShareListView {

	public Data data = Data.getInstance();
	public String tag = "ShareListView";
	public MyLog log = new MyLog(tag, true);

	public Context context;
	public ShareListView thisView;
	public ShareListController thisController;
	public Activity thisActivity;

	public LayoutInflater mInflater;

	public ListView listView;

	public View backView;
	public TextView backTitleView;

	public FileHandlers fileHandlers = FileHandlers.getInstance();

	public ShareListAdapter shareListAdapter;

	public ImageLoader imageLoader = ImageLoader.getInstance();

	public DownloadFileList downloadFileList = DownloadFileList.getInstance();

	public View footerView;

	public ShareListView(Activity thisActivity) {
		this.context = thisActivity;
		this.thisView = this;
		this.thisActivity = thisActivity;
	}

	DisplayMetrics displayMetrics;

	public void initView() {
		displayMetrics = new DisplayMetrics();
		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		this.mInflater = thisActivity.getLayoutInflater();
		footerView = mInflater.inflate(R.layout.view_listview_foot, null);
		thisActivity.setContentView(R.layout.activity_share_list);
		backView = thisActivity.findViewById(R.id.backView);
		backTitleView = (TextView) thisActivity.findViewById(R.id.backTitleView);
		backTitleView.setText("分享列表");

		listView = (ListView) thisActivity.findViewById(R.id.listView);

		shareListAdapter = new ShareListAdapter();
		listView.setAdapter(shareListAdapter);
	}

	DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();

	public class ShareListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return thisController.shares.size() + 1;
			// return 20;
		}

		@Override
		public Object getItem(int position) {
			return thisController.shares.get(position);
			// return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getViewTypeCount() {
			return 3;
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0) {
				return 0;
			} else {
				return 1;
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ShareHolder holder = null;
			if (convertView == null) {
				holder = new ShareHolder();
				if (position == 0) {
					convertView = mInflater.inflate(R.layout.view_sharelist_top, null);
					holder.converView = (ImageView) convertView.findViewById(R.id.conver);
					holder.headView = (ImageView) convertView.findViewById(R.id.head);
					holder.nickNameView = (TextView) convertView.findViewById(R.id.nickName);
					holder.businessView = (TextView) convertView.findViewById(R.id.business);
					// if (position >= 2) {
					// RelativeLayout mainContainer = (RelativeLayout) convertView.findViewById(R.id.mainContainer);
					// RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mainContainer.getLayoutParams();
					// params.topMargin = 50;
					// }
				} else {
					convertView = mInflater.inflate(R.layout.activity_share_list_item, null);
					holder.dayView = (TextView) convertView.findViewById(R.id.day);
					holder.monthView = (TextView) convertView.findViewById(R.id.month);
					holder.imageContainer = (RelativeLayout) convertView.findViewById(R.id.imageContainer);
					holder.textContentView = (TextView) convertView.findViewById(R.id.textContent);
					holder.imageCountView = (TextView) convertView.findViewById(R.id.imageCount);
				}
				convertView.setTag(holder);
			} else {
				holder = (ShareHolder) convertView.getTag();
			}

			if (position == 0) {
				String head = "";
				String nickName = "";
				String business = "";
				if (thisController.isSelf) {
					head = thisController.currentUser.head;
					nickName = thisController.currentUser.nickName;
					business = thisController.currentUser.mainBusiness;
				} else {
					head = thisController.friend.head;
					nickName = thisController.friend.nickName;
					business = thisController.friend.mainBusiness;
				}
				File file = new File(fileHandlers.sdcardHeadImageFolder, head);
				if (file.exists()) {
					imageLoader.displayImage("file://" + file.getAbsolutePath(), holder.headView);
				} else {
					DownloadFile downloadFile = new DownloadFile(API.DOMAIN_COMMONIMAGE + "heads/" + head, file.getAbsolutePath());
					downloadFile.view = holder.headView;
					downloadFile.setDownloadFileListener(thisController.downloadListener);
					downloadFileList.addDownloadFile(downloadFile);
				}
				holder.nickNameView.setText(nickName);
				holder.businessView.setText(business);
				imageLoader.displayImage("drawable://" + R.drawable.sharelisttop, holder.converView, options);
				holder.headView.setTag(R.id.tag_class, "conver_head");
				holder.headView.setOnClickListener(thisController.mOnClickListener);
			} else {
				// holder.textContentView.setText(position + "");

				String gsid = thisController.shares.get(position - 1);
				ShareMessage message = thisController.sharesMap.get(gsid);
				holder.imageContainer.removeAllViews();
				if (message != null) {
					ShareContent shareContent = thisController.gson.fromJson("{shareContentItems:" + message.content + "}", ShareContent.class);
					if (shareContent == null) {
						holder.imageContainer.removeAllViews();
						holder.imageCountView.setVisibility(View.GONE);
						holder.imageContainer.setVisibility(View.GONE);
						holder.textContentView.setText("此数据目前暂无展示方式,\n敬请谅解。");
						convertView.setTag(R.id.tag_third, "null");
						return convertView;
					}
					String textContent = "";
					List<String> imageContent = new ArrayList<String>();
					List<ShareContentItem> shareContentItems = shareContent.shareContentItems;
					for (int i = 0; i < shareContentItems.size(); i++) {
						ShareContentItem shareContentItem = shareContentItems.get(i);
						if (shareContentItem.type.equals("image")) {
							imageContent.add(shareContentItem.detail);
						} else if (shareContentItem.type.equals("text")) {
							textContent = shareContentItem.detail;
						}
					}
					holder.textContentView.setText(textContent);
					if (imageContent.size() == 0) {
						holder.imageCountView.setVisibility(View.GONE);
						holder.imageContainer.setVisibility(View.GONE);
						LinearLayout.LayoutParams params = (LayoutParams) holder.textContentView.getLayoutParams();
						params.height = (int) (75 * displayMetrics.density + 0.5f);
					} else {
						holder.imageCountView.setVisibility(View.VISIBLE);
						holder.imageContainer.setVisibility(View.VISIBLE);
						LinearLayout.LayoutParams params = (LayoutParams) holder.textContentView.getLayoutParams();
						params.height = (int) (55 * displayMetrics.density + 0.5f);
						holder.imageCountView.setText("共" + imageContent.size() + "张");
					}
					String lastTime = "";
					String[] time = DateUtil.getDayMoth(message.time);
					if (position > 1) {
						String ogsid = thisController.shares.get(position - 2);
						ShareMessage oMessage = thisController.sharesMap.get(ogsid);
						String[] lastTime0 = DateUtil.getDayMoth(oMessage.time);
						lastTime = lastTime0[0] + lastTime0[1];
					} else {
						lastTime = "";
					}
					if (time[0].equals("今") || time[0].equals("昨") || time[0].equals("前")) {
						holder.dayView.setText(time[0] + time[1]);
						if (!lastTime.equals(time[0] + time[1])) {
							lastTime = time[0] + time[1];
							holder.dayView.setVisibility(View.VISIBLE);
						} else {
							holder.dayView.setVisibility(View.INVISIBLE);
						}
						holder.monthView.setText(" ");
						holder.monthView.setVisibility(View.INVISIBLE);
					} else {
						holder.dayView.setText(time[0]);
						holder.monthView.setText(time[1]);
						if (!lastTime.equals(time[0] + time[1])) {
							lastTime = time[0] + time[1];
							holder.dayView.setVisibility(View.VISIBLE);
							holder.monthView.setVisibility(View.VISIBLE);
						} else {
							holder.dayView.setVisibility(View.INVISIBLE);
							holder.monthView.setVisibility(View.INVISIBLE);
						}
					}
					showImages(imageContent, holder.imageContainer);
				}
			}
			return convertView;
		}

		public void showImages(List<String> list, RelativeLayout container) {
			container.removeAllViews();
			for (int i = 0; i < list.size(); i++) {
				if (list.size() == 1) {
					ImageView imageView = new ImageView(context);
					int width = (int) (displayMetrics.density * 75 + 0.5f);
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
					container.addView(imageView, params);
					File file = new File(fileHandlers.sdcardCacheImageFolder, list.get(i) + "@2_2");
					if (file.exists()) {
						imageLoader.displayImage("file://" + file.getAbsolutePath(), imageView);
					} else {
						DownloadFile downloadFile = new DownloadFile(API.DOMAIN_OSS_THUMBNAIL + "images/" + list.get(i) + "@" + width / 2 + "w_" + width / 2 + "h_1c_1e_100q", file.getAbsolutePath());
						downloadFile.view = imageView;
						downloadFile.setDownloadFileListener(thisController.downloadListener);
						downloadFileList.addDownloadFile(downloadFile);
					}
				} else if (list.size() == 2) {
					ImageView imageView = new ImageView(context);
					int width = (int) (displayMetrics.density * 37 + 0.5f);
					int width75 = (int) (displayMetrics.density * 75 + 0.5f);
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width75);
					if (i == 0) {
						params.leftMargin = 0;
					} else {
						params.leftMargin = (int) (38 * displayMetrics.density + 0.5f);
					}
					container.addView(imageView, params);
					File file = new File(fileHandlers.sdcardCacheImageFolder, list.get(i) + "@1_2");
					if (file.exists()) {
						imageLoader.displayImage("file://" + file.getAbsolutePath(), imageView);
					} else {
						DownloadFile downloadFile = new DownloadFile(API.DOMAIN_OSS_THUMBNAIL + "images/" + list.get(i) + "@" + width / 2 + "w_" + width + "h_1c_1e_100q", file.getAbsolutePath());
						downloadFile.view = imageView;
						downloadFile.setDownloadFileListener(thisController.downloadListener);
						downloadFileList.addDownloadFile(downloadFile);
					}
				} else if (list.size() == 3) {
					ImageView imageView = new ImageView(context);
					int width = (int) (displayMetrics.density * 37 + 0.5f);
					int width75 = (int) (displayMetrics.density * 75 + 0.5f);
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width * 2);
					String suffix = "";
					String name = "";
					if (i == 0) {
						name = "@1_2";
						params.leftMargin = 0;
						params.height = width75;
						suffix = "@" + width / 2 + "w_" + width + "h_1c_1e_100q";
					} else if (i == 1) {
						name = "@1_1";
						params.leftMargin = (int) (width + (displayMetrics.density * 1 + 0.5f));
						params.height = width;
						suffix = "@" + width / 2 + "w_" + width / 2 + "h_1c_1e_100q";
					} else {
						name = "@1_1";
						params.leftMargin = (int) (width + (displayMetrics.density * 1 + 0.5f));
						params.topMargin = (int) (width + (displayMetrics.density * 1 + 0.5f));
						params.height = width;
						suffix = "@" + width / 2 + "w_" + width / 2 + "h_1c_1e_100q";
					}
					container.addView(imageView, params);
					File file = new File(fileHandlers.sdcardCacheImageFolder, list.get(i) + name);
					if (file.exists()) {
						imageLoader.displayImage("file://" + file.getAbsolutePath(), imageView);
					} else {
						DownloadFile downloadFile = new DownloadFile(API.DOMAIN_OSS_THUMBNAIL + "images/" + list.get(i) + suffix, file.getAbsolutePath());
						downloadFile.view = imageView;
						downloadFile.setDownloadFileListener(thisController.downloadListener);
						downloadFileList.addDownloadFile(downloadFile);
					}
				} else {
					ImageView imageView = new ImageView(context);
					int width = (int) (displayMetrics.density * 37 + 0.5f);
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
					String suffix = "";
					suffix = "@" + width / 2 + "w_" + width / 2 + "h_1c_1e_100q";
					String name = "@1_1";
					if (i == 0) {
					} else if (i == 1) {
						params.leftMargin = (int) (width + (displayMetrics.density * 1 + 0.5f));
					} else if (i == 2) {
						params.topMargin = (int) (width + (displayMetrics.density * 1 + 0.5f));
					} else if (i == 3) {
						params.leftMargin = (int) (width + (displayMetrics.density * 1 + 0.5f));
						params.topMargin = (int) (width + (displayMetrics.density * 1 + 0.5f));
					} else {
						break;
					}
					container.addView(imageView, params);
					File file = new File(fileHandlers.sdcardCacheImageFolder, list.get(i) + name);
					if (file.exists()) {
						imageLoader.displayImage("file://" + file.getAbsolutePath(), imageView);
					} else {
						DownloadFile downloadFile = new DownloadFile(API.DOMAIN_OSS_THUMBNAIL + "images/" + list.get(i) + suffix, file.getAbsolutePath());
						downloadFile.view = imageView;
						downloadFile.setDownloadFileListener(thisController.downloadListener);
						downloadFileList.addDownloadFile(downloadFile);
					}
				}
			}
		}

		public class ShareHolder {
			// first
			public ImageView converView;
			public ImageView headView;
			public TextView nickNameView;
			public TextView businessView;

			// item
			public TextView dayView;
			public TextView monthView;
			public RelativeLayout imageContainer;
			public TextView textContentView;
			public TextView imageCountView;
		}
	}
}
