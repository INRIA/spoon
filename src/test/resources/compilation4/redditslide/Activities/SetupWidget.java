package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.util.SortingUtil;
import me.ccrama.redditslide.Adapters.SubChooseAdapter;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Visuals.FontPreferences;
import me.ccrama.redditslide.Widget.SubredditWidgetProvider;
import java.util.ArrayList;
import me.ccrama.redditslide.ColorPreferences;
import me.ccrama.redditslide.UserSubscriptions;
/**
 * Created by carlo_000 on 5/4/2016.
 */
public class SetupWidget extends me.ccrama.redditslide.Activities.BaseActivity {
    private int appWidgetId = android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;

    @java.lang.Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        disableSwipeBackLayout();
        getTheme().applyStyle(new me.ccrama.redditslide.Visuals.FontPreferences(this).getCommentFontStyle().getResId(), true);
        getTheme().applyStyle(new me.ccrama.redditslide.Visuals.FontPreferences(this).getPostFontStyle().getResId(), true);
        getTheme().applyStyle(new me.ccrama.redditslide.ColorPreferences(this).getFontStyle().getBaseId(), true);
        super.onCreate(savedInstanceState);
        assignAppWidgetId();
        doShortcut();
    }

    /**
     * Widget configuration activity,always receives appwidget Id appWidget Id =
     * unique id that identifies your widget analogy : same as setting view id
     * via @+id/viewname on layout but appwidget id is assigned by the system
     * itself
     */
    private void assignAppWidgetId() {
        android.os.Bundle extras = getIntent().getExtras();
        if (extras != null)
            appWidgetId = extras.getInt(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID, android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID);

    }

    android.view.View header;

    public void doShortcut() {
        setContentView(me.ccrama.redditslide.R.layout.activity_setup_widget);
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, me.ccrama.redditslide.R.string.widget_creation_title, true, true);
        header = getLayoutInflater().inflate(me.ccrama.redditslide.R.layout.widget_header, null);
        android.widget.ListView list = ((android.widget.ListView) (findViewById(me.ccrama.redditslide.R.id.subs)));
        final java.util.ArrayList<java.lang.String> sorted = me.ccrama.redditslide.UserSubscriptions.getSubscriptionsForShortcut(this);
        final me.ccrama.redditslide.Adapters.SubChooseAdapter adapter = new me.ccrama.redditslide.Adapters.SubChooseAdapter(this, sorted, me.ccrama.redditslide.UserSubscriptions.getAllSubreddits(this));
        list.addHeaderView(header);
        list.setAdapter(adapter);
        header.findViewById(me.ccrama.redditslide.R.id.sort).clearFocus();
        ((android.widget.EditText) (header.findViewById(me.ccrama.redditslide.R.id.sort))).addTextChangedListener(new android.text.TextWatcher() {
            @java.lang.Override
            public void beforeTextChanged(java.lang.CharSequence charSequence, int i, int i2, int i3) {
            }

            @java.lang.Override
            public void onTextChanged(java.lang.CharSequence charSequence, int i, int i2, int i3) {
            }

            @java.lang.Override
            public void afterTextChanged(android.text.Editable editable) {
                final java.lang.String result = editable.toString();
                adapter.getFilter().filter(result);
            }
        });
    }

    public java.lang.String name;

    /**
     * This method right now displays the widget and starts a Service to fetch
     * remote data from Server
     */
    public void startWidget() {
        final android.content.DialogInterface.OnClickListener l2 = new android.content.DialogInterface.OnClickListener() {
            @java.lang.Override
            public void onClick(android.content.DialogInterface dialogInterface, int i) {
                me.ccrama.redditslide.Widget.SubredditWidgetProvider.setSubFromid(appWidgetId, name, me.ccrama.redditslide.Activities.SetupWidget.this);
                int theme = 0;
                switch (((android.widget.RadioGroup) (header.findViewById(me.ccrama.redditslide.R.id.theme))).getCheckedRadioButtonId()) {
                    case me.ccrama.redditslide.R.id.dark :
                        theme = 1;
                        break;
                    case me.ccrama.redditslide.R.id.light :
                        theme = 2;
                        break;
                }
                int view = 0;
                switch (((android.widget.RadioGroup) (header.findViewById(me.ccrama.redditslide.R.id.type))).getCheckedRadioButtonId()) {
                    case me.ccrama.redditslide.R.id.big :
                        view = 1;
                        break;
                    case me.ccrama.redditslide.R.id.compact :
                        view = 2;
                        break;
                }
                me.ccrama.redditslide.Widget.SubredditWidgetProvider.setThemeToId(appWidgetId, theme, me.ccrama.redditslide.Activities.SetupWidget.this);
                me.ccrama.redditslide.Widget.SubredditWidgetProvider.setViewType(appWidgetId, view, me.ccrama.redditslide.Activities.SetupWidget.this);
                me.ccrama.redditslide.Widget.SubredditWidgetProvider.setSorting(appWidgetId, i, me.ccrama.redditslide.Activities.SetupWidget.this);
                if ((i == 3) || (i == 4)) {
                    com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(me.ccrama.redditslide.Activities.SetupWidget.this);
                    builder.setTitle(me.ccrama.redditslide.R.string.sorting_choose);
                    builder.setSingleChoiceItems(me.ccrama.redditslide.util.SortingUtil.getSortingTimesStrings(), me.ccrama.redditslide.util.SortingUtil.getSortingTimeId(""), new android.content.DialogInterface.OnClickListener() {
                        @java.lang.Override
                        public void onClick(android.content.DialogInterface dialogInterface, int i) {
                            me.ccrama.redditslide.Widget.SubredditWidgetProvider.setSortingTime(appWidgetId, i, me.ccrama.redditslide.Activities.SetupWidget.this);
                            {
                                android.content.Intent intent = new android.content.Intent();
                                intent.putExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                                setResult(android.app.Activity.RESULT_OK, intent);
                            }
                            android.content.Intent intent = new android.content.Intent(android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, me.ccrama.redditslide.Activities.SetupWidget.this, me.ccrama.redditslide.Widget.SubredditWidgetProvider.class);
                            intent.putExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{ appWidgetId });
                            sendBroadcast(intent);
                            finish();
                        }
                    });
                    builder.show();
                } else {
                    {
                        android.content.Intent intent = new android.content.Intent();
                        intent.putExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                        setResult(android.app.Activity.RESULT_OK, intent);
                    }
                    android.content.Intent intent = new android.content.Intent(android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, me.ccrama.redditslide.Activities.SetupWidget.this, me.ccrama.redditslide.Widget.SubredditWidgetProvider.class);
                    intent.putExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{ appWidgetId });
                    sendBroadcast(intent);
                    finish();
                }
            }
        };
        com.afollestad.materialdialogs.AlertDialogWrapper.Builder builder = new com.afollestad.materialdialogs.AlertDialogWrapper.Builder(this);
        builder.setTitle(me.ccrama.redditslide.R.string.sorting_choose);
        builder.setSingleChoiceItems(me.ccrama.redditslide.util.SortingUtil.getSortingStrings(), me.ccrama.redditslide.util.SortingUtil.getSortingId(""), l2);
        builder.show();
        // this intent is essential to show the widget
        // if this intent is not included,you can't show
        // widget on homescreen
    }
}