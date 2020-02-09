package me.ccrama.redditslide.Views;
/**
 * Created by carlo_000 on 3/11/2016.
 */
public class RoundedBackgroundSpan extends android.text.style.ReplacementSpan {
    private int backgroundColor = 0;

    private int textColor = 0;

    private boolean half;

    private android.content.Context c;

    public RoundedBackgroundSpan(android.content.Context context, @android.support.annotation.ColorRes
    int textColor, @android.support.annotation.ColorRes
    int backgroundColor, boolean half) {
        super();
        this.backgroundColor = context.getResources().getColor(backgroundColor);
        this.textColor = context.getResources().getColor(textColor);
        this.half = half;
        this.c = context;
    }

    public RoundedBackgroundSpan(@android.support.annotation.ColorInt
    int textColor, @android.support.annotation.ColorInt
    int backgroundColor, boolean half, android.content.Context context) {
        super();
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.half = half;
        this.c = context;
    }

    @java.lang.Override
    public void draw(android.graphics.Canvas canvas, java.lang.CharSequence oldText, int start, int end, float x, int top, int y, int bottom, android.graphics.Paint paint) {
        int offset = 0;
        if (half) {
            offset = (bottom - top) / 6;
        }
        paint.setTypeface(com.devspark.robototextview.RobotoTypefaces.obtainTypeface(c, com.devspark.robototextview.RobotoTypefaces.TYPEFACE_ROBOTO_CONDENSED_BOLD));
        if (half) {
            paint.setTextSize(paint.getTextSize() / 2);
        }
        final android.graphics.RectF rect = new android.graphics.RectF(x, top + offset, x + measureText(paint, oldText, start, end), bottom - offset);
        paint.setColor(backgroundColor);
        final int CORNER_RADIUS = 8;
        canvas.drawRoundRect(rect, CORNER_RADIUS, CORNER_RADIUS, paint);
        paint.setColor(textColor);
        final float baseLine = paint.descent();
        canvas.drawText(oldText, start, end, x, (rect.bottom - ((rect.bottom - rect.top) / 2)) + (baseLine * 1.5F), paint);// center the text in the parent span

    }

    @java.lang.Override
    public int getSize(android.graphics.Paint paint, java.lang.CharSequence text, int start, int end, android.graphics.Paint.FontMetricsInt fm) {
        paint.setTypeface(com.devspark.robototextview.RobotoTypefaces.obtainTypeface(c, com.devspark.robototextview.RobotoTypefaces.TYPEFACE_ROBOTO_CONDENSED_BOLD));
        final int size = java.lang.Math.round(paint.measureText(text, start, end));
        if (half) {
            return size / 2;
        } else {
            return size;
        }
    }

    private float measureText(android.graphics.Paint paint, java.lang.CharSequence text, int start, int end) {
        return paint.measureText(text, start, end);
    }
}