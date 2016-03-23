package ck.panda.constants;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * All the common constants for the application will go here.
 *
 */
public class EmailConstants {

    /**
     * Makes sure that utility classes (classes that contain only static methods or fields in their API) do not have a
     * public constructor.
     */
    protected EmailConstants() {
        throw new UnsupportedOperationException();
    }

    /** Constant used to account related event. */
    public static final String ACCOUNT = "User";

    /** Constant used to account signup related event. */
    public static final String SUBJECT_ACCOUNT_SIGNUP = "Account created successfully";

    }
