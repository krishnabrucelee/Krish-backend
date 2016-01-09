/**
 *
 */
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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

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

    /** Starting port of the firewall rule. */
    @Column(name = "dest_port_start")
    private Integer destPortStart;

    /** Ending port of the firewall rule. */
    @Column(name = "dest_port_end")
    private Integer destPortEnd;

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

     * @return the uuid of PortForwarding.
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
     * Get the destPortStart of the PortForwarding.

     * @return the destination port start of PortForwarding.
     */
    public Integer getDestPortStart() {
        return destPortStart;
    }

    /**
     * Set the destPortStart of the PortForwarding.
     *
     * @param destPortstart the destination port start to set
     */
    public void setDestPortStart(Integer destPortStart) {
        this.destPortStart = destPortStart;
    }

    /**
     * Get the destPortEnd of the PortForwarding.

     * @return the destination port end of PortForwarding.
     */
    public Integer getDestPortEnd() {
        return destPortEnd;
    }

    /**
     * Set the destPortEnd of the PortForwarding.
     *
     * @param destPortEnd the destination port end to set
     */
    public void setDestPortEnd(Integer destPortEnd) {
        this.destPortEnd = destPortEnd;
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

}
