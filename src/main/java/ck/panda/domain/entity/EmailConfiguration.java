package ck.panda.domain.entity;

import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Email Server Configuration Entity.
 *
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "email_Configuration")
public class EmailConfiguration {

    /** Unique ID of the email. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Application Url of panda panel. */
    @Column(name = "application_url")
    private String applicationUrl;

    /** Admin user name of panda panel. */
    @Column(name = "user_name")
    private String userName;

    /** Email language. */
    @Column(name = "email_language")
    private String emailLanguage;

    /** Port no of the email server. */
    @Column(name = "port")
    private Integer port;

    /** The from address of the email server. */
    @Column(name = "email_from")
    private String emailFrom;

    /** Host of the email server. */
    @Column(name = "host")
    private String host;

    /** Admin Password of the panda panel. */
    @Column(name = "password")
    private String password;

    /** SSL is enabled or not. */
    @Column(name = "is_ssl", columnDefinition = "tinyint")
    private Boolean ssl;

    /** Sender name of panda panel. */
    @Column(name = "sender_name")
    private String senderName;

    /** Transient email to of the panda panel. */
    @Transient
    private String emailTo;

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

    /** An active attribute is to check whether the role is active or not. */
    @Column(name = "is_active", columnDefinition = "tinyint default 1")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean isActive;

    /**
     * Get the id of EmailConfiguration.java
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id of EmailConfiguration.
     *
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the applicationUrl of EmailConfiguration.
     *
     * @return the applicationUrl
     */
    public String getApplicationUrl() {
        return applicationUrl;
    }

    /**
     * Set the applicationUrl of EmailConfiguration.
     *
     * @param applicationUrl the applicationUrl to set
     */
    public void setApplicationUrl(String applicationUrl) {
        this.applicationUrl = applicationUrl;
    }

    /**
     * Get the userName of EmailConfiguration.
     *
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Set the userName of EmailConfiguration.
     *
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Get the port of EmailConfiguration.
     *
     * @return the port
     */
    public Integer getPort() {
        return port;
    }

    /**
     * Set the port of EmailConfiguration.
     *
     * @param port the port to set
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * Get the emailFrom of EmailConfiguration.
     *
     * @return the emailFrom
     */
    public String getEmailFrom() {
        return emailFrom;
    }

    /**
     * Set the emailFrom of EmailConfiguration.
     *
     * @param emailFrom the emailFrom to set
     */
    public void setEmailFrom(String emailFrom) {
        this.emailFrom = emailFrom;
    }

    /**
     * Get the host of EmailConfiguration.
     *
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * Set the host of EmailConfiguration.
     *
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Get the password of EmailConfiguration.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the password of EmailConfiguration.
     *
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get the ssl of EmailConfiguration.
     *
     * @return the ssl
     */
    public Boolean getSsl() {
        return ssl;
    }

    /**
     * Set the ssl of EmailConfiguration.
     *
     * @param ssl the ssl to set
     */
    public void setSsl(Boolean ssl) {
        this.ssl = ssl;
    }

    /**
     * Get the senderName of EmailConfiguration.
     *
     * @return the senderName
     */
    public String getSenderName() {
        return senderName;
    }

    /**
     * Set the senderName of EmailConfiguration.
     *
     * @param senderName the senderName to set
     */
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    /**
     * Get the emailTo of EmailConfiguration.
     *
     * @return the emailTo
     */
    public String getEmailTo() {
        return emailTo;
    }

    /**
     * Set the emailTo of EmailConfiguration.
     *
     * @param emailTo the emailTo to set
     */
    public void setEmailTo(String emailTo) {
        this.emailTo = emailTo;
    }

    /**
     * Get the version of EmailConfiguration.
     *
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Set the version of EmailConfiguration.
     *
     * @param version the version to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get the createdBy of EmailConfiguration.
     *
     * @return the createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the createdBy of EmailConfiguration.
     *
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updatedBy of EmailConfiguration.
     *
     * @return the updatedBy
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updatedBy of EmailConfiguration.
     *
     * @param updatedBy the updatedBy to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the createdDateTime of EmailConfiguration.
     *
     * @return the createdDateTime
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the createdDateTime of EmailConfiguration.
     *
     * @param createdDateTime the createdDateTime to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updatedDateTime of EmailConfiguration.
     *
     * @return the updatedDateTime
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updatedDateTime of EmailConfiguration.
     *
     * @param updatedDateTime the updatedDateTime to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get the isActive of EmailConfiguration.
     *
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set the isActive of EmailConfiguration.
     *
     * @param isActive the isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Get the email language.
     *
     * @return the email language
     */
    public String getEmailLanguage() {
        return emailLanguage;
    }

    /**
     * Set the email language.
     *
     * @param emailLanguage the email language to set
     */
    public void setEmailLanguage(String emailLanguage) {
        this.emailLanguage = emailLanguage;
    }

}
