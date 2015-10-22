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

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
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
    @Column(name = "os_category_uuid")
    private String osCategoryUuid;

    /** Display name of the OS type. */
    @Column(name = "description")
    private String description;

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
     * @return the osCategoryUuid
     */
    public String getOsCategoryUuid() {
        return osCategoryUuid;
    }

    /**
     * @param osCategoryUuid the osCategoryUuid to set
     */
    public void setOsCategoryUuid(String osCategoryUuid) {
        this.osCategoryUuid = osCategoryUuid;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
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

    /**
     * Convert JSONObject to os type entity.
     *
     * @param object json object
     * @return os type entity objects
     * @throws JSONException unhandled json errors
     */
    public static OsType convert(JSONObject object) throws JSONException {
        OsType osType = new OsType();
        osType.uuid = object.get("id").toString();
        osType.description = object.get("description").toString();
        osType.osCategoryUuid = object.get("oscategoryid").toString();
        return osType;
    }

    /**
     * Mapping OS type entity object in list.
     *
     * @param osTypeList list of OStypes
     * @return OStype mapped values.
     */
    public static Map<String, OsType> convert(List<OsType> osTypeList) {
        Map<String, OsType> osTypeMap = new HashMap<String, OsType>();

        for (OsType osType : osTypeList) {
            osTypeMap.put(osType.getUuid(), osType);
        }
        return osTypeMap;
    }
}
