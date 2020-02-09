package me.ccrama.redditslide.util;
/**
 * Exception thrown when encountering an invalid Base64 input character.
 *
 * @author nelson
 */
class Base64DecoderException extends java.lang.Exception {
    private static final long serialVersionUID = 1L;

    public Base64DecoderException() {
        super();
    }

    public Base64DecoderException(java.lang.String s) {
        super(s);
    }
}