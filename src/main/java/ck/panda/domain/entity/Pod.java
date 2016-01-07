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
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Type;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;
import ck.panda.util.JsonUtil;

/**
 * A pod is the third-largest organizational unit within a CloudStack deployment. Pods are contained within zones. Each
 * zone can contain one or more pods. A pod consists of one or more clusters of hosts and one or more primary storage
 * servers. *
 * Pods are not visible to the end user. *
 */
@Entity
@Table(name = "pods")
public class Pod {

    /** Unique Id of the pod. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Cloudstack's pod uuid. */
    @Column(name = "uuid")
    private String uuid;

    /** Name of the pod. */
    @Column(name = "name")
    private String name;

    /** The gateway for the pod. */
    @Column(name = "gateway")
    private String gateway;

    /** The net mask for the pod. */
    @Column(name = "netmask")
    private String netmask;

    /** Zone Object for the pod. */
    @JoinColumn(name = "zone_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Zone zone;

    /** id for the Zone. */
    @Column(name = "zone_id")
    private Long zoneId;

    /** Transient zone of the pod. */
    @Transient
    private String transZoneId;

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

    /** State of the host. */
    @Column(name = "state")
    private Status status;

    /** IsActive attribute to verify Active or Inactive. */
    @Column(name = "is_active")
    private Boolean isActive;

    /**
     * Enum type for pod Status.
     *
     */
    public enum Status {

        /** Pod will be in a Enabled State. */
        ENABLED,

        /** Pod will be in a Disabled State. */
        DISABLED,
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the uuid.
     *
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the uuid.
     *
     * @param uuid to set
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
     * @param name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get Gateway.
     *
     * @return the gateway
     */
    public String getGateway() {
        return gateway;
    }

    /**
     * Set the gateway.
     *
     * @param gateway to set
     */
    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    /**
     * Get netmask.
     *
     * @return the netmask
     */
    public String getNetmask() {
        return netmask;
    }

    /**
     * Set the netmask.
     *
     * @param netmask to set
     */
    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    /**
     * Get the zone.
     *
     * @return the zone
     */
    public Zone getZone() {
        return zone;
    }

    /**
     * Set the zone.
     *
     * @param zone to set
     */
    public void setZone(Zone zone) {
        this.zone = zone;
    }

    /**
     * Get ZoneId.
     *
     * @return the zoneId
     */
    public Long getZoneId() {
        return zoneId;
    }

    /**
     * Set the zoneId.
     *
     * @param zoneId to set
     */
    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * Get createdBy.
     *
     * @return the createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the createdBy.
     *
     * @param createdBy to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get UpdatedBy.
     *
     * @return the updatedBy
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updatedBy.
     *
     * @param updatedBy to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the createdDatetime.
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
     * Get the updatedDatetime.
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
     * Get the status.
     *
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set the status.
     *
     * @param status the status to set
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
     * Set is Active.
     *
     * @param isActive the isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Convert JSONObject into pod object.
     *
     * @param jsonObject JSON object.
     * @return pod object.
     */
    public static Pod convert(JSONObject jsonObject) {
        Pod pod = new Pod();
        try {
            pod.setName(JsonUtil.getStringValue(jsonObject, "name"));
            pod.setUuid(JsonUtil.getStringValue(jsonObject, "id"));
            pod.setTransZoneId((JsonUtil.getStringValue(jsonObject, "zoneid")));
            pod.setNetmask(JsonUtil.getStringValue(jsonObject, "netmask"));
            pod.setGateway(JsonUtil.getStringValue(jsonObject, "gateway"));
            pod.setStatus(Status.valueOf(JsonUtil.getStringValue(jsonObject, "state").toUpperCase()));
            pod.setIsActive(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return pod;
    }

    /**
     * Mapping entity object into list.
     *
     * @param podList list of pods.
     * @return pod map
     */
    public static Map<String, Pod> convert(List<Pod> podList) {
        Map<String, Pod> podMap = new HashMap<String, Pod>();

        for (Pod pod : podList) {
            podMap.put(pod.getUuid(), pod);
        }

        return podMap;
    }
}
