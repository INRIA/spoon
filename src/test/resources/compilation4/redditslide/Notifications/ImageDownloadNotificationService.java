package me.ccrama.redditslide.Notifications;
import java.net.MalformedURLException;
import java.io.IOException;
import me.ccrama.redditslide.SettingValues;
import me.ccrama.redditslide.Activities.DeleteFile;
import me.ccrama.redditslide.util.LogUtil;
import java.net.URL;
import com.google.common.io.Files;
import me.ccrama.redditslide.R;
import java.io.FileOutputStream;
import me.ccrama.redditslide.Reddit;
import java.util.List;
import java.util.UUID;
import java.io.File;
import me.ccrama.redditslide.util.FileUtil;
/**
 * Created by Carlos on 7/15/2016.
 */
public class ImageDownloadNotificationService extends android.app.Service {
    @java.lang.Override
    public android.os.IBinder onBind(android.content.Intent intent) {
        return null;
    }

    private void handleIntent(android.content.Intent intent) {
        java.lang.String actuallyLoaded = intent.getStringExtra("actuallyLoaded");
        if (actuallyLoaded.contains("imgur.com") && ((!actuallyLoaded.contains(".png")) && (!actuallyLoaded.contains(".jpg")))) {
            actuallyLoaded = actuallyLoaded + ".png";
        }
        java.lang.String subreddit = "";
        if (intent.hasExtra("subreddit")) {
            subreddit = intent.getStringExtra("subreddit");
        }
        new me.ccrama.redditslide.Notifications.ImageDownloadNotificationService.PollTask(actuallyLoaded, intent.getIntExtra("index", -1), subreddit).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class PollTask extends android.os.AsyncTask<java.lang.Void, java.lang.Void, java.lang.Void> {
        public int id;

        private android.app.NotificationManager mNotifyManager;

        private android.support.v4.app.NotificationCompat.Builder mBuilder;

        public java.lang.String actuallyLoaded;

        private int index;

        private java.lang.String subreddit;

        public PollTask(java.lang.String actuallyLoaded, int index, java.lang.String subreddit) {
            this.actuallyLoaded = actuallyLoaded;
            this.index = index;
            this.subreddit = subreddit;
        }

        public void startNotification() {
            id = ((int) (java.lang.System.currentTimeMillis() / 1000));
            mNotifyManager = ((android.app.NotificationManager) (getSystemService(android.content.Context.NOTIFICATION_SERVICE)));
            mBuilder = new android.support.v4.app.NotificationCompat.Builder(getApplicationContext());
            mBuilder.setContentTitle(getString(me.ccrama.redditslide.R.string.mediaview_notif_title)).setChannelId(me.ccrama.redditslide.Reddit.CHANNEL_IMG).setContentText(getString(me.ccrama.redditslide.R.string.mediaview_notif_text)).setSmallIcon(me.ccrama.redditslide.R.drawable.save_png);
        }

        @java.lang.Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                android.widget.Toast.makeText(me.ccrama.redditslide.Notifications.ImageDownloadNotificationService.this, "Downloading image...", android.widget.Toast.LENGTH_SHORT).show();
            } catch (java.lang.Exception ignored) {
            }
            startNotification();
            mBuilder.setProgress(100, 0, false);
            mNotifyManager.notify(id, mBuilder.build());
        }

        int percentDone;

        int latestPercentDone;

        @java.lang.Override
        protected java.lang.Void doInBackground(java.lang.Void... params) {
            java.lang.String url = actuallyLoaded;
            final java.lang.String finalUrl1 = url;
            final java.lang.String finalUrl = actuallyLoaded;
            try {
                ((me.ccrama.redditslide.Reddit) (getApplication())).getImageLoader().loadImage(finalUrl, null, new com.nostra13.universalimageloader.core.DisplayImageOptions.Builder().imageScaleType(com.nostra13.universalimageloader.core.assist.ImageScaleType.NONE).cacheInMemory(false).cacheOnDisk(true).build(), new com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener() {
                    @java.lang.Override
                    public void onLoadingComplete(java.lang.String imageUri, android.view.View view, final android.graphics.Bitmap loadedImage) {
                        java.io.File f = ((me.ccrama.redditslide.Reddit) (getApplicationContext())).getImageLoader().getDiscCache().get(finalUrl);
                        if ((f != null) && f.exists()) {
                            java.io.File f_out = null;
                            try {
                                if (me.ccrama.redditslide.SettingValues.imageSubfolders && (!subreddit.isEmpty())) {
                                    java.io.File directory = new java.io.File(me.ccrama.redditslide.Reddit.appRestart.getString("imagelocation", "") + (me.ccrama.redditslide.SettingValues.imageSubfolders && (!subreddit.isEmpty()) ? java.io.File.separator + subreddit : ""));
                                    directory.mkdirs();
                                }
                                f_out = new java.io.File((((me.ccrama.redditslide.Reddit.appRestart.getString("imagelocation", "") + (me.ccrama.redditslide.SettingValues.imageSubfolders && (!subreddit.isEmpty()) ? java.io.File.separator + subreddit : "")) + java.io.File.separator) + (index > (-1) ? java.lang.String.format("%03d_", index) : "")) + getFileName(new java.net.URL(finalUrl1)));
                            } catch (java.net.MalformedURLException e) {
                                f_out = new java.io.File(((((me.ccrama.redditslide.Reddit.appRestart.getString("imagelocation", "") + (me.ccrama.redditslide.SettingValues.imageSubfolders && (!subreddit.isEmpty()) ? java.io.File.separator + subreddit : "")) + java.io.File.separator) + (index > (-1) ? java.lang.String.format("%03d_", index) : "")) + java.util.UUID.randomUUID().toString()) + ".png");
                            }
                            me.ccrama.redditslide.util.LogUtil.v("F out is " + f_out.toString());
                            try {
                                com.google.common.io.Files.copy(f, f_out);
                                showNotifPhoto(f_out, loadedImage);
                            } catch (java.io.IOException e) {
                                try {
                                    saveImageGallery(loadedImage, finalUrl1);
                                } catch (java.io.IOException ignored) {
                                    onError(e);
                                }
                            }
                        } else {
                            try {
                                saveImageGallery(loadedImage, finalUrl1);
                            } catch (java.io.IOException e) {
                                onError(e);
                            }
                        }
                    }
                }, new com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener() {
                    @java.lang.Override
                    public void onProgressUpdate(java.lang.String imageUri, android.view.View view, int current, int total) {
                        latestPercentDone = ((int) ((current / ((float) (total))) * 100));
                        if ((percentDone <= (latestPercentDone + 30)) || (latestPercentDone == 100)) {
                            // Do every 10 percent
                            percentDone = latestPercentDone;
                            mBuilder.setProgress(100, percentDone, false);
                            mNotifyManager.notify(id, mBuilder.build());
                        }
                    }
                });
            } catch (java.lang.Exception e) {
                onError(e);
                android.util.Log.v(me.ccrama.redditslide.util.LogUtil.getTag(), "COULDN'T DOWNLOAD!");
            }
            return null;
        }

        public void onError(java.lang.Exception e) {
            e.printStackTrace();
            mNotifyManager.cancel(id);
            stopSelf();
            try {
                android.widget.Toast.makeText(getBaseContext(), "Error saving image", android.widget.Toast.LENGTH_LONG).show();
            } catch (java.lang.Exception ignored) {
            }
        }

        private java.lang.String getFileName(java.net.URL url) {
            if (url == null)
                return null;

            java.lang.String path = url.getPath();
            java.lang.String end = path.substring(path.lastIndexOf("/") + 1);
            if (((!end.endsWith(".png")) && (!end.endsWith(".jpg"))) && (!end.endsWith(".jpeg"))) {
                end = end + ".png";
            }
            return end;
        }

        @java.lang.Override
        protected void onPostExecute(java.lang.Void result) {
            super.onPostExecute(result);
            mNotifyManager.cancel(id);
        }

        public void showNotifPhoto(final java.io.File localAbsoluteFilePath, final android.graphics.Bitmap loadedImage) {
            android.media.MediaScannerConnection.scanFile(getApplicationContext(), new java.lang.String[]{ localAbsoluteFilePath.getAbsolutePath() }, null, new android.media.MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(java.lang.String path, android.net.Uri uri) {
                    android.app.PendingIntent pContentIntent;
                    android.app.PendingIntent pShareIntent;
                    android.app.PendingIntent pDeleteIntent;
                    android.app.PendingIntent pEditIntent;
                    android.net.Uri photoURI = me.ccrama.redditslide.util.FileUtil.getFileUri(localAbsoluteFilePath, me.ccrama.redditslide.Notifications.ImageDownloadNotificationService.this);
                    {
                        final android.content.Intent shareIntent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
                        shareIntent.setDataAndType(photoURI, "image/*");
                        java.util.List<android.content.pm.ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(shareIntent, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY);
                        for (android.content.pm.ResolveInfo resolveInfo : resInfoList) {
                            java.lang.String packageName = resolveInfo.activityInfo.packageName;
                            grantUriPermission(packageName, photoURI, android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION | android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }
                        pContentIntent = android.app.PendingIntent.getActivity(getApplicationContext(), id, shareIntent, android.app.PendingIntent.FLAG_CANCEL_CURRENT);
                    }
                    {
                        final android.content.Intent shareIntent = new android.content.Intent(android.content.Intent.ACTION_EDIT);
                        shareIntent.setDataAndType(photoURI, "image/*");
                        java.util.List<android.content.pm.ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(shareIntent, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY);
                        for (android.content.pm.ResolveInfo resolveInfo : resInfoList) {
                            java.lang.String packageName = resolveInfo.activityInfo.packageName;
                            grantUriPermission(packageName, photoURI, android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION | android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }
                        pEditIntent = android.app.PendingIntent.getActivity(getApplicationContext(), id + 1, shareIntent, android.app.PendingIntent.FLAG_CANCEL_CURRENT);
                    }
                    {
                        final android.content.Intent shareIntent = new android.content.Intent(android.content.Intent.ACTION_SEND);
                        java.util.List<android.content.pm.ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(shareIntent, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY);
                        for (android.content.pm.ResolveInfo resolveInfo : resInfoList) {
                            java.lang.String packageName = resolveInfo.activityInfo.packageName;
                            grantUriPermission(packageName, photoURI, android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION | android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }
                        shareIntent.putExtra(android.content.Intent.EXTRA_STREAM, photoURI);
                        shareIntent.setDataAndType(photoURI, getContentResolver().getType(photoURI));
                        pShareIntent = android.app.PendingIntent.getActivity(getApplicationContext(), id + 2, android.content.Intent.createChooser(shareIntent, getString(me.ccrama.redditslide.R.string.misc_img_share)), android.app.PendingIntent.FLAG_CANCEL_CURRENT);
                    }
                    {
                        pDeleteIntent = me.ccrama.redditslide.Activities.DeleteFile.getDeleteIntent(id + 3, getApplicationContext(), photoURI.getPath());
                    }
                    android.app.Notification notif = // maybe add this in later .addAction(R.drawable.edit, "EDIT", pEditIntent)
                    new android.support.v4.app.NotificationCompat.Builder(getApplicationContext()).setContentTitle(getString(me.ccrama.redditslide.R.string.info_photo_saved)).setSmallIcon(me.ccrama.redditslide.R.drawable.save_png).setLargeIcon(loadedImage).setContentIntent(pContentIntent).setChannelId(me.ccrama.redditslide.Reddit.CHANNEL_IMG).addAction(me.ccrama.redditslide.R.drawable.ic_share, getString(me.ccrama.redditslide.R.string.share_image), pShareIntent).addAction(me.ccrama.redditslide.R.drawable.ic_delete, getString(me.ccrama.redditslide.R.string.btn_delete), pDeleteIntent).setStyle(new android.support.v4.app.NotificationCompat.BigPictureStyle().bigPicture(android.graphics.Bitmap.createScaledBitmap(loadedImage, 400, 400, false))).build();
                    android.app.NotificationManager mNotificationManager = ((android.app.NotificationManager) (getApplicationContext().getSystemService(android.content.Context.NOTIFICATION_SERVICE)));
                    notif.flags |= android.app.Notification.FLAG_AUTO_CANCEL;
                    mNotificationManager.notify(id, notif);
                    loadedImage.recycle();
                    stopSelf();
                }
            });
        }

        private void saveImageGallery(final android.graphics.Bitmap bitmap, java.lang.String URL) throws java.io.IOException {
            java.io.File f = new java.io.File((((me.ccrama.redditslide.Reddit.appRestart.getString("imagelocation", "") + java.io.File.separator) + (index > (-1) ? java.lang.String.format("%03d_", index) : "")) + java.util.UUID.randomUUID().toString()) + (URL.endsWith("png") ? ".png" : ".jpg"));
            java.io.FileOutputStream out = null;
            f.createNewFile();
            out = new java.io.FileOutputStream(f);
            bitmap.compress(URL.endsWith("png") ? android.graphics.Bitmap.CompressFormat.PNG : android.graphics.Bitmap.CompressFormat.JPEG, 100, out);
            {
                try {
                    if (out != null) {
                        out.close();
                        showNotifPhoto(f, bitmap);
                    }
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        }
    }

    @java.lang.Override
    public void onStart(android.content.Intent intent, int startId) {
        handleIntent(intent);
    }

    @java.lang.Override
    public int onStartCommand(android.content.Intent intent, int flags, int startId) {
        handleIntent(intent);
        return android.app.Service.START_NOT_STICKY;
    }
}