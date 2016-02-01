package ck.panda.constants;

/**
 * All the common constants for the application will go here.
 *
 */
public class GenericConstants {

    /**
     * Makes sure that utility classes (classes that contain only static methods or fields in their API) do not have a
     * public constructor.
     */
    protected GenericConstants() {
        throw new UnsupportedOperationException();
    }

    /** Constant used to strip range value from UI for pagination. */
    public static final String RANGE_PREFIX = "items=";

    /** Constant used to set content range response for pagination. */
    public static final String CONTENT_RANGE_HEADER = "Content-Range";

    /** Constant for default limit. */
    public static final Integer DEFAULTLIMIT = 10;

    /** Template architecture constant values. */
    public static final String[] TEMPLATE_ARCHITECTURE = {"32", "64"};

    /** Constant for generic name. */
    public static final String NAME = "name";

    /** Constant for AES encription. */
    public static final String ENCRYPT_ALGORITHM = "AES";

    /** Constant for character encoding. */
    public static final String CHARACTER_ENCODING = "UTF-8";

    /** Constant for token separator. */
    public static final String TOKEN_SEPARATOR = "@@";

    /** Constant for content type. */
    public static final String CONTENT_TYPE = "Content-Type";

    /** Constant for authentication date format. */
    public static final String AUTH_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";

    /** Constant for user login URL path. */
    public static final String LOGIN_URL = "/login";

    /** Constant for user logout redirect URL path. */
    public static final String LOGIN_OUT_URL = "/login?out=1";

    /** Constant for user logout URL path. */
    public static final String LOGOUT_URL = "/logout";

    /** Constant for cookie name. */
    public static final String COOKIES_NAME = "JSESSIONID";

    /** Page error seperator constant. */
    public static final String PAGE_ERROR_SEPARATOR = "PAGE_ERROR";
}
