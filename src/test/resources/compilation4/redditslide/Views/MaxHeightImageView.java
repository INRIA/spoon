package me.ccrama.redditslide.Views;
/**
 * Created by Carlos on 6/2/2016.
 */
public class MaxHeightImageView extends android.widget.ImageView {
    public MaxHeightImageView(android.content.Context context) {
        super(context);
    }

    public MaxHeightImageView(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    public MaxHeightImageView(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MaxHeightImageView(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public static final int maxHeight = 3200;

    @java.lang.Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int hSize = android.view.View.MeasureSpec.getSize(heightMeasureSpec);
        int hMode = android.view.View.MeasureSpec.getMode(heightMeasureSpec);
        switch (hMode) {
            case android.view.View.MeasureSpec.AT_MOST :
                heightMeasureSpec = android.view.View.MeasureSpec.makeMeasureSpec(java.lang.Math.min(hSize, me.ccrama.redditslide.Views.MaxHeightImageView.maxHeight), android.view.View.MeasureSpec.AT_MOST);
                break;
            case android.view.View.MeasureSpec.UNSPECIFIED :
                heightMeasureSpec = android.view.View.MeasureSpec.makeMeasureSpec(me.ccrama.redditslide.Views.MaxHeightImageView.maxHeight, android.view.View.MeasureSpec.AT_MOST);
                break;
            case android.view.View.MeasureSpec.EXACTLY :
                heightMeasureSpec = android.view.View.MeasureSpec.makeMeasureSpec(java.lang.Math.min(hSize, me.ccrama.redditslide.Views.MaxHeightImageView.maxHeight), android.view.View.MeasureSpec.EXACTLY);
                break;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}