package ck.panda.domain.entity;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;
import ck.panda.util.JsonUtil;

/**
 * A host is a single computer. Hosts provide the computing resources that run guest virtual machines. Each host has
 * hypervisor software installed on it to manage the guest VMs.
 *
 */

@Entity
@Table(name = "ck_host")
@SuppressWarnings("serial")
public class Host {

    /** Unique ID of the Domain. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Unique ID from Cloud Stack. */
    @Column(name = "uuid")
    private String uuid;

    /** Name of the Domain. */
    @NotEmpty
    @Column(name = "name", nullable = false)
    private String name;

    /** Pod Object for the pod. */
    @JoinColumn(name = "pod_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Pod pod;

    /** id for the pod. */
    @Column(name = "pod_id")
    private Long podId;

    /** Transient pod of the host.*/
    @Transient
    private String transPodId;

    /** Transient zone of the host.*/
    @Transient
    private String transZoneId;

    /** Transient cluster of the host.*/
    @Transient
    private String transClusterId;

    /** Zone Object for the pod. */
    @JoinColumn(name = "zone_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Zone zone;

    /** id for the Zone. */
    @Column(name = "zone_id")
    private Long zoneId;

    /** IP address of the Host. */
    @Column(name = "host_ipaddress")
    private String hostIpaddress;

    /** Zone Object for the pod. */
    @JoinColumn(name = "zone_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Cluster cluster;

    /** id for the Zone. */
    @Column(name = "cluster_id")
    private Long clusterId;

    /** State of the host. */
    @Column(name = "state")
    private Status status;

    /** IsActive attribute to verify Active or Inactive. */
    @Column(name = "is_active")
    private Boolean isActive;
    
    /** High availability of the Host. */
    @Column(name = "host_ha")
    private String hostHighAvailability;

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
    @Column(name = "created_date_time")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentZonedDateTime")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime createdDateTime;

    /** Last modified date and time. */
    @LastModifiedDate
    @Column(name = "updated_date_time")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentZonedDateTime")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime updatedDateTime;
    
    /** Hypervisor of the host. */
    @Column(name = "hypervisor")
    private String hypervisor;

    /**
     * Enum type for  Host Status.
     *
     */
    public enum Status {

    	/**  Host will be in a Enabled State. */
        UP,
        
        /**  Host will be in a Alert State. */
        ALERT,

        /**  Host will be get disconnected. */
        DISCONNECTED,
    }
    /**
     * Get id.
     *
     * @return the id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Set id.
     *
     * @param id  to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get UUID of the host.
     *
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set uuid of the host.
     *
     * @param uuid  to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name.
     *
     * @param name  to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get Pod.
     *
     * @return the pod
     */
    public Pod getPod() {
        return pod;
    }

    /**
     * Set the pod.
     *
     * @param pod  to set
     */
    public void setPod(Pod pod) {
        this.pod = pod;
    }

    /**
     * Get Pod Id.
     *
     * @return the podId
     */
    public Long getPodId() {
        return podId;
    }

    /**
     * Set the podId.
     *
     * @param podId  to set
     */
    public void setPodId(Long podId) {
        this.podId = podId;
    }

    /**
     * @return the zone
     */
    public Zone getZone() {
        return zone;
    }

    /**
     * Set the zone.
     *
     * @param zone  to set
     */
    public void setZone(Zone zone) {
        this.zone = zone;
    }

    /**
     * Get the zoneId.
     *
     * @return the zoneId
     */
    public Long getZoneId() {
        return zoneId;
    }

    /**
     * Set the zoneId.
     *
     * @param zoneId  to set
     */
    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * @return the cluster
     */
    public Cluster getCluster() {
        return cluster;
    }

    /**
     * Set the cluster.
     *
     * @param cluster to set
     */
    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    /**
     * Get the cluster.
     *
     * @return the clusterId
     */
    public Long getClusterId() {
        return clusterId;
    }

    /**
     * Set the clusterId.
     *
     * @param clusterId  to set
     */
    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    /**
     * Get createdBy.
     *
     * @return the createdBy
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the createdBy.
     *
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
     * Set the updatedBy.
     *
     * @param updatedBy to set
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get CreatedDateTime.
     *
     * @return the createdDateTime
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the createdDateTime.
     *
     * @param createdDateTime to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updatedDate time.
     *
     * @return the updatedDateTime
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updatedDateTime.
     *
     * @param updatedDateTime to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

   /**
    * Get the transient pod id.
    *
    * @return the transPodId
    */
    public String getTransPodId() {
        return transPodId;
    }

    /**
     * Set the transient Pod Id.
     *
     * @param transPodId  to set
     */
    public void setTransPodId(String transPodId) {
        this.transPodId = transPodId;
    }

    /**
     * Get transient Zone id.
     *
     * @return the transZoneId
     */
    public String getTransZoneId() {
        return transZoneId;
    }

    /**
     * Set the transZoneId.
     *
     * @param transZoneId  to set
     */
    public void setTransZoneId(String transZoneId) {
        this.transZoneId = transZoneId;
    }

    /**
     * Get Transient Cluster Id.
     *
     * @return the transClusterId
     */
    public String getTransClusterId() {
        return transClusterId;
    }

    /**
     * Set the transClusterId .
     *
     * @param transClusterId to set
     */
    public void setTransClusterId(String transClusterId) {
        this.transClusterId = transClusterId;
    }

    /**
     * Get Status.
     *
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     *  Set the status.
     *
     * @param status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Get isActive.
     *
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set  the isActive.
     *
     * @param isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Get ipaddress of host.
     *
     * @return the ipAddress.
     */
	public String getHostIpaddress() {
		return hostIpaddress;
	}


    /**
     * Set the ipaddress for host.
     *
     * @param ipAddress to set
     */
	public void setHostIpaddress(String hostIpaddress) {
		this.hostIpaddress = hostIpaddress;
	}
	
	/**
	 * Get HostHighAvailability.
	 * 
	 * @return the HostHighAvailability.
	 */
	public String getHostHighAvailability() {
		return hostHighAvailability;
	}

	/**
	 * Set the HostHighAvailability.
	 * 
	 * @param hostHighAvailability to set.
	 */
	public void setHostHighAvailability(String hostHighAvailability) {
		this.hostHighAvailability = hostHighAvailability;
	}

	/**
	 * Get Hypervisor name
	 * 
	 * @return hypertvisor
	 */
	public String getHypervisor() {
		return hypervisor;
	}

	/**
	 * Set the hypervisor
	 * 
	 * @param hypervisor name to set
	 */
	public void setHypervisor(String hypervisor) {
		this.hypervisor = hypervisor;
	}

	/**
	 * Convert JSONObject to domain entity.
	 *
	 * @param jsonObject json object
	 * @return domain entity object.
	 * @throws JSONException handles json exception.
	 */
	public static Host convert(JSONObject jsonObject) throws JSONException {
		Host host = new Host();
		try {
			host.setName(JsonUtil.getStringValue(jsonObject, "name"));
			host.setUuid(JsonUtil.getStringValue(jsonObject, "id"));
			host.setTransPodId((JsonUtil.getStringValue(jsonObject, "podid")));
			host.setTransClusterId((JsonUtil.getStringValue(jsonObject, "clusterid")));
			host.setTransZoneId((JsonUtil.getStringValue(jsonObject, "zoneid")));
			host.setStatus(Status.valueOf(JsonUtil.getStringValue(jsonObject, "state").toUpperCase()));
			host.setHostIpaddress(JsonUtil.getStringValue(jsonObject, "ipaddress"));
			host.setHostHighAvailability(JsonUtil.getStringValue(jsonObject,"hahost"));
			host.setHypervisor(JsonUtil.getStringValue(jsonObject,"hypervisor"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return host;
	}

	/**
	 * Mapping entity object into list.
	 *
	 * @param hostList
	 *            list of hosts.
	 * @return host map
	 */
	public static Map<String, Host> convert(List<Host> hostList) {
		Map<String, Host> hostMap = new HashMap<String, Host>();

		for (Host host : hostList) {
			hostMap.put(host.getUuid(), host);
		}
		return hostMap;
	}
}
