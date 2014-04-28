package com.lejoying.wxgs.activity.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.entity.SquareMessage;
import com.lejoying.wxgs.app.data.entity.SquareMessage.Content;
import com.lejoying.wxgs.app.handler.FileHandler.FileResult;

public class SquareContentView extends HorizontalScrollView {

	MainApplication app = MainApplication.getMainApplication();
	static final int ITEM11 = 11;
	static final int ITEM12 = 12;
	static final int ITEM21 = 21;

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
			Item itemBefore = null;
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
		for (String message : messages) {
			Item item = generateItem(itemBefore, map.get(message));
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
			style = ITEM11;
		} else if (message.contentType.equals("vit")) {
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
		}
		return style;
	}

	private Item generateItem(Item itemBefore, SquareMessage message) {
		int style = message.style != 0 ? message.style : makeSureItemStyle(
				itemBefore, message);
		Item item = new Item(style, message);
		return item;
	}

	public class Item {
		SquareMessage message;
		int style;
		RelativeLayout.LayoutParams params;
		ItemContent content;
		boolean isMakeSurePosition;

		public Item(int style, SquareMessage message) {
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
		TextPanel contentText;
		ImageView headImage;
		TextPanel nickName;
		TextPanel time;
		ImageView commentImage;
		TextPanel commentNum;
		ImageView praiseImage;
		TextPanel praiseNum;

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
			contentText = new TextPanel(context);
			headImage = new ImageView(context);
			nickName = new TextPanel(context);
			time = new TextPanel(context);
			commentImage = new ImageView(context);
			commentNum = new TextPanel(context);
			praiseImage = new ImageView(context);
			praiseNum = new TextPanel(context);

			contentText.setTextColor(Color.WHITE);
			nickName.setTextColor(Color.WHITE);
			nickName.singleLine(true);
			time.setTextColor(Color.WHITE);
			time.singleLine(true);
			commentNum.setTextColor(Color.WHITE);
			commentNum.singleLine(true);
			praiseNum.setTextColor(Color.WHITE);
			praiseNum.singleLine(true);

			this.addView(contentText);
			this.addView(headImage);
			this.addView(nickName);
			this.addView(time);
			this.addView(commentImage);
			this.addView(commentNum);
			this.addView(praiseImage);
			this.addView(praiseNum);

			switch (style) {
			case 12:
			case 21:
				contentImage = new ImageView(context);
				this.addView(contentImage);
				break;
			}

			commentImage.setImageResource(R.drawable.comment);
			praiseImage.setImageResource(R.drawable.praise);

			notifyData();
		}

		void notifyData() {

			if (contentImage != null) {
				// TODO set conver image to contentImage
				// contentImage.setImageBitmap(bm);
				if (!message.contentType.equals("text")) {
					String cover = "none";
					if (message.cover.equals("voice")) {
						cover = "voice.png";
					} else if (!message.cover.equals("none")) {
						cover = message.cover;
					} else {
						Content content = message.content;
						if (content.images.size() > 0) {
							cover = content.images.get(0);
						} else if (content.voices.size() > 0) {
							cover = content.voices.get(0);
						}
					}
					if (!cover.equals("none")) {
						final String fileName = cover;
						app.fileHandler.getImage(fileName, new FileResult() {

							@Override
							public void onResult(String where) {
								contentImage
										.setImageBitmap(app.fileHandler.bitmaps
												.get(fileName));

							}
						});
					}
				}
			}
			contentText.setText(message.content.text);

			// TODO set head image to headImage
			// headImage.setImageBitmap(bm);
			app.fileHandler.getHeadImage(message.head, new FileResult() {

				@Override
				public void onResult(String where) {
					headImage.setImageBitmap(app.fileHandler.bitmaps
							.get(message.head));
				}
			});
			nickName.setText(message.nickName);
			time.setText(convertTime(currentTime, message.time));

			commentNum.setText(String.valueOf(message.comments.size()));
			praiseNum.setText(String.valueOf(message.praiseusers.size()));
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
				setContentTextAndUserInfoPosition(0, 0);
				break;
			case ITEM12:
				setContentTextAndUserInfoPosition(0, unitSideLength + padding);
				contentImage.layout(0, 0, unitSideLength, unitSideLength);
				break;
			case ITEM21:
				setContentTextAndUserInfoPosition(unitSideLength + padding, 0);
				contentImage.layout(0, 0, unitSideLength, unitSideLength);
				break;
			}
		}

		private void setContentTextAndUserInfoPosition(int left, int top) {
			contentText.layout(left + padding, top + padding, left
					+ unitSideLength - padding, top + unitSideLength
					- userInfoHeight - 2 * padding);
			float contentTextSize = contentText.getWidth() / 11;
			contentText.setTextSize(contentTextSize);
			contentText.setLineSpace(padding);

			headImage.layout(left + padding - 2, top + unitSideLength
					- userInfoHeight - padding - 2, left + userInfoHeight
					+ padding, top + unitSideLength - padding);

			nickName.layout(left + userInfoHeight + 2 * padding, top
					+ unitSideLength - userInfoHeight - padding, left
					+ unitSideLength - padding, top + unitSideLength - padding
					- userInfoHeight / 2);
			nickName.setTextSize(userInfoHeight / 2);

			float timeHeight = (float) (userInfoHeight / 2 * 0.8);
			float timeWidth = 4 * timeHeight + padding;
			time.layout(left + userInfoHeight + 2 * padding, (int) (top
					+ unitSideLength - timeHeight - padding), left
					+ unitSideLength - padding, top + unitSideLength - padding);
			time.setTextSize(timeHeight);

			commentImage
					.layout((int) (left + userInfoHeight + 2 * padding + timeWidth),
							(int) (top + unitSideLength - timeHeight - padding),
							(int) (left + userInfoHeight + 2 * padding
									+ timeWidth + userInfoHeight / 2), top
									+ unitSideLength - padding);

			commentNum.layout((int) (left + userInfoHeight + 2 * padding
					+ padding / 3 + timeWidth + userInfoHeight / 2), (int) (top
					+ unitSideLength - timeHeight - padding), (int) (left
					+ userInfoHeight + 2 * padding + padding / 3 + timeWidth
					+ userInfoHeight / 2 + 2 * timeHeight), top
					+ unitSideLength - padding);
			commentNum.setTextSize(timeHeight);

			praiseImage.layout(
					(int) (left + userInfoHeight + 2 * padding + padding / 3
							+ timeWidth + userInfoHeight / 2 + 2 * timeHeight),
					(int) (top + unitSideLength - timeHeight - padding),
					(int) (left + userInfoHeight + 2 * padding + padding / 3
							+ timeWidth + userInfoHeight + 2 * timeHeight), top
							+ unitSideLength - padding);

			praiseNum.layout(
					(int) (left + userInfoHeight + 2 * padding + padding / 3
							* 2 + timeWidth + userInfoHeight + 2 * timeHeight),
					(int) (top + unitSideLength - timeHeight - padding), left
							+ unitSideLength, top + unitSideLength - padding);
			praiseNum.setTextSize(timeHeight);

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
