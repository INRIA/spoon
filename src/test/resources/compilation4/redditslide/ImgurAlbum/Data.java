package me.ccrama.redditslide.ImgurAlbum;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.util.HashMap;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;
@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({ "count", "images" })
public class Data {
    @com.fasterxml.jackson.annotation.JsonProperty("count")
    private java.lang.Integer count;

    @com.fasterxml.jackson.annotation.JsonProperty("images")
    private java.util.List<me.ccrama.redditslide.ImgurAlbum.Image> images = new java.util.ArrayList<>();

    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.Map<java.lang.String, java.lang.Object> additionalProperties = new java.util.HashMap<>();

    /**
     *
     *
     * @return The count
     */
    @com.fasterxml.jackson.annotation.JsonProperty("count")
    public java.lang.Integer getCount() {
        return count;
    }

    /**
     *
     *
     * @param count
     * 		The count
     */
    @com.fasterxml.jackson.annotation.JsonProperty("count")
    public void setCount(java.lang.Integer count) {
        this.count = count;
    }

    /**
     *
     *
     * @return The images
     */
    @com.fasterxml.jackson.annotation.JsonProperty("images")
    public java.util.List<me.ccrama.redditslide.ImgurAlbum.Image> getImages() {
        return images;
    }

    /**
     *
     *
     * @param images
     * 		The images
     */
    @com.fasterxml.jackson.annotation.JsonProperty("images")
    public void setImages(java.util.List<me.ccrama.redditslide.ImgurAlbum.Image> images) {
        this.images = images;
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