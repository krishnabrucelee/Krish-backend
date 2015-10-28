package ck.panda.domain.entity;

import java.io.Serializable;
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
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

/**
 *  Service offering is a set of virtual hardware features such as CPU core count and speed,
 *  memory, and disk size.
 */

@Entity
@Table(name = "ck_service_offerings")
@SuppressWarnings("serial")

public class ComputeOffering implements Serializable {


    /** The id of the Compute offering table. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Unique id for the Compute Offering. */
    @Column(name = "uuid")
    private String uuid;

    /** The name of the Compute offering. */
    @Column(name = "name")
    private String name;

    /** The number of CPU cores needed. */
    //@Size(min = 1, max = 200000)
    @Column(name = "number_of_cores")
    private Integer numberOfCores;

    /** The clock rate of CPU speed in MHz. */
    //@Size(min = 1, max=1000)
    @Column(name = "clock_speed")
    private Integer clockSpeed;

    /** Description about Compute offering. */
    @NotEmpty
    @Column(name = "display_text", nullable = false)
    private String displayText;

    /** The hostTags for the Compute offering. */
    @Column(name = "host_tags")
    private String hostTags;

    /** The storage Tags for the Compute offering. */
    @Column(name = "storage_tags")
    private String storageTags;

    /** The hostTags for the Compute offering. */
    @Column(name = "qos_type")
    private String qosType;

    /** Restrict the CPU usage to committed Compute offering. */
    @Column(name = "cpu_capacity")
    private Boolean cpuCapacity;

    /** The CPU memory in Mebi Bytes Per Second. */
    //@Size(min = 32)
    @Column(name = "memory")
    private Integer memory;

    /** The Disk bytesReadRate for the Compute offering. */
    @Column(name = "disk_bytes_read_rate")
    private Integer diskBytesReadRate;

    /** The Disk bytesWriteRate for the Compute offering. */
    @Column(name = "disk_bytes_write_rate")
    private Integer diskBytesWriteRate;

    /** The Disk iopsReadRate for the Compute offering. */
    @Column(name = "disk_iops_read_rate")
    private Integer diskIopsReadRate;

    /** Network data transfer rate in megabits per second allowed. */
    //@Size(min = 1)
    @Column(name = "network_rate")
    private Integer networkRate;

    /** Minimum input output per second. */
    @Column(name = "min_iops")
    private Integer minIops;

    /** Maximum input output per second. */
    @Column(name = "max_iops")
    private Integer maxIops;

    /** The high Availability Enabled support in the Compute offering. */
    @Column(name = "is_high_availability_enabled")
    private Boolean isHighAvailabilityEnabled;

    /** Is public it is a offering. */
    @Column(name = "is_public")
    private Boolean isPublic;

    /** Is this offering is active. */
    @Column(name = "is_Active")
    private Boolean isActive;

    /** Is this offering is custom. */
    @Column(name = "is_Custom")
    private Boolean isCustom;

    /** Domain id for this offering. */
    @JoinColumn(name = "domain_id", referencedColumnName = "id" ,insertable=false, updatable=false)
    @ManyToOne
    private Domain domain;

   @Column(name="domain_id")
    private Long domainId;


    /** The Disk iopsWriteRate for the Compute offering. */
    @Column(name = "disk_iops_write_rate")
    private Integer diskIopsWriteRate;

    /**
     * The storage type local, shared for this Compute offering. */
    @Column(name = "storage_type")
    @Enumerated(EnumType.STRING)
    private StorageType storageType;

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
    private DateTime createdDateTime;

    /** Last modified date and time. */
    @LastModifiedDate
    private DateTime lastModifiedDateTime;

    /** Version attribute to handle optimistic locking. */
    @Version
    @Column(name = "version")
    private Long version;

    /** Status attribute to verify Active or Inactive. */
    @Column(name = "status")
    private Boolean status;

    /** Disk input and output is average or good or excellent. */
    @Column(name = "disk_io")
    @Enumerated(EnumType.STRING)
    private DiskIo diskIo;

    /** Zone id for this offering. */
    @JoinColumn(name = "zone_id", referencedColumnName = "id",insertable = false, updatable = false)
    @OneToOne
    private Zone zone;

    /** id of the zone.*/
    @Column(name = "zone_id")
    private Long zoneId;

    /**
     * Enumeration for Region status.
     */
    public enum DiskIo {

           /** If region is enabled we can create zones and pods. */
           AVERAGE,

           /** If region is disabled cannot create any zones and pods until region gets enabled. */
           GOOD,

           /** If region is deleted we cannot create zones and pods. */
           EXCELLENT
    }

    /**
     * Enumeration for Region status.
     */
    public enum StorageType {

           /** If region is enabled we can create zones and pods. */
           SHARED,

           /** If region is disabled cannot create any zones and pods until region gets enabled. */
           LOCAL,

    }
    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the numberOfCores
     */
    public Integer getNumberOfCores() {
        return numberOfCores;
    }

    /**
     * @return the clockSpeed
     */
    public Integer getClockSpeed() {
        return clockSpeed;
    }

    /**
     * @return the displayText
     */
    public String getDisplayText() {
        return displayText;
    }

    /**
     * @return the hostTags
     */
    public String getHostTags() {
        return hostTags;
    }

    /**
     * @return the cpuCapacity
     */
    public Boolean getCpuCapacity() {
        return cpuCapacity;
    }

    /**
     * @return the memory
     */
    public Integer getMemory() {
        return memory;
    }

    /**
     * @return the diskBytesReadRate
     */
    public Integer getDiskBytesReadRate() {
        return diskBytesReadRate;
    }

    /**
     * @return the diskBytesWriteRate
     */
    public Integer getDiskBytesWriteRate() {
        return diskBytesWriteRate;
    }

    /**
     * @return the diskIopsReadRate
     */
    public Integer getDiskIopsReadRate() {
        return diskIopsReadRate;
    }

    /**
     * @return the networkRate
     */
    public Integer getNetworkRate() {
        return networkRate;
    }

    /**
     * @return the isHighAvailabilityEnabled
     */
    public Boolean getIsHighAvailabilityEnabled() {
        return isHighAvailabilityEnabled;
    }

    /**
     * @return the diskIopsWriteRate
     */
    public Integer getDiskIopsWriteRate() {
        return diskIopsWriteRate;
    }

    /**
     * @return the createdBy
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * @return the updatedBy
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param numberOfCores the numberOfCores to set
     */
    public void setNumberOfCores(Integer numberOfCores) {
        this.numberOfCores = numberOfCores;
    }

    /**
     * @param clockSpeed the clockSpeed to set
     */
    public void setClockSpeed(Integer clockSpeed) {
        this.clockSpeed = clockSpeed;
    }

    /**
     * @param displayText the displayText to set
     */
    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    /**
     * @param hostTags the hostTags to set
     */
    public void setHostTags(String hostTags) {
        this.hostTags = hostTags;
    }

    /**
     * @param cpuCapacity the cpuCapacity to set
     */
    public void setCpuCapacity(Boolean cpuCapacity) {
        this.cpuCapacity = cpuCapacity;
    }

    /**
     * @param memory the memory to set
     */
    public void setMemory(Integer memory) {
        this.memory = memory;
    }

    /**
     * @param diskBytesReadRate the diskBytesReadRate to set
     */
    public void setDiskBytesReadRate(Integer diskBytesReadRate) {
        this.diskBytesReadRate = diskBytesReadRate;
    }

    /**
     * @param diskBytesWriteRate the diskBytesWriteRate to set
     */
    public void setDiskBytesWriteRate(Integer diskBytesWriteRate) {
        this.diskBytesWriteRate = diskBytesWriteRate;
    }

    /**
     * @param diskIopsReadRate the diskIopsReadRate to set
     */
    public void setDiskIopsReadRate(Integer diskIopsReadRate) {
        this.diskIopsReadRate = diskIopsReadRate;
    }

    /**
     * @param networkRate the networkRate to set
     */
    public void setNetworkRate(Integer networkRate) {
        this.networkRate = networkRate;
    }

    /**
     * @param isHighAvailabilityEnabled the isHighAvailabilityEnabled to set
     */
    public void setIsHighAvailabilityEnabled(Boolean isHighAvailabilityEnabled) {
        this.isHighAvailabilityEnabled = isHighAvailabilityEnabled;
    }

    /**
     * @param diskIopsWriteRate the diskIopsWriteRate to set
     */
    public void setDiskIopsWriteRate(Integer diskIopsWriteRate) {
        this.diskIopsWriteRate = diskIopsWriteRate;
    }

    /**
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @param updatedBy the updatedBy to set
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * @return the isPublic
     */
    public Boolean getIsPublic() {
        return isPublic;
    }

   /**
     * @return the domainId
     */
    public Long getDomainId() {
        return domainId;
    }

    /**
     * @param isPublic the isPublic to set
     */
    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    /**
     * @param domainId the domainId to set
     */
    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    /**
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }


    /**
     * @return the diskIo
     */
    public DiskIo getDiskIo() {
        return diskIo;
    }

    /**
     * @param diskIo the diskIo to set
     */
    public void setDiskIo(DiskIo diskIo) {
        this.diskIo = diskIo;
    }

    /**
     * @return the createdDateTime
     */
    public DateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * @return the lastModifiedDateTime
     */
    public DateTime getLastModifiedDateTime() {
        return lastModifiedDateTime;
    }

    /**
     * @param createdDateTime the createdDateTime to set
     */
    public void setCreatedDateTime(DateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * @param lastModifiedDateTime the lastModifiedDateTime to set
     */
    public void setLastModifiedDateTime(DateTime lastModifiedDateTime) {
        this.lastModifiedDateTime = lastModifiedDateTime;
    }

    /**
     * @return the status
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }

    /**
     * @return the storageTags
     */
    public String getStorageTags() {
        return storageTags;
    }

    /**
     * @return the qosType
     */
    public String getQosType() {
        return qosType;
    }

    /**
     * @return the minIops
     */
    public Integer getMinIops() {
        return minIops;
    }

    /**
     * @return the maxIops
     */
    public Integer getMaxIops() {
        return maxIops;
    }

    /**
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * @param storageTags the storageTags to set
     */
    public void setStorageTags(String storageTags) {
        this.storageTags = storageTags;
    }

    /**
     * @param qosType the qosType to set
     */
    public void setQosType(String qosType) {
        this.qosType = qosType;
    }

    /**
     * @param minIops the minIops to set
     */
    public void setMinIops(Integer minIops) {
        this.minIops = minIops;
    }

    /**
     * @param maxIops the maxIops to set
     */
    public void setMaxIops(Integer maxIops) {
        this.maxIops = maxIops;
    }

    /**
     * @param isActive the isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * @return the isCustom
     */
    public Boolean getIsCustom() {
        return isCustom;
    }

    /**
     * @param isCustom the isCustom to set
     */
    public void setIsCustom(Boolean isCustom) {
        this.isCustom = isCustom;
    }



    /**
     * @return the domain
     */
    public Domain getDomain() {
        return domain;
    }

    /**
     * @param domain the domain to set
     */
    public void setDomain(Domain domain) {
        this.domain = domain;
    }


    /**
     * @return the zone
     */
    public Zone getZone() {
        return zone;
    }

    /**
     * @param zone the zone to set
     */
    public void setZone(Zone zone) {
        this.zone = zone;
    }

    /**
     * @return the zoneId
     */
    public Long getZoneId() {
        return zoneId;
    }

    /**
     * @param zoneId the zoneId to set
     */
    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * @return the storageType
     */
    public StorageType getStorageType() {
        return storageType;
    }

    /**
     * @param storageType the storageType to set
     */
    public void setStorageType(StorageType storageType) {
        this.storageType = storageType;
    }


    /**
     * Convert JSONObject to domain entity.
     *
     * @param object json object
     * @return domain entity object.
     * @throws JSONException handles json exception.
     */
    public static ComputeOffering convert(JSONObject object) throws JSONException {
        ComputeOffering compute = new ComputeOffering();
        compute.uuid = object.get("id").toString();
        compute.name = object.get("name").toString();
        compute.displayText = object.get("displaytext").toString();
        return compute;
    }


    /**
     * Mapping entity object into list.
     *
     * @param computeOfferingList list of domains.
     * @return computeOffering map
     */
    public static Map<String, ComputeOffering> convert(List<ComputeOffering> computeOfferingList) {
        Map<String, ComputeOffering> computeOfferingMap = new HashMap<String, ComputeOffering>();

        for (ComputeOffering computeOffering : computeOfferingList) {
            computeOfferingMap.put(computeOffering.getUuid(), computeOffering);
        }

        return computeOfferingMap;
    }
 }
