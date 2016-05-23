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
import ck.panda.util.JsonUtil;

@Entity
@Table(name = "vpn_customer_gateway")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class VPNCustomerGateway implements Serializable {

     /** Constant for ike life time. */
    public static final String CS_IKE_LIFETIME = "ikelifetime";

    /** Constant for esp life time. */
    public static final String CS_ESP_LIFETIME = "esplifetime";

    /** Constant for ike policy. */
    public static final String CS_IKE_POLICY = "ikepolicy";

    /** Constant for esp policy. */
    public static final String CS_ESP_POLICY = "esppolicy";

    /** Constant for peer detection. */
    public static final String CS_DPD = "dpd";

    /** Constant for cidr list. */
    public static final String CS_CIDR_LIST = "cidrlist";

    /** Constant for ip secondary secret key. */
    public static final String CS_IPSEC_SHARED =  "ipsecpsk";


    /** Id of the vpn customer gateway. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Unique id for the vpn customer gateway. */
    @Column(name = "uuid")
    private String uuid;

    /** Name of the vpn customer gateway. */
    @Column(name = "name")
    private String name;

    /** CIDR Range of the vpn customer gateway. */
    @Column(name = "cidr")
    private String cIDR;

    /** Gateway of the vpn. */
    @Column(name = "gateway")
    private String gateway;

    /** Gateway of the vpn. */
    @Column(name = "dpd")
    private String deadPeerDetection;

    /**  Internet Key Exchange life time of the vpn customer gateway. */
    @Column(name = "ike_life_time")
    private String IKELifeTime;

    /** Encapsulating Security Payload life time of the vpn customer gateway. */
    @Column(name = "esp_life_time")
    private String ESPLifeTime;

    /** Gateway of the vpn. */
    @Transient
    private String espEncryption;

    /** Gateway of the vpn. */
    @Transient
    private String espHash;

    /** Gateway of the vpn. */
    @Column(name = "esp_policy")
    private String espPolicy;

    /** Ike encryption of the vpn customer gateway. */
    @Transient
    private String ikeEncryption;

    /** Ike hash of the vpn customer gateway. */
    @Transient
    private String ikeHash;

    /** ike policy of the vpn customer gateway. */
    @Column(name = "ike_policy")
    private String ikePolicy;

    /** Gateway of the vpn. */
    @Column(name = "ipsec_preshared_key")
    private String ipsecPresharedKey;

    /** Gateway of the vpn. */
    @Column(name = "force_encap")
    private Boolean forceEncapsulation;

    /** Domain Object for the vpn customer gateway. */
    @JoinColumn(name = "domain_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Domain domain;

    /** Id for the vpn customer gateway. */
    @Column(name = "domain_id")
    private Long domainId;

    /** Project Object vpn customer gateway. */
    @JoinColumn(name = "project_id", referencedColumnName = "id", updatable = false, insertable = false)
    @ManyToOne
    private Project project;

    /** Vpn customer gateway vpn customer gateway. */
    @Column(name = "project_id")
    private Long projectId;

    /** Department Object of the vpn customer gateway. */
    @JoinColumn(name = "department_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Department department;

    /** Id for the Department of the vpn customer gateway. */
    @Column(name = "department_id")
    private Long departmentId;

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

    /** Transient department of the network. */
    @Transient
    private String transDepartmentId;

    /** Transient project of the network. */
    @Transient
    private String transProjectId;

    /** Set syncFlag. */
    @Transient
    private Boolean syncFlag;

    /** IsActive attribute to verify Active or Inactive. */
    @Column(name = "is_active")
    private Boolean isActive;

    /** Version attribute to handle optimistic locking. */
    @Version
    @Column(name = "version")
    private Long version;

    /**
     * Get the vpn customer gateway Id.
     *
     * @return the vpn customer gateway Id
     */
    public Long getId() {
        return id;
    }

    /**
     * Get the vpn customer gateway uuid.
     *
     * @return the uuid of the vpn customer gateway
     */
    public String getUuid() {
        return uuid;
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
     * Get the vpn customer gateway Name.
     *
     * @return the name of the vpn customer gateway
     */
    public String getName() {
        return name;
    }

    /**
     * Get the vpn customer gateway State.
     *
     * @return Active or Inactive state
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Get the vpn customer gateway Version.
     *
     * @return the version of vpn customer gateway
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Get the user who creates vpn customer gateway.
     *
     * @return the createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Get the user who updates vpn customer gateway.
     *
     * @return the updatedBy
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Get the DateTime of created vpn customer gateway.
     *
     * @return the DateTime
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Get the DateTime of updated vpn customer gateway.
     *
     * @return the DateTime
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the vpn customer gateway Id.
     *
     * @param id vpn customer gateway id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Set the vpn customer gateway uuid.
     *
     * @param uuid vpn customer gateway uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Set the vpn customer gateway name.
     *
     * @param name vpn customer gateway name to set
     */
    public void setName(String name) {
        this.name = name;
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
     * Get the vpn customer gateway cIDR.
     *
     * @return the cIDR
     */
    public String getcIDR() {
        return cIDR;
    }

    /**
     * Set the vpn customer gateway cIDR.
     *
     * @param cIDR the cIDR to set
     */
    public void setcIDR(String cIDR) {
        this.cIDR = cIDR;
    }


    /**
     * Set the vpn customer gateway State.
     *
     * @param isActive the isActive state to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Set the vpn customer gateway Version.
     *
     * @param version the version to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Set the user who creates vpn customer gateway.
     *
     * @param createdBy vpn customer gateway createdBy to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Set the user who updates vpn customer gateway.
     *
     * @param updatedBy vpn customer gateway updatedBy to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Set the Created DateTime for vpn customer gateway.
     *
     * @param createdDateTime vpn customer gateway createdDateTime to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Set the Updated DateTime for vpn customer gateway.
     *
     * @param updatedDateTime vpn customer gateway updatedDateTime to set
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
     * @param domain  to set
     */
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    /**
     * Get the vpn customer gateway Gateway.
     *
     * @return the gateway
     */
    public String getGateway() {
        return gateway;
    }

    /**
     * Set the vpn customer gateway Gateway.
     *
     * @param gateway to set
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
     * @param department  to set
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
     * @param departmentId  to set
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
     * @param transDomainId  to set
     */
    public void setTransDomainId(String transDomainId) {
        this.transDomainId = transDomainId;
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
     * @param transDepartmentId  to set
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
     * @return the domainId
     */
    public Long getDomainId() {
        return domainId;
    }

    /**
     * Get the dead peer detection.
     *
     * @return the deadPeerDetection
     */
    public String getDeadPeerDetection() {
        return deadPeerDetection;
    }

    /**
     * Set the dead peer detection.
     *
     * @param deadPeerDetection  to set
     */
    public void setDeadPeerDetection(String deadPeerDetection) {
        this.deadPeerDetection = deadPeerDetection;
    }

    /**
     * Get the ike life time.
     *
     * @return the iKELifeTime
     */
    public String getIKELifeTime() {
        return IKELifeTime;
    }

    /**
     * Set the ike life time.
     *
     * @param iKELifeTime  to set
     */
    public void setIKELifeTime(String iKELifeTime) {
        IKELifeTime = iKELifeTime;
    }

    /**
     * Get the esp life time.
     *
     * @return the eSPLifeTime
     */
    public String getESPLifeTime() {
        return ESPLifeTime;
    }

    /**
     * Set the esp life time.
     *
     * @param eSPLifeTime to set
     */
    public void setESPLifeTime(String eSPLifeTime) {
        ESPLifeTime = eSPLifeTime;
    }

    /**
     * Get the esp encrption.
     *
     * @return the espEncryption
     */
    public String getEspEncryption() {
        return espEncryption;
    }

    /**
     * Set the esp encrption.
     *
     * @param espEncryption  to set
     */
    public void setEspEncryption(String espEncryption) {
        this.espEncryption = espEncryption;
    }

    /**
     * Get esp hash.
     *
     * @return the espHash
     */
    public String getEspHash() {
        return espHash;
    }

    /**
     * Set esp hash.
     *
     * @param espHash to set
     */
    public void setEspHash(String espHash) {
        this.espHash = espHash;
    }

    /**
     * Get the esp policy.
     *
     * @return the espPolicy
     */
    public String getEspPolicy() {
        return espPolicy;
    }

    /**
     * Set the esp policy.
     *
     * @param espPolicy the espPolicy to set
     */
    public void setEspPolicy(String espPolicy) {
        this.espPolicy = espPolicy;
    }

    /**
     * Get the ike encrption.
     *
     * @return the ikeEncryption
     */
    public String getIkeEncryption() {
        return ikeEncryption;
    }

    /**
     * Set the ike encrption.
     *
     * @param ikeEncryption to set
     */
    public void setIkeEncryption(String ikeEncryption) {
        this.ikeEncryption = ikeEncryption;
    }

    /**
     * Get the ike hash.
     *
     * @return the ikeHash
     */
    public String getIkeHash() {
        return ikeHash;
    }

    /**
     * Set the ike hash.
     *
     * @param ikeHash to set
     */
    public void setIkeHash(String ikeHash) {
        this.ikeHash = ikeHash;
    }

    /**
     * Get the ike policy.
     *
     * @return the ikePolicy
     */
    public String getIkePolicy() {
        return ikePolicy;
    }

    /**
     *  Set the ike policy.
     *
     * @param ikePolicy  to set
     */
    public void setIkePolicy(String ikePolicy) {
        this.ikePolicy = ikePolicy;
    }

    /**
     * Get ip secondary pre shared key.
     *
     * @return the ipsecPresharedKey
     */
    public String getIpsecPresharedKey() {
        return ipsecPresharedKey;
    }

    /**
     * Set ip secondary pre shared key.
     *
     * @param ipsecPresharedKey  to set
     */
    public void setIpsecPresharedKey(String ipsecPresharedKey) {
        this.ipsecPresharedKey = ipsecPresharedKey;
    }

    /**
     * Get the force encapsulation.
     *
     * @return the forceEncapsulation
     */
    public Boolean getForceEncapsulation() {
        return forceEncapsulation;
    }

    /**
     * Set the force encapsulation.
     *
     * @param forceEncapsulation  to set
     */
    public void setForceEncapsulation(Boolean forceEncapsulation) {
        this.forceEncapsulation = forceEncapsulation;
    }

    /**
     * Convert JSONObject into VPN Customer Gateway object.
     *
     * @param jsonObject JSON object.
     * @return VPN Customer Gateway object.
     */
    public static VPNCustomerGateway convert(JSONObject jsonObject) {
        VPNCustomerGateway gateway = new VPNCustomerGateway();
        try {
            gateway.setName(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_NAME));
            gateway.setUuid(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_ID));
            gateway.setGateway(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_GATEWAY));
            gateway.setcIDR(JsonUtil.getStringValue(jsonObject, CS_CIDR_LIST));
            gateway.setIsActive(true);
            gateway.setTransDomainId((JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_DOMAIN_ID)));
            gateway.setTransDepartmentId(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_ACCOUNT));
            gateway.setTransProjectId(JsonUtil.getStringValue(jsonObject, CloudStackConstants.CS_PROJECT_ID));
            gateway.setIpsecPresharedKey(JsonUtil.getStringValue(jsonObject, CS_IPSEC_SHARED));
            gateway.setIKELifeTime(JsonUtil.getStringValue(jsonObject,CS_IKE_LIFETIME));
            gateway.setESPLifeTime(JsonUtil.getStringValue(jsonObject,CS_ESP_LIFETIME));
            gateway.setDeadPeerDetection(JsonUtil.getStringValue(jsonObject,CS_DPD));
            gateway.setSyncFlag(true);
            gateway.setIkePolicy(JsonUtil.getStringValue(jsonObject,CS_IKE_POLICY));
            gateway.setEspPolicy(JsonUtil.getStringValue(jsonObject,CS_ESP_POLICY));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return gateway;
    }

    /**
     * Mapping entity object into list.
     *
     * @param gatewayList list of vpn customer gateway.
     * @return gateway map
     */
    public static Map<String, VPNCustomerGateway> convert(List<VPNCustomerGateway> gatewayList) {
        Map<String, VPNCustomerGateway> gatewayMap = new HashMap<String, VPNCustomerGateway>();

        for (VPNCustomerGateway gateway : gatewayList) {
            gatewayMap.put(gateway.getUuid(), gateway);
        }
        return gatewayMap;
    }

}
