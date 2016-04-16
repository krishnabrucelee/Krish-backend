package ck.panda.domain.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Login security track details.
 *
 */
@Entity
@Table(name = "login_security_track")
@SuppressWarnings("serial")
@EntityListeners(AuditingEntityListener.class)
public class LoginSecurityTrack implements Serializable {

     /** Unique Id of the login security track. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Login Ip address. */
    @Column(name = "login_ip_address")
    private String loginIpAddress;

    /** Login attempt count. */
    @Column(name = "login_attempt_count")
    private Integer loginAttemptCount;

    /** Login time stamp. */
    @Column(name = "login_time_stamp")
    private Long loginTimeStamp;

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

    /** IsActive attribute to verify Active or Inactive. */
    @Column(name = "is_active")
    private Boolean isActive;

    /**
     * Get the id of the login security track.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id of the login security track.
     *
     * @param id  to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the login ip address.
     *
     * @return the loginIpAddress
     */
    public String getLoginIpAddress() {
        return loginIpAddress;
    }

    /**
     * Set the login ip address.
     *
     * @param loginIpAddress the loginIpAddress to set
     */
    public void setLoginIpAddress(String loginIpAddress) {
        this.loginIpAddress = loginIpAddress;
    }

    /**
     * Get the login attempt count.
     *
     * @return the loginAttemptCount
     */
    public Integer getLoginAttemptCount() {
        return loginAttemptCount;
    }

    /**
     * Set the login attempt count.
     *
     * @param loginAttemptCount the loginAttemptCount to set
     */
    public void setLoginAttemptCount(Integer loginAttemptCount) {
        this.loginAttemptCount = loginAttemptCount;
    }

    /**
     * Get the login time stamp.
     *
     * @return the loginTimeStamp
     */
    public Long getLoginTimeStamp() {
        return loginTimeStamp;
    }

    /**
     * Set the login time stamp.
     *
     * @param loginTimeStamp the loginTimeStamp to set
     */
    public void setLoginTimeStamp(Long loginTimeStamp) {
        this.loginTimeStamp = loginTimeStamp;
    }

    /**
     * Get the createdDatetime.
     *
     * @return the createdDateTime
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the createdDateTime.
     *
     * @param createdDateTime to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updatedDatetime.
     *
     * @return the updatedDateTime
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Get isActive.
     *
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set is Active.
     *
     * @param isActive the isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Set the updatedDateTime.
     *
     * @param updatedDateTime to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

}
