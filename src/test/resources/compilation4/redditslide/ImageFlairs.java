package me.ccrama.redditslide;
import java.util.regex.Pattern;
import me.ccrama.redditslide.Activities.SendMessage;
import me.ccrama.redditslide.util.LogUtil;
import java.util.Locale;
import me.ccrama.redditslide.util.OkHttpImageDownloader;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.io.IOException;
import java.util.List;
import java.io.File;
import java.util.Collections;
/**
 * Created by Carlos on 4/15/2017.
 */
public class ImageFlairs {
    public static void syncFlairs(final android.content.Context context, final java.lang.String subreddit) {
        new me.ccrama.redditslide.ImageFlairs.StylesheetFetchTask(subreddit, context) {
            @java.lang.Override
            protected void onPostExecute(me.ccrama.redditslide.ImageFlairs.FlairStylesheet flairStylesheet) {
                super.onPostExecute(flairStylesheet);
                d.dismiss();
                if (flairStylesheet != null) {
                    me.ccrama.redditslide.ImageFlairs.flairs.edit().putBoolean(subreddit.toLowerCase(java.util.Locale.ENGLISH), true).commit();
                    d = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(context).setTitle("Subreddit flairs synced").setMessage(("Slide found and synced " + flairStylesheet.count) + " image flairs").setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, null).show();
                } else {
                    com.afollestad.materialdialogs.AlertDialogWrapper.Builder b = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(context).setTitle("Error syncing subreddit flairs").setMessage(("Slide could not find any subreddit flairs to sync from /r/" + subreddit) + "'s stylesheet.").setPositiveButton(me.ccrama.redditslide.R.string.btn_ok, null);
                    if (me.ccrama.redditslide.Authentication.isLoggedIn) {
                        b.setNeutralButton("Report no flairs", new android.content.DialogInterface.OnClickListener() {
                            @java.lang.Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                android.widget.Toast.makeText(context, "Not all subreddits can be parsed, but send a message to SlideBot and hopefully we can add support for this subreddit :)\n\nPlease, only send one report.", android.widget.Toast.LENGTH_LONG);
                                android.content.Intent i = new android.content.Intent(context, me.ccrama.redditslide.Activities.SendMessage.class);
                                i.putExtra(me.ccrama.redditslide.Activities.SendMessage.EXTRA_NAME, "slidebot");
                                i.putExtra(me.ccrama.redditslide.Activities.SendMessage.EXTRA_MESSAGE, "/r/" + subreddit);
                                i.putExtra(me.ccrama.redditslide.Activities.SendMessage.EXTRA_REPLY, "Subreddit flair");
                                context.startActivity(i);
                            }
                        });
                    }
                    d = b.show();
                }
            }

            @java.lang.Override
            protected void onPreExecute() {
                d = new com.afollestad.materialdialogs.MaterialDialog.Builder(context).progress(true, 100).content(me.ccrama.redditslide.R.string.misc_please_wait).title("Syncing flairs...").cancelable(false).show();
            }
        }.execute();
    }

    static class StylesheetFetchTask extends android.os.AsyncTask<java.lang.Void, java.lang.Void, me.ccrama.redditslide.ImageFlairs.FlairStylesheet> {
        java.lang.String subreddit;

        android.content.Context context;

        android.app.Dialog d;

        StylesheetFetchTask(java.lang.String subreddit, android.content.Context context) {
            super();
            this.context = context;
            this.subreddit = subreddit;
        }

        @java.lang.Override
        protected me.ccrama.redditslide.ImageFlairs.FlairStylesheet doInBackground(java.lang.Void... params) {
            try {
                net.dean.jraw.http.HttpRequest r = new net.dean.jraw.http.HttpRequest.Builder().host("reddit.com").path(("/r/" + subreddit) + "/stylesheet").expected(net.dean.jraw.http.MediaTypes.CSS.type()).build();
                net.dean.jraw.http.RestResponse response = me.ccrama.redditslide.Authentication.reddit.execute(r);
                java.lang.String stylesheet = response.getRaw();
                java.util.ArrayList<java.lang.String> allImages = new java.util.ArrayList<>();
                me.ccrama.redditslide.ImageFlairs.FlairStylesheet flairStylesheet = new me.ccrama.redditslide.ImageFlairs.FlairStylesheet(stylesheet);
                int count = 0;
                for (java.lang.String s : flairStylesheet.getListOfFlairIds()) {
                    java.lang.String classDef = flairStylesheet.getClass(flairStylesheet.stylesheetString, "flair-" + s);
                    try {
                        java.lang.String backgroundURL = flairStylesheet.getBackgroundURL(classDef);
                        if (backgroundURL == null)
                            backgroundURL = flairStylesheet.defaultURL;

                        if (!allImages.contains(backgroundURL))
                            allImages.add(backgroundURL);

                    } catch (java.lang.Exception e) {
                        // e.printStackTrace();
                    }
                }
                if (flairStylesheet.defaultURL != null) {
                    me.ccrama.redditslide.util.LogUtil.v("Default url is " + flairStylesheet.defaultURL);
                    allImages.add(flairStylesheet.defaultURL);
                }
                for (java.lang.String backgroundURL : allImages) {
                    flairStylesheet.cacheFlairsByFile(subreddit, backgroundURL, context);
                }
                return flairStylesheet;
            } catch (java.lang.Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static android.content.SharedPreferences flairs;

    public static boolean isSynced(java.lang.String subreddit) {
        return me.ccrama.redditslide.ImageFlairs.flairs.contains(subreddit.toLowerCase(java.util.Locale.ENGLISH));
    }

    public static class CropTransformation {
        private int width;

        private int height;

        private int x;

        private int y;

        private java.lang.String id;

        public CropTransformation(android.content.Context context, java.lang.String id, int width, int height, int x, int y) {
            super();
            this.id = id;
            this.width = width;
            this.height = height;
            this.x = x;
            this.y = y;
        }

        public android.graphics.Bitmap transform(android.graphics.Bitmap bitmap, boolean isPercentage) throws java.lang.Exception {
            int nX;
            int nY;
            if (isPercentage) {
                nX = java.lang.Math.max(0, java.lang.Math.min(bitmap.getWidth() - 1, (bitmap.getWidth() * x) / 100));
                nY = java.lang.Math.max(0, java.lang.Math.min(bitmap.getHeight() - 1, (bitmap.getHeight() * y) / 100));
            } else {
                nX = java.lang.Math.max(0, java.lang.Math.min(bitmap.getWidth() - 1, x));
                nY = java.lang.Math.max(0, java.lang.Math.min(bitmap.getHeight() - 1, y));
            }
            int nWidth = java.lang.Math.max(1, java.lang.Math.min((bitmap.getWidth() - nX) - 1, width));
            int nHeight = java.lang.Math.max(1, java.lang.Math.min((bitmap.getHeight() - nY) - 1, height));
            me.ccrama.redditslide.util.LogUtil.v((((((((((((("Flair loaded: " + id) + " size: ") + nWidth) + "x") + nHeight) + " location: ") + nX) + ":") + nY) + " and bit is ") + bitmap.getWidth()) + ":") + bitmap.getHeight());
            android.graphics.Bitmap b = android.graphics.Bitmap.createBitmap(bitmap, nX, nY, nWidth, nHeight);
            return b;
        }
    }

    static class FlairStylesheet {
        java.lang.String stylesheetString;

        me.ccrama.redditslide.ImageFlairs.FlairStylesheet.Dimensions defaultDimension = new me.ccrama.redditslide.ImageFlairs.FlairStylesheet.Dimensions();

        me.ccrama.redditslide.ImageFlairs.FlairStylesheet.Location defaultLocation = new me.ccrama.redditslide.ImageFlairs.FlairStylesheet.Location();

        java.lang.String defaultURL = "";

        int count;

        me.ccrama.redditslide.ImageFlairs.FlairStylesheet.Dimensions prevDimension = null;

        class Dimensions {
            int width;

            int height;

            java.lang.Boolean scale = false;

            java.lang.Boolean missing = true;

            Dimensions(int width, int height) {
                this.width = width;
                this.height = height;
                if (height == (-1)) {
                    scale = true;
                }
                missing = false;
            }

            Dimensions() {
            }
        }

        class Location {
            int x;

            int y;

            java.lang.Boolean isPercentage = false;

            java.lang.Boolean missing = true;

            Location(int x, int y) {
                this.x = x;
                this.y = y;
                missing = false;
            }

            Location(int x, int y, boolean isPercentage) {
                this.x = x;
                this.y = y;
                this.isPercentage = isPercentage;
                missing = false;
            }

            Location() {
            }
        }

        FlairStylesheet(java.lang.String stylesheetString) {
            stylesheetString = stylesheetString.replaceAll("@media[^{]+\\{([\\s\\S]+?\\})\\s*\\}", "");
            stylesheetString = stylesheetString.replaceAll("~.", " .");
            this.stylesheetString = stylesheetString;
            java.lang.String baseFlairDef = getClass(stylesheetString, "flair");
            if (baseFlairDef == null)
                return;

            me.ccrama.redditslide.util.LogUtil.v("Base is " + baseFlairDef);
            // Attempts to find default dimension, offset and image URL
            defaultDimension = getBackgroundSize(baseFlairDef);
            me.ccrama.redditslide.util.LogUtil.v((("Default dimens are " + defaultDimension.width) + ":") + defaultDimension.height);
            defaultLocation = getBackgroundPosition(baseFlairDef);
            defaultURL = getBackgroundURL(baseFlairDef);
            count = 0;
        }

        /**
         * Get class definition string by class name.
         *
         * @param cssDefinitionString
         * 		
         * @param className
         * 		
         * @return 
         */
        java.lang.String getClass(java.lang.String cssDefinitionString, java.lang.String className) {
            java.util.regex.Pattern propertyDefinition = java.util.regex.Pattern.compile(("(?<! )\\." + className) + "(?!-|\\[|[A-Za-z0-9_.])([^\\{]*)*\\{(.+?)\\}");
            java.util.regex.Matcher matches = propertyDefinition.matcher(cssDefinitionString);
            java.lang.String properties = null;
            while (matches.find()) {
                if (properties == null)
                    properties = "";

                properties = (matches.group(2) + ";") + properties;// append properties to simulate property overriding

            } 
            return properties;
        }

        /**
         * Get property value inside a class definition by property name.
         *
         * @param classDefinitionsString
         * 		
         * @param property
         * 		
         * @return 
         */
        java.lang.String getProperty(java.lang.String classDefinitionsString, java.lang.String property) {
            java.util.regex.Pattern propertyDefinition = java.util.regex.Pattern.compile(("(?<!-)" + property) + "\\s*:\\s*(.+?)(;|$)");
            java.util.regex.Matcher matches = propertyDefinition.matcher(classDefinitionsString);
            if (matches.find()) {
                return matches.group(1);
            } else {
                return null;
            }
        }

        // Attempts to get a real integer value instead of "auto", if possible
        java.lang.String getPropertyTryNoAuto(java.lang.String classDefinitionsString, java.lang.String property) {
            java.util.regex.Pattern propertyDefinition = java.util.regex.Pattern.compile(("(?<!-)" + property) + "\\s*:\\s*(.+?)(;|$)");
            java.util.regex.Matcher matches = propertyDefinition.matcher(classDefinitionsString);
            java.lang.String defaultString;
            if (matches.find()) {
                defaultString = matches.group(1);
            } else {
                return null;
            }
            me.ccrama.redditslide.util.LogUtil.v("Has auto");
            while ((defaultString.contains("auto") || ((!defaultString.contains("%")) || (!defaultString.contains("px")))) && matches.find()) {
                defaultString = matches.group(1);
            } 
            me.ccrama.redditslide.util.LogUtil.v("Returning " + defaultString);
            return defaultString;
        }

        java.lang.String getPropertyBackgroundUrl(java.lang.String classDefinitionsString) {
            java.util.regex.Pattern propertyDefinition = java.util.regex.Pattern.compile("background:url\\([\"\'](.+?)[\"\']\\)");
            java.util.regex.Matcher matches = propertyDefinition.matcher(classDefinitionsString);
            if (matches.find()) {
                return matches.group(1);
            } else {
                return null;
            }
        }

        /**
         * Get flair background url in class definition.
         *
         * @param classDefinitionString
         * 		
         * @return 
         */
        java.lang.String getBackgroundURL(java.lang.String classDefinitionString) {
            java.util.regex.Pattern urlDefinition = java.util.regex.Pattern.compile("url\\([\"\'](.+?)[\"\']\\)");
            java.lang.String backgroundProperty = getPropertyBackgroundUrl(classDefinitionString);
            if (backgroundProperty != null) {
                // check "background"
                java.lang.String url = backgroundProperty;
                if (url.startsWith("//"))
                    url = "https:" + url;

                return url;
            }
            // either backgroundProperty is null or url cannot be found
            java.lang.String backgroundImageProperty = getProperty(classDefinitionString, "background-image");
            if (backgroundImageProperty != null) {
                // check "background-image"
                java.util.regex.Matcher matches = urlDefinition.matcher(backgroundImageProperty);
                if (matches.find()) {
                    java.lang.String url = matches.group(1);
                    if (url.startsWith("//"))
                        url = "https:" + url;

                    return url;
                }
            }
            // could not find any background url
            return null;
        }

        /**
         * Get background dimension in class definition.
         *
         * @param classDefinitionString
         * 		
         * @return 
         */
        me.ccrama.redditslide.ImageFlairs.FlairStylesheet.Dimensions getBackgroundSize(java.lang.String classDefinitionString) {
            java.util.regex.Pattern numberDefinition = java.util.regex.Pattern.compile("(\\d+)\\s*px");
            boolean autoWidth = false;
            boolean autoHeight = false;
            // check common properties used to define width
            java.lang.String widthProperty = getPropertyTryNoAuto(classDefinitionString, "width");
            if (widthProperty == null) {
                widthProperty = getPropertyTryNoAuto(classDefinitionString, "min-width");
            } else if (widthProperty.equals("auto")) {
                autoWidth = true;
            }
            if (widthProperty == null) {
                widthProperty = getProperty(classDefinitionString, "text-indent");
            }
            if (widthProperty == null)
                return new me.ccrama.redditslide.ImageFlairs.FlairStylesheet.Dimensions();

            // check common properties used to define height
            java.lang.String heightProperty = getPropertyTryNoAuto(classDefinitionString, "height");
            if (heightProperty == null) {
                heightProperty = getPropertyTryNoAuto(classDefinitionString, "min-height");
            } else if (heightProperty.equals("auto")) {
                autoHeight = true;
            }
            if (heightProperty == null)
                return new me.ccrama.redditslide.ImageFlairs.FlairStylesheet.Dimensions();

            int width = 0;
            int height = 0;
            java.util.regex.Matcher matches;
            if (!autoWidth) {
                matches = numberDefinition.matcher(widthProperty);
                if (matches.find()) {
                    width = java.lang.Integer.parseInt(matches.group(1));
                } else {
                    return new me.ccrama.redditslide.ImageFlairs.FlairStylesheet.Dimensions();
                }
            }
            if (!autoHeight) {
                matches = numberDefinition.matcher(heightProperty);
                if (matches.find()) {
                    height = java.lang.Integer.parseInt(matches.group(1));
                } else {
                    return new me.ccrama.redditslide.ImageFlairs.FlairStylesheet.Dimensions();
                }
            }
            if (autoWidth) {
                width = height;
            }
            if (autoHeight) {
                height = width;
            }
            return new me.ccrama.redditslide.ImageFlairs.FlairStylesheet.Dimensions(width, height);
        }

        /**
         * Get background scaling in class definition.
         *
         * @param classDefinitionString
         * 		
         * @return 
         */
        me.ccrama.redditslide.ImageFlairs.FlairStylesheet.Dimensions getBackgroundScaling(java.lang.String classDefinitionString) {
            java.util.regex.Pattern positionDefinitionPx = java.util.regex.Pattern.compile("([+-]?\\d+|0)(px\\s|\\s)+(|([+-]?\\d+|0)(px|))");
            java.lang.String backgroundPositionProperty = getProperty(classDefinitionString, "background-size");
            java.lang.String backgroundPositionPropertySecondary = getProperty(classDefinitionString, "background-size");
            if (((backgroundPositionProperty == null) && (backgroundPositionPropertySecondary == null)) || (((backgroundPositionProperty == null) && (!backgroundPositionPropertySecondary.contains("px "))) && (!backgroundPositionPropertySecondary.contains("px;")))) {
                return new me.ccrama.redditslide.ImageFlairs.FlairStylesheet.Dimensions();
            }
            java.util.regex.Matcher matches = positionDefinitionPx.matcher(backgroundPositionProperty);
            if (matches.find()) {
                return new me.ccrama.redditslide.ImageFlairs.FlairStylesheet.Dimensions(java.lang.Integer.parseInt(matches.group(1)), matches.groupCount() < 2 ? java.lang.Integer.parseInt(matches.group(3)) : -1);
            } else {
                return new me.ccrama.redditslide.ImageFlairs.FlairStylesheet.Dimensions();
            }
        }

        /**
         * Get background offset in class definition.
         *
         * @param classDefinitionString
         * 		
         * @return 
         */
        me.ccrama.redditslide.ImageFlairs.FlairStylesheet.Location getBackgroundPosition(java.lang.String classDefinitionString) {
            java.util.regex.Pattern positionDefinitionPx = java.util.regex.Pattern.compile("([+-]?\\d+|0)(px\\s|\\s)+([+-]?\\d+|0)(px|)");
            java.util.regex.Pattern positionDefinitionPercentage = java.util.regex.Pattern.compile("([+-]?\\d+|0)(%\\s|\\s)+([+-]?\\d+|0)(%|)");
            java.lang.String backgroundPositionProperty = getProperty(classDefinitionString, "background-position");
            if (backgroundPositionProperty == null) {
                backgroundPositionProperty = getProperty(classDefinitionString, "background");
                if (backgroundPositionProperty == null) {
                    return new me.ccrama.redditslide.ImageFlairs.FlairStylesheet.Location();
                }
            }
            java.util.regex.Matcher matches = positionDefinitionPx.matcher(backgroundPositionProperty);
            try {
                if (matches.find()) {
                    return new me.ccrama.redditslide.ImageFlairs.FlairStylesheet.Location(-java.lang.Integer.parseInt(matches.group(1)), -java.lang.Integer.parseInt(matches.group(3)));
                } else {
                    matches = positionDefinitionPercentage.matcher(backgroundPositionProperty);
                    if (matches.find()) {
                        return new me.ccrama.redditslide.ImageFlairs.FlairStylesheet.Location(java.lang.Integer.parseInt(matches.group(1)), java.lang.Integer.parseInt(matches.group(3)), true);
                    }
                }
            } catch (java.lang.NumberFormatException ignored) {
            }
            return new me.ccrama.redditslide.ImageFlairs.FlairStylesheet.Location();
        }

        me.ccrama.redditslide.ImageFlairs.FlairStylesheet.Dimensions getBackgroundOffset(java.lang.String classDefinitionString) {
            java.util.regex.Pattern positionDefinitionPx = java.util.regex.Pattern.compile("([+-]?\\d+|0)\\/+([+-]?\\d+|0)(px|)");
            java.lang.String backgroundPositionProperty = getProperty(classDefinitionString, "background");
            if (backgroundPositionProperty == null) {
                return new me.ccrama.redditslide.ImageFlairs.FlairStylesheet.Dimensions();
            }
            java.util.regex.Matcher matches = positionDefinitionPx.matcher(backgroundPositionProperty);
            try {
                if (matches.find()) {
                    return new me.ccrama.redditslide.ImageFlairs.FlairStylesheet.Dimensions(java.lang.Integer.parseInt(matches.group(2)), java.lang.Integer.parseInt(matches.group(2)));
                }
            } catch (java.lang.NumberFormatException ignored) {
            }
            return new me.ccrama.redditslide.ImageFlairs.FlairStylesheet.Dimensions();
        }

        /**
         * Request a flair by flair id. `.into` can be chained onto this method call.
         *
         * @param id
         * 		
         * @param context
         * 		
         * @return 
         */
        void cacheFlairsByFile(final java.lang.String sub, final java.lang.String filename, final android.content.Context context) {
            final java.util.ArrayList<java.lang.String> flairsToGet = new java.util.ArrayList<>();
            me.ccrama.redditslide.util.LogUtil.v("Doing sheet " + filename);
            for (java.lang.String s : getListOfFlairIds()) {
                java.lang.String classDef = getClass(stylesheetString, "flair-" + s);
                if ((classDef != null) && (!classDef.isEmpty())) {
                    java.lang.String backgroundURL = getBackgroundURL(classDef);
                    if (backgroundURL == null)
                        backgroundURL = defaultURL;

                    if ((backgroundURL != null) && backgroundURL.equalsIgnoreCase(filename)) {
                        flairsToGet.add(s);
                    }
                }
            }
            java.lang.String scaling = getClass(stylesheetString, "flair");
            final me.ccrama.redditslide.ImageFlairs.FlairStylesheet.Dimensions backScaling;
            final me.ccrama.redditslide.ImageFlairs.FlairStylesheet.Dimensions offset;
            if (scaling != null) {
                backScaling = getBackgroundScaling(scaling);
                offset = getBackgroundOffset(scaling);
                me.ccrama.redditslide.util.LogUtil.v("Offset is " + offset.width);
            } else {
                backScaling = new me.ccrama.redditslide.ImageFlairs.FlairStylesheet.Dimensions();
                offset = new me.ccrama.redditslide.ImageFlairs.FlairStylesheet.Dimensions();
            }
            if (((!backScaling.missing) && (!backScaling.scale)) || ((!offset.missing) && (!offset.scale))) {
                android.graphics.Bitmap loaded = me.ccrama.redditslide.ImageFlairs.getFlairImageLoader(context).loadImageSync(filename, new com.nostra13.universalimageloader.core.assist.ImageSize(backScaling.width, backScaling.height));
                if (loaded != null) {
                    android.graphics.Bitmap b;
                    if (backScaling.missing || (backScaling.width < offset.width)) {
                        b = android.graphics.Bitmap.createScaledBitmap(loaded, offset.width, offset.height, false);
                    } else {
                        b = android.graphics.Bitmap.createScaledBitmap(loaded, backScaling.width, backScaling.height, false);
                    }
                    loadingComplete(b, sub, context, filename, flairsToGet);
                    loaded.recycle();
                }
            } else {
                android.graphics.Bitmap loadedB = me.ccrama.redditslide.ImageFlairs.getFlairImageLoader(context).loadImageSync(filename);
                if (loadedB != null) {
                    if (backScaling.scale) {
                        int width = backScaling.width;
                        int height = loadedB.getHeight();
                        int scaledHeight = (height * width) / loadedB.getWidth();
                        loadingComplete(android.graphics.Bitmap.createScaledBitmap(loadedB, width, scaledHeight, false), sub, context, filename, flairsToGet);
                        loadedB.recycle();
                    } else {
                        loadingComplete(loadedB, sub, context, filename, flairsToGet);
                    }
                }
            }
        }

        private void loadingComplete(android.graphics.Bitmap loadedImage, java.lang.String sub, android.content.Context context, java.lang.String filename, java.util.ArrayList<java.lang.String> flairsToGet) {
            if (loadedImage != null) {
                for (java.lang.String id : flairsToGet) {
                    android.graphics.Bitmap newBit = null;
                    java.lang.String classDef = this.getClass(stylesheetString, "flair-" + id);
                    if (classDef == null)
                        break;

                    me.ccrama.redditslide.ImageFlairs.FlairStylesheet.Dimensions flairDimensions = getBackgroundSize(classDef);
                    if (flairDimensions.missing) {
                        flairDimensions = defaultDimension;
                    }
                    prevDimension = flairDimensions;
                    me.ccrama.redditslide.ImageFlairs.FlairStylesheet.Location flairLocation = getBackgroundPosition(classDef);
                    if (flairLocation.missing)
                        flairLocation = defaultLocation;

                    me.ccrama.redditslide.util.LogUtil.v((((((((("Flair: " + id) + " size: ") + flairDimensions.width) + "x") + flairDimensions.height) + " location: ") + flairLocation.x) + ":") + flairLocation.y);
                    try {
                        newBit = new me.ccrama.redditslide.ImageFlairs.CropTransformation(context, id, flairDimensions.width, flairDimensions.height, flairLocation.x, flairLocation.y).transform(loadedImage, flairLocation.isPercentage);
                    } catch (java.lang.Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        me.ccrama.redditslide.ImageFlairs.getFlairImageLoader(context).getDiskCache().save((sub.toLowerCase(java.util.Locale.ENGLISH) + ":") + id.toLowerCase(java.util.Locale.ENGLISH), newBit);
                        count += 1;
                    } catch (java.lang.Exception e) {
                        e.printStackTrace();
                    }
                }
                loadedImage.recycle();
            } else {
                me.ccrama.redditslide.util.LogUtil.v("Loaded image is null for " + filename);
            }
        }

        /**
         * Util function
         *
         * @return 
         */
        java.util.List<java.lang.String> getListOfFlairIds() {
            java.util.regex.Pattern flairId = java.util.regex.Pattern.compile("\\.flair-(\\w+)\\s*(\\{|\\,|\\:|)");
            java.util.regex.Matcher matches = flairId.matcher(stylesheetString);
            java.util.List<java.lang.String> flairIds = new java.util.ArrayList<>();
            while (matches.find()) {
                if (!flairIds.contains(matches.group(1)))
                    flairIds.add(matches.group(1));

            } 
            java.util.Collections.sort(flairIds);
            return flairIds;
        }
    }

    public static class FlairImageLoader extends com.nostra13.universalimageloader.core.ImageLoader {
        private static volatile me.ccrama.redditslide.ImageFlairs.FlairImageLoader instance;

        /**
         * Returns singletone class instance
         */
        public static me.ccrama.redditslide.ImageFlairs.FlairImageLoader getInstance() {
            if (me.ccrama.redditslide.ImageFlairs.FlairImageLoader.instance == null) {
                synchronized(com.nostra13.universalimageloader.core.ImageLoader.class) {
                    if (me.ccrama.redditslide.ImageFlairs.FlairImageLoader.instance == null) {
                        me.ccrama.redditslide.ImageFlairs.FlairImageLoader.instance = new me.ccrama.redditslide.ImageFlairs.FlairImageLoader();
                    }
                }
            }
            return me.ccrama.redditslide.ImageFlairs.FlairImageLoader.instance;
        }
    }

    public static me.ccrama.redditslide.ImageFlairs.FlairImageLoader getFlairImageLoader(android.content.Context context) {
        if (me.ccrama.redditslide.ImageFlairs.imageLoader == null) {
            return me.ccrama.redditslide.ImageFlairs.initFlairImageLoader(context);
        } else {
            return me.ccrama.redditslide.ImageFlairs.imageLoader;
        }
    }

    public static me.ccrama.redditslide.ImageFlairs.FlairImageLoader imageLoader;

    public static java.io.File getCacheDirectory(android.content.Context context) {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED) && (context.getExternalCacheDir() != null)) {
            return new java.io.File(context.getExternalCacheDir(), "flairs");
        }
        return new java.io.File(context.getCacheDir(), "flairs");
    }

    public static me.ccrama.redditslide.ImageFlairs.FlairImageLoader initFlairImageLoader(android.content.Context context) {
        long discCacheSize = (1024 * 1024) * 100;// 100 MB limit

        com.nostra13.universalimageloader.cache.disc.DiskCache discCache;
        java.io.File dir = me.ccrama.redditslide.ImageFlairs.getCacheDirectory(context);
        int threadPoolSize;
        discCacheSize *= 100;
        threadPoolSize = 7;
        if (discCacheSize > 0) {
            try {
                dir.mkdir();
                discCache = new com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache(dir, new com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator(), discCacheSize);
            } catch (java.io.IOException e) {
                discCache = new com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache(dir);
            }
        } else {
            discCache = new com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache(dir);
        }
        me.ccrama.redditslide.ImageFlairs.options = new com.nostra13.universalimageloader.core.DisplayImageOptions.Builder().cacheOnDisk(true).imageScaleType(com.nostra13.universalimageloader.core.assist.ImageScaleType.NONE).cacheInMemory(false).resetViewBeforeLoading(false).build();
        com.nostra13.universalimageloader.core.ImageLoaderConfiguration config = new com.nostra13.universalimageloader.core.ImageLoaderConfiguration.Builder(context).threadPoolSize(threadPoolSize).denyCacheImageMultipleSizesInMemory().diskCache(discCache).threadPoolSize(4).imageDownloader(new me.ccrama.redditslide.util.OkHttpImageDownloader(context)).defaultDisplayImageOptions(me.ccrama.redditslide.ImageFlairs.options).build();
        if (me.ccrama.redditslide.ImageFlairs.FlairImageLoader.getInstance().isInited()) {
            me.ccrama.redditslide.ImageFlairs.FlairImageLoader.getInstance().destroy();
        }
        me.ccrama.redditslide.ImageFlairs.imageLoader = me.ccrama.redditslide.ImageFlairs.FlairImageLoader.getInstance();
        me.ccrama.redditslide.ImageFlairs.imageLoader.init(config);
        return me.ccrama.redditslide.ImageFlairs.imageLoader;
    }

    public static com.nostra13.universalimageloader.core.DisplayImageOptions options;
}