package ck.panda.domain.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@Table(name = "theme_setting")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class ThemeSetting implements Serializable {

    /** Unique Id of the theme settings. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** File name for background image */
    @Column(name = "background_img_file")
    private String backgroundImgFile;

    /** File name for logo image */
    @Column(name = "logo_img_file")
    private String logoImgFile;

    /** File path for background image */
    @Column(name = "background_img_path")
    private String backgroundImgPath;

    /** File path for logo image */
    @Column(name = "logo_img_path")
    private String logoImgPath;

    /** Domain of the Volume. */
    @OneToMany(cascade = CascadeType.ALL)
    private List<ThemeCustomisation> headers;

    /** Domain of the Volume. */
    @OneToMany(cascade = CascadeType.ALL)
    private List<ThemeCustomisation> footers;

    /** Splash Title Text for Admin login page. */
    @Column(name = "splash_title", columnDefinition = "TEXT")
    private String splashTitle;

    /** Splash Title Text for User login page. */
    @Column(name = "splash_title_user", columnDefinition = "TEXT")
    private String splashTitleUser;

    /** Welcome Text for Admin login page. */
    @Column(name = "welcome_content", columnDefinition = "TEXT")
    private String welcomeContent;

    /** Welcome Text for User login page. */
    @Column(name = "welcome_content_user", columnDefinition = "TEXT")
    private String welcomeContentUser;

    /** Footer Text for login page. */
    @Column(name = "footer_content", columnDefinition = "TEXT")
    private String footerContent;

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

    /**
     * Get the id of ThemeSettings.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id of ThemeSettings.
     *
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the backgroundImgFile of ThemeSetting.
     *
     * @return the backgroundImgFile
     */
    public String getBackgroundImgFile() {
        return backgroundImgFile;
    }

    /**
     * Set the backgroundImgFile of ThemeSetting.
     *
     * @param backgroundImgFile the backgroundImgFile to set
     */
    public void setBackgroundImgFile(String backgroundImgFile) {
        this.backgroundImgFile = backgroundImgFile;
    }

    /**
     * Get the logoImgFile of ThemeSetting.
     *
     * @return the logoImgFile
     */
    public String getLogoImgFile() {
        return logoImgFile;
    }

    /**
     * Set the logoImgFile of ThemeSetting.
     *
     * @param logoImgFile the logoImgFile to set
     */
    public void setLogoImgFile(String logoImgFile) {
        this.logoImgFile = logoImgFile;
    }

    /**
     * Get the headers of ThemeSetting.
     *
     * @return the headers
     */
    public List<ThemeCustomisation> getHeaders() {
        return headers;
    }

    /**
     * Set the headers of ThemeSetting.
     *
     * @param headers the headers to set
     */
    public void setHeaders(List<ThemeCustomisation> headers) {
        this.headers = headers;
    }

    /**
     * Get the footers of ThemeSetting.
     *
     * @return the footers
     */
    public List<ThemeCustomisation> getFooters() {
        return footers;
    }

    /**
     * Set the footers of ThemeSetting.
     *
     * @param footers the footers to set
     */
    public void setFooters(List<ThemeCustomisation> footers) {
        this.footers = footers;
    }

    /**
     * Get the backgroundImgPath of ThemeSettings.
     *
     * @return the backgroundImgPath
     */
    public String getBackgroundImgPath() {
        return backgroundImgPath;
    }

    /**
     * Set the backgroundImgPath of ThemeSettings.
     *
     * @param backgroundImgPath the backgroundImgPath to set
     */
    public void setBackgroundImgPath(String backgroundImgPath) {
        this.backgroundImgPath = backgroundImgPath;
    }

    /**
     * Get the logoImgPath of ThemeSettings.
     *
     * @return the logoImgPath
     */
    public String getLogoImgPath() {
        return logoImgPath;
    }

    /**
     * Set the logoImgPath of ThemeSettings.
     *
     * @param logoImgPath the logoImgPath to set
     */
    public void setLogoImgPath(String logoImgPath) {
        this.logoImgPath = logoImgPath;
    }

    /**
     * Get the welcomeContent of ThemeSetting.
     *
     * @return the welcomeContent
     */
    public String getWelcomeContent() {
        return welcomeContent;
    }

    /**
     * Set the welcomeContent of ThemeSetting.
     *
     * @param welcomeContent the welcomeContent to set
     */
    public void setWelcomeContent(String welcomeContent) {
        this.welcomeContent = welcomeContent;
    }


    /**
     * Get the welcomeContent of User Panel for ThemeSetting.
     *
     * @return the welcomeContentUser
     */
    public String getWelcomeContentUser() {
        return welcomeContentUser;
    }

    /**
     * Set the welcomeContent of User Panel for ThemeSetting.
     *
     * @param welcomeContentUser the welcomeContentUser to set
     */
    public void setWelcomeContentUser(String welcomeContentUser) {
        this.welcomeContentUser = welcomeContentUser;
    }

    /**
     * Get the splashTitle of Admin Panel for ThemeSetting.
     *
     * @return the splashTitle
     */
    public String getSplashTitle() {
        return splashTitle;
    }

    /**
     * Set the splashTitle of Admin Panel for ThemeSetting.
     *
     * @param splashTitle the splashTitle to set
     */
    public void setSplashTitle(String splashTitle) {
        this.splashTitle = splashTitle;
    }

    /**
     * Get the splashTitle of User Panel for ThemeSetting.
     *
     * @return the splashTitleUser
     */
    public String getSplashTitleUser() {
        return splashTitleUser;
    }

    /**
     * Get the splashTitle of User Panel for ThemeSetting.
     *
     * @param splashTitleUser the splashTitleUser to set
     */
    public void setSplashTitleUser(String splashTitleUser) {
        this.splashTitleUser = splashTitleUser;
    }

    /**
     * Get the footerContent of ThemeSetting.
     *
     * @return the footerContent
     */
    public String getFooterContent() {
        return footerContent;
    }

    /**
     * Set the footerContent of ThemeSetting.
     *
     * @param footerContent the footerContent to set
     */
    public void setFooterContent(String footerContent) {
        this.footerContent = footerContent;
    }

    /**
     * Get the isActive of ThemeSettings.
     *
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set the isActive of ThemeSettings.
     *
     * @param isActive the isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Get the createdBy of ThemeSettings.
     *
     * @return the createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the createdBy of ThemeSettings.
     *
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updatedBy of ThemeSettings.
     *
     * @return the updatedBy
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updatedBy of ThemeSettings.
     *
     * @param updatedBy the updatedBy to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the createdDateTime of ThemeSettings.
     *
     * @return the createdDateTime
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the createdDateTime of ThemeSettings.
     *
     * @param createdDateTime the createdDateTime to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updatedDateTime of ThemeSettings.
     *
     * @return the updatedDateTime
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updatedDateTime of ThemeSettings.
     *
     * @param updatedDateTime the updatedDateTime to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get the version of ThemeSettings.
     *
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Set the version of ThemeSettings.
     *
     * @param version the version to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }
}
