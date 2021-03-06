package ck.panda.domain.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * Roles are categorize the departments with different permissions. We restrict the user based on the permission and the
 * permission assigned with Role and give access based on the assigned permissions.
 */
@Entity
@Table(name = "roles")
@SuppressWarnings("serial")
public class Role implements Serializable {

    /** Id of the Role. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Name of the Role. */
    @NotEmpty
    @Column(name = "name", nullable = false)
    private String name;

    /** Domain of the role. */
    @JoinColumn(name = "domain_id", referencedColumnName = "id", updatable = false, insertable = false)
    @ManyToOne
    private Domain domain;

    /** Domain id of the role. */
    @Column(name = "domain_id")
    private Long domainId;

    /** Department of the Role. */
    @JoinColumn(name = "department_id", referencedColumnName = "id", updatable = false, insertable = false)
    @ManyToOne
    private Department department;

    /** Department id of the Role. */
    @Column(name = "department_id")
    private Long departmentId;

    /** Description of the Role. */
    @Column(name = "description")
    private String description;

    /** Permission list of the role. */
    @ManyToMany
    private List<Permission> permissionList;

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
    @Column(name = "created_by")
    private Long createdBy;

    /** Last updated by user. */
    @LastModifiedBy
    @JoinColumn(name = "updated_by")
    private Long updatedBy;

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

    /** Share of the template. */
    @Transient
    private Boolean syncFlag;

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
        /** Roles will be in a Disabled State. */
        DISABLED,
        /** Roles will be in a Enabled State. */
        ENABLED
    }

    /**
     * Default constructor.
     */
    public Role() {
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
     * Get the id of the role.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id of the role.
     *
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the name of the role.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the role.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the domain of the Role.
     *
     * @return the domain of Role.
     */
    public Domain getDomain() {
        return domain;
    }

    /**
     * Set the domain of the Role.
     *
     * @param domain the domain to set
     */
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    /**
     * Get the domainId of the Role.
     *
     * @return the domainId of Role.
     */
    public Long getDomainId() {
        return domainId;
    }

    /**
     * Set the domainId of the Role.
     *
     * @param domainId the domainId to set
     */
    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    /**
     * Get the department of the role.
     *
     * @return the department
     */
    public Department getDepartment() {
        return department;
    }

    /**
     * Set the department of the role.
     *
     * @param department the department to set
     */
    public void setDepartment(Department department) {
        this.department = department;
    }

    /**
     * Get the departmentId of the Role.
     *
     * @return the departmentId of Role.
     */
    public Long getDepartmentId() {
        return departmentId;
    }

    /**
     * Set the departmentId of the Role.
     *
     * @param departmentId the departmentId to set
     */
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * Get the description of the role.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of the role.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the permissionList of the role.
     *
     * @return the permissionList.
     */
    public List<Permission> getPermissionList() {
        return permissionList;
    }

    /**
     * Set the permissionList of the role.
     *
     * @param permissionList the permissionList to set.
     */
    public void setPermissionList(List<Permission> permissionList) {
        this.permissionList = permissionList;
    }

    /**
     * Get the version of the role.
     *
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Set the version of the role.
     *
     * @param version the version to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get the status of the role.
     *
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set the status of the role.
     *
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Get the created by user of the role.
     *
     * @return the createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the created by user of the role.
     *
     * @param createdBy the created by to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updated by user of the role.
     *
     * @return the updatedBy
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updated by user of the role.
     *
     * @param updatedBy the updated by to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the created date time of the role.
     *
     * @return the createdDateTime
     */
    public DateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the created date time of the role.
     *
     * @param createdDateTime the created date time to set
     */
    public void setCreatedDateTime(DateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updated date time of the role.
     *
     * @return the updatedDateTime
     */
    public DateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updated date time of the role.
     *
     * @param updatedDateTime the updated date time to set
     */
    public void setUpdatedDateTime(DateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get the is active of the role.
     *
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set the is active of the role.
     *
     * @param isActive the is active to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Get the sync flag for temporary usage.
     *
     * @return syncFlag
     */
    public Boolean getSyncFlag() {
        return syncFlag;
    }

    /**
     * Set the sync flag for temporary usage.
     *
     * @param syncFlag - the Boolean to set
     */
    public void setSyncFlag(Boolean syncFlag) {
        this.syncFlag = syncFlag;
    }

}
