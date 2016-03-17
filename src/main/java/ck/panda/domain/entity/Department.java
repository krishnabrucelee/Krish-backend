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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Type;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import ck.panda.constants.CloudStackConstants;
import ck.panda.domain.entity.User.UserType;
import ck.panda.util.JsonUtil;

/**
 * Departments are the first level hierarchy and we are grouping the departments with different roles. Roles should be
 * classified based on Departments.
 */
@Entity
@Table(name = "departments")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class Department implements Serializable {

    /** Id of the department. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Cloudstack's account uuid. */
    @Column(name = "uuid")
    private String uuid;

    /** Domain of the department. */
    @JoinColumn(name = "domain_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Domain domain;

    /** Domain id of the department. */
    @Column(name = "domain_id")
    private Long domainId;

    /** Department users. */
    @OneToMany
    private List<User> user;

    /** Description of the department. */
    @Column(name = "description")
    private String description;

    /** User name of the account. */
    @Column(name = "user_name")
    private String userName;

    /** Set sync status. */
    @Transient
    private Boolean syncFlag;

    /** Type of the department. */
    @Column(name = "account_type")
    private AccountType type;

    /** Check whether department is active state or in active state. */
    @Column(name = "is_active")
    private Boolean isActive;

    /** Status for department, whether it is Deleted, Disabled etc . */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    /** Version attribute to handle optimistic locking. */
    @Version
    @Column(name = "version")
    private Long version;

    /** Created user id. */
    @CreatedBy
    @Column(name = "created_user_id")
    private Long createdBy;

    /** Last updated user id. */
    @LastModifiedBy
    @Column(name = "upated_user_id")
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

    /** Transient domain of the account. */
    @Transient
    private String transDomainId;

    /**
     * Default constructor.
     */
    public Department() {
        super();
    }

    /**
     * Parameterized constructor.
     *
     * @param name to set
     */
    public Department(String name) {
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
     * @param id the Long to set
     */
    public void setId(Long id) {
        this.id = id;
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
     * Set the domain.
     *
     * @param domain the domain to set
     */
    public void setDomain(Domain domain) {
        this.domain = domain;
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
     * @param description the String to set.
     */
    public void setDescription(String description) {
        this.description = description;
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
     * Get the created user id.
     *
     * @return createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the created user id.
     *
     * @param createdBy the User to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the update user id.
     *
     * @return updatedBy
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the update user id.
     *
     * @param updatedBy the User to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
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
     * Get state of the department.
     *
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set state of the department.
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
     * Get the department account type.
     *
     * @return the type
     */
    public AccountType getType() {
        return type;
    }

    /**
     * Set the department account type.
     *
     * @param type the type to set
     */
    public void setType(AccountType type) {
        this.type = type;
    }

    /**
     * Get the username of the department.
     *
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Set the username of the department.
     *
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Get the uuid of the department.
     *
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the department uuid from cloudstack.
     *
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get the list of users of the department.
     *
     * @return the user
     */
    public List<User> getUser() {
        return user;
    }

    /**
     * Set the list of users of the department.
     *
     * @param user the user to set
     */
    public void setUser(List<User> user) {
        this.user = user;
    }

    /**
     * Get the sync status.
     *
     * @return the syncFlag
     */
    public Boolean getSyncFlag() {
        return syncFlag;
    }

    /** Set the sync status.
     *
     * @param syncFlag the syncFlag to set
     */
    public void setSyncFlag(Boolean syncFlag) {
        this.syncFlag = syncFlag;
    }

    /**
     * Get transient domain id.
     *
     * @return the transDomainId
     */
    public String getTransDomainId() {
        return transDomainId;
    }

    /**
     * Set the trans domain id .
     *
     * @param transDomainId to set
     */
    public void setTransDomainId(String transDomainId) {
        this.transDomainId = transDomainId;
    }

    /** Define user type. */
    public enum AccountType {
        /** Domain admin status make department as Domain Admin type. */
        DOMAIN_ADMIN,
        /** Root admin status make department as Root Admin type. */
        ROOT_ADMIN,
        /** User status make department as user type. */
        USER
    }

    /**
     * Enumeration status for Department.
     */
    public enum Status {
        /** Deleted status make department as soft deleted and it will not list on the applicaiton. */
        DELETED,
        /** Enabled status is used to list departments through out the application. */
        ENABLED
    }

    /**
     * Convert JSONObject into Department object.
     *
     * @param jsonObject json object from cloud stack.
     * @return user object.
     * @throws JSONException error occurs.
     */
    public static Department convert(JSONObject jsonObject) throws JSONException {
        Department department = new Department();
        department.setSyncFlag(false);
        try {
            department.setUuid(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_ID));
            department.setUserName(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_NAME));
            if (JsonUtil.getIntegerValue(jsonObject, CloudStackConstants.CS_ACCOUNT_TYPE) == 2) {
                department.setType(AccountType.DOMAIN_ADMIN);
            } else if (JsonUtil.getIntegerValue(jsonObject, CloudStackConstants.CS_ACCOUNT_TYPE) == 1) {
                department.setType(AccountType.ROOT_ADMIN);
            } else {
                department.setType(AccountType.USER);
            }
            department.setTransDomainId(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_DOMAIN_ID));
            department.setIsActive(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return department;
    }

    /**
     * Mapping entity object into list.
     *
     * @param departmentList list of department.
     * @return user map
     */
    public static Map<String, Department> convert(List<Department> departmentList) {
        Map<String, Department> departmentMap = new HashMap<String, Department>();
        for (Department department : departmentList) {
            departmentMap.put(department.getUuid(), department);
        }
        return departmentMap;
    }
}
