package ck.panda.constants;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

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

    /** Constant for default job status. */
    public static final String DEFAULT_JOB_STATUS = "10";

    /** Constants for job status. */
    public static final String ERROR_JOB_STATUS = "2", PROGRESS_JOB_STATUS = "0",  SUCCEEDED_JOB_STATUS = "1";

    /** Constant for generic exception status code. */
    public static final String NOT_IMPLEMENTED = "501";

    /** Resource type constants values. */
    public static final String RESOURCE_MEMORY = "0", RESOURCE_CPU = "1", RESOURCE_SECONDARY_STORAGE = "6",
            RESOURCE_PRIMARY_STORAGE = "3", RESOURCE_IP_ADDRESS = "4";

    /** Constant map for default resource types. */
    public static final Map<String, String> RESOURCE_CAPACITY = Arrays
            .stream(new String[][] {{"0", "9"},
                {"1", "8"},
                {"6", "11"},
                {"3", "10"},
                {"4", "1"}})
            .collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));

    /** Template architecture constant values. */
    public static final String[] TEMPLATE_ARCHITECTURE = {"32", "64"};

    /** Constant for generic name. */
    public static final String NAME = "name";

    /** Constant for AES encryption. */
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

    /** Full permission role constant. */
    public static final String FULL_PERMISSION = "FULL_PERMISSION";

    /** Constant for instance. */
    public static final String INSTANCE = "Instance";

    /** Constant for volume. */
    public static final String VOLUME = "Volume";

    /** Constant for upload volume. */
    public static final String UPLOAD_VOLUME = "UploadVolume";

    /** Constant for network. */
    public static final String NETWORK = "Network";

    /** Constant for IP. */
    public static final String IP = "IP";

    /** Constant for restore instance. */
    public static final String RESTORE_INSTANCE = "RestoreInstance";

    /** Constant for destroy status. */
    public static final String DESTROY = "Destroy";

    /** Constant for expunging status. */
    public static final String EXPUNGING = "Expunging";

    /** Constant for project. */
    public static final String PROJECT = "Project";

    /** Constant for department. */
    public static final String DEPARTMENT = "Department";

    /** Constant for update status. */
    public static final String UPDATE = "update";

    /** Constant for delete status. */
    public static final String DELETE = "delete";
   }
