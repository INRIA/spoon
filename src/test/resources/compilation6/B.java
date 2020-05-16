package compilation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.Button;
/** Some test data for the JavaPerformanceDetector */
@SuppressWarnings("unused")
public class A extends Button {

    public A(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private Rect cachedRect;

    @Override
    protected void onDraw(android.graphics.Canvas canvas) {
        super.onDraw(canvas);

        // Various allocations:
        new String("foo");

        // Cached object initialized lazily: should not complain about these
        if (cachedRect == null) {
            cachedRect = new Rect(0, 0, 100, 100);
        }
        if (cachedRect == null || cachedRect.width() != 50) {
            cachedRect = new Rect(0, 0, 50, 100);
        }

        boolean b = Boolean.valueOf(true); // auto-boxing

        Integer i2 = new Integer(i);
        myOtherMap.clear();

        // Non-allocations
        super.animate();
        int x = 4 + '5';

        // This will involve allocations, but we don't track
        // inter-procedural stuff here
        someOtherMethod();
    }

    void someOtherMethod() {
        // Allocations are okay here
        new String("foo");
        String s = new String("bar");
        boolean b = Boolean.valueOf(true); // auto-boxing

        // Sparse array candidates
        Map<Integer, String> myMap = new HashMap<Integer, String>();
        // Should use SparseBooleanArray
        Map<Integer, Boolean> myBoolMap = new HashMap<Integer, Boolean>();
        // Should use SparseIntArray
        Map<Integer, Integer> myIntMap = new java.util.HashMap<Integer, Integer>();

        // This one should not be reported:
        @SuppressLint("UseSparseArrays")
        Map<Integer, Object> myOtherMap = new HashMap<Integer, Object>();
    }

    public class DrawAllocationSampleTwo extends Button {
        public DrawAllocationSampleTwo(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }
        @Override
        protected void onDraw(android.graphics.Canvas canvas) {
            super.onDraw(canvas);
            array.clear();
            return;
        }

        private List<Integer> array = new ArrayList<Integer>();
    }

    private String s = new String("bar");

    private Integer i = new Integer(5);

    private Integer i3 = (Integer) new Integer(2);

    private Map<Integer, Object> myOtherMap = new HashMap<Integer, Object>();
}