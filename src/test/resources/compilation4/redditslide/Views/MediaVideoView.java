package me.ccrama.redditslide.Views;
import me.ccrama.redditslide.util.LogUtil;
import java.util.LinkedList;
import me.ccrama.redditslide.R;
import java.util.List;
import me.ccrama.redditslide.SettingValues;
import java.io.File;
/**
 * Created by vishna on 22/07/15.
 */
// 
// VideoView
// 
// 
// Created by Alex Ross on 1/29/13
// Modified to accept a Matrix by Wiseman Designs
// 
public class MediaVideoView extends com.devbrackets.android.exomedia.ui.widget.VideoView {
    private static final java.lang.String LOG_TAG = "VideoView";

    public int number;

    public android.view.View mute;

    com.devbrackets.android.exomedia.listener.OnPreparedListener mOnPreparedListener;

    private int currentBufferPercentage;

    private android.net.Uri uri;

    private android.content.Context mContext;

    // Listeners
    private com.devbrackets.android.exomedia.listener.OnBufferUpdateListener bufferingUpdateListener = new com.devbrackets.android.exomedia.listener.OnBufferUpdateListener() {
        @java.lang.Override
        public void onBufferingUpdate(int percent) {
            currentBufferPercentage = percent;
        }
    };

    private com.devbrackets.android.exomedia.listener.OnPreparedListener preparedListener = new com.devbrackets.android.exomedia.listener.OnPreparedListener() {
        @java.lang.Override
        public void onPrepared() {
            me.ccrama.redditslide.util.LogUtil.v("Video prepared for " + number);
            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared();
            }
            requestLayout();
            invalidate();
            start();
        }
    };

    private com.devbrackets.android.exomedia.listener.OnErrorListener errorListener = new com.devbrackets.android.exomedia.listener.OnErrorListener() {
        @java.lang.Override
        public boolean onError(java.lang.Exception e) {
            android.util.Log.e(me.ccrama.redditslide.Views.MediaVideoView.LOG_TAG, "There was an error during video playback.");
            return true;
        }
    };

    public MediaVideoView(final android.content.Context context) {
        super(context);
        mContext = context;
        initVideoView();
    }

    public MediaVideoView(final android.content.Context context, final android.util.AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initVideoView();
    }

    public MediaVideoView(android.content.Context context, android.util.AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initVideoView();
    }

    @java.lang.Override
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        boolean isKeyCodeSupported = ((((((keyCode != android.view.KeyEvent.KEYCODE_BACK) && (keyCode != android.view.KeyEvent.KEYCODE_VOLUME_UP)) && (keyCode != android.view.KeyEvent.KEYCODE_VOLUME_DOWN)) && (keyCode != android.view.KeyEvent.KEYCODE_VOLUME_MUTE)) && (keyCode != android.view.KeyEvent.KEYCODE_MENU)) && (keyCode != android.view.KeyEvent.KEYCODE_CALL)) && (keyCode != android.view.KeyEvent.KEYCODE_ENDCALL);
        if (isPlaying() && isKeyCodeSupported) {
            if ((keyCode == android.view.KeyEvent.KEYCODE_HEADSETHOOK) || (keyCode == android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)) {
                if (isPlaying()) {
                    pause();
                    getVideoControls().show();
                } else {
                    start();
                    getVideoControls().hide();
                }
                return true;
            } else if (keyCode == android.view.KeyEvent.KEYCODE_MEDIA_PLAY) {
                if (!isPlaying()) {
                    start();
                    getVideoControls().hide();
                }
                return true;
            } else if ((keyCode == android.view.KeyEvent.KEYCODE_MEDIA_STOP) || (keyCode == android.view.KeyEvent.KEYCODE_MEDIA_PAUSE)) {
                if (isPlaying()) {
                    pause();
                    getVideoControls().show();
                }
                return true;
            } else {
                toggleMediaControlsVisiblity();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @java.lang.Override
    public boolean onTrackballEvent(android.view.MotionEvent ev) {
        if (isPlaying()) {
            toggleMediaControlsVisiblity();
        }
        return false;
    }

    @java.lang.Override
    public boolean onTouchEvent(android.view.MotionEvent ev) {
        if (isPlaying() && (ev.getAction() == android.view.MotionEvent.ACTION_UP)) {
            toggleMediaControlsVisiblity();
        }
        return true;
    }

    public void setVideoURI(android.net.Uri _videoURI) {
        uri = _videoURI;
        openVideo();
        requestLayout();
        invalidate();
    }

    public void setVideoPath(java.lang.String path) {
        me.ccrama.redditslide.util.LogUtil.v("Setting video path to: " + path);
        setVideoURI(android.net.Uri.parse(path));
    }

    @java.lang.Override
    public int getBufferPercentage() {
        return currentBufferPercentage;
    }

    public void setOnPreparedListener(com.devbrackets.android.exomedia.listener.OnPreparedListener onPreparedListener) {
        this.mOnPreparedListener = onPreparedListener;
    }

    public void attachMediaControls() {
        setControls(new me.ccrama.redditslide.Views.SlideVideoControls(mContext));
    }

    public void initVideoView() {
        me.ccrama.redditslide.util.LogUtil.v("Initializing video view.");
        setAlpha(0);
        setHandleAudioFocus(false);
        setFocusable(false);
    }

    public void openVideo() {
        if (uri == null) {
            me.ccrama.redditslide.util.LogUtil.v("Cannot open video, uri or surface is null number " + number);
            return;
        }
        animate().alpha(1);
        if (mute != null) {
            if (!me.ccrama.redditslide.SettingValues.isMuted) {
                setVolume(1.0F);
                ((android.widget.ImageView) (mute)).setColorFilter(android.graphics.Color.WHITE, android.graphics.PorterDuff.Mode.SRC_ATOP);
            } else {
                setVolume(0);
                ((android.widget.ImageView) (mute)).setColorFilter(getResources().getColor(me.ccrama.redditslide.R.color.md_red_500), android.graphics.PorterDuff.Mode.SRC_ATOP);
            }
            mute.setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    if (me.ccrama.redditslide.SettingValues.isMuted) {
                        setVolume(1.0F);
                        me.ccrama.redditslide.SettingValues.isMuted = false;
                        me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_MUTE, false).apply();
                        ((android.widget.ImageView) (mute)).setColorFilter(android.graphics.Color.WHITE, android.graphics.PorterDuff.Mode.SRC_ATOP);
                    } else {
                        setVolume(0);
                        me.ccrama.redditslide.SettingValues.isMuted = true;
                        me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_MUTE, true).apply();
                        ((android.widget.ImageView) (mute)).setColorFilter(getResources().getColor(me.ccrama.redditslide.R.color.md_red_500), android.graphics.PorterDuff.Mode.SRC_ATOP);
                    }
                }
            });
        }
        try {
            attachMediaControls();
            setOnBufferUpdateListener(bufferingUpdateListener);
            setOnPreparedListener(preparedListener);
            setOnErrorListener(errorListener);
            setKeepScreenOn(true);
            com.google.android.exoplayer2.upstream.DataSource.Factory dataSourceFactory = new me.ccrama.redditslide.Views.CacheDataSourceFactory(getContext(), (100 * 1024) * 1024, (5 * 1024) * 1024);
            setVideoURI(uri, null);
            audioFocusHelper.abandonFocus();
            me.ccrama.redditslide.util.LogUtil.v("Preparing media player.");
        } catch (java.lang.IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public int resolveAdjustedSize(int desiredSize, int measureSpec) {
        me.ccrama.redditslide.util.LogUtil.v("Resolve called.");
        int result = desiredSize;
        int specMode = android.view.View.MeasureSpec.getMode(measureSpec);
        int specSize = android.view.View.MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case android.view.View.MeasureSpec.UNSPECIFIED :
                /* Parent says we can be as big as we want. Just don't be larger
                than max size imposed on ourselves.
                 */
                result = desiredSize;
                break;
            case android.view.View.MeasureSpec.AT_MOST :
                /* Parent says we can be as big as we want, up to specSize.
                Don't be larger than specSize, and don't be larger than
                the max size imposed on ourselves.
                 */
                result = java.lang.Math.min(desiredSize, specSize);
                break;
            case android.view.View.MeasureSpec.EXACTLY :
                // No choice. Do what we are told.
                result = specSize;
                break;
        }
        return result;
    }

    private void toggleMediaControlsVisiblity() {
        if (getVideoControls() != null) {
            if (getVideoControls().isVisible()) {
                getVideoControls().hide();
            } else {
                getVideoControls().show();
            }
        }
    }
}