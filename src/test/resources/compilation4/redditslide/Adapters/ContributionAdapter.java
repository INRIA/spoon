/**
 * Created by ccrama on 3/22/2015.
 */
package me.ccrama.redditslide.Adapters;
import me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager;
import java.util.Locale;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.util.SubmissionParser;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.Activities.Profile;
import me.ccrama.redditslide.Activities.SubredditView;
import me.ccrama.redditslide.Activities.Website;
import me.ccrama.redditslide.TimeUtils;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Visuals.FontPreferences;
import me.ccrama.redditslide.HasSeen;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder;
import me.ccrama.redditslide.Views.CreateCardView;
import me.ccrama.redditslide.util.LinkUtil;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.Hidden;
import java.util.List;
import me.ccrama.redditslide.ActionStates;
import me.ccrama.redditslide.OpenRedditLink;
public class ContributionAdapter extends android.support.v7.widget.RecyclerView.Adapter<android.support.v7.widget.RecyclerView.ViewHolder> implements me.ccrama.redditslide.Adapters.BaseAdapter {
    private final int SPACER = 6;

    private static final int COMMENT = 1;

    public final android.app.Activity mContext;

    private final android.support.v7.widget.RecyclerView listView;

    private final java.lang.Boolean isHiddenPost;

    public me.ccrama.redditslide.Adapters.GeneralPosts dataSet;

    public ContributionAdapter(android.app.Activity mContext, me.ccrama.redditslide.Adapters.GeneralPosts dataSet, android.support.v7.widget.RecyclerView listView) {
        this.mContext = mContext;
        this.listView = listView;
        this.dataSet = dataSet;
        this.isHiddenPost = false;
    }

    public ContributionAdapter(android.app.Activity mContext, me.ccrama.redditslide.Adapters.GeneralPosts dataSet, android.support.v7.widget.RecyclerView listView, java.lang.Boolean isHiddenPost) {
        this.mContext = mContext;
        this.listView = listView;
        this.dataSet = dataSet;
        this.isHiddenPost = isHiddenPost;
    }

    private final int LOADING_SPINNER = 5;

    private final int NO_MORE = 3;

    @java.lang.Override
    public int getItemViewType(int position) {
        if ((position == 0) && (!dataSet.posts.isEmpty())) {
            return SPACER;
        } else if (!dataSet.posts.isEmpty()) {
            position -= 1;
        }
        if (((position == dataSet.posts.size()) && (!dataSet.posts.isEmpty())) && (!dataSet.nomore)) {
            return LOADING_SPINNER;
        } else if ((position == dataSet.posts.size()) && dataSet.nomore) {
            return NO_MORE;
        }
        if (dataSet.posts.get(position) instanceof net.dean.jraw.models.Comment)
            return me.ccrama.redditslide.Adapters.ContributionAdapter.COMMENT;

        return 2;
    }

    @java.lang.Override
    public android.support.v7.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup viewGroup, int i) {
        if (i == SPACER) {
            android.view.View v = android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.spacer, viewGroup, false);
            return new me.ccrama.redditslide.Adapters.ContributionAdapter.SpacerViewHolder(v);
        } else if (i == me.ccrama.redditslide.Adapters.ContributionAdapter.COMMENT) {
            android.view.View v = android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.profile_comment, viewGroup, false);
            return new me.ccrama.redditslide.Adapters.ProfileCommentViewHolder(v);
        } else if (i == LOADING_SPINNER) {
            android.view.View v = android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.loadingmore, viewGroup, false);
            return new me.ccrama.redditslide.Adapters.ContributionAdapter.SubmissionFooterViewHolder(v);
        } else if (i == NO_MORE) {
            android.view.View v = android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.nomoreposts, viewGroup, false);
            return new me.ccrama.redditslide.Adapters.ContributionAdapter.SubmissionFooterViewHolder(v);
        } else {
            android.view.View v = me.ccrama.redditslide.Views.CreateCardView.CreateView(viewGroup);
            return new me.ccrama.redditslide.Adapters.SubmissionViewHolder(v);
        }
    }

    public class SubmissionFooterViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public SubmissionFooterViewHolder(android.view.View itemView) {
            super(itemView);
        }
    }

    public class AsyncSave extends android.os.AsyncTask<net.dean.jraw.models.Submission, java.lang.Void, java.lang.Void> {
        android.view.View v;

        public AsyncSave(android.view.View v) {
            this.v = v;
        }

        @java.lang.Override
        protected java.lang.Void doInBackground(net.dean.jraw.models.Submission... submissions) {
            try {
                if (me.ccrama.redditslide.ActionStates.isSaved(submissions[0])) {
                    new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).unsave(submissions[0]);
                    final android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(v, me.ccrama.redditslide.R.string.submission_info_unsaved, android.support.design.widget.Snackbar.LENGTH_SHORT);
                    mContext.runOnUiThread(new java.lang.Runnable() {
                        @java.lang.Override
                        public void run() {
                            android.view.View view = s.getView();
                            android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                            tv.setTextColor(android.graphics.Color.WHITE);
                            s.show();
                        }
                    });
                    submissions[0].saved = false;
                    v = null;
                } else {
                    new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).save(submissions[0]);
                    final android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(v, me.ccrama.redditslide.R.string.submission_info_saved, android.support.design.widget.Snackbar.LENGTH_SHORT);
                    mContext.runOnUiThread(new java.lang.Runnable() {
                        @java.lang.Override
                        public void run() {
                            android.view.View view = s.getView();
                            android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                            tv.setTextColor(android.graphics.Color.WHITE);
                            s.show();
                        }
                    });
                    submissions[0].saved = true;
                    v = null;
                }
            } catch (java.lang.Exception e) {
                return null;
            }
            return null;
        }
    }

    @java.lang.Override
    public void onBindViewHolder(final android.support.v7.widget.RecyclerView.ViewHolder firstHolder, final int pos) {
        int i = (pos != 0) ? pos - 1 : pos;
        if (firstHolder instanceof me.ccrama.redditslide.Adapters.SubmissionViewHolder) {
            final me.ccrama.redditslide.Adapters.SubmissionViewHolder holder = ((me.ccrama.redditslide.Adapters.SubmissionViewHolder) (firstHolder));
            final net.dean.jraw.models.Submission submission = ((net.dean.jraw.models.Submission) (dataSet.posts.get(i)));
            me.ccrama.redditslide.Views.CreateCardView.resetColorCard(holder.itemView);
            if (submission.getSubredditName() != null)
                me.ccrama.redditslide.Views.CreateCardView.colorCard(submission.getSubredditName().toLowerCase(java.util.Locale.ENGLISH), holder.itemView, "no_subreddit", false);

            holder.itemView.setOnLongClickListener(new android.view.View.OnLongClickListener() {
                @java.lang.Override
                public boolean onLongClick(android.view.View v) {
                    android.view.LayoutInflater inflater = mContext.getLayoutInflater();
                    final android.view.View dialoglayout = inflater.inflate(me.ccrama.redditslide.R.layout.postmenu, null);
                    com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext);
                    final android.widget.TextView title = dialoglayout.findViewById(me.ccrama.redditslide.R.id.title);
                    title.setText(android.text.Html.fromHtml(submission.getTitle()));
                    ((android.widget.TextView) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.userpopup))).setText("/u/" + submission.getAuthor());
                    ((android.widget.TextView) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.subpopup))).setText("/r/" + submission.getSubredditName());
                    dialoglayout.findViewById(me.ccrama.redditslide.R.id.sidebar).setOnClickListener(new android.view.View.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.view.View v) {
                            android.content.Intent i = new android.content.Intent(mContext, me.ccrama.redditslide.Activities.Profile.class);
                            i.putExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_PROFILE, submission.getAuthor());
                            mContext.startActivity(i);
                        }
                    });
                    dialoglayout.findViewById(me.ccrama.redditslide.R.id.wiki).setOnClickListener(new android.view.View.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.view.View v) {
                            android.content.Intent i = new android.content.Intent(mContext, me.ccrama.redditslide.Activities.SubredditView.class);
                            i.putExtra(me.ccrama.redditslide.Activities.SubredditView.EXTRA_SUBREDDIT, submission.getSubredditName());
                            mContext.startActivity(i);
                        }
                    });
                    dialoglayout.findViewById(me.ccrama.redditslide.R.id.save).setOnClickListener(new android.view.View.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.view.View v) {
                            if (submission.isSaved()) {
                                ((android.widget.TextView) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.savedtext))).setText(me.ccrama.redditslide.R.string.submission_save);
                            } else {
                                ((android.widget.TextView) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.savedtext))).setText(me.ccrama.redditslide.R.string.submission_post_saved);
                            }
                            new me.ccrama.redditslide.Adapters.ContributionAdapter.AsyncSave(firstHolder.itemView).execute(submission);
                        }
                    });
                    dialoglayout.findViewById(me.ccrama.redditslide.R.id.copy).setVisibility(android.view.View.GONE);
                    if (submission.isSaved()) {
                        ((android.widget.TextView) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.savedtext))).setText(me.ccrama.redditslide.R.string.submission_post_saved);
                    }
                    dialoglayout.findViewById(me.ccrama.redditslide.R.id.gild).setOnClickListener(new android.view.View.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.view.View v) {
                            java.lang.String urlString = "https://reddit.com" + submission.getPermalink();
                            android.content.Intent i = new android.content.Intent(mContext, me.ccrama.redditslide.Activities.Website.class);
                            i.putExtra(me.ccrama.redditslide.util.LinkUtil.EXTRA_URL, urlString);
                            mContext.startActivity(i);
                        }
                    });
                    dialoglayout.findViewById(me.ccrama.redditslide.R.id.share).setOnClickListener(new android.view.View.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.view.View v) {
                            if (submission.isSelfPost()) {
                                if (me.ccrama.redditslide.SettingValues.shareLongLink) {
                                    me.ccrama.redditslide.Reddit.defaultShareText("", "https://reddit.com" + submission.getPermalink(), mContext);
                                } else {
                                    me.ccrama.redditslide.Reddit.defaultShareText("", "https://redd.it/" + submission.getId(), mContext);
                                }
                            } else {
                                new com.cocosw.bottomsheet.BottomSheet.Builder(mContext).title(me.ccrama.redditslide.R.string.submission_share_title).grid().sheet(me.ccrama.redditslide.R.menu.share_menu).listener(new android.content.DialogInterface.OnClickListener() {
                                    @java.lang.Override
                                    public void onClick(android.content.DialogInterface dialog, int which) {
                                        switch (which) {
                                            case me.ccrama.redditslide.R.id.reddit_url :
                                                if (me.ccrama.redditslide.SettingValues.shareLongLink) {
                                                    me.ccrama.redditslide.Reddit.defaultShareText(submission.getTitle(), "https://reddit.com" + submission.getPermalink(), mContext);
                                                } else {
                                                    me.ccrama.redditslide.Reddit.defaultShareText(submission.getTitle(), "https://redd.it/" + submission.getId(), mContext);
                                                }
                                                break;
                                            case me.ccrama.redditslide.R.id.link_url :
                                                me.ccrama.redditslide.Reddit.defaultShareText(submission.getTitle(), submission.getUrl(), mContext);
                                                break;
                                        }
                                    }
                                }).show();
                            }
                        }
                    });
                    if ((!me.ccrama.redditslide.Authentication.isLoggedIn) || (!me.ccrama.redditslide.Authentication.didOnline)) {
                        dialoglayout.findViewById(me.ccrama.redditslide.R.id.save).setVisibility(android.view.View.GONE);
                        dialoglayout.findViewById(me.ccrama.redditslide.R.id.gild).setVisibility(android.view.View.GONE);
                    }
                    title.setBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getColor(submission.getSubredditName()));
                    builder.setView(dialoglayout);
                    final android.app.Dialog d = builder.show();
                    dialoglayout.findViewById(me.ccrama.redditslide.R.id.hide).setOnClickListener(new android.view.View.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.view.View v) {
                            final int pos = dataSet.posts.indexOf(submission);
                            final net.dean.jraw.models.Contribution old = dataSet.posts.get(pos);
                            dataSet.posts.remove(submission);
                            notifyItemRemoved(pos + 1);
                            d.dismiss();
                            me.ccrama.redditslide.Hidden.setHidden(old);
                            android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(listView, me.ccrama.redditslide.R.string.submission_info_hidden, android.support.design.widget.Snackbar.LENGTH_LONG).setAction(me.ccrama.redditslide.R.string.btn_undo, new android.view.View.OnClickListener() {
                                @java.lang.Override
                                public void onClick(android.view.View v) {
                                    dataSet.posts.add(pos, old);
                                    notifyItemInserted(pos + 1);
                                    me.ccrama.redditslide.Hidden.undoHidden(old);
                                }
                            });
                            android.view.View view = s.getView();
                            android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                            tv.setTextColor(android.graphics.Color.WHITE);
                            s.show();
                        }
                    });
                    return true;
                }
            });
            new me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder().populateSubmissionViewHolder(holder, submission, mContext, false, false, dataSet.posts, listView, false, false, null, null);
            final android.widget.ImageView hideButton = holder.itemView.findViewById(me.ccrama.redditslide.R.id.hide);
            if ((hideButton != null) && isHiddenPost) {
                hideButton.setOnClickListener(new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        final int pos = dataSet.posts.indexOf(submission);
                        final net.dean.jraw.models.Contribution old = dataSet.posts.get(pos);
                        dataSet.posts.remove(submission);
                        notifyItemRemoved(pos + 1);
                        me.ccrama.redditslide.Hidden.undoHidden(old);
                    }
                });
            }
            holder.itemView.setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    java.lang.String url = "www.reddit.com" + submission.getPermalink();
                    url = url.replace("?ref=search_posts", "");
                    new me.ccrama.redditslide.OpenRedditLink(mContext, url);
                    if (me.ccrama.redditslide.SettingValues.storeHistory) {
                        if ((me.ccrama.redditslide.SettingValues.storeNSFWHistory && submission.isNsfw()) || (!submission.isNsfw()))
                            me.ccrama.redditslide.HasSeen.addSeen(submission.getFullName());

                    }
                    notifyItemChanged(pos);
                }
            });
        } else if (firstHolder instanceof me.ccrama.redditslide.Adapters.ProfileCommentViewHolder) {
            // IS COMMENT
            me.ccrama.redditslide.Adapters.ProfileCommentViewHolder holder = ((me.ccrama.redditslide.Adapters.ProfileCommentViewHolder) (firstHolder));
            final net.dean.jraw.models.Comment comment = ((net.dean.jraw.models.Comment) (dataSet.posts.get(i)));
            java.lang.String scoreText;
            if (comment.isScoreHidden()) {
                scoreText = ("[" + mContext.getString(me.ccrama.redditslide.R.string.misc_score_hidden).toUpperCase()) + "]";
            } else {
                scoreText = java.lang.String.format(java.util.Locale.getDefault(), "%d", comment.getScore());
            }
            android.text.SpannableStringBuilder score = new android.text.SpannableStringBuilder(scoreText);
            if ((score == null) || score.toString().isEmpty()) {
                score = new android.text.SpannableStringBuilder("0");
            }
            if (!scoreText.contains("[")) {
                score.append(java.lang.String.format(java.util.Locale.getDefault(), " %s", mContext.getResources().getQuantityString(me.ccrama.redditslide.R.plurals.points, comment.getScore())));
            }
            holder.score.setText(score);
            if (me.ccrama.redditslide.Authentication.isLoggedIn) {
                if (me.ccrama.redditslide.ActionStates.getVoteDirection(comment) == net.dean.jraw.models.VoteDirection.UPVOTE) {
                    holder.score.setTextColor(mContext.getResources().getColor(me.ccrama.redditslide.R.color.md_orange_500));
                } else if (me.ccrama.redditslide.ActionStates.getVoteDirection(comment) == net.dean.jraw.models.VoteDirection.DOWNVOTE) {
                    holder.score.setTextColor(mContext.getResources().getColor(me.ccrama.redditslide.R.color.md_blue_500));
                } else {
                    holder.score.setTextColor(holder.time.getCurrentTextColor());
                }
            }
            java.lang.String spacer = mContext.getString(me.ccrama.redditslide.R.string.submission_properties_seperator);
            android.text.SpannableStringBuilder titleString = new android.text.SpannableStringBuilder();
            java.lang.String timeAgo = me.ccrama.redditslide.TimeUtils.getTimeAgo(comment.getCreated().getTime(), mContext);
            java.lang.String time = ((timeAgo == null) || timeAgo.isEmpty()) ? "just now" : timeAgo;// some users were crashing here

            time = time + (comment.getEditDate() != null ? (" (edit " + me.ccrama.redditslide.TimeUtils.getTimeAgo(comment.getEditDate().getTime(), mContext)) + ")" : "");
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
            int type = new me.ccrama.redditslide.Visuals.FontPreferences(mContext).getFontTypeComment().getTypeface();
            android.graphics.Typeface typeface;
            if (type >= 0) {
                typeface = com.devspark.robototextview.RobotoTypefaces.obtainTypeface(mContext, type);
            } else {
                typeface = android.graphics.Typeface.DEFAULT;
            }
            holder.content.setTypeface(typeface);
            ((android.widget.TextView) (holder.gild)).setText("");
            if (((comment.getTimesSilvered() > 0) || (comment.getTimesGilded() > 0)) || (comment.getTimesPlatinized() > 0)) {
                android.content.res.TypedArray a = mContext.obtainStyledAttributes(new me.ccrama.redditslide.Visuals.FontPreferences(mContext).getPostFontStyle().getResId(), me.ccrama.redditslide.R.styleable.FontStyle);
                int fontsize = ((int) (a.getDimensionPixelSize(me.ccrama.redditslide.R.styleable.FontStyle_font_cardtitle, -1) * 0.75));
                a.recycle();
                holder.gild.setVisibility(android.view.View.VISIBLE);
                // Add silver, gold, platinum icons and counts in that order
                if (comment.getTimesSilvered() > 0) {
                    final java.lang.String timesSilvered = (comment.getTimesSilvered() == 1) ? "" : "\u200ax" + java.lang.Integer.toString(comment.getTimesSilvered());
                    android.text.SpannableStringBuilder silvered = new android.text.SpannableStringBuilder(("\u00a0\u2605" + timesSilvered) + "\u00a0");
                    android.graphics.Bitmap image = android.graphics.BitmapFactory.decodeResource(mContext.getResources(), me.ccrama.redditslide.R.drawable.silver);
                    float aspectRatio = ((float) ((1.0 * image.getWidth()) / image.getHeight()));
                    if (image != null) {
                        image.recycle();
                    }
                    image = android.graphics.Bitmap.createScaledBitmap(image, ((int) (java.lang.Math.ceil(fontsize * aspectRatio))), ((int) (java.lang.Math.ceil(fontsize))), true);
                    silvered.setSpan(new android.text.style.ImageSpan(mContext, image, android.text.style.ImageSpan.ALIGN_BASELINE), 0, 2, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    silvered.setSpan(new android.text.style.RelativeSizeSpan(0.75F), 3, silvered.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ((android.widget.TextView) (holder.gild)).append(silvered);
                }
                if (comment.getTimesGilded() > 0) {
                    final java.lang.String timesGilded = (comment.getTimesGilded() == 1) ? "" : "\u200ax" + java.lang.Integer.toString(comment.getTimesGilded());
                    android.text.SpannableStringBuilder gilded = new android.text.SpannableStringBuilder(("\u00a0\u2605" + timesGilded) + "\u00a0");
                    android.graphics.Bitmap image = android.graphics.BitmapFactory.decodeResource(mContext.getResources(), me.ccrama.redditslide.R.drawable.gold);
                    float aspectRatio = ((float) ((1.0 * image.getWidth()) / image.getHeight()));
                    if (image != null) {
                        image.recycle();
                    }
                    image = android.graphics.Bitmap.createScaledBitmap(image, ((int) (java.lang.Math.ceil(fontsize * aspectRatio))), ((int) (java.lang.Math.ceil(fontsize))), true);
                    gilded.setSpan(new android.text.style.ImageSpan(mContext, image, android.text.style.ImageSpan.ALIGN_BASELINE), 0, 2, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    gilded.setSpan(new android.text.style.RelativeSizeSpan(0.75F), 3, gilded.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ((android.widget.TextView) (holder.gild)).append(gilded);
                }
                if (comment.getTimesPlatinized() > 0) {
                    final java.lang.String timesPlatinized = (comment.getTimesPlatinized() == 1) ? "" : "\u200ax" + java.lang.Integer.toString(comment.getTimesPlatinized());
                    android.text.SpannableStringBuilder platinized = new android.text.SpannableStringBuilder(("\u00a0\u2605" + timesPlatinized) + "\u00a0");
                    android.graphics.Bitmap image = android.graphics.BitmapFactory.decodeResource(mContext.getResources(), me.ccrama.redditslide.R.drawable.platinum);
                    float aspectRatio = ((float) ((1.0 * image.getWidth()) / image.getHeight()));
                    if (image != null) {
                        image.recycle();
                    }
                    image = android.graphics.Bitmap.createScaledBitmap(image, ((int) (java.lang.Math.ceil(fontsize * aspectRatio))), ((int) (java.lang.Math.ceil(fontsize))), true);
                    platinized.setSpan(new android.text.style.ImageSpan(mContext, image, android.text.style.ImageSpan.ALIGN_BASELINE), 0, 2, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    platinized.setSpan(new android.text.style.RelativeSizeSpan(0.75F), 3, platinized.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ((android.widget.TextView) (holder.gild)).append(platinized);
                }
            } else if (holder.gild.getVisibility() == android.view.View.VISIBLE)
                holder.gild.setVisibility(android.view.View.GONE);

            if (comment.getSubmissionTitle() != null)
                holder.title.setText(android.text.Html.fromHtml(comment.getSubmissionTitle()));
            else
                holder.title.setText(android.text.Html.fromHtml(comment.getAuthor()));

            holder.itemView.setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    new me.ccrama.redditslide.OpenRedditLink(mContext, comment.getSubmissionId(), comment.getSubredditName(), comment.getId());
                }
            });
            holder.content.setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    new me.ccrama.redditslide.OpenRedditLink(mContext, comment.getSubmissionId(), comment.getSubredditName(), comment.getId());
                }
            });
        } else if (firstHolder instanceof me.ccrama.redditslide.Adapters.ContributionAdapter.SpacerViewHolder) {
            firstHolder.itemView.setLayoutParams(new android.widget.LinearLayout.LayoutParams(firstHolder.itemView.getWidth(), mContext.findViewById(me.ccrama.redditslide.R.id.header).getHeight()));
            if (listView.getLayoutManager() instanceof me.ccrama.redditslide.Views.CatchStaggeredGridLayoutManager) {
                android.support.v7.widget.StaggeredGridLayoutManager.LayoutParams layoutParams = new android.support.v7.widget.StaggeredGridLayoutManager.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, mContext.findViewById(me.ccrama.redditslide.R.id.header).getHeight());
                layoutParams.setFullSpan(true);
                firstHolder.itemView.setLayoutParams(layoutParams);
            }
        }
    }

    public class SpacerViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public SpacerViewHolder(android.view.View itemView) {
            super(itemView);
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

    @java.lang.Override
    public int getItemCount() {
        if ((dataSet.posts == null) || dataSet.posts.isEmpty()) {
            return 0;
        } else {
            return dataSet.posts.size() + 2;
        }
    }

    @java.lang.Override
    public void setError(java.lang.Boolean b) {
        listView.setAdapter(new me.ccrama.redditslide.Adapters.ErrorAdapter());
    }

    @java.lang.Override
    public void undoSetError() {
        listView.setAdapter(this);
    }

    public static class EmptyViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public EmptyViewHolder(android.view.View itemView) {
            super(itemView);
        }
    }
}