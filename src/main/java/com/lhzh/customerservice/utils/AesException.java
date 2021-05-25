package com.lhzh.customerservice.utils;

public class AesException extends Exception {

    public static final String IllegalAesKey ="非法IllegalAesKey" ;
    public static final String DecryptAESError = "DecryptAESError";
    public static final String IllegalBuffer = "IllegalBuffer" ;
    public static final String ValidateAppidError = "ValidateAppidError";

    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public AesException() {
    }

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public AesException(String message) {
        super(message);
    }
}
