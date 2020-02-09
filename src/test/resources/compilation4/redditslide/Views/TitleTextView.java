package me.ccrama.redditslide.Views;
import me.ccrama.redditslide.SpoilerRobotoTextView;
import me.ccrama.redditslide.Visuals.FontPreferences;
/**
 * Created by carlo_000 on 1/10/2016.
 */
public class TitleTextView extends me.ccrama.redditslide.SpoilerRobotoTextView {
    public TitleTextView(android.content.Context c) {
        super(c);
        if (!isInEditMode()) {
            int type = new me.ccrama.redditslide.Visuals.FontPreferences(getContext()).getFontTypeTitle().getTypeface();
            android.graphics.Typeface typeface;
            if (type >= 0) {
                typeface = com.devspark.robototextview.RobotoTypefaces.obtainTypeface(c, type);
            } else {
                typeface = android.graphics.Typeface.DEFAULT;
            }
            setTypeface(typeface);
        }
    }

    public TitleTextView(android.content.Context c, android.util.AttributeSet attrs) {
        super(c, attrs);
        if (!isInEditMode()) {
            int type = new me.ccrama.redditslide.Visuals.FontPreferences(getContext()).getFontTypeTitle().getTypeface();
            android.graphics.Typeface typeface;
            if (type >= 0) {
                typeface = com.devspark.robototextview.RobotoTypefaces.obtainTypeface(c, type);
            } else {
                typeface = android.graphics.Typeface.DEFAULT;
            }
            setTypeface(typeface);
        }
    }

    public TitleTextView(android.content.Context c, android.util.AttributeSet attrs, int defStyle) {
        super(c, attrs, defStyle);
        if (!isInEditMode()) {
            int type = new me.ccrama.redditslide.Visuals.FontPreferences(getContext()).getFontTypeTitle().getTypeface();
            android.graphics.Typeface typeface;
            if (type >= 0) {
                typeface = com.devspark.robototextview.RobotoTypefaces.obtainTypeface(c, type);
            } else {
                typeface = android.graphics.Typeface.DEFAULT;
            }
            setTypeface(typeface);
        }
    }
}