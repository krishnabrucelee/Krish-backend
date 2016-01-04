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
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import ck.panda.util.JsonUtil;
import ck.panda.util.JsonValidator;

/**
 * Secondary storage Entity.
 *
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
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

    /** Domain of the Volume. */
    @JoinColumn(name = "domain_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Domain domain;

    /** Domain id of the Volume. */
    @Column(name = "domain_id")
    private Long domainId;

    /** Department of the Volume. */
    @JoinColumn(name = "department_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Department department;

    /** Department id of the Volume. */
    @Column(name = "department_id")
    private Long departmentId;

    /** Project of the Volume. */
    @JoinColumn(name = "project_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Project project;

    /** Project id of the Volume. */
    @Column(name = "project_id")
    private Long projectId;

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
    @Column(name = "disk_size")
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

    /** Format attribute to verify status of the volume. */
    @Column(name = "format")
    @Enumerated(EnumType.STRING)
    private Format format;

    /** URL of the volume. */
    @Column(name = "url")
    private String url;

    /** MD5 checksum for the volume. */
    @Column(name = "checksum")
    private String checksum;

    /** Volume event message. */
    @Column(name = "event_message")
    private String eventMessage;

    /** Version attribute to handle optimistic locking. */
    @Version
    @Column(name = "version")
    private Long version;

    /** Instance volume id. */
    @JoinColumn(name = "instance_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private VmInstance vmInstance;

    /** Instance id for disk. */
    @Column(name = "instance_id")
    private Long vmInstanceId;

    /** Check volume available or not. */
    @Column(name = "is_removed")
    private Boolean isRemoved;

    /** Check volume Shrink ok or not. */
    @Column(name = "is_shrink")
    private Boolean isShrink;

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

    /** Transient network of the instance. */
    @Transient
    private String transvmInstanceId;

    /** Transient network of the instance. */
    @Transient
    private String transZoneId;

    /** Transient network of the instance. */
    @Transient
    private String transStorageOfferingId;

    /** Transient network of the Domain. */
    @Transient
    private String transDomainId;

    /** Transient network of the Department. */
    @Transient
    private String transDepartmentId;

    /** Transient project of the volume. */
    @Transient
    private String transProjectId;

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
        ALLOCATED,
        /** Volume will be in a Destroy State. */
        DESTROY,
        /** Volume will be in a Expunged State. */
        EXPUNGED,
        /** Volume will be in a Ready State. */
        READY,
        /** Volume will be in a UploadNotStarted State. */
        UPLOAD_NOT_STARTED,
        /** The volume upload operation is in progress or in short the volume is on secondary storage. */
        UPLOAD_OP,
        /** Volume will be in a UploadAbandoned State. */
        UPLOAD_ABANDONED,
        /** Volume will be in a UploadError State. */
        UPLOAD_ERROR,
        /** Volume will be in a Abandoned State. */
        ABANDONED,
        /** Volume will be in a Uploaded State. */
        UPLOADED,
        /** Volume will be in a Upload State. */
        UPLOAD
    }

    /** Format enum type used to list the static format values. */
    public enum Format {
        /** Hypervisor format type as RAW. */
        RAW,
        /** Hypervisor format type as VHD. */
        VHD,
        /** Hypervisor format type as VHDX. */
        VHDX,
        /** Hypervisor format type as OVA. */
        OVA,
        /** Hypervisor format type as QCOW2. */
        QCOW2
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
     * Get the domain of the Volume.

     * @return the domain of Volume.
     */
    public Domain getDomain() {
        return domain;
    }

    /**
     * Set the domain of the Volume.
     *
     * @param domain the domain to set
     */
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    /**
     * Get the domainId of the Volume.

     * @return the domainId of Volume.
     */
    public Long getDomainId() {
        return domainId;
    }

    /**
     * Set the domainId of the Volume.
     *
     * @param domainId the domainId to set
     */
    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    /**
     * Get the department of the Volume.

     * @return the department of Volume.
     */
    public Department getDepartment() {
        return department;
    }

    /**
     * Set the department of the Volume.
     *
     * @param department the department to set
     */
    public void setDepartment(Department department) {
        this.department = department;
    }

    /**
     * Get the departmentId of the Volume.

     * @return the departmentId of Volume.
     */
    public Long getDepartmentId() {
        return departmentId;
    }

    /**
     * Set the departmentId of the Volume.
     *
     * @param departmentId the departmentId to set
     */
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * Get the project of the Volume.

     * @return the project of Volume.
     */
    public Project getProject() {
        return project;
    }

    /**
     * Set the project of the Volume.
     *
     * @param project the project to set
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * Get the projectId of the Volume.

     * @return the projectId of Volume.
     */
    public Long getProjectId() {
        return projectId;
    }

    /**
     * Set the projectId of the Volume.
     *
     * @param projectId the projectId to set
     */
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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
     * Get the format of the Volume.

     * @return the format of Volume.
     */
    public Format getFormat() {
        return format;
    }

    /**
     * Set the format of the Volume.
     *
     * @param format the format to set
     */
    public void setFormat(Format format) {
        this.format = format;
    }

    /**
     * Get the url of the Volume.

     * @return the url of Volume.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Set the url of the Volume.
     *
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Get the checksum of the Volume.

     * @return the checksum of Volume.
     */
    public String getChecksum() {
        return checksum;
    }

    /**
     * Set the checksum of the Volume.
     *
     * @param checksum the checksum to set
     */
    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    /**
     * Get the eventMessage of the Volume.

     * @return the eventMessage of Volume.
     */
    public String getEventMessage() {
        return eventMessage;
    }

    /**
     * Set the eventMessage of the Volume.
     *
     * @param eventMessage the eventMessage to set
     */
    public void setEventMessage(String eventMessage) {
        this.eventMessage = eventMessage;
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
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the created by of the Volume.
     *
     * @param createdBy the created by to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updated by of the Volume.
     *
     * @return the updatedBy of the Volume
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updated by of the Volume.
     *
     * @param updatedBy the updated by to set
     */
    public void setUpdatedBy(Long updatedBy) {
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
     * Get the instance.
     *
     * @return the vminstance
     */
    public VmInstance getVmInstance() {
        return vmInstance;
    }

    /**
     * Set the vminstance.
     *
     * @param vmInstance to set
     */
    public void setVmInstance(VmInstance vmInstance) {
        this.vmInstance = vmInstance;
    }

    /**
     * Get instance Id.
     *
     * @return the vmInstanceId
     */
    public Long getVmInstanceId() {
        return vmInstanceId;
    }

    /**
     * Set the vmInstanceId .
     *
     * @param vmInstanceId to set
     */
    public void setVmInstanceId(Long vmInstanceId) {
        this.vmInstanceId = vmInstanceId;
    }

    /**
     * Get the is removed of the Volume.

     * @return the isRemoved of Volume.
     */
    public Boolean getIsRemoved() {
        return isRemoved;
    }

    /**
     * Set the is removed of the Volume.
     *
     * @param isRemoved the isRemoved to set
     */
    public void setIsRemoved(Boolean isRemoved) {
        this.isRemoved = isRemoved;
    }

    /**
     * Get the is Shrink of the Volume.

     * @return the isShrink of Volume.
     */
    public Boolean getIsShrink() {
        return isShrink;
    }

    /**
     * Set the is Shrink of the Volume.
     *
     * @param isShrink the isShrink to set
     */
    public void setIsShrink(Boolean isShrink) {
        this.isShrink = isShrink;
    }

    /**
     * Get the Transient VM Instance Id.
     *
    * @return the transvmInstanceId
    */
    public String getTransvmInstanceId() {
        return transvmInstanceId;
    }

    /**
     * Set the transvmInstanceId .
     *
     * @param transvmInstanceId to set
     */
    public void setTransvmInstanceId(String transvmInstanceId) {
        this.transvmInstanceId = transvmInstanceId;
    }

    /**
     * Get the transZoneId.
     *
     * @return the transZoneId
     */
    public String getTransZoneId() {
        return transZoneId;
    }

    /**
     * Get the transZoneId.
     *
     * @param transZoneId  to set
     */
    public void setTransZoneId(String transZoneId) {
        this.transZoneId = transZoneId;
    }

    /**
     * Get transStorageOfferingId.
     *
     * @return the transStorageOfferingId
     */
    public String getTransStorageOfferingId() {
        return transStorageOfferingId;
    }

    /**
     * Set the transStorageOfferingId.
     *
     * @param transStorageOfferingId  to set
     */
    public void setTransStorageOfferingId(String transStorageOfferingId) {
        this.transStorageOfferingId = transStorageOfferingId;
    }

    /**
     * Get the transDomainId of the Volume.

     * @return the transDomainId of Volume.
     */
    public String getTransDomainId() {
        return transDomainId;
    }

    /**
     * Set the transDomainId of the Volume.
     *
     * @param transDomainId the transDomainId to set
     */
    public void setTransDomainId(String transDomainId) {
        this.transDomainId = transDomainId;
    }

    /**
     * Get the transDepartmentId of the Volume.

     * @return the transDepartmentId of Volume.
     */
    public String getTransDepartmentId() {
        return transDepartmentId;
    }

    /**
     * Set the transDepartmentId of the Volume.
     *
     * @param transDepartmentId the transDepartmentName to set
     */
    public void setTransDepartmentId(String transDepartmentId) {
        this.transDepartmentId = transDepartmentId;
    }

    /**
     * Get the transProjectId of the Volume.

     * @return the transProjectId of Volume.
     */
    public String getTransProjectId() {
        return transProjectId;
    }

    /**
     * Set the transProjectId of the Volume.
     *
     * @param transProjectId the transProjectId to set
     */
    public void setTransProjectId(String transProjectId) {
        this.transProjectId = transProjectId;
    }

    /**
     * Convert JSONObject to Volume entity.
     *
     * @param object json object
     * @return Volume entity objects
     * @throws JSONException unhandled json errors
     */
    @SuppressWarnings("static-access")
    public static Volume convert(JSONObject object) throws JSONException {
        Volume volume = new Volume();
        volume.setIsSyncFlag(false);
        try {
            volume.uuid = JsonValidator.jsonStringValidation(object, "id");
            volume.name = JsonValidator.jsonStringValidation(object, "name");
            volume.setDiskSize(object.getLong("size"));
            volume.setIsActive(true);
            volume.setVolumeType(volume.getVolumeType().valueOf(JsonValidator.jsonStringValidation(object, "type")));
            if (JsonValidator.jsonStringValidation(object, "state").equals("UploadNotStarted")) {
                volume.setStatus(volume.getStatus().UPLOAD_NOT_STARTED);
            } else if (JsonValidator.jsonStringValidation(object, "state").equals("UploadOp")) {
                volume.setStatus(volume.getStatus().UPLOAD_OP);
            } else if (JsonValidator.jsonStringValidation(object, "state").equals("UploadAbandoned")) {
                volume.setStatus(volume.getStatus().UPLOAD_ABANDONED);
            } else if (JsonValidator.jsonStringValidation(object, "state").equals("UploadError")) {
                volume.setStatus(volume.getStatus().UPLOAD_ERROR);
            } else {
                volume.setStatus(volume.getStatus().valueOf(JsonValidator.jsonStringValidation(object, "state").toUpperCase()));
            }
            volume.setCreatedDateTime(JsonUtil.convertToZonedDateTime(object.getString("created")));
            volume.setTransStorageOfferingId((JsonUtil.getStringValue(object, "diskofferingid")));
            volume.setTransZoneId((JsonUtil.getStringValue(object, "zoneid")));
            volume.setTransvmInstanceId((JsonUtil.getStringValue(object, "virtualmachineid")));
            volume.setTransDomainId((JsonUtil.getStringValue(object, "domainid")));
            volume.setTransDepartmentId((JsonUtil.getStringValue(object, "account")));
            volume.setTransProjectId(JsonUtil.getStringValue(object, "projectid"));
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
