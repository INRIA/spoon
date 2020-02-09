package me.ccrama.redditslide.SwipeLayout;
import me.ccrama.redditslide.R;
import java.util.ArrayList;
import java.util.List;
public class SwipeBackLayout extends android.widget.FrameLayout {
    /**
     * Minimum velocity that will be detected as a fling
     */
    private static final int MIN_FLING_VELOCITY = 400;// dips per second


    private static final int DEFAULT_SCRIM_COLOR = 0x99000000;

    private static final int FULL_ALPHA = 255;

    /**
     * Edge flag indicating that the left edge should be affected.
     */
    public static final int EDGE_LEFT = me.ccrama.redditslide.SwipeLayout.ViewDragHelper.EDGE_LEFT;

    /**
     * Edge flag indicating that the right edge should be affected.
     */
    public static final int EDGE_RIGHT = me.ccrama.redditslide.SwipeLayout.ViewDragHelper.EDGE_RIGHT;

    /**
     * Edge flag indicating that the bottom edge should be affected.
     */
    public static final int EDGE_BOTTOM = me.ccrama.redditslide.SwipeLayout.ViewDragHelper.EDGE_BOTTOM;

    public static final int EDGE_TOP = me.ccrama.redditslide.SwipeLayout.ViewDragHelper.EDGE_TOP;

    /**
     * Edge flag set indicating all edges should be affected.
     */
    public static final int EDGE_ALL = me.ccrama.redditslide.SwipeLayout.ViewDragHelper.EDGE_ALL;

    /**
     * A view is not currently being dragged or animating as a result of a fling/snap.
     */
    public static final int STATE_IDLE = me.ccrama.redditslide.SwipeLayout.ViewDragHelper.STATE_IDLE;

    /**
     * A view is currently being dragged. The position is currently changing as a result of user
     * input or simulated user input.
     */
    public static final int STATE_DRAGGING = me.ccrama.redditslide.SwipeLayout.ViewDragHelper.STATE_DRAGGING;

    /**
     * A view is currently settling into place as a result of a fling or predefined non-interactive
     * motion.
     */
    public static final int STATE_SETTLING = me.ccrama.redditslide.SwipeLayout.ViewDragHelper.STATE_SETTLING;

    /**
     * Default threshold of scroll
     */
    private static final float DEFAULT_SCROLL_THRESHOLD = 0.3F;

    private static final int OVERSCROLL_DISTANCE = 10;

    private static final int[] EDGE_FLAGS = new int[]{ me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_LEFT, me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_RIGHT, me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_BOTTOM, me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_TOP, me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_ALL };

    private int mEdgeFlag;

    /**
     * Threshold of scroll, we will close the activity, when scrollPercent over this value;
     */
    private float mScrollThreshold = me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.DEFAULT_SCROLL_THRESHOLD;

    private android.app.Activity mActivity;

    private boolean mEnable = true;

    private android.view.View mContentView;

    private me.ccrama.redditslide.SwipeLayout.ViewDragHelper mDragHelper;

    private float mScrollPercent;

    private int mContentLeft;

    private int mContentTop;

    /**
     * The set of listeners to be sent events through.
     */
    private java.util.List<me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.SwipeListener> mListeners;

    private android.graphics.drawable.Drawable mShadowLeft;

    private android.graphics.drawable.Drawable mShadowRight;

    private android.graphics.drawable.Drawable mShadowBottom;

    private android.graphics.drawable.Drawable mShadowTop;

    private float mScrimOpacity;

    private int mScrimColor = me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.DEFAULT_SCRIM_COLOR;

    private boolean mInLayout;

    private android.graphics.Rect mTmpRect = new android.graphics.Rect();

    /**
     * Edge being dragged
     */
    private int mTrackingEdge;

    public SwipeBackLayout(android.content.Context context) {
        this(context, null);
    }

    public SwipeBackLayout(android.content.Context context, android.util.AttributeSet attrs) {
        this(context, attrs, me.ccrama.redditslide.R.attr.SwipeBackLayoutStyle);
    }

    public SwipeBackLayout(android.content.Context context, android.util.AttributeSet attrs, int defStyle) {
        super(context, attrs);
        mDragHelper = me.ccrama.redditslide.SwipeLayout.ViewDragHelper.create(this, new me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.ViewDragCallback());
        android.content.res.TypedArray a = context.obtainStyledAttributes(attrs, me.ccrama.redditslide.R.styleable.SwipeBackLayout, defStyle, me.ccrama.redditslide.R.style.SwipeBackLayout);
        int edgeSize = a.getDimensionPixelSize(me.ccrama.redditslide.R.styleable.SwipeBackLayout_edge_size, -1);
        if (edgeSize > 0)
            setEdgeSize(edgeSize);

        int mode = me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_FLAGS[a.getInt(me.ccrama.redditslide.R.styleable.SwipeBackLayout_edge_flag, 0)];
        setEdgeTrackingEnabled(mode);
        int shadowLeft = a.getResourceId(me.ccrama.redditslide.R.styleable.SwipeBackLayout_shadow_left, me.ccrama.redditslide.R.drawable.shadow_left);
        int shadowRight = a.getResourceId(me.ccrama.redditslide.R.styleable.SwipeBackLayout_shadow_right, me.ccrama.redditslide.R.drawable.shadow_right);
        int shadowBottom = a.getResourceId(me.ccrama.redditslide.R.styleable.SwipeBackLayout_shadow_bottom, me.ccrama.redditslide.R.drawable.shadow_bottom);
        int shadowTop = a.getResourceId(me.ccrama.redditslide.R.styleable.SwipeBackLayout_shadow_top, me.ccrama.redditslide.R.drawable.shadow_top);
        setShadow(shadowLeft, me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_LEFT);
        setShadow(shadowRight, me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_RIGHT);
        setShadow(shadowBottom, me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_BOTTOM);
        setShadow(shadowTop, me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_TOP);
        a.recycle();
        final float density = getResources().getDisplayMetrics().density;
        final float minVel = me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.MIN_FLING_VELOCITY * density;
        mDragHelper.setMinVelocity(minVel);
        mDragHelper.setMaxVelocity(minVel * 2.0F);
    }

    /**
     * Sets the sensitivity of the NavigationLayout.
     *
     * @param context
     * 		The application context.
     * @param sensitivity
     * 		value between 0 and 1, the final value for touchSlop =
     * 		ViewConfiguration.getScaledTouchSlop * (1 / s);
     */
    public void setSensitivity(android.content.Context context, float sensitivity) {
        mDragHelper.setSensitivity(context, sensitivity);
    }

    /**
     * Set up contentView which will be moved by user gesture
     *
     * @param view
     * 		
     */
    private void setContentView(android.view.View view) {
        mContentView = view;
    }

    public void setEnableGesture(boolean enable) {
        mEnable = enable;
    }

    /**
     * Enable edge tracking for the selected edges of the parent view. The callback's {@link me.imid.swipebacklayout.lib.ViewDragHelper.Callback#onEdgeTouched(int, int)} and {@link me.imid.swipebacklayout.lib.ViewDragHelper.Callback#onEdgeDragStarted(int, int)} methods will
     * only be invoked for edges for which edge tracking has been enabled.
     *
     * @param edgeFlags
     * 		Combination of edge flags describing the edges to watch
     * @see #EDGE_LEFT
     * @see #EDGE_RIGHT
     * @see #EDGE_BOTTOM
     */
    public void setEdgeTrackingEnabled(int edgeFlags) {
        mEdgeFlag = edgeFlags;
        mDragHelper.setEdgeTrackingEnabled(mEdgeFlag);
    }

    /**
     * Set a color to use for the scrim that obscures primary content while a drawer is open.
     *
     * @param color
     * 		Color to use in 0xAARRGGBB format.
     */
    public void setScrimColor(int color) {
        mScrimColor = color;
        invalidate();
    }

    /**
     * Set the size of an edge. This is the range in pixels along the edges of this view that will
     * actively detect edge touches or drags if edge tracking is enabled.
     *
     * @param size
     * 		The size of an edge in pixels
     */
    public void setEdgeSize(int size) {
        mDragHelper.setEdgeSize(size);
    }

    /**
     * Register a callback to be invoked when a swipe event is sent to this view.
     *
     * @param listener
     * 		the swipe listener to attach to this view
     * @deprecated use {@link #addSwipeListener} instead
     */
    @java.lang.Deprecated
    public void setSwipeListener(me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.SwipeListener listener) {
        addSwipeListener(listener);
    }

    /**
     * Add a callback to be invoked when a swipe event is sent to this view.
     *
     * @param listener
     * 		the swipe listener to attach to this view
     */
    public void addSwipeListener(me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.SwipeListener listener) {
        if (mListeners == null) {
            mListeners = new java.util.ArrayList<me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.SwipeListener>();
        }
        mListeners.add(listener);
    }

    /**
     * Removes a listener from the set of listeners
     *
     * @param listener
     * 		
     */
    public void removeSwipeListener(me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.SwipeListener listener) {
        if (mListeners == null) {
            return;
        }
        mListeners.remove(listener);
    }

    public static interface SwipeListener {
        /**
         * Invoke when state change
         *
         * @param state
         * 		flag to describe scroll state
         * @param scrollPercent
         * 		scroll percent of this view
         * @see #STATE_IDLE
         * @see #STATE_DRAGGING
         * @see #STATE_SETTLING
         */
        public void onScrollStateChange(int state, float scrollPercent);

        /**
         * Invoke when edge touched
         *
         * @param edgeFlag
         * 		edge flag describing the edge being touched
         * @see #EDGE_LEFT
         * @see #EDGE_RIGHT
         * @see #EDGE_BOTTOM
         * @see #EDGE_TOP
         */
        public void onEdgeTouch(int edgeFlag);

        /**
         * Invoke when scroll percent over the threshold for the first time
         */
        public void onScrollOverThreshold();
    }

    /**
     * Set scroll threshold, we will close the activity, when scrollPercent over this value
     *
     * @param threshold
     * 		
     */
    public void setScrollThresHold(float threshold) {
        if ((threshold >= 1.0F) || (threshold <= 0)) {
            throw new java.lang.IllegalArgumentException("Threshold value should be between 0 and 1.0");
        }
        mScrollThreshold = threshold;
    }

    /**
     * Set a drawable used for edge shadow.
     *
     * @param shadow
     * 		Drawable to use
     * @param edgeFlags
     * 		Combination of edge flags describing the edge to set
     * @see #EDGE_LEFT
     * @see #EDGE_RIGHT
     * @see #EDGE_BOTTOM
     * @see #EDGE_TOP
     */
    public void setShadow(android.graphics.drawable.Drawable shadow, int edgeFlag) {
        if ((edgeFlag & me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_LEFT) != 0) {
            mShadowLeft = shadow;
        } else if ((edgeFlag & me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_RIGHT) != 0) {
            mShadowRight = shadow;
        } else if ((edgeFlag & me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_BOTTOM) != 0) {
            mShadowBottom = shadow;
        } else if ((edgeFlag & me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_TOP) != 0) {
            mShadowTop = shadow;
        }
        invalidate();
    }

    /**
     * Set a drawable used for edge shadow.
     *
     * @param resId
     * 		Resource of drawable to use
     * @param edgeFlags
     * 		Combination of edge flags describing the edge to set
     * @see #EDGE_LEFT
     * @see #EDGE_RIGHT
     * @see #EDGE_BOTTOM
     * @see #EDGE_TOP
     */
    public void setShadow(int resId, int edgeFlag) {
        setShadow(getResources().getDrawable(resId), edgeFlag);
    }

    /**
     * Scroll out contentView and finish the activity
     */
    public void scrollToFinishActivity() {
        final int childWidth = mContentView.getWidth();
        final int childHeight = mContentView.getHeight();
        int left = 0;
        int top = 0;
        if ((mEdgeFlag & me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_LEFT) != 0) {
            left = (childWidth + mShadowLeft.getIntrinsicWidth()) + me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.OVERSCROLL_DISTANCE;
            mTrackingEdge = me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_LEFT;
        } else if ((mEdgeFlag & me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_RIGHT) != 0) {
            left = ((-childWidth) - mShadowRight.getIntrinsicWidth()) - me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.OVERSCROLL_DISTANCE;
            mTrackingEdge = me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_RIGHT;
        } else if ((mEdgeFlag & me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_BOTTOM) != 0) {
            top = ((-childHeight) - mShadowBottom.getIntrinsicHeight()) - me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.OVERSCROLL_DISTANCE;
            mTrackingEdge = me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_BOTTOM;
        } else if ((mEdgeFlag & me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_TOP) != 0) {
            top = (childWidth + mShadowLeft.getIntrinsicWidth()) + me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.OVERSCROLL_DISTANCE;
            mTrackingEdge = me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_TOP;
        }
        mDragHelper.smoothSlideViewTo(mContentView, left, top);
        invalidate();
    }

    @java.lang.Override
    public boolean onInterceptTouchEvent(android.view.MotionEvent event) {
        if (!mEnable) {
            return false;
        }
        try {
            return mDragHelper.shouldInterceptTouchEvent(event);
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            // FIXME: handle exception
            // issues #9
            return false;
        }
    }

    @java.lang.Override
    public boolean onTouchEvent(android.view.MotionEvent event) {
        if (!mEnable) {
            return false;
        }
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @java.lang.Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mInLayout = true;
        if (mContentView != null) {
            mContentView.layout(mContentLeft, mContentTop, mContentLeft + mContentView.getMeasuredWidth(), mContentTop + mContentView.getMeasuredHeight());
        }
        mInLayout = false;
    }

    @java.lang.Override
    public void requestLayout() {
        if (!mInLayout) {
            super.requestLayout();
        }
    }

    @java.lang.Override
    protected boolean drawChild(android.graphics.Canvas canvas, android.view.View child, long drawingTime) {
        final boolean drawContent = child == mContentView;
        boolean ret = super.drawChild(canvas, child, drawingTime);
        if (((mScrimOpacity > 0) && drawContent) && (mDragHelper.getViewDragState() != me.ccrama.redditslide.SwipeLayout.ViewDragHelper.STATE_IDLE)) {
            drawShadow(canvas, child);
            drawScrim(canvas, child);
        }
        return ret;
    }

    private void drawScrim(android.graphics.Canvas canvas, android.view.View child) {
        final int baseAlpha = (mScrimColor & 0xff000000) >>> 24;
        final int alpha = ((int) (baseAlpha * mScrimOpacity));
        final int color = (alpha << 24) | (mScrimColor & 0xffffff);
        if ((mTrackingEdge & me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_LEFT) != 0) {
            canvas.clipRect(0, 0, child.getLeft(), getHeight());
        } else if ((mTrackingEdge & me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_RIGHT) != 0) {
            canvas.clipRect(child.getRight(), 0, getRight(), getHeight());
        } else if ((mTrackingEdge & me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_BOTTOM) != 0) {
            canvas.clipRect(child.getLeft(), child.getBottom(), getRight(), getHeight());
        } else if ((mTrackingEdge & me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_TOP) != 0) {
            canvas.clipRect(child.getLeft(), 0, getRight(), child.getTop() + getStatusBarHeight());
        }
        canvas.drawColor(color);
    }

    private void drawShadow(android.graphics.Canvas canvas, android.view.View child) {
        final android.graphics.Rect childRect = mTmpRect;
        child.getHitRect(childRect);
        if ((mEdgeFlag & me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_LEFT) != 0) {
            mShadowLeft.setBounds(childRect.left - mShadowLeft.getIntrinsicWidth(), childRect.top, childRect.left, childRect.bottom);
            mShadowLeft.setAlpha(((int) (mScrimOpacity * me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.FULL_ALPHA)));
            mShadowLeft.draw(canvas);
        }
        if ((mEdgeFlag & me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_RIGHT) != 0) {
            mShadowRight.setBounds(childRect.right, childRect.top, childRect.right + mShadowRight.getIntrinsicWidth(), childRect.bottom);
            mShadowRight.setAlpha(((int) (mScrimOpacity * me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.FULL_ALPHA)));
            mShadowRight.draw(canvas);
        }
        if ((mEdgeFlag & me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_BOTTOM) != 0) {
            mShadowBottom.setBounds(childRect.left, childRect.bottom, childRect.right, childRect.bottom + mShadowBottom.getIntrinsicHeight());
            mShadowBottom.setAlpha(((int) (mScrimOpacity * me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.FULL_ALPHA)));
            mShadowBottom.draw(canvas);
        }
        if ((mEdgeFlag & me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_TOP) != 0) {
            mShadowTop.setBounds(childRect.left, childRect.top - mShadowTop.getIntrinsicHeight(), childRect.right, childRect.top + getStatusBarHeight());
            mShadowTop.setAlpha(((int) (mScrimOpacity * me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.FULL_ALPHA)));
            mShadowTop.draw(canvas);
        }
    }

    /**
     * Set whether the swipe back gesture should be possible when dragging from anywhere within
     * the activity instead of only from the edges.
     * This changes the meaning of {@link #setEdgeTrackingEnabled(int)} to describe in which
     * directions drag gesture should be possible.
     *
     * @param enabled
     * 		Whether dragging from anywhere within the activity should start swipe
     * 		back.
     */
    public void setFullScreenSwipeEnabled(boolean enabled) {
        mDragHelper.setFullScreenSwipeEnabled(enabled);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void attachToActivity(android.app.Activity activity) {
        mActivity = activity;
        android.content.res.TypedArray a = activity.getTheme().obtainStyledAttributes(new int[]{ android.R.attr.windowBackground });
        int background = a.getResourceId(0, 0);
        a.recycle();
        android.view.ViewGroup decor = ((android.view.ViewGroup) (activity.getWindow().getDecorView()));
        android.view.ViewGroup decorChild = ((android.view.ViewGroup) (decor.getChildAt(0)));
        decorChild.setBackgroundResource(background);
        decor.removeView(decorChild);
        addView(decorChild);
        setContentView(decorChild);
        decor.addView(this);
    }

    @java.lang.Override
    public void computeScroll() {
        mScrimOpacity = 1 - mScrollPercent;
        if (mDragHelper.continueSettling(true)) {
            android.support.v4.view.ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private class ViewDragCallback extends me.ccrama.redditslide.SwipeLayout.ViewDragHelper.Callback {
        private boolean mIsScrollOverValid;

        @java.lang.Override
        public boolean tryCaptureView(android.view.View view, int i) {
            boolean ret = mDragHelper.isEdgeDragInProgress(mEdgeFlag, i);
            if (ret) {
                if (mDragHelper.isEdgeDragInProgress(me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_LEFT, i)) {
                    mTrackingEdge = me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_LEFT;
                } else if (mDragHelper.isEdgeDragInProgress(me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_RIGHT, i)) {
                    mTrackingEdge = me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_RIGHT;
                } else if (mDragHelper.isEdgeDragInProgress(me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_BOTTOM, i)) {
                    mTrackingEdge = me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_BOTTOM;
                } else if (mDragHelper.isEdgeDragInProgress(me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_TOP, i)) {
                    mTrackingEdge = me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_TOP;
                }
                if ((mListeners != null) && (!mListeners.isEmpty())) {
                    for (me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.SwipeListener listener : mListeners) {
                        listener.onEdgeTouch(mTrackingEdge);
                    }
                }
                mIsScrollOverValid = true;
            }
            return ret;
        }

        @java.lang.Override
        public int getViewHorizontalDragRange(android.view.View child) {
            return mEdgeFlag & (me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_LEFT | me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_RIGHT);
        }

        @java.lang.Override
        public int getViewVerticalDragRange(android.view.View child) {
            return mEdgeFlag & (me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_BOTTOM | me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_TOP);
        }

        @java.lang.Override
        public void onViewPositionChanged(android.view.View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if ((mTrackingEdge & me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_LEFT) != 0) {
                mScrollPercent = java.lang.Math.abs(((float) (left)) / (mContentView.getWidth() + mShadowLeft.getIntrinsicWidth()));
            } else if ((mTrackingEdge & me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_RIGHT) != 0) {
                mScrollPercent = java.lang.Math.abs(((float) (left)) / (mContentView.getWidth() + mShadowRight.getIntrinsicWidth()));
            } else if ((mTrackingEdge & me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_BOTTOM) != 0) {
                mScrollPercent = java.lang.Math.abs(((float) (top)) / (mContentView.getHeight() + mShadowBottom.getIntrinsicHeight()));
            } else if ((mTrackingEdge & me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_TOP) != 0) {
                mScrollPercent = java.lang.Math.abs(((float) (top)) / (mContentView.getHeight() + mShadowTop.getIntrinsicHeight()));
            }
            mContentLeft = left;
            mContentTop = top;
            invalidate();
            if ((mScrollPercent < mScrollThreshold) && (!mIsScrollOverValid)) {
                mIsScrollOverValid = true;
            }
            if (((((mListeners != null) && (!mListeners.isEmpty())) && (mDragHelper.getViewDragState() == me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.STATE_DRAGGING)) && (mScrollPercent >= mScrollThreshold)) && mIsScrollOverValid) {
                mIsScrollOverValid = false;
                for (me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.SwipeListener listener : mListeners) {
                    listener.onScrollOverThreshold();
                }
            }
            if (mScrollPercent >= 1) {
                if (!mActivity.isFinishing())
                    mActivity.finish();

            }
        }

        @java.lang.Override
        public void onViewReleased(android.view.View releasedChild, float xvel, float yvel) {
            final int childWidth = releasedChild.getWidth();
            final int childHeight = releasedChild.getHeight();
            int left = 0;
            int top = 0;
            if ((mTrackingEdge & me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_LEFT) != 0) {
                left = ((xvel > 0) || ((xvel == 0) && (mScrollPercent > mScrollThreshold))) ? (childWidth + mShadowLeft.getIntrinsicWidth()) + me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.OVERSCROLL_DISTANCE : 0;
            } else if ((mTrackingEdge & me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_RIGHT) != 0) {
                left = ((xvel < 0) || ((xvel == 0) && (mScrollPercent > mScrollThreshold))) ? -((childWidth + mShadowLeft.getIntrinsicWidth()) + me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.OVERSCROLL_DISTANCE) : 0;
            } else if ((mTrackingEdge & me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_BOTTOM) != 0) {
                top = ((yvel < 0) || ((yvel == 0) && (mScrollPercent > mScrollThreshold))) ? -((childHeight + mShadowBottom.getIntrinsicHeight()) + me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.OVERSCROLL_DISTANCE) : 0;
            } else if ((mTrackingEdge & me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_TOP) != 0) {
                top = ((yvel > 0) || ((yvel == 0) && (mScrollPercent > mScrollThreshold))) ? (childHeight + mShadowBottom.getIntrinsicHeight()) + me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.OVERSCROLL_DISTANCE : 0;
            }
            mDragHelper.settleCapturedViewAt(left, top);
            invalidate();
        }

        @java.lang.Override
        public int clampViewPositionHorizontal(android.view.View child, int left, int dx) {
            int ret = 0;
            if ((mTrackingEdge & me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_LEFT) != 0) {
                ret = java.lang.Math.min(child.getWidth(), java.lang.Math.max(left, 0));
            } else if ((mTrackingEdge & me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_RIGHT) != 0) {
                ret = java.lang.Math.min(0, java.lang.Math.max(left, -child.getWidth()));
            }
            return ret;
        }

        @java.lang.Override
        public int clampViewPositionVertical(android.view.View child, int top, int dy) {
            int ret = 0;
            if ((mTrackingEdge & me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_BOTTOM) != 0) {
                ret = java.lang.Math.min(0, java.lang.Math.max(top, -child.getHeight()));
            } else if ((mTrackingEdge & me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.EDGE_TOP) != 0) {
                ret = java.lang.Math.min(child.getHeight(), java.lang.Math.max(top, 0));
            }
            return ret;
        }

        @java.lang.Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            if ((mListeners != null) && (!mListeners.isEmpty())) {
                for (me.ccrama.redditslide.SwipeLayout.SwipeBackLayout.SwipeListener listener : mListeners) {
                    listener.onScrollStateChange(state, mScrollPercent);
                }
            }
        }
    }
}