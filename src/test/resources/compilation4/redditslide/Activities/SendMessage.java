package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.util.LogUtil;
import java.util.Locale;
import me.ccrama.redditslide.Views.DoEditorActions;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Visuals.Palette;
import java.util.ArrayList;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.UserSubscriptions;
import me.ccrama.redditslide.DataShare;
/**
 * Created by ccrama on 3/5/2015.
 */
public class SendMessage extends me.ccrama.redditslide.Activities.BaseActivity {
    public static final java.lang.String EXTRA_NAME = "name";

    public static final java.lang.String EXTRA_REPLY = "reply";

    public static final java.lang.String EXTRA_MESSAGE = "message";

    public static final java.lang.String EXTRA_SUBJECT = "subject";

    public java.lang.String URL;

    private java.lang.Boolean reply;

    private net.dean.jraw.models.PrivateMessage previousMessage;

    private android.widget.EditText subject;

    private android.widget.EditText to;

    private java.lang.String bodytext;

    private java.lang.String subjecttext;

    private java.lang.String totext;

    private android.widget.EditText body;

    private java.lang.String messageSentStatus;// the String to show in the Toast for when the message is sent


    private boolean messageSent = true;// whether or not the message was sent successfully


    java.lang.String author;

    public void onCreate(android.os.Bundle savedInstanceState) {
        disableSwipeBackLayout();
        super.onCreate(savedInstanceState);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_sendmessage);
        final android.support.v7.widget.Toolbar b = ((android.support.v7.widget.Toolbar) (findViewById(me.ccrama.redditslide.R.id.toolbar)));
        final java.lang.String name;
        reply = (getIntent() != null) && getIntent().hasExtra(me.ccrama.redditslide.Activities.SendMessage.EXTRA_REPLY);
        subject = ((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.subject)));
        to = ((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.to)));
        body = ((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.body)));
        android.view.View oldMSG = findViewById(me.ccrama.redditslide.R.id.oldMSG);
        final android.widget.TextView sendingAs = ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.sendas)));
        sendingAs.setText("Sending as /u/" + me.ccrama.redditslide.Authentication.name);
        author = me.ccrama.redditslide.Authentication.name;
        sendingAs.setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                java.util.ArrayList<java.lang.String> items = new java.util.ArrayList<>();
                items.add("/u/" + me.ccrama.redditslide.Authentication.name);
                if ((me.ccrama.redditslide.UserSubscriptions.modOf != null) && (!me.ccrama.redditslide.UserSubscriptions.modOf.isEmpty()))
                    for (java.lang.String s : me.ccrama.redditslide.UserSubscriptions.modOf) {
                        items.add("/r/" + s);
                    }

                new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.SendMessage.this).title("Send message as").items(items).itemsCallback(new com.afollestad.materialdialogs.MaterialDialog.ListCallback() {
                    @java.lang.Override
                    public void onSelection(com.afollestad.materialdialogs.MaterialDialog dialog, android.view.View itemView, int which, java.lang.CharSequence text) {
                        me.ccrama.redditslide.Activities.SendMessage.this.author = ((java.lang.String) (text));
                        sendingAs.setText("Sending as " + author);
                    }
                }).negativeText(me.ccrama.redditslide.R.string.btn_cancel).onNegative(null).show();
            }
        });
        if ((getIntent() != null) && getIntent().hasExtra(me.ccrama.redditslide.Activities.SendMessage.EXTRA_NAME)) {
            name = getIntent().getExtras().getString(me.ccrama.redditslide.Activities.SendMessage.EXTRA_NAME, "");
            to.setText(name);
            to.setInputType(android.text.InputType.TYPE_NULL);
            if (reply) {
                b.setTitle(getString(me.ccrama.redditslide.R.string.mail_reply_to, name));
                previousMessage = me.ccrama.redditslide.DataShare.sharedMessage;
                if (previousMessage.getSubject() != null)
                    subject.setText(getString(me.ccrama.redditslide.R.string.mail_re, previousMessage.getSubject()));

                subject.setInputType(android.text.InputType.TYPE_NULL);
                // Disable if replying to another user, as they are already set
                to.setEnabled(false);
                subject.setEnabled(false);
                body.requestFocus();
                oldMSG.setOnClickListener(new android.view.View.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.view.View v) {
                        com.afollestad.materialdialogs.AlertDialogWrapper.Builder b = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.SendMessage.this);
                        b.setTitle(getString(me.ccrama.redditslide.R.string.mail_author_wrote, name));
                        b.setMessage(previousMessage.getBody());
                        b.create().show();
                    }
                });
            } else {
                b.setTitle(getString(me.ccrama.redditslide.R.string.mail_send_to, name));
                oldMSG.setVisibility(android.view.View.GONE);
            }
        } else {
            name = "";
            oldMSG.setVisibility(android.view.View.GONE);
            b.setTitle(me.ccrama.redditslide.R.string.mail_send);
        }
        if (getIntent().hasExtra(me.ccrama.redditslide.Activities.SendMessage.EXTRA_MESSAGE)) {
            body.setText(getIntent().getStringExtra(me.ccrama.redditslide.Activities.SendMessage.EXTRA_MESSAGE));
        }
        if (getIntent().hasExtra(me.ccrama.redditslide.Activities.SendMessage.EXTRA_SUBJECT)) {
            subject.setText(getIntent().getStringExtra(me.ccrama.redditslide.Activities.SendMessage.EXTRA_SUBJECT));
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            android.view.Window window = this.getWindow();
            window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        setupUserAppBar(me.ccrama.redditslide.R.id.toolbar, null, true, name);
        setRecentBar(b.getTitle().toString(), me.ccrama.redditslide.Visuals.Palette.getDefaultColor());
        if ((reply || (me.ccrama.redditslide.UserSubscriptions.modOf == null)) || me.ccrama.redditslide.UserSubscriptions.modOf.isEmpty()) {
            sendingAs.setVisibility(android.view.View.GONE);
        }
        findViewById(me.ccrama.redditslide.R.id.send).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View view) {
                bodytext = body.getText().toString();
                totext = to.getText().toString();
                subjecttext = subject.getText().toString();
                ((android.support.design.widget.FloatingActionButton) (findViewById(me.ccrama.redditslide.R.id.send))).hide();
                new me.ccrama.redditslide.Activities.SendMessage.AsyncDo(null, null).execute();
            }
        });
        me.ccrama.redditslide.Views.DoEditorActions.doActions(((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.body))), findViewById(me.ccrama.redditslide.R.id.area), getSupportFragmentManager(), this, previousMessage == null ? null : previousMessage.getBody(), null);
    }

    private class AsyncDo extends android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void> {
        java.lang.String tried;

        net.dean.jraw.models.Captcha captcha;

        public AsyncDo(net.dean.jraw.models.Captcha captcha, java.lang.String tried) {
            this.captcha = captcha;
            this.tried = tried;
        }

        @java.lang.Override
        protected java.lang.Void doInBackground(java.lang.Void... voids) {
            sendMessage(captcha, tried);
            return null;
        }

        public void sendMessage(net.dean.jraw.models.Captcha captcha, java.lang.String captchaAttempt) {
            if (reply) {
                try {
                    new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).reply(previousMessage, bodytext);
                } catch (net.dean.jraw.ApiException e) {
                    messageSent = false;
                    e.printStackTrace();
                }
            } else {
                try {
                    if (captcha != null)
                        new net.dean.jraw.managers.InboxManager(me.ccrama.redditslide.Authentication.reddit).compose(totext, subjecttext, bodytext, captcha, captchaAttempt);
                    else {
                        java.lang.String to = author;
                        if (to.startsWith("/r/")) {
                            to = to.substring(3, to.length());
                            new net.dean.jraw.managers.InboxManager(me.ccrama.redditslide.Authentication.reddit).compose(to, totext, subjecttext, bodytext);
                        } else {
                            new net.dean.jraw.managers.InboxManager(me.ccrama.redditslide.Authentication.reddit).compose(totext, subjecttext, bodytext);
                        }
                    }
                } catch (net.dean.jraw.ApiException e) {
                    messageSent = false;
                    e.printStackTrace();
                    // Display a Toast with an error if the user doesn't exist
                    if (e.getReason().equals("USER_DOESNT_EXIST") || e.getReason().equals("NO_USER")) {
                        messageSentStatus = getString(me.ccrama.redditslide.R.string.msg_send_user_dne);
                    } else if (e.getReason().toLowerCase(java.util.Locale.ENGLISH).contains("captcha")) {
                        messageSentStatus = getString(me.ccrama.redditslide.R.string.misc_captcha_incorrect);
                    }
                    // todo show captcha
                }
            }
        }

        @java.lang.Override
        public void onPostExecute(java.lang.Void voids) {
            // If the error wasn't that the user doesn't exist, show a generic failure message
            if (messageSentStatus == null) {
                messageSentStatus = getString(me.ccrama.redditslide.R.string.msg_sent_failure);
                ((android.support.design.widget.FloatingActionButton) (findViewById(me.ccrama.redditslide.R.id.send))).show();
            }
            final java.lang.String MESSAGE_SENT = (messageSent) ? getString(me.ccrama.redditslide.R.string.msg_sent_success) : messageSentStatus;
            android.widget.Toast.makeText(me.ccrama.redditslide.Activities.SendMessage.this, MESSAGE_SENT, android.widget.Toast.LENGTH_SHORT).show();
            // Only finish() this Activity if the message sent successfully
            if (messageSent) {
                finish();
            } else {
                ((android.support.design.widget.FloatingActionButton) (findViewById(me.ccrama.redditslide.R.id.send))).show();
                messageSent = true;
            }
        }
    }
}