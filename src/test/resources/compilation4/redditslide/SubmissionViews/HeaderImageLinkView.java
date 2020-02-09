package me.ccrama.redditslide.SubmissionViews;
import me.ccrama.redditslide.ContentType;
import me.ccrama.redditslide.ForceTouch.PeekViewActivity;
import java.net.URISyntaxException;
import com.fasterxml.jackson.databind.JsonNode;
import me.ccrama.redditslide.SettingValues;
import java.net.URI;
import me.ccrama.redditslide.ForceTouch.callback.OnButtonUp;
import me.ccrama.redditslide.Views.TransparentTagTextView;
import me.ccrama.redditslide.util.NetworkUtil;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Views.PeekMediaView;
import me.ccrama.redditslide.HasSeen;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.util.LinkUtil;
import me.ccrama.redditslide.ForceTouch.PeekView;
import me.ccrama.redditslide.ForceTouch.builder.Peek;
import me.ccrama.redditslide.ForceTouch.builder.PeekViewOptions;
import me.ccrama.redditslide.ForceTouch.callback.OnRemove;
import me.ccrama.redditslide.ForceTouch.callback.OnPop;
import me.ccrama.redditslide.ForceTouch.callback.SimpleOnPeek;
/**
 * Created by carlo_000 on 2/7/2016.
 */
public class HeaderImageLinkView extends android.widget.RelativeLayout {
    public java.lang.String loadedUrl;

    public boolean lq;

    public android.widget.ImageView thumbImage2;

    public android.widget.TextView secondTitle;

    public android.widget.TextView secondSubTitle;

    public android.view.View wrapArea;

    boolean done;

    java.lang.String lastDone = "";

    me.ccrama.redditslide.ContentType.Type type;

    com.nostra13.universalimageloader.core.DisplayImageOptions bigOptions = new com.nostra13.universalimageloader.core.DisplayImageOptions.Builder().resetViewBeforeLoading(false).cacheOnDisk(true).imageScaleType(com.nostra13.universalimageloader.core.assist.ImageScaleType.EXACTLY).cacheInMemory(false).displayer(new com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer(250)).build();

    android.app.Activity activity = null;

    boolean clickHandled;

    android.os.Handler handler;

    android.view.MotionEvent event;

    java.lang.Runnable longClicked;

    float position;

    private android.widget.TextView title;

    private android.widget.TextView info;

    public android.widget.ImageView backdrop;

    public HeaderImageLinkView(android.content.Context context) {
        super(context);
        init();
    }

    public HeaderImageLinkView(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HeaderImageLinkView(android.content.Context context, android.util.AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    boolean thumbUsed;

    public void doImageAndText(final net.dean.jraw.models.Submission submission, boolean full, java.lang.String baseSub, boolean news) {
        boolean fullImage = me.ccrama.redditslide.ContentType.fullImage(type);
        thumbUsed = false;
        setVisibility(android.view.View.VISIBLE);
        java.lang.String url = "";
        boolean forceThumb = false;
        thumbImage2.setImageResource(android.R.color.transparent);
        boolean loadLq = ((!me.ccrama.redditslide.util.NetworkUtil.isConnectedWifi(getContext())) && me.ccrama.redditslide.SettingValues.lowResMobile) || me.ccrama.redditslide.SettingValues.lowResAlways;
        /* todo, maybe if(thumbImage2 != null && thumbImage2 instanceof RoundImageTriangleView)
        switch (ContentType.getContentType(submission)) {
        case ALBUM:
        ((RoundImageTriangleView)(thumbImage2)).setFlagColor(R.color.md_blue_300);
        break;
        case EXTERNAL:
        case LINK:
        case REDDIT:
        ((RoundImageTriangleView)(thumbImage2)).setFlagColor(R.color.md_red_300);
        break;
        case SELF:
        ((RoundImageTriangleView)(thumbImage2)).setFlagColor(R.color.md_grey_300);
        break;
        case EMBEDDED:
        case GIF:
        case STREAMABLE:
        case VIDEO:
        case VID_ME:
        ((RoundImageTriangleView)(thumbImage2)).setFlagColor(R.color.md_green_300);
        break;
        default:
        ((RoundImageTriangleView)(thumbImage2)).setFlagColor(Color.TRANSPARENT);
        break;
        }
         */
        if (((type == me.ccrama.redditslide.ContentType.Type.SELF) && me.ccrama.redditslide.SettingValues.hideSelftextLeadImage) || (me.ccrama.redditslide.SettingValues.noImages && submission.isSelfPost())) {
            setVisibility(android.view.View.GONE);
            if (wrapArea != null)
                wrapArea.setVisibility(android.view.View.GONE);

            thumbImage2.setVisibility(android.view.View.GONE);
        } else {
            if (submission.getThumbnails() != null) {
                int height = submission.getThumbnails().getSource().getHeight();
                int width = submission.getThumbnails().getSource().getWidth();
                if (full) {
                    if (((!fullImage) && (height < dpToPx(50))) && (type != me.ccrama.redditslide.ContentType.Type.SELF)) {
                        forceThumb = true;
                    } else if (me.ccrama.redditslide.SettingValues.cropImage) {
                        backdrop.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, dpToPx(200)));
                    } else {
                        double h = getHeightFromAspectRatio(height, width);
                        if (h != 0) {
                            if (h > 3200) {
                                backdrop.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, 3200));
                            } else {
                                backdrop.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, ((int) (h))));
                            }
                        } else {
                            backdrop.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT));
                        }
                    }
                } else if (me.ccrama.redditslide.SettingValues.bigPicCropped) {
                    if ((!fullImage) && (height < dpToPx(50))) {
                        forceThumb = true;
                    } else {
                        backdrop.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, dpToPx(200)));
                    }
                } else if (fullImage || (height >= dpToPx(50))) {
                    double h = getHeightFromAspectRatio(height, width);
                    if (h != 0) {
                        if (h > 3200) {
                            backdrop.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, 3200));
                        } else {
                            backdrop.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, ((int) (h))));
                        }
                    } else {
                        backdrop.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT));
                    }
                } else {
                    forceThumb = true;
                }
            }
            com.fasterxml.jackson.databind.JsonNode thumbnail = submission.getDataNode().get("thumbnail");
            net.dean.jraw.models.Submission.ThumbnailType thumbnailType;
            if (!submission.getDataNode().get("thumbnail").isNull()) {
                thumbnailType = submission.getThumbnailType();
            } else {
                thumbnailType = net.dean.jraw.models.Submission.ThumbnailType.NONE;
            }
            com.fasterxml.jackson.databind.JsonNode node = submission.getDataNode();
            if (((((!me.ccrama.redditslide.SettingValues.ignoreSubSetting) && (node != null)) && node.has("sr_detail")) && node.get("sr_detail").has("show_media")) && (!node.get("sr_detail").get("show_media").asBoolean())) {
                thumbnailType = net.dean.jraw.models.Submission.ThumbnailType.NONE;
            }
            if (me.ccrama.redditslide.SettingValues.noImages && loadLq) {
                setVisibility(android.view.View.GONE);
                if ((!full) && (!submission.isSelfPost())) {
                    thumbImage2.setVisibility(android.view.View.VISIBLE);
                } else if (full && (!submission.isSelfPost()))
                    wrapArea.setVisibility(android.view.View.VISIBLE);

                thumbImage2.setImageDrawable(android.support.v4.content.ContextCompat.getDrawable(getContext(), me.ccrama.redditslide.R.drawable.web));
                thumbUsed = true;
            } else if ((submission.isNsfw() && me.ccrama.redditslide.SettingValues.getIsNSFWEnabled()) || ((((baseSub != null) && submission.isNsfw()) && me.ccrama.redditslide.SettingValues.hideNSFWCollection) && (((baseSub.equals("frontpage") || baseSub.equals("all")) || baseSub.contains("+")) || baseSub.equals("popular")))) {
                setVisibility(android.view.View.GONE);
                if ((!full) || forceThumb) {
                    thumbImage2.setVisibility(android.view.View.VISIBLE);
                } else {
                    wrapArea.setVisibility(android.view.View.VISIBLE);
                }
                if (submission.isSelfPost() && full) {
                    wrapArea.setVisibility(android.view.View.GONE);
                } else {
                    thumbImage2.setImageDrawable(android.support.v4.content.ContextCompat.getDrawable(getContext(), me.ccrama.redditslide.R.drawable.nsfw));
                    thumbUsed = true;
                }
                loadedUrl = submission.getUrl();
            } else if (submission.getDataNode().get("spoiler").asBoolean()) {
                setVisibility(android.view.View.GONE);
                if ((!full) || forceThumb) {
                    thumbImage2.setVisibility(android.view.View.VISIBLE);
                } else {
                    wrapArea.setVisibility(android.view.View.VISIBLE);
                }
                if (submission.isSelfPost() && full) {
                    wrapArea.setVisibility(android.view.View.GONE);
                } else {
                    thumbImage2.setImageDrawable(android.support.v4.content.ContextCompat.getDrawable(getContext(), me.ccrama.redditslide.R.drawable.spoiler));
                    thumbUsed = true;
                }
                loadedUrl = submission.getUrl();
            } else if ((((type != me.ccrama.redditslide.ContentType.Type.IMAGE) && (type != me.ccrama.redditslide.ContentType.Type.SELF)) && ((!thumbnail.isNull()) && (thumbnailType != net.dean.jraw.models.Submission.ThumbnailType.URL))) || (thumbnail.asText().isEmpty() && (!submission.isSelfPost()))) {
                setVisibility(android.view.View.GONE);
                if (!full) {
                    thumbImage2.setVisibility(android.view.View.VISIBLE);
                } else {
                    wrapArea.setVisibility(android.view.View.VISIBLE);
                }
                thumbImage2.setImageDrawable(android.support.v4.content.ContextCompat.getDrawable(getContext(), me.ccrama.redditslide.R.drawable.web));
                thumbUsed = true;
                loadedUrl = submission.getUrl();
            } else if (((type == me.ccrama.redditslide.ContentType.Type.IMAGE) && (!thumbnail.isNull())) && (!thumbnail.asText().isEmpty())) {
                if (((loadLq && (submission.getThumbnails() != null)) && (submission.getThumbnails().getVariations() != null)) && (submission.getThumbnails().getVariations().length > 0)) {
                    if (me.ccrama.redditslide.ContentType.isImgurImage(submission.getUrl())) {
                        url = submission.getUrl();
                        url = (url.substring(0, url.lastIndexOf(".")) + (me.ccrama.redditslide.SettingValues.lqLow ? "m" : me.ccrama.redditslide.SettingValues.lqMid ? "l" : "h")) + url.substring(url.lastIndexOf("."), url.length());
                    } else {
                        int length = submission.getThumbnails().getVariations().length;
                        if (me.ccrama.redditslide.SettingValues.lqLow && (length >= 3)) {
                            url = android.text.Html.fromHtml(submission.getThumbnails().getVariations()[2].getUrl()).toString();// unescape url characters

                        } else if (me.ccrama.redditslide.SettingValues.lqMid && (length >= 4)) {
                            url = android.text.Html.fromHtml(submission.getThumbnails().getVariations()[3].getUrl()).toString();// unescape url characters

                        } else if (length >= 5) {
                            url = android.text.Html.fromHtml(submission.getThumbnails().getVariations()[length - 1].getUrl()).toString();// unescape url characters

                        } else {
                            url = android.text.Html.fromHtml(submission.getThumbnails().getSource().getUrl()).toString();// unescape url characters

                        }
                    }
                    lq = true;
                } else if (submission.getDataNode().has("preview") && submission.getDataNode().get("preview").get("images").get(0).get("source").has("height")) {
                    // Load the preview image which has probably already been cached in memory instead of the direct link
                    url = submission.getDataNode().get("preview").get("images").get(0).get("source").get("url").asText();
                } else {
                    url = submission.getUrl();
                }
                if (((!full) && (!me.ccrama.redditslide.SettingValues.isPicsEnabled(baseSub))) || forceThumb) {
                    if ((!submission.isSelfPost()) || full) {
                        if (!full) {
                            thumbImage2.setVisibility(android.view.View.VISIBLE);
                        } else {
                            wrapArea.setVisibility(android.view.View.VISIBLE);
                        }
                        loadedUrl = url;
                        if (!full) {
                            ((me.ccrama.redditslide.Reddit) (getContext().getApplicationContext())).getImageLoader().displayImage(url, thumbImage2);
                        } else {
                            ((me.ccrama.redditslide.Reddit) (getContext().getApplicationContext())).getImageLoader().displayImage(url, thumbImage2, bigOptions);
                        }
                    } else {
                        thumbImage2.setVisibility(android.view.View.GONE);
                    }
                    setVisibility(android.view.View.GONE);
                } else {
                    loadedUrl = url;
                    if (!full) {
                        ((me.ccrama.redditslide.Reddit) (getContext().getApplicationContext())).getImageLoader().displayImage(url, backdrop);
                    } else {
                        ((me.ccrama.redditslide.Reddit) (getContext().getApplicationContext())).getImageLoader().displayImage(url, backdrop, bigOptions);
                    }
                    setVisibility(android.view.View.VISIBLE);
                    if (!full) {
                        thumbImage2.setVisibility(android.view.View.GONE);
                    } else {
                        wrapArea.setVisibility(android.view.View.GONE);
                    }
                }
            } else if (submission.getThumbnails() != null) {
                if (loadLq && (submission.getThumbnails().getVariations().length != 0)) {
                    if (me.ccrama.redditslide.ContentType.isImgurImage(submission.getUrl())) {
                        url = submission.getUrl();
                        url = (url.substring(0, url.lastIndexOf(".")) + (me.ccrama.redditslide.SettingValues.lqLow ? "m" : me.ccrama.redditslide.SettingValues.lqMid ? "l" : "h")) + url.substring(url.lastIndexOf("."), url.length());
                    } else {
                        int length = submission.getThumbnails().getVariations().length;
                        if (me.ccrama.redditslide.SettingValues.lqLow && (length >= 3)) {
                            url = android.text.Html.fromHtml(submission.getThumbnails().getVariations()[2].getUrl()).toString();// unescape url characters

                        } else if (me.ccrama.redditslide.SettingValues.lqMid && (length >= 4)) {
                            url = android.text.Html.fromHtml(submission.getThumbnails().getVariations()[3].getUrl()).toString();// unescape url characters

                        } else if (length >= 5) {
                            url = android.text.Html.fromHtml(submission.getThumbnails().getVariations()[length - 1].getUrl()).toString();// unescape url characters

                        } else {
                            url = android.text.Html.fromHtml(submission.getThumbnails().getSource().getUrl()).toString();// unescape url characters

                        }
                    }
                    lq = true;
                } else {
                    url = android.text.Html.fromHtml(submission.getThumbnails().getSource().getUrl()).toString();// unescape url characters

                }
                if ((((!me.ccrama.redditslide.SettingValues.isPicsEnabled(baseSub)) && (!full)) || forceThumb) || (news && (submission.getScore() < 5000))) {
                    if (!full) {
                        thumbImage2.setVisibility(android.view.View.VISIBLE);
                    } else {
                        wrapArea.setVisibility(android.view.View.VISIBLE);
                    }
                    loadedUrl = url;
                    ((me.ccrama.redditslide.Reddit) (getContext().getApplicationContext())).getImageLoader().displayImage(url, thumbImage2);
                    setVisibility(android.view.View.GONE);
                } else {
                    loadedUrl = url;
                    if (!full) {
                        ((me.ccrama.redditslide.Reddit) (getContext().getApplicationContext())).getImageLoader().displayImage(url, backdrop);
                    } else {
                        ((me.ccrama.redditslide.Reddit) (getContext().getApplicationContext())).getImageLoader().displayImage(url, backdrop, bigOptions);
                    }
                    setVisibility(android.view.View.VISIBLE);
                    if (!full) {
                        thumbImage2.setVisibility(android.view.View.GONE);
                    } else {
                        wrapArea.setVisibility(android.view.View.GONE);
                    }
                }
            } else if (((!thumbnail.isNull()) && (submission.getThumbnail() != null)) && ((submission.getThumbnailType() == net.dean.jraw.models.Submission.ThumbnailType.URL) || (((!thumbnail.isNull()) && submission.isNsfw()) && me.ccrama.redditslide.SettingValues.getIsNSFWEnabled()))) {
                if (!full) {
                    thumbImage2.setVisibility(android.view.View.VISIBLE);
                } else {
                    wrapArea.setVisibility(android.view.View.VISIBLE);
                }
                loadedUrl = url;
                ((me.ccrama.redditslide.Reddit) (getContext().getApplicationContext())).getImageLoader().displayImage(url, thumbImage2);
                setVisibility(android.view.View.GONE);
            } else {
                if (!full) {
                    thumbImage2.setVisibility(android.view.View.GONE);
                } else {
                    wrapArea.setVisibility(android.view.View.GONE);
                }
                setVisibility(android.view.View.GONE);
            }
            if (full) {
                if (wrapArea.getVisibility() == android.view.View.VISIBLE) {
                    title = secondTitle;
                    info = secondSubTitle;
                    setBottomSheet(wrapArea, submission, full);
                } else {
                    title = findViewById(me.ccrama.redditslide.R.id.textimage);
                    info = findViewById(me.ccrama.redditslide.R.id.subtextimage);
                    if (forceThumb || ((submission.isNsfw() && (submission.getThumbnailType() == net.dean.jraw.models.Submission.ThumbnailType.NSFW)) || ((((type != me.ccrama.redditslide.ContentType.Type.IMAGE) && (type != me.ccrama.redditslide.ContentType.Type.SELF)) && (!submission.getDataNode().get("thumbnail").isNull())) && (submission.getThumbnailType() != net.dean.jraw.models.Submission.ThumbnailType.URL)))) {
                        setBottomSheet(thumbImage2, submission, full);
                    } else {
                        setBottomSheet(this, submission, full);
                    }
                }
            } else {
                title = findViewById(me.ccrama.redditslide.R.id.textimage);
                info = findViewById(me.ccrama.redditslide.R.id.subtextimage);
                setBottomSheet(thumbImage2, submission, full);
                setBottomSheet(this, submission, full);
            }
            if ((me.ccrama.redditslide.SettingValues.smallTag && (!full)) && (!news)) {
                title = findViewById(me.ccrama.redditslide.R.id.tag);
                findViewById(me.ccrama.redditslide.R.id.tag).setVisibility(android.view.View.VISIBLE);
                info = null;
            } else {
                findViewById(me.ccrama.redditslide.R.id.tag).setVisibility(android.view.View.GONE);
                title.setVisibility(android.view.View.VISIBLE);
                info.setVisibility(android.view.View.VISIBLE);
            }
            if ((me.ccrama.redditslide.SettingValues.smallTag && (!full)) && (!news)) {
                ((me.ccrama.redditslide.Views.TransparentTagTextView) (title)).init(getContext());
            }
            title.setText(me.ccrama.redditslide.ContentType.getContentDescription(submission, getContext()));
            if (info != null)
                info.setText(submission.getDomain());

        }
    }

    public int dpToPx(int dp) {
        return ((int) (dp * android.content.res.Resources.getSystem().getDisplayMetrics().density));
    }

    boolean popped;

    public double getHeightFromAspectRatio(int imageHeight, int imageWidth) {
        double ratio = ((double) (imageHeight)) / ((double) (imageWidth));
        double width = getWidth();
        return width * ratio;
    }

    public void onLinkLongClick(final java.lang.String url, android.view.MotionEvent event) {
        popped = false;
        if (url == null) {
            return;
        }
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
                me.ccrama.redditslide.ForceTouch.builder.Peek.into(me.ccrama.redditslide.R.layout.peek_view_submission, new me.ccrama.redditslide.ForceTouch.callback.SimpleOnPeek() {
                    @java.lang.Override
                    public void onInflated(final me.ccrama.redditslide.ForceTouch.PeekView peekView, final android.view.View rootView) {
                        // do stuff
                        android.widget.TextView text = rootView.findViewById(me.ccrama.redditslide.R.id.title);
                        text.setText(url);
                        text.setTextColor(android.graphics.Color.WHITE);
                        ((me.ccrama.redditslide.Views.PeekMediaView) (rootView.findViewById(me.ccrama.redditslide.R.id.peek))).setUrl(url);
                        peekView.addButton(me.ccrama.redditslide.R.id.share, new me.ccrama.redditslide.ForceTouch.callback.OnButtonUp() {
                            @java.lang.Override
                            public void onButtonUp() {
                                me.ccrama.redditslide.Reddit.defaultShareText("", url, rootView.getContext());
                            }
                        });
                        peekView.addButton(me.ccrama.redditslide.R.id.upvoteb, new me.ccrama.redditslide.ForceTouch.callback.OnButtonUp() {
                            @java.lang.Override
                            public void onButtonUp() {
                                ((android.view.View) (getParent())).findViewById(me.ccrama.redditslide.R.id.upvote).callOnClick();
                            }
                        });
                        peekView.setOnRemoveListener(new me.ccrama.redditslide.ForceTouch.callback.OnRemove() {
                            @java.lang.Override
                            public void onRemove() {
                                ((me.ccrama.redditslide.Views.PeekMediaView) (rootView.findViewById(me.ccrama.redditslide.R.id.peek))).doClose();
                            }
                        });
                        peekView.addButton(me.ccrama.redditslide.R.id.comments, new me.ccrama.redditslide.ForceTouch.callback.OnButtonUp() {
                            @java.lang.Override
                            public void onButtonUp() {
                                ((android.view.View) (getParent().getParent())).callOnClick();
                            }
                        });
                        peekView.setOnPop(new me.ccrama.redditslide.ForceTouch.callback.OnPop() {
                            @java.lang.Override
                            public void onPop() {
                                popped = true;
                                callOnClick();
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

    public void setBottomSheet(android.view.View v, final net.dean.jraw.models.Submission submission, final boolean full) {
        handler = new android.os.Handler();
        v.setOnTouchListener(new android.view.View.OnTouchListener() {
            @java.lang.Override
            public boolean onTouch(android.view.View v, android.view.MotionEvent event) {
                int x = ((int) (event.getX()));
                int y = ((int) (event.getY()));
                x += getScrollX();
                y += getScrollY();
                me.ccrama.redditslide.SubmissionViews.HeaderImageLinkView.this.event = event;
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    position = event.getY();// used to see if the user scrolled or not

                }
                if (!((event.getAction() == android.view.MotionEvent.ACTION_UP) || (event.getAction() == android.view.MotionEvent.ACTION_DOWN))) {
                    if (java.lang.Math.abs(position - event.getY()) > 25) {
                        handler.removeCallbacksAndMessages(null);
                    }
                    return false;
                }
                switch (event.getAction()) {
                    case android.view.MotionEvent.ACTION_DOWN :
                        clickHandled = false;
                        if (me.ccrama.redditslide.SettingValues.peek) {
                            handler.postDelayed(longClicked, android.view.ViewConfiguration.getTapTimeout() + 50);
                        } else {
                            handler.postDelayed(longClicked, android.view.ViewConfiguration.getLongPressTimeout());
                        }
                        break;
                    case android.view.MotionEvent.ACTION_UP :
                        handler.removeCallbacksAndMessages(null);
                        if (!clickHandled) {
                            // regular click
                            callOnClick();
                        }
                        break;
                }
                return true;
            }
        });
        longClicked = new java.lang.Runnable() {
            @java.lang.Override
            public void run() {
                // long click
                clickHandled = true;
                handler.removeCallbacksAndMessages(null);
                if (me.ccrama.redditslide.SettingValues.storeHistory && (!full)) {
                    if ((!submission.isNsfw()) || me.ccrama.redditslide.SettingValues.storeNSFWHistory) {
                        me.ccrama.redditslide.HasSeen.addSeen(submission.getFullName());
                        ((android.view.View) (getParent())).findViewById(me.ccrama.redditslide.R.id.title).setAlpha(0.54F);
                        ((android.view.View) (getParent())).findViewById(me.ccrama.redditslide.R.id.body).setAlpha(0.54F);
                    }
                }
                onLinkLongClick(submission.getUrl(), event);
            }
        };
    }

    public void setSecondSubtitle(android.widget.TextView v) {
        secondSubTitle = v;
    }

    public void setSecondTitle(android.widget.TextView v) {
        secondTitle = v;
    }

    public void setSubmission(final net.dean.jraw.models.Submission submission, final boolean full, java.lang.String baseSub, me.ccrama.redditslide.ContentType.Type type) {
        this.type = type;
        if (!lastDone.equals(submission.getFullName())) {
            lq = false;
            lastDone = submission.getFullName();
            backdrop.setImageResource(android.R.color.transparent);// reset the image view in case the placeholder is still visible

            thumbImage2.setImageResource(android.R.color.transparent);
            doImageAndText(submission, full, baseSub, false);
        }
    }

    public void setSubmissionNews(final net.dean.jraw.models.Submission submission, final boolean full, java.lang.String baseSub, me.ccrama.redditslide.ContentType.Type type) {
        this.type = type;
        if (!lastDone.equals(submission.getFullName())) {
            lq = false;
            lastDone = submission.getFullName();
            backdrop.setImageResource(android.R.color.transparent);// reset the image view in case the placeholder is still visible

            thumbImage2.setImageResource(android.R.color.transparent);
            doImageAndText(submission, full, baseSub, true);
        }
    }

    public void setThumbnail(android.widget.ImageView v) {
        thumbImage2 = v;
    }

    public void setUrl(java.lang.String url) {
    }

    public void setWrapArea(android.view.View v) {
        wrapArea = v;
        setSecondTitle(((android.widget.TextView) (v.findViewById(me.ccrama.redditslide.R.id.contenttitle))));
        setSecondSubtitle(((android.widget.TextView) (v.findViewById(me.ccrama.redditslide.R.id.contenturl))));
    }

    private java.lang.String getDomainName(java.lang.String url) throws java.net.URISyntaxException {
        java.net.URI uri = new java.net.URI(url);
        java.lang.String domain = uri.getHost();
        if ((domain != null) && (!domain.isEmpty())) {
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        } else {
            return "";
        }
    }

    private void init() {
        android.view.View.inflate(getContext(), me.ccrama.redditslide.R.layout.header_image_title_view, this);
        this.title = findViewById(me.ccrama.redditslide.R.id.textimage);
        this.info = findViewById(me.ccrama.redditslide.R.id.subtextimage);
        this.backdrop = findViewById(me.ccrama.redditslide.R.id.leadimage);
    }
}