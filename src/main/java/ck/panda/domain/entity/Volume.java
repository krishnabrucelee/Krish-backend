/**
 *
 */
package ck.panda.domain.entity;

import java.time.ZonedDateTime;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;
import ck.panda.util.ConvertUtil;
import ck.panda.util.JsonUtil;
import ck.panda.util.JsonValidator;

/**
 * Secondary storage Entity.
 *
 */
@Entity
@Table(name = "ck_volume")
public class Volume {

    /** Unique ID of the volume. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /**  Unique ID from Cloud Stack. */
    @Column(name = "uuid")
    private String uuid;

    /** A desired name of the volume. */
    @NotEmpty
    @Size(min = 4, max = 20)
    @Column(name = "name", nullable = false)
    private String name;

    /**  Type of disk. Either rook or data disk. */
    @Column(name = "volume_type", nullable = false)
    private VolumeType volumeType;

    /**
     * The Zone ID, this disk offering belongs to. Ignore this information as it
     * is not currently applicable.
     */
    @JoinColumn(name = "zone_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Zone zone;

    /** Volume zone id. */
    @Column(name = "zone_id")
    private Long zoneId;

    /** Plan to choose storage offering. */
    @JoinColumn(name = "storage_offer_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private StorageOffering storageOffering;

    /** Volume disk offer id. */
    @Column(name = "storage_offer_id")
    private Long storageOfferingId;

    /**
     * Appears only if Custom disk size is not selected. Define the volume size
     * in GB.
     */
    @Column(name = "disk_size", nullable = false)
    private Long diskSize;

    /** The maximum iops of the disk offering. */
    @Column(name = "max_iops")
    private Long diskMaxIops;

    /** The minimum iops of the disk offering. */
    @Column(name = "min_iops")
    private Long diskMinIops;

    /** Status attribute to verify status of the volume. */
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
    private ZonedDateTime createdDateTime;

    /** Last modified date and time. */
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
     * isSyncFlag field is not to be serialized,
     * whereas JPA's @Transient annotation is used to indicate
     * that a field is not to be persisted in the database.
     */
    @Transient
    private Boolean isSyncFlag;

    /** Enum type for disk Status. */
    public enum VolumeType {
        /** Volume will be in a DATADISK type. */
        DATADISK,
        /** Volume will be in a ROOT type. */
        ROOT,
    }

    /** Enum type for disk Status. */
    public enum Status {
        /** Volume will be in a Allocated State. */
        Allocated,
        /** Volume will be in a Destroy State. */
        Destroy,
        /** Volume will be in a Expunged State. */
        Expunged,
        /** Volume will be in a Ready State. */
        Ready
    }

    /**
     * Get the id of the Volume.
     *
     * @return the id of the Volume
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id of the Volume.
     *
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the uuid of the Volume.
     *
     * @return the uuid of the Volume
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the uuid of the Volume.
     *
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get the name of the Volume.
     *
     * @return the name of the Volume
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the Volume.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Get the volumeType of the Volume.
     *
     * @return the volumeType of the Volume
     */
    public VolumeType getVolumeType() {
        return volumeType;
    }

    /**
     * Set the volumeType of the Volume.
     *
     * @param volumeType the volumeType to set
     */
    public void setVolumeType(VolumeType volumeType) {
        this.volumeType = volumeType;
    }

    /**
     * Get the zone of the Volume.
     *
     * @return the zone of the Volume
     */
    public Zone getZone() {
        return zone;
    }

    /**
     * Set the zone of the Volume.
     *
     * @param zone the zone to set
     */
    public void setZone(Zone zone) {
        this.zone = zone;
    }

    /**
     * Get the zoneId of the Volume.java.
     *
     * @return the zoneId of the Volume.java
     */
    public Long getZoneId() {
        return zoneId;
    }

    /**
     * Set the zoneId of the Volume.java.
     *
     * @param zoneId the zoneId to set
     */
    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * Get the storageOffering of the Volume.java.
     *
     * @return the storageOffering of the Volume.java
     */
    public StorageOffering getStorageOffering() {
        return storageOffering;
    }

    /**
     * Set the storageOffering of the Volume.java.
     *
     * @param storageOffering the storageOffering to set
     */
    public void setStorageOffering(StorageOffering storageOffering) {
        this.storageOffering = storageOffering;
    }

    /**
     * Get the storageOfferingId of the Volume.java.
     *
     * @return the storageOfferingId of the Volume.java
     */
    public Long getStorageOfferingId() {
        return storageOfferingId;
    }

    /**
     * Set the storageOfferingId of the Volume.java.
     *
     * @param storageOfferingId the storageOfferingId to set
     */
    public void setStorageOfferingId(Long storageOfferingId) {
        this.storageOfferingId = storageOfferingId;
    }

    /**
     * Get the disk size of the Volume.
     *
     * @return the diskSize of the Volume
     */
    public Long getDiskSize() {
        return diskSize;
    }

    /**
     * Set the disk size of the Volume.
     *
     * @param diskSize the disk size to set
     */
    public void setDiskSize(Long diskSize) {
        this.diskSize = diskSize;
    }

    /**
     * Get the disk max iops of the Volume.
     *
     * @return the diskMaxIops of the Volume
     */
    public Long getDiskMaxIops() {
        return diskMaxIops;
    }

    /**
     * Set the disk max iops of the Volume.
     *
     * @param diskMaxIops the disk max iops to set
     */
    public void setDiskMaxIops(Long diskMaxIops) {
        this.diskMaxIops = diskMaxIops;
    }

    /**
     * Get the disk min iops of the Volume.
     *
     * @return the diskMinIops of the Volume
     */
    public Long getDiskMinIops() {
        return diskMinIops;
    }

    /**
     * Set the disk min iops of the Volume.
     *
     * @param diskMinIops the disk min iops to set
     */
    public void setDiskMinIops(Long diskMinIops) {
        this.diskMinIops = diskMinIops;
    }

    /**
     * Get the status of the Volume.
     *
     * @return the status of the Volume
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set the status of the Volume.
     *
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Get the version of the Volume.
     *
     * @return the version of the Volume
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Set the version of the Volume.
     *
     * @param version the version to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get the created by of the Volume.
     *
     * @return the createdBy of the Volume
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the created by of the Volume.
     *
     * @param createdBy the created by to set
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updated by of the Volume.
     *
     * @return the updatedBy of the Volume
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updated by of the Volume.
     *
     * @param updatedBy the updated by to set
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the created date time of the Volume.
     *
     * @return the createdDateTime of the Volume
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the created date time of the Volume.
     *
     * @param createdDateTime the created date time to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the update date time of the Volume.
     *
     * @return the updatedDateTime of the Volume
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updated date time of the Volume.
     *
     * @param updatedDateTime the update date time to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get the is active of the Volume.
     *
     * @return the isActive of the Volume
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set the is active of the Volume.
     *
     * @param isActive the is active to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
     * Convert JSONObject to Volume entity.
     *
     * @param object json object
     * @param convertUtil util class for converting json
     * @return Volume entity objects
     * @throws JSONException unhandled json errors
     */
    @SuppressWarnings("static-access")
    public static Volume convert(JSONObject object, ConvertUtil convertUtil) throws JSONException {
        Volume volume = new Volume();
        volume.setIsSyncFlag(false);
        try {
            volume.uuid = JsonValidator.jsonStringValidation(object, "id");
            volume.name = JsonValidator.jsonStringValidation(object, "name");
            volume.diskSize = object.getLong("size");
            volume.setVolumeType(volume.getVolumeType().valueOf(JsonValidator.jsonStringValidation(object, "type")));
            volume.setStatus(volume.getStatus().valueOf(JsonValidator.jsonStringValidation(object, "state")));
         //   volume.setCreatedDateTime(volume.getCreatedDateTime().);
            volume.setStorageOfferingId(convertUtil.getStorageOfferId(JsonUtil.getStringValue(object, "diskofferingid")));
            volume.setZoneId(convertUtil.getZoneId(JsonUtil.getStringValue(object, "zoneid")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return volume;
    }

    /**
     * Mapping Volume entity object in list.
     *
     * @param volumeList Volume lists of Storage Offering
     * @return volumeMap mapped values.
     */
    public static Map<String, Volume> convert(List<Volume> volumeList) {
        Map<String, Volume> volumeMap = new HashMap<String, Volume>();

        for (Volume volume : volumeList) {
            volumeMap.put(volume.getUuid(), volume);
        }
        return volumeMap;
    }
}
