package ck.panda.domain.entity;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
@Table(name = "lb_sticky_policy")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class LbStickinessPolicy {

     /** Unique Id of the Firewall Rule. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Cloudstack's LB Rule uuid. */
    @Column(name = "uuid")
    private String uuid;

    /** Stickiness policy name of LB. */
    @Column(name = "stickiness_name")
    private String stickinessName;

    /** Cookie name for LB. */
    @Column(name = "cookie_name")
    private String cookieName;

    /** Stickiness policy table size. */
    @Column(name = "sticky_table_size")
    private String stickyTableSize;

    /** Stickiness policy expires time for LB. */
    @Column(name = "sticky_expires")
    private String stickyExpires;

    /** Stickiness policy mode. */
    @Column(name = "sticky_mode")
    private String stickyMode;

    /** Stickiness policy Length. */
    @Column(name = "sticky_length")
    private String stickyLength;

    /** Stickiness policy hold time for LB. */
    @Column(name = "sticky_hold_time")
    private String stickyHoldTime;

    /** Request to learn for LB. */
    @Column(name = "sticky_request_learn")
    private Boolean stickyRequestLearn;

    /** Request to learn for LB. */
    @Column(name = "sticky_prefix")
    private Boolean stickyPrefix;

    /** Request to learn for LB. */
    @Column(name = "sticky_no_cache")
    private Boolean stickyNoCache;

    /** Request to learn for LB. */
    @Column(name = "sticky_indirect")
    private Boolean stickyIndirect;

    /** Request to learn for LB. */
    @Column(name = "sticky_post_only")
    private Boolean stickyPostOnly;

    /** Stickiness company for LB. */
    @Column(name = "sticky_company")
    private String stickyCompany;

    /** Stickiness policy's method name  . */
    @Column(name = "method_name")
    @Enumerated(EnumType.STRING)
    private StickinessMethod stickinessMethod;

    /** Check whether nic is in active state or in active state. */
    @Column(name = "is_active")
    private Boolean isActive;

    /** Temporary variable. */
    @Transient
    private Boolean syncFlag;

    /** Created by user. */
    @CreatedBy
    @Column(name = "created_by")
    private Long createdBy;

    /** Last updated by user. */
    @LastModifiedBy
    @Column(name = "updated_by")
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

    /** Types of methods for stickiness policy . */
    public enum StickinessMethod {

        /** None of the stickiness policy is added . */
        None,

        /** Source based stickiness policy. */
        SourceBased,

        /** App based stickiness policy .*/
        AppCookie,

        /** Load balancer cookie policy. */
        LbCookie
    }

    /**
     * Get lb Rule id.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set lb Rule id.
     *
     * @param id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get lb Rule Uuid.
     *
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the lb Rule uuid.
     *
     * @param uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get the stickinessName.
     *
     * @return the stickinessName
     */
    public String getStickinessName() {
        return stickinessName;
    }

    /**
     * Set the stickinessName.
     *
     * @param stickinessName  to set
     */
    public void setStickinessName(String stickinessName) {
        this.stickinessName = stickinessName;
    }

    /**
     * Get the cookieName.
     *
     * @return the cookieName
     */
    public String getCookieName() {
        return cookieName;
    }

    /**
     * Set the cookieName.
     *
     * @param cookieName  to set
     */
    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    /**
     * Get the stickyTableSize.
     *
     * @return the stickyTableSize
     */
    public String getStickyTableSize() {
        return stickyTableSize;
    }

    /**
     * Set the stickyTableSize.
     *
     * @param stickyTableSize  to set
     */
    public void setStickyTableSize(String stickyTableSize) {
        this.stickyTableSize = stickyTableSize;
    }

    /**
     * Get the stickyExpires.
     *
     * @return the stickyExpires
     */
    public String getStickyExpires() {
        return stickyExpires;
    }

    /**
     * Set the stickyExpires.
     *
     * @param stickyExpires  to set
     */
    public void setStickyExpires(String stickyExpires) {
        this.stickyExpires = stickyExpires;
    }

    /**
     * Get the stickyMode.
     *
     * @return the stickyMode
     */
    public String getStickyMode() {
        return stickyMode;
    }

    /**
     * Set the stickyMode.
     *
     * @param stickyMode  to set
     */
    public void setStickyMode(String stickyMode) {
        this.stickyMode = stickyMode;
    }

    /**
     * Get the stickyLength.
     *
     * @return the stickyLength
     */
    public String getStickyLength() {
        return stickyLength;
    }

    /**
     * Set the stickyLength.
     *
     * @param stickyLength  to set
     */
    public void setStickyLength(String stickyLength) {
        this.stickyLength = stickyLength;
    }

    /**
     * Set the stickyHoldTime.
     *
     * @return the stickyHoldTime
     */
    public String getStickyHoldTime() {
        return stickyHoldTime;
    }

    /**
     * Set the stickyHoldTime.
     *
     * @param stickyHoldTime  to set
     */
    public void setStickyHoldTime(String stickyHoldTime) {
        this.stickyHoldTime = stickyHoldTime;
    }

    /**
     * Get the stickyRequestLearn.
     *
     * @return the stickyRequestLearn
     */
    public Boolean getStickyRequestLearn() {
        return stickyRequestLearn;
    }

    /**
     * Set the stickyRequestLearn.
     *
     * @param stickyRequestLearn  to set
     */
    public void setStickyRequestLearn(Boolean stickyRequestLearn) {
        this.stickyRequestLearn = stickyRequestLearn;
    }

    /**
     * Get the stickyPrefix.
     *
     * @return the stickyPrefix
     */
    public Boolean getStickyPrefix() {
        return stickyPrefix;
    }

    /**
     * Set the stickyPrefix.
     *
     * @param stickyPrefix  to set
     */
    public void setStickyPrefix(Boolean stickyPrefix) {
        this.stickyPrefix = stickyPrefix;
    }

    /**
     * Get the stickyNoCache .
     *
     * @return the stickyNoCache
     */
    public Boolean getStickyNoCache() {
        return stickyNoCache;
    }

    /**
     * Set the stickyNoCache .
     *
     * @param stickyNoCache to set
     */
    public void setStickyNoCache(Boolean stickyNoCache) {
        this.stickyNoCache = stickyNoCache;
    }

    /**
     * Set the stickyIndirect.
     *
     * @return the stickyIndirect
     */
    public Boolean getStickyIndirect() {
        return stickyIndirect;
    }

    /**
     * Set the stickyIndirect.
     *
     * @param stickyIndirect  to set
     */
    public void setStickyIndirect(Boolean stickyIndirect) {
        this.stickyIndirect = stickyIndirect;
    }

    /**
     * Get the stickyPostOnly.
     *
     * @return the stickyPostOnly
     */
    public Boolean getStickyPostOnly() {
        return stickyPostOnly;
    }

    /**
     * Set the stickyPostOnly.
     *
     * @param stickyPostOnly  to set
     */
    public void setStickyPostOnly(Boolean stickyPostOnly) {
        this.stickyPostOnly = stickyPostOnly;
    }

    /**
     * Get the stickyCompany .
     *
     * @return the stickyCompany
     */
    public String getStickyCompany() {
        return stickyCompany;
    }

    /**
     * Set the stickyCompany .
     *
     * @param stickyCompany to set
     */
    public void setStickyCompany(String stickyCompany) {
        this.stickyCompany = stickyCompany;
    }

    /**
     * Get the created date and time.
     *
     * @return createdDateTime
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the created date and time.
     *
     * @param createdDateTime the DateTime to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updated date and time.
     *
     * @return updatedDateTime
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updated date and time.
     *
     * @param updatedDateTime the DateTime to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get created user.
     *
     * @return the createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Get updated user.
     *
     * @return the updatedBy
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the createdBy .
     *
     * @param createdBy to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Set the updatedBy .
     *
     * @param updatedBy to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get syncFlag.
     *
     * @return the syncFlag
     */
    public Boolean getSyncFlag() {
        return syncFlag;
    }

    /**
     * Set the syncFlag.
     *
     * @param syncFlag to set
     */
    public void setSyncFlag(Boolean syncFlag) {
        this.syncFlag = syncFlag;
    }

    /**
     * Get the is active of the Volume.
     *
     * @return the isActive of the Volume
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set the is active of the Volume.
     *
     * @param isActive the is active to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Get the stickiness method of the load balancer policy.
     *
     * @return the stickinessMethod
     */
    public StickinessMethod getStickinessMethod() {
        return stickinessMethod;
    }

    /**
     * Set the stickiness method of the load balancer policy.
     *
     * @param stickinessMethod to set
     */
    public void setStickinessMethod(StickinessMethod stickinessMethod) {
        this.stickinessMethod = stickinessMethod;
    }

    /**
     * Convert JSONObject to lbstickinesspolicy entity.
     *
     * @param jsonObject json object
     * @return lbstickinesspolicy entity object.
     * @throws Exception unhandled errors.
     */
    public static LbStickinessPolicy convert(JSONObject jsonObject) throws Exception {
        LbStickinessPolicy loadBalancer = new LbStickinessPolicy();
        loadBalancer.setSyncFlag(false);
        loadBalancer.setUuid(JsonUtil.getStringValue(jsonObject, "id"));
        loadBalancer.setIsActive(true);
        loadBalancer.setStickinessMethod(StickinessMethod.valueOf(JsonUtil.getStringValue(jsonObject, "methodname")));
        loadBalancer.setStickinessName(JsonUtil.getStringValue(jsonObject, "name"));
        loadBalancer.setCookieName(JsonUtil.getStringValue(jsonObject, "cookiename"));
        loadBalancer.setStickyExpires(JsonUtil.getStringValue(jsonObject,"expires"));
        loadBalancer.setStickyNoCache(JsonUtil.getBooleanValue(jsonObject,"nocache"));
        loadBalancer.setStickyTableSize(JsonUtil.getStringValue(jsonObject,"tablesize"));
        return loadBalancer;
    }

    /**
     * Mapping entity object into list.
     *
     * @param loadBalancerList list of Load Balancer.
     * @return egressMap egress.
     */
    public static Map<String, LbStickinessPolicy> convert(List<LbStickinessPolicy> loadBalancerList) {
        Map<String, LbStickinessPolicy> policyMap = new HashMap<String, LbStickinessPolicy>();

        for (LbStickinessPolicy policy : loadBalancerList) {
            policyMap.put(policy.getUuid(), policy);
        }
        return policyMap;
    }
    }
