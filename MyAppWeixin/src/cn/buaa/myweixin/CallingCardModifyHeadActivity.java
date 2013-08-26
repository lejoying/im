package cn.buaa.myweixin;

import java.io.FileNotFoundException;
import java.io.InputStream;
import cn.buaa.myweixin.utils.ImageTools;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class CallingCardModifyHeadActivity extends Activity {

	public static final int TAKEPHOTO = 0x41;
	public static final int CHOICEFROMNATIVE = 0x42;

	private ImageView iv_head;

	private RelativeLayout rl_cut;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.callingcard_modify_head);
		choiceWay();
		initView();
	}

	public void initView() {
		iv_head = (ImageView) findViewById(R.id.iv_cut);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		rl_cut = (RelativeLayout) findViewById(R.id.rl_cut);
		LayoutParams lp = rl_cut.getLayoutParams();
		lp.width = screenW - 10;
		lp.height = screenW - 10;
		rl_cut.setLayoutParams(lp);

	}

	public void choiceWay() {
		int way = getIntent().getExtras().getInt("way");
		if (way == TAKEPHOTO) {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, TAKEPHOTO);
		}
		if (way == CHOICEFROMNATIVE) {
			Intent intent = new Intent(Intent.ACTION_PICK,
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, CHOICEFROMNATIVE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == TAKEPHOTO) {
				Bitmap bitmap = (Bitmap) data.getExtras().get("data");
				iv_head.setImageBitmap(bitmap);
			}
			if (requestCode == CHOICEFROMNATIVE) {
				InputStream is = null;
				try {
					is = getContentResolver().openInputStream(data.getData());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (is == null) {
					finish();
				}
				Bitmap bitmap = ImageTools
						.getZoomBitmapFromStream(is, 720, 800);
				iv_head.setImageBitmap(bitmap);
			}
		}
		if (resultCode == RESULT_CANCELED) {
			finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void back(View v) {
		finish();
	}

	public void save(View v) {
		Intent intent = new Intent(this, CallingCardModifyActivity.class);
		startActivity(intent);
	}

}
