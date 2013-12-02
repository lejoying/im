package com.lejoying.mc.adapter;

import com.lejoying.mc.utils.ToTry.ToTryListener;

public abstract class ToTryAdapter implements ToTryListener {

	@Override
	public void beforeDoing() {
		// TODO Auto-generated method stub

	}

	@Override
	public abstract boolean isSuccess();

	@Override
	public abstract void successed(long time);

	@Override
	public void failed() {
		// TODO Auto-generated method stub

	}

}
