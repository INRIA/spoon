/**
 * Created by carlo_000 on 10/23/2015.
 */
package me.ccrama.redditslide;
public class DataBackup extends android.app.backup.BackupAgentHelper {
    static final java.lang.String[] PREFS_TO_BACKUP = new java.lang.String[]{ "AUTH", "SUBS", "SETTINGS", "COLOR", "SEEN", "HIDDEN", "HIDDEN_POSTS", "prefs", "IMAGES", "DATA" };

    static final java.lang.String MY_PREFS_BACKUP_KEY = "myprefs";

    @java.lang.Override
    public void onCreate() {
        android.app.backup.SharedPreferencesBackupHelper helper = new android.app.backup.SharedPreferencesBackupHelper(this, me.ccrama.redditslide.DataBackup.PREFS_TO_BACKUP);
        addHelper(me.ccrama.redditslide.DataBackup.MY_PREFS_BACKUP_KEY, helper);
    }
}