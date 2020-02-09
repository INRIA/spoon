package me.ccrama.redditslide;
import java.util.regex.Matcher;
import me.ccrama.redditslide.Visuals.Palette;
import java.util.ArrayList;
import me.ccrama.redditslide.Activities.MediaView;
import me.ccrama.redditslide.handler.TextViewLinkHandler;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.Views.PeekMediaView;
import me.ccrama.redditslide.Activities.Album;
import org.apache.commons.lang3.StringUtils;
import me.ccrama.redditslide.Activities.TumblrPager;
import me.ccrama.redditslide.ForceTouch.PeekView;
import me.ccrama.redditslide.ForceTouch.builder.Peek;
import java.util.List;
import me.ccrama.redditslide.ForceTouch.builder.PeekViewOptions;
import me.ccrama.redditslide.ForceTouch.callback.OnPop;
import java.util.regex.Pattern;
import me.ccrama.redditslide.ForceTouch.PeekViewActivity;
import me.ccrama.redditslide.SubmissionViews.OpenVRedditTask;
import me.ccrama.redditslide.ForceTouch.callback.OnButtonUp;
import me.ccrama.redditslide.Activities.AlbumPager;
import me.ccrama.redditslide.util.LinkUtil;
import me.ccrama.redditslide.Views.CustomQuoteSpan;
import me.ccrama.redditslide.util.GifUtils;
import java.io.File;
import me.ccrama.redditslide.ForceTouch.callback.OnRemove;
import me.ccrama.redditslide.ForceTouch.callback.SimpleOnPeek;
/**
 * Created by carlo_000 on 1/11/2016.
 */
public class SpoilerRobotoTextView extends com.devspark.robototextview.widget.RobotoTextView implements me.ccrama.redditslide.ClickableText {
    private java.util.List<android.text.style.CharacterStyle> storedSpoilerSpans = new java.util.ArrayList<>();

    private java.util.List<java.lang.Integer> storedSpoilerStarts = new java.util.ArrayList<>();

    private java.util.List<java.lang.Integer> storedSpoilerEnds = new java.util.ArrayList<>();

    private static final java.util.regex.Pattern htmlSpoilerPattern = java.util.regex.Pattern.compile("<a href=\"[#/](?:spoiler|sp|s)\">([^<]*)</a>");

    private static final java.util.regex.Pattern nativeSpoilerPattern = java.util.regex.Pattern.compile("<span class=\"[^\"]*md-spoiler-text+[^\"]*\">([^<]*)</span>");

    public SpoilerRobotoTextView(android.content.Context context) {
        super(context);
        setLineSpacing(0, 1.1F);
    }

    public SpoilerRobotoTextView(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        setLineSpacing(0, 1.1F);
    }

    public SpoilerRobotoTextView(android.content.Context context, android.util.AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLineSpacing(0, 1.1F);
    }

    public boolean isSpoilerClicked() {
        return spoilerClicked;
    }

    public void resetSpoilerClicked() {
        spoilerClicked = false;
    }

    public boolean spoilerClicked = false;

    private static android.text.SpannableStringBuilder removeNewlines(android.text.SpannableStringBuilder s) {
        int start = 0;
        int end = s.length();
        while ((start < end) && java.lang.Character.isWhitespace(s.charAt(start))) {
            start++;
        } 
        while ((end > start) && java.lang.Character.isWhitespace(s.charAt(end - 1))) {
            end--;
        } 
        return ((android.text.SpannableStringBuilder) (s.subSequence(start, end)));
    }

    /**
     * Set the text from html. Handles formatting spoilers, links etc. <p/> The text must be valid
     * html.
     *
     * @param text
     * 		html text
     */
    public void setTextHtml(java.lang.CharSequence text) {
        setTextHtml(text, "");
    }

    /**
     * Set the text from html. Handles formatting spoilers, links etc. <p/> The text must be valid
     * html.
     *
     * @param baseText
     * 		html text
     * @param subreddit
     * 		the subreddit to theme
     */
    public void setTextHtml(java.lang.CharSequence baseText, java.lang.String subreddit) {
        java.lang.String text = wrapAlternateSpoilers(saveEmotesFromDestruction(baseText.toString().trim()));
        android.text.SpannableStringBuilder builder = ((android.text.SpannableStringBuilder) (android.text.Html.fromHtml(text)));
        replaceQuoteSpans(builder);// replace the <blockquote> blue line with something more colorful

        if (text.contains("<a")) {
            setEmoteSpans(builder);// for emote enabled subreddits

        }
        if (text.contains("[")) {
            setCodeFont(builder);
            setSpoilerStyle(builder, subreddit);
        }
        if (text.contains("[[d[")) {
            setStrikethrough(builder);
        }
        if (text.contains("[[h[")) {
            setHighlight(builder, subreddit);
        }
        if ((subreddit != null) && (!subreddit.isEmpty())) {
            setMovementMethod(new me.ccrama.redditslide.handler.TextViewLinkHandler(this, subreddit, builder));
            setFocusable(false);
            setClickable(false);
            if (subreddit.equals("FORCE_LINK_CLICK")) {
                setLongClickable(false);
            }
        }
        builder = me.ccrama.redditslide.SpoilerRobotoTextView.removeNewlines(builder);
        builder.append(" ");
        super.setText(builder, android.widget.TextView.BufferType.SPANNABLE);
    }

    /**
     * Replaces the blue line produced by <blockquote>s with something more visible
     *
     * @param spannable
     * 		parsed comment text #fromHtml
     */
    private void replaceQuoteSpans(android.text.Spannable spannable) {
        android.text.style.QuoteSpan[] quoteSpans = spannable.getSpans(0, spannable.length(), android.text.style.QuoteSpan.class);
        for (android.text.style.QuoteSpan quoteSpan : quoteSpans) {
            final int start = spannable.getSpanStart(quoteSpan);
            final int end = spannable.getSpanEnd(quoteSpan);
            final int flags = spannable.getSpanFlags(quoteSpan);
            spannable.removeSpan(quoteSpan);
            // If the theme is Light or Sepia, use a darker blue; otherwise, use a lighter blue
            final int barColor = ((me.ccrama.redditslide.SettingValues.currentTheme == 1) || (me.ccrama.redditslide.SettingValues.currentTheme == 5)) ? android.support.v4.content.ContextCompat.getColor(getContext(), me.ccrama.redditslide.R.color.md_blue_600) : android.support.v4.content.ContextCompat.getColor(getContext(), me.ccrama.redditslide.R.color.md_blue_400);
            final int BAR_WIDTH = 4;
            final int GAP = 5;
            // bar + text gap
            spannable.setSpan(// background color
            // bar color
            // bar width
            new me.ccrama.redditslide.Views.CustomQuoteSpan(android.graphics.Color.TRANSPARENT, barColor, BAR_WIDTH, GAP), start, end, flags);
        }
    }

    private java.lang.String wrapAlternateSpoilers(java.lang.String html) {
        java.lang.String replacement = "<a href=\"/spoiler\">spoiler&lt; [[s[$1]s]]</a>";
        html = me.ccrama.redditslide.SpoilerRobotoTextView.htmlSpoilerPattern.matcher(html).replaceAll(replacement);
        html = me.ccrama.redditslide.SpoilerRobotoTextView.nativeSpoilerPattern.matcher(html).replaceAll(replacement);
        return html;
    }

    private java.lang.String saveEmotesFromDestruction(java.lang.String html) {
        // Emotes often have no spoiler caption, and therefore are converted to empty anchors. Html.fromHtml removes anchors with zero length node text. Find zero length anchors that start with "/" and add "." to them.
        java.util.regex.Pattern htmlEmotePattern = java.util.regex.Pattern.compile("<a href=\"/.*\"></a>");
        java.util.regex.Matcher htmlEmoteMatcher = htmlEmotePattern.matcher(html);
        while (htmlEmoteMatcher.find()) {
            java.lang.String newPiece = htmlEmoteMatcher.group();
            // Ignore empty tags marked with sp.
            if (!htmlEmoteMatcher.group().contains("href=\"/sp\"")) {
                newPiece = newPiece.replace("></a", ">.</a");
                html = html.replace(htmlEmoteMatcher.group(), newPiece);
            }
        } 
        return html;
    }

    private void setEmoteSpans(android.text.SpannableStringBuilder builder) {
        for (android.text.style.URLSpan span : builder.getSpans(0, builder.length(), android.text.style.URLSpan.class)) {
            if (me.ccrama.redditslide.SettingValues.typeInText) {
                setLinkTypes(builder, span);
            }
            if (me.ccrama.redditslide.SettingValues.largeLinks) {
                setLargeLinks(builder, span);
            }
            java.io.File emoteDir = new java.io.File(android.os.Environment.getExternalStorageDirectory(), "RedditEmotes");
            java.io.File emoteFile = new java.io.File(emoteDir, span.getURL().replace("/", "").replaceAll("-.*", "") + ".png");// BPM uses "-" to add dynamics for emotes in browser. Fall back to original here if exists.

            boolean startsWithSlash = span.getURL().startsWith("/");
            boolean hasOnlyOneSlash = org.apache.commons.lang3.StringUtils.countMatches(span.getURL(), "/") == 1;
            if (((emoteDir.exists() && startsWithSlash) && hasOnlyOneSlash) && emoteFile.exists()) {
                // We've got an emote match
                int start = builder.getSpanStart(span);
                int end = builder.getSpanEnd(span);
                java.lang.CharSequence textCovers = builder.subSequence(start, end);
                // Make sure bitmap loaded works well with screen density.
                android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
                android.util.DisplayMetrics metrics = new android.util.DisplayMetrics();
                ((android.view.WindowManager) (getContext().getSystemService(android.content.Context.WINDOW_SERVICE))).getDefaultDisplay().getMetrics(metrics);
                options.inDensity = 240;
                options.inScreenDensity = metrics.densityDpi;
                options.inScaled = true;
                // Since emotes are not directly attached to included text, add extra character to attach image to.
                builder.removeSpan(span);
                if (builder.subSequence(start, end).charAt(0) != '.') {
                    builder.insert(start, ".");
                }
                android.graphics.Bitmap emoteBitmap = android.graphics.BitmapFactory.decodeFile(emoteFile.getAbsolutePath(), options);
                builder.setSpan(new android.text.style.ImageSpan(getContext(), emoteBitmap), start, start + 1, android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                if (emoteBitmap != null) {
                    emoteBitmap.recycle();
                }
                // Check if url span has length. If it does, it's a spoiler/caption
                if (textCovers.length() > 1) {
                    builder.setSpan(new android.text.style.URLSpan("/sp"), start + 1, end + 1, android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    builder.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.ITALIC), start + 1, end + 1, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                builder.append("\n");// Newline to fix text wrapping issues

            }
        }
    }

    private void setLinkTypes(android.text.SpannableStringBuilder builder, android.text.style.URLSpan span) {
        java.lang.String url = span.getURL();
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        java.lang.String text = builder.subSequence(builder.getSpanStart(span), builder.getSpanEnd(span)).toString();
        if (!text.equalsIgnoreCase(url)) {
            me.ccrama.redditslide.ContentType.Type contentType = me.ccrama.redditslide.ContentType.getContentType(url);
            java.lang.String bod;
            try {
                bod = (" (" + ((url.contains("/") && url.startsWith("/")) && (!(url.split("/").length > 2)) ? url : getContext().getString(me.ccrama.redditslide.ContentType.getContentID(contentType, false)) + (contentType == me.ccrama.redditslide.ContentType.Type.LINK ? " " + android.net.Uri.parse(url).getHost() : ""))) + ")";
            } catch (java.lang.Exception e) {
                bod = (" (" + getContext().getString(me.ccrama.redditslide.ContentType.getContentID(contentType, false))) + ")";
            }
            android.text.SpannableStringBuilder b = new android.text.SpannableStringBuilder(bod);
            b.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, b.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            b.setSpan(new android.text.style.RelativeSizeSpan(0.8F), 0, b.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.insert(builder.getSpanEnd(span), b);
        }
    }

    private void setLargeLinks(android.text.SpannableStringBuilder builder, android.text.style.URLSpan span) {
        builder.setSpan(new android.text.style.RelativeSizeSpan(1.3F), builder.getSpanStart(span), builder.getSpanEnd(span), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void setStrikethrough(android.text.SpannableStringBuilder builder) {
        final int offset = "[[d[".length();// == "]d]]".length()

        int start = -1;
        int end;
        for (int i = 0; i < (builder.length() - 3); i++) {
            if ((((builder.charAt(i) == '[') && (builder.charAt(i + 1) == '[')) && (builder.charAt(i + 2) == 'd')) && (builder.charAt(i + 3) == '[')) {
                start = i + offset;
            } else if ((((builder.charAt(i) == ']') && (builder.charAt(i + 1) == 'd')) && (builder.charAt(i + 2) == ']')) && (builder.charAt(i + 3) == ']')) {
                end = i;
                builder.setSpan(new android.text.style.StrikethroughSpan(), start, end, android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                builder.delete(end, end + offset);
                builder.delete(start - offset, start);
                i -= offset + (end - start);// length of text

            }
        }
    }

    private void setHighlight(android.text.SpannableStringBuilder builder, java.lang.String subreddit) {
        final int offset = "[[h[".length();// == "]h]]".length()

        int start = -1;
        int end;
        for (int i = 0; i < (builder.length() - 4); i++) {
            if ((((builder.charAt(i) == '[') && (builder.charAt(i + 1) == '[')) && (builder.charAt(i + 2) == 'h')) && (builder.charAt(i + 3) == '[')) {
                start = i + offset;
            } else if ((((builder.charAt(i) == ']') && (builder.charAt(i + 1) == 'h')) && (builder.charAt(i + 2) == ']')) && (builder.charAt(i + 3) == ']')) {
                end = i;
                builder.setSpan(new android.text.style.BackgroundColorSpan(me.ccrama.redditslide.Visuals.Palette.getColor(subreddit)), start, end, android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                builder.delete(end, end + offset);
                builder.delete(start - offset, start);
                i -= offset + (end - start);// length of text

            }
        }
    }

    @java.lang.Override
    public void onLinkClick(java.lang.String url, int xOffset, java.lang.String subreddit, android.text.style.URLSpan span) {
        if (url == null) {
            ((android.view.View) (getParent())).callOnClick();
            return;
        }
        me.ccrama.redditslide.ContentType.Type type = me.ccrama.redditslide.ContentType.getContentType(url);
        android.content.Context context = getContext();
        android.app.Activity activity = null;
        if (context instanceof android.app.Activity) {
            activity = ((android.app.Activity) (context));
        } else if (context instanceof android.support.v7.view.ContextThemeWrapper) {
            activity = ((android.app.Activity) (((android.support.v7.view.ContextThemeWrapper) (context)).getBaseContext()));
        } else if (context instanceof android.content.ContextWrapper) {
            android.content.Context context1 = ((android.content.ContextWrapper) (context)).getBaseContext();
            if (context1 instanceof android.app.Activity) {
                activity = ((android.app.Activity) (context1));
            } else if (context1 instanceof android.content.ContextWrapper) {
                android.content.Context context2 = ((android.content.ContextWrapper) (context1)).getBaseContext();
                if (context2 instanceof android.app.Activity) {
                    activity = ((android.app.Activity) (context2));
                } else if (context2 instanceof android.content.ContextWrapper) {
                    activity = ((android.app.Activity) (((android.support.v7.view.ContextThemeWrapper) (context2)).getBaseContext()));
                }
            }
        } else {
            throw new java.lang.RuntimeException("Could not find activity from context:" + context);
        }
        if ((!me.ccrama.redditslide.PostMatch.openExternal(url)) || (type == me.ccrama.redditslide.ContentType.Type.VIDEO)) {
            switch (type) {
                case DEVIANTART :
                case IMGUR :
                case XKCD :
                    if (me.ccrama.redditslide.SettingValues.image) {
                        android.content.Intent intent2 = new android.content.Intent(activity, me.ccrama.redditslide.Activities.MediaView.class);
                        intent2.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_URL, url);
                        intent2.putExtra(me.ccrama.redditslide.Activities.MediaView.SUBREDDIT, subreddit);
                        activity.startActivity(intent2);
                    } else {
                        me.ccrama.redditslide.util.LinkUtil.openExternally(url);
                    }
                    break;
                case REDDIT :
                    new me.ccrama.redditslide.OpenRedditLink(activity, url);
                    break;
                case LINK :
                    me.ccrama.redditslide.util.LogUtil.v("Opening link");
                    me.ccrama.redditslide.util.LinkUtil.openUrl(url, me.ccrama.redditslide.Visuals.Palette.getColor(subreddit), activity);
                    break;
                case SELF :
                    break;
                case STREAMABLE :
                case VID_ME :
                    openStreamable(url, subreddit);
                    break;
                case ALBUM :
                    if (me.ccrama.redditslide.SettingValues.album) {
                        if (me.ccrama.redditslide.SettingValues.albumSwipe) {
                            android.content.Intent i = new android.content.Intent(activity, me.ccrama.redditslide.Activities.AlbumPager.class);
                            i.putExtra(me.ccrama.redditslide.Activities.Album.EXTRA_URL, url);
                            i.putExtra(me.ccrama.redditslide.Activities.AlbumPager.SUBREDDIT, subreddit);
                            activity.startActivity(i);
                        } else {
                            android.content.Intent i = new android.content.Intent(activity, me.ccrama.redditslide.Activities.Album.class);
                            i.putExtra(me.ccrama.redditslide.Activities.Album.SUBREDDIT, subreddit);
                            i.putExtra(me.ccrama.redditslide.Activities.Album.EXTRA_URL, url);
                            activity.startActivity(i);
                        }
                    } else {
                        me.ccrama.redditslide.util.LinkUtil.openExternally(url);
                    }
                    break;
                case TUMBLR :
                    if (me.ccrama.redditslide.SettingValues.image) {
                        if (me.ccrama.redditslide.SettingValues.albumSwipe) {
                            android.content.Intent i = new android.content.Intent(activity, me.ccrama.redditslide.Activities.TumblrPager.class);
                            i.putExtra(me.ccrama.redditslide.Activities.Album.EXTRA_URL, url);
                            activity.startActivity(i);
                        } else {
                            android.content.Intent i = new android.content.Intent(activity, me.ccrama.redditslide.Activities.TumblrPager.class);
                            i.putExtra(me.ccrama.redditslide.Activities.Album.EXTRA_URL, url);
                            activity.startActivity(i);
                        }
                    } else {
                        me.ccrama.redditslide.util.LinkUtil.openExternally(url);
                    }
                    break;
                case IMAGE :
                    openImage(url, subreddit);
                    break;
                case VREDDIT_REDIRECT :
                    openVReddit(url, subreddit, activity);
                    break;
                case GIF :
                case VREDDIT_DIRECT :
                    openGif(url, subreddit, activity);
                    break;
                case NONE :
                    break;
                case VIDEO :
                    if (!me.ccrama.redditslide.util.LinkUtil.tryOpenWithVideoPlugin(url)) {
                        me.ccrama.redditslide.util.LinkUtil.openUrl(url, me.ccrama.redditslide.Visuals.Palette.getStatusBarColor(), activity);
                    }
                case SPOILER :
                    spoilerClicked = true;
                    setOrRemoveSpoilerSpans(xOffset, span);
                    break;
                case EXTERNAL :
                    me.ccrama.redditslide.util.LinkUtil.openExternally(url);
                    break;
            }
        } else {
            me.ccrama.redditslide.util.LinkUtil.openExternally(url);
        }
    }

    @java.lang.Override
    public void onLinkLongClick(final java.lang.String baseUrl, android.view.MotionEvent event) {
        if (baseUrl == null) {
            return;
        }
        final java.lang.String url = org.apache.commons.text.StringEscapeUtils.unescapeHtml4(baseUrl);
        performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
        android.app.Activity activity = null;
        final android.content.Context context = getContext();
        if (context instanceof android.app.Activity) {
            activity = ((android.app.Activity) (context));
        } else if (context instanceof android.support.v7.view.ContextThemeWrapper) {
            activity = ((android.app.Activity) (((android.support.v7.view.ContextThemeWrapper) (context)).getBaseContext()));
        } else if (context instanceof android.content.ContextWrapper) {
            android.content.Context context1 = ((android.content.ContextWrapper) (context)).getBaseContext();
            if (context1 instanceof android.app.Activity) {
                activity = ((android.app.Activity) (context1));
            } else if (context1 instanceof android.content.ContextWrapper) {
                android.content.Context context2 = ((android.content.ContextWrapper) (context1)).getBaseContext();
                if (context2 instanceof android.app.Activity) {
                    activity = ((android.app.Activity) (context2));
                } else if (context2 instanceof android.content.ContextWrapper) {
                    activity = ((android.app.Activity) (((android.support.v7.view.ContextThemeWrapper) (context2)).getBaseContext()));
                }
            }
        } else {
            throw new java.lang.RuntimeException("Could not find activity from context:" + context);
        }
        if ((activity != null) && (!activity.isFinishing())) {
            if (me.ccrama.redditslide.SettingValues.peek) {
                me.ccrama.redditslide.ForceTouch.builder.Peek.into(me.ccrama.redditslide.R.layout.peek_view, new me.ccrama.redditslide.ForceTouch.callback.SimpleOnPeek() {
                    @java.lang.Override
                    public void onInflated(final me.ccrama.redditslide.ForceTouch.PeekView peekView, final android.view.View rootView) {
                        // do stuff
                        android.widget.TextView text = rootView.findViewById(me.ccrama.redditslide.R.id.title);
                        text.setText(url);
                        text.setTextColor(android.graphics.Color.WHITE);
                        ((me.ccrama.redditslide.Views.PeekMediaView) (rootView.findViewById(me.ccrama.redditslide.R.id.peek))).setUrl(url);
                        peekView.addButton(me.ccrama.redditslide.R.id.copy, new me.ccrama.redditslide.ForceTouch.callback.OnButtonUp() {
                            @java.lang.Override
                            public void onButtonUp() {
                                android.content.ClipboardManager clipboard = ((android.content.ClipboardManager) (rootView.getContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE)));
                                android.content.ClipData clip = android.content.ClipData.newPlainText("Link", url);
                                clipboard.setPrimaryClip(clip);
                                android.widget.Toast.makeText(rootView.getContext(), me.ccrama.redditslide.R.string.submission_link_copied, android.widget.Toast.LENGTH_SHORT).show();
                            }
                        });
                        peekView.setOnRemoveListener(new me.ccrama.redditslide.ForceTouch.callback.OnRemove() {
                            @java.lang.Override
                            public void onRemove() {
                                ((me.ccrama.redditslide.Views.PeekMediaView) (rootView.findViewById(me.ccrama.redditslide.R.id.peek))).doClose();
                            }
                        });
                        peekView.addButton(me.ccrama.redditslide.R.id.share, new me.ccrama.redditslide.ForceTouch.callback.OnButtonUp() {
                            @java.lang.Override
                            public void onButtonUp() {
                                me.ccrama.redditslide.Reddit.defaultShareText("", url, rootView.getContext());
                            }
                        });
                        peekView.addButton(me.ccrama.redditslide.R.id.pop, new me.ccrama.redditslide.ForceTouch.callback.OnButtonUp() {
                            @java.lang.Override
                            public void onButtonUp() {
                                me.ccrama.redditslide.Reddit.defaultShareText("", url, rootView.getContext());
                            }
                        });
                        peekView.addButton(me.ccrama.redditslide.R.id.external, new me.ccrama.redditslide.ForceTouch.callback.OnButtonUp() {
                            @java.lang.Override
                            public void onButtonUp() {
                                me.ccrama.redditslide.util.LinkUtil.openExternally(url);
                            }
                        });
                        peekView.setOnPop(new me.ccrama.redditslide.ForceTouch.callback.OnPop() {
                            @java.lang.Override
                            public void onPop() {
                                onLinkClick(url, 0, "", null);
                            }
                        });
                    }
                }).with(new me.ccrama.redditslide.ForceTouch.builder.PeekViewOptions().setFullScreenPeek(true)).show(((me.ccrama.redditslide.ForceTouch.PeekViewActivity) (activity)), event);
            } else {
                com.cocosw.bottomsheet.BottomSheet.Builder b = new com.cocosw.bottomsheet.BottomSheet.Builder(activity).title(url).grid();
                int[] attrs = new int[]{ me.ccrama.redditslide.R.attr.tintColor };
                android.content.res.TypedArray ta = getContext().obtainStyledAttributes(attrs);
                int color = ta.getColor(0, android.graphics.Color.WHITE);
                android.graphics.drawable.Drawable open = getResources().getDrawable(me.ccrama.redditslide.R.drawable.ic_open_in_browser);
                open.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
                android.graphics.drawable.Drawable share = getResources().getDrawable(me.ccrama.redditslide.R.drawable.ic_share);
                share.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
                android.graphics.drawable.Drawable copy = getResources().getDrawable(me.ccrama.redditslide.R.drawable.ic_content_copy);
                copy.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
                ta.recycle();
                b.sheet(me.ccrama.redditslide.R.id.open_link, open, getResources().getString(me.ccrama.redditslide.R.string.submission_link_extern));
                b.sheet(me.ccrama.redditslide.R.id.share_link, share, getResources().getString(me.ccrama.redditslide.R.string.share_link));
                b.sheet(me.ccrama.redditslide.R.id.copy_link, copy, getResources().getString(me.ccrama.redditslide.R.string.submission_link_copy));
                final android.app.Activity finalActivity = activity;
                b.listener(new android.content.DialogInterface.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        switch (which) {
                            case me.ccrama.redditslide.R.id.open_link :
                                me.ccrama.redditslide.util.LinkUtil.openExternally(url);
                                break;
                            case me.ccrama.redditslide.R.id.share_link :
                                me.ccrama.redditslide.Reddit.defaultShareText("", url, finalActivity);
                                break;
                            case me.ccrama.redditslide.R.id.copy_link :
                                me.ccrama.redditslide.util.LinkUtil.copyUrl(url, finalActivity);
                                break;
                        }
                    }
                }).show();
            }
        }
    }

    private void openVReddit(java.lang.String url, java.lang.String subreddit, android.app.Activity activity) {
        new me.ccrama.redditslide.SubmissionViews.OpenVRedditTask(activity, subreddit).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR, url);
    }

    private void openGif(java.lang.String url, java.lang.String subreddit, android.app.Activity activity) {
        if (me.ccrama.redditslide.SettingValues.gif) {
            if (me.ccrama.redditslide.util.GifUtils.AsyncLoadGif.getVideoType(url).shouldLoadPreview()) {
                me.ccrama.redditslide.util.LinkUtil.openUrl(url, me.ccrama.redditslide.Visuals.Palette.getColor(subreddit), activity);
            } else {
                android.content.Intent myIntent = new android.content.Intent(getContext(), me.ccrama.redditslide.Activities.MediaView.class);
                myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_URL, url);
                myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.SUBREDDIT, subreddit);
                getContext().startActivity(myIntent);
            }
        } else {
            me.ccrama.redditslide.util.LinkUtil.openExternally(url);
        }
    }

    private void openStreamable(java.lang.String url, java.lang.String subreddit) {
        if (me.ccrama.redditslide.SettingValues.video) {
            // todo maybe streamable here?
            android.content.Intent myIntent = new android.content.Intent(getContext(), me.ccrama.redditslide.Activities.MediaView.class);
            myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_URL, url);
            myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.SUBREDDIT, subreddit);
            getContext().startActivity(myIntent);
        } else {
            me.ccrama.redditslide.util.LinkUtil.openExternally(url);
        }
    }

    private void openImage(java.lang.String submission, java.lang.String subreddit) {
        if (me.ccrama.redditslide.SettingValues.image) {
            android.content.Intent myIntent = new android.content.Intent(getContext(), me.ccrama.redditslide.Activities.MediaView.class);
            myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.EXTRA_URL, submission);
            myIntent.putExtra(me.ccrama.redditslide.Activities.MediaView.SUBREDDIT, subreddit);
            getContext().startActivity(myIntent);
        } else {
            me.ccrama.redditslide.util.LinkUtil.openExternally(submission);
        }
    }

    public void setOrRemoveSpoilerSpans(int endOfLink, android.text.style.URLSpan span) {
        if (span != null) {
            int offset = (span.getURL().contains("hidden")) ? -1 : 2;
            android.text.Spannable text = ((android.text.Spannable) (getText()));
            // add 2 to end of link since there is a white space between the link text and the spoiler
            android.text.style.ForegroundColorSpan[] foregroundColors = text.getSpans(endOfLink + offset, endOfLink + offset, android.text.style.ForegroundColorSpan.class);
            if (foregroundColors.length > 1) {
                text.removeSpan(foregroundColors[1]);
                setText(text);
            } else {
                for (int i = 1; i < storedSpoilerStarts.size(); i++) {
                    if ((storedSpoilerStarts.get(i) < (endOfLink + offset)) && (storedSpoilerEnds.get(i) > (endOfLink + offset))) {
                        try {
                            text.setSpan(storedSpoilerSpans.get(i), storedSpoilerStarts.get(i), storedSpoilerEnds.get(i) > text.toString().length() ? storedSpoilerEnds.get(i) + offset : storedSpoilerEnds.get(i), android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        } catch (java.lang.Exception ignored) {
                            // catch out of bounds
                            ignored.printStackTrace();
                        }
                    }
                }
                setText(text);
            }
        }
    }

    /**
     * Set the necessary spans for each spoiler. <p/> The algorithm works in the same way as
     * <code>setCodeFont</code>.
     *
     * @param sequence
     * 		
     * @return 
     */
    private java.lang.CharSequence setSpoilerStyle(android.text.SpannableStringBuilder sequence, java.lang.String subreddit) {
        int start = 0;
        int end = 0;
        for (int i = 0; i < sequence.length(); i++) {
            if ((sequence.charAt(i) == '[') && (i < (sequence.length() - 3))) {
                if (((sequence.charAt(i + 1) == '[') && (sequence.charAt(i + 2) == 's')) && (sequence.charAt(i + 3) == '[')) {
                    start = i;
                }
            } else if ((sequence.charAt(i) == ']') && (i < (sequence.length() - 3))) {
                if (((sequence.charAt(i + 1) == 's') && (sequence.charAt(i + 2) == ']')) && (sequence.charAt(i + 3) == ']')) {
                    end = i;
                }
            }
            if (end > start) {
                sequence.delete(end, end + 4);
                sequence.delete(start, start + 4);
                android.text.style.BackgroundColorSpan backgroundColorSpan = new android.text.style.BackgroundColorSpan(me.ccrama.redditslide.Visuals.Palette.getDarkerColor(me.ccrama.redditslide.Visuals.Palette.getColor(subreddit)));
                android.text.style.ForegroundColorSpan foregroundColorSpan = new android.text.style.ForegroundColorSpan(me.ccrama.redditslide.Visuals.Palette.getDarkerColor(me.ccrama.redditslide.Visuals.Palette.getColor(subreddit)));
                android.text.style.ForegroundColorSpan underneathColorSpan = new android.text.style.ForegroundColorSpan(android.graphics.Color.WHITE);
                android.text.style.URLSpan urlSpan = sequence.getSpans(start, start, android.text.style.URLSpan.class)[0];
                sequence.setSpan(urlSpan, sequence.getSpanStart(urlSpan), start - 1, android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                sequence.setSpan(new me.ccrama.redditslide.SpoilerRobotoTextView.URLSpanNoUnderline("#spoilerhidden"), start, end - 4, android.text.Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                // spoiler text has a space at the front
                sequence.setSpan(backgroundColorSpan, start, end - 4, android.text.Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                sequence.setSpan(underneathColorSpan, start, end - 4, android.text.Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                sequence.setSpan(foregroundColorSpan, start, end - 4, android.text.Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                storedSpoilerSpans.add(underneathColorSpan);
                storedSpoilerSpans.add(foregroundColorSpan);
                storedSpoilerSpans.add(backgroundColorSpan);
                // Shift 1 to account for remove of beginning "<"
                storedSpoilerStarts.add(start - 1);
                storedSpoilerStarts.add(start - 1);
                storedSpoilerStarts.add(start - 1);
                storedSpoilerEnds.add(end - 5);
                storedSpoilerEnds.add(end - 5);
                storedSpoilerEnds.add(end - 5);
                sequence.delete(start - 2, start - 1);// remove the trailing <

                start = 0;
                end = 0;
                i = i - 5;// move back to compensate for removal of [[s[

            }
        }
        return sequence;
    }

    private class URLSpanNoUnderline extends android.text.style.URLSpan {
        public URLSpanNoUnderline(java.lang.String url) {
            super(url);
        }

        @java.lang.Override
        public void updateDrawState(android.text.TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }

    /**
     * Sets the styling for string with code segments. <p/> The general process is to search for
     * <code>[[&lt;[</code> and <code>]&gt;]]</code> tokens to find the code fragments within the
     * escaped text. A <code>Spannable</code> is created which which breaks up the origin sequence
     * into non-code and code fragments, and applies a monospace font to the code fragments.
     *
     * @param sequence
     * 		the Spannable generated from Html.fromHtml
     * @return the message with monospace font applied to code fragments
     */
    private android.text.SpannableStringBuilder setCodeFont(android.text.SpannableStringBuilder sequence) {
        int start = 0;
        int end = 0;
        for (int i = 0; i < sequence.length(); i++) {
            if ((sequence.charAt(i) == '[') && (i < (sequence.length() - 3))) {
                if (((sequence.charAt(i + 1) == '[') && (sequence.charAt(i + 2) == '<')) && (sequence.charAt(i + 3) == '[')) {
                    start = i;
                }
            } else if ((sequence.charAt(i) == ']') && (i < (sequence.length() - 3))) {
                if (((sequence.charAt(i + 1) == '>') && (sequence.charAt(i + 2) == ']')) && (sequence.charAt(i + 3) == ']')) {
                    end = i;
                }
            }
            if (end > start) {
                sequence.delete(end, end + 4);
                sequence.delete(start, start + 4);
                sequence.setSpan(new android.text.style.TypefaceSpan("monospace"), start, end - 4, android.text.Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                start = 0;
                end = 0;
                i = i - 4;// move back to compensate for removal of [[<[

            }
        }
        return sequence;
    }
}