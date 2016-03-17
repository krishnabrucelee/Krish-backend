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
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Type;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import ck.panda.constants.CloudStackConstants;
import ck.panda.util.JsonUtil;

/** User entity. */
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class User implements Serializable {

    /** Id of the user. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** User name of the user. */
    @NotNull
    @Column(name = "user_name")
    private String userName;

    /** Password of the user. */
    @Column(name = "password")
    private String password;

    /** Department of the user. */
    @ManyToOne
    @JoinColumn(name = "department_id", referencedColumnName = "id", updatable = false, insertable = false)
    private Department department;

    /** Department id of the user. */
    @Column(name = "department_id")
    private Long departmentId;

    /** Domain of the user. */
    @JoinColumn(name = "domain_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Domain domain;

    /** Domain id of the user. */
    @Column(name = "domain_id")
    private Long domainId;

    /** User role. */
    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id", updatable = false, insertable = false)
    private Role role;

    /** Role id of the user. */
    @Column(name = "role_id")
    private Long roleId;

    /** Email of the user. */
    @Column(name = "email")
    private String email;

    /** User type of the user. */
    @Column(name = "type")
    private UserType type;

    /** First name of the user. */
    @Column(name = "first_name")
    private String firstName;

    /** Last name of the user. */
    @Column(name = "last_name")
    private String lastName;

    /** List of projects for users. */
    @Transient
    private List<Project> projectList;

    /** User uuid. */
    @Column(name = "uuid")
    private String uuid;

    /** User status. */
    @Column(name = "status")
    private Status status;

    /** IsActive attribute to verify Active or Inactive. */
    @Column(name = "is_active")
    private Boolean isActive;

    /** Version attribute to handle optimistic locking. */
    @Version
    @Column(name = "version")
    private Long version;

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

    /** Created by user. */
    @CreatedBy
    @Column(name = "created_user_id")
    private Long createdBy;

    /** Last updated by user. */
    @LastModifiedBy
    @Column(name = "updated_user_id")
    private Long updatedBy;

    /** Transient domain of the user. */
    @Transient
    private String transDomainId;

    /** Transient account of the user. */
    @Transient
    private String transAccount;

    /** Transient department of the user. */
    @Transient
    private String transDepartment;

    /** Set syncFlag. */
    @Transient
    private Boolean syncFlag;

    /** API key of the user. */
    @Transient
    private String apiKey;

    /** API key of the user. */
    @Transient
    private String secretKey;

    /** Confirm Password of the user. */
    @Transient
    private String confirmPassword;

    /** Define user type. */
    public enum UserType {
        /** Define type constant. */
        DOMAIN_ADMIN, ROOT_ADMIN, USER;
    }

    /** Define status. */
    public enum Status {
        /** Define status constant. */
        ACTIVE, BLOCKED, DELETED, DISABLED, ENABLED;
    }

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
     * Get the userName.
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
     * @param department the department to set.
     */
    public void setDepartment(Department department) {
        this.department = department;
    }

    /**
     * Get the departmentId.
     *
     * @return the departmentId.
     */
    public Long getDepartmentId() {
        return departmentId;
    }

    /**
     * Set the departmentId.
     *
     * @param departmentId the departmentId to set.
     */
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
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
     * @param domain the domain to set.
     */
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    /**
     * Get the role.
     *
     * @return the role.
     */
    public Role getRole() {
        return role;
    }

    /**
     * Set the role.
     *
     * @param role to set.
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Get the email.
     *
     * @return the email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the email.
     *
     * @param email to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the type.
     *
     * @return the type.
     */
    public UserType getType() {
        return type;
    }

    /**
     * Set the type.
     *
     * @param type to set.
     */
    public void setType(UserType type) {
        this.type = type;
    }

    /**
     * Get the firstName.
     *
     * @return the firstName.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Set the firstName.
     *
     * @param firstName to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Get the lastName.
     *
     * @return the lastName.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set the lastName.
     *
     * @param lastName to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Get the projectList.
     *
     * @return the projectList
     */
    public List<Project> getProjectList() {
        return projectList;
    }

    /**
     * Set the projectList.
     *
     * @param projectList the projectList to set
     */
    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }

    /**
     * Get the user uuid.
     *
     * @return the uuid.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the user uuid.
     *
     * @param uuid to set.
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
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
     * @param status to set.
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Get the active data.
     *
     * @return the isActive.
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set the active data.
     *
     * @param isActive to set.
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
     * Get the created date time.
     *
     * @return the createdDateTime
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the created date time.
     *
     * @param createdDateTime - the DateTime to set
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
     * @param updatedDateTime - the DateTime to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get the created user.
     *
     * @return the createdBy.
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the created user.
     *
     * @param createdBy to set.
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updated user.
     *
     * @return the updatedBy.
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updated user.
     *
     * @param updatedBy to set.
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
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
     * Set the transient domain id.
     *
     * @param transDomainId to set
     */
    public void setTransDomainId(String transDomainId) {
        this.transDomainId = transDomainId;
    }

    /**
     * Get the transient account.
     *
     * @return the transAccount
     */
    public String getTransAccount() {
        return transAccount;
    }

    /**
     * Set the transAccount .
     *
     * @param transAccount to set
     */
    public void setTransAccount(String transAccount) {
        this.transAccount = transAccount;
    }

    /**
     * Get the transient Department.
     *
     * @return the transDepartment
     */
    public String getTransDepartment() {
        return transDepartment;
    }

    /**
     * Set the transDepartment.
     *
     * @param transDepartment to set
     */
    public void setTransDepartment(String transDepartment) {
        this.transDepartment = transDepartment;
    }

    /**
     * Get the domain Id.
     *
     * @return the domainId
     */
    public Long getDomainId() {
        return domainId;
    }

    /**
     * Set the domainId .
     *
     * @param domainId to set
     */
    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    /**
     * Get the API Key.
     *
     * @return the apiKey
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * Set the API Key.
     *
     * @param apiKey to set
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Get the secret key.
     *
     * @return the secretKey
     */
    public String getSecretKey() {
        return secretKey;
    }

    /**
     * Set the secret key .
     *
     * @param secretKey to set
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * Get the confirm password.
     *
     * @return the confirmPassword
     */
    public String getConfirmPassword() {
        return confirmPassword;
    }

    /**
     * Set the confirm password.
     *
     * @param confirmPassword the confirm password to set
     */
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    /**
     * Get the roleId.
     *
     * @return the roleId
     */
    public Long getRoleId() {
        return roleId;
    }

    /**
     * Set the roleId.
     *
     * @param roleId the roleId to set
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    /**
     * Convert JSONObject into user object.
     *
     * @param jsonObject JSON object.
     * @return user object.
     * @throws Exception error occurs.
     */
    public static User convert(JSONObject jsonObject) throws Exception {

        User user = new User();
        user.setSyncFlag(false);
        user.setUuid(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_ID));
        user.setUserName(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_USER_NAME));
        user.setFirstName(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_FIRST_NAME));
        user.setLastName(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_LAST_NAME));
        user.setEmail(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_EMAIL));
        if (JsonUtil.getIntegerValue(jsonObject, CloudStackConstants.CS_ACCOUNT_TYPE) == 2) {
            user.setType(UserType.DOMAIN_ADMIN);
        } else if (JsonUtil.getIntegerValue(jsonObject, CloudStackConstants.CS_ACCOUNT_TYPE) == 1) {
            user.setType(UserType.ROOT_ADMIN);
        } else {
            user.setType(UserType.USER);
        }
        user.setTransDomainId(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_DOMAIN_ID));
        user.setTransDepartment(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_ACCOUNT_ID));
        user.setIsActive(true);
        user.setStatus(Status.valueOf(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_STATE).toUpperCase()));
        return user;
    }

    /**
     * Mapping entity object into list.
     *
     * @param userList list of users.
     * @return user map
     */
    public static Map<String, User> convert(List<User> userList) {
        Map<String, User> userMap = new HashMap<String, User>();
        for (User user : userList) {
            userMap.put(user.getUuid(), user);
        }
        return userMap;
    }
}
