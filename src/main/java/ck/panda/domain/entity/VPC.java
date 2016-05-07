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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Type;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import ck.panda.constants.CloudStackConstants;
import ck.panda.util.JsonUtil;
import ck.panda.util.JsonValidator;

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
    @Column(name = "cidr", nullable = false)
    private String cIDR;

    /** Description of the VPC. */
    @Column(name = "description", nullable = false)
    private String description;

    /** An optional field, whether to the display the vpc to the end user or not. */
    @Column(name = "for_display")
    private Boolean forDisplay;

    /**If set to false, the VPC won't start (VPC VR will not get allocated) until its first network gets implemented. True by default. */
    @Column(name = "start", columnDefinition = "tinyint default 1")
    private Boolean start;

    /** Domain of the VPC. */
    @JoinColumn(name = "domain_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @OneToOne
    private Domain domain;

    /** Offering of the VPC. */
    @JoinColumn(name = "vpcoffering_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @OneToOne
    private VpcOffering vpcOffering;

    /** Offering id of the VPC. */
    @Column(name = "vpcoffering_id")
    private Long vpcofferingId;

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

    /** Distributed VPC router status. */
    @Column(name = "distributed_vpc_router")
    private Boolean distributedVpcRouter;
    /** IsActive attribute to verify Active or Inactive. */
    @Column(name = "is_active")
    private Boolean isActive;

    /** VPC network domain. All networks inside the VPC will belong to this domain. */
    @Column(name = "network_domain")
    private String networkDomain;

    /** Removing old elements of the vpc. */
    @Column(name = "clean_up")
    private Boolean cleanUpVPC;

    /** Make redundant vpc. */
    @Column(name = "redundant_vpc")
    private Boolean redundantVPC;

    /** Make restart required true/false. */
    @Column(name = "restart_vpc")
    private Boolean restartRequired;

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

    /** Transient VPC offering id. */
    @Transient
    private String transVpcOfferingId;

    /** Transient account of the VPC. */
    @Transient
    private String transAccount;

    /** Transient domain id of the VPC. */
    @Transient
    private String transDomainId;

    /** Transient project id of the VPC. */
    @Transient
    private String transProjectId;

    /** Transient zone of the VPC. */
    @Transient
    private String transZoneId;

    /** Enumeration status for VPC. */
    public enum Status {
        /** Inactive status make VPC as soft deleted and it will not list on the applicaiton. */
        INACTIVE,
        /** Enabled status is used to list VPC through out the application. */
        ENABLED
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
    public Boolean getForDisplay() {
        return forDisplay;
    }

    /**
     * Set the for display.
     *
     * @param forDisplay  to set
     */
    public void setForDisplay(Boolean forDisplay) {
        this.forDisplay = forDisplay;
    }

    /**
     * Get start element of the Vpc.
     *
     * @return the start
     */
    public Boolean getStart() {
        return start;
    }

    /**
     * Set start element of the Vpc.
     *
     * @param start  to set
     */
    public void setStart(Boolean start) {
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
     * Get the distributed VPC router.
     *
     * @return the distributedVpcRouter.
     */
    public Boolean getDistributedVpcRouter() {
        return distributedVpcRouter;
    }

    /**
     * Set the distributed VPC router.
     *
     * @param distributedVpcRouter to set
     */
    public void setDistributedVpcRouter(Boolean distributedVpcRouter) {
        this.distributedVpcRouter = distributedVpcRouter;
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

    /**
     * Get network domain for VPC.
     *
     * @return the networkDomain
     */
    public String getNetworkDomain() {
        return networkDomain;
    }

    /**
     * Set network domain for VPC.
     *
     * @param networkDomain the networkDomain to set
     */
    public void setNetworkDomain(String networkDomain) {
        this.networkDomain = networkDomain;
    }

    /**
     * Get vpc offering.
     *
     * @return the vpcOffering
     */
    public VpcOffering getVpcOffering() {
        return vpcOffering;
    }

    /**
     * Set vpc offering id.
     *
     * @return the vpcoffering_id
     */
    public Long getVpcofferingid() {
        return vpcofferingId;
    }

    /**
     * Get vpc offering.
     *
     * @param vpcOffering the vpcOffering to set
     */
    public void setVpcOffering(VpcOffering vpcOffering) {
        this.vpcOffering = vpcOffering;
    }

    /**
     * Set vpc offering id.
     *
     * @param vpcoffering_id the vpcoffering_id to set
     */
    public void setVpcofferingid(Long vpcofferingId) {
        this.vpcofferingId = vpcofferingId;
    }

    /**
     * Get the cleanup vpc.
     *
     * @return the cleanUpVPC
     */
    public Boolean getCleanUpVPC() {
        return cleanUpVPC;
    }

    /**
     * Set the cleanup vpc.
     *
     * @param cleanUpVPC the cleanUpVPC to set
     */
    public void setCleanUpVPC(Boolean cleanUpVPC) {
        this.cleanUpVPC = cleanUpVPC;
    }

    /**
     * Get redundant VPC details.
     *
     * @return the redundantVPC
     */
    public Boolean getRedundantVPC() {
        return redundantVPC;
    }

    /**
     * Make redundant VPC.
     *
     * @param redundantVPC the redundantVPC to set
     */
    public void setRedundantVPC(Boolean redundantVPC) {
        this.redundantVPC = redundantVPC;
    }

    /**
     * Get vpc restart required.
     *
     * @return the restartRequired
     */
    public Boolean getRestartRequired() {
        return restartRequired;
    }

    /**
     * Set vpc restart required.
     *
     * @param restartRequired the restartRequired to set
     */
    public void setRestartRequired(Boolean restartRequired) {
        this.restartRequired = restartRequired;
    }

    /**
     * Get transient VPC Offering id.
     *
     * @return the transVpcOfferingId
     */
    public String getTransVpcOfferingId() {
        return transVpcOfferingId;
    }

    /**
     * Set transient VPC Offering id.
     *
     * @param transVpcOfferingId to set
     */
    public void setTransVpcOfferingId(String transVpcOfferingId) {
        this.transVpcOfferingId = transVpcOfferingId;
    }

    /**
     * Get transient VPC account.
     *
     * @return the transAccount
     */
    public String getTransAccount() {
        return transAccount;
    }

    /**
     * Set transient VPC account.
     *
     * @param transAccount to set
     */
    public void setTransAccount(String transAccount) {
        this.transAccount = transAccount;
    }

    /**
     * Get transient VPC domain id.
     *
     * @return the transDomainId
     */
    public String getTransDomainId() {
        return transDomainId;
    }

    /**
     * Set transient VPC domain id.
     *
     * @param transDomainId to set
     */
    public void setTransDomainId(String transDomainId) {
        this.transDomainId = transDomainId;
    }

    /**
     * Get the transProjectId.
     *
     * @return the transProjectId
     */
    public String getTransProjectId() {
        return transProjectId;
    }

    /**
     * Set the transProjectId.
     *
     * @param transProjectId to set
     */
    public void setTransProjectId(String transProjectId) {
        this.transProjectId = transProjectId;
    }

    /**
     * Get the Zone Id.
     *
     * @return the transZoneId
     */
    public String getTransZoneId() {
        return transZoneId;
    }

    /**
     * Set the Zone Id.
     *
     * @param transZoneId the transZoneId to set
     */
    public void setTransZoneId(String transZoneId) {
        this.transZoneId = transZoneId;
    }

    /**
     * Convert JSONObject to VPC offering entity.
     *
     * @param object json object
     * @return VPC offering entity objects
     * @throws Exception unhandled errors.
     */
    public static VPC convert(JSONObject object) throws Exception {
        VPC vpc = new VPC();
        vpc.setSyncFlag(false);
        vpc.setIsActive(true);
        vpc.setUuid(JsonValidator.jsonStringValidation(object, CloudStackConstants.CS_ID));
        vpc.setName(JsonValidator.jsonStringValidation(object, CloudStackConstants.CS_NAME));
        vpc.setcIDR(JsonValidator.jsonStringValidation(object, CloudStackConstants.CS_CIDR));
        vpc.setDescription(JsonValidator.jsonStringValidation(object, CloudStackConstants.CS_DISPLAY_TEXT));
        vpc.setDistributedVpcRouter(JsonValidator.jsonBooleanValidation(object, CloudStackConstants.CS_DISTRIBUTED_VPC_ROUTER));
        vpc.setForDisplay(JsonValidator.jsonBooleanValidation(object, CloudStackConstants.CS_FOR_DISPLAY));
        vpc.setNetworkDomain(JsonValidator.jsonStringValidation(object, CloudStackConstants.CS_NETWORK_DOMAIN));
        vpc.setRedundantVPC(JsonValidator.jsonBooleanValidation(object, CloudStackConstants.CS_REDUNDANT_VPC_ROUTER));
        vpc.setRestartRequired(JsonValidator.jsonBooleanValidation(object, CloudStackConstants.CS_RESTART_REQUIRED));
        vpc.setTransVpcOfferingId(JsonValidator.jsonStringValidation(object, CloudStackConstants.CS_VPC_OFFERING_ID));
        vpc.setTransAccount(JsonValidator.jsonStringValidation(object, CloudStackConstants.CS_ACCOUNT));
        vpc.setTransDomainId(JsonValidator.jsonStringValidation(object, CloudStackConstants.CS_DOMAIN_ID));
        vpc.setTransProjectId(JsonUtil.getStringValue(object, CloudStackConstants.CS_PROJECT_ID));
        vpc.setTransZoneId((JsonUtil.getStringValue(object, CloudStackConstants.CS_ZONE_ID)));
        vpc.setStatus(Status.valueOf(JsonUtil.getStringValue(object, CloudStackConstants.CS_STATE).toUpperCase()));
        vpc.setStart(true);
        return vpc;
    }

    /**
     * Mapping VPC entity object in list.
     *
     * @param vpcList list of VPC offerings
     * @return VPC mapped values.
     */
    public static Map<String, VPC> convert(List<VPC> vpcList) {
        Map<String, VPC> vpcMap = new HashMap<String, VPC>();
        for (VPC vpc : vpcList) {
            vpcMap.put(vpc.getUuid(), vpc);
        }
        return vpcMap;
    }

}
