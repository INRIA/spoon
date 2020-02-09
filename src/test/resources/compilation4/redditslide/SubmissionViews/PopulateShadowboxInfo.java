package me.ccrama.redditslide.SubmissionViews;
import java.util.Locale;
import me.ccrama.redditslide.Vote;
import me.ccrama.redditslide.Views.AnimateHelper;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.Views.TitleTextView;
import me.ccrama.redditslide.Activities.Profile;
import me.ccrama.redditslide.Activities.SubredditView;
import me.ccrama.redditslide.TimeUtils;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.HasSeen;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.util.LinkUtil;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.Views.RoundedBackgroundSpan;
import me.ccrama.redditslide.ActionStates;
/**
 * Created by carlo_000 on 2/27/2016.
 */
public class PopulateShadowboxInfo {
    public static void doActionbar(final net.dean.jraw.models.Submission s, final android.view.View rootView, final android.app.Activity c, boolean extras) {
        android.widget.TextView title = rootView.findViewById(me.ccrama.redditslide.R.id.title);
        android.widget.TextView desc = rootView.findViewById(me.ccrama.redditslide.R.id.desc);
        java.lang.String distingush = "";
        if (s != null) {
            if (s.getDistinguishedStatus() == net.dean.jraw.models.DistinguishedStatus.MODERATOR)
                distingush = "[M]";
            else if (s.getDistinguishedStatus() == net.dean.jraw.models.DistinguishedStatus.ADMIN)
                distingush = "[A]";

            title.setText(android.text.Html.fromHtml(s.getTitle()));
            java.lang.String spacer = c.getString(me.ccrama.redditslide.R.string.submission_properties_seperator);
            android.text.SpannableStringBuilder titleString = new android.text.SpannableStringBuilder();
            android.text.SpannableStringBuilder subreddit = new android.text.SpannableStringBuilder((" /r/" + s.getSubredditName()) + " ");
            java.lang.String subname = s.getSubredditName().toLowerCase(java.util.Locale.ENGLISH);
            if (me.ccrama.redditslide.SettingValues.colorSubName && (me.ccrama.redditslide.Visuals.Palette.getColor(subname) != me.ccrama.redditslide.Visuals.Palette.getDefaultColor())) {
                subreddit.setSpan(new android.text.style.ForegroundColorSpan(me.ccrama.redditslide.Visuals.Palette.getColor(subname)), 0, subreddit.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                subreddit.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, subreddit.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            titleString.append(subreddit);
            titleString.append(distingush);
            titleString.append(spacer);
            titleString.append(me.ccrama.redditslide.TimeUtils.getTimeAgo(s.getCreated().getTime(), c));
            desc.setText(titleString);
            ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.comments))).setText(java.lang.String.format(java.util.Locale.getDefault(), "%d", s.getCommentCount()));
            ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setText(java.lang.String.format(java.util.Locale.getDefault(), "%d", s.getScore()));
            if (extras) {
                final android.widget.ImageView downvotebutton = rootView.findViewById(me.ccrama.redditslide.R.id.downvote);
                final android.widget.ImageView upvotebutton = rootView.findViewById(me.ccrama.redditslide.R.id.upvote);
                if (s.isArchived() || s.isLocked()) {
                    downvotebutton.setVisibility(android.view.View.GONE);
                    upvotebutton.setVisibility(android.view.View.GONE);
                } else if (me.ccrama.redditslide.Authentication.isLoggedIn && me.ccrama.redditslide.Authentication.didOnline) {
                    if (me.ccrama.redditslide.SettingValues.actionbarVisible && (downvotebutton.getVisibility() != android.view.View.VISIBLE)) {
                        downvotebutton.setVisibility(android.view.View.VISIBLE);
                        upvotebutton.setVisibility(android.view.View.VISIBLE);
                    }
                    switch (me.ccrama.redditslide.ActionStates.getVoteDirection(s)) {
                        case UPVOTE :
                            {
                                ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setTextColor(android.support.v4.content.ContextCompat.getColor(c, me.ccrama.redditslide.R.color.md_orange_500));
                                upvotebutton.setColorFilter(android.support.v4.content.ContextCompat.getColor(c, me.ccrama.redditslide.R.color.md_orange_500), android.graphics.PorterDuff.Mode.SRC_ATOP);
                                ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setTypeface(null, android.graphics.Typeface.BOLD);
                                ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setText(java.lang.String.format(java.util.Locale.getDefault(), "%d", s.getScore() + (s.getAuthor().equals(me.ccrama.redditslide.Authentication.name) ? 0 : 1)));
                                downvotebutton.setColorFilter(android.graphics.Color.WHITE, android.graphics.PorterDuff.Mode.SRC_ATOP);
                                break;
                            }
                        case DOWNVOTE :
                            {
                                ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setTextColor(android.support.v4.content.ContextCompat.getColor(c, me.ccrama.redditslide.R.color.md_blue_500));
                                downvotebutton.setColorFilter(android.support.v4.content.ContextCompat.getColor(c, me.ccrama.redditslide.R.color.md_blue_500), android.graphics.PorterDuff.Mode.SRC_ATOP);
                                ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setTypeface(null, android.graphics.Typeface.BOLD);
                                ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setText(java.lang.String.format(java.util.Locale.getDefault(), "%d", s.getScore() + (s.getAuthor().equals(me.ccrama.redditslide.Authentication.name) ? 0 : -1)));
                                upvotebutton.setColorFilter(android.graphics.Color.WHITE, android.graphics.PorterDuff.Mode.SRC_ATOP);
                                break;
                            }
                        case NO_VOTE :
                            {
                                ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setTextColor(((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.comments))).getCurrentTextColor());
                                ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setText(java.lang.String.format(java.util.Locale.getDefault(), "%d", s.getScore()));
                                ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setTypeface(null, android.graphics.Typeface.NORMAL);
                                downvotebutton.setColorFilter(android.graphics.Color.WHITE, android.graphics.PorterDuff.Mode.SRC_ATOP);
                                upvotebutton.setColorFilter(android.graphics.Color.WHITE, android.graphics.PorterDuff.Mode.SRC_ATOP);
                                break;
                            }
                    }
                }
                if (me.ccrama.redditslide.Authentication.isLoggedIn && me.ccrama.redditslide.Authentication.didOnline) {
                    if (me.ccrama.redditslide.ActionStates.isSaved(s)) {
                        ((android.widget.ImageView) (rootView.findViewById(me.ccrama.redditslide.R.id.save))).setColorFilter(android.support.v4.content.ContextCompat.getColor(c, me.ccrama.redditslide.R.color.md_amber_500), android.graphics.PorterDuff.Mode.SRC_ATOP);
                    } else {
                        ((android.widget.ImageView) (rootView.findViewById(me.ccrama.redditslide.R.id.save))).setColorFilter(android.graphics.Color.WHITE, android.graphics.PorterDuff.Mode.SRC_ATOP);
                    }
                    rootView.findViewById(me.ccrama.redditslide.R.id.save).setOnClickListener(new android.view.View.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.view.View v) {
                            new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
                                @java.lang.Override
                                protected java.lang.Void doInBackground(java.lang.Void... params) {
                                    try {
                                        if (me.ccrama.redditslide.ActionStates.isSaved(s)) {
                                            new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).unsave(s);
                                            me.ccrama.redditslide.ActionStates.setSaved(s, false);
                                        } else {
                                            new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).save(s);
                                            me.ccrama.redditslide.ActionStates.setSaved(s, true);
                                        }
                                    } catch (net.dean.jraw.ApiException e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                }

                                @java.lang.Override
                                protected void onPostExecute(java.lang.Void aVoid) {
                                    ((com.sothree.slidinguppanel.SlidingUpPanelLayout) (rootView.findViewById(me.ccrama.redditslide.R.id.sliding_layout))).setPanelState(com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED);
                                    if (me.ccrama.redditslide.ActionStates.isSaved(s)) {
                                        ((android.widget.ImageView) (rootView.findViewById(me.ccrama.redditslide.R.id.save))).setColorFilter(android.support.v4.content.ContextCompat.getColor(c, me.ccrama.redditslide.R.color.md_amber_500), android.graphics.PorterDuff.Mode.SRC_ATOP);
                                        me.ccrama.redditslide.Views.AnimateHelper.setFlashAnimation(rootView, rootView.findViewById(me.ccrama.redditslide.R.id.save), android.support.v4.content.ContextCompat.getColor(c, me.ccrama.redditslide.R.color.md_amber_500));
                                    } else {
                                        ((android.widget.ImageView) (rootView.findViewById(me.ccrama.redditslide.R.id.save))).setColorFilter(android.graphics.Color.WHITE, android.graphics.PorterDuff.Mode.SRC_ATOP);
                                    }
                                }
                            }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    });
                }
                if ((!me.ccrama.redditslide.Authentication.isLoggedIn) || (!me.ccrama.redditslide.Authentication.didOnline)) {
                    rootView.findViewById(me.ccrama.redditslide.R.id.save).setVisibility(android.view.View.GONE);
                }
                try {
                    final android.widget.TextView points = rootView.findViewById(me.ccrama.redditslide.R.id.score);
                    final android.widget.TextView comments = rootView.findViewById(me.ccrama.redditslide.R.id.comments);
                    if (me.ccrama.redditslide.Authentication.isLoggedIn && me.ccrama.redditslide.Authentication.didOnline) {
                        {
                            downvotebutton.setOnClickListener(new android.view.View.OnClickListener() {
                                @java.lang.Override
                                public void onClick(android.view.View view) {
                                    ((com.sothree.slidinguppanel.SlidingUpPanelLayout) (rootView.findViewById(me.ccrama.redditslide.R.id.sliding_layout))).setPanelState(com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED);
                                    if (me.ccrama.redditslide.SettingValues.storeHistory) {
                                        if ((!s.isNsfw()) || me.ccrama.redditslide.SettingValues.storeNSFWHistory) {
                                            me.ccrama.redditslide.HasSeen.addSeen(s.getFullName());
                                        }
                                    }
                                    if (me.ccrama.redditslide.ActionStates.getVoteDirection(s) != net.dean.jraw.models.VoteDirection.DOWNVOTE) {
                                        // has not been downvoted
                                        points.setTextColor(android.support.v4.content.ContextCompat.getColor(c, me.ccrama.redditslide.R.color.md_blue_500));
                                        downvotebutton.setColorFilter(android.support.v4.content.ContextCompat.getColor(c, me.ccrama.redditslide.R.color.md_blue_500), android.graphics.PorterDuff.Mode.SRC_ATOP);
                                        upvotebutton.setColorFilter(android.graphics.Color.WHITE, android.graphics.PorterDuff.Mode.SRC_ATOP);
                                        me.ccrama.redditslide.Views.AnimateHelper.setFlashAnimation(rootView, downvotebutton, android.support.v4.content.ContextCompat.getColor(c, me.ccrama.redditslide.R.color.md_blue_500));
                                        ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setTypeface(null, android.graphics.Typeface.BOLD);
                                        final int downvoteScore = (s.getScore() == 0) ? 0 : s.getScore() - 1;// if a post is at 0 votes, keep it at 0 when downvoting

                                        ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setText(java.lang.String.format(java.util.Locale.getDefault(), "%d", downvoteScore));
                                        new me.ccrama.redditslide.Vote(false, points, c).execute(s);
                                        me.ccrama.redditslide.ActionStates.setVoteDirection(s, net.dean.jraw.models.VoteDirection.DOWNVOTE);
                                    } else {
                                        points.setTextColor(comments.getCurrentTextColor());
                                        new me.ccrama.redditslide.Vote(points, c).execute(s);
                                        ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setTypeface(null, android.graphics.Typeface.NORMAL);
                                        ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setText(java.lang.String.format(java.util.Locale.getDefault(), "%d", s.getScore()));
                                        me.ccrama.redditslide.ActionStates.setVoteDirection(s, net.dean.jraw.models.VoteDirection.NO_VOTE);
                                        downvotebutton.setColorFilter(android.graphics.Color.WHITE, android.graphics.PorterDuff.Mode.SRC_ATOP);
                                    }
                                }
                            });
                        }
                        {
                            upvotebutton.setOnClickListener(new android.view.View.OnClickListener() {
                                @java.lang.Override
                                public void onClick(android.view.View view) {
                                    ((com.sothree.slidinguppanel.SlidingUpPanelLayout) (rootView.findViewById(me.ccrama.redditslide.R.id.sliding_layout))).setPanelState(com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED);
                                    if (me.ccrama.redditslide.SettingValues.storeHistory) {
                                        if ((!s.isNsfw()) || me.ccrama.redditslide.SettingValues.storeNSFWHistory) {
                                            me.ccrama.redditslide.HasSeen.addSeen(s.getFullName());
                                        }
                                    }
                                    if (me.ccrama.redditslide.ActionStates.getVoteDirection(s) != net.dean.jraw.models.VoteDirection.UPVOTE) {
                                        // has not been upvoted
                                        points.setTextColor(android.support.v4.content.ContextCompat.getColor(c, me.ccrama.redditslide.R.color.md_orange_500));
                                        upvotebutton.setColorFilter(android.support.v4.content.ContextCompat.getColor(c, me.ccrama.redditslide.R.color.md_orange_500), android.graphics.PorterDuff.Mode.SRC_ATOP);
                                        downvotebutton.setColorFilter(android.graphics.Color.WHITE, android.graphics.PorterDuff.Mode.SRC_ATOP);
                                        me.ccrama.redditslide.Views.AnimateHelper.setFlashAnimation(rootView, upvotebutton, android.support.v4.content.ContextCompat.getColor(c, me.ccrama.redditslide.R.color.md_orange_500));
                                        ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setTypeface(null, android.graphics.Typeface.BOLD);
                                        ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setText(java.lang.String.format(java.util.Locale.getDefault(), "%d", s.getScore() + 1));
                                        new me.ccrama.redditslide.Vote(true, points, c).execute(s);
                                        me.ccrama.redditslide.ActionStates.setVoteDirection(s, net.dean.jraw.models.VoteDirection.UPVOTE);
                                    } else {
                                        points.setTextColor(comments.getCurrentTextColor());
                                        new me.ccrama.redditslide.Vote(points, c).execute(s);
                                        ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setTypeface(null, android.graphics.Typeface.NORMAL);
                                        ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setText(java.lang.String.format(java.util.Locale.getDefault(), "%d", s.getScore()));
                                        me.ccrama.redditslide.ActionStates.setVoteDirection(s, net.dean.jraw.models.VoteDirection.NO_VOTE);
                                        upvotebutton.setColorFilter(android.graphics.Color.WHITE, android.graphics.PorterDuff.Mode.SRC_ATOP);
                                    }
                                }
                            });
                        }
                    } else {
                        upvotebutton.setVisibility(android.view.View.GONE);
                        downvotebutton.setVisibility(android.view.View.GONE);
                    }
                } catch (java.lang.Exception ignored) {
                    ignored.printStackTrace();
                }
                rootView.findViewById(me.ccrama.redditslide.R.id.menu).setOnClickListener(new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        me.ccrama.redditslide.SubmissionViews.PopulateShadowboxInfo.showBottomSheet(c, s, rootView);
                    }
                });
            }
        }
    }

    public static void doActionbar(final net.dean.jraw.models.CommentNode node, final android.view.View rootView, final android.app.Activity c, boolean extras) {
        final net.dean.jraw.models.Comment s = node.getComment();
        me.ccrama.redditslide.Views.TitleTextView title = rootView.findViewById(me.ccrama.redditslide.R.id.title);
        android.widget.TextView desc = rootView.findViewById(me.ccrama.redditslide.R.id.desc);
        java.lang.String distingush = "";
        if (s != null) {
            if (s.getDistinguishedStatus() == net.dean.jraw.models.DistinguishedStatus.MODERATOR)
                distingush = "[M]";
            else if (s.getDistinguishedStatus() == net.dean.jraw.models.DistinguishedStatus.ADMIN)
                distingush = "[A]";

            android.text.SpannableStringBuilder commentTitle = new android.text.SpannableStringBuilder();
            android.text.SpannableStringBuilder level = new android.text.SpannableStringBuilder();
            if (!node.isTopLevel()) {
                level.append(("[" + node.getDepth()) + "] ");
                level.setSpan(new android.text.style.RelativeSizeSpan(0.7F), 0, level.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                commentTitle.append(level);
            }
            commentTitle.append(android.text.Html.fromHtml(s.getDataNode().get("body_html").asText().trim()));
            title.setTextHtml(commentTitle);
            title.setMaxLines(3);
            java.lang.String spacer = c.getString(me.ccrama.redditslide.R.string.submission_properties_seperator);
            android.text.SpannableStringBuilder titleString = new android.text.SpannableStringBuilder();
            android.text.SpannableStringBuilder author = new android.text.SpannableStringBuilder((" /u/" + s.getAuthor()) + " ");
            int authorcolor = me.ccrama.redditslide.Visuals.Palette.getFontColorUser(s.getAuthor());
            if ((me.ccrama.redditslide.Authentication.name != null) && s.getAuthor().toLowerCase(java.util.Locale.ENGLISH).equals(me.ccrama.redditslide.Authentication.name.toLowerCase(java.util.Locale.ENGLISH))) {
                author.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(c, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_deep_orange_300, false), 0, author.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if ((s.getDistinguishedStatus() == net.dean.jraw.models.DistinguishedStatus.MODERATOR) || (s.getDistinguishedStatus() == net.dean.jraw.models.DistinguishedStatus.ADMIN)) {
                author.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(c, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_green_300, false), 0, author.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (authorcolor != 0) {
                author.setSpan(new android.text.style.ForegroundColorSpan(authorcolor), 0, author.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            titleString.append(author);
            titleString.append(distingush);
            titleString.append(spacer);
            titleString.append(me.ccrama.redditslide.TimeUtils.getTimeAgo(s.getCreated().getTime(), c));
            desc.setText(titleString);
            ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setText(java.lang.String.format(java.util.Locale.getDefault(), "%d", s.getScore()));
            if (extras) {
                final android.widget.ImageView downvotebutton = rootView.findViewById(me.ccrama.redditslide.R.id.downvote);
                final android.widget.ImageView upvotebutton = rootView.findViewById(me.ccrama.redditslide.R.id.upvote);
                if (s.isArchived()) {
                    downvotebutton.setVisibility(android.view.View.GONE);
                    upvotebutton.setVisibility(android.view.View.GONE);
                } else if (me.ccrama.redditslide.Authentication.isLoggedIn && me.ccrama.redditslide.Authentication.didOnline) {
                    if (me.ccrama.redditslide.SettingValues.actionbarVisible && (downvotebutton.getVisibility() != android.view.View.VISIBLE)) {
                        downvotebutton.setVisibility(android.view.View.VISIBLE);
                        upvotebutton.setVisibility(android.view.View.VISIBLE);
                    }
                    switch (me.ccrama.redditslide.ActionStates.getVoteDirection(s)) {
                        case UPVOTE :
                            {
                                ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setTextColor(android.support.v4.content.ContextCompat.getColor(c, me.ccrama.redditslide.R.color.md_orange_500));
                                upvotebutton.setColorFilter(android.support.v4.content.ContextCompat.getColor(c, me.ccrama.redditslide.R.color.md_orange_500), android.graphics.PorterDuff.Mode.SRC_ATOP);
                                ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setTypeface(null, android.graphics.Typeface.BOLD);
                                ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setText(java.lang.String.format(java.util.Locale.getDefault(), "%d", s.getScore() + (s.getAuthor().equals(me.ccrama.redditslide.Authentication.name) ? 0 : 1)));
                                downvotebutton.setColorFilter(android.graphics.Color.WHITE, android.graphics.PorterDuff.Mode.SRC_ATOP);
                                break;
                            }
                        case DOWNVOTE :
                            {
                                ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setTextColor(android.support.v4.content.ContextCompat.getColor(c, me.ccrama.redditslide.R.color.md_blue_500));
                                downvotebutton.setColorFilter(android.support.v4.content.ContextCompat.getColor(c, me.ccrama.redditslide.R.color.md_blue_500), android.graphics.PorterDuff.Mode.SRC_ATOP);
                                ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setTypeface(null, android.graphics.Typeface.BOLD);
                                ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setText(java.lang.String.format(java.util.Locale.getDefault(), "%d", s.getScore() + (s.getAuthor().equals(me.ccrama.redditslide.Authentication.name) ? 0 : -1)));
                                upvotebutton.setColorFilter(android.graphics.Color.WHITE, android.graphics.PorterDuff.Mode.SRC_ATOP);
                                break;
                            }
                        case NO_VOTE :
                            {
                                ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setTextColor(((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.comments))).getCurrentTextColor());
                                ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setText(java.lang.String.format(java.util.Locale.getDefault(), "%d", s.getScore()));
                                ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setTypeface(null, android.graphics.Typeface.NORMAL);
                                downvotebutton.setColorFilter(android.graphics.Color.WHITE, android.graphics.PorterDuff.Mode.SRC_ATOP);
                                upvotebutton.setColorFilter(android.graphics.Color.WHITE, android.graphics.PorterDuff.Mode.SRC_ATOP);
                                break;
                            }
                    }
                }
                if (me.ccrama.redditslide.Authentication.isLoggedIn && me.ccrama.redditslide.Authentication.didOnline) {
                    if (me.ccrama.redditslide.ActionStates.isSaved(s)) {
                        ((android.widget.ImageView) (rootView.findViewById(me.ccrama.redditslide.R.id.save))).setColorFilter(android.support.v4.content.ContextCompat.getColor(c, me.ccrama.redditslide.R.color.md_amber_500), android.graphics.PorterDuff.Mode.SRC_ATOP);
                    } else {
                        ((android.widget.ImageView) (rootView.findViewById(me.ccrama.redditslide.R.id.save))).setColorFilter(android.graphics.Color.WHITE, android.graphics.PorterDuff.Mode.SRC_ATOP);
                    }
                    rootView.findViewById(me.ccrama.redditslide.R.id.save).setOnClickListener(new android.view.View.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.view.View v) {
                            new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
                                @java.lang.Override
                                protected java.lang.Void doInBackground(java.lang.Void... params) {
                                    try {
                                        if (me.ccrama.redditslide.ActionStates.isSaved(s)) {
                                            new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).unsave(s);
                                            me.ccrama.redditslide.ActionStates.setSaved(s, false);
                                        } else {
                                            new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).save(s);
                                            me.ccrama.redditslide.ActionStates.setSaved(s, true);
                                        }
                                    } catch (net.dean.jraw.ApiException e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                }

                                @java.lang.Override
                                protected void onPostExecute(java.lang.Void aVoid) {
                                    ((com.sothree.slidinguppanel.SlidingUpPanelLayout) (rootView.findViewById(me.ccrama.redditslide.R.id.sliding_layout))).setPanelState(com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED);
                                    if (me.ccrama.redditslide.ActionStates.isSaved(s)) {
                                        ((android.widget.ImageView) (rootView.findViewById(me.ccrama.redditslide.R.id.save))).setColorFilter(android.support.v4.content.ContextCompat.getColor(c, me.ccrama.redditslide.R.color.md_amber_500), android.graphics.PorterDuff.Mode.SRC_ATOP);
                                        me.ccrama.redditslide.Views.AnimateHelper.setFlashAnimation(rootView, rootView.findViewById(me.ccrama.redditslide.R.id.save), android.support.v4.content.ContextCompat.getColor(c, me.ccrama.redditslide.R.color.md_amber_500));
                                    } else {
                                        ((android.widget.ImageView) (rootView.findViewById(me.ccrama.redditslide.R.id.save))).setColorFilter(android.graphics.Color.WHITE, android.graphics.PorterDuff.Mode.SRC_ATOP);
                                    }
                                }
                            }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    });
                }
                if ((!me.ccrama.redditslide.Authentication.isLoggedIn) || (!me.ccrama.redditslide.Authentication.didOnline)) {
                    rootView.findViewById(me.ccrama.redditslide.R.id.save).setVisibility(android.view.View.GONE);
                }
                try {
                    final android.widget.TextView points = rootView.findViewById(me.ccrama.redditslide.R.id.score);
                    final android.widget.TextView comments = rootView.findViewById(me.ccrama.redditslide.R.id.comments);
                    if (me.ccrama.redditslide.Authentication.isLoggedIn && me.ccrama.redditslide.Authentication.didOnline) {
                        {
                            downvotebutton.setOnClickListener(new android.view.View.OnClickListener() {
                                @java.lang.Override
                                public void onClick(android.view.View view) {
                                    ((com.sothree.slidinguppanel.SlidingUpPanelLayout) (rootView.findViewById(me.ccrama.redditslide.R.id.sliding_layout))).setPanelState(com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED);
                                    if (me.ccrama.redditslide.ActionStates.getVoteDirection(s) != net.dean.jraw.models.VoteDirection.DOWNVOTE) {
                                        // has not been downvoted
                                        points.setTextColor(android.support.v4.content.ContextCompat.getColor(c, me.ccrama.redditslide.R.color.md_blue_500));
                                        downvotebutton.setColorFilter(android.support.v4.content.ContextCompat.getColor(c, me.ccrama.redditslide.R.color.md_blue_500), android.graphics.PorterDuff.Mode.SRC_ATOP);
                                        upvotebutton.setColorFilter(android.graphics.Color.WHITE, android.graphics.PorterDuff.Mode.SRC_ATOP);
                                        me.ccrama.redditslide.Views.AnimateHelper.setFlashAnimation(rootView, downvotebutton, android.support.v4.content.ContextCompat.getColor(c, me.ccrama.redditslide.R.color.md_blue_500));
                                        ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setTypeface(null, android.graphics.Typeface.BOLD);
                                        final int downvoteScore = (s.getScore() == 0) ? 0 : s.getScore() - 1;// if a post is at 0 votes, keep it at 0 when downvoting

                                        ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setText(java.lang.String.format(java.util.Locale.getDefault(), "%d", downvoteScore));
                                        new me.ccrama.redditslide.Vote(false, points, c).execute(s);
                                        me.ccrama.redditslide.ActionStates.setVoteDirection(s, net.dean.jraw.models.VoteDirection.DOWNVOTE);
                                    } else {
                                        points.setTextColor(comments.getCurrentTextColor());
                                        new me.ccrama.redditslide.Vote(points, c).execute(s);
                                        ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setTypeface(null, android.graphics.Typeface.NORMAL);
                                        ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setText(java.lang.String.format(java.util.Locale.getDefault(), "%d", s.getScore()));
                                        me.ccrama.redditslide.ActionStates.setVoteDirection(s, net.dean.jraw.models.VoteDirection.NO_VOTE);
                                        downvotebutton.setColorFilter(android.graphics.Color.WHITE, android.graphics.PorterDuff.Mode.SRC_ATOP);
                                    }
                                }
                            });
                        }
                        {
                            upvotebutton.setOnClickListener(new android.view.View.OnClickListener() {
                                @java.lang.Override
                                public void onClick(android.view.View view) {
                                    ((com.sothree.slidinguppanel.SlidingUpPanelLayout) (rootView.findViewById(me.ccrama.redditslide.R.id.sliding_layout))).setPanelState(com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED);
                                    if (me.ccrama.redditslide.ActionStates.getVoteDirection(s) != net.dean.jraw.models.VoteDirection.UPVOTE) {
                                        // has not been upvoted
                                        points.setTextColor(android.support.v4.content.ContextCompat.getColor(c, me.ccrama.redditslide.R.color.md_orange_500));
                                        upvotebutton.setColorFilter(android.support.v4.content.ContextCompat.getColor(c, me.ccrama.redditslide.R.color.md_orange_500), android.graphics.PorterDuff.Mode.SRC_ATOP);
                                        downvotebutton.setColorFilter(android.graphics.Color.WHITE, android.graphics.PorterDuff.Mode.SRC_ATOP);
                                        me.ccrama.redditslide.Views.AnimateHelper.setFlashAnimation(rootView, upvotebutton, android.support.v4.content.ContextCompat.getColor(c, me.ccrama.redditslide.R.color.md_orange_500));
                                        ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setTypeface(null, android.graphics.Typeface.BOLD);
                                        ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setText(java.lang.String.format(java.util.Locale.getDefault(), "%d", s.getScore() + 1));
                                        new me.ccrama.redditslide.Vote(true, points, c).execute(s);
                                        me.ccrama.redditslide.ActionStates.setVoteDirection(s, net.dean.jraw.models.VoteDirection.UPVOTE);
                                    } else {
                                        points.setTextColor(comments.getCurrentTextColor());
                                        new me.ccrama.redditslide.Vote(points, c).execute(s);
                                        ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setTypeface(null, android.graphics.Typeface.NORMAL);
                                        ((android.widget.TextView) (rootView.findViewById(me.ccrama.redditslide.R.id.score))).setText(java.lang.String.format(java.util.Locale.getDefault(), "%d", s.getScore()));
                                        me.ccrama.redditslide.ActionStates.setVoteDirection(s, net.dean.jraw.models.VoteDirection.NO_VOTE);
                                        upvotebutton.setColorFilter(android.graphics.Color.WHITE, android.graphics.PorterDuff.Mode.SRC_ATOP);
                                    }
                                }
                            });
                        }
                    } else {
                        upvotebutton.setVisibility(android.view.View.GONE);
                        downvotebutton.setVisibility(android.view.View.GONE);
                    }
                } catch (java.lang.Exception ignored) {
                    ignored.printStackTrace();
                }
            }
        }
    }

    public static void showBottomSheet(final android.app.Activity mContext, final net.dean.jraw.models.Submission submission, final android.view.View rootView) {
        int[] attrs = new int[]{ me.ccrama.redditslide.R.attr.tintColor };
        android.content.res.TypedArray ta = mContext.obtainStyledAttributes(attrs);
        int color = ta.getColor(0, android.graphics.Color.WHITE);
        android.graphics.drawable.Drawable profile = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.profile);
        final android.graphics.drawable.Drawable sub = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.sub);
        final android.graphics.drawable.Drawable report = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.report);
        android.graphics.drawable.Drawable copy = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.ic_content_copy);
        android.graphics.drawable.Drawable open = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.openexternal);
        android.graphics.drawable.Drawable link = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.link);
        android.graphics.drawable.Drawable reddit = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.commentchange);
        profile.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        sub.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        report.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        copy.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        open.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        link.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        reddit.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        ta.recycle();
        com.cocosw.bottomsheet.BottomSheet.Builder b = new com.cocosw.bottomsheet.BottomSheet.Builder(mContext).title(android.text.Html.fromHtml(submission.getTitle()));
        if (me.ccrama.redditslide.Authentication.didOnline) {
            b.sheet(1, profile, "/u/" + submission.getAuthor()).sheet(2, sub, "/r/" + submission.getSubredditName());
            if (me.ccrama.redditslide.Authentication.isLoggedIn) {
                b.sheet(12, report, mContext.getString(me.ccrama.redditslide.R.string.btn_report));
            }
        }
        b.sheet(7, open, mContext.getString(me.ccrama.redditslide.R.string.submission_link_extern)).sheet(4, link, mContext.getString(me.ccrama.redditslide.R.string.submission_share_permalink)).sheet(8, reddit, mContext.getString(me.ccrama.redditslide.R.string.submission_share_reddit_url)).listener(new android.content.DialogInterface.OnClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                switch (which) {
                    case 1 :
                        {
                            android.content.Intent i = new android.content.Intent(mContext, me.ccrama.redditslide.Activities.Profile.class);
                            i.putExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_PROFILE, submission.getAuthor());
                            mContext.startActivity(i);
                        }
                        break;
                    case 2 :
                        {
                            android.content.Intent i = new android.content.Intent(mContext, me.ccrama.redditslide.Activities.SubredditView.class);
                            i.putExtra(me.ccrama.redditslide.Activities.SubredditView.EXTRA_SUBREDDIT, submission.getSubredditName());
                            mContext.startActivityForResult(i, 14);
                        }
                        break;
                    case 7 :
                        me.ccrama.redditslide.util.LinkUtil.openExternally(submission.getUrl());
                        break;
                    case 4 :
                        me.ccrama.redditslide.Reddit.defaultShareText(submission.getTitle(), submission.getUrl(), mContext);
                        break;
                    case 12 :
                        final com.afollestad.materialdialogs.MaterialDialog reportDialog = new com.afollestad.materialdialogs.MaterialDialog.Builder(mContext).customView(me.ccrama.redditslide.R.layout.report_dialog, true).title(me.ccrama.redditslide.R.string.report_post).positiveText(me.ccrama.redditslide.R.string.btn_report).negativeText(me.ccrama.redditslide.R.string.btn_cancel).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                            @java.lang.Override
                            public void onClick(com.afollestad.materialdialogs.MaterialDialog dialog, com.afollestad.materialdialogs.DialogAction which) {
                                android.widget.RadioGroup reasonGroup = dialog.getCustomView().findViewById(me.ccrama.redditslide.R.id.report_reasons);
                                java.lang.String reportReason;
                                if (reasonGroup.getCheckedRadioButtonId() == me.ccrama.redditslide.R.id.report_other) {
                                    reportReason = ((android.widget.EditText) (dialog.getCustomView().findViewById(me.ccrama.redditslide.R.id.input_report_reason))).getText().toString();
                                } else {
                                    reportReason = ((android.widget.RadioButton) (reasonGroup.findViewById(reasonGroup.getCheckedRadioButtonId()))).getText().toString();
                                }
                                new me.ccrama.redditslide.SubmissionViews.PopulateShadowboxInfo.AsyncReportTask(submission, rootView).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR, reportReason);
                            }
                        }).build();
                        final android.widget.RadioGroup reasonGroup = reportDialog.getCustomView().findViewById(me.ccrama.redditslide.R.id.report_reasons);
                        reasonGroup.setOnCheckedChangeListener(new android.widget.RadioGroup.OnCheckedChangeListener() {
                            @java.lang.Override
                            public void onCheckedChanged(android.widget.RadioGroup group, int checkedId) {
                                if (checkedId == me.ccrama.redditslide.R.id.report_other)
                                    reportDialog.getCustomView().findViewById(me.ccrama.redditslide.R.id.input_report_reason).setVisibility(android.view.View.VISIBLE);
                                else
                                    reportDialog.getCustomView().findViewById(me.ccrama.redditslide.R.id.input_report_reason).setVisibility(android.view.View.GONE);

                            }
                        });
                        // Load sub's report reasons and show the appropriate ones
                        new android.os.AsyncTask<java.lang.Void, java.lang.Void, net.dean.jraw.models.Ruleset>() {
                            @java.lang.Override
                            protected net.dean.jraw.models.Ruleset doInBackground(java.lang.Void... voids) {
                                net.dean.jraw.models.Ruleset rules = me.ccrama.redditslide.Authentication.reddit.getRules(submission.getSubredditName());
                                return rules;
                            }

                            @java.lang.Override
                            protected void onPostExecute(net.dean.jraw.models.Ruleset rules) {
                                reportDialog.getCustomView().findViewById(me.ccrama.redditslide.R.id.report_loading).setVisibility(android.view.View.GONE);
                                if (rules.getSubredditRules().size() > 0) {
                                    android.widget.TextView subHeader = new android.widget.TextView(mContext);
                                    subHeader.setText(mContext.getString(me.ccrama.redditslide.R.string.report_sub_rules, submission.getSubredditName()));
                                    reasonGroup.addView(subHeader, reasonGroup.getChildCount() - 2);
                                }
                                for (net.dean.jraw.models.SubredditRule rule : rules.getSubredditRules()) {
                                    if ((rule.getKind() == net.dean.jraw.models.SubredditRule.RuleKind.LINK) || (rule.getKind() == net.dean.jraw.models.SubredditRule.RuleKind.ALL)) {
                                        android.widget.RadioButton btn = new android.widget.RadioButton(mContext);
                                        btn.setText(rule.getViolationReason());
                                        reasonGroup.addView(btn, reasonGroup.getChildCount() - 2);
                                        btn.getLayoutParams().width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
                                    }
                                }
                                if (rules.getSiteRules().size() > 0) {
                                    android.widget.TextView siteHeader = new android.widget.TextView(mContext);
                                    siteHeader.setText(me.ccrama.redditslide.R.string.report_site_rules);
                                    reasonGroup.addView(siteHeader, reasonGroup.getChildCount() - 2);
                                }
                                for (java.lang.String rule : rules.getSiteRules()) {
                                    android.widget.RadioButton btn = new android.widget.RadioButton(mContext);
                                    btn.setText(rule);
                                    reasonGroup.addView(btn, reasonGroup.getChildCount() - 2);
                                    btn.getLayoutParams().width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
                                }
                            }
                        }.execute();
                        reportDialog.show();
                        break;
                    case 8 :
                        if (me.ccrama.redditslide.SettingValues.shareLongLink) {
                            me.ccrama.redditslide.Reddit.defaultShareText(submission.getTitle(), "https://reddit.com" + submission.getPermalink(), mContext);
                        } else {
                            me.ccrama.redditslide.Reddit.defaultShareText(submission.getTitle(), "https://redd.it/" + submission.getId(), mContext);
                        }
                        break;
                    case 6 :
                        {
                            android.content.ClipboardManager clipboard = ((android.content.ClipboardManager) (mContext.getSystemService(android.content.Context.CLIPBOARD_SERVICE)));
                            android.content.ClipData clip = android.content.ClipData.newPlainText("Link", submission.getUrl());
                            clipboard.setPrimaryClip(clip);
                            android.widget.Toast.makeText(mContext, me.ccrama.redditslide.R.string.submission_link_copied, android.widget.Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });
        b.show();
    }

    public static class AsyncReportTask extends android.os.AsyncTask<java.lang.String, java.lang.Void, java.lang.Void> {
        private net.dean.jraw.models.Submission submission;

        private android.view.View contextView;

        public AsyncReportTask(net.dean.jraw.models.Submission submission, android.view.View contextView) {
            this.submission = submission;
            this.contextView = contextView;
        }

        @java.lang.Override
        protected java.lang.Void doInBackground(java.lang.String... reason) {
            try {
                new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).report(submission, reason[0]);
            } catch (net.dean.jraw.ApiException e) {
                e.printStackTrace();
            }
            return null;
        }

        @java.lang.Override
        protected void onPostExecute(java.lang.Void aVoid) {
            android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(contextView, me.ccrama.redditslide.R.string.msg_report_sent, android.support.design.widget.Snackbar.LENGTH_SHORT);
            android.view.View view = s.getView();
            android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(android.graphics.Color.WHITE);
            s.show();
        }
    }
}