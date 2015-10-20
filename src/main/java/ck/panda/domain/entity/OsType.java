package ck.panda.domain.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * OS type purpose is to create which type of operation system you want when creating the template.
 * Get the OS type list from cloud stack server and push into the application database
 * When creating the template fetch the OS type from application database
 *
 */
@Entity
@Table(name = "ck_os_type")
@SuppressWarnings("serial")
public class OsType implements Serializable {

    /** Id of the OS type. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Unique id of the OS type. */
    @Column(name = "uuid")
    private String uuid;

    /** OS category type id. */
    @NotEmpty
    @JoinColumn(name = "oscategory_id", referencedColumnName = "id")
    @OneToOne
    private OsCategory osCategoryId;

    /** Display name of the OS type. */
    @Column(name = "display_name")
    private String displayName;

    /** Version attribute to handle optimistic locking. */
    @Version
    @Column(name = "version")
    private Long version;

    /** Created by user. */
    @CreatedBy
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    @OneToOne
    private User createdBy;

    /** Last updated by user. */
    @LastModifiedBy
    @JoinColumn(name = "updated_by", referencedColumnName = "id")
    @OneToOne
    private User updatedBy;

    /** Created date and time. */
    @CreatedDate
    @Column(name = "created_date_time")
    private DateTime createdDateTime;

    /** Last updated date and time. */
    @LastModifiedDate
    @Column(name = "updated_date_time")
    private DateTime updatedDateTime;

    /**
     * Get the id.
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id.
     * @param id - the Long to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the UUID.
     * @return uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the UUID.
     * @param uuid - the String to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get the OS category id.
     * @return osCategoryId
     */
    public OsCategory getOsCategoryId() {
        return osCategoryId;
    }

    /**
     * Set the OS category id.
     * @param osCategoryId - the OsCategory entity to set
     */
    public void setOsCategoryId(OsCategory osCategoryId) {
        this.osCategoryId = osCategoryId;
    }

    /**
     * Get the display name.
     * @return displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Set the display name.
     * @param displayName - the String to set
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get the version.
     * @return version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Set the version.
     * @param version - the Long to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get the created by.
     * @return createdBy
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the created by.
     * @param createdBy - the User entity to set
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updated by.
     * @return updatedBy
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updated by.
     * @param updatedBy - the User entity to set
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the created date time.
     * @return the createdDateTime
     */
    public DateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the created date time.
     * @param createdDateTime - the DateTime to set
     */
    public void setCreatedDateTime(DateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updated date time.
     * @return updatedDateTime
     */
    public DateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updated date time.
     * @param updatedDateTime - the DateTime to set
     */
    public void setUpdatedDateTime(DateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }
}
