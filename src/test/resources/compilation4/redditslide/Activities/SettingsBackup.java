package me.ccrama.redditslide.Activities;
import java.io.FileReader;
import java.io.Closeable;
import java.util.Calendar;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import me.ccrama.redditslide.SettingValues;
import java.io.BufferedWriter;
import me.ccrama.redditslide.util.LogUtil;
import java.text.SimpleDateFormat;
import me.ccrama.redditslide.R;
import java.io.StringWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.File;
import me.ccrama.redditslide.util.FileUtil;
/**
 * Created by ccrama on 3/5/2015.
 */
public class SettingsBackup extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    com.afollestad.materialdialogs.MaterialDialog progress;

    java.lang.String title;

    java.io.File file;

    public static void close(java.io.Closeable stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (java.io.IOException ignored) {
        }
    }

    @java.lang.Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        if (requestCode == 42) {
            progress = new com.afollestad.materialdialogs.MaterialDialog.Builder(this).title(me.ccrama.redditslide.R.string.backup_restoring).content(me.ccrama.redditslide.R.string.misc_please_wait).cancelable(false).progress(true, 1).build();
            progress.show();
            if (data != null) {
                android.net.Uri fileUri = data.getData();
                android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), "WORKED! " + fileUri.toString());
                java.io.StringWriter fw = new java.io.StringWriter();
                try {
                    java.io.InputStream is = getContentResolver().openInputStream(fileUri);
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is));
                    int c = reader.read();
                    while (c != (-1)) {
                        fw.write(c);
                        c = reader.read();
                    } 
                    java.lang.String read = fw.toString();
                    if (read.contains("Slide_backupEND>")) {
                        java.lang.String[] files = read.split("END>\\s*");
                        progress.dismiss();
                        progress = new com.afollestad.materialdialogs.MaterialDialog.Builder(this).title(me.ccrama.redditslide.R.string.backup_restoring).progress(false, files.length - 1).build();
                        progress.show();
                        for (int i = 1; i < files.length; i++) {
                            java.lang.String innerFile = files[i];
                            java.lang.String t = innerFile.substring(6, innerFile.indexOf(">"));
                            innerFile = innerFile.substring(innerFile.indexOf(">") + 1, innerFile.length());
                            java.io.File newF = new java.io.File((((getApplicationInfo().dataDir + java.io.File.separator) + "shared_prefs") + java.io.File.separator) + t);
                            android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), "WRITING TO " + newF.getAbsolutePath());
                            try {
                                java.io.FileWriter newfw = new java.io.FileWriter(newF);
                                java.io.BufferedWriter bw = new java.io.BufferedWriter(newfw);
                                bw.write(innerFile);
                                bw.close();
                                progress.setProgress(progress.getCurrentProgress() + 1);
                            } catch (java.io.IOException e) {
                                e.printStackTrace();
                            }
                        }
                        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this).setCancelable(false).setTitle(me.ccrama.redditslide.R.string.backup_restore_settings).setOnDismissListener(new android.content.DialogInterface.OnDismissListener() {
                            @java.lang.Override
                            public void onDismiss(android.content.DialogInterface dialog) {
                                com.jakewharton.processphoenix.ProcessPhoenix.triggerRebirth(me.ccrama.redditslide.Activities.SettingsBackup.this);
                            }
                        }).setMessage(me.ccrama.redditslide.R.string.backup_restarting).setOnDismissListener(new android.content.DialogInterface.OnDismissListener() {
                            @java.lang.Override
                            public void onDismiss(android.content.DialogInterface dialog) {
                                com.jakewharton.processphoenix.ProcessPhoenix.triggerRebirth(me.ccrama.redditslide.Activities.SettingsBackup.this);
                            }
                        }).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, new android.content.DialogInterface.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                com.jakewharton.processphoenix.ProcessPhoenix.triggerRebirth(me.ccrama.redditslide.Activities.SettingsBackup.this);
                            }
                        }).setCancelable(false).show();
                    } else {
                        progress.hide();
                        new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this).setTitle(me.ccrama.redditslide.R.string.err_not_valid_backup).setMessage(me.ccrama.redditslide.R.string.err_not_valid_backup_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, null).setCancelable(false).show();
                    }
                } catch (java.lang.Exception e) {
                    progress.hide();
                    e.printStackTrace();
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this).setTitle(me.ccrama.redditslide.R.string.err_file_not_found).setMessage(me.ccrama.redditslide.R.string.err_file_not_found_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, null).show();
                }
            } else {
                progress.dismiss();
                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this).setTitle(me.ccrama.redditslide.R.string.err_file_not_found).setMessage(me.ccrama.redditslide.R.string.err_file_not_found_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, null).setCancelable(false).show();
            }
        }
    }

    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_settings_sync);
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, me.ccrama.redditslide.R.string.settings_title_backup, true, true);
        if (me.ccrama.redditslide.SettingValues.isPro) {
            findViewById(me.ccrama.redditslide.R.id.backfile).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.SettingsBackup.this).setTitle(me.ccrama.redditslide.R.string.settings_backup_include_personal_title).setMessage(me.ccrama.redditslide.R.string.settings_backup_include_personal_text).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes, new android.content.DialogInterface.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.content.DialogInterface dialog, int which) {
                            backupToDir(false);
                        }
                    }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no, new android.content.DialogInterface.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.content.DialogInterface dialog, int which) {
                            backupToDir(true);
                        }
                    }).setNeutralButton(me.ccrama.redditslide.R.string.btn_cancel, null).setCancelable(false).show();
                }
            });
            findViewById(me.ccrama.redditslide.R.id.restorefile).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v) {
                    android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_GET_CONTENT);
                    intent.setType("file/*");
                    intent.addCategory(android.content.Intent.CATEGORY_OPENABLE);
                    java.lang.String[] mimeTypes = new java.lang.String[]{ "text/plain" };
                    intent.putExtra(android.content.Intent.EXTRA_MIME_TYPES, mimeTypes);
                    startActivityForResult(intent, 42);
                }
            });
        } else {
            new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this).setTitle("Settings Backup is a Pro feature").setMessage(me.ccrama.redditslide.R.string.pro_upgrade_msg).setPositiveButton(me.ccrama.redditslide.R.string.btn_yes_exclaim, new android.content.DialogInterface.OnClickListener() {
                public void onClick(android.content.DialogInterface dialog, int whichButton) {
                    try {
                        startActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("market://details?id=" + getString(me.ccrama.redditslide.R.string.ui_unlock_package))));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("http://play.google.com/store/apps/details?id=" + getString(me.ccrama.redditslide.R.string.ui_unlock_package))));
                    }
                }
            }).setNegativeButton(me.ccrama.redditslide.R.string.btn_no_danks, new android.content.DialogInterface.OnClickListener() {
                public void onClick(android.content.DialogInterface dialog, int whichButton) {
                    finish();
                }
            }).setCancelable(false).show();
        }
    }

    public void backupToDir(final boolean personal) {
        new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
            @java.lang.Override
            protected void onPreExecute() {
                progress = new com.afollestad.materialdialogs.MaterialDialog.Builder(me.ccrama.redditslide.Activities.SettingsBackup.this).cancelable(false).title(me.ccrama.redditslide.R.string.backup_backing_up).progress(false, 40).cancelable(false).build();
                progress.show();
            }

            @java.lang.Override
            protected java.lang.Void doInBackground(java.lang.Void... params) {
                java.io.File prefsdir = new java.io.File(getApplicationInfo().dataDir, "shared_prefs");
                if (prefsdir.exists() && prefsdir.isDirectory()) {
                    java.lang.String[] list = prefsdir.list();
                    android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS).mkdirs();
                    java.io.File backedup = new java.io.File(((((android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS) + java.io.File.separator) + "Slide") + new java.text.SimpleDateFormat("-yyyy-MM-dd-HH-mm-ss").format(java.util.Calendar.getInstance().getTime())) + (!personal ? "-personal" : "")) + ".txt");
                    file = backedup;
                    java.io.FileWriter fw = null;
                    try {
                        backedup.createNewFile();
                        fw = new java.io.FileWriter(backedup);
                        fw.write("Slide_backupEND>");
                        for (java.lang.String s : list) {
                            if (((((!s.contains("cache")) && (!s.contains("ion-cookies"))) && (!s.contains("albums"))) && (!s.contains("com.google"))) && (((((((((personal && (!s.contains("SUBSNEW"))) && (!s.contains("appRestart"))) && (!s.contains("STACKTRACE"))) && (!s.contains("AUTH"))) && (!s.contains("TAGS"))) && (!s.contains("SEEN"))) && (!s.contains("HIDDEN"))) && (!s.contains("HIDDEN_POSTS"))) || (!personal))) {
                                java.io.FileReader fr = null;
                                try {
                                    fr = new java.io.FileReader(new java.io.File((prefsdir + java.io.File.separator) + s));
                                    int c = fr.read();
                                    fw.write(("<START" + new java.io.File(s).getName()) + ">");
                                    while (c != (-1)) {
                                        fw.write(c);
                                        c = fr.read();
                                    } 
                                    fw.write("END>");
                                } catch (java.io.IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    me.ccrama.redditslide.Activities.SettingsBackup.close(fr);
                                }
                            }
                        }
                        return null;
                    } catch (java.lang.Exception e) {
                        e.printStackTrace();
                        // todo error
                    } finally {
                        me.ccrama.redditslide.Activities.SettingsBackup.close(fw);
                    }
                }
                return null;
            }

            @java.lang.Override
            protected void onPostExecute(java.lang.Void aVoid) {
                progress.dismiss();
                new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.SettingsBackup.this).setTitle(me.ccrama.redditslide.R.string.backup_complete).setMessage(me.ccrama.redditslide.R.string.backup_saved_downloads).setPositiveButton(me.ccrama.redditslide.R.string.btn_view, new android.content.DialogInterface.OnClickListener() {
                    @java.lang.Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        android.content.Intent intent = me.ccrama.redditslide.util.FileUtil.getFileIntent(file, new android.content.Intent(android.content.Intent.ACTION_VIEW), me.ccrama.redditslide.Activities.SettingsBackup.this);
                        if (intent.resolveActivityInfo(getPackageManager(), 0) != null) {
                            startActivity(android.content.Intent.createChooser(intent, getString(me.ccrama.redditslide.R.string.settings_backup_view)));
                        } else {
                            android.support.design.widget.Snackbar s = android.support.design.widget.Snackbar.make(findViewById(me.ccrama.redditslide.R.id.restorefile), getString(me.ccrama.redditslide.R.string.settings_backup_err_no_explorer, file.getAbsolutePath() + file), android.support.design.widget.Snackbar.LENGTH_INDEFINITE);
                            android.view.View view = s.getView();
                            android.widget.TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                            tv.setTextColor(android.graphics.Color.WHITE);
                            s.show();
                        }
                    }
                }).setNegativeButton(me.ccrama.redditslide.R.string.btn_close, null).setCancelable(false).show();
            }
        }.execute();
    }
}