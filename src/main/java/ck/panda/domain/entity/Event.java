package ck.panda.domain.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * An event is essentially a significant or meaningful change in the state of both virtual and physical resources
 * associated with a cloud environment. Events are used by monitoring systems, usage and billing systems, or any other
 * event-driven workflow systems to discern a pattern and make the right business decision. In CloudStack an event could
 * be a state change of virtual or physical resources, an action performed by an user (action events), or policy based
 * events (alerts).
 *
 */
@Entity
@Table(name = "events")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class Event implements Serializable {

    /** Unique ID of event. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Job id of event. */
    @Column(name = "job_id")
    private String jobId;

    /** Resource id of event. */
    @Column(name = "resource_id")
    private String resourceId;

    /** event type. */
    @Column(name = "event")
    private String event;

    /** event message. */
    @Column(name = "message")
    private String message;

    /** event date time. */
    @Column(name = "event_date_time")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentZonedDateTime")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime eventDateTime;

    /** event owner. */
    @JoinColumn(name = "event_owner", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private User eventOwner;

    /** event user id. */
    @Column(name = "event_owner")
    private Long eventOwnerId;

    /** Type for event, whether it is action, async, usage, alerts etc . */
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private EventType eventType;

    /**
     * Enumeration type for Event.
     */
    public enum EventType {
        /** Event type as Action for action from user end. */
        ACTION,

        /** Event type as Usage for usage based event. */
        USAGE,

        /** Event type as Alert for alert based event. */
        ALERT,

        /** Event type as Async for job based. */
        ASYNC,

        /** Event type as Resourcestate for state based. */
        REOURCESTATE
    }

    /** Status for event, whether it is completed, failed etc . */
    @Column(name = "staus")
    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     * Enumeration status for Event.
     */
    public enum Status {
        /** Event status as scheduled. */
        SCHEDULED,

        /** Event status as in progess after scheduled. */
        IN_PROGRESS,

        /** Event status as started after get start process. */
        STARTED,

        /** Event status as completed after get complete event process. */
        COMPLETED,

        /** Event status as succeeded after get complete action. */
        SUCCEEDED,

        /** Event status as prestate transition event. */
        PRESTATETRANSITIONEVENT,

        /** Event status as poststate transition event. */
        POSTSTATETRANSITIONEVENT
    }

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
     * @param id the id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the jobId.
     *
     * @return the jobId.
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * Set the jobId.
     *
     * @param jobId the jobId to set.
     */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    /**
     * Get the resourceId.
     *
     * @return the resourceId.
     */
    public String getResourceId() {
        return resourceId;
    }

    /**
     * Set the resourceId.
     *
     * @param resourceId the resourceId to set.
     */
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    /**
     * Get the event.
     *
     * @return the event.
     */
    public String getEvent() {
        return event;
    }

    /**
     * Set the event.
     *
     * @param event the event to set.
     */
    public void setEvent(String event) {
        this.event = event;
    }

    /**
     * Get the message.
     *
     * @return the message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set the message.
     *
     * @param message the message to set.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Get the event Owner.
     *
     * @return the eventOwner.
     */
    public User getEventOwner() {
        return eventOwner;
    }

    /**
     * Set the event Owner.
     *
     * @param eventOwner the eventOwner to set.
     */
    public void setEventOwner(User eventOwner) {
        this.eventOwner = eventOwner;
    }

    /**
     * Get the event OwnerId.
     *
     * @return the event OwnerId.
     */
    public Long getEventOwnerId() {
        return eventOwnerId;
    }

    /**
     * Set the event OwnerId.
     *
     * @param eventOwnerId the eventOwnerId to set.
     */
    public void setEventOwnerId(Long eventOwnerId) {
        this.eventOwnerId = eventOwnerId;
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
     * @param status the status to set.
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Get the created date and time.
     *
     * @return the createdDateTime.
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the created date and time.
     *
     * @param createdDateTime the createdDateTime to set.
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updated date and time.
     *
     * @return the updatedDateTime.
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updated date and time.
     *
     * @param updatedDateTime the updatedDateTime to set.
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get the createdBy.
     *
     * @return the createdBy.
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the createdBy.
     *
     * @param createdBy the createdBy to set.
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updatedBy.
     *
     * @return the updatedBy.
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updatedBy.
     *
     * @param updatedBy the updatedBy to set.
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the event date and time.
     *
     * @return the eventDateTime.
     */
    public ZonedDateTime getEventDateTime() {
        return eventDateTime;
    }

    /**
     * Set the event date and time.
     *
     * @param eventDateTime the eventDateTime to set.
     */
    public void setEventDateTime(ZonedDateTime eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    /**
     * Get the event type.
     *
     * @return the eventType.
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * Set the event type.
     *
     * @param eventType the eventType to set.
     */
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
}
