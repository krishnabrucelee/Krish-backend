package ck.panda.domain.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
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
    private String status;

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

    /** update status when delete an entity. */
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

    /**
     * Get the name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name.
     *
     * @param name - the String to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the department.
     *
     * @return department
     */
    public Department getDepartment() {
        return department;
    }

    /**
     * Set the department.
     *
     * @param department - object to set
     */
    public void setDepartment(Department department) {
        this.department = department;
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
     * @param description - String to set
     */
    public void setDescription(String description) {
        this.description = description;
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
     * @param version - the Long to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get the createdBy.
     *
     * @return createdBy
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the createdBy.
     *
     * @param createdBy - the User to set
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updatedBy.
     *
     * @return updatedBy
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updatedBy.
     *
     * @param updatedBy - the User to set
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the createdDateTime.
     *
     * @return createdDateTime
     */
    public DateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the createdDateTime.
     *
     * @param createdDateTime - the DateTime to set
     */
    public void setCreatedDateTime(DateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updatedDateTime.
     *
     * @return updatedDateTime
     */
    public DateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updatedDateTime.
     *
     * @param updatedDateTime - the DateTime to set
     */
    public void setUpdatedDateTime(DateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get the isActive status.
     *
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set the isActive status.
     *
     * @param isActive - the isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Get the initial status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set the initial status.
     *
     * @param status - the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }


}
