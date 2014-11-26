package com.open.welinks.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.open.welinks.R;
import com.open.welinks.R.color;
import com.open.welinks.controller.DownloadFile;
import com.open.welinks.customListener.MyOnClickListener;
import com.open.welinks.model.Constant;
import com.open.welinks.model.Data;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.utils.BaseDataUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ChatFaceView extends FrameLayout {
	private Data data = Data.getInstance();
	private FileHandlers fileHandlers = FileHandlers.getInstance();

	private Context context;
	private FaceViewPager facePager;
	private PageControlView facePagerControl;
	private LinearLayout faceViewList;

	private String type = "default";
	private List<Integer> defaultEmojis;
	private List<View> pagerViews;
	private List<String> faceList;
	private List<String> defaultFaceNames;
	private List<Integer> eachPageCountList;

	private View currentView;
	private ImageView addFace;

	private OnPageChangeListener mOnPageChangeListener;
	private MyOnClickListener mOnClickListener;
	public OnFaceSeletedListener mOnFaceSeletedListener;

	public ChatFaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		onCreate();
	}

	private void onCreate() {
		LayoutInflater.from(context).inflate(R.layout.chat_face, this);
		facePager = (FaceViewPager) this.findViewById(R.id.facePager);
		facePagerControl = (PageControlView) this.findViewById(R.id.facePagerControl);
		faceViewList = (LinearLayout) this.findViewById(R.id.faceList);
		pagerViews = new ArrayList<View>();
		defaultEmojis = new ArrayList<Integer>(Arrays.asList(Constant.EMOJIS));
		defaultFaceNames = new ArrayList<String>(Arrays.asList(Constant.DEFAULT_FACE_NAMES));
		eachPageCountList = new ArrayList<Integer>();
		faceList = data.userInformation.currentUser.faceList;
		initListener();
		nodifyChatFace();
		facePager.setAdapter(new ChatFaceAdapter());
	}

	public void nodifyChatFace() {
		faceViewList.removeAllViews();
		pagerViews.clear();
		addSpaceView();
		fillFaces();
	}

	private void initListener() {
		mOnPageChangeListener = new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				int pageCount = 0, nowSeleted = 0, nowPageCount = 0, faceViewListItem = 0;
				for (int i = 0; i < eachPageCountList.size(); i++) {
					int eachPageCount = eachPageCountList.get(i);
					pageCount += eachPageCount;
					if (position + 1 <= pageCount && position + 1 > pageCount - eachPageCount) {
						nowSeleted = eachPageCount - (pageCount - position - 1);
						nowPageCount = eachPageCount;
						faceViewListItem = i;
						break;
					}
				}
				facePagerControl.setCount(nowPageCount, nowSeleted);
				if (currentView == null) {
					currentView = faceViewList.getChildAt((faceViewListItem + 1) * 2 - 1);
					currentView.setSelected(true);
				} else {
					if (!currentView.equals(faceViewList.getChildAt((faceViewListItem + 1) * 2 - 1))) {
						currentView.setSelected(false);
						currentView = faceViewList.getChildAt((faceViewListItem + 1) * 2 - 1);
						currentView.setSelected(true);
					}
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		};
		mOnClickListener = new MyOnClickListener() {

			@Override
			public void onClickEffective(View view) {
				if (view.equals(addFace)) {

				} else {
					int position = (Integer) view.getTag(), currentItem = 0;
					for (int i = 0; i < position - 1; i++) {
						currentItem += eachPageCountList.get(i);
					}
					facePager.setCurrentItem(currentItem);
				}
			}
		};
		facePager.setOnPageChangeListener(mOnPageChangeListener);
	}

	private void fillFaces() {
		int total, line, row, eachPageNum, pageTotal;
		fillDefaultFaces();
		if (faceList != null) {
			for (int i = 0; i < faceList.size(); i++) {
				String facesName = faceList.get(i);
				List<String> faceNames = new ArrayList<String>(Arrays.asList(Constant.FACE_NAMES_MAP.get(facesName)));
				if (faceNames != null) {
					total = faceNames.size();
					line = 2;
					row = 5;
					eachPageNum = (line * row);
					pageTotal = total % eachPageNum == 0 ? total / eachPageNum : total / eachPageNum + 1;
					for (int j = 0; j < pageTotal; j++) {
						ChatFaceGridView defaultGridOne = new ChatFaceGridView(this.context, this, facePager, "");
						defaultGridOne.setNumColumns(row);
						defaultGridOne.setAdapter(new ChatFaceGridItemAdapter(faceNames, "", j, eachPageNum));
						pagerViews.add(defaultGridOne);
					}
					eachPageCountList.add(pageTotal);
					addFaceView(faceNames.get(0));
				}
			}
		}
		addFace = new ImageView(context);
		addFace.setImageResource(R.drawable.chat_add_off);
		addFaceView(addFace);
	}

	private void fillDefaultFaces() {
		int total, line, row, eachPageNum, pageTotal;
		total = defaultEmojis.size();
		line = 3;
		row = 7;
		eachPageNum = (line * row) - 1;
		pageTotal = total / eachPageNum + 1;
		for (int j = 0; j < pageTotal; j++) {
			ChatFaceGridView defaultGridOne = new ChatFaceGridView(this.context, this, facePager, "default");
			defaultGridOne.setNumColumns(row);
			defaultGridOne.setAdapter(new ChatFaceGridItemAdapter(null, "default", j, eachPageNum));
			pagerViews.add(defaultGridOne);
		}
		facePagerControl.setCount(pageTotal, 1);
		eachPageCountList.add(pageTotal);
		addFaceView(defaultEmojis.get(0));

		String facesName = "tosiji";
		List<String> faceNames = new ArrayList<String>(Arrays.asList(Constant.FACE_NAMES_MAP.get(facesName)));
		total = faceNames.size();
		line = 2;
		row = 5;
		eachPageNum = (line * row);
		pageTotal = total % eachPageNum == 0 ? total / eachPageNum : total / eachPageNum + 1;
		for (int j = 0; j < pageTotal; j++) {
			ChatFaceGridView defaultGridOne = new ChatFaceGridView(this.context, this, facePager, "");
			defaultGridOne.setNumColumns(row);
			defaultGridOne.setAdapter(new ChatFaceGridItemAdapter(faceNames, "", j, eachPageNum));
			pagerViews.add(defaultGridOne);
		}
		eachPageCountList.add(pageTotal);
		addFaceView(faceNames.get(0));
	}

	public void addFaceView(int resource) {
		ImageView image = new ImageView(context);
		LayoutParams params = new LayoutParams((int) BaseDataUtils.dpToPx(50), (int) BaseDataUtils.dpToPx(50));
		image.setPadding((int) BaseDataUtils.dpToPx(5), (int) BaseDataUtils.dpToPx(5), (int) BaseDataUtils.dpToPx(5), (int) BaseDataUtils.dpToPx(5));
		image.setImageResource(resource);
		image.setLayoutParams(params);
		image.setBackgroundResource(R.drawable.selector_chat_face_item);
		faceViewList.addView(image);
		image.setOnClickListener(mOnClickListener);
		addSpaceView();
		image.setTag((faceViewList.getChildCount() - 1) / 2);
		currentView = image;
		currentView.setSelected(true);
	}

	private void addFaceView(String filePath) {
		ImageView image = new ImageView(context);
		LayoutParams params = new LayoutParams((int) BaseDataUtils.dpToPx(50), (int) BaseDataUtils.dpToPx(50));
		image.setPadding((int) BaseDataUtils.dpToPx(5), (int) BaseDataUtils.dpToPx(5), (int) BaseDataUtils.dpToPx(5), (int) BaseDataUtils.dpToPx(5));
		image.setLayoutParams(params);
		image.setBackgroundResource(R.drawable.selector_chat_face_item);
		image.setOnClickListener(mOnClickListener);
		fileHandlers.getImage(filePath, image, DownloadFile.TYPE_GIF_IMAGE, null);
		faceViewList.addView(image);
		addSpaceView();
		image.setTag((faceViewList.getChildCount() - 1) / 2);
	}

	private void addFaceView(View view) {
		LayoutParams params = new LayoutParams((int) BaseDataUtils.dpToPx(50), (int) BaseDataUtils.dpToPx(50));
		view.setPadding((int) BaseDataUtils.dpToPx(5), (int) BaseDataUtils.dpToPx(5), (int) BaseDataUtils.dpToPx(5), (int) BaseDataUtils.dpToPx(5));
		view.setLayoutParams(params);
		view.setBackgroundResource(R.drawable.selector_chat_face_item);
		faceViewList.addView(view);
		view.setOnClickListener(mOnClickListener);
		addSpaceView();
		view.setTag((faceViewList.getChildCount() - 1) / 2);
	}

	private void addSpaceView() {
		ImageView image = new ImageView(context);
		LayoutParams params = new LayoutParams((int) BaseDataUtils.dpToPx(0.5f), LayoutParams.MATCH_PARENT);
		image.setBackgroundColor(color.black70);
		image.setLayoutParams(params);
		faceViewList.addView(image);
	}

	private class ChatFaceAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return pagerViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(pagerViews.get(position));
			return pagerViews.get(position);
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
	}

	private class ChatFaceGridItemAdapter extends BaseAdapter {
		private int total, current;
		private String type;
		private List<String> faceNames;

		public ChatFaceGridItemAdapter(List<String> faceNames, String type, int current, int eachPageNum) {
			this.total = eachPageNum;
			this.current = current;
			this.faceNames = faceNames;
			this.type = type;
		}

		@Override
		public int getCount() {
			if (type.equals("default")) {
				return total + 1;
			} else {
				return total;
			}
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint({ "ViewHolder", "InflateParams" })
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (type.equals("default")) {
				convertView = new ImageView(context);
				android.widget.AbsListView.LayoutParams params = new android.widget.AbsListView.LayoutParams(LayoutParams.WRAP_CONTENT, (int) BaseDataUtils.dpToPx(60));
				convertView.setLayoutParams(params);
				convertView.setPadding((int) BaseDataUtils.dpToPx(5), (int) BaseDataUtils.dpToPx(5), (int) BaseDataUtils.dpToPx(5), (int) BaseDataUtils.dpToPx(5));
				if (position == total) {
					convertView.setBackgroundResource(R.drawable.selector_chat_face_item);
					((ImageView) convertView).setImageResource(R.drawable.emotion_del);
					convertView.setTag(R.id.tag_first, "delete");
				} else {
					int resource = total * current + position;
					if (resource < defaultEmojis.size()) {
						convertView.setBackgroundResource(R.drawable.selector_chat_face_item);
						((ImageView) convertView).setImageResource(defaultEmojis.get(resource));
						convertView.setTag(R.id.tag_first, defaultFaceNames.get(resource));
					}
				}
			} else {
				if (faceNames != null) {
					convertView = new ImageView(context);
					android.widget.AbsListView.LayoutParams params = new android.widget.AbsListView.LayoutParams(LayoutParams.WRAP_CONTENT, (int) BaseDataUtils.dpToPx(90));
					convertView.setLayoutParams(params);
					convertView.setPadding((int) BaseDataUtils.dpToPx(5), (int) BaseDataUtils.dpToPx(5), (int) BaseDataUtils.dpToPx(5), (int) BaseDataUtils.dpToPx(5));
					int resource = total * current + position;
					if (resource < faceNames.size()) {
						convertView.setBackgroundResource(R.drawable.selector_chat_face_item);
						fileHandlers.getImage(faceNames.get(resource), ((ImageView) convertView), DownloadFile.TYPE_GIF_IMAGE, null);
						convertView.setTag(R.id.tag_first, faceNames.get(resource));
					}
				}
			}
			return convertView;
		}

	}

	public void setOnFaceSeletedListener(OnFaceSeletedListener mListener) {
		mOnFaceSeletedListener = mListener;
	}

	public interface OnFaceSeletedListener {
		public void onFaceSeleted(String faceName);
	}
}
