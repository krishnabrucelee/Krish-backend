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
import org.hibernate.annotations.Type;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import ck.panda.util.JsonUtil;

@Entity
@Table(name = "secondary_storage")
@SuppressWarnings("serial")
@EntityListeners(AuditingEntityListener.class)
public class SecondaryStorage implements Serializable{

     /** Unique id of the secondary storage. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Cloudstack's Secondary storage uuid. */
    @Column(name = "uuid")
    private String uuid;

    /** Name of the secondary storage. */
    @Column(name = "name")
    private String name;

    /** Protocol of the secondary storage. */
    @Column(name = "protocol")
    private String protocol;

    /** Provider name of the secondary storage. */
    @Column(name = "provider_name")
    private String providerName;

    /** Scope of the secondary storage. */
    @Column(name = "scope")
    private String scope;

    /** Url of the secondary storage. */
    @Column(name = "url")
    private String url;

    /** Instance zone. */
    @JoinColumn(name = "zone_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Zone zone;

    /** Instance zone id. */
    @Column(name = "zone_id")
    private Long zoneId;

    /** Set syncFlag. */
    @Transient
    private Boolean syncFlag;

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

    /** Transient zone of the secondary storage. */
    @Transient
    private String transZoneId;

    /** IsActive attribute to verify Active or Inactive. */
    @Column(name = "is_active")
    private Boolean isActive;

    /**
     * Get the id of the Secondary storage.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id of the Secondary storage.
     *
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the Uuid of the Secondary storage.
     *
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the Uuid of the Secondary storage.
     *
     * @param uuid  to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get the name of the Secondary storage.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the Secondary storage.
     *
     * @param name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the protocol of the Secondary storage.
     *
     * @return the protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Set the protocol of the Secondary storage.
     *
     * @param protocol to set
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * Get the provider name of the Secondary storage.
     *
     * @return the providerName
     */
    public String getProviderName() {
        return providerName;
    }

    /**
     * Set the provider name of the Secondary storage.
     *
     * @param providerName the providerName to set
     */
    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    /**
     * Get the scope of the Secondary storage.
     *
     * @return the scope
     */
    public String getScope() {
        return scope;
    }

    /**
     * set the scope of the Secondary storage.
     *
     * @param scope the scope to set
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Get the url of the secondary storage.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Set the url of the secondary storage.
     *
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Get the zone of the secondary storage.
     *
     * @return the zone
     */
    public Zone getZone() {
        return zone;
    }

    /**
     * Set the zone of the secondary storage.
     *
     * @param zone the zone to set
     */
    public void setZone(Zone zone) {
        this.zone = zone;
    }

    /**
     * Get the zone id of the secondary storage.
     *
     * @return the zoneId
     */
    public Long getZoneId() {
        return zoneId;
    }

    /**
     * Set the zone id of the secondary storage.
     *
     * @param zoneId the zoneId to set
     */
    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * Get the syncflag.
     *
     * @return the syncFlag
     */
    public Boolean getSyncFlag() {
        return syncFlag;
    }

    /**
     * Set the syncFlag.
     *
     * @param syncFlag  to set
     */
    public void setSyncFlag(Boolean syncFlag) {
        this.syncFlag = syncFlag;
    }

    /**
     * Get the created id of the user.
     *
     * @return the createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the created id of the user.
     *
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updated id of the user.
     *
     * @return the updatedBy
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updated id of the user.
     *
     * @param updatedBy the updatedBy to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the createdDateTime.
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
     * Get the updated Date Time.
     *
     * @return the updatedDateTime
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updated Date Time.
     *
     * @param updatedDateTime to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get the transient zone id.
     *
     * @return the transZoneId
     */
    public String getTransZoneId() {
        return transZoneId;
    }

    /**
     * Set the transient zone id.
     *
     * @param transZoneId to set
     */
    public void setTransZoneId(String transZoneId) {
        this.transZoneId = transZoneId;
    }

    /**
     * Get isActive.
     *
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set is Active.
     *
     * @param isActive the isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Convert JSONObject into pod object.
     *
     * @param jsonObject JSON object.
     * @return pod object.
     */
    public static SecondaryStorage convert(JSONObject jsonObject) {
        SecondaryStorage storage = new SecondaryStorage();
        try {
            storage.setName(JsonUtil.getStringValue(jsonObject, "name"));
            storage.setUuid(JsonUtil.getStringValue(jsonObject, "id"));
            storage.setTransZoneId((JsonUtil.getStringValue(jsonObject, "zoneid")));
            storage.setProtocol(JsonUtil.getStringValue(jsonObject, "protocol"));
            storage.setProviderName(JsonUtil.getStringValue(jsonObject, "providername"));
            storage.setScope(JsonUtil.getStringValue(jsonObject, "scope"));
            storage.setUrl(JsonUtil.getStringValue(jsonObject, "url"));
            storage.setIsActive(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return storage;
    }

    /**
     * Mapping entity object into list.
     *
     * @param podList list of storage.
     * @return storage map
     */
    public static Map<String, SecondaryStorage> convert(List<SecondaryStorage> storageList) {
        Map<String, SecondaryStorage> storageMap = new HashMap<String, SecondaryStorage>();

        for (SecondaryStorage storage : storageList) {
            storageMap.put(storage.getUuid(), storage);
        }

        return storageMap;
    }
}
