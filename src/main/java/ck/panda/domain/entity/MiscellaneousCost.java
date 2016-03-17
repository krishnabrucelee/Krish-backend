package ck.panda.domain.entity;

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

@Entity
@Table(name = "miscellaneous_cost")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class MiscellaneousCost {

     /** Unique ID of the miscellaneous cost. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Zone id for this offering. */
    @JoinColumn(name = "zone_id", referencedColumnName = "id")
    @ManyToOne
    private Zone zone;

    /** Created by user. */
    @CreatedBy
    @Column(name = "created_user_id")
    private Long createdBy;

    /** Last updated by user. */
    @LastModifiedBy
    @Column(name = "updated_user_id")
    private Long updatedBy;

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

    /** Cost of the template. */
    @Column(name = "cost", columnDefinition = "Decimal(10,4)")
    private Double costperGB;

    /** Version attribute to handle optimistic locking. */
    @Version
    @Column(name = "version")
    private Long version;

    /** Check whether department is active state or in active state. */
    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "cost_type")
    private CostTypes costType;

    @Column(name = "unit_type")
    private UnitType unitType;

    /** Template current state. */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    /** Status enum type used to list the status values. */
    public enum Status {
        /** Template status as ACTIVE. */
        ACTIVE,
        /** Template status as INACTIVE. */
        INACTIVE
    }

    /** Types of the cost .*/
    public enum CostTypes {

        /** IpAddress cost .*/
        IPADDRESS,

        /** Template cost .*/
        TEMPLATE,

        /** Vm snapshot cost. */
        VMSNAPSHOT,

        /** Volume snapshot cost .*/
        VOLUMESNAPSHOT

    }

    public enum UnitType {

        /** GB unit */
        GB,

        /** Ip Address per unit */
        IP
    }
    /**
     * Get the id.
     *
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id.
     *
     * @param id - the Long to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /** @return the zone. */
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
     * Get the created by.
     *
     * @return createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the created by.
     *
     * @param createdBy - the User entity to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updated by.
     *
     * @return updatedBy
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updated by.
     *
     * @param updatedBy - the User entity to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the created date time.
     *
     * @return the createdDateTime
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the created date time.
     *
     * @param createdDateTime - the DateTime to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updated date time.
     *
     * @return updatedDateTime
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updated date time.
     *
     * @param updatedDateTime - the DateTime to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get version of the cost.
     *
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Set version of the cost.
     *
     * @param version the version to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get cost per gb.
     *
     * @return the costperGB
     */
    public Double getCostperGB() {
        return costperGB;
    }

    /**
     * Set cost per gb.
     *
     * @param costperGB to set
     */
    public void setCostperGB(Double costperGB) {
        this.costperGB = costperGB;
    }

    /**
     * Get the costType .
     *
     * @return the costType
     */
    public CostTypes getCostType() {
        return costType;
    }

    /**
     * Set the costType .
     *
     * @param costType to set
     */
    public void setCostType(CostTypes costType) {
        this.costType = costType;
    }

    /**
     * Get the status.
     *
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set the the status.
     *
     * @param status  to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Get state of the department.
     *
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set state of the department.
     *
     * @param isActive the isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * @return the unitType
     */
    public UnitType getUnitType() {
        return unitType;
    }

    /**
     * @param unitType the unitType to set
     */
    public void setUnitType(UnitType unitType) {
        this.unitType = unitType;
    }
 }
