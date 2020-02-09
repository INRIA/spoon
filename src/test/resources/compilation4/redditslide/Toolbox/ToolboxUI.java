package me.ccrama.redditslide.Toolbox;
import java.net.MalformedURLException;
import java.util.ArrayList;
import me.ccrama.redditslide.SettingValues;
import java.util.Date;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.text.SimpleDateFormat;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Authentication;
import me.ccrama.redditslide.Views.RoundedBackgroundSpan;
import java.util.List;
import me.ccrama.redditslide.Activities.Reauthenticate;
import java.util.Map;
import me.ccrama.redditslide.OpenRedditLink;
/**
 * Misc UI stuff for toolbox - usernote display, removal display, etc.
 */
public class ToolboxUI {
    /**
     * Shows a removal reason dialog
     *
     * @param context
     * 		Context
     * @param thing
     * 		Submission or Comment being removed
     */
    public static void showRemoval(final android.content.Context context, final net.dean.jraw.models.PublicContribution thing, final me.ccrama.redditslide.Toolbox.ToolboxUI.CompletedRemovalCallback callback) {
        final me.ccrama.redditslide.Toolbox.RemovalReasons removalReasons;
        final com.afollestad.materialdialogs.MaterialDialog.Builder builder = new com.afollestad.materialdialogs.MaterialDialog.Builder(context);
        // Set the dialog title
        if (thing instanceof net.dean.jraw.models.Comment) {
            builder.title(context.getResources().getString(me.ccrama.redditslide.R.string.toolbox_removal_title, ((net.dean.jraw.models.Comment) (thing)).getSubredditName()));
            removalReasons = me.ccrama.redditslide.Toolbox.Toolbox.getConfig(((net.dean.jraw.models.Comment) (thing)).getSubredditName()).getRemovalReasons();
        } else if (thing instanceof net.dean.jraw.models.Submission) {
            builder.title(context.getResources().getString(me.ccrama.redditslide.R.string.toolbox_removal_title, ((net.dean.jraw.models.Submission) (thing)).getSubredditName()));
            removalReasons = me.ccrama.redditslide.Toolbox.Toolbox.getConfig(((net.dean.jraw.models.Submission) (thing)).getSubredditName()).getRemovalReasons();
        } else {
            return;
        }
        android.view.LayoutInflater inflater = ((android.view.LayoutInflater) (context.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE)));
        final android.view.View dialogContent = inflater.inflate(me.ccrama.redditslide.R.layout.toolbox_removal_dialog, null);
        final android.widget.CheckBox headerToggle = dialogContent.findViewById(me.ccrama.redditslide.R.id.toolbox_header_toggle);
        final android.widget.TextView headerText = dialogContent.findViewById(me.ccrama.redditslide.R.id.toolbox_header_text);
        final android.widget.LinearLayout reasonsList = dialogContent.findViewById(me.ccrama.redditslide.R.id.toolbox_reasons_list);
        final android.widget.CheckBox footerToggle = dialogContent.findViewById(me.ccrama.redditslide.R.id.toolbox_footer_toggle);
        final android.widget.TextView footerText = dialogContent.findViewById(me.ccrama.redditslide.R.id.toolbox_footer_text);
        final android.widget.RadioGroup actions = dialogContent.findViewById(me.ccrama.redditslide.R.id.toolbox_action);
        final android.widget.CheckBox actionSticky = dialogContent.findViewById(me.ccrama.redditslide.R.id.sticky_comment);
        final android.widget.CheckBox actionModmail = dialogContent.findViewById(me.ccrama.redditslide.R.id.pm_modmail);
        final android.widget.CheckBox actionLock = dialogContent.findViewById(me.ccrama.redditslide.R.id.lock);
        final android.widget.EditText logReason = dialogContent.findViewById(me.ccrama.redditslide.R.id.toolbox_log_reason);
        // Check if removal should be logged and set related views
        final boolean log = !removalReasons.getLogSub().isEmpty();
        if (log) {
            dialogContent.findViewById(me.ccrama.redditslide.R.id.none).setVisibility(android.view.View.VISIBLE);
            if (removalReasons.getLogTitle().contains("{reason}")) {
                logReason.setVisibility(android.view.View.VISIBLE);
                logReason.setText(removalReasons.getLogReason());
            }
        }
        // Hide lock option if removing a comment
        if (thing instanceof net.dean.jraw.models.Comment) {
            actionLock.setVisibility(android.view.View.GONE);
        }
        // Set up the header and footer options
        headerText.setText(me.ccrama.redditslide.Toolbox.ToolboxUI.replaceTokens(removalReasons.getHeader(), thing));
        if (removalReasons.getHeader().isEmpty()) {
            ((android.view.View) (headerToggle.getParent())).setVisibility(android.view.View.GONE);
        }
        footerText.setText(me.ccrama.redditslide.Toolbox.ToolboxUI.replaceTokens(removalReasons.getFooter(), thing));
        if (removalReasons.getFooter().isEmpty()) {
            ((android.view.View) (footerToggle.getParent())).setVisibility(android.view.View.GONE);
        }
        // Set up the removal reason list
        for (me.ccrama.redditslide.Toolbox.RemovalReasons.RemovalReason reason : removalReasons.getReasons()) {
            android.widget.CheckBox checkBox = new android.widget.CheckBox(context);
            checkBox.setMaxLines(2);
            checkBox.setEllipsize(android.text.TextUtils.TruncateAt.END);
            final android.util.TypedValue tv = new android.util.TypedValue();
            final boolean found = context.getTheme().resolveAttribute(me.ccrama.redditslide.R.attr.fontColor, tv, true);
            checkBox.setTextColor(found ? tv.data : android.graphics.Color.WHITE);
            checkBox.setText(reason.getTitle().isEmpty() ? reason.getText() : reason.getTitle());
            reasonsList.addView(checkBox);
        }
        // Set default states of checkboxes/radiobuttons
        if (me.ccrama.redditslide.SettingValues.toolboxMessageType == me.ccrama.redditslide.SettingValues.ToolboxRemovalMessageType.COMMENT.ordinal()) {
            ((android.widget.RadioButton) (actions.findViewById(me.ccrama.redditslide.R.id.comment))).setChecked(true);
        } else if (me.ccrama.redditslide.SettingValues.toolboxMessageType == me.ccrama.redditslide.SettingValues.ToolboxRemovalMessageType.PM.ordinal()) {
            ((android.widget.RadioButton) (actions.findViewById(me.ccrama.redditslide.R.id.pm))).setChecked(true);
        } else if (me.ccrama.redditslide.SettingValues.toolboxMessageType == me.ccrama.redditslide.SettingValues.ToolboxRemovalMessageType.BOTH.ordinal()) {
            ((android.widget.RadioButton) (actions.findViewById(me.ccrama.redditslide.R.id.both))).setChecked(true);
        } else {
            ((android.widget.RadioButton) (actions.findViewById(me.ccrama.redditslide.R.id.none))).setChecked(true);
        }
        actionSticky.setChecked(me.ccrama.redditslide.SettingValues.toolboxSticky);
        actionModmail.setChecked(me.ccrama.redditslide.SettingValues.toolboxModmail);
        actionLock.setChecked(me.ccrama.redditslide.SettingValues.toolboxLock);
        // Set up dialog buttons
        builder.customView(dialogContent, false);
        builder.positiveText(me.ccrama.redditslide.R.string.mod_btn_remove);
        builder.negativeText(me.ccrama.redditslide.R.string.btn_cancel);
        builder.onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
            @java.lang.Override
            public void onClick(@android.support.annotation.NonNull
            com.afollestad.materialdialogs.MaterialDialog dialog, @android.support.annotation.NonNull
            com.afollestad.materialdialogs.DialogAction which) {
                java.lang.StringBuilder removalString = new java.lang.StringBuilder();
                java.lang.StringBuilder flairText = new java.lang.StringBuilder();
                java.lang.StringBuilder flairCSS = new java.lang.StringBuilder();
                // Add the header to the removal message
                if (headerToggle.isChecked()) {
                    removalString.append(removalReasons.getHeader());
                    removalString.append("\n\n");
                }
                // Add the removal reasons
                for (int i = 0; i < reasonsList.getChildCount(); i++) {
                    if (((android.widget.CheckBox) (reasonsList.getChildAt(i))).isChecked()) {
                        removalString.append(removalReasons.getReasons().get(i).getText());
                        removalString.append("\n\n");
                        flairText.append(flairText.length() > 0 ? " " : "");
                        flairText.append(removalReasons.getReasons().get(i).getFlairText());
                        flairCSS.append(flairCSS.length() > 0 ? " " : "");
                        flairCSS.append(removalReasons.getReasons().get(i).getFlairCSS());
                    }
                }
                // Add the footer
                if (footerToggle.isChecked()) {
                    removalString.append(removalReasons.getFooter());
                }
                // Add PM footer
                if ((actions.getCheckedRadioButtonId() == me.ccrama.redditslide.R.id.pm) || (actions.getCheckedRadioButtonId() == me.ccrama.redditslide.R.id.both)) {
                    removalString.append("\n\n---\n[[Link to your {kind}]({url})]");
                }
                // Remove the item and send the message if desired
                // thing
                // action ID
                // removal reason
                // removal PM subject
                // modmail?
                // sticky?
                // lock?
                // log the removal?
                // log sub
                // flair text and css
                new me.ccrama.redditslide.Toolbox.ToolboxUI.AsyncRemoveTask(callback).execute(thing, actions.getCheckedRadioButtonId(), me.ccrama.redditslide.Toolbox.ToolboxUI.replaceTokens(removalString.toString(), thing), me.ccrama.redditslide.Toolbox.ToolboxUI.replaceTokens(removalReasons.getPmSubject(), thing), actionModmail.isChecked(), actionSticky.isChecked(), actionLock.isChecked(), log, // log post title
                me.ccrama.redditslide.Toolbox.ToolboxUI.replaceTokens(removalReasons.getLogTitle(), thing).replace("{reason}", logReason.getText()), removalReasons.getLogSub(), new java.lang.String[]{ flairText.toString(), flairCSS.toString() });
            }
        });
        builder.build().show();
    }

    /**
     * Checks if a Toolbox removal dialog can be shown for a subreddit
     *
     * @param subreddit
     * 		Subreddit
     * @return whether a toolbox removal dialog can be shown
     */
    public static boolean canShowRemoval(java.lang.String subreddit) {
        return (me.ccrama.redditslide.SettingValues.toolboxEnabled && (me.ccrama.redditslide.Toolbox.Toolbox.getConfig(subreddit) != null)) && (me.ccrama.redditslide.Toolbox.Toolbox.getConfig(subreddit).getRemovalReasons() != null);
    }

    /**
     * Replace toolbox tokens with the appropriate replacements
     * Does NOT include log-related tokens, those must be handled after logging.
     *
     * @param reason
     * 		String to be parsed
     * @param parameter
     * 		Item being acted upon
     * @return String with replacements made
     */
    public static java.lang.String replaceTokens(java.lang.String reason, net.dean.jraw.models.PublicContribution parameter) {
        if (parameter instanceof net.dean.jraw.models.Comment) {
            net.dean.jraw.models.Comment thing = ((net.dean.jraw.models.Comment) (parameter));
            return reason.replace("{subreddit}", thing.getSubredditName()).replace("{author}", thing.getAuthor()).replace("{kind}", "comment").replace("{mod}", me.ccrama.redditslide.Authentication.name).replace("{title}", "").replace("{url}", "https://www.reddit.com" + thing.getDataNode().get("permalink").asText()).replace("{domain}", "").replace("{link}", "undefined");
        } else if (parameter instanceof net.dean.jraw.models.Submission) {
            net.dean.jraw.models.Submission thing = ((net.dean.jraw.models.Submission) (parameter));
            return reason.replace("{subreddit}", thing.getSubredditName()).replace("{author}", thing.getAuthor()).replace("{kind}", "submission").replace("{mod}", me.ccrama.redditslide.Authentication.name).replace("{title}", thing.getTitle()).replace("{url}", "https://www.reddit.com" + thing.getDataNode().get("permalink").asText()).replace("{domain}", thing.getDomain()).replace("{link}", thing.getUrl());
        } else {
            throw new java.lang.IllegalArgumentException("Must be passed a submission or comment!");
        }
    }

    /**
     * Shows a user's usernotes in a dialog
     *
     * @param context
     * 		context
     * @param author
     * 		user to show usernotes for
     * @param subreddit
     * 		subreddit to get usernotes from
     * @param currentLink
     * 		Link, in Toolbox format, for the current item - used for adding usernotes
     */
    public static void showUsernotes(final android.content.Context context, java.lang.String author, java.lang.String subreddit, java.lang.String currentLink) {
        final me.ccrama.redditslide.Toolbox.ToolboxUI.UsernoteListAdapter adapter = new me.ccrama.redditslide.Toolbox.ToolboxUI.UsernoteListAdapter(context, subreddit, author);
        new com.afollestad.materialdialogs.MaterialDialog.Builder(context).title(context.getResources().getString(me.ccrama.redditslide.R.string.mod_usernotes_title, author)).adapter(adapter, null).neutralText(me.ccrama.redditslide.R.string.mod_usernotes_add).onNeutral(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
            @java.lang.Override
            public void onClick(@android.support.annotation.NonNull
            com.afollestad.materialdialogs.MaterialDialog dialog, @android.support.annotation.NonNull
            com.afollestad.materialdialogs.DialogAction which) {
                // set up layout for add note dialog
                final android.widget.LinearLayout layout = new android.widget.LinearLayout(context);
                final android.widget.Spinner spinner = new android.widget.Spinner(context);
                final android.widget.EditText noteText = new android.widget.EditText(context);
                layout.addView(spinner);
                layout.addView(noteText);
                noteText.setHint(me.ccrama.redditslide.R.string.toolbox_note_text_placeholder);
                layout.setOrientation(android.widget.LinearLayout.VERTICAL);
                android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
                spinner.setLayoutParams(params);
                noteText.setLayoutParams(params);
                // create list of types, add default "no type" type
                java.util.List<java.lang.CharSequence> types = new java.util.ArrayList<>();
                android.text.SpannableStringBuilder defaultType = new android.text.SpannableStringBuilder((" " + context.getString(me.ccrama.redditslide.R.string.toolbox_note_default)) + " ");
                defaultType.setSpan(new android.text.style.BackgroundColorSpan(android.graphics.Color.parseColor("#808080")), 0, defaultType.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                defaultType.setSpan(new android.text.style.ForegroundColorSpan(android.graphics.Color.WHITE), 0, defaultType.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                types.add(defaultType);
                // add additional types
                me.ccrama.redditslide.Toolbox.ToolboxConfig config = me.ccrama.redditslide.Toolbox.Toolbox.getConfig(subreddit);
                final java.util.Map<java.lang.String, java.util.Map<java.lang.String, java.lang.String>> typeMap;
                if (((config != null) && (config.getUsernoteTypes() != null)) && (config.getUsernoteTypes().size() > 0)) {
                    typeMap = me.ccrama.redditslide.Toolbox.Toolbox.getConfig(subreddit).getUsernoteTypes();
                } else {
                    typeMap = me.ccrama.redditslide.Toolbox.Toolbox.DEFAULT_USERNOTE_TYPES;
                }
                for (java.lang.String type : typeMap.keySet()) {
                    android.text.SpannableStringBuilder typeString = new android.text.SpannableStringBuilder((" [" + typeMap.get(type).get("text")) + "] ");
                    typeString.setSpan(new android.text.style.BackgroundColorSpan(android.graphics.Color.parseColor(typeMap.get(type).get("color"))), 0, typeString.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    typeString.setSpan(new android.text.style.ForegroundColorSpan(android.graphics.Color.WHITE), 0, typeString.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    types.add(typeString);
                }
                spinner.setAdapter(new android.widget.ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, types));
                // show add note dialog
                new com.afollestad.materialdialogs.MaterialDialog.Builder(context).customView(layout, true).autoDismiss(false).positiveText(me.ccrama.redditslide.R.string.btn_add).onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                    @java.lang.Override
                    public void onClick(@android.support.annotation.NonNull
                    com.afollestad.materialdialogs.MaterialDialog dialog, @android.support.annotation.NonNull
                    com.afollestad.materialdialogs.DialogAction which) {
                        if (noteText.getText().length() == 0) {
                            noteText.setError(context.getString(me.ccrama.redditslide.R.string.toolbox_note_text_required));
                            return;
                        }
                        int selected = spinner.getSelectedItemPosition();
                        new me.ccrama.redditslide.Toolbox.ToolboxUI.AsyncAddUsernoteTask(context).execute(subreddit, author, noteText.getText().toString(), currentLink, (selected - 1) >= 0 ? typeMap.keySet().toArray()[selected - 1].toString() : null);
                        dialog.dismiss();
                    }
                }).negativeText(me.ccrama.redditslide.R.string.btn_cancel).onNegative((com.afollestad.materialdialogs.MaterialDialog dialog1,com.afollestad.materialdialogs.DialogAction which1) -> dialog1.dismiss()).show();
            }
        }).positiveText(me.ccrama.redditslide.R.string.btn_close).show();
    }

    /**
     * Appends a usernote to builder if a usernote in the subreddit is available, and the current
     * user has it enabled.
     *
     * @param context
     * 		Android context
     * @param builder
     * 		The builder to append the usernote to
     * @param subreddit
     * 		The subreddit to look for notes in
     * @param user
     * 		The user to look for
     */
    public static void appendToolboxNote(android.content.Context context, android.text.SpannableStringBuilder builder, java.lang.String subreddit, java.lang.String user) {
        if ((!me.ccrama.redditslide.SettingValues.toolboxEnabled) || (!me.ccrama.redditslide.Authentication.mod)) {
            return;
        }
        me.ccrama.redditslide.Toolbox.Usernotes notes = me.ccrama.redditslide.Toolbox.Toolbox.getUsernotes(subreddit);
        if (notes == null) {
            return;
        }
        java.util.List<me.ccrama.redditslide.Toolbox.Usernote> notesForUser = notes.getNotesForUser(user);
        if ((notesForUser == null) || notesForUser.isEmpty()) {
            return;
        }
        android.text.SpannableStringBuilder noteBuilder = new android.text.SpannableStringBuilder(("\u00a0" + notes.getDisplayNoteForUser(user)) + "\u00a0");
        noteBuilder.setSpan(new me.ccrama.redditslide.Views.RoundedBackgroundSpan(context.getResources().getColor(me.ccrama.redditslide.R.color.white), notes.getDisplayColorForUser(user), false, context), 0, noteBuilder.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(" ");
        builder.append(noteBuilder);
    }

    public static class UsernoteListAdapter extends android.widget.ArrayAdapter<me.ccrama.redditslide.Toolbox.ToolboxUI.UsernoteListItem> {
        public UsernoteListAdapter(@android.support.annotation.NonNull
        android.content.Context context, java.lang.String subreddit, java.lang.String user) {
            super(context, me.ccrama.redditslide.R.layout.usernote_list_item, me.ccrama.redditslide.R.id.usernote_note_text);
            final me.ccrama.redditslide.Toolbox.Usernotes usernotes = me.ccrama.redditslide.Toolbox.Toolbox.getUsernotes(subreddit);
            if ((usernotes != null) && (usernotes.getNotesForUser(user) != null)) {
                for (me.ccrama.redditslide.Toolbox.Usernote note : usernotes.getNotesForUser(user)) {
                    java.lang.String dateString = java.text.SimpleDateFormat.getDateTimeInstance(java.text.SimpleDateFormat.SHORT, java.text.SimpleDateFormat.SHORT).format(new java.util.Date(note.getTime()));
                    android.text.SpannableStringBuilder authorDateText = new android.text.SpannableStringBuilder((usernotes.getModNameFromModIndex(note.getMod()) + "\n") + dateString);
                    authorDateText.setSpan(new android.text.style.RelativeSizeSpan(0.92F), authorDateText.length() - dateString.length(), authorDateText.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    android.text.SpannableStringBuilder noteText = new android.text.SpannableStringBuilder(usernotes.getWarningTextFromWarningIndex(note.getWarning(), true));
                    noteText.setSpan(new android.text.style.ForegroundColorSpan(usernotes.getColorFromWarningIndex(note.getWarning())), 0, noteText.length(), android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (noteText.length() > 0) {
                        noteText.append(" ");
                    }
                    noteText.append(note.getNoteText());
                    java.lang.String link = note.getLinkAsURL(subreddit);
                    this.add(new me.ccrama.redditslide.Toolbox.ToolboxUI.UsernoteListItem(authorDateText, noteText, link, note, subreddit, user));
                }
            }
        }

        @android.support.annotation.NonNull
        @java.lang.Override
        public android.view.View getView(int position, @android.support.annotation.Nullable
        android.view.View convertView, @android.support.annotation.NonNull
        android.view.ViewGroup parent) {
            final android.view.View view = super.getView(position, convertView, parent);
            final me.ccrama.redditslide.Toolbox.ToolboxUI.UsernoteListItem item = getItem(position);
            android.widget.TextView authorDatetime = view.findViewById(me.ccrama.redditslide.R.id.usernote_author_datetime);
            authorDatetime.setText(item.getAuthorDatetime());
            android.widget.TextView noteText = view.findViewById(me.ccrama.redditslide.R.id.usernote_note_text);
            noteText.setText(item.getNoteText());
            view.setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    if (item.getLink() != null) {
                        me.ccrama.redditslide.OpenRedditLink.openUrl(view.getContext(), item.getLink(), true);
                    }
                }
            });
            view.findViewById(me.ccrama.redditslide.R.id.delete).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    new me.ccrama.redditslide.Toolbox.ToolboxUI.AsyncRemoveUsernoteTask(item.getNote(), getContext()).execute(item.getSubreddit(), item.getUser());
                    remove(item);
                }
            });
            return view;
        }
    }

    public static class UsernoteListItem {
        private java.lang.CharSequence authorDatetime;

        private java.lang.CharSequence noteText;

        private java.lang.String link;

        private me.ccrama.redditslide.Toolbox.Usernote note;

        private java.lang.String subreddit;

        private java.lang.String user;

        public UsernoteListItem(java.lang.CharSequence authorDatetime, java.lang.CharSequence noteText, java.lang.String link, me.ccrama.redditslide.Toolbox.Usernote note, java.lang.String subreddit, java.lang.String user) {
            this.authorDatetime = authorDatetime;
            this.noteText = noteText;
            this.link = link;
            this.note = note;
            this.subreddit = subreddit;
            this.user = user;
        }

        public java.lang.CharSequence getAuthorDatetime() {
            return authorDatetime;
        }

        public java.lang.CharSequence getNoteText() {
            return noteText;
        }

        public java.lang.String getLink() {
            return link;
        }

        public me.ccrama.redditslide.Toolbox.Usernote getNote() {
            return note;
        }

        public java.lang.String getSubreddit() {
            return subreddit;
        }

        public java.lang.String getUser() {
            return user;
        }
    }

    /**
     * Removes a post/comment, optionally locking first if a post.
     * Parameters are: thing (extends PublicContribution),
     * action ID (int),
     * removal reason (String),
     * removal subject (String),
     * modmail (boolean),
     * sticky (boolean),
     * lock (boolean),
     * log (boolean),
     * logtitle (String),
     * logsub (String)
     * flair (String[] - [text, css])
     */
    public static class AsyncRemoveTask extends android.os.AsyncTask<java.lang.Object, java.lang.Void, java.lang.Boolean> {
        me.ccrama.redditslide.Toolbox.ToolboxUI.CompletedRemovalCallback callback;

        public AsyncRemoveTask(me.ccrama.redditslide.Toolbox.ToolboxUI.CompletedRemovalCallback callback) {
            this.callback = callback;
        }

        /**
         * Runs the removal and necessary action(s)
         *
         * @param objects
         * 		...
         * @return Success
         */
        @java.lang.Override
        protected java.lang.Boolean doInBackground(java.lang.Object... objects) {
            net.dean.jraw.models.PublicContribution thing = ((net.dean.jraw.models.PublicContribution) (objects[0]));
            int action = ((int) (objects[1]));
            java.lang.String removalString = ((java.lang.String) (objects[2]));
            java.lang.String pmSubject = ((java.lang.String) (objects[3]));
            boolean modmail = ((boolean) (objects[4]));
            boolean sticky = ((boolean) (objects[5]));
            boolean lock = ((boolean) (objects[6]));
            boolean log = ((boolean) (objects[7]));
            java.lang.String logTitle = ((java.lang.String) (objects[8]));
            java.lang.String logSub = ((java.lang.String) (objects[9]));
            java.lang.String[] flair = ((java.lang.String[]) (objects[10]));
            boolean success = true;
            java.lang.String logResult = "";
            if (log) {
                // Log the removal
                net.dean.jraw.models.Submission s = logRemoval(logSub, logTitle, "https://www.reddit.com" + thing.getDataNode().get("permalink").asText());
                if (s != null) {
                    logResult = "https://www.reddit.com" + s.getDataNode().get("permalink").asText();
                } else {
                    success = false;
                }
            }
            // Check what the desired action is and perform it
            switch (action) {
                case me.ccrama.redditslide.R.id.comment :
                    success &= postRemovalComment(thing, removalString.replace("{loglink}", logResult), sticky);
                    break;
                case me.ccrama.redditslide.R.id.pm :
                    if (thing instanceof net.dean.jraw.models.Comment) {
                        success &= sendRemovalPM(modmail ? ((net.dean.jraw.models.Comment) (thing)).getSubredditName() : "", ((net.dean.jraw.models.Comment) (thing)).getAuthor(), pmSubject.replace("{loglink}", logResult), removalString);
                    } else {
                        success &= sendRemovalPM(modmail ? ((net.dean.jraw.models.Submission) (thing)).getSubredditName() : "", ((net.dean.jraw.models.Submission) (thing)).getAuthor(), pmSubject.replace("{loglink}", logResult), removalString);
                    }
                    break;
                case me.ccrama.redditslide.R.id.both :
                    success &= postRemovalComment(thing, removalString.replace("{loglink}", logResult), sticky);
                    if (thing instanceof net.dean.jraw.models.Comment) {
                        success &= sendRemovalPM(modmail ? ((net.dean.jraw.models.Comment) (thing)).getSubredditName() : "", ((net.dean.jraw.models.Comment) (thing)).getAuthor(), pmSubject.replace("{loglink}", logResult), removalString);
                    } else {
                        success &= sendRemovalPM(modmail ? ((net.dean.jraw.models.Submission) (thing)).getSubredditName() : "", ((net.dean.jraw.models.Submission) (thing)).getAuthor(), pmSubject.replace("{loglink}", logResult), removalString);
                    }
                    break;
                    // case R.id.none is unnecessary as we don't do anything on none.
            }
            // Remove the item and lock/apply necessary flair
            try {
                new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).remove(((net.dean.jraw.models.PublicContribution) (objects[0])), false);
                if (lock && (thing instanceof net.dean.jraw.models.Submission)) {
                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).setLocked(((net.dean.jraw.models.Submission) (thing)));
                }
                if (((flair[0].length() > 0) || (flair[1].length() > 0)) && (thing instanceof net.dean.jraw.models.Submission)) {
                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).setFlair(((net.dean.jraw.models.Submission) (thing)).getSubredditName(), ((net.dean.jraw.models.Submission) (thing)), flair[0], flair[1]);
                }
            } catch (net.dean.jraw.ApiException | net.dean.jraw.http.NetworkException e) {
                success = false;
            }
            return success;
        }

        /**
         * Run the callback
         *
         * @param success
         * 		Whether doInBackground was a complete success
         */
        @java.lang.Override
        protected void onPostExecute(java.lang.Boolean success) {
            // Run the callback on the UI thread
            callback.onComplete(success);
        }

        /**
         * Send a removal PM
         *
         * @param from
         * 		empty string if from user, sub name if from sub
         * @param to
         * 		recipient
         * @param subject
         * 		subject
         * @param body
         * 		body
         * @return success
         */
        private boolean sendRemovalPM(java.lang.String from, java.lang.String to, java.lang.String subject, java.lang.String body) {
            try {
                new net.dean.jraw.managers.InboxManager(me.ccrama.redditslide.Authentication.reddit).compose(from, to, subject, body);
                return true;
            } catch (net.dean.jraw.ApiException | net.dean.jraw.http.NetworkException e) {
                return false;
            }
        }

        /**
         * Post a removal comment
         *
         * @param thing
         * 		thing to reply to
         * @param comment
         * 		comment text
         * @param sticky
         * 		whether to sticky the comment
         * @return success
         */
        private boolean postRemovalComment(net.dean.jraw.models.PublicContribution thing, java.lang.String comment, boolean sticky) {
            try {
                // Reply with a comment and get that comment's ID
                java.lang.String id = new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).reply(thing, comment);
                // Sticky or distinguish the posted comment
                if (sticky) {
                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).setSticky(((net.dean.jraw.models.Comment) (me.ccrama.redditslide.Authentication.reddit.get("t1_" + id).get(0))), true);
                } else {
                    new net.dean.jraw.managers.ModerationManager(me.ccrama.redditslide.Authentication.reddit).setDistinguishedStatus(me.ccrama.redditslide.Authentication.reddit.get("t1_" + id).get(0), net.dean.jraw.models.DistinguishedStatus.MODERATOR);
                }
                return true;
            } catch (net.dean.jraw.ApiException | net.dean.jraw.http.NetworkException e) {
                return false;
            }
        }

        /**
         * Log a removal to a logsub
         *
         * @param logSub
         * 		name of log sub
         * @param title
         * 		title of post
         * @return resulting submission
         */
        private net.dean.jraw.models.Submission logRemoval(java.lang.String logSub, java.lang.String title, java.lang.String link) {
            try {
                return new net.dean.jraw.managers.AccountManager(me.ccrama.redditslide.Authentication.reddit).submit(new net.dean.jraw.managers.AccountManager.SubmissionBuilder(new java.net.URL(link), logSub, title));
            } catch (java.net.MalformedURLException | net.dean.jraw.ApiException | net.dean.jraw.http.NetworkException e) {
                return null;
            }
        }

        /**
         * Convenience method to execute the task with the correct parameters
         *
         * @param thing
         * 		Thing being removed
         * @param action
         * 		Action to take
         * @param removalReason
         * 		Removal reason
         * @param pmSubject
         * 		Removal PM subject
         * @param modmail
         * 		Whether to send PM as modmail
         * @param sticky
         * 		Whether to sticky removal comment
         * @param lock
         * 		Whether to lock removed thread
         * @param log
         * 		Whether to log the removal
         * @param logTitle
         * 		Log post title
         * @param logSub
         * 		Log subreddit
         * @param flair
         * 		Flair [text, CSS]
         */
        public void execute(net.dean.jraw.models.PublicContribution thing, int action, java.lang.String removalReason, java.lang.String pmSubject, boolean modmail, boolean sticky, boolean lock, boolean log, java.lang.String logTitle, java.lang.String logSub, java.lang.String[] flair) {
            super.execute(thing, action, removalReason, pmSubject, modmail, sticky, lock, log, logTitle, logSub, flair);
        }
    }

    /**
     * Add a usernote for a subreddit
     * Parameters are:
     * subreddit
     * user
     * note text
     * link
     * type
     */
    public static class AsyncAddUsernoteTask extends android.os.AsyncTask<java.lang.String, java.lang.Void, java.lang.Boolean> {
        private java.lang.ref.WeakReference<android.content.Context> contextRef;

        AsyncAddUsernoteTask(android.content.Context context) {
            this.contextRef = new java.lang.ref.WeakReference<>(context);
        }

        @java.lang.Override
        protected java.lang.Boolean doInBackground(java.lang.String... strings) {
            java.lang.String reason;
            me.ccrama.redditslide.Toolbox.Toolbox.downloadUsernotes(strings[0]);
            if (me.ccrama.redditslide.Toolbox.Toolbox.getUsernotes(strings[0]) == null) {
                me.ccrama.redditslide.Toolbox.Toolbox.createUsernotes(strings[0]);
                reason = "create usernotes config";
            } else {
                reason = "create new note on user " + strings[1];
            }
            // user
            // note text
            // link
            // time
            // mod
            // type
            me.ccrama.redditslide.Toolbox.Toolbox.getUsernotes(strings[0]).createNote(strings[1], strings[2], strings[3], java.lang.System.currentTimeMillis(), me.ccrama.redditslide.Authentication.name, strings[4]);
            try {
                me.ccrama.redditslide.Toolbox.Toolbox.uploadUsernotes(strings[0], reason);
            } catch (net.dean.jraw.http.oauth.InvalidScopeException e) {
                // we don't have wikiedit scope, need to reauth to get it
                return false;
            }
            return true;
        }

        @java.lang.Override
        protected void onPostExecute(java.lang.Boolean success) {
            if (!success) {
                final android.content.Context context = contextRef.get();
                if (context == null) {
                    return;
                }
                new com.afollestad.materialdialogs.MaterialDialog.Builder(context).title(me.ccrama.redditslide.R.string.toolbox_wiki_edit_reauth).content(me.ccrama.redditslide.R.string.toolbox_wiki_edit_reauth_question).negativeText(me.ccrama.redditslide.R.string.misc_maybe_later).positiveText(me.ccrama.redditslide.R.string.btn_yes).onPositive((com.afollestad.materialdialogs.MaterialDialog dialog1,com.afollestad.materialdialogs.DialogAction which1) -> context.startActivity(new android.content.Intent(context, me.ccrama.redditslide.Activities.Reauthenticate.class))).show();
            }
        }
    }

    /**
     * Remove a usernote from a subreddit
     * Parameters are:
     * subreddit
     * user
     */
    public static class AsyncRemoveUsernoteTask extends android.os.AsyncTask<java.lang.String, java.lang.Void, java.lang.Boolean> {
        private me.ccrama.redditslide.Toolbox.Usernote note;

        private java.lang.ref.WeakReference<android.content.Context> contextRef;

        AsyncRemoveUsernoteTask(me.ccrama.redditslide.Toolbox.Usernote note, android.content.Context context) {
            this.note = note;
            this.contextRef = new java.lang.ref.WeakReference<>(context);
        }

        @java.lang.Override
        protected java.lang.Boolean doInBackground(java.lang.String... strings) {
            me.ccrama.redditslide.Toolbox.Toolbox.downloadUsernotes(strings[0]);
            me.ccrama.redditslide.Toolbox.Toolbox.getUsernotes(strings[0]).removeNote(strings[1], note);
            try {
                me.ccrama.redditslide.Toolbox.Toolbox.uploadUsernotes(strings[0], (("delete note " + note.getTime()) + " on user ") + strings[1]);
            } catch (net.dean.jraw.http.oauth.InvalidScopeException e) {
                // we don't have wikiedit scope, need to reauth to get it
                return false;
            }
            return true;
        }

        @java.lang.Override
        protected void onPostExecute(java.lang.Boolean success) {
            if (!success) {
                final android.content.Context context = contextRef.get();
                if (context == null) {
                    return;
                }
                new com.afollestad.materialdialogs.MaterialDialog.Builder(context).title(me.ccrama.redditslide.R.string.toolbox_wiki_edit_reauth).content(me.ccrama.redditslide.R.string.toolbox_wiki_edit_reauth_question).negativeText(me.ccrama.redditslide.R.string.misc_maybe_later).positiveText(me.ccrama.redditslide.R.string.btn_yes).onPositive((com.afollestad.materialdialogs.MaterialDialog dialog1,com.afollestad.materialdialogs.DialogAction which1) -> context.startActivity(new android.content.Intent(context, me.ccrama.redditslide.Activities.Reauthenticate.class))).show();
            }
        }
    }

    /**
     * A callback for code to be run on the UI thread after removal.
     */
    public interface CompletedRemovalCallback {
        /**
         * Called when the removal is completed
         *
         * @param success
         * 		Whether the removal and reason-sending process was 100% successful or not
         */
        void onComplete(boolean success);
    }
}