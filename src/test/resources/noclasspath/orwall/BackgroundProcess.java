package org.ethack.orwall;

import android.app.IntentService;
import android.content.Intent;

import org.ethack.orwall.iptables.InitializeIptables;
import org.ethack.orwall.lib.Constants;

/**
 * Created by cedric on 7/31/14.
 */
public class BackgroundProcess extends IntentService {

    public BackgroundProcess() {
        super("BackroundProcess");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        String action = workIntent.getStringExtra(Constants.ACTION);

        if (action.equals(Constants.ACTION_PORTAL)) {
            boolean activate = workIntent.getBooleanExtra(Constants.PARAM_ACTIVATE, false);
            managePortal(activate);
        }
    }

    private void managePortal(boolean activate) {
        InitializeIptables initializeIptables = new InitializeIptables(this);
        initializeIptables.enableCaptiveDetection(activate, this);
    }
}
