package me.ccrama.redditslide.util;
/**
 * Implementation of {@link View.OnClickListener} that ignores subsequent clicks that happen too quickly after the first one.<br/>
 * To use this class, implement {@link #onSingleClick(View)} instead of {@link View.OnClickListener#onClick(View)}.
 */
// /**
// * Wraps an {@link View.OnClickListener} into an {@link OnSingleClickListener}.
// * The argument's {@link View.OnClickListener#onClick(View)} method will be called when a single click is registered.
// *
// * @param onClickListener The listener to wrap.
// * @return the wrapped listener.
// */
// public static View.OnClickListener wrap(final View.OnClickListener onClickListener) {
// return new OnSingleClickListener() {
// @Override
// public void onSingleClick(View v) {
// onClickListener.onClick(v);
// }
// };
// }
public abstract class OnSingleClickListener implements android.view.View.OnClickListener {
    private static final long MIN_DELAY_MS = 300;

    private static final java.lang.String TAG = me.ccrama.redditslide.util.OnSingleClickListener.class.getSimpleName();

    private static long mLastClickTime;

    public static boolean override;

    /**
     * Called when a view has been clicked.
     *
     * @param v
     * 		The view that was clicked.
     */
    public abstract void onSingleClick(android.view.View v);

    @java.lang.Override
    public final void onClick(android.view.View v) {
        final long lastClickTime = me.ccrama.redditslide.util.OnSingleClickListener.mLastClickTime;
        final long now = android.os.SystemClock.uptimeMillis();// guaranteed 100% monotonic

        if (((now - lastClickTime) < me.ccrama.redditslide.util.OnSingleClickListener.MIN_DELAY_MS) && (!me.ccrama.redditslide.util.OnSingleClickListener.override)) {
            // Too fast: ignore
            if (android.util.Config.LOGD) {
                android.util.Log.d(me.ccrama.redditslide.util.OnSingleClickListener.TAG, "onClick Clicked too quickly: ignored");
            }
        } else {
            me.ccrama.redditslide.util.OnSingleClickListener.override = false;
            // Update mLastClickTime and register the click
            me.ccrama.redditslide.util.OnSingleClickListener.mLastClickTime = now;
            onSingleClick(v);
        }
    }
}