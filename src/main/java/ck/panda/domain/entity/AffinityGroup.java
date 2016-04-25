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
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import ck.panda.constants.CloudStackConstants;
import ck.panda.util.JsonUtil;
import ck.panda.util.JsonValidator;

/**
 *
 * Affinity group for grouping the virtual machine.
 */
@Entity
@Table(name = "affinity_group")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class AffinityGroup implements Serializable {

    /** Id of the affinity group. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Unique id of the affinity group. */
    @Column(name = "uuid")
    private String uuid;

    /** Name of the affinity group. */
    @NotEmpty
    @Column(name = "name")
    private String name;

    /** Description of the affinity group. */
    @Column(name = "description")
    private String description;

    /** Group of the affinity group. */
    @JoinColumn(name = "affinity_group_type_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private AffinityGroupType affinityGroupType;

    /** Group id of the affinity group. */
    @Column(name = "affinity_group_type_id")
    private Long affinityGroupTypeId;

    /** Domain of the affinity group. */
    @JoinColumn(name = "domain_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Domain domain;

    /** Domain id of the affinity group. */
    @Column(name = "domain_id")
    private Long domainId;

    /** Department of the affinity group. */
    @OneToOne
    @JoinColumn(name = "department_id", referencedColumnName = "id", updatable = false, insertable = false)
    private Department department;

    /** Department id of the affinity group. */
    @Column(name = "department_id")
    private Long departmentId;

    /** Status of the affinity group. */
    @Column(name = "is_active")
    private Boolean isActive;

    /** Affinity group current state. */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

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

    /** Temporary variable. */
    @Transient
    private Boolean isSyncFlag;

    /** Transient domain of the account. */
    @Transient
    private String transDomainId;

    /** Transient department of the user. */
    @Transient
    private String transDepartment;

    /** Transient affinity group type. */
    @Transient
    private String transAffinityGroupType;

    /** Transient instance list. */
    @Transient
    private List<String> transInstanceList;

    /** Transient affinity group type access status. */
    @Transient
    private String transAffinityGroupAccessFlag;

    /** Default constructor. */
    public AffinityGroup() {
        super();
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
     * @param id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the uuid.
     *
     * @return uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the uuid.
     *
     * @param uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
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
     * @param name to set
     */
    public void setName(String name) {
        this.name = name;
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
     * @param description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the affinity group type.
     *
     * @return affinityGroupType
     */
    public AffinityGroupType getAffinityGroupType() {
        return affinityGroupType;
    }

    /**
     * Set the affinity group type.
     *
     * @param affinityGroupType to set
     */
    public void setAffinityGroupType(AffinityGroupType affinityGroupType) {
        this.affinityGroupType = affinityGroupType;
    }

    /**
     * Get the affinity group type id.
     *
     * @return affinityGroupTypeId
     */
    public Long getAffinityGroupTypeId() {
        return affinityGroupTypeId;
    }

    /**
     * Set the affinity group type id.
     *
     * @param affinityGroupTypeId to set
     */
    public void setAffinityGroupTypeId(Long affinityGroupTypeId) {
        this.affinityGroupTypeId = affinityGroupTypeId;
    }

    /**
     * Get the domain.
     *
     * @return domain
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
     * Get the domain id.
     *
     * @return domainId
     */
    public Long getDomainId() {
        return domainId;
    }

    /**
     * Set the domain id.
     *
     * @param domainId to set
     */
    public void setDomainId(Long domainId) {
        this.domainId = domainId;
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
     * @param department to set
     *
     */
    public void setDepartment(Department department) {
        this.department = department;
    }

    /**
     * Get the department id.
     *
     * @return departmentId
     */
    public Long getDepartmentId() {
        return departmentId;
    }

    /**
     * Set the department id.
     *
     * @param departmentId to set
     *
     */
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * Get the active status.
     *
     * @return isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set the active status.
     *
     * @param isActive to set
     *
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Get the Sync Flag.
     *
     * @return the isSyncFlag.
     */
    public Boolean getIsSyncFlag() {
        return isSyncFlag;
    }

    /**
     * Set the Sync Flag.
     *
     * @param isSyncFlag to set.
     */
    public void setIsSyncFlag(Boolean isSyncFlag) {
        this.isSyncFlag = isSyncFlag;
    }

    /**
     * Get the status of the application.
     *
     * @return status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set the status of the application.
     *
     * @param status to set
     *
     */
    public void setStatus(Status status) {
        this.status = status;
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
     * @param version to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get the created user.
     *
     * @return createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the created user.
     *
     * @param createdBy to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updated user.
     *
     * @return updatedBy
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updated user.
     *
     * @param updatedBy to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the transient department.
     *
     * @return transDepartment
     */
    public String getTransDepartment() {
        return transDepartment;
    }

    /**
     * Set the transient department.
     *
     * @param transDepartment to set
     */
    public void setTransDepartment(String transDepartment) {
        this.transDepartment = transDepartment;
    }

    /**
     * Get the transient affinity group type.
     *
     * @return transAffinityGroupType
     */
    public String getTransAffinityGroupType() {
        return transAffinityGroupType;
    }

    /**
     * Set the transient affinity group type.
     *
     * @param transAffinityGroupType to set
     */
    public void setTransAffinityGroupType(String transAffinityGroupType) {
        this.transAffinityGroupType = transAffinityGroupType;
    }

    /**
     * Get the transient instance list.
     *
     * @return transInstanceList
     */
    public List<String> getTransInstanceList() {
        return transInstanceList;
    }

    /**
     * Set the transient instance list.
     *
     * @param transInstanceList to set
     */
    public void setTransInstanceList(List<String> transInstanceList) {
        this.transInstanceList = transInstanceList;
    }

    /**
     * Get the transient affinity access flag.
     *
     * @return transAffinityGroupAccessFlag
     */
    public String getTransAffinityGroupAccessFlag() {
        return transAffinityGroupAccessFlag;
    }

    /**
     * Set the transient affinity access flag.
     *
     * @param transAffinityGroupAccessFlag to set
     */
    public void setTransAffinityGroupAccessFlag(String transAffinityGroupAccessFlag) {
        this.transAffinityGroupAccessFlag = transAffinityGroupAccessFlag;
    }

    /**
     * Get transient domain id.
     *
     * @return transDomainId
     */
    public String getTransDomainId() {
        return transDomainId;
    }

    /**
     * Set the transient domain id.
     *
     * @param transDomainId to set
     */
    public void setTransDomainId(String transDomainId) {
        this.transDomainId = transDomainId;
    }

    /**
     * Get the created date time.
     *
     * @return createdDateTime
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the created date time.
     *
     * @param createdDateTime to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updated date time.
     *
     * @return updatedDateTime
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updated date time.
     *
     * @param updatedDateTime to set
     *
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /** Status enum type used to list the status values. */
    public enum Status {
        /** affinity group status as Disabled. */
        DISABLED,
        /** affinity group status as Enabled. */
        ENABLED
    }

    /** Enum type for group. */
    public enum GroupType {
        /** Affinity group type. */
        HOST_ANTI_AFFINITY
    }

    /**
     * Convert JSONObject into user object.
     *
     * @param jsonObject JSON object.
     * @return user object.
     * @throws Exception error occurs.
     */
    public static AffinityGroup convert(JSONObject jsonObject) throws Exception {
        AffinityGroup affinityGroup = new AffinityGroup();
        affinityGroup.setIsSyncFlag(false);
        affinityGroup.setUuid(JsonValidator.jsonStringValidation(jsonObject, CloudStackConstants.CS_ID));
        affinityGroup.setName(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_NAME));
        affinityGroup.setDescription(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_DESCRIPTION));
        affinityGroup.setTransDepartment(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_ACCOUNT));
        affinityGroup.setTransDomainId(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_DOMAIN_ID));
        affinityGroup.setTransAffinityGroupType(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_TYPE));
        affinityGroup.setIsActive(true);
        return affinityGroup;
    }

    /**
     * Mapping entity object into list.
     *
     * @param affinityGroupList list of affinity group.
     * @return affinity group map
     */
    public static Map<String, AffinityGroup> convert(List<AffinityGroup> affinityGroupList) {
        Map<String, AffinityGroup> affinityGroupMap = new HashMap<String, AffinityGroup>();
        for (AffinityGroup affinityGroup : affinityGroupList) {
            affinityGroupMap.put(affinityGroup.getUuid(), affinityGroup);
        }
        return affinityGroupMap;
    }
}
