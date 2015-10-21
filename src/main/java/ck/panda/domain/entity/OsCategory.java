package ck.panda.domain.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.json.JSONException;
import org.json.JSONObject;
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

    /** Unique id for the OS category. */
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
     * @return id of the OS category
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     * the OS category id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return name of the OS category
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     * the OS category name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return UUID of the OS category
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid
     * the OS category UUID to set.
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return version of the OS category
     */
    public Long getVersion() {
        return version;
    }

    /**
     * @param version
     * the OS category version to set.
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * @return the createdBy
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy
     * the OS category createdBy to set.
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @return the updatedBy
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * @param updatedBy
     * the OS category updatedBy to set.
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * @return the createdDateTime
     */
    public DateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * @param createdDateTime
     * the createdDateTime to set.
     */
    public void setCreatedDateTime(DateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * @return the updatedDateTime
     */
    public DateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * @param updatedDateTime
     * the updatedDateTime to set.
     */
    public void setUpdatedDateTime(DateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Convert JSONObject to oscategory entity.
     *
     * @param object json object.
     * @return oscategory entity object
     * @throws JSONException unhandled json errors.
     */
    public static OsCategory convert(JSONObject object) throws JSONException {
        OsCategory osCategory = new OsCategory();
        osCategory.uuid = object.get("id").toString();
        osCategory.name = object.get("name").toString();

        return osCategory;
    }

    /**
     * Mapping the os categories entity in list.
     *
     * @param  osCategoryList list of operating systems
     * @return mapped values
     */
    public static Map<String, OsCategory> convert(List<OsCategory> osCategoryList) {
        Map<String, OsCategory> osCategoryMap = new HashMap<String, OsCategory>();

        for (OsCategory osCategory : osCategoryList) {
             osCategoryMap.put(osCategory.getUuid(), osCategory);
        }
        return  osCategoryMap;
    }
}
