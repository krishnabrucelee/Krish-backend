package ck.panda.rabbitmq.util;

/**
 * Event Types from Cloudstack Server.
 *
 */
public class EventTypes {
    // VM Events
    public static final String EVENT_VM = "VM.";
    public static final String EVENT_VM_CREATE = "VM.CREATE";
    public static final String EVENT_VM_DESTROY = "VM.DESTROY";
    public static final String EVENT_VM_START = "VM.START";
    public static final String EVENT_VM_STOP = "VM.STOP";
    public static final String EVENT_VM_REBOOT = "VM.REBOOT";
    public static final String EVENT_VM_RESTORE = "VM.RESTORE";
    public static final String EVENT_VM_EXPUNGE = "VM.EXPUNGE";
    // Status of CloudStack server event status.
    public static final String EVENT_COMPLETED = "Completed";
    public static final String EVENT_STARTED = "Started";
    public static final String EVENT_ERROR = "Error";
    // Status of VM instance.
    public static final String EVENT_STATUS_CREATE = "Starting";
    public static final String EVENT_STATUS_RUNNING = "Running";
    public static final String EVENT_STATUS_STOPPED = "Stopped";
    public static final String EVENT_STATUS_DESTROYED = "Destroyed";
    public static final String EVENT_STATUS_STOPPING = "Stopping";

}
