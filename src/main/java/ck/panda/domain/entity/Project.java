package ck.panda.domain.entity;

import java.time.ZonedDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
 * Projects are used to organize people and resources. CloudStack users within a single domain can group themselves
 * into project teams so they can collaborate and share virtual resources such as VMs, snapshots, templates, data
 * disks, and IP addresses. CloudStack tracks resource usage per project as well as per user, so the usage can
 * be billed to either a user account or a project.
 */
@Entity
@Table(name = "ck_projects")
public class Project {

    /** Id of the project. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** cloudstack's project uuid. */
    @Size(min = 128)
    @Column(name = "uuid")
    private String uuid;

    /** Name of the Project. */
    @NotEmpty
    @Size(min = 4, max = 20)
    @Column(name = "name", nullable = false)
    private String name;

    /** Name of the project description. */
    @Column(name = "description")
    private String description;

    /** Project owner id. */
    @NotEmpty
    @JoinColumn(name = "project_owner_id", referencedColumnName = "id")
    @ManyToOne(targetEntity = Project.class)
    private User projectOwner;

    /** List of users for an project. */
    @NotEmpty
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "projects_users",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> userList;

    /** Project domain id. */
    @NotEmpty
    @JoinColumn(name = "domain_id", referencedColumnName = "id")
    @ManyToOne(targetEntity = Project.class)
    private Domain domain;

    /** Project department id. */
    @NotEmpty
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "projects_departments",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id")
    )
    private List<Department> department;

    /** Project current state. */
    @NotEmpty
    @Column(name = "state", nullable = false)
    private String state;

    /** Project is whether active or disable. */
    @NotEmpty
    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT DEFAULT FALSE")
    private Boolean isActive;

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
     * Get the uuid.
     *
     * @return the uuid.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the uuid.
     *
     * @param uuid - the uuid to set.
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
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
     * @param name - the name to set.
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
     * @param description - the description to set.
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
     * Set the projectOwner.
     *
     * @param projectOwner - the projectOwner to set.
     */
    public void setProjectOwner(User projectOwner) {
        this.projectOwner = projectOwner;
    }

    /**
     * Get the userList.
     *
     * @return the userList.
     */
    public List<User> getUserList() {
        return userList;
    }

    /**
     * Set the userList.
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
     * @param domain - the domain to set.
     */
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    /**
     * Get the department.
     *
     * @return the department.
     */
    public List<Department> getDepartment() {
        return department;
    }

    /**
     * Set the department.
     *
     * @param department - the department to set.
     */
    public void setDepartment(List<Department> department) {
        this.department = department;
    }

    /**
     * Get the isActive.
     *
     * @return the isActive.
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set the isActive.
     *
     * @param isActive - the isActive to set.
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Get the state.
     *
     * @return the state.
     */
    public String getState() {
        return state;
    }

    /**
     * Set the state.
     *
     * @param state - the state to set.
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Get the version.
     *
     * @return the version.
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Set the version.
     *
     * @param version - the version to set.
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get the createdBy.
     *
     * @return the createdBy.
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the createdBy.
     *
     * @param createdBy - the createdBy to set.
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updatedBy.
     *
     * @return the updatedBy.
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updatedBy.
     *
     * @param updatedBy - the updatedBy to set.
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the createdDateTime.
     *
     * @return the createdDateTime.
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the createdDateTime.
     *
     * @param createdDateTime - the createdDateTime to set.
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updatedDateTime.
     *
     * @return the updatedDateTime.
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updatedDateTime.
     *
     * @param updatedDateTime - the updatedDateTime to set.
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }
}
