package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.util.LinkUtil;
import me.ccrama.redditslide.BuildConfig;
import me.ccrama.redditslide.OpenRedditLink;
/**
 * Created by l3d00m on 11/12/2015.
 */
public class SettingsAbout extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyColorTheme();
        setContentView(me.ccrama.redditslide.R.layout.activity_settings_about);
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, me.ccrama.redditslide.R.string.settings_title_about, true, true);
        android.view.View report = findViewById(me.ccrama.redditslide.R.id.report);
        android.view.View libs = findViewById(me.ccrama.redditslide.R.id.libs);
        android.view.View changelog = findViewById(me.ccrama.redditslide.R.id.changelog);
        final android.widget.TextView version = ((android.widget.TextView) (findViewById(me.ccrama.redditslide.R.id.version)));
        version.setText("Slide v" + me.ccrama.redditslide.BuildConfig.VERSION_NAME);
        // Copy the latest stacktrace with a long click on the version number
        if (me.ccrama.redditslide.BuildConfig.DEBUG) {
            version.setOnLongClickListener(new android.view.View.OnLongClickListener() {
                @java.lang.Override
                public boolean onLongClick(android.view.View view) {
                    android.content.SharedPreferences prefs = getSharedPreferences("STACKTRACE", android.content.Context.MODE_PRIVATE);
                    java.lang.String stacktrace = prefs.getString("stacktrace", null);
                    if (stacktrace != null) {
                        android.content.ClipboardManager clipboard = ((android.content.ClipboardManager) (getSystemService(android.content.Context.CLIPBOARD_SERVICE)));
                        android.content.ClipData clip = android.content.ClipData.newPlainText("Stacktrace", stacktrace);
                        clipboard.setPrimaryClip(clip);
                    }
                    prefs.edit().clear().apply();
                    return true;
                }
            });
        }
        version.setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                java.lang.String versionNumber = version.getText().toString();
                android.content.ClipboardManager clipboard = ((android.content.ClipboardManager) (getSystemService(android.content.Context.CLIPBOARD_SERVICE)));
                android.content.ClipData clip = android.content.ClipData.newPlainText("Version", versionNumber);
                clipboard.setPrimaryClip(clip);
                android.widget.Toast.makeText(me.ccrama.redditslide.Activities.SettingsAbout.this, me.ccrama.redditslide.R.string.settings_about_version_copied_toast, android.widget.Toast.LENGTH_SHORT).show();
            }
        });
        report.setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                me.ccrama.redditslide.util.LinkUtil.openExternally("https://github.com/ccrama/Slide/issues");
            }
        });
        findViewById(me.ccrama.redditslide.R.id.sub).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                new me.ccrama.redditslide.OpenRedditLink(me.ccrama.redditslide.Activities.SettingsAbout.this, "https://reddit.com/r/slideforreddit");
            }
        });
        findViewById(me.ccrama.redditslide.R.id.rate).setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                try {
                    startActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("market://details?id=me.ccrama.redditslide")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("http://play.google.com/store/apps/details?id=me.ccrama.redditslide")));
                }
            }
        });
        changelog.setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                me.ccrama.redditslide.util.LinkUtil.openExternally("https://github.com/ccrama/Slide/blob/master/CHANGELOG.md");
            }
        });
        libs.setOnClickListener(new android.view.View.OnClickListener() {
            @java.lang.Override
            public void onClick(android.view.View v) {
                android.content.Intent i = new android.content.Intent(me.ccrama.redditslide.Activities.SettingsAbout.this, me.ccrama.redditslide.Activities.SettingsLibs.class);
                startActivity(i);
            }
        });
    }
}