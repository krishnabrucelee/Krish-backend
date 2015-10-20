package ck.panda.domain.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import org.joda.time.DateTime;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * A region is the largest available organizational unit within a CloudStack
 * deployment. A region is made up of several availability zones, where each
 * zone is roughly equivalent to a datacenter.
 *
 */
@Entity
@Table(name = "ck_region")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class Region implements Serializable {

	/** Id of the region. */
	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	/** Unique ID from Cloud Stack. */
	@Column(name = "uuid")
	private String uuid;

	/** Name of the region. */
	@Column(name = "name")
	private String name;

	/** End point of the region. */
	@Column(name = "end_point")
	private String endPoint;

	/** update status when delete an entity. */
	@Column(name = "is_active")
	private Boolean isActive;

	/** Status of the region. */
	@Column(name = "status")
	private Boolean status;

	/** Version attribute to handle optimistic locking. */
	@Version
	@Column(name = "version")
	private Long version;

	/** Created by user. */
	@Column(name = "created_by")
	private User createdBy;

	/** Last updated by user. */
	@Column(name = "updated_by")
	private User updatedBy;

	/** Created date and time. */
	@Column(name = "created_date_time")
	private DateTime createdDateTime;

	/** Updated date and time. */
	@Column(name = "updated_date_time")
	private DateTime updatedDateTime;

	/**
	 * Get the id.
	 *
	 * @return id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Set the id of the region.
	 *
	 * @param id - the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Get the uuid.
	 *
	 * @return uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * Set the uuid.
	 *
	 * @param uuid - the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * Get the name of the region.
	 *
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the region.
	 *
	 * @param name - the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the endPoint.
	 *
	 * @return endPoint
	 */
	public String getEndPoint() {
		return endPoint;
	}

	/**
	 * Set the endPoint.
	 *
	 * @param endPoint - the endPoint to set
	 */
	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

	/**
	 * Get the isActive.
	 *
	 * @return isActive
	 */
	public Boolean getIsActive() {
		return isActive;
	}

	/**
	 * Set the isActive.
	 *
	 * @param isActive - the isActive to set
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * Get the status.
	 *
	 * @return status
	 */
	public Boolean getStatus() {
		return status;
	}

	/**
	 * Set the status.
	 *
	 * @param status - the status to set
	 */
	public void setStatus(Boolean status) {
		this.status = status;
	}

	/**
	 * Get the version.
	 *
	 * @return version
	 */
	public Long getVersion() {
		return version;
	}

	/**
	 * Set the version.
	 *
	 * @param version - the version to set
	 */
	public void setVersion(Long version) {
		this.version = version;
	}

	/**
	 * Get the createdBy.
	 *
	 * @return createdBy
	 */
	public User getCreatedBy() {
		return createdBy;
	}

	/**
	 * Set the createdBy.
	 *
	 * @param createdBy - the createdBy to set
	 */
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * Get the updatedBy.
	 *
	 * @return updatedBy
	 */
	public User getUpdatedBy() {
		return updatedBy;
	}

	/**
	 * Set the updatedBy.
	 *
	 * @param updatedBy - the updatedBy to set
	 */
	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}

	/**
	 * Get the createdDateTime.
	 *
	 * @return createdDateTime
	 */
	public DateTime getCreatedDateTime() {
		return createdDateTime;
	}

	/**
	 * Set the createdDateTime.
	 *
	 * @param createdDateTime - the createdDateTime to set
	 */
	public void setCreatedDateTime(DateTime createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	/**
	 * Get the updatedDateTime.
	 *
	 * @return updatedDateTime
	 */
	public DateTime getUpdatedDateTime() {
		return updatedDateTime;
	}

	/**
	 * Set the updatedDateTime.
	 *
	 * @param updatedDateTime - the updatedDateTime to set
	 */
	public void setUpdatedDateTime(DateTime updatedDateTime) {
		this.updatedDateTime = updatedDateTime;
	}

}
