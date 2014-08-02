package com.open.test1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.nostra13.example.universalimageloader.AbsListViewBaseActivity;
import com.nostra13.example.universalimageloader.Constants.Extra;
import com.nostra13.example.universalimageloader.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TestActivity2 extends AbsListViewBaseActivity {

	DisplayImageOptions options;

	private List<String> list;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_image_list);

		Bundle bundle = getIntent().getExtras();

		list = getIntent().getStringArrayListExtra("data");

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.displayer(new RoundedBitmapDisplayer(20)).build();

		listView = (ListView) findViewById(android.R.id.list);
		((ListView) listView).setAdapter(new ItemAdapter());
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				startImagePagerActivity(position);
			}
		});
	}

	@Override
	public void onBackPressed() {
		AnimateFirstDisplayListener.displayedImages.clear();
		super.onBackPressed();
	}

	private void startImagePagerActivity(int position) {
		// Intent intent = new Intent(this, ImagePagerActivity.class);
		// intent.putExtra("imageUrl", imageUrl);
		// startActivity(intent);
	}

	private static class ViewHolder {
		TextView text;
		ImageView image;
	}

	class ItemAdapter extends BaseAdapter {

		private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view = convertView;
			final ViewHolder holder;
			if (convertView == null) {
				view = getLayoutInflater().inflate(R.layout.item_list_image,
						parent, false);
				holder = new ViewHolder();
				holder.text = (TextView) view.findViewById(R.id.text);
				holder.image = (ImageView) view.findViewById(R.id.image);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			holder.text.setText("Item " + (position + 1));
			String path = list.get(position);

			imageLoader.displayImage("file://" + path, holder.image, options,
					animateFirstListener);

			return view;
		}
	}

	static int indexBitmap = 0;

	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}

				// saveMyBitmap("pic" + indexBitmap, loadedImage);
				// indexBitmap++;
			}
		}

		public void saveMyBitmap(String bitName, Bitmap mBitmap) {
			File f = new File("/storage/sdcard0/test1/" + bitName + ".png");
			try {
				f.createNewFile();
			} catch (IOException e) {
				indexBitmap = indexBitmap + 1 - 1;
			}
			FileOutputStream fOut = null;
			try {
				fOut = new FileOutputStream(f);
			} catch (FileNotFoundException e) {
				indexBitmap = indexBitmap + 1 - 1;
				e.printStackTrace();
			}
			mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			try {
				fOut.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public long eventCount = 0;

	public int preTouchTimes = 5;
	public float pre_x = 0;
	public float pre_y = 0;
	long lastMillis = 0;

	public float pre_pre_x = 0;
	public float pre_pre_y = 0;
	long pre_lastMillis = 0;

	public float progress_test_x = 0;
	public float progress_test_y = 0;

	public float progress_line1_x = 0;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		eventCount++;
		float x = event.getX();
		float y = event.getY();
		super.onTouchEvent(event);
		long currentMillis = System.currentTimeMillis();

		RelativeLayout control_progress2 = (RelativeLayout) findViewById(R.id.control_progress2);
		ImageView progress_line1 = (ImageView) findViewById(
				R.id.control_progress2).findViewById(R.id.progress_line1);
		ImageView progress_test = (ImageView) findViewById(R.id.progress_test);

		// control_progress2.setX(x);

		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			pre_x = x;
			pre_y = y;

			progress_test_x = progress_test.getX();
			progress_test_y = progress_test.getY();

			progress_line1_x = progress_line1.getX();
			float width = progress_line1.getWidth();
			if (y > 520) {

			} else {

			}

		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			if (lastMillis == 0) {
				lastMillis = currentMillis;
				return true;
			}

			progress_test.setX(progress_test_x + x - pre_x);
			progress_test.setY(progress_test_y + y - pre_y);

			progress_line1.setX(progress_line1_x + x - pre_x);

		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			long delta = currentMillis - lastMillis;

			if (delta == 0 || x == pre_x || y == pre_y) {
				delta = currentMillis - pre_lastMillis;
				pre_x = pre_pre_x;
				pre_y = pre_pre_y;
			}

		}
		return true;
	}
}