package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.ContentType;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.util.GifUtils;
import me.ccrama.redditslide.Views.MediaVideoView;
/**
 * Created by ccrama on 01/29/2016.
 *
 * This activity is the basis for the possible inclusion of some sort of "Force Touch" preview system for comment links.
 */
public class ForceTouchLink extends me.ccrama.redditslide.Activities.BaseActivityAnim {
    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstance) {
        overridePendingTransition(0, 0);
        super.onCreate(savedInstance);
        applyColorTheme();
        supportRequestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        setContentView(me.ccrama.redditslide.R.layout.activity_force_touch_content);
        findViewById(android.R.id.content).setOnTouchListener(new android.view.View.OnTouchListener() {
            @java.lang.Override
            public boolean onTouch(android.view.View v, android.view.MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_POINTER_UP) {
                    finish();
                }
                return false;
            }
        });
        final java.lang.String url = getIntent().getExtras().getString("url");
        me.ccrama.redditslide.ContentType.Type t = me.ccrama.redditslide.ContentType.getContentType(url);
        final android.widget.ImageView mainImage = ((android.widget.ImageView) (findViewById(me.ccrama.redditslide.R.id.image)));
        me.ccrama.redditslide.Views.MediaVideoView mainVideo = ((me.ccrama.redditslide.Views.MediaVideoView) (findViewById(me.ccrama.redditslide.R.id.gif)));
        mainVideo.setVisibility(android.view.View.GONE);
        switch (t) {
            case REDDIT :
                break;
            case IMGUR :
                break;
            case IMAGE :
                ((me.ccrama.redditslide.Reddit) (getApplication())).getImageLoader().displayImage(url, mainImage);
                break;
            case GIF :
                mainVideo.setVisibility(android.view.View.VISIBLE);
                new me.ccrama.redditslide.util.GifUtils.AsyncLoadGif(this, mainVideo, null, null, false, true, true, "").execute(url);
                break;
            case ALBUM :
                break;
            case VIDEO :
                break;
            case LINK :
                new android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void>() {
                    java.lang.String urlGotten;

                    @java.lang.Override
                    protected java.lang.Void doInBackground(java.lang.Void... params) {
                        /* try {
                        urlGotten =  ImageExtractor.extractImageUrl(url);
                        } catch (IOException e) {
                        e.printStackTrace();
                        }
                         */
                        return null;
                    }

                    @java.lang.Override
                    protected void onPostExecute(java.lang.Void aVoid) {
                        ((me.ccrama.redditslide.Reddit) (getApplication())).getImageLoader().displayImage(urlGotten, mainImage);
                    }
                }.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
                break;
        }
    }
}