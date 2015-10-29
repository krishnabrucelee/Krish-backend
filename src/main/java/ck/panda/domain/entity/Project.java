package ck.panda.domain.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
 * Projects are used to organize people and resources. CloudStack users within a single domain can group themselves
 * into project teams so they can collaborate and share virtual resources such as VMs, snapshots, templates, data
 * disks, and IP addresses. CloudStack tracks resource usage per project as well as per user, so the usage can
 * be billed to either a user account or a project.
 */
@Entity
@Table(name = "projects")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class Project implements Serializable {

    /** Id of the project. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Name of the Project. */
    @NotEmpty
    @Size(min = 4, max = 20)
    @Column(name = "name", nullable = false)
    private String name;

    /** Name of the project description. */
    @Column(name = "description")
    private String description;

    /** Project owner id. */
    @NotNull
    @JoinColumn(name = "project_owner_id", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.ALL)
    private User projectOwner;

    /** List of users for an project. */
    @OneToMany(cascade = CascadeType.ALL)
    private List<User> userList;

    /** Project domain id. */
    @NotNull
    @JoinColumn(name = "domain_id", referencedColumnName = "id")
    @ManyToOne(targetEntity = Domain.class, fetch = FetchType.EAGER)
    private Domain domain;

    /** Project department id. */
    @NotNull
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER,targetEntity = Department.class )
    private Department department;

    /** Project is whether active or disable. */
    @NotNull
    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT DEFAULT FALSE")
    private Boolean isActive;

    /** Status for project, whether it is Deleted, Enabled etc . */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     * Enumeration status for Project.
     */
    public enum Status {
        /** Enabled status is used to list projects through out the application. */
        ENABLED,

        /** Deleted status make projects as soft deleted and it will not list on the applicaiton. */
        DELETED
    }

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
     * Get the id.
     *
     * @return the id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id.
     *
     * @param id - the id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the name.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name.
     *
     * @param name the name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the description.
     *
     * @return the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description.
     *
     * @param description the description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the projectOwner.
     *
     * @return the projectOwner.
     */
    public User getProjectOwner() {
        return projectOwner;
    }

    /**
     * Set the project owner.
     *
     * @param projectOwner the project owner to set.
     */
    public void setProjectOwner(User projectOwner) {
        this.projectOwner = projectOwner;
    }

    /**
     * Get the list of user.
     *uper.toString();
     * @return the userList.
     */
    public List<User> getUserList() {
        return userList;
    }

    /**
     * Set the list of user.
     *
     * @param userList - the userList to set.
     */
    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    /**
     * Get the domain.
     *
     * @return the domain.
     */
    public Domain getDomain() {
        return domain;
    }

    /**
     * Set the domain.
     *
     * @param domain the domain to set.
     */
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    /**
	 * Get the department.
	 *
	 * @return the department.
	 */
	public Department getDepartment() {
		return department;
	}

	/**
	 * Set the department.
	 *
	 * @param department - the department to set.
	 */
	public void setDepartment(Department department) {
		this.department = department;
	}

	/**
     * Get the status of project.
     *
     * @return the status.
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set the status of project.
     *
     * @param isActive the status to set.
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Get the version count.
     *
     * @return the version.
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Set the version count.
     *
     * @param version the version count to set.
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get the created user details.
     *
     * @return the createdBy.
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the created user details.
     *
     * @param createdBy the createdBy to set.
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the last updated user details.
     *
     * @return the updatedBy.
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the last updated user details.
     *
     * @param updatedBy the updatedBy to set.
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the created date and time.
     *
     * @return the createdDateTime.
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the created date and time.
     *
     * @param createdDateTime the createdDateTime to set.
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updated date and time.
     *
     * @return the updatedDateTime.
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updated date and time.
     *
     * @param updatedDateTime the updatedDateTime to set.
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

	/**
	 * Get the status.
	 *
	 * @return the status.
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Set the status.
	 *
	 * @param status - the status to set.
	 */
	public void setStatus(Status status) {
		this.status = status;
	}


	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
