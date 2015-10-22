package ck.panda.domain.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

/** User entity. */
@Entity
@Table(name = "ck_users")
public class User {

    /** Id of the Subject. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** User Name of the User. */
    @Column(name = "user_name", nullable = false,unique = true)
    private String userName;

    /** password of the User. */
    @Column(name = "password", nullable = false)
    private String password;

    /** user role. */
    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

    /** Email of the user. */
    @Column(name = "email", nullable = false,unique = true)
    private String email;

    /** User type of the user. */
    @Column(name = "user_type")
    private UserType userType;

    /** Name of the user.  */
    @Column(name = "name")
    private String name;

    /** User uuid. */
    @Column(name = "uuid")
    private String uuid;

    /** User status. */
    @Column(name = "status")
    private String status;

    /** Check user is active/in-active. */
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
     * @param id - the id to set.
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
     * Set the userName.
     *
     * @param userName - the userName to set.
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
     * @param password - the password to set.
     */
    public void setPassword(String password) {
        this.password = password;
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
     * @param role - the role to set.
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
     * @param email - the email to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the userType.
     *
     * @return the userType.
     */
    public UserType getUserType() {
        return userType;
    }

    /**
     * Set the userType.
     *
     * @param userType - the userType to set.
     */
    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    /**
     * Get the name.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name.
     *
     * @param name - the name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the uuid.
     *
     * @return the uuid.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the uuid.
     *
     * @param uuid - the uuid to set.
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get the status.
     *
     * @return the status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set the status.
     *
     * @param status - the status to set.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Get the isActive.
     *
     * @return the isActive.
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set the isActive.
     *
     * @param isActive - the isActive to set.
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
     * @param version - the version to set.
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get the createdDateTime.
     *
     * @return the createdDateTime.
     */
    public DateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the createdDateTime.
     *
     * @param createdDateTime - the createdDateTime to set.
     */
    public void setCreatedDateTime(DateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updatedDateTime.
     *
     * @return the updatedDateTime.
     */
    public DateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updatedDateTime.
     *
     * @param updatedDateTime - the updatedDateTime to set.
     */
    public void setUpdatedDateTime(DateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get the createdBy.
     *
     * @return the createdBy.
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the createdBy.
     *
     * @param createdBy - the createdBy to set.
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updatedBy.
     *
     * @return the updatedBy.
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updatedBy.
     *
     * @param updatedBy - the updatedBy to set.
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }
}
