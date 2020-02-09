package me.ccrama.redditslide.Views;
// Adapted from https://github.com/MajeurAndroid/CircularReveal/commit/a87e3ad4daac96f942be0e240ebfc098a79f5419
public class RevealRelativeLayout extends android.widget.RelativeLayout implements io.codetail.animation.RevealAnimator {
    private android.graphics.Path mRevealPath;

    private final android.graphics.Rect mTargetBounds = new android.graphics.Rect();

    private io.codetail.animation.RevealAnimator.RevealInfo mRevealInfo;

    private boolean mRunning;

    private float mRadius;

    @java.lang.Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    public RevealRelativeLayout(android.content.Context context) {
        this(context, null);
    }

    public RevealRelativeLayout(android.content.Context context, android.util.AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RevealRelativeLayout(android.content.Context context, android.util.AttributeSet attrs, int defStyle) {
        super(context, attrs);
        mRevealPath = new android.graphics.Path();
    }

    @java.lang.Override
    public void onRevealAnimationStart() {
        mRunning = true;
    }

    @java.lang.Override
    public void onRevealAnimationEnd() {
        mRunning = false;
        invalidate(mTargetBounds);
    }

    @java.lang.Override
    public void onRevealAnimationCancel() {
        onRevealAnimationEnd();
    }

    /**
     * Circle radius size
     *
     * @unknown 
     */
    @java.lang.Override
    public void setRevealRadius(float radius) {
        mRadius = radius;
        mRevealInfo.getTarget().getHitRect(mTargetBounds);
        invalidate(mTargetBounds);
    }

    /**
     * Circle radius size
     *
     * @unknown 
     */
    @java.lang.Override
    public float getRevealRadius() {
        return mRadius;
    }

    /**
     *
     *
     * @unknown 
     */
    @java.lang.Override
    public void attachRevealInfo(io.codetail.animation.RevealAnimator.RevealInfo info) {
        mRevealInfo = info;
    }

    /**
     *
     *
     * @unknown 
     */
    @java.lang.Override
    public io.codetail.animation.SupportAnimator startReverseAnimation() {
        if (((mRevealInfo != null) && mRevealInfo.hasTarget()) && (!mRunning)) {
            return io.codetail.animation.ViewAnimationUtils.createCircularReveal(mRevealInfo.getTarget(), mRevealInfo.centerX, mRevealInfo.centerY, mRevealInfo.endRadius, mRevealInfo.startRadius);
        }
        return null;
    }

    @java.lang.Override
    protected boolean drawChild(android.graphics.Canvas canvas, android.view.View child, long drawingTime) {
        if (mRunning && (child == mRevealInfo.getTarget())) {
            final int state = canvas.save();
            mRevealPath.reset();
            mRevealPath.addCircle(mRevealInfo.centerX, mRevealInfo.centerY, mRadius, android.graphics.Path.Direction.CW);
            canvas.clipPath(mRevealPath);
            boolean isInvalided = super.drawChild(canvas, child, drawingTime);
            canvas.restoreToCount(state);
            return isInvalided;
        }
        return super.drawChild(canvas, child, drawingTime);
    }
}