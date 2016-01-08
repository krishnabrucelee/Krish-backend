package ck.panda.domain.entity;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * IP addresses will need to be reserved for each POD, and a Guest IP Range assigned during the initial configuration of
 * the Zone. Every Host, System VM and Guest Instance within the whole Cloud must have a unique IP Address.
 */
@Entity
@Table(name = "ip_addresses")
public class IpAddress {

    /** Unique Id of the IP address. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** public ip address. */
    @Column(name = "public_ip_address", nullable = false)
    private String publicIpAddress;

    /** public ip address uuid. */
    @Column(name = "uuid", nullable = false)
    private String uuid;

    /** Network for ip address. */
    @JoinColumn(name = "network_id", referencedColumnName = "id", updatable = false, insertable = false)
    @OneToOne
    private Network network;

    /** Network id for ip address. */
    @Column(name = "network_id")
    private Long networkId;

    /** Domain of the ip address. */
    @JoinColumn(name = "domain_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Domain domain;

    /** Domain id of the ip address. */
    @Column(name = "domain_id")
    private Long domainId;

    /** Project of the ip address. */
    @JoinColumn(name = "project_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Project project;

    /** Project id of the ip address. */
    @Column(name = "project_id")
    private Long projectId;

    /** Department of the ip address. */
    @JoinColumn(name = "department_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Department department;

    /** Department id of the ip address. */
    @Column(name = "department_id")
    private Long departmentId;

    /** State for ipaddress, whether it is Free, Allocated etc . */
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private State state;

    /** Set syncFlag. */
    @Transient
    private Boolean syncFlag;

    /**
     * Enumeration state for ipaddress.
     */
    public enum State {
        /** Allocated status for already acquired/assigned to network. */
        ALLOCATED,

        /** Free status for available public ips. */
        FREE
    }

    /** The Zone Id. */
    @JoinColumn(name = "zone_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Zone zone;

    /** ip address zone id. */
    @Column(name = "zone_id")
    private Long zoneId;

    /** Instance id for ip address. */
    @JoinColumn(name = "instance_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private VmInstance vmInstance;

    /** Instance id for ip address. */
    @Column(name = "instance_id")
    private Long vmInstanceId;

    /** Version attribute to handle optimistic locking. */
    @Version
    @Column(name = "version")
    private Long version;

    /** Created by user. */
    @CreatedBy
    @Column(name = "created_user_id")
    private Long createdBy;

    /** Last updated by user. */
    @LastModifiedBy
    @Column(name = "updated_user_id")
    private Long updatedBy;

    /** Created date and time. */
    @CreatedDate
    @Column(name = "created_date_time")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentZonedDateTime")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime createdDateTime;

    /** modified date and time. */
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
     * @param id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get ipaddress uuid.
     *
     * @return the uuid.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * set ipaddress uuid.
     *
     * @param uuid to set.
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get the public ipaddress.
     *
     * @return the publicIpAddress.
     */
    public String getPublicIpAddress() {
        return publicIpAddress;
    }

    /**
     * Set the public ipaddress.
     *
     * @param publicIpAddress to set.
     */
    public void setPublicIpAddress(String publicIpAddress) {
        this.publicIpAddress = publicIpAddress;
    }

    /**
     * Get the network.
     *
     * @return the network.
     */
    public Network getNetwork() {
        return network;
    }

    /**
     * Set the network.
     *
     * @param network to set.
     */
    public void setNetwork(Network network) {
        this.network = network;
    }

    /**
     * Get the network's id.
     *
     * @return the networkId.
     */
    public Long getNetworkId() {
        return networkId;
    }

    /**
     * Set the network's id.
     *
     * @param networkId to set.
     */
    public void setNetworkId(Long networkId) {
        this.networkId = networkId;
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
     * @param domain to set.
     */
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    /**
     * Get the domain's id.
     *
     * @return the domainId.
     */
    public Long getDomainId() {
        return domainId;
    }

    /**
     * Set the domain's id.
     *
     * @param domainId to set.
     */
    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    /**
     * Get the project.
     *
     * @return the project.
     */
    public Project getProject() {
        return project;
    }

    /**
     * Set the project.
     *
     * @param project to set.
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * Get the project's id.
     *
     * @return the projectId.
     */
    public Long getProjectId() {
        return projectId;
    }

    /**
     * Set the project's id.
     *
     * @param projectId to set.
     */
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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
     * @param department to set.
     */
    public void setDepartment(Department department) {
        this.department = department;
    }

    /**
     * Get the department's id.
     *
     * @return the departmentId.
     */
    public Long getDepartmentId() {
        return departmentId;
    }

    /**
     * Set the department's id.
     *
     * @param departmentId to set.
     */
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * Get the zone.
     *
     * @return the zone.
     */
    public Zone getZone() {
        return zone;
    }

    /**
     * Set the zone.
     *
     * @param zone to set.
     */
    public void setZone(Zone zone) {
        this.zone = zone;
    }

    /**
     * Get the zone's id.
     *
     * @return the zoneId.
     */
    public Long getZoneId() {
        return zoneId;
    }

    /**
     * Set the zone's id.
     *
     * @param zoneId to set.
     */
    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * Get the Instance.
     *
     * @return the vmInstance.
     */
    public VmInstance getVmInstance() {
        return vmInstance;
    }

    /**
     * Set the Instance.
     *
     * @param vmInstance to set.
     */
    public void setVmInstance(VmInstance vmInstance) {
        this.vmInstance = vmInstance;
    }

    /**
     * Get the instance's id.
     *
     * @return the vmInstanceId.
     */
    public Long getVmInstanceId() {
        return vmInstanceId;
    }

    /**
     * Set the instance's id.
     *
     * @param vmInstanceId to set.
     */
    public void setVmInstanceId(Long vmInstanceId) {
        this.vmInstanceId = vmInstanceId;
    }

    /**
     * Get the state for ipaddress.
     *
     * @return the state.
     */
    public State getState() {
        return state;
    }

    /**
     * Set the state for ipaddress.
     *
     * @param state the state to set
     */
    public void setState(State state) {
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
     * @param version to set.
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get the created By.
     *
     * @return the createdBy.
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the created By.
     *
     * @param createdBy to set.
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updated By.
     *
     * @return the updatedBy.
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updated By.
     *
     * @param updatedBy to set.
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the created Date and Time.
     *
     * @return the createdDateTime.
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the created Date and Time.
     *
     * @param createdDateTime to set.
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the sync flag.
     *
     * @return the syncFlag.
     */
    public Boolean getSyncFlag() {
        return syncFlag;
    }

    /**
     * Set the sync flag.
     *
     * @param syncFlag to set.
     */
    public void setSyncFlag(Boolean syncFlag) {
        this.syncFlag = syncFlag;
    }

    /**
     * Get the updated Date and Time.
     *
     * @return the updatedDateTime.
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updated Date and Time.
     *
     * @param updatedDateTime to set.
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

}
