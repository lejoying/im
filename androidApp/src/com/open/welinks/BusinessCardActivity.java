package com.open.welinks;

import com.open.welinks.controller.BusinessCardController;
import com.open.welinks.view.BusinessCardView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class BusinessCardActivity extends Activity {

	public BusinessCardController thisController;
	public BusinessCardView thisView;
	public BusinessCardActivity thisActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		linkViewController();
	}

	public void linkViewController() {
		thisActivity = this;
		thisView = new BusinessCardView(this);
		thisController = new BusinessCardController(this);

		thisView.thisController = thisController;
		thisController.thisView = thisView;

		thisView.fillData();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		thisController.onActivityResult(requestCode, resultCode, data);
	}

}
