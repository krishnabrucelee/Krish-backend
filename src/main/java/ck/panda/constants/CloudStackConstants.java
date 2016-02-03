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
    public static final String CS_DISK_OFFERING_ID = "diskofferingid", CS_SERVICE_OFFERING_ID = "serviceofferingid",
        CS_NETWORK_OFFERING_ID = "networkofferingid";

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

    /** Constant for department. */
    public static final String CS_DEPARTMENT = "department";

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

    /** Constant for delete account response. */
    public static final String CS_DELETE_ACCOUNT_RESPONSE = "deleteaccountresponse";

    /** Constant for lsit account response. */
    public static final String CS_LIST_ACCOUNT_RESPONSE = "listaccountresponse";

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

    /** Constant for gateway of the network. */
    public static final String CS_GATEWAY = "gateway";

    /** Constant for account type. */
    public static final String CS_ACCOUNT_TYPE = "accounttype";

    /** Constant for netMask of the network. */
    public static final String CS_NETMASK = "netmask";

    /** Constant for nic id. */
    public static final String CS_NIC_ID = "nicid";

     /** Constant for create account response. */
    public static final String CS_ACCOUNT_RESPONSE = "createaccountresponse";

    /** Constants for jobs status. */
    public static final String ERROR_JOB_STATUS = "2", PROGRESS_JOB_STATUS = "0",  SUCCEEDED_JOB_STATUS = "1";

    /** Constant for CIDR. */
    public static final String CS_CIDR = "cidr";

    /** Constant for domain. */
    public static final String CS_DOMAIN = "domain";

    /** Constant for network domain. */
    public static final String CS_NETWORK_DOMAIN = "networkdomain";

    /** Constant for user name. */
    public static final String CS_USER_NAME = "username";

    /** Constant for user . */
    public static final String CS_USER = "user";

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

    /** Constant for keyboard type. */
    public static final String CS_DISPLAY_VM = "displayvm";

    /** Constant for keyboard type. */
    public static final String CS_ACTIVE_VM = "true";

    /** Constant for keyboard type. */
    public static final String CS_KEYBOARD_TYPE = "keyboard";

    /** Constant for hypervisor type. */
    public static final String CS_HYPERVISOR_TYPE = "hypervisor";

    /** Constant for custom offering cpu number. */
    public static final String CS_CUSTOM_CORE = ".cpuNumber";

    /** Constant for custom offering cpu memory. */
    public static final String CS_CUSTOM_MEMORY = ".memory";

    /** Constant for custom offering minimum iops. */
    public static final String CS_MIN_IOPS_REQUEST = ".minIops";

    /** Constant for custom offering maximum iops. */
    public static final String CS_MAX_IOPS_REQUEST = ".maxIops";

    /** Constant for custom offering cpu speed. */
    public static final String CS_CUSTOM_CPU = ".cpuSpeed";

    /** Constant for response for cpu vm deploy. */
    public static final String CS_VM_DEPLOY = "deployvirtualmachineresponse";

    /** Constant for custom details. */
    public static final String CS_CUSTOM_DETAILS = "details[0]";

    /** Constant for resource count. */
    public static final String CS_RESOURCE_COUNT = "resourcecount";

    /** Constant for response vm array. */
    public static final String CS_VM = "virtualmachine";

    /** Constant used to contact cloud admin. */
    public static final String CONTACT_CLOUD_ADMIN = "error.contact.cloud.admin";

    /** Constant used to resource limit check in puplic pool. */
    public static final String RESOURCE_CHECK = "resource.check";

    /** Constant used for entity validation. */
    public static final String ENTITY_VMINSTANCE = "vmInstance";

    /** Constant used for network offering ids . */
    public static final String CS_NETWORK_IDS = "networkids";

    /** Constant used for keyboard type. */
    public static final String KEYBOARD_VALUE = "us";

    /** Constant used for vm stop response. */
    public static final String CS_VM_STOP_RESPONSE = "stopvirtualmachineresponse";

    /** Constant used for vm reboot response. */
    public static final String CS_VM_REBOOT_RESPONSE = "rebootvirtualmachineresponse";

    /** Constant used for vm restore response. */
    public static final String CS_VM_RESTORE_RESPONSE = "restorevmresponse";

    /** Constant used for vm destroy response. */
    public static final String CS_VM_DESTROY_RESPONSE = "destroyvirtualmachineresponse";

    /** Constant used for vm expunge request. */
    public static final String CS_VM_ENPUNGE = "expunge";

    /** Constant used for vm recover response. */
    public static final String CS_VM_RECOVER_RESPONSE = "recovervirtualmachineresponse";

    /** Constant used for vm start response. */
    public static final String CS_VM_START_RESPONSE = "startvirtualmachineresponse";

    /** Constant used for vm migrate response. */
    public static final String CS_VM_MIGRATE_RESPONSE = "migratevirtualmachineresponse";

    /** Constant used for vm attach iso response. */
    public static final String CS_VM_ATTACHISO_RESPONSE = "attachisoresponse";

    /** Constant used for vm detach iso response. */
    public static final String CS_VM_DETACHISO_RESPONSE = "detachisoresponse";

    /** Constant used for vm reset password response. */
    public static final String CS_VM_RESET_PASSWORD_RESPONSE = "resetpasswordforvirtualmachineresponse";

    /** Constant used for vm display name. */
    public static final String CS_VM_DISPLAYNAME = "displayName";

    /** Constant used for list capacity response. */
    public static final String CS_CAPACITY_LIST_RESPONSE = "listcapacityresponse";

    /** Constant used for resource capacity. */
    public static final String CS_CAPACITY = "capacity";

    /** Constant used for capacity type. */
    public static final String CAPACITY_TYPE =  "type";

    /** Constant used for capacity total. */
    public static final String CS_CAPACITY_TOTAL = "capacitytotal";

    /** Constant used for capacity total. */
    public static final String CS_CAPACITY_USED = "capacityused";

    /** Constant used for list of public ipaddress response. */
    public static final String CS_PUBLIC_IPADDRESS_RESPONSE = "listpublicipaddressesresponse";

    /** Constant used for capacity count. */
    public static final String CS_CAPACITY_COUNT = "count";

    /** Constant used for update resource count response. */
    public static final String CS_UPDATE_RESOURCE_COUNT = "updateresourcecountresponse";

    /** Constant used for capacity total. */
    public static final String CS_RESOURCE_TYPE = "resourcetype";

    /** Constant used for capacity total. */
    public static final String CS_LIST_VM_RESPONSE = "listvirtualmachinesresponse";

    /** Constant used for associate network id. */
    public static final String CS_ASSOCIATE_NETWORK = "associatedNetworkId";

    /** Constant used virtual network. */
    public static final String CS_FOR_VM_NETWORK = "forvirtualnetwork";

    /** Constant used scale vm response. */
    public static final String SCALE_VM_RESPONSE = "scalevirtualmachineresponse";

    /** Constant for command information. */
    public static final String CS_CMD_INFO = "cmdInfo";

    /** Constant for is ready state. */
    public static final String CS_READY_STATE = "isready";

    /** Constant for template action. */
    public static final String CS_VISIBILITY = "ispublic", CS_FEATURED = "isfeatured", CS_EXTRACTABLE = "isextractable",
            CS_DYNAMIC_SCALABLE = "isdynamicallyscalable", CS_BOOTABLE = "bootable", CS_ROUTING = "isrouting",
            CS_REQUIRES_HVM = "requireshvm";

    /** Constant for generic OS type id. */
    public static final String CS_OS_TYPEID = "ostypeid";

    /** Constant for generic hypervisor. */
    public static final String CS_HYPERVISOR = "hypervisor";

    /** Constant for generic format. */
    public static final String CS_FORMAT = "format";

    /** Constant for generic templatetype. */
    public static final String CS_TEMPLATE_TYPE = "templatetype";

    /** Constant for generic system. */
    public static final String CS_SYSTEM = "system";

    /** Constant for template list response. */
    public static final String CS_LIST_TEMPLATE_RESPONSE = "listtemplatesresponse";

    /** Constant for ISO list response. */
    public static final String CS_LIST_ISO_RESPONSE = "listisosresponse";

    /** Constant for template response. */
    public static final String CS_PREPARE_TEMPLATE_RESPONSE = "preparetemplateresponse";

    /** Constant for register ISO response. */
    public static final String CS_REGISTER_ISO_RESPONSE = "registerisoresponse";

    /** Constant for register template response. */
    public static final String CS_REGISTER_TEMPLATE_RESPONSE = "registertemplateresponse";

    /** Constant for delete ISO response. */
    public static final String CS_DELETE_ISO_RESPONSE = "deleteisoresponse";

    /** Constant for delete template response. */
    public static final String CS_DELETE_TEMPLATE_RESPONSE = "deletetemplateresponse";

    /** Constant for template name. */
    public static final String TEMPLATE_NAME = "template";

    /** Constant for template ISO name. */
    public static final String ISO_TEMPLATE_NAME = "iso";

    /** Constant for asynchronous event type. */
    public static final String CS_COMMAND_EVENT_TYPE = "commandEventType";

    /** Constant for volume type. */
    public static final String CS_VOLUME_TYPE = "type";

}
