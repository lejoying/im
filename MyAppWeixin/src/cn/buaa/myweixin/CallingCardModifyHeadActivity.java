package cn.buaa.myweixin;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

public class CallingCardModifyHeadActivity extends Activity {

	public static final int TAKEPHOTO = 0x41;
	public static final int CHOICEFROMNATIVE = 0x42;

	private ImageView iv_head;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.callingcard_modify_head);
		iv_head = (ImageView) findViewById(R.id.imageView1);
		choiceWay();
	}

	public void choiceWay() {
		int way = getIntent().getExtras().getInt("way");
		if(way == TAKEPHOTO){
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, TAKEPHOTO);
		}
		if(way == CHOICEFROMNATIVE){
			Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, CHOICEFROMNATIVE);
		}
	}
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK){
			long time0 = new Date().getTime();
			if(requestCode == TAKEPHOTO){
				Bitmap bitmap = (Bitmap) data.getExtras().get("data");
				iv_head.setImageBitmap(bitmap);
			}		
			if(requestCode == CHOICEFROMNATIVE){
				iv_head.setImageURI(data.getData());
				long time1 = new Date().getTime();
				BitmapDrawable bd = (BitmapDrawable) iv_head.getDrawable();
				Bitmap bitmap = bd.getBitmap();
				long time2 = new Date().getTime();
				System.out.println(bitmap.getWidth()+":"+(time2-time0)+".."+(time2-time1)+".."+(time1-time0));
				long time3 = new Date().getTime();
				InputStream is = null;
				try {
					is = getContentResolver().openInputStream(data.getData());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Bitmap bitmap2 = BitmapFactory.decodeStream(is);
				iv_head.setImageBitmap(bitmap2);
				long time4 = new Date().getTime();
				System.out.println(bitmap2.getHeight()+".."+bitmap2.getWidth()+".."+(time4-time3));
			}
		}
		if(resultCode == RESULT_CANCELED){
			finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void top_back(View v) {
		finish();
	}

	public void save(View v) {
		Intent intent = new Intent(this, CallingCardModifyActivity.class);
		startActivity(intent);
	}

}
