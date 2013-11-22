package com.lejoying.listener;

public interface ToTryListener {
	public void beforeDoing();

	public boolean isSuccess();

	public void successed(long time);

	public void failed();
}
