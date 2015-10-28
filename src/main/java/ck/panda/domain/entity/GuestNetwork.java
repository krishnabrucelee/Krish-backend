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
     * @return the  Guest Network id
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the uuid of the Guest Network
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @return the name of the Guest Network
     */
    public String getName() {
        return name;
    }

    /**
     * @return the description of Guest Network
     */
    public String getDisplayText() {
        return displayText;
    }

    /**
     * @return the id of the domain
     */
    public Long getDomainId() {
        return domainId;
    }



    /**
     * @return the type of the network
     */
    public String getNetworkType() {
        return networkType;
    }

    /**
     * @return the cIDR range of IP address
     */
    public String getCIDR() {
        return cIDR;
    }

    /**
     * @return Active or Inactive state
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * @return the version of Guest Network
     */
    public Long getVersion() {
        return version;
    }

    /**
     * @return the status of Guest Network
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @return the id of the User who creates
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * @return the id of the User who updates
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * @return the DateTime when it has been Created
     */
    public DateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * @return the DateTime when it has been Updated
     */
    public DateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * @param id
     * Guest Network id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @param uuid
     * Guest Network uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @param name
     * Guest Network name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param displayText
     *  Guest Network description to set
     */
    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    /**
     * @param domainId
     * the domainId to set
     */
    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }


    /**
	 * @return the zoneId
	 */
	public Zone getZoneId() {
		return zoneId;
	}

	/**
	 * @return the networkOffering
	 */
	public NetworkOffering getNetworkOffering() {
		return networkOffering;
	}

	/**
	 * @return the cIDR
	 */
	public String getcIDR() {
		return cIDR;
	}

	/**
	 * @param zoneId the zoneId to set
	 */
	public void setZoneId(Zone zoneId) {
		this.zoneId = zoneId;
	}

	/**
	 * @param networkOffering the networkOffering to set
	 */
	public void setNetworkOffering(NetworkOffering networkOffering) {
		this.networkOffering = networkOffering;
	}

	/**
	 * @param cIDR the cIDR to set
	 */
	public void setcIDR(String cIDR) {
		this.cIDR = cIDR;
	}

	/**
     * @param networkType
     * the networkType to set
     */
    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    /**
     * @param cIDR
     * the cIDR range to set
     */
    public void setCIDR(String cIDR) {
        this.cIDR = cIDR;
    }

    /**
     * @param isActive
     * the isActive state to set
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
     * @param status
     * the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * @param createdBy
     * Guest Network createdBy to set
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @param updatedBy
     *  Guest Network updatedBy to set
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * @param createdDateTime
     * Guest Network createdDateTime to set
     */
    public void setCreatedDateTime(DateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
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
