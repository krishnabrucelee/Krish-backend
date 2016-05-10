package ck.panda.rabbitmq.util;

import java.util.Date;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Event for email.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailEvent {

    /** Event uuid. */
    private String entityUuid;

    /** Event date and time. */
    private String eventDateTime;

    /** Domain Id. */
    private String domainId;

    /** Invoice Id. */
    private String invoiceId;

    /** Event user. */
    private String user;

    /** Event name. */
    private String event;

    /** Event type. */
    private String eventType;

    /** Event message body. */
    private String messageBody;

    /** Event message subject. */
    private String subject;

    /** Event resource uuid. */
    private String resourceUuid;

    /** Event resource id. */
    private String resourceId;

    /** Event resource type. */
    private String resourceType;

    /** Event resource type. */
    private HashMap<String, String> resources;

    /** start date. */
    private String startDate;

    /** End date. */
    private String endDate;

    /**
     * Get entity uuid.
     *
     * @return the entityUuid
     */
    public String getEntityUuid() {
        return entityUuid;
    }

    /**
     * Set entity uuid.
     *
     * @param entityUuid to set
     */
    public void setEntityUuid(String entityUuid) {
        this.entityUuid = entityUuid;
    }

    /**
     * Get the invoiceId of EmailEvent.
     *
     * @return the invoiceId
     */
    public String getInvoiceId() {
        return invoiceId;
    }

    /**
     * Set the invoiceId of EmailEvent.
     *
     * @param invoiceId the invoiceId to set
     */
    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    /**
     * Get the event date and time.
     *
     * @return the eventDateTime
     */
    public String getEventDateTime() {
        return eventDateTime;
    }

    /**
     * Set the event date and time.
     *
     * @param eventDateTime to set
     */
    public void setEventDateTime(String eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    /**
     * Get the user.
     *
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * Set the user.
     *
     * @param user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Get the event.
     *
     * @return the event
     */
    public String getEvent() {
        return event;
    }

    /**
     * Set the user.
     *
     * @param event to set
     */
    public void setEvent(String event) {
        this.event = event;
    }

    /**
     * Get the event type.
     *
     * @return the eventType
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * Set the event type.
     *
     * @param eventType to set
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    /**
     * Get the message body.
     *
     * @return the messageBody
     */
    public String getMessageBody() {
        return messageBody;
    }

    /**
     * Set the message body.
     *
     * @param messageBody to set
     */
    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * Get the message subject.
     *
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Set the message subject.
     *
     * @param subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Get the resource's uuid.
     *
     * @return the resourceUuid
     */
    public String getResourceUuid() {
        return resourceUuid;
    }

    /**
     * Set the resource's uuid.
     *
     * @param resourceUuid to set
     */
    public void setResourceUuid(String resourceUuid) {
        this.resourceUuid = resourceUuid;
    }

    /**
     * Get the resource's id.
     *
     * @return the resourceId
     */
    public String getResourceId() {
        return resourceId;
    }

    /**
     * Set the resource's id.
     *
     * @param resourceId to set
     */
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    /**
     * Get the resource's type.
     *
     * @return the resourceType
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * Set the resource's type.
     *
     * @param resourceType to set
     */
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * Get the resources of EmailEvent.
     *
     * @return the resources
     */
    public HashMap<String, String> getResources() {
        return resources;
    }

    /**
     * Set the resources of EmailEvent.
     *
     * @param resources the resources to set
     */
    public void setResources(HashMap<String, String> resources) {
        this.resources = resources;
    }

    /**
     * Get the domainId of EmailEvent.
     *
     * @return the domainId
     */
    public String getDomainId() {
        return domainId;
    }

    /**
     * Set the domainId of EmailEvent.
     *
     * @param domainId the domainId to set
     */
    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    /**
     * Get the startDate of EmailEvent.
     *
     * @return the startDate
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Set the startDate of EmailEvent.
     *
     * @param startDate the startDate to set
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * Get the endDate of EmailEvent.
     *
     * @return the endDate
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * Set the endDate of EmailEvent.
     *
     * @param endDate the endDate to set
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

}
