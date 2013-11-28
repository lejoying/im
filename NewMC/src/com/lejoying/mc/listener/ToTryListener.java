package com.lejoying.mc.listener;

public interface ToTryListener {
	public void beforeDoing();

	public boolean isSuccess();

	public void successed(long time);

	public void failed();
}
