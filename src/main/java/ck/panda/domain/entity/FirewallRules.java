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

/**
 * The Firewall controlls the traffic originates from the guest network and sent to public network. This features
 * controls the Egress (outgoing) traffic from the guest network in Advanced zone. The egress firewall rules applied
 * will restrict the traffic from guest network on the Virtual Router. *
 */
@Entity
@Table(name = "firewall_rules")
@EntityListeners(AuditingEntityListener.class)
public class FirewallRules {

    /** Unique Id of the Firewall Rule. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Cloudstack's Firewall Rule uuid. */
    @Column(name = "uuid")
    private String uuid;

    /** Network for Firewall Rule. */
    @JoinColumn(name = "network_id", referencedColumnName = "id", updatable = false, insertable = false)
    @OneToOne
    private Network network;

    /** Network id for Firewall Rule. */
    @Column(name = "network_id")
    private Long networkId;

    /** Domain of the Firewall Rule. */
    @JoinColumn(name = "domain_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Domain domain;

    /** Domain id of the Firewall Rule. */
    @Column(name = "domain_id")
    private Long domainId;

    /** Project of the Firewall Rule. */
    @JoinColumn(name = "project_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Project project;

    /** Project id of the Firewall Rule. */
    @Column(name = "project_id")
    private Long projectId;

    /** Department of the Firewall Rule. */
    @JoinColumn(name = "department_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Department department;

    /** Department id of the Firewall Rule. */
    @Column(name = "department_id")
    private Long departmentId;

    /** ipAddress of the Firewall Rule. */
    @JoinColumn(name = "ipaddress_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private IpAddress ipAddress;

    /** ipAddress id of the Firewall Rule. */
    @Column(name = "ipaddress_id")
    private Long ipAddressId;

    /** Starting port of the firewall rule. */
    @Column(name = "start_port")
    private Integer startPort;

    /** Ending port of the firewall rule. */
    @Column(name = "end_port")
    private Integer endPort;

    /** Error code for ICMP message. */
    @Column(name = "icmp_code")
    private Integer icmpCode;

    /** Type of the ICMP message. */
    @Column(name = "icmp_message")
    private Integer icmpMessage;

    /** VPC for a network. */
    @Column(name = "vpc")
    private String vpc;

    /** The source cidr list to forward traffic . */
    @Column(name = "source_cidr")
    private String sourceCIDR;

    /** Is the rule for display to the regular user. */
    @Column(name = "display")
    private Boolean display;

    /** Rule state . */
    @Column(name = "state")
    private State state;

    /** Set of rules or protocols for an IP address . */
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

    /** Is this firewall rule is active. */
    @Column(name = "is_Active")
    private Boolean isActive;

    /** Temporary variable. */
    @Transient
    private Boolean syncFlag;

    /** Transient network of the instance. */
    @Transient
    private String transNetworkId;

    /** Transient ipaddress of the network. */
    @Transient
    private String transIpaddressId;

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

    /** Types of protocol for an IP Address . */
    public enum Protocol {

        /** TCP enables two hosts to establish a connection and exchange streams of data. */
        TCP,

        /**
         * User Datagram Protocol (UDP) is a transport layer protocol provides a best-effort datagram service to an End
         * System (IP host).
         */
        UDP,

        /**
         * It is used to send error message when requested service is not available or that a host or router could not
         * be reached.
         */
        ICMP,

        /** All the above three protocols . */
        ALL
    }

    public enum State {
        /** Egress rule in Active state */
        ACTIVE,

        /** Egress rule in Staged state. */
        STAGED,
        /** Egress rule in ADD state. */
        ADD
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
     * Get the department of the Firewall Rule.
     *
     * @return the department
     */
    public Department getDepartment() {
        return department;
    }

    /**
     * Set the department of the Firewall Rule.
     *
     * @param department to set
     */
    public void setDepartment(Department department) {
        this.department = department;
    }

    /**
     * Get the departmentId of the Firewall Rule.
     *
     * @return the departmentId
     */
    public Long getDepartmentId() {
        return departmentId;
    }

    /**
     * Set the departmentId of the Firewall Rule.
     *
     * @param departmentId to set
     */
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * Get the startport.
     *
     * @return the startPort
     */
    public Integer getStartPort() {
        return startPort;
    }

    /**
     * Set the startPort .
     *
     * @param startPort to set
     */
    public void setStartPort(Integer startPort) {
        this.startPort = startPort;
    }

    /**
     * Get the endPort.
     *
     * @return the endPort
     */
    public Integer getEndPort() {
        return endPort;
    }

    /**
     * Set the endPort.
     *
     * @param endPort t to set
     */
    public void setEndPort(Integer endPort) {
        this.endPort = endPort;
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
     * Get the project .
     *
     * @return the project
     */
    public Project getProject() {
        return project;
    }

    /**
     * Set the project.
     *
     * @param project to set
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * Get the projectId .
     *
     * @return the projectId
     */
    public Long getProjectId() {
        return projectId;
    }

    /**
     * Set the projectId .
     *
     * @param projectId to set
     */
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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
     * Get the transient ipaddress uuid.
     *
     * @param transIpaddressId to set
     */
    public String getTransIpaddressId() {
        return transIpaddressId;
    }

    /**
     * Set the transient ipaddress uuid.
     *
     * @param transIpaddressId to set
     */
    public void setTransIpaddressId(String transIpaddressId) {
        this.transIpaddressId = transIpaddressId;
    }

    /**
     * Convert JSONObject to nic entity.
     *
     * @param jsonObject json object
     * @return nic entity object.
     * @throws Exception unhandled errors.
     */
    public static FirewallRules convert(JSONObject jsonObject, FirewallRules.TrafficType type) throws Exception {
        FirewallRules egress = new FirewallRules();
        egress.setSyncFlag(false);
        egress.setUuid(JsonUtil.getStringValue(jsonObject, "id"));
        egress.setTransIpaddressId(JsonUtil.getStringValue(jsonObject, "ipaddressid"));
        egress.setProtocol(Protocol.valueOf(JsonUtil.getStringValue(jsonObject, "protocol").toUpperCase()));
        egress.setDisplay(JsonUtil.getBooleanValue(jsonObject, "fordisplay"));
        egress.setSourceCIDR(JsonUtil.getStringValue(jsonObject, "cidrlist"));
        egress.setTransNetworkId((JsonUtil.getStringValue(jsonObject, "networkid")));
        egress.setState(State.valueOf(JsonUtil.getStringValue(jsonObject, "state").toUpperCase()));
        egress.setStartPort(JsonUtil.getIntegerValue(jsonObject, "startport"));
        egress.setEndPort(JsonUtil.getIntegerValue(jsonObject, "endport"));
        egress.setIcmpCode(JsonUtil.getIntegerValue(jsonObject, "icmpcode"));
        egress.setIcmpMessage(JsonUtil.getIntegerValue(jsonObject, "icmptype"));
        egress.setPurpose(Purpose.FIREWALL);
        egress.setTrafficType(type);
        egress.setIsActive(true);
        return egress;
    }

    /**
     * Mapping entity object into list.
     *
     * @param egressList list of egress.
     * @return egressMap egress.
     */
    public static Map<String, FirewallRules> convert(List<FirewallRules> firewallList) {
        Map<String, FirewallRules> egressMap = new HashMap<String, FirewallRules>();

        for (FirewallRules firewall : firewallList) {
            egressMap.put(firewall.getUuid(), firewall);
        }
        return egressMap;
    }
}
