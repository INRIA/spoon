package test.resources.com.leafactor.cli.rules.RecycleRefactoringRule.DifferentTypesShouldRecycleAll;




public class Input {
    public void methodTypedArray(AttributeSet attrs , int defStyle) {
        final TypedArray a = getContext();
        String example = a.getString(0);
        if (a != null) {
            a.recycle();
        }
    }
    public void methodBitmap(AttributeSet attrs , int defStyle) {
        final Bitmap a = getContext();
        String example = a.getString(0);
        if (a != null) {
            a.recycle();
        }
    }
    public void methodCursor(AttributeSet attrs , int defStyle) {
        final Cursor a = getContext();
        String example = a.getString(0);
        if (a != null) {
            a.close();
        }
    }
    public void methodVelocityTracker(AttributeSet attrs , int defStyle) {
        final VelocityTracker a = getContext();
        String example = a.getString(0);
        if (a != null) {
            a.recycle();
        }
    }
    public void methodMessage(AttributeSet attrs , int defStyle) {
        final Message a = getContext();
        String example = a.getString(0);
        if (a != null) {
            a.recycle();
        }
    }
    public void methodMotionEvent(AttributeSet attrs , int defStyle) {
        final MotionEvent a = getContext();
        String example = a.getString(0);
        if (a != null) {
            a.recycle();
        }
    }
    public void methodParcel(AttributeSet attrs , int defStyle) {
        final Parcel a = getContext();
        String example = a.getString(0);
        if (a != null) {
            a.recycle();
        }
    }
    public void methodContentProviderClient(AttributeSet attrs , int defStyle) {
        final ContentProviderClient a = getContext();
        String example = a.getString(0);
        if (a != null) {
            a.release();
        }
    }
}