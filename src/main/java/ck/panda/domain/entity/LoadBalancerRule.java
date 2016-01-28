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
import javax.persistence.ManyToMany;
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

/**
 * The Firewall controlls the traffic originates from the guest network and sent to public network. This features
 * controls the Egress (outgoing) traffic from the guest network in Advanced zone. The egress firewall rules applied
 * will restrict the traffic from guest network on the Virtual Router.
 */
@Entity
@Table(name = "load_balance_rules")
@EntityListeners(AuditingEntityListener.class)
public class LoadBalancerRule {

    /** Unique Id of the Firewall Rule. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Cloudstack's LB Rule uuid. */
    @Column(name = "uuid")
    private String uuid;

    /** Cloudstack's Stikiness Rule uuid. */
    @Column(name = "sticky_uuid")
    private String stickyUuid;

    /** Cloudstack's Firewall Rule name. */
    @Column(name = "name")
    private String name;

    /** Network for Firewall Rule. */
    @JoinColumn(name = "network_id", referencedColumnName = "id", updatable = false, insertable = false)
    @OneToOne
    private Network network;

    /** Network id for Firewall Rule. */
    @Column(name = "network_id")
    private Long networkId;

    /** Zone for Firewall Rule. */
    @JoinColumn(name = "zone_id", referencedColumnName = "id", updatable = false, insertable = false)
    @OneToOne
    private Zone zone;

    /** Zone id for Firewall Rule. */
    @Column(name = "zone_id")
    private Long zoneId;

    /** List of instance Class for an Load balancer. */
    @ManyToMany
    private List<VmInstance> vmInstanceList;

    /** Domain of the Firewall Rule. */
    @JoinColumn(name = "domain_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Domain domain;

    /** Domain id of the Firewall Rule. */
    @Column(name = "domain_id")
    private Long domainId;

    /** ipAddress of the Firewall Rule. */
    @JoinColumn(name = "ipaddress_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private IpAddress ipAddress;

    /** ipAddress id of the Firewall Rule. */
    @Column(name = "ipaddress_id")
    private Long ipAddressId;

    /** Private port of the firewall rule. */
    @Column(name = "private_port")
    private Integer privatePort;

    /** Public port of the firewall rule. */
    @Column(name = "public_port")
    private Integer publicPort;

    /** Error code for ICMP message. */
    @Column(name = "icmp_code")
    private Integer icmpCode;

    /** Type of the ICMP message. */
    @Column(name = "icmp_message")
    private Integer icmpMessage;

    /** VPC for a network. */
    @Column(name = "vpc")
    private String vpc;

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

    /** The source cidr list to forward traffic . */
    @Column(name = "source_cidr")
    private String sourceCIDR;

    /** Is the rule for display to the regular user. */
    @Column(name = "display")
    private Boolean display;

    /** Rule state . */
    @Column(name = "state")
    private State state;

    /** Set of rules or protocols for an IP address .*/
    @Column(name = "protocol")
    @Enumerated(EnumType.STRING)
    private Protocol protocol;

    /** Different set of rules for a network . */
    @Column(name = "purpose")
    @Enumerated(EnumType.STRING)
    private Purpose purpose;

    /** TYpes of traffic over a network . */
    @Column(name = "traffic_type")
    @Enumerated(EnumType.STRING)
    private TrafficType trafficType;

    /** Stickiness policy's method name  . */
    @Column(name = "method_name")
    @Enumerated(EnumType.STRING)
    private SticknessMethod stickinessMethod;

    /** Cloudstack's Firewall Rule algorithm. */
    @Column(name = "algorithm")
    private String algorithm;

    /** Is this firewall rule is active. */
    @Column(name = "is_Active")
    private Boolean isActive;

    /** Temporary variable. */
    @Transient
    private Boolean syncFlag;

    /** Transient network of the instance. */
    @Transient
    private String transNetworkId;

    /** Transient zone of the instance. */
    @Transient
    private String transZoneId;

    /** Transient id of the instance. */
    @Transient
    private String transvmInstanceId;

    /** Transient id of the ipAddress. */
    @Transient
    private String transIpAddressId;

    /** Transient id of the domain. */
    @Transient
    private String transDomainId;

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

    /** Different set of rules . */
    public enum Purpose {
        /** Firewall rule for network. */
        FIREWALL,

        /** Port Forwarding rule for network. */
        PORTFORWARDING,

        /** Load Balancing rule for network. */
        LOADBALANCING
    }

    /** Traffic type for a network . */
    public enum TrafficType {
        /** Rule for controlling traffic to outside . */
        EGRESS,

        /** Rule for controlling traffic from inside. */
        INGRESS
    }

    /** Types of protocol for an IP Address .*/
    public enum Protocol {
        /**  TCP enables two hosts to establish a connection and exchange streams of data. */
        TCP,

        /** User Datagram Protocol (UDP) is a transport layer protocol provides a best-effort datagram service to an End System (IP host).*/
        UDP,

        /** It is used to send error message when  requested service is not available or that a host or router could not be reached. */
        ICMP,

        /** All the above three protocols .*/
        ALL
    }

    /** Types of state for an firewall . */
    public enum State {
        /** Load Balance rule in Active state. */
        ACTIVE,

        /** Load Balance rule in Staged state.*/
        STAGED,

        /** Load Balance rule in ADD state. */
        ADD
    }

    /** Types of methods for stickiness policy . */
    public enum SticknessMethod {

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
     * Get Firewall Rule id.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the Firewall Rule id.
     *
     * @param id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get Firewall Rule Uuid.
     *
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the Firewall Rule uuid.
     *
     * @param uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get Firewall Rule Name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the Firewall Rule Name.
     *
     * @param name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get network of the Firewall Rule.
     *
     * @return the network
     */
    public Network getNetwork() {
        return network;
    }

    /**
     * Set the network of the Firewall Rule.
     *
     * @param network to set
     */
    public void setNetwork(Network network) {
        this.network = network;
    }

    /**
     * Get the networkId of the Firewall Rule.
     *
     * @return the networkId
     */
    public Long getNetworkId() {
        return networkId;
    }

    /**
     * Set the networkId of the Firewall Rule.
     *
     * @param networkId to set
     */
    public void setNetworkId(Long networkId) {
        this.networkId = networkId;
    }

    /**
     * Get zone of the Firewall Rule.
     *
     * @return the zone
     */
    public Zone getZone() {
        return zone;
    }

    /**
     * Set the zone of the Firewall Rule.
     *
     * @param zone to set
     */
    public void setZone(Zone zone) {
        this.zone = zone;
    }

    /**
     * Get the zoneId of the Firewall Rule.
     *
     * @return the zoneId
     */
    public Long getZoneId() {
        return zoneId;
    }

    /**
     * Set the zoneId of the Firewall Rule.
     *
     * @param zoneId to set
     */
    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * Get the instance list.
     *
     * @return the instance list.
     */
    public List<VmInstance> getVmInstanceList() {
        return vmInstanceList;
    }

    /**
     * Set the instance list.
     *
     * @param vmInstanceList the instance list to set.
     */
    public void setVmInstanceList(List<VmInstance> vmInstanceList) {
        this.vmInstanceList = vmInstanceList;
    }

    /**
     * Get the domain of the Firewall Rule.
     *
     * @return the domain
     */
    public Domain getDomain() {
        return domain;
    }

    /**
     * Set the domain of the Firewall Rule.
     *
     * @param domain to set
     */
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    /**
     * Get the domainId of the Firewall Rule.
     *
     * @return the domainId
     */
    public Long getDomainId() {
        return domainId;
    }

    /**
     * Set the domainId of the Firewall Rule.
     *
     * @param domainId to set
     */
    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    /**
     * Get the private port.
     *
     * @return the privatePort
     */
    public Integer getPrivatePort() {
        return privatePort;
    }

    /**
     * Set the private port .
     *
     * @param privatePort to set
     */
    public void setPrivatePort(Integer privatePort) {
        this.privatePort = privatePort;
    }

    /**
     * Get the public port.
     *
     * @return the publicPort
     */
    public Integer getPublicPort() {
        return publicPort;
    }

    /**
     * Set the public port.
     *
     * @param publicPort to set
     */
    public void setPublicPort(Integer publicPort) {
        this.publicPort = publicPort;
    }

    /**
     * Get the icmpCode.
     *
     * @return the icmpCode
     */
    public Integer getIcmpCode() {
        return icmpCode;
    }

    /**
     * Set the icmpCode.
     *
     * @param icmpCode to set
     */
    public void setIcmpCode(Integer icmpCode) {
        this.icmpCode = icmpCode;
    }

    /**
     * Get the icmpMessage .
     *
     * @return the icmpMessage
     */
    public Integer getIcmpMessage() {
        return icmpMessage;
    }

    /**
     * Set the icmpMessage .
     *
     * @param icmpMessage to set
     */
    public void setIcmpMessage(Integer icmpMessage) {
        this.icmpMessage = icmpMessage;
    }

    /**
     * Get vpc.
     *
     * @return the vpc
     */
    public String getVpc() {
        return vpc;
    }

    /**
     * Set the vpc.
     *
     * @param vpc to set
     */
    public void setVpc(String vpc) {
        this.vpc = vpc;
    }

    /**
     * Get the sourceCIDR.
     *
     * @return the sourceCIDR
     */
    public String getSourceCIDR() {
        return sourceCIDR;
    }

    /**
     * Set the sourceCIDR.
     *
     * @param sourceCIDR to set
     */
    public void setSourceCIDR(String sourceCIDR) {
        this.sourceCIDR = sourceCIDR;
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
     * Get the state.
     *
     * @return the state
     */
    public State getState() {
        return state;
    }

    /**
     * Set the state.
     *
     * @param state to set
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * Get the ipAddress of the Firewall Rule.
     *
     * @return the ipAddress
     */
    public IpAddress getIpAddress() {
        return ipAddress;
    }

    /**
     * Set the ipAddress of the Firewall Rule.
     *
     * @param ipAddress to set
     */
    public void setIpAddress(IpAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Get the ipAddressId of the Firewall Rule.
     *
     * @return the ipAddressId
     */
    public Long getIpAddressId() {
        return ipAddressId;
    }

    /**
     * Get the purpose.
     *
     * @return the purpose
     */
    public Purpose getPurpose() {
        return purpose;
    }

    /**
     * Set the purpose.
     *
     * @param purpose to set
     */
    public void setPurpose(Purpose purpose) {
        this.purpose = purpose;
    }

    /**
     * Set the ipAddressId of the Firewall Rule.
     *
     * @param ipAddressId to set
     */
    public void setIpAddressId(Long ipAddressId) {
        this.ipAddressId = ipAddressId;
    }

    /**
     * Get the trafficType.
     *
     * @return the trafficType
     */
    public TrafficType getTrafficType() {
        return trafficType;
    }

    /**
     * Get Firewall Rule Algorithm.
     *
     * @return the algorithm
     */
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * Set the Firewall Rule Algorithm.
     *
     * @param algorithm to set
     */
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * Set the trafficType.
     *
     * @param trafficType to set
     */
    public void setTrafficType(TrafficType trafficType) {
        this.trafficType = trafficType;
    }

    /**
     * Get protocol of an Ip address.
     *
     * @return the protocol
     */
    public Protocol getProtocol() {
        return protocol;
    }

    /**
     * Set the protocol of an Ip address.
     *
     * @param protocol to set
     */
    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
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
     * Set the isActive.
     *
     * @param isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
     * @param syncFlag to set
     */
    public void setSyncFlag(Boolean syncFlag) {
        this.syncFlag = syncFlag;
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
     * Get the Transient Zone Id.
     *
     * @return the transZoneId
     */
    public String getTransZoneId() {
        return transZoneId;
    }

    /**
     * Set the transZoneId .
     *
     * @param transZoneId to set
     */
    public void setTransZoneId(String transZoneId) {
        this.transZoneId = transZoneId;
    }

    /**
     * Get the Transient IP Address Id.
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

    /**
     * Get the Transient domain Id.
     *
     * @return the transDomainId
     */
    public String getTransDomainId() {
        return transDomainId;
    }

    /**
     * Set the transDomainId .
     *
     * @param transDomainId to set
     */
    public void setTransDomainId(String transDomainId) {
        this.transDomainId = transDomainId;
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
     * @return the stickyUuid
     */
    public String getStickyUuid() {
        return stickyUuid;
    }

    /**
     * @param stickyUuid the stickyUuid to set
     */
    public void setStickyUuid(String stickyUuid) {
        this.stickyUuid = stickyUuid;
    }



    /**
     * Get the createdBy user id.
     *
     * @return the createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the createdBy user id.
     *
     * @param createdBy to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updatedBy user id.
     *
     * @return the updatedBy
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updatedBy user id.
     *
     * @param updatedBy to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the createdDateTime.
     *
     * @return the createdDateTime
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the createdDateTime.
     *
     * @param createdDateTime to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updatedDateTime.
     *
     * @return the updatedDateTime
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updatedDateTime.
     *
     * @param updatedDateTime to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }


    /**
     * @return the stickinessMethod
     */
    public SticknessMethod getStickinessMethod() {
        return stickinessMethod;
    }

    /**
     * @param stickinessMethod the stickinessMethod to set
     */
    public void setStickinessMethod(SticknessMethod stickinessMethod) {
        this.stickinessMethod = stickinessMethod;
    }

    /**
     * Convert JSONObject to nic entity.
     *
     * @param jsonObject json object
     * @return nic entity object.
     * @throws Exception unhandled errors.
     */
    public static LoadBalancerRule convert(JSONObject jsonObject) throws Exception {
        LoadBalancerRule loadBalancer = new LoadBalancerRule();
        loadBalancer.setSyncFlag(false);
        loadBalancer.setUuid(JsonUtil.getStringValue(jsonObject, "id"));
        loadBalancer.setDisplay(JsonUtil.getBooleanValue(jsonObject, "fordisplay"));
        loadBalancer.setSourceCIDR(JsonUtil.getStringValue(jsonObject,"cidrlist"));
        loadBalancer.setState(State.valueOf(JsonUtil.getStringValue(jsonObject,"state").toUpperCase()));
        loadBalancer.setName(JsonUtil.getStringValue(jsonObject, "name"));
        loadBalancer.setPrivatePort(JsonUtil.getIntegerValue(jsonObject, "privateport"));
        loadBalancer.setPublicPort(JsonUtil.getIntegerValue(jsonObject, "publicport"));
        loadBalancer.setAlgorithm(JsonUtil.getStringValue(jsonObject, "algorithm"));
        loadBalancer.setTransNetworkId((JsonUtil.getStringValue(jsonObject, "networkid")));
        loadBalancer.setTransIpAddressId(JsonUtil.getStringValue(jsonObject, "publicipid"));
        loadBalancer.setTransZoneId(JsonUtil.getStringValue(jsonObject, "zoneid"));
        loadBalancer.setTransDomainId(JsonUtil.getStringValue(jsonObject, "domainid"));
        loadBalancer.setIsActive(true);
        return loadBalancer;
    }

    /**
     * Mapping entity object into list.
     *
     * @param loadBalancerList list of Load Balancer.
     * @return egressMap egress.
     */
    public static Map<String, LoadBalancerRule> convert(List<LoadBalancerRule> loadBalancerList) {
        Map<String, LoadBalancerRule> egressMap = new HashMap<String, LoadBalancerRule>();

        for (LoadBalancerRule nic : loadBalancerList) {
            egressMap.put(nic.getUuid(), nic);
        }
        return egressMap;
    }
}
