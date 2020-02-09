package me.ccrama.redditslide.Synccit;
import me.ccrama.redditslide.SettingValues;
/**
 * Created by carlo_000 on 2/16/2016.
 */
public class MySynccitUpdateTask extends me.ccrama.redditslide.Synccit.SynccitUpdateTask {
    private static final java.lang.String MY_DEV_NAME = "slide_for_reddit";

    public MySynccitUpdateTask() {
        super(me.ccrama.redditslide.Synccit.MySynccitUpdateTask.MY_DEV_NAME);
    }

    @java.lang.Override
    protected java.lang.String getUsername() {
        return me.ccrama.redditslide.SettingValues.synccitName;
    }

    @java.lang.Override
    protected java.lang.String getAuth() {
        return me.ccrama.redditslide.SettingValues.synccitAuth;
    }

    @java.lang.Override
    protected java.lang.String getUserAgent() {
        return "slide_for_reddit";
    }
}