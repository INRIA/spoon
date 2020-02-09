package me.ccrama.redditslide.util;
import java.util.LinkedList;
import java.util.List;
public class SoftKeyboardStateWatcher implements android.view.ViewTreeObserver.OnGlobalLayoutListener {
    public interface SoftKeyboardStateListener {
        void onSoftKeyboardOpened(int keyboardHeightInPx);

        void onSoftKeyboardClosed();
    }

    private final java.util.List<me.ccrama.redditslide.util.SoftKeyboardStateWatcher.SoftKeyboardStateListener> listeners = new java.util.LinkedList<me.ccrama.redditslide.util.SoftKeyboardStateWatcher.SoftKeyboardStateListener>();

    private final android.view.View activityRootView;

    private int lastSoftKeyboardHeightInPx;

    private boolean isSoftKeyboardOpened;

    public SoftKeyboardStateWatcher(android.view.View activityRootView) {
        this(activityRootView, false);
    }

    public SoftKeyboardStateWatcher(android.view.View activityRootView, boolean isSoftKeyboardOpened) {
        this.activityRootView = activityRootView;
        this.isSoftKeyboardOpened = isSoftKeyboardOpened;
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @java.lang.Override
    public void onGlobalLayout() {
        final android.graphics.Rect r = new android.graphics.Rect();
        // r will be populated with the coordinates of your view that area still visible.
        activityRootView.getWindowVisibleDisplayFrame(r);
        final int heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);
        if ((!isSoftKeyboardOpened) && (heightDiff > 100)) {
            // if more than 100 pixels, its probably a keyboard...
            isSoftKeyboardOpened = true;
            notifyOnSoftKeyboardOpened(heightDiff);
        } else if (isSoftKeyboardOpened && (heightDiff < 100)) {
            isSoftKeyboardOpened = false;
            notifyOnSoftKeyboardClosed();
        }
    }

    public void setIsSoftKeyboardOpened(boolean isSoftKeyboardOpened) {
        this.isSoftKeyboardOpened = isSoftKeyboardOpened;
    }

    public boolean isSoftKeyboardOpened() {
        return isSoftKeyboardOpened;
    }

    /**
     * Default value is zero {@code 0}.
     *
     * @return last saved keyboard height in px
     */
    public int getLastSoftKeyboardHeightInPx() {
        return lastSoftKeyboardHeightInPx;
    }

    public void addSoftKeyboardStateListener(me.ccrama.redditslide.util.SoftKeyboardStateWatcher.SoftKeyboardStateListener listener) {
        listeners.add(listener);
    }

    public void removeSoftKeyboardStateListener(me.ccrama.redditslide.util.SoftKeyboardStateWatcher.SoftKeyboardStateListener listener) {
        listeners.remove(listener);
    }

    private void notifyOnSoftKeyboardOpened(int keyboardHeightInPx) {
        this.lastSoftKeyboardHeightInPx = keyboardHeightInPx;
        for (me.ccrama.redditslide.util.SoftKeyboardStateWatcher.SoftKeyboardStateListener listener : listeners) {
            if (listener != null) {
                listener.onSoftKeyboardOpened(keyboardHeightInPx);
            }
        }
    }

    private void notifyOnSoftKeyboardClosed() {
        for (me.ccrama.redditslide.util.SoftKeyboardStateWatcher.SoftKeyboardStateListener listener : listeners) {
            if (listener != null) {
                listener.onSoftKeyboardClosed();
            }
        }
    }
}/* FROM: https://stackoverflow.com/a/19354201
USAGE EXAMPLE:
final SoftKeyboardStateWatcher softKeyboardStateWatcher
= new SoftKeyboardStateWatcher(findViewById(R.id.activity_main_layout);

// Add listener
softKeyboardStateWatcher.addSoftKeyboardStateListener(...);
// then just handle callbacks
 */
