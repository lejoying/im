package com.lejoying.mc;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

import com.lejoying.mc.utils.MCImageTools;

public class ChatActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);

		Bitmap bm = MCImageTools.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.xiaohei), true, 2, Color.rgb(255,
				255, 255));

		ImageView iv1 = (ImageView) findViewById(R.id.iv_headleft);
		ImageView iv2 = (ImageView) findViewById(R.id.iv_headright);
		iv1.setImageBitmap(bm);
		iv2.setImageBitmap(bm);

		// CircleMenu circleMenu = new CircleMenu(this);
		// circleMenu.showMenu(CircleMenu.SHOW_TOP, null, true);
	}

}
