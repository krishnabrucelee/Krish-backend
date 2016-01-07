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
import javax.persistence.Table;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

import ck.panda.util.JsonValidator;

/**
 * The CloudStack administrator can create any number of custom network offerings, in addition to the default network
 * offerings provided by CloudStack.
 */
@Entity
@Table(name = "network_offerings")
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

    /** Availability of the Network Offering. */
    @Column(name = "availability")
    private String availability;

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
    @Column(name = "created_by")
    private Long createdBy;

    /** Last updated by user. */
    @LastModifiedBy
    @Column(name = "updated_by")
    private Long updatedBy;

    /** Created date and time. */
    @CreatedDate
    @Column(name = "created_date_time")
    private DateTime createdDateTime;

    /** Last modified date and time. */
    @LastModifiedDate
    @Column(name = "updated_date_time")
    private DateTime updatedDateTime;

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
     * Get the NetworkOffer Id.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Get the NetworkOffer uuid.
     *
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Get the NetworkOffer Name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the NetworkOffer Description.
     *
     * @return the displayText
     */
    public String getDisplayText() {
        return displayText;
    }

    /**
     * Get the NetworkOffer State.
     *
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Get the NetworkOffer Version.
     *
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Get the user who creates NetworkOffer.
     *
     * @return the createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Get the user who updates NetworkOffer.
     *
     * @return the updatedBy
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Get the NetworkOffer Created Date.
     *
     * @return the createdDateTime
     */
    public DateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Get the NetworkOffer Updated Date.
     *
     * @return the updatedDateTime
     */
    public DateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the NetworkOffer Id.
     *
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Set the NetworkOffer uuid.
     *
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Set the NetworkOffer Name.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the NetworkOffer description.
     *
     * @param displayText the displayText to set
     */
    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    /**
     * Get the NetworkOffer guestIpType.
     *
     * @return the guestIpType
     */
    public String getGuestIpType() {
        return guestIpType;
    }

    /**
     * Set the NetworkOffer guestIpType.
     *
     * @param guestIpType the guestIpType to set
     */
    public void setGuestIpType(String guestIpType) {
        this.guestIpType = guestIpType;
    }

    /**
     * Set the NetworkOffer State.
     *
     * @param isActive the isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Set the NetworkOffer Version.
     *
     * @param version the version to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get the NetworkOffer status.
     *
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set the NetworkOffer status.
     *
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Set the user who creates NetworkOffer.
     *
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Set the offer updated user.
     *
     * @param updatedBy the updatedBy to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Set Network offer created date.
     *
     * @param createdDateTime the createdDateTime to set
     */
    public void setCreatedDateTime(DateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Set Network offer updated time.
     *
     * @param updatedDateTime the updatedDateTime to set
     */
    public void setUpdatedDateTime(DateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get the Network Offer traffic type.
     *
     * @return the trafficType
     */
    public String getTrafficType() {
        return trafficType;
    }

    /**
     * Set the Network Offer Traffic type.
     *
     * @param trafficType the trafficType to set
     */
    public void setTrafficType(String trafficType) {
        this.trafficType = trafficType;
    }

    /**
     * Get the Network Offer availability.
     *
     * @return the availability
     */
    public String getAvailability() {
        return availability;
    }

    /**
     * Set the Network Offer availability.
     *
     * @param availability the availability to set
     */
    public void setAvailability(String availability) {
        this.availability = availability;
    }

    /**
     * Convert JSONObject to network offering entity.
     *
     * @param object json object
     * @return network offering entity object.
     * @throws JSONException handles json exception.
     */
    public static NetworkOffering convert(JSONObject object) throws JSONException {
        NetworkOffering networkOffering = new NetworkOffering();
        try {
            networkOffering.uuid = JsonValidator.jsonStringValidation(object, "id");
            networkOffering.name = JsonValidator.jsonStringValidation(object, "name");
            networkOffering.trafficType = JsonValidator.jsonStringValidation(object, "traffictype");
            networkOffering.guestIpType = JsonValidator.jsonStringValidation(object, "guestiptype");
            networkOffering.displayText = JsonValidator.jsonStringValidation(object, "displaytext");
            networkOffering.availability = JsonValidator.jsonStringValidation(object, "availability");
            networkOffering.setIsActive(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return networkOffering;
    }

    /**
     * Mapping entity object into list.
     *
     * @param networkOfferingList list of network offering.
     * @return network offering map
     */
    public static Map<String, NetworkOffering> convert(List<NetworkOffering> networkOfferingList) {
        Map<String, NetworkOffering> networkOfferingMap = new HashMap<String, NetworkOffering>();

        for (NetworkOffering networkOffering : networkOfferingList) {
            networkOfferingMap.put(networkOffering.getUuid(), networkOffering);
        }

        return networkOfferingMap;
    }
}
