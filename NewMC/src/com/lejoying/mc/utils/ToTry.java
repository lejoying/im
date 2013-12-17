package com.lejoying.mc.utils;

import android.os.Handler;

public class ToTry {
	
	public static void tryDoing(final int delay, final int times,
			final ToTryListener toTryListener) {
		if (delay < 0 || times < 0 || toTryListener == null) {
			return;
		}
		final Handler handler = MCNetTools.handler;
		toTryListener.beforeDoing();
		final Thread thread = new Thread() {
			int tryCount = 0;
			boolean tryFlag = false;

			@Override
			public void run() {
				boolean success = false;
				while (!tryFlag) {
					tryCount++;
					try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (toTryListener.isSuccess()) {
						success = tryFlag = true;
						handler.post(new Runnable() {
							@Override
							public void run() {
								toTryListener.successed(delay * tryCount);
							}
						});

					}
					if (tryCount == times || tryCount * delay > 30000) {
						tryFlag = true;
					}
				}
				if (!success) {
					toTryListener.failed();
				}
			};
		};
		thread.start();
	}

	public interface ToTryListener {
		public void beforeDoing();

		public boolean isSuccess();

		public void successed(long time);

		public void failed();
	}
}
