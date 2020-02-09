package me.ccrama.redditslide.ImgurAlbum;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.util.HashMap;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({ "data", "success", "status" })
public class SingleAlbumImage {
    @com.fasterxml.jackson.annotation.JsonProperty("data")
    private me.ccrama.redditslide.ImgurAlbum.SingleImage data;

    @com.fasterxml.jackson.annotation.JsonProperty("success")
    private java.lang.Boolean success;

    @com.fasterxml.jackson.annotation.JsonProperty("status")
    private java.lang.Integer status;

    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.Map<java.lang.String, java.lang.Object> additionalProperties = new java.util.HashMap<>();

    /**
     *
     *
     * @return The data
     */
    @com.fasterxml.jackson.annotation.JsonProperty("data")
    public me.ccrama.redditslide.ImgurAlbum.SingleImage getData() {
        return data;
    }

    /**
     *
     *
     * @param data
     * 		The data
     */
    @com.fasterxml.jackson.annotation.JsonProperty("data")
    public void setData(me.ccrama.redditslide.ImgurAlbum.SingleImage data) {
        this.data = data;
    }

    /**
     *
     *
     * @return The success
     */
    @com.fasterxml.jackson.annotation.JsonProperty("success")
    public java.lang.Boolean getSuccess() {
        return success;
    }

    /**
     *
     *
     * @param success
     * 		The success
     */
    @com.fasterxml.jackson.annotation.JsonProperty("success")
    public void setSuccess(java.lang.Boolean success) {
        this.success = success;
    }

    /**
     *
     *
     * @return The status
     */
    @com.fasterxml.jackson.annotation.JsonProperty("status")
    public java.lang.Integer getStatus() {
        return status;
    }

    /**
     *
     *
     * @param status
     * 		The status
     */
    @com.fasterxml.jackson.annotation.JsonProperty("status")
    public void setStatus(java.lang.Integer status) {
        this.status = status;
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