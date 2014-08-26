package com.open.lib;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Choreographer;

public class OpenLooper {

	public LegacyAndroidSpringLooper legacyAndroidSpringLooper = null;
	public ChoreographerAndroidSpringLooper choreographerAndroidSpringLooper = null;
	public LoopCallback loopCallback = null;

	public void createOpenLooper() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			choreographerAndroidSpringLooper = new ChoreographerAndroidSpringLooper();
		} else {
			legacyAndroidSpringLooper = new LegacyAndroidSpringLooper();
		}
	}

	public void start() {
		if (choreographerAndroidSpringLooper != null) {
			choreographerAndroidSpringLooper.start();
		} else if (legacyAndroidSpringLooper != null) {
			legacyAndroidSpringLooper.start();
		}
	}

	public void stop() {
		if (choreographerAndroidSpringLooper != null) {
			choreographerAndroidSpringLooper.stop();
		} else if (legacyAndroidSpringLooper != null) {
			legacyAndroidSpringLooper.stop();
		}
	}

	public class LoopCallback {

		public void loop(double ellapsedMillis) {

		}
	}

	public void loop(double ellapsedMillis) {
		if (this.loopCallback != null) {
			this.loopCallback.loop(ellapsedMillis);
		}
	}

	public class LegacyAndroidSpringLooper {

		public Handler mHandler;
		public Runnable mLooperRunnable;
		public boolean mStarted;
		public long mLastTime;

		public LegacyAndroidSpringLooper() {
			initialize(new Handler());
		}

		public void initialize(Handler handler) {
			mHandler = handler;
			mLooperRunnable = new Runnable() {
				@Override
				public void run() {
					if (!mStarted) {
						return;
					}
					long currentTime = SystemClock.uptimeMillis();
					loop(currentTime - mLastTime);
					mHandler.post(mLooperRunnable);
				}
			};
		}

		public void start() {
			if (mStarted) {
				return;
			}
			mStarted = true;
			mLastTime = SystemClock.uptimeMillis();
			mHandler.removeCallbacks(mLooperRunnable);
			mHandler.post(mLooperRunnable);
		}

		public void stop() {
			mStarted = false;
			mHandler.removeCallbacks(mLooperRunnable);
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public class ChoreographerAndroidSpringLooper {

		public Choreographer mChoreographer;
		public Choreographer.FrameCallback mFrameCallback;
		public boolean mStarted;
		public long mLastTime;

		public ChoreographerAndroidSpringLooper() {
			initialize(Choreographer.getInstance());
		}

		public void initialize(Choreographer choreographer) {
			mChoreographer = choreographer;
			mFrameCallback = new Choreographer.FrameCallback() {
				@Override
				public void doFrame(long frameTimeNanos) {
					if (!mStarted) {
						return;
					}
					long currentTime = SystemClock.uptimeMillis();
					loop(currentTime - mLastTime);
					mLastTime = currentTime;
					mChoreographer.postFrameCallback(mFrameCallback);
				}
			};
		}

		public void start() {
			if (mStarted) {
				return;
			}
			mStarted = true;
			mLastTime = SystemClock.uptimeMillis();
			mChoreographer.removeFrameCallback(mFrameCallback);
			mChoreographer.postFrameCallback(mFrameCallback);
		}

		public void stop() {
			mStarted = false;
			mChoreographer.removeFrameCallback(mFrameCallback);
		}
	}
}