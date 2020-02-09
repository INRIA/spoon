package me.ccrama.redditslide.Views;
/**
 * Created by Carlos on 11/5/2016.
 */
public class ImageInsertEditText extends android.widget.EditText {
    public interface ImageSelectedCallback {
        void onImageSelected(android.net.Uri content, java.lang.String mimeType);
    }

    // region view constructors
    public ImageInsertEditText(android.content.Context context) {
        super(context);
    }

    public ImageInsertEditText(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageInsertEditText(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ImageInsertEditText(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    // endregion
    private me.ccrama.redditslide.Views.ImageInsertEditText.ImageSelectedCallback callback;

    public void setImageSelectedCallback(me.ccrama.redditslide.Views.ImageInsertEditText.ImageSelectedCallback callback) {
        this.callback = callback;
    }

    @java.lang.Override
    public android.view.inputmethod.InputConnection onCreateInputConnection(android.view.inputmethod.EditorInfo attrs) {
        android.view.inputmethod.InputConnection con = super.onCreateInputConnection(attrs);
        android.support.v13.view.inputmethod.EditorInfoCompat.setContentMimeTypes(attrs, new java.lang.String[]{ "image/gif", "image/png" });
        return android.support.v13.view.inputmethod.InputConnectionCompat.createWrapper(con, attrs, new android.support.v13.view.inputmethod.InputConnectionCompat.OnCommitContentListener() {
            @java.lang.Override
            public boolean onCommitContent(android.support.v13.view.inputmethod.InputContentInfoCompat inputContentInfo, int flags, android.os.Bundle opts) {
                if (callback != null) {
                    if (android.support.v4.os.BuildCompat.isAtLeastNMR1() && ((flags & android.support.v13.view.inputmethod.InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION) != 0)) {
                        try {
                            inputContentInfo.requestPermission();
                        } catch (java.lang.Exception e) {
                            return false;
                        }
                    }
                    callback.onImageSelected(inputContentInfo.getContentUri(), inputContentInfo.getDescription().getMimeType(0));
                    return true;
                } else {
                    return false;
                }
            }
        });
    }
}