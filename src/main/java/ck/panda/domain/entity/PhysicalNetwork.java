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
import org.springframework.data.annotation.Version;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import ck.panda.constants.CloudStackConstants;
import ck.panda.util.JsonValidator;

/**
 * Get the physical networks service list from cloud stack server and push into the application database.
 *
 */
@Entity
@Table(name = "physical_network")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class PhysicalNetwork implements Serializable {

    /** Id of the physical network. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Unique id of the physical network. */
    @Column(name = "uuid")
    private String uuid;

    /** Name of the physical network. */
    @Column(name = "name", nullable = false)
    private String name;

    /** Status of the physical network. */
    @Column(name = "status")
    private Status status;

    /** Domain Object for the physical network. */
    @JoinColumn(name = "domain_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Domain domain;

    /** id for the Domain. */
    @Column(name = "domain_id")
    private Long domainId;

    /** Physical network zone. */
    @ManyToOne
    @JoinColumn(name = "zone_id", referencedColumnName = "id", updatable = false, insertable = false)
    private Zone zone;

    /** Physical network zone id. */
    @Column(name = "zone_id")
    private Long zoneId;

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

    /** Physical network zone transient. */
    @Transient
    private String transZone;

    /** Physical network domain transient. */
    @Transient
    private String transDomain;

    /**
     * Get the id.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id.
     *
     * @param id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name.
     *
     * @param name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the uuid.
     *
     * @return uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the uuid.
     *
     * @param uuid - the String to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get the status.
     *
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set the status.
     *
     * @param status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Get the domain.
     *
     * @return the domain
     */
    public Domain getDomain() {
        return domain;
    }

    /**
     * Set the domain.
     *
     * @param domain to set
     */
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    /**
     * Get the domain id.
     *
     * @return the domain id
     */
    public Long getDomainId() {
        return domainId;
    }

    /**
     * Set the domain id.
     *
     * @param domainId to set
     */
    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    /**
     * Get the zone.
     *
     * @return the zone
     */
    public Zone getZone() {
        return zone;
    }

    /**
     * Set the zone.
     *
     * @param zone to set
     */
    public void setZone(Zone zone) {
        this.zone = zone;
    }

    /**
     * Get the zone id.
     *
     * @return the zone id
     */
    public Long getZoneId() {
        return zoneId;
    }

    /**
     * Set the zone id.
     *
     * @param zoneId to set
     */
    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * Get the version.
     *
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Set the version.
     *
     * @param version to set
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
     * @param createdBy to set
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
     * @param updatedBy to set
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
     * @param createdDateTime to set
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
     * @param updatedDateTime to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get the transient zone name.
     *
     * @return transZone
     */
    public String getTransZone() {
        return transZone;
    }

    /**
     * Set the transient zone name.
     *
     * @param transZone to set
     */
    public void setTransZone(String transZone) {
        this.transZone = transZone;
    }

    /**
     * Get the transient domain name.
     *
     * @return transDomain
     */
    public String getTransDomain() {
        return transDomain;
    }

    /**
     * Set the transient domain name.
     *
     * @param transDomain to set
     */
    public void setTransDomain(String transDomain) {
        this.transDomain = transDomain;
    }

    /** Enumeration status for physical network. */
    public enum Status {
        /** Disabled status make physical network as soft deleted and it will not list on the applicaiton. */
        DISABLED,
        /** Enabled status is used to list physical network through out the application. */
        ENABLED
    }

    /**
     * Convert JSONObject to physical network entity.
     *
     * @param object json object
     * @return physical network entity objects
     * @throws Exception unhandled errors.
     */
    public static PhysicalNetwork convert(JSONObject object) throws Exception {
        PhysicalNetwork physicalNetwork = new PhysicalNetwork();
        physicalNetwork.setUuid(JsonValidator.jsonStringValidation(object, CloudStackConstants.CS_ID));
        physicalNetwork.setName(JsonValidator.jsonStringValidation(object, CloudStackConstants.CS_NAME));
        physicalNetwork.setTransDomain(JsonValidator.jsonStringValidation(object, CloudStackConstants.CS_DOMAIN_ID));
        physicalNetwork.setTransZone(JsonValidator.jsonStringValidation(object, CloudStackConstants.CS_ZONE_ID));
        physicalNetwork.setStatus(Status.ENABLED);
        return physicalNetwork;
    }

    /**
     * Mapping physical network entity object in list.
     *
     * @param physicalNetworkList list of physical networks
     * @return physical network mapped values.
     */
    public static Map<String, PhysicalNetwork> convert(List<PhysicalNetwork> physicalNetworkList) {
        Map<String, PhysicalNetwork> physicalNetworkMap = new HashMap<String, PhysicalNetwork>();
        for (PhysicalNetwork physicalNetwork : physicalNetworkList) {
            physicalNetworkMap.put(physicalNetwork.getUuid(), physicalNetwork);
        }
        return physicalNetworkMap;
    }
}
