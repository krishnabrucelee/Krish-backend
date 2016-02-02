package ck.panda.constants;

/**
 * All the common cloudStack constants for the application will go here.
 *
 */
public class CloudStackConstants {

    /**
     * Makes sure that utility classes (classes that contain only static methods or fields in their API) do not have a
     * public constructor.
     */
    protected CloudStackConstants() {
        throw new UnsupportedOperationException();
    }

    /** Constant for upload types. */
    public static final String CS_UPLOAD_NOT_STARTED = "UploadNotStarted", CS_UPLOAD_OPERATION = "UploadOp",
            CS_UPLOAD_ABANDONED = "UploadAbandoned", CS_UPLOAD_ERROR = "UploadError";

    /** Constant for job types. */
    public static final String CS_JOB_ID = "jobid", CS_JOB_RESULT = "jobresult", CS_JOB_STATUS = "jobstatus";

    /** Constant for asynchronous job result response. */
    public static final String QUERY_ASYNC_JOB_RESULT_RESPONSE = "queryasyncjobresultresponse";

    /** Constant for finger print. */
    public static final String CS_FINGER_PRINT = "fingerprint";

    /** Constant for created date and time. */
    public static final String CS_CREATED = "created";

    /** Constant for custom offering. */
    public static final String CS_CUSTOM_STATUS = "iscustomized";

    /** Constant for custom offering iops. */
    public static final String CS_CUSTOM_IOPS_STATUS = "iscustomizediops";

    /** Constant for generic id. */
    public static final String CS_ID = "id";

    /** Constant for virtual machine id. */
    public static final String CS_VIRTUAL_MACHINE_ID = "virtualmachineid";

    /** Constant for iops. */
    public static final String CS_MAX_IOPS = "maxiops", CS_MIN_IOPS = "miniops";

    /** Constant for resource's state. */
    public static final String CS_STATE = "state";

    /** Constant for uploaded volume. */
    public static final String CS_UPLOADED = "Uploaded";

    /** Constant for offerings id. */
    public static final String CS_DISK_OFFERING_ID = "diskofferingid", CS_SERVICE_OFFERING_ID = "serviceofferingid";

    /** Constant for resource's tags. */
    public static final String CS_TAGS = "tags";

    /** Constant for offerings. */
    public static final String CS_DISK_OFFERING = "diskoffering", CS_STORAGE_OFFERING = "storageOffering";

    /** Constant for disk bytes rate. */
    public static final String CS_BYTES_READ = "bytesreadrate", CS_BYTES_WRITE = "byteswriterate";

    /** Constant for disk iops rate. */
    public static final String CS_IOPS_READ = "iopsreadrate", CS_IOPS_WRITE = "iopswriterate";

    /** Constant for disk size. */
    public static final String CS_DISK_SIZE = "disksize";

    /** Constant for custom offering iops. */
    public static final String CS_CUSTOM_IOPS = "customizediops";

    /** Constant for storage offering type. */
    public static final String CS_STORAGE_TYPE = "storagetype";

    /** Constant for custom disk offering. */
    public static final String CS_CUSTOM_OFFER = "customized";

    /** Constant for resource visibility. */
    public static final String CS_PUBLIC = "public";

    /** Constant for resource's description. */
    public static final String CS_DISPLAY_TEXT = "displaytext";

    /** Constant for password. */
    public static final String CS_PASSWORD = "password";

    /** Constant value for json response. */
    public static final String JSON = "json";

    /** Constant for domain id. */
    public static final String CS_DOMAIN_ID = "domainid";

    /** Constant for department id. */
    public static final String CS_DEPARTMENT_ID = "departmentid";

    /** Constant for project id. */
    public static final String CS_PROJECT_ID = "projectid";

    /** Constant for zone id. */
    public static final String CS_ZONE_ID = "zoneid";

    /** Constant for api key. */
    public static final String CS_API_KEY = "apikey";

    /** Constant for secret key. */
    public static final String CS_SECRET_KEY = "secretkey";

    /** Constant for status. */
    public static final String STATUS_ACTIVE = "true", STATUS_INACTIVE = "false";

    /** Constant for list all. */
    public static final String CS_LIST_ALL = "listall";

    /** Constant for account name. */
    public static final String CS_ACCOUNT = "account";

    /** Constant for disk size. */
    public static final String CS_SIZE = "size";

    /** Constant for generic name. */
    public static final String CS_NAME = "name";

    /** Constant for cloudStack error response. */
    public static final String CS_ERROR_CODE = "errorcode", CS_ERROR_TEXT = "errortext";

    /** Constant for login response. */
    public static final String CS_LOGIN_RESPONSE = "loginresponse";

    /** Constant for list user response. */
    public static final String CS_LIST_USER_RESPONSE = "listusersresponse";

    /** Constant for register user key response. */
    public static final String CS_REGISTER_KEY_RESPONSE = "registeruserkeysresponse";

<<<<<<< HEAD
    /** Constants for jobs status. */
    public static final String ERROR_JOB_STATUS = "2", PROGRESS_JOB_STATUS = "0", SUCCEEDED_JOB_STATUS = "1";

    /** Constant for volume type. */
    public static final String CS_VOLUME_TYPE = "type";
=======
    /** Constant for account type. */
    public static final String CS_ACCOUNT_TYPE = "accounttype";

    /** Constant for create account response. */
    public static final String CS_ACCOUNT_RESPONSE = "createaccountresponse";

    /** Constant for domain. */
    public static final String CS_DOMAIN = "domain";

    /** Constant for user name. */
    public static final String CS_USER_NAME = "username";

    /** Constant for first name of the user. */
    public static final String CS_FIRST_NAME = "firstname";

    /** Constant for last name of the user. */
    public static final String CS_LAST_NAME = "lastname";

    /** Constant for email of the user. */
    public static final String CS_EMAIL = "email";

    /** Constant for host id. */
    public static final String CS_HOST_ID = "hostid";

    /** Constant for template id. */
    public static final String CS_TEMPLATE_ID = "templateid";

    /** Constant for cpu details. */
    public static final String CS_CPU_NUMBER = "cpunumber", CS_CPU_SPEED = "cpuspeed", CS_CPU_USED = "cpuused";

    /** Constant for storage details. */
    public static final String CS_DISK_IO_READ = "diskioread", CS_DISK_IO_WRITE = "diskiowrite",
            CS_DISK_KBS_READ = "diskkbsread", CS_DISK_KBS_WRITE = "diskkbswrite";

    /** Constant for network details. */
    public static final String CS_NETWORK_KBS_READ = "networkkbsread", CS_NETWORK_KBS_WRITE = "networkkbswrite";

    /** Constant for password status. */
    public static final String CS_PASSWORD_STATUS = "passwordenabled";

    /** Constant for iso id. */
    public static final String CS_ISO_ID = "isoid";

    /** Constant for iso name. */
    public static final String CS_ISO_NAME = "isoname";

    /** Constant for nic. */
    public static final String CS_NIC = "nic";

    /** Constant for network id. */
    public static final String CS_NETWORK_ID = "networkid";

    /** Constant for instance name. */
    public static final String CS_INSTANCE_NAME = "instancename";

    /** Constant for user id. */
    public static final String CS_USER_ID = "userid";

    /** Constant for ip address. */
    public static final String CS_IP_ADDRESS = "ipaddress";

    /** Constant for primary memory. */
    public static final String CS_MEMORY = "memory";

    /** Constant for resource name. */
    public static final String CS_DISPLAY_NAME = "displayname";
>>>>>>> ddf963b11ff0a99b2a6a8f3c56119804093f74a6
}
