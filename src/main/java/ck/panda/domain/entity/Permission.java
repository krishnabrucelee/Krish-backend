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
     * Get role list of the permission.
     *
     * @return the roleList
     */
    public List<Role> getRoleList() {
       return roleList;
    }

    /**
     * Set the role list of the permission.
     *
     * @param roleList the roleList to set
     */
    public void setRoleList(List<Role> roleList) {
       this.roleList = roleList;
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
    public User getCreatedBy() {
       return createdBy;
    }

    /**
     * Set the createdBy of the permission.
     *
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(User createdBy) {
       this.createdBy = createdBy;
    }

    /**
     * Get the updatedBy of the permission.
     *
     * @return the updatedBy
     */
    public User getUpdatedBy() {
       return updatedBy;
    }

    /**
     * Set the updatedBy of the permission.
     *
     * @param updatedBy the updatedBy to set
     */
    public void setUpdatedBy(User updatedBy) {
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

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((module == null) ? 0 : module.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Permission)) {
			return false;
		}
		Permission other = (Permission) obj;
		if (action == null) {
			if (other.action != null) {
				return false;
			}
		} else if (!action.equals(other.action)) {
			return false;
		}
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (module != other.module) {
			return false;
		}
		return true;
	}
}
