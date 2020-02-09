package me.ccrama.redditslide.Views;
import java.util.Locale;
import me.ccrama.redditslide.ForceTouch.PeekViewActivity;
import me.ccrama.redditslide.Adapters.ProfileCommentViewHolder;
import me.ccrama.redditslide.Adapters.SubmissionViewHolder;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.util.SubmissionParser;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.TimeUtils;
import me.ccrama.redditslide.SpoilerRobotoTextView;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Visuals.FontPreferences;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.UserSubscriptions;
import java.util.List;
import java.util.Arrays;
import me.ccrama.redditslide.ActionStates;
import me.ccrama.redditslide.OpenRedditLink;
/**
 * Created by ccrama on 3/5/2015.
 */
public class RedditItemView extends android.widget.RelativeLayout {
    me.ccrama.redditslide.OpenRedditLink.RedditLinkType contentType;

    public RedditItemView(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RedditItemView(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RedditItemView(android.content.Context context) {
        super(context);
        init();
    }

    public void loadUrl(me.ccrama.redditslide.Views.PeekMediaView v, java.lang.String url, android.widget.ProgressBar progress) {
        this.progress = progress;
        android.net.Uri uri = me.ccrama.redditslide.OpenRedditLink.formatRedditUrl(url);
        if (uri == null) {
            v.doLoadLink(url);
        } else if (url.startsWith("np")) {
            uri = uri.buildUpon().authority(uri.getHost().substring(2)).build();
        }
        java.util.List<java.lang.String> parts = uri.getPathSegments();
        contentType = me.ccrama.redditslide.OpenRedditLink.getRedditLinkType(uri);
        switch (contentType) {
            case SHORTENED :
                {
                    new me.ccrama.redditslide.Views.RedditItemView.AsyncLoadSubmission(parts.get(0)).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                    break;
                }
            case LIVE :
                {
                    v.doLoadLink(url);
                }
                break;
            case WIKI :
                {
                    v.doLoadLink(url);
                    break;
                }
            case SEARCH :
                {
                    v.doLoadLink(url);
                    break;
                }
            case COMMENT_PERMALINK :
                {
                    java.lang.String submission = parts.get(3);
                    if (parts.size() >= 6) {
                        // is likely a comment
                        java.lang.String end = parts.get(5);
                        if (end.length() >= 3) {
                            new me.ccrama.redditslide.Views.RedditItemView.AsyncLoadComment(end).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            new me.ccrama.redditslide.Views.RedditItemView.AsyncLoadSubmission(submission).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    } else {
                        new me.ccrama.redditslide.Views.RedditItemView.AsyncLoadSubmission(submission).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                    break;
                }
            case SUBMISSION :
                {
                    new me.ccrama.redditslide.Views.RedditItemView.AsyncLoadSubmission(parts.get(3)).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                    break;
                }
            case SUBMISSION_WITHOUT_SUB :
                {
                    new me.ccrama.redditslide.Views.RedditItemView.AsyncLoadSubmission(parts.get(1)).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                    break;
                }
            case SUBREDDIT :
                {
                    new me.ccrama.redditslide.Views.RedditItemView.AsyncLoadSubreddit(parts.get(1)).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                    break;
                }
            case USER :
                {
                    java.lang.String name = parts.get(1);
                    new me.ccrama.redditslide.Views.RedditItemView.AsyncLoadProfile(name).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                    break;
                }
            case OTHER :
                {
                    v.doLoadLink(url);
                    break;
                }
        }
    }

    android.widget.ProgressBar progress;

    public class AsyncLoadProfile extends android.os.AsyncTask<java.lang.Void, java.lang.Void, net.dean.jraw.models.Account> {
        java.lang.String id;

        public AsyncLoadProfile(java.lang.String profileName) {
            this.id = profileName;
        }

        @java.lang.Override
        protected net.dean.jraw.models.Account doInBackground(java.lang.Void... params) {
            try {
                return me.ccrama.redditslide.Authentication.reddit.getUser(id);
            } catch (java.lang.Exception e) {
                return null;
            }
        }

        @java.lang.Override
        protected void onPostExecute(net.dean.jraw.models.Account account) {
            if ((account != null) && (account.getDataNode().has("is_suspended") && (!account.getDataNode().get("is_suspended").asBoolean()))) {
                android.view.View content = android.view.LayoutInflater.from(getContext()).inflate(me.ccrama.redditslide.R.layout.account_pop, me.ccrama.redditslide.Views.RedditItemView.this, false);
                android.widget.RelativeLayout.LayoutParams params = ((android.widget.RelativeLayout.LayoutParams) (content.getLayoutParams()));
                params.addRule(android.widget.RelativeLayout.CENTER_IN_PARENT);
                addView(content);
                doUser(account, content);
            }
            if (progress != null) {
                progress.setVisibility(android.view.View.GONE);
            }
        }
    }

    private void doUser(net.dean.jraw.models.Account account, android.view.View content) {
        java.lang.String name = account.getFullName();
        final android.widget.TextView title = ((android.widget.TextView) (content.findViewById(me.ccrama.redditslide.R.id.title)));
        title.setText(name);
        final int currentColor = me.ccrama.redditslide.Visuals.Palette.getColorUser(name);
        title.setBackgroundColor(currentColor);
        java.lang.String info = getContext().getString(me.ccrama.redditslide.R.string.profile_age, me.ccrama.redditslide.TimeUtils.getTimeSince(account.getCreated().getTime(), getContext()));
        ((android.widget.TextView) (content.findViewById(me.ccrama.redditslide.R.id.moreinfo))).setText(info);
        ((android.widget.TextView) (content.findViewById(me.ccrama.redditslide.R.id.commentkarma))).setText(java.lang.String.format(java.util.Locale.getDefault(), "%d", account.getCommentKarma()));
        ((android.widget.TextView) (content.findViewById(me.ccrama.redditslide.R.id.linkkarma))).setText(java.lang.String.format(java.util.Locale.getDefault(), "%d", account.getLinkKarma()));
    }

    public class AsyncLoadSubreddit extends android.os.AsyncTask<java.lang.Void, java.lang.Void, net.dean.jraw.models.Subreddit> {
        java.lang.String id;

        public AsyncLoadSubreddit(java.lang.String subredditName) {
            this.id = subredditName;
        }

        @java.lang.Override
        protected net.dean.jraw.models.Subreddit doInBackground(java.lang.Void... params) {
            try {
                return me.ccrama.redditslide.Authentication.reddit.getSubreddit(id);
            } catch (java.lang.Exception e) {
                return null;
            }
        }

        @java.lang.Override
        protected void onPostExecute(net.dean.jraw.models.Subreddit subreddit) {
            if (subreddit != null) {
                android.view.View content = android.view.LayoutInflater.from(getContext()).inflate(me.ccrama.redditslide.R.layout.subreddit_pop, me.ccrama.redditslide.Views.RedditItemView.this, false);
                android.widget.RelativeLayout.LayoutParams params = ((android.widget.RelativeLayout.LayoutParams) (content.getLayoutParams()));
                params.addRule(android.widget.RelativeLayout.CENTER_IN_PARENT);
                addView(content);
                doSidebar(subreddit, content);
            }
            if (progress != null) {
                progress.setVisibility(android.view.View.GONE);
            }
        }
    }

    private void doSidebar(net.dean.jraw.models.Subreddit subreddit, android.view.View content) {
        if (((!me.ccrama.redditslide.Authentication.isLoggedIn) && me.ccrama.redditslide.UserSubscriptions.getSubscriptions(getContext()).contains(subreddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH))) || (me.ccrama.redditslide.Authentication.isLoggedIn && subreddit.isUserSubscriber())) {
            ((android.support.v7.widget.AppCompatCheckBox) (content.findViewById(me.ccrama.redditslide.R.id.subscribed))).setChecked(true);
        }
        content.findViewById(me.ccrama.redditslide.R.id.header_sub).setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getColor(subreddit.getDisplayName()));
        ((android.widget.TextView) (content.findViewById(me.ccrama.redditslide.R.id.sub_infotitle))).setText(subreddit.getDisplayName());
        if (!subreddit.getPublicDescription().isEmpty()) {
            content.findViewById(me.ccrama.redditslide.R.id.sub_title).setVisibility(android.view.View.VISIBLE);
            setViews(subreddit.getDataNode().get("public_description_html").asText(), subreddit.getDisplayName().toLowerCase(java.util.Locale.ENGLISH), ((me.ccrama.redditslide.SpoilerRobotoTextView) (content.findViewById(me.ccrama.redditslide.R.id.sub_title))), ((me.ccrama.redditslide.Views.CommentOverflow) (content.findViewById(me.ccrama.redditslide.R.id.sub_title_overflow))));
        } else {
            content.findViewById(me.ccrama.redditslide.R.id.sub_title).setVisibility(android.view.View.GONE);
        }
        if (subreddit.getDataNode().has("icon_img") && (!subreddit.getDataNode().get("icon_img").asText().isEmpty())) {
            ((me.ccrama.redditslide.Reddit) (((me.ccrama.redditslide.ForceTouch.PeekViewActivity) (getContext())).getApplication())).getImageLoader().displayImage(subreddit.getDataNode().get("icon_img").asText(), ((android.widget.ImageView) (content.findViewById(me.ccrama.redditslide.R.id.subimage))));
        } else {
            content.findViewById(me.ccrama.redditslide.R.id.subimage).setVisibility(android.view.View.GONE);
        }
        ((android.widget.TextView) (content.findViewById(me.ccrama.redditslide.R.id.subscribers))).setText(getContext().getString(me.ccrama.redditslide.R.string.subreddit_subscribers_string, subreddit.getLocalizedSubscriberCount()));
        content.findViewById(me.ccrama.redditslide.R.id.subscribers).setVisibility(android.view.View.VISIBLE);
        ((android.widget.TextView) (content.findViewById(me.ccrama.redditslide.R.id.active_users))).setText(getContext().getString(me.ccrama.redditslide.R.string.subreddit_active_users_string_new, subreddit.getLocalizedAccountsActive()));
        content.findViewById(me.ccrama.redditslide.R.id.active_users).setVisibility(android.view.View.VISIBLE);
    }

    public class AsyncLoadComment extends android.os.AsyncTask<java.lang.Void, java.lang.Void, net.dean.jraw.models.Comment> {
        java.lang.String id;

        public AsyncLoadComment(java.lang.String commentId) {
            this.id = commentId;
        }

        @java.lang.Override
        protected net.dean.jraw.models.Comment doInBackground(java.lang.Void... params) {
            try {
                return ((net.dean.jraw.models.Comment) (me.ccrama.redditslide.Authentication.reddit.get("t1_" + id).get(0)));
            } catch (java.lang.Exception e) {
                return null;
            }
        }

        @java.lang.Override
        protected void onPostExecute(net.dean.jraw.models.Comment comment) {
            if (comment != null) {
                me.ccrama.redditslide.util.LogUtil.v("Adding view");
                android.view.View content = android.view.LayoutInflater.from(getContext()).inflate(me.ccrama.redditslide.R.layout.profile_comment, me.ccrama.redditslide.Views.RedditItemView.this, false);
                android.widget.RelativeLayout.LayoutParams params = ((android.widget.RelativeLayout.LayoutParams) (content.getLayoutParams()));
                params.addRule(android.widget.RelativeLayout.CENTER_IN_PARENT);
                addView(content);
                doComment(comment, content);
            }
            if (progress != null) {
                progress.setVisibility(android.view.View.GONE);
            }
        }
    }

    public class AsyncLoadSubmission extends android.os.AsyncTask<java.lang.Void, java.lang.Void, net.dean.jraw.models.Submission> {
        java.lang.String id;

        public AsyncLoadSubmission(java.lang.String submissionId) {
            this.id = submissionId;
        }

        @java.lang.Override
        protected net.dean.jraw.models.Submission doInBackground(java.lang.Void... params) {
            try {
                return me.ccrama.redditslide.Authentication.reddit.getSubmission(id);
            } catch (java.lang.Exception e) {
                return null;
            }
        }

        @java.lang.Override
        protected void onPostExecute(net.dean.jraw.models.Submission submission) {
            if (submission != null) {
                android.view.View content = me.ccrama.redditslide.Views.CreateCardView.CreateView(me.ccrama.redditslide.Views.RedditItemView.this);
                android.widget.RelativeLayout.LayoutParams params = ((android.widget.RelativeLayout.LayoutParams) (content.getLayoutParams()));
                params.addRule(android.widget.RelativeLayout.CENTER_IN_PARENT);
                addView(content);
                doSubmission(submission, content);
            }
            if (progress != null) {
                progress.setVisibility(android.view.View.GONE);
            }
        }
    }

    public void doComment(net.dean.jraw.models.Comment comment, android.view.View content) {
        me.ccrama.redditslide.Adapters.ProfileCommentViewHolder holder = new me.ccrama.redditslide.Adapters.ProfileCommentViewHolder(content);
        java.lang.String scoreText;
        if (comment.isScoreHidden()) {
            scoreText = ("[" + getContext().getString(me.ccrama.redditslide.R.string.misc_score_hidden).toUpperCase()) + "]";
        } else {
            scoreText = java.lang.String.format(java.util.Locale.getDefault(), "%d", comment.getScore());
        }
        android.text.SpannableStringBuilder score = new android.text.SpannableStringBuilder(scoreText);
        if ((score == null) || score.toString().isEmpty()) {
            score = new android.text.SpannableStringBuilder("0");
        }
        if (!scoreText.contains("[")) {
            score.append(java.lang.String.format(java.util.Locale.getDefault(), " %s", getContext().getResources().getQuantityString(me.ccrama.redditslide.R.plurals.points, comment.getScore())));
        }
        holder.score.setText(score);
        if (me.ccrama.redditslide.Authentication.isLoggedIn) {
            if (me.ccrama.redditslide.ActionStates.getVoteDirection(comment) == net.dean.jraw.models.VoteDirection.UPVOTE) {
                holder.score.setTextColor(getContext().getResources().getColor(me.ccrama.redditslide.R.color.md_orange_500));
            } else if (me.ccrama.redditslide.ActionStates.getVoteDirection(comment) == net.dean.jraw.models.VoteDirection.DOWNVOTE) {
                holder.score.setTextColor(getContext().getResources().getColor(me.ccrama.redditslide.R.color.md_blue_500));
            } else {
                holder.score.setTextColor(holder.time.getCurrentTextColor());
            }
        }
        java.lang.String spacer = getContext().getString(me.ccrama.redditslide.R.string.submission_properties_seperator);
        android.text.SpannableStringBuilder titleString = new android.text.SpannableStringBuilder();
        java.lang.String timeAgo = me.ccrama.redditslide.TimeUtils.getTimeAgo(comment.getCreated().getTime(), getContext());
        java.lang.String time = ((timeAgo == null) || timeAgo.isEmpty()) ? "just now" : timeAgo;// some users were crashing here

        time = time + (comment.getEditDate() != null ? (" (edit " + me.ccrama.redditslide.TimeUtils.getTimeAgo(comment.getEditDate().getTime(), getContext())) + ")" : "");
        titleString.append(time);
        titleString.append(spacer);
        if (comment.getSubredditName() != null) {
            java.lang.String subname = comment.getSubredditName();
            android.text.SpannableStringBuilder subreddit = new android.text.SpannableStringBuilder("/r/" + subname);
            if (me.ccrama.redditslide.SettingValues.colorSubName && (me.ccrama.redditslide.Visuals.Palette.getColor(subname) != me.ccrama.redditslide.Visuals.Palette.getDefaultColor())) {
                subreddit.setSpan(new android.text.style.ForegroundColorSpan(me.ccrama.redditslide.Visuals.Palette.getColor(subname)), 0, subreddit.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                subreddit.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, subreddit.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            titleString.append(subreddit);
        }
        holder.time.setText(titleString);
        setViews(comment.getDataNode().get("body_html").asText(), comment.getSubredditName(), holder);
        int type = new me.ccrama.redditslide.Visuals.FontPreferences(getContext()).getFontTypeComment().getTypeface();
        android.graphics.Typeface typeface;
        if (type >= 0) {
            typeface = com.devspark.robototextview.RobotoTypefaces.obtainTypeface(getContext(), type);
        } else {
            typeface = android.graphics.Typeface.DEFAULT;
        }
        holder.content.setTypeface(typeface);
        if (((comment.getTimesSilvered() > 0) || (comment.getTimesGilded() > 0)) || (comment.getTimesPlatinized() > 0)) {
            android.content.res.TypedArray a = getContext().obtainStyledAttributes(new me.ccrama.redditslide.Visuals.FontPreferences(getContext()).getPostFontStyle().getResId(), me.ccrama.redditslide.R.styleable.FontStyle);
            int fontsize = ((int) (a.getDimensionPixelSize(me.ccrama.redditslide.R.styleable.FontStyle_font_cardtitle, -1) * 0.75));
            a.recycle();
            holder.gild.setVisibility(android.view.View.VISIBLE);
            // Add silver, gold, platinum icons and counts in that order
            if (comment.getTimesSilvered() > 0) {
                final java.lang.String timesSilvered = (comment.getTimesSilvered() == 1) ? "" : "\u200ax" + java.lang.Integer.toString(comment.getTimesSilvered());
                android.text.SpannableStringBuilder silvered = new android.text.SpannableStringBuilder(("\u00a0\u2605" + timesSilvered) + "\u00a0");
                android.graphics.Bitmap image = android.graphics.BitmapFactory.decodeResource(getContext().getResources(), me.ccrama.redditslide.R.drawable.silver);
                float aspectRatio = ((float) ((1.0 * image.getWidth()) / image.getHeight()));
                if (image != null) {
                    image.recycle();
                }
                image = android.graphics.Bitmap.createScaledBitmap(image, ((int) (java.lang.Math.ceil(fontsize * aspectRatio))), ((int) (java.lang.Math.ceil(fontsize))), true);
                silvered.setSpan(new android.text.style.ImageSpan(getContext(), image, android.text.style.ImageSpan.ALIGN_BASELINE), 0, 2, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                silvered.setSpan(new android.text.style.RelativeSizeSpan(0.75F), 3, silvered.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ((android.widget.TextView) (holder.gild)).append(silvered);
            }
            if (comment.getTimesGilded() > 0) {
                final java.lang.String timesGilded = (comment.getTimesGilded() == 1) ? "" : "\u200ax" + java.lang.Integer.toString(comment.getTimesGilded());
                android.text.SpannableStringBuilder gilded = new android.text.SpannableStringBuilder(("\u00a0\u2605" + timesGilded) + "\u00a0");
                android.graphics.Bitmap image = android.graphics.BitmapFactory.decodeResource(getContext().getResources(), me.ccrama.redditslide.R.drawable.gold);
                float aspectRatio = ((float) ((1.0 * image.getWidth()) / image.getHeight()));
                if (image != null) {
                    image.recycle();
                }
                image = android.graphics.Bitmap.createScaledBitmap(image, ((int) (java.lang.Math.ceil(fontsize * aspectRatio))), ((int) (java.lang.Math.ceil(fontsize))), true);
                gilded.setSpan(new android.text.style.ImageSpan(getContext(), image, android.text.style.ImageSpan.ALIGN_BASELINE), 0, 2, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                gilded.setSpan(new android.text.style.RelativeSizeSpan(0.75F), 3, gilded.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ((android.widget.TextView) (holder.gild)).append(gilded);
            }
            if (comment.getTimesPlatinized() > 0) {
                final java.lang.String timesPlatinized = (comment.getTimesPlatinized() == 1) ? "" : "\u200ax" + java.lang.Integer.toString(comment.getTimesPlatinized());
                android.text.SpannableStringBuilder platinized = new android.text.SpannableStringBuilder(("\u00a0\u2605" + timesPlatinized) + "\u00a0");
                android.graphics.Bitmap image = android.graphics.BitmapFactory.decodeResource(getContext().getResources(), me.ccrama.redditslide.R.drawable.platinum);
                float aspectRatio = ((float) ((1.0 * image.getWidth()) / image.getHeight()));
                if (image != null) {
                    image.recycle();
                }
                image = android.graphics.Bitmap.createScaledBitmap(image, ((int) (java.lang.Math.ceil(fontsize * aspectRatio))), ((int) (java.lang.Math.ceil(fontsize))), true);
                platinized.setSpan(new android.text.style.ImageSpan(getContext(), image, android.text.style.ImageSpan.ALIGN_BASELINE), 0, 2, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                platinized.setSpan(new android.text.style.RelativeSizeSpan(0.75F), 3, platinized.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ((android.widget.TextView) (holder.gild)).append(platinized);
            }
        } else if (holder.gild.getVisibility() == android.view.View.VISIBLE) {
            holder.gild.setVisibility(android.view.View.GONE);
        }
        if (comment.getSubmissionTitle() != null) {
            holder.title.setText(android.text.Html.fromHtml(comment.getSubmissionTitle()));
        } else {
            holder.title.setText(android.text.Html.fromHtml(comment.getAuthor()));
        }
    }

    public void doSubmission(net.dean.jraw.models.Submission submission, android.view.View content) {
        final me.ccrama.redditslide.Adapters.SubmissionViewHolder holder = new me.ccrama.redditslide.Adapters.SubmissionViewHolder(content);
        me.ccrama.redditslide.Views.CreateCardView.resetColorCard(holder.itemView);
        if (submission.getSubredditName() != null) {
            me.ccrama.redditslide.Views.CreateCardView.colorCard(submission.getSubredditName().toLowerCase(java.util.Locale.ENGLISH), holder.itemView, "no_subreddit", false);
        }
        new me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder().populateSubmissionViewHolder(holder, submission, ((me.ccrama.redditslide.ForceTouch.PeekViewActivity) (getContext())), false, false, null, null, false, false, null, null);
    }

    private void init() {
    }

    private void setViews(java.lang.String rawHTML, java.lang.String subreddit, me.ccrama.redditslide.SpoilerRobotoTextView firstTextView, me.ccrama.redditslide.Views.CommentOverflow commentOverflow) {
        if (rawHTML.isEmpty()) {
            return;
        }
        java.util.List<java.lang.String> blocks = me.ccrama.redditslide.util.SubmissionParser.getBlocks(rawHTML);
        int startIndex = 0;
        // the <div class="md"> case is when the body contains a table or code block first
        if (!blocks.get(0).equals("<div class=\"md\">")) {
            firstTextView.setVisibility(android.view.View.VISIBLE);
            firstTextView.setTextHtml(blocks.get(0), subreddit);
            startIndex = 1;
        } else {
            firstTextView.setText("");
            firstTextView.setVisibility(android.view.View.GONE);
        }
        if (blocks.size() > 1) {
            if (startIndex == 0) {
                commentOverflow.setViews(blocks, subreddit);
            } else {
                commentOverflow.setViews(blocks.subList(startIndex, blocks.size()), subreddit);
            }
            me.ccrama.redditslide.Views.SidebarLayout sidebar = ((me.ccrama.redditslide.Views.SidebarLayout) (findViewById(me.ccrama.redditslide.R.id.drawer_layout)));
            for (int i = 0; i < commentOverflow.getChildCount(); i++) {
                android.view.View maybeScrollable = commentOverflow.getChildAt(i);
                if (maybeScrollable instanceof android.widget.HorizontalScrollView) {
                    sidebar.addScrollable(maybeScrollable);
                }
            }
        } else {
            commentOverflow.removeAllViews();
        }
    }

    private void setViews(java.lang.String rawHTML, java.lang.String subredditName, me.ccrama.redditslide.Adapters.ProfileCommentViewHolder holder) {
        if (rawHTML.isEmpty()) {
            return;
        }
        java.util.List<java.lang.String> blocks = me.ccrama.redditslide.util.SubmissionParser.getBlocks(rawHTML);
        int startIndex = 0;
        // the <div class="md"> case is when the body contains a table or code block first
        if (!blocks.get(0).equals("<div class=\"md\">")) {
            holder.content.setVisibility(android.view.View.VISIBLE);
            holder.content.setTextHtml(blocks.get(0), subredditName);
            startIndex = 1;
        } else {
            holder.content.setText("");
            holder.content.setVisibility(android.view.View.GONE);
        }
        if (blocks.size() > 1) {
            if (startIndex == 0) {
                holder.overflow.setViews(blocks, subredditName);
            } else {
                holder.overflow.setViews(blocks.subList(startIndex, blocks.size()), subredditName);
            }
        } else {
            holder.overflow.removeAllViews();
        }
    }
}