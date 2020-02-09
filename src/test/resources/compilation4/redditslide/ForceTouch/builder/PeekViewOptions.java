package me.ccrama.redditslide.ForceTouch.builder;
// endregion
public class PeekViewOptions {
    @android.support.annotation.FloatRange(from = 0.1, to = 0.9)
    private float widthPercent = 0.6F;

    @android.support.annotation.FloatRange(from = 0.1, to = 0.9)
    private float heightPercent = 0.5F;

    // Values should be in DP
    private int absoluteWidth = 0;

    private int absoluteHeight = 0;

    // 0.0 = fully transparent background dim
    // 1.0 = fully opaque (black) background dim
    @android.support.annotation.FloatRange(from = 0, to = 1)
    private float backgroundDim = 0.6F;

    private boolean useFadeAnimation = true;

    private boolean hapticFeedback = true;

    private boolean fullScreenPeek = false;

    private boolean blurBackground = true;

    private int blurOverlayColor = android.graphics.Color.parseColor("#99000000");

    // region setters
    public me.ccrama.redditslide.ForceTouch.builder.PeekViewOptions setWidthPercent(@android.support.annotation.FloatRange(from = 0.1, to = 0.9)
    float widthPercent) {
        this.widthPercent = widthPercent;
        return this;
    }

    public me.ccrama.redditslide.ForceTouch.builder.PeekViewOptions setAbsoluteWidth(int width) {
        this.absoluteWidth = width;
        return this;
    }

    public me.ccrama.redditslide.ForceTouch.builder.PeekViewOptions setAbsoluteHeight(int height) {
        this.absoluteHeight = height;
        return this;
    }

    public me.ccrama.redditslide.ForceTouch.builder.PeekViewOptions setHeightPercent(@android.support.annotation.FloatRange(from = 0.1, to = 0.9)
    float heightPercent) {
        this.heightPercent = heightPercent;
        return this;
    }

    public me.ccrama.redditslide.ForceTouch.builder.PeekViewOptions setBackgroundDim(@android.support.annotation.FloatRange(from = 0, to = 1)
    float backgroundDim) {
        this.backgroundDim = backgroundDim;
        return this;
    }

    public me.ccrama.redditslide.ForceTouch.builder.PeekViewOptions setHapticFeedback(boolean useFeedback) {
        this.hapticFeedback = useFeedback;
        return this;
    }

    public me.ccrama.redditslide.ForceTouch.builder.PeekViewOptions setUseFadeAnimation(boolean useFadeAnimation) {
        this.useFadeAnimation = useFadeAnimation;
        return this;
    }

    public me.ccrama.redditslide.ForceTouch.builder.PeekViewOptions setFullScreenPeek(boolean fullScreenPeek) {
        this.fullScreenPeek = fullScreenPeek;
        return this;
    }

    public me.ccrama.redditslide.ForceTouch.builder.PeekViewOptions setBlurBackground(boolean blur) {
        this.blurBackground = blur;
        return this;
    }

    public me.ccrama.redditslide.ForceTouch.builder.PeekViewOptions setBlurOverlayColor(int color) {
        this.blurOverlayColor = color;
        return this;
    }

    // endregion
    // region getters
    public float getWidthPercent() {
        return widthPercent;
    }

    public float getHeightPercent() {
        return heightPercent;
    }

    public int getAbsoluteWidth() {
        return absoluteWidth;
    }

    public int getAbsoluteHeight() {
        return absoluteHeight;
    }

    public float getBackgroundDim() {
        return backgroundDim;
    }

    public boolean getHapticFeedback() {
        return hapticFeedback;
    }

    public boolean useFadeAnimation() {
        return useFadeAnimation;
    }

    public boolean fullScreenPeek() {
        return fullScreenPeek;
    }

    public boolean shouldBlurBackground() {
        return blurBackground;
    }

    public int getBlurOverlayColor() {
        return blurOverlayColor;
    }
}