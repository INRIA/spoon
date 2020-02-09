/* Copyright 2015 Michal Pawlowski

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package me.ccrama.redditslide.Views;
import me.ccrama.redditslide.R;
import java.util.ArrayList;
/**
 * Helper class that iterates through Toolbar views, and sets dynamically icons and texts color
 * Created by chomi3 on 2015-01-19.
 */
public class ToolbarColorizeHelper {
    private ToolbarColorizeHelper() {
    }

    /**
     * Use this method to colorize toolbar icons to the desired target color
     *
     * @param toolbarView
     * 		toolbar view being colored
     * @param toolbarIconsColor
     * 		the target color of toolbar icons
     * @param activity
     * 		reference to activity needed to register observers
     */
    public static void colorizeToolbar(android.support.v7.widget.Toolbar toolbarView, int toolbarIconsColor, android.app.Activity activity) {
        final android.graphics.PorterDuffColorFilter colorFilter = new android.graphics.PorterDuffColorFilter(toolbarIconsColor, android.graphics.PorterDuff.Mode.MULTIPLY);
        for (int i = 0; i < toolbarView.getChildCount(); i++) {
            final android.view.View v = toolbarView.getChildAt(i);
            // Step 1 : Changing the color of back button (or open drawer button).
            if (v instanceof android.widget.ImageButton) {
                // Action Bar back button
                ((android.widget.ImageButton) (v)).getDrawable().setColorFilter(colorFilter);
            }
            if (v instanceof android.support.v7.widget.ActionMenuView) {
                for (int j = 0; j < ((android.support.v7.widget.ActionMenuView) (v)).getChildCount(); j++) {
                    // Step 2: Changing the color of any ActionMenuViews - icons that are not back button, nor text, nor overflow menu icon.
                    // Colorize the ActionViews -> all icons that are NOT: back button | overflow menu
                    final android.view.View innerView = ((android.support.v7.widget.ActionMenuView) (v)).getChildAt(j);
                    if (innerView instanceof android.support.v7.view.menu.ActionMenuItemView) {
                        for (int k = 0; k < ((android.support.v7.view.menu.ActionMenuItemView) (innerView)).getCompoundDrawables().length; k++) {
                            if (((android.support.v7.view.menu.ActionMenuItemView) (innerView)).getCompoundDrawables()[k] != null) {
                                final int finalK = k;
                                // Important to set the color filter in seperate thread, by adding it to the message queue
                                // Won't work otherwise.
                                innerView.post(new java.lang.Runnable() {
                                    @java.lang.Override
                                    public void run() {
                                        ((android.support.v7.view.menu.ActionMenuItemView) (innerView)).getCompoundDrawables()[finalK].setColorFilter(colorFilter);
                                    }
                                });
                            }
                        }
                    }
                }
            }
            // Step 3: Changing the color of title and subtitle.
            toolbarView.setTitleTextColor(toolbarIconsColor);
            toolbarView.setSubtitleTextColor(toolbarIconsColor);
            // Step 4: Changing the color of the Overflow Menu icon.
            me.ccrama.redditslide.Views.ToolbarColorizeHelper.setOverflowButtonColor(activity, colorFilter);
        }
    }

    /**
     * It's important to set overflowDescription atribute in styles, so we can grab the reference
     * to the overflow icon. Check: res/values/styles.xml
     *
     * @param activity
     * 		
     * @param colorFilter
     * 		
     */
    private static void setOverflowButtonColor(final android.app.Activity activity, final android.graphics.PorterDuffColorFilter colorFilter) {
        final java.lang.String overflowDescription = activity.getString(me.ccrama.redditslide.R.string.abc_action_menu_overflow_description);
        final android.view.ViewGroup decorView = ((android.view.ViewGroup) (activity.getWindow().getDecorView()));
        final android.view.ViewTreeObserver viewTreeObserver = decorView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {
            @java.lang.Override
            public void onGlobalLayout() {
                final java.util.ArrayList<android.view.View> outViews = new java.util.ArrayList<>();
                decorView.findViewsWithText(outViews, overflowDescription, android.view.View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
                if (outViews.isEmpty()) {
                    return;
                }
                android.widget.ImageView overflow = ((android.widget.ImageView) (outViews.get(0)));
                overflow.setColorFilter(colorFilter);
                me.ccrama.redditslide.Views.ToolbarColorizeHelper.removeOnGlobalLayoutListener(decorView, this);
            }
        });
    }

    private static void removeOnGlobalLayoutListener(android.view.View v, android.view.ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }
}