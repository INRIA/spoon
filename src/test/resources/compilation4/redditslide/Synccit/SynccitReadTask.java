package me.ccrama.redditslide.Synccit;
import java.util.Scanner;
import java.io.InputStream;
import java.util.HashSet;
/**
 * https://github.com/drakeapps/synccit#example-json-read-call
 */
abstract class SynccitReadTask extends me.ccrama.redditslide.Synccit.SynccitTask {
    private static final java.lang.String READ_MODE = "read";

    SynccitReadTask(java.lang.String devName) {
        super(devName);
    }

    @java.lang.Override
    protected java.lang.String getMode() {
        return me.ccrama.redditslide.Synccit.SynccitReadTask.READ_MODE;
    }

    @java.lang.Override
    protected me.ccrama.redditslide.Synccit.SynccitResponse onInput(java.lang.String in) throws java.lang.Exception {
        java.util.HashSet<java.lang.String> visitedLinkIds = new java.util.HashSet<>();
        try {
            org.json.JSONArray links = new org.json.JSONArray(in);
            int length = links.length();
            for (int i = 0; i < length; i++) {
                org.json.JSONObject linkNode = ((org.json.JSONObject) (links.get(i)));
                visitedLinkIds.add(linkNode.get("id").toString());
            }
            onVisited(visitedLinkIds);
        } catch (org.json.JSONException ex) {
            org.json.JSONObject node = new org.json.JSONObject(in);
            if (node.has("error")) {
                return new me.ccrama.redditslide.Synccit.SynccitResponse("error", node.get("error").toString());
            }
        }
        return null;
    }

    protected abstract void onVisited(java.util.HashSet<java.lang.String> visitedThreadIds);
}