package ck.panda.domain.entity;

import java.io.Serializable;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Type;
import org.json.JSONException;
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

/**
 * A virtual network is a logical construct that enables multi-tenancy on a single physical network. In CloudStack a
 * virtual network can be shared or isolated.
 */
@Entity
@Table(name = "networks")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class Network implements Serializable {

    /** Id of the Network. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Unique id for the Network. */
    @Column(name = "uuid")
    private String uuid;

    /** Name of the Network. */
    @Column(name = "name", nullable = false)
    private String name;

    /** Description of the Network. */
    @Column(name = "display_text", nullable = true)
    private String displayText;

    /** Domain Object for the Network. */
    @JoinColumn(name = "domain_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Domain domain;

    /** id for the Domain. */
    @Column(name = "domain_id")
    private Long domainId;

    /** Zone Object for the Network. */
    @JoinColumn(name = "zone_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Zone zone;

    /** id for the Zone. */
    @Column(name = "zone_id")
    private Long zoneId;

    /** Project Object for the Network. */
    @JoinColumn(name = "project_id", referencedColumnName = "id", updatable = false, insertable = false)
    @ManyToOne
    private Project project;

    /** Network project id. */
    @Column(name = "project_id")
    private Long projectId;

    /** Department Object for the Network. */
    @JoinColumn(name = "department_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Department department;

    /** id for the Department. */
    @Column(name = "department_id")
    private Long departmentId;

    /** NetworkOffering Object for the Network Offer. */
    @ManyToOne
    @JoinColumn(name = "networkoffering_id", referencedColumnName = "id", updatable = false, insertable = false)
    private NetworkOffering networkOffering;

    /** NetworkOffering id for the Zone. */
    @Column(name = "networkoffering_id")
    private Long networkOfferingId;

    /** Type of the Network. */
    @Column(name = "network_type")
    @Enumerated(EnumType.STRING)
    private NetworkType networkType;

    /** CIDR Range of the IP address. */
    @Column(name = "cidr")
    private String cIDR;

    /** Gateway of the Network. */
    @Column(name = "gateway")
    private String gateway;

    /** Netmask of the Network. */
    @Column(name = "netmask", nullable = true)
    private String netMask;

    /** Network Domain for the Network. */
    @Column(name = "network_domain", nullable = true)
    private String networkDomain;

    /** IsActive attribute to verify Active or Inactive. */
    @Column(name = "is_active")
    private Boolean isActive;

    /** Version attribute to handle optimistic locking. */
    @Version
    @Column(name = "version")
    private Long version;

    /** Status attribute to verify status of the Network. */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

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

    /** Transient domain of the account. */
    @Transient
    private String transDomainId;

    /** Transient zone of the network. */
    @Transient
    private String transZoneId;

    /** Transient department of the network. */
    @Transient
    private String transDepartmentId;

    /** Transient network offering of the network. */
    @Transient
    private String transNetworkOfferingId;

    /** Transient project of the network. */
    @Transient
    private String transProjectId;

    /** Instance id. */
    @JoinColumn(name = "instance_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private VmInstance vmInstance;

    /** Instance id for Network. */
    @Column(name = "instance_id")
    private Long vmInstanceId;

    /** Removing old elements of the networks. */
    @Column(name = "clean_up")
    private Boolean cleanUpNetwork;

    /** To check whether network is restarted. */
    @Column(name = "network_restart")
    private Boolean networkRestart;

    /** Enum type for Network Type. */
    public enum NetworkType {
        /** Network type be Shared. */
        Shared,
        /** Network type be Isolated. */
        Isolated,
    }

    /** Enum type for Network Status. */
    public enum Status {
        /** Network will be in a Implemented State. */
        IMPLEMENTED,
        /** Network will be in a Allocated State. */
        ALLOCATED,
        /** Network will be in a destroyed State. */
        DESTROY,
        /** Network will be in a Shutdown State. */
        SHUTDOWN,
        /** Network will be in a Setup State. */
        SETUP
    }

    /** Set syncFlag. */
    @Transient
    private Boolean syncFlag;

    /**
     * Get the Network Id.
     *
     * @return the Network Id
     */
    public Long getId() {
        return id;
    }

    /**
     * Get the Network uuid.
     *
     * @return the uuid of the Network
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Get the Zone.
     *
     * @return the zone
     */
    public Zone getZone() {
        return zone;
    }

    /**
     * Set the Zone.
     *
     * @param zone the zone to set
     */
    public void setZone(Zone zone) {
        this.zone = zone;
    }

    /**
     * Get the zoneId.
     *
     * @return the zoneId
     */
    public Long getZoneId() {
        return zoneId;
    }

    /**
     * Set the zoneId.
     *
     * @param zoneId the zoneId to set
     */
    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * Get network project.
     *
     * @return the project.
     */
    public Project getProject() {
        return project;
    }

    /**
     * Set network project.
     *
     * @param project to set.
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * Get network project id.
     *
     * @return the projectId.
     */
    public Long getProjectId() {
        return projectId;
    }

    /**
     * Set network project id.
     *
     * @param projectId the project id to set.
     */
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    /**
     * Get the Network Name.
     *
     * @return the name of the Network
     */
    public String getName() {
        return name;
    }

    /**
     * Get the Network Description.
     *
     * @return the description of Network
     */
    public String getDisplayText() {
        return displayText;
    }

    /**
     * Get the Domain.
     *
     * @return the id of the domain
     */
    public Long getDomainId() {
        return domainId;
    }

    /**
     * Get the Network type.
     *
     * @return the type of the network
     */
    public NetworkType getNetworkType() {
        return networkType;
    }

    /**
     * Get the Network State.
     *
     * @return Active or Inactive state
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Get the Network Version.
     *
     * @return the version of Network
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Get the Network Status.
     *
     * @return the status of Network
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Get the user who creates Network.
     *
     * @return the createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Get the user who updates Network.
     *
     * @return the updatedBy
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Get the DateTime of created Network.
     *
     * @return the DateTime
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Get the DateTime of updated Network.
     *
     * @return the DateTime
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the Network Id.
     *
     * @param id Network id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Set the Network uuid.
     *
     * @param uuid Network uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Set the Network name.
     *
     * @param name Network name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the Network Description.
     *
     * @param displayText Network description to set
     */
    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    /**
     * Set the Domain Id.
     *
     * @param domainId the domainId to set
     */
    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    /**
     * Get the NetworkOffering Id.
     *
     * @return the networkOffering
     */
    public NetworkOffering getNetworkOffering() {
        return networkOffering;
    }

    /**
     * Get the Network cIDR.
     *
     * @return the cIDR
     */
    public String getcIDR() {
        return cIDR;
    }

    /**
     * Set the Network Offering Id.
     *
     * @param networkOffering the networkOffering to set
     */
    public void setNetworkOffering(NetworkOffering networkOffering) {
        this.networkOffering = networkOffering;
    }

    /**
     * Set the Network cIDR.
     *
     * @param cIDR the cIDR to set
     */
    public void setcIDR(String cIDR) {
        this.cIDR = cIDR;
    }

    /**
     * Set the Network Type.
     *
     * @param networkType the networkType to set
     */
    public void setNetworkType(NetworkType networkType) {
        this.networkType = networkType;
    }

    /**
     * Set the Network State.
     *
     * @param isActive the isActive state to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Set the Network Version.
     *
     * @param version the version to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Set the Network Status.
     *
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Set the user who creates Network.
     *
     * @param createdBy Network createdBy to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Set the user who updates Network.
     *
     * @param updatedBy Network updatedBy to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Set the Created DateTime for Network.
     *
     * @param createdDateTime Network createdDateTime to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Set the Updated DateTime for Network.
     *
     * @param updatedDateTime Network updatedDateTime to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get the syncFlag.
     *
     * @return the syncFlag
     */
    public Boolean getSyncFlag() {
        return syncFlag;
    }

    /**
     * Set the syncFlag.
     *
     * @param syncFlag the syncFlag to set
     */
    public void setSyncFlag(Boolean syncFlag) {
        this.syncFlag = syncFlag;
    }

    /**
     * Get the Domain Object.
     *
     * @return the domain
     */
    public Domain getDomain() {
        return domain;
    }

    /**
     * Set the Domain Object.
     *
     * @param domain the domain to set
     */
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    /**
     * Get the networkOffering Id.
     *
     * @return the networkOfferingId
     */
    public Long getNetworkOfferingId() {
        return networkOfferingId;
    }

    /**
     * Set the networkOffering Id.
     *
     * @param networkOfferingId the networkOfferingId to set
     */
    public void setNetworkOfferingId(Long networkOfferingId) {
        this.networkOfferingId = networkOfferingId;
    }

    /**
     * Get the Network Gateway.
     *
     * @return the gateway
     */
    public String getGateway() {
        return gateway;
    }

    /**
     * Set the Network Gateway.
     *
     * @param gateway the gateway to set
     */
    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    /**
     * Get the Department object.
     *
     * @return the department
     */
    public Department getDepartment() {
        return department;
    }

    /**
     * Set the Department object.
     *
     * @param department the department to set
     */

    public void setDepartment(Department department) {
        this.department = department;
    }

    /**
     * Get the department Id.
     *
     * @return the departmentId
     */

    public Long getDepartmentId() {
        return departmentId;
    }

    /**
     * Set the Department Id.
     *
     * @param departmentId the departmentId to set
     */
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * Get the domain Id.
     *
     * @return the transDomainId
     */
    public String getTransDomainId() {
        return transDomainId;
    }

    /**
     * Set the domain Id.
     *
     * @param transDomainId the transDomainId to set
     */
    public void setTransDomainId(String transDomainId) {
        this.transDomainId = transDomainId;
    }

    /**
     * Get the Zone Id.
     *
     * @return the transZoneId
     */
    public String getTransZoneId() {
        return transZoneId;
    }

    /**
     * Set the Zone Id.
     *
     * @param transZoneId the transZoneId to set
     */
    public void setTransZoneId(String transZoneId) {
        this.transZoneId = transZoneId;
    }

    /**
     * Get the Department Id.
     *
     * @return the transDepartmentId
     */
    public String getTransDepartmentId() {
        return transDepartmentId;
    }

    /**
     * Set the department Id.
     *
     * @param transDepartmentId the transDepartmentId to set
     */
    public void setTransDepartmentId(String transDepartmentId) {
        this.transDepartmentId = transDepartmentId;
    }

    /**
     * Get the NetworkOffering Id.
     *
     * @return the transNetworkOfferingId
     */
    public String getTransNetworkOfferingId() {
        return transNetworkOfferingId;
    }

    /**
     * Set the NetworkOffering Id.
     *
     * @param transNetworkOfferingId the transNetworkOfferingId to set
     */
    public void setTransNetworkOfferingId(String transNetworkOfferingId) {
        this.transNetworkOfferingId = transNetworkOfferingId;
    }

    /**
     * Get the transProjectId.
     *
     * @return the transProjectId
     */
    public String getTransProjectId() {
        return transProjectId;
    }

    /**
     * Set the transProjectId.
     *
     * @param transProjectId to set
     */
    public void setTransProjectId(String transProjectId) {
        this.transProjectId = transProjectId;
    }

    /**
     * Get the netMask of the Network.
     *
     * @return the netMask
     */
    public String getNetMask() {
        return netMask;
    }

    /**
     * Get the netMask to the Network.
     *
     * @param netMask the netMask to set
     */
    public void setNetMask(String netMask) {
        this.netMask = netMask;
    }

    /**
     * Get the Network Domain of the Network.
     *
     * @return the networkDomain
     */
    public String getNetworkDomain() {
        return networkDomain;
    }

    /**
     * Set the Network Domain to the Network.
     *
     * @param networkDomain the networkDomain to set
     */
    public void setNetworkDomain(String networkDomain) {
        this.networkDomain = networkDomain;
    }

    /**
     * Get the instance.
     *
     * @return the vmInstance
     */
    public VmInstance getVmInstance() {
        return vmInstance;
    }

    /**
     * Set the vmInstance.
     *
     * @param vmInstance to set
     */
    public void setVmInstance(VmInstance vmInstance) {
        this.vmInstance = vmInstance;
    }

    /**
     * Get instance Id.
     *
     * @return the vmInstanceId
     */
    public Long getVmInstanceId() {
        return vmInstanceId;
    }

    /**
     * Set the vmInstanceId.
     *
     * @param vmInstanceId to set
     */
    public void setVmInstanceId(Long vmInstanceId) {
        this.vmInstanceId = vmInstanceId;
    }

    /**
     * Get the network state.
     *
     * @return cleanUpNetwork
     */
    public Boolean getCleanUpNetwork() {
        return cleanUpNetwork;
    }

    /**
     * Set the network state.
     *
     * @param cleanUpNetwork to set.
     */
    public void setCleanUpNetwork(Boolean cleanUpNetwork) {
        this.cleanUpNetwork = cleanUpNetwork;
    }

    /**
     * Get the network to restart.
     *
     * @return the networkRestart
     */
    public Boolean getNetworkRestart() {
        return networkRestart;
    }

    /**
     * Set the network to restart.
     *
     * @param networkRestart to set
     */
    public void setNetworkRestart(Boolean networkRestart) {
        this.networkRestart = networkRestart;
    }

    /**
     * Convert JSONObject to domain entity.
     *
     * @param jsonObject Object
     * @return domain entity object.
     * @throws JSONException handles json exception.
     */
    public static Network convert(JSONObject jsonObject) throws JSONException {
        Network network = new Network();
        network.setSyncFlag(false);
        try {
            network.setName(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_NAME));
            network.setUuid(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_ID));
            network.setTransZoneId((JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_ZONE_ID)));
            network.setTransDomainId((JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_DOMAIN_ID)));
            network.setNetworkType(NetworkType.valueOf(JsonUtil.getStringValue(jsonObject,CloudStackConstants.CS_TYPE)));
            network.setTransNetworkOfferingId(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_NETWORK_OFFERING_ID));
            network.setcIDR(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_CIDR));
            network.setDisplayText(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_DISPLAY_TEXT));
            network.setGateway(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_GATEWAY));
            network.setTransDepartmentId(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_ACCOUNT));
            network.setStatus(Status.valueOf(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_STATE).toUpperCase()));
            network.setNetMask(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_NETMASK));
            network.setNetworkDomain(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_NETWORK_DOMAIN));
            network.setTransProjectId(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_PROJECT_ID));
            network.setIsActive(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return network;
    }

    /**
     * Mapping entity object into list.
     *
     * @param networkList list of networks.
     * @return network map
     */
    public static Map<String, Network> convert(List<Network> networkList) {
        Map<String, Network> networkMap = new HashMap<String, Network>();
        for (Network network : networkList) {
            networkMap.put(network.getUuid(), network);
        }
        return networkMap;
    }
}
