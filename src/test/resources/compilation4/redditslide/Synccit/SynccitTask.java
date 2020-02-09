package me.ccrama.redditslide.Synccit;
import me.ccrama.redditslide.Synccit.http.HttpPostTask;
public abstract class SynccitTask extends me.ccrama.redditslide.Synccit.http.HttpPostTask<me.ccrama.redditslide.Synccit.SynccitResponse> {
    private static final java.lang.String TAG = me.ccrama.redditslide.Synccit.SynccitTask.class.getSimpleName();

    private static final java.lang.String API_URL = "http://api.synccit.com/api.php";

    private static final java.lang.String PARAM_TYPE = "type";

    private static final java.lang.String PARAM_DATA = "data";

    private static final java.lang.String TYPE_JSON = "json";

    private static final java.lang.String KEY_USERNAME = "username";

    private static final java.lang.String KEY_AUTH = "auth";

    private static final java.lang.String KEY_DEV = "dev";

    private static final java.lang.String KEY_MODE = "mode";

    private static final java.lang.String KEY_API = "api";

    private static final java.lang.String KEY_LINKS = "links";

    private static final java.lang.String API_LEVEL = "1";

    /**
     * developer name
     */
    private java.lang.String devName;

    SynccitTask(java.lang.String devName) {
        super(me.ccrama.redditslide.Synccit.SynccitTask.API_URL);
        this.devName = devName;
    }

    @java.lang.Override
    protected me.ccrama.redditslide.Synccit.SynccitResponse doInBackground(java.lang.String... linkIds) {
        if (android.text.TextUtils.isEmpty(getUsername()) || android.text.TextUtils.isEmpty(getAuth())) {
            android.util.Log.i(me.ccrama.redditslide.Synccit.SynccitTask.TAG, "synccit username or auth not set. aborting");
            return null;
        }
        java.lang.String data;
        try {
            data = buildJson(linkIds);
        } catch (java.lang.Exception e) {
            android.util.Log.e(me.ccrama.redditslide.Synccit.SynccitTask.TAG, "buildJson", e);
            return null;
        }
        return super.doInBackground(me.ccrama.redditslide.Synccit.SynccitTask.PARAM_TYPE, me.ccrama.redditslide.Synccit.SynccitTask.TYPE_JSON, me.ccrama.redditslide.Synccit.SynccitTask.PARAM_DATA, data);
    }

    /**
     * https://github.com/drakeapps/synccit#example-json-update-call
     *
     * {
     * "username"  : "james",
     * "auth"      : "9m89x0",
     * "dev"       : "synccit json",
     * "mode"      : "update",
     * "links"     : [
     * {
     * "id" : "111111"
     * },
     * {
     * "id" : "222222",
     * "comments" : "132"
     * },
     * {
     * "id" : "333333",
     * "comments" : "313",
     * "both" : true
     * },
     * {
     * "id" : "444444"
     * }
     * ]
     * }
     *
     * @throws JSONException
     * 		
     */
    private java.lang.String buildJson(java.lang.String... linkIds) throws org.json.JSONException {
        org.json.JSONObject rootOb = new org.json.JSONObject();
        rootOb.put(me.ccrama.redditslide.Synccit.SynccitTask.KEY_USERNAME, getUsername());
        rootOb.put(me.ccrama.redditslide.Synccit.SynccitTask.KEY_AUTH, getAuth());
        rootOb.put(me.ccrama.redditslide.Synccit.SynccitTask.KEY_DEV, devName);
        rootOb.put(me.ccrama.redditslide.Synccit.SynccitTask.KEY_MODE, getMode());
        rootOb.put(me.ccrama.redditslide.Synccit.SynccitTask.KEY_API, me.ccrama.redditslide.Synccit.SynccitTask.API_LEVEL);
        org.json.JSONArray links = new org.json.JSONArray();
        for (java.lang.String linkId : linkIds) {
            org.json.JSONObject linkObject = new org.json.JSONObject();
            linkObject.put("id", linkId);
            links.put(linkObject);
        }
        rootOb.put(me.ccrama.redditslide.Synccit.SynccitTask.KEY_LINKS, links);
        return rootOb.toString();
    }

    protected abstract java.lang.String getUsername();

    protected abstract java.lang.String getAuth();

    protected abstract java.lang.String getMode();

    @java.lang.Override
    protected void onPostExecute(me.ccrama.redditslide.Synccit.SynccitResponse result) {
        super.onPostExecute(result);
        if ((result != null) && result.isError()) {
            android.util.Log.w(me.ccrama.redditslide.Synccit.SynccitTask.TAG, "synccit error: " + result.getMessage());
        }
    }
}