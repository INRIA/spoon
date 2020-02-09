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
@com.fasterxml.jackson.annotation.JsonPropertyOrder({ "header_full_width", "header_full_height", "header_focus_width", "header_focus_height", "avatar_shape", "background_color", "body_font", "header_bounds", "header_image", "header_image_focused", "header_image_scaled", "header_stretch", "link_color", "show_avatar", "show_description", "show_header_image", "show_title", "title_color", "title_font", "title_font_weight" })
public class Theme {
    @com.fasterxml.jackson.annotation.JsonProperty("header_full_width")
    private java.lang.Integer headerFullWidth;

    @com.fasterxml.jackson.annotation.JsonProperty("header_full_height")
    private java.lang.Integer headerFullHeight;

    @com.fasterxml.jackson.annotation.JsonProperty("header_focus_width")
    private java.lang.Integer headerFocusWidth;

    @com.fasterxml.jackson.annotation.JsonProperty("header_focus_height")
    private java.lang.Integer headerFocusHeight;

    @com.fasterxml.jackson.annotation.JsonProperty("avatar_shape")
    private java.lang.String avatarShape;

    @com.fasterxml.jackson.annotation.JsonProperty("background_color")
    private java.lang.String backgroundColor;

    @com.fasterxml.jackson.annotation.JsonProperty("body_font")
    private java.lang.String bodyFont;

    @com.fasterxml.jackson.annotation.JsonProperty("header_bounds")
    private java.lang.String headerBounds;

    @com.fasterxml.jackson.annotation.JsonProperty("header_image")
    private java.lang.String headerImage;

    @com.fasterxml.jackson.annotation.JsonProperty("header_image_focused")
    private java.lang.String headerImageFocused;

    @com.fasterxml.jackson.annotation.JsonProperty("header_image_scaled")
    private java.lang.String headerImageScaled;

    @com.fasterxml.jackson.annotation.JsonProperty("header_stretch")
    private java.lang.Boolean headerStretch;

    @com.fasterxml.jackson.annotation.JsonProperty("link_color")
    private java.lang.String linkColor;

    @com.fasterxml.jackson.annotation.JsonProperty("show_avatar")
    private java.lang.Boolean showAvatar;

    @com.fasterxml.jackson.annotation.JsonProperty("show_description")
    private java.lang.Boolean showDescription;

    @com.fasterxml.jackson.annotation.JsonProperty("show_header_image")
    private java.lang.Boolean showHeaderImage;

    @com.fasterxml.jackson.annotation.JsonProperty("show_title")
    private java.lang.Boolean showTitle;

    @com.fasterxml.jackson.annotation.JsonProperty("title_color")
    private java.lang.String titleColor;

    @com.fasterxml.jackson.annotation.JsonProperty("title_font")
    private java.lang.String titleFont;

    @com.fasterxml.jackson.annotation.JsonProperty("title_font_weight")
    private java.lang.String titleFontWeight;

    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.Map<java.lang.String, java.lang.Object> additionalProperties = new java.util.HashMap<java.lang.String, java.lang.Object>();

    /**
     *
     *
     * @return The headerFullWidth
     */
    @com.fasterxml.jackson.annotation.JsonProperty("header_full_width")
    public java.lang.Integer getHeaderFullWidth() {
        return headerFullWidth;
    }

    /**
     *
     *
     * @param headerFullWidth
     * 		The header_full_width
     */
    @com.fasterxml.jackson.annotation.JsonProperty("header_full_width")
    public void setHeaderFullWidth(java.lang.Integer headerFullWidth) {
        this.headerFullWidth = headerFullWidth;
    }

    /**
     *
     *
     * @return The headerFullHeight
     */
    @com.fasterxml.jackson.annotation.JsonProperty("header_full_height")
    public java.lang.Integer getHeaderFullHeight() {
        return headerFullHeight;
    }

    /**
     *
     *
     * @param headerFullHeight
     * 		The header_full_height
     */
    @com.fasterxml.jackson.annotation.JsonProperty("header_full_height")
    public void setHeaderFullHeight(java.lang.Integer headerFullHeight) {
        this.headerFullHeight = headerFullHeight;
    }

    /**
     *
     *
     * @return The headerFocusWidth
     */
    @com.fasterxml.jackson.annotation.JsonProperty("header_focus_width")
    public java.lang.Integer getHeaderFocusWidth() {
        return headerFocusWidth;
    }

    /**
     *
     *
     * @param headerFocusWidth
     * 		The header_focus_width
     */
    @com.fasterxml.jackson.annotation.JsonProperty("header_focus_width")
    public void setHeaderFocusWidth(java.lang.Integer headerFocusWidth) {
        this.headerFocusWidth = headerFocusWidth;
    }

    /**
     *
     *
     * @return The headerFocusHeight
     */
    @com.fasterxml.jackson.annotation.JsonProperty("header_focus_height")
    public java.lang.Integer getHeaderFocusHeight() {
        return headerFocusHeight;
    }

    /**
     *
     *
     * @param headerFocusHeight
     * 		The header_focus_height
     */
    @com.fasterxml.jackson.annotation.JsonProperty("header_focus_height")
    public void setHeaderFocusHeight(java.lang.Integer headerFocusHeight) {
        this.headerFocusHeight = headerFocusHeight;
    }

    /**
     *
     *
     * @return The avatarShape
     */
    @com.fasterxml.jackson.annotation.JsonProperty("avatar_shape")
    public java.lang.String getAvatarShape() {
        return avatarShape;
    }

    /**
     *
     *
     * @param avatarShape
     * 		The avatar_shape
     */
    @com.fasterxml.jackson.annotation.JsonProperty("avatar_shape")
    public void setAvatarShape(java.lang.String avatarShape) {
        this.avatarShape = avatarShape;
    }

    /**
     *
     *
     * @return The backgroundColor
     */
    @com.fasterxml.jackson.annotation.JsonProperty("background_color")
    public java.lang.String getBackgroundColor() {
        return backgroundColor;
    }

    /**
     *
     *
     * @param backgroundColor
     * 		The background_color
     */
    @com.fasterxml.jackson.annotation.JsonProperty("background_color")
    public void setBackgroundColor(java.lang.String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     *
     *
     * @return The bodyFont
     */
    @com.fasterxml.jackson.annotation.JsonProperty("body_font")
    public java.lang.String getBodyFont() {
        return bodyFont;
    }

    /**
     *
     *
     * @param bodyFont
     * 		The body_font
     */
    @com.fasterxml.jackson.annotation.JsonProperty("body_font")
    public void setBodyFont(java.lang.String bodyFont) {
        this.bodyFont = bodyFont;
    }

    /**
     *
     *
     * @return The headerBounds
     */
    @com.fasterxml.jackson.annotation.JsonProperty("header_bounds")
    public java.lang.String getHeaderBounds() {
        return headerBounds;
    }

    /**
     *
     *
     * @param headerBounds
     * 		The header_bounds
     */
    @com.fasterxml.jackson.annotation.JsonProperty("header_bounds")
    public void setHeaderBounds(java.lang.String headerBounds) {
        this.headerBounds = headerBounds;
    }

    /**
     *
     *
     * @return The headerImage
     */
    @com.fasterxml.jackson.annotation.JsonProperty("header_image")
    public java.lang.String getHeaderImage() {
        return headerImage;
    }

    /**
     *
     *
     * @param headerImage
     * 		The header_image
     */
    @com.fasterxml.jackson.annotation.JsonProperty("header_image")
    public void setHeaderImage(java.lang.String headerImage) {
        this.headerImage = headerImage;
    }

    /**
     *
     *
     * @return The headerImageFocused
     */
    @com.fasterxml.jackson.annotation.JsonProperty("header_image_focused")
    public java.lang.String getHeaderImageFocused() {
        return headerImageFocused;
    }

    /**
     *
     *
     * @param headerImageFocused
     * 		The header_image_focused
     */
    @com.fasterxml.jackson.annotation.JsonProperty("header_image_focused")
    public void setHeaderImageFocused(java.lang.String headerImageFocused) {
        this.headerImageFocused = headerImageFocused;
    }

    /**
     *
     *
     * @return The headerImageScaled
     */
    @com.fasterxml.jackson.annotation.JsonProperty("header_image_scaled")
    public java.lang.String getHeaderImageScaled() {
        return headerImageScaled;
    }

    /**
     *
     *
     * @param headerImageScaled
     * 		The header_image_scaled
     */
    @com.fasterxml.jackson.annotation.JsonProperty("header_image_scaled")
    public void setHeaderImageScaled(java.lang.String headerImageScaled) {
        this.headerImageScaled = headerImageScaled;
    }

    /**
     *
     *
     * @return The headerStretch
     */
    @com.fasterxml.jackson.annotation.JsonProperty("header_stretch")
    public java.lang.Boolean getHeaderStretch() {
        return headerStretch;
    }

    /**
     *
     *
     * @param headerStretch
     * 		The header_stretch
     */
    @com.fasterxml.jackson.annotation.JsonProperty("header_stretch")
    public void setHeaderStretch(java.lang.Boolean headerStretch) {
        this.headerStretch = headerStretch;
    }

    /**
     *
     *
     * @return The linkColor
     */
    @com.fasterxml.jackson.annotation.JsonProperty("link_color")
    public java.lang.String getLinkColor() {
        return linkColor;
    }

    /**
     *
     *
     * @param linkColor
     * 		The link_color
     */
    @com.fasterxml.jackson.annotation.JsonProperty("link_color")
    public void setLinkColor(java.lang.String linkColor) {
        this.linkColor = linkColor;
    }

    /**
     *
     *
     * @return The showAvatar
     */
    @com.fasterxml.jackson.annotation.JsonProperty("show_avatar")
    public java.lang.Boolean getShowAvatar() {
        return showAvatar;
    }

    /**
     *
     *
     * @param showAvatar
     * 		The show_avatar
     */
    @com.fasterxml.jackson.annotation.JsonProperty("show_avatar")
    public void setShowAvatar(java.lang.Boolean showAvatar) {
        this.showAvatar = showAvatar;
    }

    /**
     *
     *
     * @return The showDescription
     */
    @com.fasterxml.jackson.annotation.JsonProperty("show_description")
    public java.lang.Boolean getShowDescription() {
        return showDescription;
    }

    /**
     *
     *
     * @param showDescription
     * 		The show_description
     */
    @com.fasterxml.jackson.annotation.JsonProperty("show_description")
    public void setShowDescription(java.lang.Boolean showDescription) {
        this.showDescription = showDescription;
    }

    /**
     *
     *
     * @return The showHeaderImage
     */
    @com.fasterxml.jackson.annotation.JsonProperty("show_header_image")
    public java.lang.Boolean getShowHeaderImage() {
        return showHeaderImage;
    }

    /**
     *
     *
     * @param showHeaderImage
     * 		The show_header_image
     */
    @com.fasterxml.jackson.annotation.JsonProperty("show_header_image")
    public void setShowHeaderImage(java.lang.Boolean showHeaderImage) {
        this.showHeaderImage = showHeaderImage;
    }

    /**
     *
     *
     * @return The showTitle
     */
    @com.fasterxml.jackson.annotation.JsonProperty("show_title")
    public java.lang.Boolean getShowTitle() {
        return showTitle;
    }

    /**
     *
     *
     * @param showTitle
     * 		The show_title
     */
    @com.fasterxml.jackson.annotation.JsonProperty("show_title")
    public void setShowTitle(java.lang.Boolean showTitle) {
        this.showTitle = showTitle;
    }

    /**
     *
     *
     * @return The titleColor
     */
    @com.fasterxml.jackson.annotation.JsonProperty("title_color")
    public java.lang.String getTitleColor() {
        return titleColor;
    }

    /**
     *
     *
     * @param titleColor
     * 		The title_color
     */
    @com.fasterxml.jackson.annotation.JsonProperty("title_color")
    public void setTitleColor(java.lang.String titleColor) {
        this.titleColor = titleColor;
    }

    /**
     *
     *
     * @return The titleFont
     */
    @com.fasterxml.jackson.annotation.JsonProperty("title_font")
    public java.lang.String getTitleFont() {
        return titleFont;
    }

    /**
     *
     *
     * @param titleFont
     * 		The title_font
     */
    @com.fasterxml.jackson.annotation.JsonProperty("title_font")
    public void setTitleFont(java.lang.String titleFont) {
        this.titleFont = titleFont;
    }

    /**
     *
     *
     * @return The titleFontWeight
     */
    @com.fasterxml.jackson.annotation.JsonProperty("title_font_weight")
    public java.lang.String getTitleFontWeight() {
        return titleFontWeight;
    }

    /**
     *
     *
     * @param titleFontWeight
     * 		The title_font_weight
     */
    @com.fasterxml.jackson.annotation.JsonProperty("title_font_weight")
    public void setTitleFontWeight(java.lang.String titleFontWeight) {
        this.titleFontWeight = titleFontWeight;
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