package ck.panda.domain.entity;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Resource limit Project entity. *
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "resource_limit_projects")
public class ResourceLimitProject {

    /** Unique ID of the Resource limit. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Resource limit for domain id. */
    @JoinColumn(name = "domain_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Domain domain;

    /** Resource limit for domain id. */
    @Column(name = "domain_id")
    private Long domainId;

    /** Resource limit for department id. */
    @JoinColumn(name = "department_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Department department;

    /** Resource limit for department id. */
    @Column(name = "department_id")
    private Long departmentId;

    /** Resource limit for project id. */
    @JoinColumn(name = "project_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Project project;

    /** Resource limit forS project id. */
    @Column(name = "project_id")
    private Long projectId;

    /** Type of resource. */
    @Column(name = "resource_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;

    /** Maximum resource limit. */
    @Column(name = "max", columnDefinition = "bigint(20) default -1")
    private Long max;

    /** Available Resource limit usage for Project. */
    @Column(name = "available_limit")
    private Long available;

    /** Resource limit usage for Project. */
    @Column(name = "used_limit")
    private Long usedLimit;

    /** Status attribute to verify status of the resource limit. */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    /** unique separator for each project with resource type. */
    @Transient
    private String uniqueSeperator;

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

    /** An active attribute is to check whether the resource is active or not. */
    @Column(name = "is_active", columnDefinition = "tinyint default 1")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean isActive;

    /** Transient domain of the user. */
    @Transient
    private String transProjectId;

    /** Transient resource type of the resource limit domain. */
    @Transient
    private Integer transResourceType;

    /**
     * isSyncFlag field is not to be serialized, whereas JPA's @Transient annotation is used to indicate that a field is
     * not to be persisted in the database.
     */
    @Transient
    private Boolean isSyncFlag;

    /** Enum type for Resource Limit. */
    public enum ResourceType {
        /** Number of instances a user can create. */
        Instance,
        /** Number of public IP addresses a user can own. */
        IP,
        /** Number of disk volumes a user can create. */
        Volume,
        /** Number of snapshots a user can create. */
        Snapshot,
        /** Number of templates that a user can register/create. */
        Template,
        /** Number of projects an account can own. */
        Project,
        /** Number of guest network a user can create. */
        Network,
        /** Number of VPC a user can create. */
        VPC,
        /** Total number of CPU cores a user can use. */
        CPU,
        /** Total Memory (in MB) a user can use. */
        Memory,
        /** Total primary storage space (in GiB) a user can use. */
        PrimaryStorage,
        /** Total secondary storage space (in GiB) a user can use. */
        SecondaryStorage

    }

    /** Enum type for Resource Limit. */
    public enum Status {
        /** Resource Limit will be in a Enabled State. */
        ENABLED,
        /** Resource Limit will be in a Disabled State. */
        DISABLED
    }

    /**
     * Get the id of the ResourceLimit.
     *
     * @return the id of the ResourceLimit.
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id of the Resource limit.
     *
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the domain of the Resource limit.
     *
     * @return the domain of the Resource limit.
     */
    public Domain getDomain() {
        return domain;
    }

    /**
     * Set the domain of the Resource limit.
     *
     * @param domain the domain to set
     */
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    /**
     * Get the domain Id of the Resource limit.
     *
     * @return the domainId of the Resource limit.
     */
    public Long getDomainId() {
        return domainId;
    }

    /**
     * Set the domain Id of the Resource limit.
     *
     * @param domainId the domainId to set
     */
    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    /**
     * Get the department of the Resource limit.
     *
     * @return the department of the Resource limit.
     */
    public Department getDepartment() {
        return department;
    }

    /**
     * Set the department of the Resource limit.
     *
     * @param department the department to set
     */
    public void setDepartment(Department department) {
        this.department = department;
    }

    /**
     * Get the department Id of the Resource limit.
     *
     * @return the departmentId of the Resource limit.
     */
    public Long getDepartmentId() {
        return departmentId;
    }

    /**
     * Set the department Id of the Resource limit.
     *
     * @param departmentId the departmentId to set
     */
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * Get the project of the Resource limit.
     *
     * @return the project of the Resource limit.
     */
    public Project getProject() {
        return project;
    }

    /**
     * Set the project of the Resource limit.
     *
     * @param project the project to set
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * Get the project Id of the Resource limit.
     *
     * @return the projectId of the Resource limit.
     */
    public Long getProjectId() {
        return projectId;
    }

    /**
     * Set the project Id of the Resource limit.
     *
     * @param projectId the projectId to set
     */
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    /**
     * Get the resource type of the Resource limit.
     *
     * @return the resourceType of the Resource limit.
     */
    public ResourceType getResourceType() {
        return resourceType;
    }

    /**
     * Set the resource type of the Resource limit.
     *
     * @param resourceType the resourceType to set
     */
    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * Get the max of the Resource Limit.
     *
     * @return the max of the Resource Limit.
     */
    public Long getMax() {
        return max;
    }

    /**
     * Set the max of the ResourceLimit.
     *
     * @param max the max to set
     */
    public void setMax(Long max) {
        this.max = max;
    }

    /**
     * Get the available of the ResourceLimitProject.

     * @return the available of ResourceLimitProject.
     */
    public Long getAvailable() {
        return available;
    }

    /**
     * Set the available of the ResourceLimitProject.
     *
     * @param available the available to set
     */
    public void setAvailable(Long available) {
        this.available = available;
    }

    /**
     * Get the usedLimit of the ResourceLimitProject.

     * @return the usedLimit of ResourceLimitProject.
     */
    public Long getUsedLimit() {
        return usedLimit;
    }

    /**
     * Set the usedLimit of the ResourceLimitProject.
     *
     * @param usedLimit the usedLimit to set
     */
    public void setUsedLimit(Long usedLimit) {
        this.usedLimit = usedLimit;
    }

    /**
     * Get the status of the Resource limit.
     *
     * @return the status of the Resource limit.
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set the status of the Resource limit.
     *
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Get the version of the Resource limit.
     *
     * @return the version of the Resource limit.
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Set the version of the Resource limit.
     *
     * @param version the version to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get the created by of the Resource limit.
     *
     * @return the createdBy of the Resource limit.
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the created by of the Resource limit.
     *
     * @param createdBy the created by to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updated by of the Resource limit.
     *
     * @return the updatedBy of the Resource limit.
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updated by of the Resource limit.
     *
     * @param updatedBy the updated by to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the created date time of the Resource limit.
     *
     * @return the createdDateTime of the Resource limit.
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the created date time of the Resource limit.
     *
     * @param createdDateTime the created date time to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updated date time of the Resource limit.
     *
     * @return the updatedDateTime of the Resource limit.
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updated date time of the Resource limit.
     *
     * @param updatedDateTime the updated date time to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get the is active of the Resource limit.
     *
     * @return the isActive of the Resource limit.
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set the is active of the Resource limit.
     *
     * @param isActive the is active to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Get the is sync flag of the Resource limit.
     *
     * @return the isSyncFlag of the Resource limit.
     */
    public Boolean getIsSyncFlag() {
        return isSyncFlag;
    }

    /**
     * Set the is sync flag of the Resource limit.
     *
     * @param isSyncFlag the is sync flag to set
     */
    public void setIsSyncFlag(Boolean isSyncFlag) {
        this.isSyncFlag = isSyncFlag;
    }

    /**
     * Set the unique separator for the Resource limit.
     *
     * @return the unique Separator for the Resource limit.
     */
    public String getUniqueSeperator() {
        return uniqueSeperator;
    }

    /**
     * Get the unique Separator for the Resource limit.
     *
     * @param uniqueSeperator for the Resource limit.
     */
    public void setUniqueSeperator(String uniqueSeperator) {
        this.uniqueSeperator = uniqueSeperator;
    }

    /**
     * Get Transient Project Id.
     *
     * @return the transProjectId
     */
    public String getTransProjectId() {
        return transProjectId;
    }

    /**
     * Set the Transient Project Id.
     *
     * @param transProjectId the transProjectId to set
     */
    public void setTransProjectId(String transProjectId) {
        this.transProjectId = transProjectId;
    }

    /**
     * Get the transient Resource Type.
     *
     * @return the transResourceType
     */
    public Integer getTransResourceType() {
        return transResourceType;
    }

    /**
     * Set the transResourceType.
     *
     * @param transResourceType to set
     */
    public void setTransResourceType(Integer transResourceType) {
        this.transResourceType = transResourceType;
    }

    /**
     * Mapping ResourceLimit entity object in list.
     *
     * @param resourceList lists of resource limits
     * @return resourceMap mapped values.
     */
    public static Map<String, ResourceLimitProject> convert(List<ResourceLimitProject> resourceList) {
        Map<String, ResourceLimitProject> resourceMap = new HashMap<String, ResourceLimitProject>();

        for (ResourceLimitProject resource : resourceList) {
            resourceMap.put(resource.getUniqueSeperator(), resource);
        }
        return resourceMap;
    }
}
