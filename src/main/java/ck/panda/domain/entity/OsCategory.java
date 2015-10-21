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
import javax.validation.constraints.Size;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * OS category purpose is to create which category of operation system you want when creating the template.
 * Get the OS category list from cloud stack server and push into the application database
 * When creating the template and instance fetch the OS category from application database
 *
 */
@Entity
@Table(name = "ck_os_category")
@SuppressWarnings("serial")
public class OsCategory implements Serializable {

    /** Id of the OS category. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Name of the OS category. */
    @Size(min = 4, max = 25)
    @Column(name = "name", nullable = false)
    private String name;

    /** Unique id of the OS category. */
    @Column(name = "uuid")
    private String uuid;

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
     * Get the name.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name.
     * @param name - the String to set
     */
    public void setName(String name) {
        this.name = name;
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
