package me.ccrama.redditslide.Views;
import java.io.InputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.Map;
/**
 * Created by vishna on 22/07/15.
 */
public class MediaVideoViewOld extends android.view.SurfaceView implements android.widget.MediaController.MediaPlayerControl {
    // all possible internal statfes
    private static final int STATE_ERROR = -1;

    private static final int STATE_IDLE = 0;

    private static final int STATE_PREPARING = 1;

    private static final int STATE_PREPARED = 2;

    private static final int STATE_PLAYING = 3;

    private static final int STATE_PAUSED = 4;

    private static final int STATE_PLAYBACK_COMPLETED = 5;

    private java.lang.String TAG = "VideoView";

    // settable by the client
    private android.net.Uri mUri;

    private java.util.Map<java.lang.String, java.lang.String> mHeaders;

    // mCurrentState is a VideoView object's current state.
    // mTargetState is the state that a method caller intends to reach.
    // For instance, regardless the VideoView object's current state,
    // calling pause() intends to bring the object to a target state
    // of STATE_PAUSED.
    private int mCurrentState = me.ccrama.redditslide.Views.MediaVideoViewOld.STATE_IDLE;

    private int mTargetState = me.ccrama.redditslide.Views.MediaVideoViewOld.STATE_IDLE;

    // All the stuff we need for playing and showing a video
    private android.view.SurfaceHolder mSurfaceHolder = null;

    private android.media.MediaPlayer mMediaPlayer = null;

    private int mAudioSession;

    private int mVideoWidth;

    private int mVideoHeight;

    android.media.MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = new android.media.MediaPlayer.OnVideoSizeChangedListener() {
        public void onVideoSizeChanged(android.media.MediaPlayer mp, int width, int height) {
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            if ((mVideoWidth != 0) && (mVideoHeight != 0)) {
                getHolder().setFixedSize(mVideoWidth, mVideoHeight);
                requestLayout();
            }
        }
    };

    private int mSurfaceWidth;

    private int mSurfaceHeight;

    private android.widget.MediaController mMediaController;

    private android.media.MediaPlayer.OnCompletionListener mOnCompletionListener;

    private android.media.MediaPlayer.OnPreparedListener mOnPreparedListener;

    private int mCurrentBufferPercentage;

    private android.media.MediaPlayer.OnErrorListener mOnErrorListener;

    private android.media.MediaPlayer.OnInfoListener mOnInfoListener;

    private int mSeekWhenPrepared;// recording the seek position while preparing


    private boolean mCanPause;

    private boolean mCanSeekBack;

    private boolean mCanSeekForward;

    android.media.MediaPlayer.OnPreparedListener mPreparedListener = new android.media.MediaPlayer.OnPreparedListener() {
        public void onPrepared(android.media.MediaPlayer mp) {
            mCurrentState = me.ccrama.redditslide.Views.MediaVideoViewOld.STATE_PREPARED;
            // Get the capabilities of the player for this stream
            // Metadata data = mp.getMetadata(MediaPlayer.METADATA_ALL,
            // MediaPlayer.BYPASS_METADATA_FILTER);
            // if (data != null) {
            // mCanPause = !data.has(Metadata.PAUSE_AVAILABLE)
            // || data.getBoolean(Metadata.PAUSE_AVAILABLE);
            // mCanSeekBack = !data.has(Metadata.SEEK_BACKWARD_AVAILABLE)
            // || data.getBoolean(Metadata.SEEK_BACKWARD_AVAILABLE);
            // mCanSeekForward = !data.has(Metadata.SEEK_FORWARD_AVAILABLE)
            // || data.getBoolean(Metadata.SEEK_FORWARD_AVAILABLE);
            // } else {
            mCanPause = mCanSeekBack = mCanSeekForward = true;
            // }
            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(mMediaPlayer);
            }
            if (mMediaController != null) {
                mMediaController.setEnabled(true);
            }
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            int seekToPosition = mSeekWhenPrepared;// mSeekWhenPrepared may be changed after seekTo() call

            if (seekToPosition != 0) {
                seekTo(seekToPosition);
            }
            if ((mVideoWidth != 0) && (mVideoHeight != 0)) {
                // Log.i("@@@@", "video size: " + mVideoWidth +"/"+ mVideoHeight);
                getHolder().setFixedSize(mVideoWidth, mVideoHeight);
                if ((mSurfaceWidth == mVideoWidth) && (mSurfaceHeight == mVideoHeight)) {
                    // We didn't actually change the size (it was already at the size
                    // we need), so we won't get a "surface changed" callback, so
                    // start the video here instead of in the callback.
                    if (mTargetState == me.ccrama.redditslide.Views.MediaVideoViewOld.STATE_PLAYING) {
                        start();
                        if (mMediaController != null) {
                            mMediaController.show();
                        }
                    } else if ((!isPlaying()) && ((seekToPosition != 0) || (getCurrentPosition() > 0))) {
                        if (mMediaController != null) {
                            // Show the media controls when we're paused into a video and make 'em stick.
                            mMediaController.show(0);
                        }
                    }
                }
            } else // We don't know the video size yet, but should start anyway.
            // The video size might be reported to us later.
            if (mTargetState == me.ccrama.redditslide.Views.MediaVideoViewOld.STATE_PLAYING) {
                start();
            }
        }
    };

    private java.util.Vector<android.util.Pair<java.io.InputStream, android.media.MediaFormat>> mPendingSubtitleTracks;

    private android.media.MediaPlayer.OnCompletionListener mCompletionListener = new android.media.MediaPlayer.OnCompletionListener() {
        public void onCompletion(android.media.MediaPlayer mp) {
            mCurrentState = me.ccrama.redditslide.Views.MediaVideoViewOld.STATE_PLAYBACK_COMPLETED;
            mTargetState = me.ccrama.redditslide.Views.MediaVideoViewOld.STATE_PLAYBACK_COMPLETED;
            if (mMediaController != null) {
                mMediaController.hide();
            }
            if (mOnCompletionListener != null) {
                mOnCompletionListener.onCompletion(mMediaPlayer);
            }
        }
    };

    private android.media.MediaPlayer.OnInfoListener mInfoListener = new android.media.MediaPlayer.OnInfoListener() {
        public boolean onInfo(android.media.MediaPlayer mp, int arg1, int arg2) {
            if (mOnInfoListener != null) {
                mOnInfoListener.onInfo(mp, arg1, arg2);
            }
            return true;
        }
    };

    private android.media.MediaPlayer.OnErrorListener mErrorListener = new android.media.MediaPlayer.OnErrorListener() {
        public boolean onError(android.media.MediaPlayer mp, int framework_err, int impl_err) {
            android.util.Log.d(TAG, (("Error: " + framework_err) + ",") + impl_err);
            mCurrentState = me.ccrama.redditslide.Views.MediaVideoViewOld.STATE_ERROR;
            mTargetState = me.ccrama.redditslide.Views.MediaVideoViewOld.STATE_ERROR;
            if (mMediaController != null) {
                mMediaController.hide();
            }
            /* If an error handler has been supplied, use it and finish. */
            if (mOnErrorListener != null) {
                if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err)) {
                    return true;
                }
            }
            /* Otherwise, pop up an error dialog so the user knows that
            something bad has happened. Only try and pop up the dialog
            if we're attached to a window. When we're going away and no
            longer have a window, don't bother showing the user an error.
             */
            // if (getWindowToken() != null) {
            // Resources r = getContext().getResources();
            // int messageId;
            // 
            // if (framework_err == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
            // messageId = com.android.internal.R.string.VideoView_error_text_invalid_progressive_playback;
            // } else {
            // messageId = com.android.internal.R.string.VideoView_error_text_unknown;
            // }
            // 
            // new AlertDialogWrapper.Builder(getContext())
            // .setMessage(messageId)
            // .setPositiveButton(com.android.internal.R.string.VideoView_error_button,
            // new DialogInterface.OnClickListener() {
            // public void onClick(DialogInterface dialog, int whichButton) {
            // /* If we get here, there is no onError listener, so
            // * at least inform them that the video is over.
            // */
            // if (mOnCompletionListener != null) {
            // mOnCompletionListener.onCompletion(mMediaPlayer);
            // }
            // }
            // })
            // .setCancelable(false)
            // .show();
            // }
            return true;
        }
    };

    private android.media.MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new android.media.MediaPlayer.OnBufferingUpdateListener() {
        public void onBufferingUpdate(android.media.MediaPlayer mp, int percent) {
            mCurrentBufferPercentage = percent;
        }
    };

    android.view.SurfaceHolder.Callback mSHCallback = new android.view.SurfaceHolder.Callback() {
        public void surfaceChanged(android.view.SurfaceHolder holder, int format, int w, int h) {
            mSurfaceWidth = w;
            mSurfaceHeight = h;
            boolean isValidState = mTargetState == me.ccrama.redditslide.Views.MediaVideoViewOld.STATE_PLAYING;
            boolean hasValidSize = (mVideoWidth == w) && (mVideoHeight == h);
            if (((mMediaPlayer != null) && isValidState) && hasValidSize) {
                if (mSeekWhenPrepared != 0) {
                    seekTo(mSeekWhenPrepared);
                }
                start();
            }
        }

        public void surfaceCreated(android.view.SurfaceHolder holder) {
            mSurfaceHolder = holder;
            openVideo();
        }

        public void surfaceDestroyed(android.view.SurfaceHolder holder) {
            // after we return from this we can't use the surface any more
            mSurfaceHolder = null;
            if (mMediaController != null)
                mMediaController.hide();

            release(true);
        }
    };

    public MediaVideoViewOld(android.content.Context context) {
        super(context);
        initVideoView();
    }

    public MediaVideoViewOld(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs, 0);
        initVideoView();
    }

    public MediaVideoViewOld(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVideoView();
    }

    @android.annotation.TargetApi(android.os.Build.VERSION_CODES.LOLLIPOP)
    public MediaVideoViewOld(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initVideoView();
    }

    @java.lang.Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Log.i("@@@@", "onMeasure(" + MeasureSpec.toString(widthMeasureSpec) + ", "
        // + MeasureSpec.toString(heightMeasureSpec) + ")");
        int width = android.view.View.getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = android.view.View.getDefaultSize(mVideoHeight, heightMeasureSpec);
        if ((mVideoWidth > 0) && (mVideoHeight > 0)) {
            int widthSpecMode = android.view.View.MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = android.view.View.MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecMode = android.view.View.MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = android.view.View.MeasureSpec.getSize(heightMeasureSpec);
            if ((widthSpecMode == android.view.View.MeasureSpec.EXACTLY) && (heightSpecMode == android.view.View.MeasureSpec.EXACTLY)) {
                // the size is fixed
                width = widthSpecSize;
                height = heightSpecSize;
                // for compatibility, we adjust size based on aspect ratio
                if ((mVideoWidth * height) < (width * mVideoHeight)) {
                    // Log.i("@@@", "image too wide, correcting");
                    width = (height * mVideoWidth) / mVideoHeight;
                } else if ((mVideoWidth * height) > (width * mVideoHeight)) {
                    // Log.i("@@@", "image too tall, correcting");
                    height = (width * mVideoHeight) / mVideoWidth;
                }
            } else if (widthSpecMode == android.view.View.MeasureSpec.EXACTLY) {
                // only the width is fixed, adjust the height to match aspect ratio if possible
                width = widthSpecSize;
                height = (width * mVideoHeight) / mVideoWidth;
                if ((heightSpecMode == android.view.View.MeasureSpec.AT_MOST) && (height > heightSpecSize)) {
                    // couldn't match aspect ratio within the constraints
                    height = heightSpecSize;
                }
            } else if (heightSpecMode == android.view.View.MeasureSpec.EXACTLY) {
                // only the height is fixed, adjust the width to match aspect ratio if possible
                height = heightSpecSize;
                width = (height * mVideoWidth) / mVideoHeight;
                if ((widthSpecMode == android.view.View.MeasureSpec.AT_MOST) && (width > widthSpecSize)) {
                    // couldn't match aspect ratio within the constraints
                    width = widthSpecSize;
                }
            } else {
                // neither the width nor the height are fixed, try to use actual video size
                width = mVideoWidth;
                height = mVideoHeight;
                if ((heightSpecMode == android.view.View.MeasureSpec.AT_MOST) && (height > heightSpecSize)) {
                    // too tall, decrease both width and height
                    height = heightSpecSize;
                    width = (height * mVideoWidth) / mVideoHeight;
                }
                if ((widthSpecMode == android.view.View.MeasureSpec.AT_MOST) && (width > widthSpecSize)) {
                    // too wide, decrease both width and height
                    width = widthSpecSize;
                    height = (width * mVideoHeight) / mVideoWidth;
                }
            }
        } else {
            // no size yet, just adopt the given spec sizes
        }
        setMeasuredDimension(width, height);
    }

    @java.lang.Override
    public void onInitializeAccessibilityEvent(android.view.accessibility.AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(me.ccrama.redditslide.Views.MediaVideoViewOld.class.getName());
    }

    @java.lang.Override
    public void onInitializeAccessibilityNodeInfo(android.view.accessibility.AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(me.ccrama.redditslide.Views.MediaVideoViewOld.class.getName());
    }

    public int resolveAdjustedSize(int desiredSize, int measureSpec) {
        return android.view.View.getDefaultSize(desiredSize, measureSpec);
    }

    private void initVideoView() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        getHolder().addCallback(mSHCallback);
        getHolder().setType(android.view.SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        mPendingSubtitleTracks = new java.util.Vector<>();
        mCurrentState = me.ccrama.redditslide.Views.MediaVideoViewOld.STATE_IDLE;
        mTargetState = me.ccrama.redditslide.Views.MediaVideoViewOld.STATE_IDLE;
    }

    /**
     * Sets video path.
     *
     * @param path
     * 		the path of the video.
     */
    public void setVideoPath(java.lang.String path) {
        setVideoURI(android.net.Uri.parse(path));
    }

    /**
     * Sets video URI.
     *
     * @param uri
     * 		the URI of the video.
     */
    public void setVideoURI(android.net.Uri uri) {
        setVideoURI(uri, null);
    }

    /**
     * Sets video URI using specific headers.
     *
     * @param uri
     * 		the URI of the video.
     * @param headers
     * 		the headers for the URI request.
     * 		Note that the cross domain redirection is allowed by default, but that can be
     * 		changed with key/value pairs through the headers parameter with
     * 		"android-allow-cross-domain-redirect" as the key and "0" or "1" as the value
     * 		to disallow or allow cross domain redirection.
     */
    public void setVideoURI(android.net.Uri uri, java.util.Map<java.lang.String, java.lang.String> headers) {
        mUri = uri;
        mHeaders = headers;
        mSeekWhenPrepared = 0;
        openVideo();
        requestLayout();
        invalidate();
    }

    public void stopPlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = me.ccrama.redditslide.Views.MediaVideoViewOld.STATE_IDLE;
            mTargetState = me.ccrama.redditslide.Views.MediaVideoViewOld.STATE_IDLE;
        }
    }

    private void openVideo() {
        if ((mUri == null) || (mSurfaceHolder == null)) {
            // not ready for playback just yet, will try again later
            return;
        }
        // AudioManager am = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        // am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        // we shouldn't clear the target state, because somebody might have
        // called start() previously
        release(false);
        try {
            mMediaPlayer = new android.media.MediaPlayer();
            // TODO: create SubtitleController in MediaPlayer, but we need
            // a context for the subtitle renderers
            final android.content.Context context = getContext();
            if (mAudioSession != 0) {
                mMediaPlayer.setAudioSessionId(mAudioSession);
            } else {
                mAudioSession = mMediaPlayer.getAudioSessionId();
            }
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mCurrentBufferPercentage = 0;
            mMediaPlayer.setDataSource(getContext(), mUri, mHeaders);
            mMediaPlayer.setDisplay(mSurfaceHolder);
            mMediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync();
            // we don't set the target state here either, but preserve the
            // target state that was there before.
            mCurrentState = me.ccrama.redditslide.Views.MediaVideoViewOld.STATE_PREPARING;
            attachMediaController();
        } catch (java.io.IOException ex) {
            android.util.Log.w(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = me.ccrama.redditslide.Views.MediaVideoViewOld.STATE_ERROR;
            mTargetState = me.ccrama.redditslide.Views.MediaVideoViewOld.STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, android.media.MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
        } catch (java.lang.IllegalArgumentException ex) {
            android.util.Log.w(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = me.ccrama.redditslide.Views.MediaVideoViewOld.STATE_ERROR;
            mTargetState = me.ccrama.redditslide.Views.MediaVideoViewOld.STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, android.media.MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
        } finally {
            mPendingSubtitleTracks.clear();
        }
    }

    public void setMediaController(android.widget.MediaController controller) {
        if (mMediaController != null) {
            mMediaController.hide();
        }
        mMediaController = controller;
        attachMediaController();
    }

    private void attachMediaController() {
        if ((mMediaPlayer != null) && (mMediaController != null)) {
            mMediaController.setMediaPlayer(this);
            android.view.View anchorView = (this.getParent() instanceof android.view.View) ? ((android.view.View) (this.getParent())) : this;
            mMediaController.setAnchorView(anchorView);
            mMediaController.setEnabled(isInPlaybackState());
        }
    }

    /**
     * Register a callback to be invoked when the media file
     * is loaded and ready to go.
     *
     * @param l
     * 		The callback that will be run
     */
    public void setOnPreparedListener(android.media.MediaPlayer.OnPreparedListener l) {
        mOnPreparedListener = l;
    }

    /**
     * Register a callback to be invoked when the end of a media file
     * has been reached during playback.
     *
     * @param l
     * 		The callback that will be run
     */
    public void setOnCompletionListener(android.media.MediaPlayer.OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    /**
     * Register a callback to be invoked when an error occurs
     * during playback or setup.  If no listener is specified,
     * or if the listener returned false, VideoView will inform
     * the user of any errors.
     *
     * @param l
     * 		The callback that will be run
     */
    public void setOnErrorListener(android.media.MediaPlayer.OnErrorListener l) {
        mOnErrorListener = l;
    }

    /**
     * Register a callback to be invoked when an informational event
     * occurs during playback or setup.
     *
     * @param l
     * 		The callback that will be run
     */
    public void setOnInfoListener(android.media.MediaPlayer.OnInfoListener l) {
        mOnInfoListener = l;
    }

    /* release the media player in any state */
    private void release(boolean cleartargetstate) {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mPendingSubtitleTracks.clear();
            mCurrentState = me.ccrama.redditslide.Views.MediaVideoViewOld.STATE_IDLE;
            if (cleartargetstate) {
                mTargetState = me.ccrama.redditslide.Views.MediaVideoViewOld.STATE_IDLE;
            }
        }
    }

    @java.lang.Override
    public boolean onTouchEvent(android.view.MotionEvent ev) {
        if (isInPlaybackState() && (mMediaController != null)) {
            toggleMediaControlsVisiblity();
        }
        return false;
    }

    @java.lang.Override
    public boolean onTrackballEvent(android.view.MotionEvent ev) {
        if (isInPlaybackState() && (mMediaController != null)) {
            toggleMediaControlsVisiblity();
        }
        return false;
    }

    @java.lang.Override
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        boolean isKeyCodeSupported = ((((((keyCode != android.view.KeyEvent.KEYCODE_BACK) && (keyCode != android.view.KeyEvent.KEYCODE_VOLUME_UP)) && (keyCode != android.view.KeyEvent.KEYCODE_VOLUME_DOWN)) && (keyCode != android.view.KeyEvent.KEYCODE_VOLUME_MUTE)) && (keyCode != android.view.KeyEvent.KEYCODE_MENU)) && (keyCode != android.view.KeyEvent.KEYCODE_CALL)) && (keyCode != android.view.KeyEvent.KEYCODE_ENDCALL);
        if ((isInPlaybackState() && isKeyCodeSupported) && (mMediaController != null)) {
            if ((keyCode == android.view.KeyEvent.KEYCODE_HEADSETHOOK) || (keyCode == android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)) {
                if (mMediaPlayer.isPlaying()) {
                    pause();
                    mMediaController.show();
                } else {
                    start();
                    mMediaController.hide();
                }
                return true;
            } else if (keyCode == android.view.KeyEvent.KEYCODE_MEDIA_PLAY) {
                if (!mMediaPlayer.isPlaying()) {
                    start();
                    mMediaController.hide();
                }
                return true;
            } else if ((keyCode == android.view.KeyEvent.KEYCODE_MEDIA_STOP) || (keyCode == android.view.KeyEvent.KEYCODE_MEDIA_PAUSE)) {
                if (mMediaPlayer.isPlaying()) {
                    pause();
                    mMediaController.show();
                }
                return true;
            } else {
                toggleMediaControlsVisiblity();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void toggleMediaControlsVisiblity() {
        if (mMediaController.isShowing()) {
            mMediaController.hide();
        } else {
            mMediaController.show();
        }
    }

    @java.lang.Override
    public void start() {
        if (isInPlaybackState()) {
            mMediaPlayer.start();
            mCurrentState = me.ccrama.redditslide.Views.MediaVideoViewOld.STATE_PLAYING;
        }
        mTargetState = me.ccrama.redditslide.Views.MediaVideoViewOld.STATE_PLAYING;
    }

    @java.lang.Override
    public void pause() {
        if (isInPlaybackState()) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mCurrentState = me.ccrama.redditslide.Views.MediaVideoViewOld.STATE_PAUSED;
            }
        }
        mTargetState = me.ccrama.redditslide.Views.MediaVideoViewOld.STATE_PAUSED;
    }

    public void suspend() {
        release(false);
    }

    public void resume() {
        openVideo();
    }

    @java.lang.Override
    public int getDuration() {
        if (isInPlaybackState()) {
            return mMediaPlayer.getDuration();
        }
        return -1;
    }

    @java.lang.Override
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @java.lang.Override
    public void seekTo(int msec) {
        if (isInPlaybackState()) {
            mMediaPlayer.seekTo(msec);
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = msec;
        }
    }

    @java.lang.Override
    public boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    @java.lang.Override
    public int getBufferPercentage() {
        if (mMediaPlayer != null) {
            return mCurrentBufferPercentage;
        }
        return 0;
    }

    private boolean isInPlaybackState() {
        return (((mMediaPlayer != null) && (mCurrentState != me.ccrama.redditslide.Views.MediaVideoViewOld.STATE_ERROR)) && (mCurrentState != me.ccrama.redditslide.Views.MediaVideoViewOld.STATE_IDLE)) && (mCurrentState != me.ccrama.redditslide.Views.MediaVideoViewOld.STATE_PREPARING);
    }

    @java.lang.Override
    public boolean canPause() {
        return mCanPause;
    }

    @java.lang.Override
    public boolean canSeekBackward() {
        return mCanSeekBack;
    }

    @java.lang.Override
    public boolean canSeekForward() {
        return mCanSeekForward;
    }

    @java.lang.Override
    public int getAudioSessionId() {
        if (mAudioSession == 0) {
            android.media.MediaPlayer foo = new android.media.MediaPlayer();
            mAudioSession = foo.getAudioSessionId();
            foo.release();
        }
        return mAudioSession;
    }

    @java.lang.Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // if (mSubtitleWidget != null) {
        // mSubtitleWidget.onAttachedToWindow();
        // }
    }

    @java.lang.Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // if (mSubtitleWidget != null) {
        // mSubtitleWidget.onDetachedFromWindow();
        // }
    }

    @java.lang.Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // if (mSubtitleWidget != null) {
        // measureAndLayoutSubtitleWidget();
        // }
    }

    @java.lang.Override
    public void draw(android.graphics.Canvas canvas) {
        super.draw(canvas);
        // if (mSubtitleWidget != null) {
        // final int saveCount = canvas.save();
        // canvas.translate(getPaddingLeft(), getPaddingTop());
        // mSubtitleWidget.draw(canvas);
        // canvas.restoreToCount(saveCount);
        // }
    }
}