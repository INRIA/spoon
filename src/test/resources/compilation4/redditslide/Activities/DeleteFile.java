package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.util.LogUtil;
import me.ccrama.redditslide.Adapters.MarkAsReadService;
import me.ccrama.redditslide.Reddit;
import java.util.ArrayList;
import me.ccrama.redditslide.Notifications.CheckForMail;
import java.io.File;
import java.net.URI;
/**
 * Created by ccrama on 9/28/2015.
 */
public class DeleteFile extends android.app.Activity {
    public static final java.lang.String NOTIFICATION_ID = "NOTIFICATION_ID";

    public static final java.lang.String PATH = "path";

    public static android.app.PendingIntent getDeleteIntent(int notificationId, android.content.Context context, java.lang.String toDelete) {
        android.content.Intent intent = new android.content.Intent(context, me.ccrama.redditslide.Activities.DeleteFile.class);
        intent.putExtra(me.ccrama.redditslide.Activities.DeleteFile.NOTIFICATION_ID, notificationId - 3);
        intent.putExtra(me.ccrama.redditslide.Activities.DeleteFile.PATH, toDelete);
        return android.app.PendingIntent.getActivity(context, notificationId, intent, android.app.PendingIntent.FLAG_CANCEL_CURRENT);
    }

    @java.lang.Override
    public void onCreate(android.os.Bundle savedInstance) {
        super.onCreate(savedInstance);
        android.content.Intent intent = getIntent();
        android.app.NotificationManager manager = ((android.app.NotificationManager) (getSystemService(android.content.Context.NOTIFICATION_SERVICE)));
        manager.cancel(intent.getIntExtra(me.ccrama.redditslide.Activities.DeleteFile.NOTIFICATION_ID, -1));
        android.os.Bundle extras = intent.getExtras();
        java.lang.String image;
        if (extras != null) {
            image = getIntent().getStringExtra(me.ccrama.redditslide.Activities.DeleteFile.PATH);
            image = image.replace("/external_files", android.os.Environment.getExternalStorageDirectory().toString());
            try {
                final java.lang.String finalImage = image;
                android.media.MediaScannerConnection.scanFile(this, new java.lang.String[]{ image }, null, new android.media.MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(java.lang.String path, android.net.Uri uri) {
                        if (uri != null) {
                            getContentResolver().delete(uri, null, null);
                        }
                        new java.io.File(finalImage).delete();
                    }
                });
            } catch (java.lang.Exception e) {
                e.printStackTrace();
            }
        }
        finish();
    }
}