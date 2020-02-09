package me.ccrama.redditslide;
import me.ccrama.redditslide.util.SortingUtil;
import me.ccrama.redditslide.Fragments.SettingsHandlingFragment;
import me.ccrama.redditslide.Views.CreateCardView;
import me.ccrama.redditslide.Visuals.Palette;
/**
 * Created by ccrama on 9/19/2015.
 */
public class SettingValues {
    public static final java.lang.String PREF_SINGLE = "Single";

    public static final java.lang.String PREF_FAB = "Fab";

    public static final java.lang.String PREF_UPVOTE_PERCENTAGE = "upvotePercentage";

    public static final java.lang.String PREF_FAB_TYPE = "FabType";

    public static final java.lang.String PREF_DAY_TIME = "day";

    public static final java.lang.String PREF_VOTE_GESTURES = "voteGestures";

    public static final java.lang.String PREF_NIGHT_MODE_STATE = "nightModeState";

    public static final java.lang.String PREF_NIGHT_MODE = "nightMode";

    public static final java.lang.String PREF_NIGHT_THEME = "nightTheme";

    public static final java.lang.String PREF_TYPE_IN_TEXT = "typeInText";

    public static final java.lang.String PREF_AUTOHIDE_COMMENTS = "autohideComments";

    public static final java.lang.String PREF_SHOW_COLLAPSE_EXPAND = "showCollapseExpand";

    public static final java.lang.String PREF_NO_IMAGES = "noImages";

    public static final java.lang.String PREF_AUTOTHEME = "autotime";

    public static final java.lang.String PREVIEWS_LEFT = "previewsLeft";

    public static final java.lang.String PREF_ALPHABETIZE_SUBSCRIBE = "alphabetizeSubscribe";

    public static final java.lang.String PREF_COLOR_BACK = "colorBack";

    public static final java.lang.String PREF_IMAGE_SUBFOLDERS = "imageSubfolders";

    public static final java.lang.String PREF_COLOR_NAV_BAR = "colorNavBar";

    public static final java.lang.String PREF_READER_MODE = "readerDefault";

    public static final java.lang.String PREF_READER_NIGHT = "readernight";

    public static final java.lang.String PREF_COLOR_EVERYWHERE = "colorEverywhere";

    public static final java.lang.String PREF_EXPANDED_TOOLBAR = "expandedToolbar";

    public static final java.lang.String PREF_SWAP = "Swap";

    public static final java.lang.String PREF_ACTIONBAR_VISIBLE = "actionbarVisible";

    public static final java.lang.String PREF_SMALL_TAG = "smallTag";

    public static final java.lang.String PREF_ACTIONBAR_TAP = "actionbarTap";

    public static final java.lang.String PREF_STORE_HISTORY = "storehistory";

    public static final java.lang.String PREF_STORE_NSFW_HISTORY = "storensfw";

    public static final java.lang.String PREF_SCROLL_SEEN = "scrollSeen";

    public static final java.lang.String PREF_TITLE_FILTERS = "titleFilters";

    public static final java.lang.String PREF_TEXT_FILTERS = "textFilters";

    public static final java.lang.String PREF_DOMAIN_FILTERS = "domainFilters";

    public static final java.lang.String PREF_ALWAYS_EXTERNAL = "alwaysExternal";

    public static final java.lang.String PREF_DRAFTS = "drafts";

    public static final java.lang.String PREF_SUBREDDIT_FILTERS = "subredditFilters";

    public static final java.lang.String PREF_ABBREVIATE_SCORES = "abbreviateScores";

    public static final java.lang.String PREF_FLAIR_FILTERS = "subFlairFilters";

    public static final java.lang.String PREF_COMMENT_LAST_VISIT = "commentLastVisit";

    public static final java.lang.String PREF_VOTES_INFO_LINE = "votesInfoLine";

    public static final java.lang.String PREF_TYPE_INFO_LINE = "typeInfoLine";

    public static final java.lang.String PREF_COMMENT_PAGER = "commentPager";

    public static final java.lang.String PREF_COLLAPSE_COMMENTS = "collapseCOmments";

    public static final java.lang.String PREF_COLLAPSE_COMMENTS_DEFAULT = "collapseCommentsDefault";

    public static final java.lang.String PREF_RIGHT_HANDED_COMMENT_MENU = "rightHandedCommentMenu";

    public static final java.lang.String PREF_DUAL_PORTRAIT = "dualPortrait";

    public static final java.lang.String PREF_SINGLE_COLUMN_MULTI = "singleColumnMultiWindow";

    public static final java.lang.String PREF_CROP_IMAGE = "cropImage";

    public static final java.lang.String PREF_COMMENT_FAB = "commentFab";

    public static final java.lang.String PREF_SWITCH_THUMB = "switchThumb";

    public static final java.lang.String PREF_BIG_THUMBS = "bigThumbnails";

    public static final java.lang.String PREF_LOW_RES_ALWAYS = "lowResAlways";

    public static final java.lang.String PREF_LOW_RES_MOBILE = "lowRes";

    public static final java.lang.String PREF_IMAGE_LQ = "imageLq";

    public static final java.lang.String PREF_COLOR_SUB_NAME = "colorSubName";

    public static final java.lang.String PREF_OVERRIDE_LANGUAGE = "overrideLanguage";

    public static final java.lang.String PREF_IMMERSIVE_MODE = "immersiveMode";

    public static final java.lang.String PREF_SHOW_DOMAIN = "showDomain";

    public static final java.lang.String PREF_CARD_TEXT = "cardText";

    public static final java.lang.String PREF_ZOOM_DEFAULT = "zoomDefault";

    public static final java.lang.String PREF_SUBREDDIT_SEARCH_METHOD = "subredditSearchMethod";

    public static final java.lang.String PREF_BACK_BUTTON_BEHAVIOR = "backButtonBehavior";

    public static final java.lang.String PREF_LQ_LOW = "lqLow";

    public static final java.lang.String PREF_LQ_MID = "lqMid";

    public static final java.lang.String PREF_LQ_HIGH = "lqHigh";

    public static final java.lang.String PREF_SOUND_NOTIFS = "soundNotifs";

    public static final java.lang.String PREF_COOKIES = "storeCookies";

    public static final java.lang.String PREF_NIGHT_START = "nightStart";

    public static final java.lang.String PREF_NIGHT_END = "nightEnd";

    public static final java.lang.String PREF_SHOW_NSFW_CONTENT = "showNSFWContent";

    public static final java.lang.String PREF_HIDE_NSFW_PREVIEW = "hideNSFWPreviews";

    public static final java.lang.String PREF_HIDE_NSFW_COLLECTION = "hideNSFWPreviewsCollection";

    public static final java.lang.String PREF_IGNORE_SUB_SETTINGS = "ignoreSub";

    public static final java.lang.String PREF_HIGHLIGHT_TIME = "highlightTime";

    public static final java.lang.String PREF_MUTE = "muted";

    public static final java.lang.String PREF_LINK_HANDLING_MODE = "linkHandlingMode";

    public static final java.lang.String PREF_FULL_COMMENT_OVERRIDE = "fullCommentOverride";

    public static final java.lang.String PREF_ALBUM = "album";

    public static final java.lang.String PREF_GIF = "gif";

    public static final java.lang.String PREF_FASTSCROLL = "Fastscroll";

    public static final java.lang.String PREF_FAB_CLEAR = "fabClear";

    public static final java.lang.String PREF_HIDEBUTTON = "Hidebutton";

    public static final java.lang.String PREF_SAVE_BUTTON = "saveButton";

    public static final java.lang.String PREF_IMAGE = "image";

    public static final java.lang.String PREF_SELFTEXT_IMAGE_COMMENT = "selftextImageComment";

    public static final java.lang.String SYNCCIT_AUTH = "SYNCCIT_AUTH";

    public static final java.lang.String SYNCCIT_NAME = "SYNCCIT_NAME";

    public static final java.lang.String PREF_BLUR = "blur";

    public static final java.lang.String PREF_ALBUM_SWIPE = "albumswipe";

    public static final java.lang.String PREF_COMMENT_NAV = "commentVolumeNav";

    public static final java.lang.String PREF_COLOR_COMMENT_DEPTH = "colorCommentDepth";

    public static final java.lang.String COMMENT_DEPTH = "commentDepth";

    public static final java.lang.String COMMENT_COUNT = "commentcount";

    public static final java.lang.String PREF_USER_FILTERS = "userFilters";

    public static final java.lang.String PREF_COLOR_ICON = "colorIcon";

    public static final java.lang.String PREF_PEEK = "peek";

    public static final java.lang.String PREF_LARGE_LINKS = "largeLinks";

    public static final java.lang.String PREF_LARGE_DEPTH = "largeDepth";

    public static final java.lang.String PREF_TITLE_TOP = "titleTop";

    public static final java.lang.String PREF_HIGHLIGHT_COMMENT_OP = "commentOP";

    public static final java.lang.String PREF_LONG_LINK = "shareLongLink";

    public static final java.lang.String PREF_SELECTED_BROWSER = "selectedBrowser";

    public static final java.lang.String PREF_SELECTED_DRAWER_ITEMS = "selectedDrawerItems";

    public static final java.lang.String PREF_MOD_REMOVAL_TYPE = "removalReasonType";

    public static final java.lang.String PREF_MOD_TOOLBOX_ENABLED = "toolboxEnabled";

    public static final java.lang.String PREF_MOD_TOOLBOX_MESSAGE = "toolboxMessageType";

    public static final java.lang.String PREF_MOD_TOOLBOX_STICKY = "toolboxSticky";

    public static final java.lang.String PREF_MOD_TOOLBOX_LOCK = "toolboxLock";

    public static final java.lang.String PREF_MOD_TOOLBOX_MODMAIL = "toolboxModmail";

    public static me.ccrama.redditslide.Views.CreateCardView.CardEnum defaultCardView;

    public static net.dean.jraw.paginators.Sorting defaultSorting;

    public static net.dean.jraw.paginators.TimePeriod timePeriod;

    public static net.dean.jraw.models.CommentSort defaultCommentSorting;

    public static boolean middleImage;

    public static boolean bigPicEnabled;

    public static boolean bigPicCropped;

    public static me.ccrama.redditslide.SettingValues.ColorMatchingMode colorMatchingMode;

    public static me.ccrama.redditslide.SettingValues.ColorIndicator colorIndicator;

    public static me.ccrama.redditslide.Visuals.Palette.ThemeEnum theme;

    public static android.content.SharedPreferences prefs;

    public static boolean expandedToolbar;

    public static boolean single;

    public static boolean swap;

    public static boolean album;

    public static boolean cache;

    public static boolean expandedSettings;

    public static boolean fabComments;

    public static boolean largeDepth;

    public static boolean cacheDefault;

    public static boolean image;

    public static boolean video;

    public static boolean upvotePercentage;

    public static boolean colorBack;

    public static boolean colorNavBar;

    public static boolean actionbarVisible;

    public static boolean actionbarTap;

    public static boolean commentAutoHide;

    public static boolean showCollapseExpand;

    public static boolean fullCommentOverride;

    public static boolean lowResAlways;

    public static boolean noImages;

    public static boolean lowResMobile;

    public static boolean blurCheck;

    public static boolean readerNight;

    public static boolean swipeAnywhere;

    public static boolean commentLastVisit;

    public static boolean storeHistory;

    public static boolean showNSFWContent;

    public static boolean storeNSFWHistory;

    public static boolean scrollSeen;

    public static boolean saveButton;

    public static boolean voteGestures;

    public static boolean colorEverywhere;

    public static boolean gif;

    public static boolean colorCommentDepth;

    public static boolean commentVolumeNav;

    public static boolean postNav;

    public static boolean cropImage;

    public static boolean smallTag;

    public static boolean typeInfoLine;

    public static boolean votesInfoLine;

    public static boolean readerMode;

    public static boolean collapseComments;

    public static boolean collapseCommentsDefault;

    public static boolean rightHandedCommentMenu;

    public static boolean abbreviateScores;

    public static boolean shareLongLink;

    public static boolean isMuted;

    public static int subredditSearchMethod;

    public static int backButtonBehavior;

    public static int nightStart;

    public static int nightEnd;

    public static int linkHandlingMode;

    public static int previews;

    public static java.lang.String synccitName;

    public static java.lang.String synccitAuth;

    public static java.util.Set<java.lang.String> titleFilters;

    public static java.util.Set<java.lang.String> textFilters;

    public static java.util.Set<java.lang.String> domainFilters;

    public static java.util.Set<java.lang.String> subredditFilters;

    public static java.util.Set<java.lang.String> flairFilters;

    public static java.util.Set<java.lang.String> alwaysExternal;

    public static java.util.Set<java.lang.String> userFilters;

    public static boolean loadImageLq;

    public static boolean ignoreSubSetting;

    public static boolean hideNSFWCollection;

    public static boolean fastscroll;

    public static boolean fab = true;

    public static int fabType = me.ccrama.redditslide.Constants.FAB_POST;

    public static boolean hideButton;

    public static boolean isPro;

    public static boolean customtabs;

    public static boolean titleTop;

    public static boolean dualPortrait;

    public static boolean singleColumnMultiWindow;

    public static int nightModeState;

    public static boolean imageSubfolders;

    public static boolean autoTime;

    public static boolean albumSwipe;

    public static boolean switchThumb;

    public static boolean bigThumbnails;

    public static boolean commentPager;

    public static boolean alphabetizeOnSubscribe;

    public static boolean colorSubName;

    public static boolean hideSelftextLeadImage;

    public static boolean overrideLanguage;

    public static boolean immersiveMode;

    public static boolean showDomain;

    public static boolean cardText;

    public static boolean alwaysZoom;

    public static boolean lqLow = false;

    public static boolean lqMid = true;

    public static boolean lqHigh = false;

    public static int currentTheme;// current base theme (Light, Dark, Dark blue, etc.)


    public static int nightTheme;

    public static boolean typeInText;

    public static boolean notifSound;

    public static boolean cookies;

    public static boolean colorIcon;

    public static boolean peek;

    public static boolean largeLinks;

    public static boolean highlightCommentOP;

    public static boolean highlightTime;

    public static java.lang.String selectedBrowser;

    public static long selectedDrawerItems;

    public static me.ccrama.redditslide.SettingValues.ForcedState forcedNightModeState = me.ccrama.redditslide.SettingValues.ForcedState.NOT_FORCED;

    public static boolean toolboxEnabled;

    public static int removalReasonType;

    public static int toolboxMessageType;

    public static boolean toolboxSticky;

    public static boolean toolboxLock;

    public static boolean toolboxModmail;

    public static void setAllValues(android.content.SharedPreferences settings) {
        me.ccrama.redditslide.SettingValues.prefs = settings;
        me.ccrama.redditslide.SettingValues.defaultCardView = me.ccrama.redditslide.Views.CreateCardView.CardEnum.valueOf(settings.getString("defaultCardViewNew", "LARGE").toUpperCase());
        me.ccrama.redditslide.SettingValues.middleImage = settings.getBoolean("middleCard", false);
        me.ccrama.redditslide.SettingValues.bigPicCropped = settings.getBoolean("bigPicCropped", true);
        me.ccrama.redditslide.SettingValues.bigPicEnabled = settings.getBoolean("bigPicEnabled", true);
        me.ccrama.redditslide.SettingValues.colorMatchingMode = me.ccrama.redditslide.SettingValues.ColorMatchingMode.valueOf(settings.getString("ccolorMatchingModeNew", "MATCH_EXTERNALLY"));
        me.ccrama.redditslide.SettingValues.colorIndicator = me.ccrama.redditslide.SettingValues.ColorIndicator.valueOf(settings.getString("colorIndicatorNew", "CARD_BACKGROUND"));
        me.ccrama.redditslide.SettingValues.defaultSorting = net.dean.jraw.paginators.Sorting.valueOf(settings.getString("defaultSorting", "HOT"));
        me.ccrama.redditslide.SettingValues.timePeriod = net.dean.jraw.paginators.TimePeriod.valueOf(settings.getString("timePeriod", "DAY"));
        me.ccrama.redditslide.SettingValues.defaultCommentSorting = net.dean.jraw.models.CommentSort.valueOf(settings.getString("defaultCommentSortingNew", "CONFIDENCE"));
        me.ccrama.redditslide.SettingValues.showNSFWContent = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_SHOW_NSFW_CONTENT, false);
        me.ccrama.redditslide.SettingValues.hideNSFWCollection = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_HIDE_NSFW_COLLECTION, true);
        me.ccrama.redditslide.SettingValues.ignoreSubSetting = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_IGNORE_SUB_SETTINGS, false);
        me.ccrama.redditslide.SettingValues.single = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_SINGLE, false);
        me.ccrama.redditslide.SettingValues.readerNight = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_READER_NIGHT, false);
        me.ccrama.redditslide.SettingValues.blurCheck = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_BLUR, false);
        me.ccrama.redditslide.SettingValues.overrideLanguage = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_OVERRIDE_LANGUAGE, false);
        me.ccrama.redditslide.SettingValues.immersiveMode = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_IMMERSIVE_MODE, false);
        me.ccrama.redditslide.SettingValues.largeDepth = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_LARGE_DEPTH, false);
        me.ccrama.redditslide.SettingValues.readerMode = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_READER_MODE, false);
        me.ccrama.redditslide.SettingValues.imageSubfolders = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_IMAGE_SUBFOLDERS, false);
        me.ccrama.redditslide.SettingValues.isMuted = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_MUTE, false);
        me.ccrama.redditslide.SettingValues.commentVolumeNav = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_COMMENT_NAV, false);
        me.ccrama.redditslide.SettingValues.postNav = false;
        me.ccrama.redditslide.SettingValues.fab = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_FAB, true);
        me.ccrama.redditslide.SettingValues.fabType = me.ccrama.redditslide.SettingValues.prefs.getInt(me.ccrama.redditslide.SettingValues.PREF_FAB_TYPE, me.ccrama.redditslide.Constants.FAB_DISMISS);
        if ((me.ccrama.redditslide.SettingValues.fabType > 3) || (me.ccrama.redditslide.SettingValues.fabType < 0)) {
            me.ccrama.redditslide.SettingValues.fabType = me.ccrama.redditslide.Constants.FAB_DISMISS;
            me.ccrama.redditslide.SettingValues.prefs.edit().putInt(me.ccrama.redditslide.SettingValues.PREF_FAB_TYPE, me.ccrama.redditslide.Constants.FAB_DISMISS).apply();
        }
        me.ccrama.redditslide.SettingValues.subredditSearchMethod = me.ccrama.redditslide.SettingValues.prefs.getInt(me.ccrama.redditslide.SettingValues.PREF_SUBREDDIT_SEARCH_METHOD, me.ccrama.redditslide.Constants.SUBREDDIT_SEARCH_METHOD_DRAWER);
        if ((me.ccrama.redditslide.SettingValues.subredditSearchMethod > 3) || (me.ccrama.redditslide.SettingValues.subredditSearchMethod < 0)) {
            me.ccrama.redditslide.SettingValues.subredditSearchMethod = 1;
            me.ccrama.redditslide.SettingValues.prefs.edit().putInt(me.ccrama.redditslide.SettingValues.PREF_SUBREDDIT_SEARCH_METHOD, 1).apply();
        }
        me.ccrama.redditslide.SettingValues.backButtonBehavior = me.ccrama.redditslide.SettingValues.prefs.getInt(me.ccrama.redditslide.SettingValues.PREF_BACK_BUTTON_BEHAVIOR, me.ccrama.redditslide.Constants.BackButtonBehaviorOptions.ConfirmExit.getValue());
        me.ccrama.redditslide.SettingValues.highlightTime = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_HIGHLIGHT_TIME, true);
        // TODO: Remove the old pref check in a later version
        // This handles forward migration from the old night_mode boolean state
        me.ccrama.redditslide.SettingValues.nightModeState = me.ccrama.redditslide.SettingValues.prefs.getInt(me.ccrama.redditslide.SettingValues.PREF_NIGHT_MODE_STATE, me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_NIGHT_MODE, false) ? me.ccrama.redditslide.SettingValues.NightModeState.MANUAL.ordinal() : me.ccrama.redditslide.SettingValues.NightModeState.DISABLED.ordinal());
        me.ccrama.redditslide.SettingValues.nightTheme = me.ccrama.redditslide.SettingValues.prefs.getInt(me.ccrama.redditslide.SettingValues.PREF_NIGHT_THEME, 0);
        me.ccrama.redditslide.SettingValues.autoTime = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_AUTOTHEME, false);
        me.ccrama.redditslide.SettingValues.colorBack = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_COLOR_BACK, false);
        me.ccrama.redditslide.SettingValues.cardText = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_CARD_TEXT, false);
        me.ccrama.redditslide.SettingValues.colorNavBar = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_COLOR_NAV_BAR, false);
        me.ccrama.redditslide.SettingValues.shareLongLink = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_LONG_LINK, false);
        me.ccrama.redditslide.SettingValues.colorEverywhere = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_COLOR_EVERYWHERE, true);
        me.ccrama.redditslide.SettingValues.colorCommentDepth = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_COLOR_COMMENT_DEPTH, true);
        me.ccrama.redditslide.SettingValues.alwaysZoom = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_ZOOM_DEFAULT, true);
        me.ccrama.redditslide.SettingValues.collapseComments = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_COLLAPSE_COMMENTS, false);
        me.ccrama.redditslide.SettingValues.collapseCommentsDefault = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_COLLAPSE_COMMENTS_DEFAULT, false);
        me.ccrama.redditslide.SettingValues.rightHandedCommentMenu = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_RIGHT_HANDED_COMMENT_MENU, false);
        me.ccrama.redditslide.SettingValues.commentAutoHide = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_AUTOHIDE_COMMENTS, false);
        me.ccrama.redditslide.SettingValues.showCollapseExpand = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_SHOW_COLLAPSE_EXPAND, false);
        me.ccrama.redditslide.SettingValues.highlightCommentOP = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_HIGHLIGHT_COMMENT_OP, true);
        me.ccrama.redditslide.SettingValues.typeInfoLine = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_TYPE_INFO_LINE, false);
        me.ccrama.redditslide.SettingValues.votesInfoLine = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_VOTES_INFO_LINE, false);
        me.ccrama.redditslide.SettingValues.titleTop = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_TITLE_TOP, true);
        me.ccrama.redditslide.SettingValues.lqLow = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_LQ_LOW, false);
        me.ccrama.redditslide.SettingValues.lqMid = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_LQ_MID, true);
        me.ccrama.redditslide.SettingValues.lqHigh = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_LQ_HIGH, false);
        me.ccrama.redditslide.SettingValues.noImages = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_NO_IMAGES, false);
        me.ccrama.redditslide.SettingValues.abbreviateScores = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_ABBREVIATE_SCORES, true);
        me.ccrama.redditslide.SettingValues.lowResAlways = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_LOW_RES_ALWAYS, false);
        me.ccrama.redditslide.SettingValues.lowResMobile = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_LOW_RES_MOBILE, false);
        me.ccrama.redditslide.SettingValues.loadImageLq = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_IMAGE_LQ, false);
        me.ccrama.redditslide.SettingValues.showDomain = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_SHOW_DOMAIN, false);
        me.ccrama.redditslide.SettingValues.expandedToolbar = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_EXPANDED_TOOLBAR, false);
        me.ccrama.redditslide.SettingValues.voteGestures = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_VOTE_GESTURES, false);
        me.ccrama.redditslide.SettingValues.fullCommentOverride = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_FULL_COMMENT_OVERRIDE, false);
        me.ccrama.redditslide.SettingValues.alphabetizeOnSubscribe = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_ALPHABETIZE_SUBSCRIBE, false);
        me.ccrama.redditslide.SettingValues.commentPager = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_COMMENT_PAGER, false);
        me.ccrama.redditslide.SettingValues.smallTag = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_SMALL_TAG, false);
        me.ccrama.redditslide.SettingValues.swap = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_SWAP, false);
        me.ccrama.redditslide.SettingValues.hideSelftextLeadImage = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_SELFTEXT_IMAGE_COMMENT, false);
        me.ccrama.redditslide.SettingValues.image = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_IMAGE, true);
        me.ccrama.redditslide.SettingValues.cache = true;
        me.ccrama.redditslide.SettingValues.cacheDefault = false;
        me.ccrama.redditslide.SettingValues.storeHistory = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_STORE_HISTORY, true);
        me.ccrama.redditslide.SettingValues.upvotePercentage = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_UPVOTE_PERCENTAGE, false);
        me.ccrama.redditslide.SettingValues.storeNSFWHistory = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_STORE_NSFW_HISTORY, false);
        me.ccrama.redditslide.SettingValues.scrollSeen = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_SCROLL_SEEN, false);
        me.ccrama.redditslide.SettingValues.synccitName = me.ccrama.redditslide.SettingValues.prefs.getString(me.ccrama.redditslide.SettingValues.SYNCCIT_NAME, "");
        me.ccrama.redditslide.SettingValues.synccitAuth = me.ccrama.redditslide.SettingValues.prefs.getString(me.ccrama.redditslide.SettingValues.SYNCCIT_AUTH, "");
        me.ccrama.redditslide.SettingValues.notifSound = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_SOUND_NOTIFS, false);
        me.ccrama.redditslide.SettingValues.cookies = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_COOKIES, true);
        me.ccrama.redditslide.SettingValues.linkHandlingMode = me.ccrama.redditslide.SettingValues.prefs.getInt(me.ccrama.redditslide.SettingValues.PREF_LINK_HANDLING_MODE, me.ccrama.redditslide.Fragments.SettingsHandlingFragment.LinkHandlingMode.EXTERNAL.getValue());
        me.ccrama.redditslide.SettingValues.previews = me.ccrama.redditslide.SettingValues.prefs.getInt(me.ccrama.redditslide.SettingValues.PREVIEWS_LEFT, 10);
        me.ccrama.redditslide.SettingValues.nightStart = me.ccrama.redditslide.SettingValues.prefs.getInt(me.ccrama.redditslide.SettingValues.PREF_NIGHT_START, 9);
        me.ccrama.redditslide.SettingValues.nightEnd = me.ccrama.redditslide.SettingValues.prefs.getInt(me.ccrama.redditslide.SettingValues.PREF_NIGHT_END, 5);
        me.ccrama.redditslide.SettingValues.fabComments = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_COMMENT_FAB, false);
        me.ccrama.redditslide.SettingValues.largeLinks = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_LARGE_LINKS, false);
        // SharedPreferences' StringSets should never be modified, so we duplicate them into a new HashSet
        me.ccrama.redditslide.SettingValues.titleFilters = new java.util.HashSet<>(me.ccrama.redditslide.SettingValues.prefs.getStringSet(me.ccrama.redditslide.SettingValues.PREF_TITLE_FILTERS, new java.util.HashSet<>()));
        me.ccrama.redditslide.SettingValues.textFilters = new java.util.HashSet<>(me.ccrama.redditslide.SettingValues.prefs.getStringSet(me.ccrama.redditslide.SettingValues.PREF_TEXT_FILTERS, new java.util.HashSet<>()));
        me.ccrama.redditslide.SettingValues.domainFilters = new java.util.HashSet<>(me.ccrama.redditslide.SettingValues.prefs.getStringSet(me.ccrama.redditslide.SettingValues.PREF_DOMAIN_FILTERS, new java.util.HashSet<>()));
        me.ccrama.redditslide.SettingValues.subredditFilters = new java.util.HashSet<>(me.ccrama.redditslide.SettingValues.prefs.getStringSet(me.ccrama.redditslide.SettingValues.PREF_SUBREDDIT_FILTERS, new java.util.HashSet<>()));
        me.ccrama.redditslide.SettingValues.alwaysExternal = new java.util.HashSet<>(me.ccrama.redditslide.SettingValues.prefs.getStringSet(me.ccrama.redditslide.SettingValues.PREF_ALWAYS_EXTERNAL, new java.util.HashSet<>()));
        me.ccrama.redditslide.SettingValues.flairFilters = new java.util.HashSet<>(me.ccrama.redditslide.SettingValues.prefs.getStringSet(me.ccrama.redditslide.SettingValues.PREF_FLAIR_FILTERS, new java.util.HashSet<>()));
        me.ccrama.redditslide.SettingValues.userFilters = new java.util.HashSet<>(me.ccrama.redditslide.SettingValues.prefs.getStringSet(me.ccrama.redditslide.SettingValues.PREF_USER_FILTERS, new java.util.HashSet<>()));
        me.ccrama.redditslide.SettingValues.dualPortrait = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_DUAL_PORTRAIT, false);
        me.ccrama.redditslide.SettingValues.singleColumnMultiWindow = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_SINGLE_COLUMN_MULTI, false);
        me.ccrama.redditslide.SettingValues.colorSubName = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_COLOR_SUB_NAME, false);
        me.ccrama.redditslide.SettingValues.cropImage = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_CROP_IMAGE, true);
        me.ccrama.redditslide.SettingValues.switchThumb = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_SWITCH_THUMB, true);
        me.ccrama.redditslide.SettingValues.bigThumbnails = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_BIG_THUMBS, false);
        me.ccrama.redditslide.SettingValues.swipeAnywhere = true;// override this always now

        me.ccrama.redditslide.SettingValues.album = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_ALBUM, true);
        me.ccrama.redditslide.SettingValues.albumSwipe = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_ALBUM_SWIPE, true);
        me.ccrama.redditslide.SettingValues.commentLastVisit = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_COMMENT_LAST_VISIT, false);
        me.ccrama.redditslide.SettingValues.gif = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_GIF, true);
        me.ccrama.redditslide.SettingValues.video = true;
        me.ccrama.redditslide.SettingValues.fastscroll = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_FASTSCROLL, true);
        me.ccrama.redditslide.SettingValues.typeInText = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_TYPE_IN_TEXT, false);
        me.ccrama.redditslide.SettingValues.hideButton = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_HIDEBUTTON, false);
        me.ccrama.redditslide.SettingValues.saveButton = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_SAVE_BUTTON, false);
        me.ccrama.redditslide.SettingValues.actionbarVisible = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_ACTIONBAR_VISIBLE, true);
        me.ccrama.redditslide.SettingValues.actionbarTap = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_ACTIONBAR_TAP, false);
        me.ccrama.redditslide.SettingValues.colorIcon = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_COLOR_ICON, false);
        me.ccrama.redditslide.SettingValues.peek = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_PEEK, false);
        me.ccrama.redditslide.SettingValues.selectedBrowser = me.ccrama.redditslide.SettingValues.prefs.getString(me.ccrama.redditslide.SettingValues.PREF_SELECTED_BROWSER, "");
        me.ccrama.redditslide.SettingValues.selectedDrawerItems = me.ccrama.redditslide.SettingValues.prefs.getLong(me.ccrama.redditslide.SettingValues.PREF_SELECTED_DRAWER_ITEMS, -1);
        me.ccrama.redditslide.SettingValues.toolboxEnabled = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_MOD_TOOLBOX_ENABLED, false);
        me.ccrama.redditslide.SettingValues.removalReasonType = me.ccrama.redditslide.SettingValues.prefs.getInt(me.ccrama.redditslide.SettingValues.PREF_MOD_REMOVAL_TYPE, me.ccrama.redditslide.SettingValues.RemovalReasonType.SLIDE.ordinal());
        me.ccrama.redditslide.SettingValues.toolboxMessageType = me.ccrama.redditslide.SettingValues.prefs.getInt(me.ccrama.redditslide.SettingValues.PREF_MOD_TOOLBOX_MESSAGE, me.ccrama.redditslide.SettingValues.ToolboxRemovalMessageType.COMMENT.ordinal());
        me.ccrama.redditslide.SettingValues.toolboxSticky = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_MOD_TOOLBOX_STICKY, false);
        me.ccrama.redditslide.SettingValues.toolboxLock = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_MOD_TOOLBOX_LOCK, false);
        me.ccrama.redditslide.SettingValues.toolboxModmail = me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_MOD_TOOLBOX_MODMAIL, false);
    }

    public static void setPicsEnabled(java.lang.String sub, boolean checked) {
        me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean("picsenabled" + sub.toLowerCase(java.util.Locale.ENGLISH), checked).apply();
    }

    public static void resetPicsEnabled(java.lang.String sub) {
        me.ccrama.redditslide.SettingValues.prefs.edit().remove("picsenabled" + sub.toLowerCase(java.util.Locale.ENGLISH)).apply();
    }

    public static boolean isPicsEnabled(java.lang.String subreddit) {
        if (subreddit == null)
            return me.ccrama.redditslide.SettingValues.bigPicEnabled;

        return me.ccrama.redditslide.SettingValues.prefs.getBoolean("picsenabled" + subreddit.toLowerCase(java.util.Locale.ENGLISH), me.ccrama.redditslide.SettingValues.bigPicEnabled);
    }

    public static boolean isSelftextEnabled(java.lang.String subreddit) {
        if (subreddit == null)
            return me.ccrama.redditslide.SettingValues.cardText;

        return me.ccrama.redditslide.SettingValues.prefs.getBoolean("cardtextenabled" + subreddit.toLowerCase(java.util.Locale.ENGLISH), me.ccrama.redditslide.SettingValues.cardText);
    }

    public static void setSelftextEnabled(java.lang.String sub, boolean checked) {
        me.ccrama.redditslide.SettingValues.prefs.edit().putBoolean("cardtextenabled" + sub.toLowerCase(java.util.Locale.ENGLISH), checked).apply();
    }

    public static boolean getIsNSFWEnabled() {
        return me.ccrama.redditslide.SettingValues.prefs.getBoolean(me.ccrama.redditslide.SettingValues.PREF_HIDE_NSFW_PREVIEW + me.ccrama.redditslide.Authentication.name, true);
    }

    public static void resetSelftextEnabled(java.lang.String subreddit) {
        me.ccrama.redditslide.SettingValues.prefs.edit().remove("cardtextenabled" + subreddit.toLowerCase(java.util.Locale.ENGLISH)).apply();
    }

    public static void setDefaultCommentSorting(net.dean.jraw.models.CommentSort commentSorting, java.lang.String subreddit) {
        me.ccrama.redditslide.SettingValues.prefs.edit().putString("defaultComment" + subreddit.toLowerCase(java.util.Locale.ENGLISH), commentSorting.name()).apply();
    }

    public static net.dean.jraw.models.CommentSort getCommentSorting(java.lang.String sub) {
        return net.dean.jraw.models.CommentSort.valueOf(me.ccrama.redditslide.SettingValues.prefs.getString("defaultComment" + sub.toLowerCase(java.util.Locale.ENGLISH), me.ccrama.redditslide.SettingValues.defaultCommentSorting.name()));
    }

    public static void setSubSorting(net.dean.jraw.paginators.Sorting linkSorting, net.dean.jraw.paginators.TimePeriod time, java.lang.String subreddit) {
        me.ccrama.redditslide.SettingValues.prefs.edit().putString("defaultSort" + subreddit.toLowerCase(java.util.Locale.ENGLISH), linkSorting.name()).apply();
        me.ccrama.redditslide.SettingValues.prefs.edit().putString("defaultTime" + subreddit.toLowerCase(java.util.Locale.ENGLISH), time.name()).apply();
    }

    public static net.dean.jraw.paginators.Sorting getSubmissionSort(java.lang.String sub) {
        java.lang.String subreddit = sub.toLowerCase(java.util.Locale.ENGLISH);
        if (me.ccrama.redditslide.util.SortingUtil.sorting.containsKey(subreddit)) {
            return me.ccrama.redditslide.util.SortingUtil.sorting.get(subreddit);
        } else {
            return net.dean.jraw.paginators.Sorting.valueOf(me.ccrama.redditslide.SettingValues.prefs.getString("defaultSort" + sub.toLowerCase(java.util.Locale.ENGLISH), me.ccrama.redditslide.util.SortingUtil.defaultSorting.name()));
        }
    }

    public static net.dean.jraw.paginators.TimePeriod getSubmissionTimePeriod(java.lang.String sub) {
        java.lang.String subreddit = sub.toLowerCase(java.util.Locale.ENGLISH);
        if (me.ccrama.redditslide.util.SortingUtil.times.containsKey(subreddit)) {
            return me.ccrama.redditslide.util.SortingUtil.times.get(subreddit);
        } else {
            return net.dean.jraw.paginators.TimePeriod.valueOf(me.ccrama.redditslide.SettingValues.prefs.getString("defaultTime" + sub.toLowerCase(java.util.Locale.ENGLISH), me.ccrama.redditslide.util.SortingUtil.timePeriod.name()));
        }
    }

    public static boolean isNight() {
        /* Logic for the now rather complicated night mode:

        Normal       | Forced            | Actual state
        -----------------------------------------------------
        Disabled     | On/Off            | Forced state
        On           | On - gets unset   | On
        Off          | Off - gets unset  | Off
        On           | Off               | Off
        Off          | On                | On
        On/Off       | Unset             | Normal state

        Forced night mode state is intentionally not persisted between app runs and defaults to unset
         */
        if (me.ccrama.redditslide.SettingValues.isPro && me.ccrama.redditslide.SettingValues.NightModeState.isEnabled()) {
            boolean night = false;
            if (me.ccrama.redditslide.Reddit.canUseNightModeAuto && (me.ccrama.redditslide.SettingValues.nightModeState == me.ccrama.redditslide.SettingValues.NightModeState.AUTOMATIC.ordinal())) {
                night = (me.ccrama.redditslide.Reddit.getAppContext().getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES;
            } else if (me.ccrama.redditslide.SettingValues.nightModeState == me.ccrama.redditslide.SettingValues.NightModeState.MANUAL.ordinal()) {
                int hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
                night = (hour >= (me.ccrama.redditslide.SettingValues.nightStart + 12)) || (hour < me.ccrama.redditslide.SettingValues.nightEnd);
            }
            // unset forced state if forcing is now unnecessary - allows for normal night mode on/off transitions
            if ((night && (me.ccrama.redditslide.SettingValues.forcedNightModeState == me.ccrama.redditslide.SettingValues.ForcedState.FORCED_ON)) || ((!night) && (me.ccrama.redditslide.SettingValues.forcedNightModeState == me.ccrama.redditslide.SettingValues.ForcedState.FORCED_OFF))) {
                me.ccrama.redditslide.SettingValues.forcedNightModeState = me.ccrama.redditslide.SettingValues.ForcedState.NOT_FORCED;
            }
            if ((me.ccrama.redditslide.SettingValues.forcedNightModeState == me.ccrama.redditslide.SettingValues.ForcedState.FORCED_ON) || (me.ccrama.redditslide.SettingValues.forcedNightModeState == me.ccrama.redditslide.SettingValues.ForcedState.FORCED_OFF)) {
                return me.ccrama.redditslide.SettingValues.forcedNightModeState == me.ccrama.redditslide.SettingValues.ForcedState.FORCED_ON;
            } else {
                return night;
            }
        } else {
            return false;
        }
    }

    public static net.dean.jraw.paginators.Sorting getBaseSubmissionSort(java.lang.String sub) {
        return net.dean.jraw.paginators.Sorting.valueOf(me.ccrama.redditslide.SettingValues.prefs.getString("defaultSort" + sub.toLowerCase(java.util.Locale.ENGLISH), me.ccrama.redditslide.util.SortingUtil.defaultSorting.name()));
    }

    public static net.dean.jraw.paginators.TimePeriod getBaseTimePeriod(java.lang.String sub) {
        return net.dean.jraw.paginators.TimePeriod.valueOf(me.ccrama.redditslide.SettingValues.prefs.getString("defaultTime" + sub.toLowerCase(java.util.Locale.ENGLISH), me.ccrama.redditslide.util.SortingUtil.timePeriod.name()));
    }

    public static boolean hasSort(java.lang.String subreddit) {
        return me.ccrama.redditslide.SettingValues.prefs.contains("defaultSort" + subreddit.toLowerCase(java.util.Locale.ENGLISH));
    }

    public enum RemovalReasonType {

        SLIDE,
        TOOLBOX,
        REDDIT;}

    public enum ToolboxRemovalMessageType {

        COMMENT,
        PM,
        BOTH,
        NONE;}

    public enum ColorIndicator {

        CARD_BACKGROUND,
        TEXT_COLOR,
        NONE;}

    public enum ColorMatchingMode {

        ALWAYS_MATCH,
        MATCH_EXTERNALLY;}

    public enum NightModeState {

        DISABLED,
        MANUAL,
        AUTOMATIC;
        public static boolean isEnabled() {
            return (me.ccrama.redditslide.SettingValues.nightModeState != me.ccrama.redditslide.SettingValues.NightModeState.DISABLED.ordinal()) || (me.ccrama.redditslide.SettingValues.forcedNightModeState != me.ccrama.redditslide.SettingValues.ForcedState.NOT_FORCED);
        }
    }

    public enum ForcedState {

        NOT_FORCED,
        FORCED_ON,
        FORCED_OFF;}
}