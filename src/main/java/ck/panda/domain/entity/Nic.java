package ck.panda.domain.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Type;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;
import ck.panda.util.JsonUtil;

/**
 * A Network Interface Card must be installed in a Virtual Machine so that it can be connected to a Network.
 */
@Entity
@Table(name = "nics")
@SuppressWarnings("serial")
public class Nic implements Serializable {

    /** ID of the nic. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Unique ID from Cloud Stack. */
    @Column(name = "uuid")
    private String uuid;

    /** Name of the network from Cloud Stack. */
    @Column(name = "name")
    private String name;

    /** Instance nic id. */
    @JoinColumn(name = "instance_id", referencedColumnName = "id", updatable = false, insertable = false)
    @OneToOne
    private VmInstance vmInstance;

    /** Instance id for nic. */
    @Column(name = "instance_id")
    private Long vmInstanceId;

    /** Network for nic. */
    @JoinColumn(name = "network_id", referencedColumnName = "id", updatable = false, insertable = false)
    @OneToOne
    private Network network;

    /** Network id for nic. */
    @Column(name = "network_id")
    private Long networkId;

    /** Check whether nic is in active state or in active state. */
    @Column(name = "is_active")
    private Boolean isActive;

    /** Secondary ipAddress of the Nic. */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VmIpaddress> vmIpAddress;

    /** Network ip Address to establish a connection. */
    @Column(name = "ip_address")
    private String ipAddress;

    /** Net mask value of Network . */
    @Column(name = "net_mask")
    private String netMask;

    /** Net mask value of Network . */
    @Transient
    private String secondaryIpAddress;

    /** Gateway of a network . */
    @Column(name = "gate_way")
    private String gateway;

    /** Is Default nic for a Vm Instance . */
    @Column(name = "is_default")
    private Boolean isDefault;

    /** Temporary variable. */
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

    /** Transient network of the instance. */
    @Transient
    private String transvmInstanceId;

    /** Transient network of the instance. */
    @Transient
    private String transNetworkId;

    /**
     * Get the id of the Nic.
     *
     * @return the id of the Nic
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id of the Nic.
     *
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the uuid of the Nic.
     *
     * @return the uuid of the Nic
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the uuid of the Nic.
     *
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get the instance.
     *
     * @return the vminstance
     */
    public VmInstance getVmInstance() {
        return vmInstance;
    }

    /**
     * Set the vminstance.
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
     * Set the vmInstanceId .
     *
     * @param vmInstanceId to set
     */
    public void setVmInstanceId(Long vmInstanceId) {
        this.vmInstanceId = vmInstanceId;
    }

    /**
     * Get Network for vm Instance.
     *
     * @return the Network.
     */
    public Network getNetwork() {
        return network;
    }

    /**
     * Set the the network.
     *
     * @param network to set
     */
    public void setNetwork(Network network) {
        this.network = network;
    }

    /**
     * Get Network Id for vm Instance.
     *
     * @return the networkId.
     */
    public Long getNetworkId() {
        return networkId;
    }

    /**
     * Set the the network Id.
     *
     * @param networkId to set.
     */
    public void setNetworkId(Long networkId) {
        this.networkId = networkId;
    }


    /**
     * @return the vmIpAddress
     */
    public List<VmIpaddress> getVmIpAddress() {
        return vmIpAddress;
    }

    /**
     * @param vmIpAddress the vmIpAddress to set
     */
    public void setVmIpAddress(List<VmIpaddress> vmIpAddress) {
        this.vmIpAddress = vmIpAddress;
    }

    /**
     * Get the created by of the Nic.
     *
     * @return the createdBy of the Nic
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the created by of the Nic.
     *
     * @param createdBy the created by to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updated by of the Nic.
     *
     * @return the updatedBy of the Nic
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updated by of the Nic.
     *
     * @param updatedBy the updated by to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the created date time of the Nic.
     *
     * @return the createdDateTime of the Nic
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the created date time of the Nic.
     *
     * @param createdDateTime the created date time to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the update date time of the Nic.
     *
     * @return the updatedDateTime of the Nic
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updated date time of the Nic.
     *
     * @param updatedDateTime the update date time to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
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
     * Get the ipAddress of a Network.
     *
     * @return the ipAddress
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Set the ipAddress.
     *
     * @param ipAddress to set
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Get the netMask.
     *
     * @return the netMask
     */
    public String getNetMask() {
        return netMask;
    }

    /**
     * Set the netMask .
     *
     * @param netMask to set
     */
    public void setNetMask(String netMask) {
        this.netMask = netMask;
    }

    /**
     * Get the gateway.
     *
     * @return the gateway
     */
    public String getGateway() {
        return gateway;
    }

    /**
     * Set the gateway.
     *
     * @param gateway to set
     */
    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    /**
     * Get isDefault.
     *
     * @return the isDefault
     */
    public Boolean getIsDefault() {
        return isDefault;
    }

    /**
     * Set the isDefault.
     *
     * @param isDefault to set
     */
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * Get the Transient VM Instance Id.
     *
     * @return the transvmInstanceId
     */
    public String getTransvmInstanceId() {
        return transvmInstanceId;
    }

    /**
     * Set the transvmInstanceId .
     *
     * @param transvmInstanceId to set
     */
    public void setTransvmInstanceId(String transvmInstanceId) {
        this.transvmInstanceId = transvmInstanceId;
    }

    /**
     * Get the Transient Network Id.
     *
     * @return the transNetworkId
     */
    public String getTransNetworkId() {
        return transNetworkId;
    }

    /**
     * Set the transNetworkId .
     *
     * @param transNetworkId to set
     */
    public void setTransNetworkId(String transNetworkId) {
        this.transNetworkId = transNetworkId;
    }

    /**
     * Get the name of the network.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name .
     *
     * @param name to set
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Get the secondaryIpAddress.
     *
     * @return the secondaryIpAddress
     */
    public String getSecondaryIpAddress() {
        return secondaryIpAddress;
    }

    /**
     * Set the secondaryIpAddress.
     *
     * @param secondaryIpAddress to set
     */
    public void setSecondaryIpAddress(String secondaryIpAddress) {
        this.secondaryIpAddress = secondaryIpAddress;
    }

    /**
     * Convert JSONObject to nic entity.
     *
     * @param jsonObject json object
     * @return nic entity object.
     * @throws Exception unhandled errors.
     */
    public static Nic convert(JSONObject jsonObject) throws Exception {
        Nic nic = new Nic();
        nic.setSyncFlag(false);
        nic.setUuid(JsonUtil.getStringValue(jsonObject, "id"));
        nic.setIpAddress(JsonUtil.getStringValue(jsonObject, "ipaddress"));
        nic.setGateway(JsonUtil.getStringValue(jsonObject, "gateway"));
        nic.setIsDefault(JsonUtil.getBooleanValue(jsonObject, "isdefault"));
        nic.setNetMask(JsonUtil.getStringValue(jsonObject, "netmask"));
        nic.setTransvmInstanceId((JsonUtil.getStringValue(jsonObject, "virtualmachineid")));
        nic.setTransNetworkId((JsonUtil.getStringValue(jsonObject, "networkid")));
        nic.setIsActive(true);
        return nic;
    }

    /**
     * Mapping entity object into list.
     *
     * @param nicList list of nics.
     * @return nicMap nics.
     */
    public static Map<String, Nic> convert(List<Nic> nicList) {
        Map<String, Nic> nicMap = new HashMap<String, Nic>();

        for (Nic nic : nicList) {
            nicMap.put(nic.getUuid(), nic);
        }

        return nicMap;
    }

}
