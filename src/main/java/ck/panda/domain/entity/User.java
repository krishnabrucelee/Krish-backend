package ck.panda.domain.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import ck.panda.util.JsonUtil;

/** User entity. */
@Entity
@Table(name = "ck_users")
public class User {

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
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    private Department department;

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
    private Type type;

    /** First name of the user.  */
    @Column(name = "first_name")
    private String firstName;

    /** Last name of the user.  */
    @Column(name = "last_name")
    private String lastName;

    /** List of projects for users. */
    @ManyToMany
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
    @Column(name = "created_date_time")
    private DateTime createdDateTime;

    /** Last modified date and time. */
    @Column(name = "updated_date_time")
    private DateTime updatedDateTime;

    /** Created by user. */
    @CreatedBy
    @JoinColumn(name = "created_user_id", referencedColumnName = "id")
    @OneToOne(cascade = {CascadeType.ALL })
    private User createdBy;

    /** Last updated by user. */
    @LastModifiedBy
    @JoinColumn(name = "updated_user_id", referencedColumnName = "id")
    @OneToOne(cascade = {CascadeType.ALL })
    private User updatedBy;

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

    /** Define user type. */
    public enum Type {
       /** Define type constant. */
        USER,
        ROOT_ADMIN,
        DOMAIN_ADMIN;
    }

    /** Define status. */
    public enum Status {
       /** Define status constant. */
        DELETED,
        BLOCKED;
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
     * @return the department
     */
    public Department getDepartment() {
        return department;
    }

    /**
     * @param department the department to set
     */
    public void setDepartment(Department department) {
        this.department = department;
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
    public Type getType() {
        return type;
    }

    /**
     * Set the type.
     *
     * @param type to set.
     */
    public void setType(Type type) {
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
     * @return the projectList
     */
    public List<Project> getProjectList() {
        return projectList;
    }

    /**
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
     * @return the createdDateTime.
     */
    public DateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the created date time.
     *
     * @param createdDateTime to set.
     */
    public void setCreatedDateTime(DateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updated date time.
     *
     * @return the updatedDateTime.
     */
    public DateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updated date time.
     *
     * @param updatedDateTime to set.
     */
    public void setUpdatedDateTime(DateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get the created user.
     *
     * @return the createdBy.
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the created user.
     *
     * @param createdBy to set.
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updated user.
     *
     * @return the updatedBy.
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updated user.
     *
     * @param updatedBy to set.
     */
    public void setUpdatedBy(User updatedBy) {
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
    * Set  the transAccount .
    *
    * @param transAccountto set
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
    * @param transDepartment  to set
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
	 * @return the roleId
	 */
	public Long getRoleId() {
		return roleId;
	}

	/**
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
        user.setUuid(JsonUtil.getStringValue(jsonObject, "id"));
        user.setUserName(JsonUtil.getStringValue(jsonObject, "username"));
        user.setFirstName(JsonUtil.getStringValue(jsonObject, "firstname"));
        user.setLastName(JsonUtil.getStringValue(jsonObject, "lastname"));
        user.setEmail(JsonUtil.getStringValue(jsonObject, "email"));
        if (JsonUtil.getIntegerValue(jsonObject, "accounttype") == 0) {
            user.setType(Type.USER);
        } else if (JsonUtil.getIntegerValue(jsonObject, "accounttype") == 1) {
            user.setType(Type.ROOT_ADMIN);
        } else {
            user.setType(Type.DOMAIN_ADMIN);
        }
        user.setTransDomainId(JsonUtil.getStringValue(jsonObject, "domainid"));
        user.setTransDepartment(JsonUtil.getStringValue(jsonObject, "accountid"));
        user.setIsActive(true);
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
