package me.ccrama.redditslide.Views;
/**
 * Created by carlo_000 on 10/12/2015.
 */
public class ToastHelpCreation {
    public static void makeToast(android.view.View view, java.lang.String message, android.content.Context context) {
        int x = view.getLeft();
        int y = view.getTop() + (2 * view.getHeight());
        android.widget.Toast toast = android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT);
        toast.setGravity(android.view.Gravity.TOP | android.view.Gravity.LEFT, x, y);
        toast.show();
    }
}