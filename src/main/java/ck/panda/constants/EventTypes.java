package ck.panda.constants;

/**
 * Event Types from Cloudstack Server.
 */
public class EventTypes {
    /**
     * Makes sure that utility classes (classes that contain only static methods
     * or fields in their API) do not have a public constructor.
     */
    protected EventTypes() {
        throw new UnsupportedOperationException();
    }

    /** VM Events . */
    public static final String EVENT_VM = "VM.";

    /** VM Events . */
    public static final String EVENT_VM_CREATE = "VM.CREATE";

    /** VM Events . */
    public static final String EVENT_VM_DESTROY = "VM.DESTROY";

    /** VM Events . */
    public static final String EVENT_VM_START = "VM.START";
    
    /** VM Events . */
    public static final String EVENT_PROJECT = "PROJECT.";

    /** VM Events . */
    public static final String EVENT_VM_STOP = "VM.STOP";

    /** VM Events . */
    public static final String EVENT_VM_REBOOT = "VM.REBOOT";

    /** VM Events . */
    public static final String EVENT_VM_UPDATE = "VM.UPDATE";

    /** VM Events . */
    public static final String EVENT_VM_UPGRADE = "VM.UPGRADE";

    /** VM ATTACH ISO . */
    public static final String EVENT_ISO_ATTACH = "ISO.ATTACH";

    /** VM DETACH ISO . */
    public static final String EVENT_ISO_DETACH = "ISO.DETACH";

    /** VM Events . */
    public static final String EVENT_VM_DYNAMIC_SCALE = "VM.DYNAMIC.SCALE";

    /** VM Events . */
    public static final String EVENT_VM_RESETPASSWORD = "VM.RESETPASSWORD";

    /** VM Events . */
    public static final String EVENT_VM_RESETSSHKEY = "VM.RESETSSHKEY";

    /** VM Events . */
    public static final String EVENT_VM_MIGRATE = "VM.MIGRATE";

    /** VM Events . */
    public static final String EVENT_VM_MOVE = "VM.MOVE";

    /** VM Events . */
    public static final String EVENT_VM_RESTORE = "VM.RESTORE";

    /** VM Events . */
    public static final String EVENT_VM_EXPUNGE = "VM.EXPUNGE";

    /** VM snapshot Events . */
    public static final String EVENT_VM_SNAPSHOT_CREATE = "VMSNAPSHOT.CREATE";

    /** VM snapshot Events . */
    public static final String EVENT_VM_SNAPSHOT_DELETE = "VMSNAPSHOT.DELETE";

    /** VM snapshot Events . */
    public static final String EVENT_VM_SNAPSHOT_REVERT = "VMSNAPSHOT.REVERTTO";

    /** Domain Router. */
    public static final String EVENT_ROUTER = "ROUTER.";

    /** Console proxy. */
    public static final String EVENT_PROXY = "PROXY.";

    /** VNC Console Events. */
    public static final String EVENT_VNC = "VNC.";

    /** Global Load Balancer rules. */
    public static final String EVENT_GLOBAL_LB = "GLOBAL.LB.";

    /** Account events. */
    public static final String EVENT_ACCOUNT = "ACCOUNT.";

    /** UserVO Events. */
    public static final String EVENT_USER = "USER.";

    /** UserVO Events. */
    public static final String EVENT_USER_LOGIN = "USER.LOGIN";

    /** UserVO Events. */
    public static final String EVENT_USER_LOGOUT = "USER.LOGOUT";

    /** registering SSH keypair events. */
    public static final String EVENT_REGISTER_SSH = "REGISTER.";

    /** registering SSH keypair events. */
    public static final String EVENT_REGISTER_SSH_KEYPAIR = "REGISTER.SSH.KEYPAIR";

    /** register for user API and secret keys. */
    public static final String EVENT_REGISTER_SECRET = "REGISTER.";

    /** register for user API and secret keys. */
    public static final String EVENT_REGISTER_FOR_SECRET_API_KEY = "REGISTER.USER.KEY";

    /** Template Events. */
    public static final String EVENT_TEMPLATE = "TEMPLATE.";

    /** Volume Events. */
    public static final String EVENT_VOLUME = "VOLUME.";

    /** Domains. */
    public static final String EVENT_DOMAIN = "DOMAIN.";

    /** Snapshots. */
    public static final String EVENT_SNAPSHOT = "SNAPSHOT.";

    /** ISO. */
    public static final String EVENT_ISO = "ISO.";

    /** Service Offerings. */
    public static final String EVENT_SERVICE = "SERVICE.";

    /** Disk Offerings. */
    public static final String EVENT_DISK = "DISK.";

    /** Network offerings. */
    public static final String EVENT_NETWORK = "NETWORK.";

    /** Pods. */
    public static final String EVENT_POD = "POD.";

    /** Zones. */
    public static final String EVENT_ZONE = "ZONE.";
    
    /** Nic. */
    public static final String EVENT_NIC = "NIC.";

    /** Physical Network Events. */
    public static final String EVENT_PHYSICAL = "PHYSICAL.";

    /** VPC. */
    public static final String EVENT_VPC = "VPC.";

    /** vm snapshot events. */
    public static final String EVENT_VM_SNAPSHOT = "VMSNAPSHOT.";

    /** Guest OS related events. */
    public static final String EVENT_GUEST = "GUEST.";

    /** Status of CloudStack server action event status. */
    public static final String EVENT_COMPLETED = "Completed";

    /** Status of CloudStack server action event status. */
    public static final String EVENT_STARTED = "Started";

    /** Status of CloudStack server action event status. */
    public static final String EVENT_ERROR = "Error";

    /** Status of VM instance. */
    public static final String EVENT_STATUS_CREATE = "Starting";

    /** Status of VM instance. */
    public static final String EVENT_STATUS_RUNNING = "Running";

    /** Status of VM instance. */
    public static final String EVENT_STATUS_STOPPED = "Stopped";

    /** Status of VM instance. */
    public static final String EVENT_STATUS_DESTROYED = "Destroyed";

    /** Status of VM instance. */
    public static final String EVENT_STATUS_STOPPING = "Stopping";

    /** Status of VM instance. */
    public static final String EVENT_STATUS_EXPUNGING = "Expunging";

    /** State of resources. */
    public static final String RESOURCE_STATE = "new-state";

    /** State of Volume. */
    public static final String ALLOCATED = "ALLOCATED";

    /** State of Volume. */
    public static final String READY = "READY";

    /** State of Volume. */
    public static final String ABANDONED = "ABANDONED";

    /** State of Volume. */
    public static final String UPLOAD_NOT_STARTED = "UPLOADNOTSTARTED";

    /** State of Volume. */
    public static final String UPLOAD_OP = "UPLOADOP";

    /** State of Volume. */
    public static final String UPLOADED = "UPLOADED";

    /** State of snapshot. */
    public static final String EVENT_CREATE = "Creating";

    /** State of snapshot. */
    public static final String EVENT_READY = "Ready";

}
