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
 * Get the network service provider service list from cloud stack server and push into the application database.
 *
 */
@Entity
@Table(name = "network_service_provider")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class NetworkServiceProvider implements Serializable {

    /** Id of the network service provider. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Unique id of the network service provider. */
    @Column(name = "uuid")
    private String uuid;

    /** Name of the network service provider. */
    @Column(name = "name", nullable = false)
    private String name;

    /** Status of the network service provider. */
    @Column(name = "status")
    private Status status;

    /** Physical network Object for the network service provider. */
    @JoinColumn(name = "physical_network_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private PhysicalNetwork physicalNetwork;

    /** id for the physical network. */
    @Column(name = "physical_network_id")
    private Long physicalNetworkId;

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

    /** Physical network transient. */
    @Transient
    private String transPhysicalNetwork;

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
     * Get the physical network.
     *
     * @return the physicalNetwork
     */
    public PhysicalNetwork getPhysicalNetwork() {
        return physicalNetwork;
    }

    /**
     * Set the physical network.
     *
     * @param physicalNetwork to set
     */
    public void setPhysicalNetwork(PhysicalNetwork physicalNetwork) {
        this.physicalNetwork = physicalNetwork;
    }

    /**
     * Get the physical network id.
     *
     * @return the physicalNetworkId
     */
    public Long getPhysicalNetworkId() {
        return physicalNetworkId;
    }

    /**
     * Set the physical network id.
     *
     * @param physicalNetworkId to set
     */
    public void setPhysicalNetworkId(Long physicalNetworkId) {
        this.physicalNetworkId = physicalNetworkId;
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
     * Get the transient physical network.
     *
     * @return transPhysicalNetwork
     */
    public String getTransPhysicalNetwork() {
        return transPhysicalNetwork;
    }

    /**
     * Set the transient physical network.
     *
     * @param transPhysicalNetwork to set
     */
    public void setTransPhysicalNetwork(String transPhysicalNetwork) {
        this.transPhysicalNetwork = transPhysicalNetwork;
    }

    /** Enumeration status for network service provider. */
    public enum Status {
        /** Disabled status make network service provider as soft deleted and it will not list on the applicaiton. */
        DISABLED,
        /** Enabled status is used to list network service provider through out the application. */
        ENABLED
    }

    /**
     * Convert JSONObject to network service provider entity.
     *
     * @param object json object
     * @return network service provider entity objects
     * @throws Exception unhandled errors.
     */
    public static NetworkServiceProvider convert(JSONObject object) throws Exception {
        NetworkServiceProvider networkServiceProvider = new NetworkServiceProvider();
        networkServiceProvider.setUuid(JsonValidator.jsonStringValidation(object, CloudStackConstants.CS_ID));
        networkServiceProvider.setName(JsonValidator.jsonStringValidation(object, CloudStackConstants.CS_NAME));
        networkServiceProvider.setTransPhysicalNetwork(JsonValidator.jsonStringValidation(object, CloudStackConstants.CS_PHYSICAL_NETWORK_ID));
        networkServiceProvider.setStatus(Status.ENABLED);
        return networkServiceProvider;
    }

    /**
     * Mapping network service provider entity object in list.
     *
     * @param networkServiceProviderList list of network service providers
     * @return network service provider mapped values.
     */
    public static Map<String, NetworkServiceProvider> convert(List<NetworkServiceProvider> networkServiceProviderList) {
        Map<String, NetworkServiceProvider> networkServiceProviderMap = new HashMap<String, NetworkServiceProvider>();
        for (NetworkServiceProvider networkServiceProvider : networkServiceProviderList) {
            networkServiceProviderMap.put(networkServiceProvider.getUuid(), networkServiceProvider);
        }
        return networkServiceProviderMap;
    }
}
