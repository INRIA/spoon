package me.ccrama.redditslide.util;
import java.io.File;
public class FileUtil {
    private FileUtil() {
    }

    /**
     * Modifies an {@code Intent} to open a file with the FileProvider
     *
     * @param file
     * 		the {@code File} to open
     * @param intent
     * 		the {@Intent } to modify
     * @param context
     * 		Current context
     * @return a base {@code Intent} with read and write permissions granted to the receiving
    application
     */
    public static android.content.Intent getFileIntent(java.io.File file, android.content.Intent intent, android.content.Context context) {
        android.net.Uri selectedUri = me.ccrama.redditslide.util.FileUtil.getFileUri(file, context);
        intent.setDataAndType(selectedUri, context.getContentResolver().getType(selectedUri));
        intent.setFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION | android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        return intent;
    }

    /**
     * Gets a valid File Uri to a file in the system
     *
     * @param file
     * 		the {@code File} to open
     * @param context
     * 		Current context
     * @return a File Uri to the given file
     */
    public static android.net.Uri getFileUri(java.io.File file, android.content.Context context) {
        java.lang.String packageName = context.getApplicationContext().getPackageName() + ".provider";
        android.net.Uri selectedUri = android.support.v4.content.FileProvider.getUriForFile(context, packageName, file);
        context.grantUriPermission(packageName, selectedUri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION | android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        return selectedUri;
    }

    /**
     * Deletes all files in a folder
     *
     * @param dir
     * 		to clear contents
     */
    public static void deleteFilesInDir(java.io.File dir) {
        for (java.io.File child : dir.listFiles()) {
            child.delete();
        }
    }
}