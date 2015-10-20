/**
 *
 */
package ck.panda.domain.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

/**
 *The CloudStack administrator can create any number of custom network offerings, in addition to the default network
 *offerings provided by CloudStack. By creating multiple custom network offerings, you can set up your cloud to offer
 *different classes of service on a single multi-tenant physical network.
 */
@Entity
@Table(name = "ck_networkOffering")
@SuppressWarnings("serial")
public class NetworkOffering implements Serializable {
	/** Id of the NetworkOffering. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Unique id for the Network Offering. */
    @Column(name = "uuid")
    private String uuid;

    /** Name of the Network Offering. */
    @Column(name = "name", nullable = false)
    private String name;

    /** Description of the Network Offering. */
    @Column(name = "display_text", nullable = false)
    private String displayText;

    /** Guest IP Network Type. */
    @Column(name = "guest_ip_type", nullable = false)
    private String guestIpType;

    /** Traffic Network Type. */
    @Column(name = "traffic_type", nullable = false)
    private String trafficType;

    /** Supported Services for Network Offering. */
    @Column(name = "supported_services", nullable = false)
    private String supportedServices;

    /** IsActive attribute to verify Active or Inactive. */
    @Column(name = "is_active")
    private Boolean isActive;

    /** Version attribute to handle optimistic locking. */
    @Version
    @Column(name = "version")
    private Long version;

    /** Status attribute to verify status of the Network Offering. */
    @Column(name = "status")
    private Boolean status;

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
    private DateTime createdDateTime;

    /** Last modified date and time. */
    @LastModifiedDate
    @Column(name = "updated_date_time")
    private DateTime updatedDateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayText() {
		return displayText;
	}

	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}

	public String getGuestIpType() {
		return guestIpType;
	}

	public void setGuestIpType(String guestIpType) {
		this.guestIpType = guestIpType;
	}

	public String getTrafficType() {
		return trafficType;
	}

	public void setTrafficType(String trafficType) {
		this.trafficType = trafficType;
	}

	public String getSupportedServices() {
		return supportedServices;
	}

	public void setSupportedServices(String supportedServices) {
		this.supportedServices = supportedServices;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
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

	public DateTime getUpdatedDateTime() {
		return updatedDateTime;
	}

	public void setUpdatedDateTime(DateTime updatedDateTime) {
		this.updatedDateTime = updatedDateTime;
	}

}
