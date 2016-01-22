package ck.panda.domain.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import ck.panda.util.JsonUtil;

/**
 * VM Instances are main entity in our panda project to keep track the status of each instances. Based on VM Instances
 * we can create and destroy, update instance, list Active VM's from cloud stack, etc.,
 */
@Entity
@Table(name = "vm_instances")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class VmInstance implements Serializable {
    /** Unique Id of the instance. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Name of the instance. */
    @NotEmpty
    @Column(name = "name", nullable = false)
    private String name;

    /** Display name of the instance. */
    @Column(name = "display_name")
    private String displayName;

    /** Internal name of the instance. */
    @Column(name = "instance_internal_name")
    private String instanceInternalName;

    /** cloudstack's instance uuid. */
    @Column(name = "uuid")
    private String uuid;

    /** cloudstack's instance iso uuid. */
    @Transient
    private String iso;

    /** cloudstack's instance host uuid. */
    @Transient
    private String hostUuid;

    /** cloudstack's instance iso . */
    @Column(name = "instance_iso_name")
    private String isoName;

    /** Instance iso id. */
    @Column(name = "instance_iso_id")
    private Long isoId;

    /** instance vnc password. */
    @Column(name = "vnc_password")
    private String vncPassword;

    /** Instance owner id. */
    @JoinColumn(name = "instance_owner_id", referencedColumnName = "id", updatable = false, insertable = false)
    @OneToOne
    private User instanceOwner;

    /** Instance owner id. */
    @Column(name = "instance_owner_id")
    private Long instanceOwnerId;

    /** Instance application id. */
    @Column(name = "application_name")
    private String application;

    /** Set syncFlag. */
    @Transient
    private Boolean syncFlag;

    /** Set syncFlag. */
    @Transient
    private String event;

    /** Set password. */
    @Transient
    private String password;

    /** List of Application Class for an instance. */
    @ManyToMany
    private List<Application> applicationList;

    /** Instance project id. */
    @JoinColumn(name = "project_id", referencedColumnName = "id", updatable = false, insertable = false)
    @ManyToOne
    private Project project;

    /** Instance project id. */
    @Column(name = "project_id")
    private Long projectId;

    /** Instance department id. */
    @JoinColumn(name = "department_id", referencedColumnName = "id", updatable = false, insertable = false)
    @ManyToOne
    private Department department;

    /** Instance department id. */
    @Column(name = "department_id")
    private Long departmentId;

    /** Instance Root-Volume id. */
    @Column(name = "volume_size")
    private Long volumeSize;

    /** Instance template id. */
    @JoinColumn(name = "template_id", referencedColumnName = "id", updatable = false, insertable = false)
    @OneToOne
    private Template template;

    /** Instance template id. */
    @Column(name = "template_id")
    private Long templateId;

    /** Instance domain id. */
    @JoinColumn(name = "domain_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Domain domain;

    /** Instance domain id. */
    @Column(name = "domain_id")
    private Long domainId;

    /** Instance zone. */
    @JoinColumn(name = "zone_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Zone zone;

    /** Instance zone id. */
    @Column(name = "zone_id")
    private Long zoneId;

    /** Instance compute offer. */
    @JoinColumn(name = "compute_offer_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @OneToOne
    private ComputeOffering computeOffering;

    /** Instance compute offer id. */
    @Column(name = "compute_offer_id")
    private Long computeOfferingId;

    /** Instance disk offer. */
    @JoinColumn(name = "storage_offer_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @OneToOne
    private StorageOffering storageOffering;

    /** Instance disk offer id. */
    @Column(name = "storage_offer_id")
    private Long storageOfferingId;

    /** Instance network offer. */
    @JoinColumn(name = "network_offer_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @OneToOne
    private NetworkOffering networkOffering;

    /** Instance network offer id. */
    @Column(name = "network_offer_id")
    private Long networkOfferingId;

    /** Instance network. */
    @JoinColumn(name = "network_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @OneToOne
    private Network network;

    /** Instance network id. */
    @Column(name = "network_id")
    private Long networkId;

    /** Instance network uuid. */
    @Transient
    private String networkUuid;

    /** Instance current state. */
    @Column(name = "status")
    private Status status;

    /** Enumeration status for instance. */
    public enum Status {
        /** Running status of instance. */
        Running,
        /** destroy status of instance. */
        Destroy,
        /** destroyed status of instance. */
        Destroyed,
        /** Stopped status of instance. */
        Stopped,
        /** Status of instance. */
        Migrating,
        /** after launch or start instance get status as starting. */
        Starting,
        /** after stop or destroy instance get status as stopping. */
        Stopping,
        /** after destroy or expunge instance get status as expunging. */
        Expunging,
        /** after destroy or expunge instance get status as expunged. */
        Expunged,
        /** while instance creation if get failure get status as Error. */
        Error,
        /** while instance creation if get status as creating. */
        Creating,
        /** while instance creation if get status as Implemented. */
        Implemented,
        /** after launch instance if get status as ready. */
        Ready
    }

    /** Instance host. */
    @JoinColumn(name = "host_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @OneToOne
    private Host host;

    /** Instance host id. */
    @Column(name = "host_id")
    private Long hostId;

    /** Instance pod id. */
    @Column(name = "pod_id")
    private Long podId;

    /** Hypervisor object. */
    @ManyToOne
    @JoinColumn(name = "hypervisor_type_id", referencedColumnName = "id", updatable = false, insertable = false)
    private Hypervisor hypervisor;

    /** Hypervisor type id. */
    @Column(name = "hypervisor_type_id")
    private Long hypervisorId;

    /** Instance event message. */
    @Column(name = "event_message")
    private String eventMessage;

    /** Event type. */
    @Column(name = "instance_event_type")
    private String eventType;

    /** Instance note. */
    @Column(name = "instance_note")
    private String instanceNote;

    /** Check instance available or not. */
    @Column(name = "is_removed")
    private Boolean isRemoved;

    /** Check instance available or not. */
    @Column(name = "is_password_enabled")
    private Boolean passwordEnabled;

    /** instance private ip address. */
    @Column(name = "instance_private_ip")
    private String ipAddress;

    /** The number of CPU cores needed. */
    @Column(name = "cpu_cores")
    private Integer cpuCore;

    /** The clock rate of CPU speed in MHz. */
    @Column(name = "cpu_speed")
    private Integer cpuSpeed;

    /** The CPU memory in Mebi Bytes Per Second. */
    @Column(name = "memory")
    private Integer memory;

    /** instance cpu usage. */
    @Column(name = "instance_usage")
    private String cpuUsage;

    /** The network read in kbs. */
    @Column(name = "network_kbs_read")
    private Integer networkKbsRead;

    /** The network write in kbs. */
    @Column(name = "network_kbs_write")
    private Integer networkKbsWrite;

    /** The disk read in bytes. */
    @Column(name = "disk_kbs_read")
    private Integer diskKbsRead;

    /** The disk write in bytes. */
    @Column(name = "disk_kbs_write")
    private Integer diskKbsWrite;

    /** The disk read input/output. */
    @Column(name = "disk_io_read")
    private Integer diskIoRead;

    /** The disk write input/output. */
    @Column(name = "disk_io_write")
    private Integer diskIoWrite;

    /** Compute offering Minimum input output per second. */
    @Column(name = "compute_min_iops")
    private Integer computeMinIops;

    /** Compute offering maximum input output per second. */
    @Column(name = "compute_max_iops")
    private Integer computeMaxIops;

    /** Version attribute to handle optimistic locking. */
    @Version
    @Column(name = "version")
    private Long version;

    /** Created by user. */
    @CreatedBy
    @Column(name = "created_user_id")
    private Long createdBy;

    /** Last updated by user. */
    @LastModifiedBy
    @Column(name = "updated_user_id")
    private Long updatedBy;

    /** Created date and time. */
    @CreatedDate
    @Column(name = "created_date_time")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentZonedDateTime")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime createdDateTime;

    /** Last modified date and time. */
    @LastModifiedDate
    @Column(name = "updated_date_time")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentZonedDateTime")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime updatedDateTime;

    /** Transient domain of the instance. */
    @Transient
    private String transDomainId;

    /** Transient zone of the instance. */
    @Transient
    private String transZoneId;

    /** Transient host of the instance. */
    @Transient
    private String transHostId;

    /** Transient host of the instance. */
    @Transient
    private String transComputeOfferingId;

    /** Transient host of the instance. */
    @Transient
    private String transProjectId;

    /** Transient network of the instance. */
    @Transient
    private String transNetworkId;

    /** Transient template of the instance. */
    @Transient
    private String transTemplateId;

    /** Transient iso of the instance. */
    @Transient
    private String transIsoId;

    /** Transient name of the instance. */
    @Transient
    private String transDisplayName;

    /** Transient department id of the instance. */
    @Transient
    private String transDepartmentId;

    /** Transient volume id of the instance. */
    @Transient
    private String transVolumeId;

    /** Transient hypervisor of the template. */
    @Transient
    private String transHypervisor;

    /** Transient owner id of the instance. */
    @Transient
    private String transOwnerId;

    /**
     * Get sync status.
     *
     * @return the syncFlag
     */
    public Boolean getSyncFlag() {
        return syncFlag;
    }

    /**
     * Set sync status.
     *
     * @param syncFlag the syncFlag to set
     */
    public void setSyncFlag(Boolean syncFlag) {
        this.syncFlag = syncFlag;
    }

    /**
     * Get updated date and time.
     *
     * @return the updatedDateTime
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set updated date and time.
     *
     * @param updatedDateTime the updatedDateTime to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get the the status of instance.
     *
     * @return the status.
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set the instance status.
     *
     * @param status to set.
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Check whether instance removed or not.
     *
     * @return the isRemoved.
     */
    public Boolean getIsRemoved() {
        return isRemoved;
    }

    /**
     * Delete the instance.
     *
     * @param isRemoved to set.
     */
    public void setIsRemoved(Boolean isRemoved) {
        this.isRemoved = isRemoved;
    }

    /**
     * get the instance id.
     *
     * @return the id.
     */
    public Long getId() {
        return id;
    }

    /**
     * set the instance id.
     *
     * @param id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * get the instance.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * set the instance name.
     *
     * @param name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * get instance UUID.
     *
     * @return the uuid.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * set UUID from cloud stack.
     *
     * @param uuid to set.
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * get instance owner.
     *
     * @return the instanceOwner.
     */
    public User getInstanceOwner() {
        return instanceOwner;
    }

    /**
     * set instance owner.
     *
     * @param instanceOwner to set.
     */
    public void setInstanceOwner(User instanceOwner) {
        this.instanceOwner = instanceOwner;
    }

    /**
     * @return the application.
     */
    public String getApplication() {
        return application;
    }

    /**
     * @param application the application name to set.
     */
    public void setApplication(String application) {
        this.application = application;
    }

    /**
     * get instance project.
     *
     * @return the project.
     */
    public Project getProject() {
        return project;
    }

    /**
     * set instance project.
     *
     * @param project to set.
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * get instance department.
     *
     * @return the department
     */
    public Department getDepartment() {
        return department;
    }

    /**
     * set instance department.
     *
     * @param department to set.
     */
    public void setDepartment(Department department) {
        this.department = department;
    }

    /**
     * get instance template.
     *
     * @return the template.
     */
    public Template getTemplate() {
        return template;
    }

    /**
     * set instance template.
     *
     * @param template to set.
     */
    public void setTemplate(Template template) {
        this.template = template;
    }

    /**
     * @return the computeOfferingId.
     */
    public Long getComputeOfferingId() {
        return computeOfferingId;
    }

    /**
     * @param computeOfferingId the compute offering id to set.
     */
    public void setComputeOfferingId(Long computeOfferingId) {
        this.computeOfferingId = computeOfferingId;
    }

    /**
     * @return the storageOfferingId.
     */
    public Long getStorageOfferingId() {
        return storageOfferingId;
    }

    /**
     * @param storageOfferingId the storage offering id to set.
     */
    public void setStorageOfferingId(Long storageOfferingId) {
        this.storageOfferingId = storageOfferingId;
    }

    /**
     * @return the networkOfferingId.
     */
    public Long getNetworkOfferingId() {
        return networkOfferingId;
    }

    /**
     * @param networkOfferingId the network offering id to set.
     */
    public void setNetworkOfferingId(Long networkOfferingId) {
        this.networkOfferingId = networkOfferingId;
    }

    /**
     * Get the version count.
     *
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Set the version count.
     *
     * @param version to set.
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get created user id.
     *
     * @return the createdBy.
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set created user id.
     *
     * @param createdBy to set.
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the last modified user id.
     *
     * @return the updated user.
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the last modified user id.
     *
     * @param updatedBy to set.
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the created date and time.
     *
     * @return the createdDateTime.
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the created date and time.
     *
     * @param createdDateTime to set.
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get last modified date and time.
     *
     * @return the updatedDateTime.
     */
    public ZonedDateTime getLastModifiedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set last modified date and time.
     *
     * @param updatedDateTime last modified to set
     */
    public void setLastModifiedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * get domain for instance.
     *
     * @return the domain
     */
    public Domain getDomain() {
        return domain;
    }

    /**
     * set domain for instance.
     *
     * @param domain domain to set.
     */
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    /**
     * Get the instance IPAddress.
     *
     * @return the ipAddress
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Set the instance IPAddress.
     *
     * @param ipAddress ip address to set.
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Get the VNC console password.
     *
     * @return the vncPassword.
     */
    public String getVncPassword() {
        return vncPassword;
    }

    /**
     * Set the VNC console password.
     *
     * @param vncPassword vnc password to set.
     */
    public void setVncPassword(String vncPassword) {
        this.vncPassword = vncPassword;
    }

    /**
     * Get the host.
     *
     * @return the host
     */
    public Host getHost() {
        return host;
    }

    /**
     * Get the networkKbsRead of the VmInstance.
     *
     * @return the networkKbsRead of VmInstance.
     */
    public Integer getNetworkKbsRead() {
        return networkKbsRead;
    }

    /**
     * Set the networkKbsRead of the VmInstance.
     *
     * @param networkKbsRead the networkKbsRead to set
     */
    public void setNetworkKbsRead(Integer networkKbsRead) {
        this.networkKbsRead = networkKbsRead;
    }

    /**
     * Get the networkKbsWrite of the VmInstance.
     *
     * @return the networkKbsWrite of VmInstance.
     */
    public Integer getNetworkKbsWrite() {
        return networkKbsWrite;
    }

    /**
     * Set the networkKbsWrite of the VmInstance.
     *
     * @param networkKbsWrite the networkKbsWrite to set
     */
    public void setNetworkKbsWrite(Integer networkKbsWrite) {
        this.networkKbsWrite = networkKbsWrite;
    }

    /**
     * Get the diskKbsRead of the VmInstance.
     *
     * @return the diskKbsRead of VmInstance.
     */
    public Integer getDiskKbsRead() {
        return diskKbsRead;
    }

    /**
     * Set the diskKbsRead of the VmInstance.
     *
     * @param diskKbsRead the diskKbsRead to set
     */
    public void setDiskKbsRead(Integer diskKbsRead) {
        this.diskKbsRead = diskKbsRead;
    }

    /**
     * Get the diskKbsWrite of the VmInstance.
     *
     * @return the diskKbsWrite of VmInstance.
     */
    public Integer getDiskKbsWrite() {
        return diskKbsWrite;
    }

    /**
     * Set the diskKbsWrite of the VmInstance.
     *
     * @param diskKbsWrite the diskKbsWrite to set
     */
    public void setDiskKbsWrite(Integer diskKbsWrite) {
        this.diskKbsWrite = diskKbsWrite;
    }

    /**
     * Get the diskIoRead of the VmInstance.
     *
     * @return the diskIoRead of VmInstance.
     */
    public Integer getDiskIoRead() {
        return diskIoRead;
    }

    /**
     * Set the diskIoRead of the VmInstance.
     *
     * @param diskIoRead the diskIoRead to set
     */
    public void setDiskIoRead(Integer diskIoRead) {
        this.diskIoRead = diskIoRead;
    }

    /**
     * Get the diskIoWrite of the VmInstance.
     *
     * @return the diskIoWrite of VmInstance.
     */
    public Integer getDiskIoWrite() {
        return diskIoWrite;
    }

    /**
     * Set the diskIoWrite of the VmInstance.
     *
     * @param diskIoWrite the diskIoWrite to set
     */
    public void setDiskIoWrite(Integer diskIoWrite) {
        this.diskIoWrite = diskIoWrite;
    }

    /**
     * Set the host.
     *
     * @param host the host to set
     */
    public void setHost(Host host) {
        this.host = host;
    }

    /**
     * Get the host id of instance.
     *
     * @return the hostId.
     */
    public Long getHostId() {
        return hostId;
    }

    /**
     * Set the host id of instance.
     *
     * @param hostID to set.
     */
    public void setHostId(Long hostID) {
        this.hostId = hostID;
    }

    /**
     * Get the pod id.
     *
     * @return the podId.
     */
    public Long getPodId() {
        return podId;
    }

    /**
     * Set the pod id.
     *
     * @param podID to set.
     */
    public void setPodId(Long podID) {
        this.podId = podID;
    }

    /**
     * Get the event type.
     *
     * @return the event type.
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * Set the eventType.
     *
     * @param eventType the event type to set.
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    /**
     * Get the zone.
     *
     * @return the zone.
     */
    public Zone getZone() {
        return zone;
    }

    /**
     * Set the zone.
     *
     * @param zone the zone to set.
     */
    public void setZone(Zone zone) {
        this.zone = zone;
    }

    /**
     * Get the instance owner id.
     *
     * @return the instance owner id.
     */
    public Long getInstanceOwnerId() {
        return instanceOwnerId;
    }

    /**
     * Set the instance owner id.
     *
     * @param instanceOwnerId the instance owner id to set.
     */
    public void setInstanceOwnerId(Long instanceOwnerId) {
        this.instanceOwnerId = instanceOwnerId;
    }

    /**
     * Get the project's id.
     *
     * @return the projectId.
     */
    public Long getProjectId() {
        return projectId;
    }

    /**
     * Set the project's id.
     *
     * @param projectId the project id to set.
     */
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    /**
     * Get the department's id.
     *
     * @return the departmentId.
     */
    public Long getDepartmentId() {
        return departmentId;
    }

    /**
     * Set the department's id.
     *
     * @param departmentId the department id to set.
     */
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * Get the template's id.
     *
     * @return the templateId.
     */
    public Long getTemplateId() {
        return templateId;
    }

    /**
     * Set the template's id.
     *
     * @param templateId the template id to set.
     */
    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    /**
     * Get the domain's id.
     *
     * @return the domainId.
     */
    public Long getDomainId() {
        return domainId;
    }

    /**
     * Set the domain's id.
     *
     * @param domainId the domain id to set.
     */
    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    /**
     * Get the zone's id.
     *
     * @return the zoneId.
     */
    public Long getZoneId() {
        return zoneId;
    }

    /**
     * Set the zone's id.
     *
     * @param zoneId the zone id to set.
     */
    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * Get the compute offering.
     *
     * @return the computeOffering.
     */
    public ComputeOffering getComputeOffering() {
        return computeOffering;
    }

    /**
     * Set the compute offering.
     *
     * @param computeOffering compute Offering to set.
     */
    public void setComputeOffering(ComputeOffering computeOffering) {
        this.computeOffering = computeOffering;
    }

    /**
     * Get the application list.
     *
     * @return the application list.
     */
    public List<Application> getApplicationList() {
        return applicationList;
    }

    /**
     * Set the application list.
     *
     * @param applicationList the application list to set.
     */
    public void setApplicationList(List<Application> applicationList) {
        this.applicationList = applicationList;
    }

    /**
     * Get the storage Offering.
     *
     * @return the storageOffering.
     */
    public StorageOffering getStorageOffering() {
        return storageOffering;
    }

    /**
     * Set the storage Offering.
     *
     * @param storageOffering the storage Offering to set.
     */
    public void setStorageOffering(StorageOffering storageOffering) {
        this.storageOffering = storageOffering;
    }

    /**
     * Get the network Offering.
     *
     * @return the networkOffering.
     */
    public NetworkOffering getNetworkOffering() {
        return networkOffering;
    }

    /**
     * Set the network Offering.
     *
     * @param networkOffering the network Offering to set.
     */
    public void setNetworkOffering(NetworkOffering networkOffering) {
        this.networkOffering = networkOffering;
    }

    /**
     * Get the network.
     *
     * @return the network.
     */
    public Network getNetwork() {
        return network;
    }

    /**
     * Set the network.
     *
     * @param network the network to set.
     */
    public void setNetwork(Network network) {
        this.network = network;
    }

    /**
     * Get the network id.
     *
     * @return the networkId.
     */
    public Long getNetworkId() {
        return networkId;
    }

    /**
     * Set the network id..
     *
     * @param networkId network id to set.
     */
    public void setNetworkId(Long networkId) {
        this.networkId = networkId;
    }

    /**
     * Get the networkUuid.
     *
     * @return the networkUuid.
     */
    public String getNetworkUuid() {
        return networkUuid;
    }

    /**
     * Set the networkUuid.
     *
     * @param networkUuid network uuid to set.
     */
    public void setNetworkUuid(String networkUuid) {
        this.networkUuid = networkUuid;
    }

    /**
     * @return the eventMessage.
     */
    public String getEventMessage() {
        return eventMessage;
    }

    /**
     * @param eventMessage event message to set.
     */
    public void setEventMessage(String eventMessage) {
        this.eventMessage = eventMessage;
    }

    /**
     * Get CPU number.
     *
     * @return the cpuCore
     */
    public Integer getCpuCore() {
        return cpuCore;
    }

    /**
     * Set CPU number.
     *
     * @param cpuCore the cpuCore to set
     */
    public void setCpuCore(Integer cpuCore) {
        this.cpuCore = cpuCore;
    }

    /**
     * Get CPU speed.
     *
     * @return the cpuSpeed
     */
    public Integer getCpuSpeed() {
        return cpuSpeed;
    }

    /**
     * Set CPU speed.
     *
     * @param cpuSpeed the cpuSpeed to set
     */
    public void setCpuSpeed(Integer cpuSpeed) {
        this.cpuSpeed = cpuSpeed;
    }

    /**
     * Get memory size.
     *
     * @return the memory
     */
    public Integer getMemory() {
        return memory;
    }

    /**
     * Set memory size.
     *
     * @param memory the memory to set
     */
    public void setMemory(Integer memory) {
        this.memory = memory;
    }

    /**
     * Get cpu usage.
     *
     * @return the cpuUsage
     */
    public String getCpuUsage() {
        return cpuUsage;
    }

    /**
     * Set cpu usage.
     *
     * @param cpuUsage the cpuUsage to set
     */
    public void setCpuUsage(String cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    /**
     * Get the Note.
     *
     * @return the instanceNote
     */
    public String getInstanceNote() {
        return instanceNote;
    }

    /**
     * Set the instance note.
     *
     * @param instanceNote the eventNote to set
     */
    public void setInstanceNote(String instanceNote) {
        this.instanceNote = instanceNote;
    }

    /**
     * Get temp password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set temp password.
     *
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get status of vnc password for an instance is enabled/disabled.
     *
     * @return the passwordEnabled
     */
    public Boolean getPasswordEnabled() {
        return passwordEnabled;
    }

    /**
     * Set status of vnc password for an instance is enabled/disabled.
     *
     * @param passwordEnabled the passwordEnabled to set
     */
    public void setPasswordEnabled(Boolean passwordEnabled) {
        this.passwordEnabled = passwordEnabled;
    }

    /**
     * Get the host uuid.
     *
     * @return the hostUuid
     */
    public String getHostUuid() {
        return hostUuid;
    }

    /**
     * Set the host uuid.
     *
     * @param hostUuid the hostUuid to set
     */
    public void setHostUuid(String hostUuid) {
        this.hostUuid = hostUuid;
    }

    /**
     * Get the iso id.
     *
     * @return the isoId
     */
    public Long getIsoId() {
        return isoId;
    }

    /**
     * Set the iso id.
     *
     * @param isoId the isoId to set
     */
    public void setIsoId(Long isoId) {
        this.isoId = isoId;
    }

    /**
     * Get the iso.
     *
     * @return the iso
     */
    public String getIso() {
        return iso;
    }

    /**
     * Set the iso.
     *
     * @param iso the iso to set
     */
    public void setIso(String iso) {
        this.iso = iso;
    }

    /**
     * Get the iso name.
     *
     * @return the isoName
     */
    public String getIsoName() {
        return isoName;
    }

    /**
     * Set the iso name.
     *
     * @param isoName the isoName to set
     */
    public void setIsoName(String isoName) {
        this.isoName = isoName;
    }

    /**
     * Get the event name.
     *
     * @return the event
     */
    public String getEvent() {
        return event;
    }

    /**
     * Set the event name.
     *
     * @param event the event to set
     */
    public void setEvent(String event) {
        this.event = event;
    }

    /**
     * Get the transient domain id.
     *
     * @return the transDomainId
     */
    public String getTransDomainId() {
        return transDomainId;
    }

    /**
     * Set the transient domain id..
     *
     * @param transDomainId to set
     */
    public void setTransDomainId(String transDomainId) {
        this.transDomainId = transDomainId;
    }

    /**
     * Get transient zone id.
     *
     * @return the transZoneId
     */
    public String getTransZoneId() {
        return transZoneId;
    }

    /**
     * Set the transZoneId.
     *
     * @param transZoneId to set
     */
    public void setTransZoneId(String transZoneId) {
        this.transZoneId = transZoneId;
    }

    /**
     * Get the transHostId.
     *
     * @return the transHostId
     */
    public String getTransHostId() {
        return transHostId;
    }

    /**
     * Set the transHostId.
     *
     * @param transHostId to set
     */
    public void setTransHostId(String transHostId) {
        this.transHostId = transHostId;
    }

    /**
     * Get the transComputeOfferingId.
     *
     * @return the transComputeOfferingId
     */
    public String getTransComputeOfferingId() {
        return transComputeOfferingId;
    }

    /**
     * Set the transComputeOfferingId .
     *
     * @param transComputeOfferingId to set
     */
    public void setTransComputeOfferingId(String transComputeOfferingId) {
        this.transComputeOfferingId = transComputeOfferingId;
    }

    /**
     * Get the transProjectId.
     *
     * @return the transProjectId
     */
    public String getTransProjectId() {
        return transProjectId;
    }

    /**
     * Set the transProjectId.
     *
     * @param transProjectId to set
     */
    public void setTransProjectId(String transProjectId) {
        this.transProjectId = transProjectId;
    }

    /**
     * Get the transNetworkId.
     *
     * @return the transNetworkId
     */
    public String getTransNetworkId() {
        return transNetworkId;
    }

    /**
     * Set the transNetworkId.
     *
     * @param transNetworkId to set
     */
    public void setTransNetworkId(String transNetworkId) {
        this.transNetworkId = transNetworkId;
    }

    /**
     * Get the transIsoId.
     *
     * @return the transIsoId
     */
    public String getTransIsoId() {
        return transIsoId;
    }

    /**
     * Set the transIsoId.
     *
     * @param transIsoId the transIsoId to set
     */
    public void setTransIsoId(String transIsoId) {
        this.transIsoId = transIsoId;
    }

    /**
     * Get the transTemplateId.
     *
     * @return the transTemplateId
     */
    public String getTransTemplateId() {
        return transTemplateId;
    }

    /**
     * Get the transTemplateId.
     *
     * @param transTemplateId to set
     */
    public void setTransTemplateId(String transTemplateId) {
        this.transTemplateId = transTemplateId;
    }

    /**
     * Get the transDepartmentId.
     *
     * @return the transDepartmentId
     */
    public String getTransDepartmentId() {
        return transDepartmentId;
    }

    /**
     * Get the transDepartmentId.
     *
     * @param transDepartmentId to set
     */
    public void setTransDepartmentId(String transDepartmentId) {
        this.transDepartmentId = transDepartmentId;
    }

    /**
     * Get the transDisplayName.
     *
     * @return the transDisplayName
     */
    public String getTransDisplayName() {
        return transDisplayName;
    }

    /**
     * Set the transDisplayName .
     *
     * @param transDisplayName to set
     */
    public void setTransDisplayName(String transDisplayName) {
        this.transDisplayName = transDisplayName;
    }

    /**
     * Get the instance's internal name.
     *
     * @return the instanceInternalName
     */
    public String getInstanceInternalName() {
        return instanceInternalName;
    }

    /**
     * Set the instance's internal name .
     *
     * @param instanceInternalName to set
     */
    public void setInstanceInternalName(String instanceInternalName) {
        this.instanceInternalName = instanceInternalName;
    }

    /**
     * Get the instance's display name.
     *
     * @return the displayName.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Set the instance's display name .
     *
     * @param displayName to set.
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get the instance's volume size.
     *
     * @return the volumeId.
     */
    public Long getVolumeSize() {
        return volumeSize;
    }

    /**
     * Set the instance's volume size .
     *
     * @param volumeSize to set.
     */
    public void setVolumeSize(Long volumeSize) {
        this.volumeSize = volumeSize;
    }

    /**
     * Get the instance's transient volumeId.
     *
     * @return the volumeId.
     */
    public String getTransVolumeId() {
        return transVolumeId;
    }

    /**
     * Set the instance's transient volume id .
     *
     * @param transVolumeId to set.
     */
    public void setTransVolumeId(String transVolumeId) {
        this.transVolumeId = transVolumeId;
    }

    /**
     * Get the hypervisor of the VmInstance.

     * @return the hypervisor of VmInstance.
     */
    public Hypervisor getHypervisor() {
        return hypervisor;
    }

    /**
     * Set the hypervisor of the VmInstance.
     *
     * @param hypervisor the hypervisor to set
     */
    public void setHypervisor(Hypervisor hypervisor) {
        this.hypervisor = hypervisor;
    }

    /**
     * Get the hypervisorId of the VmInstance.

     * @return the hypervisorId of VmInstance.
     */
    public Long getHypervisorId() {
        return hypervisorId;
    }

    /**
     * Set the hypervisorId of the VmInstance.
     *
     * @param hypervisorId the hypervisorId to set
     */
    public void setHypervisorId(Long hypervisorId) {
        this.hypervisorId = hypervisorId;
    }

    /**
     * Get the transHypervisor of the VmInstance.

     * @return the transHypervisor of VmInstance.
     */
    public String getTransHypervisor() {
        return transHypervisor;
    }

    /**
     * Set the transHypervisor of the VmInstance.
     *
     * @param transHypervisor the transHypervisor to set
     */
    public void setTransHypervisor(String transHypervisor) {
        this.transHypervisor = transHypervisor;
    }

    /**
     * Get the transOwnerId of the VmInstance.

     * @return the transOwnerId of VmInstance.
     */
    public String getTransOwnerId() {
        return transOwnerId;
    }

    /**
     * Set the transOwnerId of the VmInstance.
     *
     * @param transOwnerId the transOwnerId to set
     */
    public void setTransOwnerId(String transOwnerId) {
        this.transOwnerId = transOwnerId;
    }

    /**
     * Get the  Compute offering Min Iops..
     *
     * @return the computeMinIops
     */
    public Integer getComputeMinIops() {
        return computeMinIops;
    }

    /**
     * Set the Compute offering Min Iops.
     *
     * @param computeMinIops  to set
     */
    public void setComputeMinIops(Integer computeMinIops) {
        this.computeMinIops = computeMinIops;
    }

    /**
     * Get the Compute offering Max Iops.
     *
     * @return the computeMaxIops
     */
    public Integer getComputeMaxIops() {
        return computeMaxIops;
    }

    /**
     * Set the Compute offering Max Iops..
     *
     * @param computeMaxIops  to set
     */
    public void setComputeMaxIops(Integer computeMaxIops) {
        this.computeMaxIops = computeMaxIops;
    }

    @Override
    public String toString() {
        return "VmInstance [Id=" + id + ", name=" + name + ", uuid=" + uuid + ", vncPassword=" + vncPassword
                + ", instanceOwner=" + instanceOwner + ", instanceOwnerId=" + instanceOwnerId + ", application="
                + application + ", applicationList=" + applicationList + ", project=" + project + ", projectId="
                + projectId + ", department=" + department + ", departmentId=" + departmentId + ", template=" + template
                + ", templateId=" + templateId + ", domain=" + domain + ", domainId=" + domainId + ", zone=" + zone
                + ", zoneId=" + zoneId + ", computeOffering=" + computeOffering + ", computeOfferingId="
                + computeOfferingId + ", storageOffering=" + storageOffering + "" + ", networkOffering="
                + networkOffering + ", networkOfferingId=" + networkOfferingId + ", network=" + network + ", networkId="
                + networkId + ", status=" + status + ", hostId=" + hostId + ", podId=" + podId + ", type=" + eventType
                + ", isRemoved=" + isRemoved + ", ipAddress=" + ipAddress + ", version=" + version + ", createdBy="
                + createdBy + ", updatedBy=" + updatedBy + ", createdDateTime=" + createdDateTime
                + ", lastModifiedDateTime=" + updatedDateTime + "]";
    }

    /**
     * Convert JSONObject into vm object.
     *
     * @param jsonObject JSON object.
     * @return vm object.
     */
    public static VmInstance convert(JSONObject jsonObject) {
        VmInstance vmInstance = new VmInstance();
        vmInstance.setSyncFlag(false);
        try {
            vmInstance.setName(JsonUtil.getStringValue(jsonObject, "name"));
            vmInstance.setUuid(JsonUtil.getStringValue(jsonObject, "id"));
            vmInstance.setTransDomainId(JsonUtil.getStringValue(jsonObject, "domainid"));
            vmInstance.setStatus(Status.valueOf(JsonUtil.getStringValue(jsonObject, "state")));
            vmInstance.setTransZoneId(JsonUtil.getStringValue(jsonObject, "zoneid"));
            vmInstance.setTransHostId(JsonUtil.getStringValue(jsonObject, "hostid"));
            vmInstance.setTransDisplayName(JsonUtil.getStringValue(jsonObject, "displayname"));
            vmInstance.setTransHostId(JsonUtil.getStringValue(jsonObject, "hostid"));
            vmInstance.setTransTemplateId(JsonUtil.getStringValue(jsonObject, "templateid"));
            vmInstance.setTransComputeOfferingId(JsonUtil.getStringValue(jsonObject, "serviceofferingid"));
            vmInstance.setCpuCore(JsonUtil.getIntegerValue(jsonObject, "cpunumber"));
            vmInstance.setCpuSpeed(JsonUtil.getIntegerValue(jsonObject, "cpuspeed"));
            vmInstance.setMemory(JsonUtil.getIntegerValue(jsonObject, "memory"));
            vmInstance.setCpuUsage(JsonUtil.getStringValue(jsonObject, "cpuused"));
            vmInstance.setDiskIoRead(JsonUtil.getIntegerValue(jsonObject, "diskioread"));
            vmInstance.setDiskIoWrite(JsonUtil.getIntegerValue(jsonObject, "diskiowrite"));
            vmInstance.setDiskKbsRead(JsonUtil.getIntegerValue(jsonObject, "diskkbsread"));
            vmInstance.setDiskKbsWrite(JsonUtil.getIntegerValue(jsonObject, "diskkbswrite"));
            vmInstance.setNetworkKbsRead(JsonUtil.getIntegerValue(jsonObject, "networkkbsread"));
            vmInstance.setNetworkKbsWrite(JsonUtil.getIntegerValue(jsonObject, "networkkbswrite"));
            vmInstance.setPasswordEnabled(JsonUtil.getBooleanValue(jsonObject, "passwordenabled"));
            vmInstance.setPassword(JsonUtil.getStringValue(jsonObject, "password"));
            vmInstance.setIso(JsonUtil.getStringValue(jsonObject, "isoid"));
            vmInstance.setIsoName(JsonUtil.getStringValue(jsonObject, "isoname"));
            vmInstance.setTransIsoId(JsonUtil.getStringValue(jsonObject, "isoid"));
            vmInstance.setDisplayName(JsonUtil.getStringValue(jsonObject, "displayname"));
            JSONArray nicArray = jsonObject.getJSONArray("nic");
            vmInstance.setIpAddress(JsonUtil.getStringValue(nicArray.getJSONObject(0), "ipaddress"));
            vmInstance.setTransNetworkId(JsonUtil.getStringValue(nicArray.getJSONObject(0), "networkid"));
            vmInstance.setTransDepartmentId(JsonUtil.getStringValue(jsonObject, "account"));
            vmInstance.setTransProjectId(JsonUtil.getStringValue(jsonObject, "projectid"));
            vmInstance.setInstanceInternalName(JsonUtil.getStringValue(jsonObject, "instancename"));
            vmInstance.setTransOwnerId(JsonUtil.getStringValue(jsonObject, "userid"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vmInstance;
    }

    /**
     * Mapping entity object into list.
     *
     * @param csInstanceService list of vms.
     * @return vm
     */
    public static Map<String, VmInstance> convert(List<VmInstance> csInstanceService) {
        Map<String, VmInstance> vmMap = new HashMap<String, VmInstance>();
        for (VmInstance instance : csInstanceService) {
            vmMap.put(instance.getUuid(), instance);
        }
        return vmMap;
    }
}
