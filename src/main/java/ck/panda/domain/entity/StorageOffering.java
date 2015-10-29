package ck.panda.domain.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Storage Offerings, defined by administrator. provide a choice of disk size
 * and IOPS (Quality of Service) for primary data storage
 *
 */
@Entity
@Table(name = "ck_storage_offering")
public class StorageOffering {

    /**
     * Unique ID of the storage offering.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /**
     * Unique ID from Cloud Stack.
     */
    @Column(name = "uuid")
    private String uuid;

    /**
     * A desired name of the Storage offering.
     */
    @NotEmpty
    @Size(min = 4, max = 20)
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * The domain ID this disk offering belongs to. Ignore this information as
     * it is not currently applicable.
     */
    @JoinColumn(name = "domain_id", referencedColumnName = "id")
    @OneToOne
    private Domain domain;

    /**
     * Description of Storage Offering.
     */
    @NotEmpty
    @Size(min = 4, max = 20)
    @Column(name = "description")
    private String description;

    /**
     * Indicate whether the storage offering should be available all domains or
     * only some domains. If checked, to make it available to all domains. If
     * not checked, to limit the scope to a sub domain
     */
    @Column(name = "is_public", columnDefinition = "tinyint default 1")
    private Boolean isPublic;

    /**
     * If checked, the user can set their own disk size. If not checked, the
     * root administrator must define a value in Disk size.
     */
    @Column(name = "is_custom_disk", columnDefinition = "tinyint default 0")
    private Boolean isCustomDisk;

    /**
     * Appears only if Custom disk size is not selected. Define the volume size
     * in GB.
     */
    @Column(name = "disk_size", nullable = false, columnDefinition = "bigint(20) default 1")
    private Long diskSize;

    /**
     * The storage type for this storage offering, whether it is isolated or
     * shared.
     */
    @Column(name = "storage_type")
    @Enumerated(EnumType.STRING)
    private StorageType storageType;

    /**
     * The tags for the Storage offering. The tags that should be associated
     * with the primary storage for this disk.
     */
    @Column(name = "storage_tags")
    private String storageTags;

    /**
     * Quality of Service type, whether it is Hypervisor or Storage.
     */
    @Column(name = "qos_type")
    @Enumerated(EnumType.STRING)
    private QosType qosType;

    /**
     * The bytes read rate of the storage offering.
     */
    @Column(name = "bytes_read_rate")
    private Long diskBytesReadRate;

    /**
     * The bytes write rate of the storage offering.
     */
    @Column(name = "bytes_write_rate")
    private Long diskBytesWriteRate;

    /**
     * I/O requests read rate of the storage offering.
     */
    @Column(name = "iops_read_rate")
    private Long diskIopsReadRate;

    /**
     * I/O requests write rate of the storage offering.
     */
    @Column(name = "iops_write_rate")
    private Long diskIopsWriteRate;

    /**
     * If checked, the user can set their own IOPS. If not checked, the root
     * administrator can define values.
     */
    @Column(name = "is_customized_iops", columnDefinition = "tinyint default 0")
    private Boolean isCustomizedIops;

    /**
     * The maximum iops of the disk offering.
     */
    @Column(name = "max_iops")
    private Long diskMaxIops;

    /**
     * The minimum iops of the disk offering.
     */
    @Column(name = "min_iops")
    private Long diskMinIops;

    /**
     * The Zone ID, this disk offering belongs to. Ignore this information as it
     * is not currently applicable.
     */
    @JoinColumn(name = "zone_id", referencedColumnName = "id")
    @OneToOne
    private Zone zone;

    /**
     * Cost per month usage.
     */
    @Column(name = "cost_per_hour_disk")
    private Double costPerMonth;

    /**
     * Cost for 1 Gb per month usage.
     */
    @Column(name = "cost_gb_per_month")
    private Double costGbPerMonth;

    /**
     * Cost per month usage.
     */
    @Column(name = "cost_per_hour_iops")
    private Double costPerIops;

    @Transient
    private Boolean isSyncFlag;
    /**
     * Cost for 1 Iops per month usage.
     */
    @Column(name = "cost_iops_per_month")
    private Double costIopsPerMonth;

    /** Status attribute to verify status of the Storage offering. */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    /** Version attribute to handle optimistic locking. */
    @Version
    @Column(name = "version")
    private Long version;

    /** Created by user. */
    @CreatedBy
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    @OneToOne
    private User createdBy;

    /** Last updated by user. */
    @LastModifiedBy
    @JoinColumn(name = "updated_by", referencedColumnName = "id")
    @OneToOne
    private User updatedBy;

    /** Created date and time. */
    @CreatedDate
    @Column(name = "created_date_time")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentZonedDateTime")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private DateTime createdDateTime;

    /** Last updated date and time. */
    @LastModifiedDate
    @Column(name = "updated_date_time")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentZonedDateTime")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private DateTime updatedDateTime;

    /** An active attribute is to check whether the role is active or not. */
    @Column(name = "is_active", columnDefinition = "tinyint default 1")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean isActive;

    /**
     * To set the default value while creating tables in database.
     */
    @PrePersist
    void preInsert() {
        this.isActive = true;
    }

    /**
     * Enum type for Storage Offering Status.
     *
     */
    public enum Status {
        /** Storage Offering will be in a Enabled State. */
        ENABLED,
        /** Storage Offering will be in a Disabled State. */
        DISABLED
    }

    /**
     * Enum type for Storage Type. Type of Disk for virtual machine.
     *
     */
    public enum StorageType {
        /** Shared is storage accessible via NFS. */
        shared,
        /** Isolated is attached to the hypervisor host where vm is running. */
        local
    }

    /**
     * Enum type for Storage Type. Type of Disk for virtual machine.
     *
     */
    public enum QosType {
        /** Shared is storage accessible via NFS. */
        Hypervisor,
        /** Isolated is attached to the hypervisor host where vm is running. */
        Storage
    }

    /**
     * Get the id of the storage offering.
     *
     * @return the id of the storage offering
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id of the storage offering.
     *
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the uuid of the storage offering.
     *
     * @return the uuid of the storage offering
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the uuid of the storage offering.
     *
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get the name of the storage offering.
     *
     * @return the name of the storage offering
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the storage offering.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the domain of the storage offering.
     *
     * @return the domain of the storage offering
     */
    public Domain getDomain() {
        return domain;
    }

    /**
     * Set the domain of the storage offering.
     *
     * @param domain the domain to set
     */
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    /**
     * Get the description of the storage offering.
     *
     * @return the description of the storage offering
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of the storage offering.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the is public of the storage offering.
     *
     * @return the isPublic of the storage offering
     */
    public Boolean getIsPublic() {
        return isPublic;
    }

    /**
     * Set the is public of the storage offering.
     *
     * @param isPublic the is public to set
     */
    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    /**
     * Get the is custom disk of the storage offering.
     *
     * @return the isCustomDisk of the storage offering
     */
    public Boolean getIsCustomDisk() {
        return isCustomDisk;
    }

    /**
     * Set the is custom disk of the storage offering.
     *
     * @param isCustomDisk the is custom disk to set
     */
    public void setIsCustomDisk(Boolean isCustomDisk) {
        this.isCustomDisk = isCustomDisk;
    }

    /**
     * Get the disk size of the storage offering.
     *
     * @return the diskSize of the storage offering
     */
    public Long getDiskSize() {
        return diskSize;
    }

    /**
     * Set the disk size of the storage offering.
     *
     * @param diskSize the disk size to set
     */
    public void setDiskSize(Long diskSize) {
        this.diskSize = diskSize;
    }

    /**
     * Get the storage type of the storage offering.
     *
     * @return the storageType of the storage offering
     */
    public StorageType getStorageType() {
        return storageType;
    }

    /**
     * Set the storage type of the storage offering.
     *
     * @param storageType the storage type to set
     */
    public void setStorageType(StorageType storageType) {
        this.storageType = storageType;
    }

    /**
     * Get the storage tags of the storage offering.
     *
     * @return the storageTags of the storage offering
     */
    public String getStorageTags() {
        return storageTags;
    }

    /**
     * Set the storage tags of the storage offering.
     *
     * @param storageTags the storage tags to set
     */
    public void setStorageTags(String storageTags) {
        this.storageTags = storageTags;
    }

    /**
     * Get the qos type of the storage offering.
     *
     * @return the qosType of the storage offering
     */
    public QosType getQosType() {
        return qosType;
    }

    /**
     * Set the qos type of the storage offering.
     *
     * @param qosType the qos type to set
     */
    public void setQosType(QosType qosType) {
        this.qosType = qosType;
    }

    /**
     * Get the disk bytes read rate of the storage offering.
     *
     * @return the diskBytesReadRate of the storage offering
     */
    public Long getDiskBytesReadRate() {
        return diskBytesReadRate;
    }

    /**
     * Set the disk bytes read rate of the storage offering.
     *
     * @param diskBytesReadRate the disk bytes read rate to set
     */
    public void setDiskBytesReadRate(Long diskBytesReadRate) {
        this.diskBytesReadRate = diskBytesReadRate;
    }

    /**
     * Get the disk bytes write rate of the storage offering.
     *
     * @return the diskBytesWriteRate of the storage offering
     */
    public Long getDiskBytesWriteRate() {
        return diskBytesWriteRate;
    }

    /**
     * Set the disk bytes write rate of the storage offering.
     *
     * @param diskBytesWriteRate the disk bytes write rate to set
     */
    public void setDiskBytesWriteRate(Long diskBytesWriteRate) {
        this.diskBytesWriteRate = diskBytesWriteRate;
    }

    /**
     * Get the disk iops read rate of the storage offering.
     *
     * @return the diskIopsReadRate of the storage offering
     */
    public Long getDiskIopsReadRate() {
        return diskIopsReadRate;
    }

    /**
     * Set the disk iops read rate of the storage offering.
     *
     * @param diskIopsReadRate the disk iops read rate to set
     */
    public void setDiskIopsReadRate(Long diskIopsReadRate) {
        this.diskIopsReadRate = diskIopsReadRate;
    }

    /**
     * Get the disk iops write rate of the storage offering.
     *
     * @return the diskIopsWriteRate of the storage offering
     */
    public Long getDiskIopsWriteRate() {
        return diskIopsWriteRate;
    }

    /**
     * Set the disk iops write rate of the storage offering.
     *
     * @param diskIopsWriteRate the disk iops write rate to set
     */
    public void setDiskIopsWriteRate(Long diskIopsWriteRate) {
        this.diskIopsWriteRate = diskIopsWriteRate;
    }

    /**
     * Get the isCustomizedIops of the storage offering.
     *
     * @return the isCustomizedIops of the storage offering
     */
    public Boolean getIsCustomizedIops() {
        return isCustomizedIops;
    }

    /**
     * Set the isCustomizedIops of the storage offering.
     *
     * @param isCustomizedIops the isCustomizedIops to set
     */
    public void setIsCustomizedIops(Boolean isCustomizedIops) {
        this.isCustomizedIops = isCustomizedIops;
    }

    /**
     * Get the disk max iops of the storage offering.
     *
     * @return the diskMaxIops of the storage offering
     */
    public Long getDiskMaxIops() {
        return diskMaxIops;
    }

    /**
     * Set the disk max iops of the storage offering.
     *
     * @param diskMaxIops the disk max iops to set
     */
    public void setDiskMaxIops(Long diskMaxIops) {
        this.diskMaxIops = diskMaxIops;
    }

    /**
     * Get the disk min iops of the storage offering.
     *
     * @return the diskMinIops of the storage offering
     */
    public Long getDiskMinIops() {
        return diskMinIops;
    }

    /**
     * Set the disk min iops of the storage offering.
     *
     * @param diskMinIops the disk min iops to set
     */
    public void setDiskMinIops(Long diskMinIops) {
        this.diskMinIops = diskMinIops;
    }

    /**
     * Get the zone of the storage offering.
     * @return the zone of the storage offering
     */
    public Zone getZone() {
        return zone;
    }

    /**
     * Set the zone of the storage offering.
     * @param zone the zone to set
     */
    public void setZone(Zone zone) {
        this.zone = zone;
    }

    /**
     * Get the cost per month of the storage offering.
     * @return the costPerMonth of the storage offering
     */
    public Double getCostPerMonth() {
        return costPerMonth;
    }

    /**
     * Set the cost per month of the storage offering.
     * @param costPerMonth the costPerMonth to set
     */
    public void setCostPerMonth(Double costPerMonth) {
        this.costPerMonth = costPerMonth;
    }

    /**
     * Get the cost Gb per month of the storage offering.
     * @return the costGbPerMonth of the storage offering
     */
    public Double getCostGbPerMonth() {
        return costGbPerMonth;
    }

    /**
     * Set the cost Gb per month of the storage offering.
     * @param costGbPerMonth the costGbPerMonth to set
     */
    public void setCostGbPerMonth(Double costGbPerMonth) {
        this.costGbPerMonth = costGbPerMonth;
    }

    /**
     * Get the cost per iops of the storage offering.
     * @return the costPerIops of the storage offering
     */
    public Double getCostPerIops() {
        return costPerIops;
    }

    /**
     * Set the cost per iops of the storage offering.
     * @param costPerIops the costPerIops to set
     */
    public void setCostPerIops(Double costPerIops) {
        this.costPerIops = costPerIops;
    }

    /**
     * Get the cost iops per month of the storage offering.
     * @return the costIopsPerMonth of the storage offering
     */
    public Double getCostIopsPerMonth() {
        return costIopsPerMonth;
    }

    /**
     * Set the cost iops per month of the storage offering.
     * @param costIopsPerMonth the costIopsPerMonth to set
     */
    public void setCostIopsPerMonth(Double costIopsPerMonth) {
        this.costIopsPerMonth = costIopsPerMonth;
    }

    /**
     * Get the status of the storage offering.
     *
     * @return the status of the storage offering
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set the status of the storage offering.
     *
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Get the version of the storage offering.
     *
     * @return the version of the storage offering
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Set the version of the storage offering.
     *
     * @param version the version to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get the created by of the storage offering.
     *
     * @return the createdBy of the storage offering
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the created by of the storage offering.
     *
     * @param createdBy the created by to set
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updated by of the storage offering.
     *
     * @return the updatedBy of the storage offering
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updated by of the storage offering.
     *
     * @param updatedBy the updated by to set
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the created date time of the storage offering.
     *
     * @return the createdDateTime of the storage offering
     */
    public DateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the created date time of the storage offering.
     *
     * @param createdDateTime the created date time to set
     */
    public void setCreatedDateTime(DateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updated date time of the storage offering.
     *
     * @return the updatedDateTime of the storage offering
     */
    public DateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updated date time of the storage offering.
     *
     * @param updatedDateTime the updated date time to set
     */
    public void setUpdatedDateTime(DateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get the is active of the storage offering.
     *
     * @return the isActive of the storage offering
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set the is active of the storage offering.
     *
     * @param isActive the is active to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Convert JSONObject to Storage Offering entity.
     *
     * @param object json object
     * @return Storage Offering entity objects
     * @throws JSONException unhandled json errors
     */
    public static StorageOffering convert(JSONObject object) throws JSONException {
        StorageOffering storageOffering = new StorageOffering();
        storageOffering.uuid = object.has("id") ? object.get("id").toString() : "";
        storageOffering.name = object.has("name") ? object.get("name").toString() : "";
        storageOffering.description = object.has("displaytext") ? object.get("displaytext").toString() : "";
        storageOffering.diskSize = object.has("disksize") ? object.getLong("disksize") : 0;
        storageOffering.setIsSyncFlag(false);

        return storageOffering;
    }

    /**
	 * Get the synch flag .
	 *
	 * @return the isSyncFlag.
	 */
	public Boolean getIsSyncFlag() {
		return isSyncFlag;
	}

	/**
	 * Set the Sync Flag.
	 *
	 * @param isSyncFlag - the isSyncFlag to set.
	 */
	public void setIsSyncFlag(Boolean isSyncFlag) {
		this.isSyncFlag = isSyncFlag;
	}

	/**
     * Mapping Storage Offering entity object in list.
     *
     * @param Storage Offering lists of Storage Offering
     * @return Storage Offering mapped values.
     */
    public static Map<String, StorageOffering> convert(List<StorageOffering> storageOfferingList) {
        Map<String, StorageOffering> storageOfferingMap = new HashMap<String, StorageOffering>();

        for (StorageOffering storageOffering : storageOfferingList) {
            storageOfferingMap.put(storageOffering.getUuid(), storageOffering);
        }
        return storageOfferingMap;
    }
}
