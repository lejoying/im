package com.lejoying.wxgs.activity.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.AbsListViewBaseActivity;
import com.lejoying.wxgs.activity.ChatActivity;
import com.lejoying.wxgs.activity.mode.fragment.SquareFragment;
import com.lejoying.wxgs.activity.utils.ExpressionUtil;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.entity.SquareMessage;
import com.lejoying.wxgs.app.data.entity.SquareMessage.Content;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileResult;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class SquareContentView extends HorizontalScrollView {

	MainApplication app = MainApplication.getMainApplication();
	static final int ITEM11 = 11;
	static final int ITEM12 = 12;
	static final int ITEM21 = 21;
	static final int ITEM22 = 22;
	byte aa[];
	int height;
	int unitSideLength, unitSideLength2;
	int userInfoWidth, userInfoHeight;
	int padding;
	float scalePadding = 0.034682f;
	float scaleUserInfoHeitht = 0.17f;

	int firstLayerTop, secondLayerTop, threadLayerTop;

	Context mContext;
	RelativeLayout mViewContainer;
	LayoutParams mViewContainerParmas;

	int itemBackgroundColor;

	List<Item> items;
	List<String> messages;
	Map<String, SquareMessage> SquareMessageMap;

	long currentTime;

	boolean isInIt;

	DisplayImageOptions options;

	public SquareContentView(Context context) {
		super(context);
		mContext = context;
		initialize();
	}

	public SquareContentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initialize();
	}

	private void initialize() {
		options = new DisplayImageOptions.Builder()
				// .showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		removeAllViews();
		mViewContainer = new RelativeLayout(mContext);
		mViewContainerParmas = new LayoutParams(0, LayoutParams.MATCH_PARENT);
		addView(mViewContainer, mViewContainerParmas);

		setOverScrollMode(OVER_SCROLL_NEVER);
		setHorizontalScrollBarEnabled(false);

		itemBackgroundColor = Color.argb(38, 255, 255, 255);

		currentTime = System.currentTimeMillis();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed) {
			if (!isInIt) {
				isInIt = true;
				height = b - t;
				padding = (int) (height * (scalePadding / (3 + 4 * scalePadding)));

				unitSideLength = (height - 4 * padding) / 3;
				unitSideLength2 = unitSideLength * 2 + padding;

				userInfoWidth = unitSideLength - 2 * padding;
				userInfoHeight = (int) (unitSideLength * scaleUserInfoHeitht);

				firstLayerTop = padding;
				secondLayerTop = unitSideLength + padding * 2;
				threadLayerTop = unitSideLength2 + padding * 2;
			}
			notifyItemPosition();
		}
		super.onLayout(changed, l, t, r, b);
	}

	public void setSquareMessageList(List<String> messages,
			Map<String, SquareMessage> SquareMessageMap) {
		if (messages == null || SquareMessageMap == null) {
			return;
		}
		this.messages = messages;
		this.SquareMessageMap = SquareMessageMap;
		generateItems(messages, SquareMessageMap);
		notifyItemPosition();
	}

	public void justSetSquareMessageList(List<String> messages,
			Map<String, SquareMessage> SquareMessageMap) {
		if (messages == null || SquareMessageMap == null) {
			return;
		}
		this.messages = messages;
		this.SquareMessageMap = SquareMessageMap;
	}

	public void notifyDataSetChanged() {
		if (messages == null || SquareMessageMap == null) {
			return;
		}
		if (items == null || items.size() == 0) {
			generateItems(messages, SquareMessageMap);
		} else {
			int itemsSize = items.size();
			int messagesSize = messages.size();
			// delete message generate all views
			if (messagesSize < itemsSize) {
				mViewContainer.removeAllViews();
				items.clear();
				itemsSize = 0;
			}
			Item itemBefore = null;
			// TODO int startSize = messagesSize > 20 ? messagesSize - 20 : 0;
			for (int i = 0; i < messagesSize; i++) {
				SquareMessage message = SquareMessageMap.get(messages.get(i));
				Item item = null;
				if (i < itemsSize) {
					item = items.get(i);
					if (!message.gmid.equals(item.message.gmid)) {
						item.removeFromParent();
						items.remove(item);
						item = generateItem(itemBefore, message);
						item.content.setTag(i);
						items.add(i, item);
					} else {
						item.content.notifyData();
					}
				} else {
					item = generateItem(itemBefore, message);
					item.content.setTag(items.size());
					items.add(item);
				}
				itemBefore = item;
			}
		}
		notifyItemPosition();
	}

	private void notifyItemPosition() {
		if (items == null || items.size() == 0) {
			return;
		}
		if (height == 0) {
			return;
		}
		Item itemBefore = null;
		// int startSize = items.size() > 20 ? items.size() - 20 : 0;
		for (int i = 0; i < items.size(); i++) {
			Item item = items.get(i);
			if (item.isMakeSurePosition) {
				itemBefore = item;
				continue;
			}
			item.isMakeSurePosition = true;
			int itemWidth = unitSideLength, itemHeight = unitSideLength, itemLeft = padding, itemTop = firstLayerTop;
			if (itemBefore == null) {
				switch (item.style) {
				case ITEM11:
					itemTop = threadLayerTop;
					break;
				case ITEM12:
					itemHeight = unitSideLength2;
					itemTop = secondLayerTop;
					break;
				case ITEM21:
					itemWidth = unitSideLength2;
					itemTop = threadLayerTop;
					break;
				case ITEM22:
					itemWidth = unitSideLength2;
					itemTop = secondLayerTop;
					break;
				}
			} else {
				switch (item.style) {
				case ITEM11:
					if (itemBefore.params.topMargin >= unitSideLength2) {
						itemTop = secondLayerTop;
					} else if (itemBefore.params.topMargin >= unitSideLength) {
						itemTop = firstLayerTop;
					} else {
						itemTop = threadLayerTop;
						setLeftOffset(unitSideLength + padding);
					}
					break;
				case ITEM12:
					itemHeight = unitSideLength2;
					if (itemBefore.params.topMargin >= unitSideLength2) {
						itemTop = firstLayerTop;
					} else {
						itemTop = secondLayerTop;
						setLeftOffset(unitSideLength + padding);
					}
					break;
				case ITEM21:
					itemWidth = unitSideLength2;
					if (itemBefore.params.topMargin >= unitSideLength2) {
						itemTop = secondLayerTop;
						setLeftOffset(unitSideLength + padding);
					} else if (itemBefore.params.topMargin >= unitSideLength) {
						itemTop = firstLayerTop;
						setLeftOffset(unitSideLength + padding);
					} else {
						itemTop = threadLayerTop;
						setLeftOffset(unitSideLength2 + padding);
					}
					break;
				case ITEM22:
					itemWidth = unitSideLength2;
					itemHeight = unitSideLength2;
					if (itemBefore.params.topMargin >= unitSideLength2) {
						itemTop = firstLayerTop;
						if (itemBefore.style != ITEM21) {
							setLeftOffset(unitSideLength + padding);
						}
					} else {
						itemTop = secondLayerTop;
						setLeftOffset(unitSideLength2 + padding);
					}
					break;
				}
			}

			item.addToContainer();
			item.setContentParams(itemWidth, itemHeight, itemLeft, itemTop);
			itemBefore = item;
		}
	}

	private void setLeftOffset(int offset) {
		int childCount = mViewContainer.getChildCount();
		if (childCount == 0) {
			return;
		}
		mViewContainer.layout(0, 0, mViewContainer.getWidth() + offset, height);
		int scrollX = getScrollX();
		if (scrollX != 0) {
			scrollTo(scrollX + offset, 0);
		}
		for (int i = childCount - 1; i > -1; i--) {
			View child = mViewContainer.getChildAt(i);
			Item item = items.get(((Integer) child.getTag()).intValue());
			item.setLeftMargin(item.getLeftMargin() + offset);
		}
	}

	private void generateItems(List<String> messages,
			Map<String, SquareMessage> map) {
		if (items == null) {
			items = new ArrayList<Item>();
		}
		mViewContainer.removeAllViews();
		items.clear();
		Item itemBefore = null;
		// TODO 2014.05.22 setting square images count to 20.
		// int messageSize = messages.size() > 20 ? messages.size() - 20 : 0;
		for (int i = 0; i < messages.size(); i++) {
			Item item = generateItem(itemBefore, map.get(messages.get(i)));
			itemBefore = item;
			item.content.setTag(items.size());
			items.add(item);
		}
	}

	private int makeSureItemStyle(Item itemBefore, SquareMessage message) {
		// message.contentType =
		// "text"||"image"||"voice"||"voiceandimage"||"textandimage"||"textandvoice"||"vit"
		int style = 0;
		if (message.contentType.equals("text")) {
			style = ITEM11;
		} else if (message.contentType.equals("image")) {
			style = ITEM12;
			if (itemBefore != null) {
				if (itemBefore.style == ITEM11) {
					style = ITEM21;
				} else if (itemBefore.style == ITEM12) {
					style = ITEM21;
				} else if (itemBefore.style == ITEM21
						&& itemBefore.params.topMargin == secondLayerTop) {
					style = ITEM21;
				}
			}
		} else if (message.contentType.equals("voice")) {
			style = ITEM11;
		} else if (message.contentType.equals("voiceandimage")) {
			style = ITEM12;
			if (itemBefore != null) {
				if (itemBefore.style == ITEM11) {
					style = ITEM21;
				} else if (itemBefore.style == ITEM12) {
					style = ITEM21;
				} else if (itemBefore.style == ITEM21
						&& itemBefore.params.topMargin == secondLayerTop) {
					style = ITEM21;
				}
			}
		} else if (message.contentType.equals("textandimage")) {
			style = ITEM12;
			if (itemBefore != null) {
				if (itemBefore.style == ITEM11) {
					style = ITEM21;
				} else if (itemBefore.style == ITEM12) {
					style = ITEM21;
				} else if (itemBefore.style == ITEM21
						&& itemBefore.params.topMargin == secondLayerTop) {
					style = ITEM21;
				}
			}
		} else if (message.contentType.equals("textandvoice")) {
			// style = ITEM11;
			style = ITEM12;
			if (itemBefore != null) {
				if (itemBefore.style == ITEM11) {
					style = ITEM21;
				} else if (itemBefore.style == ITEM12) {
					style = ITEM21;
				} else if (itemBefore.style == ITEM21
						&& itemBefore.params.topMargin == secondLayerTop) {
					style = ITEM21;
				}
			}
		} else if (message.contentType.equals("vit")) {
			style = ITEM22;
		}
		return style;
	}

	private Item generateItem(Item itemBefore, SquareMessage message) {
		int style = message.style != 0 ? message.style : makeSureItemStyle(
				itemBefore, message);
		Item item = new Item(style, message);
		return item;
	}

	public interface OnItemClickListener {
		public void onItemClick(SquareMessage message);
	}

	private OnItemClickListener onItemClickListener;

	public void setOnItemClickListener(OnItemClickListener l) {
		this.onItemClickListener = l;
	}

	public class Item {
		SquareMessage message;
		int style;
		RelativeLayout.LayoutParams params;
		ItemContent content;
		boolean isMakeSurePosition;

		public Item(int style, final SquareMessage message) {
			this.style = style;
			this.message = message;
			if (message.style != style) {
				// TODO modify message show style
				message.style = style;
			}
			content = new ItemContent(mContext, style, message);
			content.setBackgroundColor(itemBackgroundColor);
			params = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);

			content.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (onItemClickListener != null) {
						onItemClickListener.onItemClick(message);
					}
				}
			});
		}

		public void addToContainer() {
			if (content.getParent() == null) {
				mViewContainer.addView(content, params);
			}
		}

		public void removeFromParent() {
			ViewGroup vg = (ViewGroup) content.getParent();
			if (vg != null) {
				vg.removeView(content);
			}
		}

		public void setContentSize(int width, int height) {
			params.width = width;
			params.height = height;
			content.setLayoutParams(params);
		}

		public void setLeftMargin(int left) {
			params.leftMargin = left;
			content.setLayoutParams(params);
		}

		public int getLeftMargin() {
			return params.leftMargin;
		}

		public void setContentParams(int width, int height, int leftMargin,
				int topMargin) {
			params.width = width;
			params.height = height;
			params.topMargin = topMargin;
			params.leftMargin = leftMargin;
			params.rightMargin = -Integer.MAX_VALUE;
			params.bottomMargin = -Integer.MAX_VALUE;
			content.setLayoutParams(params);
		}
	}

	class ItemContent extends RelativeLayout {

		ImageView contentImage;
		ImageView contentVoice;
		TextPanel voiceLength;
		// TextPanel contentText;
		TextView contentText;
		ImageView headImage;
		TextPanel nickName;
		TextPanel time;

		int style;
		SquareMessage message;
		boolean isGenerated;

		public ItemContent(Context context, int style, SquareMessage message) {
			super(context);
			this.style = style;
			this.message = message;
			initialize(context);
		}

		private void initialize(Context context) {
			contentText = new TextView(context);
			headImage = new ImageView(context);
			nickName = new TextPanel(context);
			time = new TextPanel(context);

			contentText.setTextColor(Color.WHITE);
			nickName.setTextColor(Color.WHITE);
			nickName.singleLine(true);
			time.setTextColor(Color.WHITE);
			time.singleLine(true);

			this.addView(contentText);
			this.addView(headImage);
			this.addView(nickName);
			this.addView(time);

			switch (style) {
			case 22:
			case 12:
			case 21:
				contentImage = new ImageView(context);
				this.addView(contentImage);
				contentVoice = new ImageView(context);
				this.addView(contentVoice);
				voiceLength = new TextPanel(context);
				voiceLength.singleLine(true);
				voiceLength.setTextColor(Color.WHITE);
				this.addView(voiceLength);
				break;
			}
			if (message.contentType.equals("voice")) {
				contentImage = new ImageView(context);
				this.addView(contentImage);
				contentVoice = new ImageView(context);
				this.addView(contentVoice);
				voiceLength = new TextPanel(context);
				voiceLength.singleLine(true);
				voiceLength.setTextColor(Color.WHITE);
				this.addView(voiceLength);
			}
		}

		void notifyData() {
			if (contentImage != null) {
				// TODO set conver image to contentImage
				// contentImage.setImageBitmap(bm);
				String cover = "none";
				Content content = message.content;
				if (content.images.size() > 0) {
					cover = content.images.get(0);
				}
				if (!"none".equals(message.cover)) {
					cover = message.cover;
				}
				if (!cover.equals("none")) {
					final String fileName = cover;
					int width = 0;
					int height = 0;
					String _style = "";
					switch (style) {
					case ITEM12:
						if (!"".equals(message.content.text)) {
							width = unitSideLength;
							height = unitSideLength;
						} else {
							width = unitSideLength;
							height = ((unitSideLength * 2) / 10) * 9;
						}
						_style = "_12.";
						break;
					case ITEM21:
						if (!"".equals(message.content.text)) {
							width = unitSideLength;
							height = (int) (unitSideLength * 0.781499f);
						} else {
							width = unitSideLength2;
							height = (int) (unitSideLength * 0.781499f);
						}
						_style = "_21.";
						break;
					case ITEM22:
						width = unitSideLength;
						height = ((unitSideLength * 2) / 10) * 9;
						_style = "_22.";
						break;
					default:
						break;
					}
					final int width0 = width;
					final int height0 = height;
					AbsListViewBaseActivity.imageLoader.displayImage(
							API.DOMAIN_HANDLEIMAGE + "images/" + fileName + "@"
									+ width / 2 + "w_" + height / 2
									+ "h_1c_1e_100q", contentImage, options,
							new SimpleImageLoadingListener() {
								@Override
								public void onLoadingStarted(String imageUri,
										View view) {
								}

								@Override
								public void onLoadingFailed(String imageUri,
										View view, FailReason failReason) {
									try {
										Thread.sleep(3000);
										AbsListViewBaseActivity.imageLoader
												.displayImage(
														API.DOMAIN_HANDLEIMAGE
																+ "images/"
																+ fileName
																+ "@"
																+ width0
																/ 2
																+ "w_"
																+ height0
																/ 2
																+ "h_1c_1e_100q",
														contentImage, options);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}

								@Override
								public void onLoadingComplete(String imageUri,
										View view, Bitmap loadedImage) {
									// int height = (int)
									// (loadedImage.getHeight() * (width /
									// loadedImage
									// .getWidth()));
									// LinearLayout.LayoutParams params = new
									// LinearLayout.LayoutParams(
									// (int) width, height);
									// imageView.setLayoutParams(params);
								}
							});
					// app.fileHandler.getThumbnail(fileName, _style, width,
					// height, new FileResult() {
					//
					// @Override
					// public void onResult(String where, Bitmap bitmap) {
					// contentImage.setImageBitmap(bitmap);
					//
					// }
					// });
				}
				if (content.voices.size() > 0) {
					contentVoice.setImageBitmap(BitmapFactory.decodeResource(
							mContext.getResources(), R.drawable.square_voice));
				}
			}
			// TODO show face
			// contentText.setText(message.content.text);
			// TODO set head image to headImage
			// headImage.setImageBitmap(bm);
			app.fileHandler.getHeadImage(message.head, "男", new FileResult() {

				@Override
				public void onResult(String where, Bitmap bitmap) {
					Bitmap newBitmap = app.fileHandler.bitmaps
							.get(message.head);
					// Matrix matrix = new Matrix();
					// matrix.postScale(2, 2);
					// bitmap = Bitmap.createBitmap(bitmap, 0, 0,
					// bitmap.getWidth(), bitmap.getHeight(), matrix, true);
					headImage.setImageBitmap(newBitmap);
				}
			});
			String nickNames = message.nickName.length() > 6 ? message.nickName
					.substring(0, 6) + "..." : message.nickName;
			nickName.setText(nickNames);
			time.setText(convertTime(currentTime, message.time));
		}

		void saveThumbnailToLocal(final Bitmap bitmap, final String filename) {

			File thumbnailFile = new File(app.sdcardThumbnailFolder, filename);
			FileOutputStream fOut = null;
			try {
				thumbnailFile.createNewFile();
				fOut = new FileOutputStream(thumbnailFile);
				if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)) {
					fOut.flush();
					fOut.close();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		String convertTime(long currentTime, long targetTime) {
			long time = currentTime - targetTime;
			if (time > 0) {
				time /= 1000;
				if (time < 60) {
					return time + "秒前";
				}
				time /= 60;
				if (time < 60) {
					return time + "分钟前";
				}
				time /= 60;
				if (time < 24) {
					return time + "小时前";
				}
				time /= 24;
				if (time < 30) {
					return time + "天前";
				}
				time /= 30;
				if (time < 6) {
					return time + "个月前";
				} else if (time < 12) {
					return "半年前";
				}
				time /= 12;
				return time + "年前";
			} else if (time <= 0) {
				return "刚刚";
			}
			return "";
		}

		@Override
		protected void onLayout(boolean changed, int l, int t, int r, int b) {
			if (isGenerated) {
				return;
			}
			isGenerated = true;
			super.onLayout(changed, l, t, r, b);

			switch (style) {
			case ITEM11:
				setInfoPosition(0, 0);
				if (contentVoice != null) {
					// contentImage.setBackgroundColor(Color.GREEN);
					contentVoice.layout((int) (unitSideLength * 0.22265625f),
							(int) (unitSideLength * 0.10546875f),
							(int) (unitSideLength * 0.755859375f),
							(int) (unitSideLength * 0.70703125f));
				}
				break;
			case ITEM12:
				setInfoPosition(0, unitSideLength + padding);
				// contentImage.setBackgroundColor(Color.BLUE);// TODO delete
				if (!"".equals(message.content.text)) {
					contentImage.layout(0, 0, unitSideLength, unitSideLength);
				} else {
					contentImage.layout(0, 0, unitSideLength,
							((unitSideLength * 2) / 10) * 9);
				}
				contentVoice.layout((int) (unitSideLength * 0.232506f),
						(int) (unitSideLength * 0.2595935f),
						(int) (unitSideLength * 0.7494357f),
						(int) (unitSideLength * 0.77878f));
				break;
			case ITEM21:
				setInfoPosition(unitSideLength + padding, 0);
				// contentImage.setBackgroundColor(Color.RED);// TODO delete
				if (!"".equals(message.content.text)) {
					contentImage.layout(0, 0, unitSideLength, unitSideLength
							- unitSideLength / 4);
					contentVoice.layout((int) (unitSideLength * 0.22265625f),
							(int) (unitSideLength * 0.10546875f),
							(int) (unitSideLength * 0.755859375f),
							(int) (unitSideLength * 0.70703125f));
				} else {
					contentImage.layout(0, 0, unitSideLength2,
							(int) (unitSideLength * 0.781499f));
					contentVoice.layout((int) (unitSideLength2 * 0.3786794f),
							(int) (unitSideLength * 0.1802233f),
							(int) (unitSideLength2 * 0.641209f),
							(int) (unitSideLength * 0.7033493f));
				}
				break;
			case ITEM22:
				setInfoPosition(unitSideLength + padding, unitSideLength
						+ padding);
				contentImage.layout(0, 0, unitSideLength,
						((unitSideLength * 2) / 10) * 9);
				contentVoice.layout((int) (unitSideLength * 0.232506f),
						(int) (unitSideLength * 0.2595935f),
						(int) (unitSideLength * 0.7494357f),
						(int) (unitSideLength * 0.77878f));
				break;
			}
			notifyData();
		}

		private void setInfoPosition(int left, int top) {
			// contentText.setBackgroundColor(Color.YELLOW);
			if (style == ITEM22) {
				contentText.layout(left + padding, padding, left
						+ unitSideLength - padding, top + unitSideLength
						- userInfoHeight - 2 * padding);
			} else {
				contentText.layout(left + padding, top + padding, left
						+ unitSideLength - padding, top + unitSideLength
						- userInfoHeight - 2 * padding);
			}

			contentText.setText(ExpressionUtil.getExpressionString(mContext,
					message.content.text, ChatActivity.faceRegx,
					SquareFragment.expressionFaceMap));
			// float contentTextSize = contentText.getWidth() / 11;
			// contentText.setTextSize(contentTextSize);
			// contentText.setLineSpace(padding);

			headImage.layout(padding + 2, top + unitSideLength - userInfoHeight
					- padding - 2, userInfoHeight + padding / 2, top
					+ unitSideLength - padding);// right and + padding

			nickName.layout(
					userInfoHeight + 2 * padding,
					(int) (top + unitSideLength - userInfoHeight / 2 - padding - userInfoHeight / 4),
					unitSideLength - padding, top + unitSideLength - padding
							- userInfoHeight / 4);
			nickName.setTextSize(userInfoHeight / 2);

			float timeHeight = (float) (userInfoHeight / 2 * 0.8);
			float timeWidth = 4 * timeHeight + padding;
			time.layout(
					(int) (left + unitSideLength - padding - timeWidth),
					(int) (top + unitSideLength - timeHeight - padding - userInfoHeight / 4),
					(int) (left + unitSideLength - padding), top
							+ unitSideLength - padding - userInfoHeight / 4);
			time.setTextSize(timeHeight);
		}
	}
}

class TextPanel extends View {

	Paint mPaint;
	String drawText;
	float baseLineHeight;
	float lineSpace;
	boolean singleLine;

	public TextPanel(Context context) {
		super(context);
		mPaint = new Paint();
	}

	public void layout(float f, float g, float h, int b) {
		// TODO Auto-generated method stub

	}

	public void setTextColor(int color) {
		mPaint.setColor(color);
	}

	public void setText(String text) {
		drawText = text;
		postInvalidate();
	}

	public void setTextSize(float textSize) {
		mPaint.setTextSize(textSize);
		FontMetrics fm = mPaint.getFontMetrics();
		float fFontHeight = fm.descent - fm.ascent;
		baseLineHeight = textSize + textSize - fFontHeight;
		postInvalidate();
	}

	public void setLineSpace(float lineSpace) {
		this.lineSpace = lineSpace;
		postInvalidate();
	}

	public void singleLine(boolean singleLine) {
		this.singleLine = singleLine;
		postInvalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int width = canvas.getWidth();
		int height = canvas.getHeight();
		if (drawText != null) {
			float y = 0;
			y += baseLineHeight;
			if (singleLine) {
				String str = drawText;
				float textWidth = mPaint.measureText(str);
				if (textWidth > width) {
					str = drawText.substring(0, 1);
					int i = 1;
					while (mPaint.measureText(str + "...", 0,
							(str + "...").length()) < width) {
						str = drawText.substring(0, ++i);
					}
					str += "...";
				}
				canvas.drawText(str, 0, y, mPaint);
			} else {
				String[] strLines = autoSplit(drawText, mPaint, width);
				int i = 0;
				float nextY = 0;
				for (String str : strLines) {
					if (str == null) {
						continue;
					}
					if (i < strLines.length) {
						nextY = y + baseLineHeight + lineSpace;
					}
					if (nextY > height) {
						String strLast = str;
						float textWidth = mPaint.measureText(str + "...");
						// TODO can't show "..."
						if (textWidth > width) {
							strLast = str.substring(0, 1);
							int j = 0;
							while (mPaint.measureText(strLast + "...", 0,
									(strLast + "...").length()) < width) {
								strLast = drawText.substring(0, ++j);
							}
							strLast += "...";
						}
						canvas.drawText(strLast, 0, y, mPaint);
						break;
					} else {
						canvas.drawText(str, 0, y, mPaint);
					}
					y = nextY;
					i++;
				}
			}
		}
	}

	private String[] autoSplit(String content, Paint p, float width) {
		// TODO add '\n'
		int length = content.length();
		float textWidth = p.measureText(content);
		if (textWidth <= width) {
			return new String[] { content };
		}

		int start = 0, end = 1, i = 0;
		int lines = (int) Math.ceil(textWidth / width);
		lines += 1;
		String[] lineTexts = new String[lines];
		while (start < length) {
			if (p.measureText(content, start, end) > width) {
				end -= 1;
				lineTexts[i++] = content.substring(start, end);
				start = end;
			}
			if (end == length) {
				lineTexts[i] = (String) content.substring(start, end);
				break;
			}
			end += 1;
		}
		return lineTexts;
	}
}
