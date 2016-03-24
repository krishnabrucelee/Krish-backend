package ck.panda.domain.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name = "email_template")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class EmailTemplate implements Serializable {

    /** Unique Id of the email template. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Name of the event .*/
    @Column(name = "event_name")
    private String eventName;

    /** Subject of the template */
    @Column(name = "subject")
    private String subject;

    /** Language of the template */
    @Column(name = "eng_language")
    private String englishLanguage;

    /** Language of the template */
    @Column(name = "chinese_language")
    private String chineseLanguage;

    /** Created by user. */
    @CreatedBy
    @Column(name = "created_user_id")
    private Long createdBy;

    /** Last updated by user. */
    @LastModifiedBy
    @Column(name = "updated_user_id")
    private Long updatedBy;

    /** Recipient attribute to verify type of the user. */
    @Column(name = "recipient_type")
    private RecipientType recipientType;

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

    /** Types of the user. */
    public enum RecipientType {

        /** User type .*/
        USER,

        /** Root admin type */
        ROOT_ADMIN,

        /** Domain admin type.*/
        DOMAIN_ADMIN
    }

    @Transient
    private MultipartFile file;

    /**
     * @return the file
     */
    public MultipartFile getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(MultipartFile file) {
        this.file = file;
    }


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
     * Get the recipientType .
     *
     * @return the recipientType
     */
    public RecipientType getRecipientType() {
        return recipientType;
    }

    /**
     * Set he recipientType.
     *
     * @param recipientType to set
     */
    public void setRecipientType(RecipientType recipientType) {
        this.recipientType = recipientType;
    }

    /**
     * Get subject.
     *
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Set the subject.
     *
     * @param subject to set.
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Get english langauage.
     *
     * @return the englishLanguage
     */
    public String getEnglishLanguage() {
        return englishLanguage;
    }

    /**
     * Set the english langauage..
     *
     * @param englishLanguage to set
     */
    public void setEnglishLanguage(String englishLanguage) {
        this.englishLanguage = englishLanguage;
    }

    /**
     * Ge chinese language.
     *
     * @return the chineseLanguage
     */
    public String getChineseLanguage() {
        return chineseLanguage;
    }

    /**
     * Set the chineseLanguage.
     *
     * @param chineseLanguage  to set
     */
    public void setChineseLanguage(String chineseLanguage) {
        this.chineseLanguage = chineseLanguage;
    }

}
