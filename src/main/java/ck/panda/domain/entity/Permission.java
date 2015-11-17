package ck.panda.domain.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
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
@Table(name = "ck_permission")
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

    /** Description of the Permission. */
    @Column(name = "description")
    private String description;

    /** Assign module category. */
    @Column(name = "module")
    private Module module;

    /** List of role for permission. */
    @ManyToMany
    private List<Role> roleList;

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

    /**
     * Default constructor.
     */
    public Permission() {
        super();
    }

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
     * @return the action
     */
    public String getAction() {
       return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(String action) {
       this.action = action;
    }

    /**
     * @return the description
     */
    public String getDescription() {
       return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
       this.description = description;
    }

    /**
     * @return the module
     */
    public Module getModule() {
       return module;
    }

    /**
     * @param module the module to set
     */
    public void setModule(Module module) {
       this.module = module;
    }

    /**
     * @return the roleList
     */
    public List<Role> getRoleList() {
       return roleList;
    }

    /**
     * @param roleList the roleList to set
     */
    public void setRoleList(List<Role> roleList) {
       this.roleList = roleList;
    }

    /**
     * @return the isActive
     */
    public Boolean getIsActive() {
       return isActive;
    }

    /**
     * @param isActive the isActive to set
     */
    public void setIsActive(Boolean isActive) {
       this.isActive = isActive;
    }

    /**
     * @return the status
     */
    public Status getStatus() {
       return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Status status) {
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
    public ZonedDateTime getCreatedDateTime() {
       return createdDateTime;
    }

    /**
     * @param createdDateTime the createdDateTime to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
       this.createdDateTime = createdDateTime;
    }

    /**
     * @return the updatedDateTime
     */
    public ZonedDateTime getUpdatedDateTime() {
       return updatedDateTime;
    }

    /**
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
    REPORT;
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
