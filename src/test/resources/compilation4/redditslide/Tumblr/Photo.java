package me.ccrama.redditslide.Tumblr;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;
@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({ "caption", "alt_sizes", "original_size" })
public class Photo {
    @com.fasterxml.jackson.annotation.JsonProperty("caption")
    private java.lang.String caption;

    @com.fasterxml.jackson.annotation.JsonProperty("alt_sizes")
    private java.util.List<me.ccrama.redditslide.Tumblr.AltSize> altSizes = new java.util.ArrayList<me.ccrama.redditslide.Tumblr.AltSize>();

    @com.fasterxml.jackson.annotation.JsonProperty("original_size")
    private me.ccrama.redditslide.Tumblr.OriginalSize originalSize;

    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.Map<java.lang.String, java.lang.Object> additionalProperties = new java.util.HashMap<java.lang.String, java.lang.Object>();

    /**
     *
     *
     * @return The caption
     */
    @com.fasterxml.jackson.annotation.JsonProperty("caption")
    public java.lang.String getCaption() {
        return caption;
    }

    /**
     *
     *
     * @param caption
     * 		The caption
     */
    @com.fasterxml.jackson.annotation.JsonProperty("caption")
    public void setCaption(java.lang.String caption) {
        this.caption = caption;
    }

    /**
     *
     *
     * @return The altSizes
     */
    @com.fasterxml.jackson.annotation.JsonProperty("alt_sizes")
    public java.util.List<me.ccrama.redditslide.Tumblr.AltSize> getAltSizes() {
        return altSizes;
    }

    /**
     *
     *
     * @param altSizes
     * 		The alt_sizes
     */
    @com.fasterxml.jackson.annotation.JsonProperty("alt_sizes")
    public void setAltSizes(java.util.List<me.ccrama.redditslide.Tumblr.AltSize> altSizes) {
        this.altSizes = altSizes;
    }

    /**
     *
     *
     * @return The originalSize
     */
    @com.fasterxml.jackson.annotation.JsonProperty("original_size")
    public me.ccrama.redditslide.Tumblr.OriginalSize getOriginalSize() {
        return originalSize;
    }

    /**
     *
     *
     * @param originalSize
     * 		The original_size
     */
    @com.fasterxml.jackson.annotation.JsonProperty("original_size")
    public void setOriginalSize(me.ccrama.redditslide.Tumblr.OriginalSize originalSize) {
        this.originalSize = originalSize;
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