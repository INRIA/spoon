package me.ccrama.redditslide.Synccit;
import java.util.Scanner;
import java.io.InputStream;
/**
 * https://github.com/drakeapps/synccit#example-json-update-call
 */
abstract class SynccitUpdateTask extends me.ccrama.redditslide.Synccit.SynccitTask {
    @java.lang.SuppressWarnings("unused")
    private static final java.lang.String TAG = me.ccrama.redditslide.Synccit.SynccitUpdateTask.class.getSimpleName();

    private static final java.lang.String UPDATE_MODE = "update";

    SynccitUpdateTask(java.lang.String devName) {
        super(devName);
    }

    @java.lang.Override
    protected java.lang.String getMode() {
        return me.ccrama.redditslide.Synccit.SynccitUpdateTask.UPDATE_MODE;
    }

    @java.lang.Override
    protected me.ccrama.redditslide.Synccit.SynccitResponse onInput(java.lang.String in) throws java.lang.Exception {
        org.json.JSONObject obj = new org.json.JSONObject(in);
        java.lang.String key = (obj.has("success")) ? "success" : "error";
        return new me.ccrama.redditslide.Synccit.SynccitResponse(key, obj.get(key).toString());
    }
}