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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
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
     * @param id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the displayText
     */
    public String getDisplayText() {
        return displayText;
    }

    /**
     * @param displayText  to set
     */
    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    /**
     * @return the guestIpType
     */
    public String getGuestIpType() {
        return guestIpType;
    }

    /**
     * @param guestIpType to set
     */
    public void setGuestIpType(String guestIpType) {
        this.guestIpType = guestIpType;
    }

    /**
     * @return the trafficType
     */
    public String getTrafficType() {
        return trafficType;
    }

    /**
     * @param trafficType to set
     */
    public void setTrafficType(String trafficType) {
        this.trafficType = trafficType;
    }

    /**
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * @param isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * @param version  to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @param status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * @return the createdBy
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy  to set
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @return the updatedBy
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * @param updatedBy to set
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * @return the createdDateTime
     */
    public DateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * @param createdDateTime to set
     */
    public void setCreatedDateTime(DateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * @return the updatedDateTime
     */
    public DateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
      * @param updatedDateTime to update time.
     */
    public void setUpdatedDateTime(DateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
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
        networkOffering.uuid = object.get("id").toString();
        networkOffering.name = object.get("name").toString();
        networkOffering.trafficType = object.get("traffictype").toString();
        networkOffering.guestIpType = object.get("guestiptype").toString();
        networkOffering.displayText = object.get("displaytext").toString();

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
