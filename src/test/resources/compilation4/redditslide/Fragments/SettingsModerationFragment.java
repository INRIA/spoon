package me.ccrama.redditslide.Fragments;
import me.ccrama.redditslide.Toolbox.Toolbox;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.UserSubscriptions;
import me.ccrama.redditslide.SettingValues;
public class SettingsModerationFragment {
    private android.app.Activity context;

    public SettingsModerationFragment(android.app.Activity context) {
        this.context = context;
    }

    public void Bind() {
        final android.widget.TextView removalReasonsCurrent = context.findViewById(me.ccrama.redditslide.R.id.settings_moderation_removal_reasons_current);
        final android.support.v7.widget.SwitchCompat toolboxEnabled = context.findViewById(me.ccrama.redditslide.R.id.settings_moderation_toolbox_enabled);
        final android.widget.TextView removalMessageCurrent = context.findViewById(me.ccrama.redditslide.R.id.settings_moderation_toolbox_message_current);
        final android.widget.RelativeLayout removalMessage = context.findViewById(me.ccrama.redditslide.R.id.settings_moderation_toolbox_message);
        final android.support.v7.widget.SwitchCompat modmail = context.findViewById(me.ccrama.redditslide.R.id.settings_moderation_toolbox_modmail);
        final android.support.v7.widget.SwitchCompat stickyMessage = context.findViewById(me.ccrama.redditslide.R.id.settings_moderation_toolbox_sticky);
        final android.support.v7.widget.SwitchCompat lock = context.findViewById(me.ccrama.redditslide.R.id.settings_moderation_toolbox_lock);
        final android.widget.RelativeLayout refresh = context.findViewById(me.ccrama.redditslide.R.id.settings_moderation_toolbox_refresh);
        {
            // Set up removal reason setting
            if (me.ccrama.redditslide.SettingValues.removalReasonType == me.ccrama.redditslide.SettingValues.RemovalReasonType.SLIDE.ordinal()) {
                removalReasonsCurrent.setText(context.getString(me.ccrama.redditslide.R.string.settings_mod_removal_slide));
            } else if (me.ccrama.redditslide.SettingValues.removalReasonType == me.ccrama.redditslide.SettingValues.RemovalReasonType.TOOLBOX.ordinal()) {
                removalReasonsCurrent.setText(context.getString(me.ccrama.redditslide.R.string.settings_mod_removal_toolbox));
            } else {
                removalReasonsCurrent.setText(context.getString(me.ccrama.redditslide.R.string.settings_mod_removal_reddit));
            }
            context.findViewById(me.ccrama.redditslide.R.id.settings_moderation_removal_reasons).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(context, v);
                    popupMenu.getMenuInflater().inflate(me.ccrama.redditslide.R.menu.removal_reason_setings, popupMenu.getMenu());
                    popupMenu.getMenu().findItem(me.ccrama.redditslide.R.id.toolbox).setEnabled(me.ccrama.redditslide.SettingValues.toolboxEnabled);
                    popupMenu.setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {
                        @java.lang.Override
                        public boolean onMenuItemClick(android.view.MenuItem item) {
                            switch (item.getItemId()) {
                                case me.ccrama.redditslide.R.id.slide :
                                    me.ccrama.redditslide.SettingValues.removalReasonType = me.ccrama.redditslide.SettingValues.RemovalReasonType.SLIDE.ordinal();
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putInt(me.ccrama.redditslide.SettingValues.PREF_MOD_REMOVAL_TYPE, me.ccrama.redditslide.SettingValues.removalReasonType).apply();
                                    removalReasonsCurrent.setText(context.getString(me.ccrama.redditslide.R.string.settings_mod_removal_slide));
                                    break;
                                case me.ccrama.redditslide.R.id.toolbox :
                                    me.ccrama.redditslide.SettingValues.removalReasonType = me.ccrama.redditslide.SettingValues.RemovalReasonType.TOOLBOX.ordinal();
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putInt(me.ccrama.redditslide.SettingValues.PREF_MOD_REMOVAL_TYPE, me.ccrama.redditslide.SettingValues.removalReasonType).apply();
                                    removalReasonsCurrent.setText(context.getString(me.ccrama.redditslide.R.string.settings_mod_removal_toolbox));
                                    break;
                                    // For implementing reddit native removal reasons:
                                    /* case R.id.reddit:
                                    SettingValues.removalReasonType = SettingValues.RemovalReasonType.REDDIT.ordinal();
                                    SettingValues.prefs.edit().putInt(SettingValues.PREF_MOD_REMOVAL_TYPE,
                                    SettingValues.removalReasonType).apply();
                                    removalReasonsCurrent.setText(
                                    context.getString(R.string.settings_mod_removal_reddit));
                                    break;
                                     */
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }
            });
        }
        {
            // Set up toolbox enabled switch
            toolboxEnabled.setChecked(me.ccrama.redditslide.SettingValues.toolboxEnabled);
            toolboxEnabled.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.toolboxEnabled = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_MOD_TOOLBOX_ENABLED, isChecked).apply();
                    removalMessage.setEnabled(isChecked);
                    modmail.setEnabled(isChecked);
                    stickyMessage.setEnabled(isChecked);
                    lock.setEnabled(isChecked);
                    refresh.setEnabled(isChecked);
                    if (!isChecked) {
                        me.ccrama.redditslide.SettingValues.removalReasonType = me.ccrama.redditslide.SettingValues.RemovalReasonType.SLIDE.ordinal();
                        me.ccrama.redditslide.SettingValues.prefs.edit().putInt(me.ccrama.redditslide.SettingValues.PREF_MOD_REMOVAL_TYPE, me.ccrama.redditslide.SettingValues.removalReasonType).apply();
                        removalReasonsCurrent.setText(context.getString(me.ccrama.redditslide.R.string.settings_mod_removal_slide));
                    }
                    // download and cache toolbox stuff in the background unless it's already loaded
                    for (java.lang.String sub : me.ccrama.redditslide.UserSubscriptions.modOf) {
                        me.ccrama.redditslide.Toolbox.Toolbox.ensureConfigCachedLoaded(sub, false);
                        me.ccrama.redditslide.Toolbox.Toolbox.ensureUsernotesCachedLoaded(sub, false);
                    }
                }
            });
        }
        {
            // Set up toolbox default removal type
            removalMessage.setEnabled(me.ccrama.redditslide.SettingValues.toolboxEnabled);
            if (me.ccrama.redditslide.SettingValues.toolboxMessageType == me.ccrama.redditslide.SettingValues.ToolboxRemovalMessageType.COMMENT.ordinal()) {
                removalMessageCurrent.setText(context.getString(me.ccrama.redditslide.R.string.toolbox_removal_comment));
            } else if (me.ccrama.redditslide.SettingValues.toolboxMessageType == me.ccrama.redditslide.SettingValues.ToolboxRemovalMessageType.PM.ordinal()) {
                removalMessageCurrent.setText(context.getString(me.ccrama.redditslide.R.string.toolbox_removal_pm));
            } else if (me.ccrama.redditslide.SettingValues.toolboxMessageType == me.ccrama.redditslide.SettingValues.ToolboxRemovalMessageType.BOTH.ordinal()) {
                removalMessageCurrent.setText(context.getString(me.ccrama.redditslide.R.string.toolbox_removal_both));
            } else {
                removalMessageCurrent.setText(context.getString(me.ccrama.redditslide.R.string.toolbox_removal_none));
            }
            removalMessage.setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(context, v);
                    popupMenu.getMenuInflater().inflate(me.ccrama.redditslide.R.menu.settings_toolbox_message, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {
                        @java.lang.Override
                        public boolean onMenuItemClick(android.view.MenuItem item) {
                            switch (item.getItemId()) {
                                case me.ccrama.redditslide.R.id.comment :
                                    me.ccrama.redditslide.SettingValues.toolboxMessageType = me.ccrama.redditslide.SettingValues.ToolboxRemovalMessageType.COMMENT.ordinal();
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putInt(me.ccrama.redditslide.SettingValues.PREF_MOD_REMOVAL_TYPE, me.ccrama.redditslide.SettingValues.toolboxMessageType).apply();
                                    removalMessageCurrent.setText(context.getString(me.ccrama.redditslide.R.string.toolbox_removal_comment));
                                    break;
                                case me.ccrama.redditslide.R.id.pm :
                                    me.ccrama.redditslide.SettingValues.toolboxMessageType = me.ccrama.redditslide.SettingValues.ToolboxRemovalMessageType.PM.ordinal();
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putInt(me.ccrama.redditslide.SettingValues.PREF_MOD_REMOVAL_TYPE, me.ccrama.redditslide.SettingValues.toolboxMessageType).apply();
                                    removalMessageCurrent.setText(context.getString(me.ccrama.redditslide.R.string.toolbox_removal_pm));
                                    break;
                                case me.ccrama.redditslide.R.id.both :
                                    me.ccrama.redditslide.SettingValues.toolboxMessageType = me.ccrama.redditslide.SettingValues.ToolboxRemovalMessageType.BOTH.ordinal();
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putInt(me.ccrama.redditslide.SettingValues.PREF_MOD_REMOVAL_TYPE, me.ccrama.redditslide.SettingValues.toolboxMessageType).apply();
                                    removalMessageCurrent.setText(context.getString(me.ccrama.redditslide.R.string.toolbox_removal_both));
                                    break;
                                case me.ccrama.redditslide.R.id.none :
                                    me.ccrama.redditslide.SettingValues.toolboxMessageType = me.ccrama.redditslide.SettingValues.ToolboxRemovalMessageType.NONE.ordinal();
                                    me.ccrama.redditslide.SettingValues.prefs.edit().putInt(me.ccrama.redditslide.SettingValues.PREF_MOD_REMOVAL_TYPE, me.ccrama.redditslide.SettingValues.toolboxMessageType).apply();
                                    removalMessageCurrent.setText(context.getString(me.ccrama.redditslide.R.string.toolbox_removal_none));
                                    break;
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }
            });
        }
        {
            // Set up modmail switch
            modmail.setEnabled(me.ccrama.redditslide.SettingValues.toolboxEnabled);
            modmail.setChecked(me.ccrama.redditslide.SettingValues.toolboxModmail);
            modmail.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.toolboxModmail = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_MOD_TOOLBOX_MODMAIL, isChecked).apply();
                }
            });
        }
        {
            // Set up sticky switch
            stickyMessage.setEnabled(me.ccrama.redditslide.SettingValues.toolboxEnabled);
            stickyMessage.setChecked(me.ccrama.redditslide.SettingValues.toolboxSticky);
            stickyMessage.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.toolboxSticky = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_MOD_TOOLBOX_STICKY, isChecked).apply();
                }
            });
        }
        {
            // Set up lock switch
            lock.setEnabled(me.ccrama.redditslide.SettingValues.toolboxEnabled);
            lock.setChecked(me.ccrama.redditslide.SettingValues.toolboxLock);
            lock.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @java.lang.Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    me.ccrama.redditslide.SettingValues.toolboxLock = isChecked;
                    me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_MOD_TOOLBOX_LOCK, isChecked).apply();
                }
            });
        }
        {
            // Set up force refresh button
            refresh.setEnabled(me.ccrama.redditslide.SettingValues.toolboxEnabled);
            refresh.setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    new com.afollestad.materialdialogs.MaterialDialog.Builder(context).content(me.ccrama.redditslide.R.string.settings_mod_toolbox_refreshing).progress(false, me.ccrama.redditslide.UserSubscriptions.modOf.size() * 2).showListener(new android.content.DialogInterface.OnShowListener() {
                        @java.lang.Override
                        public void onShow(android.content.DialogInterface dialog) {
                            new me.ccrama.redditslide.Fragments.SettingsModerationFragment.AsyncRefreshToolboxTask(dialog).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    }).cancelable(false).show();
                }
            });
        }
    }

    private static class AsyncRefreshToolboxTask extends android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void> {
        final com.afollestad.materialdialogs.MaterialDialog dialog;

        AsyncRefreshToolboxTask(android.content.DialogInterface dialog) {
            this.dialog = ((com.afollestad.materialdialogs.MaterialDialog) (dialog));
        }

        @java.lang.Override
        protected java.lang.Void doInBackground(java.lang.Void... voids) {
            for (java.lang.String sub : me.ccrama.redditslide.UserSubscriptions.modOf) {
                me.ccrama.redditslide.Toolbox.Toolbox.downloadToolboxConfig(sub);
                publishProgress();
                me.ccrama.redditslide.Toolbox.Toolbox.downloadUsernotes(sub);
                publishProgress();
            }
            return null;
        }

        @java.lang.Override
        protected void onPostExecute(java.lang.Void aVoid) {
            dialog.dismiss();
        }

        @java.lang.Override
        protected void onProgressUpdate(java.lang.Void... voids) {
            dialog.incrementProgress(1);
        }
    }
}