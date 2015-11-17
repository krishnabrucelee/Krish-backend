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
import org.hibernate.validator.constraints.NotEmpty;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;
import ck.panda.util.ConvertUtil;
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

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Snapshot.class);

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

    /** Zone Object for the pod. */
    @JoinColumn(name = "zone_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Zone zone;

    /** id for the Zone. */
    @Column(name = "zone_id")
    private Long zoneId;

    /** Zone Object for the pod. */
    @JoinColumn(name = "zone_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Cluster cluster;

    /** id for the Zone. */
    @Column(name = "cluster_id")
    private Long clusterId;

    /** State of the host. */
    @Column(name = "state")
    private String state;

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
     * Get the state.
     *
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * Set the state.
     *
     * @param state  to set
     */
    public void setState(String state) {
        this.state = state;
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
       * Convert JSONObject to domain entity.
       *
       * @param jsonObject json object
       * @param convertUtil convert Entity object from UUID.
       * @return domain entity object.
       * @throws JSONException handles json exception.
       */
      public static Host convert(JSONObject jsonObject, ConvertUtil convertUtil) throws JSONException {
          Host host = new Host();
          try {
              host.setName(JsonUtil.getStringValue(jsonObject, "name"));
              host.setUuid(JsonUtil.getStringValue(jsonObject, "id"));
              host.setClusterId(convertUtil.getZoneId(JsonUtil.getStringValue(jsonObject, "zoneid")));
              host.setZoneId(convertUtil.getZoneId(JsonUtil.getStringValue(jsonObject, "zoneid")));
              host.setPodId(convertUtil.getPodId(JsonUtil.getStringValue(jsonObject, "podid")));

          } catch (Exception ex) {
              ex.printStackTrace();
          }
        return host;
      }

      /**
       * Mapping entity object into list.
       *
       * @param hostList list of hosts.
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
