package me.ccrama.redditslide.util;
/**
 * Created by Fernando Barillas on 5/2/16.
 *
 * Allows easier validation of EditText input via the use of an InputFilter. This way invalid text
 * is not allowed to be input.
 */
// TODO: Add validation for subreddits/multireddits
public class EditTextValidator {
    private EditTextValidator() {
    }

    /**
     * Validates EditTexts intended for reddit username input. Valid characters include:
     * A-Z, a-z
     * 0-9
     * - (hyphen)
     * _ (underscore)
     *
     * @param editText
     * 		The EditText to validate a username for
     */
    public static void validateUsername(final android.widget.EditText editText) {
        if (editText == null)
            return;

        android.text.InputFilter filter = new android.text.InputFilter() {
            @java.lang.Override
            public java.lang.CharSequence filter(java.lang.CharSequence source, int start, int end, android.text.Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    char character = source.charAt(i);
                    if (((!java.lang.Character.isLetterOrDigit(character)) && (character != '_')) && (character != '-')) {
                        return "";
                    }
                }
                return null;
            }
        };
        editText.setFilters(new android.text.InputFilter[]{ filter });
    }
}