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

    /**
     * The Zone ID, this disk offering belongs to. Ignore this information as it is not currently applicable.
     */
    @JoinColumn(name = "zone_id", referencedColumnName = "Id")
    @ManyToOne
    private Zone zone;

    /**
     * Cost per month usage.
     */
    @Column(name = "cost_per_hour_disk")
    private Double costPerMonth;

    /**
     * Cost for 1 Gb per month usage.
     */
    @Column(name = "cost_gb_per_month", columnDefinition = "bigint(20) default 0")
    private Double costGbPerMonth;

    /**
     * Cost per month usage.
     */
    @Column(name = "cost_per_hour_iops")
    private Double costPerIops;

    /**
     * Cost for 1 Iops per month usage.
     */
    @Column(name = "cost_iops_per_month", columnDefinition = "bigint(20) default 0")
    private Double costIopsPerMonth;

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
     * Get the cost per iops of the Storage offering cost.
     *
     * @return the costPerIops of the Storage offering cost.
     */
    public Double getCostPerIops() {
        return costPerIops;
    }

    /**
     * Set the cost per iops of the Storage offering cost.
     *
     * @param costPerIops the costPerIops to set
     */
    public void setCostPerIops(Double costPerIops) {
        this.costPerIops = costPerIops;
    }

    /**
     * Get the cost iops per month of the Storage offering cost.
     *
     * @return the costIopsPerMonth of the Storage offering cost.
     */
    public Double getCostIopsPerMonth() {
        return costIopsPerMonth;
    }

    /**
     * Set the cost iops per month of the Storage offering cost.
     *
     * @param costIopsPerMonth the costIopsPerMonth to set
     */
    public void setCostIopsPerMonth(Double costIopsPerMonth) {
        this.costIopsPerMonth = costIopsPerMonth;
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

}
