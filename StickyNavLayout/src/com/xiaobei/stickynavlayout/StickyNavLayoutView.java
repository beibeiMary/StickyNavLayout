package com.xiaobei.stickynavlayout;



import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.OverScroller;
import android.widget.ScrollView;

public class StickyNavLayoutView extends LinearLayout{
	private View mTop;
	private View mNav;
	private ViewPager mViewPager;

	private int mTopViewHeight;
	private ViewGroup mInnerScrollView;
	private boolean isTopHidden = false;

	private OverScroller mScroller;
	private VelocityTracker mVelocityTracker;
	private int mTouchSlop;
	private int mMaximumVelocity, mMinimumVelocity;

	private float mLastY;
	private boolean mDragging;

	private boolean isInControl = false;
	private float downY;
	private float moveY;
	private float allY;
	private Context mContext;
	private boolean isDownShow;
	private float dy;
	private boolean isFling = false;
	private ListView mListView;
	public StickyNavLayoutView(Context context) {
		super(context);
	}
	public StickyNavLayoutView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(LinearLayout.VERTICAL);

		mScroller = new OverScroller(context);
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mMaximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
		mMinimumVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
		this.mContext = context;
	}
	public StickyNavLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		ViewGroup.LayoutParams params = mViewPager.getLayoutParams();
//		params.height = getMeasuredHeight() - mNav.getMeasuredHeight();
		params.height = params.MATCH_PARENT;
	}
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mTop = findViewById(R.id.id_stickynavlayout_topview);
		mNav = findViewById(R.id.id_stickynavlayout_indicator);
		View view = findViewById(R.id.id_stickynavlayout_viewpager);
		if (!(view instanceof ViewPager)) {
			throw new RuntimeException("id_stickynavlayout_viewpager show used by ViewPager !");
		}
		mViewPager = (ViewPager) view;

	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mTopViewHeight = mTop.getMeasuredHeight();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			float dy = y - mLastY;
			getCurrentScrollView();

			if (mInnerScrollView instanceof ScrollView) {
				if (mInnerScrollView.getScrollY() == 0 && isTopHidden && dy > 0 && !isInControl) {
					return resetMotionEvent(ev);
				}
			} else if (mInnerScrollView instanceof ListView) {

				ListView lv = (ListView) mInnerScrollView;
				View c = lv.getChildAt(lv.getFirstVisiblePosition());

				if (!isInControl && c != null && c.getTop() == 0 && isTopHidden && dy > 0) {
					return resetMotionEvent(ev);
				}
			} 
//			else if (mInnerScrollView instanceof PullToRefreshListView) {
//				PullToRefreshListView lv = (PullToRefreshListView) mInnerScrollView;
//				mListView = lv.getRefreshableView();
//				View c = mListView.getChildAt(mListView.getFirstVisiblePosition());
//				if (!isInControl && c != null && c.getTop() == 0 && isTopHidden && dy > 0) {
//					return resetMotionEvent(ev);
//				}
//			}
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 闁插秶鐤哅otionEvent;
	 *
	 * @param ev
	 * @return
	 */
	private boolean resetMotionEvent(MotionEvent ev) {
		isInControl = true;
		ev.setAction(MotionEvent.ACTION_CANCEL);
		MotionEvent ev2 = MotionEvent.obtain(ev);
		dispatchTouchEvent(ev);
		ev2.setAction(MotionEvent.ACTION_DOWN);
		return dispatchTouchEvent(ev2);
	}

	/**
	 *
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		float y = ev.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			float dy = y - mLastY;
			getCurrentScrollView();
			if (Math.abs(dy) > mTouchSlop) {
				mDragging = true;
				if (mInnerScrollView instanceof ScrollView) {
					if (!isTopHidden || (mInnerScrollView.getScrollY() == 0 && isTopHidden && dy > 0)) {
						return executeSelf(ev, y);
					}
				} else if (mInnerScrollView instanceof ListView) {

					ListView lv = (ListView) mInnerScrollView;
					View c = lv.getChildAt(lv.getFirstVisiblePosition());
					if (!isTopHidden && dy < 0 || (c != null && c.getTop() == 0 && isTopHidden && dy > 0)) {
						return executeSelf(ev, y);
					}
				} 
//				else if (mInnerScrollView instanceof PullToRefreshListView) {
//					PullToRefreshListView lv = (PullToRefreshListView) mInnerScrollView;
//					mListView = lv.getRefreshableView();
//					View c = mListView.getChildAt(mListView.getFirstVisiblePosition());
//					if (!isTopHidden && dy < 0 || (c != null && c.getTop() == 0) && !isDownShow && dy > 0) {
//						return resetMotionEvent(ev);
//					}
//				}

			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mDragging = false;
			recycleVelocityTracker();
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	/**
	 * 娴滃娆㈤懛顏勭箒濞戝牐鍨�
	 *
	 * @param ev
	 * @param y
	 * @return
	 */
	private boolean executeSelf(MotionEvent ev, float y) {
		initVelocityTrackerIfNotExists();
		mVelocityTracker.addMovement(ev);
		mLastY = y;
		return true;
	}

	private void getCurrentScrollView() {

		int currentItem = mViewPager.getCurrentItem();
		PagerAdapter a = mViewPager.getAdapter();
		if (a instanceof FragmentPagerAdapter) {
			FragmentPagerAdapter fadapter = (FragmentPagerAdapter) a;
			Fragment item = (Fragment) fadapter.instantiateItem(mViewPager, currentItem);
			mInnerScrollView = (ViewGroup) (item.getView().findViewById(R.id.id_stickynavlayout_innerscrollview));
		} else if (a instanceof FragmentStatePagerAdapter) {
			FragmentStatePagerAdapter fsAdapter = (FragmentStatePagerAdapter) a;
			Fragment item = (Fragment) fsAdapter.instantiateItem(mViewPager, currentItem);
			mInnerScrollView = (ViewGroup) (item.getView().findViewById(R.id.id_stickynavlayout_innerscrollview));
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		initVelocityTrackerIfNotExists();
		mVelocityTracker.addMovement(event);
		int action = event.getAction();
		float y = event.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:

			downY = event.getY();

			if (!mScroller.isFinished())
				mScroller.abortAnimation();
			mLastY = y;
			return true;
		case MotionEvent.ACTION_MOVE:
			dy = y - mLastY;

			if (!mDragging && Math.abs(dy) > mTouchSlop) {
				mDragging = true;
			}
			if (mDragging) {
				scrollBy(0, (int) -dy);

				moveY = event.getY();
				allY += downY - moveY;
				if (onTopViewScrollingListener != null && !isFling)
					onTopViewScrollingListener.onTopViewScrolling();

				if (getScrollY() == mTopViewHeight && dy < 0) {
					event.setAction(MotionEvent.ACTION_DOWN);
					dispatchTouchEvent(event);
					isInControl = false;
				}
			}

			mLastY = y;
			break;
		case MotionEvent.ACTION_CANCEL:
			mDragging = false;
			recycleVelocityTracker();
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			break;
		case MotionEvent.ACTION_UP:
			mDragging = false;
			mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
			int velocityY = (int) mVelocityTracker.getYVelocity();
			if (Math.abs(velocityY) > mMinimumVelocity) {
				fling(-velocityY);
			}
			recycleVelocityTracker();
			break;
		}

		return super.onTouchEvent(event);
	}

	public void fling(int velocityY) {
		isFling = true;
		mScroller.fling(0, getScrollY(), 0, velocityY, 0, 0, 0, mTopViewHeight);
		// if (onTopViewScrollingListener != null)
		// onTopViewScrollingListener.onFlying(dy > 0);

		if (onTopViewScrollingListener != null) {
			onTopViewScrollingListener.onFling(mScroller.getFinalY(), dy < 0);
			// dy<0 瀵帮拷绗傚鎴濆З;
		}
		invalidate();
		isFling = false;
	}

	public void transY(int y) {
		this.setTranslationY(y);
	}

	public int getTopViewHeight() {
		return mTopViewHeight;
	}

	@Override
	public void scrollTo(int x, int y) {
		if (y < 0) {
			y = 0;
		}
		if (y > mTopViewHeight) {
			y = mTopViewHeight;
		}
		if (y != getScrollY()) {
			super.scrollTo(x, y);
		}

		isTopHidden = getScrollY() == mTopViewHeight;
		isDownShow = getScrollY() == 0;

	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (onTopViewScrollingListener != null && !isFling)
				onTopViewScrollingListener.onTopViewScrolling();
			scrollTo(0, mScroller.getCurrY());
			invalidate();
		}
	}

	private void initVelocityTrackerIfNotExists() {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
	}

	private void recycleVelocityTracker() {
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}

	private OnTopViewScrollingListener onTopViewScrollingListener;

	public void setOnTopViewScrollingListener(OnTopViewScrollingListener onTopViewScrollingListener) {
		this.onTopViewScrollingListener = onTopViewScrollingListener;
	}

	public interface OnTopViewScrollingListener {
		void onTopViewScrolling();

		void onFling(float flingDistance, boolean flingUp);
	}

}
