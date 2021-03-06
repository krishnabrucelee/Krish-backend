package ck.panda.domain.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import ck.panda.domain.entity.StorageOffering.Status;

/**
 * Storage offering pricing that includes cost for disk and iops for per zone.
 *
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "storage_offerings_cost")
@SuppressWarnings("serial")
public class StorageOfferingCost implements Serializable {

    /**
     * Unique ID of the disk offering cost.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Id of the storage offering. */
    @Column(name = "storage_id")
    private Long storageId;

    /** Total cost of the storage offering. */
    @Column(name = "total_cost")
    private Double totalCost;

    /**
     * The Zone object, this storage offering belongs to.
     */
    @JoinColumn(name = "zone_id", referencedColumnName = "id",insertable = false, updatable = false)
    @ManyToOne
    private Zone zone;


    /** Id of the zone. */
    @Column(name = "zone_id")
    private Long zoneId;

    /**
     * Cost per month usage.
     */
    @Column(name = "non_custom_disk")
    private Double costPerMonth;

    /**
     * Cost for 1 Gb per month usage.
     */
    @Column(name = "custom_disk")
    private Double costGbPerMonth;

    /** Status attribute to verify status of the Storage offering. */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    /** Version attribute to handle optimistic locking. */
    @Version
    @Column(name = "version")
    private Long version;

    /** Created by user. */
    @CreatedBy
    @Column(name = "created_by")
    private Long createdBy;

    /** Last updated by user. */
    @LastModifiedBy
    @Column(name = "updated_by")
    private Long updatedBy;

    /** Created date and time. */
    @CreatedDate
    @Column(name = "created_date_time")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentZonedDateTime")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime createdDateTime;

    /** Last updated date and time. */
    @LastModifiedDate
    @Column(name = "updated_date_time")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentZonedDateTime")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime updatedDateTime;

    /** An active attribute is to check whether the role is active or not. */
    @Column(name = "is_active", columnDefinition = "tinyint default 1")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean isActive;

    /**
     * Get the id of the Storage offering cost.
     *
     * @return the id of the Storage offering cost.
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id of the Storage offering cost.
     *
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the zone of the Storage offering cost.
     *
     * @return the zone of the Storage offering cost.
     */
    public Zone getZone() {
        return zone;
    }

    /**
     * Set the zone of the Storage offering cost.
     *
     * @param zone the zone to set
     */
    public void setZone(Zone zone) {
        this.zone = zone;
    }

    /**
     * Get the cost per month of the Storage offering cost.
     *
     * @return the costPerMonth of the Storage offering cost.
     */
    public Double getCostPerMonth() {
        return costPerMonth;
    }

    /**
     * Set the cost per month of the Storage offering cost.
     *
     * @param costPerMonth the costPerMonth to set
     */
    public void setCostPerMonth(Double costPerMonth) {
        this.costPerMonth = costPerMonth;
    }

    /**
     * Get the cost gb per month of the Storage offering cost.
     *
     * @return the costGbPerMonth of the Storage offering cost.
     */
    public Double getCostGbPerMonth() {
        return costGbPerMonth;
    }

    /**
     * Set the cost gb per month of the Storage offering cost.
     *
     * @param costGbPerMonth the costGbPerMonth to set
     */
    public void setCostGbPerMonth(Double costGbPerMonth) {
        this.costGbPerMonth = costGbPerMonth;
    }

    /**
     * Get the status of the Storage offering cost.
     *
     * @return the status of the Storage offering cost.
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set the status of the Storage offering cost.
     *
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Get the version of the Storage offering cost.
     *
     * @return the version of the Storage offering cost.
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Set the version of the Storage offering cost.
     *
     * @param version the version to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get the created by of the storage offering.
     *
     * @return the createdBy of the storage offering
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the created by of the storage offering cost.
     *
     * @param createdBy the created by to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updated by of the storage offering cost.
     *
     * @return the updatedBy of the storage offering cost
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updated by of the storage offering cost.
     *
     * @param updatedBy the updated by to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the created date time of the storage offering cost.
     *
     * @return the createdDateTime of the storage offering cost
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the created date time of the storage offering cost.
     *
     * @param createdDateTime the created date time to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updated date time of the storage offering cost.
     *
     * @return the updatedDateTime of the storage offering cost
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updated date time of the storage offering cost.
     *
     * @param updatedDateTime the updated date time to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get the is active of the storage offering cost.
     *
     * @return the isActive of the storage offering cost
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set the is active of the storage offering cost.
     *
     * @param isActive the is active to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Get the total cost.
     *
     * @return the totalCost
     */
    public Double getTotalCost() {
        return totalCost;
    }

    /**
     * Set the totalCost.
     *
     * @param totalCost  to set
     */
    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    /**
     * Get the storage offering id.
     *
     * @return the storageId
     */
    public Long getStorageId() {
        return storageId;
    }

    /**
     * Set the storage offering id.
     *
     * @param storageId  to set
     */
    public void setStorageId(Long storageId) {
        this.storageId = storageId;
    }

    /**
     * Get zone id of the offering.
     *
     * @return the zoneId
     */
    public Long getZoneId() {
        return zoneId;
    }

    /**
     * Set zone id of the offering.
     *
     * @param zoneId to set
     */
    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }


}
