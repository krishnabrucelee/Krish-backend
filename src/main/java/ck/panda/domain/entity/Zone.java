package ck.panda.domain.entity;

import java.io.Serializable;
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
import javax.validation.constraints.Size;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

/**
 * A zone is the second largest organizational unit within a CloudStack
 * deployment. A zone typically corresponds to a single datacenter, although it
 * is permissible to have multiple zones in a datacenter. The benefit of
 * organizing infrastructure into zones is to provide physical isolation and
 * redundancy.
 */
@Entity
@Table(name = "ck_zone")
@SuppressWarnings("serial")
public class Zone implements Serializable {

    /** Id of the Zone. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Name of the Zone. */
    @Size(min = 4, max = 20)
    @Column(name = "name", nullable = false)
    private String name;

    /** Unique id for the Zone. */
    @Column(name = "uuid")
    private String uuid;

    /** id for the Domain. */
    //@OneToMany(mappedBy = "domain")
    @Column(name = "domain_id")
    private Long domainId;

    /** id for the Region. */
    //@OneToOne
    //@JoinColumn(name = "region_id", referencedColumnName = "id")
    @Column(name = "region_id")
    private Long regionId;

    /** IsActive attribute to verify Active or Inactive. */
    @Column(name = "is_active")
    private Boolean isActive;

    /** Version attribute to handle optimistic locking. */
    @Version
    @Column(name = "version")
    private Long version;

    /** Status attribute to verify status of the zone. */
    @Column(name = "status")
    private String status;

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
    private DateTime createdDateTime;

    /** Last modified date and time. */
    @LastModifiedDate
    private DateTime lastModifiedDateTime;

    /**
     * @return id the id of the zone
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     * the zone id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return name the name of the zone.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     * the zone name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid
     * the zone uuid to set.
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the domainId
     */
    public Long getDomainId() {
        return domainId;
    }

    /**
     * @param domainId
     * the domain Id to set
     */
    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    /**
     * @return the regionId
     */
    public Long getRegionId() {
        return regionId;
    }

    /**
     * @param regionId
     * the regionId to set.
     */
    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    /**
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * @param isActive
     * the isActive to set.
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * @param version
     * the version to set.
     */
    public void setVersion(Long version) {
        this.version = version;
    }


    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the createdBy.
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy
     * the createdBy to set.
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
     * @param updatedBy
     * the updatedBy to set.
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * @return the createdDateTime
     */
    public DateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * @param createdDateTime
     * the createdDateTime to set.
     */
    public void setCreatedDateTime(DateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * @return the lastModifiedDateTime
     */
    public DateTime getLastModifiedDateTime() {
        return lastModifiedDateTime;
    }

    /**
     * @param lastModifiedDateTime
     * the lastModifiedDateTime to set.
     */
    public void setLastModifiedDateTime(DateTime lastModifiedDateTime) {
        this.lastModifiedDateTime = lastModifiedDateTime;
    }

    /**
     * Convert JSONObject to domain entity.
     *
     * @param object json object
     * @return zone object
     * @throws JSONException unhandled json errors.
     */
    public static Zone convert(JSONObject object) throws JSONException {
        Zone zone = new Zone();
        zone.uuid = object.get("id").toString();
        zone.name = object.get("name").toString();

        return zone;
    }

    /**
     * Mapping zone entity object into list.
     *
     * @param zoneList list of Zones
     * @return zone mapping values.
     */
    public static Map<String, Zone> convert(List<Zone> zoneList) {
        Map<String, Zone> zoneMap = new HashMap<String, Zone>();

        for (Zone zone : zoneList) {
            zoneMap.put(zone.getUuid(), zone);
        }
        return zoneMap;
    }
}
