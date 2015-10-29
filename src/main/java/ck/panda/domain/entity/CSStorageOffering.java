package ck.panda.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author Krishna<krishnakumar@assistanz.com>
 */

public class CSStorageOffering {

	/**
	 * unique ID of the disk offering
	 */
	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	/**
	 * the cache mode to use for this disk offering. none, write back or
	 * writethrough
	 */
	@Column(name = "cache_mode")
	private String cacheMode;

	/**
	 * the date this disk offering was created
	 */
	@Column(name = "created")
	private String created;

	/**
	 * the bytes read rate of the disk offering
	 */
	@Column(name = "bytes_read_rate")
	private String diskBytesReadRate;

	/**
	 * the bytes write rate of the disk offering
	 */
	@Column(name = "bytes_write_rate")
	private String diskBytesWriteRate;

	/**
	 * io requests read rate of the disk offering
	 */
	@Column(name = "iops_read_rate")
	private String diskIopsReadRate;

	/**
	 * io requests write rate of the disk offering
	 */
	@Column(name = "iops_write_rate")
	private String diskIopsWriteRate;

	/**
	 * the size of the disk offering in GB
	 */
	@Column(name = "disk_size")
	private String diskSize;

	/**
	 * whether to display the offering to the end user or not
	 */
	@Column(name = "display_offering")
	private String displayOffering;

	/**
	 * an alternate display text of the disk offering.
	 */
	@Column(name = "display_text")
	private String displayText;

	// /**
	// * the domain name this disk offering belongs to. Ignore this information
	// as
	// * it is not currently applicable.
	// */
	// @Column(name = "")
	// private String domain;

	/**
	 * the domain ID this disk offering belongs to. Ignore this information as
	 * it is not currently applicable.
	 */
	@Column(name = "domain_id")
	private String domainId;

	/**
	 * Hypervisor snapshot reserve space as a percent of a volume (for managed
	 * storage using Xen or VMware)
	 */
	@Column(name = "hv_ss_reserve")
	private String hypervisorSnapshotReserve;

	/**
	 * true if disk offering uses custom size, false otherwise
	 */
	@Column(name = "customized")
	private String isCustomized;

	/**
	 * true if disk offering uses custom iops, false otherwise
	 */
	@Column(name = "customized_iops")
	private String isCustomizedIops;

	/**
	 * the max iops of the disk offering
	 */
	@Column(name = "max_iops")
	private String maxIops;

	/**
	 * the min iops of the disk offering
	 */
	@Column(name = "min_iops")
	private String minIops;

	/**
	 * the name of the disk offering
	 */
	@Column(name = "name")
	private String name;

	/**
	 * provisioning type used to create volumes. Valid values are thin, sparse,
	 * fat.
	 */
	@Column(name = "provisioning_type")
	private String provisioningType;

	/**
	 * 
	 */
	@Column(name = "recreatable")
	private String recreatable;

	/**
	 * 
	 */
	@Column(name = "removed")
	private String removed;

	/**
	 * 
	 */
	@Column(name = "sort_key")
	private String sortKey;

	/**
	 * 
	 */
	@Column(name = "state")
	private String state;

	/**
	 * the storage type for this disk offering
	 */
	@Column(name = "type")
	private String storageType;

	/**
	 * 
	 */
	@Column(name = "system_use")
	private String systemUse;

	/**
	 * the tags for the disk offering
	 */
	@Column(name = "tags")
	private String tags;

	/**
	 * 
	 */
	@Column(name = "qos_type")
	private String qosType;
	
	/**
	 * 
	 */
	@Column(name = "use_local_storage")
	private String useLocalStorage;

	/**
	 * 
	 */
	@Column(name = "unique_name")
	private String uniqueName;

	/**
	 * 
	 */
	@Column(name = "uuid")
	private String uuid;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCacheMode() {
		return cacheMode;
	}

	public void setCacheMode(String cacheMode) {
		this.cacheMode = cacheMode;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getDiskBytesReadRate() {
		return diskBytesReadRate;
	}

	public void setDiskBytesReadRate(String diskBytesReadRate) {
		this.diskBytesReadRate = diskBytesReadRate;
	}

	public String getDiskBytesWriteRate() {
		return diskBytesWriteRate;
	}

	public void setDiskBytesWriteRate(String diskBytesWriteRate) {
		this.diskBytesWriteRate = diskBytesWriteRate;
	}

	public String getDiskIopsReadRate() {
		return diskIopsReadRate;
	}

	public void setDiskIopsReadRate(String diskIopsReadRate) {
		this.diskIopsReadRate = diskIopsReadRate;
	}

	public String getDiskIopsWriteRate() {
		return diskIopsWriteRate;
	}

	public void setDiskIopsWriteRate(String diskIopsWriteRate) {
		this.diskIopsWriteRate = diskIopsWriteRate;
	}

	public String getDiskSize() {
		return diskSize;
	}

	public void setDiskSize(String diskSize) {
		this.diskSize = diskSize;
	}

	public String getDisplayOffering() {
		return displayOffering;
	}

	public void setDisplayOffering(String displayOffering) {
		this.displayOffering = displayOffering;
	}

	public String getDisplayText() {
		return displayText;
	}

	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}

	// public String getDomain() {
	// return domain;
	// }
	//
	// public void setDomain(String domain) {
	// this.domain = domain;
	// }

	public String getDomainId() {
		return domainId;
	}

	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}

	public String getHypervisorSnapshotReserve() {
		return hypervisorSnapshotReserve;
	}

	public void setHypervisorSnapshotReserve(String hypervisorSnapshotReserve) {
		this.hypervisorSnapshotReserve = hypervisorSnapshotReserve;
	}

	public String getIsCustomized() {
		return isCustomized;
	}

	public void setIsCustomized(String isCustomized) {
		this.isCustomized = isCustomized;
	}

	public String getIsCustomizedIops() {
		return isCustomizedIops;
	}

	public void setIsCustomizedIops(String isCustomizedIops) {
		this.isCustomizedIops = isCustomizedIops;
	}

	public String getMaxIops() {
		return maxIops;
	}

	public void setMaxIops(String maxIops) {
		this.maxIops = maxIops;
	}

	public String getMinIops() {
		return minIops;
	}

	public void setMinIops(String minIops) {
		this.minIops = minIops;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProvisioningType() {
		return provisioningType;
	}

	public void setProvisioningType(String provisioningType) {
		this.provisioningType = provisioningType;
	}

	public String getStorageType() {
		return storageType;
	}

	public void setStorageType(String storageType) {
		this.storageType = storageType;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
	
	
	
}
