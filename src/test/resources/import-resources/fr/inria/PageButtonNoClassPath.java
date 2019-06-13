package fr.inria;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.carconnectivity.mlmediaplayer.R;

public class PageButtonNoClassPath extends FrameLayout {
    private final Button mButton;
    private int mCurrentActiveColor;

    public PageButtonNoClassPath(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mButton = (Button) findViewById(R.id.page_button_button);
        mCurrentActiveColor = getColor(R.color.c4_active_button_color);
        mCurrentActiveColor = getResources().getColor(R.color.c4_active_button_color);
        mCurrentActiveColor = getData().getResources().getColor(R.color.c4_active_button_color);
    }
}
