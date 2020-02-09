package me.ccrama.redditslide.util;
import me.ccrama.redditslide.Reddit;
/**
 * Collection of various network utility methods.
 *
 * @author Matthew Dean
 */
public class NetworkUtil {
    // Assigned a random value that is not a value of ConnectivityManager.TYPE_*
    private static final int CONST_NO_NETWORK = 525138;

    private NetworkUtil() {
    }

    /**
     * Uses the provided context to determine the current connectivity status.
     *
     * @param context
     * 		A context used to retrieve connection information from
     * @return A non-null value defined in {@link Status}
     */
    public static me.ccrama.redditslide.util.NetworkUtil.Status getConnectivityStatus(android.content.Context context) {
        android.net.ConnectivityManager cm = ((android.net.ConnectivityManager) (context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE)));
        android.net.NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        switch (activeNetwork != null ? activeNetwork.getType() : me.ccrama.redditslide.util.NetworkUtil.CONST_NO_NETWORK) {
            case android.net.ConnectivityManager.TYPE_WIFI :
            case android.net.ConnectivityManager.TYPE_ETHERNET :
                return me.ccrama.redditslide.util.NetworkUtil.Status.WIFI;
            case android.net.ConnectivityManager.TYPE_MOBILE :
            case android.net.ConnectivityManager.TYPE_BLUETOOTH :
            case android.net.ConnectivityManager.TYPE_WIMAX :
                return me.ccrama.redditslide.util.NetworkUtil.Status.MOBILE;
            default :
                return me.ccrama.redditslide.util.NetworkUtil.Status.NONE;
        }
    }

    /**
     * Checks if the network is connected. An application context is said to have connection if
     * {@link #getConnectivityStatus(Context)} does not equal {@link Status#NONE}.
     *
     * @param context
     * 		The context used to retrieve connection information
     * @return True if the application is connected, false if else.
     */
    public static boolean isConnected(android.content.Context context) {
        return (!me.ccrama.redditslide.Reddit.appRestart.contains("forceoffline")) && (me.ccrama.redditslide.util.NetworkUtil.getConnectivityStatus(context) != me.ccrama.redditslide.util.NetworkUtil.Status.NONE);
    }

    public static boolean isConnectedNoOverride(android.content.Context context) {
        return me.ccrama.redditslide.util.NetworkUtil.getConnectivityStatus(context) != me.ccrama.redditslide.util.NetworkUtil.Status.NONE;
    }

    /**
     * Checks if the network is connected to WiFi.
     *
     * @param context
     * 		The context used to retrieve connection information
     * @return True if the application is connected, false if else.
     */
    public static boolean isConnectedWifi(android.content.Context context) {
        return me.ccrama.redditslide.util.NetworkUtil.getConnectivityStatus(context) == me.ccrama.redditslide.util.NetworkUtil.Status.WIFI;
    }

    /**
     * A simplified list of connectivity statuses. See {@link ConnectivityManager}'s {@code TYPE_*} for a full list.
     *
     * @author Matthew Dean
     */
    public enum Status {

        /**
         * Operating on a wireless connection
         */
        WIFI,
        /**
         * Operating on 3G, 4G, 4G LTE, etc.
         */
        MOBILE,
        /**
         * No connection present
         */
        NONE;}
}