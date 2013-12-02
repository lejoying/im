package com.lejoying.mc.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class InformReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("Ω” ’¡À" + intent.getIntExtra("remain", 0));
	}

}
