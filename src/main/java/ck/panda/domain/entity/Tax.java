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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Tax contains the name for different offerings.
 */
@Entity
@Table(name = "tax")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class Tax implements Serializable {

    /** Id of the BillableItem. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Name of the tax */
    private String name;


    /** Description of the tax */
    private String description;

    /**
     * The percentage value of the tax.
     */
    private Double percentage;

    /** Whether BillableItem is in active state or in active state. */
    @Column(name = "is_active")
    private Boolean isActive;

    /** Status for BillableItem, whether it is Deleted, Disabled etc . */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    /** Version attribute to handle optimistic locking. */
    @Version
    @Column(name = "version")
    private Long version;

    /** Created by user. */
    @CreatedBy
    @Column(name = "created_user_id")
    private Long createdBy;

    /** Last updated by user. */
    @LastModifiedBy
    @Column(name = "upated_user_id")
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

    /**
     * Default constructor.
     */
    public Tax() {
        super();
    }

    /**
     * Parameterized constructor.
     *
     * @param name to set
     */
    public Tax(String name) {
        super();
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
     * @param id the Long to set
     */
    public void setId(Long id) {
        this.id = id;
    }



    /**
     * Get the tax name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the tax name.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }



    /**
     * Get the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the tax percentage.
     *
     * @return the percentage
     */
    public Double getPercentage() {
        return percentage;
    }

    /**
     * Set the tax percentage.
     *
     * @param percentage the percentage to set
     */
    public void setPercentage(Double percentage) {
        this.percentage = percentage;
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
     * @param version the Long to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get the createdBy.
     *
     * @return createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the createdBy.
     *
     * @param createdBy the User to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updatedBy.
     *
     * @return updatedBy
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updatedBy.
     *
     * @param updatedBy the User to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the createdDateTime.
     *
     * @return createdDateTime
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the createdDateTime.
     *
     * @param createdDateTime the DateTime to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updatedDateTime.
     *
     * @return updatedDateTime
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updatedDateTime.
     *
     * @param updatedDateTime the DateTime to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get is Active state of the Tax.
     *
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set is Active state of the Tax.
     *
     * @param isActive the isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Get the department status.
     *
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set the department status.
     *
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }


    /**
     * Enumeration status for Tax.
     */
    public enum Status {
        /** Enabled status is used to list departments through out the application. */
        ENABLED,

        /** Deleted status make department as soft deleted and it will not list on the applicaiton. */
        DELETED
    }

}