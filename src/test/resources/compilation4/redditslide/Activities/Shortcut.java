package me.ccrama.redditslide.Activities;
import me.ccrama.redditslide.Adapters.SubChooseAdapter;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.Visuals.FontPreferences;
import java.util.ArrayList;
import me.ccrama.redditslide.ColorPreferences;
import me.ccrama.redditslide.UserSubscriptions;
/**
 * Created by ccrama on 10/2/2015.
 */
public class Shortcut extends me.ccrama.redditslide.Activities.BaseActivity {
    private java.lang.String name = "";

    public static android.graphics.Bitmap drawableToBitmap(android.graphics.drawable.Drawable drawable) {
        android.graphics.Bitmap bitmap;
        if (drawable instanceof android.graphics.drawable.BitmapDrawable) {
            android.graphics.drawable.BitmapDrawable bitmapDrawable = ((android.graphics.drawable.BitmapDrawable) (drawable));
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if ((drawable.getIntrinsicWidth() <= 0) || (drawable.getIntrinsicHeight() <= 0)) {
            bitmap = android.graphics.Bitmap.createBitmap(1, 1, android.graphics.Bitmap.Config.ARGB_8888);// Single color bitmap will be created of 1x1 pixel

        } else {
            bitmap = android.graphics.Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), android.graphics.Bitmap.Config.ARGB_8888);
        }
        android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @java.lang.Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        getTheme().applyStyle(new me.ccrama.redditslide.Visuals.FontPreferences(this).getCommentFontStyle().getResId(), true);
        getTheme().applyStyle(new me.ccrama.redditslide.Visuals.FontPreferences(this).getPostFontStyle().getResId(), true);
        getTheme().applyStyle(new me.ccrama.redditslide.ColorPreferences(this).getFontStyle().getBaseId(), true);
        super.onCreate(savedInstanceState);
        doShortcut();
    }

    android.view.View header;

    public void doShortcut() {
        setContentView(me.ccrama.redditslide.R.layout.activity_setup_widget);
        setupAppBar(me.ccrama.redditslide.R.id.toolbar, me.ccrama.redditslide.R.string.shortcut_creation_title, true, true);
        header = getLayoutInflater().inflate(me.ccrama.redditslide.R.layout.shortcut_header, null);
        android.widget.ListView list = ((android.widget.ListView) (findViewById(me.ccrama.redditslide.R.id.subs)));
        list.addHeaderView(header);
        final java.util.ArrayList<java.lang.String> sorted = me.ccrama.redditslide.UserSubscriptions.getSubscriptionsForShortcut(this);
        final me.ccrama.redditslide.Adapters.SubChooseAdapter adapter = new me.ccrama.redditslide.Adapters.SubChooseAdapter(this, sorted, me.ccrama.redditslide.UserSubscriptions.getAllSubreddits(this));
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
}