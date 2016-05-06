package ck.panda.domain.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *  A VPC can have its own virtual network topology that resembles a traditional physical network. You can
 *  launch VMs in the virtual network that can have private addresses in the range of your choice.
 *
 */

@Entity
@Table(name = "vpc")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class VPC implements Serializable {

    /** Id of the VPC. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Name of the VPC. */
    @Column(name = "name", nullable = false)
    private String name;

    /** Unique id for the VPC. */
    @Column(name = "uuid")
    private String uuid;

    /** CIDR Range of the IP address. */
    @Column(name = "cidr")
    private String cIDR;

    /** Description of the VPC. */
    @Column(name = "description", nullable = false)
    private String description;

    /** An optional field, whether to the display the vpc to the end user or not. */
    @Column(name = "for_display", nullable = false)
    private String forDisplay;

    /**If set to false, the VPC won't start (VPC VR will not get allocated) until its first network gets implemented. True by default. */
    @Column(name = "start", nullable = false)
    private String start;

    /** Domain of the VPC. */
    @JoinColumn(name = "domain_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @OneToOne
    private Domain domain;

    /** Domain id of the VPC. */
    @Column(name = "domain_id")
    private Long domainId;

    /** Department of the VPC. */
    @JoinColumn(name = "department_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @OneToOne
    private Department department;

    /** Department id of the VPC. */
    @Column(name = "department_id")
    private Long departmentId;

    /** Project of the VPC. */
    @JoinColumn(name = "project_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @OneToOne
    private Project project;

    /** Project id of the VPC. */
    @Column(name = "project_id")
    private Long projectId;

    /** The Zone object this VPC belongs to. */
    @JoinColumn(name = "zone_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @OneToOne
    private Zone zone;

    /** Zone id of the VPC. */
    @Column(name = "zone_id")
    private Long zoneId;

    /** IsActive attribute to verify Active or Inactive. */
    @Column(name = "is_active")
    private Boolean isActive;

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

    /** Version attribute to handle optimistic locking. */
    @Version
    @Column(name = "version")
    private Long version;

    /** Status attribute to verify status of the Vpc. */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    /** Set syncFlag. */
    @Transient
    private Boolean syncFlag;

    /** Enum type for Vpc Status. */
    public enum Status {
        /** Indicates the network configuration is in allocated but not setup (Vlan is not set, and network is not ready for use). Isolated network goes to this state right after it's created with NO Vlan passed in. As vlan is optional parameter in createNetwork call only for Isolated networks, you should see this state for isolated networks only. */
        ALLOCATED,
        /**  Indicates that the network is destroyed and not displayed to the end user. */
        DESTROY,
        /** Indicates the network configuration is ready to be used by VM (Vlan is set for the network). */
        IMPLEMENTED,
        /** Indicates the network configuration is being implemented. */
        IMPLEMENTING,
        /** Indicates the network configuration is setup with Vlan from the moment it was created. Happens when vlan is passed in to the createNetwork call, so its immutable for the network for its entire lifecycle. Happens for Shared networks. */
        SETUP,
        /** Indicates the network configuration is being shutdown (this is intermediate state, although the name doesn't sound so). During this stage Vlan is being released, and the network goes back to Allocated state. */
        SHUTDOWN
    }

    /**
     * Get the id of the Vpc.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id of the Vpc.
     *
     * @param id  to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the name of the Vpc.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the Vpc.
     *
     * @param name  to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get UUID of the Vpc.
     *
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set UUID of the Vpc.
     *
     * @param uuid  to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get the description of the Vpc.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of the Vpc.
     *
     * @param description  to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get for display.
     *
     * @return the for display.
     */
    public String getForDisplay() {
        return forDisplay;
    }

    /**
     * Set the for display.
     *
     * @param forDisplay  to set
     */
    public void setForDisplay(String forDisplay) {
        this.forDisplay = forDisplay;
    }

    /**
     * Get start element of the Vpc.
     *
     * @return the start
     */
    public String getStart() {
        return start;
    }

    /**
     * Set start element of the Vpc.
     *
     * @param start  to set
     */
    public void setStart(String start) {
        this.start = start;
    }

    /**
     * Get the domain of Vpc.
     *
     * @return the domain
     */
    public Domain getDomain() {
        return domain;
    }

    /**
     * Set the domain of Vpc.
     *
     * @param domain to set
     */
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    /**
     * Get the domain id of the Vpc.
     *
     * @return the domainId.
     */
    public Long getDomainId() {
        return domainId;
    }

    /**
     * Set the domain id of the Vpc.
     *
     * @param domainId  to set
     */
    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    /**
     * Get the department.
     *
     * @return the department
     */
    public Department getDepartment() {
        return department;
    }

    /**
     * Set the department.
     *
     * @param department  to set
     */
    public void setDepartment(Department department) {
        this.department = department;
    }

    /**
     * Get the department id of the Vpc.
     *
     * @return the departmentId
     */
    public Long getDepartmentId() {
        return departmentId;
    }

    /**
     * Set the department id of the Vpc.
     *
     * @param departmentId  to set
     */
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * Get the project of Vpc.
     *
     * @return the project
     */
    public Project getProject() {
        return project;
    }

    /**
     * Set the project of Vpc.
     *
     * @param project the project to set
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * Get the project Id of Vpc.
     *
     * @return the projectId
     */
    public Long getProjectId() {
        return projectId;
    }

    /**
     * Set the project Id of Vpc.
     *
     * @param projectId the projectId to set
     */
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    /**
     * Get the zone of the Vpc.
     *
     * @return the zone
     */
    public Zone getZone() {
        return zone;
    }

    /**
     * Set the zone fo the Vpc.
     *
     * @param zone the zone to set
     */
    public void setZone(Zone zone) {
        this.zone = zone;
    }

    /**
     * Get the zone Id of the Vpc.
     *
     * @return the zoneId
     */
    public Long getZoneId() {
        return zoneId;
    }

    /**
     * Set the zone id of the Vpc.
     *
     * @param zoneId the zoneId to set
     */
    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }


    /**
     * Get the created date and time.
     *
     * @return createdDateTime
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the created date and time.
     *
     * @param createdDateTime the DateTime to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updated date and time.
     *
     * @return updatedDateTime
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updated date and time.
     *
     * @param updatedDateTime the DateTime to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get created user.
     *
     * @return the createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Get updated user.
     *
     * @return the updatedBy
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the createdBy .
     *
     * @param createdBy to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Set the updatedBy .
     *
     * @param updatedBy to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the cidr range of the ip address.
     *
     * @return the cIDR
     */
    public String getcIDR() {
        return cIDR;
    }

    /**
     * Set the cidr range of the ip address.
     *
     * @param cIDR  to set
     */
    public void setcIDR(String cIDR) {
        this.cIDR = cIDR;
    }

    /**
     * Get is active status of Vpc.
     *
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set is Active status of Vpc.
     *
     * @param isActive the isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Get version of the table.
     *
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Set version of the table.
     *
     * @param version to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get the status of the Vpc.
     *
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set the status of the Vpc.
     *
     * @param status  to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

	/**
	 * Get sync flag.
	 *
	 * @return the syncFlag
	 */
	public Boolean getSyncFlag() {
		return syncFlag;
	}

	/**
	 * Set sync flag.
	 *
	 * @param syncFlag the syncFlag to set
	 */
	public void setSyncFlag(Boolean syncFlag) {
		this.syncFlag = syncFlag;
	}
}
