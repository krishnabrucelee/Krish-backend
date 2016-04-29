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
@Table(name = "theme_customisation")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class ThemeCustomisation implements Serializable {

    /** Unique Id of the theme settings. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Id of the theme setting. */
    @Column(name = "theme_setting_id")
    private Long themeSettingId;

    /** Header name for theme settings */
    @Column(name = "name")
    private String name;

    /** Header url for theme settings */
    @Column(name = "url")
    private String url;

    /** Type of custom either header/footer. */
    @Column(name = "custom_type", nullable = false)
    private CustomType customType;

    /** IsActive attribute to verify Active or Inactive. */
    @Column(name = "is_active")
    private Boolean isActive;

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

    /** Enum type for theme. */
    public enum CustomType {
        /** Header type. */
        HEADER,
        /** Footer type. */
        FOOTER,
    }

    /**
     * Get the id of ThemeCustomisation.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id of ThemeCustomisation.
     *
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the themeSettingId of ThemeCustomisation.
     *
     * @return the themeSettingId
     */
    public Long getThemeSettingId() {
        return themeSettingId;
    }

    /**
     * Set the themeSettingId of ThemeCustomisation.
     *
     * @param themeSettingId the themeSettingId to set
     */
    public void setThemeSettingId(Long themeSettingId) {
        this.themeSettingId = themeSettingId;
    }

    /**
     * Get the name of ThemeCustomisation.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of ThemeCustomisation.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the url of ThemeCustomisation.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Set the url of ThemeCustomisation.
     *
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Get the customType of ThemeCustomisation.
     *
     * @return the customType
     */
    public CustomType getCustomType() {
        return customType;
    }

    /**
     * Set the customType of ThemeCustomisation.
     *
     * @param customType the customType to set
     */
    public void setCustomType(CustomType customType) {
        this.customType = customType;
    }

    /**
     * Get the isActive of ThemeCustomisation.
     *
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set the isActive of ThemeCustomisation.
     *
     * @param isActive the isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Get the createdBy of ThemeCustomisation.
     *
     * @return the createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the createdBy of ThemeCustomisation.
     *
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updatedBy of ThemeCustomisation.
     *
     * @return the updatedBy
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updatedBy of ThemeCustomisation.
     *
     * @param updatedBy the updatedBy to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the createdDateTime of ThemeCustomisation.
     *
     * @return the createdDateTime
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the createdDateTime of ThemeCustomisation.
     *
     * @param createdDateTime the createdDateTime to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updatedDateTime of ThemeCustomisation.
     *
     * @return the updatedDateTime
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updatedDateTime of ThemeCustomisation.
     *
     * @param updatedDateTime the updatedDateTime to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get the version of ThemeCustomisation.
     *
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Set the version of ThemeCustomisation.
     *
     * @param version the version to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }


}
