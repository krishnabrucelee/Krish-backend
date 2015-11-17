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
import ck.panda.util.ConvertUtil;
import ck.panda.util.JsonUtil;

/**
 * VM snapshot: snapshot on entire VM, including its volumes, memory and CPU
 * state, resides on primary storage. Mainly used for revert purpose.
 */
@Entity
@Table(name = "vm_snapshot")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class VmSnapshot implements Serializable {
    /** Unique Id of the snapshot. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Name of the snapshot. */
    @NotEmpty
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
    @NotNull
    @Column(name = "domain_id")
    private Long domainId;

    /** Instance zone. */
    @JoinColumn(name = "zone_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Zone zone;

    /** Instance zone id. */
    @Column(name = "zone_id")
    private Long zoneId;

    /** current state. */
    @Column(name = "status")
    private Status status;

    /** Enumeration status for snapshot. */
    public enum Status {
        /** after destroy or expunge snapshot get status as expunging. */
        Expunging,
        /** while snapshot creation if get failure and it get status as Error. */
        Error,
        /** while snapshot creation it get status as creating . */
        Creating,
        /** while snapshot creation it get implemented the status as Implemented. */
        Implemented,
        /** after create snapshot it get status as ready . */
        Ready
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
    @Column(name = "is_removed")
    private Boolean isRemoved;

    /** Check snapshot is revert as current. */
    @Column(name = "is_current")
    private Boolean isCurrent;

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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("VmSnapshot [id=");
        builder.append(id);
        builder.append(", name=");
        builder.append(name);
        builder.append(", description=");
        builder.append(description);
        builder.append(", uuid=");
        builder.append(uuid);
        builder.append(", owner=");
        builder.append(owner);
        builder.append(", ownerId=");
        builder.append(ownerId);
        builder.append(", syncFlag=");
        builder.append(syncFlag);
        builder.append(", vm=");
        builder.append(vm);
        builder.append(", vmId=");
        builder.append(vmId);
        builder.append(", domain=");
        builder.append(domain);
        builder.append(", domainId=");
        builder.append(domainId);
        builder.append(", zone=");
        builder.append(zone);
        builder.append(", zoneId=");
        builder.append(zoneId);
        builder.append(", status=");
        builder.append(status);
        builder.append(", type=");
        builder.append(type);
        builder.append(", isRemoved=");
        builder.append(isRemoved);
        builder.append(", version=");
        builder.append(version);
        builder.append(", createdBy=");
        builder.append(createdBy);
        builder.append(", updatedBy=");
        builder.append(updatedBy);
        builder.append(", createdDateTime=");
        builder.append(createdDateTime);
        builder.append(", updatedDateTime=");
        builder.append(updatedDateTime);
        builder.append("]");
        return builder.toString();
    }

    /**
     * Convert JSONObject into vm snapshot object.
     *
     * @param jsonObject JSON object.
     * @param convertUtil convert Entity object from UUID.
     * @return vm snapshot object.
     */
    public static VmSnapshot convert(JSONObject jsonObject, ConvertUtil convertUtil) {
        VmSnapshot vmSnapshot = new VmSnapshot();
        vmSnapshot.setSyncFlag(false);
        try {
            vmSnapshot.setIsRemoved(false);
            vmSnapshot.setName(JsonUtil.getStringValue(jsonObject, "displayname"));
            vmSnapshot.setDescription(JsonUtil.getStringValue(jsonObject, "description"));
            vmSnapshot.setParent(JsonUtil.getStringValue(jsonObject, "parent"));
            vmSnapshot.setIsCurrent(JsonUtil.getBooleanValue(jsonObject, "current"));
            vmSnapshot.setUuid(JsonUtil.getStringValue(jsonObject, "id"));
            vmSnapshot.setVmId(convertUtil.getVmId(JsonUtil.getStringValue(jsonObject, "virtualmachineid")));
            vmSnapshot.setDomainId(convertUtil.getVm(JsonUtil.getStringValue(jsonObject, "virtualmachineid")).getDomainId());
            vmSnapshot.setStatus(Status.valueOf(JsonUtil.getStringValue(jsonObject, "state")));
            vmSnapshot.setType(SnapshotType.valueOf(JsonUtil.getStringValue(jsonObject, "type")));
            vmSnapshot.setZoneId(convertUtil.getVm(JsonUtil.getStringValue(jsonObject, "virtualmachineid")).getZoneId());
            vmSnapshot.setOwnerId(convertUtil.getVm(JsonUtil.getStringValue(jsonObject, "virtualmachineid")).getInstanceOwnerId());
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
