/**
 * Created by ccrama on 3/22/2015.
 */
package me.ccrama.redditslide.Adapters;
import java.util.Locale;
import me.ccrama.redditslide.util.OnSingleClickListener;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.Activities.Profile;
import me.ccrama.redditslide.Activities.SubredditView;
import me.ccrama.redditslide.Activities.Website;
import me.ccrama.redditslide.Toolbox.Toolbox;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.TimeUtils;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder;
import me.ccrama.redditslide.Views.CreateCardView;
import me.ccrama.redditslide.Hidden;
import me.ccrama.redditslide.UserSubscriptions;
import java.util.List;
import me.ccrama.redditslide.ActionStates;
import me.ccrama.redditslide.Toolbox.ToolboxUI;
import me.ccrama.redditslide.util.SubmissionParser;
import me.ccrama.redditslide.Visuals.FontPreferences;
import me.ccrama.redditslide.util.LinkUtil;
import me.ccrama.redditslide.Views.RoundedBackgroundSpan;
import me.ccrama.redditslide.Authentication;
import java.util.Map;
import me.ccrama.redditslide.OpenRedditLink;
public class ModeratorAdapter extends android.support.v7.widget.RecyclerView.Adapter<android.support.v7.widget.RecyclerView.ViewHolder> implements me.ccrama.redditslide.Adapters.BaseAdapter {
    public static final int COMMENT = 1;

    private final int SPACER = 6;

    public static final int MESSAGE = 2;

    public static final int POST = 3;

    public final android.app.Activity mContext;

    private final android.support.v7.widget.RecyclerView listView;

    public me.ccrama.redditslide.Adapters.ModeratorPosts dataSet;

    public ModeratorAdapter(android.app.Activity mContext, me.ccrama.redditslide.Adapters.ModeratorPosts dataSet, android.support.v7.widget.RecyclerView listView) {
        this.mContext = mContext;
        this.listView = listView;
        this.dataSet = dataSet;
    }

    @java.lang.Override
    public void setError(java.lang.Boolean b) {
        listView.setAdapter(new me.ccrama.redditslide.Adapters.ErrorAdapter());
    }

    @java.lang.Override
    public void undoSetError() {
        listView.setAdapter(this);
    }

    @java.lang.Override
    public int getItemViewType(int position) {
        if ((position == 0) && (!dataSet.posts.isEmpty())) {
            return SPACER;
        } else if (!dataSet.posts.isEmpty()) {
            position -= 1;
        }
        // IS COMMENT
        if (dataSet.posts.get(position).getFullName().startsWith("t1"))
            return me.ccrama.redditslide.Adapters.ModeratorAdapter.COMMENT;

        // IS MESSAGE
        if (dataSet.posts.get(position).getFullName().startsWith("t4"))
            return me.ccrama.redditslide.Adapters.ModeratorAdapter.MESSAGE;

        return me.ccrama.redditslide.Adapters.ModeratorAdapter.POST;
    }

    public class SpacerViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public SpacerViewHolder(android.view.View itemView) {
            super(itemView);
        }
    }

    @java.lang.Override
    public android.support.v7.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup viewGroup, int i) {
        if (i == SPACER) {
            android.view.View v = android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.spacer, viewGroup, false);
            return new me.ccrama.redditslide.Adapters.ModeratorAdapter.SpacerViewHolder(v);
        } else if (i == me.ccrama.redditslide.Adapters.ModeratorAdapter.MESSAGE) {
            android.view.View v = android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.top_level_message, viewGroup, false);
            return new me.ccrama.redditslide.Adapters.MessageViewHolder(v);
        }
        if (i == me.ccrama.redditslide.Adapters.ModeratorAdapter.COMMENT) {
            android.view.View v = android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.profile_comment, viewGroup, false);
            return new me.ccrama.redditslide.Adapters.ProfileCommentViewHolder(v);
        } else {
            android.view.View v = me.ccrama.redditslide.Views.CreateCardView.CreateView(viewGroup);
            return new me.ccrama.redditslide.Adapters.SubmissionViewHolder(v);
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
    public void onBindViewHolder(final android.support.v7.widget.RecyclerView.ViewHolder firstHold, final int pos) {
        int i = (pos != 0) ? pos - 1 : pos;
        if (firstHold instanceof me.ccrama.redditslide.Adapters.SubmissionViewHolder) {
            me.ccrama.redditslide.Adapters.SubmissionViewHolder holder = ((me.ccrama.redditslide.Adapters.SubmissionViewHolder) (firstHold));
            final net.dean.jraw.models.Submission submission = ((net.dean.jraw.models.Submission) (dataSet.posts.get(i)));
            me.ccrama.redditslide.Views.CreateCardView.resetColorCard(holder.itemView);
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
                            new me.ccrama.redditslide.Adapters.ModeratorAdapter.AsyncSave(firstHold.itemView).execute(submission);
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
                                    dataSet.posts.add(pos, ((net.dean.jraw.models.PublicContribution) (old)));
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
            if (hideButton != null) {
                hideButton.setVisibility(android.view.View.GONE);
            }
            holder.itemView.setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    java.lang.String url = "www.reddit.com" + submission.getPermalink();
                    url = url.replace("?ref=search_posts", "");
                    new me.ccrama.redditslide.OpenRedditLink(mContext, url);
                }
            });
        } else if (firstHold instanceof me.ccrama.redditslide.Adapters.ProfileCommentViewHolder) {
            // IS COMMENT
            final me.ccrama.redditslide.Adapters.ProfileCommentViewHolder holder = ((me.ccrama.redditslide.Adapters.ProfileCommentViewHolder) (firstHold));
            final net.dean.jraw.models.Comment comment = ((net.dean.jraw.models.Comment) (dataSet.posts.get(i)));
            android.text.SpannableStringBuilder author = new android.text.SpannableStringBuilder(comment.getAuthor());
            final int authorcolor = me.ccrama.redditslide.Visuals.Palette.getFontColorUser(comment.getAuthor());
            if (comment.getDistinguishedStatus() == net.dean.jraw.models.DistinguishedStatus.ADMIN) {
                author.replace(0, author.length(), (" " + comment.getAuthor()) + " ");
                author.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(mContext, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_red_300, false), 0, author.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (comment.getDistinguishedStatus() == net.dean.jraw.models.DistinguishedStatus.SPECIAL) {
                author.replace(0, author.length(), (" " + comment.getAuthor()) + " ");
                author.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(mContext, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_red_500, false), 0, author.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (comment.getDistinguishedStatus() == net.dean.jraw.models.DistinguishedStatus.MODERATOR) {
                author.replace(0, author.length(), (" " + comment.getAuthor()) + " ");
                author.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(mContext, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_green_300, false), 0, author.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if ((me.ccrama.redditslide.Authentication.name != null) && comment.getAuthor().toLowerCase(java.util.Locale.ENGLISH).equals(me.ccrama.redditslide.Authentication.name.toLowerCase(java.util.Locale.ENGLISH))) {
                author.replace(0, author.length(), (" " + comment.getAuthor()) + " ");
                author.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(mContext, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_deep_orange_300, false), 0, author.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (authorcolor != 0) {
                author.setSpan(new android.text.style.ForegroundColorSpan(authorcolor), 0, author.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            me.ccrama.redditslide.Toolbox.ToolboxUI.appendToolboxNote(mContext, author, comment.getSubredditName(), comment.getAuthor());
            holder.user.setText(author);
            holder.user.append(mContext.getResources().getString(me.ccrama.redditslide.R.string.submission_properties_seperator));
            holder.user.setVisibility(android.view.View.VISIBLE);
            holder.score.setText((comment.getScore() + " ") + mContext.getResources().getQuantityString(me.ccrama.redditslide.R.plurals.points, comment.getScore()));
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
            {
                final android.widget.ImageView mod = holder.itemView.findViewById(me.ccrama.redditslide.R.id.mod);
                try {
                    if (me.ccrama.redditslide.UserSubscriptions.modOf.contains(comment.getSubredditName())) {
                        // todo
                        mod.setVisibility(android.view.View.GONE);
                    } else {
                        mod.setVisibility(android.view.View.GONE);
                    }
                } catch (java.lang.Exception e) {
                    android.util.Log.d(me.ccrama.redditslide.util.LogUtil.getTag(), "Error loading mod " + e.toString());
                }
            }
            if ((me.ccrama.redditslide.UserSubscriptions.modOf != null) && me.ccrama.redditslide.UserSubscriptions.modOf.contains(comment.getSubredditName().toLowerCase(java.util.Locale.ENGLISH))) {
                holder.itemView.findViewById(me.ccrama.redditslide.R.id.mod).setVisibility(android.view.View.VISIBLE);
                final java.util.Map<java.lang.String, java.lang.Integer> reports = comment.getUserReports();
                final java.util.Map<java.lang.String, java.lang.String> reports2 = comment.getModeratorReports();
                if ((reports.size() + reports2.size()) > 0) {
                    ((android.widget.ImageView) (holder.itemView.findViewById(me.ccrama.redditslide.R.id.mod))).setColorFilter(android.support.v4.content.ContextCompat.getColor(mContext, me.ccrama.redditslide.R.color.md_red_300), android.graphics.PorterDuff.Mode.SRC_ATOP);
                } else {
                    int[] attrs = new int[]{ me.ccrama.redditslide.R.attr.tintColor };
                    android.content.res.TypedArray ta = mContext.obtainStyledAttributes(attrs);
                    int color = ta.getColor(0, android.graphics.Color.WHITE);
                    ((android.widget.ImageView) (holder.itemView.findViewById(me.ccrama.redditslide.R.id.mod))).setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
                    ta.recycle();
                }
                holder.itemView.findViewById(me.ccrama.redditslide.R.id.mod).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                    @java.lang.Override
                    public void onSingleClick(android.view.View v) {
                        me.ccrama.redditslide.Adapters.ModeratorAdapter.showModBottomSheet(mContext, comment, holder, reports, reports2);
                    }
                });
            } else {
                holder.itemView.findViewById(me.ccrama.redditslide.R.id.mod).setVisibility(android.view.View.GONE);
            }
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
        }
        if (firstHold instanceof me.ccrama.redditslide.Adapters.ModeratorAdapter.SpacerViewHolder) {
            firstHold.itemView.findViewById(me.ccrama.redditslide.R.id.height).setLayoutParams(new android.widget.LinearLayout.LayoutParams(firstHold.itemView.getWidth(), mContext.findViewById(me.ccrama.redditslide.R.id.header).getHeight()));
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
            return dataSet.posts.size() + 1;
        }
    }

    public static void showModBottomSheet(final android.content.Context mContext, final net.dean.jraw.models.Comment comment, final me.ccrama.redditslide.Adapters.ProfileCommentViewHolder holder, final java.util.Map<java.lang.String, java.lang.Integer> reports, final java.util.Map<java.lang.String, java.lang.String> reports2) {
        int[] attrs = new int[]{ me.ccrama.redditslide.R.attr.tintColor };
        android.content.res.TypedArray ta = mContext.obtainStyledAttributes(attrs);
        // Initialize drawables
        int color = ta.getColor(0, android.graphics.Color.WHITE);
        final android.graphics.drawable.Drawable profile = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.profile);
        final android.graphics.drawable.Drawable report = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.report);
        final android.graphics.drawable.Drawable approve = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.support);
        final android.graphics.drawable.Drawable nsfw = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.hide);
        final android.graphics.drawable.Drawable pin = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.sub);
        final android.graphics.drawable.Drawable distinguish = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.iconstarfilled);
        final android.graphics.drawable.Drawable remove = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.close);
        final android.graphics.drawable.Drawable ban = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.ban);
        final android.graphics.drawable.Drawable spam = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.spam);
        final android.graphics.drawable.Drawable note = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.note);
        final android.graphics.drawable.Drawable removeReason = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.reportreason);
        // Tint drawables
        profile.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        report.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        approve.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        nsfw.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        distinguish.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        remove.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        pin.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        ban.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        spam.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        note.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        removeReason.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        ta.recycle();
        // Bottom sheet builder
        com.cocosw.bottomsheet.BottomSheet.Builder b = new com.cocosw.bottomsheet.BottomSheet.Builder(((android.app.Activity) (mContext))).title(android.text.Html.fromHtml(comment.getBody()));
        int reportCount = reports.size() + reports2.size();
        if (reportCount == 0) {
            b.sheet(0, report, mContext.getString(me.ccrama.redditslide.R.string.mod_no_reports));
        } else {
            b.sheet(0, report, mContext.getResources().getQuantityString(me.ccrama.redditslide.R.plurals.mod_btn_reports, reportCount, reportCount));
        }
        if (me.ccrama.redditslide.SettingValues.toolboxEnabled) {
            b.sheet(24, note, mContext.getString(me.ccrama.redditslide.R.string.mod_usernotes_view));
        }
        b.sheet(1, approve, mContext.getString(me.ccrama.redditslide.R.string.mod_btn_approve));
        b.sheet(6, remove, mContext.getString(me.ccrama.redditslide.R.string.btn_remove));
        b.sheet(7, removeReason, mContext.getString(me.ccrama.redditslide.R.string.mod_btn_remove_reason));
        b.sheet(10, spam, mContext.getString(me.ccrama.redditslide.R.string.mod_btn_spam));
        final boolean distinguished = !comment.getDataNode().get("distinguished").isNull();
        if (comment.getAuthor().equalsIgnoreCase(me.ccrama.redditslide.Authentication.name)) {
            if (!distinguished) {
                b.sheet(9, distinguish, mContext.getString(me.ccrama.redditslide.R.string.mod_distinguish));
            } else {
                b.sheet(9, distinguish, mContext.getString(me.ccrama.redditslide.R.string.mod_undistinguish));
            }
        }
        b.sheet(8, profile, mContext.getString(me.ccrama.redditslide.R.string.mod_btn_author));
        b.sheet(23, ban, mContext.getString(me.ccrama.redditslide.R.string.mod_ban_user));
        b.listener(new android.content.DialogInterface.OnClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                switch (which) {
                    case 0 :
                        me.ccrama.redditslide.Adapters.CommentAdapterHelper.viewReports(mContext, reports, reports2);
                        break;
                    case 1 :
                        me.ccrama.redditslide.Adapters.ModeratorAdapter.doApproval(mContext, holder, comment);
                        break;
                    case 9 :
                        if (distinguished) {
                            me.ccrama.redditslide.Adapters.ModeratorAdapter.unDistinguishComment(mContext, holder, comment);
                        } else {
                            me.ccrama.redditslide.Adapters.ModeratorAdapter.distinguishComment(mContext, holder, comment);
                        }
                        break;
                    case 6 :
                        me.ccrama.redditslide.Adapters.ModeratorAdapter.removeComment(mContext, holder, comment, false);
                        break;
                    case 7 :
                        if ((me.ccrama.redditslide.SettingValues.removalReasonType == me.ccrama.redditslide.SettingValues.RemovalReasonType.TOOLBOX.ordinal()) && me.ccrama.redditslide.Toolbox.ToolboxUI.canShowRemoval(comment.getSubredditName())) {
                            me.ccrama.redditslide.Toolbox.ToolboxUI.showRemoval(mContext, comment, new me.ccrama.redditslide.Toolbox.ToolboxUI.CompletedRemovalCallback() {
                                @java.lang.Override
                                public void onComplete(boolean success) {
                                    if (success) {
                                        android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(holder.itemView, me.ccrama.redditslide.R.string.comment_removed, android.support.design.widget.Snackbar.LENGTH_LONG);
                                        android.view.View view = s.getView();
                                        android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                                        tv.setTextColor(android.graphics.Color.WHITE);
                                        s.show();
                                    } else {
                                        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.err_general).setMessage(me.ccrama.redditslide.R.string.err_retry_later).show();
                                    }
                                }
                            });
                        } else {
                            // Show a Slide reason dialog if we can't show a toolbox or reddit reason
                            me.ccrama.redditslide.Adapters.ModeratorAdapter.doRemoveCommentReason(mContext, holder, comment);
                        }
                        break;
                    case 10 :
                        me.ccrama.redditslide.Adapters.ModeratorAdapter.removeComment(mContext, holder, comment, true);
                        break;
                    case 8 :
                        android.content.Intent i = new android.content.Intent(mContext, me.ccrama.redditslide.Activities.Profile.class);
                        i.putExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_PROFILE, comment.getAuthor());
                        mContext.startActivity(i);
                        break;
                    case 23 :
                        me.ccrama.redditslide.Adapters.CommentAdapterHelper.showBan(mContext, holder.itemView, comment, "", "", "", "");
                        break;
                    case 24 :
                        me.ccrama.redditslide.Toolbox.ToolboxUI.showUsernotes(mContext, comment.getAuthor(), comment.getSubredditName(), (("l," + comment.getParentId()) + ",") + comment.getId());
                        break;
                }
            }
        });
        b.show();
    }

    public static void doApproval(final android.content.Context mContext, final me.ccrama.redditslide.Adapters.ProfileCommentViewHolder holder, final net.dean.jraw.models.Comment comment) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
            @java.lang.Override
            public void onPostExecute(java.lang.Boolean b) {
                if (b) {
                    android.support.design.widget.Snackbar.make(holder.itemView, me.ccrama.redditslide.R.string.mod_approved, android.support.design.widget.Snackbar.LENGTH_LONG).show();
                } else {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.err_general).setMessage(me.ccrama.redditslide.R.string.err_retry_later).show();
                }
            }

            @java.lang.Override
            protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                try {
                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).approve(comment);
                } catch (net.dean.jraw.ApiException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }.execute();
    }

    public static void distinguishComment(final android.content.Context mContext, final me.ccrama.redditslide.Adapters.ProfileCommentViewHolder holder, final net.dean.jraw.models.Comment comment) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
            @java.lang.Override
            public void onPostExecute(java.lang.Boolean b) {
                if (b) {
                    android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(holder.itemView, me.ccrama.redditslide.R.string.comment_distinguished, android.support.design.widget.Snackbar.LENGTH_LONG);
                    android.view.View view = s.getView();
                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(android.graphics.Color.WHITE);
                    s.show();
                } else {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.err_general).setMessage(me.ccrama.redditslide.R.string.err_retry_later).show();
                }
            }

            @java.lang.Override
            protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                try {
                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).setDistinguishedStatus(comment, net.dean.jraw.models.DistinguishedStatus.MODERATOR);
                } catch (net.dean.jraw.ApiException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }.execute();
    }

    public static void unDistinguishComment(final android.content.Context mContext, final me.ccrama.redditslide.Adapters.ProfileCommentViewHolder holder, final net.dean.jraw.models.Comment comment) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
            @java.lang.Override
            public void onPostExecute(java.lang.Boolean b) {
                if (b) {
                    android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(holder.itemView, me.ccrama.redditslide.R.string.comment_undistinguished, android.support.design.widget.Snackbar.LENGTH_LONG);
                    android.view.View view = s.getView();
                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(android.graphics.Color.WHITE);
                    s.show();
                } else {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.err_general).setMessage(me.ccrama.redditslide.R.string.err_retry_later).show();
                }
            }

            @java.lang.Override
            protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                try {
                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).setDistinguishedStatus(comment, net.dean.jraw.models.DistinguishedStatus.NORMAL);
                } catch (net.dean.jraw.ApiException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }.execute();
    }

    public static void removeComment(final android.content.Context mContext, final me.ccrama.redditslide.Adapters.ProfileCommentViewHolder holder, final net.dean.jraw.models.Comment comment, final boolean spam) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
            @java.lang.Override
            public void onPostExecute(java.lang.Boolean b) {
                if (b) {
                    android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(holder.itemView, me.ccrama.redditslide.R.string.comment_removed, android.support.design.widget.Snackbar.LENGTH_LONG);
                    android.view.View view = s.getView();
                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(android.graphics.Color.WHITE);
                    s.show();
                } else {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.err_general).setMessage(me.ccrama.redditslide.R.string.err_retry_later).show();
                }
            }

            @java.lang.Override
            protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                try {
                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).remove(comment, spam);
                } catch (net.dean.jraw.ApiException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }.execute();
    }

    /**
     * Show a removal dialog to input a reason, then remove comment and post reason
     *
     * @param mContext
     * 		context
     * @param holder
     * 		commentviewholder
     * @param comment
     * 		comment
     */
    public static void doRemoveCommentReason(final android.content.Context mContext, final me.ccrama.redditslide.Adapters.ProfileCommentViewHolder holder, final net.dean.jraw.models.Comment comment) {
        new com.afollestad.materialdialogs.MaterialDialog.Builder(mContext).title(me.ccrama.redditslide.R.string.mod_remove_title).positiveText(me.ccrama.redditslide.R.string.btn_remove).alwaysCallInputCallback().input(mContext.getString(me.ccrama.redditslide.R.string.mod_remove_hint), mContext.getString(me.ccrama.redditslide.R.string.mod_remove_template), false, new com.afollestad.materialdialogs.MaterialDialog.InputCallback() {
            @java.lang.Override
            public void onInput(com.afollestad.materialdialogs.MaterialDialog dialog, java.lang.CharSequence input) {
            }
        }).inputType(android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES).neutralText(me.ccrama.redditslide.R.string.mod_remove_insert_draft).onNeutral(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
            @java.lang.Override
            public void onClick(@android.support.annotation.NonNull
            com.afollestad.materialdialogs.MaterialDialog dialog, @android.support.annotation.NonNull
            com.afollestad.materialdialogs.DialogAction which) {
            }
        }).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
            @java.lang.Override
            public void onClick(final com.afollestad.materialdialogs.MaterialDialog dialog, com.afollestad.materialdialogs.DialogAction which) {
                me.ccrama.redditslide.Adapters.ModeratorAdapter.removeCommentReason(comment, mContext, holder, dialog.getInputEditText().getText().toString());
            }
        }).negativeText(me.ccrama.redditslide.R.string.btn_cancel).show();
    }

    /**
     * Remove a comment and post a reason
     *
     * @param comment
     * 		comment
     * @param mContext
     * 		context
     * @param holder
     * 		commentviewholder
     * @param reason
     * 		reason
     */
    public static void removeCommentReason(final net.dean.jraw.models.Comment comment, final android.content.Context mContext, me.ccrama.redditslide.Adapters.ProfileCommentViewHolder holder, final java.lang.String reason) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
            @java.lang.Override
            public void onPostExecute(java.lang.Boolean b) {
                if (b) {
                    android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(holder.itemView, me.ccrama.redditslide.R.string.comment_removed, android.support.design.widget.Snackbar.LENGTH_LONG);
                    android.view.View view = s.getView();
                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(android.graphics.Color.WHITE);
                    s.show();
                } else {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.err_general).setMessage(me.ccrama.redditslide.R.string.err_retry_later).show();
                }
            }

            @java.lang.Override
            protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                try {
                    new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).reply(comment, reason);
                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).remove(comment, false);
                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).setDistinguishedStatus(me.ccrama.redditslide.Authentication.reddit.get(comment.getFullName()).get(0), net.dean.jraw.models.DistinguishedStatus.MODERATOR);
                } catch (net.dean.jraw.ApiException | net.dean.jraw.http.NetworkException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }
}