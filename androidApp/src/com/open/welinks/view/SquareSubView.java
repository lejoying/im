package com.open.welinks.view;

import java.io.File;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.lib.viewbody.ListBody1;
import com.open.lib.viewbody.ListBody1.MyListItemBody;
import com.open.welinks.R;
import com.open.welinks.controller.DownloadFile;
import com.open.welinks.controller.DownloadFileList;
import com.open.welinks.controller.SquareSubController;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.ShareContent;
import com.open.welinks.model.Data.ShareContent.ShareContentItem;
import com.open.welinks.model.Data.Squares.Square;
import com.open.welinks.model.Data.Squares.Square.SquareMessage;

public class SquareSubView {

	public Data data = Data.getInstance();

	public String tag = "SquareSubView";

	public DisplayMetrics displayMetrics;

	public MainView mainView;

	public LayoutInflater mInflater;

	public SquareSubController thisController;

	public ListBody1 squareListBody;

	public RelativeLayout squareView;

	public ImageView squareReleaseButtonView;
	public TextView squareNameView;
	public RelativeLayout squareContainerView;

	public DownloadFileList downloadFileList = DownloadFileList.getInstance();

	public Gson gson = new Gson();

	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DisplayImageOptions options;

	public SquareSubView(MainView mainView) {
		this.mainView = mainView;
	}

	public void initViews() {
		this.displayMetrics = mainView.displayMetrics;
		this.squareView = mainView.squareView;
		this.mInflater = mainView.mInflater;

		squareReleaseButtonView = (ImageView) squareView.findViewById(R.id.squareReleaseButton);
		squareNameView = (TextView) squareView.findViewById(R.id.squareName);
		squareContainerView = (RelativeLayout) squareView.findViewById(R.id.squareContainer);

		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();

		squareListBody = new ListBody1();
		squareListBody.initialize(displayMetrics, squareContainerView);
		squareListBody.active();

		showSquareMessages();
	}

	public void showSquareMessages() {
		Square square = data.squares.squareMap.get("1001");
		List<String> squareMessagesOrder = square.squareMessagesOrder;
		Map<String, SquareMessage> squareMessagesMap = square.squareMessagesMap;
		squareListBody.listItemsSequence.clear();
		squareListBody.containerView.removeAllViews();

		for (int i = 0; i < squareMessagesOrder.size(); i++) {
			String key = squareMessagesOrder.get(i);
			SquareMessage squareMessage = squareMessagesMap.get(key);

			SquareMessageBody squareMessageBody = null;
			squareMessageBody = new SquareMessageBody(squareListBody);
			squareMessageBody.initialize(i);
			squareMessageBody.setData(squareMessage);

			this.squareListBody.listItemsSequence.add("square#" + squareMessage.gsid);
			this.squareListBody.listItemBodiesMap.put("square#" + squareMessage.gsid, squareMessageBody);

			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (115 * displayMetrics.density));
			squareMessageBody.y = this.squareListBody.height;
			squareMessageBody.cardView.setY(squareMessageBody.y);
			squareMessageBody.cardView.setX(0);
			this.squareListBody.height = this.squareListBody.height + 115 * displayMetrics.density;
			this.squareListBody.containerView.addView(squareMessageBody.cardView, layoutParams);
		}
		this.squareListBody.containerHeight = (int) (this.displayMetrics.heightPixels - 38 - displayMetrics.density * 48);
	}

	public class SquareMessageBody extends MyListItemBody {

		SquareMessageBody(ListBody1 listBody) {
			listBody.super();
		}

		public View cardView;

		public ImageView messageImageView;
		public TextView messageContentView;
		public TextView messageAuthorView;

		public String option;

		public DownloadFile downloadFile = null;

		public View initialize(int i) {
			if (i % 2 == 1) {
				this.cardView = mInflater.inflate(R.layout.square_message_item_left, null);
				option = "left";
			} else {
				this.cardView = mInflater.inflate(R.layout.square_message_item_right, null);
				option = "right";
			}

			this.messageImageView = (ImageView) this.cardView.findViewById(R.id.messageImage);
			this.messageContentView = (TextView) this.cardView.findViewById(R.id.messageContent);
			this.messageAuthorView = (TextView) this.cardView.findViewById(R.id.messageAuthor);

			this.itemHeight = 115 * displayMetrics.density;
			super.initialize(cardView);
			return cardView;
		}

		public void setData(SquareMessage message) {
			ShareContent shareContent = gson.fromJson("{shareContentItems:" + message.content + "}", ShareContent.class);
			String textContent = "";
			String imageContent = "";
			List<ShareContentItem> shareContentItems = shareContent.shareContentItems;
			for (int i = 0; i < shareContentItems.size(); i++) {
				ShareContentItem shareContentItem = shareContentItems.get(i);
				if (shareContentItem.type.equals("image")) {
					imageContent = shareContentItem.detail;
					if (!"".equals(textContent))
						break;
				} else if (shareContentItem.type.equals("text")) {
					textContent = shareContentItem.detail;
					if (!"".equals(imageContent))
						break;
				}
			}
			if ("left".equals(option)) {
				this.messageImageView.setImageResource(R.drawable.square_temp);
			} else {
				this.messageImageView.setImageResource(R.drawable.square_temp1);
			}
			this.messageContentView.setText(textContent);
			this.messageAuthorView.setText(message.nickName);

			File sdFile = Environment.getExternalStorageDirectory();
			String fileName = "2092fb60dc1e6f1384ec3ac06012511e0a5e3d2f.jpg" + "@" + displayMetrics.widthPixels + "w_" + this.itemHeight + "h_1c_1e_100q";
			File file = new File(sdFile, "welinks/thumbnail/" + fileName);

			final String url = API.DOMAIN_OSS_THUMBNAIL + "images/" + fileName;
			final String path = file.getAbsolutePath();
			if (file.exists()) {
				imageLoader.displayImage("file://" + path, messageImageView, options, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						downloadFile = new DownloadFile(url, path);
						downloadFile.view = messageImageView;
						downloadFile.view.setTag("image");
						downloadFile.setDownloadFileListener(thisController.downloadListener);
						downloadFileList.addDownloadFile(downloadFile);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					}
				});
			} else {
				File file2 = new File(sdFile, "welinks/images/" + imageContent);
				final String path2 = file2.getAbsolutePath();
				if (file2.exists()) {
					imageLoader.displayImage("file://" + path2, messageImageView, options);
				}
				downloadFile = new DownloadFile(url, path);
				downloadFile.view = messageImageView;
				downloadFile.view.setTag("image");
				downloadFile.setDownloadFileListener(thisController.downloadListener);
				downloadFileList.addDownloadFile(downloadFile);
			}
		}
	}
}
