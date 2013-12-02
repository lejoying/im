package com.lejoying.mc.api;

import android.os.Bundle;

import com.lejoying.mc.listener.ResponseListener;

public interface Session {
	public void eventweb(Bundle params,
			ResponseListener responseListener);
	public void event(Bundle params,
			ResponseListener responseListener);
	
}
