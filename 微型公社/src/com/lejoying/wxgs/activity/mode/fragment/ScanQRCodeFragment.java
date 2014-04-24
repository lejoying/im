package com.lejoying.wxgs.activity.mode.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.view.ScanView;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.data.entity.Group;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.parser.JSONParser;

public class ScanQRCodeFragment extends BaseFragment implements
		SurfaceHolder.Callback, PreviewCallback, AutoFocusCallback {

	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;
	private View mContent;

	private Camera camera;

	private final int OPERATE_DECODE_SUCCESS_WEBLOGIN = 0x00001;
	private final int OPERATE_DECODE_FAILED = 0x00002;
	private final int OPERATE_AUTOFOCUS = 0x00003;
	private final int OPERATE_SHOWSCAN = 0x0004;
	private final int OPERATE_DECODE_SUCCESS_USERCARD = 0x00005;
	private final int OPERATE_DECODE_SUCCESS_GROUPCARD = 0x00006;

	private SurfaceView sfv_scanqrcode;

	private SurfaceHolder surfaceHolder;

	private Handler handler;

	private static final int MIN_PREVIEW_PIXELS = 470 * 320;
	private static final int MAX_PREVIEW_PIXELS = 1280 * 800;

	private boolean isTake;

	private Point screenResolution;
	private Point cameraResolution;

	private MultiFormatReader multiFormatReader;

	private ScanView scanview;

	private Rect framingRect;

	private int count = 0;

	private String ScanContent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

		int width = dm.widthPixels;
		int height = dm.heightPixels;

		if (width < height) {
			width ^= height;
			height ^= width;
			width ^= height;
		}
		handler = new ScanHandler();
		screenResolution = new Point(width, height);
		isTake = true;
		multiFormatReader = new MultiFormatReader();
	}

	@Override
	public void onResume() {
		CircleMenu.showBack();
		super.onResume();
	}

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContent = inflater.inflate(R.layout.fragment_scanqrcode, null);

		sfv_scanqrcode = (SurfaceView) mContent
				.findViewById(R.id.sfv_scanqrcode);

		surfaceHolder = sfv_scanqrcode.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		scanview = (ScanView) mContent.findViewById(R.id.scanview);

		scanview.setFramingRect(getFramingRect(screenResolution));

		return mContent;
	}

	public void initCamera() {
		isTake = true;
		try {
			camera = Camera.open();
			camera.setPreviewDisplay(surfaceHolder);
			camera.setDisplayOrientation(90);
			Camera.Parameters parameters = camera.getParameters();
			cameraResolution = findBestPreviewSizeValue(parameters,
					screenResolution);
			framingRect = getPreviewFramingRect(cameraResolution);
			parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
			camera.setParameters(parameters);
			camera.startPreview();
			autoFocus();
			reDecode();
		} catch (Exception e) {
			e.printStackTrace();
			count++;
			if (count < 2) {
				destoryCamera();
				initCamera();
			} else {
				// TODO can't initialize camera , return
			}
		}
	}

	public void destoryCamera() {
		isTake = false;
		if (camera != null) {
			camera.release();
			camera = null;
		}
	}

	@Override
	public void onPause() {
		destoryCamera();
		super.onPause();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		new Thread() {
			public void run() {
				initCamera();
				if (!isTake) {
					destoryCamera();
				}
			}
		}.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		Result rawResult = null;
		PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data,
				cameraResolution.x, cameraResolution.y, framingRect.top,
				framingRect.left, framingRect.bottom - framingRect.top,
				framingRect.right - framingRect.left, false);
		if (source != null) {
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
			try {
				rawResult = multiFormatReader.decodeWithState(bitmap);
			} catch (ReaderException re) {
				// continue
			} finally {
				multiFormatReader.reset();
			}
		}

		int what = OPERATE_DECODE_FAILED;
		if (rawResult != null) {
			what = processQRCode(rawResult);
		}
		Message message = handler.obtainMessage(what, rawResult);
		message.sendToTarget();
	}

	private Point findBestPreviewSizeValue(Camera.Parameters parameters,
			Point screenResolution) {

		List<Camera.Size> rawSupportedSizes = parameters
				.getSupportedPreviewSizes();
		if (rawSupportedSizes == null) {
			Camera.Size defaultSize = parameters.getPreviewSize();
			return new Point(defaultSize.width, defaultSize.height);
		}

		// Sort by size, descending
		List<Camera.Size> supportedPreviewSizes = new ArrayList<Camera.Size>(
				rawSupportedSizes);
		Collections.sort(supportedPreviewSizes, new Comparator<Camera.Size>() {
			@Override
			public int compare(Camera.Size a, Camera.Size b) {
				int aPixels = a.height * a.width;
				int bPixels = b.height * b.width;
				if (bPixels < aPixels) {
					return -1;
				}
				if (bPixels > aPixels) {
					return 1;
				}
				return 0;
			}
		});

		Point bestSize = null;
		float screenAspectRatio = (float) screenResolution.x
				/ (float) screenResolution.y;

		float diff = Float.POSITIVE_INFINITY;
		for (Camera.Size supportedPreviewSize : supportedPreviewSizes) {
			int realWidth = supportedPreviewSize.width;
			int realHeight = supportedPreviewSize.height;
			int pixels = realWidth * realHeight;
			if (pixels < MIN_PREVIEW_PIXELS || pixels > MAX_PREVIEW_PIXELS) {
				continue;
			}
			boolean isCandidatePortrait = realWidth < realHeight;
			int maybeFlippedWidth = isCandidatePortrait ? realHeight
					: realWidth;
			int maybeFlippedHeight = isCandidatePortrait ? realWidth
					: realHeight;
			if (maybeFlippedWidth == screenResolution.x
					&& maybeFlippedHeight == screenResolution.y) {
				Point exactPoint = new Point(realWidth, realHeight);
				return exactPoint;
			}
			float aspectRatio = (float) maybeFlippedWidth
					/ (float) maybeFlippedHeight;
			float newDiff = Math.abs(aspectRatio - screenAspectRatio);
			if (newDiff < diff) {
				bestSize = new Point(realWidth, realHeight);
				diff = newDiff;
			}
		}

		if (bestSize == null) {
			Camera.Size defaultSize = parameters.getPreviewSize();
			bestSize = new Point(defaultSize.width, defaultSize.height);

		}

		return bestSize;
	}

	public void reDecode() {
		if (isTake) {
			camera.setOneShotPreviewCallback(this);
		}
	}

	public Rect getFramingRect(Point screenResolution) {
		int minSide = screenResolution.y;
		int maxSide = screenResolution.x;
		float framingSide = minSide * 0.6f;
		float leftOffset = (minSide - framingSide) / 2;
		float topOffset = (maxSide - framingSide) / 2;
		return new Rect((int) leftOffset, (int) topOffset,
				(int) (leftOffset + framingSide),
				(int) (topOffset + framingSide));
	}

	public Rect getPreviewFramingRect(Point cameraResolution) {
		return getFramingRect(cameraResolution);
	}

	public void autoFocus() {
		if (isTake) {
			handler.sendEmptyMessageDelayed(OPERATE_AUTOFOCUS, 2000);
		}
	}

	@Override
	public void onAutoFocus(boolean success, Camera camera) {
		if (camera != null) {
			if (isTake) {
				autoFocus();
			}
		}
	}

	@SuppressLint("HandlerLeak")
	class ScanHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case OPERATE_DECODE_SUCCESS_WEBLOGIN:
				destoryCamera();
				new AlertDialog.Builder(getActivity()).setTitle("网页端登录")
						.setPositiveButton("确定", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								int index = ScanContent.indexOf(":", 3);
								mMainModeManager.back();
								destoryCamera();
								webScanQRCodelogin(ScanContent
										.substring(index + 1));
								getActivity().getSupportFragmentManager()
										.popBackStack();
							}
						}).setNegativeButton("取消", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								initCamera();
							}
						}).setOnCancelListener(new OnCancelListener() {
							@Override
							public void onCancel(DialogInterface dialog) {
								initCamera();
							}
						}).setCancelable(false).create().show();
				break;
			case OPERATE_DECODE_SUCCESS_USERCARD:
				int index = ScanContent.indexOf(":", 3);
				mMainModeManager.back();
				scanUserCard(ScanContent.substring(index + 1));
				destoryCamera();
				break;
			case OPERATE_DECODE_SUCCESS_GROUPCARD:
				int indexg = ScanContent.indexOf(":", 3);
				mMainModeManager.back();
				scanGroupCard(ScanContent.substring(indexg + 1));
				destoryCamera();
				break;
			case OPERATE_DECODE_FAILED:
				if (isTake) {
					if (msg.obj != null) {
						reDecode();
						// destoryCamera();
						// new AlertDialog.Builder(getActivity())
						// .setTitle("aa")
						// .setOnCancelListener(new OnCancelListener() {
						// @Override
						// public void onCancel(DialogInterface dialog) {
						// initCamera();
						// }
						// }).setMessage(msg.obj.toString()).create()
						// .show();
					} else {
						reDecode();
					}
				}
				break;
			case OPERATE_AUTOFOCUS:
				if (camera != null) {
					if (isTake) {
						camera.autoFocus(ScanQRCodeFragment.this);
					}
				}
				break;
			case OPERATE_SHOWSCAN:

				break;
			default:
				break;
			}
		}
	}

	private int processQRCode(Result rawResult) {
		int what = OPERATE_DECODE_FAILED;
		String str = rawResult.toString();
		ScanContent = str;
		if (str.substring(0, 2).equals("mc")) {
			int index = str.indexOf(":", 3);
			if (str.substring(3, index).equals("weblogin")) {
				what = OPERATE_DECODE_SUCCESS_WEBLOGIN;
			} else if (str.substring(3, index).equals("usercard")) {
				what = OPERATE_DECODE_SUCCESS_USERCARD;
			} else if (str.substring(3, index).equals("groupcard")) {
				what = OPERATE_DECODE_SUCCESS_GROUPCARD;
			}
		}
		return what;
	}

	public void setMode(MainModeManager mainMode) {
		this.mMainModeManager = mainMode;
	}

	public void webScanQRCodelogin(final String sessionID) {
		app.networkHandler.connection(new CommonNetConnection() {
			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.WEBCODE_WEBCODELOGIN;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("sessionID", sessionID);
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
			}
		});
	}

	public void scanUserCard(final String phone) {
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
				initCamera();
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
								data.user.sex = friend.sex;
								data.user.id = friend.id;
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

	public void scanGroupCard(final String gid) {
		app.networkHandler.connection(new CommonNetConnection() {
			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.GROUP_GET;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("gid",
						(Long.valueOf("100000000000") - Long.valueOf(gid)) + "");
				params.put("type", "group");
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
				initCamera();
				JSONObject groupJSON = null;
				try {
					groupJSON = jData.getJSONObject("group");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				Group group = new Group();
				try {
					group.gid = groupJSON.getInt("gid");
					try {
						group.icon = groupJSON.getString("icon");
					} catch (JSONException e) {
					}
					try {
						group.name = groupJSON.getString("name");
					} catch (JSONException e) {
					}
					try {
						group.description = groupJSON.getString("description");
					} catch (JSONException e) {
					}
				} catch (Exception e) {
				}
				mMainModeManager.mGroupBusinessCardFragment.mGroup = group;
				mMainModeManager
						.showNext(mMainModeManager.mGroupBusinessCardFragment);
			}
		});
	}
}
