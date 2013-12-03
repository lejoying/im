package com.lejoying.mc.utils;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;

import com.lejoying.mc.entity.Account;

public class MCStaticData {
	static Account nowAccount;
	static final String DOMAIN = "http://115.28.51.197:8071";
	public static Bundle registerBundle;
	public static List<View> messages = new ArrayList<View>();
}
