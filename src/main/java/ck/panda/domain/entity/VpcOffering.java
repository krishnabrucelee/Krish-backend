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
import javax.persistence.ManyToMany;
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
import ck.panda.util.JsonUtil;
import ck.panda.util.JsonValidator;

/**
 * Get the VPC offering list from cloud stack server and push into the application database.
 *
 */
@Entity
@Table(name = "vpc_offering")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class VpcOffering implements Serializable {

    /** Id of the VPC offering. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Unique id of the VPC offering. */
    @Column(name = "uuid")
    private String uuid;

    /** Name of the VPC offering. */
    @Column(name = "name", nullable = false)
    private String name;

    /** Display text of the VPC offering. */
    @Column(name = "display_text", nullable = false)
    private String displayText;

    /** Distributed router of the VPC offering. */
    @Column(name = "distributed_vpc_router")
    private Boolean distributedVpcRouter;

    /** VPC offering default value. */
    @Column(name = "is_default")
    private Boolean isDefault;

    /** Status of the VPC offering. */
    @Column(name = "status")
    private Status status;

    /** Support region level of the VPC offering. */
    @Column(name = "supports_region_level_vpc")
    private Boolean supportsRegionLevelVpc;

    /** Supported network list. */
    @ManyToMany
    private List<SupportedNetwork> supportedNetworkList;

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

    /** Transient value of VPC offering. */
    @Transient
    private String serviceName;

    /** Transient service id list. */
    @Transient
    private List<String> transServiceList;

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
     * Get the uuid.
     *
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the uuid.
     *
     * @param uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
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
     * Get the display text.
     *
     * @return the displayText
     */
    public String getDisplayText() {
        return displayText;
    }

    /**
     * Set the display text.
     *
     * @param displayText to set
     */
    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    /**
     * Get the distributed VPC router.
     *
     * @return the distributedVpcRouter
     */
    public Boolean getDistributedVpcRouter() {
        return distributedVpcRouter;
    }

    /**
     * Set the distributed VPC router.
     *
     * @param distributedVpcRouter to set
     */
    public void setDistributedVpcRouter(Boolean distributedVpcRouter) {
        this.distributedVpcRouter = distributedVpcRouter;
    }

    /**
     * Get the is default.
     *
     * @return the isDefault
     */
    public Boolean getIsDefault() {
        return isDefault;
    }

    /**
     * Set the is default.
     *
     * @param isDefault to set
     */
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
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
     * Get the supports region level VPC.
     *
     * @return the supportsRegionLevelVpc
     */
    public Boolean getSupportsRegionLevelVpc() {
        return supportsRegionLevelVpc;
    }

    /**
     * Set the supports region level VPC.
     *
     * @param supportsRegionLevelVpc to set
     */
    public void setSupportsRegionLevelVpc(Boolean supportsRegionLevelVpc) {
        this.supportsRegionLevelVpc = supportsRegionLevelVpc;
    }

    /**
     * Get the list of supported network.
     *
     * @return the supportedNetworkList
     */
    public List<SupportedNetwork> getSupportedNetworkList() {
        return supportedNetworkList;
    }

    /**
     * Set the list of supported network.
     *
     * @param supportedNetworkList to set
     */
    public void setSupportedNetworkList(List<SupportedNetwork> supportedNetworkList) {
        this.supportedNetworkList = supportedNetworkList;
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
     * Get the service name.
     *
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Set the service name.
     *
     * @param serviceName to set
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * Get the transient service list.
     *
     * @return transServiceList
     */
    public List<String> getTransServiceList() {
        return transServiceList;
    }

    /**
     * Set the transient service list.
     *
     * @param transServiceList to set
     */
    public void setTransServiceList(List<String> transServiceList) {
        this.transServiceList = transServiceList;
    }

    /** Enumeration status for VPC offering. */
    public enum Status {
        /** Disabled status make VPC offering as soft deleted and it will not list on the applicaiton. */
        DISABLED,
        /** Enabled status is used to list VPC offering through out the application. */
        ENABLED
    }

    /**
     * Convert JSONObject to VPC offering entity.
     *
     * @param object json object
     * @return VPC offering entity objects
     * @throws Exception unhandled errors.
     */
    public static VpcOffering convert(JSONObject object) throws Exception {
        VpcOffering vpcOffering = new VpcOffering();
        vpcOffering.setUuid(JsonValidator.jsonStringValidation(object, CloudStackConstants.CS_ID));
        vpcOffering.setName(JsonValidator.jsonStringValidation(object, CloudStackConstants.CS_NAME));
        vpcOffering.setDisplayText(JsonValidator.jsonStringValidation(object, CloudStackConstants.CS_DISPLAY_TEXT));
        vpcOffering.setDistributedVpcRouter(JsonValidator.jsonBooleanValidation(object, CloudStackConstants.CS_DISTRIBUTED_VPC_ROUTER));
        vpcOffering.setIsDefault(JsonValidator.jsonBooleanValidation(object, CloudStackConstants.CS_IS_DEFAULT));
        vpcOffering.setStatus(Status.valueOf(JsonUtil.getStringValue(object, CloudStackConstants.CS_STATE).toUpperCase()));
        vpcOffering.setSupportsRegionLevelVpc(JsonValidator.jsonBooleanValidation(object, CloudStackConstants.CS_SUPPORTS_REGION_LEVEL_VPC));
        return vpcOffering;
    }

    /**
     * Mapping VPC offering entity object in list.
     *
     * @param vpcOfferingList list of VPC offerings
     * @return VPC offering mapped values.
     */
    public static Map<String, VpcOffering> convert(List<VpcOffering> vpcOfferingList) {
        Map<String, VpcOffering> vpcOfferingMap = new HashMap<String, VpcOffering>();
        for (VpcOffering vpcOffering : vpcOfferingList) {
            vpcOfferingMap.put(vpcOffering.getUuid(), vpcOffering);
        }
        return vpcOfferingMap;
    }
}
