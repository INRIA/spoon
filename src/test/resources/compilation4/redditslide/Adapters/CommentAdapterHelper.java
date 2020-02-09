package me.ccrama.redditslide.Adapters;
import java.util.Locale;
import me.ccrama.redditslide.Views.DoEditorActions;
import me.ccrama.redditslide.Visuals.Palette;
import java.util.ArrayList;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.Activities.Profile;
import me.ccrama.redditslide.Activities.Website;
import me.ccrama.redditslide.Toolbox.Toolbox;
import me.ccrama.redditslide.TimeUtils;
import me.ccrama.redditslide.SpoilerRobotoTextView;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Views.CommentOverflow;
import me.ccrama.redditslide.UserSubscriptions;
import java.util.List;
import me.ccrama.redditslide.UserTags;
import me.ccrama.redditslide.ActionStates;
import me.ccrama.redditslide.Toolbox.ToolboxUI;
import me.ccrama.redditslide.Visuals.FontPreferences;
import me.ccrama.redditslide.util.LinkUtil;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.Views.RoundedBackgroundSpan;
import me.ccrama.redditslide.Activities.Reauthenticate;
import java.util.Map;
import me.ccrama.redditslide.OpenRedditLink;
/**
 * Created by Carlos on 8/4/2016.
 */
public class CommentAdapterHelper {
    public static void showOverflowBottomSheet(final me.ccrama.redditslide.Adapters.CommentAdapter adapter, final android.content.Context mContext, final me.ccrama.redditslide.Adapters.CommentViewHolder holder, final net.dean.jraw.models.CommentNode baseNode) {
        int[] attrs = new int[]{ me.ccrama.redditslide.R.attr.tintColor };
        final net.dean.jraw.models.Comment n = baseNode.getComment();
        android.content.res.TypedArray ta = mContext.obtainStyledAttributes(attrs);
        int color = ta.getColor(0, android.graphics.Color.WHITE);
        android.graphics.drawable.Drawable profile = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.profile);
        android.graphics.drawable.Drawable saved = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.iconstarfilled);
        android.graphics.drawable.Drawable gild = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.gild);
        android.graphics.drawable.Drawable copy = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.ic_content_copy);
        android.graphics.drawable.Drawable share = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.share);
        android.graphics.drawable.Drawable parent = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.commentchange);
        android.graphics.drawable.Drawable replies = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.notifs);
        android.graphics.drawable.Drawable permalink = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.link);
        android.graphics.drawable.Drawable report = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.report);
        profile.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        saved.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        gild.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        report.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        copy.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        share.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        parent.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        permalink.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        replies.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
        ta.recycle();
        com.cocosw.bottomsheet.BottomSheet.Builder b = new com.cocosw.bottomsheet.BottomSheet.Builder(((android.app.Activity) (mContext))).title(android.text.Html.fromHtml(n.getBody()));
        if (me.ccrama.redditslide.Authentication.didOnline) {
            b.sheet(1, profile, "/u/" + n.getAuthor());
            java.lang.String save = mContext.getString(me.ccrama.redditslide.R.string.btn_save);
            if (me.ccrama.redditslide.ActionStates.isSaved(n)) {
                save = mContext.getString(me.ccrama.redditslide.R.string.comment_unsave);
            }
            if (me.ccrama.redditslide.Authentication.isLoggedIn) {
                b.sheet(3, saved, save);
                b.sheet(16, report, mContext.getString(me.ccrama.redditslide.R.string.btn_report));
            }
            if (me.ccrama.redditslide.Authentication.name.equalsIgnoreCase(baseNode.getComment().getAuthor())) {
                b.sheet(50, replies, mContext.getString(me.ccrama.redditslide.R.string.disable_replies_comment));
            }
        }
        b.sheet(5, gild, mContext.getString(me.ccrama.redditslide.R.string.comment_gild)).sheet(7, copy, mContext.getString(me.ccrama.redditslide.R.string.misc_copy_text)).sheet(23, permalink, mContext.getString(me.ccrama.redditslide.R.string.comment_permalink)).sheet(4, share, mContext.getString(me.ccrama.redditslide.R.string.comment_share));
        if (!adapter.currentBaseNode.isTopLevel()) {
            b.sheet(10, parent, mContext.getString(me.ccrama.redditslide.R.string.comment_parent));
        }
        b.listener(new android.content.DialogInterface.OnClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                switch (which) {
                    case 1 :
                        {
                            // Go to author
                            android.content.Intent i = new android.content.Intent(mContext, me.ccrama.redditslide.Activities.Profile.class);
                            i.putExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_PROFILE, n.getAuthor());
                            mContext.startActivity(i);
                        }
                        break;
                    case 3 :
                        // Save comment
                        me.ccrama.redditslide.Adapters.CommentAdapterHelper.saveComment(n, mContext, holder);
                        break;
                    case 23 :
                        {
                            // Go to comment permalink
                            java.lang.String s = (("https://reddit.com" + adapter.submission.getPermalink()) + n.getFullName().substring(3, n.getFullName().length())) + "?context=3";
                            new me.ccrama.redditslide.OpenRedditLink(mContext, s);
                        }
                        break;
                    case 50 :
                        {
                            me.ccrama.redditslide.Adapters.CommentAdapterHelper.setReplies(baseNode.getComment(), holder, !baseNode.getComment().getDataNode().get("send_replies").asBoolean());
                        }
                        break;
                    case 5 :
                        {
                            // Gild comment
                            android.content.Intent i = new android.content.Intent(mContext, me.ccrama.redditslide.Activities.Website.class);
                            i.putExtra(me.ccrama.redditslide.util.LinkUtil.EXTRA_URL, (("https://reddit.com" + adapter.submission.getPermalink()) + n.getFullName().substring(3, n.getFullName().length())) + "?context=3&inapp=false");
                            i.putExtra(me.ccrama.redditslide.util.LinkUtil.EXTRA_COLOR, me.ccrama.redditslide.Visuals.Palette.getColor(n.getSubredditName()));
                            mContext.startActivity(i);
                        }
                        break;
                    case 16 :
                        // report
                        final com.afollestad.materialdialogs.MaterialDialog reportDialog = new com.afollestad.materialdialogs.MaterialDialog.Builder(mContext).customView(me.ccrama.redditslide.R.layout.report_dialog, true).title(me.ccrama.redditslide.R.string.report_comment).positiveText(me.ccrama.redditslide.R.string.btn_report).negativeText(me.ccrama.redditslide.R.string.btn_cancel).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                            @java.lang.Override
                            public void onClick(com.afollestad.materialdialogs.MaterialDialog dialog, com.afollestad.materialdialogs.DialogAction which) {
                                android.widget.RadioGroup reasonGroup = dialog.getCustomView().findViewById(me.ccrama.redditslide.R.id.report_reasons);
                                java.lang.String reportReason;
                                if (reasonGroup.getCheckedRadioButtonId() == me.ccrama.redditslide.R.id.report_other) {
                                    reportReason = ((android.widget.EditText) (dialog.getCustomView().findViewById(me.ccrama.redditslide.R.id.input_report_reason))).getText().toString();
                                } else {
                                    reportReason = ((android.widget.RadioButton) (reasonGroup.findViewById(reasonGroup.getCheckedRadioButtonId()))).getText().toString();
                                }
                                new me.ccrama.redditslide.Adapters.CommentAdapterHelper.AsyncReportTask(adapter.currentBaseNode, adapter.listView).execute(reportReason);
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
                                net.dean.jraw.models.Ruleset rules = me.ccrama.redditslide.Authentication.reddit.getRules(adapter.currentBaseNode.getComment().getSubredditName());
                                return rules;
                            }

                            @java.lang.Override
                            protected void onPostExecute(net.dean.jraw.models.Ruleset rules) {
                                reportDialog.getCustomView().findViewById(me.ccrama.redditslide.R.id.report_loading).setVisibility(android.view.View.GONE);
                                if (rules.getSubredditRules().size() > 0) {
                                    android.widget.TextView subHeader = new android.widget.TextView(mContext);
                                    subHeader.setText(mContext.getString(me.ccrama.redditslide.R.string.report_sub_rules, adapter.currentBaseNode.getComment().getSubredditName()));
                                    reasonGroup.addView(subHeader, reasonGroup.getChildCount() - 2);
                                }
                                for (net.dean.jraw.models.SubredditRule rule : rules.getSubredditRules()) {
                                    if ((rule.getKind() == net.dean.jraw.models.SubredditRule.RuleKind.COMMENT) || (rule.getKind() == net.dean.jraw.models.SubredditRule.RuleKind.ALL)) {
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
                    case 10 :
                        // View comment parent
                        me.ccrama.redditslide.Adapters.CommentAdapterHelper.viewCommentParent(adapter, holder, mContext, baseNode);
                        break;
                    case 7 :
                        // Show select and copy text to clipboard
                        final android.widget.TextView showText = new android.widget.TextView(mContext);
                        showText.setText(org.apache.commons.text.StringEscapeUtils.unescapeHtml4(n.getBody()));
                        showText.setTextIsSelectable(true);
                        int sixteen = me.ccrama.redditslide.Reddit.dpToPxVertical(24);
                        showText.setPadding(sixteen, 0, sixteen, 0);
                        com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext);
                        builder.setView(showText).setTitle("Select text to copy").setCancelable(true).setPositiveButton("COPY", new android.content.DialogInterface.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                java.lang.String selected = showText.getText().toString().substring(showText.getSelectionStart(), showText.getSelectionEnd());
                                android.content.ClipboardManager clipboard = ((android.content.ClipboardManager) (mContext.getSystemService(android.content.Context.CLIPBOARD_SERVICE)));
                                android.content.ClipData clip = android.content.ClipData.newPlainText("Comment text", selected);
                                clipboard.setPrimaryClip(clip);
                                android.widget.Toast.makeText(mContext, me.ccrama.redditslide.R.string.submission_comment_copied, android.widget.Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton(me.ccrama.redditslide.R.string.btn_cancel, null).setNeutralButton("COPY ALL", new android.content.DialogInterface.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                android.content.ClipboardManager clipboard = ((android.content.ClipboardManager) (mContext.getSystemService(android.content.Context.CLIPBOARD_SERVICE)));
                                android.content.ClipData clip = android.content.ClipData.newPlainText("Comment text", android.text.Html.fromHtml(n.getBody()));
                                clipboard.setPrimaryClip(clip);
                                android.widget.Toast.makeText(mContext, me.ccrama.redditslide.R.string.submission_comment_copied, android.widget.Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                        break;
                    case 4 :
                        // Share comment
                        me.ccrama.redditslide.Reddit.defaultShareText(adapter.submission.getTitle(), (("https://reddit.com" + adapter.submission.getPermalink()) + n.getFullName().substring(3, n.getFullName().length())) + "?context=3", mContext);
                        break;
                }
            }
        });
        b.show();
    }

    private static void setReplies(final net.dean.jraw.models.Comment comment, final me.ccrama.redditslide.Adapters.CommentViewHolder holder, final boolean showReplies) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
            @java.lang.Override
            protected java.lang.Void doInBackground(java.lang.Void... params) {
                try {
                    new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).sendRepliesToInbox(comment, showReplies);
                } catch (net.dean.jraw.ApiException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @java.lang.Override
            protected void onPostExecute(java.lang.Void aVoid) {
                android.support.design.widget.Snackbar s;
                try {
                    if (holder.itemView != null) {
                        if (!showReplies) {
                            s = android.support.design.widget.Snackbar.make(holder.itemView, me.ccrama.redditslide.R.string.replies_disabled_comment, android.support.design.widget.Snackbar.LENGTH_LONG);
                        } else {
                            s = android.support.design.widget.Snackbar.make(holder.itemView, me.ccrama.redditslide.R.string.replies_enabled_comment, android.support.design.widget.Snackbar.LENGTH_SHORT);
                        }
                        android.view.View view = s.getView();
                        android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(android.graphics.Color.WHITE);
                        s.show();
                    }
                } catch (java.lang.Exception ignored) {
                }
            }
        }.execute();
    }

    private static void viewCommentParent(me.ccrama.redditslide.Adapters.CommentAdapter adapter, me.ccrama.redditslide.Adapters.CommentViewHolder holder, android.content.Context mContext, net.dean.jraw.models.CommentNode baseNode) {
        int old = holder.getAdapterPosition();
        int pos = (old < 2) ? 0 : old - 1;
        for (int i = pos - 1; i >= 0; i--) {
            me.ccrama.redditslide.Adapters.CommentObject o = adapter.currentComments.get(adapter.getRealPosition(i));
            if (((o instanceof me.ccrama.redditslide.Adapters.CommentItem) && ((pos - 1) != i)) && (o.comment.getDepth() < baseNode.getDepth())) {
                android.view.LayoutInflater inflater = ((android.app.Activity) (mContext)).getLayoutInflater();
                final android.view.View dialoglayout = inflater.inflate(me.ccrama.redditslide.R.layout.parent_comment_dialog, null);
                final com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext);
                net.dean.jraw.models.Comment parent = o.comment.getComment();
                adapter.setViews(parent.getDataNode().get("body_html").asText(), adapter.submission.getSubredditName(), ((me.ccrama.redditslide.SpoilerRobotoTextView) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.firstTextView))), ((me.ccrama.redditslide.Views.CommentOverflow) (dialoglayout.findViewById(me.ccrama.redditslide.R.id.commentOverflow))));
                builder.setView(dialoglayout);
                builder.show();
                break;
            }
        }
    }

    private static void saveComment(final net.dean.jraw.models.Comment comment, final android.content.Context mContext, final me.ccrama.redditslide.Adapters.CommentViewHolder holder) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
            @java.lang.Override
            protected java.lang.Void doInBackground(java.lang.Void... params) {
                try {
                    if (me.ccrama.redditslide.ActionStates.isSaved(comment)) {
                        new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).unsave(comment);
                        me.ccrama.redditslide.ActionStates.setSaved(comment, false);
                    } else {
                        new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).save(comment);
                        me.ccrama.redditslide.ActionStates.setSaved(comment, true);
                    }
                } catch (net.dean.jraw.ApiException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @java.lang.Override
            protected void onPostExecute(java.lang.Void aVoid) {
                android.support.design.widget.Snackbar s;
                try {
                    if (holder.itemView != null) {
                        if (me.ccrama.redditslide.ActionStates.isSaved(comment)) {
                            s = android.support.design.widget.Snackbar.make(holder.itemView, me.ccrama.redditslide.R.string.submission_comment_saved, android.support.design.widget.Snackbar.LENGTH_LONG);
                            if ((me.ccrama.redditslide.Authentication.me != null) && me.ccrama.redditslide.Authentication.me.hasGold()) {
                                s.setAction(me.ccrama.redditslide.R.string.category_categorize, new android.view.View.OnClickListener() {
                                    @java.lang.Override
                                    public void onClick(android.view.View v) {
                                        me.ccrama.redditslide.Adapters.CommentAdapterHelper.categorizeComment(comment, mContext);
                                    }
                                });
                            }
                        } else {
                            s = android.support.design.widget.Snackbar.make(holder.itemView, me.ccrama.redditslide.R.string.submission_comment_unsaved, android.support.design.widget.Snackbar.LENGTH_SHORT);
                        }
                        android.view.View view = s.getView();
                        android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(android.graphics.Color.WHITE);
                        s.show();
                    }
                } catch (java.lang.Exception ignored) {
                }
            }
        }.execute();
    }

    private static void categorizeComment(final net.dean.jraw.models.Comment comment, final android.content.Context mContext) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.util.List<java.lang.String>>() {
            android.app.Dialog d;

            @java.lang.Override
            public void onPreExecute() {
                d = new com.afollestad.materialdialogs.MaterialDialog.Builder(mContext).progress(true, 100).content(me.ccrama.redditslide.R.string.misc_please_wait).title(me.ccrama.redditslide.R.string.profile_category_loading).show();
            }

            @java.lang.Override
            protected java.util.List<java.lang.String> doInBackground(java.lang.Void... params) {
                try {
                    java.util.List<java.lang.String> categories = new java.util.ArrayList<java.lang.String>(new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).getSavedCategories());
                    categories.add("New category");
                    return categories;
                } catch (java.lang.Exception e) {
                    e.printStackTrace();
                    return new java.util.ArrayList<java.lang.String>() {
                        {
                            add("New category");
                        }
                    };
                }
            }

            @java.lang.Override
            public void onPostExecute(final java.util.List<java.lang.String> data) {
                try {
                    new com.afollestad.materialdialogs.MaterialDialog.Builder(mContext).items(data).title(me.ccrama.redditslide.R.string.sidebar_select_flair).itemsCallback(new com.afollestad.materialdialogs.MaterialDialog.ListCallback() {
                        @java.lang.Override
                        public void onSelection(com.afollestad.materialdialogs.MaterialDialog dialog, final android.view.View itemView, int which, java.lang.CharSequence text) {
                            final java.lang.String t = data.get(which);
                            if (which == (data.size() - 1)) {
                                new com.afollestad.materialdialogs.MaterialDialog.Builder(mContext).title(me.ccrama.redditslide.R.string.category_set_name).input(mContext.getString(me.ccrama.redditslide.R.string.category_set_name_hint), null, false, new com.afollestad.materialdialogs.MaterialDialog.InputCallback() {
                                    @java.lang.Override
                                    public void onInput(com.afollestad.materialdialogs.MaterialDialog dialog, java.lang.CharSequence input) {
                                    }
                                }).positiveText(me.ccrama.redditslide.R.string.btn_set).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                                    @java.lang.Override
                                    public void onClick(com.afollestad.materialdialogs.MaterialDialog dialog, com.afollestad.materialdialogs.DialogAction which) {
                                        final java.lang.String flair = dialog.getInputEditText().getText().toString();
                                        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
                                            @java.lang.Override
                                            protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                                                try {
                                                    new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).save(comment, flair);
                                                    return true;
                                                } catch (net.dean.jraw.ApiException e) {
                                                    e.printStackTrace();
                                                    return false;
                                                }
                                            }

                                            @java.lang.Override
                                            protected void onPostExecute(java.lang.Boolean done) {
                                                android.support.design.widget.Snackbar s;
                                                if (done) {
                                                    if (itemView != null) {
                                                        s = android.support.design.widget.Snackbar.make(itemView, me.ccrama.redditslide.R.string.submission_info_saved, android.support.design.widget.Snackbar.LENGTH_SHORT);
                                                        android.view.View view = s.getView();
                                                        android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                                                        tv.setTextColor(android.graphics.Color.WHITE);
                                                        s.show();
                                                    }
                                                } else if (itemView != null) {
                                                    s = android.support.design.widget.Snackbar.make(itemView, me.ccrama.redditslide.R.string.category_set_error, android.support.design.widget.Snackbar.LENGTH_SHORT);
                                                    android.view.View view = s.getView();
                                                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                                                    tv.setTextColor(android.graphics.Color.WHITE);
                                                    s.show();
                                                }
                                            }
                                        }.execute();
                                    }
                                }).negativeText(me.ccrama.redditslide.R.string.btn_cancel).show();
                            } else {
                                new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
                                    @java.lang.Override
                                    protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                                        try {
                                            new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).save(comment, t);
                                            return true;
                                        } catch (net.dean.jraw.ApiException e) {
                                            e.printStackTrace();
                                            return false;
                                        }
                                    }

                                    @java.lang.Override
                                    protected void onPostExecute(java.lang.Boolean done) {
                                        android.support.design.widget.Snackbar s;
                                        if (done) {
                                            if (itemView != null) {
                                                s = android.support.design.widget.Snackbar.make(itemView, me.ccrama.redditslide.R.string.submission_info_saved, android.support.design.widget.Snackbar.LENGTH_SHORT);
                                                android.view.View view = s.getView();
                                                android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                                                tv.setTextColor(android.graphics.Color.WHITE);
                                                s.show();
                                            }
                                        } else if (itemView != null) {
                                            s = android.support.design.widget.Snackbar.make(itemView, me.ccrama.redditslide.R.string.category_set_error, android.support.design.widget.Snackbar.LENGTH_SHORT);
                                            android.view.View view = s.getView();
                                            android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                                            tv.setTextColor(android.graphics.Color.WHITE);
                                            s.show();
                                        }
                                    }
                                }.execute();
                            }
                        }
                    }).show();
                    if (d != null) {
                        d.dismiss();
                    }
                } catch (java.lang.Exception ignored) {
                }
            }
        }.execute();
    }

    public static void showModBottomSheet(final me.ccrama.redditslide.Adapters.CommentAdapter adapter, final android.content.Context mContext, final net.dean.jraw.models.CommentNode baseNode, final net.dean.jraw.models.Comment comment, final me.ccrama.redditslide.Adapters.CommentViewHolder holder, final java.util.Map<java.lang.String, java.lang.Integer> reports, final java.util.Map<java.lang.String, java.lang.String> reports2) {
        int[] attrs = new int[]{ me.ccrama.redditslide.R.attr.tintColor };
        android.content.res.TypedArray ta = mContext.obtainStyledAttributes(attrs);
        // Initialize drawables
        int color = ta.getColor(0, android.graphics.Color.WHITE);
        android.graphics.drawable.Drawable profile = mContext.getResources().getDrawable(me.ccrama.redditslide.R.drawable.profile);
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
        final boolean stickied = comment.getDataNode().has("stickied") && comment.getDataNode().get("stickied").asBoolean();
        if (baseNode.isTopLevel() && comment.getAuthor().equalsIgnoreCase(me.ccrama.redditslide.Authentication.name)) {
            if (!stickied) {
                b.sheet(4, pin, mContext.getString(me.ccrama.redditslide.R.string.mod_sticky));
            } else {
                b.sheet(4, pin, mContext.getString(me.ccrama.redditslide.R.string.mod_unsticky));
            }
        }
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
                        me.ccrama.redditslide.Adapters.CommentAdapterHelper.doApproval(mContext, holder, comment, adapter);
                        break;
                    case 4 :
                        if (stickied) {
                            me.ccrama.redditslide.Adapters.CommentAdapterHelper.unStickyComment(mContext, holder, comment);
                        } else {
                            me.ccrama.redditslide.Adapters.CommentAdapterHelper.stickyComment(mContext, holder, comment);
                        }
                        break;
                    case 9 :
                        if (distinguished) {
                            me.ccrama.redditslide.Adapters.CommentAdapterHelper.unDistinguishComment(mContext, holder, comment);
                        } else {
                            me.ccrama.redditslide.Adapters.CommentAdapterHelper.distinguishComment(mContext, holder, comment);
                        }
                        break;
                    case 6 :
                        me.ccrama.redditslide.Adapters.CommentAdapterHelper.removeComment(mContext, holder, comment, adapter, false);
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
                                        adapter.removed.add(comment.getFullName());
                                        adapter.approved.remove(comment.getFullName());
                                        holder.content.setText(me.ccrama.redditslide.Adapters.CommentAdapterHelper.getScoreString(comment, mContext, holder, adapter.submission, adapter));
                                    } else {
                                        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.err_general).setMessage(me.ccrama.redditslide.R.string.err_retry_later).show();
                                    }
                                }
                            });
                        } else {
                            // Show a Slide reason dialog if we can't show a toolbox or reddit one
                            me.ccrama.redditslide.Adapters.CommentAdapterHelper.doRemoveCommentReason(mContext, holder, comment, adapter);
                        }
                        break;
                    case 10 :
                        me.ccrama.redditslide.Adapters.CommentAdapterHelper.removeComment(mContext, holder, comment, adapter, true);
                        break;
                    case 8 :
                        android.content.Intent i = new android.content.Intent(mContext, me.ccrama.redditslide.Activities.Profile.class);
                        i.putExtra(me.ccrama.redditslide.Activities.Profile.EXTRA_PROFILE, comment.getAuthor());
                        mContext.startActivity(i);
                        break;
                    case 23 :
                        me.ccrama.redditslide.Adapters.CommentAdapterHelper.showBan(mContext, adapter.listView, comment, "", "", "", "");
                        break;
                    case 24 :
                        me.ccrama.redditslide.Toolbox.ToolboxUI.showUsernotes(mContext, comment.getAuthor(), comment.getSubredditName(), (("l," + comment.getParentId()) + ",") + comment.getId());
                        break;
                }
            }
        });
        b.show();
    }

    public static void showBan(final android.content.Context mContext, final android.view.View mToolbar, final net.dean.jraw.models.Comment submission, java.lang.String rs, java.lang.String nt, java.lang.String msg, java.lang.String t) {
        android.widget.LinearLayout l = new android.widget.LinearLayout(mContext);
        l.setOrientation(android.widget.LinearLayout.VERTICAL);
        int sixteen = me.ccrama.redditslide.Reddit.dpToPxVertical(16);
        l.setPadding(sixteen, 0, sixteen, 0);
        final android.widget.EditText reason = new android.widget.EditText(mContext);
        reason.setHint(me.ccrama.redditslide.R.string.mod_ban_reason);
        reason.setText(rs);
        reason.setInputType(android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        l.addView(reason);
        final android.widget.EditText note = new android.widget.EditText(mContext);
        note.setHint(me.ccrama.redditslide.R.string.mod_ban_note_mod);
        note.setText(nt);
        note.setInputType(android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        l.addView(note);
        final android.widget.EditText message = new android.widget.EditText(mContext);
        message.setHint(me.ccrama.redditslide.R.string.mod_ban_note_user);
        message.setText(msg);
        message.setInputType(android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        l.addView(message);
        final android.widget.EditText time = new android.widget.EditText(mContext);
        time.setHint(me.ccrama.redditslide.R.string.mod_ban_time);
        time.setText(t);
        time.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        l.addView(time);
        com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext);
        builder.setView(l).setTitle(mContext.getString(me.ccrama.redditslide.R.string.mod_ban_title, submission.getAuthor())).setCancelable(true).setPositiveButton(me.ccrama.redditslide.R.string.mod_btn_ban, new android.content.DialogInterface.OnClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                // to ban
                if (reason.getText().toString().isEmpty()) {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.mod_ban_reason_required).setMessage(me.ccrama.redditslide.R.string.misc_please_try_again).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.content.DialogInterface dialog, int which) {
                            me.ccrama.redditslide.Adapters.CommentAdapterHelper.showBan(mContext, mToolbar, submission, reason.getText().toString(), note.getText().toString(), message.getText().toString(), time.getText().toString());
                        }
                    }).setCancelable(false).show();
                } else {
                    new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
                        @java.lang.Override
                        protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                            try {
                                java.lang.String n = note.getText().toString();
                                java.lang.String m = message.getText().toString();
                                if (n.isEmpty()) {
                                    n = null;
                                }
                                if (m.isEmpty()) {
                                    m = null;
                                }
                                if (time.getText().toString().isEmpty()) {
                                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).banUserPermanently(submission.getSubredditName(), submission.getAuthor(), reason.getText().toString(), n, m);
                                } else {
                                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).banUser(submission.getSubredditName(), submission.getAuthor(), reason.getText().toString(), n, m, java.lang.Integer.valueOf(time.getText().toString()));
                                }
                                return true;
                            } catch (java.lang.Exception e) {
                                if (e instanceof net.dean.jraw.http.oauth.InvalidScopeException) {
                                    scope = true;
                                }
                                e.printStackTrace();
                                return false;
                            }
                        }

                        boolean scope;

                        @java.lang.Override
                        protected void onPostExecute(java.lang.Boolean done) {
                            android.support.design.widget.Snackbar s;
                            if (done) {
                                s = android.support.design.widget.Snackbar.make(mToolbar, me.ccrama.redditslide.R.string.mod_ban_success, android.support.design.widget.Snackbar.LENGTH_SHORT);
                            } else {
                                if (scope) {
                                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.mod_ban_reauth).setMessage(me.ccrama.redditslide.R.string.mod_ban_reauth_question).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                                        @java.lang.Override
                                        public void onClick(android.content.DialogInterface dialog, int which) {
                                            android.content.Intent i = new android.content.Intent(mContext, me.ccrama.redditslide.Activities.Reauthenticate.class);
                                            mContext.startActivity(i);
                                        }
                                    }).setNegativeButton(me.ccrama.redditslide.R.string.misc_maybe_later, null).setCancelable(false).show();
                                }
                                s = android.support.design.widget.Snackbar.make(mToolbar, me.ccrama.redditslide.R.string.mod_ban_fail, android.support.design.widget.Snackbar.LENGTH_INDEFINITE).setAction(me.ccrama.redditslide.R.string.misc_try_again, new android.view.View.OnClickListener() {
                                    @java.lang.Override
                                    public void onClick(android.view.View v) {
                                        me.ccrama.redditslide.Adapters.CommentAdapterHelper.showBan(mContext, mToolbar, submission, reason.getText().toString(), note.getText().toString(), message.getText().toString(), time.getText().toString());
                                    }
                                });
                            }
                            if (s != null) {
                                android.view.View view = s.getView();
                                android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                                tv.setTextColor(android.graphics.Color.WHITE);
                                s.show();
                            }
                        }
                    }.execute();
                }
            }
        }).setNegativeButton(me.ccrama.redditslide.R.string.btn_cancel, null).show();
    }

    public static void distinguishComment(final android.content.Context mContext, final me.ccrama.redditslide.Adapters.CommentViewHolder holder, final net.dean.jraw.models.Comment comment) {
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

    public static void unDistinguishComment(final android.content.Context mContext, final me.ccrama.redditslide.Adapters.CommentViewHolder holder, final net.dean.jraw.models.Comment comment) {
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

    public static void stickyComment(final android.content.Context mContext, final me.ccrama.redditslide.Adapters.CommentViewHolder holder, final net.dean.jraw.models.Comment comment) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
            @java.lang.Override
            public void onPostExecute(java.lang.Boolean b) {
                if (b) {
                    android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(holder.itemView, me.ccrama.redditslide.R.string.comment_stickied, android.support.design.widget.Snackbar.LENGTH_LONG);
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
                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).setSticky(comment, true);
                } catch (net.dean.jraw.ApiException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }.execute();
    }

    public static void viewReports(final android.content.Context mContext, final java.util.Map<java.lang.String, java.lang.Integer> reports, final java.util.Map<java.lang.String, java.lang.String> reports2) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.util.ArrayList<java.lang.String>>() {
            @java.lang.Override
            protected java.util.ArrayList<java.lang.String> doInBackground(java.lang.Void... params) {
                java.util.ArrayList<java.lang.String> finalReports = new java.util.ArrayList<>();
                for (java.util.Map.Entry<java.lang.String, java.lang.Integer> entry : reports.entrySet()) {
                    finalReports.add((("x" + entry.getValue()) + " ") + entry.getKey());
                }
                for (java.util.Map.Entry<java.lang.String, java.lang.String> entry : reports2.entrySet()) {
                    finalReports.add((entry.getKey() + ": ") + entry.getValue());
                }
                if (finalReports.isEmpty()) {
                    finalReports.add(mContext.getString(me.ccrama.redditslide.R.string.mod_no_reports));
                }
                return finalReports;
            }

            @java.lang.Override
            public void onPostExecute(java.util.ArrayList<java.lang.String> data) {
                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.mod_reports).setItems(data.toArray(new java.lang.CharSequence[data.size()]), new android.content.DialogInterface.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                    }
                }).show();
            }
        }.execute();
    }

    public static void doApproval(final android.content.Context mContext, final me.ccrama.redditslide.Adapters.CommentViewHolder holder, final net.dean.jraw.models.Comment comment, final me.ccrama.redditslide.Adapters.CommentAdapter adapter) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
            @java.lang.Override
            public void onPostExecute(java.lang.Boolean b) {
                if (b) {
                    adapter.approved.add(comment.getFullName());
                    adapter.removed.remove(comment.getFullName());
                    holder.content.setText(me.ccrama.redditslide.Adapters.CommentAdapterHelper.getScoreString(comment, mContext, holder, adapter.submission, adapter));
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

    public static void unStickyComment(final android.content.Context mContext, final me.ccrama.redditslide.Adapters.CommentViewHolder holder, final net.dean.jraw.models.Comment comment) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
            @java.lang.Override
            public void onPostExecute(java.lang.Boolean b) {
                if (b) {
                    android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(holder.itemView, me.ccrama.redditslide.R.string.comment_unstickied, android.support.design.widget.Snackbar.LENGTH_LONG);
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
                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).setSticky(comment, false);
                } catch (net.dean.jraw.ApiException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }.execute();
    }

    public static void removeComment(final android.content.Context mContext, final me.ccrama.redditslide.Adapters.CommentViewHolder holder, final net.dean.jraw.models.Comment comment, final me.ccrama.redditslide.Adapters.CommentAdapter adapter, final boolean spam) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
            @java.lang.Override
            public void onPostExecute(java.lang.Boolean b) {
                if (b) {
                    android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(holder.itemView, me.ccrama.redditslide.R.string.comment_removed, android.support.design.widget.Snackbar.LENGTH_LONG);
                    android.view.View view = s.getView();
                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(android.graphics.Color.WHITE);
                    s.show();
                    adapter.removed.add(comment.getFullName());
                    adapter.approved.remove(comment.getFullName());
                    holder.content.setText(me.ccrama.redditslide.Adapters.CommentAdapterHelper.getScoreString(comment, mContext, holder, adapter.submission, adapter));
                } else {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.err_general).setMessage(me.ccrama.redditslide.R.string.err_retry_later).show();
                }
            }

            @java.lang.Override
            protected java.lang.Boolean doInBackground(java.lang.Void... params) {
                try {
                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).remove(comment, spam);
                } catch (net.dean.jraw.ApiException | net.dean.jraw.http.NetworkException e) {
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
     * @param adapter
     * 		commentadapter
     */
    public static void doRemoveCommentReason(final android.content.Context mContext, final me.ccrama.redditslide.Adapters.CommentViewHolder holder, final net.dean.jraw.models.Comment comment, final me.ccrama.redditslide.Adapters.CommentAdapter adapter) {
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
                me.ccrama.redditslide.Adapters.CommentAdapterHelper.removeCommentReason(comment, mContext, holder, adapter, dialog.getInputEditText().getText().toString());
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
     * @param adapter
     * 		commentadapter
     * @param reason
     * 		reason
     */
    public static void removeCommentReason(final net.dean.jraw.models.Comment comment, final android.content.Context mContext, me.ccrama.redditslide.Adapters.CommentViewHolder holder, final me.ccrama.redditslide.Adapters.CommentAdapter adapter, final java.lang.String reason) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean>() {
            @java.lang.Override
            public void onPostExecute(java.lang.Boolean b) {
                if (b) {
                    android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(holder.itemView, me.ccrama.redditslide.R.string.comment_removed, android.support.design.widget.Snackbar.LENGTH_LONG);
                    android.view.View view = s.getView();
                    android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(android.graphics.Color.WHITE);
                    s.show();
                    adapter.removed.add(comment.getFullName());
                    adapter.approved.remove(comment.getFullName());
                    holder.content.setText(me.ccrama.redditslide.Adapters.CommentAdapterHelper.getScoreString(comment, mContext, holder, adapter.submission, adapter));
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

    public static android.text.SpannableStringBuilder createApprovedLine(java.lang.String approvedBy, android.content.Context c) {
        android.text.SpannableStringBuilder removedString = new android.text.SpannableStringBuilder("\n");
        android.text.SpannableStringBuilder mod = new android.text.SpannableStringBuilder("Approved by ");
        mod.append(approvedBy);
        mod.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, mod.length(), android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mod.setSpan(new android.text.style.RelativeSizeSpan(0.8F), 0, mod.length(), android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mod.setSpan(new android.text.style.ForegroundColorSpan(c.getResources().getColor(me.ccrama.redditslide.R.color.md_green_300)), 0, mod.length(), android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        removedString.append(mod);
        return removedString;
    }

    public static android.text.SpannableStringBuilder createRemovedLine(java.lang.String removedBy, android.content.Context c) {
        android.text.SpannableStringBuilder removedString = new android.text.SpannableStringBuilder("\n");
        android.text.SpannableStringBuilder mod = new android.text.SpannableStringBuilder("Removed by ");
        if (removedBy.equalsIgnoreCase("true")) {
            // Probably shadowbanned or removed not by mod action
            mod = new android.text.SpannableStringBuilder("Removed by Reddit");
        } else {
            mod.append(removedBy);
        }
        mod.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, mod.length(), android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mod.setSpan(new android.text.style.RelativeSizeSpan(0.8F), 0, mod.length(), android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mod.setSpan(new android.text.style.ForegroundColorSpan(c.getResources().getColor(me.ccrama.redditslide.R.color.md_red_300)), 0, mod.length(), android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        removedString.append(mod);
        return removedString;
    }

    public static android.text.Spannable getScoreString(net.dean.jraw.models.Comment comment, android.content.Context mContext, me.ccrama.redditslide.Adapters.CommentViewHolder holder, net.dean.jraw.models.Submission submission, me.ccrama.redditslide.Adapters.CommentAdapter adapter) {
        final java.lang.String spacer = (" " + mContext.getString(me.ccrama.redditslide.R.string.submission_properties_seperator_comments)) + " ";
        android.text.SpannableStringBuilder titleString = new android.text.SpannableStringBuilder("\u200b");// zero width space to fix first span height

        android.text.SpannableStringBuilder author = new android.text.SpannableStringBuilder(comment.getAuthor());
        final int authorcolor = me.ccrama.redditslide.Visuals.Palette.getFontColorUser(comment.getAuthor());
        author.setSpan(new android.text.style.TypefaceSpan("sans-serif-condensed"), 0, author.length(), android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        author.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, author.length(), android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
        } else if (((submission != null) && comment.getAuthor().toLowerCase(java.util.Locale.ENGLISH).equals(submission.getAuthor().toLowerCase(java.util.Locale.ENGLISH))) && (!comment.getAuthor().equals("[deleted]"))) {
            author.replace(0, author.length(), (" " + comment.getAuthor()) + " ");
            author.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(mContext, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_blue_300, false), 0, author.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (authorcolor != 0) {
            author.setSpan(new android.text.style.ForegroundColorSpan(authorcolor), 0, author.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        titleString.append(author);
        titleString.append(spacer);
        int scoreColor;
        switch (me.ccrama.redditslide.ActionStates.getVoteDirection(comment)) {
            case UPVOTE :
                scoreColor = holder.textColorUp;
                break;
            case DOWNVOTE :
                scoreColor = holder.textColorDown;
                break;
            default :
                scoreColor = holder.textColorRegular;
                break;
        }
        java.lang.String scoreText;
        if (comment.isScoreHidden()) {
            scoreText = ("[" + mContext.getString(me.ccrama.redditslide.R.string.misc_score_hidden).toUpperCase()) + "]";
        } else {
            scoreText = java.lang.String.format(java.util.Locale.getDefault(), "%d", me.ccrama.redditslide.Adapters.CommentAdapterHelper.getScoreText(comment));
        }
        android.text.SpannableStringBuilder score = new android.text.SpannableStringBuilder(scoreText);
        if ((score == null) || score.toString().isEmpty()) {
            score = new android.text.SpannableStringBuilder("0");
        }
        if (!scoreText.contains("[")) {
            score.append(java.lang.String.format(java.util.Locale.getDefault(), " %s", mContext.getResources().getQuantityString(me.ccrama.redditslide.R.plurals.points, comment.getScore())));
        }
        score.setSpan(new android.text.style.ForegroundColorSpan(scoreColor), 0, score.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        titleString.append(score);
        titleString.append(comment.isControversial() ? " †" : "");
        titleString.append(spacer);
        java.lang.Long time = comment.getCreated().getTime();
        java.lang.String timeAgo = me.ccrama.redditslide.TimeUtils.getTimeAgo(time, mContext);
        android.text.SpannableStringBuilder timeSpan = new android.text.SpannableStringBuilder().append((timeAgo == null) || timeAgo.isEmpty() ? "just now" : timeAgo);
        if ((((me.ccrama.redditslide.SettingValues.highlightTime && (adapter.lastSeen != 0)) && (adapter.lastSeen < time)) && (!adapter.dataSet.single)) && me.ccrama.redditslide.SettingValues.commentLastVisit) {
            timeSpan.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(android.graphics.Color.WHITE, me.ccrama.redditslide.Visuals.Palette.getColor(comment.getSubredditName()), false, mContext), 0, timeSpan.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        titleString.append(timeSpan);
        titleString.append(comment.getEditDate() != null ? (" (edit " + me.ccrama.redditslide.TimeUtils.getTimeAgo(comment.getEditDate().getTime(), mContext)) + ")" : "");
        titleString.append("  ");
        if (comment.getDataNode().get("stickied").asBoolean()) {
            android.text.SpannableStringBuilder pinned = new android.text.SpannableStringBuilder(("\u00a0" + mContext.getString(me.ccrama.redditslide.R.string.submission_stickied).toUpperCase()) + "\u00a0");
            pinned.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(mContext, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_green_300, false), 0, pinned.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            titleString.append(pinned);
            titleString.append(" ");
        }
        if (((comment.getTimesSilvered() > 0) || (comment.getTimesGilded() > 0)) || (comment.getTimesPlatinized() > 0)) {
            android.content.res.TypedArray a = mContext.obtainStyledAttributes(new me.ccrama.redditslide.Visuals.FontPreferences(mContext).getPostFontStyle().getResId(), me.ccrama.redditslide.R.styleable.FontStyle);
            int fontsize = ((int) (a.getDimensionPixelSize(me.ccrama.redditslide.R.styleable.FontStyle_font_cardtitle, -1) * 0.75));
            a.recycle();
            // Add silver, gold, platinum icons and counts in that order
            if (comment.getTimesSilvered() > 0) {
                final java.lang.String timesSilvered = (comment.getTimesSilvered() == 1) ? "" : "\u200ax" + java.lang.Integer.toString(comment.getTimesSilvered());
                android.text.SpannableStringBuilder silvered = new android.text.SpannableStringBuilder(("\u00a0\u2605" + timesSilvered) + "\u00a0");
                android.graphics.Bitmap image = adapter.awardIcons[0];
                float aspectRatio = ((float) ((1.0 * image.getWidth()) / image.getHeight()));
                if (image != null) {
                    image.recycle();
                }
                image = android.graphics.Bitmap.createScaledBitmap(image, ((int) (java.lang.Math.ceil(fontsize * aspectRatio))), ((int) (java.lang.Math.ceil(fontsize))), true);
                silvered.setSpan(new android.text.style.ImageSpan(mContext, image, android.text.style.ImageSpan.ALIGN_BASELINE), 0, 2, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                silvered.setSpan(new android.text.style.RelativeSizeSpan(0.75F), 3, silvered.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                titleString.append(silvered);
                titleString.append(" ");
            }
            if (comment.getTimesGilded() > 0) {
                final java.lang.String timesGilded = (comment.getTimesGilded() == 1) ? "" : "\u200ax" + java.lang.Integer.toString(comment.getTimesGilded());
                android.text.SpannableStringBuilder gilded = new android.text.SpannableStringBuilder(("\u00a0\u2605" + timesGilded) + "\u00a0");
                android.graphics.Bitmap image = adapter.awardIcons[1];
                float aspectRatio = ((float) ((1.0 * image.getWidth()) / image.getHeight()));
                if (image != null) {
                    image.recycle();
                }
                image = android.graphics.Bitmap.createScaledBitmap(image, ((int) (java.lang.Math.ceil(fontsize * aspectRatio))), ((int) (java.lang.Math.ceil(fontsize))), true);
                gilded.setSpan(new android.text.style.ImageSpan(mContext, image, android.text.style.ImageSpan.ALIGN_BASELINE), 0, 2, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                gilded.setSpan(new android.text.style.RelativeSizeSpan(0.75F), 3, gilded.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                titleString.append(gilded);
                titleString.append(" ");
            }
            if (comment.getTimesPlatinized() > 0) {
                final java.lang.String timesPlatinized = (comment.getTimesPlatinized() == 1) ? "" : "\u200ax" + java.lang.Integer.toString(comment.getTimesPlatinized());
                android.text.SpannableStringBuilder platinized = new android.text.SpannableStringBuilder(("\u00a0\u2605" + timesPlatinized) + "\u00a0");
                android.graphics.Bitmap image = adapter.awardIcons[2];
                float aspectRatio = ((float) ((1.0 * image.getWidth()) / image.getHeight()));
                if (image != null) {
                    image.recycle();
                }
                image = android.graphics.Bitmap.createScaledBitmap(image, ((int) (java.lang.Math.ceil(fontsize * aspectRatio))), ((int) (java.lang.Math.ceil(fontsize))), true);
                platinized.setSpan(new android.text.style.ImageSpan(mContext, image, android.text.style.ImageSpan.ALIGN_BASELINE), 0, 2, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                platinized.setSpan(new android.text.style.RelativeSizeSpan(0.75F), 3, platinized.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                titleString.append(platinized);
                titleString.append(" ");
            }
        }
        if (me.ccrama.redditslide.UserTags.isUserTagged(comment.getAuthor())) {
            android.text.SpannableStringBuilder pinned = new android.text.SpannableStringBuilder(("\u00a0" + me.ccrama.redditslide.UserTags.getUserTag(comment.getAuthor())) + "\u00a0");
            pinned.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(mContext, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_blue_500, false), 0, pinned.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            titleString.append(pinned);
            titleString.append(" ");
        }
        if (me.ccrama.redditslide.UserSubscriptions.friends.contains(comment.getAuthor())) {
            android.text.SpannableStringBuilder pinned = new android.text.SpannableStringBuilder(("\u00a0" + mContext.getString(me.ccrama.redditslide.R.string.profile_friend)) + "\u00a0");
            pinned.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(mContext, me.ccrama.redditslide.R.color.white, me.ccrama.redditslide.R.color.md_deep_orange_500, false), 0, pinned.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            titleString.append(pinned);
            titleString.append(" ");
        }
        if ((comment.getAuthorFlair() != null) && ((comment.getAuthorFlair().getText() != null) || (comment.getAuthorFlair().getCssClass() != null))) {
            java.lang.String flairText = null;
            if (((comment.getAuthorFlair() != null) && (comment.getAuthorFlair().getText() != null)) && (!comment.getAuthorFlair().getText().isEmpty())) {
                flairText = comment.getAuthorFlair().getText();
            } else if (((comment.getAuthorFlair() != null) && (comment.getAuthorFlair().getCssClass() != null)) && (!comment.getAuthorFlair().getCssClass().isEmpty())) {
                flairText = comment.getAuthorFlair().getCssClass();
            }
            if (flairText != null) {
                android.util.TypedValue typedValue = new android.util.TypedValue();
                android.content.res.Resources.Theme theme = mContext.getTheme();
                theme.resolveAttribute(me.ccrama.redditslide.R.attr.activity_background, typedValue, true);
                int color = typedValue.data;
                android.text.SpannableStringBuilder pinned = new android.text.SpannableStringBuilder(("\u00a0" + android.text.Html.fromHtml(flairText)) + "\u00a0");
                pinned.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(holder.firstTextView.getCurrentTextColor(), color, false, mContext), 0, pinned.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                titleString.append(pinned);
                titleString.append(" ");
            }
        }
        me.ccrama.redditslide.Toolbox.ToolboxUI.appendToolboxNote(mContext, titleString, comment.getSubredditName(), comment.getAuthor());
        if (adapter.removed.contains(comment.getFullName()) || ((comment.getBannedBy() != null) && (!adapter.approved.contains(comment.getFullName())))) {
            titleString.append(me.ccrama.redditslide.Adapters.CommentAdapterHelper.createRemovedLine(comment.getBannedBy() == null ? me.ccrama.redditslide.Authentication.name : comment.getBannedBy(), mContext));
        } else if (adapter.approved.contains(comment.getFullName()) || ((comment.getApprovedBy() != null) && (!adapter.removed.contains(comment.getFullName())))) {
            titleString.append(me.ccrama.redditslide.Adapters.CommentAdapterHelper.createApprovedLine(comment.getApprovedBy() == null ? me.ccrama.redditslide.Authentication.name : comment.getApprovedBy(), mContext));
        }
        return titleString;
    }

    public static int getScoreText(net.dean.jraw.models.Comment comment) {
        int submissionScore = comment.getScore();
        switch (me.ccrama.redditslide.ActionStates.getVoteDirection(comment)) {
            case UPVOTE :
                {
                    if (comment.getVote() != net.dean.jraw.models.VoteDirection.UPVOTE) {
                        if (comment.getVote() == net.dean.jraw.models.VoteDirection.DOWNVOTE)
                            ++submissionScore;

                        ++submissionScore;// offset the score by +1

                    }
                    break;
                }
            case DOWNVOTE :
                {
                    if (comment.getVote() != net.dean.jraw.models.VoteDirection.DOWNVOTE) {
                        if (comment.getVote() == net.dean.jraw.models.VoteDirection.UPVOTE)
                            --submissionScore;

                        --submissionScore;// offset the score by +1

                    }
                    break;
                }
            case NO_VOTE :
                if ((comment.getVote() == net.dean.jraw.models.VoteDirection.UPVOTE) && comment.getAuthor().equalsIgnoreCase(me.ccrama.redditslide.Authentication.name)) {
                    submissionScore--;
                }
                break;
        }
        return submissionScore;
    }

    public static void doCommentEdit(final me.ccrama.redditslide.Adapters.CommentAdapter adapter, final android.content.Context mContext, android.support.v4.app.FragmentManager fm, final net.dean.jraw.models.CommentNode baseNode, java.lang.String replyText, final me.ccrama.redditslide.Adapters.CommentViewHolder holder) {
        android.view.LayoutInflater inflater = ((android.app.Activity) (mContext)).getLayoutInflater();
        final android.view.View dialoglayout = inflater.inflate(me.ccrama.redditslide.R.layout.edit_comment, null);
        final com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext);
        final android.widget.EditText e = dialoglayout.findViewById(me.ccrama.redditslide.R.id.entry);
        e.setText(org.apache.commons.text.StringEscapeUtils.unescapeHtml4(baseNode.getComment().getBody()));
        me.ccrama.redditslide.Views.DoEditorActions.doActions(e, dialoglayout, fm, ((android.app.Activity) (mContext)), org.apache.commons.text.StringEscapeUtils.unescapeHtml4(replyText), null);
        builder.setCancelable(false).setView(dialoglayout);
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
                new me.ccrama.redditslide.Adapters.CommentAdapterHelper.AsyncEditTask(adapter, baseNode, text, mContext, d, holder).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
    }

    public static void deleteComment(final me.ccrama.redditslide.Adapters.CommentAdapter adapter, final android.content.Context mContext, final net.dean.jraw.models.CommentNode baseNode, final me.ccrama.redditslide.Adapters.CommentViewHolder holder) {
        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.comment_delete).setMessage(me.ccrama.redditslide.R.string.comment_delete_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                new me.ccrama.redditslide.Adapters.CommentAdapterHelper.AsyncDeleteTask(adapter, baseNode, holder, mContext).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, new android.content.DialogInterface.OnClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public static class AsyncEditTask extends android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void> {
        me.ccrama.redditslide.Adapters.CommentAdapter adapter;

        net.dean.jraw.models.CommentNode baseNode;

        java.lang.String text;

        android.content.Context mContext;

        android.app.Dialog dialog;

        me.ccrama.redditslide.Adapters.CommentViewHolder holder;

        public AsyncEditTask(me.ccrama.redditslide.Adapters.CommentAdapter adapter, net.dean.jraw.models.CommentNode baseNode, java.lang.String text, android.content.Context mContext, android.app.Dialog dialog, me.ccrama.redditslide.Adapters.CommentViewHolder holder) {
            this.adapter = adapter;
            this.baseNode = baseNode;
            this.text = text;
            this.mContext = mContext;
            this.dialog = dialog;
            this.holder = holder;
        }

        @java.lang.Override
        protected java.lang.Void doInBackground(java.lang.Void... params) {
            try {
                new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).updateContribution(baseNode.getComment(), text);
                adapter.currentSelectedItem = baseNode.getComment().getFullName();
                net.dean.jraw.models.CommentNode n = baseNode.notifyCommentChanged(me.ccrama.redditslide.Authentication.reddit);
                adapter.editComment(n, holder);
                dialog.dismiss();
            } catch (java.lang.Exception e) {
                e.printStackTrace();
                ((android.app.Activity) (mContext)).runOnUiThread(new java.lang.Runnable() {
                    @java.lang.Override
                    public void run() {
                        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.comment_delete_err).setMessage(me.ccrama.redditslide.R.string.comment_delete_err_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                dialog.dismiss();
                                new me.ccrama.redditslide.Adapters.CommentAdapterHelper.AsyncEditTask(adapter, baseNode, text, mContext, me.ccrama.redditslide.Adapters.CommentAdapterHelper.AsyncEditTask.this.dialog, holder).execute();
                            }
                        }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, new android.content.DialogInterface.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                    }
                });
            }
            return null;
        }
    }

    public static class AsyncDeleteTask extends android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Boolean> {
        me.ccrama.redditslide.Adapters.CommentAdapter adapter;

        net.dean.jraw.models.CommentNode baseNode;

        me.ccrama.redditslide.Adapters.CommentViewHolder holder;

        android.content.Context mContext;

        public AsyncDeleteTask(me.ccrama.redditslide.Adapters.CommentAdapter adapter, net.dean.jraw.models.CommentNode baseNode, me.ccrama.redditslide.Adapters.CommentViewHolder holder, android.content.Context mContext) {
            this.adapter = adapter;
            this.baseNode = baseNode;
            this.holder = holder;
            this.mContext = mContext;
        }

        @java.lang.Override
        protected void onPostExecute(java.lang.Boolean success) {
            if (success) {
                holder.firstTextView.setTextHtml(mContext.getString(me.ccrama.redditslide.R.string.content_deleted));
                holder.content.setText(me.ccrama.redditslide.R.string.content_deleted);
            } else {
                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(mContext).setTitle(me.ccrama.redditslide.R.string.comment_delete_err).setMessage(me.ccrama.redditslide.R.string.comment_delete_err_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        dialog.dismiss();
                        doInBackground();
                    }
                }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, new android.content.DialogInterface.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        }

        @java.lang.Override
        protected java.lang.Boolean doInBackground(java.lang.Void... params) {
            try {
                new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).delete(baseNode.getComment());
                adapter.deleted.add(baseNode.getComment().getFullName());
                return true;
            } catch (net.dean.jraw.ApiException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public static class AsyncReportTask extends android.os.AsyncTask<java.lang.String, java.lang.Void, java.lang.Void> {
        private net.dean.jraw.models.CommentNode baseNode;

        private android.view.View contextView;

        public AsyncReportTask(net.dean.jraw.models.CommentNode baseNode, android.view.View contextView) {
            this.baseNode = baseNode;
            this.contextView = contextView;
        }

        @java.lang.Override
        protected java.lang.Void doInBackground(java.lang.String... reason) {
            try {
                new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).report(baseNode.getComment(), reason[0]);
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

    public static void showChildrenObject(final android.view.View v) {
        v.setVisibility(android.view.View.VISIBLE);
        android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofFloat(0, 1.0F);
        animator.setDuration(250);
        animator.setInterpolator(new android.support.v4.view.animation.FastOutSlowInInterpolator());
        animator.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {
            @java.lang.Override
            public void onAnimationUpdate(android.animation.ValueAnimator animation) {
                float value = ((java.lang.Float) (animation.getAnimatedValue())).floatValue();
                v.setAlpha(value);
                v.setScaleX(value);
                v.setScaleY(value);
            }
        });
        animator.start();
    }

    public static void hideChildrenObject(final android.view.View v) {
        android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofFloat(1.0F, 0);
        animator.setDuration(250);
        animator.setInterpolator(new android.support.v4.view.animation.FastOutSlowInInterpolator());
        animator.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {
            @java.lang.Override
            public void onAnimationUpdate(android.animation.ValueAnimator animation) {
                float value = ((java.lang.Float) (animation.getAnimatedValue())).floatValue();
                v.setAlpha(value);
                v.setScaleX(value);
                v.setScaleY(value);
            }
        });
        animator.addListener(new android.animation.Animator.AnimatorListener() {
            @java.lang.Override
            public void onAnimationStart(android.animation.Animator arg0) {
            }

            @java.lang.Override
            public void onAnimationRepeat(android.animation.Animator arg0) {
            }

            @java.lang.Override
            public void onAnimationEnd(android.animation.Animator arg0) {
                v.setVisibility(android.view.View.GONE);
            }

            @java.lang.Override
            public void onAnimationCancel(android.animation.Animator arg0) {
                v.setVisibility(android.view.View.GONE);
            }
        });
        animator.start();
    }
}