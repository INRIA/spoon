package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.util.LogUtil;
import java.util.Locale;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.OpenRedditLink;
/**
 * Created by ccrama on 9/28/2015.
 */
public class OpenContent extends android.app.Activity {
    public static final java.lang.String EXTRA_URL = "url";

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(me.ccrama.redditslide.R.layout.clear);
        android.content.Intent intent = getIntent();
        android.net.Uri data = intent.getData();
        android.os.Bundle extras = intent.getExtras();
        java.lang.String url;
        if (data != null) {
            url = data.toString();
        } else if (extras != null) {
            url = extras.getString(me.ccrama.redditslide.Activities.OpenContent.EXTRA_URL, "");
        } else {
            android.widget.Toast.makeText(this, me.ccrama.redditslide.R.string.err_invalid_url, android.widget.Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        url = url.toLowerCase(java.util.Locale.ENGLISH);
        new me.ccrama.redditslide.OpenRedditLink(this, url);
    }

    boolean second = false;

    @java.lang.Override
    public void onResume() {
        super.onResume();
        if (second) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                finishAndRemoveTask();
            } else {
                finish();
            }
        } else {
            second = true;
        }
    }

    @java.lang.Override
    public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
        android.net.Uri data = intent.getData();
        android.os.Bundle extras = intent.getExtras();
        java.lang.String url;
        if (data != null) {
            url = data.toString();
        } else if (extras != null) {
            url = extras.getString(me.ccrama.redditslide.Activities.OpenContent.EXTRA_URL, "");
        } else {
            android.widget.Toast.makeText(this, me.ccrama.redditslide.R.string.err_invalid_url, android.widget.Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        url = url.toLowerCase(java.util.Locale.ENGLISH);
        android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), url);
        new me.ccrama.redditslide.OpenRedditLink(this, url);
    }
}