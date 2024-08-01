package com.at;

/**
 * @author wenzhilong
 */
public class JaninoCompareException extends Exception {

    public JaninoCompareException() {
        super();
    }

    public JaninoCompareException(String message) {
        super(message);
    }

    public JaninoCompareException(String message, Throwable cause) {
        super(message, cause);
    }

    public JaninoCompareException(Throwable cause) {
        super(cause);
    }

    protected JaninoCompareException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
