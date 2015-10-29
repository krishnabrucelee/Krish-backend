package ck.panda.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

/**
 *
 * @author Krishna<krishnakumar@assistanz.com>
 */
@Entity
@Table(name = "ck_storage_offering_cost")
public class StorageOfferingCost {

	/**
	 * Unique ID of the disk offering cost.
	 */
	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	/**
	 * Unique ID of the Storage offering.
	 */
	@JoinColumn(name = "storage_offering_id", referencedColumnName = "id")
    @OneToOne
	private StorageOffering storageOfferingId;

	/**
	 * Unique ID from Cloud Stack.
	 */
	@Size(min = 128)
	@Column(name = "uuid")
	private String uuid;

	/**
	 * The domain ID, this disk offering belongs to. Ignore this information as
	 * it is not currently applicable.
	 */
	@JoinColumn(name = "domain_id", referencedColumnName = "id")
	@OneToOne
	private Domain domain;

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
	@Column(name = "cost_per_month")
	private Double costPerMonth;
	
	/**
	 * Cost for 1 Gb per month usage. 
	 */
	@Column(name = "cost_gb_per_month")
	private Double costGbPerMonth;
	
	/**
	 * State for storage offering, whether it is Active or InActive
	 */
	@Column(name = "status", columnDefinition = "tinyint default 0")
	private Boolean status;

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
    private DateTime createdDateTime;

    /** Last modified date and time. */
    @LastModifiedDate
    private DateTime lastModifiedDateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public StorageOffering getStorageOfferingId() {
		return storageOfferingId;
	}

	public void setStorageOfferingId(StorageOffering storageOfferingId) {
		this.storageOfferingId = storageOfferingId;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	public Zone getZone() {
		return zone;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}

	public Double getCostPerMonth() {
		return costPerMonth;
	}

	public void setCostPerMonth(Double costPerMonth) {
		this.costPerMonth = costPerMonth;
	}

	public Double getCostGbPerMonth() {
		return costGbPerMonth;
	}

	public void setCostGbPerMonth(Double costGbPerMonth) {
		this.costGbPerMonth = costGbPerMonth;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public User getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}

	public DateTime getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(DateTime createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public DateTime getLastModifiedDateTime() {
		return lastModifiedDateTime;
	}

	public void setLastModifiedDateTime(DateTime lastModifiedDateTime) {
		this.lastModifiedDateTime = lastModifiedDateTime;
	}

    
}
