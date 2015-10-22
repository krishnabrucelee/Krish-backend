package ck.panda.domain.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
 * The CloudStack administrator can create any number of custom network
 * offerings, in addition to the default network offerings provided by
 * CloudStack.
 */
@Entity
@Table(name = "ck_network_offering")
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
    @Enumerated(EnumType.STRING)
    private GuestIpType guestIpType;

    /** Traffic Network Type. */
    @Enumerated(EnumType.STRING)
    @Column(name = "traffic_type", nullable = false)
    private TrafficType trafficType;

    /** Supported Services for Network Offering. */
    @Column(name = "supported_services", nullable = false)
    private NetworkOfferingServiceList supportedServices;

    /** IsActive attribute to verify Active or Inactive. */
    @Column(name = "is_active")
    private Boolean isActive;

    /** Version attribute to handle optimistic locking. */
    @Version
    @Column(name = "version")
    private Long version;

    /** Status attribute to verify status of the Network Offering. */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

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

    /**
     * Enum type for GuestIpTypes.
     *
     */
    public enum GuestIpType {
        /** Network type will be Shared. */
        SHARED,
        /** Network type will be Private. */
        ISOLATED
    }

    /**
     * Enum type for TrafficTypes.
     *
     */
    public enum TrafficType {
        /** Traffic type will be GUEST. */
        GUEST,
        /** Traffic type will be PUBLIC. */
        PUBLIC,
        /** Traffic type will be MANAGEMENT. */
        MANAGEMENT,
        /** Traffic type will be CONTROL. */
        CONTROL,
        /** Traffic type will be VLAN. */
        VLAN,
        /** Traffic type will be STORAGE. */
        STORAGE,
    }

    /**
     * Enum type for Network Offering Status.
     *
     */
    public enum Status {
        /** Network Offering will be in a Enabled State. */
        ENABLED,
        /** Network Offering will be in a Disabled State. */
        DISABLED
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
     * @return the displayText
     */
    public String getDisplayText() {
        return displayText;
    }

    /**
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * @return the version
     */
    public Long getVersion() {
        return version;
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
     * @return the createdDateTime
     */
    public DateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * @return the updatedDateTime
     */
    public DateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * @return the supportedServices
     */
    public NetworkOfferingServiceList getSupportedServices() {
        return supportedServices;
    }

    /**
     * @param supportedServices
     * the supportedServices to set
     */
    public void setSupportedServices(NetworkOfferingServiceList supportedServices) {
        this.supportedServices = supportedServices;
    }

    /**
     * @param id
     * the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @param uuid
     * the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @param name
     * the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param displayText
     * the displayText to set
     */
    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    /**
     * @return the guestIpType
     */
    public GuestIpType getGuestIpType() {
        return guestIpType;
    }

    /**
     * @param guestIpType
     * the guestIpType to set
     */
    public void setGuestIpType(GuestIpType guestIpType) {
        this.guestIpType = guestIpType;
    }

    /**
     * @param isActive
     * the isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * @param version
     * the version to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * @return the trafficType
     */
    public TrafficType getTrafficType() {
        return trafficType;
    }

    /**
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @param trafficType
     * the trafficType to set
     */
    public void setTrafficType(TrafficType trafficType) {
        this.trafficType = trafficType;
    }

    /**
     * @param status
     * the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * @param createdBy
     * the createdBy to set
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @param updatedBy
     * the updatedBy to set
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * @param createdDateTime
     * the createdDateTime to set
     */
    public void setCreatedDateTime(DateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * @param updatedDateTime
     * the updatedDateTime to set
     */
    public void setUpdatedDateTime(DateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }
}
