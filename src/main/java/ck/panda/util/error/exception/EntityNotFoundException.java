package ck.panda.util.error.exception;

/**
 * An Exception class used to handle EntityNotFoundException.
 */
public class EntityNotFoundException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor to display error message for EntityNotFoundException.
     * 
     * @param message to be displayed as exception message.
     */
    public EntityNotFoundException(String message) {
        super(message);
    }
}
