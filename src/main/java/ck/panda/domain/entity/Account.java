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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Type;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import ck.panda.util.JsonUtil;

/**
 * Departments are the first level hierarchy and we are grouping the departments
 * with different roles.
 * Roles should be classified based on Departments.
 */
@Entity
@Table(name = "ck_Account")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class Account implements Serializable {

    /** Id of the Department. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** User uuid. */
    @Column(name = "uuid")
    private String uuid;

    /** Domain of the account. */
    @JoinColumn(name = "domain_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Domain domain;

    /** Domain id of the account. */
    @Column(name = "domain_id")
    private Long domainId;

    /** Department users. */
    @OneToMany
    private List<User> user;

    /** Description of the Department. */
    @Column(name = "description")
    private String description;

    /** First name of the user. */
    @Column(name = "first_name")
    private String firstName;

    /** Last name of the user.  */
    @Column(name = "last_name")
    private String lastName;

    /** User name of the account. */
    @Column(name = "user_name")
    private String userName;

    /** Password of the account. */
    @Column(name = "password")
    private String password;

    /** Set syncFlag. */
    @Transient
    private Boolean syncFlag;

    /** Email of the user. */
    @Column(name = "email")
    private String email;

    /** User type of the user. */
    @Column(name = "account_type")
    private AccountType type;

    /** Check whether Department is in active state or in active state. */
    @Column(name = "is_active")
    private Boolean isActive;

    /** Status for Department, whether it is Deleted, Disabled etc . */
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

    /** Transient domain of the account. */
    @Transient
    private String transDomainId;

    /**
     * Default constructor.
     */
    public Account() {
        super();
    }

    /**
     * Parameterized constructor.
     *
     * @param name to set
     */
    public Account(String name) {
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
     * Get the createdBy.
     *
     * @return createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the createdBy.
     *
     * @param createdBy the User to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updatedBy.
     *
     * @return updatedBy
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updatedBy.
     *
     * @param updatedBy the User to set
     */
    public void setUpdatedBy(Long updatedBy) {
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
     * Get is Active state of the Department.
     *
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set is Active state of the Department.
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
     * Get the email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the email.
     *
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the  account type.
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
     * Get the department password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the department account password.
     *
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get the department firstname.
     *
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Set the department firstname.
     *
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Get the lastname.
     *
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set the lastname.
     *
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
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
     * @return the user
     */
    public List<User> getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(List<User> user) {
        this.user = user;
    }

    /**
     * @return the syncFlag
     */
    public Boolean getSyncFlag() {
        return syncFlag;
    }

    /**
     * @param syncFlag the syncFlag to set
     */
    public void setSyncFlag(Boolean syncFlag) {
        this.syncFlag = syncFlag;
    }

    /**
     * Get the transient DomainId.
     *
    * @return the transDomainId
    */
    public String getTransDomainId() {
        return transDomainId;
    }

    /**
    * Set the transDomainId.
    *
    * @param transDomainId  to set
    */
    public void setTransDomainId(String transDomainId) {
        this.transDomainId = transDomainId;
    }

     /** Define user type. */
    public enum AccountType {

       /** User status make department as user type. */
         USER,

       /** Root admin status make department as Root Admin type. */
         ROOT_ADMIN,

       /** Domain admin status make department as Domain Admin type. */
         DOMAIN_ADMIN;

    }

    /**
     * Enumeration status for Department.
     */
    public enum Status {
        /** Enabled status is used to list departments through out the application. */
        ENABLED,

        /** Deleted status make department as soft deleted and it will not list on the applicaiton. */
        DELETED
    }

    /**
     * Convert JSONObject into user object.
     *
     * @param jsonObject json object from cloud stack.
     * @return account object.
     * @throws JSONException error occurs.
     */
    public static Account convert(JSONObject jsonObject) throws JSONException {
        Account account = new Account();
        account.setSyncFlag(false);
        try {
            account.setUuid(JsonUtil.getStringValue(jsonObject, "id"));
            JSONArray userList = jsonObject.getJSONArray("user");
            JSONObject userObject = userList.getJSONObject(0);
            account.setFirstName(JsonUtil.getStringValue(userObject, "firstname"));
            account.setLastName(JsonUtil.getStringValue(userObject, "lastname"));
            account.setUserName(JsonUtil.getStringValue(userObject, "username"));
            account.setType(AccountType.values()[(JsonUtil.getIntegerValue(userObject, "accounttype"))]);
            account.setTransDomainId(JsonUtil.getStringValue(jsonObject, "domainid"));
            account.setEmail(JsonUtil.getStringValue(userObject, "email"));
            account.setPassword("l3tm3in");
            account.setIsActive(true);
            account.setStatus(Status.valueOf(JsonUtil.getStringValue(userObject, "state").toUpperCase()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return account;
    }

    /**
     * Mapping entity object into list.
     *
     * @param accountList list of account.
     * @return accountmap
     */
    public static Map<String, Account> convert(List<Account> accountList) {
        Map<String, Account> accountMap = new HashMap<String, Account>();
        for (Account account : accountList) {
            accountMap.put(account.getUuid(), account);
        }
        return accountMap;
    }
}

