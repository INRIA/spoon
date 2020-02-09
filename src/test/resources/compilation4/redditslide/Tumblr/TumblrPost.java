package me.ccrama.redditslide.Tumblr;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude;
@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({ "meta", "response" })
public class TumblrPost {
    @com.fasterxml.jackson.annotation.JsonProperty("meta")
    private me.ccrama.redditslide.Tumblr.Meta meta;

    @com.fasterxml.jackson.annotation.JsonProperty("response")
    private me.ccrama.redditslide.Tumblr.Response response;

    /**
     *
     *
     * @return The meta
     */
    @com.fasterxml.jackson.annotation.JsonProperty("meta")
    public me.ccrama.redditslide.Tumblr.Meta getMeta() {
        return meta;
    }

    /**
     *
     *
     * @param meta
     * 		The meta
     */
    @com.fasterxml.jackson.annotation.JsonProperty("meta")
    public void setMeta(me.ccrama.redditslide.Tumblr.Meta meta) {
        this.meta = meta;
    }

    /**
     *
     *
     * @return The response
     */
    @com.fasterxml.jackson.annotation.JsonProperty("response")
    public me.ccrama.redditslide.Tumblr.Response getResponse() {
        return response;
    }

    /**
     *
     *
     * @param response
     * 		The response
     */
    @com.fasterxml.jackson.annotation.JsonProperty("response")
    public void setResponse(me.ccrama.redditslide.Tumblr.Response response) {
        this.response = response;
    }
}