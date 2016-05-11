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
import javax.persistence.OneToOne;
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

/**
 * IP addresses will need to be reserved for each POD, and a Guest IP Range assigned during the initial configuration of
 * the Zone. Every Host, System VM and Guest Instance within the whole Cloud must have a unique IP Address.
 */
@Entity
@Table(name = "ip_addresses")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class IpAddress implements Serializable {

    /** Unique Id of the IP address. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** public ip address. */
    @Column(name = "public_ip_address", nullable = false)
    private String publicIpAddress;

    /** public ip address uuid. */
    @Column(name = "uuid", nullable = false)
    private String uuid;

    /** public ip address vlan. */
    @Column(name = "vlan")
    private String vlan;

    /** Ip address source nat. */
    @Column(name = "is_source_nat")
    private Boolean isSourcenat;

    /** Ip address static nat. */
    @Column(name = "is_static_nat")
    private Boolean isStaticnat;

    /** Network for ip address. */
    @JoinColumn(name = "network_id", referencedColumnName = "id", updatable = false, insertable = false)
    @OneToOne
    private Network network;

    /** Network id for ip address. */
    @Column(name = "network_id")
    private Long networkId;

    /** Is the rule for display to the regular user. */
    @Column(name = "display")
    private Boolean display;

    /** IsActive attribute to verify Active or Inactive. */
    @Column(name = "is_active")
    private Boolean isActive;

    /** Domain of the ip address. */
    @JoinColumn(name = "domain_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Domain domain;

    /** Domain id of the ip address. */
    @Column(name = "domain_id")
    private Long domainId;

    /** Project of the ip address. */
    @JoinColumn(name = "project_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Project project;

    /** Project id of the ip address. */
    @Column(name = "project_id")
    private Long projectId;

    /** Department of the ip address. */
    @JoinColumn(name = "department_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Department department;

    /** Department id of the ip address. */
    @Column(name = "department_id")
    private Long departmentId;

    /** State for ipaddress, whether it is Free, Allocated etc . */
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private State state;

    /** public VPN uuid. */
    @Column(name = "vpn_uuid")
    private String vpnUuid;

    /** public VPN IP range. */
    @Column(name = "vpn_ip_range")
    private String vpnIpRange;

    /** public VPN Preshared Key. */
    @Column(name = "vpn_preshared_key")
    private String vpnPresharedKey;

    /** public VPN state. */
    @Column(name = "vpn_state")
    @Enumerated(EnumType.STRING)
    private VpnState vpnState;

    /** public VPN display. */
    @Column(name = "vpn_for_display")
    private Boolean vpnForDisplay;

    /** The Zone Id. */
    @JoinColumn(name = "zone_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Zone zone;

    /** ip address zone id. */
    @Column(name = "zone_id")
    private Long zoneId;

    /** Instance id for ip address. */
    @JoinColumn(name = "instance_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private VmInstance vmInstance;

    /** Instance id for ip address. */
    @Column(name = "instance_id")
    private Long vmInstanceId;

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

    /** Set syncFlag. */
    @Transient
    private Boolean syncFlag;

    /** Transient domain of the ip Address. */
    @Transient
    private String transDomainId;

    /** Transient host of the ip Address. */
    @Transient
    private String transProjectId;

    /** Transient network of the ip Address. */
    @Transient
    private String transNetworkId;

    /** Transient department id of the ip Address. */
    @Transient
    private String transDepartmentId;

    /** Transient zone of the instance. */
    @Transient
    private String transZoneId;

    /** VPC Object for the Ip address. */
    @JoinColumn(name = "vpc_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private VPC vpc;

    /** id for the VPC. */
    @Column(name = "vpc_id")
    private Long vpcId;

    /** Transient VPC id of the Ip address. */
    @Transient
    private String transVpcId;

    /**
     * Enumeration state for ipaddress.
     */
    public enum State {
        /** Allocated status for already acquired/assigned to network. */
        ALLOCATED,

        /** Free status for available public ips. */
        FREE
    }

    /**
     * Enumeration VPN state for ipaddress.
     */
    public enum VpnState {
        /** Disabled status for VPN state. */
        DISABLED,

        /** Allocated status for VPN state. */
        RUNNING
    }

    /**
     * Get the id.
     *
     * @return the id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id.
     *
     * @param id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get ipaddress uuid.
     *
     * @return the uuid.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * set ipaddress uuid.
     *
     * @param uuid to set.
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get the public ipaddress.
     *
     * @return the publicIpAddress.
     */
    public String getPublicIpAddress() {
        return publicIpAddress;
    }

    /**
     * Set the public ipaddress.
     *
     * @param publicIpAddress to set.
     */
    public void setPublicIpAddress(String publicIpAddress) {
        this.publicIpAddress = publicIpAddress;
    }

    /**
     * Get the network.
     *
     * @return the network.
     */
    public Network getNetwork() {
        return network;
    }

    /**
     * Set the network.
     *
     * @param network to set.
     */
    public void setNetwork(Network network) {
        this.network = network;
    }

    /**
     * Get the network's id.
     *
     * @return the networkId.
     */
    public Long getNetworkId() {
        return networkId;
    }

    /**
     * Set the network's id.
     *
     * @param networkId to set.
     */
    public void setNetworkId(Long networkId) {
        this.networkId = networkId;
    }

    /**
     * Get the domain.
     *
     * @return the domain.
     */
    public Domain getDomain() {
        return domain;
    }

    /**
     * Set the domain.
     *
     * @param domain to set.
     */
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    /**
     * Get the domain's id.
     *
     * @return the domainId.
     */
    public Long getDomainId() {
        return domainId;
    }

    /**
     * Set the domain's id.
     *
     * @param domainId to set.
     */
    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    /**
     * Get the project.
     *
     * @return the project.
     */
    public Project getProject() {
        return project;
    }

    /**
     * Set the project.
     *
     * @param project to set.
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * Get the project's id.
     *
     * @return the projectId.
     */
    public Long getProjectId() {
        return projectId;
    }

    /**
     * Set the project's id.
     *
     * @param projectId to set.
     */
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    /**
     * Get the department.
     *
     * @return the department.
     */
    public Department getDepartment() {
        return department;
    }

    /**
     * Set the department.
     *
     * @param department to set.
     */
    public void setDepartment(Department department) {
        this.department = department;
    }

    /**
     * Get the department's id.
     *
     * @return the departmentId.
     */
    public Long getDepartmentId() {
        return departmentId;
    }

    /**
     * Set the department's id.
     *
     * @param departmentId to set.
     */
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * Get the zone.
     *
     * @return the zone.
     */
    public Zone getZone() {
        return zone;
    }

    /**
     * Set the zone.
     *
     * @param zone to set.
     */
    public void setZone(Zone zone) {
        this.zone = zone;
    }

    /**
     * Get the zone's id.
     *
     * @return the zoneId.
     */
    public Long getZoneId() {
        return zoneId;
    }

    /**
     * Set the zone's id.
     *
     * @param zoneId to set.
     */
    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * Get the Instance.
     *
     * @return the vmInstance.
     */
    public VmInstance getVmInstance() {
        return vmInstance;
    }

    /**
     * Set the Instance.
     *
     * @param vmInstance to set.
     */
    public void setVmInstance(VmInstance vmInstance) {
        this.vmInstance = vmInstance;
    }

    /**
     * Get the instance's id.
     *
     * @return the vmInstanceId.
     */
    public Long getVmInstanceId() {
        return vmInstanceId;
    }

    /**
     * Set the instance's id.
     *
     * @param vmInstanceId to set.
     */
    public void setVmInstanceId(Long vmInstanceId) {
        this.vmInstanceId = vmInstanceId;
    }

    /**
     * Get the state for ipaddress.
     *
     * @return the state.
     */
    public State getState() {
        return state;
    }

    /**
     * Set the state for ipaddress.
     *
     * @param state the state to set
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * Get the uuid for VPN.
     *
     * @return the vpnUuid
     */
    public String getVpnUuid() {
        return vpnUuid;
    }

    /**
     * Set the uuid for VPN.
     *
     * @param vpnUuid the vpnUuid to set
     */
    public void setVpnUuid(String vpnUuid) {
        this.vpnUuid = vpnUuid;
    }

    /**
     * Get the range for VPN.
     *
     * @return the vpnIpRange
     */
    public String getVpnIpRange() {
        return vpnIpRange;
    }

    /**
     * Set the range for VPN.
     *
     * @param vpnIpRange the vpnIpRange to set
     */
    public void setVpnIpRange(String vpnIpRange) {
        this.vpnIpRange = vpnIpRange;
    }

    /**
     * Get the preshared key for VPN.
     *
     * @return the vpnPresharedKey
     */
    public String getVpnPresharedKey() {
        return vpnPresharedKey;
    }

    /**
     * Set the preshared key for VPN.
     *
     * @param vpnPresharedKey the vpnPresharedKey to set
     */
    public void setVpnPresharedKey(String vpnPresharedKey) {
        this.vpnPresharedKey = vpnPresharedKey;
    }

    /**
     * Get the state for VPN.
     *
     * @return the vpnState
     */
    public VpnState getVpnState() {
        return vpnState;
    }

    /**
     * Set the state for VPN.
     *
     * @param vpnState the vpnState to set
     */
    public void setVpnState(VpnState vpnState) {
        this.vpnState = vpnState;
    }

    /**
     * Get the state for display.
     *
     * @return the vpnForDisplay
     */
    public Boolean getVpnForDisplay() {
        return vpnForDisplay;
    }

    /**
     * Set the state for display.
     *
     * @param vpnForDisplay the vpnForDisplay to set
     */
    public void setVpnForDisplay(Boolean vpnForDisplay) {
        this.vpnForDisplay = vpnForDisplay;
    }

    /**
     * Get the version.
     *
     * @return the version.
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Set the version.
     *
     * @param version to set.
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get the isSourcenat.
     *
     * @return the isSourcenat
     */
    public Boolean getIsSourcenat() {
        return isSourcenat;
    }

    /**
     * Set the isSourcenat.
     *
     * @param isSourcenat  to set
     */
    public void setIsSourcenat(Boolean isSourcenat) {
        this.isSourcenat = isSourcenat;
    }

    /**
     * Get the display.
     *
     * @return the display
     */
    public Boolean getDisplay() {
        return display;
    }

    /**
     * Set the display.
     *
     * @param display to set
     */
    public void setDisplay(Boolean display) {
        this.display = display;
    }

    /**
     * Get the transDepartmentId.
     *
     * @return the transDepartmentId
     */
    public String getTransDepartmentId() {
        return transDepartmentId;
    }

    /**
     * Get the transDepartmentId.
     *
     * @param transDepartmentId to set
     */
    public void setTransDepartmentId(String transDepartmentId) {
        this.transDepartmentId = transDepartmentId;
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
     * Get the transNetworkId.
     *
     * @return the transNetworkId
     */
    public String getTransNetworkId() {
        return transNetworkId;
    }

    /**
     * Set the transNetworkId.
     *
     * @param transNetworkId to set
     */
    public void setTransNetworkId(String transNetworkId) {
        this.transNetworkId = transNetworkId;
    }

    /**
     * Get the transient domain id.
     *
     * @return the transDomainId
     */
    public String getTransDomainId() {
        return transDomainId;
    }

    /**
     * Set the transient domain id..
     *
     * @param transDomainId to set
     */
    public void setTransDomainId(String transDomainId) {
        this.transDomainId = transDomainId;
    }

    /**
     * Get transient zone id.
     *
     * @return the transZoneId
     */
    public String getTransZoneId() {
        return transZoneId;
    }

    /**
     * Set the transZoneId.
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
     * Get the created By.
     *
     * @return the createdBy.
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the created By.
     *
     * @param createdBy to set.
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updated By.
     *
     * @return the updatedBy.
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updated By.
     *
     * @param updatedBy to set.
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the created Date and Time.
     *
     * @return the createdDateTime.
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the created Date and Time.
     *
     * @param createdDateTime to set.
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the sync flag.
     *
     * @return the syncFlag.
     */
    public Boolean getSyncFlag() {
        return syncFlag;
    }

    /**
     * Set the sync flag.
     *
     * @param syncFlag to set.
     */
    public void setSyncFlag(Boolean syncFlag) {
        this.syncFlag = syncFlag;
    }

    /**
     * Get the updated Date and Time.
     *
     * @return the updatedDateTime.
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updated Date and Time.
     *
     * @param updatedDateTime to set.
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get ipaddress Vlan name.
     *
     * @return vlan name.
     */
    public String getVlan() {
        return vlan;
    }

    /**
     * Set ipaddress Vlan name.
     *
     * @param vlan to set.
     */
    public void setVlan(String vlan) {
        this.vlan = vlan;
    }

    /**
     * Get the isStaticnat.
     *
     * @return Static nat
     */
    public Boolean getIsStaticnat() {
        return isStaticnat;
    }

    /**
     * Set the isStaticnat.
     *
     * @param isStaticnat  to set
     */
    public void setIsStaticnat(Boolean isStaticnat) {
        this.isStaticnat = isStaticnat;
    }

    /**
     * Get vpc for network.
     *
     * @return the vpc
     */
    public VPC getVpc() {
        return vpc;
    }

    /**
     * Set Vpc for network.
     *
     * @param vpc the vpc to set
     */
    public void setVpc(VPC vpc) {
        this.vpc = vpc;
    }

    /**
     * Get Vpc id of the network.
     *
     * @return the vpcId
     */
    public Long getVpcId() {
        return vpcId;
    }

    /**
     * Set Vpc id of the network.
     *
     * @param vpcId the vpcId to set
     */
    public void setVpcId(Long vpcId) {
        this.vpcId = vpcId;
    }

    /**
     * Get the transVpcId.
     *
     * @return the transVpcId
     */
    public String getTransVpcId() {
        return transVpcId;
    }

    /**
     * Set the transVpcId.
     *
     * @param transVpcId to set
     */
    public void setTransVpcId(String transVpcId) {
        this.transVpcId = transVpcId;
    }

    /**
     * Convert JSONObject into pod object.
     *
     * @param jsonObject JSON object.
     * @return pod object.
     */
    public static IpAddress convert(JSONObject jsonObject) {
        IpAddress ipAddress = new IpAddress();
        try {
            ipAddress.setUuid(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_ID));
            ipAddress.setIsStaticnat(JsonUtil.getBooleanValue(jsonObject, CloudStackConstants.CS_IS_STATIC_NAT));
            ipAddress.setVlan(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_VLAN_NAME));
            ipAddress.setTransZoneId((JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_ZONE_ID)));
            ipAddress.setTransDomainId(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_DOMAIN_ID));
            ipAddress.setTransNetworkId(JsonUtil.getStringValue(jsonObject,CloudStackConstants.CS_ASSOCIATED_NETWORK_ID));
            ipAddress.setTransDepartmentId(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_ACCOUNT));
            ipAddress.setTransProjectId(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_PROJECT_ID));
            ipAddress.setPublicIpAddress(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_IP_ADDRESS));
            ipAddress.setDisplay(JsonUtil.getBooleanValue(jsonObject, CloudStackConstants.CS_FOR_DISPLAY));
            ipAddress.setIsSourcenat(JsonUtil.getBooleanValue(jsonObject, CloudStackConstants.CS_IS_SOURCE_NAT));
            ipAddress.setState(State.valueOf(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_STATE).toUpperCase()));
            ipAddress.setTransVpcId(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_VPC_ID));
            ipAddress.setIsActive(true);
            ipAddress.setSyncFlag(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ipAddress;
    }

    /**
     * Mapping entity object into list.
     *
     * @param ipAddressList list of IpAddress.
     * @return IpAddress map
     */
    public static Map<String, IpAddress> convert(List<IpAddress> ipAddressList) {
        Map<String, IpAddress> ipAddressMap = new HashMap<String, IpAddress>();

        for (IpAddress ipAddress : ipAddressList) {
            ipAddressMap.put(ipAddress.getUuid(), ipAddress);
        }

        return ipAddressMap;
    }

}
