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
 * A cluster is a XenServer server pool, a set of KVM servers, , or a VMware cluster preconfigured in vCenter.
 *
 * The hosts in a cluster all have identical hardware, run the same hypervisor,are on the same subnet,
 * and access the same shared primary storage.
 *
 */
@Entity
@Table(name = "ck_cluster")
public class Cluster {

    /** Unique Id of the cluster. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Cloudstack's cluster uuid. */
    @Column(name = "uuid")
    private String uuid;

    /** Name of the cluster. */
    @Column(name = "name")
    private String name;

    /** Type of the cluster. */
    @Column(name = "cluster_type")
    private String clusterType;

    /** Type of the hypervisor. */
    @Column(name = "hypervisor_type")
    private String hypervisorType;

    /** Pod Object for the pod. */
    @JoinColumn(name = "pod_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Pod pod;

    /** id for the pod. */
    @Column(name = "pod_id")
    private Long podId;

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
     * Get id
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id
     *
     * @param id  to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get Uuid
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
     * @param name  to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the ClusterType
     *
     * @return the clusterType
     */
    public String getClusterType() {
        return clusterType;
    }

    /**
     * @param clusterType the clusterType to set
     */
    public void setClusterType(String clusterType) {
        this.clusterType = clusterType;
    }

    /**
     * Get the hypervisor type
     *
     * @return the hypervisorType
     */
    public String getHypervisorType() {
        return hypervisorType;
    }

    /**
     * Set the hypervisorType
     *
     * @param hypervisorType  to set
     */
    public void setHypervisorType(String hypervisorType) {
        this.hypervisorType = hypervisorType;
    }

    /**
     * Get pod
     *
     * @return the pod
     */
    public Pod getPod() {
        return pod;
    }

    /**
     * Set the pod
     *
     * @param pod  to set
     */
    public void setPod(Pod pod) {
        this.pod = pod;
    }

    /**
     * @return the podId
     */
    public Long getPodId() {
        return podId;
    }

    /**
     * Set the podId
     *
     * @param podId to set
     */
    public void setPodId(Long podId) {
        this.podId = podId;
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
     * Get zoneId
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
     * Get CreatedBy
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
     * Get updatedBy
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
     * Get createdDateTime
     *
     * @return the createdDateTime
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the createdDateTime
     *
     * @param createdDateTime to set
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
     * @param updatedDateTime  to set
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
    public static Cluster convert(JSONObject jsonObject, ConvertUtil convertUtil) {
        Cluster cluster = new Cluster();
        try {
            cluster.setName(JsonUtil.getStringValue(jsonObject, "name"));
            cluster.setUuid(JsonUtil.getStringValue(jsonObject, "id"));
            cluster.setZoneId(convertUtil.getZoneId(JsonUtil.getStringValue(jsonObject, "zoneid")));
            cluster.setPodId(convertUtil.getPodId(JsonUtil.getStringValue(jsonObject, "podid")));
            cluster.setHypervisorType(JsonUtil.getStringValue(jsonObject, "hypervisortype"));
            cluster.setClusterType(JsonUtil.getStringValue(jsonObject, "clustertype"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
      return cluster;
    }

    /**
     * Mapping entity object into list.
     *
     * @param clusterList list of cluster.
     * @return cluster map
     */
    public static Map<String, Cluster> convert(List<Cluster> clusterList) {
        Map<String, Cluster> clusterMap = new HashMap<String, Cluster>();

        for (Cluster cluster : clusterList) {
            clusterMap.put(cluster.getUuid(), cluster);
        }
        return clusterMap;
    }
}
