package me.ccrama.redditslide.Views;
import me.ccrama.redditslide.R;
/**
 * Created by carlos on 3/14/16.
 */
public class TransparentTagTextView extends android.widget.TextView {
    android.graphics.Bitmap mMaskBitmap;

    android.graphics.Canvas mMaskCanvas;

    android.graphics.Paint mPaint;

    android.graphics.drawable.Drawable mBackground;

    android.graphics.Bitmap mBackgroundBitmap;

    android.graphics.Canvas mBackgroundCanvas;

    boolean mSetBoundsOnSizeAvailable = false;

    public TransparentTagTextView(android.content.Context context) {
        super(context);
        init(context);
    }

    public TransparentTagTextView(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(android.content.Context context) {
        mSetBoundsOnSizeAvailable = true;
        mPaint = new android.graphics.Paint();
        super.setTextColor(android.graphics.Color.BLACK);
        super.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        setBackgroundDrawable(context.getResources().getDrawable(me.ccrama.redditslide.R.drawable.flairback));
    }

    android.graphics.drawable.Drawable backdrop;

    public void resetBackground(android.content.Context context) {
        mPaint = new android.graphics.Paint();
        super.setTextColor(android.graphics.Color.BLACK);
        super.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        backdrop = context.getResources().getDrawable(me.ccrama.redditslide.R.drawable.flairback);
        setBackgroundDrawable(backdrop);
    }

    @java.lang.Override
    public void setBackgroundDrawable(android.graphics.drawable.Drawable bg) {
        if (bg != null) {
            mBackground = bg;
            int w = bg.getIntrinsicWidth();
            int h = bg.getIntrinsicHeight();
            // Drawable has no dimensions, retrieve View's dimensions
            if ((w == (-1)) || (h == (-1))) {
                w = getWidth();
                h = getHeight();
            }
            // Layout has not run
            if ((w == 0) || (h == 0)) {
                mSetBoundsOnSizeAvailable = true;
                return;
            }
            mBackground.setBounds(0, 0, w, h);
        }
        invalidate();
    }

    @java.lang.Override
    public void setBackgroundColor(int color) {
        setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(color));
    }

    @java.lang.Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if ((w > 0) && (h > 0)) {
            mBackgroundBitmap = android.graphics.Bitmap.createBitmap(w, h, android.graphics.Bitmap.Config.ARGB_8888);
            mBackgroundCanvas = new android.graphics.Canvas(mBackgroundBitmap);
            mMaskBitmap = android.graphics.Bitmap.createBitmap(w, h, android.graphics.Bitmap.Config.ARGB_8888);
            mMaskCanvas = new android.graphics.Canvas(mMaskBitmap);
            if (mSetBoundsOnSizeAvailable) {
                mBackground.setBounds(0, 0, w, h);
                mSetBoundsOnSizeAvailable = false;
            }
        }
    }

    @java.lang.Override
    protected void onDraw(android.graphics.Canvas canvas) {
        super.setBackgroundDrawable(backdrop);
        // Draw background
        mBackground.draw(mBackgroundCanvas);
        // Draw mask
        if (mMaskCanvas != null) {
            mMaskCanvas.drawColor(android.graphics.Color.BLACK, android.graphics.PorterDuff.Mode.CLEAR);
            super.onDraw(mMaskCanvas);
            mBackgroundCanvas.drawBitmap(mMaskBitmap, 0.0F, 0.0F, mPaint);
            canvas.drawBitmap(mBackgroundBitmap, 0.0F, 0.0F, null);
        }
    }
}