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
import javax.persistence.Table;
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
 * Get the VPC ACL list from cloud stack server and push into the application database.
 *
 */
@Entity
@Table(name = "vpc_acl")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class VpcAcl implements Serializable {

    /** Id of the VPC ACL. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Unique id of the VPC ACL. */
    @Column(name = "uuid")
    private String uuid;

    /** Name of the VPC ACL. */
    @Column(name = "name", nullable = false)
    private String name;

    /** Description of the VPC ACL. */
    @Column(name = "description", nullable = false)
    private String description;

    /** For display status. */
    @Column(name = "for_display")
    private Boolean forDisplay;

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
     * Get the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description.
     *
     * @param description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the for display.
     *
     * @return the forDisplay
     */
    public Boolean getForDisplay() {
        return forDisplay;
    }

    /**
     * Set the for display.
     *
     * @param forDisplay to set
     */
    public void setForDisplay(Boolean forDisplay) {
        this.forDisplay = forDisplay;
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
     * Convert JSONObject to VPC ACL entity.
     *
     * @param object json object
     * @return VPC ACL entity objects
     * @throws Exception unhandled errors.
     */
    public static VpcAcl convert(JSONObject object) throws Exception {
        VpcAcl vpcAcl = new VpcAcl();
        vpcAcl.setUuid(JsonValidator.jsonStringValidation(object, CloudStackConstants.CS_ID));
        vpcAcl.setName(JsonValidator.jsonStringValidation(object, CloudStackConstants.CS_NAME));
        vpcAcl.setDescription(JsonValidator.jsonStringValidation(object, CloudStackConstants.CS_DESCRIPTION));
        vpcAcl.setForDisplay(JsonValidator.jsonBooleanValidation(object, CloudStackConstants.CS_FOR_DISPLAY));
        return vpcAcl;
    }

    /**
     * Mapping VPC ACL entity object in list.
     *
     * @param vpcAclList list of VPC ACL
     * @return VPC ACL mapped values.
     */
    public static Map<String, VpcAcl> convert(List<VpcAcl> vpcAclList) {
        Map<String, VpcAcl> vpcAclMap = new HashMap<String, VpcAcl>();
        for (VpcAcl vpcAcl : vpcAclList) {
            vpcAclMap.put(vpcAcl.getUuid(), vpcAcl);
        }
        return vpcAclMap;
    }
}
