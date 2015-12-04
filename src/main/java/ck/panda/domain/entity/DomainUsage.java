package ck.panda.domain.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

/**
 * Usage contains a list of service from cloud stack like Instance, Volume, Snapshot etc., and its Usage.
 * And it stores the hourly based usage.
 * @author Assistanz
 *
 */
@Entity
@Table(name = "ck_usage")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class DomainUsage implements Serializable {

    /** Id of the Usage. */
    @Id
    @Column(name = "id")
    private String id;


    /** Account which the usage record belongs to. */
    @JoinColumn(name = "account_id", referencedColumnName = "id", updatable = false, insertable = false)
    @ManyToOne
    private Account account;

    @NotNull
    @Column(name = "account_id")
    private Long accountId;

    @JoinColumn(name = "domain_id", referencedColumnName = "id", updatable = false, insertable = false)
    @ManyToOne
    private Domain domain;

    @NotNull
    @Column(name = "domain_id")
    private Long domainId;


    @JoinColumn(name = "zone_id", referencedColumnName = "id", updatable = false, insertable = false)
    @ManyToOne
    private Zone zone;

    @NotNull
    @Column(name = "zone_id")
    private Long zoneId;

    /** Description of the Usage. */
    @Column(name = "description")
    private String description;

    /** Display the usage in hours. */
    @Column(name = "usage_display")
    private String usageDisplay;

    /** The Type of usage which belong to this offering */
    @Column(name = "usage_type")
    @Enumerated(EnumType.STRING)
    private UsageType usageType;

    /** Reference id of a virtual machine. */
    @Column(name = "vm_instance_id")
    private String vmInstanceId;

    /** Name of the virtual machine. */
    @Column(name = "vm_name")
    private String vmName;

    /** Raw usage of the service*/
    @Column(name = "raw_usage")
    private Double rawUsage;


    /** Usage hours of the service*/
    @Column(name = "hours")
    private Double hours;

    /** Reference id of service offering. */
    @Column(name = "offering_id")
    private String offeringId;

    /** Reference id of template. */
    @Column(name = "template_id")
    private String templateId;

    /**  The ID of the the Usage type service */
    @Column(name = "usage_id")
    private String usageId;

    /** Type of the service Device type, Hypervisor, etc. */
    @Column(name = "type")
    private String type;

    /** Size of the service */
    @Column(name = "size")
    private Long size;

    /** Reference id of the network. */
    @Column(name = "network_id")
    private String networkId;

    /** Start date of the usage. */
    @DateTimeFormat(iso = ISO.DATE_TIME)
    @Column(name = "start_date")
    private Date startDate;

    /** End date of the usage. */
    @DateTimeFormat(iso = ISO.DATE_TIME)
    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "virtual_size")
    private Long virtualSize;

    @Column(name = "cpu_speed")
    private Integer cpuSpeed;

    @Column(name = "cpu_cores")
    private Integer cpuCores;

    @Column(name = "memory")
    private Integer memory;


    /** Check whether Usage is in active state or in active state. */
    @Column(name = "is_active")
    private Boolean isActive;

    /** Status for Usage, whether it is Deleted, Disabled etc . */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
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
    public DomainUsage() {
        super();
    }

    /**
     * Parameterized constructor.
     *
     * @param name to set
     */
    public DomainUsage(String name) {
        super();
    }

    /**
     * Get the id.
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Set the id.
     *
     * @param id the Long to set
     */
    public void setId(String id) {
        this.id = id;
    }



    /**
     * @return the zone
     */
    public Zone getZone() {
        return zone;
    }

    /**
     * @param zone the zone to set
     */
    public void setZone(Zone zone) {
        this.zone = zone;
    }



    /**
     * @return the accountId
     */
    public Long getAccountId() {
        return accountId;
    }

    /**
     * @param accountId the accountId to set
     */
    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    /**
     * @return the domainId
     */
    public Long getDomainId() {
        return domainId;
    }

    /**
     * @param domainId the domainId to set
     */
    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    /**
     * @return the zoneId
     */
    public Long getZoneId() {
        return zoneId;
    }

    /**
     * @param zoneId the zoneId to set
     */
    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * @return the user
     */
    public Account getUser() {
        return account;
    }

    /**
     * @param user the user to set
     */
    public void setUser(Account account) {
        this.account = account;
    }

    /**
     * @return the domain
     */
    public Domain getDomain() {
        return domain;
    }

    /**
     * @param domain the domain to set
     */
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the usageDisplay
     */
    public String getUsageDisplay() {
        return usageDisplay;
    }

    /**
     * @param usageDisplay the usageDisplay to set
     */
    public void setUsageDisplay(String usageDisplay) {
        this.usageDisplay = usageDisplay;
    }

    /**
     * @return the usageType
     */
    public UsageType getUsageType() {
        return usageType;
    }

    /**
     * @param usageType the usageType to set
     */
    public void setUsageType(UsageType usageType) {
        this.usageType = usageType;
    }

    /**
     * @return the vmInstanceId
     */
    public String getVmInstanceId() {
        return vmInstanceId;
    }

    /**
     * @param vmInstanceId the vmInstanceId to set
     */
    public void setVmInstanceId(String vmInstanceId) {
        this.vmInstanceId = vmInstanceId;
    }

    /**
     * @return the vmName
     */
    public String getVmName() {
        return vmName;
    }

    /**
     * @param vmName the vmName to set
     */
    public void setVmName(String vmName) {
        this.vmName = vmName;
    }

    /**
     * @return the rawUsage
     */
    public Double getRawUsage() {
        return rawUsage;
    }

    /**
     * @param rawUsage the rawUsage to set
     */
    public void setRawUsage(Double rawUsage) {
        this.rawUsage = rawUsage;
    }



    /**
     * @return the account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * @param account the account to set
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * @return the hours
     */
    public Double getHours() {
        return hours;
    }

    /**
     * @param hours the hours to set
     */
    public void setHours(Double hours) {
        this.hours = hours;
    }

    /**
     * @return the offeringId
     */
    public String getOfferingId() {
        return offeringId;
    }

    /**
     * @param offeringId the offeringId to set
     */
    public void setOfferingId(String offeringId) {
        this.offeringId = offeringId;
    }

    /**
     * @return the templateId
     */
    public String getTemplateId() {
        return templateId;
    }

    /**
     * @param templateId the templateId to set
     */
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    /**
     * @return the usageId
     */
    public String getUsageId() {
        return usageId;
    }

    /**
     * @param usageId the usageId to set
     */
    public void setUsageId(String usageId) {
        this.usageId = usageId;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the size
     */
    public Long getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * @return the networkId
     */
    public String getNetworkId() {
        return networkId;
    }

    /**
     * @param networkId the networkId to set
     */
    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    /**
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the virtualSize
     */
    public Long getVirtualSize() {
        return virtualSize;
    }

    /**
     * @param virtualSize the virtualSize to set
     */
    public void setVirtualSize(Long virtualSize) {
        this.virtualSize = virtualSize;
    }

    /**
     * @return the cpuSpeed
     */
    public Integer getCpuSpeed() {
        return cpuSpeed;
    }

    /**
     * @param cpuSpeed the cpuSpeed to set
     */
    public void setCpuSpeed(Integer cpuSpeed) {
        this.cpuSpeed = cpuSpeed;
    }

    /**
     * @return the cpuCores
     */
    public Integer getCpuCores() {
        return cpuCores;
    }

    /**
     * @param cpuCores the cpuCores to set
     */
    public void setCpuCores(Integer cpuCores) {
        this.cpuCores = cpuCores;
    }

    /**
     * @return the memory
     */
    public Integer getMemory() {
        return memory;
    }

    /**
     * @param memory the memory to set
     */
    public void setMemory(Integer memory) {
        this.memory = memory;
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
     * @param version the Long to set
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
     * @param createdBy the User to set
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
     * @param updatedBy the User to set
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the createdDateTime.
     *
     * @return createdDateTime
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the createdDateTime.
     *
     * @param createdDateTime the DateTime to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updatedDateTime.
     *
     * @return updatedDateTime
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updatedDateTime.
     *
     * @param updatedDateTime the DateTime to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get is Active state of the Usage.
     *
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set is Active state of the Usage.
     *
     * @param isActive the isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }



    /**
     * Get the department status.
     *
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set the department status.
     *
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }


    /**
     * Enumeration status for Usage.
     */
    public enum Status {
        /** Enabled status is used to list usages through out the application. */
        ENABLED,

        /** Deleted status make usage as soft deleted and it will not list on the applicaiton. */
        DELETED
    }


    /**
     * Enumeration status for Usage.
     *
     * Reference https://cwiki.apache.org/confluence/display/CLOUDSTACK/Usage+and+Usage+Events
     */
    public enum UsageType {
        NOT_AVAILABLE,//0

        /** Running VM usage mapping Id 1. */
        RUNNING_VM,

        /** Allocated VM usage mapping Id 2. */
        ALLOCATED_VM,

        /** IP_ADDRESS usage mapping Id 3. */
        IP_ADDRESS,

        /** NETWORK_BYTES_SENT usage mapping Id 4. */
        NETWORK_BYTES_SENT,

        /** NETWORK_BYTES_RECEIVED usage mapping Id 5. */
        NETWORK_BYTES_RECEIVED,

        /** VOLUME usage mapping Id 6. */
        VOLUME,

        /** TEMPLATE usage mapping Id 7. */
        TEMPLATE,

        /** ISO usage mapping Id 8. */
        ISO,

        /** IP_ADDRESS usage mapping Id 9. */
        SNAPSHOT,

        /** Security Group usage */
        SECURITY_GROUP,

        /** LOAD_BALANCER_POLICY usage mapping Id 11. */
        LOAD_BALANCER_POLICY,

        /** PORT_FORWARDING_RULE usage mapping Id 12. */
        PORT_FORWARDING_RULE,

        /** NETWORK_OFFERING usage mapping Id 13. */
        NETWORK_OFFERING,

        /** VPN_USERS usage mapping Id 14. */
        VPN_USERS,
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
