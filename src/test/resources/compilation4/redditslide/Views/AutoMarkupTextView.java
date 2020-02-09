package me.ccrama.redditslide.Views;
/**
 * Created by ccrama on 5/5/2015.
 */
public class AutoMarkupTextView extends android.widget.TextView {
    public AutoMarkupTextView(android.content.Context context, android.util.AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public AutoMarkupTextView(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoMarkupTextView(android.content.Context context) {
        super(context);
    }

    public void addText(java.lang.String s) {
        parseLinks(s);
    }

    private void parseLinks(java.lang.String s) {
        setText(s);
        int mask = android.text.util.Linkify.WEB_URLS;
        android.text.util.Linkify.addLinks(this, mask);
        // todo this setMovementMethod(new CommentMovementMethod());
    }
}