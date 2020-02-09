package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.Views.DoEditorActions;
import java.io.Closeable;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import me.ccrama.redditslide.util.LogUtil;
import java.net.URL;
import me.ccrama.redditslide.SpoilerRobotoTextView;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.SecretConstants;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Views.CommentOverflow;
import me.ccrama.redditslide.UserSubscriptions;
import me.ccrama.redditslide.util.TitleExtractor;
import java.util.List;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import me.ccrama.redditslide.util.SubmissionParser;
import java.io.IOException;
import me.ccrama.redditslide.Drafts;
import java.io.FileOutputStream;
import me.ccrama.redditslide.Constants;
import me.ccrama.redditslide.Authentication;
import java.io.File;
import me.ccrama.redditslide.Views.ImageInsertEditText;
import me.ccrama.redditslide.OpenRedditLink;
/**
 * Created by ccrama on 3/5/2015.
 */
public class Submit extends me.ccrama.redditslide.Activities.BaseActivity {
    private boolean sent;

    private java.lang.String trying;

    private java.lang.String URL;

    private android.support.v7.widget.SwitchCompat inboxReplies;

    private android.view.View image;

    private android.view.View link;

    private android.view.View self;

    public static final java.lang.String EXTRA_SUBREDDIT = "subreddit";

    public static final java.lang.String EXTRA_BODY = "body";

    public static final java.lang.String EXTRA_IS_SELF = "is_self";

    android.os.AsyncTask<java.lang.Void, java.lang.Void, net.dean.jraw.models.Subreddit> tchange;

    @java.lang.Override
    public void onDestroy() {
        super.onDestroy();
        try {
            java.lang.String text = ((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.bodytext))).getText().toString();
            if ((!text.isEmpty()) && sent) {
                me.ccrama.redditslide.Drafts.addDraft(text);
                android.widget.Toast.makeText(getApplicationContext(), me.ccrama.redditslide.R.string.msg_save_draft, android.widget.Toast.LENGTH_LONG).show();
            }
        } catch (java.lang.Exception e) {
        }
    }

    public void onCreate(android.os.Bundle savedInstanceState) {
        disableSwipeBackLayout();
        super.onCreate(savedInstanceState);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_submit);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            android.view.Window window = this.getWindow();
            window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, me.ccrama.redditslide.R.string.title_submit_post, true, true);
        inboxReplies = ((android.support.v7.widget.SwitchCompat) (findViewById(me.ccrama.redditslide.R.id.replies)));
        android.content.Intent intent = getIntent();
        final java.lang.String subreddit = intent.getStringExtra(me.ccrama.redditslide.Activities.Submit.EXTRA_SUBREDDIT);
        final java.lang.String initialBody = intent.getStringExtra(me.ccrama.redditslide.Activities.Submit.EXTRA_BODY);
        self = findViewById(me.ccrama.redditslide.R.id.selftext);
        final android.widget.AutoCompleteTextView subredditText = ((android.widget.AutoCompleteTextView) (findViewById(me.ccrama.redditslide.R.id.subreddittext)));
        image = findViewById(me.ccrama.redditslide.R.id.image);
        link = findViewById(me.ccrama.redditslide.R.id.url);
        image.setVisibility(android.view.View.GONE);
        link.setVisibility(android.view.View.GONE);
        if (((((((subreddit != null) && (!subreddit.equals("frontpage"))) && (!subreddit.equals("all"))) && (!subreddit.equals("friends"))) && (!subreddit.equals("mod"))) && (!subreddit.contains("/m/"))) && (!subreddit.contains("+"))) {
            subredditText.setText(subreddit);
        }
        if (initialBody != null) {
            ((me.ccrama.redditslide.Views.ImageInsertEditText) (self.findViewById(me.ccrama.redditslide.R.id.bodytext))).setText(initialBody);
        }
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
                                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.Submit.this).setTitle(me.ccrama.redditslide.R.string.err_submit_restricted).setMessage(me.ccrama.redditslide.R.string.err_submit_restricted_text).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, null).show();
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
        findViewById(me.ccrama.redditslide.R.id.selftextradio).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View view) {
                self.setVisibility(android.view.View.VISIBLE);
                image.setVisibility(android.view.View.GONE);
                link.setVisibility(android.view.View.GONE);
            }
        });
        findViewById(me.ccrama.redditslide.R.id.imageradio).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View view) {
                self.setVisibility(android.view.View.GONE);
                image.setVisibility(android.view.View.VISIBLE);
                link.setVisibility(android.view.View.GONE);
            }
        });
        findViewById(me.ccrama.redditslide.R.id.linkradio).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View view) {
                self.setVisibility(android.view.View.GONE);
                image.setVisibility(android.view.View.GONE);
                link.setVisibility(android.view.View.VISIBLE);
            }
        });
        findViewById(me.ccrama.redditslide.R.id.suggest).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                new android.os.AsyncTask<java.lang.String, java.lang.Void, java.lang.String>() {
                    android.app.Dialog d;

                    @java.lang.Override
                    protected java.lang.String doInBackground(java.lang.String... params) {
                        try {
                            return me.ccrama.redditslide.util.TitleExtractor.getPageTitle(params[0]);
                        } catch (java.lang.Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @java.lang.Override
                    protected void onPreExecute() {
                        d = new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.Submit.this).progress(true, 100).title(me.ccrama.redditslide.R.string.editor_finding_title).content(me.ccrama.redditslide.R.string.misc_please_wait).show();
                    }

                    @java.lang.Override
                    protected void onPostExecute(java.lang.String s) {
                        if (s != null) {
                            ((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.titletext))).setText(s);
                            d.dismiss();
                        } else {
                            d.dismiss();
                            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.Submit.this).setTitle(me.ccrama.redditslide.R.string.title_not_found).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, null).show();
                        }
                    }
                }.execute(((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.urltext))).getText().toString());
            }
        });
        findViewById(me.ccrama.redditslide.R.id.selImage).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View view) {
                gun0912.tedbottompicker.TedBottomPicker tedBottomPicker = new gun0912.tedbottompicker.TedBottomPicker.Builder(me.ccrama.redditslide.Activities.Submit.this).setOnImageSelectedListener(new gun0912.tedbottompicker.TedBottomPicker.OnImageSelectedListener() {
                    @java.lang.Override
                    public void onImageSelected(java.util.List<android.net.Uri> uri) {
                        handleImageIntent(uri);
                    }
                }).setLayoutResource(me.ccrama.redditslide.R.layout.image_sheet_dialog).setTitle("Choose a photo").create();
                tedBottomPicker.show(getSupportFragmentManager());
                android.view.inputmethod.InputMethodManager imm = ((android.view.inputmethod.InputMethodManager) (getSystemService(android.content.Context.INPUT_METHOD_SERVICE)));
                imm.hideSoftInputFromWindow(findViewById(me.ccrama.redditslide.R.id.bodytext).getWindowToken(), 0);
            }
        });
        me.ccrama.redditslide.Views.DoEditorActions.doActions(((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.bodytext))), findViewById(me.ccrama.redditslide.R.id.selftext), getSupportFragmentManager(), this, null, null);
        if ((intent.hasExtra(android.content.Intent.EXTRA_TEXT) && (!intent.getExtras().getString(android.content.Intent.EXTRA_TEXT, "").isEmpty())) && (!intent.getBooleanExtra(me.ccrama.redditslide.Activities.Submit.EXTRA_IS_SELF, false))) {
            java.lang.String data = intent.getStringExtra(android.content.Intent.EXTRA_TEXT);
            if (data.contains("\n")) {
                ((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.titletext))).setText(data.substring(0, data.indexOf("\n")));
                ((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.urltext))).setText(data.substring(data.indexOf("\n"), data.length()));
            } else {
                ((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.urltext))).setText(data);
            }
            self.setVisibility(android.view.View.GONE);
            image.setVisibility(android.view.View.GONE);
            link.setVisibility(android.view.View.VISIBLE);
            ((android.widget.RadioButton) (findViewById(me.ccrama.redditslide.R.id.linkradio))).setChecked(true);
        } else if (intent.hasExtra(android.content.Intent.EXTRA_STREAM)) {
            final android.net.Uri imageUri = intent.getParcelableExtra(android.content.Intent.EXTRA_STREAM);
            if (imageUri != null) {
                handleImageIntent(new java.util.ArrayList<android.net.Uri>() {
                    {
                        add(imageUri);
                    }
                });
                self.setVisibility(android.view.View.GONE);
                image.setVisibility(android.view.View.VISIBLE);
                link.setVisibility(android.view.View.GONE);
                ((android.widget.RadioButton) (findViewById(me.ccrama.redditslide.R.id.imageradio))).setChecked(true);
            }
        }
        if (intent.hasExtra(android.content.Intent.EXTRA_SUBJECT) && (!intent.getExtras().getString(android.content.Intent.EXTRA_SUBJECT, "").isEmpty())) {
            java.lang.String data = intent.getStringExtra(android.content.Intent.EXTRA_SUBJECT);
            ((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.titletext))).setText(data);
        }
        findViewById(me.ccrama.redditslide.R.id.send).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View view) {
                ((android.support.design.widget.FloatingActionButton) (findViewById(me.ccrama.redditslide.R.id.send))).hide();
                new me.ccrama.redditslide.Activities.Submit.AsyncDo().execute();
            }
        });
    }

    private void setImage(final java.lang.String URL) {
        this.URL = URL;
        runOnUiThread(new java.lang.Runnable() {
            @java.lang.Override
            public void run() {
                findViewById(me.ccrama.redditslide.R.id.imagepost).setVisibility(android.view.View.VISIBLE);
                ((me.ccrama.redditslide.Reddit) (getApplication())).getImageLoader().displayImage(URL, ((android.widget.ImageView) (findViewById(me.ccrama.redditslide.R.id.imagepost))));
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

    public void handleImageIntent(java.util.List<android.net.Uri> uris) {
        if (uris.size() == 1) {
            // Get the Image from data (single image)
            try {
                new me.ccrama.redditslide.Activities.Submit.UploadImgur(this, uris.get(0));
            } catch (java.lang.Exception e) {
                e.printStackTrace();
            }
        } else {
            // Multiple images
            try {
                new me.ccrama.redditslide.Activities.Submit.UploadImgurAlbum(this, uris.toArray(new android.net.Uri[uris.size()]));
            } catch (java.lang.Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class AsyncDo extends android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void> {
        @java.lang.Override
        protected java.lang.Void doInBackground(java.lang.Void... voids) {
            try {
                if (self.getVisibility() == android.view.View.VISIBLE) {
                    final java.lang.String text = ((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.bodytext))).getText().toString();
                    try {
                        net.dean.jraw.models.Submission s = new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).submit(new net.dean.jraw.managers.AccountManager.SubmissionBuilder(((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.bodytext))).getText().toString(), ((android.widget.AutoCompleteTextView) (findViewById(me.ccrama.redditslide.R.id.subreddittext))).getText().toString(), ((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.titletext))).getText().toString()));
                        new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).sendRepliesToInbox(s, inboxReplies.isChecked());
                        new me.ccrama.redditslide.OpenRedditLink(me.ccrama.redditslide.Activities.Submit.this, (("reddit.com/r/" + ((android.widget.AutoCompleteTextView) (findViewById(me.ccrama.redditslide.R.id.subreddittext))).getText().toString()) + "/comments/") + s.getFullName().substring(3, s.getFullName().length()));
                        me.ccrama.redditslide.Activities.Submit.this.finish();
                    } catch (final net.dean.jraw.ApiException e) {
                        me.ccrama.redditslide.Drafts.addDraft(text);
                        e.printStackTrace();
                        runOnUiThread(new java.lang.Runnable() {
                            @java.lang.Override
                            public void run() {
                                showErrorRetryDialog((((getString(me.ccrama.redditslide.R.string.misc_err) + ": ") + e.getExplanation()) + "\n") + getString(me.ccrama.redditslide.R.string.misc_retry_draft));
                            }
                        });
                    }
                } else if (link.getVisibility() == android.view.View.VISIBLE) {
                    try {
                        net.dean.jraw.models.Submission s = new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).submit(new net.dean.jraw.managers.AccountManager.SubmissionBuilder(new java.net.URL(((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.urltext))).getText().toString()), ((android.widget.AutoCompleteTextView) (findViewById(me.ccrama.redditslide.R.id.subreddittext))).getText().toString(), ((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.titletext))).getText().toString()));
                        new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).sendRepliesToInbox(s, inboxReplies.isChecked());
                        new me.ccrama.redditslide.OpenRedditLink(me.ccrama.redditslide.Activities.Submit.this, (("reddit.com/r/" + ((android.widget.AutoCompleteTextView) (findViewById(me.ccrama.redditslide.R.id.subreddittext))).getText().toString()) + "/comments/") + s.getFullName().substring(3, s.getFullName().length()));
                        me.ccrama.redditslide.Activities.Submit.this.finish();
                    } catch (final net.dean.jraw.ApiException e) {
                        e.printStackTrace();
                        runOnUiThread(new java.lang.Runnable() {
                            @java.lang.Override
                            public void run() {
                                if (e instanceof net.dean.jraw.ApiException) {
                                    showErrorRetryDialog((((getString(me.ccrama.redditslide.R.string.misc_err) + ": ") + e.getExplanation()) + "\n") + getString(me.ccrama.redditslide.R.string.misc_retry));
                                } else {
                                    showErrorRetryDialog((((getString(me.ccrama.redditslide.R.string.misc_err) + ": ") + getString(me.ccrama.redditslide.R.string.err_invalid_url)) + "\n") + getString(me.ccrama.redditslide.R.string.misc_retry));
                                }
                            }
                        });
                    }
                } else if (image.getVisibility() == android.view.View.VISIBLE) {
                    try {
                        net.dean.jraw.models.Submission s = new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).submit(new net.dean.jraw.managers.AccountManager.SubmissionBuilder(new java.net.URL(URL), ((android.widget.AutoCompleteTextView) (findViewById(me.ccrama.redditslide.R.id.subreddittext))).getText().toString(), ((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.titletext))).getText().toString()));
                        new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).sendRepliesToInbox(s, inboxReplies.isChecked());
                        new me.ccrama.redditslide.OpenRedditLink(me.ccrama.redditslide.Activities.Submit.this, (("reddit.com/r/" + ((android.widget.AutoCompleteTextView) (findViewById(me.ccrama.redditslide.R.id.subreddittext))).getText().toString()) + "/comments/") + s.getFullName().substring(3, s.getFullName().length()));
                        me.ccrama.redditslide.Activities.Submit.this.finish();
                    } catch (final java.lang.Exception e) {
                        runOnUiThread(new java.lang.Runnable() {
                            @java.lang.Override
                            public void run() {
                                if (e instanceof net.dean.jraw.ApiException) {
                                    showErrorRetryDialog((((getString(me.ccrama.redditslide.R.string.misc_err) + ": ") + ((net.dean.jraw.ApiException) (e)).getExplanation()) + "\n") + getString(me.ccrama.redditslide.R.string.misc_retry));
                                } else {
                                    showErrorRetryDialog((((getString(me.ccrama.redditslide.R.string.misc_err) + ": ") + getString(me.ccrama.redditslide.R.string.err_invalid_url)) + "\n") + getString(me.ccrama.redditslide.R.string.misc_retry));
                                }
                            }
                        });
                    }
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

    private class UploadImgur extends android.os.AsyncTask<android.net.Uri, java.lang.Integer, org.json.JSONObject> {
        final android.content.Context c;

        private final com.afollestad.materialdialogs.MaterialDialog dialog;

        private final android.net.Uri uri;

        public android.graphics.Bitmap b;

        public UploadImgur(android.content.Context c, android.net.Uri u) {
            this.c = c;
            this.uri = u;
            dialog = new com.afollestad.materialdialogs.MaterialDialog.Builder(c).title(c.getString(me.ccrama.redditslide.R.string.editor_uploading_image)).progress(false, 100).cancelable(false).autoDismiss(false).build();
            new com.afollestad.materialdialogs.MaterialDialog.Builder(c).title(c.getString(me.ccrama.redditslide.R.string.editor_upload_image_question)).cancelable(false).autoDismiss(false).positiveText(c.getString(me.ccrama.redditslide.R.string.btn_upload)).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                @java.lang.Override
                public void onClick(com.afollestad.materialdialogs.MaterialDialog d, com.afollestad.materialdialogs.DialogAction w) {
                    d.dismiss();
                    dialog.show();
                    execute(uri);
                }
            }).negativeText(c.getString(me.ccrama.redditslide.R.string.btn_cancel)).onNegative(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                @java.lang.Override
                public void onClick(com.afollestad.materialdialogs.MaterialDialog d, com.afollestad.materialdialogs.DialogAction w) {
                    d.dismiss();
                }
            }).show();
        }

        // Following methods sourced from https://github.com/Kennyc1012/Opengur, Code by Kenny Campagna
        public java.io.File createFile(android.net.Uri uri, @android.support.annotation.NonNull
        android.content.Context context) {
            java.io.InputStream in;
            android.content.ContentResolver resolver = context.getContentResolver();
            java.lang.String type = resolver.getType(uri);
            java.lang.String extension;
            if ("image/png".equals(type)) {
                extension = ".gif";
            } else if ("image/png".equals(type)) {
                extension = ".png";
            } else {
                extension = ".jpg";
            }
            try {
                in = resolver.openInputStream(uri);
            } catch (java.io.FileNotFoundException e) {
                return null;
            }
            // Create files from a uri in our cache directory so they eventually get deleted
            java.lang.String timeStamp = java.lang.String.valueOf(java.lang.System.currentTimeMillis());
            java.io.File cacheDir = ((me.ccrama.redditslide.Reddit) (context.getApplicationContext())).getImageLoader().getDiskCache().getDirectory();
            java.io.File tempFile = new java.io.File(cacheDir, timeStamp + extension);
            if (writeInputStreamToFile(in, tempFile)) {
                return tempFile;
            } else {
                // If writeInputStreamToFile fails, delete the excess file
                tempFile.delete();
            }
            return null;
        }

        public boolean writeInputStreamToFile(@android.support.annotation.NonNull
        java.io.InputStream in, @android.support.annotation.NonNull
        java.io.File file) {
            java.io.BufferedOutputStream buffer = null;
            boolean didFinish = false;
            try {
                buffer = new java.io.BufferedOutputStream(new java.io.FileOutputStream(file));
                byte[] byt = new byte[1024];
                int i;
                for (long l = 0L; (i = in.read(byt)) != (-1); l += i) {
                    buffer.write(byt, 0, i);
                }
                buffer.flush();
                didFinish = true;
            } catch (java.io.IOException e) {
                didFinish = false;
            } finally {
                closeStream(in);
                closeStream(buffer);
            }
            return didFinish;
        }

        public void closeStream(@android.support.annotation.Nullable
        java.io.Closeable closeable) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (java.io.IOException ex) {
                }
            }
        }

        // End methods sourced from Opengur
        @java.lang.Override
        protected org.json.JSONObject doInBackground(android.net.Uri... sub) {
            java.io.File bitmap = createFile(sub[0], c);
            final okhttp3.OkHttpClient client = me.ccrama.redditslide.Reddit.client;
            try {
                okhttp3.RequestBody formBody = new okhttp3.MultipartBody.Builder().setType(okhttp3.MultipartBody.FORM).addFormDataPart("image", bitmap.getName(), okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/*"), bitmap)).build();
                me.ccrama.redditslide.Views.DoEditorActions.ProgressRequestBody body = new me.ccrama.redditslide.Views.DoEditorActions.ProgressRequestBody(formBody, new me.ccrama.redditslide.Views.DoEditorActions.ProgressRequestBody.Listener() {
                    @java.lang.Override
                    public void onProgress(int progress) {
                        publishProgress(progress);
                    }
                });
                okhttp3.Request request = new okhttp3.Request.Builder().header("Authorization", "Client-ID " + me.ccrama.redditslide.Constants.IMGUR_MASHAPE_CLIENT_ID).header("X-Mashape-Key", me.ccrama.redditslide.SecretConstants.getImgurApiKey(c)).url("https://imgur-apiv3.p.mashape.com/3/image").post(body).build();
                okhttp3.Response response = client.newCall(request).execute();
                if (!response.isSuccessful())
                    throw new java.io.IOException("Unexpected code " + response);

                return new org.json.JSONObject(response.body().string());
            } catch (java.lang.Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @java.lang.Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @java.lang.Override
        protected void onPostExecute(final org.json.JSONObject result) {
            dialog.dismiss();
            try {
                final java.lang.String url = result.getJSONObject("data").getString("link");
                setImage(url);
            } catch (java.lang.Exception e) {
                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(c).setTitle(me.ccrama.redditslide.R.string.err_title).setMessage(me.ccrama.redditslide.R.string.editor_err_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                    }
                }).show();
                e.printStackTrace();
            }
        }

        @java.lang.Override
        protected void onProgressUpdate(java.lang.Integer... values) {
            dialog.setProgress(values[0]);
            me.ccrama.redditslide.util.LogUtil.v("Progress:" + values[0]);
        }
    }

    private class UploadImgurAlbum extends android.os.AsyncTask<android.net.Uri, java.lang.Integer, java.lang.String> {
        final android.content.Context c;

        private final com.afollestad.materialdialogs.MaterialDialog dialog;

        private final android.net.Uri[] uris;

        public android.graphics.Bitmap b;

        public UploadImgurAlbum(android.content.Context c, android.net.Uri... u) {
            this.c = c;
            this.uris = u;
            dialog = new com.afollestad.materialdialogs.MaterialDialog.Builder(c).title(c.getString(me.ccrama.redditslide.R.string.editor_uploading_image)).progress(false, 100).cancelable(false).build();
            new com.afollestad.materialdialogs.MaterialDialog.Builder(c).title(c.getString(me.ccrama.redditslide.R.string.editor_upload_image_question)).cancelable(false).autoDismiss(false).positiveText(c.getString(me.ccrama.redditslide.R.string.btn_upload)).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                @java.lang.Override
                public void onClick(com.afollestad.materialdialogs.MaterialDialog d, com.afollestad.materialdialogs.DialogAction w) {
                    d.dismiss();
                    dialog.show();
                    execute(uris);
                }
            }).negativeText(c.getString(me.ccrama.redditslide.R.string.btn_cancel)).onNegative(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                @java.lang.Override
                public void onClick(com.afollestad.materialdialogs.MaterialDialog d, com.afollestad.materialdialogs.DialogAction w) {
                    d.dismiss();
                }
            }).show();
        }

        // Following methods sourced from https://github.com/Kennyc1012/Opengur, Code by Kenny Campagna
        public java.io.File createFile(android.net.Uri uri, @android.support.annotation.NonNull
        android.content.Context context) {
            java.io.InputStream in;
            android.content.ContentResolver resolver = context.getContentResolver();
            java.lang.String type = resolver.getType(uri);
            java.lang.String extension;
            if ("image/png".equals(type)) {
                extension = ".gif";
            } else if ("image/png".equals(type)) {
                extension = ".png";
            } else {
                extension = ".jpg";
            }
            try {
                in = resolver.openInputStream(uri);
            } catch (java.io.FileNotFoundException e) {
                return null;
            }
            // Create files from a uri in our cache directory so they eventually get deleted
            java.lang.String timeStamp = java.lang.String.valueOf(java.lang.System.currentTimeMillis());
            java.io.File cacheDir = ((me.ccrama.redditslide.Reddit) (context.getApplicationContext())).getImageLoader().getDiskCache().getDirectory();
            java.io.File tempFile = new java.io.File(cacheDir, timeStamp + extension);
            if (writeInputStreamToFile(in, tempFile)) {
                return tempFile;
            } else {
                // If writeInputStreamToFile fails, delete the excess file
                tempFile.delete();
            }
            return null;
        }

        public boolean writeInputStreamToFile(@android.support.annotation.NonNull
        java.io.InputStream in, @android.support.annotation.NonNull
        java.io.File file) {
            java.io.BufferedOutputStream buffer = null;
            boolean didFinish = false;
            try {
                buffer = new java.io.BufferedOutputStream(new java.io.FileOutputStream(file));
                byte[] byt = new byte[1024];
                int i;
                for (long l = 0L; (i = in.read(byt)) != (-1); l += i) {
                    buffer.write(byt, 0, i);
                }
                buffer.flush();
                didFinish = true;
            } catch (java.io.IOException e) {
                didFinish = false;
            } finally {
                closeStream(in);
                closeStream(buffer);
            }
            return didFinish;
        }

        public void closeStream(@android.support.annotation.Nullable
        java.io.Closeable closeable) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (java.io.IOException ex) {
                }
            }
        }

        // End methods sourced from Opengur
        java.lang.String finalUrl;

        @java.lang.Override
        protected java.lang.String doInBackground(android.net.Uri... sub) {
            totalCount = sub.length;
            final okhttp3.OkHttpClient client = me.ccrama.redditslide.Reddit.client;
            java.lang.String albumurl;
            {
                okhttp3.Request request = new okhttp3.Request.Builder().header("Authorization", "Client-ID " + me.ccrama.redditslide.Constants.IMGUR_MASHAPE_CLIENT_ID).header("X-Mashape-Key", me.ccrama.redditslide.SecretConstants.getImgurApiKey(c)).url("https://imgur-apiv3.p.mashape.com/3/album").post(new okhttp3.RequestBody() {
                    @java.lang.Override
                    public okhttp3.MediaType contentType() {
                        return null;
                    }

                    @java.lang.Override
                    public void writeTo(okio.BufferedSink sink) throws java.io.IOException {
                    }
                }).build();
                okhttp3.Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if (!response.isSuccessful()) {
                        throw new java.io.IOException("Unexpected code " + response);
                    }
                    org.json.JSONObject album = new org.json.JSONObject(response.body().string());
                    albumurl = album.getJSONObject("data").getString("deletehash");
                    finalUrl = "http://imgur.com/a/" + album.getJSONObject("data").getString("id");
                } catch (java.lang.Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            try {
                okhttp3.MultipartBody.Builder formBodyBuilder = new okhttp3.MultipartBody.Builder().setType(okhttp3.MultipartBody.FORM);
                for (android.net.Uri uri : sub) {
                    java.io.File bitmap = createFile(uri, c);
                    formBodyBuilder.addFormDataPart("image", bitmap.getName(), okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/*"), bitmap));
                    formBodyBuilder.addFormDataPart("album", albumurl);
                    okhttp3.MultipartBody formBody = formBodyBuilder.build();
                    me.ccrama.redditslide.Views.DoEditorActions.ProgressRequestBody body = new me.ccrama.redditslide.Views.DoEditorActions.ProgressRequestBody(formBody, new me.ccrama.redditslide.Views.DoEditorActions.ProgressRequestBody.Listener() {
                        @java.lang.Override
                        public void onProgress(int progress) {
                            publishProgress(progress);
                        }
                    });
                    okhttp3.Request request = new okhttp3.Request.Builder().header("Authorization", "Client-ID " + me.ccrama.redditslide.Constants.IMGUR_MASHAPE_CLIENT_ID).header("X-Mashape-Key", me.ccrama.redditslide.SecretConstants.getImgurApiKey(c)).url("https://imgur-apiv3.p.mashape.com/3/image").post(body).build();
                    okhttp3.Response response = client.newCall(request).execute();
                    if (!response.isSuccessful()) {
                        throw new java.io.IOException("Unexpected code " + response);
                    }
                }
            } catch (java.lang.Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @java.lang.Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @java.lang.Override
        protected void onPostExecute(final java.lang.String result) {
            dialog.dismiss();
            try {
                ((android.widget.RadioButton) (findViewById(me.ccrama.redditslide.R.id.linkradio))).setChecked(true);
                link.setVisibility(android.view.View.VISIBLE);
                ((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.urltext))).setText(finalUrl);
            } catch (java.lang.Exception e) {
                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(c).setTitle(me.ccrama.redditslide.R.string.err_title).setMessage(me.ccrama.redditslide.R.string.editor_err_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                    }
                }).show();
                e.printStackTrace();
            }
        }

        int uploadCount;

        int totalCount;

        @java.lang.Override
        protected void onProgressUpdate(java.lang.Integer... values) {
            int progress = values[0];
            if ((progress < dialog.getCurrentProgress()) || (uploadCount == 0)) {
                uploadCount += 1;
            }
            dialog.setContent((("Image " + uploadCount) + "/") + totalCount);
            dialog.setProgress(progress);
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