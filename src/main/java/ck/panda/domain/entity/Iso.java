package ck.panda.domain.entity;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;
import ck.panda.util.JsonUtil;

/**
 * ISO images — disc images containing data or bootable media for operating systems.
 *
 */
@Entity
@Table(name = "ck_iso")
public class Iso {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Iso.class);

    /** Unique ID of the volume. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Unique ID from Cloud Stack. */
    @Column(name = "uuid")
    private String uuid;

    /** A desired name of the iso. */
    @NotEmpty
    @Column(name = "name", nullable = false)
    private String name;

    /** Instance domain id. */
    @JoinColumn(name = "domain_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Domain domain;

    /** Instance domain id. */
    @Column(name = "domain_id")
    private Long domainId;

    /** iso ostype object. */
    @JoinColumn(name = "ostype_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private OsType osType;

    /** Iso ostype id. */
    @Column(name = "ostype_id")
    private Long osTypeId;

    /** Created by user. */
    @CreatedBy
    @JoinColumn(name = "created_user_id", referencedColumnName = "id")
    @OneToOne
    private User createdBy;

    /** Last updated by user. */
    @LastModifiedBy
    @JoinColumn(name = "updated_user_id", referencedColumnName = "id")
    @OneToOne
    private User updatedBy;

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

    /** Transient domain of the iso. */
    @Transient
    private String transDomainId;

    /** Transient os type of the iso. */
    @Transient
    private String transOsTypeId;

    /** IsActive attribute to verify Active or Inactive. */
    @Column(name = "is_active")
    private Boolean isActive;

    /** IsRemoved attribute to verify removed or not. */
    @Column(name = "is_removed")
    private Boolean isRemoved;
    
    /** Is Iso ready to boot. */
    @Column(name = "is_ready")
    private Boolean isReady;
    
    /** IsBootable Iso or not. */
    @Column(name = "is_bootable")
    private Boolean isBootable;
    
    /** Is Public Iso or not. */
    @Column(name = "is_public")
    private Boolean isPublic;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set id.
     *
     * @param id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get UUID.
     *
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the UUID.
     *
     * @param uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set name.
     *
     * @param name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get domain object.
     *
     * @return the domain
     */
    public Domain getDomain() {
        return domain;
    }

    /**
     * Set domain object.
     *
     * @param domain to set
     */
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    /**
     * Get DomainId.
     *
     * @return the domainId
     */
    public Long getDomainId() {
        return domainId;
    }

    /**
     * Set the domainId.
     *
     * @param domainId to set
     */
    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    /**
     * Get type of the OS.
     *
     * @return the osType
     */
    public OsType getOsType() {
        return osType;
    }

    /**
     * Set type of the OS..
     *
     * @param osType to set
     */
    public void setOsType(OsType osType) {
        this.osType = osType;
    }

    /**
     * Get ostype ID.
     *
     * @return the osTypeId
     */
    public Long getOsTypeId() {
        return osTypeId;
    }

    /**
     * Set the ostype Id.
     *
     * @param osTypeId to set
     */
    public void setOsTypeId(Long osTypeId) {
        this.osTypeId = osTypeId;
    }

    /**
     * Get the create user id.
     *
     * @return the createdBy
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the create user id.
     *
     * @param createdBy to set
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updated user id.
     *
     * @return the updatedBy
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updated user id.
     *
     * @param updatedBy to set
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get createdDate time.
     *
     * @return the createdDateTime
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set createdDate time.
     *
     * @param createdDateTime to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get updated Date time.
     *
     * @return the updatedDateTime
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updatedDate time.
     *
     * @param updatedDateTime to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get Transient Domain Id.
     *
    * @return the transDomainId
    */
    public String getTransDomainId() {
        return transDomainId;
    }

    /**
    * Set the transient domain Id.
    *
    * @param transDomainId to set
    */
    public void setTransDomainId(String transDomainId) {
        this.transDomainId = transDomainId;
    }


    /**
    * Get the TransOs Type Id.
    *
    * @return the transOsTypeId
    */
    public String getTransOsTypeId() {
        return transOsTypeId;
    }

    /**
    * Set the transOsTypeId.
    *
    * @param transOsTypeId  to set
    */
    public void setTransOsTypeId(String transOsTypeId) {
        this.transOsTypeId = transOsTypeId;
    }

    /**
    * @return the isActive
    */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
    * Set the isActive .
    *
    * @param isActive to set
    */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
    * Get is Removed.
    *
    * @return the isRemoved
    */
    public Boolean getIsRemoved() {
        return isRemoved;
    }

    /**
    * Set the isRemoved.
    *
    * @param isRemoved  to set
    */
    public void setIsRemoved(Boolean isRemoved) {
        this.isRemoved = isRemoved;
    }

    /**
     * Get Is Ready to boot.
     * 
     * @return response.
     */
    public Boolean getIsReady() {
		return isReady;
	}

    /**
     * Set is Ready to boot.
     * 
     * @param isReady to set.
     */
	public void setIsReady(Boolean isReady) {
		this.isReady = isReady;
	}
	
	 /**
     * Get Is Bootable iso.
     * 
     * @return iso.
     */
	public Boolean getIsBootable() {
		return isBootable;
	}

	 /**
     * Set Is Bootable iso.
     * 
     * @return bootable iso status.
     */
	public void setIsBootable(Boolean isBootable) {
		this.isBootable = isBootable;
	}

	/**
	 * Get is Public.
	 * 
	 * @return isPublic.
	 */
	public Boolean getIsPublic() {
		return isPublic;
	}

	/**
	 * Set is Public
	 * 
	 * @param isPublic iso to set.
	 */
	public void setIsPublic(Boolean isPublic) {
		this.isPublic = isPublic;
	}

	/**
     * Convert JSONObject to domain entity.
     *
     * @param jsonObject json object
     * @return domain entity object.
     * @throws JSONException handles json exception.
     */
    public static Iso convert(JSONObject jsonObject) throws JSONException {
        Iso iso = new Iso();

        try {
            iso.setName(JsonUtil.getStringValue(jsonObject, "name"));
            iso.setUuid(JsonUtil.getStringValue(jsonObject, "id"));
            iso.setTransDomainId(JsonUtil.getStringValue(jsonObject, "domainid"));
            iso.setTransOsTypeId(JsonUtil.getStringValue(jsonObject, "ostypeid"));
            iso.setIsBootable(JsonUtil.getBooleanValue(jsonObject, "bootable"));
            iso.setIsPublic(JsonUtil.getBooleanValue(jsonObject,"ispublic"));
            iso.setIsReady(JsonUtil.getBooleanValue(jsonObject,"isready"));
            iso.setIsActive(true);
        } catch (Exception ex) {
            LOGGER.error("Iso-convert", ex);
        }
        return iso;
    }

    /**
     * Mapping entity object into list.
     *
     * @param isoList
     *            list of iso images.
     * @return iso map
     */
    public static Map<String, Iso> convert(List<Iso> isoList) {
        Map<String, Iso> isoMap = new HashMap<String, Iso>();

        for (Iso iso : isoList) {
            isoMap.put(iso.getUuid(), iso);
        }
        return isoMap;
    }
}
