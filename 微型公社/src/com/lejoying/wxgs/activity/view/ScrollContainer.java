package com.lejoying.wxgs.activity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class ScrollContainer extends RelativeLayout {

	public static final int DIRECTION_HORIZONTAL = 0x5;
	public static final int DIRECTION_VERTICALITY = 0x13;

	public static final int SCROLL_SMOOTH = 0xb;
	public static final int SCROLL_PAGING = 0x6;

	public int mScrollStatus;
	public int mDirection;

	private GestureDetector mInterceptDetector;
	private GestureDetector mTouchDetector;

	private onInterceptTouchDownListener mInterceptTouchDownListener;

	private boolean isInterceptTouchEvent;
	private boolean isDecision;
	private boolean isCanScroll;
	private boolean isRequestDisallowIntercept;

	private VScroll mVScroll;
	private HScroll mHScroll;
	private int mHeight;
	private int mWidth;

	private int mPageCount;
	private int mCurrentPageIndex;

	private ViewContainer mViewContainer;

	private VelocityTracker mVelocityTracker;

	private OnTouchListener mOnScrollContainerTouchListener;
	private OnTouchListener mOnViewContainerTouchListener;
	private OnClickListener mOnScrollContainerClickListener;
	private OnLongClickListener mOnScrollContainerLongClickListener;
	private OnClickListener mOnViewContainerClickListener;
	private OnLongClickListener mOnViewContainerLongClickListener;
	private OnPageChangedListener mOnPageChangedListener;

	public ScrollContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initialize(context);
		initGestureDetector(context);
	}

	private void initialize(Context context) {
		mVScroll = new VScroll(context);
		mHScroll = new HScroll(context);
		mViewContainer = new ViewContainer(context);
		mViewContainer.setGravity(Gravity.LEFT | Gravity.TOP);
		isCanScroll = true;
		setScrollDirection(DIRECTION_VERTICALITY);
		setScrollStatus(SCROLL_SMOOTH);
		mViewContainer.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
	}

	private void initGestureDetector(Context context) {

		mInterceptDetector = new GestureDetector(context,
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onDown(MotionEvent e) {
						return true;
					}

					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {
						float absDistanceX = Math.abs(distanceX);
						float absDistanceY = Math.abs(distanceY);
						switch (mDirection) {
						case DIRECTION_HORIZONTAL:
							if (absDistanceX > absDistanceY) {
								isInterceptTouchEvent = true;
							}
							break;
						case DIRECTION_VERTICALITY:
							if (absDistanceY > absDistanceX) {
								isInterceptTouchEvent = true;
							}
							break;
						default:
							break;
						}
						isDecision = true;
						return true;
					}
				});

		mTouchDetector = new GestureDetector(context,
				new SimpleOnGestureListener() {

					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						if (mOnScrollContainerClickListener != null) {
							mOnScrollContainerClickListener
									.onClick(ScrollContainer.this);
						}
						if (mOnViewContainerClickListener != null) {
							mOnViewContainerClickListener
									.onClick(getViewContainer());
						}
						return true;
					}

					@Override
					public void onLongPress(MotionEvent e) {
						if (mOnScrollContainerLongClickListener != null) {
							mOnScrollContainerLongClickListener
									.onLongClick(ScrollContainer.this);
						}
						if (mOnViewContainerLongClickListener != null) {
							mOnViewContainerLongClickListener
									.onLongClick(getViewContainer());
						}
					}

					@Override
					public boolean onDown(MotionEvent e) {
						return true;
					}

				});
	}

	private void initOrResetVelocityTracker() {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		} else {
			mVelocityTracker.clear();
		}
	}

	private void recycleVelocityTracker() {
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		mHeight = b - t;
		mWidth = r - l;
		super.onLayout(changed, l, t, r, b);
	}

	
	public ViewContainer getViewContainer() {
		return mViewContainer;
	}

	public int getCurrentPage() {
		int currentPageIndex = -1;
		if (mScrollStatus == SCROLL_PAGING) {
			currentPageIndex = mCurrentPageIndex;
		}
		return currentPageIndex;
	}

	public int getPageCount() {
		int pageCount = -1;
		if (mScrollStatus == SCROLL_PAGING) {
			pageCount = mPageCount;
		}
		return pageCount;
	}

	public void showPageIndicator(boolean show) {

	}

	public void setAnticipatedHeight(int height) {
		if (mDirection == DIRECTION_VERTICALITY) {
			getViewContainer().layout(0, 0, mWidth, height);
		}
	}

	public void setAnticipatedWidth(int width) {
		if (mDirection == DIRECTION_HORIZONTAL) {
			getViewContainer().layout(0, 0, width, mHeight);
		}
	}

	@Override
	public void scrollTo(int x, int y) {
		switch (mDirection) {
		case DIRECTION_HORIZONTAL:
			mHScroll.scrollTo(x, y);
			break;
		case DIRECTION_VERTICALITY:
			mVScroll.scrollTo(x, y);
			break;
		default:
			break;
		}
	}

	@Override
	public void scrollBy(int dx, int dy) {
		switch (mDirection) {
		case DIRECTION_HORIZONTAL:
			mHScroll.scrollBy(dx, dy);
			break;
		case DIRECTION_VERTICALITY:
			mVScroll.scrollBy(dx, dy);
			break;
		default:
			break;
		}
	}

	public void smoothScrollTo(int x, int y) {
		switch (mDirection) {
		case DIRECTION_HORIZONTAL:
			mHScroll.smoothScrollTo(x, y);
			break;
		case DIRECTION_VERTICALITY:
			mVScroll.smoothScrollTo(x, y);
			break;
		default:
			break;
		}
	}

	public void smoothScrollBy(int dx, int dy) {
		switch (mDirection) {
		case DIRECTION_HORIZONTAL:
			mHScroll.smoothScrollBy(dx, dy);
			break;
		case DIRECTION_VERTICALITY:
			mVScroll.smoothScrollBy(dx, dy);
			break;
		}
	}

	public void setPage(int page) {
		if (mScrollStatus == SCROLL_PAGING) {
			if (page >= mPageCount) {
				mCurrentPageIndex = mPageCount - 1;
			} else {
				mCurrentPageIndex = page;
			}
			if (mDirection == DIRECTION_HORIZONTAL) {
				scrollTo(mWidth * mCurrentPageIndex, 0);
			} else if (mDirection == DIRECTION_VERTICALITY) {
				scrollTo(0, mHeight * mCurrentPageIndex);
			}
		}
	}

	public int getContainerScrollX() {
		int x = 0;
		switch (mDirection) {
		case DIRECTION_HORIZONTAL:
			x = mHScroll.getScrollX();
			break;
		case DIRECTION_VERTICALITY:
			x = mVScroll.getScrollX();
			break;
		}
		return x;
	}

	public int getContainerScrollY() {
		int y = 0;
		switch (mDirection) {
		case DIRECTION_HORIZONTAL:
			y = mHScroll.getScrollY();
			break;
		case DIRECTION_VERTICALITY:
			y = mVScroll.getScrollY();
			break;
		}
		return y;
	}

	public void setScrollEnable(boolean scrollEnable) {
		isCanScroll = scrollEnable;
	}

	@Override
	public void setOnTouchListener(OnTouchListener l) {
		mOnScrollContainerTouchListener = l;
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		mOnScrollContainerClickListener = l;
	}

	@Override
	public void setOnLongClickListener(OnLongClickListener l) {
		mOnScrollContainerLongClickListener = l;
	}

	public void setOnInterceptTouchDownListener(onInterceptTouchDownListener l) {
		this.mInterceptTouchDownListener = l;
	}

	public void setOnPageChangedListener(OnPageChangedListener l) {
		mOnPageChangedListener = l;
	}

	public void setScrollDirection(int direction) {
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		if (direction == DIRECTION_VERTICALITY
				&& this.mDirection != DIRECTION_VERTICALITY) {
			this.mDirection = DIRECTION_VERTICALITY;
			this.removeAllViews();
			this.addView(mVScroll, params);
			if (mViewContainer.getParent() != null) {
				((ViewGroup) mViewContainer.getParent())
						.removeView(mViewContainer);
			}
			LayoutParams containerParams = new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			mVScroll.addView(mViewContainer, containerParams);
		} else if (direction == DIRECTION_HORIZONTAL
				&& this.mDirection != DIRECTION_HORIZONTAL) {
			this.mDirection = DIRECTION_HORIZONTAL;
			this.removeAllViews();
			this.addView(mHScroll, params);
			if (mViewContainer.getParent() != null) {
				((ViewGroup) mViewContainer.getParent())
						.removeView(mViewContainer);
			}
			LayoutParams containerParams = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
			mHScroll.addView(mViewContainer, containerParams);
		}
	}

	public void setScrollStatus(int scrollStatus) {
		if (scrollStatus == SCROLL_PAGING
				&& this.mScrollStatus != SCROLL_PAGING) {
			this.mScrollStatus = SCROLL_PAGING;
		} else if (scrollStatus == SCROLL_SMOOTH
				&& this.mScrollStatus != SCROLL_SMOOTH) {
			this.mScrollStatus = SCROLL_SMOOTH;
		}
	}

	public void requestDisallowInterceptToScroll(boolean disallowIntercept) {
		this.isRequestDisallowIntercept = disallowIntercept;
	}

	public interface OnPageChangedListener {
		public void pageChanged(int currentPage);
	}

	public interface onInterceptTouchDownListener {
		public void onInterceptTouchDown(MotionEvent ev);
	}

	class VScroll extends ScrollView {

		int velocityY;

		public VScroll(Context context) {
			super(context);
			this.setOverScrollMode(OVER_SCROLL_NEVER);
			this.setVerticalScrollBarEnabled(false);
		}

		@Override
		public boolean onTouchEvent(MotionEvent ev) {
			if (!isCanScroll) {
				return false;
			}
			int action = ev.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				initOrResetVelocityTracker();
				mVelocityTracker.addMovement(ev);
				break;
			case MotionEvent.ACTION_MOVE:
				mVelocityTracker.addMovement(ev);
				break;
			case MotionEvent.ACTION_UP:
				if (mScrollStatus == SCROLL_PAGING) {
					mVelocityTracker.computeCurrentVelocity(1000);
					velocityY = (int) mVelocityTracker.getYVelocity();
					recycleVelocityTracker();
					int scrollY = this.getScrollY();
					int scrollRemainder = scrollY % mHeight;
					int beforePageIndex = mCurrentPageIndex;
					int deltaY = 0;
					if ((scrollRemainder >= mHeight / 2 && velocityY <= 1000)
							|| velocityY < -1000) {
						deltaY = mHeight - scrollRemainder;
					} else {
						deltaY = -scrollRemainder;
					}
					smoothScrollBy(0, deltaY);
					mCurrentPageIndex = Math
							.round((scrollY + deltaY) / mHeight);
					mCurrentPageIndex = mCurrentPageIndex >= mPageCount ? mCurrentPageIndex - 1
							: mCurrentPageIndex;
					if (mCurrentPageIndex != beforePageIndex
							&& mOnPageChangedListener != null) {
						mOnPageChangedListener.pageChanged(mCurrentPageIndex);
					}
					return true;
				}
				break;
			}

			mTouchDetector.onTouchEvent(ev);

			if (mOnScrollContainerTouchListener != null) {
				mOnScrollContainerTouchListener.onTouch(this, ev);
			}
			if (mOnViewContainerTouchListener != null) {
				mOnViewContainerTouchListener.onTouch(getViewContainer(), ev);
			}
			return super.onTouchEvent(ev);
		}

		@Override
		public boolean onInterceptTouchEvent(MotionEvent ev) {
			int action = ev.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				isInterceptTouchEvent = false;
				isDecision = false;
				isRequestDisallowIntercept = false;
				initOrResetVelocityTracker();
				mVelocityTracker.addMovement(ev);
				super.onTouchEvent(ev);
				if (mInterceptTouchDownListener != null) {
					mInterceptTouchDownListener.onInterceptTouchDown(ev);
				}
				break;
			case MotionEvent.ACTION_UP:
				break;
			}
			if (!isDecision) {
				mInterceptDetector.onTouchEvent(ev);
				super.onInterceptTouchEvent(ev);
			}
			return isCanScroll && !isRequestDisallowIntercept
					&& isInterceptTouchEvent;
		}

	}

	class HScroll extends HorizontalScrollView {

		int velocityX;

		public HScroll(Context context) {
			super(context);
			this.setOverScrollMode(OVER_SCROLL_NEVER);
			this.setHorizontalScrollBarEnabled(false);
		}

		@Override
		public boolean onTouchEvent(MotionEvent ev) {
			if (!isCanScroll) {
				return false;
			}
			int action = ev.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				initOrResetVelocityTracker();
				mVelocityTracker.addMovement(ev);
				break;
			case MotionEvent.ACTION_MOVE:
				mVelocityTracker.addMovement(ev);
				break;
			case MotionEvent.ACTION_UP:
				if (mScrollStatus == SCROLL_PAGING) {
					mVelocityTracker.computeCurrentVelocity(1000);
					velocityX = (int) mVelocityTracker.getXVelocity();
					recycleVelocityTracker();
					int scrollX = this.getScrollX();
					int scrollRemainder = scrollX % mWidth;
					int beforePageIndex = mCurrentPageIndex;
					int deltaX = 0;
					if ((scrollRemainder >= mWidth / 2 && velocityX < 1000)
							|| velocityX < -1000) {
						deltaX = mWidth - scrollRemainder;
					} else {
						deltaX = -scrollRemainder;
					}
					smoothScrollBy(deltaX, 0);
					mCurrentPageIndex = Math.round((scrollX + deltaX) / mWidth);
					mCurrentPageIndex = mCurrentPageIndex >= mPageCount ? mCurrentPageIndex - 1
							: mCurrentPageIndex;
					if (mCurrentPageIndex != beforePageIndex
							&& mOnPageChangedListener != null) {
						mOnPageChangedListener.pageChanged(mCurrentPageIndex);
					}
					return true;
				}
				break;
			}

			mTouchDetector.onTouchEvent(ev);

			if (mOnScrollContainerTouchListener != null) {
				mOnScrollContainerTouchListener.onTouch(this, ev);
			}
			if (mOnViewContainerTouchListener != null) {
				mOnViewContainerTouchListener.onTouch(getViewContainer(), ev);
			}
			return super.onTouchEvent(ev);
		}

		@Override
		public boolean onInterceptTouchEvent(MotionEvent ev) {
			int action = ev.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				isInterceptTouchEvent = false;
				isDecision = false;
				isRequestDisallowIntercept = false;
				initOrResetVelocityTracker();
				mVelocityTracker.addMovement(ev);
				super.onTouchEvent(ev);
				if (mInterceptTouchDownListener != null) {
					mInterceptTouchDownListener.onInterceptTouchDown(ev);
				}
				break;
			case MotionEvent.ACTION_UP:
				break;
			}
			if (!isDecision) {
				mInterceptDetector.onTouchEvent(ev);
				super.onInterceptTouchEvent(ev);
			}
			return isCanScroll && !isRequestDisallowIntercept
					&& isInterceptTouchEvent;
		}

	}

	public class ViewContainer extends RelativeLayout {
		int width;
		int height;

		private ViewContainer(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onLayout(boolean changed, int l, int t, int r, int b) {
			// TODO Auto-generated method stub
			if (changed) {
				width = r - l;
				height = b - t;
				switch (mDirection & mScrollStatus) {
				case DIRECTION_HORIZONTAL & SCROLL_PAGING:
					if (width != 0 && mWidth != 0) {
						int remainder = width % mWidth;
						boolean isFit = remainder == 0;
						mPageCount = isFit ? width / mWidth : width / mWidth
								+ 1;
						if (!isFit) {
							this.layout(l, t, r + mWidth - remainder, b);
							return;
						}
					}
					break;
				case DIRECTION_VERTICALITY & SCROLL_PAGING:
					if (height != 0 && mHeight != 0) {
						int remainder = height % mHeight;
						boolean isFit = remainder == 0;
						mPageCount = isFit ? height / mHeight : height
								/ mHeight + 1;
						if (!isFit) {
							this.layout(l, t, r, b + mHeight - remainder);
							return;
						}
					}
					break;
				case DIRECTION_VERTICALITY & SCROLL_SMOOTH:
					break;
				case DIRECTION_HORIZONTAL & SCROLL_SMOOTH:
					break;
				}
			}
			super.onLayout(changed, l, t, r, b);
		}

		@Override
		public void scrollTo(int x, int y) {
			switch (mDirection) {
			case DIRECTION_HORIZONTAL:
				mHScroll.scrollTo(x, y);
				break;
			case DIRECTION_VERTICALITY:
				mVScroll.scrollTo(x, y);
				break;
			default:
				break;
			}
		}

		@Override
		public void scrollBy(int dx, int dy) {
			switch (mDirection) {
			case DIRECTION_HORIZONTAL:
				mHScroll.scrollBy(dx, dy);
				break;
			case DIRECTION_VERTICALITY:
				mVScroll.scrollBy(dx, dy);
				break;
			default:
				break;
			}
		}

		public void smoothScrollTo(int x, int y) {
			switch (mDirection) {
			case DIRECTION_HORIZONTAL:
				mHScroll.smoothScrollTo(x, y);
				break;
			case DIRECTION_VERTICALITY:
				mVScroll.smoothScrollTo(x, y);
				break;
			default:
				break;
			}
		}

		public void smoothScrollBy(int dx, int dy) {
			switch (mDirection) {
			case DIRECTION_HORIZONTAL:
				mHScroll.smoothScrollBy(dx, dy);
				break;
			case DIRECTION_VERTICALITY:
				mVScroll.smoothScrollBy(dx, dy);
				break;
			}
		}

		public void setScrollEnable(boolean scrollEnable) {
			isCanScroll = scrollEnable;
		}

		@Override
		public void setOnTouchListener(OnTouchListener l) {
			mOnViewContainerTouchListener = l;
		}

		@Override
		public void setOnClickListener(OnClickListener l) {
			mOnViewContainerClickListener = l;
		}

		@Override
		public void setOnLongClickListener(OnLongClickListener l) {
			mOnViewContainerLongClickListener = l;
		}

		public void setOnPageChangedListener(OnPageChangedListener l) {
			mOnPageChangedListener = l;
		}

		public void setAnticipatedHeight(int height) {
			if (mDirection == DIRECTION_VERTICALITY) {
				getViewContainer().layout(0, 0, mWidth, height);
			}
		}

		public void setAnticipatedWidth(int width) {
			if (mDirection == DIRECTION_HORIZONTAL) {
				getViewContainer().layout(0, 0, width, mHeight);
			}
		}

	}
}
