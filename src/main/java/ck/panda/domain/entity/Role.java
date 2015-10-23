package ck.panda.domain.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * Roles are categorize the departments with different permissions. We restrict
 * the user based on the permission and the permission assigned with Role and
 * give access based on the assigned permissions.
 *
 */
@Entity
@Table(name = "ck_role")
@SuppressWarnings("serial")
public class Role implements Serializable {

    /** Id of the Department. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Name of the Role. */
    @NotEmpty
    @Size(min = 4, max = 20)
    @Column(name = "name", nullable = false)
    private String name;

    /** Roles Department. */
    @ManyToOne
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    private Department department;

    /** Description of the Department. */
    @Column(name = "description")
    private String description;

    /** Version attribute to handle optimistic locking. */
    @Version
    @Column(name = "version")
    private Long version;

    /** Status attribute to verify status of the Role. */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

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
    private DateTime createdDateTime;

    /** Last updated date and time. */
    @LastModifiedDate
    @Column(name = "updated_date_time")
    private DateTime updatedDateTime;

    /** An active attribute is to check whether the role is active or not. */
    @Column(name = "is_active", columnDefinition = "tinyint default 1")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean isActive;

    /**
     * To set the default value while creating tables in database.
     */
    @PrePersist
    void preInsert() {
        this.isActive = true;
    }

    /**
     * Enum type for Role Status.
     *
     */
    public enum Status {
        /** Roles will be in a Enabled State. */
        ENABLED,
        /** Roles will be in a Disabled State. */
        DISABLED
    }

    /**
     * Default constructor.
     */
    protected Role() {
        super();
    }

    /**
     * Parameterized constructor.
     *
     * @param name to set
     */
    public Role(String name) {
        super();
        this.name = name;
    }

	/**
	 * Get the id of the role
	 *
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Set the id of the role
	 *
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Get the name of the role
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the role
	 *
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the department of the role
	 *
	 * @return the department
	 */
	public Department getDepartment() {
		return department;
	}

	/**
	 * Set the department of the role
	 *
	 * @param department the department to set
	 */
	public void setDepartment(Department department) {
		this.department = department;
	}

	/**
	 * Get the description of the role
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description of the role
	 *
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get the version of the role
	 *
	 * @return the version
	 */
	public Long getVersion() {
		return version;
	}

	/**
	 * Set the version of the role
	 *
	 * @param version the version to set
	 */
	public void setVersion(Long version) {
		this.version = version;
	}

	/**
	 * Get the status of the role
	 *
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Set the status of the role
	 *
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * Get the created by user of the role
	 *
	 * @return the createdBy
	 */
	public User getCreatedBy() {
		return createdBy;
	}

	/**
	 * Set the created by user of the role
	 *
	 * @param createdBy the created by to set
	 */
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * Get the updated by user of the role
	 *
	 * @return the updatedBy
	 */
	public User getUpdatedBy() {
		return updatedBy;
	}

	/**
	 * Set the updated by user of the role
	 *
	 * @param updatedBy the updated by to set
	 */
	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}

	/**
	 * Get the created date time of the role
	 *
	 * @return the createdDateTime
	 */
	public DateTime getCreatedDateTime() {
		return createdDateTime;
	}

	/**
	 * Set the created date time of the role
	 *
	 * @param createdDateTime the created date time to set
	 */
	public void setCreatedDateTime(DateTime createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	/**
	 * Get the updated date time of the role
	 *
	 * @return the updatedDateTime
	 */
	public DateTime getUpdatedDateTime() {
		return updatedDateTime;
	}

	/**
	 * Set the updated date time of the role
	 *
	 * @param updatedDateTime the updated date time to set
	 */
	public void setUpdatedDateTime(DateTime updatedDateTime) {
		this.updatedDateTime = updatedDateTime;
	}

	/**
	 * Get the is active of the role
	 *
	 * @return the isActive
	 */
	public Boolean getIsActive() {
		return isActive;
	}

	/**
	 * Set the is active of the role
	 *
	 * @param isActive the is active to set
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

}
