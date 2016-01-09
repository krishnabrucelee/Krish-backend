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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
import ck.panda.util.JsonValidator;

/**
 * Port forwarding.
 *
 */
@Entity
@Table(name = "port_forwarding_rule")
@EntityListeners(AuditingEntityListener.class)
public class PortForwarding {

    /** Unique Id of the Port Forwarding. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Cloudstack's Port Forwarding uuid. */
    @Column(name = "uuid")
    private String uuid;

    /** ipAddress of the Port Forwarding. */
    @JoinColumn(name = "ipaddress_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private IpAddress ipAddress;

    /** ipAddress id of the Port Forwarding. */
    @Column(name = "ipaddress_id")
    private Long ipAddressId;

    /** Starting private port of the firewall rule. */
    @Column(name = "private_start_port")
    private Integer privateStartPort;

    /** Ending private port of the firewall rule. */
    @Column(name = "private_end_port")
    private Integer privateEndPort;

    /** Starting public port of the firewall rule. */
    @Column(name = "public_start_port")
    private Integer publicStartPort;

    /** Ending public port of the firewall rule. */
    @Column(name = "public_end_port")
    private Integer publicEndPort;

    /** Instance Port Forwarding id. */
    @JoinColumn(name = "instance_id", referencedColumnName = "id", updatable = false, insertable = false)
    @OneToOne
    private VmInstance vmInstance;

    /** Instance id for Port Forwarding. */
    @Column(name = "instance_id")
    private Long vmInstanceId;

    /** Fordisplay of the Port Forwarding. */
    @Column(name = "fordisplay", columnDefinition = "tinyint default 0")
    private Boolean fordisplay;

    /** VM Guest Ip of the Port Forwarding. */
    @Column(name = "vm_guest_ip")
    private String vmGuestIp;

    /** Root disk controller id. */
    @Column(name = "protocol_type")
    @Enumerated(EnumType.STRING)
    private ProtocolType protocolType;

    /** Network Port Forwarding id. */
    @JoinColumn(name = "network_id", referencedColumnName = "id", updatable = false, insertable = false)
    @OneToOne
    private Network network;

    /** Network id for Port Forwarding. */
    @Column(name = "network_id")
    private Long networkId;

    /** An active attribute is to check whether the role is active or not. */
    @Column(name = "is_active", columnDefinition = "tinyint default 1")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean isActive;

    /** Created by user. */
    @CreatedBy
    @Column(name = "created_user_id")
    private Long createdBy;

    /** Last updated by user. */
    @LastModifiedBy
    @Column(name = "upated_user_id")
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

    /** temporary usage sync flag. */
    @Transient
    private Boolean syncFlag;

    /** Transient id of the instance. */
    @Transient
    private String transvmInstanceId;

    /** Transient id of the network. */
    @Transient
    private String transNetworkId;

    /** Transient id of the IP address. */
    @Transient
    private String transIpAddressId;

    /**
     * Get the id of the PortForwarding.

     * @return the id of PortForwarding.
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id of the PortForwarding.
     *
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the uuid of the PortForwarding.
     *
     * @return the uuid of the PortForwarding
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the uuid of the PortForwarding.
     *
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get the ipAddress of the PortForwarding.

     * @return the ipAddress of PortForwarding.
     */
    public IpAddress getIpAddress() {
        return ipAddress;
    }

    /**
     * Set the ipAddress of the PortForwarding.
     *
     * @param ipAddress the ipAddress to set
     */
    public void setIpAddress(IpAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Get the ipAddressId of the PortForwarding.

     * @return the ipAddressId of PortForwarding.
     */
    public Long getIpAddressId() {
        return ipAddressId;
    }

    /**
     * Set the ipAddressId of the PortForwarding.
     *
     * @param ipAddressId the ipAddressId to set
     */
    public void setIpAddressId(Long ipAddressId) {
        this.ipAddressId = ipAddressId;
    }

    /**
     * Get the private start port of the PortForwarding.

     * @return the privateStartPort of PortForwarding.
     */
    public Integer getPrivateStartPort() {
        return privateStartPort;
    }

    /**
     * Set the privateStartPort of the PortForwarding.
     *
     * @param privateStartPort the privateStartPort to set
     */
    public void setPrivateStartPort(Integer privateStartPort) {
        this.privateStartPort = privateStartPort;
    }

    /**
     * Get the private end port of the PortForwarding.

     * @return the privateEndPort of PortForwarding.
     */
    public Integer getPrivateEndPort() {
        return privateEndPort;
    }

    /**
     * Set the privateEndPort of the PortForwarding.
     *
     * @param privateEndPort the privateEndPort to set
     */
    public void setPrivateEndPort(Integer privateEndPort) {
        this.privateEndPort = privateEndPort;
    }

    /**
     * Get the public start port of the PortForwarding.

     * @return the publicStartPort of PortForwarding.
     */
    public Integer getPublicStartPort() {
        return publicStartPort;
    }

    /**
     * Set the publicStartPort of the PortForwarding.
     *
     * @param publicStartPort the publicStartPort to set
     */
    public void setPublicStartPort(Integer publicStartPort) {
        this.publicStartPort = publicStartPort;
    }

    /**
     * Get the public end port of the PortForwarding.

     * @return the publicEndPort of PortForwarding.
     */
    public Integer getPublicEndPort() {
        return publicEndPort;
    }

    /**
     * Set the publicEndPort of the PortForwarding.
     *
     * @param publicEndPort the publicEndPort to set
     */
    public void setPublicEndPort(Integer publicEndPort) {
        this.publicEndPort = publicEndPort;
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
     * Get the fordisplay.
     *
     * @return fordisplay
     */
    public Boolean getFordisplay() {
        return fordisplay;
    }

    /**
     * Set the fordisplay.
     *
     * @param fordisplay - the Boolean to set
     */
    public void setFordisplay(Boolean fordisplay) {
        this.fordisplay = fordisplay;
    }

    /**
     * Get the vm Guest Ip of the PortForwarding.

     * @return the vmGuestIp of PortForwarding.
     */
    public String getvmGuestIp() {
        return vmGuestIp;
    }

    /**
     * Set the vm Guest Ip of the PortForwarding.
     *
     * @param vmGuestIp the vmGuestIp to set
     */
    public void setvmGuestIp(String vmGuestIp) {
        this.vmGuestIp = vmGuestIp;
    }

    /**
     * Get the protocol type.
     *
     * @return protocolType
     */
    public ProtocolType getProtocolType() {
        return protocolType;
    }

    /**
     * Set the protocolType.
     *
     * @param protocolType - the protocolType enum to set
     */
    public void setProtocolType(ProtocolType protocolType) {
        this.protocolType = protocolType;
    }

    /**
     * Get the network.
     *
     * @return the network
     */
    public Network getNetwork() {
        return network;
    }

    /**
     * Set the network.
     *
     * @param network to set
     */
    public void setNetwork(Network network) {
        this.network = network;
    }

    /**
     * Get network Id.
     *
     * @return the networkId
     */
    public Long getNetworkId() {
        return networkId;
    }

    /**
     * Set the networkId .
     *
     * @param networkId to set
     */
    public void setNetworkId(Long networkId) {
        this.networkId = networkId;
    }

    /**
     * Get the isActive of the PortForwarding.

     * @return the isActive of PortForwarding.
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set the isActive of the PortForwarding.
     *
     * @param isActive the isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Get the createdBy of the PortForwarding.

     * @return the createdBy of PortForwarding.
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the createdBy of the PortForwarding.
     *
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updatedBy of the PortForwarding.

     * @return the updatedBy of PortForwarding.
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updatedBy of the PortForwarding.
     *
     * @param updatedBy the updatedBy to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the createdDateTime of the PortForwarding.

     * @return the createdDateTime of PortForwarding.
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the createdDateTime of the PortForwarding.
     *
     * @param createdDateTime the createdDateTime to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updatedDateTime of the PortForwarding.

     * @return the updatedDateTime of PortForwarding.
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updatedDateTime of the PortForwarding.
     *
     * @param updatedDateTime the updatedDateTime to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get the sync flag for temporary usage.
     *
     * @return syncFlag
     */
    public Boolean getSyncFlag() {
        return syncFlag;
    }

    /**
     * Set the sync flag for temporary usage.
     *
     * @param syncFlag - the Boolean to set
     */
    public void setSyncFlag(Boolean syncFlag) {
        this.syncFlag = syncFlag;
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
     * Get the Transient IP address Id.
     *
     * @return the transIpAddressId
     */
    public String getTransIpAddressId() {
        return transIpAddressId;
    }

    /**
     * Set the transIpAddressId .
     *
     * @param transIpAddressId to set
     */
    public void setTransIpAddressId(String transIpAddressId) {
        this.transIpAddressId = transIpAddressId;
    }

    /** protocol Type enum type used to list the static protocol type values. */
    public enum ProtocolType {
        /** protocolType as TCP. */
        TCP,
        /** protocolType type as UDP. */
        UDP
    }

    /**
     * Convert JSONObject to Port Forwarding entity.
     *
     * @param jsonObject json object
     * @return PortForwarding entity object.
     * @throws Exception unhandled errors.
     */
    public static PortForwarding convert(JSONObject jsonObject) throws Exception {
        PortForwarding portForwarding = new PortForwarding();
        portForwarding.setSyncFlag(false);
        portForwarding.setUuid(JsonUtil.getStringValue(jsonObject, "id"));
        portForwarding.setPrivateStartPort(JsonUtil.getIntegerValue(jsonObject, "privateport"));
        portForwarding.setPrivateEndPort(JsonUtil.getIntegerValue(jsonObject, "privateendport"));
        portForwarding.setPublicStartPort(JsonUtil.getIntegerValue(jsonObject, "publicport"));
        portForwarding.setPublicEndPort(JsonUtil.getIntegerValue(jsonObject, "publicendport"));
        portForwarding.setFordisplay(JsonUtil.getBooleanValue(jsonObject, "fordisplay"));
        portForwarding.setvmGuestIp(JsonUtil.getStringValue(jsonObject, "vmguestip"));
        portForwarding.setProtocolType(portForwarding.getProtocolType().valueOf(JsonValidator.jsonStringValidation(jsonObject, "protocol").toUpperCase()));
        portForwarding.setTransvmInstanceId((JsonUtil.getStringValue(jsonObject, "virtualmachineid")));
        portForwarding.setTransNetworkId((JsonUtil.getStringValue(jsonObject, "networkid")));
        portForwarding.setTransIpAddressId((JsonUtil.getStringValue(jsonObject, "ipaddressid")));
        portForwarding.setIsActive(true);
        return portForwarding;
    }

    /**
     * Mapping entity object into list.
     *
     * @param portForwardingList list of PortForwarding.
     * @return PortForwardingMap PortForwarding.
     */
    public static Map<String, PortForwarding> convert(List<PortForwarding> portForwardingList) {
        Map<String, PortForwarding> portForwardingMap = new HashMap<String, PortForwarding>();

        for (PortForwarding portForwarding : portForwardingList) {
            portForwardingMap.put(portForwarding.getUuid(), portForwarding);
        }

        return portForwardingMap;
    }

}
