package com.lejoying.mc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.lejoying.view.ScanView;

public class ScanQRCodeActivity extends Activity implements
		SurfaceHolder.Callback, PreviewCallback, AutoFocusCallback {

	private Camera camera;

	private final int OPERATE_DECODE_SUCCESS_WEBLOGIN = 0x00001;
	private final int OPERATE_DECODE_FAILED = 0x00002;
	private final int OPERATE_AUTOFOCUS = 0x00003;

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

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scanqrcode);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		sfv_scanqrcode = (SurfaceView) ScanQRCodeActivity.this
				.findViewById(R.id.sfv_scanqrcode);

		handler = new ScanHandler();

		isTake = true;
		multiFormatReader = new MultiFormatReader();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		int width = dm.widthPixels;
		int height = dm.heightPixels;

		if (width < height) {
			int temp = width;
			width = height;
			height = temp;
		}

		screenResolution = new Point(width, height);

		surfaceHolder = sfv_scanqrcode.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		scanview = (ScanView) findViewById(R.id.scanview);
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
			framingRect = getFramingRect(cameraResolution);
			scanview.setFramingRect(framingRect);
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
				// 相机故障或被占用
				finish();
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
	public void finish() {
		destoryCamera();
		super.finish();
	}

	@Override
	protected void onPause() {
		destoryCamera();
		super.onPause();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		initCamera();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

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
			if (processQRCode(rawResult)) {
				what = OPERATE_DECODE_SUCCESS_WEBLOGIN;
			}
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
		camera.setOneShotPreviewCallback(ScanQRCodeActivity.this);
	}

	public Rect getFramingRect(Point cameraResolution) {
		int minSide = cameraResolution.y;
		int maxSide = cameraResolution.x;
		float framingSide = minSide * 0.6f;
		float leftOffset = (minSide - framingSide) / 2;
		float topOffset = (maxSide - framingSide) / 2;
		return new Rect((int) leftOffset, (int) topOffset,
				(int) (leftOffset + framingSide),
				(int) (topOffset + framingSide));
	}

	public void autoFocus() {
		handler.sendEmptyMessageDelayed(OPERATE_AUTOFOCUS, 2000);
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
				new AlertDialog.Builder(ScanQRCodeActivity.this)
						.setTitle("网页登陆成功")
						.setOnCancelListener(new OnCancelListener() {
							@Override
							public void onCancel(DialogInterface dialog) {
								initCamera();
							}
						}).setMessage(msg.obj.toString()).create().show();
				break;
			case OPERATE_DECODE_FAILED:
				if (isTake) {
					if (msg.obj != null) {
						destoryCamera();
						new AlertDialog.Builder(ScanQRCodeActivity.this)
								.setTitle("无效请求")
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
						camera.autoFocus(ScanQRCodeActivity.this);
					}
				}
				break;
			default:
				break;
			}
		}
	}

	private boolean processQRCode(Result rawResult) {
		boolean flag = false;
		String str = rawResult.toString();
		if (str.substring(0, 2).equals("mc")) {
			int index = str.indexOf(":", 3);
			if (str.substring(3, index).equals("weblogin")) {
				flag = true;
			}
		}
		return flag;
	}

}