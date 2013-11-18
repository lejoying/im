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
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ScanQRCodeActivity extends Activity implements
		SurfaceHolder.Callback, PreviewCallback {

	private Camera camera;

	private SurfaceView sfv_scanqrcode;

	private SurfaceHolder surfaceHolder;

	private Handler handler;

	private static final int MIN_PREVIEW_PIXELS = 470 * 320;
	private static final int MAX_PREVIEW_PIXELS = 1280 * 800;

	private boolean isTake;

	private Point cameraResolution;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scanqrcode);
		sfv_scanqrcode = (SurfaceView) ScanQRCodeActivity.this
				.findViewById(R.id.sfv_scanqrcode);
		surfaceHolder = sfv_scanqrcode.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void finish() {
		if (camera != null) {
			isTake = false;
			camera.release();
		}
		super.finish();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			camera = Camera.open();
			camera.setPreviewDisplay(surfaceHolder);
			camera.setDisplayOrientation(90);
			camera.setOneShotPreviewCallback(this);
			isTake = true;
		} catch (IOException e) {
			isTake = false;
			camera.release();
			camera = null;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Camera.Parameters parameters = camera.getParameters();
		int w = width > height ? width : height;
		int h = width > height ? height : width;
		cameraResolution = findBestPreviewSizeValue(parameters, new Point(w, h));
		parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
		camera.setParameters(parameters);
		camera.startPreview();
		final Timer t = new Timer();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				if (isTake) {
					camera.autoFocus(null);
				} else {
					t.cancel();
				}
			}
		}, 0, 2000);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		System.out.println(data);
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

}