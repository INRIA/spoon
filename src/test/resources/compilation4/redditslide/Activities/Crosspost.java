package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.SpoilerRobotoTextView;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.util.SubmissionParser;
import me.ccrama.redditslide.Views.CommentOverflow;
import me.ccrama.redditslide.UserSubscriptions;
import java.util.List;
import me.ccrama.redditslide.OpenRedditLink;
/**
 * Created by ccrama on 3/5/2015.
 */
public class Crosspost extends me.ccrama.redditslide.Activities.BaseActivity {
    public static net.dean.jraw.models.Submission toCrosspost;

    private android.support.v7.widget.SwitchCompat inboxReplies;

    android.os.AsyncTask<java.lang.Void, java.lang.Void, net.dean.jraw.models.Subreddit> tchange;

    public void onCreate(android.os.Bundle savedInstanceState) {
        disableSwipeBackLayout();
        super.onCreate(savedInstanceState);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_crosspost);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            android.view.Window window = this.getWindow();
            window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, me.ccrama.redditslide.R.string.title_crosspost, true, true);
        inboxReplies = ((android.support.v7.widget.SwitchCompat) (findViewById(me.ccrama.redditslide.R.id.replies)));
        final android.widget.AutoCompleteTextView subredditText = ((android.widget.AutoCompleteTextView) (findViewById(me.ccrama.redditslide.R.id.subreddittext)));
        ((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.crossposttext))).setText(((me.ccrama.redditslide.Activities.Crosspost.toCrosspost.getTitle() + getString(me.ccrama.redditslide.R.string.submission_properties_seperator)) + "/u/") + me.ccrama.redditslide.Activities.Crosspost.toCrosspost.getAuthor());
        findViewById(me.ccrama.redditslide.R.id.crossposttext).setEnabled(false);
        android.widget.ArrayAdapter adapter = new android.widget.ArrayAdapter(this, android.R.layout.simple_list_item_1, me.ccrama.redditslide.UserSubscriptions.getAllSubreddits(this));
        subredditText.setAdapter(adapter);
        subredditText.setThreshold(2);
        subredditText.addTextChangedListener(new android.text.TextWatcher() {
            @java.lang.Override
            public void beforeTextChanged(java.lang.CharSequence s, int start, int count, int after) {
            }

            @java.lang.Override
            public void onTextChanged(java.lang.CharSequence s, int start, int before, int count) {
                if (tchange != null) {
                    tchange.cancel(true);
                }
                findViewById(me.ccrama.redditslide.R.id.submittext).setVisibility(android.view.View.GONE);
            }

            @java.lang.Override
            public void afterTextChanged(android.text.Editable s) {
            }
        });
        subredditText.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @java.lang.Override
            public void onFocusChange(android.view.View v, boolean hasFocus) {
                findViewById(me.ccrama.redditslide.R.id.submittext).setVisibility(android.view.View.GONE);
                if (!hasFocus) {
                    tchange = new android.os.AsyncTask<java.lang.Void, java.lang.Void, net.dean.jraw.models.Subreddit>() {
                        @java.lang.Override
                        protected net.dean.jraw.models.Subreddit doInBackground(java.lang.Void... params) {
                            try {
                                return me.ccrama.redditslide.Authentication.reddit.getSubreddit(subredditText.getText().toString());
                            } catch (java.lang.Exception ignored) {
                            }
                            return null;
                        }

                        @java.lang.Override
                        protected void onPostExecute(net.dean.jraw.models.Subreddit s) {
                            if (s != null) {
                                java.lang.String text = s.getDataNode().get("submit_text_html").asText();
                                if (((text != null) && (!text.isEmpty())) && (!text.equals("null"))) {
                                    findViewById(me.ccrama.redditslide.R.id.submittext).setVisibility(android.view.View.VISIBLE);
                                    setViews(text, subredditText.getText().toString(), ((me.ccrama.redditslide.SpoilerRobotoTextView) (findViewById(me.ccrama.redditslide.R.id.submittext))), ((me.ccrama.redditslide.Views.CommentOverflow) (findViewById(me.ccrama.redditslide.R.id.commentOverflow))));
                                }
                                if (s.getSubredditType().equals("RESTRICTED")) {
                                    subredditText.setText("");
                                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.Crosspost.this).setTitle(me.ccrama.redditslide.R.string.err_submit_restricted).setMessage(me.ccrama.redditslide.R.string.err_submit_restricted_text).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, null).show();
                                }
                            } else {
                                findViewById(me.ccrama.redditslide.R.id.submittext).setVisibility(android.view.View.GONE);
                            }
                        }
                    };
                    tchange.execute();
                }
            }
        });
        ((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.titletext))).setText(me.ccrama.redditslide.Activities.Crosspost.toCrosspost.getTitle());
        findViewById(me.ccrama.redditslide.R.id.suggest).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                ((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.titletext))).setText(me.ccrama.redditslide.Activities.Crosspost.toCrosspost.getTitle());
            }
        });
        findViewById(me.ccrama.redditslide.R.id.send).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View view) {
                ((android.support.design.widget.FloatingActionButton) (findViewById(me.ccrama.redditslide.R.id.send))).hide();
                new me.ccrama.redditslide.Activities.Crosspost.AsyncDo().execute();
            }
        });
    }

    public void setViews(java.lang.String rawHTML, java.lang.String subredditName, me.ccrama.redditslide.SpoilerRobotoTextView firstTextView, me.ccrama.redditslide.Views.CommentOverflow commentOverflow) {
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
            firstTextView.setVisibility(android.view.View.GONE);
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

    private class AsyncDo extends android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void> {
        @java.lang.Override
        protected java.lang.Void doInBackground(java.lang.Void... voids) {
            try {
                try {
                    net.dean.jraw.models.Submission s = new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).crosspost(me.ccrama.redditslide.Activities.Crosspost.toCrosspost, ((android.widget.AutoCompleteTextView) (findViewById(me.ccrama.redditslide.R.id.subreddittext))).getText().toString(), ((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.titletext))).getText().toString(), null, "");
                    new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).sendRepliesToInbox(s, inboxReplies.isChecked());
                    new me.ccrama.redditslide.OpenRedditLink(me.ccrama.redditslide.Activities.Crosspost.this, (("reddit.com/r/" + ((android.widget.AutoCompleteTextView) (findViewById(me.ccrama.redditslide.R.id.subreddittext))).getText().toString()) + "/comments/") + s.getFullName().substring(3, s.getFullName().length()));
                    me.ccrama.redditslide.Activities.Crosspost.this.finish();
                } catch (final net.dean.jraw.ApiException e) {
                    e.printStackTrace();
                    runOnUiThread(new java.lang.Runnable() {
                        @java.lang.Override
                        public void run() {
                            showErrorRetryDialog((((getString(me.ccrama.redditslide.R.string.misc_err) + ": ") + e.getExplanation()) + "\n") + getString(me.ccrama.redditslide.R.string.misc_retry));
                        }
                    });
                }
            } catch (java.lang.Exception e) {
                e.printStackTrace();
                runOnUiThread(new java.lang.Runnable() {
                    @java.lang.Override
                    public void run() {
                        showErrorRetryDialog(getString(me.ccrama.redditslide.R.string.misc_retry));
                    }
                });
            }
            return null;
        }
    }

    private void showErrorRetryDialog(java.lang.String message) {
        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this).setTitle(me.ccrama.redditslide.R.string.err_title).setMessage(message).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, new android.content.DialogInterface.OnClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialogInterface, int i) {
                finish();
            }
        }).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialogInterface, int i) {
                ((android.support.design.widget.FloatingActionButton) (findViewById(me.ccrama.redditslide.R.id.send))).show();
            }
        }).setOnDismissListener(new android.content.DialogInterface.OnDismissListener() {
            @java.lang.Override
            public void onDismiss(android.content.DialogInterface dialog) {
                ((android.support.design.widget.FloatingActionButton) (findViewById(me.ccrama.redditslide.R.id.send))).show();
            }
        }).create().show();
    }
}