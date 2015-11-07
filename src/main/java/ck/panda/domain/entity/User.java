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
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import ck.panda.service.DepartmentService;

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

    /** User domain. */
    @ManyToOne
    @JoinColumn(name = "domain_id", referencedColumnName = "id")
    private Domain domain;

    /** User role. */
    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

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

    /** Set syncFlag. */
    @Transient
    private Boolean syncFlag;

    /** Autowired department object. */
    @Autowired
	private static DepartmentService departmentService;

    /** Define user type. */
    public enum Type {
       /** Define type constant. */
        USER,
        ADMIN;
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
     * Convert JSONObject into user object.
     *
     * @param object JSON object.
     * @return user object.
     * @throws Exception
     */
    public static User convert(JSONObject object) throws Exception {

        User user = new User();
        user.uuid = object.get("id").toString();
        user.userName = object.get("username").toString();
        user.firstName = object.get("firstname").toString();
        user.lastName = object.get("lastname").toString();
        user.email = object.has("email") ? object.get("email").toString() : "";
        user.type = ((Integer)object.get("accounttype") == 0 ? User.Type.USER : User.Type.ADMIN);
        user.department = departmentService.findByUuid(object.get("accountid").toString());
        user.setSyncFlag(false);
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