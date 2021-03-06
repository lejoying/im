package com.lejoying.mc.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
import android.widget.EditText;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.lejoying.mc.R;
import com.lejoying.mc.data.App;
import com.lejoying.mc.view.ScanView;

public class ScanQRCodeFragment extends BaseFragment implements
		SurfaceHolder.Callback, PreviewCallback, AutoFocusCallback {

	App app = App.getInstance();
	
	private View mContent;

	private Camera camera;

	private final int OPERATE_DECODE_SUCCESS_WEBLOGIN = 0x00001;
	private final int OPERATE_DECODE_FAILED = 0x00002;
	private final int OPERATE_AUTOFOCUS = 0x00003;
	private final int OPERATE_SHOWSCAN = 0x0004;

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
		super.onResume();
	}

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContent = inflater.inflate(R.layout.f_scanqrcode, null);

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
				getString(R.string.app_cameranotready);
				getActivity().getSupportFragmentManager().popBackStack();
			}
		}
	}

	public void destoryCamera() {
		isTake = false;
		if (camera != null) {
			camera.release();
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
		autoFocus();
	}

	private class ScanHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case OPERATE_DECODE_SUCCESS_WEBLOGIN:
				destoryCamera();
				new AlertDialog.Builder(getActivity())
						.setTitle(getString(R.string.scan_weblogin))
						.setPositiveButton(getString(R.string.btn_confirm),
								new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										getActivity()
												.getSupportFragmentManager()
												.popBackStack();
									}
								})
						.setNegativeButton(getString(R.string.btn_cancel),
								new OnClickListener() {

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
			case OPERATE_DECODE_FAILED:
				if (isTake) {
					if (msg.obj != null) {
						destoryCamera();
						new AlertDialog.Builder(getActivity())
								.setTitle(getString(R.string.scan_invalid))
								.setOnCancelListener(new OnCancelListener() {
									@Override
									public void onCancel(DialogInterface dialog) {
										initCamera();
									}
								}).setMessage(msg.obj.toString()).create()
								.show();
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
		if (str.substring(0, 2).equals("mc")) {
			int index = str.indexOf(":", 3);
			if (str.substring(3, index).equals("weblogin")) {
				what = OPERATE_DECODE_SUCCESS_WEBLOGIN;
			}
		}
		return what;
	}

	@Override
	public EditText showSoftInputOnShow() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String setMark() {
		// TODO Auto-generated method stub
		return app.scanQRCodeFragment;
	}

}
