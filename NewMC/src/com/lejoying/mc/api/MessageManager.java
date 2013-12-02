package com.lejoying.mc.api;

import android.os.Bundle;

import com.lejoying.mc.listener.ResponseListener;

public interface MessageManager {
	public void send(Bundle params,
			ResponseListener responseListener);

	public void get(Bundle params,
			ResponseListener responseListener);
}
