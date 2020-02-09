package me.ccrama.redditslide.ForceTouch;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
public class PeekViewActivity extends android.support.v7.app.AppCompatActivity {
    private me.ccrama.redditslide.ForceTouch.PeekView peekView;

    @java.lang.Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @java.lang.Override
    public void onPause() {
        super.onPause();
        removePeek(null);
    }

    float origY;

    int twelve = me.ccrama.redditslide.Reddit.dpToPxVertical(12);

    @java.lang.Override
    public boolean dispatchTouchEvent(android.view.MotionEvent event) {
        if ((peekView != null) && (event.getAction() == android.view.MotionEvent.ACTION_UP)) {
            if (me.ccrama.redditslide.Reddit.peek) {
                peekView.pop();
                peekView.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
                me.ccrama.redditslide.Reddit.peek = false;
            }
            // the user lifted their finger, so we are going to remove the peek view
            removePeek(event);
            return false;
        } else if (peekView != null) {
            // peekView.doScroll(event);
            peekView.highlightMenu(event);
            android.view.View peek = peekView.content.findViewById(me.ccrama.redditslide.R.id.peek);
            android.widget.RelativeLayout.LayoutParams params = ((android.widget.RelativeLayout.LayoutParams) (peek.getLayoutParams()));
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_MOVE :
                    params.topMargin = ((int) (-((origY - event.getY()) / 5)));
                    if (false && (event.getY() < ((2 * origY) / 3))) {
                        params.leftMargin = twelve - (((int) (origY - event.getY())) / 2);
                        params.rightMargin = twelve - (((int) (origY - event.getY())) / 2);
                    } else {
                        params.leftMargin = twelve;
                        params.rightMargin = twelve;
                    }
                    if ((event.getY() < (origY / 2)) && (!me.ccrama.redditslide.Reddit.peek)) {
                        peekView.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
                        me.ccrama.redditslide.Reddit.peek = true;
                    } else if (event.getY() > (origY / 2)) {
                        me.ccrama.redditslide.Reddit.peek = false;
                    }
                    peek.setLayoutParams(params);
                    break;
            }
            // we don't want to pass along the touch event or else it will just scroll under the PeekView
            return false;
        } else if ((event.getAction() == android.view.MotionEvent.ACTION_MOVE) && me.ccrama.redditslide.Reddit.peek) {
            return false;
        }
        try {
            return super.dispatchTouchEvent(event);
        } catch (java.lang.Exception e) {
            return false;
        }
    }

    public boolean isPeeking() {
        return isPeeking;
    }

    public boolean isPeeking;

    public void showPeek(final me.ccrama.redditslide.ForceTouch.PeekView view, float origY) {
        isPeeking = true;
        peekView = view;
        peekView.show();
        this.origY = origY;
    }

    public void removePeek(android.view.MotionEvent event) {
        isPeeking = false;
        if (peekView != null) {
            if (event != null)
                peekView.checkButtons(event);

            peekView.hide();
            peekView = null;
        }
    }
}