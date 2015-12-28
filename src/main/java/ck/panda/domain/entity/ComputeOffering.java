package ck.panda.domain.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import ck.panda.util.JsonUtil;

/**
 *  A service offering is a set of virtual hardware features such as CPU core count and speed, memory, and disk size.
 *  The CloudStack administrator can set up various offerings, and then end users choose from the
 *  available offerings when they create a new VM.
 *
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
    @Column(name = "number_of_cores")
    private Integer numberOfCores;

    /** The clock rate of CPU speed in MHz. */
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
    @Enumerated(EnumType.STRING)
    private QosType qosType;

    /** Restrict the CPU usage to committed Compute offering. */
    @Column(name = "cpu_capacity")
    private Boolean cpuCapacity;

    /** Temporary variable. */
    @Transient
    private Boolean isSyncFlag;

    /** The CPU memory in Mebi Bytes Per Second. */
    @Column(name = "memory")
    private Integer memory;

    /** The Disk Bytes read rate.  */
    @Column(name = "disk_bytes_read_rate")
    private Integer diskBytesReadRate;

    /** The Disk bytes Write Rate . */
    @Column(name = "disk_bytes_write_rate")
    private Integer diskBytesWriteRate;

    /** The Disk Input and Output write rate per second. */
    @Column(name = "disk_iops_read_rate")
    private Integer diskIopsReadRate;

    /** Network data transfer rate in megabits per second allowed. */
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
    @Column(name = "customized")
    private Boolean customized;

    /** Is this offering consists of customized iops. */
    @Column(name = "customized_iops")
    private Boolean customizedIops;

    /** Reference Domain id for this offering. */
    @JoinColumn(name = "domain_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne
    private Domain domain;

    /**  Domain id for offer. */
   @Column(name = "domain_id")
    private Long domainId;


    /** The Disk Input and Output write rate per second. */
    @Column(name = "disk_iops_write_rate")
    private Integer diskIopsWriteRate;

    /**
     * The storage type local, shared for this Compute offering. */
    @Column(name = "storage_type")
    @Enumerated(EnumType.STRING)
    private StorageType storageType;

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

    /** cost of the compute offering. */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "compute_id")
    private List<ComputeOfferingCost> computeCost;

    /**
     * Enumeration for Region status.
     */
    public enum DiskIo {

           /** If average disk input and speed at is better level. */
           AVERAGE,

           /** If good disk input and output speed is above average level. */
           GOOD,

           /** If excellent disk input and output speed is at highest level. */
           EXCELLENT
    }

    /**
     * Enumeration for Storage Type status.
     */
    public enum StorageType {

           /** If shared is selected we can create instance without enabling zone to use local storage. */
           shared,

           /** If zone is enabled to access local storage then only we can create vm using this option. */
           local,
    }

    /**
     * Enumeration for QOS type status.
     */
    public enum QosType {

           /** If hypervisor is chosed we can specify disk bytes read and write bytes value. */
           HYPERVISOR,

           /** If storage is chosed we can specify minimum and maximum iops values. */
           STORAGE,
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Get UUID.
     *
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Get name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Get number of cores.
     *
     * @return the numberOfCores
     */
    public Integer getNumberOfCores() {
        return numberOfCores;
    }

    /**
     * Get the clock speed.
     *
     * @return the clockSpeed
     */
    public Integer getClockSpeed() {
        return clockSpeed;
    }

    /**
     * Get the display text.
     *
     * @return the displayText
     */
    public String getDisplayText() {
        return displayText;
    }

    /**
     * Get the host tags.
     *
     * @return the hostTags
     */
    public String getHostTags() {
        return hostTags;
    }

    /**
     * Get the cpu capacity.
     *
     * @return the cpuCapacity
     */
    public Boolean getCpuCapacity() {
        return cpuCapacity;
    }

    /**
     * Get memory.
     *
     * @return the memory
     */
    public Integer getMemory() {
        return memory;
    }

    /**
     * Get Disk bytes read rate.
     *
     * @return the diskBytesReadRate
     */
    public Integer getDiskBytesReadRate() {
        return diskBytesReadRate;
    }

    /**
     * Get Disk bytes write rate.
     *
     * @return the diskBytesWriteRate
     */
    public Integer getDiskBytesWriteRate() {
        return diskBytesWriteRate;
    }

    /**
     * Get Disk iops read rate.
     *
     * @return the diskIopsReadRate
     */
    public Integer getDiskIopsReadRate() {
        return diskIopsReadRate;
    }

    /**
     * Get network rate.
     *
     * @return the networkRate
     */
    public Integer getNetworkRate() {
        return networkRate;
    }

    /**
     * Get isHigh Availabilty Enabled.
     *
     * @return the isHighAvailabilityEnabled
     */
    public Boolean getIsHighAvailabilityEnabled() {
        return isHighAvailabilityEnabled;
    }

    /**
     * Get Disk iops write rate.
     *
     * @return the diskIopsWriteRate
     */
    public Integer getDiskIopsWriteRate() {
        return diskIopsWriteRate;
    }

    /**
     * Get created user.
     *
     * @return the createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Get updated user.
     *
     * @return the updatedBy
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the id.
     *
     * @param id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Set the uuid.
     *
     * @param uuid  to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Set the name.
     *
     * @param name  to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the numberOfCores.
     *
     * @param numberOfCores  to set
     */
    public void setNumberOfCores(Integer numberOfCores) {
        this.numberOfCores = numberOfCores;
    }

    /**
     * Set the clockSpeed.
     *
     * @param clockSpeed  to set
     */
    public void setClockSpeed(Integer clockSpeed) {
        this.clockSpeed = clockSpeed;
    }

    /**
     * Set the displayText.
     *
     * @param displayText  to set
     */
    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    /**
     * Set the hostTags.
     *
     * @param hostTags  to set
     */
    public void setHostTags(String hostTags) {
        this.hostTags = hostTags;
    }

    /**
     * Set the cpuCapacity.
     *
     * @param cpuCapacity to set
     */
    public void setCpuCapacity(Boolean cpuCapacity) {
        this.cpuCapacity = cpuCapacity;
    }

    /**
     * Set the memory .
     *
     * @param memory to set
     */
    public void setMemory(Integer memory) {
        this.memory = memory;
    }

    /**
     * Set the diskBytesReadRate.
     *
     * @param diskBytesReadRate  to set
     */
    public void setDiskBytesReadRate(Integer diskBytesReadRate) {
        this.diskBytesReadRate = diskBytesReadRate;
    }

    /**
     * Set the diskBytesWriteRate.
     *
     * @param diskBytesWriteRate  to set
     */
    public void setDiskBytesWriteRate(Integer diskBytesWriteRate) {
        this.diskBytesWriteRate = diskBytesWriteRate;
    }

    /**
     * Set the diskIopsReadRate .
     *
     * @param diskIopsReadRate to set
     */
    public void setDiskIopsReadRate(Integer diskIopsReadRate) {
        this.diskIopsReadRate = diskIopsReadRate;
    }

    /**
     * Set the networkRate.
     *
     * @param networkRate to set
     */
    public void setNetworkRate(Integer networkRate) {
        this.networkRate = networkRate;
    }

    /**
     * Set the isHighAvailabilityEnabled .
     *
     * @param isHighAvailabilityEnabled to set
     */
    public void setIsHighAvailabilityEnabled(Boolean isHighAvailabilityEnabled) {
        this.isHighAvailabilityEnabled = isHighAvailabilityEnabled;
    }

    /**
     * Set the diskIopsWriteRate.
     *
     * @param diskIopsWriteRate  to set
     */
    public void setDiskIopsWriteRate(Integer diskIopsWriteRate) {
        this.diskIopsWriteRate = diskIopsWriteRate;
    }

    /**
     * Set the createdBy .
     *
     * @param createdBy to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Set the updatedBy .
     *
     * @param updatedBy to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * @return the isPublic
     */
    public Boolean getIsPublic() {
        return isPublic;
    }

   /**
     * Get domain id.
     *
     * @return the domainId
     */
    public Long getDomainId() {
        return domainId;
    }

    /**
     * Set the isPublic.
     *
     * @param isPublic  to set
     */
    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    /**
     * Set the domainId.
     *
     * @param domainId  to set
     */
    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    /**
     * Get the version.
     *
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Set the version .
     *
     * @param version to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }


    /**
     * Get disk io.
     *
     * @return the diskIo
     */
    public DiskIo getDiskIo() {
        return diskIo;
    }

    /**
     * Set the diskIo.
     *
     * @param diskIo  to set
     */
    public void setDiskIo(DiskIo diskIo) {
        this.diskIo = diskIo;
    }

    /**
     * Get created date time.
     *
     * @return the createdDateTime
     */
    public DateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Get last modified time.
     *
     * @return the lastModifiedDateTime
     */
    public DateTime getLastModifiedDateTime() {
        return lastModifiedDateTime;
    }

    /**
     * Set the createdDateTime .
     *
     * @param createdDateTime to set
     */
    public void setCreatedDateTime(DateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Set the lastModifiedDateTime.
     *
     * @param lastModifiedDateTime  to set
     */
    public void setLastModifiedDateTime(DateTime lastModifiedDateTime) {
        this.lastModifiedDateTime = lastModifiedDateTime;
    }

    /**
     * Set the status.
     *
     * @return the status
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * Get the status .
     *
     * @param status to set
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }

    /**
     * Get the storage tags.
     *
     * @return the storageTags
     */
    public String getStorageTags() {
        return storageTags;
    }

    /**
     * Get qos type.
     *
     * @return the qosType
     */
    public QosType getQosType() {
        return qosType;
    }

    /**
     * Get min iops.
     *
     * @return the minIops
     */
    public Integer getMinIops() {
        return minIops;
    }

    /**
     * Get max iops.
     *
     * @return the maxIops
     */
    public Integer getMaxIops() {
        return maxIops;
    }

    /**
     * Get isActive.
     *
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set the storageTags.
     *
     * @param storageTags  to set
     */
    public void setStorageTags(String storageTags) {
        this.storageTags = storageTags;
    }

    /**
     * Set the qosType.
     *
     * @param qosType  to set
     */
    public void setQosType(QosType qosType) {
        this.qosType = qosType;
    }

    /**
     * Set the minIops.
     *
     * @param minIops  to set
     */
    public void setMinIops(Integer minIops) {
        this.minIops = minIops;
    }

    /**
     * Set the maxIops.
     *
     * @param maxIops  to set
     */
    public void setMaxIops(Integer maxIops) {
        this.maxIops = maxIops;
    }

    /**
     * Set the isActive.
     *
     * @param isActive  to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Get iscustom.
     *
     * @return the isCustom
     */
    public Boolean getCustomized() {
        return customized;
    }

    /**
     * Get the customized iops.
     *
     * @return the customizedIops
     */
    public Boolean getCustomizedIops() {
        return customizedIops;
    }

    /**
     * Set the customizedIops.
     *
     * @param customizedIops  to set
     */
    public void setCustomizedIops(Boolean customizedIops) {
        this.customizedIops = customizedIops;
    }

    /**
     * Set the customized.
     *
     * @param customized  to set
     */
    public void setCustomized(Boolean customized) {
        this.customized = customized;
    }

    /**
     * Get the Sync Flag.
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
     * Get Domain.
     *
     * @return the domain
     */
    public Domain getDomain() {
        return domain;
    }

    /**
     * Set the domain .
     *
     * @param domain to set
     */
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    /**
     * Ge storage type.
     *
     * @return the storageType
     */
    public StorageType getStorageType() {
        return storageType;
    }

    /**
     * Set the storageType.
     *
     * @param storageType  to set
     */
    public void setStorageType(StorageType storageType) {
        this.storageType = storageType;
    }



    /**
     * @return the computeCost
     */
    public List<ComputeOfferingCost> getComputeCost() {
        return computeCost;
    }

    /**
     * Set the computeCost .
     *
     * @param computeCost to set
     */
    public void setComputeCost(List<ComputeOfferingCost> computeCost) {
        this.computeCost = computeCost;
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
        compute.setIsSyncFlag(false);
        try {
        compute.setUuid(JsonUtil.getStringValue(object, "id"));
        compute.setDisplayText(JsonUtil.getStringValue(object, "displaytext"));
        compute.setName(JsonUtil.getStringValue(object, "name"));
        compute.setMemory(JsonUtil.getIntegerValue(object, "memory"));
        compute.setClockSpeed(JsonUtil.getIntegerValue(object, "cpuspeed"));
        compute.setCustomized(JsonUtil.getBooleanValue(object, "iscustomized"));
        compute.setCustomizedIops(JsonUtil.getBooleanValue(object, "iscustomizediops"));
        compute.setNumberOfCores(JsonUtil.getIntegerValue(object, "cpunumber"));
        compute.setStorageType(StorageType.valueOf(JsonUtil.getStringValue(object, "storagetype")));
        compute.setIsHighAvailabilityEnabled(JsonUtil.getBooleanValue(object,"offerha"));
        compute.setIsActive(true);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
