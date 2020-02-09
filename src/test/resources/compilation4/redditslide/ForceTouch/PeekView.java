package me.ccrama.redditslide.ForceTouch;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Views.PeekMediaView;
import java.util.HashMap;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.ForceTouch.callback.OnPeek;
import me.ccrama.redditslide.ForceTouch.util.DensityUtils;
import me.ccrama.redditslide.ForceTouch.util.NavigationUtils;
import me.ccrama.redditslide.ForceTouch.callback.OnButtonUp;
import me.ccrama.redditslide.ForceTouch.builder.PeekViewOptions;
import me.ccrama.redditslide.ForceTouch.callback.OnRemove;
import me.ccrama.redditslide.ForceTouch.callback.OnPop;
public class PeekView extends android.widget.FrameLayout {
    private static final int ANIMATION_TIME = 300;

    private static final android.view.animation.Interpolator INTERPOLATOR = new android.view.animation.DecelerateInterpolator();

    private static final int FINGER_SIZE_DP = 40;

    private int FINGER_SIZE;

    public android.view.View content;

    private android.view.ViewGroup.LayoutParams contentParams;

    private android.view.View dim;

    private me.ccrama.redditslide.ForceTouch.builder.PeekViewOptions options;

    private int distanceFromTop;

    private int distanceFromLeft;

    private int screenWidth;

    private int screenHeight;

    private android.view.ViewGroup androidContentView = null;

    private me.ccrama.redditslide.ForceTouch.callback.OnPeek callbacks;

    private me.ccrama.redditslide.ForceTouch.callback.OnRemove remove;

    public PeekView(android.app.Activity context, me.ccrama.redditslide.ForceTouch.builder.PeekViewOptions options, @android.support.annotation.LayoutRes
    int layoutRes, @android.support.annotation.Nullable
    me.ccrama.redditslide.ForceTouch.callback.OnPeek callbacks) {
        super(context);
        init(context, options, android.view.LayoutInflater.from(context).inflate(layoutRes, this, false), callbacks);
    }

    public void addButton(@android.support.annotation.IdRes
    java.lang.Integer i, me.ccrama.redditslide.ForceTouch.callback.OnButtonUp onButtonUp) {
        buttons.put(i, onButtonUp);
    }

    private me.ccrama.redditslide.ForceTouch.callback.OnPop mOnPop;

    int currentHighlight;

    static int eight = me.ccrama.redditslide.Reddit.dpToPxVertical(8);

    public void highlightMenu(android.view.MotionEvent event) {
        if (currentHighlight != 0) {
            final android.view.View v = content.findViewById(currentHighlight);
            android.graphics.Rect outRect = new android.graphics.Rect();
            v.getGlobalVisibleRect(outRect);
            if (!outRect.contains(((int) (event.getX())), ((int) (event.getY())))) {
                currentHighlight = 0;
                android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofInt(me.ccrama.redditslide.ForceTouch.PeekView.eight, me.ccrama.redditslide.ForceTouch.PeekView.eight * 2);
                animator.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {
                    @java.lang.Override
                    public void onAnimationUpdate(android.animation.ValueAnimator valueAnimator) {
                        v.setPadding(0, ((java.lang.Integer) (valueAnimator.getAnimatedValue())), 0, ((java.lang.Integer) (valueAnimator.getAnimatedValue())));
                    }
                });
                animator.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
                animator.setDuration(150);
                animator.start();
            } else {
                return;
            }
        }
        for (java.lang.Integer i : buttons.keySet()) {
            final android.view.View v = content.findViewById(i);
            android.graphics.Rect outRect = new android.graphics.Rect();
            v.getGlobalVisibleRect(outRect);
            if (outRect.contains(((int) (event.getX())), ((int) (event.getY()))) && (i != currentHighlight)) {
                currentHighlight = i;
                android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofInt(me.ccrama.redditslide.ForceTouch.PeekView.eight * 2, me.ccrama.redditslide.ForceTouch.PeekView.eight);
                animator.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {
                    @java.lang.Override
                    public void onAnimationUpdate(android.animation.ValueAnimator valueAnimator) {
                        v.setPadding(0, ((java.lang.Integer) (valueAnimator.getAnimatedValue())), 0, ((java.lang.Integer) (valueAnimator.getAnimatedValue())));
                    }
                });
                animator.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
                animator.setDuration(150);
                animator.start();
                break;
            } else if (outRect.contains(((int) (event.getX())), ((int) (event.getY())))) {
                break;
            }
        }
    }

    public void pop() {
        if (mOnPop != null)
            mOnPop.onPop();

    }

    public void setOnPop(me.ccrama.redditslide.ForceTouch.callback.OnPop mOnPop) {
        this.mOnPop = mOnPop;
    }

    public PeekView(android.app.Activity context, me.ccrama.redditslide.ForceTouch.builder.PeekViewOptions options, @android.support.annotation.NonNull
    android.view.View content, @android.support.annotation.Nullable
    me.ccrama.redditslide.ForceTouch.callback.OnPeek callbacks) {
        super(context);
        init(context, options, content, callbacks);
    }

    java.util.HashMap<java.lang.Integer, me.ccrama.redditslide.ForceTouch.callback.OnButtonUp> buttons = new java.util.HashMap<>();

    public void checkButtons(android.view.MotionEvent event) {
        for (java.lang.Integer i : buttons.keySet()) {
            android.view.View v = content.findViewById(i);
            android.graphics.Rect outRect = new android.graphics.Rect();
            v.getGlobalVisibleRect(outRect);
            if (outRect.contains(((int) (event.getX())), ((int) (event.getY())))) {
                buttons.get(i).onButtonUp();
            }
        }
    }

    public void doScroll(android.view.MotionEvent event) {
        ((me.ccrama.redditslide.Views.PeekMediaView) (content.findViewById(me.ccrama.redditslide.R.id.peek))).doScroll(event);
    }

    private void init(android.app.Activity context, me.ccrama.redditslide.ForceTouch.builder.PeekViewOptions options, @android.support.annotation.NonNull
    android.view.View content, @android.support.annotation.Nullable
    me.ccrama.redditslide.ForceTouch.callback.OnPeek callbacks) {
        this.options = options;
        this.callbacks = callbacks;
        FINGER_SIZE = me.ccrama.redditslide.ForceTouch.util.DensityUtils.toPx(context, me.ccrama.redditslide.ForceTouch.PeekView.FINGER_SIZE_DP);
        // get the main content view of the display
        androidContentView = ((android.widget.FrameLayout) (context.findViewById(android.R.id.content).getRootView()));
        // initialize the display size
        android.view.Display display = context.getWindowManager().getDefaultDisplay();
        android.graphics.Point size = new android.graphics.Point();
        display.getSize(size);
        screenHeight = size.y;
        screenWidth = size.x;
        // set up the content we want to show
        this.content = content;
        contentParams = content.getLayoutParams();
        if (options.getAbsoluteHeight() != 0) {
            setHeight(me.ccrama.redditslide.ForceTouch.util.DensityUtils.toPx(context, options.getAbsoluteHeight()));
        } else {
            setHeightByPercent(options.getHeightPercent());
        }
        if (options.getAbsoluteWidth() != 0) {
            setWidth(me.ccrama.redditslide.ForceTouch.util.DensityUtils.toPx(context, options.getAbsoluteWidth()));
        } else {
            setWidthByPercent(options.getWidthPercent());
        }
        // tell the code that the view has been onInflated and let them use it to
        // set up the layout.
        if (callbacks != null) {
            callbacks.onInflated(this, content);
        }
        // add the background dim to the frame
        dim = new android.view.View(context);
        dim.setBackgroundColor(android.graphics.Color.BLACK);
        dim.setAlpha(options.getBackgroundDim());
        android.widget.FrameLayout.LayoutParams dimParams = new android.widget.FrameLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        dim.setLayoutParams(dimParams);
        if (options.shouldBlurBackground()) {
            try {
                jp.wasabeef.blurry.Blurry.with(context).radius(2).sampling(5).animate().color(options.getBlurOverlayColor()).onto(((android.view.ViewGroup) (androidContentView.getRootView())));
                dim.setAlpha(0.0F);
            } catch (java.lang.Exception ignored) {
            }
        }
        // add the dim and the content view to the upper level frame layout
        addView(dim);
        addView(content);
    }

    /**
     * Sets how far away from the top of the screen the view should be displayed. Distance should be
     * the value in PX.
     *
     * @param distance
     * 		the distance from the top in px.
     */
    private void setDistanceFromTop(int distance) {
        this.distanceFromTop = (options.fullScreenPeek()) ? 0 : distance;
    }

    /**
     * Sets how far away from the left side of the screen the view should be displayed. Distance
     * should be the value in PX.
     *
     * @param distance
     * 		the distance from the left in px.
     */
    private void setDistanceFromLeft(int distance) {
        this.distanceFromLeft = (options.fullScreenPeek()) ? 0 : distance;
    }

    /**
     * Sets the width of the view in PX.
     *
     * @param width
     * 		the width of the circle in px
     */
    private void setWidth(int width) {
        contentParams.width = (options.fullScreenPeek()) ? screenWidth : width;
        content.setLayoutParams(contentParams);
    }

    /**
     * Sets the height of the view in PX.
     *
     * @param height
     * 		the height of the circle in px
     */
    private void setHeight(int height) {
        contentParams.height = (options.fullScreenPeek()) ? screenHeight : height;
        content.setLayoutParams(contentParams);
    }

    /**
     * Sets the width of the window according to the screen width.
     *
     * @param percent
     * 		of screen width
     */
    public void setWidthByPercent(@android.support.annotation.FloatRange(from = 0, to = 1)
    float percent) {
        setWidth(((int) (screenWidth * percent)));
    }

    /**
     * Sets the height of the window according to the screen height.
     *
     * @param percent
     * 		of screen height
     */
    public void setHeightByPercent(@android.support.annotation.FloatRange(from = 0, to = 1)
    float percent) {
        setHeight(((int) (screenHeight * percent)));
    }

    /**
     * Places the peek view over the top of a motion event. This will translate the motion event's
     * start points so that the PeekView isn't covered by the finger.
     *
     * @param event
     * 		event that activates the peek view
     */
    public void setOffsetByMotionEvent(android.view.MotionEvent event) {
        int x = ((int) (event.getRawX()));
        int y = ((int) (event.getRawY()));
        if (((x + contentParams.width) + FINGER_SIZE) < screenWidth) {
            setContentOffset(x, y, me.ccrama.redditslide.ForceTouch.PeekView.Translation.HORIZONTAL, FINGER_SIZE);
        } else if (((x - FINGER_SIZE) - contentParams.width) > 0) {
            setContentOffset(x, y, me.ccrama.redditslide.ForceTouch.PeekView.Translation.HORIZONTAL, (-1) * FINGER_SIZE);
        } else if (((y + contentParams.height) + FINGER_SIZE) < screenHeight) {
            setContentOffset(x, y, me.ccrama.redditslide.ForceTouch.PeekView.Translation.VERTICAL, FINGER_SIZE);
        } else if (((y - FINGER_SIZE) - contentParams.height) > 0) {
            setContentOffset(x, y, me.ccrama.redditslide.ForceTouch.PeekView.Translation.VERTICAL, (-1) * FINGER_SIZE);
        } else // it won't fit anywhere
        if (x < (screenWidth / 2)) {
            setContentOffset(x, y, me.ccrama.redditslide.ForceTouch.PeekView.Translation.HORIZONTAL, FINGER_SIZE);
        } else {
            setContentOffset(x, y, me.ccrama.redditslide.ForceTouch.PeekView.Translation.HORIZONTAL, (-1) * FINGER_SIZE);
        }
    }

    /**
     * Show the PeekView over the point of motion
     *
     * @param startX
     * 		
     * @param startY
     * 		
     */
    private void setContentOffset(int startX, int startY, me.ccrama.redditslide.ForceTouch.PeekView.Translation translation, int movementAmount) {
        if (translation == me.ccrama.redditslide.ForceTouch.PeekView.Translation.VERTICAL) {
            // center the X around the start point
            int originalStartX = startX;
            startX -= contentParams.width / 2;
            // if Y is in the lower half, we want it to go up, otherwise, leave it the same
            boolean moveDown = true;
            if (((startY + contentParams.height) + FINGER_SIZE) > screenHeight) {
                startY -= contentParams.height;
                moveDown = false;
                if (movementAmount > 0) {
                    movementAmount *= -1;
                }
            }
            // when moving the peek view below the finger location, we want to offset it a bit to the right
            // or left as well, just so the hand doesn't cover it up.
            int extraXOffset = 0;
            if (moveDown) {
                extraXOffset = me.ccrama.redditslide.ForceTouch.util.DensityUtils.toPx(getContext(), 200);
                if (originalStartX > (screenWidth / 2)) {
                    extraXOffset = extraXOffset * (-1);// move it a bit to the left

                }
            }
            // make sure they aren't outside of the layout bounds and move them with the movementAmount
            // I move the x just a bit to the right or left here as well, because it just makes things look better
            startX = ensureWithinBounds(startX + extraXOffset, screenWidth, contentParams.width);
            startY = ensureWithinBounds(startY + movementAmount, screenHeight, contentParams.height);
        } else {
            // center the Y around the start point
            startY -= contentParams.height / 2;
            // if X is in the right half, we want it to go left
            if (((startX + contentParams.width) + FINGER_SIZE) > screenWidth) {
                startX -= contentParams.width;
                if (movementAmount > 0) {
                    movementAmount *= -1;
                }
            }
            // make sure they aren't outside of the layout bounds and move them with the movementAmount
            startX = ensureWithinBounds(startX + movementAmount, screenWidth, contentParams.width);
            startY = ensureWithinBounds(startY, screenHeight, contentParams.height);
        }
        // check to see if the system bars are covering anything
        int statusBar = me.ccrama.redditslide.ForceTouch.util.NavigationUtils.getStatusBarHeight(getContext());
        if (startY < statusBar) {
            // if it is above the status bar and action bar
            startY = statusBar + 10;
        } else if (me.ccrama.redditslide.ForceTouch.util.NavigationUtils.hasNavBar(getContext()) && ((startY + contentParams.height) > (screenHeight - me.ccrama.redditslide.ForceTouch.util.NavigationUtils.getNavBarHeight(getContext())))) {
            // if there is a nav bar and the popup is underneath it
            startY = ((screenHeight - contentParams.height) - me.ccrama.redditslide.ForceTouch.util.NavigationUtils.getNavBarHeight(getContext())) - me.ccrama.redditslide.ForceTouch.util.DensityUtils.toDp(getContext(), 10);
        } else if ((!me.ccrama.redditslide.ForceTouch.util.NavigationUtils.hasNavBar(getContext())) && ((startY + contentParams.height) > screenHeight)) {
            startY = (screenHeight - contentParams.height) - me.ccrama.redditslide.ForceTouch.util.DensityUtils.toDp(getContext(), 10);
        }
        // set the newly computed distances from the start and top sides
        setDistanceFromLeft(startX);
        setDistanceFromTop(startY);
    }

    private int ensureWithinBounds(int value, int screenSize, int contentSize) {
        // check these against the layout bounds
        if (value < 0) {
            // if it is off the left side
            value = 10;
        } else if (value > (screenSize - contentSize)) {
            // if it is off the right side
            value = (screenSize - contentSize) - 10;
        }
        return value;
    }

    /**
     * Show the content of the PeekView by adding it to the android.R.id.content FrameLayout.
     */
    public void show() {
        androidContentView.addView(this);
        // set the translations for the content view
        content.setTranslationX(distanceFromLeft);
        content.setTranslationY(distanceFromTop);
        // animate the alpha of the PeekView
        android.animation.ObjectAnimator animator = android.animation.ObjectAnimator.ofFloat(this, android.view.View.ALPHA, 0.0F, 1.0F);
        animator.addListener(new me.ccrama.redditslide.ForceTouch.PeekView.AnimatorEndListener() {
            @java.lang.Override
            public void onAnimationEnd(android.animation.Animator animator) {
                if (callbacks != null) {
                    callbacks.shown();
                }
            }
        });
        animator.setDuration(options.useFadeAnimation() ? me.ccrama.redditslide.ForceTouch.PeekView.ANIMATION_TIME : 0);
        animator.setInterpolator(me.ccrama.redditslide.ForceTouch.PeekView.INTERPOLATOR);
        animator.start();
    }

    /**
     * Hide the PeekView and remove it from the android.R.id.content FrameLayout.
     */
    public void hide() {
        // animate with a fade
        android.animation.ObjectAnimator animator = android.animation.ObjectAnimator.ofFloat(this, android.view.View.ALPHA, 1.0F, 0.0F);
        animator.addListener(new me.ccrama.redditslide.ForceTouch.PeekView.AnimatorEndListener() {
            @java.lang.Override
            public void onAnimationEnd(android.animation.Animator animator) {
                // remove the view from the screen
                androidContentView.removeView(me.ccrama.redditslide.ForceTouch.PeekView.this);
                if (callbacks != null) {
                    callbacks.dismissed();
                }
            }
        });
        animator.setDuration(options.useFadeAnimation() ? me.ccrama.redditslide.ForceTouch.PeekView.ANIMATION_TIME : 0);
        animator.setInterpolator(me.ccrama.redditslide.ForceTouch.PeekView.INTERPOLATOR);
        animator.start();
        jp.wasabeef.blurry.Blurry.delete(((android.view.ViewGroup) (androidContentView.getRootView())));
        if (remove != null)
            remove.onRemove();

    }

    public void setOnRemoveListener(me.ccrama.redditslide.ForceTouch.callback.OnRemove onRemove) {
        this.remove = onRemove;
    }

    /**
     * Wrapper class so we only have to implement the onAnimationEnd method.
     */
    private abstract class AnimatorEndListener implements android.animation.Animator.AnimatorListener {
        @java.lang.Override
        public void onAnimationStart(android.animation.Animator animator) {
        }

        @java.lang.Override
        public void onAnimationCancel(android.animation.Animator animator) {
        }

        @java.lang.Override
        public void onAnimationRepeat(android.animation.Animator animator) {
        }
    }

    private enum Translation {

        HORIZONTAL,
        VERTICAL;}
}