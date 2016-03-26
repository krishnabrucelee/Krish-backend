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

    /** Constant used to account delete related event. */
    public static final String SUBJECT_ACCOUNT_DELETE = "Account deleted successfully";

    /** Constant used to account password reset related event. */
    public static final String SUBJECT_ACCOUNT_PASSWORD = "Account updated successfully";

    /** Constant used to system error alert related event. */
    public static final String SYSTEM_ERROR = "System Error";

    /** Constant for capacity. */
    public static final String EMAIL_CAPACITY = "CAPACITY";

    /** Constant for capacity. */
    public static final String EMAIL_ACCOUNT_SIGNUP = "ACCOUNT SIGNUP";

    /** Constant for capacity. */
    public static final String EMAIL_ACCOUNT_REMOVAL = "ACCOUNT REMOVAL";

    /** Constant for capacity. */
    public static final String EMAIL_PASSWORD_RESET = "PASSWORD RESET";

    /** Constant for capacity. */
    public static final String EMAIL_SYSTEM_ERROR = "SYSTEM ERROR";

    /** Constant for capacity. */
    public static final String EMAIL_Cpu = "Cpu";

    /** Constant for capacity. */
    public static final String EMAIL_Memory = "Memory";

    /** Constant for capacity. */
    public static final String EMAIL_Primary_storage = "Primary storage";

    /** Constant for capacity. */
    public static final String EMAIL_Ip = "Ip";

    /** Constant for capacity. */
    public static final String EMAIL_English = "English";

    /** Constant for capacity. */
    public static final String EMAIL_Chinese = "Chinese";

    /** Constant for capacity. */
    public static final String EMAIL_FreeMarker = "FreeMarker";

    /** Constant for capacity. */
    public static final String EMAIL_dataCenterId = "dataCenterId";

    /** Constant for capacity. */
    public static final String EMAIL_podId = "podId";

    public static final String EMAIL_TEMPLATE_user = "user";

    public static final String EMAIL_TEMPLATE_alert = "alert";

    public static final String EMAIL_TEMPLATE_capacity = "capacity";

    public static final String EMAIL_zonename = "zonename";

    }
