package com.lejoying.mc.api;

import android.os.Bundle;

import com.lejoying.mc.listener.ResponseListener;

public interface CircleManager {
	public void modify(Bundle params,
			ResponseListener responseListener);
	public void delete(Bundle params,
			ResponseListener responseListener);
}
