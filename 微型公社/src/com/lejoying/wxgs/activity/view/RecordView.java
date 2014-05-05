package com.lejoying.wxgs.activity.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class RecordView extends ViewGroup {

	public static final int MODE_TIMER = 1;
	public static final int MODE_PROGRESS = 2;
	private static final int REFRESH_PROGRESSVIEW = 0x2b;

	int mode;

	Paint mPaint;

	float outCircleBorderWidthScale = 0.01124f; // to canvas width
	float innerRaidusScale = 0.77667f; // to out circle radius
	float innerCirlceBorderWidthScale = 0.60143f; // to out circle width
	float timeRectWidthScale = 0.56889f; // to out circle width
	float timeRectHeightScale = 0.34375f; // to it width
	float timeRectTopScale = 0.68966f;// to out circle radius
	float dragPointRadiusScale = 0.07692f;// to out circle radius
	float timeTextSizeScale = 0.3914f; // to time rect height
	float playButtonRadiusScale = 0.33811f; // to out circle radius
	float playButtonBorderWidthScale = 0.66667f; // to out circle border width

	RectF outRectF;
	RectF innerRectF;
	RectF timeRectF;

	float centerX;
	float centerY;

	float timeTextCenterX;
	float timeTextX;
	float timeTextY;

	float outCircleBorderWidth;
	float innerCircleBorderWidth;

	float timeTextSize;

	float innerCircleRadius;

	float dragPointRadius;
	float dragPointCenterX;
	float dragPointCenterY;

	float playButtonRadius;
	float playButtonBorderWidth;

	float currentAngle;
	double deltaAngle;

	int innerCircleColor;

	int mWidth;
	int mHeight;

	boolean isStartProgress;
	boolean isPause;

	boolean isDragEnable = true;

	boolean isDrag;
	float prevPercent = -1;

	float dragSpace;

	StaticView staticView;
	ProgressView progressView;

	long timerTime;
	long progressTime;
	String showProgressTime = "0''";

	long drawDelay = 20;
	String secondSign = "''";
	long currentTimer;

	double angleToRadian = Math.PI / 180;

	ProgressListener mProgressListener;

	PlayButtonClickListener mButtonClickListener;

	RefreshHandler refreshHandler;

	TimerThread timerThread;

	GestureDetector gestureDetector;

	Path playPath;
	RectF pauseRectfLeft;
	RectF pauseRectfRight;

	boolean isShowPlay;

	public RecordView(Context context) {
		super(context);
		initialize(context);
	}

	public RecordView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	private void initialize(Context context) {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		innerCircleColor = Color.argb(50, 255, 255, 255);
		staticView = new StaticView(context);
		progressView = new ProgressView(context);
		refreshHandler = new RefreshHandler(progressView);
		mode = MODE_TIMER;

		isShowPlay = true;

		playPath = new Path();

		timerTime = 60000;
		deltaAngle = 260f / timerTime;

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
							* distanceYToDragPointCenter <= dragPointRadius
							* dragPointRadius) {
						isDrag = true;
					} else {
						drag(downX, downY);
						isDrag = true;
					}
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
				if (mode == MODE_PROGRESS
						&& (squareDistanceX + squareDistanceY < playButtonRadius
								* playButtonRadius)) {
					if (isShowPlay) {
						startProgress();
						if (mButtonClickListener != null) {
							mButtonClickListener.onPlay();
						}
					} else {
						pauseProgress();
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
				if (sqrtToCenter < innerCircleRadius + dragSpace
						&& sqrtToCenter >= innerCircleRadius
								- innerCircleBorderWidth - dragSpace) {
					float angle = (float) (Math.asin(distanceYToCenter
							/ sqrtToCenter) / angleToRadian);
					if (angle < -40 && angle > -45) {
						angle = -40;
					}
					if (angle >= -40) {
						if (x < centerX) {
							currentAngle = angle + 40;
						} else {
							currentAngle = 220 - angle;
						}
						progressView.postInvalidate(200, 400, 700, 600);
						float percent = currentAngle / 260f;
						if (mProgressListener != null && percent != prevPercent) {
							mProgressListener.onDrag(percent);
							prevPercent = percent;
						}
					}
				}
			}

		};
		gestureDetector = new GestureDetector(context, simpleOnGestureListener);
	}

	private void initializeParams(int width, int height) {
		if (mWidth != width || mHeight != height) {
			mWidth = width;
			mHeight = height;

			int side = width < height ? width : height;

			centerX = mWidth / 2;
			centerY = mHeight * 0.4f;
			float outCircleRadius = side / 4;
			innerCircleRadius = outCircleRadius * innerRaidusScale;
			outCircleBorderWidth = side * outCircleBorderWidthScale;
			innerCircleBorderWidth = outCircleBorderWidth
					* innerCirlceBorderWidthScale;

			outRectF = new RectF(centerX - outCircleRadius, centerY
					- outCircleRadius, centerX + outCircleRadius, centerY
					+ outCircleRadius);
			innerRectF = new RectF(centerX - innerCircleRadius, centerY
					- innerCircleRadius, centerX + innerCircleRadius, centerY
					+ innerCircleRadius);

			float timeRectWidth = outCircleRadius * 2 * timeRectWidthScale;
			float timeRectHeight = timeRectWidth * timeRectHeightScale;
			float timeRectFL = (width - timeRectWidth) / 2;
			float timeRectFT = centerY + outCircleRadius * timeRectTopScale;
			float timeRectFR = timeRectFL + timeRectWidth;
			float timeRectFB = timeRectFT + timeRectHeight;
			timeRectF = new RectF(timeRectFL, timeRectFT, timeRectFR,
					timeRectFB);

			dragPointRadius = outCircleRadius * dragPointRadiusScale;

			timeTextSize = timeRectHeight * timeTextSizeScale;
			mPaint.setTextSize(timeTextSize);

			FontMetrics fm = mPaint.getFontMetrics();
			float fFontHeight = fm.descent - fm.ascent;

			timeTextCenterX = timeRectFL + timeRectWidth / 2;

			timeTextY = timeRectFT + (timeRectHeight + timeTextSize) / 2
					+ timeTextSize - fFontHeight + 2;

			playButtonRadius = outCircleRadius * playButtonRadiusScale;
			playButtonBorderWidth = outCircleBorderWidth
					* playButtonBorderWidthScale;

			dragSpace = outCircleRadius - innerCircleRadius;

			playPath.reset();
			float startX = centerX - playButtonRadius + playButtonRadius * 2
					* 0.38288f;
			playPath.moveTo(startX - 1.5f, centerY - playButtonRadius / 2f);
			playPath.lineTo(startX - 1.5f, centerY + playButtonRadius / 2f);
			playPath.lineTo(startX + 1.5f, centerY + playButtonRadius / 2f);
			playPath.lineTo(centerX + playButtonRadius - playButtonRadius * 2f
					* 0.27667f, centerY + 1.5f);
			playPath.lineTo(centerX + playButtonRadius - playButtonRadius * 2f
					* 0.27667f, centerY - 1.5f);
			playPath.lineTo(startX + 1.5f, centerY - playButtonRadius / 2f);
			playPath.close();

			pauseRectfLeft = new RectF(centerX - playButtonRadius / 3f, centerY
					- playButtonRadius / 2 + 3,
					centerX - playButtonRadius / 8f, centerY + playButtonRadius
							/ 2 - 3);
			pauseRectfRight = new RectF(centerX + playButtonRadius / 8f,
					centerY - playButtonRadius / 2 + 3, centerX
							+ playButtonRadius / 3f, centerY + playButtonRadius
							/ 2f - 3);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		initializeParams(getWidth(), getHeight());
		staticView.layout(0, 0, w, h);
		progressView.layout(0, 0, w, h);
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getMode() {
		return mode;
	}

	public boolean isStartProgress() {
		return isStartProgress;
	}

	public boolean isShowPlay() {
		return isShowPlay;
	}

	public void seekToTime(long time) {
		switch (mode) {
		case MODE_PROGRESS:
			if (time >= 0 && time <= progressTime) {
				currentAngle = 260f / progressTime * time;
			}
			break;
		case MODE_TIMER:
			if (time >= 0 && time <= timerTime) {
				currentAngle = 260f / timerTime * time;
			}
			break;
		}
		progressView.postInvalidate();
	}

	public void seekTo(double percent) {
		if (percent >= 0 && percent <= 1) {
			currentAngle = (float) (260f * percent);
		}
		progressView.postInvalidate();
	}

	public void setDragEnable(boolean isEnable) {
		this.isDragEnable = isEnable;
	}

	public void startTimer() {
		if (mode != MODE_TIMER) {
			stopProgress();
			mode = MODE_TIMER;
		}
		if (!isStartProgress && timerTime > 0) {
			isStartProgress = true;
			if (currentAngle == 260) {
				currentAngle = 0;
			}
			isPause = false;
			deltaAngle = 260f / timerTime;
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
			deltaAngle = 260f / timerTime;
		}
		if (!isStartProgress || isPause) {
			refreshProgressView();
		}
	}

	public void resetTimer() {
		if (mode == MODE_TIMER) {
			currentAngle = 0;
			deltaAngle = 260f / timerTime;
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

	public void setShowPlay(boolean showPlay) {
		isShowPlay = showPlay;
		staticView.postInvalidate();
	}

	public void startProgress() {
		if (mode != MODE_PROGRESS) {
			stopTimer();
			mode = MODE_PROGRESS;
		}
		if (!isStartProgress && progressTime > 0) {
			isStartProgress = true;
			if (currentAngle == 260) {
				currentAngle = 0;
			}
			isPause = false;
			deltaAngle = 260f / progressTime;
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
		timeTextX = timeTextCenterX - mPaint.measureText(showProgressTime) / 2;
		if (mode == MODE_PROGRESS) {
			deltaAngle = 260f / progressTime;
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
			deltaAngle = 260f / progressTime;
			refreshProgressView();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			isDrag = false;
			prevPercent = -1;
		}
		return true;
	}

	private void refreshProgressView() {
		refreshHandler.sendEmptyMessage(REFRESH_PROGRESSVIEW);
	}

	private void refreshProgressViewDelay(long delayMillis) {
		refreshHandler.sendEmptyMessageDelayed(REFRESH_PROGRESSVIEW,
				delayMillis);
	}

	private class StaticView extends View {

		public StaticView(Context context) {
			super(context);
		}

		@Override
		protected void onDraw(Canvas canvas) {

//			canvas.drawARGB(38, 255, 255, 255);

			mPaint.setColor(Color.WHITE);

			mPaint.setStrokeWidth(0);
			mPaint.setStyle(Paint.Style.FILL);

			if (isShowPlay) {
				canvas.drawPath(playPath, mPaint);
			} else {
				canvas.drawRoundRect(pauseRectfLeft, 0, 0, mPaint);
				canvas.drawRoundRect(pauseRectfRight, 0, 0, mPaint);
			}

			mPaint.setStyle(Paint.Style.STROKE);

			mPaint.setStrokeWidth(outCircleBorderWidth);
			canvas.drawArc(outRectF, 140, 260, false, mPaint);

			mPaint.setStrokeWidth(innerCircleBorderWidth);
			float round = (timeRectF.bottom - timeRectF.top) / 2;
			canvas.drawRoundRect(timeRectF, round, round, mPaint);

			canvas.drawCircle(centerX, centerY, playButtonRadius, mPaint);

			mPaint.setColor(innerCircleColor);
			canvas.drawArc(innerRectF, 140, 260, false, mPaint);
		}
	}

	public void setProgressListener(ProgressListener l) {
		this.mProgressListener = l;
	}

	public void setPlayButtonClickListener(PlayButtonClickListener l) {
		this.mButtonClickListener = l;
	}

	public interface ProgressListener {
		public void onDrag(float percent);

		public void onProgressEnd();
	}

	public interface PlayButtonClickListener {
		public void onPlay();

		public void onPause();
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
				if (currentAngle >= 260) {
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

	private class ProgressView extends View {

		public ProgressView(Context context) {
			super(context);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			mPaint.setColor(Color.WHITE);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(innerCircleBorderWidth);
			if (currentAngle > 260) {
				currentAngle = 260;
			} else if (currentAngle < 0) {
				currentAngle = 0;
			}
			canvas.drawArc(innerRectF, 140, currentAngle, false, mPaint);

			float sin = (float) Math.sin((currentAngle + 50) * angleToRadian);
			float cos = (float) Math.cos((currentAngle + 50) * angleToRadian);

			dragPointCenterX = centerX - innerCircleRadius * sin;
			dragPointCenterY = centerY + innerCircleRadius * cos;

			mPaint.setStrokeWidth(0);
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			canvas.drawCircle(dragPointCenterX, dragPointCenterY,
					dragPointRadius, mPaint);

			mPaint.setStrokeWidth(innerCircleBorderWidth - 2.5f);
			if (mode == MODE_TIMER) {
				currentTimer = (long) (timerTime - currentAngle / deltaAngle);
				int current = (int) (Math.ceil(currentTimer / 1000f));
				String show = current > 9 ? current + secondSign : "0"
						+ current + secondSign;
				canvas.drawText(show,
						timeTextCenterX - mPaint.measureText(show) / 2,
						timeTextY, mPaint);
			} else if (mode == MODE_PROGRESS) {
				if (timeTextX == 0) {
					timeTextX = timeTextCenterX
							- mPaint.measureText(showProgressTime) / 2;
				}
				// System.out.println( + ">>>>>"
				// + timeTextY);
				canvas.drawText(showProgressTime,
						(mWidth - mPaint.measureText(showProgressTime)) / 2,
						timeTextY, mPaint);
				// canvas.drawText(showProgressTime, timeTextX, timeTextY,
				// mPaint);
			}
			if (isStartProgress) {
				if (currentAngle < 260) {
					refreshProgressViewDelay(drawDelay);
				} else {
					isStartProgress = false;
					setShowPlay(true);
					currentAngle = 260;
					if (mProgressListener != null) {
						mProgressListener.onProgressEnd();
					}
				}
			}
		}
	}

	private static final class RefreshHandler extends Handler {

		ProgressView progressView;

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
