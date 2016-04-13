package ck.panda.domain.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Permissions is the authorization to allow the users for specific actions.
 */
@Entity
@Table(name = "permissions")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class Permission implements Serializable {

    /** Id of the Permission. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Action of the Permission. */
    @NotEmpty
    @Column(name = "action", nullable = false)
    private String action;

    /** Action key of the Permission. */
    @NotEmpty
    @Column(name = "action_key", nullable = false)
    private String actionKey;

    /** Description of the Permission. */
    @Column(name = "description")
    private String description;

    /** Assign module category. */
    @Column(name = "module")
    private Module module;

    /** Check whether Permission is in active state or in active state. */
    @Column(name = "is_active")
    private Boolean isActive;

    /** Status for Permission, whether it is Deleted, Disabled etc . */
    @Column(name = "status")
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

    /** Last modified date and time. */
    @LastModifiedDate
    @Column(name = "updated_date_time")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentZonedDateTime")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime updatedDateTime;

    /**
     * Default constructor.
     */
    public Permission() {
        super();
    }

    /**
     * Get the id of the permission.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id of the permission.
     *
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the action of the permission.
     *
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * Set the action of the permission.
     *
     * @param action action to set
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Get the actionKeyof the permission.
     *
     * @return the actionKey
     */
    public String getActionKey() {
        return actionKey;
    }

    /**
     * Set the actionKey of the permission.
     *
     * @param actionKey the actionKey to set
     */
    public void setActionKey(String actionKey) {
        this.actionKey = actionKey;
    }

    /**
     * Get the description of the permission.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of the permission.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the module of the permission.
     *
     * @return the module
     */
    public Module getModule() {
        return module;
    }

    /**
     * Set the module of the permission.
     *
     * @param module the module to set
     */
    public void setModule(Module module) {
        this.module = module;
    }

    /**
     * Get the isActive of the permission.
     *
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set the isActive of the permission.
     *
     * @param isActive the isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Get the status of the permission.
     *
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set the status of the permission.
     *
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Get the version of the permission.
     *
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Set the version of the permission.
     *
     * @param version the version to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get the createdBy of the permission.
     *
     * @return the createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the createdBy of the permission.
     *
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updatedBy of the permission.
     *
     * @return the updatedBy
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updatedBy of the permission.
     *
     * @param updatedBy the updatedBy to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the createdDateTime of the permission.
     *
     * @return the createdDateTime
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the createdDateTime of the permission.
     *
     * @param createdDateTime the createdDateTime to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updatedDateTime of the permission.
     *
     * @return the updatedDateTime
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updatedDateTime of the permission.
     *
     * @param updatedDateTime the updatedDateTime to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Enumeration module list.
     */
    public enum Module {

        /** Instance module constant. */
        INSTANCE,
        /** Storage module constant. */
        STORAGE,
        /** Network module constant. */
        NETWORK,
        /** SSH keys module constant. */
        SSH_KEYS,
        /** Quota limit module constant. */
        QUOTA_LIMIT,
        /** VPC module constant. */
        VPC,
        /** Template module constant. */
        TEMPLATES,
        /** Additional Services module constant. */
        ADDITIONAL_SERVICE,
        /** Project module constant. */
        PROJECTS,
        /** Application module constant. */
        APPLICATION,
        /** Department module constant. */
        DEPARTMENT,
        /** Roles module constant. */
        ROLES,
        /** User module constant. */
        USER,
        /** Report module constant. */
        REPORT,
        /** billing module constant. */
        BILLING;
    }

    /**
     * Enumeration status for Permission.
     */
    public enum Status {
        /** Enabled status is used to list permissions through out the application. */
        ENABLED,

        /** Deleted status make permission as soft deleted and it will not list on the applicaiton. */
        DELETED
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
