/**
 * Created by ccrama on 7/18/2015.
 */
package me.ccrama.redditslide;
/**
 * Implementation of LinkMovementMethod to allow the loading of
 * a link clicked inside text inside an Android application
 * without exiting to an external browser.
 *
 * @author Isaac Whitfield
 * @version 25/08/2013
 */
class CustomMovementMethod extends android.text.method.LinkMovementMethod {
    @java.lang.Override
    public boolean canSelectArbitrarily() {
        return true;
    }

    @java.lang.Override
    public void initialize(android.widget.TextView widget, android.text.Spannable text) {
        android.text.Selection.setSelection(text, text.length());
    }

    @java.lang.Override
    public void onTakeFocus(android.widget.TextView view, android.text.Spannable text, int dir) {
        if ((dir & (android.view.View.FOCUS_FORWARD | android.view.View.FOCUS_DOWN)) != 0) {
            if (view.getLayout() == null) {
                // This shouldn't be null, but do something sensible if it is.
                android.text.Selection.setSelection(text, text.length());
            }
        } else {
            android.text.Selection.setSelection(text, text.length());
        }
    }

    @java.lang.Override
    public boolean onTouchEvent(android.widget.TextView widget, android.text.Spannable buffer, android.view.MotionEvent event) {
        int action = event.getAction();
        if ((action == android.view.MotionEvent.ACTION_UP) || (action == android.view.MotionEvent.ACTION_DOWN)) {
            int x = ((int) (event.getX()));
            int y = ((int) (event.getY()));
            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();
            x += widget.getScrollX();
            y += widget.getScrollY();
            android.text.Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);
            android.text.style.ClickableSpan[] link = buffer.getSpans(off, off, android.text.style.ClickableSpan.class);
            if (link.length != 0) {
                if (action == android.view.MotionEvent.ACTION_UP) {
                    link[0].onClick(widget);
                } else if (action == android.view.MotionEvent.ACTION_DOWN) {
                    android.text.Selection.setSelection(buffer, buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]));
                }
                return true;
            }
        }
        return android.text.method.Touch.onTouchEvent(widget, buffer, event);
    }
}