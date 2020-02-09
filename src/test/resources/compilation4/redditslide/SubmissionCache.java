package me.ccrama.redditslide;
import me.ccrama.redditslide.Toolbox.Toolbox;
import java.util.Locale;
import me.ccrama.redditslide.Visuals.FontPreferences;
import me.ccrama.redditslide.Visuals.Palette;
import java.util.ArrayList;
import me.ccrama.redditslide.Toolbox.ToolboxUI;
import me.ccrama.redditslide.Views.RoundedBackgroundSpan;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.WeakHashMap;
import me.ccrama.redditslide.Adapters.CommentAdapterHelper;
/**
 * Created by carlo_000 on 4/22/2016.
 */
public class SubmissionCache {
    private static java.util.WeakHashMap<java.lang.String, android.text.SpannableStringBuilder> titles;

    private static java.util.WeakHashMap<java.lang.String, android.text.SpannableStringBuilder> info;

    private static java.util.WeakHashMap<java.lang.String, android.text.SpannableStringBuilder> crosspost;

    public static void cacheSubmissions(java.util.List<net.dean.jraw.models.Submission> submissions, android.content.Context mContext, java.lang.String baseSub) {
        me.ccrama.redditslide.SubmissionCache.cacheInfo(submissions, mContext, baseSub);
    }

    public static android.text.SpannableStringBuilder getCrosspostLine(net.dean.jraw.models.Submission s, android.content.Context mContext) {
        if (me.ccrama.redditslide.SubmissionCache.crosspost == null)
            me.ccrama.redditslide.SubmissionCache.crosspost = new java.util.WeakHashMap<>();

        if (me.ccrama.redditslide.SubmissionCache.crosspost.containsKey(s.getFullName())) {
            return me.ccrama.redditslide.SubmissionCache.crosspost.get(s.getFullName());
        } else {
            return me.ccrama.redditslide.SubmissionCache.getCrosspostSpannable(s, mContext);
        }
    }

    private static void cacheInfo(java.util.List<net.dean.jraw.models.Submission> submissions, android.content.Context mContext, java.lang.String baseSub) {
        if (me.ccrama.redditslide.SubmissionCache.titles == null)
            me.ccrama.redditslide.SubmissionCache.titles = new java.util.WeakHashMap<>();

        if (me.ccrama.redditslide.SubmissionCache.info == null)
            me.ccrama.redditslide.SubmissionCache.info = new java.util.WeakHashMap<>();

        if (me.ccrama.redditslide.SubmissionCache.crosspost == null)
            me.ccrama.redditslide.SubmissionCache.crosspost = new java.util.WeakHashMap<>();

        for (net.dean.jraw.models.Submission submission : submissions) {
            me.ccrama.redditslide.SubmissionCache.titles.put(submission.getFullName(), me.ccrama.redditslide.SubmissionCache.getTitleSpannable(submission, mContext));
            me.ccrama.redditslide.SubmissionCache.info.put(submission.getFullName(), me.ccrama.redditslide.SubmissionCache.getInfoSpannable(submission, mContext, baseSub));
            me.ccrama.redditslide.SubmissionCache.crosspost.put(submission.getFullName(), me.ccrama.redditslide.SubmissionCache.getCrosspostLine(submission, mContext));
        }
    }

    public static void updateInfoSpannable(net.dean.jraw.models.Submission changed, android.content.Context mContext, java.lang.String baseSub) {
        me.ccrama.redditslide.SubmissionCache.info.put(changed.getFullName(), me.ccrama.redditslide.SubmissionCache.getInfoSpannable(changed, mContext, baseSub));
    }

    public static void updateTitleFlair(net.dean.jraw.models.Submission s, java.lang.String flair, android.content.Context c) {
        me.ccrama.redditslide.SubmissionCache.titles.put(s.getFullName(), me.ccrama.redditslide.SubmissionCache.getTitleSpannable(s, flair, c));
    }

    public static android.text.SpannableStringBuilder getTitleLine(net.dean.jraw.models.Submission s, android.content.Context mContext) {
        if (me.ccrama.redditslide.SubmissionCache.titles == null)
            me.ccrama.redditslide.SubmissionCache.titles = new java.util.WeakHashMap<>();

        if (me.ccrama.redditslide.SubmissionCache.titles.containsKey(s.getFullName())) {
            return me.ccrama.redditslide.SubmissionCache.titles.get(s.getFullName());
        } else {
            return me.ccrama.redditslide.SubmissionCache.getTitleSpannable(s, mContext);
        }
    }

    public static android.text.SpannableStringBuilder getInfoLine(net.dean.jraw.models.Submission s, android.content.Context mContext, java.lang.String baseSub) {
        if (me.ccrama.redditslide.SubmissionCache.info == null)
            me.ccrama.redditslide.SubmissionCache.info = new java.util.WeakHashMap<>();

        if (me.ccrama.redditslide.SubmissionCache.info.containsKey(s.getFullName())) {
            return me.ccrama.redditslide.SubmissionCache.info.get(s.getFullName());
        } else {
            return me.ccrama.redditslide.SubmissionCache.getInfoSpannable(s, mContext, baseSub);
        }
    }

    private static android.text.SpannableStringBuilder getCrosspostSpannable(net.dean.jraw.models.Submission s, android.content.Context mContext) {
        java.lang.String spacer = mContext.getString(me.ccrama.redditslide.R.string.submission_properties_seperator);
        android.text.SpannableStringBuilder titleString = new android.text.SpannableStringBuilder("Crosspost" + spacer);
        com.fasterxml.jackson.databind.JsonNode json = s.getDataNode();
        if (((!json.has("crosspost_parent_list")) || (json.get("crosspost_parent_list") == null)) || (json.get("crosspost_parent_list").get(0) == null)) {
            // is not a crosspost
            return null;
        }
        json = json.get("crosspost_parent_list").get(0);
        if (json.has("subreddit")) {
            java.lang.String subname = json.get("subreddit").asText().toLowerCase(java.util.Locale.ENGLISH);
            android.text.SpannableStringBuilder subreddit = new android.text.SpannableStringBuilder(("/r/" + subname) + spacer);
            if ((me.ccrama.redditslide.SettingValues.colorSubName && (me.ccrama.redditslide.Visuals.Palette.getColor(subname) != me.ccrama.redditslide.Visuals.Palette.getDefaultColor())) || (me.ccrama.redditslide.SettingValues.colorSubName && (me.ccrama.redditslide.Visuals.Palette.getColor(subname) != me.ccrama.redditslide.Visuals.Palette.getDefaultColor()))) {
                if (!me.ccrama.redditslide.SettingValues.colorEverywhere) {
                    subreddit.setSpan(new android.text.style.ForegroundColorSpan(me.ccrama.redditslide.Visuals.Palette.getColor(subname)), 0, subreddit.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    subreddit.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, subreddit.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            titleString.append(subreddit);
        }
        android.text.SpannableStringBuilder author = new android.text.SpannableStringBuilder(json.get("author").asText() + " ");
        int authorcolor = me.ccrama.redditslide.Visuals.Palette.getFontColorUser(json.get("author").asText());
        if (authorcolor != 0) {
            author.setSpan(new android.text.style.ForegroundColorSpan(authorcolor), 0, author.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        titleString.append(author);
        if (me.ccrama.redditslide.UserTags.isUserTagged(json.get("author").asText())) {
            android.text.SpannableStringBuilder pinned = new android.text.SpannableStringBuilder((" " + me.ccrama.redditslide.UserTags.getUserTag(json.get("author").asText())) + " ");
            pinned.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(mContext, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_blue_500, false), 0, pinned.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            titleString.append(pinned);
        }
        if (me.ccrama.redditslide.UserSubscriptions.friends.contains(json.get("author").asText())) {
            android.text.SpannableStringBuilder pinned = new android.text.SpannableStringBuilder((" " + mContext.getString(me.ccrama.redditslide.R.string.profile_friend)) + " ");
            pinned.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(mContext, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_deep_orange_500, false), 0, pinned.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            titleString.append(pinned);
        }
        return titleString;
    }

    private static android.text.SpannableStringBuilder getInfoSpannable(net.dean.jraw.models.Submission submission, android.content.Context mContext, java.lang.String baseSub) {
        java.lang.String spacer = mContext.getString(me.ccrama.redditslide.R.string.submission_properties_seperator);
        android.text.SpannableStringBuilder titleString = new android.text.SpannableStringBuilder();
        android.text.SpannableStringBuilder subreddit = new android.text.SpannableStringBuilder((" /r/" + submission.getSubredditName()) + " ");
        if (submission.getSubredditName() == null) {
            subreddit = new android.text.SpannableStringBuilder("Promoted ");
        }
        java.lang.String subname;
        if (submission.getSubredditName() != null) {
            subname = submission.getSubredditName().toLowerCase(java.util.Locale.ENGLISH);
        } else {
            subname = "";
        }
        if ((baseSub == null) || baseSub.isEmpty())
            baseSub = subname;

        if ((me.ccrama.redditslide.SettingValues.colorSubName && (me.ccrama.redditslide.Visuals.Palette.getColor(subname) != me.ccrama.redditslide.Visuals.Palette.getDefaultColor())) || (baseSub.equals("nomatching") && (me.ccrama.redditslide.SettingValues.colorSubName && (me.ccrama.redditslide.Visuals.Palette.getColor(subname) != me.ccrama.redditslide.Visuals.Palette.getDefaultColor())))) {
            boolean secondary = (((((baseSub.equalsIgnoreCase("frontpage") || baseSub.equalsIgnoreCase("all")) || baseSub.equalsIgnoreCase("popular")) || baseSub.equalsIgnoreCase("friends")) || baseSub.equalsIgnoreCase("mod")) || baseSub.contains(".")) || baseSub.contains("+");
            if (((!secondary) && (!me.ccrama.redditslide.SettingValues.colorEverywhere)) || secondary) {
                subreddit.setSpan(new android.text.style.ForegroundColorSpan(me.ccrama.redditslide.Visuals.Palette.getColor(subname)), 0, subreddit.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                subreddit.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, subreddit.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        titleString.append(subreddit);
        titleString.append(spacer);
        try {
            java.lang.String time = me.ccrama.redditslide.TimeUtils.getTimeAgo(submission.getCreated().getTime(), mContext);
            titleString.append(time);
        } catch (java.lang.Exception e) {
            titleString.append("just now");
        }
        titleString.append(submission.getEdited() != null ? (" (edit " + me.ccrama.redditslide.TimeUtils.getTimeAgo(submission.getEdited().getTime(), mContext)) + ")" : "");
        titleString.append(spacer);
        android.text.SpannableStringBuilder author = new android.text.SpannableStringBuilder((" " + submission.getAuthor()) + " ");
        int authorcolor = me.ccrama.redditslide.Visuals.Palette.getFontColorUser(submission.getAuthor());
        if (submission.getAuthor() != null) {
            if ((me.ccrama.redditslide.Authentication.name != null) && submission.getAuthor().toLowerCase(java.util.Locale.ENGLISH).equals(me.ccrama.redditslide.Authentication.name.toLowerCase(java.util.Locale.ENGLISH))) {
                author.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(mContext, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_deep_orange_300, false), 0, author.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (submission.getDistinguishedStatus() == net.dean.jraw.models.DistinguishedStatus.ADMIN) {
                author.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(mContext, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_red_300, false), 0, author.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (submission.getDistinguishedStatus() == net.dean.jraw.models.DistinguishedStatus.SPECIAL) {
                author.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(mContext, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_purple_300, false), 0, author.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (submission.getDistinguishedStatus() == net.dean.jraw.models.DistinguishedStatus.MODERATOR) {
                author.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(mContext, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_green_300, false), 0, author.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (authorcolor != 0) {
                author.setSpan(new android.text.style.ForegroundColorSpan(authorcolor), 0, author.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            titleString.append(author);
        }
        /* todo maybe?  titleString.append(((comment.hasBeenEdited() && comment.getEditDate() != null) ? " *" + TimeUtils.getTimeAgo(comment.getEditDate().getTime(), mContext) : ""));
        titleString.append("  ");
         */
        if (me.ccrama.redditslide.UserTags.isUserTagged(submission.getAuthor())) {
            android.text.SpannableStringBuilder pinned = new android.text.SpannableStringBuilder((" " + me.ccrama.redditslide.UserTags.getUserTag(submission.getAuthor())) + " ");
            pinned.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(mContext, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_blue_500, false), 0, pinned.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            titleString.append(" ");
            titleString.append(pinned);
        }
        if (me.ccrama.redditslide.UserSubscriptions.friends.contains(submission.getAuthor())) {
            android.text.SpannableStringBuilder pinned = new android.text.SpannableStringBuilder((" " + mContext.getString(me.ccrama.redditslide.R.string.profile_friend)) + " ");
            pinned.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(mContext, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_deep_orange_500, false), 0, pinned.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            titleString.append(" ");
            titleString.append(pinned);
        }
        me.ccrama.redditslide.Toolbox.ToolboxUI.appendToolboxNote(mContext, titleString, submission.getSubredditName(), submission.getAuthor());
        /* too big, might add later todo
        if (submission.getAuthorFlair() != null && submission.getAuthorFlair().getText() != null && !submission.getAuthorFlair().getText().isEmpty()) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = mContext.getTheme();
        theme.resolveAttribute(R.attr.activity_background, typedValue, true);
        int color = typedValue.data;
        SpannableStringBuilder pinned = new SpannableStringBuilder(" " + submission.getAuthorFlair().getText() + " ");
        pinned.setSpan(new RoundedBackgroundSpan(holder.title.getCurrentTextColor(), color, false, mContext), 0, pinned.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        titleString.append(pinned);
        titleString.append(" ");
        }



        if (holder.leadImage.getVisibility() == View.GONE && !full) {
        String text = "";

        switch (ContentType.getContentType(submission)) {
        case NSFW_IMAGE:
        text = mContext.getString(R.string.type_nsfw_img);
        break;

        case NSFW_GIF:
        case NSFW_GFY:
        text = mContext.getString(R.string.type_nsfw_gif);
        break;

        case REDDIT:
        text = mContext.getString(R.string.type_reddit);
        break;

        case LINK:
        case IMAGE_LINK:
        text = mContext.getString(R.string.type_link);
        break;

        case NSFW_LINK:
        text = mContext.getString(R.string.type_nsfw_link);

        break;
        case STREAMABLE:
        text = ("Streamable");
        break;
        case SELF:
        text = ("Selftext");
        break;

        case ALBUM:
        text = mContext.getString(R.string.type_album);
        break;

        case IMAGE:
        text = mContext.getString(R.string.type_img);
        break;
        case IMGUR:
        text = mContext.getString(R.string.type_imgur);
        break;
        case GFY:
        case GIF:
        case NONE_GFY:
        case NONE_GIF:
        text = mContext.getString(R.string.type_gif);
        break;

        case NONE:
        text = mContext.getString(R.string.type_title_only);
        break;

        case NONE_IMAGE:
        text = mContext.getString(R.string.type_img);
        break;

        case VIDEO:
        text = mContext.getString(R.string.type_vid);
        break;

        case EMBEDDED:
        text = mContext.getString(R.string.type_emb);
        break;

        case NONE_URL:
        text = mContext.getString(R.string.type_link);
        break;
        }
        if(!text.isEmpty()) {
        titleString.append(" \n");
        text = text.toUpperCase();
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = mContext.getTheme();
        theme.resolveAttribute(R.attr.activity_background, typedValue, true);
        int color = typedValue.data;
        SpannableStringBuilder pinned = new SpannableStringBuilder(" " + text + " ");
        pinned.setSpan(new RoundedBackgroundSpan(holder.title.getCurrentTextColor(), color, false, mContext), 0, pinned.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        titleString.append(pinned);
        }
        }
         */
        if (me.ccrama.redditslide.SettingValues.showDomain) {
            titleString.append(spacer);
            titleString.append(submission.getDomain());
        }
        if (me.ccrama.redditslide.SettingValues.typeInfoLine) {
            titleString.append(spacer);
            android.text.SpannableStringBuilder s = new android.text.SpannableStringBuilder(me.ccrama.redditslide.ContentType.getContentDescription(submission, mContext));
            s.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, s.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            titleString.append(s);
        }
        if (me.ccrama.redditslide.SettingValues.votesInfoLine) {
            titleString.append("\n ");
            android.text.SpannableStringBuilder s = new android.text.SpannableStringBuilder((((submission.getScore() + java.lang.String.format(java.util.Locale.getDefault(), " %s", mContext.getResources().getQuantityString(me.ccrama.redditslide.R.plurals.points, submission.getScore()))) + spacer) + submission.getCommentCount()) + java.lang.String.format(java.util.Locale.getDefault(), " %s", mContext.getResources().getQuantityString(me.ccrama.redditslide.R.plurals.comments, submission.getCommentCount())));
            s.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, s.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (me.ccrama.redditslide.SettingValues.commentLastVisit) {
                final int more = me.ccrama.redditslide.LastComments.commentsSince(submission);
                s.append(more > 0 ? ("(+" + more) + ")" : "");
            }
            titleString.append(s);
        }
        if (me.ccrama.redditslide.SubmissionCache.removed.contains(submission.getFullName()) || ((submission.getBannedBy() != null) && (!me.ccrama.redditslide.SubmissionCache.approved.contains(submission.getFullName())))) {
            titleString.append(me.ccrama.redditslide.Adapters.CommentAdapterHelper.createRemovedLine(submission.getBannedBy() == null ? me.ccrama.redditslide.Authentication.name : submission.getBannedBy(), mContext));
        } else if (me.ccrama.redditslide.SubmissionCache.approved.contains(submission.getFullName()) || ((submission.getApprovedBy() != null) && (!me.ccrama.redditslide.SubmissionCache.removed.contains(submission.getFullName())))) {
            titleString.append(me.ccrama.redditslide.Adapters.CommentAdapterHelper.createApprovedLine(submission.getApprovedBy() == null ? me.ccrama.redditslide.Authentication.name : submission.getApprovedBy(), mContext));
        }
        return titleString;
    }

    private static android.text.SpannableStringBuilder getTitleSpannable(net.dean.jraw.models.Submission submission, java.lang.String flairOverride, android.content.Context mContext) {
        android.text.SpannableStringBuilder titleString = new android.text.SpannableStringBuilder();
        titleString.append(android.text.Html.fromHtml(submission.getTitle()));
        if (submission.isStickied()) {
            android.text.SpannableStringBuilder pinned = new android.text.SpannableStringBuilder(("\u00a0" + mContext.getString(me.ccrama.redditslide.R.string.submission_stickied).toUpperCase()) + "\u00a0");
            pinned.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(mContext, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_green_300, true), 0, pinned.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            titleString.append(" ");
            titleString.append(pinned);
        }
        if (((submission.getTimesSilvered() > 0) || (submission.getTimesGilded() > 0)) || (submission.getTimesPlatinized() > 0)) {
            android.content.res.TypedArray a = mContext.obtainStyledAttributes(new me.ccrama.redditslide.Visuals.FontPreferences(mContext).getPostFontStyle().getResId(), me.ccrama.redditslide.R.styleable.FontStyle);
            int fontsize = ((int) (a.getDimensionPixelSize(me.ccrama.redditslide.R.styleable.FontStyle_font_cardtitle, -1) * 0.75));
            a.recycle();
            // Add silver, gold, platinum icons and counts in that order
            if (submission.getTimesSilvered() > 0) {
                final java.lang.String timesSilvered = (submission.getTimesSilvered() == 1) ? "" : "\u200ax" + java.lang.Integer.toString(submission.getTimesSilvered());
                android.text.SpannableStringBuilder silvered = new android.text.SpannableStringBuilder(("\u00a0\u2605" + timesSilvered) + "\u00a0");
                android.graphics.Bitmap image = android.graphics.BitmapFactory.decodeResource(mContext.getResources(), me.ccrama.redditslide.R.drawable.silver);
                float aspectRatio = ((float) ((1.0 * image.getWidth()) / image.getHeight()));
                if (image != null) {
                    image.recycle();
                }
                image = android.graphics.Bitmap.createScaledBitmap(image, ((int) (java.lang.Math.ceil(fontsize * aspectRatio))), ((int) (java.lang.Math.ceil(fontsize))), true);
                silvered.setSpan(new android.text.style.ImageSpan(mContext, image, android.text.style.ImageSpan.ALIGN_BASELINE), 0, 2, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                silvered.setSpan(new android.text.style.RelativeSizeSpan(0.75F), 3, silvered.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                titleString.append(" ");
                titleString.append(silvered);
            }
            if (submission.getTimesGilded() > 0) {
                final java.lang.String timesGilded = (submission.getTimesGilded() == 1) ? "" : "\u200ax" + java.lang.Integer.toString(submission.getTimesGilded());
                android.text.SpannableStringBuilder gilded = new android.text.SpannableStringBuilder(("\u00a0\u2605" + timesGilded) + "\u00a0");
                android.graphics.Bitmap image = android.graphics.BitmapFactory.decodeResource(mContext.getResources(), me.ccrama.redditslide.R.drawable.gold);
                float aspectRatio = ((float) ((1.0 * image.getWidth()) / image.getHeight()));
                if (image != null) {
                    image.recycle();
                }
                image = android.graphics.Bitmap.createScaledBitmap(image, ((int) (java.lang.Math.ceil(fontsize * aspectRatio))), ((int) (java.lang.Math.ceil(fontsize))), true);
                gilded.setSpan(new android.text.style.ImageSpan(mContext, image, android.text.style.ImageSpan.ALIGN_BASELINE), 0, 2, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                gilded.setSpan(new android.text.style.RelativeSizeSpan(0.75F), 3, gilded.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                titleString.append(" ");
                titleString.append(gilded);
            }
            if (submission.getTimesPlatinized() > 0) {
                final java.lang.String timesPlatinized = (submission.getTimesPlatinized() == 1) ? "" : "\u200ax" + java.lang.Integer.toString(submission.getTimesPlatinized());
                android.text.SpannableStringBuilder platinized = new android.text.SpannableStringBuilder(("\u00a0\u2605" + timesPlatinized) + "\u00a0");
                android.graphics.Bitmap image = android.graphics.BitmapFactory.decodeResource(mContext.getResources(), me.ccrama.redditslide.R.drawable.platinum);
                float aspectRatio = ((float) ((1.0 * image.getWidth()) / image.getHeight()));
                if (image != null) {
                    image.recycle();
                }
                image = android.graphics.Bitmap.createScaledBitmap(image, ((int) (java.lang.Math.ceil(fontsize * aspectRatio))), ((int) (java.lang.Math.ceil(fontsize))), true);
                platinized.setSpan(new android.text.style.ImageSpan(mContext, image, android.text.style.ImageSpan.ALIGN_BASELINE), 0, 2, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                platinized.setSpan(new android.text.style.RelativeSizeSpan(0.75F), 3, platinized.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                titleString.append(" ");
                titleString.append(platinized);
            }
        }
        if (submission.isNsfw()) {
            android.text.SpannableStringBuilder pinned = new android.text.SpannableStringBuilder("\u00a0NSFW\u00a0");
            pinned.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(mContext, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_red_300, true), 0, pinned.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            titleString.append(" ");
            titleString.append(pinned);
        }
        if (submission.getDataNode().get("spoiler").asBoolean()) {
            android.text.SpannableStringBuilder pinned = new android.text.SpannableStringBuilder("\u00a0SPOILER\u00a0");
            pinned.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(mContext, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_grey_600, true), 0, pinned.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            titleString.append(" ");
            titleString.append(pinned);
        }
        if (submission.getDataNode().get("is_original_content").asBoolean()) {
            android.text.SpannableStringBuilder pinned = new android.text.SpannableStringBuilder("\u00a0OC\u00a0");
            pinned.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(mContext, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_blue_500, true), 0, pinned.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            titleString.append(" ");
            titleString.append(pinned);
        }
        if ((((submission.getSubmissionFlair().getText() != null) && (!submission.getSubmissionFlair().getText().isEmpty())) || (flairOverride != null)) || (submission.getSubmissionFlair().getCssClass() != null)) {
            android.util.TypedValue typedValue = new android.util.TypedValue();
            android.content.res.Resources.Theme theme = mContext.getTheme();
            theme.resolveAttribute(me.ccrama.redditslide.R.attr.activity_background, typedValue, false);
            int color = typedValue.data;
            theme.resolveAttribute(me.ccrama.redditslide.R.attr.fontColor, typedValue, false);
            int font = typedValue.data;
            java.lang.String flairString;
            if (flairOverride != null) {
                flairString = flairOverride;
            } else if (((submission.getSubmissionFlair().getText() == null) || submission.getSubmissionFlair().getText().isEmpty()) && (submission.getSubmissionFlair().getCssClass() != null)) {
                flairString = submission.getSubmissionFlair().getCssClass();
            } else {
                flairString = submission.getSubmissionFlair().getText();
            }
            android.text.SpannableStringBuilder pinned = new android.text.SpannableStringBuilder(("\u00a0" + android.text.Html.fromHtml(flairString)) + "\u00a0");
            pinned.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(font, color, true, mContext), 0, pinned.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            titleString.append(" ");
            titleString.append(pinned);
        }
        return titleString;
    }

    public static java.util.ArrayList<java.lang.String> removed = new java.util.ArrayList<>();

    public static java.util.ArrayList<java.lang.String> approved = new java.util.ArrayList<>();

    private static android.text.SpannableStringBuilder getTitleSpannable(net.dean.jraw.models.Submission submission, android.content.Context mContext) {
        return me.ccrama.redditslide.SubmissionCache.getTitleSpannable(submission, null, mContext);
    }

    public static void evictAll() {
        me.ccrama.redditslide.SubmissionCache.info = new java.util.WeakHashMap<>();
    }
}