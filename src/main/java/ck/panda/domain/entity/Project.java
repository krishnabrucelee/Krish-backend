package ck.panda.domain.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import ck.panda.util.ConvertUtil;
import ck.panda.util.JsonUtil;
import ck.panda.util.JsonValidator;

/**
 * Projects are used to organize people and resources. CloudStack users within a single domain can group
 * themselves into project teams so they can collaborate and share virtual resources such as VMs, snapshots,
 * templates, data disks, and IP addresses. CloudStack tracks resource usage per project as well as per user,
 * so the usage can be billed to either a user account or a project.
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
    @JoinColumn(name = "project_owner_id", referencedColumnName = "id", updatable = false, insertable = false)
    @ManyToOne
    private User projectOwner;

    /** cloudstack's project uuid. */
    @Column(name = "uuid")
    private String uuid;

    /** Project owner id. */
    @Column(name = "project_owner_id")
    private Long projectOwnerId;

    /** List of users for projects. */
    @ManyToMany
    private List<User> userList;

    /** Project domain id. */
    @JoinColumn(name = "domain_id", referencedColumnName = "id", updatable = false, insertable = false)
    @ManyToOne(targetEntity = Domain.class, fetch = FetchType.EAGER)
    private Domain domain;

    /** Project domain id. */
    @NotNull
    @Column(name = "domain_id")
    private Long domainId;

    /** Project department id. */
    @JoinColumn(name = "department_id", referencedColumnName = "id", updatable = false, insertable = false)
    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Department.class)
    private Department department;

    /** Project department id. */
    @Column(name = "department_id")
    private Long departmentId;

    /** Project is whether active or disable. */
    @NotNull
    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT DEFAULT FALSE")
    private Boolean isActive;

    /** Status for project, whether it is Deleted, Enabled etc . */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    /** Enumeration status for Project. */
    public enum Status {
        /** Enabled status is used to list projects through out the application. */
        ENABLED, /** Deleted status make projects as soft deleted and it will not list on the applicaiton. */
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

    /** Transient value . */
    @Transient
    private Boolean syncFlag;

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
     * @param id project unique id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the name.
     *
     * @return the project name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name.
     *
     * @param name project name to set.
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
     * @param description project description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the projectOwner.
     *
     * @return the project Owner.
     */
    public User getProjectOwner() {
        return projectOwner;
    }

    /**
     * Set the project owner.
     *
     * @param projectOwner owner id to set.
     */
    public void setProjectOwner(User projectOwner) {
        this.projectOwner = projectOwner;
    }

    /**
     * Get the list of user. uper.toString();
     *
     * @return the list of user.
     */
    public List<User> getUserList() {
        return userList;
    }

    /**
     * Set the list of user.
     *
     * @param userList list of user to set.
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
     * @param domain domain id to set.
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
     * @param department department object to set.
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
     * @param isActive project's status to set.
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Get the version count.
     *
     * @return the version count.
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Set the version count.
     *
     * @param version version count to set.
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get the created user details.
     *
     * @return the created user.
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the created user details.
     *
     * @param createdBy created user to set.
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the last updated user details.
     *
     * @return the updated user.
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the last updated user details.
     *
     * @param updatedBy updated user to set.
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the created date and time.
     *
     * @return the created date and time.
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the created date and time.
     *
     * @param createdDateTime created date and time to set.
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updated date and time.
     *
     * @return the updated date and time.
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updated date and time.
     *
     * @param updatedDateTime updated date and time to set.
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
     * @param status status of project to set.
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Get project owner id.
     *
     * @return the projectOwnerId.
     */
    public Long getProjectOwnerId() {
        return projectOwnerId;
    }

    /**
     * Set project owner id.
     *
     * @param projectOwnerId the project owner id to set.
     */
    public void setProjectOwnerId(Long projectOwnerId) {
        this.projectOwnerId = projectOwnerId;
    }

    /**
     * Get domain id.
     *
     * @return the domainId.
     */
    public Long getDomainId() {
        return domainId;
    }

    /**
     * Set domain id.
     *
     * @param domainId the domain id to set.
     */
    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    /**
     * Get department id.
     *
     * @return the departmentId.
     */
    public Long getDepartmentId() {
        return departmentId;
    }

    /**
     * Set department id.
     *
     * @param departmentId the department id to set.
     */
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * get instance UUID.
     *
     * @return the uuid.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * set UUID from cloud stack.
     *
     * @param uuid to set.
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }


    /**
     * @return the syncFlag
     */
    public Boolean getSyncFlag() {
        return syncFlag;
    }

    /**
     * @param syncFlag the syncFlag to set
     */
    public void setSyncFlag(Boolean syncFlag) {
        this.syncFlag = syncFlag;
    }

    /**
     * Convert JSONObject to project entity.
     *
     * @param object json object
     * @return project entity object.
     * @throws JSONException handles json exception.
     */
    @SuppressWarnings("static-access")
    public static Project convert(JSONObject jsonObject, ConvertUtil convertUtil ) throws JSONException {
        Project project = new Project();
        project.setSyncFlag(false);
        try {
            project.setName(JsonUtil.getStringValue(jsonObject, "name"));
            project.setUuid(JsonUtil.getStringValue(jsonObject, "id"));
            project.setDomainId(convertUtil.getDomainId(JsonUtil.getStringValue(jsonObject, "domainid")));
            project.setDepartmentId(convertUtil.getDepartmentByUsernameAndDomain(JsonUtil.getStringValue(jsonObject, "account"),
                    convertUtil.getDomain(JsonUtil.getStringValue(jsonObject, "domainid"))));
            project.setIsActive(convertUtil.getState(JsonUtil.getStringValue(jsonObject, "state")));
            project.setStatus((Project.Status)convertUtil.getStatus(JsonUtil.getStringValue(jsonObject, "state")));
            project.setDescription(JsonUtil.getStringValue(jsonObject, "displaytext"));
            project.setProjectOwnerId(convertUtil.getUserIdByAccount(JsonUtil.getStringValue(jsonObject, "account"), convertUtil.getDomain(JsonUtil.getStringValue(jsonObject, "domainid"))));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return project;
    }

    /**
     * Mapping entity object into list.
     *
     * @param projectList to set.
     * @return project map
     */
    public static Map<String, Project> convert(List<Project> projectList) {
        Map<String, Project> projectMap = new HashMap<String, Project>();

        for (Project project : projectList) {
            projectMap.put(project.getUuid(), project);
        }

        return projectMap;
    }
}
