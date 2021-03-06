package ck.panda.domain.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import org.hibernate.annotations.Type;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import ck.panda.util.JsonUtil;

/**
 * OS type purpose is to create which type of operation system you want when creating the template. Get the OS type list
 * from cloud stack server and push into the application database When creating the template fetch the OS type from
 * application database
 *
 */
@Entity
@Table(name = "os_types")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class OsType implements Serializable {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(OsType.class);

    /** Id of the OS type. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Unique id of the OS type. */
    @Column(name = "uuid")
    private String uuid;

    /** OsCategory of the OS type. */
    @JoinColumn(name = "oscategory_id", referencedColumnName = "id", updatable = false, insertable = false)
    @ManyToOne
    private OsCategory osCategory;

    /** OSCategory id of the OS type. */
    @Column(name = "oscategory_id")
    private Long osCategoryId;

    /** Display name of the OS type. */
    @Column(name = "description")
    private String description;

    /** Version attribute to handle optimistic locking. */
    @Version
    @Column(name = "version")
    private Long version;

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

    /** modified date and time. */
    @LastModifiedDate
    @Column(name = "updated_date_time")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentZonedDateTime")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime updatedDateTime;

    /** Transient oscategory of the ostype. */
    @Transient
    private String transOsCategoryId;

    /**
     * Get the id.
     *
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id.
     *
     * @param id - the Long to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the UUID.
     *
     * @return uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the UUID.
     *
     * @param uuid - the String to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get the osCategory.
     *
     * @return the osCategory
     */
    public OsCategory getOsCategory() {
        return osCategory;
    }

    /**
     * Set the osCategory.
     *
     * @param osCategory the osCategory to set
     */
    public void setOsCategory(OsCategory osCategory) {
        this.osCategory = osCategory;
    }

    /**
     * Get the osCategoryId.
     *
     * @return the osCategoryId
     */
    public Long getOsCategoryId() {
        return osCategoryId;
    }

    /**
     * Set the osCategoryId.
     *
     * @param osCategoryId the osCategoryId to set
     */
    public void setOsCategoryId(Long osCategoryId) {
        this.osCategoryId = osCategoryId;
    }

    /**
     * Get the description of the OS type.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of the OS type.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the version.
     *
     * @return version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Set the version.
     *
     * @param version - the Long to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get the created by.
     *
     * @return createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the created by.
     *
     * @param createdBy - the User entity to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updated by.
     *
     * @return updatedBy
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updated by.
     *
     * @param updatedBy - the User entity to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the created date time.
     *
     * @return the createdDateTime
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the created date time.
     *
     * @param createdDateTime - the DateTime to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updated date time.
     *
     * @return updatedDateTime
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updated date time.
     *
     * @param updatedDateTime - the DateTime to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get transient OsCategory id.
     *
     * @return the transOsCategoryId
     */
    public String getTransOsCategoryId() {
        return transOsCategoryId;
    }

    /**
     * Set transient OsCategory id.
     *
     * @param transOsCategoryId to set
     */
    public void setTransOsCategoryId(String transOsCategoryId) {
        this.transOsCategoryId = transOsCategoryId;
    }

    /**
     * Convert JSONObject to os type entity.
     *
     * @param jsonObject to set
     * @return OS type
     * @throws JSONException raise if error
     */
    public static OsType convert(JSONObject jsonObject) throws JSONException {
        OsType osType = new OsType();
        try {
            osType.setUuid(JsonUtil.getStringValue(jsonObject, "id"));
            osType.setDescription(JsonUtil.getStringValue(jsonObject, "description"));
            osType.setTransOsCategoryId(JsonUtil.getStringValue(jsonObject, "oscategoryid"));
        } catch (Exception ex) {
            LOGGER.error("OSType-convert", ex);
        }
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
