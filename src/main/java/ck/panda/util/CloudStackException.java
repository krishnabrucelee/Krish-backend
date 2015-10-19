package ck.panda.util;

/**
 *  If the user credentials is invalid the CloudStackException throws exception.
 */
public class CloudStackException extends Exception {

    /** Default constructor. */
    public CloudStackException() {
        super();
    }

    /**
     * Throws error with a message.
     * @param message that consists of reason.
     */
    public CloudStackException(String message) {
        super(message);
    }

    /**
     * Displays message with throwable reason.
     * @param message that consists of reason.
     * @param cause for why the error occurs.
     */
    public CloudStackException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Throws only the reason.
     * @param cause for why the error occurs.
     */
    public CloudStackException(Throwable cause) {
        super(cause);
    }

    /**
     * Throws exception with a cause.
     * @param message that consists of reason.
     * @param cause for why the error occurs.
     * @param enableSuppression when exceptional error occurs
     * @param writableStackTrace shows error exception name.
     */
    protected CloudStackException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
