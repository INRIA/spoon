package me.ccrama.redditslide.Views;
/**
 * Code from https://github.com/father2sisters/scale_videoview
 */
public class PinchZoomVideoView extends android.widget.VideoView {
    android.view.ScaleGestureDetector mScaleGestureDetector;

    android.view.GestureDetector mGestureDetector;

    public PinchZoomVideoView(android.content.Context context) {
        super(context);
        mScaleGestureDetector = new android.view.ScaleGestureDetector(getContext(), new me.ccrama.redditslide.Views.PinchZoomVideoView.MyScaleGestureListener());
        setOnTouchListener(new android.view.View.OnTouchListener() {
            @java.lang.Override
            public boolean onTouch(android.view.View v, android.view.MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                mScaleGestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    public PinchZoomVideoView(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    public PinchZoomVideoView(android.content.Context context, android.util.AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Resize video view by using SurfaceHolder.setFixedSize(...). See {@link android.view.SurfaceHolder#setFixedSize}
     *
     * @param width
     * 		
     * @param height
     * 		
     */
    public void setFixedVideoSize(int width, int height) {
        getHolder().setFixedSize(width, height);
    }

    private class MyScaleGestureListener implements android.view.ScaleGestureDetector.OnScaleGestureListener {
        private int mW;

        private int mH;

        @java.lang.Override
        public boolean onScale(android.view.ScaleGestureDetector detector) {
            // scale our video view
            mW *= detector.getScaleFactor();
            mH *= detector.getScaleFactor();
            if (mW < 200) {
                // limits width
                mW = getWidth();
                mH = getHeight();
            }
            android.util.Log.d("onScale", (((("scale=" + detector.getScaleFactor()) + ", w=") + mW) + ", h=") + mH);
            setFixedVideoSize(mW, mH);// important

            getLayoutParams().width = mW;
            getLayoutParams().height = mH;
            return true;
        }

        @java.lang.Override
        public boolean onScaleBegin(android.view.ScaleGestureDetector detector) {
            mW = getWidth();
            mH = getHeight();
            android.util.Log.d("onScaleBegin", (((("scale=" + detector.getScaleFactor()) + ", w=") + mW) + ", h=") + mH);
            return true;
        }

        @java.lang.Override
        public void onScaleEnd(android.view.ScaleGestureDetector detector) {
            android.util.Log.d("onScaleEnd", (((("scale=" + detector.getScaleFactor()) + ", w=") + mW) + ", h=") + mH);
        }
    }
}