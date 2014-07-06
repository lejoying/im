package com.lejoying.wxgs.activity;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.fragment.GroupShareFragment;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.utils.MCImageUtils;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.activity.view.widget.Alert.AlertInputDialog;
import com.lejoying.wxgs.activity.view.widget.Alert.AlertInputDialog.OnDialogClickListener;
import com.lejoying.wxgs.activity.view.widget.Alert.OnLoadingCancelListener;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.handler.OSSFileHandler;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileMessageInfoInterface;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileMessageInfoSettings;
import com.lejoying.wxgs.app.handler.OSSFileHandler.ImageMessageInfo;
import com.lejoying.wxgs.app.handler.OSSFileHandler.UploadFileInterface;
import com.lejoying.wxgs.app.handler.OSSFileHandler.UploadFileSettings;

public class ReleaseImageAndTextActivity extends Activity implements
		OnClickListener {
	MainApplication app = MainApplication.getMainApplication();
	LayoutInflater mInflater;
	GridView gridView;

	int height, width, dip;
	float density;
	int RESULT_TAKEPICTURE = 0x2, RESULT_PERVIEW = 0x4;
	boolean sending = false;
	Bitmaps bitmaps;

	View sl_content, bottom_bar, rl_back, rl_send, rl_sync;
	EditText release_et;

	PopupWindow pop;
	View popView;
	LinearLayout ll_releaselocal, ll_releasecamera;
	File tempFile;

	GestureDetector backViewDetector, sendViewDetector;

	List<String> photoList;
	HashMap<String, HashMap<String, Object>> photoListMap;
	MyGridViewAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.release_imageandtext);
		mInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		dip = (int) (40 * density + 0.5f);
		height = dm.heightPixels;
		width = dm.widthPixels;
		initLayout();
		initData();
		initEvent();
	}

	@Override
	public void onBackPressed() {
		if (!sending) {
			if (pop.isShowing()) {
				pop.dismiss();
			} else {
				mFinish();
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		pop.dismiss();
		if (requestCode == MapStorageDirectoryActivity.RESULT_SELECTPIC
				&& resultCode == Activity.RESULT_OK && data != null) {
			HashMap<String, HashMap<String, Object>> map = (HashMap<String, HashMap<String, Object>>) data
					.getSerializableExtra("photoListMap");
			List<String> list = data.getStringArrayListExtra("photoList");

			for (int i = 0; i < photoList.size(); i++) {
				if (photoListMap.containsKey(photoList.get(i))) {
					if (!photoListMap.get(photoList.get(i)).containsKey("way")) {
						photoList.remove(photoList.get(i));
						i--;
					}
				}
			}
			for (int i = 0; i < list.size(); i++) {
				photoList.add(list.get(i));
				photoListMap.put(list.get(i), map.get(list.get(i)));
			}
			mAdapter.notifyDataSetChanged();
		} else if (requestCode == RESULT_TAKEPICTURE
				&& resultCode == Activity.RESULT_OK) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("contentType", "image/jpg");
			map.put("way", "camera");
			photoList.add(tempFile.getAbsolutePath());
			photoListMap.put(tempFile.getAbsolutePath(), map);
			mAdapter.notifyDataSetChanged();
		} else if (requestCode == RESULT_PERVIEW
				&& resultCode == Activity.RESULT_OK) {
			if (data.getStringArrayListExtra("photoList") != null) {
				photoList.clear();
				photoList = data.getStringArrayListExtra("photoList");
			}
			mAdapter.notifyDataSetChanged();
		}
	}

	void initData() {
		photoList = new ArrayList<String>();
		photoListMap = new HashMap<String, HashMap<String, Object>>();
		mAdapter = new MyGridViewAdapter();
		bitmaps = new Bitmaps();
		gridView.setAdapter(mAdapter);
	}

	void initLayout() {
		rl_back = findViewById(R.id.rl_back);
		rl_send = findViewById(R.id.rl_send);
		rl_sync = findViewById(R.id.rl_sync);
		release_et = (EditText) findViewById(R.id.release_et);
		gridView = (GridView) findViewById(R.id.release_gv);
		sl_content = findViewById(R.id.sl_content);
		popView = mInflater.inflate(R.layout.f_release_sel, null);
		pop = new PopupWindow(popView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		ll_releaselocal = (LinearLayout) popView
				.findViewById(R.id.ll_releaselocal);
		ll_releasecamera = (LinearLayout) popView
				.findViewById(R.id.ll_releasecamera);
		ll_releaselocal.setOnClickListener(this);
		ll_releasecamera.setOnClickListener(this);
	}

	void initEvent() {
		rl_back.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					rl_back.setBackgroundColor(Color.argb(143, 0, 0, 0));
					break;
				case MotionEvent.ACTION_UP:
					rl_back.setBackgroundColor(Color.argb(0, 0, 0, 0));
					break;
				}
				return backViewDetector.onTouchEvent(event);
			}
		});
		rl_send.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					rl_send.setBackgroundColor(Color.argb(143, 0, 0, 0));
					break;
				case MotionEvent.ACTION_UP:
					rl_send.setBackgroundColor(Color.argb(0, 0, 0, 0));
					break;
				}
				return sendViewDetector.onTouchEvent(event);
			}
		});
		rl_sync.setOnClickListener(this);

		sendViewDetector = new GestureDetector(
				ReleaseImageAndTextActivity.this,
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onDown(MotionEvent e) {
						return true;
					}

					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						sendImageTextMessage();
						return true;
					}
				});

		backViewDetector = new GestureDetector(
				ReleaseImageAndTextActivity.this,
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onDown(MotionEvent e) {
						return true;
					}

					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						mFinish();
						return true;
					}
				});
		popView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pop.dismiss();
			}
		});
	}

	void sendImageTextMessage() {
		String messageContent = release_et.getText().toString().trim();
		if (photoList.size() == 0 || "".equals(messageContent)) {
			Alert.showMessage("图文分享内容不完整");
			return;
		}
		sending = true;
		Alert.showLoading(new OnLoadingCancelListener() {
			@Override
			public void loadingCancel() {
				System.out.println("loading ...send message");
			}
		});
		final JSONArray messageJsonArray = new JSONArray();
		JSONObject contentObject = new JSONObject();
		try {
			contentObject.put("type", "text");
			contentObject.put("detail", messageContent);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		messageJsonArray.put(contentObject);
		if (photoList.size() == 0) {
			sendMessage("imagetext", messageJsonArray.toString());
		} else {
			for (int i = 0; i < photoList.size(); i++) {
				final int j = i;
				app.fileHandler
						.getFileMessageInfo(new FileMessageInfoInterface() {

							@Override
							public void setParams(
									FileMessageInfoSettings settings) {
								settings.path = photoList.get(j);
								settings.FILE_TYPE = OSSFileHandler.FILE_TYPE_SDSELECTIMAGE;
								settings.fileName = photoList.get(j);
							}

							@Override
							public void onSuccess(
									ImageMessageInfo imageMessageInfo) {
								checkImage(
										imageMessageInfo,
										(String) photoListMap.get(
												photoList.get(j)).get(
												"contentType"),
										photoList.get(j), "image",
										messageJsonArray);
							}
						});
			}
		}
	}

	public void checkImage(final ImageMessageInfo imageMessageInfo,
			final String contentType, final String path, final String fileType,
			final JSONArray selectedImages) {
		app.networkHandler.connection(new CommonNetConnection() {

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.IMAGE_CHECK;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("filename", imageMessageInfo.fileName);
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
				try {
					if (jData.getBoolean("exists")) {
						if ("image".equals(fileType)) {
							JSONObject imageObject = new JSONObject();
							imageObject.put("type", fileType);
							imageObject
									.put("detail", imageMessageInfo.fileName);
							selectedImages.put(imageObject);
							if (selectedImages.length() - 1 == photoList.size()) {
								sendMessage("imagetext",
										selectedImages.toString());
							}
						} else {
							// TODO SEND VOICE
						}
					} else {

						app.fileHandler.uploadFile(new UploadFileInterface() {

							@Override
							public void setParams(UploadFileSettings settings) {
								settings.imageMessageInfo = imageMessageInfo;
								settings.contentType = contentType;
								settings.fileName = imageMessageInfo.fileName;
								settings.path = path;
								if ("image".equals(fileType)) {
									settings.uploadFileType = OSSFileHandler.UPLOAD_FILE_TYPE_IMAGES;
								} else if ("voice".equals(fileType)) {
									settings.uploadFileType = OSSFileHandler.UPLOAD_FILE_TYPE_VOICES;
								}
							}

							@Override
							public void onSuccess(Boolean flag, String fileName) {
								if ("image".equals(fileType)) {
									try {
										JSONObject imageObject = new JSONObject();
										imageObject.put("type", fileType);
										imageObject.put("detail",
												imageMessageInfo.fileName);
										selectedImages.put(imageObject);
										if (selectedImages.length() - 1 == photoList
												.size()) {
											sendMessage("imagetext",
													selectedImages.toString());
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								} else {
									// TODO SEND VOICE
								}
							}
						});
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	void sendMessage(final String contentType, final String content) {
		app.networkHandler.connection(new CommonNetConnection() {

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.SHARE_SEND;
				settings.params = generateMessageParams(contentType, content);
			}

			@Override
			public void success(JSONObject jData) {
				Alert.removeLoading();
				finish();
			}

			@Override
			protected void unSuccess(JSONObject jData) {
				Alert.removeLoading();
				super.unSuccess(jData);
			}
		});
	}

	public Map<String, String> generateMessageParams(String type, String content) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", app.data.user.phone);
		params.put("accessKey", app.data.user.accessKey);
		params.put("gid", GroupShareFragment.mCurrentGroupShareID);
		JSONObject messageObject = new JSONObject();
		try {
			messageObject.put("type", type);
			messageObject.put("content", content);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		params.put("message", messageObject.toString());
		return params;
	}

	void Sync() {

	}

	void mFinish() {
		if (!"".equals(release_et.getText().toString())
				|| photoList.size() != 0) {
			Alert.createDialog(this).setTitle("您尚有编辑未提交,是否退出?")
					.setOnConfirmClickListener(new OnDialogClickListener() {
						@Override
						public void onClick(AlertInputDialog dialog) {
							finish();
						}
					}).show();
		} else {
			finish();
		}
	}

	public class MyGridViewAdapter extends BaseAdapter {
		int picWidth = (int) (width * 0.29444444f),
				picHeight = (int) (height * 0.165625f);

		public MyGridViewAdapter() {
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return photoList.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			if (position < photoList.size()) {
				return photoList.get(position);
			} else {
				return null;
			}

		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView,
				final ViewGroup parent) {
			final GridViewHolder gridViewHolder;
			final int currentIndex = position;
			if (convertView == null) {
				gridViewHolder = new GridViewHolder();
				convertView = mInflater.inflate(R.layout.view_child, null);
				gridViewHolder.iv_child = (ImageView) convertView
						.findViewById(R.id.iv_child);
				LayoutParams childParams = gridViewHolder.iv_child
						.getLayoutParams();
				childParams.height = picHeight;
				childParams.width = picWidth;
				gridViewHolder.iv_child.setLayoutParams(childParams);
				convertView.setTag(gridViewHolder);
			} else {
				gridViewHolder = (GridViewHolder) convertView.getTag();
			}
			if (position < photoList.size()) {

				if (bitmaps.get(photoList.get(position)) != null) {
					gridViewHolder.iv_child.setImageBitmap(bitmaps
							.get(photoList.get(position)));
				} else {
					bitmaps.put(photoList.get(position), ThumbnailUtils
							.extractThumbnail(MCImageUtils
									.getZoomBitmapFromFile(
											new File(photoList.get(position)),
											picWidth, picHeight), picWidth,
									picHeight));
					gridViewHolder.iv_child.setImageBitmap(bitmaps
							.get(photoList.get(position)));
				}
				gridViewHolder.iv_child
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent(
										ReleaseImageAndTextActivity.this,
										PicAndVoiceDetailActivity.class);
								intent.putExtra("Activity", "MapStrage");
								intent.putExtra("currentIndex",
										currentIndex + 1);
								intent.putStringArrayListExtra("content",
										(ArrayList<String>) photoList);
								startActivityForResult(intent, RESULT_PERVIEW);
							}
						});
			} else {
				gridViewHolder.iv_child
						.setImageResource(R.drawable.release_imgandtext_add);
				gridViewHolder.iv_child
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								pop.showAtLocation((View) parent.getParent(),
										Gravity.CENTER, 0, 0);
							}
						});
			}
			return convertView;
		}

		class GridViewHolder {
			ImageView iv_child;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_sync:
			Sync();
			break;
		case R.id.ll_releaselocal:
			Intent selectFromGallery = new Intent(this,
					MapStorageDirectoryActivity.class);
			if (photoList.size() != 0) {
				selectFromGallery.putExtra("init", false);
			}
			startActivityForResult(selectFromGallery,
					MapStorageDirectoryActivity.RESULT_SELECTPIC);
			break;
		case R.id.ll_releasecamera:
			tempFile = new File(app.sdcardImageFolder, "tempimage.jpg");
			int i = 1;
			while (tempFile.exists()) {
				tempFile = new File(app.sdcardImageFolder, "tempimage" + (i++)
						+ ".jpg");
			}
			Uri uri = Uri.fromFile(tempFile);
			Intent tackPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			tackPicture.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
			tackPicture.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			startActivityForResult(tackPicture, RESULT_TAKEPICTURE);
			break;
		default:
			break;
		}
	}

	public class Bitmaps {
		public Map<String, SoftReference<Bitmap>> softBitmaps = new Hashtable<String, SoftReference<Bitmap>>();

		public void put(String key, Bitmap bitmap) {
			softBitmaps.put(key, new SoftReference<Bitmap>(bitmap));
		}

		public Bitmap get(String key) {
			if (softBitmaps.get(key) == null) {
				return null;
			}
			return softBitmaps.get(key).get();
		}
	}

}
