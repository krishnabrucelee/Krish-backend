package ck.panda.domain.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import ck.panda.constants.CloudStackConstants;
import ck.panda.util.JsonUtil;
import ck.panda.util.JsonValidator;

/**
 * Storage Offerings, defined by administrator. provide a choice of disk size and IOPS (Quality of Service) for primary
 * data storage
 *
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "storage_offerings")
@SuppressWarnings("serial")
public class StorageOffering implements Serializable {
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
    @Size(min = 1, max = 30)
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * The domain ID this disk offering belongs to. Ignore this information as it is not currently applicable.
     */
    @JoinColumn(name = "domain_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Domain domain;

    /** Domain id of the Storage Offering. */
    @Column(name = "domain_id")
    private Long domainId;

    /**
     * Description of Storage Offering.
     */
    @NotEmpty
    @Column(name = "description", nullable = false)
    private String description;

    /**
     * Indicate whether the storage offering should be available all domains or only some domains. If checked, to make
     * it available to all domains. If not checked, to limit the scope to a sub domain
     */
    @Column(name = "is_public", columnDefinition = "tinyint default 1")
    private Boolean isPublic;

    /**
     * If checked, the user can set their own disk size. If not checked, the root administrator must define a value in
     * Disk size.
     */
    @Column(name = "is_custom_disk", nullable = false, columnDefinition = "tinyint default 0")
    private Boolean isCustomDisk;

    /**
     * Appears only if Custom disk size is not selected. Define the volume size in GB.
     */
    @Column(name = "disk_size", nullable = false)
    private Long diskSize;

    /**
     * The storage type for this storage offering, whether it is isolated or shared.
     */
    @Column(name = "storage_type")
    @Enumerated(EnumType.STRING)
    private StorageType storageType;

    /**
     * The tags for the Storage offering. The tags that should be associated with the primary storage for this disk.
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
     * If checked, the user can set their own IOPS. If not checked, the root administrator can define values.
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
     * The Provisioning Type of the disk offering.
     */
    @Column(name = "provisioning_type")
    @Enumerated(EnumType.STRING)
    private ProvisioningType provisioningType;

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
    @Column(name = "created_by")
    private Long createdBy;

    /** Last updated by user. */
    @LastModifiedBy
    @Column(name = "updated_by")
    private Long updatedBy;

    /** Created date and time. */
    @CreatedDate
    @Column(name = "created_date_time")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentZonedDateTime")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime createdDateTime;

    /** Last updated date and time. */
    @LastModifiedDate
    @Column(name = "updated_date_time")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentZonedDateTime")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime updatedDateTime;

    /** An active attribute is to check whether the role is active or not. */
    @Column(name = "is_active", columnDefinition = "tinyint default 1")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean isActive;

    /**
     * Storage offering price.
     */
    @OneToMany(cascade = CascadeType.ALL)
    @OrderBy("id DESC")
    private List<StorageOfferingCost> storagePrice;

    /**
     * isSyncFlag field is not to be serialized, whereas JPA's @Transient annotation is used to indicate that a field is
     * not to be persisted in the database.
     */
    @Transient
    private Boolean isSyncFlag;

    /** Transient domain of the instance. */
    @Transient
    private String transDomainId;

    /**
     * To set the default value while creating tables in database.
     */
    public StorageOffering() {
        super();
        this.isActive = true;
    }

    /**
     * Enum type for Storage Offering Status.
     *
     */
    public enum Status {
        /** Storage Offering will be in a Disabled State. */
        DISABLED,
        /** Storage Offering will be in a Enabled State. */
        ENABLED
    }

    /**
     * Enum type for Storage Type. Type of Disk for virtual machine.
     *
     */
    public enum StorageType {
        /** Isolated is attached to the hypervisor host where vm is running. */
        local,
        /** Shared is storage accessible via NFS. */
        shared
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
     * Enum type for Provisioning Type. Type used to create volumes. Valid values are thin, sparse, fat..
     *
     */
    public enum ProvisioningType {
        /** Thin provisioning type. */
        thin,
        /** Sparse provisioning type. */
        sparse,
        /** Fat provisioning type. */
        fat
    }

    /**
     * Enum type for Cache Mode Type.
     *
     */
    public enum CacheMode {
        /** None disk cache. */
        None,
        /** Write-back disk caching. */
        writeback,
        /** Write-through disk caching. */
        writethrough
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
     * Get the domain id of the Storage Offering.
     *
     * @return the domainId
     */
    public Long getDomainId() {
        return domainId;
    }

    /**
     * Set the domain id of the Storage Offering.
     *
     * @param domainId the domainId to set
     */
    public void setDomainId(Long domainId) {
        this.domainId = domainId;
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
     * Get the is Customized Iops of the storage offering.
     *
     * @return the isCustomizedIops of the storage offering
     */
    public Boolean getIsCustomizedIops() {
        return isCustomizedIops;
    }

    /**
     * Set the is Customized Iops of the storage offering.
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
     * Get the provisioning type of StorageOffering.
     *
     * @return the provisioning type
     */
    public ProvisioningType getProvisioningType() {
        return provisioningType;
    }

    /**
     * Set the provisioning type of StorageOffering.
     *
     * @param provisioningType the provisioning type to set
     */
    public void setProvisioningType(ProvisioningType provisioningType) {
        this.provisioningType = provisioningType;
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
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the created by of the storage offering.
     *
     * @param createdBy the created by to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updated by of the storage offering.
     *
     * @return the updatedBy of the storage offering
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updated by of the storage offering.
     *
     * @param updatedBy the updated by to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the created date time of the storage offering.
     *
     * @return the createdDateTime of the storage offering
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the created date time of the storage offering.
     *
     * @param createdDateTime the created date time to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updated date time of the storage offering.
     *
     * @return the updatedDateTime of the storage offering
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updated date time of the storage offering.
     *
     * @param updatedDateTime the updated date time to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get the storage price of the Storage Offering.
     *
     * @return the storagePrice of the Storage Offering.
     */
    public List<StorageOfferingCost> getStoragePrice() {
        return storagePrice;
    }

    /**
     * Set the storage price of the Storage Offering.
     *
     * @param storagePrice the storagePrice to set
     */
    public void setStoragePrice(List<StorageOfferingCost> storagePrice) {
        this.storagePrice = storagePrice;
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
     * Convert JSONObject to Storage Offering entity.
     *
     * @param storageMap json object
     * @return Storage Offering entity objects
     * @throws Exception error at Storage Offering
     */
    public static StorageOffering convert(JSONObject storageMap) throws Exception {
        StorageOffering storageOffering = new StorageOffering();
        storageOffering.setIsSyncFlag(false);
        try {
            storageOffering.setUuid(JsonUtil.getStringValue(storageMap, CloudStackConstants.CS_ID));
            storageOffering.setName(JsonUtil.getStringValue(storageMap, CloudStackConstants.CS_NAME));
            storageOffering.setDescription(JsonUtil.getStringValue(storageMap, CloudStackConstants.CS_DISPLAY_TEXT));
            if (storageMap.has(CloudStackConstants.CS_DISK_BYTES_READ)) {
                storageOffering.setDiskBytesReadRate(storageMap.getLong(CloudStackConstants.CS_DISK_BYTES_READ));
            }
            if (storageMap.has(CloudStackConstants.CS_DISK_BYTES_WRITE)) {
                storageOffering.setDiskBytesWriteRate(storageMap.getLong(CloudStackConstants.CS_DISK_BYTES_WRITE));
            }
            if (storageMap.has(CloudStackConstants.CS_DISK_IOPS_READ)) {
                storageOffering.setDiskIopsReadRate(storageMap.getLong(CloudStackConstants.CS_DISK_IOPS_READ));
            }
            if (storageMap.has(CloudStackConstants.CS_DISK_IOPS_WRITE)) {
                storageOffering.setDiskIopsWriteRate(storageMap.getLong(CloudStackConstants.CS_DISK_IOPS_WRITE));
            }
            if (storageMap.has(CloudStackConstants.CS_MAX_IOPS)) {
                storageOffering.setDiskMaxIops(storageMap.getLong(CloudStackConstants.CS_MAX_IOPS));
            }
            if (storageMap.has(CloudStackConstants.CS_MIN_IOPS)) {
                storageOffering.setDiskMinIops(storageMap.getLong(CloudStackConstants.CS_MIN_IOPS));
            }
            if (storageMap.has(CloudStackConstants.CS_CUSTOM_IOPS_STATUS)) {
                storageOffering.setIsCustomizedIops(storageMap.getBoolean(CloudStackConstants.CS_CUSTOM_IOPS_STATUS));
            }
            if (storageMap.has(CloudStackConstants.CS_DOMAIN_ID)) {
                storageOffering.setTransDomainId(JsonUtil.getStringValue(storageMap, CloudStackConstants.CS_DOMAIN_ID));
            }
            if (storageMap.has(CloudStackConstants.CS_TAGS)) {
                storageOffering.setStorageTags(storageMap.getString(CloudStackConstants.CS_TAGS));
            }
            if (storageMap.has(CloudStackConstants.CS_DISK_BYTES_READ) || storageMap.has(CloudStackConstants.CS_DISK_BYTES_WRITE)
                    || storageMap.has(CloudStackConstants.CS_DISK_IOPS_READ) || storageMap.has(CloudStackConstants.CS_DISK_IOPS_WRITE)) {
                storageOffering.setQosType(QosType.Hypervisor);
            }
            if (storageMap.has(CloudStackConstants.CS_MAX_IOPS) || storageMap.has(CloudStackConstants.CS_MIN_IOPS) || storageMap.has(CloudStackConstants.CS_CUSTOM_IOPS_STATUS)) {
                storageOffering.setQosType(QosType.Storage);
            }
            if (storageMap.has(CloudStackConstants.CS_PROVISIONING_TYPE)) {
                storageOffering.setProvisioningType(storageOffering.getProvisioningType().valueOf(JsonValidator.jsonStringValidation(storageMap, CloudStackConstants.CS_PROVISIONING_TYPE)));
            }
            storageOffering.setDiskSize(storageMap.getLong(CloudStackConstants.CS_DISK_SIZE));
            storageOffering.setIsCustomDisk(storageMap.getBoolean(CloudStackConstants.CS_CUSTOM_STATUS));
            storageOffering
                    .setStorageType(StorageType.valueOf(JsonValidator.jsonStringValidation(storageMap, CloudStackConstants.CS_STORAGE_TYPE)));
            storageOffering.setCreatedDateTime(JsonUtil.convertToZonedDateTime(storageMap.getString(CloudStackConstants.CS_CREATED)));
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return storageOffering;
    }

    /**
     * Get the sync flag.
     *
     * @return the isSyncFlag.
     */
    public Boolean getIsSyncFlag() {
        return isSyncFlag;
    }

    /**
     * Set the Sync Flag.
     *
     * @param isSyncFlag of the isSyncFlag to set.
     */
    public void setIsSyncFlag(Boolean isSyncFlag) {
        this.isSyncFlag = isSyncFlag;
    }

    /**
     * Mapping Storage Offering entity object in list.
     *
     * @param storageOfferingList Storage Offering lists of Storage Offering
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
