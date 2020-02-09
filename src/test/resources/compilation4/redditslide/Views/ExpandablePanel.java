package me.ccrama.redditslide.Views;
import me.ccrama.redditslide.R;
public class ExpandablePanel extends android.widget.RelativeLayout {
    private static final int DEFAULT_ANIM_DURATION = 500;

    private final int mHandleId;

    private final int mContentContainerId;

    private final int mContentId;

    private android.view.View mHandle;

    private android.view.View mContentContainer;

    private android.view.View mContent;

    private boolean mExpanded = false;

    private boolean mFirstOpen = true;

    private int mCollapsedHeight;

    private int mContentHeight;

    private int mContentWidth;

    private int mAnimationDuration = 0;

    private me.ccrama.redditslide.Views.ExpandablePanel.OnExpandListener mListener;

    public ExpandablePanel(android.content.Context context) {
        this(context, null);
    }

    public ExpandablePanel(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        android.content.res.TypedArray a = context.obtainStyledAttributes(attrs, me.ccrama.redditslide.R.styleable.ExpandablePanel, 0, 0);
        mAnimationDuration = a.getInteger(me.ccrama.redditslide.R.styleable.ExpandablePanel_animationDuration, me.ccrama.redditslide.Views.ExpandablePanel.DEFAULT_ANIM_DURATION);
        int handleId = a.getResourceId(me.ccrama.redditslide.R.styleable.ExpandablePanel_handle, 0);
        if (handleId == 0) {
            throw new java.lang.IllegalArgumentException("The handle attribute is required and must refer to a valid child.");
        }
        int contentContainerId = a.getResourceId(me.ccrama.redditslide.R.styleable.ExpandablePanel_contentContainer, 0);
        if (contentContainerId == 0) {
            throw new java.lang.IllegalArgumentException("The content attribute is required and must refer to a valid child.");
        }
        int contentId = a.getResourceId(me.ccrama.redditslide.R.styleable.ExpandablePanel_content, 0);
        if (contentId == 0) {
            throw new java.lang.IllegalArgumentException("The content attribute is required and must refer to a valid child.");
        }
        mHandleId = handleId;
        mContentContainerId = contentContainerId;
        mContentId = contentId;
        a.recycle();
    }

    public void setOnExpandListener(me.ccrama.redditslide.Views.ExpandablePanel.OnExpandListener listener) {
        mListener = listener;
    }

    public void setAnimationDuration(int animationDuration) {
        mAnimationDuration = animationDuration;
    }

    @java.lang.Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mHandle = findViewById(mHandleId);
        if (mHandle == null) {
            throw new java.lang.IllegalArgumentException("The handle attribute is must refer to an existing child.");
        }
        mContentContainer = findViewById(mContentContainerId);
        if (mContentContainer == null) {
            throw new java.lang.IllegalArgumentException("The content container attribute must refer to an existing child.");
        }
        mContent = findViewById(mContentId);
        if (mContentContainer == null) {
            throw new java.lang.IllegalArgumentException("The content attribute must refer to an existing child.");
        }
        mContent.setVisibility(android.view.View.INVISIBLE);
        mHandle.setOnClickListener(new me.ccrama.redditslide.Views.ExpandablePanel.PanelToggler());
    }

    @java.lang.Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mContentContainer.measure(android.view.View.MeasureSpec.UNSPECIFIED, android.view.View.MeasureSpec.UNSPECIFIED);
        mHandle.measure(android.view.View.MeasureSpec.UNSPECIFIED, heightMeasureSpec);
        mCollapsedHeight = mHandle.getMeasuredHeight();
        mContentWidth = mContentContainer.getMeasuredWidth();
        mContentHeight = mContentContainer.getMeasuredHeight();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mFirstOpen) {
            mContentContainer.getLayoutParams().width = 0;
            mContentContainer.getLayoutParams().height = mCollapsedHeight;
            mFirstOpen = false;
        }
        int width = (mHandle.getMeasuredWidth() + mContentContainer.getMeasuredWidth()) + mContentContainer.getPaddingRight();
        int height = mContentContainer.getMeasuredHeight() + mContentContainer.getPaddingBottom();
        setMeasuredDimension(width, height);
    }

    public interface OnExpandListener {
        void onExpand(android.view.View handle, android.view.View content);

        void onCollapse(android.view.View handle, android.view.View content);
    }

    private class PanelToggler implements android.view.View.OnClickListener {
        @java.lang.Override
        public void onClick(android.view.View v) {
            android.view.animation.Animation animation;
            if (mExpanded) {
                mContent.setVisibility(android.view.View.INVISIBLE);
                animation = new me.ccrama.redditslide.Views.ExpandablePanel.ExpandAnimation(mContentWidth, 0, mContentHeight, mCollapsedHeight);
                if (mListener != null) {
                    mListener.onCollapse(mHandle, mContentContainer);
                }
            } else {
                me.ccrama.redditslide.Views.ExpandablePanel.this.invalidate();
                animation = new me.ccrama.redditslide.Views.ExpandablePanel.ExpandAnimation(0, mContentWidth, mCollapsedHeight, mContentHeight);
                if (mListener != null) {
                    mListener.onExpand(mHandle, mContentContainer);
                }
            }
            animation.setDuration(mAnimationDuration);
            animation.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
                @java.lang.Override
                public void onAnimationStart(android.view.animation.Animation animation) {
                }

                @java.lang.Override
                public void onAnimationRepeat(android.view.animation.Animation animation) {
                }

                @java.lang.Override
                public void onAnimationEnd(android.view.animation.Animation animation) {
                    mExpanded = !mExpanded;
                    if (mExpanded) {
                        mContent.setVisibility(android.view.View.VISIBLE);
                    }
                }
            });
            mContentContainer.startAnimation(animation);
        }
    }

    private class ExpandAnimation extends android.view.animation.Animation {
        private final int mStartWidth;

        private final int mDeltaWidth;

        private final int mStartHeight;

        private final int mDeltaHeight;

        public ExpandAnimation(int startWidth, int endWidth, int startHeight, int endHeight) {
            mStartWidth = startWidth;
            mDeltaWidth = endWidth - startWidth;
            mStartHeight = startHeight;
            mDeltaHeight = endHeight - startHeight;
        }

        @java.lang.Override
        protected void applyTransformation(float interpolatedTime, android.view.animation.Transformation t) {
            android.view.ViewGroup.LayoutParams lp = mContentContainer.getLayoutParams();
            lp.width = ((int) (mStartWidth + (mDeltaWidth * interpolatedTime)));
            lp.height = ((int) (mStartHeight + (mDeltaHeight * interpolatedTime)));
            mContentContainer.setLayoutParams(lp);
        }

        @java.lang.Override
        public boolean willChangeBounds() {
            return true;
        }
    }
}