package me.ccrama.redditslide.Tumblr;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({ "meta", "response" })
public class Example {
    @com.fasterxml.jackson.annotation.JsonProperty("meta")
    private me.ccrama.redditslide.Tumblr.Meta meta;

    @com.fasterxml.jackson.annotation.JsonProperty("response")
    private me.ccrama.redditslide.Tumblr.Response response;

    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.Map<java.lang.String, java.lang.Object> additionalProperties = new java.util.HashMap<java.lang.String, java.lang.Object>();

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

    @com.fasterxml.jackson.annotation.JsonAnyGetter
    public java.util.Map<java.lang.String, java.lang.Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @com.fasterxml.jackson.annotation.JsonAnySetter
    public void setAdditionalProperty(java.lang.String name, java.lang.Object value) {
        this.additionalProperties.put(name, value);
    }
}