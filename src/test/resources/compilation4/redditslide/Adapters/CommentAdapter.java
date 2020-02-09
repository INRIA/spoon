/**
 * Created by ccrama on 3/22/2015.
 */
package me.ccrama.redditslide.Adapters;
import me.ccrama.redditslide.Views.DoEditorActions;
import me.ccrama.redditslide.util.OnSingleClickListener;
import me.ccrama.redditslide.Fragments.CommentPage;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.util.SubmissionParser;
import me.ccrama.redditslide.*;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.Views.PreCachingLayoutManagerComments;
import me.ccrama.redditslide.Visuals.FontPreferences;
import me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder;
import java.io.StringWriter;
import me.ccrama.redditslide.Activities.BaseActivity;
import me.ccrama.redditslide.Views.CommentOverflow;
import java.io.Writer;
import java.io.File;
import java.io.PrintWriter;
public class CommentAdapter extends android.support.v7.widget.RecyclerView.Adapter<android.support.v7.widget.RecyclerView.ViewHolder> {
    static final int HEADER = 1;

    private final int SPACER = 6;

    public final android.graphics.Bitmap[] awardIcons;

    public android.content.Context mContext;

    public me.ccrama.redditslide.Adapters.SubmissionComments dataSet;

    public net.dean.jraw.models.Submission submission;

    public me.ccrama.redditslide.Adapters.CommentViewHolder currentlySelected;

    public net.dean.jraw.models.CommentNode currentNode;

    public java.lang.String currentSelectedItem = "";

    public int shiftFrom;

    public android.support.v4.app.FragmentManager fm;

    public int clickpos;

    public int currentPos;

    public me.ccrama.redditslide.Adapters.CommentViewHolder isHolder;

    public boolean isClicking;

    public java.util.HashMap<java.lang.String, java.lang.Integer> keys = new java.util.HashMap<>();

    public java.util.ArrayList<me.ccrama.redditslide.Adapters.CommentObject> currentComments;

    public java.util.ArrayList<java.lang.String> deleted = new java.util.ArrayList<>();

    android.support.v7.widget.RecyclerView listView;

    me.ccrama.redditslide.Fragments.CommentPage mPage;

    int shifted;

    int toShiftTo;

    java.util.HashSet<java.lang.String> hidden;

    java.util.ArrayList<java.lang.String> hiddenPersons;

    java.util.ArrayList<java.lang.String> toCollapse;

    private java.lang.String backedText = "";

    private java.lang.String currentlyEditingId = "";

    public me.ccrama.redditslide.Adapters.SubmissionViewHolder submissionViewHolder;

    long lastSeen = 0;

    public java.util.ArrayList<java.lang.String> approved = new java.util.ArrayList<>();

    public java.util.ArrayList<java.lang.String> removed = new java.util.ArrayList<>();

    public CommentAdapter(me.ccrama.redditslide.Fragments.CommentPage mContext, me.ccrama.redditslide.Adapters.SubmissionComments dataSet, android.support.v7.widget.RecyclerView listView, net.dean.jraw.models.Submission submission, android.support.v4.app.FragmentManager fm) {
        this.mContext = mContext.getContext();
        mPage = mContext;
        this.listView = listView;
        this.dataSet = dataSet;
        this.fm = fm;
        this.submission = submission;
        hidden = new java.util.HashSet<>();
        currentComments = dataSet.comments;
        if (currentComments != null) {
            for (int i = 0; i < currentComments.size(); i++) {
                keys.put(currentComments.get(i).getName(), i);
            }
        }
        hiddenPersons = new java.util.ArrayList<>();
        toCollapse = new java.util.ArrayList<>();
        shifted = 0;
        // As per reddit API gids: 0=silver, 1=gold, 2=platinum
        awardIcons = new android.graphics.Bitmap[]{ android.graphics.BitmapFactory.decodeResource(mContext.getResources(), me.ccrama.redditslide.R.drawable.silver), android.graphics.BitmapFactory.decodeResource(mContext.getResources(), me.ccrama.redditslide.R.drawable.gold), android.graphics.BitmapFactory.decodeResource(mContext.getResources(), me.ccrama.redditslide.R.drawable.platinum) };
    }

    public void reset(android.content.Context mContext, me.ccrama.redditslide.Adapters.SubmissionComments dataSet, android.support.v7.widget.RecyclerView listView, net.dean.jraw.models.Submission submission, boolean reset) {
        doTimes();
        this.mContext = mContext;
        this.listView = listView;
        this.dataSet = dataSet;
        this.submission = submission;
        hidden = new java.util.HashSet<>();
        currentComments = dataSet.comments;
        if (currentComments != null) {
            for (int i = 0; i < currentComments.size(); i++) {
                keys.put(currentComments.get(i).getName(), i);
            }
        }
        hiddenPersons = new java.util.ArrayList<>();
        toCollapse = new java.util.ArrayList<>();
        if (((currentSelectedItem != null) && (!currentSelectedItem.isEmpty())) && (!reset)) {
            notifyDataSetChanged();
        } else if ((currentComments != null) && (!reset)) {
            notifyItemRangeChanged(2, currentComments.size() + 1);
        } else if (currentComments == null) {
            currentComments = new java.util.ArrayList<>();
            notifyDataSetChanged();
        } else {
            notifyDataSetChanged();
        }
        if ((((currentSelectedItem != null) && (!currentSelectedItem.isEmpty())) && (currentComments != null)) && (!currentComments.isEmpty())) {
            int i = 2;
            for (me.ccrama.redditslide.Adapters.CommentObject n : currentComments) {
                if ((n instanceof me.ccrama.redditslide.Adapters.CommentItem) && n.comment.getComment().getFullName().contains(currentSelectedItem)) {
                    ((me.ccrama.redditslide.Views.PreCachingLayoutManagerComments) (listView.getLayoutManager())).scrollToPositionWithOffset(i, mPage.headerHeight);
                    break;
                }
                i++;
            }
            mPage.resetScroll(true);
        }
        if (mContext instanceof me.ccrama.redditslide.Activities.BaseActivity) {
            ((me.ccrama.redditslide.Activities.BaseActivity) (mContext)).setShareUrl("https://reddit.com" + submission.getPermalink());
        }
    }

    @java.lang.Override
    public android.support.v7.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup viewGroup, int i) {
        switch (i) {
            case SPACER :
                {
                    android.view.View v = android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.spacer_post, viewGroup, false);
                    return new me.ccrama.redditslide.Adapters.CommentAdapter.SpacerViewHolder(v);
                }
            case me.ccrama.redditslide.Adapters.CommentAdapter.HEADER :
                {
                    android.view.View v = android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.submission_fullscreen, viewGroup, false);
                    return new me.ccrama.redditslide.Adapters.SubmissionViewHolder(v);
                }
            case 2 :
                {
                    android.view.View v = android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.comment, viewGroup, false);
                    return new me.ccrama.redditslide.Adapters.CommentViewHolder(v);
                }
            default :
                {
                    android.view.View v = android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.morecomment, viewGroup, false);
                    return new me.ccrama.redditslide.Adapters.MoreCommentViewHolder(v);
                }
        }
    }

    public class SpacerViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public SpacerViewHolder(android.view.View itemView) {
            super(itemView);
        }
    }

    public void expandAll() {
        if (currentComments == null)
            return;

        for (me.ccrama.redditslide.Adapters.CommentObject o : currentComments) {
            if (o.comment.isTopLevel()) {
                if (hiddenPersons.contains(o.comment.getComment().getFullName())) {
                    hiddenPersons.remove(o.comment.getComment().getFullName());
                }
                unhideAll(o.comment);
            }
        }
        notifyItemChanged(2);
    }

    public void collapseAll() {
        if (currentComments == null)
            return;

        for (me.ccrama.redditslide.Adapters.CommentObject o : currentComments) {
            if (o.comment.isTopLevel()) {
                if (!hiddenPersons.contains(o.comment.getComment().getFullName())) {
                    hiddenPersons.add(o.comment.getComment().getFullName());
                }
                hideAll(o.comment);
            }
        }
        notifyItemChanged(2);
    }

    public void doScoreText(me.ccrama.redditslide.Adapters.CommentViewHolder holder, net.dean.jraw.models.Comment comment, me.ccrama.redditslide.Adapters.CommentAdapter adapter) {
        holder.content.setText(me.ccrama.redditslide.Adapters.CommentAdapterHelper.getScoreString(comment, mContext, holder, submission, adapter));
    }

    public void doTimes() {
        if ((((submission != null) && me.ccrama.redditslide.SettingValues.commentLastVisit) && (!dataSet.single)) && (me.ccrama.redditslide.SettingValues.storeHistory && ((!submission.isNsfw()) || me.ccrama.redditslide.SettingValues.storeNSFWHistory))) {
            lastSeen = me.ccrama.redditslide.HasSeen.getSeenTime(submission);
            java.lang.String fullname = submission.getFullName();
            if (fullname.contains("t3_")) {
                fullname = fullname.substring(3, fullname.length());
            }
            me.ccrama.redditslide.HasSeen.seenTimes.put(fullname, java.lang.System.currentTimeMillis());
            com.lusfold.androidkeyvaluestore.KVStore.getInstance().insert(fullname, java.lang.String.valueOf(java.lang.System.currentTimeMillis()));
        }
        if (submission != null) {
            if (me.ccrama.redditslide.SettingValues.storeHistory) {
                if (submission.isNsfw() && (!me.ccrama.redditslide.SettingValues.storeNSFWHistory)) {
                } else {
                    me.ccrama.redditslide.HasSeen.addSeen(submission.getFullName());
                }
                me.ccrama.redditslide.LastComments.setComments(submission);
            }
        }
    }

    @java.lang.Override
    public void onBindViewHolder(final android.support.v7.widget.RecyclerView.ViewHolder firstHolder, int old) {
        int pos = (old != 0) ? old - 1 : old;
        if (firstHolder instanceof me.ccrama.redditslide.Adapters.CommentViewHolder) {
            final me.ccrama.redditslide.Adapters.CommentViewHolder holder = ((me.ccrama.redditslide.Adapters.CommentViewHolder) (firstHolder));
            int datasetPosition = pos - 1;
            datasetPosition = getRealPosition(datasetPosition);
            if (pos > toShiftTo) {
                shifted = 0;
            }
            if (pos < shiftFrom) {
                shifted = 0;
            }
            final net.dean.jraw.models.CommentNode baseNode = currentComments.get(datasetPosition).comment;
            final net.dean.jraw.models.Comment comment = baseNode.getComment();
            if (pos == (getItemCount() - 1)) {
                holder.itemView.setPadding(0, 0, 0, ((int) (mContext.getResources().getDimension(me.ccrama.redditslide.R.dimen.overview_top_padding_single))));
            } else {
                holder.itemView.setPadding(0, 0, 0, 0);
            }
            doScoreText(holder, comment, this);
            // Long click listeners
            android.view.View.OnLongClickListener onLongClickListener = new android.view.View.OnLongClickListener() {
                @java.lang.Override
                public boolean onLongClick(android.view.View v) {
                    if (me.ccrama.redditslide.SettingValues.swap) {
                        doOnClick(holder, comment, baseNode);
                    } else {
                        doLongClick(holder, comment, baseNode);
                    }
                    return true;
                }
            };
            holder.firstTextView.setOnLongClickListener(onLongClickListener);
            holder.commentOverflow.setOnLongClickListener(onLongClickListener);
            holder.itemView.setOnLongClickListener(new android.view.View.OnLongClickListener() {
                @java.lang.Override
                public boolean onLongClick(android.view.View v) {
                    if (!currentlyEditingId.equals(comment.getFullName())) {
                        if (me.ccrama.redditslide.SettingValues.swap) {
                            doOnClick(holder, comment, baseNode);
                        } else {
                            doLongClick(holder, comment, baseNode);
                        }
                    }
                    return true;
                }
            });
            // Single click listeners
            me.ccrama.redditslide.util.OnSingleClickListener singleClick = new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View v) {
                    if (!currentlyEditingId.equals(comment.getFullName())) {
                        if (me.ccrama.redditslide.SettingValues.swap) {
                            doLongClick(holder, comment, baseNode);
                        } else {
                            doOnClick(holder, comment, baseNode);
                        }
                    }
                }
            };
            holder.itemView.setOnClickListener(singleClick);
            holder.commentOverflow.setOnClickListener(singleClick);
            if (((!toCollapse.contains(comment.getFullName())) && me.ccrama.redditslide.SettingValues.collapseComments) || (!me.ccrama.redditslide.SettingValues.collapseComments)) {
                setViews(comment.getDataNode().get("body_html").asText(), submission.getSubredditName(), holder, singleClick, onLongClickListener);
            }
            holder.firstTextView.setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View v) {
                    me.ccrama.redditslide.SpoilerRobotoTextView SpoilerRobotoTextView = ((me.ccrama.redditslide.SpoilerRobotoTextView) (v));
                    if (me.ccrama.redditslide.SettingValues.swap) {
                        if (!SpoilerRobotoTextView.isSpoilerClicked()) {
                            doLongClick(holder, comment, baseNode);
                        } else if (SpoilerRobotoTextView.isSpoilerClicked()) {
                            SpoilerRobotoTextView.resetSpoilerClicked();
                        }
                    } else if (!SpoilerRobotoTextView.isSpoilerClicked()) {
                        doOnClick(holder, comment, baseNode);
                    } else if (SpoilerRobotoTextView.isSpoilerClicked()) {
                        SpoilerRobotoTextView.resetSpoilerClicked();
                    }
                }
            });
            if (((me.ccrama.redditslide.ImageFlairs.isSynced(comment.getSubredditName()) && (comment.getAuthorFlair() != null)) && (comment.getAuthorFlair().getCssClass() != null)) && (!comment.getAuthorFlair().getCssClass().isEmpty())) {
                boolean set = false;
                for (java.lang.String s : comment.getAuthorFlair().getCssClass().split(" ")) {
                    java.io.File file = com.nostra13.universalimageloader.utils.DiskCacheUtils.findInCache((comment.getSubredditName().toLowerCase(java.util.Locale.ENGLISH) + ":") + s.toLowerCase(java.util.Locale.ENGLISH), me.ccrama.redditslide.ImageFlairs.getFlairImageLoader(mContext).getInstance().getDiskCache());
                    if ((file != null) && file.exists()) {
                        set = true;
                        holder.imageFlair.setVisibility(android.view.View.VISIBLE);
                        java.lang.String decodedImgUri = android.net.Uri.fromFile(file).toString();
                        me.ccrama.redditslide.ImageFlairs.getFlairImageLoader(mContext).displayImage(decodedImgUri, holder.imageFlair);
                        break;
                    }
                }
                if (!set) {
                    holder.imageFlair.setImageDrawable(null);
                    holder.imageFlair.setVisibility(android.view.View.GONE);
                }
            } else {
                holder.imageFlair.setVisibility(android.view.View.GONE);
            }
            // Set typeface for body
            int type = new me.ccrama.redditslide.Visuals.FontPreferences(mContext).getFontTypeComment().getTypeface();
            android.graphics.Typeface typeface;
            if (type >= 0) {
                typeface = com.devspark.robototextview.RobotoTypefaces.obtainTypeface(mContext, type);
            } else {
                typeface = android.graphics.Typeface.DEFAULT;
            }
            holder.firstTextView.setTypeface(typeface);
            // Show padding on top
            if (baseNode.isTopLevel()) {
                holder.itemView.findViewById(me.ccrama.redditslide.R.id.next).setVisibility(android.view.View.VISIBLE);
            } else if (holder.itemView.findViewById(me.ccrama.redditslide.R.id.next).getVisibility() == android.view.View.VISIBLE) {
                holder.itemView.findViewById(me.ccrama.redditslide.R.id.next).setVisibility(android.view.View.GONE);
            }
            // Should be collapsed?
            if (hiddenPersons.contains(comment.getFullName()) || toCollapse.contains(comment.getFullName())) {
                int childnumber = getChildNumber(baseNode);
                if (hiddenPersons.contains(comment.getFullName()) && (childnumber > 0)) {
                    holder.childrenNumber.setVisibility(android.view.View.VISIBLE);
                    holder.childrenNumber.setText("+" + childnumber);
                } else {
                    holder.childrenNumber.setVisibility(android.view.View.GONE);
                }
                if (me.ccrama.redditslide.SettingValues.collapseComments && toCollapse.contains(comment.getFullName())) {
                    holder.firstTextView.setVisibility(android.view.View.GONE);
                    holder.commentOverflow.setVisibility(android.view.View.GONE);
                }
            } else {
                holder.childrenNumber.setVisibility(android.view.View.GONE);
                holder.commentOverflow.setVisibility(android.view.View.VISIBLE);
            }
            holder.dot.setVisibility(android.view.View.VISIBLE);
            int dwidth = ((int) ((me.ccrama.redditslide.SettingValues.largeDepth ? 5 : 3) * android.content.res.Resources.getSystem().getDisplayMetrics().density));
            int width = 0;
            // Padding on the left, starting with the third comment
            for (int i = 2; i < baseNode.getDepth(); i++) {
                width += dwidth;
            }
            android.support.v7.widget.RecyclerView.LayoutParams params = ((android.support.v7.widget.RecyclerView.LayoutParams) (holder.itemView.getLayoutParams()));
            params.setMargins(width, 0, 0, 0);
            holder.itemView.setLayoutParams(params);
            android.widget.RelativeLayout.LayoutParams params2 = ((android.widget.RelativeLayout.LayoutParams) (holder.dot.getLayoutParams()));
            params2.width = dwidth;
            holder.dot.setLayoutParams(params2);
            if ((baseNode.getDepth() - 1) > 0) {
                int i22 = baseNode.getDepth() - 2;
                java.lang.String commentOp = dataSet.commentOPs.get(comment.getId());
                if (((me.ccrama.redditslide.SettingValues.highlightCommentOP && (commentOp != null)) && (comment != null)) && commentOp.equals(comment.getAuthor())) {
                    holder.dot.setBackgroundColor(android.support.v4.content.ContextCompat.getColor(mContext, me.ccrama.redditslide.R.color.md_purple_500));
                } else if ((i22 % 5) == 0) {
                    holder.dot.setBackgroundColor(android.support.v4.content.ContextCompat.getColor(mContext, !me.ccrama.redditslide.SettingValues.colorCommentDepth ? me.ccrama.redditslide.R.color.md_grey_700 : me.ccrama.redditslide.R.color.md_blue_500));
                } else if ((i22 % 4) == 0) {
                    holder.dot.setBackgroundColor(android.support.v4.content.ContextCompat.getColor(mContext, !me.ccrama.redditslide.SettingValues.colorCommentDepth ? me.ccrama.redditslide.R.color.md_grey_600 : me.ccrama.redditslide.R.color.md_green_500));
                } else if ((i22 % 3) == 0) {
                    holder.dot.setBackgroundColor(android.support.v4.content.ContextCompat.getColor(mContext, !me.ccrama.redditslide.SettingValues.colorCommentDepth ? me.ccrama.redditslide.R.color.md_grey_500 : me.ccrama.redditslide.R.color.md_yellow_500));
                } else if ((i22 % 2) == 0) {
                    holder.dot.setBackgroundColor(android.support.v4.content.ContextCompat.getColor(mContext, !me.ccrama.redditslide.SettingValues.colorCommentDepth ? me.ccrama.redditslide.R.color.md_grey_400 : me.ccrama.redditslide.R.color.md_orange_500));
                } else {
                    holder.dot.setBackgroundColor(android.support.v4.content.ContextCompat.getColor(mContext, !me.ccrama.redditslide.SettingValues.colorCommentDepth ? me.ccrama.redditslide.R.color.md_grey_300 : me.ccrama.redditslide.R.color.md_red_500));
                }
            } else {
                holder.dot.setVisibility(android.view.View.GONE);
            }
            if ((((currentSelectedItem != null) && comment.getFullName().contains(currentSelectedItem)) && (!currentSelectedItem.isEmpty())) && (!currentlyEditingId.equals(comment.getFullName()))) {
                doHighlighted(holder, comment, baseNode, false);
            } else if (!currentlyEditingId.equals(comment.getFullName())) {
                setCommentStateUnhighlighted(holder, baseNode, false);
            }
            if (deleted.contains(comment.getFullName())) {
                holder.firstTextView.setText(me.ccrama.redditslide.R.string.comment_deleted);
                holder.content.setText(me.ccrama.redditslide.R.string.comment_deleted);
            }
            if (currentlyEditingId.equals(comment.getFullName())) {
                setCommentStateUnhighlighted(holder, baseNode, false);
                setCommentStateHighlighted(holder, comment, baseNode, true, false);
            }
        } else if ((firstHolder instanceof me.ccrama.redditslide.Adapters.SubmissionViewHolder) && (submission != null)) {
            submissionViewHolder = ((me.ccrama.redditslide.Adapters.SubmissionViewHolder) (firstHolder));
            new me.ccrama.redditslide.SubmissionViews.PopulateSubmissionViewHolder().populateSubmissionViewHolder(((me.ccrama.redditslide.Adapters.SubmissionViewHolder) (firstHolder)), submission, ((android.app.Activity) (mContext)), true, true, null, listView, false, false, null, this);
            if (me.ccrama.redditslide.Authentication.isLoggedIn && me.ccrama.redditslide.Authentication.didOnline) {
                if (submission.isArchived() || submission.isLocked()) {
                    firstHolder.itemView.findViewById(me.ccrama.redditslide.R.id.reply).setVisibility(android.view.View.GONE);
                } else {
                    firstHolder.itemView.findViewById(me.ccrama.redditslide.R.id.reply).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                        @java.lang.Override
                        public void onSingleClick(android.view.View v) {
                            doReplySubmission(firstHolder);
                        }
                    });
                    firstHolder.itemView.findViewById(me.ccrama.redditslide.R.id.discard).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                        @java.lang.Override
                        public void onSingleClick(android.view.View v) {
                            firstHolder.itemView.findViewById(me.ccrama.redditslide.R.id.innerSend).setVisibility(android.view.View.GONE);
                            currentlyEditing = null;
                            editingPosition = -1;
                            if (me.ccrama.redditslide.SettingValues.fastscroll) {
                                mPage.fastScroll.setVisibility(android.view.View.VISIBLE);
                            }
                            if (mPage.fab != null)
                                mPage.fab.setVisibility(android.view.View.VISIBLE);

                            mPage.overrideFab = false;
                            currentlyEditingId = "";
                            backedText = "";
                            android.view.View view = ((android.app.Activity) (mContext)).findViewById(android.R.id.content);
                            if (view != null) {
                                android.view.inputmethod.InputMethodManager imm = ((android.view.inputmethod.InputMethodManager) (mContext.getSystemService(android.content.Context.INPUT_METHOD_SERVICE)));
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                        }
                    });
                }
            } else {
                firstHolder.itemView.findViewById(me.ccrama.redditslide.R.id.innerSend).setVisibility(android.view.View.GONE);
                firstHolder.itemView.findViewById(me.ccrama.redditslide.R.id.reply).setVisibility(android.view.View.GONE);
            }
            firstHolder.itemView.findViewById(me.ccrama.redditslide.R.id.more).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View v) {
                    firstHolder.itemView.findViewById(me.ccrama.redditslide.R.id.menu).callOnClick();
                }
            });
        } else if (firstHolder instanceof me.ccrama.redditslide.Adapters.MoreCommentViewHolder) {
            final me.ccrama.redditslide.Adapters.MoreCommentViewHolder holder = ((me.ccrama.redditslide.Adapters.MoreCommentViewHolder) (firstHolder));
            int nextPos = pos - 1;
            nextPos = getRealPosition(nextPos);
            final me.ccrama.redditslide.Adapters.MoreChildItem baseNode = ((me.ccrama.redditslide.Adapters.MoreChildItem) (currentComments.get(nextPos)));
            if (baseNode.children.getCount() > 0) {
                try {
                    holder.content.setText(mContext.getString(me.ccrama.redditslide.R.string.comment_load_more_string_new, baseNode.children.getLocalizedCount()));
                } catch (java.lang.Exception e) {
                    holder.content.setText(me.ccrama.redditslide.R.string.comment_load_more_number_unknown);
                }
            } else if (!baseNode.children.getChildrenIds().isEmpty()) {
                holder.content.setText(me.ccrama.redditslide.R.string.comment_load_more_number_unknown);
            } else {
                holder.content.setText(me.ccrama.redditslide.R.string.thread_continue);
            }
            int dwidth = ((int) ((me.ccrama.redditslide.SettingValues.largeDepth ? 5 : 3) * android.content.res.Resources.getSystem().getDisplayMetrics().density));
            int width = 0;
            for (int i = 1; i < baseNode.comment.getDepth(); i++) {
                width += dwidth;
            }
            final android.view.View progress = holder.loading;
            progress.setVisibility(android.view.View.GONE);
            final int finalNextPos = nextPos;
            holder.content.setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View v) {
                    if (baseNode.children.getChildrenIds().isEmpty()) {
                        java.lang.String toGoTo = (("https://reddit.com" + submission.getPermalink()) + baseNode.comment.getComment().getId()) + "?context=0";
                        new me.ccrama.redditslide.OpenRedditLink(mContext, toGoTo, true);
                    } else if (progress.getVisibility() == android.view.View.GONE) {
                        progress.setVisibility(android.view.View.VISIBLE);
                        holder.content.setText(me.ccrama.redditslide.R.string.comment_loading_more);
                        currentLoading = new me.ccrama.redditslide.Adapters.CommentAdapter.AsyncLoadMore(getRealPosition(holder.getAdapterPosition() - 2), holder.getAdapterPosition(), holder, finalNextPos, baseNode.comment.getComment().getFullName());
                        currentLoading.execute(baseNode);
                    }
                }
            });
            android.support.v7.widget.RecyclerView.LayoutParams params = ((android.support.v7.widget.RecyclerView.LayoutParams) (holder.itemView.getLayoutParams()));
            params.setMargins(width, 0, 0, 0);
            holder.itemView.setLayoutParams(params);
        }
        if (firstHolder instanceof me.ccrama.redditslide.Adapters.CommentAdapter.SpacerViewHolder) {
            // Make a space the size of the toolbar minus 1 so there isn't a gap
            firstHolder.itemView.findViewById(me.ccrama.redditslide.R.id.height).setLayoutParams(new android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, (me.ccrama.redditslide.Constants.SINGLE_HEADER_VIEW_OFFSET - me.ccrama.redditslide.Reddit.dpToPxVertical(1)) + mPage.shownHeaders));
        }
    }

    me.ccrama.redditslide.Adapters.CommentAdapter.AsyncLoadMore currentLoading;

    java.lang.String changedProfile;

    private void doReplySubmission(android.support.v7.widget.RecyclerView.ViewHolder submissionViewHolder) {
        final android.view.View replyArea = submissionViewHolder.itemView.findViewById(me.ccrama.redditslide.R.id.innerSend);
        if (replyArea.getVisibility() == android.view.View.GONE) {
            expandSubmissionReply(replyArea);
            android.widget.EditText replyLine = submissionViewHolder.itemView.findViewById(me.ccrama.redditslide.R.id.replyLine);
            me.ccrama.redditslide.Views.DoEditorActions.doActions(replyLine, submissionViewHolder.itemView, fm, ((android.app.Activity) (mContext)), submission.isSelfPost() ? submission.getSelftext() : null, new java.lang.String[]{ submission.getAuthor() });
            currentlyEditing = submissionViewHolder.itemView.findViewById(me.ccrama.redditslide.R.id.replyLine);
            final android.widget.TextView profile = submissionViewHolder.itemView.findViewById(me.ccrama.redditslide.R.id.profile);
            changedProfile = me.ccrama.redditslide.Authentication.name;
            profile.setText("/u/" + changedProfile);
            profile.setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    final java.util.HashMap<java.lang.String, java.lang.String> accounts = new java.util.HashMap<>();
                    for (java.lang.String s : me.ccrama.redditslide.Authentication.authentication.getStringSet("accounts", new java.util.HashSet<java.lang.String>())) {
                        if (s.contains(":")) {
                            accounts.put(s.split(":")[0], s.split(":")[1]);
                        } else {
                            accounts.put(s, "");
                        }
                    }
                    final java.util.ArrayList<java.lang.String> keys = new java.util.ArrayList<>(accounts.keySet());
                    final int i = keys.indexOf(changedProfile);
                    com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext);
                    builder.setTitle(mContext.getString(me.ccrama.redditslide.R.string.replies_switch_accounts));
                    builder.setSingleChoiceItems(keys.toArray(new java.lang.String[keys.size()]), i, new android.content.DialogInterface.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.content.DialogInterface dialog, int which) {
                            changedProfile = keys.get(which);
                            profile.setText("/u/" + changedProfile);
                        }
                    });
                    builder.alwaysCallSingleChoiceCallback();
                    builder.setNegativeButton(me.ccrama.redditslide.R.string.btn_cancel, null);
                    builder.show();
                }
            });
            currentlyEditing.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
                @java.lang.Override
                public void onFocusChange(android.view.View v, boolean hasFocus) {
                    if (hasFocus) {
                        mPage.fastScroll.setVisibility(android.view.View.GONE);
                        if (mPage.fab != null)
                            mPage.fab.setVisibility(android.view.View.GONE);

                        mPage.overrideFab = true;
                    } else if (me.ccrama.redditslide.SettingValues.fastscroll) {
                        mPage.fastScroll.setVisibility(android.view.View.VISIBLE);
                        if (mPage.fab != null)
                            mPage.fab.setVisibility(android.view.View.VISIBLE);

                        mPage.overrideFab = false;
                    }
                }
            });
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                replyLine.setOnFocusChangeListener((android.view.View v,boolean b) -> {
                    if (b) {
                        v.postDelayed(() -> {
                            if (!v.hasFocus())
                                v.requestFocus();

                        }, 100);
                    }
                });
            }
            replyLine.requestFocus();
            android.view.inputmethod.InputMethodManager imm = ((android.view.inputmethod.InputMethodManager) (mContext.getSystemService(android.content.Context.INPUT_METHOD_SERVICE)));
            imm.toggleSoftInput(android.view.inputmethod.InputMethodManager.SHOW_FORCED, android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY);
            editingPosition = submissionViewHolder.getAdapterPosition();
            submissionViewHolder.itemView.findViewById(me.ccrama.redditslide.R.id.send).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View v) {
                    dataSet.refreshLayout.setRefreshing(true);
                    if (me.ccrama.redditslide.SettingValues.fastscroll) {
                        mPage.fastScroll.setVisibility(android.view.View.VISIBLE);
                    }
                    if (mPage.fab != null)
                        mPage.fab.setVisibility(android.view.View.VISIBLE);

                    mPage.overrideFab = false;
                    if (currentlyEditing != null) {
                        java.lang.String text = currentlyEditing.getText().toString();
                        new me.ccrama.redditslide.Adapters.CommentAdapter.ReplyTaskComment(submission, changedProfile).execute(text);
                        replyArea.setVisibility(android.view.View.GONE);
                        currentlyEditing.setText("");
                        currentlyEditing = null;
                        editingPosition = -1;
                        // Hide soft keyboard
                        android.view.View view = ((android.app.Activity) (mContext)).findViewById(android.R.id.content);
                        if (view != null) {
                            android.view.inputmethod.InputMethodManager imm = ((android.view.inputmethod.InputMethodManager) (mContext.getSystemService(android.content.Context.INPUT_METHOD_SERVICE)));
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                }
            });
        } else {
            android.view.View view = ((android.app.Activity) (mContext)).findViewById(android.R.id.content);
            if (view != null) {
                android.view.inputmethod.InputMethodManager imm = ((android.view.inputmethod.InputMethodManager) (mContext.getSystemService(android.content.Context.INPUT_METHOD_SERVICE)));
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            collapseAndHide(replyArea);
        }
    }

    public void setViews(java.lang.String rawHTML, java.lang.String subredditName, final me.ccrama.redditslide.SpoilerRobotoTextView firstTextView, me.ccrama.redditslide.Views.CommentOverflow commentOverflow) {
        if (rawHTML.isEmpty()) {
            return;
        }
        java.util.List<java.lang.String> blocks = me.ccrama.redditslide.util.SubmissionParser.getBlocks(rawHTML);
        int startIndex = 0;
        // the <div class="md"> case is when the body contains a table or code block first
        if (!blocks.get(0).equals("<div class=\"md\">")) {
            firstTextView.setVisibility(android.view.View.VISIBLE);
            firstTextView.setTextHtml(blocks.get(0), subredditName);
            startIndex = 1;
        } else {
            firstTextView.setText("");
        }
        if (blocks.size() > 1) {
            if (startIndex == 0) {
                commentOverflow.setViews(blocks, subredditName);
            } else {
                commentOverflow.setViews(blocks.subList(startIndex, blocks.size()), subredditName);
            }
        } else {
            commentOverflow.removeAllViews();
        }
    }

    public void setViews(java.lang.String rawHTML, java.lang.String subredditName, final me.ccrama.redditslide.SpoilerRobotoTextView firstTextView, me.ccrama.redditslide.Views.CommentOverflow commentOverflow, android.view.View.OnClickListener click, android.view.View.OnLongClickListener onLongClickListener) {
        if (rawHTML.isEmpty()) {
            return;
        }
        java.util.List<java.lang.String> blocks = me.ccrama.redditslide.util.SubmissionParser.getBlocks(rawHTML);
        int startIndex = 0;
        // the <div class="md"> case is when the body contains a table or code block first
        if (!blocks.get(0).equals("<div class=\"md\">")) {
            firstTextView.setVisibility(android.view.View.VISIBLE);
            firstTextView.setTextHtml(blocks.get(0) + " ", subredditName);
            startIndex = 1;
        } else {
            firstTextView.setText("");
        }
        if (blocks.size() > 1) {
            if (startIndex == 0) {
                commentOverflow.setViews(blocks, subredditName, click, onLongClickListener);
            } else {
                commentOverflow.setViews(blocks.subList(startIndex, blocks.size()), subredditName, click, onLongClickListener);
            }
        } else {
            commentOverflow.removeAllViews();
        }
    }

    private void setViews(java.lang.String rawHTML, java.lang.String subredditName, me.ccrama.redditslide.Adapters.CommentViewHolder holder) {
        setViews(rawHTML, subredditName, holder.firstTextView, holder.commentOverflow);
    }

    private void setViews(java.lang.String rawHTML, java.lang.String subredditName, me.ccrama.redditslide.Adapters.CommentViewHolder holder, android.view.View.OnClickListener click, android.view.View.OnLongClickListener longClickListener) {
        setViews(rawHTML, subredditName, holder.firstTextView, holder.commentOverflow, click, longClickListener);
    }

    int editingPosition;

    private android.animation.ValueAnimator slideAnimator(int start, int end, final android.view.View v) {
        android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofInt(start, end);
        animator.setInterpolator(new android.support.v4.view.animation.FastOutSlowInInterpolator());
        animator.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {
            @java.lang.Override
            public void onAnimationUpdate(android.animation.ValueAnimator valueAnimator) {
                // Update Height
                int value = ((java.lang.Integer) (valueAnimator.getAnimatedValue()));
                android.view.ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.height = value;
                v.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    private void collapseAndHide(final android.view.View v) {
        int finalHeight = v.getHeight();
        mAnimator = slideAnimator(finalHeight, 0, v);
        mAnimator.addListener(new android.animation.Animator.AnimatorListener() {
            @java.lang.Override
            public void onAnimationStart(android.animation.Animator animation) {
            }

            @java.lang.Override
            public void onAnimationEnd(android.animation.Animator animator) {
                // Height=0, but it set visibility to GONE
                v.setVisibility(android.view.View.GONE);
            }

            @java.lang.Override
            public void onAnimationCancel(android.animation.Animator animation) {
                v.setVisibility(android.view.View.GONE);
            }

            @java.lang.Override
            public void onAnimationRepeat(android.animation.Animator animation) {
            }
        });
        mAnimator.start();
    }

    private void collapseAndRemove(final android.view.View v) {
        int finalHeight = v.getHeight();
        mAnimator = slideAnimator(finalHeight, 0, v);
        mAnimator.addListener(new android.animation.Animator.AnimatorListener() {
            @java.lang.Override
            public void onAnimationStart(android.animation.Animator animation) {
            }

            @java.lang.Override
            public void onAnimationEnd(android.animation.Animator animator) {
                // Height=0, but it set visibility to GONE
                ((android.widget.LinearLayout) (v)).removeAllViews();
            }

            @java.lang.Override
            public void onAnimationCancel(android.animation.Animator animation) {
                ((android.widget.LinearLayout) (v)).removeAllViews();
            }

            @java.lang.Override
            public void onAnimationRepeat(android.animation.Animator animation) {
            }
        });
        mAnimator.start();
    }

    private void doShowMenu(final android.view.View l) {
        l.setVisibility(android.view.View.VISIBLE);
        final int widthSpec = android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED);
        l.measure(widthSpec, heightSpec);
        final android.view.View l2 = l.findViewById(me.ccrama.redditslide.R.id.menu);
        final int widthSpec2 = android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED);
        final int heightSpec2 = android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED);
        l2.measure(widthSpec2, heightSpec2);
        android.animation.ValueAnimator mAnimator = slideAnimator(l.getMeasuredHeight(), l2.getMeasuredHeight(), l);
        mAnimator.addListener(new android.animation.Animator.AnimatorListener() {
            @java.lang.Override
            public void onAnimationStart(android.animation.Animator animation) {
            }

            @java.lang.Override
            public void onAnimationEnd(android.animation.Animator animation) {
                l2.setVisibility(android.view.View.VISIBLE);
            }

            @java.lang.Override
            public void onAnimationCancel(android.animation.Animator animation) {
                l2.setVisibility(android.view.View.VISIBLE);
            }

            @java.lang.Override
            public void onAnimationRepeat(android.animation.Animator animation) {
            }
        });
        mAnimator.start();
    }

    android.animation.ValueAnimator mAnimator;

    private void expand(final android.view.View l) {
        l.setVisibility(android.view.View.VISIBLE);
        final int widthSpec = android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED);
        l.measure(widthSpec, heightSpec);
        android.view.View l2 = (l.findViewById(me.ccrama.redditslide.R.id.replyArea) == null) ? l.findViewById(me.ccrama.redditslide.R.id.innerSend) : l.findViewById(me.ccrama.redditslide.R.id.replyArea);
        final int widthSpec2 = android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED);
        final int heightSpec2 = android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED);
        l2.measure(widthSpec2, heightSpec2);
        mAnimator = slideAnimator(0, l.getMeasuredHeight() - l2.getMeasuredHeight(), l);
        mAnimator.start();
    }

    private void expandAndSetParams(final android.view.View l) {
        l.setVisibility(android.view.View.VISIBLE);
        final int widthSpec = android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED);
        l.measure(widthSpec, heightSpec);
        android.view.View l2 = (l.findViewById(me.ccrama.redditslide.R.id.replyArea) == null) ? l.findViewById(me.ccrama.redditslide.R.id.innerSend) : l.findViewById(me.ccrama.redditslide.R.id.replyArea);
        final int widthSpec2 = android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED);
        final int heightSpec2 = android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED);
        l2.measure(widthSpec2, heightSpec2);
        mAnimator = slideAnimator(l.getMeasuredHeight() - l2.getMeasuredHeight(), l.getMeasuredHeight() - (l.getMeasuredHeight() - l2.getMeasuredHeight()), l);
        mAnimator.addListener(new android.animation.Animator.AnimatorListener() {
            @java.lang.Override
            public void onAnimationStart(android.animation.Animator animation) {
            }

            @java.lang.Override
            public void onAnimationEnd(android.animation.Animator animation) {
                android.widget.RelativeLayout.LayoutParams params = ((android.widget.RelativeLayout.LayoutParams) (l.getLayoutParams()));
                params.height = android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT;
                params.addRule(android.widget.RelativeLayout.BELOW, me.ccrama.redditslide.R.id.commentOverflow);
                l.setLayoutParams(params);
            }

            @java.lang.Override
            public void onAnimationCancel(android.animation.Animator animation) {
                android.widget.RelativeLayout.LayoutParams params = ((android.widget.RelativeLayout.LayoutParams) (l.getLayoutParams()));
                params.height = android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT;
                params.addRule(android.widget.RelativeLayout.BELOW, me.ccrama.redditslide.R.id.commentOverflow);
                l.setLayoutParams(params);
            }

            @java.lang.Override
            public void onAnimationRepeat(android.animation.Animator animation) {
            }
        });
        mAnimator.start();
    }

    private void expandSubmissionReply(final android.view.View l) {
        l.setVisibility(android.view.View.VISIBLE);
        final int widthSpec = android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED);
        l.measure(widthSpec, heightSpec);
        mAnimator = slideAnimator(0, l.getMeasuredHeight(), l);
        mAnimator.addListener(new android.animation.Animator.AnimatorListener() {
            @java.lang.Override
            public void onAnimationStart(android.animation.Animator animation) {
            }

            @java.lang.Override
            public void onAnimationEnd(android.animation.Animator animation) {
                android.widget.LinearLayout.LayoutParams params = ((android.widget.LinearLayout.LayoutParams) (l.getLayoutParams()));
                params.height = android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT;
                l.setLayoutParams(params);
            }

            @java.lang.Override
            public void onAnimationCancel(android.animation.Animator animation) {
                android.widget.LinearLayout.LayoutParams params = ((android.widget.LinearLayout.LayoutParams) (l.getLayoutParams()));
                params.height = android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT;
                l.setLayoutParams(params);
            }

            @java.lang.Override
            public void onAnimationRepeat(android.animation.Animator animation) {
            }
        });
        mAnimator.start();
    }

    net.dean.jraw.models.CommentNode currentBaseNode;

    public void setCommentStateHighlighted(final me.ccrama.redditslide.Adapters.CommentViewHolder holder, final net.dean.jraw.models.Comment n, final net.dean.jraw.models.CommentNode baseNode, boolean isReplying, boolean animate) {
        if ((currentlySelected != null) && (currentlySelected != holder)) {
            setCommentStateUnhighlighted(currentlySelected, currentBaseNode, true);
        }
        if (mContext instanceof me.ccrama.redditslide.Activities.BaseActivity) {
            ((me.ccrama.redditslide.Activities.BaseActivity) (mContext)).setShareUrl((("https://reddit.com" + submission.getPermalink()) + n.getFullName()) + "?context=3");
        }
        // If a comment is hidden and (Swap long press == true), then a single click will un-hide the comment
        // and expand to show all children comments
        if ((me.ccrama.redditslide.SettingValues.swap && (holder.firstTextView.getVisibility() == android.view.View.GONE)) && (!isReplying)) {
            hiddenPersons.remove(n.getFullName());
            unhideAll(baseNode, holder.getAdapterPosition() + 1);
            if (toCollapse.contains(n.getFullName()) && me.ccrama.redditslide.SettingValues.collapseComments) {
                setViews(n.getDataNode().get("body_html").asText(), submission.getSubredditName(), holder);
            }
            me.ccrama.redditslide.Adapters.CommentAdapterHelper.hideChildrenObject(holder.childrenNumber);
            holder.commentOverflow.setVisibility(android.view.View.VISIBLE);
            toCollapse.remove(n.getFullName());
        } else {
            currentlySelected = holder;
            currentBaseNode = baseNode;
            int color = me.ccrama.redditslide.Visuals.Palette.getColor(n.getSubredditName());
            currentSelectedItem = n.getFullName();
            currentNode = baseNode;
            android.view.LayoutInflater inflater = ((android.app.Activity) (mContext)).getLayoutInflater();
            resetMenu(holder.menuArea, false);
            final android.view.View baseView = (me.ccrama.redditslide.SettingValues.rightHandedCommentMenu) ? inflater.inflate(me.ccrama.redditslide.R.layout.comment_menu_right_handed, holder.menuArea) : inflater.inflate(me.ccrama.redditslide.R.layout.comment_menu, holder.menuArea);
            if (!isReplying) {
                baseView.setVisibility(android.view.View.GONE);
                if (animate) {
                    expand(baseView);
                } else {
                    baseView.setVisibility(android.view.View.VISIBLE);
                    final int widthSpec = android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED);
                    final int heightSpec = android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED);
                    baseView.measure(widthSpec, heightSpec);
                    android.view.View l2 = (baseView.findViewById(me.ccrama.redditslide.R.id.replyArea) == null) ? baseView.findViewById(me.ccrama.redditslide.R.id.innerSend) : baseView.findViewById(me.ccrama.redditslide.R.id.replyArea);
                    final int widthSpec2 = android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED);
                    final int heightSpec2 = android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED);
                    l2.measure(widthSpec2, heightSpec2);
                    android.view.ViewGroup.LayoutParams layoutParams = baseView.getLayoutParams();
                    layoutParams.height = baseView.getMeasuredHeight() - l2.getMeasuredHeight();
                    baseView.setLayoutParams(layoutParams);
                }
            }
            android.support.v7.widget.RecyclerView.LayoutParams params = ((android.support.v7.widget.RecyclerView.LayoutParams) (holder.itemView.getLayoutParams()));
            params.setMargins(0, 0, 0, 0);
            holder.itemView.setLayoutParams(params);
            android.view.View reply = baseView.findViewById(me.ccrama.redditslide.R.id.reply);
            android.view.View send = baseView.findViewById(me.ccrama.redditslide.R.id.send);
            final android.view.View menu = baseView.findViewById(me.ccrama.redditslide.R.id.menu);
            final android.view.View replyArea = baseView.findViewById(me.ccrama.redditslide.R.id.replyArea);
            final android.view.View more = baseView.findViewById(me.ccrama.redditslide.R.id.more);
            final android.widget.ImageView upvote = baseView.findViewById(me.ccrama.redditslide.R.id.upvote);
            final android.widget.ImageView downvote = baseView.findViewById(me.ccrama.redditslide.R.id.downvote);
            android.view.View discard = baseView.findViewById(me.ccrama.redditslide.R.id.discard);
            final android.widget.EditText replyLine = baseView.findViewById(me.ccrama.redditslide.R.id.replyLine);
            final net.dean.jraw.models.Comment comment = baseNode.getComment();
            if (me.ccrama.redditslide.ActionStates.getVoteDirection(comment) == net.dean.jraw.models.VoteDirection.UPVOTE) {
                upvote.setColorFilter(holder.textColorUp, android.graphics.PorterDuff.Mode.MULTIPLY);
                upvote.setContentDescription(mContext.getResources().getString(me.ccrama.redditslide.R.string.btn_upvoted));
            } else if (me.ccrama.redditslide.ActionStates.getVoteDirection(comment) == net.dean.jraw.models.VoteDirection.DOWNVOTE) {
                downvote.setColorFilter(holder.textColorDown, android.graphics.PorterDuff.Mode.MULTIPLY);
                downvote.setContentDescription(mContext.getResources().getString(me.ccrama.redditslide.R.string.btn_downvoted));
            } else {
                downvote.clearColorFilter();
                downvote.setContentDescription(mContext.getResources().getString(me.ccrama.redditslide.R.string.btn_downvote));
                upvote.clearColorFilter();
                upvote.setContentDescription(mContext.getResources().getString(me.ccrama.redditslide.R.string.btn_upvote));
            }
            {
                final android.widget.ImageView mod = baseView.findViewById(me.ccrama.redditslide.R.id.mod);
                try {
                    if (me.ccrama.redditslide.UserSubscriptions.modOf.contains(submission.getSubredditName())) {
                        // todo
                        mod.setVisibility(android.view.View.GONE);
                    } else {
                        mod.setVisibility(android.view.View.GONE);
                    }
                } catch (java.lang.Exception e) {
                    android.util.Log.d(me.ccrama.redditslide.util.LogUtil.getTag(), "Error loading mod " + e.toString());
                }
            }
            if ((me.ccrama.redditslide.UserSubscriptions.modOf != null) && me.ccrama.redditslide.UserSubscriptions.modOf.contains(submission.getSubredditName().toLowerCase(java.util.Locale.ENGLISH))) {
                baseView.findViewById(me.ccrama.redditslide.R.id.mod).setVisibility(android.view.View.VISIBLE);
                final java.util.Map<java.lang.String, java.lang.Integer> reports = comment.getUserReports();
                final java.util.Map<java.lang.String, java.lang.String> reports2 = comment.getModeratorReports();
                if ((reports.size() + reports2.size()) > 0) {
                    ((android.widget.ImageView) (baseView.findViewById(me.ccrama.redditslide.R.id.mod))).setColorFilter(android.support.v4.content.ContextCompat.getColor(mContext, me.ccrama.redditslide.R.color.md_red_300), android.graphics.PorterDuff.Mode.SRC_ATOP);
                } else {
                    ((android.widget.ImageView) (baseView.findViewById(me.ccrama.redditslide.R.id.mod))).setColorFilter(android.graphics.Color.WHITE, android.graphics.PorterDuff.Mode.SRC_ATOP);
                }
                baseView.findViewById(me.ccrama.redditslide.R.id.mod).setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                    @java.lang.Override
                    public void onSingleClick(android.view.View v) {
                        me.ccrama.redditslide.Adapters.CommentAdapterHelper.showModBottomSheet(me.ccrama.redditslide.Adapters.CommentAdapter.this, mContext, baseNode, comment, holder, reports, reports2);
                    }
                });
            } else {
                baseView.findViewById(me.ccrama.redditslide.R.id.mod).setVisibility(android.view.View.GONE);
            }
            final android.widget.ImageView edit = baseView.findViewById(me.ccrama.redditslide.R.id.edit);
            if (((me.ccrama.redditslide.Authentication.name != null) && me.ccrama.redditslide.Authentication.name.toLowerCase(java.util.Locale.ENGLISH).equals(comment.getAuthor().toLowerCase(java.util.Locale.ENGLISH))) && me.ccrama.redditslide.Authentication.didOnline) {
                edit.setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                    @java.lang.Override
                    public void onSingleClick(android.view.View v) {
                        me.ccrama.redditslide.Adapters.CommentAdapterHelper.doCommentEdit(me.ccrama.redditslide.Adapters.CommentAdapter.this, mContext, fm, baseNode, baseNode.isTopLevel() ? submission.getSelftext() : baseNode.getParent().getComment().getBody(), holder);
                    }
                });
            } else {
                edit.setVisibility(android.view.View.GONE);
            }
            final android.widget.ImageView delete = baseView.findViewById(me.ccrama.redditslide.R.id.delete);
            if (((me.ccrama.redditslide.Authentication.name != null) && me.ccrama.redditslide.Authentication.name.toLowerCase(java.util.Locale.ENGLISH).equals(comment.getAuthor().toLowerCase(java.util.Locale.ENGLISH))) && me.ccrama.redditslide.Authentication.didOnline) {
                delete.setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                    @java.lang.Override
                    public void onSingleClick(android.view.View v) {
                        me.ccrama.redditslide.Adapters.CommentAdapterHelper.deleteComment(me.ccrama.redditslide.Adapters.CommentAdapter.this, mContext, baseNode, holder);
                    }
                });
            } else {
                delete.setVisibility(android.view.View.GONE);
            }
            if (((((me.ccrama.redditslide.Authentication.isLoggedIn && (!submission.isArchived())) && (!submission.isLocked())) && (!deleted.contains(n.getFullName()))) && (!comment.getAuthor().equals("[deleted]"))) && me.ccrama.redditslide.Authentication.didOnline) {
                if (isReplying) {
                    baseView.setVisibility(android.view.View.VISIBLE);
                    final int widthSpec = android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED);
                    final int heightSpec = android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED);
                    baseView.measure(widthSpec, heightSpec);
                    android.view.View l2 = (baseView.findViewById(me.ccrama.redditslide.R.id.replyArea) == null) ? baseView.findViewById(me.ccrama.redditslide.R.id.innerSend) : baseView.findViewById(me.ccrama.redditslide.R.id.replyArea);
                    final int widthSpec2 = android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED);
                    final int heightSpec2 = android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED);
                    l2.measure(widthSpec2, heightSpec2);
                    android.widget.RelativeLayout.LayoutParams params2 = ((android.widget.RelativeLayout.LayoutParams) (baseView.getLayoutParams()));
                    params2.height = android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT;
                    params2.addRule(android.widget.RelativeLayout.BELOW, me.ccrama.redditslide.R.id.commentOverflow);
                    baseView.setLayoutParams(params2);
                    replyArea.setVisibility(android.view.View.VISIBLE);
                    menu.setVisibility(android.view.View.GONE);
                    currentlyEditing = replyLine;
                    currentlyEditing.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
                        @java.lang.Override
                        public void onFocusChange(android.view.View v, boolean hasFocus) {
                            if (hasFocus) {
                                mPage.fastScroll.setVisibility(android.view.View.GONE);
                                if (mPage.fab != null) {
                                    mPage.fab.setVisibility(android.view.View.GONE);
                                }
                                mPage.overrideFab = true;
                            } else if (me.ccrama.redditslide.SettingValues.fastscroll) {
                                mPage.fastScroll.setVisibility(android.view.View.VISIBLE);
                                if (mPage.fab != null) {
                                    mPage.fab.setVisibility(android.view.View.VISIBLE);
                                }
                                mPage.overrideFab = false;
                            }
                        }
                    });
                    final android.widget.TextView profile = baseView.findViewById(me.ccrama.redditslide.R.id.profile);
                    changedProfile = me.ccrama.redditslide.Authentication.name;
                    profile.setText("/u/" + changedProfile);
                    profile.setOnClickListener(new android.view.View.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.view.View v) {
                            final java.util.HashMap<java.lang.String, java.lang.String> accounts = new java.util.HashMap<>();
                            for (java.lang.String s : me.ccrama.redditslide.Authentication.authentication.getStringSet("accounts", new java.util.HashSet<java.lang.String>())) {
                                if (s.contains(":")) {
                                    accounts.put(s.split(":")[0], s.split(":")[1]);
                                } else {
                                    accounts.put(s, "");
                                }
                            }
                            final java.util.ArrayList<java.lang.String> keys = new java.util.ArrayList<>(accounts.keySet());
                            final int i = keys.indexOf(changedProfile);
                            com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext);
                            builder.setTitle(me.ccrama.redditslide.R.string.sorting_choose);
                            builder.setSingleChoiceItems(keys.toArray(new java.lang.String[keys.size()]), i, new android.content.DialogInterface.OnClickListener() {
                                @java.lang.Override
                                public void onClick(android.content.DialogInterface dialog, int which) {
                                    changedProfile = keys.get(which);
                                    profile.setText("/u/" + changedProfile);
                                }
                            });
                            builder.alwaysCallSingleChoiceCallback();
                            builder.setNegativeButton(me.ccrama.redditslide.R.string.btn_cancel, null);
                            builder.show();
                        }
                    });
                    replyLine.requestFocus();
                    android.view.inputmethod.InputMethodManager imm = ((android.view.inputmethod.InputMethodManager) (mContext.getSystemService(android.content.Context.INPUT_METHOD_SERVICE)));
                    imm.toggleSoftInput(android.view.inputmethod.InputMethodManager.SHOW_FORCED, android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY);
                    currentlyEditingId = n.getFullName();
                    replyLine.setText(backedText);
                    replyLine.addTextChangedListener(new android.text.TextWatcher() {
                        @java.lang.Override
                        public void beforeTextChanged(java.lang.CharSequence s, int start, int count, int after) {
                        }

                        @java.lang.Override
                        public void onTextChanged(java.lang.CharSequence s, int start, int before, int count) {
                            backedText = s.toString();
                        }

                        @java.lang.Override
                        public void afterTextChanged(android.text.Editable s) {
                        }
                    });
                    editingPosition = holder.getAdapterPosition();
                }
                reply.setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                    @java.lang.Override
                    public void onSingleClick(android.view.View v) {
                        expandAndSetParams(baseView);
                        // If the base theme is Light or Sepia, tint the Editor actions to be white
                        if ((me.ccrama.redditslide.SettingValues.currentTheme == 1) || (me.ccrama.redditslide.SettingValues.currentTheme == 5)) {
                            ((android.widget.ImageView) (replyArea.findViewById(me.ccrama.redditslide.R.id.savedraft))).setColorFilter(android.graphics.Color.WHITE);
                            ((android.widget.ImageView) (replyArea.findViewById(me.ccrama.redditslide.R.id.draft))).setColorFilter(android.graphics.Color.WHITE);
                            ((android.widget.ImageView) (replyArea.findViewById(me.ccrama.redditslide.R.id.imagerep))).setColorFilter(android.graphics.Color.WHITE);
                            ((android.widget.ImageView) (replyArea.findViewById(me.ccrama.redditslide.R.id.link))).setColorFilter(android.graphics.Color.WHITE);
                            ((android.widget.ImageView) (replyArea.findViewById(me.ccrama.redditslide.R.id.bold))).setColorFilter(android.graphics.Color.WHITE);
                            ((android.widget.ImageView) (replyArea.findViewById(me.ccrama.redditslide.R.id.italics))).setColorFilter(android.graphics.Color.WHITE);
                            ((android.widget.ImageView) (replyArea.findViewById(me.ccrama.redditslide.R.id.bulletlist))).setColorFilter(android.graphics.Color.WHITE);
                            ((android.widget.ImageView) (replyArea.findViewById(me.ccrama.redditslide.R.id.numlist))).setColorFilter(android.graphics.Color.WHITE);
                            ((android.widget.ImageView) (replyArea.findViewById(me.ccrama.redditslide.R.id.draw))).setColorFilter(android.graphics.Color.WHITE);
                            ((android.widget.ImageView) (replyArea.findViewById(me.ccrama.redditslide.R.id.quote))).setColorFilter(android.graphics.Color.WHITE);
                            ((android.widget.ImageView) (replyArea.findViewById(me.ccrama.redditslide.R.id.size))).setColorFilter(android.graphics.Color.WHITE);
                            ((android.widget.ImageView) (replyArea.findViewById(me.ccrama.redditslide.R.id.strike))).setColorFilter(android.graphics.Color.WHITE);
                            ((android.widget.ImageView) (replyArea.findViewById(me.ccrama.redditslide.R.id.author))).setColorFilter(android.graphics.Color.WHITE);
                            ((android.widget.ImageView) (replyArea.findViewById(me.ccrama.redditslide.R.id.spoiler))).setColorFilter(android.graphics.Color.WHITE);
                            replyLine.getBackground().setColorFilter(android.graphics.Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
                        }
                        replyArea.setVisibility(android.view.View.VISIBLE);
                        menu.setVisibility(android.view.View.GONE);
                        currentlyEditing = replyLine;
                        me.ccrama.redditslide.Views.DoEditorActions.doActions(currentlyEditing, replyArea, fm, ((android.app.Activity) (mContext)), comment.getBody(), getParents(baseNode));
                        currentlyEditing.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
                            @java.lang.Override
                            public void onFocusChange(android.view.View v, boolean hasFocus) {
                                if (hasFocus) {
                                    mPage.fastScroll.setVisibility(android.view.View.GONE);
                                    if (mPage.fab != null)
                                        mPage.fab.setVisibility(android.view.View.GONE);

                                    mPage.overrideFab = true;
                                } else if (me.ccrama.redditslide.SettingValues.fastscroll) {
                                    mPage.fastScroll.setVisibility(android.view.View.VISIBLE);
                                    if (mPage.fab != null)
                                        mPage.fab.setVisibility(android.view.View.VISIBLE);

                                    mPage.overrideFab = false;
                                }
                            }
                        });
                        final android.widget.TextView profile = baseView.findViewById(me.ccrama.redditslide.R.id.profile);
                        changedProfile = me.ccrama.redditslide.Authentication.name;
                        profile.setText("/u/" + changedProfile);
                        profile.setOnClickListener(new android.view.View.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.view.View v) {
                                final java.util.HashMap<java.lang.String, java.lang.String> accounts = new java.util.HashMap<>();
                                for (java.lang.String s : me.ccrama.redditslide.Authentication.authentication.getStringSet("accounts", new java.util.HashSet<java.lang.String>())) {
                                    if (s.contains(":")) {
                                        accounts.put(s.split(":")[0], s.split(":")[1]);
                                    } else {
                                        accounts.put(s, "");
                                    }
                                }
                                final java.util.ArrayList<java.lang.String> keys = new java.util.ArrayList<>(accounts.keySet());
                                final int i = keys.indexOf(changedProfile);
                                com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext);
                                builder.setTitle(me.ccrama.redditslide.R.string.sorting_choose);
                                builder.setSingleChoiceItems(keys.toArray(new java.lang.String[keys.size()]), i, new android.content.DialogInterface.OnClickListener() {
                                    @java.lang.Override
                                    public void onClick(android.content.DialogInterface dialog, int which) {
                                        changedProfile = keys.get(which);
                                        profile.setText("/u/" + changedProfile);
                                    }
                                });
                                builder.alwaysCallSingleChoiceCallback();
                                builder.setNegativeButton(me.ccrama.redditslide.R.string.btn_cancel, null);
                                builder.show();
                            }
                        });
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                            replyLine.setOnFocusChangeListener((android.view.View view,boolean b) -> {
                                if (b) {
                                    view.postDelayed(() -> {
                                        if (!view.hasFocus())
                                            view.requestFocus();

                                    }, 100);
                                }
                            });
                        }
                        replyLine.requestFocus();// TODO: Not working when called a second time

                        android.view.inputmethod.InputMethodManager imm = ((android.view.inputmethod.InputMethodManager) (mContext.getSystemService(android.content.Context.INPUT_METHOD_SERVICE)));
                        imm.toggleSoftInput(android.view.inputmethod.InputMethodManager.SHOW_FORCED, android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY);
                        currentlyEditingId = n.getFullName();
                        replyLine.addTextChangedListener(new android.text.TextWatcher() {
                            @java.lang.Override
                            public void beforeTextChanged(java.lang.CharSequence s, int start, int count, int after) {
                            }

                            @java.lang.Override
                            public void onTextChanged(java.lang.CharSequence s, int start, int before, int count) {
                                backedText = s.toString();
                            }

                            @java.lang.Override
                            public void afterTextChanged(android.text.Editable s) {
                            }
                        });
                        editingPosition = holder.getAdapterPosition();
                    }
                });
                send.setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                    @java.lang.Override
                    public void onSingleClick(android.view.View v) {
                        currentlyEditingId = "";
                        backedText = "";
                        doShowMenu(baseView);
                        if (me.ccrama.redditslide.SettingValues.fastscroll) {
                            mPage.fastScroll.setVisibility(android.view.View.VISIBLE);
                            if (mPage.fab != null)
                                mPage.fab.setVisibility(android.view.View.VISIBLE);

                            mPage.overrideFab = false;
                        }
                        dataSet.refreshLayout.setRefreshing(true);
                        if (currentlyEditing != null) {
                            java.lang.String text = currentlyEditing.getText().toString();
                            new me.ccrama.redditslide.Adapters.CommentAdapter.ReplyTaskComment(n, baseNode, holder, changedProfile).execute(text);
                            currentlyEditing = null;
                            editingPosition = -1;
                        }
                        // Hide soft keyboard
                        android.view.View view = ((android.app.Activity) (mContext)).findViewById(android.R.id.content);
                        if (view != null) {
                            android.view.inputmethod.InputMethodManager imm = ((android.view.inputmethod.InputMethodManager) (mContext.getSystemService(android.content.Context.INPUT_METHOD_SERVICE)));
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                });
                discard.setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                    @java.lang.Override
                    public void onSingleClick(android.view.View v) {
                        currentlyEditing = null;
                        editingPosition = -1;
                        currentlyEditingId = "";
                        backedText = "";
                        mPage.overrideFab = false;
                        android.view.View view = ((android.app.Activity) (mContext)).findViewById(android.R.id.content);
                        if (view != null) {
                            android.view.inputmethod.InputMethodManager imm = ((android.view.inputmethod.InputMethodManager) (mContext.getSystemService(android.content.Context.INPUT_METHOD_SERVICE)));
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                        doShowMenu(baseView);
                    }
                });
            } else {
                if (reply.getVisibility() == android.view.View.VISIBLE) {
                    reply.setVisibility(android.view.View.GONE);
                }
                if (((((submission.isArchived() || deleted.contains(n.getFullName())) || comment.getAuthor().equals("[deleted]")) && me.ccrama.redditslide.Authentication.isLoggedIn) && me.ccrama.redditslide.Authentication.didOnline) && (upvote.getVisibility() == android.view.View.VISIBLE)) {
                    upvote.setVisibility(android.view.View.GONE);
                }
                if (((((submission.isArchived() || deleted.contains(n.getFullName())) || comment.getAuthor().equals("[deleted]")) && me.ccrama.redditslide.Authentication.isLoggedIn) && me.ccrama.redditslide.Authentication.didOnline) && (downvote.getVisibility() == android.view.View.VISIBLE)) {
                    downvote.setVisibility(android.view.View.GONE);
                }
            }
            more.setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View v) {
                    me.ccrama.redditslide.Adapters.CommentAdapterHelper.showOverflowBottomSheet(me.ccrama.redditslide.Adapters.CommentAdapter.this, mContext, holder, baseNode);
                }
            });
            upvote.setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View v) {
                    setCommentStateUnhighlighted(holder, comment, baseNode, true);
                    if (me.ccrama.redditslide.ActionStates.getVoteDirection(comment) == net.dean.jraw.models.VoteDirection.UPVOTE) {
                        new me.ccrama.redditslide.Vote(v, mContext).execute(n);
                        me.ccrama.redditslide.ActionStates.setVoteDirection(comment, net.dean.jraw.models.VoteDirection.NO_VOTE);
                        doScoreText(holder, n, me.ccrama.redditslide.Adapters.CommentAdapter.this);
                        upvote.clearColorFilter();
                    } else {
                        new me.ccrama.redditslide.Vote(true, v, mContext).execute(n);
                        me.ccrama.redditslide.ActionStates.setVoteDirection(comment, net.dean.jraw.models.VoteDirection.UPVOTE);
                        downvote.clearColorFilter();// reset colour

                        doScoreText(holder, n, me.ccrama.redditslide.Adapters.CommentAdapter.this);
                        upvote.setColorFilter(holder.textColorUp, android.graphics.PorterDuff.Mode.MULTIPLY);
                    }
                }
            });
            downvote.setOnClickListener(new me.ccrama.redditslide.util.OnSingleClickListener() {
                @java.lang.Override
                public void onSingleClick(android.view.View v) {
                    setCommentStateUnhighlighted(holder, comment, baseNode, true);
                    if (me.ccrama.redditslide.ActionStates.getVoteDirection(comment) == net.dean.jraw.models.VoteDirection.DOWNVOTE) {
                        new me.ccrama.redditslide.Vote(v, mContext).execute(n);
                        me.ccrama.redditslide.ActionStates.setVoteDirection(comment, net.dean.jraw.models.VoteDirection.NO_VOTE);
                        doScoreText(holder, n, me.ccrama.redditslide.Adapters.CommentAdapter.this);
                        downvote.clearColorFilter();
                    } else {
                        new me.ccrama.redditslide.Vote(false, v, mContext).execute(n);
                        me.ccrama.redditslide.ActionStates.setVoteDirection(comment, net.dean.jraw.models.VoteDirection.DOWNVOTE);
                        upvote.clearColorFilter();// reset colour

                        doScoreText(holder, n, me.ccrama.redditslide.Adapters.CommentAdapter.this);
                        downvote.setColorFilter(holder.textColorDown, android.graphics.PorterDuff.Mode.MULTIPLY);
                    }
                }
            });
            menu.setBackgroundColor(color);
            replyArea.setBackgroundColor(color);
            if (!isReplying) {
                menu.setVisibility(android.view.View.VISIBLE);
                replyArea.setVisibility(android.view.View.GONE);
            }
            holder.itemView.findViewById(me.ccrama.redditslide.R.id.background).setBackgroundColor(android.graphics.Color.argb(50, android.graphics.Color.red(color), android.graphics.Color.green(color), android.graphics.Color.blue(color)));
        }
    }

    public void doHighlighted(final me.ccrama.redditslide.Adapters.CommentViewHolder holder, final net.dean.jraw.models.Comment n, final net.dean.jraw.models.CommentNode baseNode, boolean animate) {
        if ((mAnimator != null) && mAnimator.isRunning()) {
            holder.itemView.postDelayed(new java.lang.Runnable() {
                @java.lang.Override
                public void run() {
                    setCommentStateHighlighted(holder, n, baseNode, false, true);
                }
            }, mAnimator.getDuration());
        } else {
            setCommentStateHighlighted(holder, n, baseNode, false, animate);
        }
    }

    public android.widget.EditText currentlyEditing;

    public void resetMenu(android.widget.LinearLayout v, boolean collapsed) {
        v.removeAllViews();
        if (collapsed) {
            android.widget.RelativeLayout.LayoutParams params = ((android.widget.RelativeLayout.LayoutParams) (v.getLayoutParams()));
            params.height = 0;
            v.setLayoutParams(params);
        } else {
            android.widget.RelativeLayout.LayoutParams params = ((android.widget.RelativeLayout.LayoutParams) (v.getLayoutParams()));
            params.height = android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT;
            v.setLayoutParams(params);
        }
    }

    public void setCommentStateUnhighlighted(final me.ccrama.redditslide.Adapters.CommentViewHolder holder, final net.dean.jraw.models.CommentNode baseNode, boolean animate) {
        if (animate) {
            collapseAndRemove(holder.menuArea);
        } else {
            resetMenu(holder.menuArea, true);
        }
        int color;
        net.dean.jraw.models.Comment c = baseNode.getComment();
        if (((((lastSeen != 0) && (lastSeen < c.getCreated().getTime())) && (!dataSet.single)) && me.ccrama.redditslide.SettingValues.commentLastVisit) && (!me.ccrama.redditslide.Authentication.name.equals(c.getAuthor()))) {
            color = me.ccrama.redditslide.Visuals.Palette.getColor(baseNode.getComment().getSubredditName());
            color = android.graphics.Color.argb(20, android.graphics.Color.red(color), android.graphics.Color.green(color), android.graphics.Color.blue(color));
        } else {
            android.util.TypedValue typedValue = new android.util.TypedValue();
            android.content.res.Resources.Theme theme = mContext.getTheme();
            theme.resolveAttribute(me.ccrama.redditslide.R.attr.card_background, typedValue, true);
            color = typedValue.data;
        }
        int dwidth = ((int) (3 * android.content.res.Resources.getSystem().getDisplayMetrics().density));
        int width = 0;
        // Padding on the left, starting with the third comment
        for (int i = 2; i < baseNode.getDepth(); i++) {
            width += dwidth;
        }
        android.support.v7.widget.RecyclerView.LayoutParams params = ((android.support.v7.widget.RecyclerView.LayoutParams) (holder.itemView.getLayoutParams()));
        params.setMargins(width, 0, 0, 0);
        holder.itemView.setLayoutParams(params);
        holder.itemView.findViewById(me.ccrama.redditslide.R.id.background).setBackgroundColor(color);
    }

    public void setCommentStateUnhighlighted(final me.ccrama.redditslide.Adapters.CommentViewHolder holder, final net.dean.jraw.models.Comment comment, final net.dean.jraw.models.CommentNode baseNode, boolean animate) {
        if (((currentlyEditing != null) && (!currentlyEditing.getText().toString().isEmpty())) && (holder.getAdapterPosition() <= editingPosition)) {
            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.discard_comment_title).setMessage(me.ccrama.redditslide.R.string.comment_discard_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                @java.lang.Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    currentlyEditing = null;
                    editingPosition = -1;
                    if (me.ccrama.redditslide.SettingValues.fastscroll) {
                        mPage.fastScroll.setVisibility(android.view.View.VISIBLE);
                    }
                    if (mPage.fab != null)
                        mPage.fab.setVisibility(android.view.View.VISIBLE);

                    mPage.overrideFab = false;
                    currentlyEditingId = "";
                    backedText = "";
                    android.view.View view = ((android.app.Activity) (mContext)).findViewById(android.R.id.content);
                    if (view != null) {
                        android.view.inputmethod.InputMethodManager imm = ((android.view.inputmethod.InputMethodManager) (mContext.getSystemService(android.content.Context.INPUT_METHOD_SERVICE)));
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    if (mContext instanceof me.ccrama.redditslide.Activities.BaseActivity) {
                        ((me.ccrama.redditslide.Activities.BaseActivity) (mContext)).setShareUrl("https://reddit.com" + submission.getPermalink());
                    }
                    setCommentStateUnhighlighted(holder, comment, baseNode, true);
                }
            }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, null).show();
        } else {
            if (mContext instanceof me.ccrama.redditslide.Activities.BaseActivity) {
                ((me.ccrama.redditslide.Activities.BaseActivity) (mContext)).setShareUrl("https://freddit.com" + submission.getPermalink());
            }
            currentlySelected = null;
            currentSelectedItem = "";
            if (animate) {
                collapseAndRemove(holder.menuArea);
            } else {
                resetMenu(holder.menuArea, true);
            }
            int dwidth = ((int) (3 * android.content.res.Resources.getSystem().getDisplayMetrics().density));
            int width = 0;
            // Padding on the left, starting with the third comment
            for (int i = 2; i < baseNode.getDepth(); i++) {
                width += dwidth;
            }
            android.support.v7.widget.RecyclerView.LayoutParams params = ((android.support.v7.widget.RecyclerView.LayoutParams) (holder.itemView.getLayoutParams()));
            params.setMargins(width, 0, 0, 0);
            holder.itemView.setLayoutParams(params);
            android.util.TypedValue typedValue = new android.util.TypedValue();
            android.content.res.Resources.Theme theme = mContext.getTheme();
            theme.resolveAttribute(me.ccrama.redditslide.R.attr.card_background, typedValue, true);
            int color = typedValue.data;
            holder.itemView.findViewById(me.ccrama.redditslide.R.id.background).setBackgroundColor(color);
        }
    }

    public void doLongClick(final me.ccrama.redditslide.Adapters.CommentViewHolder holder, final net.dean.jraw.models.Comment comment, final net.dean.jraw.models.CommentNode baseNode) {
        if ((currentlyEditing != null) && (!currentlyEditing.getText().toString().isEmpty())) {
            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.discard_comment_title).setMessage(me.ccrama.redditslide.R.string.comment_discard_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                @java.lang.Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    currentlyEditing = null;
                    editingPosition = -1;
                    if (me.ccrama.redditslide.SettingValues.fastscroll) {
                        mPage.fastScroll.setVisibility(android.view.View.VISIBLE);
                    }
                    if (mPage.fab != null)
                        mPage.fab.setVisibility(android.view.View.VISIBLE);

                    mPage.overrideFab = false;
                    currentlyEditingId = "";
                    backedText = "";
                    android.view.View view = ((android.app.Activity) (mContext)).findViewById(android.R.id.content);
                    if (view != null) {
                        android.view.inputmethod.InputMethodManager imm = ((android.view.inputmethod.InputMethodManager) (mContext.getSystemService(android.content.Context.INPUT_METHOD_SERVICE)));
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    doLongClick(holder, comment, baseNode);
                }
            }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, null).show();
        } else if ((currentSelectedItem != null) && currentSelectedItem.contains(comment.getFullName())) {
            setCommentStateUnhighlighted(holder, comment, baseNode, true);
        } else {
            doHighlighted(holder, comment, baseNode, true);
        }
    }

    public void doOnClick(me.ccrama.redditslide.Adapters.CommentViewHolder holder, net.dean.jraw.models.Comment comment, net.dean.jraw.models.CommentNode baseNode) {
        if ((currentSelectedItem != null) && currentSelectedItem.contains(comment.getFullName())) {
            if (me.ccrama.redditslide.SettingValues.swap) {
                // If the comment is highlighted and the user is long pressing the comment,
                // hide the comment.
                doOnClick(holder, baseNode, comment);
            }
            setCommentStateUnhighlighted(holder, comment, baseNode, true);
        } else {
            doOnClick(holder, baseNode, comment);
        }
    }

    public void doOnClick(final me.ccrama.redditslide.Adapters.CommentViewHolder holder, final net.dean.jraw.models.CommentNode baseNode, final net.dean.jraw.models.Comment comment) {
        if (((currentlyEditing != null) && (!currentlyEditing.getText().toString().isEmpty())) && (holder.getAdapterPosition() <= editingPosition)) {
            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.discard_comment_title).setMessage(me.ccrama.redditslide.R.string.comment_discard_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                @java.lang.Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    currentlyEditing = null;
                    editingPosition = -1;
                    if (me.ccrama.redditslide.SettingValues.fastscroll) {
                        mPage.fastScroll.setVisibility(android.view.View.VISIBLE);
                    }
                    if (mPage.fab != null)
                        mPage.fab.setVisibility(android.view.View.VISIBLE);

                    mPage.overrideFab = false;
                    currentlyEditingId = "";
                    backedText = "";
                    android.view.View view = ((android.app.Activity) (mContext)).findViewById(android.R.id.content);
                    if (view != null) {
                        android.view.inputmethod.InputMethodManager imm = ((android.view.inputmethod.InputMethodManager) (mContext.getSystemService(android.content.Context.INPUT_METHOD_SERVICE)));
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    doOnClick(holder, baseNode, comment);
                }
            }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, null).show();
        } else if (isClicking) {
            isClicking = false;
            resetMenu(holder.menuArea, true);
            isHolder.itemView.findViewById(me.ccrama.redditslide.R.id.menu).setVisibility(android.view.View.GONE);
        } else {
            if (hiddenPersons.contains(comment.getFullName())) {
                hiddenPersons.remove(comment.getFullName());
                unhideAll(baseNode, holder.getAdapterPosition() + 1);
                if (toCollapse.contains(comment.getFullName()) && me.ccrama.redditslide.SettingValues.collapseComments) {
                    setViews(comment.getDataNode().get("body_html").asText(), submission.getSubredditName(), holder);
                }
                me.ccrama.redditslide.Adapters.CommentAdapterHelper.hideChildrenObject(holder.childrenNumber);
                if (!holder.firstTextView.getText().toString().isEmpty()) {
                    holder.firstTextView.setVisibility(android.view.View.VISIBLE);
                } else {
                    holder.firstTextView.setVisibility(android.view.View.GONE);
                }
                holder.commentOverflow.setVisibility(android.view.View.VISIBLE);
                toCollapse.remove(comment.getFullName());
            } else {
                int childNumber = getChildNumber(baseNode);
                if (childNumber > 0) {
                    hideAll(baseNode, holder.getAdapterPosition() + 1);
                    if (!hiddenPersons.contains(comment.getFullName())) {
                        hiddenPersons.add(comment.getFullName());
                    }
                    if (childNumber > 0) {
                        me.ccrama.redditslide.Adapters.CommentAdapterHelper.showChildrenObject(holder.childrenNumber);
                        holder.childrenNumber.setText("+" + childNumber);
                    }
                } else if (!me.ccrama.redditslide.SettingValues.collapseComments) {
                    doLongClick(holder, comment, baseNode);
                }
                toCollapse.add(comment.getFullName());
                if (((holder.firstTextView.getVisibility() == android.view.View.VISIBLE) || (holder.commentOverflow.getVisibility() == android.view.View.VISIBLE)) && me.ccrama.redditslide.SettingValues.collapseComments) {
                    holder.firstTextView.setVisibility(android.view.View.GONE);
                    holder.commentOverflow.setVisibility(android.view.View.GONE);
                } else if (me.ccrama.redditslide.SettingValues.collapseComments) {
                    if (!holder.firstTextView.getText().toString().isEmpty()) {
                        holder.firstTextView.setVisibility(android.view.View.VISIBLE);
                    } else {
                        holder.firstTextView.setVisibility(android.view.View.GONE);
                    }
                    holder.commentOverflow.setVisibility(android.view.View.VISIBLE);
                }
            }
            clickpos = holder.getAdapterPosition() + 1;
        }
    }

    private int getChildNumber(net.dean.jraw.models.CommentNode user) {
        int i = 0;
        for (net.dean.jraw.models.CommentNode ignored : user.walkTree()) {
            i++;
            if (ignored.hasMoreComments() && dataSet.online) {
                i++;
            }
        }
        return i - 1;
    }

    @java.lang.Override
    public int getItemViewType(int position) {
        if (((position == 0) || (((currentComments != null) && (!currentComments.isEmpty())) && (position == ((currentComments.size() - hidden.size()) + 2)))) || (((currentComments != null) && currentComments.isEmpty()) && (position == 2))) {
            return SPACER;
        } else {
            position -= 1;
        }
        if (position == 0) {
            return me.ccrama.redditslide.Adapters.CommentAdapter.HEADER;
        }
        return currentComments.get(getRealPosition(position - 1)) instanceof me.ccrama.redditslide.Adapters.CommentItem ? 2 : 3;
    }

    @java.lang.Override
    public int getItemCount() {
        if (currentComments == null) {
            return 2;
        } else {
            return 3 + (currentComments.size() - hidden.size());
        }
    }

    public void unhideAll(net.dean.jraw.models.CommentNode n, int i) {
        try {
            int counter = unhideNumber(n, 0);
            if (me.ccrama.redditslide.SettingValues.collapseComments) {
                listView.setItemAnimator(null);
                notifyItemRangeInserted(i, counter);
            } else {
                try {
                    listView.setItemAnimator(new com.mikepenz.itemanimators.AlphaInAnimator());
                } catch (java.lang.Exception ignored) {
                }
                notifyItemRangeInserted(i, counter);
            }
        } catch (java.lang.Exception ignored) {
        }
    }

    public void unhideAll(net.dean.jraw.models.CommentNode n) {
        unhideNumber(n, 0);
        if (me.ccrama.redditslide.SettingValues.collapseComments) {
            listView.setItemAnimator(null);
            notifyDataSetChanged();
        } else {
            listView.setItemAnimator(new com.mikepenz.itemanimators.AlphaInAnimator());
            notifyDataSetChanged();
        }
    }

    public void hideAll(net.dean.jraw.models.CommentNode n) {
        hideNumber(n, 0);
        if (me.ccrama.redditslide.SettingValues.collapseComments) {
            listView.setItemAnimator(null);
            notifyDataSetChanged();
        } else {
            listView.setItemAnimator(new com.mikepenz.itemanimators.AlphaInAnimator());
            notifyDataSetChanged();
        }
    }

    public void hideAll(net.dean.jraw.models.CommentNode n, int i) {
        int counter = hideNumber(n, 0);
        if (me.ccrama.redditslide.SettingValues.collapseComments) {
            listView.setItemAnimator(null);
            notifyItemRangeRemoved(i, counter);
        } else {
            listView.setItemAnimator(new com.mikepenz.itemanimators.AlphaInAnimator());
            notifyItemRangeRemoved(i, counter);
        }
    }

    public boolean parentHidden(net.dean.jraw.models.CommentNode n) {
        n = n.getParent();
        while ((n != null) && (n.getDepth() > 0)) {
            java.lang.String name = n.getComment().getFullName();
            if (hiddenPersons.contains(name) || hidden.contains(name)) {
                return true;
            }
            n = n.getParent();
        } 
        return false;
    }

    public int unhideNumber(net.dean.jraw.models.CommentNode n, int i) {
        for (net.dean.jraw.models.CommentNode ignored : n.getChildren()) {
            if (!ignored.getComment().getFullName().equals(n.getComment().getFullName())) {
                boolean parentHidden = parentHidden(ignored);
                if (parentHidden) {
                    continue;
                }
                java.lang.String name = ignored.getComment().getFullName();
                if (hidden.contains(name) || hiddenPersons.contains(name)) {
                    hidden.remove(name);
                    i++;
                    if ((ignored.hasMoreComments() && (!hiddenPersons.contains(name))) && dataSet.online) {
                        name = name + "more";
                        if (hidden.contains(name)) {
                            hidden.remove(name);
                            toCollapse.remove(name);
                            i++;
                        }
                    }
                }
                i += unhideNumber(ignored, 0);
            }
        }
        if (((n.hasMoreComments() && (!parentHidden(n))) && (!hiddenPersons.contains(n.getComment().getFullName()))) && dataSet.online) {
            java.lang.String fullname = n.getComment().getFullName() + "more";
            if (hidden.contains(fullname)) {
                i++;
                hidden.remove(fullname);
            }
        }
        return i;
    }

    public int hideNumber(net.dean.jraw.models.CommentNode n, int i) {
        for (net.dean.jraw.models.CommentNode ignored : n.getChildren()) {
            if (!ignored.getComment().getFullName().equals(n.getComment().getFullName())) {
                java.lang.String fullname = ignored.getComment().getFullName();
                if (!hidden.contains(fullname)) {
                    i++;
                    hidden.add(fullname);
                }
                if (ignored.hasMoreComments() && dataSet.online) {
                    if ((currentLoading != null) && currentLoading.fullname.equals(fullname)) {
                        currentLoading.cancel(true);
                    }
                    fullname = fullname + "more";
                    if (!hidden.contains(fullname)) {
                        i++;
                        hidden.add(fullname);
                    }
                }
                i += hideNumber(ignored, 0);
            }
        }
        if (n.hasMoreComments() && dataSet.online) {
            java.lang.String fullname = n.getComment().getFullName() + "more";
            if (!hidden.contains(fullname)) {
                i++;
                hidden.add(fullname);
            }
        }
        return i;
    }

    public java.lang.String[] getParents(net.dean.jraw.models.CommentNode comment) {
        java.lang.String[] bodies = new java.lang.String[comment.getDepth() + 1];
        bodies[0] = comment.getComment().getAuthor();
        net.dean.jraw.models.CommentNode parent = comment.getParent();
        int index = 1;
        while (parent != null) {
            bodies[index] = parent.getComment().getAuthor();
            index++;
            parent = parent.getParent();
        } 
        bodies[index - 1] = submission.getAuthor();
        // Reverse the array so Submission > Author > ... > Current OP
        for (int i = 0; i < (bodies.length / 2); i++) {
            java.lang.String temp = bodies[i];
            bodies[i] = bodies[(bodies.length - i) - 1];
            bodies[(bodies.length - i) - 1] = temp;
        }
        return bodies;
    }

    public int getRealPosition(int position) {
        int hElements = getHiddenCountUpTo(position);
        int diff = 0;
        for (int i = 0; i < hElements; i++) {
            diff++;
            if ((currentComments.size() > (position + diff)) && hidden.contains(currentComments.get(position + diff).getName())) {
                i--;
            }
        }
        return position + diff;
    }

    private int getHiddenCountUpTo(int location) {
        int count = 0;
        for (int i = 0; (i <= location) && (i < currentComments.size()); i++) {
            if ((currentComments.size() > i) && hidden.contains(currentComments.get(i).getName())) {
                count++;
            }
        }
        return count;
    }

    public class AsyncLoadMore extends android.os.AsyncTask<me.ccrama.redditslide.Adapters.MoreChildItem, java.lang.Void, java.lang.Integer> {
        public me.ccrama.redditslide.Adapters.MoreCommentViewHolder holder;

        public int holderPos;

        public int position;

        public int dataPos;

        public java.lang.String fullname;

        public AsyncLoadMore(int position, int holderPos, me.ccrama.redditslide.Adapters.MoreCommentViewHolder holder, int dataPos, java.lang.String fullname) {
            this.holderPos = holderPos;
            this.holder = holder;
            this.position = position;
            this.dataPos = dataPos;
            this.fullname = fullname;
        }

        @java.lang.Override
        public void onPostExecute(java.lang.Integer data) {
            currentLoading = null;
            if ((!isCancelled()) && (data != null)) {
                shifted += data;
                ((android.app.Activity) (mContext)).runOnUiThread(new java.lang.Runnable() {
                    @java.lang.Override
                    public void run() {
                        currentComments.remove(position);
                        notifyItemRemoved(holderPos);
                    }
                });
                int oldSize = currentComments.size();
                currentComments.addAll(position, finalData);
                int newSize = currentComments.size();
                for (int i2 = 0; i2 < currentComments.size(); i2++) {
                    keys.put(currentComments.get(i2).getName(), i2);
                }
                data = newSize - oldSize;
                listView.setItemAnimator(new com.mikepenz.itemanimators.SlideRightAlphaAnimator());
                notifyItemRangeInserted(holderPos, data);
                currentPos = holderPos;
                toShiftTo = ((android.support.v7.widget.LinearLayoutManager) (listView.getLayoutManager())).findLastVisibleItemPosition();
                shiftFrom = ((android.support.v7.widget.LinearLayoutManager) (listView.getLayoutManager())).findFirstVisibleItemPosition();
            } else if ((data == null) && (currentComments.get(dataPos) instanceof me.ccrama.redditslide.Adapters.MoreChildItem)) {
                final me.ccrama.redditslide.Adapters.MoreChildItem baseNode = ((me.ccrama.redditslide.Adapters.MoreChildItem) (currentComments.get(dataPos)));
                if (baseNode.children.getCount() > 0) {
                    holder.content.setText(mContext.getString(me.ccrama.redditslide.R.string.comment_load_more, baseNode.children.getCount()));
                } else if (!baseNode.children.getChildrenIds().isEmpty()) {
                    holder.content.setText(me.ccrama.redditslide.R.string.comment_load_more_number_unknown);
                } else {
                    holder.content.setText(me.ccrama.redditslide.R.string.thread_continue);
                }
                holder.loading.setVisibility(android.view.View.GONE);
            }
        }

        java.util.ArrayList<me.ccrama.redditslide.Adapters.CommentObject> finalData;

        @java.lang.Override
        protected java.lang.Integer doInBackground(me.ccrama.redditslide.Adapters.MoreChildItem... params) {
            finalData = new java.util.ArrayList<>();
            int i = 0;
            if (params.length > 0) {
                try {
                    net.dean.jraw.models.CommentNode node = params[0].comment;
                    node.loadMoreComments(me.ccrama.redditslide.Authentication.reddit);
                    java.util.HashMap<java.lang.Integer, me.ccrama.redditslide.Adapters.MoreChildItem> waiting = new java.util.HashMap<>();
                    for (net.dean.jraw.models.CommentNode n : node.walkTree()) {
                        if (!keys.containsKey(n.getComment().getFullName())) {
                            me.ccrama.redditslide.Adapters.CommentObject obj = new me.ccrama.redditslide.Adapters.CommentItem(n);
                            java.util.ArrayList<java.lang.Integer> removed = new java.util.ArrayList<>();
                            java.util.Map<java.lang.Integer, me.ccrama.redditslide.Adapters.MoreChildItem> map = new java.util.TreeMap<>(java.util.Collections.reverseOrder());
                            map.putAll(waiting);
                            for (java.lang.Integer i2 : map.keySet()) {
                                if (i2 >= n.getDepth()) {
                                    finalData.add(waiting.get(i2));
                                    removed.add(i2);
                                    waiting.remove(i2);
                                    i++;
                                }
                            }
                            finalData.add(obj);
                            i++;
                            if (n.hasMoreComments()) {
                                waiting.put(n.getDepth(), new me.ccrama.redditslide.Adapters.MoreChildItem(n, n.getMoreChildren()));
                            }
                        }
                    }
                    if (node.hasMoreComments()) {
                        finalData.add(new me.ccrama.redditslide.Adapters.MoreChildItem(node, node.getMoreChildren()));
                        i++;
                    }
                } catch (java.lang.Exception e) {
                    android.util.Log.w(me.ccrama.redditslide.util.LogUtil.getTag(), "Cannot load more comments " + e);
                    java.io.Writer writer = new java.io.StringWriter();
                    java.io.PrintWriter printWriter = new java.io.PrintWriter(writer);
                    e.printStackTrace(printWriter);
                    java.lang.String stacktrace = writer.toString().replace(";", ",");
                    if ((stacktrace.contains("UnknownHostException") || stacktrace.contains("SocketTimeoutException")) || stacktrace.contains("ConnectException")) {
                        // is offline
                        final android.os.Handler mHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                        mHandler.post(new java.lang.Runnable() {
                            @java.lang.Override
                            public void run() {
                                try {
                                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.err_title).setMessage(me.ccrama.redditslide.R.string.err_connection_failed_msg).setNegativeButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                                        @java.lang.Override
                                        public void onClick(android.content.DialogInterface dialog, int which) {
                                        }
                                    }).show();
                                } catch (java.lang.Exception ignored) {
                                }
                            }
                        });
                    } else if (stacktrace.contains("403 Forbidden") || stacktrace.contains("401 Unauthorized")) {
                        // Un-authenticated
                        final android.os.Handler mHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                        mHandler.post(new java.lang.Runnable() {
                            @java.lang.Override
                            public void run() {
                                try {
                                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.err_title).setMessage(me.ccrama.redditslide.R.string.err_refused_request_msg).setNegativeButton("No", new android.content.DialogInterface.OnClickListener() {
                                        @java.lang.Override
                                        public void onClick(android.content.DialogInterface dialog, int which) {
                                        }
                                    }).setPositiveButton("Yes", new android.content.DialogInterface.OnClickListener() {
                                        @java.lang.Override
                                        public void onClick(android.content.DialogInterface dialog, int which) {
                                            me.ccrama.redditslide.Reddit.authentication.updateToken(mContext);
                                        }
                                    }).show();
                                } catch (java.lang.Exception ignored) {
                                }
                            }
                        });
                    } else if (stacktrace.contains("404 Not Found") || stacktrace.contains("400 Bad Request")) {
                        final android.os.Handler mHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                        mHandler.post(new java.lang.Runnable() {
                            @java.lang.Override
                            public void run() {
                                try {
                                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.err_title).setMessage(me.ccrama.redditslide.R.string.err_could_not_find_content_msg).setNegativeButton("Close", new android.content.DialogInterface.OnClickListener() {
                                        @java.lang.Override
                                        public void onClick(android.content.DialogInterface dialog, int which) {
                                        }
                                    }).show();
                                } catch (java.lang.Exception ignored) {
                                }
                            }
                        });
                    }
                    return null;
                }
            }
            return i;
        }
    }

    public class AsyncForceLoadChild extends android.os.AsyncTask<java.lang.String, java.lang.Void, java.lang.Integer> {
        net.dean.jraw.models.CommentNode node;

        public int holderPos;

        public int position;

        public AsyncForceLoadChild(int position, int holderPos, net.dean.jraw.models.CommentNode baseNode) {
            this.holderPos = holderPos;
            this.node = baseNode;
            this.position = position;
        }

        @java.lang.Override
        public void onPostExecute(java.lang.Integer data) {
            if (data != (-1)) {
                listView.setItemAnimator(new com.mikepenz.itemanimators.SlideRightAlphaAnimator());
                notifyItemInserted(holderPos + 1);
                currentPos = holderPos + 1;
                toShiftTo = ((android.support.v7.widget.LinearLayoutManager) (listView.getLayoutManager())).findLastVisibleItemPosition();
                shiftFrom = ((android.support.v7.widget.LinearLayoutManager) (listView.getLayoutManager())).findFirstVisibleItemPosition();
                dataSet.refreshLayout.setRefreshing(false);
            } else {
                // Comment could not be found, force a reload
                android.os.Handler handler2 = new android.os.Handler();
                handler2.postDelayed(new java.lang.Runnable() {
                    public void run() {
                        ((android.app.Activity) (mContext)).runOnUiThread(new java.lang.Runnable() {
                            @java.lang.Override
                            public void run() {
                                dataSet.refreshLayout.setRefreshing(false);
                                dataSet.loadMoreReply(me.ccrama.redditslide.Adapters.CommentAdapter.this);
                            }
                        });
                    }
                }, 2000);
            }
        }

        @java.lang.Override
        protected java.lang.Integer doInBackground(java.lang.String... params) {
            int i = 0;
            if (params.length > 0) {
                try {
                    node.insertComment(me.ccrama.redditslide.Authentication.reddit, "t1_" + params[0]);
                    for (net.dean.jraw.models.CommentNode n : node.walkTree()) {
                        if (n.getComment().getFullName().contains(params[0])) {
                            currentComments.add(position, new me.ccrama.redditslide.Adapters.CommentItem(n));
                            i++;
                        }
                    }
                } catch (java.lang.Exception e) {
                    android.util.Log.w(me.ccrama.redditslide.util.LogUtil.getTag(), "Cannot load more comments " + e);
                    i = -1;
                }
                shifted += i;
                if (currentComments != null) {
                    for (int i2 = 0; i2 < currentComments.size(); i2++) {
                        keys.put(currentComments.get(i2).getName(), i2);
                    }
                } else {
                    i = -1;
                }
            }
            return i;
        }
    }

    public void editComment(net.dean.jraw.models.CommentNode n, me.ccrama.redditslide.Adapters.CommentViewHolder holder) {
        if (n == null) {
            dataSet.loadMoreReply(this);
        } else {
            int position = getRealPosition(holder.getAdapterPosition() - 1);
            final int holderpos = holder.getAdapterPosition();
            currentComments.remove(position - 1);
            currentComments.add(position - 1, new me.ccrama.redditslide.Adapters.CommentItem(n));
            listView.setItemAnimator(new com.mikepenz.itemanimators.SlideRightAlphaAnimator());
            ((android.app.Activity) (mContext)).runOnUiThread(new java.lang.Runnable() {
                @java.lang.Override
                public void run() {
                    notifyItemChanged(holderpos);
                }
            });
        }
    }

    public class ReplyTaskComment extends android.os.AsyncTask<java.lang.String, java.lang.Void, java.lang.String> {
        public net.dean.jraw.models.Contribution sub;

        net.dean.jraw.models.CommentNode node;

        me.ccrama.redditslide.Adapters.CommentViewHolder holder;

        boolean isSubmission;

        java.lang.String profileName;

        public ReplyTaskComment(net.dean.jraw.models.Contribution n, net.dean.jraw.models.CommentNode node, me.ccrama.redditslide.Adapters.CommentViewHolder holder, java.lang.String profileName) {
            sub = n;
            this.holder = holder;
            this.node = node;
            this.profileName = profileName;
        }

        public ReplyTaskComment(net.dean.jraw.models.Contribution n, java.lang.String profileName) {
            sub = n;
            isSubmission = true;
            this.profileName = profileName;
        }

        @java.lang.Override
        public void onPostExecute(final java.lang.String s) {
            if ((s == null) || s.isEmpty()) {
                if ((commentBack != null) && (!commentBack.isEmpty())) {
                    me.ccrama.redditslide.Drafts.addDraft(commentBack);
                    try {
                        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.err_comment_post).setMessage((why == null ? "" : mContext.getString(me.ccrama.redditslide.R.string.err_comment_post_reason, why)) + mContext.getString(me.ccrama.redditslide.R.string.err_comment_post_message)).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, null).show();
                    } catch (java.lang.Exception ignored) {
                    }
                } else {
                    try {
                        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.err_comment_post).setMessage((why == null ? "" : mContext.getString(me.ccrama.redditslide.R.string.err_comment_post_reason, why)) + mContext.getString(me.ccrama.redditslide.R.string.err_comment_post_nosave_message)).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, null).show();
                    } catch (java.lang.Exception ignored) {
                    }
                }
            } else if (isSubmission) {
                new me.ccrama.redditslide.Adapters.CommentAdapter.AsyncForceLoadChild(0, 0, submission.getComments()).execute(s);
            } else {
                new me.ccrama.redditslide.Adapters.CommentAdapter.AsyncForceLoadChild(getRealPosition(holder.getAdapterPosition() - 1), holder.getAdapterPosition(), node).execute(s);
            }
        }

        java.lang.String why;

        java.lang.String commentBack;

        @java.lang.Override
        protected java.lang.String doInBackground(java.lang.String... comment) {
            if (me.ccrama.redditslide.Authentication.me != null) {
                try {
                    commentBack = comment[0];
                    if (profileName.equals(me.ccrama.redditslide.Authentication.name)) {
                        return new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).reply(sub, comment[0]);
                    } else {
                        me.ccrama.redditslide.util.LogUtil.v("Switching to " + profileName);
                        return new net.dean.jraw.managers.AccountManager(getAuthenticatedClient(profileName)).reply(sub, comment[0]);
                    }
                } catch (java.lang.Exception e) {
                    if (e instanceof net.dean.jraw.ApiException) {
                        why = ((net.dean.jraw.ApiException) (e)).getExplanation();
                    }
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    private net.dean.jraw.RedditClient getAuthenticatedClient(java.lang.String profileName) {
        java.lang.String token;
        net.dean.jraw.RedditClient reddit = new net.dean.jraw.RedditClient(net.dean.jraw.http.UserAgent.of("android:me.ccrama.RedditSlide:v" + me.ccrama.redditslide.BuildConfig.VERSION_NAME));
        final java.util.HashMap<java.lang.String, java.lang.String> accounts = new java.util.HashMap<>();
        for (java.lang.String s : me.ccrama.redditslide.Authentication.authentication.getStringSet("accounts", new java.util.HashSet<java.lang.String>())) {
            if (s.contains(":")) {
                accounts.put(s.split(":")[0], s.split(":")[1]);
            } else {
                accounts.put(s, "");
            }
        }
        final java.util.ArrayList<java.lang.String> keys = new java.util.ArrayList<>(accounts.keySet());
        if (accounts.containsKey(profileName) && (!accounts.get(profileName).isEmpty())) {
            token = accounts.get(profileName);
        } else {
            java.util.ArrayList<java.lang.String> tokens = new java.util.ArrayList<>(me.ccrama.redditslide.Authentication.authentication.getStringSet("tokens", new java.util.HashSet<java.lang.String>()));
            int index = keys.indexOf(profileName);
            if (keys.indexOf(profileName) > tokens.size()) {
                index -= 1;
            }
            token = tokens.get(index);
        }
        me.ccrama.redditslide.Authentication.doVerify(token, reddit, true, mContext);
        return reddit;
    }
}