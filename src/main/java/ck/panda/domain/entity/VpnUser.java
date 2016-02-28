package ck.panda.domain.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
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

/**
 * Virtual private network entity.
 */
@Entity
@Table(name = "vpn_user")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class VpnUser implements Serializable {

    /** VPN user id. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** VPN user unique id. */
    @Column(name = "uuid")
    private String uuid;

    /** VPN user name. */
    @NotNull
    @Column(name = "user_name", nullable = false)
    private String userName;

    /** VPN user password. */
    @Transient
    private String password;

    /** Domain of the VPN user. */
    @JoinColumn(name = "domain_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Domain domain;

    /** Domain id of the VPN user. */
    @Column(name = "domain_id")
    private Long domainId;

    /** Project of the VPN user. */
    @JoinColumn(name = "project_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Project project;

    /** Project id of the VPN user. */
    @Column(name = "project_id")
    private Long projectId;

    /** Department of the VPN user. */
    @JoinColumn(name = "department_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Department department;

    /** Department id of the VPN user. */
    @Column(name = "department_id")
    private Long departmentId;

    /** IsActive attribute to verify Active or Inactive. */
    @Column(name = "is_active")
    private Boolean isActive;

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

    /** Modified date and time. */
    @LastModifiedDate
    @Column(name = "updated_date_time")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentZonedDateTime")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime updatedDateTime;

    /** Set sync flag. */
    @Transient
    private Boolean syncFlag;

    /** Transient domain of the VPN user. */
    @Transient
    private String transDomainId;

    /** Transient host of the VPN user. */
    @Transient
    private String transProjectId;

    /** Transient department of the VPN user. */
    @Transient
    private String transDepartment;

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
     * Get VPN user uuid.
     *
     * @return the uuid.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * set VPN user uuid.
     *
     * @param uuid to set.
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get the user name.
     *
     * @return the userName.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Set the user name.
     *
     * @param userName to set.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Get the password.
     *
     * @return the password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the password.
     *
     * @param password to set.
     */
    public void setPassword(String password) {
        this.password = password;
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
     * Get the is active status.
     *
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set the is active status.
     *
     * @param isActive the is active to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
     * Get the transient domain id.
     *
     * @return the transDomainId
     */
    public String getTransDomainId() {
        return transDomainId;
    }

    /**
     * Set the transient domain id..
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
     * Get the transDepartment.
     *
     * @return the transDepartment
     */
    public String getTransDepartment() {
        return transDepartment;
    }

    /**
     * Get the transDepartment.
     *
     * @param transDepartment to set
     */
    public void setTransDepartment(String transDepartment) {
        this.transDepartment = transDepartment;
    }

    /**
     * Convert JSONObject into VPN user entity.
     *
     * @param jsonObject JSON object.
     * @return pod object.
     */
    public static VpnUser convert(JSONObject jsonObject) {
        VpnUser vpnUser = new VpnUser();
        try {
            vpnUser.setUuid(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_ID));
            vpnUser.setUserName(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_USER_NAME));
            vpnUser.setTransDomainId(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_DOMAIN_ID));
            vpnUser.setTransProjectId(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_PROJECT_ID));
            vpnUser.setTransDepartment(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_ACCOUNT));
            vpnUser.setSyncFlag(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return vpnUser;
    }

    /**
     * Mapping entity object into list.
     *
     * @param vpnUserList list of VPN users.
     * @return User list map
     */
    public static Map<String, VpnUser> convert(List<VpnUser> vpnUserList) {
        Map<String, VpnUser> vpnUserMap = new HashMap<String, VpnUser>();
        for (VpnUser vpnUser : vpnUserList) {
            vpnUserMap.put(vpnUser.getUuid(), vpnUser);
        }
        return vpnUserMap;
    }
}
