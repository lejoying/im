package com.open.welinks.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.open.lib.TouchImageView;
import com.open.welinks.R;
import com.open.welinks.controller.ShareReleaseImageTextController;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.LocalStatus.LocalData.ShareDraft;
import com.open.welinks.model.Data.UserInformation.User.Location;
import com.open.welinks.utils.BaseDataUtils;

public class ShareReleaseImageTextView {
	public Data data = Data.getInstance();
	public String tag = "ShareReleaseImageTextView";

	public Context context;
	public ShareReleaseImageTextView thisView;
	public ShareReleaseImageTextController thisController;
	public Activity thisActivity;

	public EditText mEditTextView;
	public RelativeLayout mImagesContentView;
	public RelativeLayout mReleaseButtomBarView;
	public RelativeLayout addressLayout;

	public TextView mCancleButtonView;
	public TextView mConfirmButtonView;
	public TextView address;
	public ImageView mSelectImageButtonView;
	public ImageView mFaceView;
	public ImageView mVoiceView;

	public DisplayMetrics displayMetrics;
	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DisplayImageOptions options;

	public View maxView;

	public PopupWindow popDialogView;
	public View groupEditor, dialogGroupEditor, dialogView, buttons, manage, buttonOne, buttonTwo, buttonThree, background, onTouchDownView, onLongPressView;
	public DragSortListView groupCircleList;
	public ImageView moreView, rditorLine;
	public TextView dialogGroupEditorConfirm, dialogGroupEditorCancel, groupEditorConfirm, groupEditorCancel, backTitileView, titleView, sectionNameTextView, buttonOneText, buttonTwoText, buttonThreeText;

	public View dialogContainer;

	public View singleButton;

	public LayoutInflater mInflater;
	public AddressDialogAdapter dialogAdapter;
	public ListController listController;

	public MyScrollImageBody myScrollImageBody;

	public float imageHeightScale1 = 0.7586206896551724f;

	public int showImageHeight;

	public ShareReleaseImageTextView(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;
		this.thisView = this;
	}

	public void initView() {
		displayMetrics = new DisplayMetrics();
		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		mInflater = thisActivity.getLayoutInflater();
		if (thisController.gtype.equals("square")) {
			showImageHeight = (int) ((displayMetrics.widthPixels - 20 * displayMetrics.density - 0.5f) * thisController.imageHeightScale);
		} else {
			showImageHeight = (int) (displayMetrics.widthPixels * thisController.imageHeightScale);
		}

		thisActivity.setContentView(R.layout.share_release_imagetext);
		maxView = thisActivity.findViewById(R.id.maxView);
		mEditTextView = (EditText) thisActivity.findViewById(R.id.releaseTextContentView);
		mImagesContentView = (RelativeLayout) thisActivity.findViewById(R.id.releaseImagesContent);
		mReleaseButtomBarView = (RelativeLayout) thisActivity.findViewById(R.id.releaseButtomBar);
		addressLayout = (RelativeLayout) thisActivity.findViewById(R.id.addressLayout);
		mCancleButtonView = (TextView) thisActivity.findViewById(R.id.releaseCancel);
		mConfirmButtonView = (TextView) thisActivity.findViewById(R.id.releaseConfirm);
		address = (TextView) thisActivity.findViewById(R.id.address);
		mSelectImageButtonView = (ImageView) thisActivity.findViewById(R.id.selectImageButton);
		mFaceView = (ImageView) thisActivity.findViewById(R.id.releaseFace);
		mVoiceView = (ImageView) thisActivity.findViewById(R.id.releaseVoice);
		if (thisController.type.equals("text")) {
			mEditTextView.setHint("请输入文本内容");
		} else if (thisController.type.equals("album")) {
			mEditTextView.setHint("请输入相册描述");
		} else if (thisController.type.equals("imagetext")) {
			mEditTextView.setHint("请输入图文内容");
		}

		int widthItem = displayMetrics.widthPixels / 5;
		RelativeLayout.LayoutParams cancleParams = (LayoutParams) mCancleButtonView.getLayoutParams();
		cancleParams.leftMargin = 0;
		cancleParams.width = widthItem;

		RelativeLayout.LayoutParams mVoiceViewParams = (LayoutParams) mVoiceView.getLayoutParams();
		mVoiceViewParams.leftMargin = widthItem * 1;
		mVoiceViewParams.width = widthItem;

		RelativeLayout.LayoutParams mFaceViewParams = (LayoutParams) mFaceView.getLayoutParams();
		mFaceViewParams.leftMargin = widthItem * 2;
		mFaceViewParams.width = widthItem;

		RelativeLayout.LayoutParams mSelectImageButtonViewParams = (LayoutParams) mSelectImageButtonView.getLayoutParams();
		mSelectImageButtonViewParams.leftMargin = widthItem * 3;
		mSelectImageButtonViewParams.width = widthItem;

		RelativeLayout.LayoutParams mConfirmButtonViewParams = (LayoutParams) mConfirmButtonView.getLayoutParams();
		mConfirmButtonViewParams.leftMargin = widthItem * 4;
		mConfirmButtonViewParams.width = widthItem;

		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).considerExifParams(true).displayer(new RoundedBitmapDisplayer(0)).build();
		myScrollImageBody = new MyScrollImageBody();
		myScrollImageBody.initialize(mImagesContentView);

		thisController.parser.check();
		if (data.localStatus.localData.notSendShareMessagesMap != null) {
			ShareDraft shareDraft = data.localStatus.localData.notSendShareMessagesMap.get(thisController.gtype);
			if (shareDraft != null) {
				this.mEditTextView.setText(shareDraft.content);
				if (!"".equals(shareDraft.imagesContent)) {
					data.tempData.selectedImageList = thisController.gson.fromJson(shareDraft.imagesContent, new TypeToken<ArrayList<String>>() {
					}.getType());
					this.showSelectedImages();
				}
			}
		}
		if (thisController.address != null) {
			addressLayout.setVisibility(View.VISIBLE);
			((RelativeLayout.LayoutParams) mEditTextView.getLayoutParams()).bottomMargin = BaseDataUtils.dpToPxint(90);
			address.setText(thisController.address);
			initializationGroupCirclesDialog();
		} else {
			((RelativeLayout.LayoutParams) mEditTextView.getLayoutParams()).bottomMargin = BaseDataUtils.dpToPxint(50);
			addressLayout.setVisibility(View.GONE);
		}
	}

	public void initializationGroupCirclesDialog() {
		dialogView = mInflater.inflate(R.layout.dialog_listview, null);
		groupCircleList = (DragSortListView) dialogView.findViewById(R.id.content);
		dialogContainer = dialogView.findViewById(R.id.container);
		buttons = dialogView.findViewById(R.id.buttons);
		manage = dialogView.findViewById(R.id.manage);
		manage.setVisibility(View.GONE);
		background = dialogView.findViewById(R.id.background);
		buttonOne = dialogView.findViewById(R.id.buttonOne);
		buttonTwo = dialogView.findViewById(R.id.buttonTwo);
		buttonThree = dialogView.findViewById(R.id.buttonThree);
		dialogGroupEditor = dialogView.findViewById(R.id.groupEditor);
		rditorLine = (ImageView) dialogView.findViewById(R.id.rditorLine);
		buttonOneText = (TextView) dialogView.findViewById(R.id.buttonOneText);
		buttonTwoText = (TextView) dialogView.findViewById(R.id.buttonTwoText);
		buttonThreeText = (TextView) dialogView.findViewById(R.id.buttonThreeText);
		dialogGroupEditorConfirm = (TextView) dialogView.findViewById(R.id.confirm);
		dialogGroupEditorCancel = (TextView) dialogView.findViewById(R.id.cancel);

		this.singleButton = dialogView.findViewById(R.id.singleButton);
		this.singleButton.setVisibility(View.GONE);

		popDialogView = new PopupWindow(dialogView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		popDialogView.setBackgroundDrawable(new BitmapDrawable());
		showLocationList();
	}

	public void showLocationList() {
		if (dialogAdapter == null) {
			dialogAdapter = new AddressDialogAdapter();
			groupCircleList.setAdapter(dialogAdapter);
			listController = new ListController(groupCircleList, dialogAdapter);
			groupCircleList.setFloatViewManager(listController);
			groupCircleList.setOnItemClickListener(listController);
		} else {
			dialogAdapter.notifyDataSetChanged();
		}
	}

	public class AddressDialogAdapter extends BaseAdapter {
		public List<Location> addressList;

		public AddressDialogAdapter() {
			addressList = new ArrayList<Data.UserInformation.User.Location>(thisController.data.userInformation.currentUser.commonUsedLocations);
			Location location = data.userInformation.currentUser.new Location();
			location.address = thisController.address;
			location.longitude = thisController.longitude;
			location.latitude = thisController.latitude;
			location.remark = "当前地址";
			addressList.add(0, location);
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return addressList.size();
		}

		@Override
		public Object getItem(int position) {
			return addressList.get(position);
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
				convertView = mInflater.inflate(R.layout.address_list_dialog_item, null, false);
				holder.selectedStatus = (ImageView) convertView.findViewById(R.id.selectedStatus);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.address = (TextView) convertView.findViewById(R.id.address);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			Location location = addressList.get(position);
			holder.name.setText(location.remark);
			holder.address.setText(location.address);
			return convertView;
		}

		class Holder {
			public ImageView status, selectedStatus;
			public TextView name, address;
		}
	}

	public class ListController extends DragSortController implements android.widget.AdapterView.OnItemClickListener {
		private AddressDialogAdapter adapter;

		public ListController(DragSortListView dslv, AddressDialogAdapter dialogAdapter) {
			super(dslv);
			this.adapter = dialogAdapter;
			setRemoveEnabled(false);
			setSortEnabled(false);
		}

		@Override
		public View onCreateFloatView(int position) {
			View view = adapter.getView(position, null, thisView.groupCircleList);
			view.setBackgroundResource(R.drawable.card_login_background_press);
			return null;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Location location = (Location) adapter.getItem(position);
			thisController.address = location.address;
			thisController.longitude = location.longitude;
			thisController.latitude = location.latitude;
			thisView.address.setText(thisController.address);
			changePopupWindow();
		}
	}

	public void changePopupWindow() {
		if (popDialogView.isShowing()) {
			popDialogView.dismiss();
		} else {
			popDialogView.showAtLocation(maxView, Gravity.CENTER, 0, 0);
		}
	}

	int width;

	public void showSelectedImages() {
		this.mImagesContentView.removeAllViews();
		ArrayList<String> selectedImageList = data.tempData.selectedImageList;
		if (selectedImageList.size() > 0) {
			this.mImagesContentView.setVisibility(View.VISIBLE);
			RelativeLayout.LayoutParams layoutParams = (LayoutParams) this.mEditTextView.getLayoutParams();
			layoutParams.bottomMargin = (int) (displayMetrics.density * 100 + 0.5f);
		} else {
			this.mImagesContentView.setVisibility(View.GONE);
			RelativeLayout.LayoutParams layoutParams = (LayoutParams) this.mEditTextView.getLayoutParams();
			layoutParams.bottomMargin = (int) (displayMetrics.density * 50 + 0.5f);
		}
		for (int i = 0; i < selectedImageList.size(); i++) {
			String key = selectedImageList.get(i);
			ImageBody imageBody = new ImageBody();
			imageBody.initialize();

			width = (int) (displayMetrics.density * 50);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, width);
			myScrollImageBody.contentView.addView(imageBody.imageView, layoutParams);
			float x = i * (width + 2 * displayMetrics.density) + 2 * displayMetrics.density;
			if (i == 0) {
				x = 2 * displayMetrics.density;
			}
			imageBody.imageView.setX(x);// Translation
			imageLoader.displayImage("file://" + key, imageBody.imageView, options);
			myScrollImageBody.selectedImagesSequence.add(key);
			myScrollImageBody.selectedImagesSequenceMap.put(key, imageBody);
			imageBody.imageView.setTag(i);
			imageBody.imageView.setOnClickListener(thisController.monClickListener);
			imageBody.imageView.setOnTouchListener(thisController.mScrollOnTouchListener);
		}
		myScrollImageBody.contentView.setOnTouchListener(thisController.onTouchListener);
	}

	public class MyScrollImageBody {
		public ArrayList<String> selectedImagesSequence = new ArrayList<String>();
		public HashMap<String, ImageBody> selectedImagesSequenceMap = new HashMap<String, ImageBody>();

		public RelativeLayout contentView;

		public RelativeLayout initialize(RelativeLayout view) {
			this.contentView = view;
			return view;
		}

		public void recordChildrenPosition() {
			for (int i = 0; i < selectedImagesSequence.size(); i++) {
				String key = selectedImagesSequence.get(i);
				ImageBody imageBody = selectedImagesSequenceMap.get(key);
				imageBody.x = imageBody.imageView.getX();
				imageBody.y = imageBody.imageView.getY();
			}
		}

		public void setChildrenPosition(float deltaX, float deltaY) {
			float screenWidth = displayMetrics.widthPixels;
			float totalLength = selectedImagesSequence.size() * (width + 2 * displayMetrics.density) + 2 * displayMetrics.density;
			if (totalLength < screenWidth) {
				return;
			}
			for (int i = 0; i < selectedImagesSequence.size(); i++) {
				String key = selectedImagesSequence.get(i);
				ImageBody imageBody = selectedImagesSequenceMap.get(key);
				if ((imageBody.x + deltaX) < (screenWidth - totalLength))
					break;
				if (i == 0 && (imageBody.x + deltaX) > (5 * displayMetrics.density))
					break;
				imageBody.imageView.setX(imageBody.x + deltaX);
				imageBody.imageView.setY(imageBody.y + deltaY);
			}
		}
	}

	public class ImageBody {
		public int i;

		public float x;
		public float y;
		public TouchImageView imageView;

		public TouchImageView initialize() {
			this.imageView = new TouchImageView(context);
			return this.imageView;
		}
	}
}
