package test.resources.com.leafactor.cli.rules.RecycleRefactoringRule.DifferentTypesShouldRecycleAll;

public class Input {
    public void methodTypedArray(AttributeSet attrs , int defStyle) {
        final TypedArray a = getContext();
        String example = a.getString(0);
    }
    public void methodBitmap(AttributeSet attrs , int defStyle) {
        final Bitmap a = getContext();
        String example = a.getString(0);
    }
    public void methodCursor(AttributeSet attrs , int defStyle) {
        final Cursor a = getContext();
        String example = a.getString(0);
    }
    public void methodVelocityTracker(AttributeSet attrs , int defStyle) {
        final VelocityTracker a = getContext();
        String example = a.getString(0);
    }
    public void methodMessage(AttributeSet attrs , int defStyle) {
        final Message a = getContext();
        String example = a.getString(0);
    }
    public void methodMotionEvent(AttributeSet attrs , int defStyle) {
        final MotionEvent a = getContext();
        String example = a.getString(0);
    }
    public void methodParcel(AttributeSet attrs , int defStyle) {
        final Parcel a = getContext();
        String example = a.getString(0);
    }
    public void methodContentProviderClient(AttributeSet attrs , int defStyle) {
        final ContentProviderClient a = getContext();
        String example = a.getString(0);
    }
}