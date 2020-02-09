package me.ccrama.redditslide.util;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by Carlos on 9/10/2016.
 */
public class NetworkStateReceiver extends android.content.BroadcastReceiver {
    protected java.util.List<me.ccrama.redditslide.util.NetworkStateReceiver.NetworkStateReceiverListener> listeners;

    protected java.lang.Boolean connected;

    public NetworkStateReceiver() {
        listeners = new java.util.ArrayList<me.ccrama.redditslide.util.NetworkStateReceiver.NetworkStateReceiverListener>();
        connected = null;
    }

    public void onReceive(android.content.Context context, android.content.Intent intent) {
        if ((intent == null) || (intent.getExtras() == null))
            return;

        connected = me.ccrama.redditslide.util.NetworkUtil.isConnected(context);
        notifyStateToAll();
    }

    private void notifyStateToAll() {
        for (me.ccrama.redditslide.util.NetworkStateReceiver.NetworkStateReceiverListener listener : listeners)
            notifyState(listener);

    }

    private void notifyState(me.ccrama.redditslide.util.NetworkStateReceiver.NetworkStateReceiverListener listener) {
        if ((connected == null) || (listener == null))
            return;

        if (connected == true)
            listener.networkAvailable();
        else
            listener.networkUnavailable();

    }

    public void addListener(me.ccrama.redditslide.util.NetworkStateReceiver.NetworkStateReceiverListener l) {
        listeners.add(l);
        notifyState(l);
    }

    public void removeListener(me.ccrama.redditslide.util.NetworkStateReceiver.NetworkStateReceiverListener l) {
        listeners.remove(l);
    }

    public interface NetworkStateReceiverListener {
        public void networkAvailable();

        public void networkUnavailable();
    }
}