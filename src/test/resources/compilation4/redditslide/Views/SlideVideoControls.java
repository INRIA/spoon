package me.ccrama.redditslide.Views;
import me.ccrama.redditslide.util.LogUtil;
import java.util.LinkedList;
import me.ccrama.redditslide.R;
import java.util.List;
import me.ccrama.redditslide.SettingValues;
import java.io.File;
class SlideVideoControls extends com.devbrackets.android.exomedia.ui.widget.VideoControls {
    protected android.widget.SeekBar seekBar;

    protected android.widget.LinearLayout extraViewsContainer;

    protected boolean userInteracting = false;

    public SlideVideoControls(android.content.Context context) {
        super(context);
    }

    public SlideVideoControls(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    public SlideVideoControls(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @android.annotation.TargetApi(android.os.Build.VERSION_CODES.LOLLIPOP)
    public SlideVideoControls(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @java.lang.Override
    public void setPosition(@android.support.annotation.IntRange(from = 0)
    long position) {
        currentTimeTextView.setText(com.devbrackets.android.exomedia.util.TimeFormatUtil.formatMs(position));
        seekBar.setProgress(((int) (position)));
    }

    @java.lang.Override
    public void setDuration(@android.support.annotation.IntRange(from = 0)
    long duration) {
        if (duration != seekBar.getMax()) {
            endTimeTextView.setText(com.devbrackets.android.exomedia.util.TimeFormatUtil.formatMs(duration));
            seekBar.setMax(((int) (duration)));
        }
    }

    @java.lang.Override
    public void updateProgress(@android.support.annotation.IntRange(from = 0)
    long position, @android.support.annotation.IntRange(from = 0)
    long duration, @android.support.annotation.IntRange(from = 0, to = 100)
    int bufferPercent) {
        if (!userInteracting) {
            seekBar.setSecondaryProgress(((int) (seekBar.getMax() * (((float) (bufferPercent)) / 100))));
            seekBar.setProgress(((int) (position)));
            currentTimeTextView.setText(com.devbrackets.android.exomedia.util.TimeFormatUtil.formatMs(position));
        }
    }

    @java.lang.Override
    protected int getLayoutResource() {
        return me.ccrama.redditslide.R.layout.media_controls;
    }

    @java.lang.Override
    protected void animateVisibility(boolean toVisible) {
        if (isVisible == toVisible) {
            return;
        }
        if ((!hideEmptyTextContainer) || (!isTextContainerEmpty())) {
            textContainer.startAnimation(new me.ccrama.redditslide.Views.FadeInAnimation(textContainer, toVisible, 100));
        }
        if (!isLoading) {
            controlsContainer.startAnimation(new me.ccrama.redditslide.Views.FadeInAnimation(controlsContainer, toVisible, 100));
        }
        isVisible = toVisible;
        onVisibilityChanged();
    }

    @java.lang.Override
    protected void updateTextContainerVisibility() {
        if (!isVisible) {
            return;
        }
        boolean emptyText = isTextContainerEmpty();
        if ((hideEmptyTextContainer && emptyText) && (textContainer.getVisibility() == android.view.View.VISIBLE)) {
            textContainer.clearAnimation();
            textContainer.startAnimation(new me.ccrama.redditslide.Views.FadeInAnimation(textContainer, false, com.devbrackets.android.exomedia.ui.widget.VideoControls.CONTROL_VISIBILITY_ANIMATION_LENGTH));
        } else if (((!hideEmptyTextContainer) || (!emptyText)) && (textContainer.getVisibility() != android.view.View.VISIBLE)) {
            textContainer.clearAnimation();
            textContainer.startAnimation(new me.ccrama.redditslide.Views.FadeInAnimation(textContainer, true, com.devbrackets.android.exomedia.ui.widget.VideoControls.CONTROL_VISIBILITY_ANIMATION_LENGTH));
        }
    }

    @java.lang.Override
    public void showLoading(boolean initialLoad) {
        if (isLoading) {
            return;
        }
        isLoading = true;
        loadingProgressBar.setVisibility(android.view.View.GONE);
        if (initialLoad) {
            controlsContainer.setVisibility(android.view.View.GONE);
        } else {
            playPauseButton.setEnabled(false);
            previousButton.setEnabled(false);
            nextButton.setEnabled(false);
        }
    }

    @java.lang.Override
    public void finishLoading() {
        if (!isLoading) {
            return;
        }
        isLoading = false;
        loadingProgressBar.setVisibility(android.view.View.GONE);
        playPauseButton.setEnabled(true);
        previousButton.setEnabled(true);
        nextButton.setEnabled(true);
        updatePlaybackState(true);
        hide();
    }

    @java.lang.Override
    public void updatePlaybackState(boolean isPlaying) {
        updatePlayPauseImage(isPlaying);
        progressPollRepeater.start();
    }

    @java.lang.Override
    public void addExtraView(@android.support.annotation.NonNull
    android.view.View view) {
        extraViewsContainer.addView(view);
    }

    @java.lang.Override
    public void removeExtraView(@android.support.annotation.NonNull
    android.view.View view) {
        extraViewsContainer.removeView(view);
    }

    @android.support.annotation.NonNull
    @java.lang.Override
    public java.util.List<android.view.View> getExtraViews() {
        int childCount = extraViewsContainer.getChildCount();
        if (childCount <= 0) {
            return super.getExtraViews();
        }
        // Retrieves the layouts children
        java.util.List<android.view.View> children = new java.util.LinkedList<>();
        for (int i = 0; i < childCount; i++) {
            children.add(extraViewsContainer.getChildAt(i));
        }
        return children;
    }

    @java.lang.Override
    public void show() {
        controlsContainer.setVisibility(android.view.View.VISIBLE);
        super.show();
    }

    @java.lang.Override
    public void hide() {
        super.hide();
        controlsContainer.setVisibility(android.view.View.GONE);
    }

    @java.lang.Override
    public void hideDelayed(long delay) {
        hideDelay = delay;
        me.ccrama.redditslide.util.LogUtil.v("Hiding delayed");
        if (((delay < 0) || (!canViewHide)) || isLoading) {
            return;
        }
        // If the user is interacting with controls we don't want to start the delayed hide yet
        if (!userInteracting) {
            visibilityHandler.postDelayed(new java.lang.Runnable() {
                @java.lang.Override
                public void run() {
                    animateVisibility(false);
                }
            }, delay);
        }
    }

    @java.lang.Override
    protected void retrieveViews() {
        super.retrieveViews();
        seekBar = findViewById(com.devbrackets.android.exomedia.R.id.exomedia_controls_video_seek);
        extraViewsContainer = findViewById(com.devbrackets.android.exomedia.R.id.exomedia_controls_extra_container);
    }

    @java.lang.Override
    protected void registerListeners() {
        super.registerListeners();
        seekBar.setOnSeekBarChangeListener(new me.ccrama.redditslide.Views.SlideVideoControls.SeekBarChanged());
    }

    /**
     * Listens to the seek bar change events and correctly handles the changes
     */
    protected class SeekBarChanged implements android.widget.SeekBar.OnSeekBarChangeListener {
        private long seekToTime;

        @java.lang.Override
        public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {
                return;
            }
            seekToTime = progress;
            if (currentTimeTextView != null) {
                currentTimeTextView.setText(com.devbrackets.android.exomedia.util.TimeFormatUtil.formatMs(seekToTime));
            }
        }

        @java.lang.Override
        public void onStartTrackingTouch(android.widget.SeekBar seekBar) {
            userInteracting = true;
            if ((seekListener == null) || (!seekListener.onSeekStarted())) {
                internalListener.onSeekStarted();
            }
        }

        @java.lang.Override
        public void onStopTrackingTouch(android.widget.SeekBar seekBar) {
            userInteracting = false;
            if ((seekListener == null) || (!seekListener.onSeekEnded(seekToTime))) {
                internalListener.onSeekEnded(seekToTime);
            }
        }
    }
}