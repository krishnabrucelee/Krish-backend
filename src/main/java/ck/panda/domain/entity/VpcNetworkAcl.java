package ck.panda.domain.entity;

import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Get the VPC NETWORK ACL list from cloud stack server and push into the application database.
 *
 */
@Entity
@Table(name = "vpc_network_acl")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class VpcNetworkAcl {

    /** Id of the VPC NETWORK ACL. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Unique id of the VPC NETWORK ACL. */
    @Column(name = "uuid")
    private String uuid;

    /** Vpc acl of the VPC NETWORK ACL. */
    @JoinColumn(name = "vpc_acl_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private VpcAcl vpcAcl;

    /** Vpc acl id of the Volume. */
    @Column(name = "vpc_acl_id")
    private Long vpcAclId;

    /** Rule Number of the VPC NETWORK ACL. */
    @Column(name = "rule_number")
   private String ruleNumber;

    /** Cidr list of the VPC NETWORK ACL. */
    @Column(name = "cidr_list")
    private String cidrList;

    /** Action of the VPC NETWORK ACL. */
    @Column(name = "action")
    private String action;

    /** Protocol of the VPC NETWORK ACL. */
    @Column(name = "protocol")
    private String protocol;

    /** Protocol of the VPC NETWORK ACL. */
    @Column(name = "protocol_number")
    private String protocolNumber;

    /**Startport of the VPC NETWORK ACL. */
    @Column(name = "start_port")
    private String startPort;

    /** Endport of the VPC NETWORK ACL. */
    @Column(name = "end_port")
    private String endPort;

    /** Icmp type of the VPC NETWORK ACL. */
    @Column(name = "icmp_type")
    private String icmpType;

    /** Icmp code of the VPC NETWORK ACL. */
    @Column(name = "icmp_code")
    private String icmpCode;

    /** Unique id of the VPC NETWORK ACL. */
    @Column(name = "traffictype")
    private String trafficType;

    /** For display of the VPC NETWORK ACL. */
    @Column(name = "for_display")
    private Boolean forDisplay;

    /** Is Active of the VPC NETWORK ACL. */
    @Column(name = "is_active")
    private Boolean isActive;

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
     * Get the id of VpcNetworkAcl.java
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id of VpcNetworkAcl.java
     *
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the uuid of VpcNetworkAcl.java
     *
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the uuid of VpcNetworkAcl.java
     *
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get the vpcAcl of VpcNetworkAcl.java
     *
     * @return the vpcAcl
     */
    public VpcAcl getVpcAcl() {
        return vpcAcl;
    }

    /**
     * Set the vpcAcl of VpcNetworkAcl.
     *
     * @param vpcAcl the vpcAcl to set
     */
    public void setVpcAcl(VpcAcl vpcAcl) {
        this.vpcAcl = vpcAcl;
    }

    /**
     * Get the vpcAclId of VpcNetworkAcl.
     *
     * @return the vpcAclId
     */
    public Long getVpcAclId() {
        return vpcAclId;
    }

    /**
     * Set the vpcAclId of VpcNetworkAcl.
     *
     * @param vpcAclId the vpcAclId to set
     */
    public void setVpcAclId(Long vpcAclId) {
        this.vpcAclId = vpcAclId;
    }

    /**
     * Get the ruleNumber of VpcNetworkAcl.
     *
     * @return the ruleNumber
     */
    public String getRuleNumber() {
        return ruleNumber;
    }

    /**
     * Set the ruleNumber of VpcNetworkAcl.
     *
     * @param ruleNumber the ruleNumber to set
     */
    public void setRuleNumber(String ruleNumber) {
        this.ruleNumber = ruleNumber;
    }

    /**
     * Get the cidrlist of VpcNetworkAcl.
     *
     * @return the cidrlist
     */
    public String getCidrList() {
        return cidrList;
    }

    /**
     * Set the cidrlist of VpcNetworkAcl.
     *
     * @param cidrlist the cidrlist to set
     */
    public void setCidrlist(String cidrList) {
        this.cidrList = cidrList;
    }

    /**
     * Get the action of VpcNetworkAcl.
     *
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * Set the action of VpcNetworkAcl.
     *
     * @param action the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Get the protocol of VpcNetworkAcl.
     *
     * @return the protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Set the protocol of VpcNetworkAcl.
     *
     * @param protocol the protocol to set
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * Get the protocolNumber of VpcNetworkAcl.
     *
     * @return the protocolNumber
     */
    public String getProtocolNumber() {
        return protocolNumber;
    }

    /**
     * Set the protocolNumber of VpcNetworkAcl.
     *
     * @param protocolNumber the protocolNumber to set
     */
    public void setProtocolNumber(String protocolNumber) {
        this.protocolNumber = protocolNumber;
    }

    /**
     * Get the startport of VpcNetworkAcl.
     *
     * @return the startport
     */
    public String getStartPort() {
        return startPort;
    }

    /**
     * Set the startport of VpcNetworkAcl.
     *
     * @param startport the startport to set
     */
    public void setStartPort(String startPort) {
        this.startPort = startPort;
    }

    /**
     * Get the endport of VpcNetworkAcl.
     *
     * @return the endport
     */
    public String getEndPort() {
        return endPort;
    }

    /**
     * Set the endport of VpcNetworkAcl.
     *
     * @param endport the endport to set
     */
    public void setEndPort(String endPort) {
        this.endPort = endPort;
    }

    /**
     * Get the icmptype of VpcNetworkAcl.
     *
     * @return the icmptype
     */
    public String getIcmpType() {
        return icmpType;
    }

    /**
     * Set the icmptype of VpcNetworkAcl.
     *
     * @param icmptype the icmptype to set
     */
    public void setIcmpType(String icmpType) {
        this.icmpType = icmpType;
    }

    /**
     * Get the icmpcode of VpcNetworkAcl.
     *
     * @return the icmpcode
     */
    public String getIcmpCode() {
        return icmpCode;
    }

    /**
     * Set the icmpcode of VpcNetworkAcl.
     *
     * @param icmpcode the icmpcode to set
     */
    public void setIcmpcode(String icmpCode) {
        this.icmpCode = icmpCode;
    }

    /**
     * Get the traffictype of VpcNetworkAcl.
     *
     * @return the traffictype
     */
    public String getTrafficType() {
        return trafficType;
    }

    /**
     * Set the traffictype of VpcNetworkAcl.
     *
     * @param traffictype the traffictype to set
     */
    public void setTrafficType(String trafficType) {
        this.trafficType = trafficType;
    }

    /**
     * Get the fordisplay of VpcNetworkAcl.
     *
     * @return the fordisplay
     */
    public Boolean getForDisplay() {
        return forDisplay;
    }

    /**
     * Set the fordisplay of VpcNetworkAcl.
     *
     * @param fordisplay the fordisplay to set
     */
    public void setForDisplay(Boolean forDisplay) {
        this.forDisplay = forDisplay;
    }

    /**
     * Get the isActive of VpcNetworkAcl.
     *
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set the isActive of VpcNetworkAcl.
     *
     * @param isActive the isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Get the version of VpcNetworkAcl.
     *
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Set the version of VpcNetworkAcl.
     *
     * @param version the version to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get the createdBy of VpcNetworkAcl.
     *
     * @return the createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the createdBy of VpcNetworkAcl.
     *
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updatedBy of VpcNetworkAcl.
     *
     * @return the updatedBy
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updatedBy of VpcNetworkAcl.
     *
     * @param updatedBy the updatedBy to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the createdDateTime of VpcNetworkAcl.
     *
     * @return the createdDateTime
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the createdDateTime of VpcNetworkAcl.
     *
     * @param createdDateTime the createdDateTime to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updatedDateTime of VpcNetworkAcl.
     *
     * @return the updatedDateTime
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updatedDateTime of VpcNetworkAcl.
     *
     * @param updatedDateTime the updatedDateTime to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }
}
