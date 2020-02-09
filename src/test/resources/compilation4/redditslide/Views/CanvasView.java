/**
 * CanvasView.java
 * <p/>
 * Copyright (c) 2014 Tomohiro IKEDA (Korilakkuma)
 * Released under the MIT license
 *
 * Code based off of https://github.com/Korilakkuma/CanvasView
 */
package me.ccrama.redditslide.Views;
import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.util.List;
// import android.util.Log;
// import android.widget.Toast;
/**
 * This class defines fields and methods for drawing.
 */
public class CanvasView extends android.view.View {
    // Enumeration for Mode
    public enum Mode {

        DRAW,
        TEXT,
        ERASER;}

    // Enumeration for Drawer
    public enum Drawer {

        PEN,
        LINE,
        RECTANGLE,
        CIRCLE,
        ELLIPSE,
        QUADRATIC_BEZIER,
        QUBIC_BEZIER;}

    private android.content.Context context = null;

    private android.graphics.Canvas canvas = null;

    private android.graphics.Bitmap bitmap = null;

    private java.util.List<android.graphics.Path> pathLists = new java.util.ArrayList<android.graphics.Path>();

    private java.util.List<android.graphics.Paint> paintLists = new java.util.ArrayList<android.graphics.Paint>();

    // for Eraser
    private int baseColor = android.graphics.Color.parseColor("#303030");

    // for Undo, Redo
    private int historyPointer = 0;

    // Flags
    private me.ccrama.redditslide.Views.CanvasView.Mode mode = me.ccrama.redditslide.Views.CanvasView.Mode.DRAW;

    private me.ccrama.redditslide.Views.CanvasView.Drawer drawer = me.ccrama.redditslide.Views.CanvasView.Drawer.PEN;

    private boolean isDown = false;

    // for Paint
    private android.graphics.Paint.Style paintStyle = android.graphics.Paint.Style.STROKE;

    private int paintStrokeColor = android.graphics.Color.BLACK;

    private int paintFillColor = android.graphics.Color.BLACK;

    private float paintStrokeWidth = 3.0F;

    private int opacity = 255;

    private float blur = 0.0F;

    private android.graphics.Paint.Cap lineCap = android.graphics.Paint.Cap.ROUND;

    // for Text
    private java.lang.String text = "";

    private android.graphics.Typeface fontFamily = android.graphics.Typeface.DEFAULT;

    private float fontSize = 32.0F;

    private android.graphics.Paint.Align textAlign = android.graphics.Paint.Align.RIGHT;// fixed


    private android.graphics.Paint textPaint = new android.graphics.Paint();

    private float textX = 0.0F;

    private float textY = 0.0F;

    // for Drawer
    private float startX = 0.0F;

    private float startY = 0.0F;

    private float controlX = 0.0F;

    private float controlY = 0.0F;

    /**
     * Copy Constructor
     *
     * @param context
     * 		
     * @param attrs
     * 		
     * @param defStyle
     * 		
     */
    public CanvasView(android.content.Context context, android.util.AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setup(context);
    }

    /**
     * Copy Constructor
     *
     * @param context
     * 		
     * @param attrs
     * 		
     */
    public CanvasView(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        this.setup(context);
    }

    /**
     * Copy Constructor
     *
     * @param context
     * 		
     */
    public CanvasView(android.content.Context context) {
        super(context);
        this.setup(context);
    }

    /**
     * Common initialization.
     *
     * @param context
     * 		
     */
    private void setup(android.content.Context context) {
        this.context = context;
        this.pathLists.add(new android.graphics.Path());
        this.paintLists.add(this.createPaint());
        this.historyPointer++;
        this.textPaint.setARGB(0, 255, 255, 255);
    }

    /**
     * This method creates the instance of Paint.
     * In addition, this method sets styles for Paint.
     *
     * @return paint This is returned as the instance of Paint
     */
    private android.graphics.Paint createPaint() {
        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setAntiAlias(true);
        paint.setStyle(this.paintStyle);
        paint.setStrokeWidth(this.paintStrokeWidth);
        paint.setStrokeCap(this.lineCap);
        paint.setStrokeJoin(android.graphics.Paint.Join.MITER);// fixed

        // for Text
        if (this.mode == me.ccrama.redditslide.Views.CanvasView.Mode.TEXT) {
            paint.setTypeface(this.fontFamily);
            paint.setTextSize(this.fontSize);
            paint.setTextAlign(this.textAlign);
            paint.setStrokeWidth(0.0F);
        }
        if (this.mode == me.ccrama.redditslide.Views.CanvasView.Mode.ERASER) {
            // Eraser
            paint.setXfermode(new android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR));
            paint.setARGB(0, 0, 0, 0);
            // paint.setColor(this.baseColor);
            // paint.setShadowLayer(this.blur, 0F, 0F, this.baseColor);
        } else {
            // Otherwise
            paint.setColor(this.paintStrokeColor);
            paint.setShadowLayer(this.blur, 0.0F, 0.0F, this.paintStrokeColor);
            paint.setAlpha(this.opacity);
        }
        return paint;
    }

    /**
     * This method initialize Path.
     * Namely, this method creates the instance of Path,
     * and moves current position.
     *
     * @param event
     * 		This is argument of onTouchEvent method
     * @return path This is returned as the instance of Path
     */
    private android.graphics.Path createPath(android.view.MotionEvent event) {
        android.graphics.Path path = new android.graphics.Path();
        // Save for ACTION_MOVE
        this.startX = event.getX();
        this.startY = event.getY();
        path.moveTo(this.startX, this.startY);
        return path;
    }

    /**
     * This method updates the lists for the instance of Path and Paint.
     * "Undo" and "Redo" are enabled by this method.
     *
     * @param path
     * 		the instance of Path
     * @param paint
     * 		the instance of Paint
     */
    private void updateHistory(android.graphics.Path path) {
        if (this.historyPointer == this.pathLists.size()) {
            this.pathLists.add(path);
            this.paintLists.add(this.createPaint());
            this.historyPointer++;
        } else {
            // On the way of Undo or Redo
            this.pathLists.set(this.historyPointer, path);
            this.paintLists.set(this.historyPointer, this.createPaint());
            this.historyPointer++;
            for (int i = this.historyPointer, size = this.paintLists.size(); i < size; i++) {
                this.pathLists.remove(this.historyPointer);
                this.paintLists.remove(this.historyPointer);
            }
        }
    }

    /**
     * This method gets the instance of Path that pointer indicates.
     *
     * @return the instance of Path
     */
    private android.graphics.Path getCurrentPath() {
        return this.pathLists.get(this.historyPointer - 1);
    }

    /**
     * This method draws text.
     *
     * @param canvas
     * 		the instance of Canvas
     */
    private void drawText(android.graphics.Canvas canvas) {
        if (this.text.length() <= 0) {
            return;
        }
        if (this.mode == me.ccrama.redditslide.Views.CanvasView.Mode.TEXT) {
            this.textX = this.startX;
            this.textY = this.startY;
            this.textPaint = this.createPaint();
        }
        float textX = this.textX;
        float textY = this.textY;
        android.graphics.Paint paintForMeasureText = new android.graphics.Paint();
        // Line break automatically
        float textLength = paintForMeasureText.measureText(this.text);
        float lengthOfChar = textLength / ((float) (this.text.length()));
        float restWidth = this.canvas.getWidth() - textX;// text-align : right

        int numChars = (lengthOfChar <= 0) ? 1 : ((int) (java.lang.Math.floor(((double) (restWidth / lengthOfChar)))));// The number of characters at 1 line

        int modNumChars = (numChars < 1) ? 1 : numChars;
        float y = textY;
        for (int i = 0, len = this.text.length(); i < len; i += modNumChars) {
            java.lang.String substring = "";
            if ((i + modNumChars) < len) {
                substring = this.text.substring(i, i + modNumChars);
            } else {
                substring = this.text.substring(i, len);
            }
            y += this.fontSize;
            canvas.drawText(substring, textX, y, this.textPaint);
        }
    }

    /**
     * This method defines processes on MotionEvent.ACTION_DOWN
     *
     * @param event
     * 		This is argument of onTouchEvent method
     */
    private void onActionDown(android.view.MotionEvent event) {
        switch (this.mode) {
            case DRAW :
            case ERASER :
                if ((this.drawer != me.ccrama.redditslide.Views.CanvasView.Drawer.QUADRATIC_BEZIER) && (this.drawer != me.ccrama.redditslide.Views.CanvasView.Drawer.QUBIC_BEZIER)) {
                    // Oherwise
                    this.updateHistory(this.createPath(event));
                    this.isDown = true;
                } else // Bezier
                if ((this.startX == 0.0F) && (this.startY == 0.0F)) {
                    // The 1st tap
                    this.updateHistory(this.createPath(event));
                } else {
                    // The 2nd tap
                    this.controlX = event.getX();
                    this.controlY = event.getY();
                    this.isDown = true;
                }
                break;
            case TEXT :
                this.startX = event.getX();
                this.startY = event.getY();
                break;
            default :
                break;
        }
    }

    /**
     * This method defines processes on MotionEvent.ACTION_MOVE
     *
     * @param event
     * 		This is argument of onTouchEvent method
     */
    private void onActionMove(android.view.MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (this.mode) {
            case DRAW :
            case ERASER :
                if ((this.drawer != me.ccrama.redditslide.Views.CanvasView.Drawer.QUADRATIC_BEZIER) && (this.drawer != me.ccrama.redditslide.Views.CanvasView.Drawer.QUBIC_BEZIER)) {
                    if (!isDown) {
                        return;
                    }
                    android.graphics.Path path = this.getCurrentPath();
                    switch (this.drawer) {
                        case PEN :
                            path.lineTo(x, y);
                            break;
                        case LINE :
                            path.reset();
                            path.moveTo(this.startX, this.startY);
                            path.lineTo(x, y);
                            break;
                        case RECTANGLE :
                            path.reset();
                            path.addRect(this.startX, this.startY, x, y, android.graphics.Path.Direction.CCW);
                            break;
                        case CIRCLE :
                            double distanceX = java.lang.Math.abs(((double) (this.startX - x)));
                            double distanceY = java.lang.Math.abs(((double) (this.startX - y)));
                            double radius = java.lang.Math.sqrt(java.lang.Math.pow(distanceX, 2.0) + java.lang.Math.pow(distanceY, 2.0));
                            path.reset();
                            path.addCircle(this.startX, this.startY, ((float) (radius)), android.graphics.Path.Direction.CCW);
                            break;
                        case ELLIPSE :
                            android.graphics.RectF rect = new android.graphics.RectF(this.startX, this.startY, x, y);
                            path.reset();
                            path.addOval(rect, android.graphics.Path.Direction.CCW);
                            break;
                        default :
                            break;
                    }
                } else {
                    if (!isDown) {
                        return;
                    }
                    android.graphics.Path path = this.getCurrentPath();
                    path.reset();
                    path.moveTo(this.startX, this.startY);
                    path.quadTo(this.controlX, this.controlY, x, y);
                }
                break;
            case TEXT :
                this.startX = x;
                this.startY = y;
                break;
            default :
                break;
        }
    }

    /**
     * This method defines processes on MotionEvent.ACTION_DOWN
     *
     * @param event
     * 		This is argument of onTouchEvent method
     */
    private void onActionUp(android.view.MotionEvent event) {
        if (isDown) {
            this.startX = 0.0F;
            this.startY = 0.0F;
            this.isDown = false;
        }
    }

    boolean imageChange = false;

    public float right;

    public float height;

    public float width;

    public float bottom;

    /**
     * This method updates the instance of Canvas (View)
     *
     * @param canvas
     * 		the new instance of Canvas
     */
    @java.lang.Override
    protected void onDraw(android.graphics.Canvas canvas) {
        super.onDraw(canvas);
        // Before "drawPath"
        canvas.drawColor(this.baseColor);
        if (this.bitmap != null) {
            m.setRectToRect(new android.graphics.RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new android.graphics.RectF(0, 0, canvas.getWidth(), canvas.getHeight()), android.graphics.Matrix.ScaleToFit.CENTER);
            bitmap = android.graphics.Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
            height = (canvas.getHeight() / 2) - (bitmap.getHeight() / 2);
            width = (canvas.getWidth() / 2) - (bitmap.getWidth() / 2);
            canvas.drawBitmap(bitmap, width, height, new android.graphics.Paint());
            right = canvas.getWidth();
            bottom = height + bitmap.getHeight();
            canvas.clipRect(0, height, right, bottom);
        }
        for (int i = 0; i < this.historyPointer; i++) {
            android.graphics.Path path = this.pathLists.get(i);
            android.graphics.Paint paint = this.paintLists.get(i);
            canvas.drawPath(path, paint);
        }
        this.drawText(canvas);
        this.canvas = canvas;
    }

    /**
     * This method set event listener for drawing.
     *
     * @param event
     * 		the instance of MotionEvent
     * @return 
     */
    @java.lang.Override
    public boolean onTouchEvent(android.view.MotionEvent event) {
        switch (event.getAction()) {
            case android.view.MotionEvent.ACTION_DOWN :
                this.onActionDown(event);
                break;
            case android.view.MotionEvent.ACTION_MOVE :
                this.onActionMove(event);
                break;
            case android.view.MotionEvent.ACTION_UP :
                this.onActionUp(event);
                break;
            default :
                break;
        }
        // Re draw
        this.invalidate();
        return true;
    }

    /**
     * This method is getter for mode.
     *
     * @return 
     */
    public me.ccrama.redditslide.Views.CanvasView.Mode getMode() {
        return this.mode;
    }

    /**
     * This method is setter for mode.
     *
     * @param mode
     * 		
     */
    public void setMode(me.ccrama.redditslide.Views.CanvasView.Mode mode) {
        this.mode = mode;
    }

    /**
     * This method is getter for drawer.
     *
     * @return 
     */
    public me.ccrama.redditslide.Views.CanvasView.Drawer getDrawer() {
        return this.drawer;
    }

    /**
     * This method is setter for drawer.
     *
     * @param drawer
     * 		
     */
    public void setDrawer(me.ccrama.redditslide.Views.CanvasView.Drawer drawer) {
        this.drawer = drawer;
    }

    /**
     * This method draws canvas again for Undo.
     *
     * @return If Undo is enabled, this is returned as true. Otherwise, this is returned as false.
     */
    public boolean undo() {
        if (this.historyPointer > 1) {
            this.historyPointer--;
            this.invalidate();
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method draws canvas again for Redo.
     *
     * @return If Redo is enabled, this is returned as true. Otherwise, this is returned as false.
     */
    public boolean redo() {
        if (this.historyPointer < this.pathLists.size()) {
            this.historyPointer++;
            this.invalidate();
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method initializes canvas.
     *
     * @return 
     */
    public void clear() {
        android.graphics.Path path = new android.graphics.Path();
        path.moveTo(0.0F, 0.0F);
        path.addRect(0.0F, 0.0F, 1000.0F, 1000.0F, android.graphics.Path.Direction.CCW);
        path.close();
        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setColor(android.graphics.Color.parseColor("#303030"));
        paint.setStyle(android.graphics.Paint.Style.FILL);
        if (this.historyPointer == this.pathLists.size()) {
            this.pathLists.add(path);
            this.paintLists.add(paint);
            this.historyPointer++;
        } else {
            // On the way of Undo or Redo
            this.pathLists.set(this.historyPointer, path);
            this.paintLists.set(this.historyPointer, paint);
            this.historyPointer++;
            for (int i = this.historyPointer, size = this.paintLists.size(); i < size; i++) {
                this.pathLists.remove(this.historyPointer);
                this.paintLists.remove(this.historyPointer);
            }
        }
        this.text = "";
        // Clear
        this.invalidate();
    }

    /**
     * This method is getter for canvas background color
     *
     * @return 
     */
    public int getBaseColor() {
        return this.baseColor;
    }

    /**
     * This method is setter for canvas background color
     *
     * @param color
     * 		
     */
    public void setBaseColor(int color) {
        this.baseColor = color;
    }

    /**
     * This method is getter for drawn text.
     *
     * @return 
     */
    public java.lang.String getText() {
        return this.text;
    }

    /**
     * This method is setter for drawn text.
     *
     * @param text
     * 		
     */
    public void setText(java.lang.String text) {
        this.text = text;
    }

    /**
     * This method is getter for stroke or fill.
     *
     * @return 
     */
    public android.graphics.Paint.Style getPaintStyle() {
        return this.paintStyle;
    }

    /**
     * This method is setter for stroke or fill.
     *
     * @param style
     * 		
     */
    public void setPaintStyle(android.graphics.Paint.Style style) {
        this.paintStyle = style;
    }

    /**
     * This method is getter for stroke color.
     *
     * @return 
     */
    public int getPaintStrokeColor() {
        return this.paintStrokeColor;
    }

    /**
     * This method is setter for stroke color.
     *
     * @param color
     * 		
     */
    public void setPaintStrokeColor(int color) {
        this.paintStrokeColor = color;
    }

    /**
     * This method is getter for fill color.
     * But, current Android API cannot set fill color (?).
     *
     * @return 
     */
    public int getPaintFillColor() {
        return this.paintFillColor;
    }

    /**
     * This method is setter for fill color.
     * But, current Android API cannot set fill color (?).
     *
     * @param color
     * 		
     */
    public void setPaintFillColor(int color) {
        this.paintFillColor = color;
    }

    /**
     * This method is getter for stroke width.
     *
     * @return 
     */
    public float getPaintStrokeWidth() {
        return this.paintStrokeWidth;
    }

    /**
     * This method is setter for stroke width.
     *
     * @param width
     * 		
     */
    public void setPaintStrokeWidth(float width) {
        if (width >= 0) {
            this.paintStrokeWidth = width;
        } else {
            this.paintStrokeWidth = 3.0F;
        }
    }

    /**
     * This method is getter for alpha.
     *
     * @return 
     */
    public int getOpacity() {
        return this.opacity;
    }

    /**
     * This method is setter for alpha.
     * The 1st argument must be between 0 and 255.
     *
     * @param opacity
     * 		
     */
    public void setOpacity(int opacity) {
        if ((opacity >= 0) && (opacity <= 255)) {
            this.opacity = opacity;
        } else {
            this.opacity = 255;
        }
    }

    /**
     * This method is getter for amount of blur.
     *
     * @return 
     */
    public float getBlur() {
        return this.blur;
    }

    /**
     * This method is setter for amount of blur.
     * The 1st argument is greater than or equal to 0.0.
     *
     * @param blur
     * 		
     */
    public void setBlur(float blur) {
        if (blur >= 0) {
            this.blur = blur;
        } else {
            this.blur = 0.0F;
        }
    }

    /**
     * This method is getter for line cap.
     *
     * @return 
     */
    public android.graphics.Paint.Cap getLineCap() {
        return this.lineCap;
    }

    /**
     * This method is setter for line cap.
     *
     * @param cap
     * 		
     */
    public void setLineCap(android.graphics.Paint.Cap cap) {
        this.lineCap = cap;
    }

    /**
     * This method is getter for font size,
     *
     * @return 
     */
    public float getFontSize() {
        return this.fontSize;
    }

    /**
     * This method is setter for font size.
     * The 1st argument is greater than or equal to 0.0.
     *
     * @param size
     * 		
     */
    public void setFontSize(float size) {
        if (size >= 0.0F) {
            this.fontSize = size;
        } else {
            this.fontSize = 32.0F;
        }
    }

    /**
     * This method is getter for font-family.
     *
     * @return 
     */
    public android.graphics.Typeface getFontFamily() {
        return this.fontFamily;
    }

    /**
     * This method is setter for font-family.
     *
     * @param face
     * 		
     */
    public void setFontFamily(android.graphics.Typeface face) {
        this.fontFamily = face;
    }

    /**
     * This method gets current canvas as bitmap.
     *
     * @return This is returned as bitmap.
     */
    public android.graphics.Bitmap getBitmap() {
        this.setDrawingCacheEnabled(false);
        this.setDrawingCacheEnabled(true);
        return android.graphics.Bitmap.createBitmap(this.getDrawingCache());
    }

    /**
     * This method gets current canvas as scaled bitmap.
     *
     * @return This is returned as scaled bitmap.
     */
    public android.graphics.Bitmap getScaleBitmap(int w, int h) {
        this.setDrawingCacheEnabled(false);
        this.setDrawingCacheEnabled(true);
        return android.graphics.Bitmap.createScaledBitmap(this.getDrawingCache(), w, h, true);
    }

    /**
     * This method draws the designated bitmap to canvas.
     *
     * @param bitmap
     * 		
     */
    public void drawBitmap(android.graphics.Bitmap bitmap) {
        this.bitmap = bitmap;
        imageChange = true;
        this.invalidate();
    }

    /**
     * This method draws the designated byte array of bitmap to canvas.
     *
     * @param byteArray
     * 		This is returned as byte array of bitmap.
     */
    public void drawBitmap(byte[] byteArray) {
        this.drawBitmap(android.graphics.BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
    }

    /**
     * This static method gets the designated bitmap as byte array.
     *
     * @param bitmap
     * 		
     * @param format
     * 		
     * @param quality
     * 		
     * @return This is returned as byte array of bitmap.
     */
    public static byte[] getBitmapAsByteArray(android.graphics.Bitmap bitmap, android.graphics.Bitmap.CompressFormat format, int quality) {
        java.io.ByteArrayOutputStream byteArrayOutputStream = new java.io.ByteArrayOutputStream();
        bitmap.compress(format, quality, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * This method gets the bitmap as byte array.
     *
     * @param format
     * 		
     * @param quality
     * 		
     * @return This is returned as byte array of bitmap.
     */
    public byte[] getBitmapAsByteArray(android.graphics.Bitmap.CompressFormat format, int quality) {
        java.io.ByteArrayOutputStream byteArrayOutputStream = new java.io.ByteArrayOutputStream();
        this.getBitmap().compress(format, quality, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * This method gets the bitmap as byte array.
     * Bitmap format is PNG, and quality is 100.
     *
     * @return This is returned as byte array of bitmap.
     */
    public byte[] getBitmapAsByteArray() {
        return this.getBitmapAsByteArray(android.graphics.Bitmap.CompressFormat.PNG, 100);
    }

    private android.graphics.Matrix m = new android.graphics.Matrix();
}