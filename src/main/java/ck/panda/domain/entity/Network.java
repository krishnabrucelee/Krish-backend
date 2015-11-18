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
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import ck.panda.util.ConvertUtil;
import ck.panda.util.JsonUtil;

/**
 * A virtual network is a logical construct that enables multi-tenancy on a single physical network.
 * In CloudStack a virtual network can be shared or isolated.
 */
@Entity
@Table(name = "ck_network")
@SuppressWarnings("serial")
public class Network implements Serializable {

    /** Id of the Network. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Unique id for the Network. */
    @Column(name = "uuid")
    private String uuid;

    /** Name of the Network. */
    @Column(name = "name", nullable = false)
    private String name;

    /** Description of the Network. */
    @Column(name = "display_text", nullable = true)
    private String displayText;

    /** Domain Object for the Network. */
    @JoinColumn(name = "domain_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Domain domain;

    /** id for the Domain. */
    @Column(name = "domain_id")
    private Long domainId;

    /** Zone Object for the Network. */
    @JoinColumn(name = "zone_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Zone zone;

    /** id for the Zone. */
    @Column(name = "zone_id")
    private Long zoneId;

    /** Name of the account. */
    @Column(name = "account")
    private String account;

    /** NetworkOffering Object for the Network Offer. */
    @ManyToOne
    @JoinColumn(name = "networkoffering_id", referencedColumnName = "id", updatable = false, insertable = false)
    private NetworkOffering networkOffering;

    /** NetworkOffering id for the Zone. */
    @Column(name = "networkoffering_id")
    private Long networkOfferingId;

    /** Type of the Network. */
    @Column(name = "network_type")
    @Enumerated(EnumType.STRING)
    private NetworkType networkType;

    /**  CIDR Range of the IP address. */
    @Column(name = "cidr")
    private String cIDR;

    /**  Gateway of the Network. */
    @Column(name = "gateway")
    private String gateway;

    /** IsActive attribute to verify Active or Inactive. */
    @Column(name = "is_active")
    private Boolean isActive;

    /** Version attribute to handle optimistic locking. */
    @Version
    @Column(name = "version")
    private Long version;

    /** Status attribute to verify status of the Network . */
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
     * Enum type for Network Type.
     *
     */
    public enum NetworkType {
        /**  Network type be Shared. */
        Shared,
        /**  Network type be Isolated. */
        Isolated
    }

    /**
     * Enum type for  Network Status.
     *
     */
    public enum Status {
        /**  Network will be in a Enabled State. */
        Implemented,
        /**  Network will be in a Disabled State. */
        Allocated
    }

    /** Set syncFlag. */
    @Transient
    private Boolean syncFlag;

    /**
     * Get the Network Id.
     *
     * @return the Network Id
     */
    public Long getId() {
        return id;
    }

    /**
     * Get the  Network uuid.
     *
     * @return the uuid of the Network
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Get the Zone
     *
     * @return the zone
     */
    public Zone getZone() {
        return zone;
    }

    /**
     * Set the Zone
     *
     * @param zone the zone to set
     */
    public void setZone(Zone zone) {
        this.zone = zone;
    }

    /**
     * Get the zoneId
     *
     * @return the zoneId
     */
    public Long getZoneId() {
        return zoneId;
    }

    /**
     * Set the zoneId
     *
     * @param zoneId the zoneId to set
     */
    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * Get the  Network Name.
     *
     * @return the name of the Network
     */
    public String getName() {
        return name;
    }

    /**
     * Get the  Network Description.
     *
     * @return the description of Network
     */
    public String getDisplayText() {
        return displayText;
    }

    /**
     * Get the Domain.
     *
     * @return the id of the domain
     */
    public Long getDomainId() {
        return domainId;
    }

    /**
     * Get the  Network type.
     *
     * @return the type of the network
     */
    public NetworkType getNetworkType() {
        return networkType;
    }

    /**
     * Get the Network State.
     *
     * @return Active or Inactive state
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Get the Network Version.
     *
     * @return the version of Network
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Get the Network Status.
     *
     * @return the status of  Network
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Get the user who creates Network.
     *
     * @return the createdBy
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * Get the user who updates Network.
     *
     * @return the updatedBy
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Get the DateTime of created Network.
     *
     * @return the DateTime
     */
    public DateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Get the DateTime of updated Network.
     *
     * @return the DateTime
     */
    public DateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the Network Id.
     *
     * @param id
     * Network id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Set the Network uuid.
     *
     * @param uuid
     * Network uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Set the Network name.
     *
     * @param name
     * Network name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the Network Description.
     *
     * @param displayText
     *  Network description to set
     */
    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    /**
     * Set the Domain Id
     *
	 * @param domainId the domainId to set
	 */
	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	/**
     * Get the NetworkOffering Id.
     *
     * @return the networkOffering
     */
    public NetworkOffering getNetworkOffering() {
      return networkOffering;
    }

    /**
     * Get the Network cIDR.
     * @return the cIDR
     */
    public String getcIDR() {
        return cIDR;
    }

    /**
     * Set the Network Offering Id.
     *
     * @param networkOffering the networkOffering to set
     */
    public void setNetworkOffering(NetworkOffering networkOffering) {
        this.networkOffering = networkOffering;
    }

    /**
     * Set the Network cIDR.
     *
     * @param cIDR the cIDR to set
     */
    public void setcIDR(String cIDR) {
        this.cIDR = cIDR;
    }

    /**
     * Set the Network Type.
     *
     * @param networkType
     * the networkType to set
     */
    public void setNetworkType(NetworkType networkType) {
        this.networkType = networkType;
    }

    /**
     * Set the Network State.
     *
     * @param isActive
     * the isActive state to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Set the Network Version.
     *
     * @param version
     * the version to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Set the  Network Status.
     *
     * @param status
     * the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Set the user who creates Network.
     *
     * @param createdBy
     * Network createdBy to set
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Set the user who updates Network.
     *
     * @param updatedBy
     *  Network updatedBy to set
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Set the Created DateTime for Network.
     *
     * @param createdDateTime
     * Network createdDateTime to set
     */
    public void setCreatedDateTime(DateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Set the Updated DateTime for Network.
     *
     * @param updatedDateTime
     * Network updatedDateTime to set
     */
    public void setUpdatedDateTime(DateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }


    /**
     * Get the syncFlag
     *
	 * @return the syncFlag
	 */
	public Boolean getSyncFlag() {
		return syncFlag;
	}

	/**
	 * Set the syncFlag
	 *
	 * @param syncFlag the syncFlag to set
	 */
	public void setSyncFlag(Boolean syncFlag) {
		this.syncFlag = syncFlag;
	}

	/**
	 * Get the Domain Object
	 *
	 * @return the domain
	 */
	public Domain getDomain() {
		return domain;
	}

	/**
	 * Set the Domain Object
	 *
	 * @param domain the domain to set
	 */
	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	/**
	 * Get the networkOffering Id
	 *
	 * @return the networkOfferingId
	 */
	public Long getNetworkOfferingId() {
		return networkOfferingId;
	}

	/**
	 * Set the networkOffering Id
	 *
	 * @param networkOfferingId the networkOfferingId to set
	 */
	public void setNetworkOfferingId(Long networkOfferingId) {
		this.networkOfferingId = networkOfferingId;
	}


	/**
	 * Get the Network Gateway.
	 *
	 * @return the gateway
	 */
	public String getGateway() {
		return gateway;
	}

	/**
	 * Set the Network Gateway.
	 *
	 * @param gateway the gateway to set
	 */
	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	/**
	 * Get the account name.
	 *
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}

	/**
	 * Set the account name.
	 *
	 * @param account the account to set
	 */
	public void setAccount(String account) {
		this.account = account;
	}

	/** Convert JSONObject to domain entity.
     *
     * @param object json object
     * @return domain entity object.
     * @throws JSONException handles json exception.
     */
   public static Network convert(JSONObject jsonObject, ConvertUtil convertUtil) throws JSONException {
       Network network = new Network();
       network.setSyncFlag(false);
       try {
    	   network.setName(JsonUtil.getStringValue(jsonObject, "name"));
    	   network.setUuid(JsonUtil.getStringValue(jsonObject, "id"));
           network.setZoneId(convertUtil.getZoneId(JsonUtil.getStringValue(jsonObject, "zoneid")));
           network.setDomainId(convertUtil.getDomainId(JsonUtil.getStringValue(jsonObject, "domainid")));;
           network.setNetworkType(NetworkType.valueOf(JsonUtil.getStringValue(jsonObject, "type")));
           network.setNetworkOfferingId(convertUtil.getNetworkOfferingId(JsonUtil.getStringValue(jsonObject, "networkofferingid")));
           network.setcIDR(JsonUtil.getStringValue(jsonObject, "cidr"));
           network.setDisplayText(JsonUtil.getStringValue(jsonObject, "displaytext"));
           network.setGateway(JsonUtil.getStringValue(jsonObject, "gateway"));
           network.setAccount(JsonUtil.getStringValue(jsonObject, "account"));
           network.setStatus(Status.valueOf(JsonUtil.getStringValue(jsonObject, "state")));
       } catch ( Exception ex){
    	   ex.printStackTrace();

       }
       return network;
   }

   /**
    * Mapping entity object into list.
    *
    * @param networkList list of networks.
    * @return network map
    */
   public static Map<String, Network> convert(List<Network> networkList) {
       Map<String, Network> networkMap = new HashMap<String, Network>();

       for (Network network : networkList) {
    	   networkMap.put(network.getUuid(), network);
       }

       return networkMap;
   }
}
