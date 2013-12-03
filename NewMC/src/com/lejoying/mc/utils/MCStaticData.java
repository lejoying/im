package com.lejoying.mc.utils;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;

import com.lejoying.mc.entity.User;

public class MCStaticData {
	static User mUser;
	static final String DOMAIN = "http://115.28.51.197:8071/api2/";
	public static Bundle registerBundle;
	public static Bundle loginCodeBundle = new Bundle();
	public static List<View> messages = new ArrayList<View>();
}
