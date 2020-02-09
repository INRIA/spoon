/* Copyright (C) 2008 The Android Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package me.ccrama.redditslide.ForceTouch.util;
/**
 * Detects various gestures and events using the supplied {@link MotionEvent}s.
 * The {@link OnGestureListener} callback will notify users when a particular
 * motion event has occurred. This class should only be used with {@link MotionEvent}s
 * reported via touch (don't use for trackball events).
 *
 * <p>This compatibility implementation of the framework's GestureDetector guarantees
 * the newer focal point scrolling behavior from Jellybean MR1 on all platform versions.</p>
 *
 * To use this class:
 * <ul>
 *  <li>Create an instance of the {@code GestureDetectorCompat} for your {@link View}
 *  <li>In the {@link View#onTouchEvent(MotionEvent)} method ensure you call
 *          {@link #onTouchEvent(MotionEvent)}. The methods defined in your callback
 *          will be executed when the events occur.
 * </ul>
 */
public class GestureDetectorCompat {
    interface GestureDetectorCompatImpl {
        boolean isLongpressEnabled();

        boolean onTouchEvent(android.view.MotionEvent ev);

        void setIsLongpressEnabled(boolean enabled);

        void setOnDoubleTapListener(android.view.GestureDetector.OnDoubleTapListener listener);
    }

    static class GestureDetectorCompatImplBase implements me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImpl {
        private int mTouchSlopSquare;

        private int mDoubleTapSlopSquare;

        private int mMinimumFlingVelocity;

        private int mMaximumFlingVelocity;

        private static final int LONGPRESS_TIMEOUT = 100;

        private static final int TAP_TIMEOUT = android.view.ViewConfiguration.getTapTimeout();

        private static final int DOUBLE_TAP_TIMEOUT = android.view.ViewConfiguration.getDoubleTapTimeout();

        // constants for Message.what used by GestureHandler below
        private static final int SHOW_PRESS = 1;

        private static final int LONG_PRESS = 2;

        private static final int TAP = 3;

        private final android.os.Handler mHandler;

        private final android.view.GestureDetector.OnGestureListener mListener;

        private android.view.GestureDetector.OnDoubleTapListener mDoubleTapListener;

        private boolean mStillDown;

        private boolean mDeferConfirmSingleTap;

        private boolean mInLongPress;

        private boolean mAlwaysInTapRegion;

        private boolean mAlwaysInBiggerTapRegion;

        private android.view.MotionEvent mCurrentDownEvent;

        private android.view.MotionEvent mPreviousUpEvent;

        /**
         * True when the user is still touching for the second tap (down, move, and
         * up events). Can only be true if there is a double tap listener attached.
         */
        private boolean mIsDoubleTapping;

        private float mLastFocusX;

        private float mLastFocusY;

        private float mDownFocusX;

        private float mDownFocusY;

        private boolean mIsLongpressEnabled;

        /**
         * Determines speed during touch scrolling
         */
        private android.view.VelocityTracker mVelocityTracker;

        private class GestureHandler extends android.os.Handler {
            GestureHandler() {
                super();
            }

            GestureHandler(android.os.Handler handler) {
                super(handler.getLooper());
            }

            @java.lang.Override
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImplBase.SHOW_PRESS :
                        mListener.onShowPress(mCurrentDownEvent);
                        break;
                    case me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImplBase.LONG_PRESS :
                        dispatchLongPress();
                        break;
                    case me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImplBase.TAP :
                        // If the user's finger is still down, do not count it as a tap
                        if (mDoubleTapListener != null) {
                            if (!mStillDown) {
                                mDoubleTapListener.onSingleTapConfirmed(mCurrentDownEvent);
                            } else {
                                mDeferConfirmSingleTap = true;
                            }
                        }
                        break;
                    default :
                        throw new java.lang.RuntimeException("Unknown message " + msg);// never

                }
            }
        }

        /**
         * Creates a GestureDetector with the supplied listener.
         * You may only use this constructor from a UI thread (this is the usual situation).
         *
         * @see Handler#Handler()
         * @param context
         * 		the application's context
         * @param listener
         * 		the listener invoked for all the callbacks, this must
         * 		not be null.
         * @param handler
         * 		the handler to use
         * @throws NullPointerException
         * 		if {@code listener} is null.
         */
        public GestureDetectorCompatImplBase(android.content.Context context, android.view.GestureDetector.OnGestureListener listener, android.os.Handler handler) {
            if (handler != null) {
                mHandler = new me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImplBase.GestureHandler(handler);
            } else {
                mHandler = new me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImplBase.GestureHandler();
            }
            mListener = listener;
            if (listener instanceof android.view.GestureDetector.OnDoubleTapListener) {
                setOnDoubleTapListener(((android.view.GestureDetector.OnDoubleTapListener) (listener)));
            }
            init(context);
        }

        private void init(android.content.Context context) {
            if (context == null) {
                throw new java.lang.IllegalArgumentException("Context must not be null");
            }
            if (mListener == null) {
                throw new java.lang.IllegalArgumentException("OnGestureListener must not be null");
            }
            mIsLongpressEnabled = true;
            final android.view.ViewConfiguration configuration = android.view.ViewConfiguration.get(context);
            final int touchSlop = configuration.getScaledTouchSlop();
            final int doubleTapSlop = configuration.getScaledDoubleTapSlop();
            mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
            mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();
            mTouchSlopSquare = touchSlop * touchSlop;
            mDoubleTapSlopSquare = doubleTapSlop * doubleTapSlop;
        }

        /**
         * Sets the listener which will be called for double-tap and related
         * gestures.
         *
         * @param onDoubleTapListener
         * 		the listener invoked for all the callbacks, or
         * 		null to stop listening for double-tap gestures.
         */
        public void setOnDoubleTapListener(android.view.GestureDetector.OnDoubleTapListener onDoubleTapListener) {
            mDoubleTapListener = onDoubleTapListener;
        }

        /**
         * Set whether longpress is enabled, if this is enabled when a user
         * presses and holds down you get a longpress event and nothing further.
         * If it's disabled the user can press and hold down and then later
         * moved their finger and you will get scroll events. By default
         * longpress is enabled.
         *
         * @param isLongpressEnabled
         * 		whether longpress should be enabled.
         */
        public void setIsLongpressEnabled(boolean isLongpressEnabled) {
            mIsLongpressEnabled = isLongpressEnabled;
        }

        /**
         *
         *
         * @return true if longpress is enabled, else false.
         */
        public boolean isLongpressEnabled() {
            return mIsLongpressEnabled;
        }

        /**
         * Analyzes the given motion event and if applicable triggers the
         * appropriate callbacks on the {@link OnGestureListener} supplied.
         *
         * @param ev
         * 		The current motion event.
         * @return true if the {@link OnGestureListener} consumed the event,
        else false.
         */
        public boolean onTouchEvent(android.view.MotionEvent ev) {
            final int action = ev.getAction();
            if (mVelocityTracker == null) {
                mVelocityTracker = android.view.VelocityTracker.obtain();
            }
            mVelocityTracker.addMovement(ev);
            final boolean pointerUp = (action & android.support.v4.view.MotionEventCompat.ACTION_MASK) == android.support.v4.view.MotionEventCompat.ACTION_POINTER_UP;
            final int skipIndex = (pointerUp) ? android.support.v4.view.MotionEventCompat.getActionIndex(ev) : -1;
            // Determine focal point
            float sumX = 0;
            float sumY = 0;
            final int count = android.support.v4.view.MotionEventCompat.getPointerCount(ev);
            for (int i = 0; i < count; i++) {
                if (skipIndex == i)
                    continue;

                sumX += android.support.v4.view.MotionEventCompat.getX(ev, i);
                sumY += android.support.v4.view.MotionEventCompat.getY(ev, i);
            }
            final int div = (pointerUp) ? count - 1 : count;
            final float focusX = sumX / div;
            final float focusY = sumY / div;
            boolean handled = false;
            switch (action & android.support.v4.view.MotionEventCompat.ACTION_MASK) {
                case android.support.v4.view.MotionEventCompat.ACTION_POINTER_DOWN :
                    mDownFocusX = mLastFocusX = focusX;
                    mDownFocusY = mLastFocusY = focusY;
                    // Cancel long press and taps
                    cancelTaps();
                    break;
                case android.support.v4.view.MotionEventCompat.ACTION_POINTER_UP :
                    mDownFocusX = mLastFocusX = focusX;
                    mDownFocusY = mLastFocusY = focusY;
                    // Check the dot product of current velocities.
                    // If the pointer that left was opposing another velocity vector, clear.
                    mVelocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
                    final int upIndex = android.support.v4.view.MotionEventCompat.getActionIndex(ev);
                    final int id1 = android.support.v4.view.MotionEventCompat.getPointerId(ev, upIndex);
                    final float x1 = android.support.v4.view.VelocityTrackerCompat.getXVelocity(mVelocityTracker, id1);
                    final float y1 = android.support.v4.view.VelocityTrackerCompat.getYVelocity(mVelocityTracker, id1);
                    for (int i = 0; i < count; i++) {
                        if (i == upIndex)
                            continue;

                        final int id2 = android.support.v4.view.MotionEventCompat.getPointerId(ev, i);
                        final float x = x1 * android.support.v4.view.VelocityTrackerCompat.getXVelocity(mVelocityTracker, id2);
                        final float y = y1 * android.support.v4.view.VelocityTrackerCompat.getYVelocity(mVelocityTracker, id2);
                        final float dot = x + y;
                        if (dot < 0) {
                            mVelocityTracker.clear();
                            break;
                        }
                    }
                    break;
                case android.view.MotionEvent.ACTION_DOWN :
                    if (mDoubleTapListener != null) {
                        boolean hadTapMessage = mHandler.hasMessages(me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImplBase.TAP);
                        if (hadTapMessage)
                            mHandler.removeMessages(me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImplBase.TAP);

                        if ((((mCurrentDownEvent != null) && (mPreviousUpEvent != null)) && hadTapMessage) && isConsideredDoubleTap(mCurrentDownEvent, mPreviousUpEvent, ev)) {
                            // This is a second tap
                            mIsDoubleTapping = true;
                            // Give a callback with the first tap of the double-tap
                            handled |= mDoubleTapListener.onDoubleTap(mCurrentDownEvent);
                            // Give a callback with down event of the double-tap
                            handled |= mDoubleTapListener.onDoubleTapEvent(ev);
                        } else {
                            // This is a first tap
                            mHandler.sendEmptyMessageDelayed(me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImplBase.TAP, me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImplBase.DOUBLE_TAP_TIMEOUT);
                        }
                    }
                    mDownFocusX = mLastFocusX = focusX;
                    mDownFocusY = mLastFocusY = focusY;
                    if (mCurrentDownEvent != null) {
                        mCurrentDownEvent.recycle();
                    }
                    mCurrentDownEvent = android.view.MotionEvent.obtain(ev);
                    mAlwaysInTapRegion = true;
                    mAlwaysInBiggerTapRegion = true;
                    mStillDown = true;
                    mInLongPress = false;
                    mDeferConfirmSingleTap = false;
                    if (mIsLongpressEnabled) {
                        mHandler.removeMessages(me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImplBase.LONG_PRESS);
                        mHandler.sendEmptyMessageAtTime(me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImplBase.LONG_PRESS, (mCurrentDownEvent.getDownTime() + me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImplBase.TAP_TIMEOUT) + me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImplBase.LONGPRESS_TIMEOUT);
                    }
                    mHandler.sendEmptyMessageAtTime(me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImplBase.SHOW_PRESS, mCurrentDownEvent.getDownTime() + me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImplBase.TAP_TIMEOUT);
                    handled |= mListener.onDown(ev);
                    break;
                case android.view.MotionEvent.ACTION_MOVE :
                    if (mInLongPress) {
                        break;
                    }
                    final float scrollX = mLastFocusX - focusX;
                    final float scrollY = mLastFocusY - focusY;
                    if (mIsDoubleTapping) {
                        // Give the move events of the double-tap
                        handled |= mDoubleTapListener.onDoubleTapEvent(ev);
                    } else if (mAlwaysInTapRegion) {
                        final int deltaX = ((int) (focusX - mDownFocusX));
                        final int deltaY = ((int) (focusY - mDownFocusY));
                        int distance = (deltaX * deltaX) + (deltaY * deltaY);
                        if (distance > mTouchSlopSquare) {
                            handled = mListener.onScroll(mCurrentDownEvent, ev, scrollX, scrollY);
                            mLastFocusX = focusX;
                            mLastFocusY = focusY;
                            mAlwaysInTapRegion = false;
                            mHandler.removeMessages(me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImplBase.TAP);
                            mHandler.removeMessages(me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImplBase.SHOW_PRESS);
                            mHandler.removeMessages(me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImplBase.LONG_PRESS);
                        }
                        if (distance > mTouchSlopSquare) {
                            mAlwaysInBiggerTapRegion = false;
                        }
                    } else if ((java.lang.Math.abs(scrollX) >= 1) || (java.lang.Math.abs(scrollY) >= 1)) {
                        handled = mListener.onScroll(mCurrentDownEvent, ev, scrollX, scrollY);
                        mLastFocusX = focusX;
                        mLastFocusY = focusY;
                    }
                    break;
                case android.view.MotionEvent.ACTION_UP :
                    mStillDown = false;
                    android.view.MotionEvent currentUpEvent = android.view.MotionEvent.obtain(ev);
                    if (mIsDoubleTapping) {
                        // Finally, give the up event of the double-tap
                        handled |= mDoubleTapListener.onDoubleTapEvent(ev);
                    } else if (mInLongPress) {
                        mHandler.removeMessages(me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImplBase.TAP);
                        mInLongPress = false;
                    } else if (mAlwaysInTapRegion) {
                        handled = mListener.onSingleTapUp(ev);
                        if (mDeferConfirmSingleTap && (mDoubleTapListener != null)) {
                            mDoubleTapListener.onSingleTapConfirmed(ev);
                        }
                    } else {
                        // A fling must travel the minimum tap distance
                        final android.view.VelocityTracker velocityTracker = mVelocityTracker;
                        final int pointerId = android.support.v4.view.MotionEventCompat.getPointerId(ev, 0);
                        velocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
                        final float velocityY = android.support.v4.view.VelocityTrackerCompat.getYVelocity(velocityTracker, pointerId);
                        final float velocityX = android.support.v4.view.VelocityTrackerCompat.getXVelocity(velocityTracker, pointerId);
                        if ((java.lang.Math.abs(velocityY) > mMinimumFlingVelocity) || (java.lang.Math.abs(velocityX) > mMinimumFlingVelocity)) {
                            handled = mListener.onFling(mCurrentDownEvent, ev, velocityX, velocityY);
                        }
                    }
                    if (mPreviousUpEvent != null) {
                        mPreviousUpEvent.recycle();
                    }
                    // Hold the event we obtained above - listeners may have changed the original.
                    mPreviousUpEvent = currentUpEvent;
                    if (mVelocityTracker != null) {
                        // This may have been cleared when we called out to the
                        // application above.
                        mVelocityTracker.recycle();
                        mVelocityTracker = null;
                    }
                    mIsDoubleTapping = false;
                    mDeferConfirmSingleTap = false;
                    mHandler.removeMessages(me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImplBase.SHOW_PRESS);
                    mHandler.removeMessages(me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImplBase.LONG_PRESS);
                    break;
                case android.view.MotionEvent.ACTION_CANCEL :
                    cancel();
                    break;
            }
            return handled;
        }

        private void cancel() {
            mHandler.removeMessages(me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImplBase.SHOW_PRESS);
            mHandler.removeMessages(me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImplBase.LONG_PRESS);
            mHandler.removeMessages(me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImplBase.TAP);
            mVelocityTracker.recycle();
            mVelocityTracker = null;
            mIsDoubleTapping = false;
            mStillDown = false;
            mAlwaysInTapRegion = false;
            mAlwaysInBiggerTapRegion = false;
            mDeferConfirmSingleTap = false;
            if (mInLongPress) {
                mInLongPress = false;
            }
        }

        private void cancelTaps() {
            mHandler.removeMessages(me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImplBase.SHOW_PRESS);
            mHandler.removeMessages(me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImplBase.LONG_PRESS);
            mHandler.removeMessages(me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImplBase.TAP);
            mIsDoubleTapping = false;
            mAlwaysInTapRegion = false;
            mAlwaysInBiggerTapRegion = false;
            mDeferConfirmSingleTap = false;
            if (mInLongPress) {
                mInLongPress = false;
            }
        }

        private boolean isConsideredDoubleTap(android.view.MotionEvent firstDown, android.view.MotionEvent firstUp, android.view.MotionEvent secondDown) {
            if (!mAlwaysInBiggerTapRegion) {
                return false;
            }
            if ((secondDown.getEventTime() - firstUp.getEventTime()) > me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImplBase.DOUBLE_TAP_TIMEOUT) {
                return false;
            }
            int deltaX = ((int) (firstDown.getX())) - ((int) (secondDown.getX()));
            int deltaY = ((int) (firstDown.getY())) - ((int) (secondDown.getY()));
            return ((deltaX * deltaX) + (deltaY * deltaY)) < mDoubleTapSlopSquare;
        }

        private void dispatchLongPress() {
            mHandler.removeMessages(me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImplBase.TAP);
            mDeferConfirmSingleTap = false;
            mInLongPress = true;
            mListener.onLongPress(mCurrentDownEvent);
        }
    }

    static class GestureDetectorCompatImplJellybeanMr2 implements me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImpl {
        private final android.view.GestureDetector mDetector;

        public GestureDetectorCompatImplJellybeanMr2(android.content.Context context, android.view.GestureDetector.OnGestureListener listener, android.os.Handler handler) {
            mDetector = new android.view.GestureDetector(context, listener, handler);
        }

        @java.lang.Override
        public boolean isLongpressEnabled() {
            return mDetector.isLongpressEnabled();
        }

        @java.lang.Override
        public boolean onTouchEvent(android.view.MotionEvent ev) {
            return mDetector.onTouchEvent(ev);
        }

        @java.lang.Override
        public void setIsLongpressEnabled(boolean enabled) {
            mDetector.setIsLongpressEnabled(enabled);
        }

        @java.lang.Override
        public void setOnDoubleTapListener(android.view.GestureDetector.OnDoubleTapListener listener) {
            mDetector.setOnDoubleTapListener(listener);
        }
    }

    private final me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImpl mImpl;

    /**
     * Creates a GestureDetectorCompat with the supplied listener.
     * As usual, you may only use this constructor from a UI thread.
     *
     * @see Handler#Handler()
     * @param context
     * 		the application's context
     * @param listener
     * 		the listener invoked for all the callbacks, this must
     * 		not be null.
     */
    public GestureDetectorCompat(android.content.Context context, android.view.GestureDetector.OnGestureListener listener) {
        this(context, listener, null);
    }

    /**
     * Creates a GestureDetectorCompat with the supplied listener.
     * As usual, you may only use this constructor from a UI thread.
     *
     * @see Handler#Handler()
     * @param context
     * 		the application's context
     * @param listener
     * 		the listener invoked for all the callbacks, this must
     * 		not be null.
     * @param handler
     * 		the handler that will be used for posting deferred messages
     */
    public GestureDetectorCompat(android.content.Context context, android.view.GestureDetector.OnGestureListener listener, android.os.Handler handler) {
        if (android.os.Build.VERSION.SDK_INT > 17) {
            mImpl = new me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImplJellybeanMr2(context, listener, handler);
        } else {
            mImpl = new me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat.GestureDetectorCompatImplBase(context, listener, handler);
        }
    }

    /**
     *
     *
     * @return true if longpress is enabled, else false.
     */
    public boolean isLongpressEnabled() {
        return mImpl.isLongpressEnabled();
    }

    /**
     * Analyzes the given motion event and if applicable triggers the
     * appropriate callbacks on the {@link OnGestureListener} supplied.
     *
     * @param event
     * 		The current motion event.
     * @return true if the {@link OnGestureListener} consumed the event,
    else false.
     */
    public boolean onTouchEvent(android.view.MotionEvent event) {
        return mImpl.onTouchEvent(event);
    }

    /**
     * Set whether longpress is enabled, if this is enabled when a user
     * presses and holds down you get a longpress event and nothing further.
     * If it's disabled the user can press and hold down and then later
     * moved their finger and you will get scroll events. By default
     * longpress is enabled.
     *
     * @param enabled
     * 		whether longpress should be enabled.
     */
    public void setIsLongpressEnabled(boolean enabled) {
        mImpl.setIsLongpressEnabled(enabled);
    }

    /**
     * Sets the listener which will be called for double-tap and related
     * gestures.
     *
     * @param listener
     * 		the listener invoked for all the callbacks, or
     * 		null to stop listening for double-tap gestures.
     */
    public void setOnDoubleTapListener(android.view.GestureDetector.OnDoubleTapListener listener) {
        mImpl.setOnDoubleTapListener(listener);
    }
}