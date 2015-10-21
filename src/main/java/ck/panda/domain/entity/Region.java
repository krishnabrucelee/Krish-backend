package ck.panda.domain.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * A region is the largest available organizational unit within a CloudStack
 * deployment. A region is made up of several availability Regions, where each
 * Region is roughly equivalent to a datacenter.
 *
 */
@Entity
@Table(name = "ck_region")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class Region implements Serializable {

    /** Id of the region. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Unique ID from Cloud Stack. */
    @NotEmpty
    @Column(name = "uuid")
    private String uuid;

    /** Name of the region. */
    @Column(name = "name")
    private String name;

    /** End point of the region. */
    @Column(name = "end_point")
    private String endPoint;

    /** update status when delete an entity. */
    @Column(name = "is_active")
    private Boolean isActive;

    /** Status of the region. */
    @Column(name = "status")
    private String status;

    /** Version attribute to handle optimistic locking. */
    @Version
    @Column(name = "version")
    private Long version;

    /** Created by user. */
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    @CreatedBy
    @OneToOne
    private User createdBy;

    /** Last updated by user. */
    @JoinColumn(name = "updated_by", referencedColumnName = "id")
    @LastModifiedBy
    @OneToOne
    private User updatedBy;

    /** Created date and time. */
    @Column(name = "created_date_time")
    @CreatedDate
    private DateTime createdDateTime;

    /** Updated date and time. */
    @Column(name = "updated_date_time")
    @LastModifiedDate
    private DateTime updatedDateTime;

    /**
     * Get the id.
     *
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id of the region.
     *
     * @param id - the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the uuid.
     *
     * @return uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the uuid.
     *
     * @param uuid - the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get the name of the region.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the region.
     *
     * @param name - the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the endPoint.
     *
     * @return endPoint
     */
    public String getEndPoint() {
        return endPoint;
    }

    /**
     * Set the endPoint.
     *
     * @param endPoint - the endPoint to set
     */
    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    /**
     * Get the isActive.
     *
     * @return isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set the isActive.
     *
     * @param isActive - the isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
     * Get the version.
     *
     * @return version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Set the version.
     *
     * @param version - the version to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get the createdBy.
     *
     * @return createdBy
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the createdBy.
     *
     * @param createdBy - the createdBy to set
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updatedBy.
     *
     * @return updatedBy
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updatedBy.
     *
     * @param updatedBy - the updatedBy to set
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the createdDateTime.
     *
     * @return createdDateTime
     */
    public DateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the createdDateTime.
     *
     * @param createdDateTime - the createdDateTime to set
     */
    public void setCreatedDateTime(DateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updatedDateTime.
     *
     * @return updatedDateTime
     */
    public DateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updatedDateTime.
     *
     * @param updatedDateTime - the updatedDateTime to set
     */
    public void setUpdatedDateTime(DateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Convert JSONObject to region entity.
     *
     * @param object json object
     * @return region entity object
     * @throws JSONException unhandled json errors.
     */
    public static Region convert(JSONObject object) throws JSONException {
        Region region = new Region();
        region.uuid = object.get("id").toString();
        region.name = object.get("name").toString();

        return region;
    }

    /**
     * Mapping region entity object into list.
     *
     * @param regionList list of regions
     * @return region mapped values.
     */
    public static Map<String, Region> convert(List<Region> regionList) {
        Map<String, Region> regionMap = new HashMap<String, Region>();

        for (Region region : regionList) {
            regionMap.put(region.getName(), region);
        }
        return regionMap;
    }

}
