package me.ccrama.redditslide.Views;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Visuals.Palette;
import java.util.ArrayList;
import me.ccrama.redditslide.SettingValues;
/**
 * Created by ccrama on 9/18/2015.
 */
public class CreateCardView {
    public static android.view.View CreateViewNews(android.view.ViewGroup viewGroup) {
        return android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.submission_news, viewGroup, false);
    }

    public static android.view.View CreateView(android.view.ViewGroup viewGroup) {
        me.ccrama.redditslide.Views.CreateCardView.CardEnum cardEnum = me.ccrama.redditslide.SettingValues.defaultCardView;
        android.view.View v = null;
        switch (cardEnum) {
            case LARGE :
                if (me.ccrama.redditslide.SettingValues.middleImage) {
                    v = android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.submission_largecard_middle, viewGroup, false);
                } else {
                    v = android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.submission_largecard, viewGroup, false);
                }
                break;
            case LIST :
                v = android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.submission_list, viewGroup, false);
                // if the radius is set to 0 on KitKat--it crashes.
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ((android.support.v7.widget.CardView) (v.findViewById(me.ccrama.redditslide.R.id.card))).setRadius(0.0F);
                }
                break;
            case DESKTOP :
                v = android.view.LayoutInflater.from(viewGroup.getContext()).inflate(me.ccrama.redditslide.R.layout.submission_list_desktop, viewGroup, false);
                // if the radius is set to 0 on KitKat--it crashes.
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ((android.support.v7.widget.CardView) (v.findViewById(me.ccrama.redditslide.R.id.card))).setRadius(0.0F);
                }
                break;
        }
        android.view.View thumbImage = v.findViewById(me.ccrama.redditslide.R.id.thumbimage2);
        /**
         * If the user wants small thumbnails, revert the list style to the "old" list view.
         * The "old" thumbnails were (70dp x 70dp).
         * Adjusts the paddingTop of the innerrelative, and adjusts the margins on the thumbnail.
         */
        if (!me.ccrama.redditslide.SettingValues.bigThumbnails) {
            if (me.ccrama.redditslide.SettingValues.defaultCardView == me.ccrama.redditslide.Views.CreateCardView.CardEnum.DESKTOP) {
                final int SQUARE_THUMBNAIL_SIZE = 48;
                thumbImage.getLayoutParams().height = me.ccrama.redditslide.Reddit.dpToPxVertical(SQUARE_THUMBNAIL_SIZE);
                thumbImage.getLayoutParams().width = me.ccrama.redditslide.Reddit.dpToPxHorizontal(SQUARE_THUMBNAIL_SIZE);
            } else {
                final int SQUARE_THUMBNAIL_SIZE = 70;
                thumbImage.getLayoutParams().height = me.ccrama.redditslide.Reddit.dpToPxVertical(SQUARE_THUMBNAIL_SIZE);
                thumbImage.getLayoutParams().width = me.ccrama.redditslide.Reddit.dpToPxHorizontal(SQUARE_THUMBNAIL_SIZE);
                final int EIGHT_DP_Y = me.ccrama.redditslide.Reddit.dpToPxVertical(8);
                final int EIGHT_DP_X = me.ccrama.redditslide.Reddit.dpToPxHorizontal(8);
                ((android.widget.RelativeLayout.LayoutParams) (thumbImage.getLayoutParams())).setMargins(EIGHT_DP_X * 2, EIGHT_DP_Y, EIGHT_DP_X, EIGHT_DP_Y);
                v.findViewById(me.ccrama.redditslide.R.id.innerrelative).setPadding(0, EIGHT_DP_Y, 0, 0);
            }
        }
        me.ccrama.redditslide.Views.CreateCardView.doHideObjects(v);
        return v;
    }

    public static void resetColorCard(android.view.View v) {
        v.setTag(v.getId(), "none");
        android.util.TypedValue background = new android.util.TypedValue();
        v.getContext().getTheme().resolveAttribute(me.ccrama.redditslide.R.attr.card_background, background, true);
        ((android.support.v7.widget.CardView) (v.findViewById(me.ccrama.redditslide.R.id.card))).setCardBackgroundColor(background.data);
        if (!me.ccrama.redditslide.SettingValues.actionbarVisible) {
            for (android.view.View v2 : me.ccrama.redditslide.Views.CreateCardView.getViewsByTag(((android.view.ViewGroup) (v)), "tintactionbar")) {
                v2.setVisibility(android.view.View.GONE);
            }
        }
        me.ccrama.redditslide.Views.CreateCardView.doColor(me.ccrama.redditslide.Views.CreateCardView.getViewsByTag(((android.view.ViewGroup) (v)), "tint"));
        me.ccrama.redditslide.Views.CreateCardView.doColorSecond(me.ccrama.redditslide.Views.CreateCardView.getViewsByTag(((android.view.ViewGroup) (v)), "tintsecond"));
        me.ccrama.redditslide.Views.CreateCardView.doColorSecond(me.ccrama.redditslide.Views.CreateCardView.getViewsByTag(((android.view.ViewGroup) (v)), "tintactionbar"));
    }

    public static void doColor(java.util.ArrayList<android.view.View> v) {
        for (android.view.View v2 : v) {
            if (v2 instanceof android.widget.TextView) {
                ((android.widget.TextView) (v2)).setTextColor(me.ccrama.redditslide.Views.CreateCardView.getCurrentFontColor(v2.getContext()));
            } else if (v2 instanceof android.widget.ImageView) {
                ((android.widget.ImageView) (v2)).setColorFilter(me.ccrama.redditslide.Views.CreateCardView.getCurrentTintColor(v2.getContext()));
            }
        }
    }

    public static void doColorSecond(java.util.ArrayList<android.view.View> v) {
        for (android.view.View v2 : v) {
            if (v2 instanceof android.widget.TextView) {
                ((android.widget.TextView) (v2)).setTextColor(me.ccrama.redditslide.Views.CreateCardView.getSecondFontColor(v2.getContext()));
            } else if (v2 instanceof android.widget.ImageView) {
                ((android.widget.ImageView) (v2)).setColorFilter(me.ccrama.redditslide.Views.CreateCardView.getCurrentTintColor(v2.getContext()));
            }
        }
    }

    public static void resetColor(java.util.ArrayList<android.view.View> v) {
        for (android.view.View v2 : v) {
            if (v2 instanceof android.widget.TextView) {
                ((android.widget.TextView) (v2)).setTextColor(me.ccrama.redditslide.Views.CreateCardView.getWhiteFontColor());
            } else if (v2 instanceof android.widget.ImageView) {
                ((android.widget.ImageView) (v2)).setColorFilter(me.ccrama.redditslide.Views.CreateCardView.getWhiteTintColor(), android.graphics.PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    public static int getStyleAttribColorValue(final android.content.Context context, final int attribResId, final int defaultValue) {
        final android.util.TypedValue tv = new android.util.TypedValue();
        final boolean found = context.getTheme().resolveAttribute(attribResId, tv, true);
        return found ? tv.data : defaultValue;
    }

    private static java.util.ArrayList<android.view.View> getViewsByTag(android.view.ViewGroup root, java.lang.String tag) {
        java.util.ArrayList<android.view.View> views = new java.util.ArrayList<>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final android.view.View child = root.getChildAt(i);
            if (child instanceof android.view.ViewGroup) {
                views.addAll(me.ccrama.redditslide.Views.CreateCardView.getViewsByTag(((android.view.ViewGroup) (child)), tag));
            }
            final java.lang.Object tagObj = child.getTag();
            if ((tagObj != null) && tagObj.equals(tag)) {
                views.add(child);
            }
        }
        return views;
    }

    public static int getCurrentTintColor(android.content.Context v) {
        return me.ccrama.redditslide.Views.CreateCardView.getStyleAttribColorValue(v, me.ccrama.redditslide.R.attr.tintColor, android.graphics.Color.WHITE);
    }

    public static int getWhiteTintColor() {
        return me.ccrama.redditslide.Visuals.Palette.ThemeEnum.DARK.getTint();
    }

    public static int getCurrentFontColor(android.content.Context v) {
        return me.ccrama.redditslide.Views.CreateCardView.getStyleAttribColorValue(v, me.ccrama.redditslide.R.attr.fontColor, android.graphics.Color.WHITE);
    }

    public static int getSecondFontColor(android.content.Context v) {
        return me.ccrama.redditslide.Views.CreateCardView.getStyleAttribColorValue(v, me.ccrama.redditslide.R.attr.tintColor, android.graphics.Color.WHITE);
    }

    public static int getWhiteFontColor() {
        return me.ccrama.redditslide.Visuals.Palette.ThemeEnum.DARK.getFontColor();
    }

    public static void colorCard(java.lang.String sec, android.view.View v, java.lang.String subToMatch, boolean secondary) {
        me.ccrama.redditslide.Views.CreateCardView.resetColorCard(v);
        if (((me.ccrama.redditslide.SettingValues.colorBack && (!me.ccrama.redditslide.SettingValues.colorSubName)) && (me.ccrama.redditslide.Visuals.Palette.getColor(sec) != me.ccrama.redditslide.Visuals.Palette.getDefaultColor())) || (subToMatch.equals("nomatching") && ((me.ccrama.redditslide.SettingValues.colorBack && (!me.ccrama.redditslide.SettingValues.colorSubName)) && (me.ccrama.redditslide.Visuals.Palette.getColor(sec) != me.ccrama.redditslide.Visuals.Palette.getDefaultColor())))) {
            if (((!secondary) && (!me.ccrama.redditslide.SettingValues.colorEverywhere)) || secondary) {
                ((android.support.v7.widget.CardView) (v.findViewById(me.ccrama.redditslide.R.id.card))).setCardBackgroundColor(me.ccrama.redditslide.Visuals.Palette.getColor(sec));
                v.setTag(v.getId(), "color");
                me.ccrama.redditslide.Views.CreateCardView.resetColor(me.ccrama.redditslide.Views.CreateCardView.getViewsByTag(((android.view.ViewGroup) (v)), "tint"));
                me.ccrama.redditslide.Views.CreateCardView.resetColor(me.ccrama.redditslide.Views.CreateCardView.getViewsByTag(((android.view.ViewGroup) (v)), "tintsecond"));
                me.ccrama.redditslide.Views.CreateCardView.resetColor(me.ccrama.redditslide.Views.CreateCardView.getViewsByTag(((android.view.ViewGroup) (v)), "tintactionbar"));
            }
        }
    }

    public static android.view.View setActionbarVisible(boolean isChecked, android.view.ViewGroup parent) {
        me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_ACTIONBAR_VISIBLE, isChecked).apply();
        me.ccrama.redditslide.SettingValues.actionbarVisible = isChecked;
        return me.ccrama.redditslide.Views.CreateCardView.CreateView(parent);
    }

    public static android.view.View setSmallTag(boolean isChecked, android.view.ViewGroup parent) {
        me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_SMALL_TAG, isChecked).apply();
        me.ccrama.redditslide.SettingValues.smallTag = isChecked;
        return me.ccrama.redditslide.Views.CreateCardView.CreateView(parent);
    }

    public static android.view.View setCardViewType(me.ccrama.redditslide.Views.CreateCardView.CardEnum cardEnum, android.view.ViewGroup parent) {
        me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean("middleCard", false).apply();
        me.ccrama.redditslide.SettingValues.middleImage = false;
        me.ccrama.redditslide.SettingValues.prefs.edit().putString("defaultCardViewNew", cardEnum.name()).apply();
        me.ccrama.redditslide.SettingValues.defaultCardView = cardEnum;
        return me.ccrama.redditslide.Views.CreateCardView.CreateView(parent);
    }

    public static android.view.View setBigPicEnabled(java.lang.Boolean b, android.view.ViewGroup parent) {
        me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean("bigPicEnabled", b).apply();
        me.ccrama.redditslide.SettingValues.bigPicEnabled = b;
        me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean("bigPicCropped", false).apply();
        me.ccrama.redditslide.SettingValues.bigPicCropped = false;
        return me.ccrama.redditslide.Views.CreateCardView.CreateView(parent);
    }

    public static android.view.View setBigPicCropped(java.lang.Boolean b, android.view.ViewGroup parent) {
        me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean("bigPicCropped", b).apply();
        me.ccrama.redditslide.SettingValues.bigPicCropped = b;
        me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean("bigPicEnabled", b).apply();
        me.ccrama.redditslide.SettingValues.bigPicEnabled = b;
        return me.ccrama.redditslide.Views.CreateCardView.CreateView(parent);
    }

    public static android.view.View setMiddleCard(boolean b, android.view.ViewGroup parent) {
        me.ccrama.redditslide.SettingValues.prefs.edit().putString("defaultCardViewNew", me.ccrama.redditslide.Views.CreateCardView.CardEnum.LARGE.name()).apply();
        me.ccrama.redditslide.SettingValues.defaultCardView = me.ccrama.redditslide.Views.CreateCardView.CardEnum.LARGE;
        me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean("middleCard", b).apply();
        me.ccrama.redditslide.SettingValues.middleImage = b;
        return me.ccrama.redditslide.Views.CreateCardView.CreateView(parent);
    }

    public static android.view.View setSwitchThumb(boolean b, android.view.ViewGroup parent) {
        me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean(me.ccrama.redditslide.SettingValues.PREF_SWITCH_THUMB, b).apply();
        me.ccrama.redditslide.SettingValues.switchThumb = b;
        return me.ccrama.redditslide.Views.CreateCardView.CreateView(parent);
    }

    private static android.animation.ValueAnimator slideAnimator(int start, int end, final android.view.View v) {
        android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofInt(start, end);
        animator.setInterpolator(new android.support.v4.view.animation.FastOutSlowInInterpolator());
        animator.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {
            @java.lang.Override
            public void onAnimationUpdate(android.animation.ValueAnimator valueAnimator) {
                // Update Height
                int value = ((java.lang.Integer) (valueAnimator.getAnimatedValue()));
                android.view.ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.height = value;
                v.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    private static android.animation.ValueAnimator flipAnimator(boolean isFlipped, final android.view.View v) {
        if (v != null) {
            android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofFloat(isFlipped ? -1.0F : 1.0F, isFlipped ? 1.0F : -1.0F);
            animator.setInterpolator(new android.support.v4.view.animation.FastOutSlowInInterpolator());
            animator.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {
                @java.lang.Override
                public void onAnimationUpdate(android.animation.ValueAnimator valueAnimator) {
                    // Update Height
                    v.setScaleY(((java.lang.Float) (valueAnimator.getAnimatedValue())));
                }
            });
            return animator;
        }
        return null;
    }

    public static void animateIn(android.view.View l) {
        l.setVisibility(android.view.View.VISIBLE);
        android.animation.ValueAnimator mAnimator = me.ccrama.redditslide.Views.CreateCardView.slideAnimator(0, me.ccrama.redditslide.Reddit.dpToPxVertical(36), l);
        mAnimator.start();
    }

    public static void animateOut(final android.view.View l) {
        android.animation.ValueAnimator mAnimator = me.ccrama.redditslide.Views.CreateCardView.slideAnimator(me.ccrama.redditslide.Reddit.dpToPxVertical(36), 0, l);
        mAnimator.addListener(new android.animation.Animator.AnimatorListener() {
            @java.lang.Override
            public void onAnimationStart(android.animation.Animator animation) {
            }

            @java.lang.Override
            public void onAnimationEnd(android.animation.Animator animation) {
                l.setVisibility(android.view.View.GONE);
            }

            @java.lang.Override
            public void onAnimationCancel(android.animation.Animator animation) {
            }

            @java.lang.Override
            public void onAnimationRepeat(android.animation.Animator animation) {
            }
        });
        mAnimator.start();
    }

    public static void toggleActionbar(android.view.View v) {
        if (!me.ccrama.redditslide.SettingValues.actionbarVisible) {
            android.animation.ValueAnimator a = me.ccrama.redditslide.Views.CreateCardView.flipAnimator(v.findViewById(me.ccrama.redditslide.R.id.upvote).getVisibility() == android.view.View.VISIBLE, v.findViewById(me.ccrama.redditslide.R.id.secondMenu));
            if (a != null)
                a.start();

            for (android.view.View v2 : me.ccrama.redditslide.Views.CreateCardView.getViewsByTag(((android.view.ViewGroup) (v)), "tintactionbar")) {
                if ((v2.getId() != me.ccrama.redditslide.R.id.mod) && (v2.getId() != me.ccrama.redditslide.R.id.edit)) {
                    if (v2.getId() == me.ccrama.redditslide.R.id.save) {
                        if (me.ccrama.redditslide.SettingValues.saveButton) {
                            if (v2.getVisibility() == android.view.View.VISIBLE) {
                                me.ccrama.redditslide.Views.CreateCardView.animateOut(v2);
                            } else {
                                me.ccrama.redditslide.Views.CreateCardView.animateIn(v2);
                            }
                        }
                    } else if (v2.getId() == me.ccrama.redditslide.R.id.hide) {
                        if (me.ccrama.redditslide.SettingValues.hideButton) {
                            if (v2.getVisibility() == android.view.View.VISIBLE) {
                                me.ccrama.redditslide.Views.CreateCardView.animateOut(v2);
                            } else {
                                me.ccrama.redditslide.Views.CreateCardView.animateIn(v2);
                            }
                        }
                    } else if (v2.getVisibility() == android.view.View.VISIBLE) {
                        me.ccrama.redditslide.Views.CreateCardView.animateOut(v2);
                    } else {
                        me.ccrama.redditslide.Views.CreateCardView.animateIn(v2);
                    }
                }
            }
        }
    }

    private static void doHideObjects(final android.view.View v) {
        if (me.ccrama.redditslide.SettingValues.smallTag) {
            v.findViewById(me.ccrama.redditslide.R.id.base).setVisibility(android.view.View.GONE);
            v.findViewById(me.ccrama.redditslide.R.id.tag).setVisibility(android.view.View.VISIBLE);
        } else {
            v.findViewById(me.ccrama.redditslide.R.id.tag).setVisibility(android.view.View.GONE);
        }
        if (me.ccrama.redditslide.SettingValues.bigPicCropped) {
            ((android.widget.ImageView) (v.findViewById(me.ccrama.redditslide.R.id.leadimage))).setMaxHeight(900);
            ((android.widget.ImageView) (v.findViewById(me.ccrama.redditslide.R.id.leadimage))).setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
        }
        if ((!me.ccrama.redditslide.SettingValues.actionbarVisible) && (!me.ccrama.redditslide.SettingValues.actionbarTap)) {
            for (android.view.View v2 : me.ccrama.redditslide.Views.CreateCardView.getViewsByTag(((android.view.ViewGroup) (v)), "tintactionbar")) {
                v2.setVisibility(android.view.View.GONE);
            }
            v.findViewById(me.ccrama.redditslide.R.id.secondMenu).setOnClickListener(new android.view.View.OnClickListener() {
                @java.lang.Override
                public void onClick(android.view.View v3) {
                    me.ccrama.redditslide.Views.CreateCardView.toggleActionbar(v);
                }
            });
        } else {
            v.findViewById(me.ccrama.redditslide.R.id.secondMenu).setVisibility(android.view.View.GONE);
            if (me.ccrama.redditslide.SettingValues.actionbarTap) {
                for (android.view.View v2 : me.ccrama.redditslide.Views.CreateCardView.getViewsByTag(((android.view.ViewGroup) (v)), "tintactionbar")) {
                    v2.setVisibility(android.view.View.GONE);
                }
                v.setOnLongClickListener(new android.view.View.OnLongClickListener() {
                    @java.lang.Override
                    public boolean onLongClick(android.view.View v) {
                        me.ccrama.redditslide.Views.CreateCardView.toggleActionbar(v);
                        return true;
                    }
                });
            }
        }
        if (me.ccrama.redditslide.SettingValues.switchThumb) {
            android.widget.RelativeLayout.LayoutParams picParams = ((android.widget.RelativeLayout.LayoutParams) (v.findViewById(me.ccrama.redditslide.R.id.thumbimage2).getLayoutParams()));
            android.widget.RelativeLayout.LayoutParams layoutParams = ((android.widget.RelativeLayout.LayoutParams) (v.findViewById(me.ccrama.redditslide.R.id.inside).getLayoutParams()));
            if ((!me.ccrama.redditslide.SettingValues.actionbarVisible) && (!me.ccrama.redditslide.SettingValues.actionbarTap)) {
                picParams.addRule(android.widget.RelativeLayout.LEFT_OF, me.ccrama.redditslide.R.id.secondMenu);
            } else {
                picParams.addRule(android.widget.RelativeLayout.ALIGN_PARENT_RIGHT, android.widget.RelativeLayout.TRUE);
            }
            picParams.setMargins(picParams.rightMargin, picParams.topMargin, picParams.leftMargin, picParams.bottomMargin);
            layoutParams.addRule(android.widget.RelativeLayout.LEFT_OF, me.ccrama.redditslide.R.id.thumbimage2);
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.JELLY_BEAN) {
                layoutParams.removeRule(android.widget.RelativeLayout.RIGHT_OF);
            } else {
                layoutParams.addRule(android.widget.RelativeLayout.RIGHT_OF, 0);
            }
        }
        if (!me.ccrama.redditslide.SettingValues.bigPicEnabled) {
            v.findViewById(me.ccrama.redditslide.R.id.thumbimage2).setVisibility(android.view.View.VISIBLE);
            v.findViewById(me.ccrama.redditslide.R.id.headerimage).setVisibility(android.view.View.GONE);
        } else if (me.ccrama.redditslide.SettingValues.bigPicEnabled) {
            v.findViewById(me.ccrama.redditslide.R.id.thumbimage2).setVisibility(android.view.View.GONE);
        }
    }

    public static boolean isCard() {
        return me.ccrama.redditslide.Views.CreateCardView.CardEnum.valueOf(me.ccrama.redditslide.SettingValues.prefs.getString("defaultCardViewNew", me.ccrama.redditslide.SettingValues.defaultCardView.toString())) == me.ccrama.redditslide.Views.CreateCardView.CardEnum.LARGE;
    }

    public static boolean isMiddle() {
        return me.ccrama.redditslide.SettingValues.prefs.getBoolean("middleCard", false);
    }

    public static boolean isDesktop() {
        return me.ccrama.redditslide.Views.CreateCardView.CardEnum.valueOf(me.ccrama.redditslide.SettingValues.prefs.getString("defaultCardViewNew", me.ccrama.redditslide.SettingValues.defaultCardView.toString())) == me.ccrama.redditslide.Views.CreateCardView.CardEnum.DESKTOP;
    }

    public static me.ccrama.redditslide.Views.CreateCardView.CardEnum getCardView() {
        return me.ccrama.redditslide.Views.CreateCardView.CardEnum.valueOf(me.ccrama.redditslide.SettingValues.prefs.getString("defaultCardViewNew", me.ccrama.redditslide.SettingValues.defaultCardView.toString()));
    }

    public static me.ccrama.redditslide.SettingValues.ColorIndicator getColorIndicator() {
        java.lang.String subreddit = "";
        return me.ccrama.redditslide.SettingValues.ColorIndicator.valueOf(me.ccrama.redditslide.SettingValues.prefs.getString(subreddit + "colorIndicatorNew", me.ccrama.redditslide.SettingValues.colorIndicator.toString()));
    }

    public static me.ccrama.redditslide.SettingValues.ColorMatchingMode getColorMatchingMode() {
        java.lang.String subreddit = "";
        return me.ccrama.redditslide.SettingValues.ColorMatchingMode.valueOf(me.ccrama.redditslide.SettingValues.prefs.getString(subreddit + "ccolorMatchingModeNew", me.ccrama.redditslide.SettingValues.colorMatchingMode.toString()));
    }

    public static void setColorMatchingMode(me.ccrama.redditslide.SettingValues.ColorMatchingMode b) {
        java.lang.String subreddit = "";
        if (subreddit.isEmpty()) {
            me.ccrama.redditslide.SettingValues.prefs.edit().putString("ccolorMatchingModeNew", b.toString()).apply();
            me.ccrama.redditslide.SettingValues.colorMatchingMode = b;
        } else {
            me.ccrama.redditslide.SettingValues.prefs.edit().putString(subreddit + "ccolorMatchingModeNew", b.toString()).apply();
        }
    }

    public enum CardEnum {

        LARGE("Big Card"),
        LIST("List"),
        DESKTOP("Desktop");
        final java.lang.String displayName;

        CardEnum(java.lang.String s) {
            this.displayName = s;
        }

        public java.lang.String getDisplayName() {
            return displayName;
        }
    }
}