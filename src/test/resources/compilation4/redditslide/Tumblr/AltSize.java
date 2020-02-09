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
@com.fasterxml.jackson.annotation.JsonPropertyOrder({ "url", "width", "height" })
public class AltSize {
    @com.fasterxml.jackson.annotation.JsonProperty("url")
    private java.lang.String url;

    @com.fasterxml.jackson.annotation.JsonProperty("width")
    private java.lang.Integer width;

    @com.fasterxml.jackson.annotation.JsonProperty("height")
    private java.lang.Integer height;

    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.Map<java.lang.String, java.lang.Object> additionalProperties = new java.util.HashMap<java.lang.String, java.lang.Object>();

    /**
     *
     *
     * @return The url
     */
    @com.fasterxml.jackson.annotation.JsonProperty("url")
    public java.lang.String getUrl() {
        return url;
    }

    /**
     *
     *
     * @param url
     * 		The url
     */
    @com.fasterxml.jackson.annotation.JsonProperty("url")
    public void setUrl(java.lang.String url) {
        this.url = url;
    }

    /**
     *
     *
     * @return The width
     */
    @com.fasterxml.jackson.annotation.JsonProperty("width")
    public java.lang.Integer getWidth() {
        return width;
    }

    /**
     *
     *
     * @param width
     * 		The width
     */
    @com.fasterxml.jackson.annotation.JsonProperty("width")
    public void setWidth(java.lang.Integer width) {
        this.width = width;
    }

    /**
     *
     *
     * @return The height
     */
    @com.fasterxml.jackson.annotation.JsonProperty("height")
    public java.lang.Integer getHeight() {
        return height;
    }

    /**
     *
     *
     * @param height
     * 		The height
     */
    @com.fasterxml.jackson.annotation.JsonProperty("height")
    public void setHeight(java.lang.Integer height) {
        this.height = height;
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