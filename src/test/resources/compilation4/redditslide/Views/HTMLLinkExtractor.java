package me.ccrama.redditslide.Views;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
/**
 * Created by ccrama on 5/17/2015.
 */
class HTMLLinkExtractor {
    private static final java.lang.String HTML_A_TAG_PATTERN = "(?i)<a([^>]+)>(.+?)</a>";

    private static final java.lang.String HTML_A_HREF_TAG_PATTERN = "\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|\'[^\']*\'|([^\'\">\\s]+))";

    private final java.util.regex.Pattern patternTag;

    private final java.util.regex.Pattern patternLink;

    public HTMLLinkExtractor() {
        patternTag = java.util.regex.Pattern.compile(me.ccrama.redditslide.Views.HTMLLinkExtractor.HTML_A_TAG_PATTERN);
        patternLink = java.util.regex.Pattern.compile(me.ccrama.redditslide.Views.HTMLLinkExtractor.HTML_A_HREF_TAG_PATTERN);
    }

    /**
     * Validate html with regular expression
     *
     * @param html
     * 		html content for validation
     * @return Vector links and link text
     */
    public java.util.ArrayList<me.ccrama.redditslide.Views.HTMLLinkExtractor.HtmlLink> grabHTMLLinks(final java.lang.String html) {
        java.util.ArrayList<me.ccrama.redditslide.Views.HTMLLinkExtractor.HtmlLink> result = new java.util.ArrayList<>();
        java.util.regex.Matcher matcherTag = patternTag.matcher(html);
        while (matcherTag.find()) {
            java.lang.String href = matcherTag.group(1);// href

            java.lang.String linkText = matcherTag.group(2);// link text

            java.util.regex.Matcher matcherLink = patternLink.matcher(href);
            while (matcherLink.find()) {
                java.lang.String link = matcherLink.group(1);// link

                me.ccrama.redditslide.Views.HTMLLinkExtractor.HtmlLink obj = new me.ccrama.redditslide.Views.HTMLLinkExtractor.HtmlLink();
                obj.setLink(link);
                obj.setLinkText(linkText);
                result.add(obj);
            } 
        } 
        return result;
    }

    public class HtmlLink {
        java.lang.String link;

        java.lang.String linkText;

        HtmlLink() {
        }

        @java.lang.Override
        public java.lang.String toString() {
            return (("Link : " + this.link) + " Link Text : ") + this.linkText;
        }

        public java.lang.String getLink() {
            return link;
        }

        public void setLink(java.lang.String link) {
            this.link = replaceInvalidChar(link);
        }

        public java.lang.String getLinkText() {
            return linkText;
        }

        public void setLinkText(java.lang.String linkText) {
            this.linkText = linkText;
        }

        private java.lang.String replaceInvalidChar(java.lang.String link) {
            link = link.replaceAll("'", "");
            link = link.replaceAll("\"", "");
            return link;
        }
    }
}