/**
 * Created by carlo_000 on 10/18/2015.
 */
/* DragSortRecycler

Added drag and drop functionality to your RecyclerView


Copyright 2014 Emile Belanger.

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
package me.ccrama.redditslide.DragSort;
import me.ccrama.redditslide.R;
class DragSortRecycler extends android.support.v7.widget.RecyclerView.ItemDecoration implements android.support.v7.widget.RecyclerView.OnItemTouchListener {
    private final android.graphics.Paint bgColor = new android.graphics.Paint();

    private int dragHandleWidth = 0;

    private int selectedDragItemPos = -1;

    private int fingerAnchorY;

    private final android.support.v7.widget.RecyclerView.OnScrollListener scrollListener = new android.support.v7.widget.RecyclerView.OnScrollListener() {
        @java.lang.Override
        public void onScrolled(android.support.v7.widget.RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            debugLog((("Scrolled: " + dx) + " ") + dy);
            fingerAnchorY -= dy;
        }
    };

    private int fingerY;

    private int fingerOffsetInViewY;

    private float autoScrollWindow = 0.1F;

    private float autoScrollSpeed = 0.5F;

    private android.graphics.drawable.BitmapDrawable floatingItem;

    private android.graphics.Rect floatingItemStatingBounds;

    private android.graphics.Rect floatingItemBounds;

    private float floatingItemAlpha = 0.5F;

    private int floatingItemBgColor = 0;

    private int viewHandleId = -1;

    private me.ccrama.redditslide.DragSort.DragSortRecycler.OnItemMovedListener moveInterface;

    private boolean isDragging;

    @android.support.annotation.Nullable
    private me.ccrama.redditslide.DragSort.DragSortRecycler.OnDragStateChangedListener dragStateChangedListener;

    private void debugLog(java.lang.String log) {
        boolean DEBUG = false;
        java.lang.String TAG = "DragSortRecycler";
        if (DEBUG)
            android.util.Log.d(TAG, log);

    }

    public android.support.v7.widget.RecyclerView.OnScrollListener getScrollListener() {
        return scrollListener;
    }

    /* Set the item move interface */
    public void setOnItemMovedListener(me.ccrama.redditslide.DragSort.DragSortRecycler.OnItemMovedListener swif) {
        moveInterface = swif;
    }

    public void setViewHandleId() {
        viewHandleId = me.ccrama.redditslide.R.id.dragit;
    }

    public void setLeftDragArea(int w) {
        dragHandleWidth = w;
    }

    public void setFloatingAlpha() {
        floatingItemAlpha = 0.4F;
    }

    public void setFloatingBgColor(int c) {
        floatingItemBgColor = c;
    }

    /* Set the window at top and bottom of list, must be between 0 and 0.5
    For example 0.1 uses the top and bottom 10% of the lists for scrolling
     */
    public void setAutoScrollWindow() {
        autoScrollWindow = 0.1F;
    }

    /* Set the autoscroll speed, default is 0.5 */
    public void setAutoScrollSpeed() {
        autoScrollSpeed = 0.3F;
    }

    @java.lang.Override
    public void getItemOffsets(android.graphics.Rect outRect, android.view.View view, android.support.v7.widget.RecyclerView rv, android.support.v7.widget.RecyclerView.State state) {
        super.getItemOffsets(outRect, view, rv, state);
        debugLog("getItemOffsets");
        debugLog("View top = " + view.getTop());
        if (selectedDragItemPos != (-1)) {
            int itemPos = rv.getChildPosition(view);
            debugLog("itemPos =" + itemPos);
            if (!canDragOver(itemPos)) {
                return;
            }
            if (itemPos == selectedDragItemPos) {
                view.setVisibility(android.view.View.INVISIBLE);
            } else {
                // Make view visible incase invisible
                view.setVisibility(android.view.View.VISIBLE);
                // Find middle of the floatingItem
                float floatMiddleY = floatingItemBounds.top + (floatingItemBounds.height() / 2);
                // Moving down the list
                // These will auto-animate if the device continually sends touch motion events
                // if (totalMovment>0)
                {
                    if ((itemPos > selectedDragItemPos) && (view.getTop() < floatMiddleY)) {
                        float amountUp = (floatMiddleY - view.getTop()) / ((float) (view.getHeight()));
                        // amountUp *= 0.5f;
                        if (amountUp > 1)
                            amountUp = 1;

                        outRect.top = -((int) (floatingItemBounds.height() * amountUp));
                        outRect.bottom = ((int) (floatingItemBounds.height() * amountUp));
                    }
                }// Moving up the list

                // else if (totalMovment < 0)
                {
                    if ((itemPos < selectedDragItemPos) && (view.getBottom() > floatMiddleY)) {
                        float amountDown = (((float) (view.getBottom())) - floatMiddleY) / ((float) (view.getHeight()));
                        // amountDown *= 0.5f;
                        if (amountDown > 1)
                            amountDown = 1;

                        outRect.top = ((int) (floatingItemBounds.height() * amountDown));
                        outRect.bottom = -((int) (floatingItemBounds.height() * amountDown));
                    }
                }
            }
        } else {
            outRect.top = 0;
            outRect.bottom = 0;
            // Make view visible incase invisible
            view.setVisibility(android.view.View.VISIBLE);
        }
    }

    /**
     * Find the new position by scanning through the items on
     * screen and finding the positional relationship.
     * This *seems* to work, another method would be to use
     * getItemOffsets, but I think that could miss items?..
     */
    private int getNewPostion(android.support.v7.widget.RecyclerView rv) {
        int itemsOnScreen = rv.getLayoutManager().getChildCount();
        float floatMiddleY = floatingItemBounds.top + (floatingItemBounds.height() / 2);
        int above = 0;
        int below = java.lang.Integer.MAX_VALUE;
        // Scan though items on screen, however they may not
        for (int n = 0; n < itemsOnScreen; n++) {
            // be in order!
            android.view.View view = rv.getLayoutManager().getChildAt(n);
            if (view.getVisibility() != android.view.View.VISIBLE)
                continue;

            int itemPos = rv.getChildPosition(view);
            // Don't check against itself!
            if (itemPos == selectedDragItemPos)
                continue;

            float viewMiddleY = view.getTop() + (view.getHeight() / 2);
            // Is above this item
            if (floatMiddleY > viewMiddleY) {
                if (itemPos > above)
                    above = itemPos;

            } else // Is below this item
            if ((floatMiddleY <= viewMiddleY) && (itemPos < below)) {
                below = itemPos;
            }
        }
        debugLog((("above = " + above) + " below = ") + below);
        if (below != java.lang.Integer.MAX_VALUE) {
            // Need to count itself
            if (below < selectedDragItemPos)
                below++;

            return below - 1;
        } else {
            if (above < selectedDragItemPos)
                above++;

            return above;
        }
    }

    @java.lang.Override
    public boolean onInterceptTouchEvent(android.support.v7.widget.RecyclerView rv, android.view.MotionEvent e) {
        debugLog("onInterceptTouchEvent");
        // if (e.getAction() == MotionEvent.ACTION_DOWN)
        {
            android.view.View itemView = rv.findChildViewUnder(e.getX(), e.getY());
            if (itemView == null)
                return false;

            boolean dragging = false;
            if ((dragHandleWidth > 0) && (e.getX() < dragHandleWidth)) {
                dragging = true;
            } else if (viewHandleId != (-1)) {
                // Find the handle in the list item
                android.view.View handleView = itemView.findViewById(viewHandleId);
                if (handleView == null) {
                    java.lang.String TAG = "DragSortRecycler";
                    android.util.Log.e(TAG, ("The view ID " + viewHandleId) + " was not found in the RecycleView item");
                    return false;
                }
                // View should be visible to drag
                if (handleView.getVisibility() != android.view.View.VISIBLE) {
                    return false;
                }
                // We need to find the relative position of the handle to the parent view
                // Then we can work out if the touch is within the handle
                int[] parentItemPos = new int[2];
                itemView.getLocationInWindow(parentItemPos);
                int[] handlePos = new int[2];
                handleView.getLocationInWindow(handlePos);
                int xRel = handlePos[0] - parentItemPos[0];
                int yRel = handlePos[1] - parentItemPos[1];
                android.graphics.Rect touchBounds = new android.graphics.Rect(itemView.getLeft() + xRel, itemView.getTop() + yRel, (itemView.getLeft() + xRel) + handleView.getWidth(), (itemView.getTop() + yRel) + handleView.getHeight());
                if (touchBounds.contains(((int) (e.getX())), ((int) (e.getY()))))
                    dragging = true;

                debugLog((("parentItemPos = " + parentItemPos[0]) + " ") + parentItemPos[1]);
                debugLog((("handlePos = " + handlePos[0]) + " ") + handlePos[1]);
            }
            if (dragging) {
                debugLog("Started Drag");
                setIsDragging(true);
                floatingItem = createFloatingBitmap(itemView);
                fingerAnchorY = ((int) (e.getY()));
                fingerOffsetInViewY = fingerAnchorY - itemView.getTop();
                fingerY = fingerAnchorY;
                selectedDragItemPos = rv.getChildPosition(itemView);
                debugLog("selectedDragItemPos = " + selectedDragItemPos);
                return true;
            }
        }
        return false;
    }

    @java.lang.Override
    public void onTouchEvent(android.support.v7.widget.RecyclerView rv, android.view.MotionEvent e) {
        debugLog("onTouchEvent");
        if ((e.getAction() == android.view.MotionEvent.ACTION_UP) || (e.getAction() == android.view.MotionEvent.ACTION_CANCEL)) {
            if ((e.getAction() == android.view.MotionEvent.ACTION_UP) && (selectedDragItemPos != (-1))) {
                int newPos = getNewPostion(rv);
                if (moveInterface != null)
                    moveInterface.onItemMoved(selectedDragItemPos, newPos);

            }
            setIsDragging(false);
            selectedDragItemPos = -1;
            floatingItem = null;
            rv.invalidateItemDecorations();
            return;
        }
        fingerY = ((int) (e.getY()));
        if (floatingItem != null) {
            floatingItemBounds.top = fingerY - fingerOffsetInViewY;
            // Allow half the view out the top
            if (floatingItemBounds.top < ((-floatingItemStatingBounds.height()) / 2))
                floatingItemBounds.top = (-floatingItemStatingBounds.height()) / 2;

            floatingItemBounds.bottom = floatingItemBounds.top + floatingItemStatingBounds.height();
            floatingItem.setBounds(floatingItemBounds);
        }
        // Do auto scrolling at end of list
        float scrollAmount = 0;
        if (fingerY > (rv.getHeight() * (1 - autoScrollWindow))) {
            scrollAmount = fingerY - (rv.getHeight() * (1 - autoScrollWindow));
        } else if (fingerY < (rv.getHeight() * autoScrollWindow)) {
            scrollAmount = fingerY - (rv.getHeight() * autoScrollWindow);
        }
        debugLog("Scroll: " + scrollAmount);
        scrollAmount *= autoScrollSpeed;
        rv.scrollBy(0, ((int) (scrollAmount)));
        rv.invalidateItemDecorations();// Redraw

    }

    @java.lang.Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    private void setIsDragging(final boolean dragging) {
        if (dragging != isDragging) {
            isDragging = dragging;
            if (dragStateChangedListener != null) {
                if (isDragging) {
                    dragStateChangedListener.onDragStart();
                } else {
                    dragStateChangedListener.onDragStop();
                }
            }
        }
    }

    public void setOnDragStateChangedListener(final me.ccrama.redditslide.DragSort.DragSortRecycler.OnDragStateChangedListener dragStateChangedListener) {
        this.dragStateChangedListener = dragStateChangedListener;
    }

    @java.lang.Override
    public void onDrawOver(android.graphics.Canvas c, android.support.v7.widget.RecyclerView parent, android.support.v7.widget.RecyclerView.State state) {
        if (floatingItem != null) {
            floatingItem.setAlpha(((int) (255 * floatingItemAlpha)));
            bgColor.setColor(floatingItemBgColor);
            c.drawRect(floatingItemBounds, bgColor);
            floatingItem.draw(c);
        }
    }

    /**
     *
     *
     * @param position
     * 		
     * @return True if we can drag the item over this position, False if not.
     */
    private boolean canDragOver(int position) {
        return true;
    }

    private android.graphics.drawable.BitmapDrawable createFloatingBitmap(android.view.View v) {
        floatingItemStatingBounds = new android.graphics.Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        floatingItemBounds = new android.graphics.Rect(floatingItemStatingBounds);
        android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(floatingItemStatingBounds.width(), floatingItemStatingBounds.height(), android.graphics.Bitmap.Config.ARGB_8888);
        android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
        v.draw(canvas);
        android.graphics.drawable.BitmapDrawable retDrawable = new android.graphics.drawable.BitmapDrawable(v.getResources(), bitmap);
        if (bitmap != null) {
            bitmap.recycle();
        }
        retDrawable.setBounds(floatingItemBounds);
        return retDrawable;
    }

    public interface OnItemMovedListener {
        void onItemMoved(int from, int to);
    }

    public interface OnDragStateChangedListener {
        void onDragStart();

        void onDragStop();
    }
}