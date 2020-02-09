package me.ccrama.redditslide.Views;
import me.ccrama.redditslide.R;
/**
 * Created by carlo_000 on 2/24/2016.
 */
public class AnimateHelper {
    private AnimateHelper() {
    }

    public static void setFlashAnimation(final android.view.View vBig, final android.view.View from, final int color) {
        // get the center for the clipping circle
        final android.view.View v = vBig.findViewById(me.ccrama.redditslide.R.id.vote);
        v.post(new java.lang.Runnable() {
            @java.lang.Override
            public void run() {
                v.setBackgroundColor(color);
                v.setVisibility(android.view.View.VISIBLE);
                v.setAlpha(1.0F);
                final int cx = (from.getLeft() + from.getRight()) / 2;
                final int cy = vBig.getHeight() - (from.getHeight() / 2);// from.getRight() - ( from.getWidth()/ 2);

                // get the final radius for the clipping circle
                int dx = java.lang.Math.max(cx, vBig.getWidth() - cx);
                int dy = java.lang.Math.max(cy, vBig.getHeight() - cy);
                final float finalRadius = ((float) (java.lang.Math.hypot(dx, dy)));
                try {
                    io.codetail.animation.SupportAnimator animator = io.codetail.animation.ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, finalRadius);
                    animator.setInterpolator(new android.support.v4.view.animation.FastOutSlowInInterpolator());
                    animator.setDuration(250);
                    animator.start();
                    v.postDelayed(new java.lang.Runnable() {
                        @java.lang.Override
                        public void run() {
                            android.animation.ObjectAnimator animator2 = android.animation.ObjectAnimator.ofFloat(v, android.view.View.ALPHA, 1.0F, 0.0F);
                            animator2.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
                            animator2.setDuration(450);
                            animator2.addListener(new android.animation.Animator.AnimatorListener() {
                                @java.lang.Override
                                public void onAnimationStart(android.animation.Animator animation) {
                                }

                                @java.lang.Override
                                public void onAnimationEnd(android.animation.Animator animation) {
                                    v.setVisibility(android.view.View.GONE);
                                }

                                @java.lang.Override
                                public void onAnimationCancel(android.animation.Animator animation) {
                                }

                                @java.lang.Override
                                public void onAnimationRepeat(android.animation.Animator animation) {
                                }
                            });
                            animator2.start();
                        }
                    }, 450);
                } catch (java.lang.Exception e) {
                    v.setVisibility(android.view.View.GONE);
                }
            }
        });
    }
}