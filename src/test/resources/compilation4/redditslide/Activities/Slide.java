package me.ccrama.redditslide.Activities;
/**
 * Created by ccrama on 9/28/2015.
 */
public class Slide extends android.app.Activity {
    public static boolean hasStarted;

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstance) {
        super.onCreate(savedInstance);
        if (!me.ccrama.redditslide.Activities.Slide.hasStarted) {
            me.ccrama.redditslide.Activities.Slide.hasStarted = true;
            android.content.Intent i = new android.content.Intent(this, me.ccrama.redditslide.Activities.MainActivity.class);
            startActivity(i);
        }
        finish();
    }
}