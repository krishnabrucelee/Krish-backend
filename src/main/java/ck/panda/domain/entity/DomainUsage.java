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
 * @author Jamseer N <jamseer@assistanz.com>
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

    /** Account id of the usage */
    @NotNull
    @Column(name = "account_id")
    private Long accountId;

    /** Domain of the usage */
    @JoinColumn(name = "domain_id", referencedColumnName = "id", updatable = false, insertable = false)
    @ManyToOne
    private Domain domain;

    /** Domain id of the usage */
    @NotNull
    @Column(name = "domain_id")
    private Long domainId;

    /** Zone of the domain usage. */
    @JoinColumn(name = "zone_id", referencedColumnName = "id", updatable = false, insertable = false)
    @ManyToOne
    private Zone zone;

    /** Zone id of the domain usage. */
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

    /** Usage virtual size */
    @Column(name = "virtual_size")
    private Long virtualSize;

    /** Usage CPU speed */
    @Column(name = "cpu_speed")
    private Integer cpuSpeed;

    /** Usage CPU Cores */
    @Column(name = "cpu_cores")
    private Integer cpuCores;

    /** Usage memeory */
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
     * Get the zone.
     *
     * @return the zone
     */
    public Zone getZone() {
        return zone;
    }

    /**
     * Set the zone.
     *
     * @param zone the zone to set
     */
    public void setZone(Zone zone) {
        this.zone = zone;
    }


    /**
     * Get the account id.
     *
     * @return the accountId
     */
    public Long getAccountId() {
        return accountId;
    }

    /**
     * Set the account id.
     *
     * @param accountId the accountId to set
     */
    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    /**
     * Get the domain id.
     *
     * @return the domainId
     */
    public Long getDomainId() {
        return domainId;
    }

    /**
     * Set the domain id.
     *
     * @param domainId the domainId to set
     */
    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    /**
     * Get the zone id.
     *
     * @return the zoneId
     */
    public Long getZoneId() {
        return zoneId;
    }

    /**
     * Set the zone id.
     *
     * @param zoneId the zoneId to set
     */
    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * Get the account.
     *
     * @return the user
     */
    public Account getUser() {
        return account;
    }

    /**
     * Set the account.
     *
     * @param user the user to set
     */
    public void setUser(Account account) {
        this.account = account;
    }

    /**
     * Get the domain.
     *
     * @return the domain
     */
    public Domain getDomain() {
        return domain;
    }

    /**
     * Set the account.
     *
     * @param domain the domain to set
     */
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    /**
     * Get the usage description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the usage description.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the usage display.
     *
     * @return the usageDisplay
     */
    public String getUsageDisplay() {
        return usageDisplay;
    }

    /**
     * Set the usage display.
     *
     * @param usageDisplay the usageDisplay to set
     */
    public void setUsageDisplay(String usageDisplay) {
        this.usageDisplay = usageDisplay;
    }

    /**
     * Get the usage type.
     *
     * @return the usageType
     */
    public UsageType getUsageType() {
        return usageType;
    }

    /**
     * Set the usage type.
     *
     * @param usageType the usageType to set
     */
    public void setUsageType(UsageType usageType) {
        this.usageType = usageType;
    }

    /**
     * Get the vm instance id.
     * @return the vmInstanceId
     */
    public String getVmInstanceId() {
        return vmInstanceId;
    }

    /**
     * Set the vm instance id.
     *
     * @param vmInstanceId the vmInstanceId to set
     */
    public void setVmInstanceId(String vmInstanceId) {
        this.vmInstanceId = vmInstanceId;
    }

    /**
     * Get the name of the vm.
     *
     * @return the vmName
     */
    public String getVmName() {
        return vmName;
    }

    /**
     * Set the name of the vm.
     *
     * @param vmName the vmName to set
     */
    public void setVmName(String vmName) {
        this.vmName = vmName;
    }

    /**
     * Get the raw usage.
     *
     * @return the rawUsage
     */
    public Double getRawUsage() {
        return rawUsage;
    }

    /**
     * Set the raw usage.
     *
     * @param rawUsage the rawUsage to set
     */
    public void setRawUsage(Double rawUsage) {
        this.rawUsage = rawUsage;
    }



    /**
     * Get the account.
     *
     * @return the account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Set the account.
     *
     * @param account the account to set
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Get the hours.
     *
     * @return the hours
     */
    public Double getHours() {
        return hours;
    }

    /**
     * Set the hours.
     *
     * @param hours the hours to set
     */
    public void setHours(Double hours) {
        this.hours = hours;
    }

    /**
     * Get the offering id.
     *
     * @return the offeringId
     */
    public String getOfferingId() {
        return offeringId;
    }

    /**
     * Set the offering id.
     *
     * @param offeringId the offeringId to set
     */
    public void setOfferingId(String offeringId) {
        this.offeringId = offeringId;
    }

    /**
     * Get the template id.
     *
     * @return the templateId
     */
    public String getTemplateId() {
        return templateId;
    }

    /**
     * Set the template id.
     *
     * @param templateId the templateId to set
     */
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    /**
     * Get the usage id.
     *
     * @return the usageId
     */
    public String getUsageId() {
        return usageId;
    }

    /**
     * Set the usage id.
     *
     * @param usageId the usageId to set
     */
    public void setUsageId(String usageId) {
        this.usageId = usageId;
    }

    /**
     * Get the usage type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Set the usage type.
     *
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get the size of the usage.
     *
     * @return the size
     */
    public Long getSize() {
        return size;
    }

    /**
     * Set the size of the usage.
     *
     * @param size the size to set
     */
    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * Get the network id.
     *
     * @return the networkId
     */
    public String getNetworkId() {
        return networkId;
    }

    /**
     * Set the network id.
     *
     * @param networkId the networkId to set
     */
    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    /**
     * Get the usage start date.
     *
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Set the usage start date.
     *
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Get the usage end date.
     *
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Set the usage end date.
     *
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Get the virtual size.
     *
     * @return the virtualSize
     */
    public Long getVirtualSize() {
        return virtualSize;
    }

    /**
     * Set the virtual size.
     *
     * @param virtualSize the virtualSize to set
     */
    public void setVirtualSize(Long virtualSize) {
        this.virtualSize = virtualSize;
    }

    /**
     * Get the CPU speed.
     *
     * @return the cpuSpeed
     */
    public Integer getCpuSpeed() {
        return cpuSpeed;
    }

    /**
     * Set the CPU speed.
     *
     * @param cpuSpeed the cpuSpeed to set
     */
    public void setCpuSpeed(Integer cpuSpeed) {
        this.cpuSpeed = cpuSpeed;
    }

    /**
     * Get the CPU Cores.
     *
     * @return the cpuCores
     */
    public Integer getCpuCores() {
        return cpuCores;
    }

    /**
     * Set the CPU Cores.
     *
     * @param cpuCores the cpuCores to set
     */
    public void setCpuCores(Integer cpuCores) {
        this.cpuCores = cpuCores;
    }

    /**
     * Get the usage memory.
     *
     * @return the memory
     */
    public Integer getMemory() {
        return memory;
    }

    /**
     * Set the usage memory.
     *
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
     * Get the created user.
     *
     * @return createdBy
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the created user.
     *
     * @param createdBy the User to set
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updated user.
     *
     * @return updatedBy
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updated user.
     *
     * @param updatedBy the User to set
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the created date.
     *
     * @return createdDateTime
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the created date.
     *
     * @param createdDateTime the DateTime to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updated date.
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
