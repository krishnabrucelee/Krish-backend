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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import ck.panda.util.JsonValidator;

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

    /** Pod ID from Cloud Stack. */
    @Column(name = "pod_uuid")
    private String podId;

    /** Pod ID from Cloud Stack. */
    @Column(name = "pod_name")
    private String podName;

    /** Zone ID from Cloud Stack. */
    @Column(name = "zone_uuid")
    private String zoneId;

    /** Pod ID from Cloud Stack. */
    @Column(name = "zone_name")
    private String zoneName;

    /** Cluster ID from Cloud Stack. */
    @Column(name = "cluster_uuid")
    private String clusterId;

    /** Cluster ID from Cloud Stack. */
    @Column(name = "cluster_name")
    private String clusterName;

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
     * @return the podId
     */
    public String getPodId() {
        return podId;
    }

    /**
     * @param podId the podId to set
     */
    public void setPodId(String podId) {
        this.podId = podId;
    }

    /**
     * @return the zoneId
     */
    public String getZoneId() {
        return zoneId;
    }

    /**
     * @param zoneId the zoneId to set
     */
    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * @return the clusterId
     */
    public String getClusterId() {
        return clusterId;
    }

    /**
     * @param clusterId the clusterId to set
     */
    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the podName
     */
    public String getPodName() {
        return podName;
    }

    /**
     * @param podName the podName to set
     */
    public void setPodName(String podName) {
        this.podName = podName;
    }

    /**
     * @return the zoneName
     */
    public String getZoneName() {
        return zoneName;
    }

    /**
     * @param zoneName the zoneName to set
     */
    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    /**
     * @return the clusterName
     */
    public String getClusterName() {
        return clusterName;
    }

    /**
     * @param clusterName the clusterName to set
     */
    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
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
       * Convert JSONObject to domain entity.
       *
       * @param object json object
       * @return domain entity object.
       * @throws JSONException handles json exception.
       */
      public static Host convert(JSONObject object) throws JSONException {
          Host host = new Host();
          try {
          host.uuid =  JsonValidator.jsonStringValidation(object, "id");
          host.name =  JsonValidator.jsonStringValidation(object, "name");
          host.clusterId = JsonValidator.jsonStringValidation(object, "clusterid");
          host.clusterName = JsonValidator.jsonStringValidation(object, "clustername");
          host.podId = JsonValidator.jsonStringValidation(object,"podid");
          host.podName = JsonValidator.jsonStringValidation(object,"podname");
          host.zoneId = JsonValidator.jsonStringValidation(object,"zoneid");
          host.zoneName = JsonValidator.jsonStringValidation(object,"zonename");
          host.state = JsonValidator.jsonStringValidation(object,"state");
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
