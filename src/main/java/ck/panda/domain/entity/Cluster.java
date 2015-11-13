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
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid the uuid to set
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
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
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
     * @return the hypervisorType
     */
    public String getHypervisorType() {
        return hypervisorType;
    }

    /**
     * @param hypervisorType the hypervisorType to set
     */
    public void setHypervisorType(String hypervisorType) {
        this.hypervisorType = hypervisorType;
    }

    /**
     * @return the pod
     */
    public Pod getPod() {
        return pod;
    }

    /**
     * @param pod the pod to set
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
     * @param podId the podId to set
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
     * @param zone the zone to set
     */
    public void setZone(Zone zone) {
        this.zone = zone;
    }

    /**
     * @return the zoneId
     */
    public Long getZoneId() {
        return zoneId;
    }

    /**
     * @param zoneId the zoneId to set
     */
    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * @return the createdBy
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy the createdBy to set
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
     * @param updatedBy the updatedBy to set
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * @return the createdDateTime
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * @param createdDateTime the createdDateTime to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * @return the updatedDateTime
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * @param updatedDateTime the updatedDateTime to set
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
