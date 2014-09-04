package com.open.lib.viewbody;

import java.util.List;

public class BodyCallback {

	public void onStart(String bodyTag, float variables) {

	}

	public void onFlipping(String bodyTag, float variable) {

	}
	
	public void onFixed(String bodyTag, float variable) {

	}

	public boolean onOverRange(String bodyTag, float variable) {
		return false;
	}
	

	public void onStopOrdering(List<String> listItemsSequence) {
		
	}
}
