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
import javax.persistence.Version;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import ck.panda.util.JsonUtil;

/**
 * SSH Keys are used for authentication,In addition to the username and password authentication. with SSH Key, an
 * instance can be created and also multiple instances can be managed.
 */
@Entity
@Table(name = "sshkeys")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class SSHKey implements Serializable {

    /** Status enum type used to list the status values. */
    public enum Status {
        /** SSH Key status as Enabled. */
        ENABLED,
        /** SSH Key status as Disabled. */
        DISABLED
    }

    /** Id of the SSH key. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Name of the Keypair. */
    @NotEmpty
    @Size(min = 4, max = 20)
    @Column(name = "name", nullable = false)
    private String name;

    /** Fingerprint of the public key. */
    @Column(name = "finger_print")
    private String fingerPrint;

    /** Private key. */
    @Transient
    private String privatekey;

    /** public Key. */
    @Column(name = "public_key")
    private String publicKey;

    /** Domain of the SSH Key. */
    @JoinColumn(name = "domain_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Domain domain;

    /** Domain id of the SSH Key. */
    @Column(name = "domain_id")
    private Long domainId;

    /** Department of the SSH Key. */
    @OneToOne
    @JoinColumn(name = "department_id", referencedColumnName = "id", updatable = false, insertable = false)
    private Department department;

    /** Department id of the SSH key. */
    @Column(name = "department_id")
    private Long departmentId;

    /** Update status when delete an entity. */
    @Column(name = "is_active")
    private Boolean isActive;

    /** Temporary variable. */
    @Transient
    private Boolean isSyncFlag;

    /** SSH Key current state. */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    /** Version attribute to handle optimistic locking. */
    @Version
    @Column(name = "version")
    private Long version;

    /** Created by user. */
    @CreatedBy
    @Column(name = "created_by")
    private Long createdBy;

    /** Last updated by user. */
    @LastModifiedBy
    @Column(name = "updated_by")
    private Long updatedBy;

    /** Transient department of the user. */
    @Transient
    private String transDepartment;

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

    /** Default constructor. */
    public SSHKey() {
        super();
    }

    /**
     * Get the id of the SSH key.
     *
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id of the SSH key.
     *
     * @param id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the name of the keypair.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the keypair.
     *
     * @param name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the fingerprint of the public key.
     *
     * @return fingerPrint
     */
    public String getFingerPrint() {
        return fingerPrint;
    }

    /**
     * Set the fingerprint of the public key.
     *
     * @param fingerPrint to set
     */
    public void setFingerPrint(String fingerPrint) {
        this.fingerPrint = fingerPrint;
    }

    /**
     * Get the private key.
     *
     * @return the privatekey
     */
    public String getPrivatekey() {
        return privatekey;
    }

    /**
     * Set the private key.
     *
     * @param privatekey the privatekey to set
     */
    public void setPrivatekey(String privatekey) {
        this.privatekey = privatekey;
    }

    /**
     * Get the public key.
     *
     * @return the publicKey
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * Set the public key.
     *
     * @param publicKey the publicKey to set
     */
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    /**
     * Get domain for SSH key.
     *
     * @return the domain
     */
    public Domain getDomain() {
        return domain;
    }

    /**
     * Set domain for SSH Key.
     *
     * @param domain domain to set.
     */
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    /**
     * Get the domainId.
     *
     * @return domainId
     */
    public Long getDomainId() {
        return domainId;
    }

    /**
     * Set the domainId.
     *
     * @param domainId to set
     */
    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    /**
     * Get the department.
     *
     * @return department
     */
    public Department getDepartment() {
        return department;
    }

    /**
     * Set the department.
     *
     * @param department to set
     *
     */
    public void setDepartment(Department department) {
        this.department = department;
    }

    /**
     * Get the department id.
     *
     * @return the departmentId
     */
    public Long getDepartmentId() {
        return departmentId;
    }

    /**
     * Set the department id.
     *
     * @param departmentId to set
     *
     */
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * Get the active status.
     *
     * @return isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set the active status.
     *
     * @param isActive to set
     *
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Get the Sync Flag.
     *
     * @return the isSyncFlag.
     */
    public Boolean getIsSyncFlag() {
        return isSyncFlag;
    }

    /**
     * Set the Sync Flag.
     *
     * @param isSyncFlag - the isSyncFlag to set.
     */
    public void setIsSyncFlag(Boolean isSyncFlag) {
        this.isSyncFlag = isSyncFlag;
    }

    /**
     * Get the status of the application.
     *
     * @return status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set the status of the application.
     *
     * @param status to set
     *
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Get the version.
     *
     * @return version
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
     * Get the created user.
     *
     * @return createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the created user.
     *
     * @param createdBy to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updated user.
     *
     * @return updatedBy
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updated user.
     *
     * @param updatedBy to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the transient Department.
     *
     * @return the transDepartment
     */
    public String getTransDepartment() {
        return transDepartment;
    }

    /**
     * Set the transDepartment.
     *
     * @param transDepartment to set
     */
    public void setTransDepartment(String transDepartment) {
        this.transDepartment = transDepartment;
    }

    /**
     * Get transient Domain Id.
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
     * Get the created date time.
     *
     * @return createdDateTime
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
     *
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Convert JSONObject into user object.
     *
     * @param jsonObject JSON object.
     * @return user object.
     * @throws Exception error occurs.
     */
    public static SSHKey convert(JSONObject jsonObject) throws Exception {
        SSHKey sshkey = new SSHKey();
        sshkey.setIsSyncFlag(false);
        sshkey.setName(JsonUtil.getStringValue(jsonObject, "name"));
        sshkey.setFingerPrint(JsonUtil.getStringValue(jsonObject, "fingerprint"));
        sshkey.setTransDepartment(JsonUtil.getStringValue(jsonObject, "account"));
        sshkey.setTransDomainId(JsonUtil.getStringValue(jsonObject, "domainid"));
        sshkey.setIsActive(true);
        return sshkey;
    }

    /**
     * Mapping entity object into list.
     *
     * @param sshkeyList list of SSH keys.
     * @return SSh key map
     */
    public static Map<String, SSHKey> convert(List<SSHKey> sshkeyList) {
        Map<String, SSHKey> sshkeyMap = new HashMap<String, SSHKey>();

        for (SSHKey sshkey : sshkeyList) {
            sshkeyMap.put(sshkey.getFingerPrint(), sshkey);
        }
        return sshkeyMap;
    }

}
