package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Synccit.MySynccitUpdateTask;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.Synccit.MySynccitReadTask;
import me.ccrama.redditslide.Synccit.SynccitRead;
import java.util.Collections;
/**
 * Created by ccrama on 2/16/2015.
 */
public class SettingsSynccit extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    android.widget.EditText name;

    android.widget.EditText auth;

    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_settings_synccit);
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, me.ccrama.redditslide.R.string.settings_synccit, true, true);
        name = ((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.name)));
        auth = ((android.widget.EditText) (findViewById(me.ccrama.redditslide.R.id.auth)));
        name.setText(me.ccrama.redditslide.SettingValues.synccitName);
        auth.setText(me.ccrama.redditslide.SettingValues.synccitAuth);
        if (me.ccrama.redditslide.SettingValues.synccitAuth.isEmpty()) {
            findViewById(me.ccrama.redditslide.R.id.remove).setEnabled(false);
        }
        findViewById(me.ccrama.redditslide.R.id.remove).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                if (!me.ccrama.redditslide.SettingValues.synccitAuth.isEmpty()) {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.SettingsSynccit.this).setTitle(me.ccrama.redditslide.R.string.settings_synccit_delete).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.content.DialogInterface dialog, int which) {
                            me.ccrama.redditslide.SettingValues.synccitName = "";
                            me.ccrama.redditslide.SettingValues.synccitAuth = "";
                            android.content.SharedPreferences.Editor e = me.ccrama.redditslide.SettingValues.prefs.edit();
                            e.putString(me.ccrama.redditslide.SettingValues.SYNCCIT_NAME, me.ccrama.redditslide.SettingValues.synccitName);
                            e.putString(me.ccrama.redditslide.SettingValues.SYNCCIT_AUTH, me.ccrama.redditslide.SettingValues.synccitAuth);
                            e.apply();
                            name.setText(me.ccrama.redditslide.SettingValues.synccitName);
                            auth.setText(me.ccrama.redditslide.SettingValues.synccitAuth);
                            me.ccrama.redditslide.Synccit.SynccitRead.visitedIds.removeAll(java.util.Collections.singleton("16noez"));
                        }
                    }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, null).show();
                }
            }
        });
        findViewById(me.ccrama.redditslide.R.id.save).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                final android.app.Dialog d = new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.SettingsSynccit.this).title(me.ccrama.redditslide.R.string.settings_synccit_authenticate).progress(true, 100).content(me.ccrama.redditslide.R.string.misc_please_wait).cancelable(false).show();
                new me.ccrama.redditslide.Synccit.MySynccitUpdateTask().execute("16noez");
                me.ccrama.redditslide.SettingValues.synccitName = name.getText().toString();
                me.ccrama.redditslide.SettingValues.synccitAuth = auth.getText().toString();
                try {
                    new me.ccrama.redditslide.Synccit.MySynccitReadTask().execute("16noez").get();
                    if (me.ccrama.redditslide.Synccit.SynccitRead.visitedIds.contains("16noez")) {
                        // success
                        d.dismiss();
                        android.content.SharedPreferences.Editor e = me.ccrama.redditslide.SettingValues.prefs.edit();
                        e.putString(me.ccrama.redditslide.SettingValues.SYNCCIT_NAME, me.ccrama.redditslide.SettingValues.synccitName);
                        e.putString(me.ccrama.redditslide.SettingValues.SYNCCIT_AUTH, me.ccrama.redditslide.SettingValues.synccitAuth);
                        e.apply();
                        findViewById(me.ccrama.redditslide.R.id.remove).setEnabled(true);
                        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.SettingsSynccit.this).setTitle(me.ccrama.redditslide.R.string.settings_synccit_connected).setMessage(me.ccrama.redditslide.R.string.settings_synccit_active).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                finish();
                            }
                        }).setOnDismissListener(new android.content.DialogInterface.OnDismissListener() {
                            @java.lang.Override
                            public void onDismiss(android.content.DialogInterface dialog) {
                                finish();
                            }
                        }).show();
                    } else {
                        d.dismiss();
                        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.SettingsSynccit.this).setTitle(me.ccrama.redditslide.R.string.settings_synccit_failed).setMessage(me.ccrama.redditslide.R.string.settings_synccit_failed_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, null).show();
                    }
                } catch (java.lang.Exception e) {
                    d.dismiss();
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.SettingsSynccit.this).setTitle(me.ccrama.redditslide.R.string.settings_synccit_failed).setMessage(me.ccrama.redditslide.R.string.settings_synccit_failed_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, null).show();
                }
            }
        });
    }
}