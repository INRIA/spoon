package me.ccrama.redditslide.Views;
import me.ccrama.redditslide.util.LogUtil;
import java.util.LinkedList;
import me.ccrama.redditslide.R;
import java.util.List;
import me.ccrama.redditslide.SettingValues;
import java.io.File;
class FadeInAnimation extends android.view.animation.AnimationSet {
    private android.view.View animationView;

    private boolean toVisible;

    public FadeInAnimation(android.view.View view, boolean toVisible, long duration) {
        super(false);
        this.toVisible = toVisible;
        this.animationView = view;
        // Creates the Alpha animation for the transition
        float startAlpha = (toVisible) ? 0 : 1;
        float endAlpha = (toVisible) ? 1 : 0;
        android.view.animation.AlphaAnimation alphaAnimation = new android.view.animation.AlphaAnimation(startAlpha, endAlpha);
        alphaAnimation.setDuration(duration);
        addAnimation(alphaAnimation);
        setAnimationListener(new me.ccrama.redditslide.Views.FadeInAnimation.Listener());
    }

    private class Listener implements android.view.animation.Animation.AnimationListener {
        @java.lang.Override
        public void onAnimationStart(android.view.animation.Animation animation) {
            animationView.setVisibility(android.view.View.VISIBLE);
        }

        @java.lang.Override
        public void onAnimationEnd(android.view.animation.Animation animation) {
            animationView.setVisibility(toVisible ? android.view.View.VISIBLE : android.view.View.GONE);
        }

        @java.lang.Override
        public void onAnimationRepeat(android.view.animation.Animation animation) {
            // Purposefully left blank
        }
    }
}