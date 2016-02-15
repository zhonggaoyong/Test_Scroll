package com.zhonggaoyong.scroll.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

@SuppressLint("ClickableViewAccessibility")
public class ScrollViewGroup extends ViewGroup {

	/**
	 * 判定为拖动的最小移动像素数
	 */
//	private int mTouchSlop;
	/**
	 * 界面可滚动的左边界
	 */
	private int leftBorder;
	/**
	 * 用于完成滚动操作的实例
	 */
	private Scroller mScroller;
	/**
	 * 界面可滚动的右边界
	 */
	private int rightBorder;

	public ScrollViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		ViewConfiguration configuration = ViewConfiguration.get(context);
		// 获取TouchSlop值
//		mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
		mScroller = new Scroller(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			View childView = getChildAt(i);
			measureChild(childView, widthMeasureSpec, heightMeasureSpec);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		if (changed) {
			int childCount = getChildCount();
			for (int i = 0; i < childCount; i++) {
				View childView = getChildAt(i);
				childView.layout(i * childView.getMeasuredWidth(), 0, (i + 1) * childView.getMeasuredWidth(), childView.getMeasuredHeight());
			}
			// 初始化左右边界值
			leftBorder = getChildAt(0).getLeft();
			rightBorder = getChildAt(getChildCount() - 1).getRight();
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			oldLeft = (int) ev.getRawX();
			return false;
		}
		return true;
	}

	int oldLeft = 0;
	int newLeft = 0;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			oldLeft = (int) event.getRawX();
			break;
		case MotionEvent.ACTION_UP:
			// 当手指抬起时，根据当前的滚动值来判定应该滚动到哪个子控件的界面
			int targetIndex = (getScrollX() + getWidth() / 2) / getWidth();
			int dx = targetIndex * getWidth() - getScrollX();
			// 第二步，调用startScroll()方法来初始化滚动数据并刷新界面
			mScroller.startScroll(getScrollX(), 0, dx, 0);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			newLeft = (int) event.getRawX();
			int scrolledX = (int) (oldLeft - newLeft);
			int getScrollX = getScrollX();
			if (getScrollX + scrolledX < leftBorder) {
				scrollTo(0, 0);
			} else if (getScrollX + scrolledX + getWidth() > rightBorder) {
				scrollTo(rightBorder - getWidth(), 0);
			} else {
				scrollBy(scrolledX, 0);
			}
			oldLeft = newLeft;
			break;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void computeScroll() {
		// 第三步，重写computeScroll()方法，并在其内部完成平滑滚动的逻辑
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			invalidate();
		}
	}
}
