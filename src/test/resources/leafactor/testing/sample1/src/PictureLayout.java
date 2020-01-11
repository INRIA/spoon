/*
 * Copyright (C) 2014 Valerio Bozzolan
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package anupam.acrylic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;


public class PictureLayout extends ViewGroup {
	private static String error = "PictureLayout can host only one direct child";
    private final Picture mPicture = new Picture();

    public PictureLayout(Context context) {
        super(context);
    }

    public PictureLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void addView(View child) {
        if (getChildCount() > 1) {
            throw new IllegalStateException(error);
        }

        super.addView(child);
    }

    @Override
    public void addView(View child, int index) {
        if (getChildCount() > 1) {
            throw new IllegalStateException(error);
        }

        super.addView(child, index);
    }

    @Override
    public void addView(View child, LayoutParams params) {
        if (getChildCount() > 1) {
            throw new IllegalStateException(error);
        }

        super.addView(child, params);
    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
        if (getChildCount() > 1) {
            throw new IllegalStateException(error);
        }

        super.addView(child, index, params);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int count = getChildCount();

        int maxHeight = 0;
        int maxWidth = 0;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
            }
        }

        maxWidth += getPaddingLeft() + getPaddingRight();
        maxHeight += getPaddingTop() + getPaddingBottom();

        Drawable drawable = getBackground();
        if (drawable != null) {
            maxHeight = Math.max(maxHeight, drawable.getMinimumHeight());
            maxWidth = Math.max(maxWidth, drawable.getMinimumWidth());
        }

        setMeasuredDimension(resolveSize(maxWidth, widthMeasureSpec),
                resolveSize(maxHeight, heightMeasureSpec));
    }

    private void drawPict(Canvas canvas, int x, int y, int w, int h,
                          float sx, float sy) {
        canvas.save();
        canvas.translate(x, y);
        canvas.clipRect(0, 0, w, h);
        canvas.scale(0.5f, 0.5f);
        canvas.scale(sx, sy, w, h);
        canvas.drawPicture(mPicture);
        canvas.restore();
    }

    @SuppressWarnings("unused")
	@Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(mPicture.beginRecording(getWidth(), getHeight()));
        mPicture.endRecording();

        int x = getWidth()/2;
        int y = getHeight()/2;

        if (false) {
            canvas.drawPicture(mPicture);
        } else {
            drawPict(canvas, 0, 0, x, y,  1,  1);
            drawPict(canvas, x, 0, x, y, -1,  1);
            drawPict(canvas, 0, y, x, y,  1, -1);
            drawPict(canvas, x, y, x, y, -1, -1);
        }
    }

    @Override
    public ViewParent invalidateChildInParent(int[] location, Rect dirty) {
        location[0] = getLeft();
        location[1] = getTop();
        dirty.set(0, 0, getWidth(), getHeight());
        return getParent();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = super.getChildCount();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final int childLeft = getPaddingLeft();
                final int childTop = getPaddingTop();
                child.layout(childLeft, childTop,
                        childLeft + child.getMeasuredWidth(),
                        childTop + child.getMeasuredHeight());

            }
        }
    }
}
