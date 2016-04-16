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
 * General configuration details.
 *
 */
@Entity
@Table(name = "general_configuration")
@SuppressWarnings("serial")
@EntityListeners(AuditingEntityListener.class)
public class GeneralConfiguration implements Serializable {

     /** Unique Id of the general configuration. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Max login of the general configuration. */
    @Column(name = "max_login")
    private Integer maxLogin;

    /** Unlock time of the general configuration. */
    @Column(name = "unlock_time")
    private Integer unlockTime;

    /** Remember me expired days time of the general configuration. */
    @Column(name = "remember_me_expired_days")
    private Integer rememberMeExpiredDays;

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
     * Get the id of the general configuration.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id of the general configuration.
     *
     * @param id  to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the max login.
     *
     * @return the maxLogin
     */
    public Integer getMaxLogin() {
        return maxLogin;
    }

    /**
     * Set the max login.
     *
     * @param maxLogin the maxLogin to set
     */
    public void setMaxLogin(Integer maxLogin) {
        this.maxLogin = maxLogin;
    }

    /**
     * Get the unlock time.
     *
     * @return the unlockTime
     */
    public Integer getUnlockTime() {
        return unlockTime;
    }

    /**
     * Set the unlock time.
     *
     * @param unlockTime the unlockTime to set
     */
    public void setUnlockTime(Integer unlockTime) {
        this.unlockTime = unlockTime;
    }

    /**
     * Get the remember me expired days.
     *
     * @return the rememberMeExpiredDays
     */
    public Integer getRememberMeExpiredDays() {
        return rememberMeExpiredDays;
    }

    /**
     * Set the remember me expired days.
     *
     * @param rememberMeExpiredDays the rememberMeExpiredDays to set
     */
    public void setRememberMeExpiredDays(Integer rememberMeExpiredDays) {
        this.rememberMeExpiredDays = rememberMeExpiredDays;
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
