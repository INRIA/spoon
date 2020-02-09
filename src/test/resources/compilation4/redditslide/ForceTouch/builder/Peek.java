/* Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package me.ccrama.redditslide.ForceTouch.builder;
import me.ccrama.redditslide.ForceTouch.PeekViewActivity;
import me.ccrama.redditslide.ForceTouch.PeekView;
import me.ccrama.redditslide.ForceTouch.util.GestureListener;
import me.ccrama.redditslide.ForceTouch.callback.OnPeek;
import me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat;
/**
 * This is a builder class to facilitate the creation of the PeekView.
 */
public class Peek {
    public static me.ccrama.redditslide.ForceTouch.builder.Peek into(@android.support.annotation.LayoutRes
    int layoutRes, @android.support.annotation.Nullable
    me.ccrama.redditslide.ForceTouch.callback.OnPeek onPeek) {
        return new me.ccrama.redditslide.ForceTouch.builder.Peek(layoutRes, onPeek);
    }

    public static me.ccrama.redditslide.ForceTouch.builder.Peek into(android.view.View layout, @android.support.annotation.Nullable
    me.ccrama.redditslide.ForceTouch.callback.OnPeek onPeek) {
        return new me.ccrama.redditslide.ForceTouch.builder.Peek(layout, onPeek);
    }

    /**
     * Used to clear the peeking ability. This could be useful for a RecyclerView/ListView, where a recycled item
     * shouldn't use the PeekView, but the original item did.
     *
     * @param view
     * 		the view we want to stop peeking into
     */
    public static void clear(android.view.View view) {
        view.setOnTouchListener(null);
    }

    private int layoutRes = 0;

    private android.view.View layout = null;

    private me.ccrama.redditslide.ForceTouch.builder.PeekViewOptions options = new me.ccrama.redditslide.ForceTouch.builder.PeekViewOptions();

    private me.ccrama.redditslide.ForceTouch.callback.OnPeek callbacks;

    private Peek(@android.support.annotation.LayoutRes
    int layoutRes, @android.support.annotation.Nullable
    me.ccrama.redditslide.ForceTouch.callback.OnPeek callbacks) {
        this.layoutRes = layoutRes;
        this.callbacks = callbacks;
    }

    private Peek(android.view.View layout, @android.support.annotation.Nullable
    me.ccrama.redditslide.ForceTouch.callback.OnPeek callbacks) {
        this.layout = layout;
        this.callbacks = callbacks;
    }

    /**
     * Apply the options to the PeekView, when it is shown.
     *
     * @param options
     * 		
     */
    public me.ccrama.redditslide.ForceTouch.builder.Peek with(me.ccrama.redditslide.ForceTouch.builder.PeekViewOptions options) {
        this.options = options;
        return this;
    }

    /**
     * Finish the builder by selecting the base view that you want to show the PeekView from.
     *
     * @param activity
     * 		the PeekViewActivity that you are on.
     * @param base
     * 		the view that you want to touch to apply the peek to.
     */
    public void applyTo(final me.ccrama.redditslide.ForceTouch.PeekViewActivity activity, final android.view.View base) {
        final me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat detector = new me.ccrama.redditslide.ForceTouch.util.GestureDetectorCompat(activity, new me.ccrama.redditslide.ForceTouch.util.GestureListener(activity, base, this));
        base.setOnTouchListener(new android.view.View.OnTouchListener() {
            @java.lang.Override
            public boolean onTouch(android.view.View view, final android.view.MotionEvent motionEvent) {
                // we use the detector for the long and short click motion events
                detector.onTouchEvent(motionEvent);
                if (motionEvent.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    forceRippleAnimation(base, motionEvent);
                }
                return true;
            }
        });
    }

    /**
     * Show the PeekView
     *
     * @param activity
     * 		
     * @param motionEvent
     * 		
     */
    public void show(me.ccrama.redditslide.ForceTouch.PeekViewActivity activity, android.view.MotionEvent motionEvent) {
        me.ccrama.redditslide.ForceTouch.PeekView peek;
        if (layout == null) {
            peek = new me.ccrama.redditslide.ForceTouch.PeekView(activity, options, layoutRes, callbacks);
        } else {
            peek = new me.ccrama.redditslide.ForceTouch.PeekView(activity, options, layout, callbacks);
        }
        peek.setOffsetByMotionEvent(motionEvent);
        activity.showPeek(peek, motionEvent.getRawY());
    }

    private void forceRippleAnimation(android.view.View view, android.view.MotionEvent event) {
        android.graphics.drawable.Drawable background = view.getBackground();
        if ((android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) && (background instanceof android.graphics.drawable.RippleDrawable)) {
            final android.graphics.drawable.RippleDrawable rippleDrawable = ((android.graphics.drawable.RippleDrawable) (background));
            rippleDrawable.setState(new int[]{ android.R.attr.state_pressed, android.R.attr.state_enabled });
            rippleDrawable.setHotspot(event.getX(), event.getY());
            new android.os.Handler().postDelayed(new java.lang.Runnable() {
                @java.lang.Override
                public void run() {
                    rippleDrawable.setState(new int[]{  });
                }
            }, 300);
        }
    }
}