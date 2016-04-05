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
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import ck.panda.constants.CloudStackConstants;
import ck.panda.util.JsonUtil;

/**
 * VM snapshot: snapshot on entire VM, including its volumes, memory and CPU state, resides on primary storage. Mainly
 * used for revert purpose.
 */
@Entity
@Table(name = "vm_snapshots")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class VmSnapshot implements Serializable {
    /** Unique Id of the snapshot. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Name of the snapshot. */
    @Column(name = "name", nullable = false)
    private String name;

    /** description of the snapshot. */
    @Column(name = "description")
    private String description;

    /** cloudstack's snapshot uuid. */
    @Column(name = "uuid")
    private String uuid;

    /** cloudstack's snapshot parent uuid. */
    @Column(name = "parrent_uuid")
    private String parent;

    /** owner id. */
    @JoinColumn(name = "owner_id", referencedColumnName = "id", updatable = false, insertable = false)
    @OneToOne
    private User owner;

    /** owner id. */
    @Column(name = "owner_id")
    private Long ownerId;

    /** Set syncFlag. */
    @Transient
    private Boolean syncFlag;

    /** Set Memory. */
    @Transient
    private Boolean snapshotMemory;

    /** Instance id. */
    @JoinColumn(name = "vm_id", referencedColumnName = "id", updatable = false, insertable = false)
    @ManyToOne
    private VmInstance vm;

    /** Instance id. */
    @Column(name = "vm_id")
    private Long vmId;

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

    /** Project Object for the Network. */
    @JoinColumn(name = "project_id", referencedColumnName = "id", updatable = false, insertable = false)
    @ManyToOne
    private Project project;

    /** Network project id. */
    @Column(name = "project_id")
    private Long projectId;

    /** Department Object for the Network. */
    @JoinColumn(name = "department_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Department department;

    /** id for the Department. */
    @Column(name = "department_id")
    private Long departmentId;


    /** current state. */
    @Column(name = "status")
    private Status status;

    /** Enumeration status for snapshot. */
    public enum Status {
        /** While snapshot creation it gets status as creating. */
        Creating,
        /** While snapshot creation if gets failure and it gets status as Error. */
        Error,
        /** After destroy or expunge snapshot get status as expunging. */
        Expunging,
        /** While snapshot creation it gets the status as Implemented. */
        Implemented,
        /** After create snapshot it gets status as ready. */
        Ready,
        /** Allocated status whne snapshot memory is occupied */
        Allocated
    }

    /** current state. */
    @Column(name = "type")
    private SnapshotType type;

    /** Enumeration Type for snapshot. */
    public enum SnapshotType {
        /** snapshot disk of current VM. */
        Disk,
        /** snapshot disk and memory of current VM. */
        DiskAndMemory
    }

    /** Check snapshot available or not. */
    @Column(name = "is_removed", columnDefinition = "tinyint default 0")
    private Boolean isRemoved;

    /** Check snapshot is revert as current. */
    @Column(name = "is_current", columnDefinition = "tinyint default 0")
    private Boolean isCurrent;

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

    /** Transient network of the instance. */
    @Transient
    private String transvmInstanceId;

    /** Transient domain of the instance. */
    @Transient
    private String transDomainId;

    /** Transient name of the instance. */
    @Transient
    private String transDisplayName;

    /**
     * Get the sync status.
     *
     * @return the syncFlag
     */
    public Boolean getSyncFlag() {
        return syncFlag;
    }

    /**
     * Set the sync status.
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
     * Get the the status of snapshot.
     *
     * @return the status.
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set the snapshot status.
     *
     * @param status to set.
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Get the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the Owner .
     *
     * @return the owner
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Set the Owner .
     *
     * @param owner the owner to set
     */
    public void setOwner(User owner) {
        this.owner = owner;
    }

    /**
     * Get the owner id.
     *
     * @return the ownerId
     */
    public Long getOwnerId() {
        return ownerId;
    }

    /**
     * Set the owner id.
     *
     * @param ownerId the ownerId to set
     */
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * Get the vm.
     *
     * @return the vm
     */
    public VmInstance getVm() {
        return vm;
    }

    /**
     * Set the vm.
     *
     * @param vm the vm to set
     */
    public void setVm(VmInstance vm) {
        this.vm = vm;
    }

    /**
     * Get the vm id.
     *
     * @return the vmId
     */
    public Long getVmId() {
        return vmId;
    }

    /**
     * Set the vm id.
     *
     * @param vmId the vmId to set
     */
    public void setVmId(Long vmId) {
        this.vmId = vmId;
    }

    /**
     * Get the type.
     *
     * @return the type
     */
    public SnapshotType getType() {
        return type;
    }

    /**
     * Set the type.
     *
     * @param type the type to set
     */
    public void setType(SnapshotType type) {
        this.type = type;
    }

    /**
     * Check whether snapshot removed or not.
     *
     * @return the isRemoved.
     */
    public Boolean getIsRemoved() {
        return isRemoved;
    }

    /**
     * Delete the snapshot.
     *
     * @param isRemoved to set.
     */
    public void setIsRemoved(Boolean isRemoved) {
        this.isRemoved = isRemoved;
    }

    /**
     * get the snapshot id.
     *
     * @return the id.
     */
    public Long getId() {
        return id;
    }

    /**
     * set the snapshot id.
     *
     * @param id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * get the snapshot name.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * set the snapshot name.
     *
     * @param name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * get the snapshot UUID.
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
     * get the domain for snapshot.
     *
     * @return the domain
     */
    public Domain getDomain() {
        return domain;
    }

    /**
     * set the domain for snapshot.
     *
     * @param domain domain to set.
     */
    public void setDomain(Domain domain) {
        this.domain = domain;
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
     * Set the Zone.
     *
     * @param zone the zone to set.
     */
    public void setZone(Zone zone) {
        this.zone = zone;
    }

    /**
     * Get the Zone.
     *
     * @return the domainId.
     */
    public Long getDomainId() {
        return domainId;
    }

    /**
     * Set the domain id.
     *
     * @param domainId the domain id to set.
     */
    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    /**
     * Get the Zone id.
     *
     * @return the zoneId.
     */
    public Long getZoneId() {
        return zoneId;
    }

    /**
     * Set the Zone id.
     *
     * @param zoneId the zone id to set.
     */
    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * Get the parent.
     *
     * @return the parent
     */
    public String getParent() {
        return parent;
    }

    /**
     * Set the parent.
     *
     * @param parent the parent to set
     */
    public void setParent(String parent) {
        this.parent = parent;
    }

    /**
     * Get status of last snapshot.
     *
     * @return the isCurrent
     */
    public Boolean getIsCurrent() {
        return isCurrent;
    }

    /**
     * Set status of last snapshot.
     *
     * @param isCurrent the isCurrent to set
     */
    public void setIsCurrent(Boolean isCurrent) {
        this.isCurrent = isCurrent;
    }

    /**
     * Get the memory snapshot.
     *
     * @return the snapshotMemory
     */
    public Boolean getSnapshotMemory() {
        return snapshotMemory;
    }

    /**
     * Set the memory snapshot.
     *
     * @param snapshotMemory the snapshotMemory to set
     */
    public void setSnapshotMemory(Boolean snapshotMemory) {
        this.snapshotMemory = snapshotMemory;
    }

    /**
     * @return the transvmInstanceId
     */
    public String getTransvmInstanceId() {
        return transvmInstanceId;
    }

    /**
     * @param transvmInstanceId the transvmInstanceId to set
     */
    public void setTransvmInstanceId(String transvmInstanceId) {
        this.transvmInstanceId = transvmInstanceId;
    }

    /**
     * @return the transDisplayName
     */
    public String getTransDisplayName() {
        return transDisplayName;
    }

    /**
     * @param transDisplayName the transDisplayName to set
     */
    public void setTransDisplayName(String transDisplayName) {
        this.transDisplayName = transDisplayName;
    }

    /**
     * @return the transDomainId
     */
    public String getTransDomainId() {
        return transDomainId;
    }

    /**
     * @param transDomainId the transDomainId to set
     */
    public void setTransDomainId(String transDomainId) {
        this.transDomainId = transDomainId;
    }

    /**
     * Get network project.
     *
     * @return the project.
     */
    public Project getProject() {
        return project;
    }

    /**
     * Set network project.
     *
     * @param project to set.
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * Get network project id.
     *
     * @return the projectId.
     */
    public Long getProjectId() {
        return projectId;
    }

    /**
     * Set network project id.
     *
     * @param projectId the project id to set.
     */
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }


    /**
     * Get the department for snapshot.
     *
     * @return the department
     */
    public Department getDepartment() {
        return department;
    }

    /**
     * Set the department for snapshot.
     *
     * @param department to set
     */
    public void setDepartment(Department department) {
        this.department = department;
    }

    /**
     * Get the department id of the snapshot.
     *
     * @return the departmentId
     */
    public Long getDepartmentId() {
        return departmentId;
    }

    /**
     * Set the department id of the snapshot.
     *
     * @param departmentId to set
     */
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * Convert JSONObject into vm snapshot object.
     *
     * @param jsonObject JSON object.
     * @return vm snapshot object.
     */
    public static VmSnapshot convert(JSONObject jsonObject) {
        VmSnapshot vmSnapshot = new VmSnapshot();
        vmSnapshot.setSyncFlag(false);
        try {
            vmSnapshot.setIsRemoved(false);
            vmSnapshot.setUuid(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_ID));
            vmSnapshot.setStatus(Status.valueOf(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_STATE)));
            vmSnapshot.setDescription(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_DESCRIPTION));
            vmSnapshot.setName(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_DISPLAY_NAME));
            vmSnapshot.setIsCurrent(JsonUtil.getBooleanValue(jsonObject, CloudStackConstants.CS_CURRENT));
            vmSnapshot.setType(SnapshotType.valueOf(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_TYPE)));
            vmSnapshot.setParent(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_PARENT));
            vmSnapshot.setTransvmInstanceId(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_VIRTUAL_MACHINE_ID));
            vmSnapshot.setTransDomainId(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_DOMAIN_ID));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vmSnapshot;
    }

    /**
     * Mapping entity object into list.
     *
     * @param csSnapshotService list of vms snapshot.
     * @return vm snapshot.
     */
    public static Map<String, VmSnapshot> convert(List<VmSnapshot> csSnapshotService) {
        Map<String, VmSnapshot> vmMap = new HashMap<String, VmSnapshot>();
        for (VmSnapshot instancesm : csSnapshotService) {
            vmMap.put(instancesm.getUuid(), instancesm);
        }
        return vmMap;
    }

}
