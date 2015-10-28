package ck.panda.domain.entity;

import java.io.Serializable;
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
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

/**
 * Guest Network is network where instances are attached to it.
 * and it can be of either Shared or Isolated types.
 */
@Entity
@Table(name = "ck_guest_network")
@SuppressWarnings("serial")
public class GuestNetwork implements Serializable{

    /** Id of the Guest Network. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Unique id for the Guest Network. */
    @Column(name = "uuid")
    private String uuid;

    /** Name of the Guest Network. */
    @Column(name = "name", nullable = false)
    private String name;

    /** Description of the Guest Network. */
    @Column(name = "display_text", nullable = true)
    private String displayText;

    /** id for the Domain. */
    //@OneToMany(mappedBy = "domain")
    @Column(name = "domain_id")
    private Long domainId;

    /** id for the Zone. */
    @ManyToOne
    @JoinColumn(name = "zone_id", referencedColumnName = "id")
    private Zone zoneId;

    /** id for the Network Offer. */
    @ManyToOne
    @JoinColumn(name = "network_offer_id", referencedColumnName = "id")
    private NetworkOffering networkOffering;

    /**  Network Type. */
    //@Enumerated(EnumType.STRING)
    @Column(name = "network_type")
    private String networkType;

    /**  CIDR Range of the IP address. */
    // @Enumerated(EnumType.STRING)
    @Column(name = "cidr")
    private String cIDR;

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
     * Enum type for Guest Network Status.
     *
     */
    public enum Status {
        /** Guest Network will be in a Enabled State. */
        ENABLED,
        /** Guest Network will be in a Disabled State. */
        DISABLED
    }

    /**
     * Get the Guest Network Id
     *
     * @return the  Guest Network Id
     */
    public Long getId() {
        return id;
    }

    /**
     * Get the Guest Network uuid
     *
     * @return the uuid of the Guest Network
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Get the Guest Network Name
     *
     * @return the name of the Guest Network
     */
    public String getName() {
        return name;
    }

    /**
     * Get the Guest Network Description
     *
     * @return the description of Guest Network
     */
    public String getDisplayText() {
        return displayText;
    }

    /**
     * Get the Domain
     *
     * @return the id of the domain
     */
    public Long getDomainId() {
        return domainId;
    }


    /**
     * Get the Guest Network type
     *
     * @return the type of the network
     */
    public String getNetworkType() {
        return networkType;
    }

    /**
     * Get the Guest Network State
     *
     * @return Active or Inactive state
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Get the Guest Network Version
     *
     * @return the version of Guest Network
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Get the Guest Network Status
     *
     * @return the status of Guest Network
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Get the user who creates Guest Network
     *
     * @return the createdBy
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * Get the user who updates Guest Network
     *
     * @return the updatedBy
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Get the DateTime of created Guest Network
     *
     * @return the DateTime
     */
    public DateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Get the DateTime of updated Guest Network
     *
     * @return the DateTime
     */
    public DateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**Set the Guest Network Id
     *
     * @param id
     * Guest Network id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Set the Guest Network uuid
     *
     * @param uuid
     * Guest Network uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Set the Guest Network namee
     *
     * @param name
     * Guest Network name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the Guest Network Description
     *
     * @param displayText
     *  Guest Network description to set
     */
    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    /**
     * Set the Domain Id
     *
     * @param domainId
     * the domainId to set
     */
    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }


    /**
     * Get the Zone Id
     *
	 * @return the zoneId
	 */
	public Zone getZoneId() {
		return zoneId;
	}

	/**
	 * Get the NetworkOffering Id
	 *
	 * @return the networkOffering
	 */
	public NetworkOffering getNetworkOffering() {
		return networkOffering;
	}

	/**
	 * Get the Guest Network cIDR
	 * @return the cIDR
	 */
	public String getcIDR() {
		return cIDR;
	}

	/**
	 * Set the Zone Id
	 *
	 * @param zoneId the zoneId to set
	 */
	public void setZoneId(Zone zoneId) {
		this.zoneId = zoneId;
	}

	/**
	 * Set the Network Offering Id
	 *
	 * @param networkOffering the networkOffering to set
	 */
	public void setNetworkOffering(NetworkOffering networkOffering) {
		this.networkOffering = networkOffering;
	}

	/**
	 * Set the Guest Network cIDR
	 *
	 * @param cIDR the cIDR to set
	 */
	public void setcIDR(String cIDR) {
		this.cIDR = cIDR;
	}

	/**
	 * Set the Guest Network Type
	 *
     * @param networkType
     * the networkType to set
     */
    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }


    /**
     * Set the Guest Network State
     *
     * @param isActive
     * the isActive state to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Set the Guest Network Version
     *
     * @param version
     * the version to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Set the Guest Network Status
     *
     * @param status
     * the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Set the user who creates Guest Network
     *
     * @param createdBy
     * Guest Network createdBy to set
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Set the user who updates Guest Network
     *
     * @param updatedBy
     *  Guest Network updatedBy to set
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Set the Created DateTime for Guest Network
     *
     * @param createdDateTime
     * Guest Network createdDateTime to set
     */
    public void setCreatedDateTime(DateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Set the Updated DateTime for Guest Network
     *
     * @param updatedDateTime
     * Guest Network updatedDateTime to set
     */
    public void setUpdatedDateTime(DateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /* Convert JSONObject to domain entity.
    *
    * @param object json object
    * @return domain entity object.
    * @throws JSONException handles json exception.
    */
   public static GuestNetwork convert(JSONObject object) throws JSONException {
    	GuestNetwork guestnetwork = new GuestNetwork();
    	guestnetwork.uuid = object.get("id").toString();
       return guestnetwork;
   }
}
