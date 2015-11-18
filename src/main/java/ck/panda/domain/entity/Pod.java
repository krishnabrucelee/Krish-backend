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
import org.hibernate.annotations.Type;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;
import ck.panda.util.ConvertUtil;
import ck.panda.util.JsonUtil;

/**
 * A pod is the third-largest organizational unit within a CloudStack deployment. Pods are contained within zones.
 *  Each zone can contain one or more pods. A pod consists of one or more clusters of hosts and one or more primary storage
 *  servers.
 *
 *  Pods are not visible to the end user.
 *
 */

@Entity
@Table(name = "ck_pod")
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
     * Get the uuid
     *
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the uuid
     *
     * @param uuid  to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get name
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name
     *
     * @param name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get Gateway
     *
     * @return the gateway
     */
    public String getGateway() {
        return gateway;
    }

    /**
     * Set the gateway
     *
     * @param gateway to set
     */
    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    /**
     * Get netmask
     *
     * @return the netmask
     */
    public String getNetmask() {
        return netmask;
    }

    /**
     * Set the netmask
     *
     * @param netmask  to set
     */
    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    /**
     * Get the zone
     *
     * @return the zone
     */
    public Zone getZone() {
        return zone;
    }

    /**
     * Set the zone
     *
     * @param zone to set
     */
    public void setZone(Zone zone) {
        this.zone = zone;
    }

    /**
     * Get ZoneId
     *
     * @return the zoneId
     */
    public Long getZoneId() {
        return zoneId;
    }

    /**
     * Set the zoneId
     *
     * @param zoneId  to set
     */
    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * Get createdBy
     *
     * @return the createdBy
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the createdBy
     *
     * @param createdBy  to set
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get UpdatedBy
     *
     * @return the updatedBy
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updatedBy
     *
     * @param updatedBy  to set
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the createdDatetime
     *
     * @return the createdDateTime
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the createdDateTime
     *
     * @param createdDateTime  to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updatedDatetime
     *
     * @return the updatedDateTime
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updatedDateTime
     *
     * @param updatedDateTime to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

     /**
     * Convert JSONObject into pod object.
     *
     * @param jsonObject JSON object.
     * @param convertUtil convert Entity object from UUID.
     * @return pod object.
     */
    public static Pod convert(JSONObject jsonObject, ConvertUtil convertUtil) {
        Pod pod = new Pod();
        try {
            pod.setName(JsonUtil.getStringValue(jsonObject, "name"));
            pod.setUuid(JsonUtil.getStringValue(jsonObject, "id"));
            pod.setZoneId(convertUtil.getZoneId(JsonUtil.getStringValue(jsonObject, "zoneid")));
            pod.setNetmask(JsonUtil.getStringValue(jsonObject, "netmask"));
            pod.setGateway(JsonUtil.getStringValue(jsonObject, "gateway"));
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

