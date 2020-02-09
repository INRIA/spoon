package me.ccrama.redditslide.Views;
import me.ccrama.redditslide.SpoilerRobotoTextView;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Visuals.FontPreferences;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.ColorPreferences;
import java.util.List;
/**
 * Class that provides methods to help bind submissions with
 * multiple blocks of text.
 */
public class CommentOverflow extends android.widget.LinearLayout {
    private me.ccrama.redditslide.ColorPreferences colorPreferences;

    private android.graphics.Typeface typeface = null;

    private int textColor;

    private int fontSize;

    private static final android.view.ViewGroup.MarginLayoutParams COLUMN_PARAMS;

    private static final android.view.ViewGroup.MarginLayoutParams MARGIN_PARAMS;

    private static final android.view.ViewGroup.MarginLayoutParams HR_PARAMS;

    static {
        me.ccrama.redditslide.Views.CommentOverflow.COLUMN_PARAMS = new android.widget.TableRow.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        me.ccrama.redditslide.Views.CommentOverflow.COLUMN_PARAMS.setMargins(0, 0, 32, 0);
        me.ccrama.redditslide.Views.CommentOverflow.MARGIN_PARAMS = new android.widget.LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        me.ccrama.redditslide.Views.CommentOverflow.MARGIN_PARAMS.setMargins(0, 16, 0, 16);
        me.ccrama.redditslide.Views.CommentOverflow.HR_PARAMS = new android.widget.LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, me.ccrama.redditslide.Reddit.dpToPxVertical(2));
        me.ccrama.redditslide.Views.CommentOverflow.HR_PARAMS.setMargins(0, 16, 0, 16);
    }

    public CommentOverflow(android.content.Context context) {
        super(context);
        init(context);
    }

    public CommentOverflow(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CommentOverflow(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(android.content.Context context) {
        colorPreferences = new me.ccrama.redditslide.ColorPreferences(context);
    }

    /**
     * Set the text for the corresponding views.
     *
     * @param blocks
     * 		list of all blocks to be set
     * @param subreddit
     * 		
     */
    public void setViews(java.util.List<java.lang.String> blocks, java.lang.String subreddit) {
        setViews(blocks, subreddit, null, null);
    }

    /**
     * Set the text for the corresponding views.
     *
     * @param blocks
     * 		list of all blocks to be set
     * @param subreddit
     * 		
     */
    public void setViews(java.util.List<java.lang.String> blocks, java.lang.String subreddit, android.view.View.OnClickListener click, android.view.View.OnLongClickListener longClick) {
        android.content.Context context = getContext();
        int type = new me.ccrama.redditslide.Visuals.FontPreferences(context).getFontTypeComment().getTypeface();
        if (type >= 0) {
            typeface = com.devspark.robototextview.RobotoTypefaces.obtainTypeface(context, type);
        } else {
            typeface = android.graphics.Typeface.DEFAULT;
        }
        android.util.TypedValue typedValue = new android.util.TypedValue();
        android.content.res.Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(me.ccrama.redditslide.R.attr.fontColor, typedValue, true);
        textColor = typedValue.data;
        android.util.TypedValue fontSizeTypedValue = new android.util.TypedValue();
        theme.resolveAttribute(me.ccrama.redditslide.R.attr.font_commentbody, fontSizeTypedValue, true);
        android.content.res.TypedArray a = context.obtainStyledAttributes(null, new int[]{ me.ccrama.redditslide.R.attr.font_commentbody }, me.ccrama.redditslide.R.attr.font_commentbody, new me.ccrama.redditslide.Visuals.FontPreferences(context).getCommentFontStyle().getResId());
        fontSize = a.getDimensionPixelSize(0, -1);
        a.recycle();
        removeAllViews();
        if (!blocks.isEmpty()) {
            setVisibility(android.view.View.VISIBLE);
        }
        for (java.lang.String block : blocks) {
            if (block.startsWith("<table>")) {
                android.widget.HorizontalScrollView scrollView = new android.widget.HorizontalScrollView(context);
                scrollView.setScrollbarFadingEnabled(false);
                android.widget.TableLayout table = formatTable(block, subreddit, click, longClick);
                scrollView.setLayoutParams(me.ccrama.redditslide.Views.CommentOverflow.MARGIN_PARAMS);
                table.setPaddingRelative(0, 0, 0, me.ccrama.redditslide.Reddit.dpToPxVertical(10));
                scrollView.addView(table);
                addView(scrollView);
            } else if (block.equals("<hr/>")) {
                android.view.View line = new android.view.View(context);
                line.setLayoutParams(me.ccrama.redditslide.Views.CommentOverflow.HR_PARAMS);
                line.setBackgroundColor(textColor);
                line.setAlpha(0.6F);
                addView(line);
            } else if (block.startsWith("<pre>")) {
                android.widget.HorizontalScrollView scrollView = new android.widget.HorizontalScrollView(context);
                scrollView.setScrollbarFadingEnabled(false);
                me.ccrama.redditslide.SpoilerRobotoTextView newTextView = new me.ccrama.redditslide.SpoilerRobotoTextView(context);
                newTextView.setTextHtml(block, subreddit);
                setStyle(newTextView, subreddit);
                scrollView.setLayoutParams(me.ccrama.redditslide.Views.CommentOverflow.MARGIN_PARAMS);
                newTextView.setPaddingRelative(0, 0, 0, me.ccrama.redditslide.Reddit.dpToPxVertical(10));
                scrollView.addView(newTextView);
                if (click != null)
                    newTextView.setOnClickListener(click);

                if (longClick != null)
                    newTextView.setOnLongClickListener(longClick);

                addView(scrollView);
            } else {
                me.ccrama.redditslide.SpoilerRobotoTextView newTextView = new me.ccrama.redditslide.SpoilerRobotoTextView(context);
                newTextView.setTextHtml(block, subreddit);
                setStyle(newTextView, subreddit);
                newTextView.setLayoutParams(me.ccrama.redditslide.Views.CommentOverflow.MARGIN_PARAMS);
                if (click != null)
                    newTextView.setOnClickListener(click);

                if (longClick != null)
                    newTextView.setOnLongClickListener(longClick);

                addView(newTextView);
            }
        }
    }

    /* todo: possibly fix tapping issues, better method required (this disables scrolling the HorizontalScrollView)
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
    super.dispatchTouchEvent(event);
    return false;
    }
     */
    private android.widget.TableLayout formatTable(java.lang.String text, java.lang.String subreddit) {
        return formatTable(text, subreddit, null, null);
    }

    private android.widget.TableLayout formatTable(java.lang.String text, java.lang.String subreddit, android.view.View.OnClickListener click, android.view.View.OnLongClickListener longClick) {
        android.widget.TableRow.LayoutParams rowParams = new android.widget.TableRow.LayoutParams(android.widget.TableRow.LayoutParams.WRAP_CONTENT, android.widget.TableRow.LayoutParams.WRAP_CONTENT);
        android.content.Context context = getContext();
        android.widget.TableLayout table = new android.widget.TableLayout(context);
        android.widget.TableLayout.LayoutParams params = new android.widget.TableLayout.LayoutParams(android.widget.TableLayout.LayoutParams.WRAP_CONTENT, android.widget.TableLayout.LayoutParams.WRAP_CONTENT);
        table.setLayoutParams(params);
        final java.lang.String tableStart = "<table>";
        final java.lang.String tableEnd = "</table>";
        final java.lang.String tableHeadStart = "<thead>";
        final java.lang.String tableHeadEnd = "</thead>";
        final java.lang.String tableRowStart = "<tr>";
        final java.lang.String tableRowEnd = "</tr>";
        final java.lang.String tableColumnStart = "<td>";
        final java.lang.String tableColumnEnd = "</td>";
        final java.lang.String tableColumnStartLeft = "<td align=\"left\">";
        final java.lang.String tableColumnStartRight = "<td align=\"right\">";
        final java.lang.String tableColumnStartCenter = "<td align=\"center\">";
        final java.lang.String tableHeaderStart = "<th>";
        final java.lang.String tableHeaderStartLeft = "<th align=\"left\">";
        final java.lang.String tableHeaderStartRight = "<th align=\"right\">";
        final java.lang.String tableHeaderStartCenter = "<th align=\"center\">";
        final java.lang.String tableHeaderEnd = "</th>";
        int i = 0;
        int columnStart = 0;
        int columnEnd;
        int gravity = android.view.Gravity.START;
        boolean columnStarted = false;
        android.widget.TableRow row = null;
        while (i < text.length()) {
            if (text.charAt(i) != '<') {
                // quick check otherwise it falls through to else
                i += 1;
            } else if (text.subSequence(i, i + tableStart.length()).toString().equals(tableStart)) {
                i += tableStart.length();
            } else if (text.subSequence(i, i + tableHeadStart.length()).toString().equals(tableHeadStart)) {
                i += tableHeadStart.length();
            } else if (text.subSequence(i, i + tableRowStart.length()).toString().equals(tableRowStart)) {
                row = new android.widget.TableRow(context);
                row.setLayoutParams(rowParams);
                i += tableRowStart.length();
            } else if (text.subSequence(i, i + tableRowEnd.length()).toString().equals(tableRowEnd)) {
                table.addView(row);
                i += tableRowEnd.length();
            } else if (text.subSequence(i, i + tableEnd.length()).toString().equals(tableEnd)) {
                i += tableEnd.length();
            } else if (text.subSequence(i, i + tableHeadEnd.length()).toString().equals(tableHeadEnd)) {
                i += tableHeadEnd.length();
            } else if (((!columnStarted) && ((i + tableColumnStart.length()) < text.length())) && (text.subSequence(i, i + tableColumnStart.length()).toString().equals(tableColumnStart) || text.subSequence(i, i + tableHeaderStart.length()).toString().equals(tableHeaderStart))) {
                columnStarted = true;
                gravity = android.view.Gravity.START;
                i += tableColumnStart.length();
                columnStart = i;
            } else if (((!columnStarted) && ((i + tableColumnStartRight.length()) < text.length())) && (text.subSequence(i, i + tableColumnStartRight.length()).toString().equals(tableColumnStartRight) || text.subSequence(i, i + tableHeaderStartRight.length()).toString().equals(tableHeaderStartRight))) {
                columnStarted = true;
                gravity = android.view.Gravity.END;
                i += tableColumnStartRight.length();
                columnStart = i;
            } else if (((!columnStarted) && ((i + tableColumnStartCenter.length()) < text.length())) && (text.subSequence(i, i + tableColumnStartCenter.length()).toString().equals(tableColumnStartCenter) || text.subSequence(i, i + tableHeaderStartCenter.length()).toString().equals(tableHeaderStartCenter))) {
                columnStarted = true;
                gravity = android.view.Gravity.CENTER;
                i += tableColumnStartCenter.length();
                columnStart = i;
            } else if (((!columnStarted) && ((i + tableColumnStartLeft.length()) < text.length())) && (text.subSequence(i, i + tableColumnStartLeft.length()).toString().equals(tableColumnStartLeft) || text.subSequence(i, i + tableHeaderStartLeft.length()).toString().equals(tableHeaderStartLeft))) {
                columnStarted = true;
                gravity = android.view.Gravity.START;
                i += tableColumnStartLeft.length();
                columnStart = i;
            } else if (text.substring(i).startsWith("<td")) {
                // case for <td colspan="2"  align="left">
                // See last table in https://www.reddit.com/r/GlobalOffensive/comments/51s3r8/virtuspro_vs_vgcyberzen_sl_ileague_s2_finals/
                columnStarted = true;
                i += text.substring(i).indexOf(">") + 1;
                columnStart = i;
            } else if (text.subSequence(i, i + tableColumnEnd.length()).toString().equals(tableColumnEnd) || text.subSequence(i, i + tableHeaderEnd.length()).toString().equals(tableHeaderEnd)) {
                columnEnd = i;
                me.ccrama.redditslide.SpoilerRobotoTextView textView = new me.ccrama.redditslide.SpoilerRobotoTextView(context);
                textView.setTextHtml(text.subSequence(columnStart, columnEnd), subreddit);
                setStyle(textView, subreddit);
                textView.setLayoutParams(me.ccrama.redditslide.Views.CommentOverflow.COLUMN_PARAMS);
                textView.setGravity(gravity);
                if (click != null)
                    textView.setOnClickListener(click);

                if (longClick != null)
                    textView.setOnLongClickListener(longClick);

                if (text.subSequence(i, i + tableHeaderEnd.length()).toString().equals(tableHeaderEnd)) {
                    textView.setTypeface(null, android.graphics.Typeface.BOLD);
                }
                if (row != null) {
                    row.addView(textView);
                }
                columnStart = 0;
                columnStarted = false;
                i += tableColumnEnd.length();
            } else {
                i += 1;
            }
        } 
        return table;
    }

    private void setStyle(me.ccrama.redditslide.SpoilerRobotoTextView textView, java.lang.String subreddit) {
        textView.setTextColor(textColor);
        textView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, fontSize);
        if (typeface != null)
            textView.setTypeface(typeface);

        textView.setLinkTextColor(colorPreferences.getColor(subreddit));
    }
}