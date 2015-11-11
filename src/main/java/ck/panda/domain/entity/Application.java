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
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Application consists of different application types. Application types are unique to domain.
 * Application types may vary with respect to domain.
 *
 */
@Entity
@Table(name = "ck_application")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class Application implements Serializable {

    /** Status enum type used to list the status values. */
    public enum Status {
        /** Application status as Enabled. */
        ENABLED,
        /** Application status as Disabled. */
        DISABLED
    }

    /** Id of the Application. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Type of the Application. */
    @NotEmpty
    @Size(min = 4, max = 20)
    @Column(name = "type", nullable = false)
    private String type;

    /** Description of the Application. */
    @Column(name = "description")
    private String description;

    //TODO Yasin: JDI
    /** Application Domain. */
    @ManyToOne
    @JoinColumn(name = "domain_id", referencedColumnName = "id")
    private Domain domain;

    /** Update status when delete an entity. */
    @Column(name = "is_active")
    private Boolean isActive;

    /** Application current state. */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    /** Version attribute to handle optimistic locking. */
    @Version
    @Column(name = "version")
    private Long version;

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

    /** Default constructor. */
    public Application() {
        super();
    }

    /**
     * Get the id of the application.
     *
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id of the application.
     *
     * @param id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the application type.
     *
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * Set the application type.
     *
     * @param type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get the description.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description.
     *
     * @param description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the domain.
     *
     * @return domain
     */
    public Domain getDomain() {
        return domain;
    }

    /**
     * Set the domain.
     *
     * @param domain to set
     */
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    /**
     * Get the active status.
     *
     * @return isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set the active status.
     *
     * @param isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Get the status of the application.
     *
     * @return status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set the status of the application.
     *
     * @param status to set
     */
    public void setStatus(Status status) {
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
     * @param version to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get the created user.
     *
     * @return createdBy
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the created user.
     *
     * @param createdBy to set
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updated user.
     *
     * @return updatedBy
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updated user.
     *
     * @param updatedBy to set
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the created date time.
     *
     * @return createdDateTime
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the created date time.
     *
     * @param createdDateTime to set
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
     * @param updatedDateTime to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /** Set the default value for isActive before executing entity manager. */
    //TODO Yasin: we should not set default values here. move it to service layer.
    @PrePersist
    void preInsert() {
        this.isActive = true;
    }

}
