package me.ccrama.redditslide.Views;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by Carlos on 9/13/2016.
 */
public class RoundImageTriangleView extends com.makeramen.roundedimageview.RoundedImageView {
    public RoundImageTriangleView(android.content.Context context) {
        super(context);
    }

    public RoundImageTriangleView(android.content.Context context, android.util.AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public RoundImageTriangleView(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    int color = android.graphics.Color.TRANSPARENT;

    public void setFlagColor(@android.support.annotation.ColorRes
    int color) {
        this.color = color;
        invalidate();
    }

    protected void onDraw(android.graphics.Canvas canvas) {
        super.onDraw(canvas);
        // Various allocations:
        new java.lang.String("foo");
        boolean b = java.lang.Boolean.valueOf(true);// auto-boxing

        java.lang.Integer i2 = new java.lang.Integer(i);
        myOtherMap.clear();
        // Non-allocations
        super.animate();
        int x = 4 + '5';
        // This will involve allocations, but we don't track
        /* maybe soon int w = getWidth() / 5;

        Path path = new Path();
        path.moveTo( w*4, 0);
        path.lineTo( 5 * w , 0);
        path.lineTo( 5 * w , w);
        path.lineTo( w*4 , 0);
        path.close();

        Paint p = new Paint();
        p.setColor( color );

        canvas.drawPath(path, p);
         */
    }

    private java.lang.String s = new java.lang.String("bar");

    private java.lang.Integer i = new java.lang.Integer(5);

    private java.lang.Integer i3 = ((java.lang.Integer) (new java.lang.Integer(2)));

    private java.util.Map<java.lang.Integer, java.lang.Object> myOtherMap = new java.util.HashMap<java.lang.Integer, java.lang.Object>();
}