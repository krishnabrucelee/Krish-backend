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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
import ck.panda.util.ConvertUtil;
import ck.panda.util.JsonUtil;

/**
 * VM Instances are main entity in our panda project to keep track the status of each instances. Based on VM
 * Instances we can create and destroy, update instance, list Active VM's from cloud stack, etc.,
 */
@Entity
@Table(name = "ck_vm_instance")
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

    /** cloudstack's instance uuid. */
    @Column(name = "uuid")
    private String uuid;

    /** cloudstack's instance iso uuid. */
    @Column(name = "iso")
    private String iso;

    /** cloudstack's instance iso name. */
    @Column(name = "iso_name")
    private String isoName;

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
    @NotNull
    @Column(name = "domain_id")
    private Long domainId;

    /** Instance zone. */
    @JoinColumn(name = "zone_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Zone zone;

    /** Instance zone id. */
    @NotNull
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
        /** while instance creation if get failure get status as Error . */
        Error,
        /** while instance creation if get status as creating . */
        Creating,
        /** while instance creation if get status as Implemented . */
        Implemented,
        /** after launch instance if get status as ready . */
        Ready
    }

    /** Instance host. */
    @JoinColumn(name = "host_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @OneToOne
    private Host host;

    /** Instance host id. */
    @Column(name = "host_id")
    private Long hostId;

    /** Instance pod. */
    @JoinColumn(name = "pod_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @OneToOne
    private Pod pod;

    /** Instance pod id. */
    @Column(name = "pod_id")
    private Long podId;

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

    /** Version attribute to handle optimistic locking. */
    @Version
    @Column(name = "version")
    private Long version;

    /** Created by user. */
    @CreatedBy
    @JoinColumn(name = "created_user_id", referencedColumnName = "id")
    @OneToOne
    private User createdBy;

    /** Last updated by user. */
    @LastModifiedBy
    @JoinColumn(name = "updated_user_id", referencedColumnName = "id")
    @OneToOne
    private User updatedBy;

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
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * Set created user id.
     *
     * @param createdBy to set.
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the last modified user id.
     *
     * @return the updated user.
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the last modified user id.
     *
     * @param updatedBy to set.
     */
    public void setUpdatedBy(User updatedBy) {
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
     * Set the host.
     *
     * @param host the host to set
     */
    public void setHost(Host host) {
        this.host = host;
    }

    /**
     * Get the host.
     *
     * @return the pod
     */
    public Pod getPod() {
        return pod;
    }

    /**
     * Set the host.
     *
     * @param pod the pod to set
     */
    public void setPod(Pod pod) {
        this.pod = pod;
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
     * @return the zone.
     */
    public Zone getZone() {
        return zone;
    }

    /**
     * @param zone the zone to set.
     */
    public void setZone(Zone zone) {
        this.zone = zone;
    }

    /**
     * @return the instance owner id.
     */
    public Long getInstanceOwnerId() {
        return instanceOwnerId;
    }

    /**
     * @param instanceOwnerId the instance owner id to set.
     */
    public void setInstanceOwnerId(Long instanceOwnerId) {
        this.instanceOwnerId = instanceOwnerId;
    }

    /**
     * @return the projectId.
     */
    public Long getProjectId() {
        return projectId;
    }

    /**
     * @param projectId the project id to set.
     */
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    /**
     * @return the departmentId.
     */
    public Long getDepartmentId() {
        return departmentId;
    }

    /**
     * @param departmentId the department id to set.
     */
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * @return the templateId.
     */
    public Long getTemplateId() {
        return templateId;
    }

    /**
     * @param templateId the template id to set.
     */
    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    /**
     * @return the domainId.
     */
    public Long getDomainId() {
        return domainId;
    }

    /**
     * @param domainId the domain id to set.
     */
    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    /**
     * @return the zoneId.
     */
    public Long getZoneId() {
        return zoneId;
    }

    /**
     * @param zoneId the zone id to set.
     */
    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * @return the computeOffering.
     */
    public ComputeOffering getComputeOffering() {
        return computeOffering;
    }

    /**
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
     * @param convertUtil convert Entity object from UUID.
     * @return vm object.
     */
    public static VmInstance convert(JSONObject jsonObject, ConvertUtil convertUtil) {
        VmInstance vmInstance = new VmInstance();
        vmInstance.setSyncFlag(false);
        try {
            vmInstance.setName(JsonUtil.getStringValue(jsonObject, "name"));
            vmInstance.setUuid(JsonUtil.getStringValue(jsonObject, "id"));
            vmInstance.setDomainId(convertUtil.getDomainId(JsonUtil.getStringValue(jsonObject, "domainid")));
            vmInstance.setStatus(Status.valueOf(JsonUtil.getStringValue(jsonObject, "state")));
            vmInstance.setZoneId(convertUtil.getZoneId(JsonUtil.getStringValue(jsonObject, "zoneid")));
            vmInstance.setHostId(convertUtil.getHostId(JsonUtil.getStringValue(jsonObject, "hostid")));
            vmInstance.setPodId(
                    convertUtil.getPodId(convertUtil.getHostId(JsonUtil.getStringValue(jsonObject, "hostid"))));
            vmInstance.setTemplateId(convertUtil.getTemplateId(JsonUtil.getStringValue(jsonObject, "templateid")));
            vmInstance.setComputeOfferingId(
                    convertUtil.getComputeOfferId(JsonUtil.getStringValue(jsonObject, "serviceofferingid")));
            vmInstance.setCpuCore(JsonUtil.getIntegerValue(jsonObject, "cpunumber"));
            vmInstance.setCpuSpeed(JsonUtil.getIntegerValue(jsonObject, "cpuspeed"));
            vmInstance.setMemory(JsonUtil.getIntegerValue(jsonObject, "memory"));
            vmInstance.setCpuUsage(JsonUtil.getStringValue(jsonObject, "cpuused"));
            vmInstance.setPasswordEnabled(JsonUtil.getBooleanValue(jsonObject, "passwordenabled"));
            vmInstance.setPassword(JsonUtil.getStringValue(jsonObject, "password"));
            vmInstance.setIso(JsonUtil.getStringValue(jsonObject, "isoid"));
            vmInstance.setIsoName(JsonUtil.getStringValue(jsonObject, "isoname"));
            JSONArray nicArray = jsonObject.getJSONArray("nic");
            vmInstance.setIpAddress(JsonUtil.getStringValue(nicArray.getJSONObject(0), "ipaddress"));
            vmInstance.setNetworkId(
                    convertUtil.getNetworkId(JsonUtil.getStringValue(nicArray.getJSONObject(0), "networkid")));
            vmInstance.setInstanceOwnerId(convertUtil.getOwnerId(JsonUtil.getStringValue(jsonObject, "account"),
                    convertUtil.getDomain(JsonUtil.getStringValue(jsonObject, "domainid"))));
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
