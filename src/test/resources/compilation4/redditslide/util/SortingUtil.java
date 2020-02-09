package me.ccrama.redditslide.util;
import java.util.Locale;
import me.ccrama.redditslide.R;
import java.util.HashMap;
import me.ccrama.redditslide.Reddit;
import me.ccrama.redditslide.Activities.Search;
import java.util.ArrayList;
import me.ccrama.redditslide.ColorPreferences;
import java.util.Map;
public class SortingUtil {
    public static final java.util.Map<java.lang.String, net.dean.jraw.paginators.TimePeriod> times = new java.util.HashMap<>();

    public static net.dean.jraw.paginators.SubmissionSearchPaginator.SearchSort search = net.dean.jraw.paginators.SubmissionSearchPaginator.SearchSort.RELEVANCE;

    public static net.dean.jraw.paginators.Sorting defaultSorting;

    public static net.dean.jraw.paginators.TimePeriod timePeriod;

    public static java.lang.Integer getSortingId(net.dean.jraw.paginators.Sorting sort) {
        switch (sort) {
            case HOT :
                return 0;
            case NEW :
                return 1;
            case RISING :
                return 2;
            case TOP :
                return 3;
            case CONTROVERSIAL :
                return 4;
            case BEST :
                return 5;
            default :
                return 0;
        }
    }

    public static java.lang.String[] getSearch() {
        return new java.lang.String[]{ me.ccrama.redditslide.Reddit.getAppContext().getString(me.ccrama.redditslide.R.string.search_relevance), me.ccrama.redditslide.Reddit.getAppContext().getString(me.ccrama.redditslide.R.string.search_top), me.ccrama.redditslide.Reddit.getAppContext().getString(me.ccrama.redditslide.R.string.search_new), me.ccrama.redditslide.Reddit.getAppContext().getString(me.ccrama.redditslide.R.string.search_comments) };
    }

    private static java.lang.Integer getSortingTimeId(net.dean.jraw.paginators.TimePeriod time) {
        switch (time) {
            case HOUR :
                return 0;
            case DAY :
                return 1;
            case WEEK :
                return 2;
            case MONTH :
                return 3;
            case YEAR :
                return 4;
            case ALL :
                return 5;
            default :
                return 0;
        }
    }

    public static java.lang.Integer getSortingId(java.lang.String subreddit) {
        subreddit = subreddit.toLowerCase(java.util.Locale.ENGLISH);
        net.dean.jraw.paginators.Sorting sort = (me.ccrama.redditslide.util.SortingUtil.sorting.containsKey(subreddit)) ? me.ccrama.redditslide.util.SortingUtil.sorting.get(subreddit) : me.ccrama.redditslide.util.SortingUtil.defaultSorting;
        return me.ccrama.redditslide.util.SortingUtil.getSortingId(sort);
    }

    public static java.lang.Integer getSortingTimeId(java.lang.String subreddit) {
        subreddit = subreddit.toLowerCase(java.util.Locale.ENGLISH);
        net.dean.jraw.paginators.TimePeriod time = (me.ccrama.redditslide.util.SortingUtil.times.containsKey(subreddit)) ? me.ccrama.redditslide.util.SortingUtil.times.get(subreddit) : me.ccrama.redditslide.util.SortingUtil.timePeriod;
        return me.ccrama.redditslide.util.SortingUtil.getSortingTimeId(time);
    }

    public static android.text.Spannable[] getSortingSpannables(java.lang.String currentSub) {
        return me.ccrama.redditslide.util.SortingUtil.getSortingSpannables(me.ccrama.redditslide.util.SortingUtil.getSortingId(currentSub), currentSub);
    }

    public static android.text.Spannable[] getSortingSpannables(net.dean.jraw.paginators.Sorting sorting) {
        return me.ccrama.redditslide.util.SortingUtil.getSortingSpannables(me.ccrama.redditslide.util.SortingUtil.getSortingId(sorting), " ");
    }

    public static java.lang.Integer getSortingSearchId(me.ccrama.redditslide.Activities.Search s) {
        return s.time == net.dean.jraw.paginators.TimePeriod.HOUR ? 0 : s.time == net.dean.jraw.paginators.TimePeriod.DAY ? 1 : s.time == net.dean.jraw.paginators.TimePeriod.WEEK ? 2 : s.time == net.dean.jraw.paginators.TimePeriod.MONTH ? 3 : s.time == net.dean.jraw.paginators.TimePeriod.YEAR ? 4 : 5;
    }

    public static java.lang.Integer getSearchType() {
        return me.ccrama.redditslide.util.SortingUtil.search == net.dean.jraw.paginators.SubmissionSearchPaginator.SearchSort.RELEVANCE ? 0 : me.ccrama.redditslide.util.SortingUtil.search == net.dean.jraw.paginators.SubmissionSearchPaginator.SearchSort.TOP ? 1 : me.ccrama.redditslide.util.SortingUtil.search == net.dean.jraw.paginators.SubmissionSearchPaginator.SearchSort.NEW ? 2 : 3;
    }

    public static android.text.Spannable[] getSortingTimesSpannables(java.lang.String currentSub) {
        return me.ccrama.redditslide.util.SortingUtil.getSortingTimesSpannables(me.ccrama.redditslide.util.SortingUtil.getSortingTimeId(currentSub), currentSub);
    }

    public static android.text.Spannable[] getSortingTimesSpannables(net.dean.jraw.paginators.TimePeriod time) {
        return me.ccrama.redditslide.util.SortingUtil.getSortingTimesSpannables(me.ccrama.redditslide.util.SortingUtil.getSortingTimeId(time), " ");
    }

    public static java.lang.String[] getSortingStrings() {
        java.lang.String[] current = new java.lang.String[]{ me.ccrama.redditslide.Reddit.getAppContext().getString(me.ccrama.redditslide.R.string.sorting_hot), me.ccrama.redditslide.Reddit.getAppContext().getString(me.ccrama.redditslide.R.string.sorting_new), me.ccrama.redditslide.Reddit.getAppContext().getString(me.ccrama.redditslide.R.string.sorting_rising), me.ccrama.redditslide.Reddit.getAppContext().getString(me.ccrama.redditslide.R.string.sorting_top), me.ccrama.redditslide.Reddit.getAppContext().getString(me.ccrama.redditslide.R.string.sorting_controversial), me.ccrama.redditslide.Reddit.getAppContext().getString(me.ccrama.redditslide.R.string.sorting_best) };
        return current;
    }

    public static java.lang.String[] getSortingCommentsStrings() {
        return new java.lang.String[]{ me.ccrama.redditslide.Reddit.getAppContext().getString(me.ccrama.redditslide.R.string.sorting_best), me.ccrama.redditslide.Reddit.getAppContext().getString(me.ccrama.redditslide.R.string.sorting_top), me.ccrama.redditslide.Reddit.getAppContext().getString(me.ccrama.redditslide.R.string.sorting_new), me.ccrama.redditslide.Reddit.getAppContext().getString(me.ccrama.redditslide.R.string.sorting_controversial), me.ccrama.redditslide.Reddit.getAppContext().getString(me.ccrama.redditslide.R.string.sorting_old), me.ccrama.redditslide.Reddit.getAppContext().getString(me.ccrama.redditslide.R.string.sorting_ama) };
    }

    public static java.lang.String[] getSortingTimesStrings() {
        java.lang.String[] current = new java.lang.String[]{ me.ccrama.redditslide.Reddit.getAppContext().getString(me.ccrama.redditslide.R.string.sorting_hour), me.ccrama.redditslide.Reddit.getAppContext().getString(me.ccrama.redditslide.R.string.sorting_day), me.ccrama.redditslide.Reddit.getAppContext().getString(me.ccrama.redditslide.R.string.sorting_week), me.ccrama.redditslide.Reddit.getAppContext().getString(me.ccrama.redditslide.R.string.sorting_month), me.ccrama.redditslide.Reddit.getAppContext().getString(me.ccrama.redditslide.R.string.sorting_year), me.ccrama.redditslide.Reddit.getAppContext().getString(me.ccrama.redditslide.R.string.sorting_all) };
        return current;
    }

    public static net.dean.jraw.paginators.TimePeriod getTime(java.lang.String subreddit, net.dean.jraw.paginators.TimePeriod defaultTime) {
        subreddit = subreddit.toLowerCase(java.util.Locale.ENGLISH);
        if (me.ccrama.redditslide.util.SortingUtil.times.containsKey(subreddit)) {
            return me.ccrama.redditslide.util.SortingUtil.times.get(subreddit);
        } else {
            return defaultTime;
        }
    }

    public static void setTime(java.lang.String s, net.dean.jraw.paginators.TimePeriod sort) {
        me.ccrama.redditslide.util.SortingUtil.times.put(s.toLowerCase(java.util.Locale.ENGLISH), sort);
    }

    private static android.text.Spannable[] getSortingSpannables(int sortingId, java.lang.String sub) {
        java.util.ArrayList<android.text.Spannable> spannables = new java.util.ArrayList<>();
        java.lang.String[] sortingStrings = me.ccrama.redditslide.util.SortingUtil.getSortingStrings();
        for (int i = 0; i < sortingStrings.length; i++) {
            android.text.SpannableString spanString = new android.text.SpannableString(sortingStrings[i]);
            if (i == sortingId) {
                spanString.setSpan(new android.text.style.ForegroundColorSpan(new me.ccrama.redditslide.ColorPreferences(me.ccrama.redditslide.Reddit.getAppContext()).getColor(sub)), 0, spanString.length(), 0);
                spanString.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, spanString.length(), 0);
            }
            spannables.add(spanString);
        }
        return spannables.toArray(new android.text.Spannable[spannables.size()]);
    }

    private static android.text.Spannable[] getSortingTimesSpannables(int sortingId, java.lang.String sub) {
        java.util.ArrayList<android.text.Spannable> spannables = new java.util.ArrayList<>();
        java.lang.String[] sortingStrings = me.ccrama.redditslide.util.SortingUtil.getSortingTimesStrings();
        for (int i = 0; i < sortingStrings.length; i++) {
            android.text.SpannableString spanString = new android.text.SpannableString(sortingStrings[i]);
            if (i == sortingId) {
                spanString.setSpan(new android.text.style.ForegroundColorSpan(new me.ccrama.redditslide.ColorPreferences(me.ccrama.redditslide.Reddit.getAppContext()).getColor(sub)), 0, spanString.length(), 0);
                spanString.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, spanString.length(), 0);
            }
            spannables.add(spanString);
        }
        return spannables.toArray(new android.text.Spannable[spannables.size()]);
    }

    public static void setSorting(java.lang.String s, net.dean.jraw.paginators.Sorting sort) {
        me.ccrama.redditslide.util.SortingUtil.sorting.put(s.toLowerCase(java.util.Locale.ENGLISH), sort);
    }

    public static final java.util.Map<java.lang.String, net.dean.jraw.paginators.Sorting> sorting = new java.util.HashMap<>();

    public static net.dean.jraw.paginators.Sorting getSorting(java.lang.String subreddit, net.dean.jraw.paginators.Sorting defaultSort) {
        subreddit = subreddit.toLowerCase(java.util.Locale.ENGLISH);
        if (me.ccrama.redditslide.util.SortingUtil.sorting.containsKey(subreddit)) {
            return me.ccrama.redditslide.util.SortingUtil.sorting.get(subreddit);
        } else {
            return defaultSort;
        }
    }
}