/**
 * Created by ccrama on 3/22/2015.
 */
package me.ccrama.redditslide.Adapters;
import me.ccrama.redditslide.Activities.SendMessage;
import java.util.Locale;
import me.ccrama.redditslide.Views.DoEditorActions;
import me.ccrama.redditslide.Visuals.Palette;
import me.ccrama.redditslide.util.SubmissionParser;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.DataShare;
import me.ccrama.redditslide.Activities.Profile;
import me.ccrama.redditslide.Drafts;
import me.ccrama.redditslide.TimeUtils;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Visuals.FontPreferences;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.Views.RoundedBackgroundSpan;
import me.ccrama.redditslide.UserSubscriptions;
import java.util.List;
import me.ccrama.redditslide.UserTags;
import me.ccrama.redditslide.OpenRedditLink;
import me.ccrama.redditslide.Activities.Inbox;
public class InboxAdapter extends android.support.v7.widget.RecyclerView.Adapter<android.support.v7.widget.RecyclerView.ViewHolder> implements me.ccrama.redditslide.Adapters.BaseAdapter {
    private static final int TOP_LEVEL = 1;

    private final int SPACER = 6;

    public final android.content.Context mContext;

    private final android.support.v7.widget.RecyclerView listView;

    public me.ccrama.redditslide.Adapters.InboxMessages dataSet;

    public InboxAdapter(android.content.Context mContext, me.ccrama.redditslide.Adapters.InboxMessages dataSet, android.support.v7.widget.RecyclerView listView) {
        this.mContext = mContext;
        this.listView = listView;
        this.dataSet = dataSet;
        boolean isSame = false;
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
        if (((position == 0) && (!dataSet.posts.isEmpty())) || (((position == (dataSet.posts.size() + 1)) && dataSet.nomore) && (!dataSet.where.equalsIgnoreCase("where")))) {
            return SPACER;
        } else {
            position -= 1;
        }
        if (((position == dataSet.posts.size()) && (!dataSet.posts.isEmpty())) && (!dataSet.nomore)) {
            return 5;
        }
        // IS COMMENT IN MESSAGES
        if (dataSet.posts.get(position).getSubject().toLowerCase(java.util.Locale.ENGLISH).contains("re:") && dataSet.where.equalsIgnoreCase("messages")) {
            return 2;
        }
        return me.ccrama.redditslide.Adapters.InboxAdapter.TOP_LEVEL;
    }

    @java.lang.Override
    public android.support.v7.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup viewGroup, int i) {
        if (i == SPACER) {
            android.view.View v = android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.spacer, viewGroup, false);
            return new me.ccrama.redditslide.Adapters.InboxAdapter.SpacerViewHolder(v);
        } else if (i == me.ccrama.redditslide.Adapters.InboxAdapter.TOP_LEVEL) {
            android.view.View v = android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.top_level_message, viewGroup, false);
            return new me.ccrama.redditslide.Adapters.MessageViewHolder(v);
        } else if (i == 5) {
            android.view.View v = android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.loadingmore, viewGroup, false);
            return new me.ccrama.redditslide.Adapters.ContributionAdapter.EmptyViewHolder(v);
        } else {
            android.view.View v = android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.message_reply, viewGroup, false);
            return new me.ccrama.redditslide.Adapters.MessageViewHolder(v);
        }
    }

    @java.lang.Override
    public void onBindViewHolder(final android.support.v7.widget.RecyclerView.ViewHolder viewHolder, int pos) {
        int i = (pos != 0) ? pos - 1 : pos;
        if ((!(viewHolder instanceof me.ccrama.redditslide.Adapters.ContributionAdapter.EmptyViewHolder)) && (!(viewHolder instanceof me.ccrama.redditslide.Adapters.InboxAdapter.SpacerViewHolder))) {
            final me.ccrama.redditslide.Adapters.MessageViewHolder messageViewHolder = ((me.ccrama.redditslide.Adapters.MessageViewHolder) (viewHolder));
            final net.dean.jraw.models.Message comment = dataSet.posts.get(i);
            messageViewHolder.time.setText(me.ccrama.redditslide.TimeUtils.getTimeAgo(comment.getCreated().getTime(), mContext));
            android.text.SpannableStringBuilder titleString = new android.text.SpannableStringBuilder();
            java.lang.String author = comment.getAuthor();
            java.lang.String direction = "from ";
            if ((((!dataSet.where.contains("mod")) && comment.getDataNode().has("dest")) && (!me.ccrama.redditslide.Authentication.name.equalsIgnoreCase(comment.getDataNode().get("dest").asText()))) && (!comment.getDataNode().get("dest").asText().equals("reddit"))) {
                author = comment.getDataNode().get("dest").asText().replace("#", "/r/");
                direction = "to ";
            }
            if ((comment.getDataNode().has("subreddit") && (author == null)) || author.isEmpty()) {
                direction = "via /r/" + comment.getSubreddit();
            }
            titleString.append(direction);
            if (author != null) {
                titleString.append(author);
                titleString.append(" ");
                if (me.ccrama.redditslide.UserTags.isUserTagged(author)) {
                    android.text.SpannableStringBuilder pinned = new android.text.SpannableStringBuilder((" " + me.ccrama.redditslide.UserTags.getUserTag(author)) + " ");
                    pinned.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(mContext, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_blue_500, false), 0, pinned.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    titleString.append(pinned);
                    titleString.append(" ");
                }
                if (me.ccrama.redditslide.UserSubscriptions.friends.contains(author)) {
                    android.text.SpannableStringBuilder pinned = new android.text.SpannableStringBuilder((" " + mContext.getString(me.ccrama.redditslide.R.string.profile_friend)) + " ");
                    pinned.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(mContext, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_deep_orange_500, false), 0, pinned.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    titleString.append(pinned);
                    titleString.append(" ");
                }
            }
            java.lang.String spacer = mContext.getString(me.ccrama.redditslide.R.string.submission_properties_seperator);
            if (comment.getDataNode().has("subreddit") && (!comment.getDataNode().get("subreddit").isNull())) {
                titleString.append(spacer);
                java.lang.String subname = comment.getDataNode().get("subreddit").asText();
                android.text.SpannableStringBuilder subreddit = new android.text.SpannableStringBuilder("/r/" + subname);
                if (me.ccrama.redditslide.SettingValues.colorSubName && (me.ccrama.redditslide.Visuals.Palette.getColor(subname) != me.ccrama.redditslide.Visuals.Palette.getDefaultColor())) {
                    subreddit.setSpan(new android.text.style.ForegroundColorSpan(me.ccrama.redditslide.Visuals.Palette.getColor(subname)), 0, subreddit.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    subreddit.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, subreddit.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                titleString.append(subreddit);
            }
            messageViewHolder.user.setText(titleString);
            android.text.SpannableStringBuilder b = new android.text.SpannableStringBuilder();
            if (((mContext instanceof me.ccrama.redditslide.Activities.Inbox) && (comment.getCreated().getTime() > ((me.ccrama.redditslide.Activities.Inbox) (mContext)).last)) && (!comment.isRead())) {
                android.text.SpannableStringBuilder tagNew = new android.text.SpannableStringBuilder("\u00a0NEW\u00a0");
                tagNew.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(android.graphics.Color.WHITE, mContext.getResources().getColor(me.ccrama.redditslide.R.color.md_green_400), true, mContext), 0, tagNew.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                b.append(tagNew);
                b.append(" ");
            }
            b.append(comment.getSubject());
            if (comment.getDataNode().has("link_title")) {
                android.text.SpannableStringBuilder link = new android.text.SpannableStringBuilder((" " + android.text.Html.fromHtml(comment.getDataNode().get("link_title").asText())) + " ");
                link.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD_ITALIC), 0, link.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                link.setSpan(new android.text.style.RelativeSizeSpan(0.8F), 0, link.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                b.append(link);
            }
            messageViewHolder.title.setText(b);
            if (comment.isRead()) {
                messageViewHolder.title.setTextColor(messageViewHolder.content.getCurrentTextColor());
            } else {
                messageViewHolder.title.setTextColor(android.support.v4.content.ContextCompat.getColor(mContext, me.ccrama.redditslide.R.color.md_red_400));
            }
            messageViewHolder.itemView.setOnLongClickListener(new android.view.View.OnLongClickListener() {
                @java.lang.Override
                public boolean onLongClick(android.view.View v) {
                    int[] attrs = new int[]{ me.ccrama.redditslide.R.attr.tintColor };
                    android.content.res.TypedArray ta = mContext.obtainStyledAttributes(attrs);
                    final int color = ta.getColor(0, android.graphics.Color.WHITE);
                    android.graphics.drawable.Drawable profile = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.profile);
                    final android.graphics.drawable.Drawable reply = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.reply);
                    android.graphics.drawable.Drawable unhide = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.ic_visibility);
                    android.graphics.drawable.Drawable hide = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.hide);
                    android.graphics.drawable.Drawable copy = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.ic_content_copy);
                    android.graphics.drawable.Drawable reddit = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.commentchange);
                    profile.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
                    hide.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
                    copy.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
                    reddit.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
                    reply.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
                    unhide.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
                    ta.recycle();
                    com.cocosw.bottomsheet.BottomSheet.Builder b = new com.cocosw.bottomsheet.BottomSheet.Builder(((android.app.Activity) (mContext))).title(android.text.Html.fromHtml(comment.getSubject()));
                    java.lang.String author = comment.getAuthor();
                    if ((((!dataSet.where.contains("mod")) && comment.getDataNode().has("dest")) && (!me.ccrama.redditslide.Authentication.name.equalsIgnoreCase(comment.getDataNode().get("dest").asText()))) && (!comment.getDataNode().get("dest").asText().equals("reddit"))) {
                        author = comment.getDataNode().get("dest").asText().replace("#", "/r/");
                    }
                    if (comment.getAuthor() != null) {
                        b.sheet(1, profile, "/u/" + author);
                    }
                    java.lang.String read = mContext.getString(me.ccrama.redditslide.R.string.mail_mark_read);
                    android.graphics.drawable.Drawable rDrawable = hide;
                    if (comment.isRead()) {
                        read = mContext.getString(me.ccrama.redditslide.R.string.mail_mark_unread);
                        rDrawable = unhide;
                    }
                    b.sheet(2, rDrawable, read);
                    b.sheet(3, reply, mContext.getString(me.ccrama.redditslide.R.string.btn_reply));
                    b.sheet(25, copy, mContext.getString(me.ccrama.redditslide.R.string.misc_copy_text));
                    if (comment.isComment()) {
                        b.sheet(30, reddit, mContext.getString(me.ccrama.redditslide.R.string.mail_view_full_thread));
                    }
                    final java.lang.String finalAuthor = author;
                    b.listener(new android.content.DialogInterface.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.content.DialogInterface dialog, int which) {
                            switch (which) {
                                case 1 :
                                    {
                                        android.content.Intent i = new android.content.Intent(mContext, me.ccrama.redditslide.Activities.Profile.class);
                                        i.putExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_PROFILE, finalAuthor);
                                        mContext.startActivity(i);
                                    }
                                    break;
                                case 2 :
                                    {
                                        if (comment.isRead()) {
                                            comment.read = false;
                                            new me.ccrama.redditslide.Adapters.InboxAdapter.AsyncSetRead(false).execute(comment);
                                            messageViewHolder.title.setTextColor(android.support.v4.content.ContextCompat.getColor(mContext, me.ccrama.redditslide.R.color.md_red_400));
                                        } else {
                                            comment.read = true;
                                            new me.ccrama.redditslide.Adapters.InboxAdapter.AsyncSetRead(true).execute(comment);
                                            messageViewHolder.title.setTextColor(messageViewHolder.content.getCurrentTextColor());
                                        }
                                    }
                                    break;
                                case 3 :
                                    {
                                        doInboxReply(comment);
                                    }
                                    break;
                                case 25 :
                                    {
                                        android.content.ClipboardManager clipboard = ((android.content.ClipboardManager) (mContext.getSystemService(android.content.Context.CLIPBOARD_SERVICE)));
                                        android.content.ClipData clip = android.content.ClipData.newPlainText("Message", comment.getBody());
                                        clipboard.setPrimaryClip(clip);
                                        android.widget.Toast.makeText(mContext, mContext.getString(me.ccrama.redditslide.R.string.mail_message_copied), android.widget.Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case 30 :
                                    {
                                        java.lang.String context = comment.getDataNode().get("context").asText();
                                        new me.ccrama.redditslide.OpenRedditLink(mContext, "https://reddit.com" + context.substring(0, context.lastIndexOf("/")));
                                    }
                                    break;
                            }
                        }
                    }).show();
                    return true;
                }
            });
            messageViewHolder.itemView.setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    if (comment.isRead()) {
                        if (comment instanceof net.dean.jraw.models.PrivateMessage) {
                            me.ccrama.redditslide.DataShare.sharedMessage = ((net.dean.jraw.models.PrivateMessage) (comment));
                            android.content.Intent i = new android.content.Intent(mContext, me.ccrama.redditslide.Activities.SendMessage.class);
                            i.putExtra(me.ccrama.redditslide.Activities.SendMessage.EXTRA_NAME, comment.getAuthor());
                            i.putExtra(me.ccrama.redditslide.Activities.SendMessage.EXTRA_REPLY, true);
                            mContext.startActivity(i);
                        } else {
                            new me.ccrama.redditslide.OpenRedditLink(mContext, comment.getDataNode().get("context").asText());
                        }
                    } else {
                        comment.read = true;
                        new me.ccrama.redditslide.Adapters.InboxAdapter.AsyncSetRead(true).execute(comment);
                        messageViewHolder.title.setTextColor(messageViewHolder.content.getCurrentTextColor());
                        {
                            android.text.SpannableStringBuilder b = new android.text.SpannableStringBuilder(comment.getSubject());
                            if (comment.getDataNode().has("link_title")) {
                                android.text.SpannableStringBuilder link = new android.text.SpannableStringBuilder((" " + android.text.Html.fromHtml(comment.getDataNode().get("link_title").asText())) + " ");
                                link.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD_ITALIC), 0, link.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                link.setSpan(new android.text.style.RelativeSizeSpan(0.8F), 0, link.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                b.append(link);
                            }
                            messageViewHolder.title.setText(b);
                        }
                    }
                }
            });// Set typeface for body

            int type = new me.ccrama.redditslide.Visuals.FontPreferences(mContext).getFontTypeComment().getTypeface();
            android.graphics.Typeface typeface;
            if (type >= 0) {
                typeface = com.devspark.robototextview.RobotoTypefaces.obtainTypeface(mContext, type);
            } else {
                typeface = android.graphics.Typeface.DEFAULT;
            }
            messageViewHolder.content.setTypeface(typeface);
            setViews(comment.getDataNode().get("body_html").asText(), "FORCE_LINK_CLICK", messageViewHolder);
        }
        if (viewHolder instanceof me.ccrama.redditslide.Adapters.InboxAdapter.SpacerViewHolder) {
            viewHolder.itemView.findViewById(me.ccrama.redditslide.R.id.height).setLayoutParams(new android.widget.LinearLayout.LayoutParams(viewHolder.itemView.getWidth(), ((android.app.Activity) (mContext)).findViewById(me.ccrama.redditslide.R.id.header).getHeight()));
        }
    }

    private void doInboxReply(final net.dean.jraw.models.Message replyTo) {
        android.view.LayoutInflater inflater = ((android.app.Activity) (mContext)).getLayoutInflater();
        final android.view.View dialoglayout = inflater.inflate(me.ccrama.redditslide.R.layout.edit_comment, null);
        final com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext);
        final android.widget.EditText e = ((android.widget.EditText) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.entry)));
        me.ccrama.redditslide.Views.DoEditorActions.doActions(e, dialoglayout, ((android.support.v7.app.AppCompatActivity) (mContext)).getSupportFragmentManager(), ((android.app.Activity) (mContext)), replyTo.getBody(), null);
        builder.setView(dialoglayout);
        final android.app.Dialog d = builder.create();
        d.getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        d.show();
        dialoglayout.findViewById(me.ccrama.redditslide.R.id.cancel).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                d.dismiss();
            }
        });
        dialoglayout.findViewById(me.ccrama.redditslide.R.id.submit).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                final java.lang.String text = e.getText().toString();
                new me.ccrama.redditslide.Adapters.InboxAdapter.AsyncReplyTask(replyTo, text).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                d.dismiss();
            }
        });
    }

    private class AsyncReplyTask extends android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void> {
        java.lang.String trying;

        net.dean.jraw.models.Message replyTo;

        java.lang.String text;

        public AsyncReplyTask(net.dean.jraw.models.Message replyTo, java.lang.String text) {
            this.replyTo = replyTo;
            this.text = text;
        }

        @java.lang.Override
        protected java.lang.Void doInBackground(java.lang.Void... voids) {
            sendMessage(null, null);
            return null;
        }

        boolean sent;

        public void sendMessage(net.dean.jraw.models.Captcha captcha, java.lang.String captchaAttempt) {
            try {
                new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).reply(replyTo, text);
                sent = true;
            } catch (net.dean.jraw.ApiException e) {
                sent = false;
                e.printStackTrace();
            }
        }

        @java.lang.Override
        public void onPostExecute(java.lang.Void voids) {
            if (sent) {
                android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(listView, "Reply sent!", android.support.design.widget.Snackbar.LENGTH_LONG);
                android.view.View view = s.getView();
                android.widget.TextView tv = ((android.widget.TextView) (view.findViewById(android.support.design.R.id.snackbar_text)));
                tv.setTextColor(android.graphics.Color.WHITE);
                s.show();
            } else {
                android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(listView, "Sending failed! Reply saved as a draft.", android.support.design.widget.Snackbar.LENGTH_LONG);
                android.view.View view = s.getView();
                android.widget.TextView tv = ((android.widget.TextView) (view.findViewById(android.support.design.R.id.snackbar_text)));
                tv.setTextColor(android.graphics.Color.WHITE);
                s.show();
                me.ccrama.redditslide.Drafts.addDraft(text);
                sent = true;
            }
        }
    }

    public class SpacerViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public SpacerViewHolder(android.view.View itemView) {
            super(itemView);
        }
    }

    private void setViews(java.lang.String rawHTML, java.lang.String subredditName, me.ccrama.redditslide.Adapters.MessageViewHolder holder) {
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
                holder.commentOverflow.setViews(blocks, subredditName);
            } else {
                holder.commentOverflow.setViews(blocks.subList(startIndex, blocks.size()), subredditName);
            }
        } else {
            holder.commentOverflow.removeAllViews();
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

    private class AsyncSetRead extends android.os.AsyncTask<net.dean.jraw.models.Message, java.lang.Void, java.lang.Void> {
        java.lang.Boolean b;

        public AsyncSetRead(java.lang.Boolean b) {
            this.b = b;
        }

        @java.lang.Override
        protected java.lang.Void doInBackground(net.dean.jraw.models.Message... params) {
            new net.dean.jraw.managers.InboxManager(me.ccrama.redditslide.Authentication.reddit).setRead(b, params[0]);
            return null;
        }
    }
}