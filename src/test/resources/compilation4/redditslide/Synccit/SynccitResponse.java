package me.ccrama.redditslide.Synccit;
/**
 * https://github.com/drakeapps/synccit#example-json-update-call
 */
class SynccitResponse {
    private java.lang.String key;

    private java.lang.String value;

    SynccitResponse(java.lang.String key, java.lang.String value) {
        this.key = key;
        this.value = value;
    }

    public boolean isSuccess() {
        return "success".equals(key);
    }

    public boolean isError() {
        return "error".equals(key);
    }

    public java.lang.String getMessage() {
        return value;
    }
}