package ck.panda.domain.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Type;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import ck.panda.constants.CloudStackConstants;
import ck.panda.util.JsonUtil;

@Entity
@Table(name = "primary_storage")
@SuppressWarnings("serial")
@EntityListeners(AuditingEntityListener.class)
public class PrimaryStorage implements Serializable{

     /** Constant for state. */
    public static final String CS_STATE = "state";

    /** Constant for cluster id. */
    public static final String CS_CLUSTER_ID = "clusterid";

    /** Constant for path. */
    public static final String CS_PATH = "path";

     /** Unique id of the primary storage. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Cloudstack's Primary storage uuid. */
    @Column(name = "uuid")
    private String uuid;

    /** Name of the primary storage. */
    @Column(name = "name")
    private String name;

    /** Ip Address of the primary storage. */
    @Column(name = "ip_address")
    private String ipAddress;

    /** Path of the primary storage. */
    @Column(name = "path")
    private String path;

    /** Scope of the primary storage. */
    @Column(name = "scope")
    private String scope;

    /** Type of the primary storage. */
    @Column(name = "type")
    private String type;

    /** Hypervisor of the primary storage. */
    @Column(name = "hypervisor")
    private String hypervisor;

    /** State of the primary storage. */
    @Column(name = "state")
    private String state;

    /** Zone object for primary storage. */
    @JoinColumn(name = "zone_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Zone zone;

    /** Zone id for primary storage. */
    @Column(name = "zone_id")
    private Long zoneId;

    /** Cluster object of the primary storage. */
    @JoinColumn(name = "cluster_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Cluster cluster;

    /** Cluster id of the primary storage. */
    @Column(name = "cluster_id")
    private Long clusterId;

    /** Set syncFlag. */
    @Transient
    private Boolean syncFlag;

    /** Created by user. */
    @CreatedBy
    @Column(name = "created_user_id")
    private Long createdBy;

    /** Last updated by user. */
    @LastModifiedBy
    @Column(name = "updated_user_id")
    private Long updatedBy;

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

    /** Transient zone of the primary storage. */
    @Transient
    private String transZoneId;

    /** IsActive attribute to verify Active or Inactive. */
    @Column(name = "is_active")
    private Boolean isActive;

    /** Transient cluster of the host. */
    @Transient
    private String transClusterId;


    /**
     * Get the id of the Secondary storage.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id of the Secondary storage.
     *
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the Uuid of the Secondary storage.
     *
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the Uuid of the Secondary storage.
     *
     * @param uuid  to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get the name of the Secondary storage.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the Secondary storage.
     *
     * @param name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the scope of the Secondary storage.
     *
     * @return the scope
     */
    public String getScope() {
        return scope;
    }

    /**
     * set the scope of the Secondary storage.
     *
     * @param scope the scope to set
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Get the zone of the primary storage.
     *
     * @return the zone
     */
    public Zone getZone() {
        return zone;
    }

    /**
     * Set the zone of the primary storage.
     *
     * @param zone the zone to set
     */
    public void setZone(Zone zone) {
        this.zone = zone;
    }

    /**
     * Get the zone id of the primary storage.
     *
     * @return the zoneId
     */
    public Long getZoneId() {
        return zoneId;
    }

    /**
     * Set the zone id of the primary storage.
     *
     * @param zoneId the zoneId to set
     */
    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * Get the syncflag.
     *
     * @return the syncFlag
     */
    public Boolean getSyncFlag() {
        return syncFlag;
    }

    /**
     * Set the syncFlag.
     *
     * @param syncFlag  to set
     */
    public void setSyncFlag(Boolean syncFlag) {
        this.syncFlag = syncFlag;
    }

    /**
     * Get the created id of the user.
     *
     * @return the createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the created id of the user.
     *
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updated id of the user.
     *
     * @return the updatedBy
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updated id of the user.
     *
     * @param updatedBy the updatedBy to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the createdDateTime.
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
     * Get the updated Date Time.
     *
     * @return the updatedDateTime
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updated Date Time.
     *
     * @param updatedDateTime to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get the transient zone id.
     *
     * @return the transZoneId
     */
    public String getTransZoneId() {
        return transZoneId;
    }

    /**
     * Set the transient zone id.
     *
     * @param transZoneId to set
     */
    public void setTransZoneId(String transZoneId) {
        this.transZoneId = transZoneId;
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
     * Set is Active.
     *
     * @param isActive the isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Get the ip address.
     *
     * @return the ipAddress
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Set the ipAddress.
     *
     * @param ipAddress to set
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Get the path.
     *
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Set the path
     *
     * @param path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Get the type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Set the type.
     *
     * @param type  to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get the hypervisor.
     *
     * @return the hypervisor
     */
    public String getHypervisor() {
        return hypervisor;
    }

    /**
     * Set the hypervisor.
     *
     * @param hypervisor to set
     */
    public void setHypervisor(String hypervisor) {
        this.hypervisor = hypervisor;
    }

    /**
     * Get the state of the primary storage.
     *
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * Set the state of the primary storage.
     *
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
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
     * @param clusterId to set
     */
    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
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
     * Convert JSONObject into primary storage object.
     *
     * @param jsonObject JSON object.
     * @return primary storage object.
     */
    public static PrimaryStorage convert(JSONObject jsonObject) {
        PrimaryStorage storage = new PrimaryStorage();
        try {
            storage.setName(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_NAME));
            storage.setUuid(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_ID));
            storage.setTransZoneId((JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_ZONE_ID)));
            storage.setType(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CAPACITY_TYPE));
            storage.setState(JsonUtil.getStringValue(jsonObject, CS_STATE));
            storage.setIpAddress(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_IP_ADDRESS));
            storage.setScope(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_SCOPE));
            storage.setHypervisor(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_HYPERVISOR));
            storage.setTransClusterId(JsonUtil.getStringValue(jsonObject, CS_CLUSTER_ID));
            storage.setPath(JsonUtil.getStringValue(jsonObject, CS_PATH));
            storage.setIsActive(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return storage;
    }

    /**
     * Mapping entity object into list.
     *
     * @param podList list of storage.
     * @return storage map
     */
    public static Map<String, PrimaryStorage> convert(List<PrimaryStorage> storageList) {
        Map<String, PrimaryStorage> storageMap = new HashMap<String, PrimaryStorage>();

        for (PrimaryStorage storage : storageList) {
            storageMap.put(storage.getUuid(), storage);
        }

        return storageMap;
    }
}
