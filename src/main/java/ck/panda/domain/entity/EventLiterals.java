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
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "event_literals")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class EventLiterals implements Serializable {

    /** Unique Id of the email template. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Name of the event .*/
    @Column(name = "event_name")
    private String eventName;

    /** Name of the event .*/
    @Column(name = "event_literals")
    private String eventLiterals;

    /** Name of the event .*/
    @Column(name = "event_literals_key")
    private String eventLiteralsKey;

    /** IsActive attribute to verify Active or Inactive. */
    @Column(name = "is_active")
    private Boolean isActive;

    /** Description for the event.*/
    @Column(name = "description")
    private String description;

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

    /** Last modified date and time. */
    @LastModifiedDate
    @Column(name = "updated_date_time")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentZonedDateTime")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime updatedDateTime;

    /** Version attribute to handle optimistic locking. */
    @Version
    @Column(name = "version")
    private Long version;

    /**
     * Get the id of the template.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id of the template.
     *
     * @param id  to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the event name.
     *
     * @return the eventName
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Set the eventName.
     *
     * @param eventName to set
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * Get the eventLiterals.
     *
     * @return the eventLiterals
     */
    public String getEventLiterals() {
        return eventLiterals;
    }

    /**
     * Set the eventLiterals.
     *
     * @param eventLiterals to set
     */
    public void setEventLiterals(String eventLiterals) {
        this.eventLiterals = eventLiterals;
    }

    /**
     * Get the version of the template.
     *
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Set the version of the template.
     *
     * @param version to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get createdBy.
     *
     * @return the createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the createdBy.
     *
     * @param createdBy to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updated user id.
     *
     * @return the updatedBy
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updated user id.
     *
     * @param updatedBy to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
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
     * Set the updatedDateTime.
     *
     * @param updatedDateTime to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * @return the eventLiteralsKey
     */
    public String getEventLiteralsKey() {
        return eventLiteralsKey;
    }

    /**
     * @param eventLiteralsKey the eventLiteralsKey to set
     */
    public void setEventLiteralsKey(String eventLiteralsKey) {
        this.eventLiteralsKey = eventLiteralsKey;
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
     * Get the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description .
     *
     * @param description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }



 }
