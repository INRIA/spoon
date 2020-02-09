package me.ccrama.redditslide;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;
/**
 * Created by Deadl on 26/11/2015.
 */
public class SecretConstants {
    private static java.lang.String apiKey;

    private static java.lang.String base64EncodedPublicKey;

    public static java.lang.String getBase64EncodedPublicKey(android.content.Context context) {
        if (me.ccrama.redditslide.SecretConstants.base64EncodedPublicKey == null) {
            java.io.InputStream input;
            try {
                input = context.getAssets().open("secretconstants.properties");
                java.util.Properties properties = new java.util.Properties();
                properties.load(input);
                me.ccrama.redditslide.SecretConstants.base64EncodedPublicKey = properties.getProperty("base64EncodedPublicKey");
            } catch (java.io.IOException e) {
                // file not found
                me.ccrama.redditslide.SecretConstants.base64EncodedPublicKey = "";
            }
        }
        return me.ccrama.redditslide.SecretConstants.base64EncodedPublicKey;
    }

    public static java.lang.String getApiKey(android.content.Context context) {
        if (me.ccrama.redditslide.SecretConstants.apiKey == null) {
            java.io.InputStream input;
            try {
                input = context.getAssets().open("secretconstants.properties");
                java.util.Properties properties = new java.util.Properties();
                properties.load(input);
                me.ccrama.redditslide.SecretConstants.apiKey = properties.getProperty("apiKey");
            } catch (java.io.IOException e) {
                // file not found
                me.ccrama.redditslide.SecretConstants.apiKey = "";
            }
        }
        return me.ccrama.redditslide.SecretConstants.apiKey;
    }

    public static java.lang.String getImgurApiKey(android.content.Context context) {
        if (me.ccrama.redditslide.SecretConstants.apiKey == null) {
            java.io.InputStream input;
            try {
                input = context.getAssets().open("secretconstants.properties");
                java.util.Properties properties = new java.util.Properties();
                properties.load(input);
                me.ccrama.redditslide.SecretConstants.apiKey = properties.getProperty("imgur");
            } catch (java.io.IOException e) {
                // file not found
                me.ccrama.redditslide.SecretConstants.apiKey = "3P3GlZj91emshgWU6YuQL98Q9Zihp1c2vCSjsnOQLIchXPzDLh";// Testing key, will not work in production

            }
        }
        return me.ccrama.redditslide.SecretConstants.apiKey;
    }
}