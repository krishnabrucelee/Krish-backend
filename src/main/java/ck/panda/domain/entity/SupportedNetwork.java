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
import ck.panda.util.JsonValidator;

/**
 * Get the supported network service list from cloud stack server and push into the application database.
 *
 */
@Entity
@Table(name = "supported_network")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class SupportedNetwork implements Serializable {

    /** Id of the supported network. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Name of the supported network. */
    @Column(name = "name", nullable = false)
    private String name;

    /** Network service provider. */
    @ManyToMany
    private List<NetworkServiceProvider> networkServiceProviderList;

    /** Status of the supported network. */
    @Column(name = "status")
    private Status status;

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

    /** Transient value of network service provider. */
    @Transient
    private String providerName;

    /** Transient provider id list. */
    @Transient
    private List<String> transProviderList;

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
     * Get the list of network service provider.
     *
     * @return the networkServiceProvider
     */
    public List<NetworkServiceProvider> getNetworkServiceProviderList() {
        return networkServiceProviderList;
    }

    /**
     * Set the list of network service provider.
     *
     * @param networkServiceProviderList to set
     */
    public void setNetworkServiceProviderList(List<NetworkServiceProvider> networkServiceProviderList) {
        this.networkServiceProviderList = networkServiceProviderList;
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
     * Get the provider name.
     *
     * @return the providerName
     */
    public String getProviderName() {
        return providerName;
    }

    /**
     * Set the provider name.
     *
     * @param providerName to set
     */
    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    /**
     * Get the transient provider list.
     *
     * @return transProviderList
     */
    public List<String> getTransProviderList() {
        return transProviderList;
    }

    /**
     * Set the transient provider list.
     *
     * @param transProviderList to set
     */
    public void setTransProviderList(List<String> transProviderList) {
        this.transProviderList = transProviderList;
    }

    /** Enumeration status for supported network. */
    public enum Status {
        /** Disabled status make supported network as soft deleted and it will not list on the applicaiton. */
        DISABLED,
        /** Enabled status is used to list supported network through out the application. */
        ENABLED
    }

    /**
     * Convert JSONObject to supported network entity.
     *
     * @param object json object
     * @return supported network entity objects
     * @throws Exception unhandled errors.
     */
    public static SupportedNetwork convert(JSONObject object) throws Exception {
        SupportedNetwork supportedNetwork = new SupportedNetwork();
        supportedNetwork.setName(JsonValidator.jsonStringValidation(object, CloudStackConstants.CS_NAME));
        supportedNetwork.setStatus(Status.ENABLED);
        return supportedNetwork;
    }

    /**
     * Mapping supported network entity object in list.
     *
     * @param supportedNetworkList list of supported networks
     * @return supported network mapped values.
     */
    public static Map<String, SupportedNetwork> convert(List<SupportedNetwork> supportedNetworkList) {
        Map<String, SupportedNetwork> supportedNetworkMap = new HashMap<String, SupportedNetwork>();
        for (SupportedNetwork supportedNetwork : supportedNetworkList) {
            supportedNetworkMap.put(supportedNetwork.getName(), supportedNetwork);
        }
        return supportedNetworkMap;
    }
}
