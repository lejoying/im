package com.lejoying.wxgs.app.handler;

import java.util.LinkedList;
import java.util.Queue;

import android.os.Handler;

public class AsyncHandler {
	private int mThreadCount;
	private boolean isInitialized = false;
	private WorkThread[] mWorkTHreads;
	private Queue<Execution<?>> mQueue;
	private Handler mHandler;

	public synchronized void initialized(int workThreadCount, Handler handler) {
		if (!isInitialized) {
			mHandler = handler;
			mThreadCount = workThreadCount;
			if (mThreadCount < 0) {
				mThreadCount = 1;
			}
			mWorkTHreads = new WorkThread[workThreadCount];
			for (int i = 0; i < mThreadCount; i++) {
				mWorkTHreads[i] = new WorkThread();
				mWorkTHreads[i].start();
			}
			isInitialized = true;
			mQueue = new LinkedList<Execution<?>>();
		}
	}

	public synchronized void release() {
		if (isInitialized) {
			isInitialized = false;
			for (WorkThread thread : mWorkTHreads) {
				thread.interrupt();
			}
			mWorkTHreads = null;
			mQueue.clear();
		}
	}

	public boolean isInitialized() {
		return isInitialized;
	}

	public static abstract class Execution<T> {
		Handler mHandler;

		private Runnable result(final T t) {
			return new Runnable() {

				@Override
				public void run() {
					onResult(t);
				}
			};
		}

		private void setHandler(Handler handler) {
			mHandler = handler;
		}

		public final void postUpdate(final float result) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					onPostUpdate(result);
				}
			});
		}

		private void execute() {
			mHandler.post(result(asyncExecute()));
		}

		protected abstract T asyncExecute();

		protected void onPostUpdate(float result) {
		}

		protected abstract void onResult(T t);
	}

	public synchronized <T> void execute(Execution<T> execution) {
		mQueue.offer(execution);
		notify();
	}

	private synchronized Execution<?> getExecution()
			throws InterruptedException {
		if (mQueue.size() == 0) {
			wait();
		}
		return mQueue.poll();
	}

	private class WorkThread extends Thread {
		private boolean interrupt = false;

		@Override
		public void interrupt() {
			// TODO Auto-generated method stub
			this.interrupt = true;
			super.interrupt();
		}

		@Override
		public void run() {
			while (!interrupt) {
				Execution<?> execution = null;
				try {
					while ((execution = getExecution()) == null)
						;
				} catch (InterruptedException e) {
					return;
				}
				execution.setHandler(mHandler);
				execution.execute();
			}
		}
	}
}
