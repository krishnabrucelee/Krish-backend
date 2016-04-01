package ck.panda.rabbitmq.util;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Event for email.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailEvent {

    /** Event uuid. */
    private String entityuuid;

    /** Event date and time. */
    private String eventDateTime;

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

    /**
     * Get entity uuid.
     *
     * @return the entityuuid
     */
    public String getEntityuuid() {
        return entityuuid;
    }

    /**
     * Set entity uuid.
     *
     * @param entityuuid to set
     */
    public void setEntityuuid(String entityuuid) {
        this.entityuuid = entityuuid;
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

}
