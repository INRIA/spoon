package me.ccrama.redditslide.handler;
import me.ccrama.redditslide.ClickableText;
import me.ccrama.redditslide.SpoilerRobotoTextView;
import me.ccrama.redditslide.SettingValues;
public class TextViewLinkHandler extends android.text.method.BaseMovementMethod {
    private final me.ccrama.redditslide.ClickableText clickableText;

    java.lang.String subreddit;

    me.ccrama.redditslide.SpoilerRobotoTextView comm;

    android.text.Spannable sequence;

    float position;

    boolean clickHandled;

    android.os.Handler handler;

    java.lang.Runnable longClicked;

    android.text.style.URLSpan[] link;

    android.view.MotionEvent event;

    public TextViewLinkHandler(me.ccrama.redditslide.ClickableText clickableText, java.lang.String subreddit, android.text.Spannable sequence) {
        this.clickableText = clickableText;
        this.subreddit = subreddit;
        this.sequence = sequence;
        clickHandled = false;
        handler = new android.os.Handler();
        longClicked = new java.lang.Runnable() {
            @java.lang.Override
            public void run() {
                // long click
                clickHandled = true;
                handler.removeCallbacksAndMessages(null);
                if (((link != null) && (link.length > 0)) && (link[0] != null)) {
                    me.ccrama.redditslide.handler.TextViewLinkHandler.this.clickableText.onLinkLongClick(link[0].getURL(), event);
                }
            }
        };
    }

    @java.lang.Override
    public boolean canSelectArbitrarily() {
        return false;
    }

    @java.lang.Override
    public boolean onTouchEvent(android.widget.TextView widget, final android.text.Spannable buffer, android.view.MotionEvent event) {
        comm = ((me.ccrama.redditslide.SpoilerRobotoTextView) (widget));
        int x = ((int) (event.getX()));
        int y = ((int) (event.getY()));
        x -= widget.getTotalPaddingLeft();
        y -= widget.getTotalPaddingTop();
        x += widget.getScrollX();
        y += widget.getScrollY();
        android.text.Layout layout = widget.getLayout();
        int line = layout.getLineForVertical(y);
        int off = layout.getOffsetForHorizontal(line, x);
        link = buffer.getSpans(off, off, android.text.style.URLSpan.class);
        if (link.length > 0) {
            comm.setLongClickable(false);
            if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                position = event.getY();// used to see if the user scrolled or not

            }
            if (!((event.getAction() == android.view.MotionEvent.ACTION_UP) || (event.getAction() == android.view.MotionEvent.ACTION_DOWN))) {
                if (java.lang.Math.abs(position - event.getY()) > 25) {
                    handler.removeCallbacksAndMessages(null);
                }
                return super.onTouchEvent(widget, buffer, event);
            }
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN :
                    clickHandled = false;
                    this.event = event;
                    if (me.ccrama.redditslide.SettingValues.peek) {
                        handler.postDelayed(longClicked, android.view.ViewConfiguration.getTapTimeout() + 50);
                    } else {
                        handler.postDelayed(longClicked, android.view.ViewConfiguration.getLongPressTimeout());
                    }
                    break;
                case android.view.MotionEvent.ACTION_UP :
                    comm.setLongClickable(true);
                    handler.removeCallbacksAndMessages(null);
                    if (!clickHandled) {
                        // regular click
                        if (link.length != 0) {
                            int i = 0;
                            if (sequence != null) {
                                i = sequence.getSpanEnd(link[0]);
                            }
                            if (!link[0].getURL().isEmpty()) {
                                clickableText.onLinkClick(link[0].getURL(), i, subreddit, link[0]);
                            }
                        } else {
                            return false;
                        }
                    }
                    break;
            }
            return true;
        } else {
            android.text.Selection.removeSelection(buffer);
            return false;
        }
    }
}