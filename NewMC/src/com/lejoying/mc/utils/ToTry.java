package com.lejoying.mc.utils;

import android.os.Handler;

import com.lejoying.mc.listener.ToTryListener;

public class ToTry {
	/**
	 * 在子线程中每delay毫秒执行一次，执行times次。结果由ToTryListener监听 如果在30秒内没有成功则会退出
	 * 
	 * @param delay
	 *            must greater than 0
	 * @param times
	 *            must greater than 0
	 * @param toTryListener
	 *            must unequal to null
	 */
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
}
