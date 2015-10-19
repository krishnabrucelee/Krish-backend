package ck.panda.domain.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import org.joda.time.DateTime;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * A region is the largest available organizational unit within a CloudStack
 * deployment. A region is made up of several availability zones, where each
 * zone is roughly equivalent to a datacenter.
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

    /** unique id of the region in cloudStack. */
    @Column(name = "region_id")
    private Long regionId;

    /** Name of the region. */
    @Column(name = "region_name")
    private String regionName;

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
    private User createdBy;

    /** Last updated by user. */
    private User updatedBy;

    /** Created date and time. */
    private DateTime createdDateTime;

    /** Updated date and time. */
    private DateTime updatedDateTime;

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
     * @return the regionId
     */
    public Long getRegionId() {
        return regionId;
    }

    /**
     * @param regionId the regionId to set
     */
    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    /**
     * @return the regionName
     */
    public String getRegionName() {
        return regionName;
    }

    /**
     * @param regionName the regionName to set
     */
    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    /**
     * @return the endPoint
     */
    public String getEndPoint() {
        return endPoint;
    }

    /**
     * @param endPoint the endPoint to set
     */
    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    /**
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * @param isActive the isActive to set    *
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
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(Long version) {
        this.version = version;
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
    public DateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * @param createdDateTime the createdDateTime to set
     */
    public void setCreatedDateTime(DateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * @return the updatedDateTime
     */
    public DateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * @param updatedDateTime the updatedDateTime to set
     */
    public void setUpdatedDateTime(DateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

}
