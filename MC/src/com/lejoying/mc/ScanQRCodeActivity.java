package com.lejoying.mc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Point;
import android.hardware.Camera;
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

public class ScanQRCodeActivity extends Activity implements
		SurfaceHolder.Callback, PreviewCallback {

	private Camera camera;

	private final int OPERATE_DECODE_SUCCESS = 0x00001;
	private final int OPERATE_DECODE_FAILED = 0x00002;

	private SurfaceView sfv_scanqrcode;

	private SurfaceHolder surfaceHolder;

	private Handler handler;

	private static final int MIN_PREVIEW_PIXELS = 470 * 320;
	private static final int MAX_PREVIEW_PIXELS = 1280 * 800;

	private boolean isTake;

	private Point screenResolution;
	private Point cameraResolution;

	private MultiFormatReader multiFormatReader;

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
			parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
			camera.setParameters(parameters);
			camera.startPreview();
			reDecode();
			final Timer t2 = new Timer();
			t2.schedule(new TimerTask() {
				@Override
				public void run() {
					if (isTake) {
						camera.autoFocus(null);
					} else {
						t2.cancel();
					}
				}
			}, 0, 2000);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void destoryCamera() {
		if (camera != null) {
			isTake = false;
			camera.release();
		}
	}

	@Override
	public void finish() {
		destoryCamera();
		super.finish();
	}

	@Override
	protected void onResume() {
		if (!isTake) {
			initCamera();
		}
		super.onResume();
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
				cameraResolution.x, cameraResolution.y, 0, 0, 1280, 720, false);
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

		if (rawResult != null) {
			Message message = handler.obtainMessage(OPERATE_DECODE_SUCCESS,
					rawResult);
			message.sendToTarget();
		} else {
			Message message = handler.obtainMessage(OPERATE_DECODE_FAILED);
			message.sendToTarget();
		}
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

	private class ScanHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case OPERATE_DECODE_SUCCESS:
				System.out.println(msg.obj.toString());

				break;
			case OPERATE_DECODE_FAILED:
				if (isTake)
					reDecode();
				break;
			default:
				break;
			}
		}
	}

}