package ck.panda.domain.entity;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Application Classes are dependent for application.
 *
 * @author Ibrahim
 */
@Entity
@Table(name = "ck_application_class")
public class ApplicationClass {
    /**
     * Unique Id.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    public Long Id;

    /**
     * Name of the application class.
     */
    @NotEmpty
    @Size(min = 3, max = 30)
    @Column(name = "application_class", nullable = false)
    public String className;

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
    @Column(name = "last_modified_date_time")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentZonedDateTime")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime lastModifiedDateTime;

    /**
     * Get the application class ID.
     *
     * @return the id
     */
    public Long getId() {
        return Id;
    }

    /**
     * Set the id of application class.
     *
     * @param id
     *            to set
     *
     */
    public void setId(Long id) {
        Id = id;
    }

    /**
     * Get the application class name.
     *
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * Set the application class name.
     *
     * @param className
     *            to set
     *
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Get the version count.
     *
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /**
     *
     * Set the version count.
     *
     * @param version
     *            to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get the created user id.
     *
     * @return the createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the created user id.
     *
     * @param createdBy
     *            the createdBy to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     *
     * Get the modified user id.
     *
     * @return the updatedBy
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the modified user id.
     *
     * @param updatedBy
     *            to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the created date and time.
     *
     * @return the createdDateTime
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the created date and time.
     *
     * @param createdDateTime
     *            to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the last modified date and time.
     *
     * @return the lastModifiedDateTime
     */
    public ZonedDateTime getLastModifiedDateTime() {
        return lastModifiedDateTime;
    }

    /**
     * Set the last modified date and time.
     *
     * @param lastModifiedDateTime
     *            the lastModifiedDateTime to set
     */
    public void setLastModifiedDateTime(ZonedDateTime lastModifiedDateTime) {
        this.lastModifiedDateTime = lastModifiedDateTime;
    }

}
