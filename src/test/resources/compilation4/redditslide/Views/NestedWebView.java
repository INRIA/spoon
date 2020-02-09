package me.ccrama.redditslide.Views;
/**
 * Copyright (c) Tuenti Technologies. All rights reserved.
 *
 * WebView compatible with CoordinatorLayout.
 * The implementation based on NestedScrollView of design library
 */
public class NestedWebView extends android.webkit.WebView implements android.support.v4.view.NestedScrollingChild , android.support.v4.view.NestedScrollingParent {
    private static final int INVALID_POINTER = -1;

    private static final java.lang.String TAG = "NestedWebView";

    private final int[] mScrollOffset = new int[2];

    private final int[] mScrollConsumed = new int[2];

    private int mLastMotionY;

    private android.support.v4.view.NestedScrollingChildHelper mChildHelper;

    private boolean mIsBeingDragged = false;

    private android.view.VelocityTracker mVelocityTracker;

    private int mTouchSlop;

    private int mActivePointerId = me.ccrama.redditslide.Views.NestedWebView.INVALID_POINTER;

    private int mNestedYOffset;

    private android.support.v4.widget.ScrollerCompat mScroller;

    private int mMinimumVelocity;

    private int mMaximumVelocity;

    public NestedWebView(android.content.Context context) {
        this(context, null);
    }

    public NestedWebView(android.content.Context context, android.util.AttributeSet attrs) {
        this(context, attrs, android.R.attr.webViewStyle);
    }

    public NestedWebView(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOverScrollMode(android.webkit.WebView.OVER_SCROLL_NEVER);
        initScrollView();
        mChildHelper = new android.support.v4.view.NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
    }

    private void initScrollView() {
        mScroller = android.support.v4.widget.ScrollerCompat.create(getContext(), null);
        final android.view.ViewConfiguration configuration = android.view.ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    @java.lang.Override
    public boolean onInterceptTouchEvent(android.view.MotionEvent ev) {
        final int action = ev.getAction();
        if ((action == android.view.MotionEvent.ACTION_MOVE) && mIsBeingDragged) {
            return true;
        }
        switch (action & android.support.v4.view.MotionEventCompat.ACTION_MASK) {
            case android.view.MotionEvent.ACTION_MOVE :
                {
                    final int activePointerId = mActivePointerId;
                    if (activePointerId == me.ccrama.redditslide.Views.NestedWebView.INVALID_POINTER) {
                        break;
                    }
                    final int pointerIndex = ev.findPointerIndex(activePointerId);
                    if (pointerIndex == (-1)) {
                        android.util.Log.e(me.ccrama.redditslide.Views.NestedWebView.TAG, ("Invalid pointerId=" + activePointerId) + " in onInterceptTouchEvent");
                        break;
                    }
                    final int y = ((int) (ev.getY(pointerIndex)));
                    final int yDiff = java.lang.Math.abs(y - mLastMotionY);
                    if ((yDiff > mTouchSlop) && ((getNestedScrollAxes() & android.support.v4.view.ViewCompat.SCROLL_AXIS_VERTICAL) == 0)) {
                        mIsBeingDragged = true;
                        mLastMotionY = y;
                        initVelocityTrackerIfNotExists();
                        mVelocityTracker.addMovement(ev);
                        mNestedYOffset = 0;
                        final android.view.ViewParent parent = getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(true);
                        }
                    }
                    break;
                }
            case android.view.MotionEvent.ACTION_DOWN :
                {
                    final int y = ((int) (ev.getY()));
                    mLastMotionY = y;
                    mActivePointerId = ev.getPointerId(0);
                    initOrResetVelocityTracker();
                    mVelocityTracker.addMovement(ev);
                    mScroller.computeScrollOffset();
                    mIsBeingDragged = !mScroller.isFinished();
                    startNestedScroll(android.support.v4.view.ViewCompat.SCROLL_AXIS_VERTICAL);
                    break;
                }
            case android.view.MotionEvent.ACTION_CANCEL :
            case android.view.MotionEvent.ACTION_UP :
                mIsBeingDragged = false;
                mActivePointerId = me.ccrama.redditslide.Views.NestedWebView.INVALID_POINTER;
                recycleVelocityTracker();
                if (mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0, getScrollRange())) {
                    android.support.v4.view.ViewCompat.postInvalidateOnAnimation(this);
                }
                stopNestedScroll();
                break;
            case android.support.v4.view.MotionEventCompat.ACTION_POINTER_UP :
                onSecondaryPointerUp(ev);
                break;
        }
        return mIsBeingDragged;
    }

    @java.lang.Override
    public boolean onTouchEvent(android.view.MotionEvent ev) {
        initVelocityTrackerIfNotExists();
        android.view.MotionEvent vtev = android.view.MotionEvent.obtain(ev);
        final int actionMasked = android.support.v4.view.MotionEventCompat.getActionMasked(ev);
        if (actionMasked == android.view.MotionEvent.ACTION_DOWN) {
            mNestedYOffset = 0;
        }
        vtev.offsetLocation(0, mNestedYOffset);
        switch (actionMasked) {
            case android.view.MotionEvent.ACTION_DOWN :
                {
                    if (mIsBeingDragged = !mScroller.isFinished()) {
                        final android.view.ViewParent parent = getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(true);
                        }
                    }
                    if (!mScroller.isFinished()) {
                        mScroller.abortAnimation();
                    }
                    mLastMotionY = ((int) (ev.getY()));
                    mActivePointerId = ev.getPointerId(0);
                    startNestedScroll(android.support.v4.view.ViewCompat.SCROLL_AXIS_VERTICAL);
                    break;
                }
            case android.view.MotionEvent.ACTION_MOVE :
                final int activePointerIndex = ev.findPointerIndex(mActivePointerId);
                if (activePointerIndex == (-1)) {
                    android.util.Log.e(me.ccrama.redditslide.Views.NestedWebView.TAG, ("Invalid pointerId=" + mActivePointerId) + " in onTouchEvent");
                    break;
                }
                final int y = ((int) (ev.getY(activePointerIndex)));
                int deltaY = mLastMotionY - y;
                if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset)) {
                    deltaY -= mScrollConsumed[1];
                    vtev.offsetLocation(0, mScrollOffset[1]);
                    mNestedYOffset += mScrollOffset[1];
                }
                if ((!mIsBeingDragged) && (java.lang.Math.abs(deltaY) > mTouchSlop)) {
                    final android.view.ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    mIsBeingDragged = true;
                    if (deltaY > 0) {
                        deltaY -= mTouchSlop;
                    } else {
                        deltaY += mTouchSlop;
                    }
                }
                if (mIsBeingDragged) {
                    mLastMotionY = y - mScrollOffset[1];
                    final int oldY = getScrollY();
                    final int scrolledDeltaY = getScrollY() - oldY;
                    final int unconsumedY = deltaY - scrolledDeltaY;
                    if (dispatchNestedScroll(0, scrolledDeltaY, 0, unconsumedY, mScrollOffset)) {
                        mLastMotionY -= mScrollOffset[1];
                        vtev.offsetLocation(0, mScrollOffset[1]);
                        mNestedYOffset += mScrollOffset[1];
                    }
                }
                break;
            case android.view.MotionEvent.ACTION_UP :
                if (mIsBeingDragged) {
                    final android.view.VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int initialVelocity = ((int) (android.support.v4.view.VelocityTrackerCompat.getYVelocity(velocityTracker, mActivePointerId)));
                    if (java.lang.Math.abs(initialVelocity) > mMinimumVelocity) {
                        flingWithNestedDispatch(-initialVelocity);
                    } else if (mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0, getScrollRange())) {
                        android.support.v4.view.ViewCompat.postInvalidateOnAnimation(this);
                    }
                }
                mActivePointerId = me.ccrama.redditslide.Views.NestedWebView.INVALID_POINTER;
                endDrag();
                break;
            case android.view.MotionEvent.ACTION_CANCEL :
                if (mIsBeingDragged && (getChildCount() > 0)) {
                    if (mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0, getScrollRange())) {
                        android.support.v4.view.ViewCompat.postInvalidateOnAnimation(this);
                    }
                }
                mActivePointerId = me.ccrama.redditslide.Views.NestedWebView.INVALID_POINTER;
                endDrag();
                break;
            case android.support.v4.view.MotionEventCompat.ACTION_POINTER_DOWN :
                {
                    final int index = android.support.v4.view.MotionEventCompat.getActionIndex(ev);
                    mLastMotionY = ((int) (ev.getY(index)));
                    mActivePointerId = ev.getPointerId(index);
                    break;
                }
            case android.support.v4.view.MotionEventCompat.ACTION_POINTER_UP :
                onSecondaryPointerUp(ev);
                mLastMotionY = ((int) (ev.getY(ev.findPointerIndex(mActivePointerId))));
                break;
        }
        if (mVelocityTracker != null) {
            mVelocityTracker.addMovement(vtev);
        }
        vtev.recycle();
        return super.onTouchEvent(ev);
    }

    int getScrollRange() {
        // Using scroll range of webview instead of childs as NestedScrollView does.
        return computeVerticalScrollRange();
    }

    private void endDrag() {
        mIsBeingDragged = false;
        recycleVelocityTracker();
        stopNestedScroll();
    }

    private void onSecondaryPointerUp(android.view.MotionEvent ev) {
        final int pointerIndex = (ev.getAction() & android.support.v4.view.MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> android.support.v4.view.MotionEventCompat.ACTION_POINTER_INDEX_SHIFT;
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            final int newPointerIndex = (pointerIndex == 0) ? 1 : 0;
            mLastMotionY = ((int) (ev.getY(newPointerIndex)));
            mActivePointerId = ev.getPointerId(newPointerIndex);
            if (mVelocityTracker != null) {
                mVelocityTracker.clear();
            }
        }
    }

    private void initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = android.view.VelocityTracker.obtain();
        } else {
            mVelocityTracker.clear();
        }
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = android.view.VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void flingWithNestedDispatch(int velocityY) {
        final int scrollY = getScrollY();
        final boolean canFling = ((scrollY > 0) || (velocityY > 0)) && ((scrollY < getScrollRange()) || (velocityY < 0));
        if (!dispatchNestedPreFling(0, velocityY)) {
            dispatchNestedFling(0, velocityY, canFling);
            if (canFling) {
                fling(velocityY);
            }
        }
    }

    public void fling(int velocityY) {
        if (getChildCount() > 0) {
            int height = (getHeight() - getPaddingBottom()) - getPaddingTop();
            int bottom = getChildAt(0).getHeight();
            mScroller.fling(getScrollX(), getScrollY(), 0, velocityY, 0, 0, 0, java.lang.Math.max(0, bottom - height), 0, height / 2);
            android.support.v4.view.ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @java.lang.Override
    public boolean isNestedScrollingEnabled() {
        return mChildHelper.isNestedScrollingEnabled();
    }

    @java.lang.Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @java.lang.Override
    public boolean startNestedScroll(int axes) {
        return mChildHelper.startNestedScroll(axes);
    }

    @java.lang.Override
    public void stopNestedScroll() {
        mChildHelper.stopNestedScroll();
    }

    @java.lang.Override
    public boolean hasNestedScrollingParent() {
        return mChildHelper.hasNestedScrollingParent();
    }

    @java.lang.Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @java.lang.Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @java.lang.Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @java.lang.Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    @java.lang.Override
    public int getNestedScrollAxes() {
        return android.support.v4.view.ViewCompat.SCROLL_AXIS_NONE;
    }
}