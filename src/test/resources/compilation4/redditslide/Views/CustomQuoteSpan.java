package me.ccrama.redditslide.Views;
/**
 * android.text.style.QuoteSpan hard-codes the strip color and gap; so this will change that
 */
public class CustomQuoteSpan implements android.text.style.LeadingMarginSpan , android.text.style.LineBackgroundSpan {
    private final int backgroundColor;

    private final int stripeColor;

    private final float stripeWidth;

    private final float gap;

    public CustomQuoteSpan(int backgroundColor, int stripeColor, float stripeWidth, float gap) {
        this.backgroundColor = backgroundColor;
        this.stripeColor = stripeColor;
        this.stripeWidth = stripeWidth;
        this.gap = gap;
    }

    @java.lang.Override
    public int getLeadingMargin(boolean first) {
        return ((int) (stripeWidth + gap));
    }

    @java.lang.Override
    public void drawLeadingMargin(android.graphics.Canvas c, android.graphics.Paint p, int x, int dir, int top, int baseline, int bottom, java.lang.CharSequence text, int start, int end, boolean first, android.text.Layout layout) {
        android.graphics.Paint.Style style = p.getStyle();
        int paintColor = p.getColor();
        p.setStyle(android.graphics.Paint.Style.FILL);
        p.setColor(stripeColor);
        c.drawRect(x, top, x + (dir * stripeWidth), bottom, p);
        p.setStyle(style);
        p.setColor(paintColor);
    }

    @java.lang.Override
    public void drawBackground(android.graphics.Canvas c, android.graphics.Paint p, int left, int right, int top, int baseline, int bottom, java.lang.CharSequence text, int start, int end, int lnum) {
        int paintColor = p.getColor();
        p.setColor(backgroundColor);
        c.drawRect(left, top, right, bottom, p);
        p.setColor(paintColor);
    }
}