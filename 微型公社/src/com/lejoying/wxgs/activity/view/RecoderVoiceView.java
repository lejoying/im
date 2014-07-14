package com.lejoying.wxgs.activity.view;

import com.lejoying.wxgs.activity.MainActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.SimpleOnGestureListener;

@SuppressLint("HandlerLeak")
public class RecoderVoiceView extends ViewGroup {

	public static final int MODE_TIMER = 1;
	public static final int MODE_PROGRESS = 2;
	private static final int REFRESH_PROGRESSVIEW = 0x2b;

	float currentAngle = 0;
	double deltaAngle;

	boolean isShowPlay;
	double angleToRadian = Math.PI / 180;

	boolean isStartProgress;
	boolean isPause;

	float dragPointRadius;
	float dragPointCenterX;
	float dragPointCenterY;

	float dragSpace;
	float prevPercent = -1;

	float timeTextCenterX;
	float timeTextX;
	float timeTextY;

	TimerThread timerThread;

	Paint mPaint;
	StaticView staticView;
	ProgressView progressView;

	Path playPath;
	RectF pauseRectfLeft;
	RectF pauseRectfRight;

	long timerTime;
	long progressTime;
	String showProgressTime = "0";

	float distancePoint;

	long drawDelay = 20;
	long currentTimer;
	String secondSign = "";

	boolean isDrag;

	int mWidth;
	int mHeight;

	RectF timeRectF;

	boolean isDragEnable = true;

	int mode;

	RefreshHandler refreshHandler;

	PlayButtonClickListener mButtonClickListener;
	ProgressListener mProgressListener;

	DeleteRecorderClickListener mDeleteRecorderClickListener;

	GestureDetector gestureDetector;

	public boolean isShowDelete;

	float timeRectWidthScale = 0.56889f; // to out circle width
	float timeRectHeightScale = 0.34375f; // to it width
	float timeRectTopScale = 0.68966f;// to out circle radius

	float centerX;
	float centerY;
	float outCircleRadius;
	float outCircleWidth;
	float circleWidthScale = 0.0088888888888889f;
	float outCircleRadiusScale = 0.6031995277449823f;// 0.5731995277449823f
	float innerCircleRadius;
	float innerCircleRadiusScale = 0.2887640449438202f;

	public RecoderVoiceView(Context context) {
		super(context);
		initialize(context);
	}

	public RecoderVoiceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	private void initialize(Context context) {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);

		mode = MODE_TIMER;

		timerTime = 60000;
		deltaAngle = 360f / timerTime;

		isShowPlay = true;
		isShowDelete = true;

		staticView = new StaticView(context);
		progressView = new ProgressView(context);

		playPath = new Path();

		refreshHandler = new RefreshHandler(progressView);

		this.addView(staticView);
		this.addView(progressView);

		SimpleOnGestureListener simpleOnGestureListener = new SimpleOnGestureListener() {
			float downX;
			float downY;

			@Override
			public boolean onDown(MotionEvent e) {
				isDrag = false;
				prevPercent = -1;
				downX = e.getX();
				downY = e.getY();
				if (isDragEnable) {
					float distanceXToDragPointCenter = downX - dragPointCenterX;
					float distanceYToDragPointCenter = downY - dragPointCenterY;
					if (distanceXToDragPointCenter * distanceXToDragPointCenter
							+ distanceYToDragPointCenter
							* distanceYToDragPointCenter <= (dragPointRadius * dragPointRadius)) {
						isDrag = true;
					} else {
						isDrag = true;
						drag(downX, downY);
					}
				}
				float radiusButom = (innerCircleRadius / 3) * 2;
				if (((centerX - radiusButom) <= downX && downX <= (centerX + radiusButom))
						&& ((centerY + outCircleRadius + distancePoint * 10 + 20) <= downY && (centerY
								+ outCircleRadius + distancePoint * 10 + 20 + radiusButom * 2) >= downY)) {
					// delete already recorder voice aac
					mDeleteRecorderClickListener.onDeleteRecoder();
				}
				return true;
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {
				if (isDrag) {
					drag(e2.getX(), e2.getY());
				}
				return true;
			}

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				float squareDistanceX = (float) Math.pow(downX - centerX, 2);
				float squareDistanceY = (float) Math.pow(downY - centerY, 2);
				if ((squareDistanceX + squareDistanceY < innerCircleRadius// mode
																			// ==
																			// MODE_PROGRESS
																			// &&
						* innerCircleRadius)) {
					if (isShowPlay) {
						setShowPlay(false);
						if (mode == MODE_PROGRESS) {
							startProgress();
						} else {
							startTimer();
						}
						if (mButtonClickListener != null) {
							mButtonClickListener.onPlay();
						}
					} else {
						setShowPlay(true);
						if (mode == MODE_PROGRESS) {
							pauseProgress();
						} else {
							pauseTimer();
						}
						if (mButtonClickListener != null) {
							mButtonClickListener.onPause();
						}
					}
				}
				return true;
			}

			private void drag(float x, float y) {
				float distanceXToCenter = centerX - x;
				float distanceYToCenter = centerY - y;
				float squareXToCenter = distanceXToCenter * distanceXToCenter;
				float squareYToCenter = distanceYToCenter * distanceYToCenter;
				double sqrtToCenter = Math.sqrt(squareXToCenter
						+ squareYToCenter);
				if (sqrtToCenter < outCircleRadius + dragPointRadius * 3
						&& sqrtToCenter >= outCircleRadius - outCircleWidth
								- dragPointRadius * 3) {
					float angle = (float) (Math.asin(distanceYToCenter
							/ sqrtToCenter) / angleToRadian);
					if (x < centerX) {
						currentAngle = angle + 270;
					} else {
						currentAngle = 90 - angle;
					}
					progressView.postInvalidate(
							(int) (centerX - outCircleRadius),
							(int) (centerY - outCircleRadius),
							(int) (centerX + outCircleRadius),
							(int) (centerY + outCircleRadius));
					float percent = currentAngle / 360f;
					if (mProgressListener != null && percent != prevPercent) {
						mProgressListener.onDrag(percent);
						prevPercent = percent;
					}
				}
			}

		};
		gestureDetector = new GestureDetector(context, simpleOnGestureListener);
	}

	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			isDrag = false;
			prevPercent = -1;
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		return true;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		initializeParams(getWidth(), getHeight());
		staticView.layout(0, 0, w, h);
		progressView.layout(0, 0, w, h);
	}

	public boolean isStartProgress() {
		return isStartProgress;
	}

	public void startTimer() {
		if (mode != MODE_TIMER) {
			stopProgress();
			mode = MODE_TIMER;
		}
		// System.out.println(isStartProgress + "----" + timerTime);
		if (!isStartProgress && timerTime > 0) {
			isStartProgress = true;
			if (currentAngle == 360) {
				currentAngle = 0;
			}
			isPause = false;
			deltaAngle = 360f / timerTime;
			refreshProgressView();
			if (timerThread == null || timerThread.isInterrupt) {
				timerThread = new TimerThread();
				timerThread.start();
			}
		}
	}

	public void setTimerTime(long timerTime) {
		this.timerTime = timerTime;
		if (mode == MODE_TIMER) {
			deltaAngle = 360f / timerTime;
		}
		if (!isStartProgress || isPause) {
			refreshProgressView();
		}
	}

	public void resetTimer() {
		if (mode == MODE_TIMER) {
			currentAngle = 0;
			deltaAngle = 360f / timerTime;
			refreshProgressView();
		}
	}

	public void pauseTimer() {
		if (mode == MODE_TIMER && isStartProgress) {
			isStartProgress = false;
			isPause = true;
		}
	}

	public void stopTimer() {
		if (mode == MODE_TIMER || isPause) {
			isStartProgress = false;
			currentAngle = 0;
			refreshHandler.removeMessages(REFRESH_PROGRESSVIEW);
			progressView.postInvalidate();
			if (timerThread != null) {
				timerThread.interrupt();
			}
		}
	}

	public void startProgress() {
		if (mode != MODE_PROGRESS) {
			stopTimer();
			mode = MODE_PROGRESS;
		}
		if (!isStartProgress && progressTime > 0) {
			isStartProgress = true;
			if (currentAngle == 360) {
				currentAngle = 0;
			}
			isPause = false;
			deltaAngle = 360f / progressTime;
			refreshProgressView();
			if (timerThread == null || timerThread.isInterrupt) {
				timerThread = new TimerThread();
				timerThread.start();
			}
			setShowPlay(false);
		}
	}

	public void setProgressTime(long progressTime) {
		this.progressTime = progressTime;
		int showTime = (int) Math.floor(progressTime / 1000f);
		showProgressTime = showTime > 9 ? showTime + secondSign : "0"
				+ showTime + secondSign;
		showProgressTime = "00:" + showProgressTime;
		timeTextX = timeTextCenterX - mPaint.measureText(showProgressTime) / 2;
		if (mode == MODE_PROGRESS) {
			deltaAngle = 360f / progressTime;
		}
		if (!isStartProgress || isPause) {
			refreshProgressView();
		}
	}

	public void pauseProgress() {
		if (mode == MODE_PROGRESS && isStartProgress) {
			isStartProgress = false;
			isPause = true;
			setShowPlay(true);
		}
	}

	public void stopProgress() {
		if (mode == MODE_PROGRESS || isPause) {
			isStartProgress = false;
			currentAngle = 0;
			refreshHandler.removeMessages(REFRESH_PROGRESSVIEW);
			progressView.postInvalidate();
			if (timerThread != null) {
				timerThread.interrupt();
			}
			setShowPlay(true);
		}
	}

	public void resetProgress() {
		if (mode == MODE_PROGRESS) {
			currentAngle = 0;
			deltaAngle = 360f / progressTime;
			refreshProgressView();
		}
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getMode() {
		return mode;
	}

	boolean centerColorFlag = false;

	public void setCenterColor() {
		centerColorFlag = true;
	}

	public void setDragEnable(boolean isEnable) {
		this.isDragEnable = isEnable;
	}

	public void setShowPlay(boolean showPlay) {
		isShowPlay = showPlay;
		staticView.postInvalidate();
	}

	public void setShowDelete(boolean showDelete) {
		isShowDelete = showDelete;
		staticView.postInvalidate();
	}

	private void refreshProgressView() {
		refreshHandler.sendEmptyMessage(REFRESH_PROGRESSVIEW);
	}

	private void initializeParams(int width, int height) {
		if (mWidth != width || mHeight != height) {
			mWidth = width;
			mHeight = height;

			int side = width < height ? width : height;
			centerX = width / 2;
			centerY = (height - MainActivity.statusBarHeight) * 0.4f;
			outCircleRadius = (side * outCircleRadiusScale) / 2;
			outCircleWidth = outCircleRadius * 2 * circleWidthScale;
			innerCircleRadius = (outCircleRadius * 2 * innerCircleRadiusScale) / 2;

			dragSpace = outCircleRadius - innerCircleRadius;

			dragPointRadius = 10;
			float timeRectWidthScale = 1f;
			float timeRectWidth = innerCircleRadius * 2 * timeRectWidthScale;
			float timeRectFL = (mWidth - timeRectWidth) / 2;
			float timeRectHeight = timeRectWidth * timeRectHeightScale;
			float timeRectFT = centerY + outCircleRadius * timeRectTopScale;
			float timeRectFR = timeRectFL + timeRectWidth;
			float timeRectFB = timeRectFT + timeRectHeight;
			timeRectF = new RectF(timeRectFL, timeRectFT, timeRectFR,
					timeRectFB);

			timeTextCenterX = timeRectFL + timeRectWidth / 2;

			timeTextY = centerX + innerCircleRadius * 2;

			playPath.reset();
			float startX = centerX - innerCircleRadius + innerCircleRadius * 2
					* 0.38288f;
			playPath.moveTo(startX - 1.5f, centerY - innerCircleRadius / 2f);
			playPath.lineTo(startX - 1.5f, centerY + innerCircleRadius / 2f);
			playPath.lineTo(startX + 1.5f, centerY + innerCircleRadius / 2f);
			playPath.lineTo(centerX + innerCircleRadius - innerCircleRadius
					* 2f * 0.27667f, centerY + 1.5f);
			playPath.lineTo(centerX + innerCircleRadius - innerCircleRadius
					* 2f * 0.27667f, centerY - 1.5f);
			playPath.lineTo(startX + 1.5f, centerY - innerCircleRadius / 2f);
			playPath.close();

			pauseRectfLeft = new RectF(centerX - innerCircleRadius / 3f,
					centerY - innerCircleRadius / 2 + 3, centerX
							- innerCircleRadius / 8f, centerY
							+ innerCircleRadius / 2 - 3);
			pauseRectfRight = new RectF(centerX + innerCircleRadius / 8f,
					centerY - innerCircleRadius / 2 + 3, centerX
							+ innerCircleRadius / 3f, centerY
							+ innerCircleRadius / 2f - 3);
		}
	}

	private void refreshProgressViewDelay(long delayMillis) {
		refreshHandler.sendEmptyMessageDelayed(REFRESH_PROGRESSVIEW,
				delayMillis);
	}

	private class ProgressView extends View {

		public ProgressView(Context context) {
			super(context);
		}

		@SuppressLint("DrawAllocation")
		@Override
		protected void onDraw(Canvas canvas) {
			mPaint.setColor(Color.WHITE);
			mPaint.setStyle(Paint.Style.STROKE);
			RectF innerRectF = new RectF(centerX - outCircleRadius
					+ dragPointRadius, centerY - outCircleRadius
					+ dragPointRadius, centerX + outCircleRadius
					- dragPointRadius, centerY + outCircleRadius
					- dragPointRadius);
			mPaint.setColor(Color.argb(50, 255, 255, 255));
			mPaint.setStrokeWidth(outCircleWidth);
			canvas.drawCircle(centerX, centerY, outCircleRadius
					- dragPointRadius, mPaint);
			mPaint.setColor(Color.WHITE);
			canvas.drawArc(innerRectF, 270, currentAngle, false, mPaint);

			float sin = (float) Math.sin((currentAngle + 180) * angleToRadian);
			float cos = (float) Math.cos((currentAngle + 180) * angleToRadian);

			dragPointCenterX = centerX - (outCircleRadius - dragPointRadius)
					* sin;
			dragPointCenterY = centerY + (outCircleRadius - dragPointRadius)
					* cos;

			mPaint.setStrokeWidth(0);
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			canvas.drawCircle(dragPointCenterX, dragPointCenterY, 10, mPaint);

			mPaint.setStrokeWidth(outCircleWidth / 2);
			mPaint.setTextSize(20);
			mPaint.setTextScaleX(1.2f);
			if (mode == MODE_TIMER) {
				currentTimer = (long) (timerTime - currentAngle / deltaAngle);
				int current = (int) (Math.ceil(currentTimer / 1000f));
				String show = current > 9 ? current + secondSign : "0"
						+ current + secondSign;
				// canvas.drawText(show,
				// timeTextCenterX - mPaint.measureText(show) / 2,
				// timeTextY, mPaint);
				show = "00:" + show;
				canvas.drawText(show, (mWidth - mPaint.measureText(show)) / 2,
						centerY + innerCircleRadius * 2, mPaint);
			} else if (mode == MODE_PROGRESS) {
				if (timeTextX == 0) {
					timeTextX = timeTextCenterX
							- mPaint.measureText(showProgressTime) / 2;
				}
				// System.out.println( + ">>>>>"
				// + timeTextY);
				// canvas.drawText(showProgressTime,
				// (mWidth - mPaint.measureText(showProgressTime)) / 2,
				// timeTextY, mPaint);
				canvas.drawText(showProgressTime,
						(mWidth - mPaint.measureText(showProgressTime)) / 2,
						centerY + innerCircleRadius * 2, mPaint);
			}
			// canvas.drawText(showProgressTime, timeTextX, timeTextY, mPaint);

			if (isStartProgress) {
				if (currentAngle < 360) {
					// System.out.println(currentAngle + "---------");
					refreshProgressViewDelay(drawDelay);
				} else {
					// System.out
					// .println(currentAngle + "------00000000000000---");
					isStartProgress = false;
					setShowPlay(true);
					currentAngle = 360;
					if (mProgressListener != null) {
						mProgressListener.onProgressEnd();
					}
				}
			}
		}
	}

	public void setProgressListener(ProgressListener l) {
		this.mProgressListener = l;
	}

	public void setPlayButtonClickListener(PlayButtonClickListener l) {
		this.mButtonClickListener = l;
	}

	public void setDeleteRecorderClickListener(DeleteRecorderClickListener l) {
		this.mDeleteRecorderClickListener = l;
	}

	public interface DeleteRecorderClickListener {
		public void onDeleteRecoder();
	}

	public interface ProgressListener {
		public void onDrag(float percent);

		public void onProgressEnd();
	}

	public interface PlayButtonClickListener {
		public void onPlay();

		public void onPause();
	}

	class StaticView extends View {

		public StaticView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onDraw(Canvas canvas) {
			float scale = 0.0518565941101152f;
			distancePoint = outCircleRadius * 2 * scale;
			// canvas.drawARGB(38, 255, 255, 255);
			mPaint.setColor(Color.WHITE);
			mPaint.setStyle(Paint.Style.STROKE);
			if (centerColorFlag) {
				mPaint.setStyle(Paint.Style.FILL);
			}
			mPaint.setStrokeWidth(outCircleWidth);
			canvas.drawCircle(centerX, centerY, innerCircleRadius, mPaint);

			mPaint.setStrokeWidth(0);
			mPaint.setStyle(Paint.Style.FILL);
			if (centerColorFlag) {
				mPaint.setColor(Color.parseColor("#13b6ed"));// 13b6ed
			}
			if (isShowPlay) {
				canvas.drawPath(playPath, mPaint);
			} else {
				canvas.drawRoundRect(pauseRectfLeft, 0, 0, mPaint);
				canvas.drawRoundRect(pauseRectfRight, 0, 0, mPaint);
			}
			mPaint.setColor(Color.WHITE);
			if (isShowDelete) {
				mPaint.setColor(Color.argb(10, 255, 255, 255));
				mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
				canvas.drawCircle(centerX, centerY + outCircleRadius
						+ distancePoint, 5, mPaint);
				mPaint.setColor(Color.argb(25, 255, 255, 255));
				mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
				canvas.drawCircle(centerX, centerY + outCircleRadius
						+ distancePoint * 2, 5, mPaint);
				mPaint.setColor(Color.argb(40, 255, 255, 255));
				mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
				canvas.drawCircle(centerX, centerY + outCircleRadius
						+ distancePoint * 3, 5, mPaint);
				mPaint.setColor(Color.argb(55, 255, 255, 255));
				mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
				canvas.drawCircle(centerX, centerY + outCircleRadius
						+ distancePoint * 4, 5, mPaint);
				mPaint.setColor(Color.argb(70, 255, 255, 255));
				mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
				canvas.drawCircle(centerX, centerY + outCircleRadius
						+ distancePoint * 5, 5, mPaint);
				mPaint.setColor(Color.argb(85, 255, 255, 255));
				mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
				canvas.drawCircle(centerX, centerY + outCircleRadius
						+ distancePoint * 6, 5, mPaint);
				mPaint.setColor(Color.argb(100, 255, 255, 255));
				mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
				canvas.drawCircle(centerX, centerY + outCircleRadius
						+ distancePoint * 7, 5, mPaint);
				mPaint.setColor(Color.WHITE);
				mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
				canvas.drawCircle(centerX, centerY + outCircleRadius
						+ distancePoint * 8, 5, mPaint);
				mPaint.setColor(Color.WHITE);
				mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
				canvas.drawCircle(centerX, centerY + outCircleRadius
						+ distancePoint * 9, 5, mPaint);
				mPaint.setColor(Color.WHITE);
				mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
				canvas.drawCircle(centerX, centerY + outCircleRadius
						+ distancePoint * 10, 5, mPaint);
				mPaint.setColor(Color.WHITE);
				mPaint.setStyle(Paint.Style.STROKE);
				mPaint.setStrokeWidth(outCircleWidth);
				float radiusButom = (innerCircleRadius / 3) * 2;
				canvas.drawCircle(centerX, centerY + outCircleRadius
						+ distancePoint * 10 + 20 + radiusButom, radiusButom,
						mPaint);
				mPaint.setColor(Color.WHITE);
				mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
				mPaint.setStrokeWidth(outCircleWidth * 2);
				canvas.drawLine(centerX - radiusButom / 3, centerY
						+ outCircleRadius + distancePoint * 10 + radiusButom
						+ 20 - radiusButom / 3, centerX + radiusButom / 3,
						centerY + outCircleRadius + distancePoint * 10
								+ radiusButom + 20 + radiusButom / 3, mPaint);
				canvas.drawLine(centerX - radiusButom / 3, centerY
						+ outCircleRadius + distancePoint * 10 + radiusButom
						+ 20 + radiusButom / 3, centerX + radiusButom / 3,
						centerY + outCircleRadius + distancePoint * 10
								+ radiusButom + 20 - radiusButom / 3, mPaint);
			}
			// canvas.drawCircle(centerX, centerY, outCircleRadius, mPaint);
		}
	}

	private class TimerThread extends Thread {
		boolean isInterrupt;

		@Override
		public void interrupt() {
			isInterrupt = true;
			super.interrupt();
		}

		@Override
		public void run() {
			long start = System.currentTimeMillis();
			while (!isInterrupt) {
				long current = System.currentTimeMillis();
				if (!isPause) {
					currentAngle += deltaAngle * (current - start);
				}
				start = current;
				if (currentAngle >= 360) {
					// currentAngle = 0;
					isInterrupt = true;
					break;
				}
				try {
					Thread.sleep(drawDelay);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}

	class RefreshHandler extends Handler {

		ProgressView progressView;

		@SuppressLint("HandlerLeak")
		public RefreshHandler(ProgressView progressView) {
			this.progressView = progressView;
		}

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case REFRESH_PROGRESSVIEW:
				progressView.postInvalidate();
				break;
			}
		}
	}

}
