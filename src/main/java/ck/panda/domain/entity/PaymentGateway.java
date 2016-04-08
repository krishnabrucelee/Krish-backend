package ck.panda.domain.entity;

import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "paymentgateway")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class PaymentGateway {

    /** Id of the payment gateway. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Partner  */
    @Column(name = "partner")
    private String partner;

    /** Notify URL for payment. */
    @Column(name = "notify_url")
    private String notifyURL;

    /** Return URL for payment */
    @Column(name = "return_url")
    private String returnURL;

    /** Service type . */
    @Column(name = "service")
    private ServiceType serviceType;

    /** Security code. */
    @Column(name = "security_code")
    private String securityCode;

    /** Module display name. */
    @Column(name = "module_display_name")
    private String moduleDisplayName;

    /** Module display name. */
    @Column(name = "seller_email")
    private String sellerEmail;

    /** Payment gateway status. */
    @Column(name = "is_active")
    private Boolean isActive;

    /** Currency type. */
    @Column(name = "currency_type")
    private CurrencyType currencyType;

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

    /** Service types */
    public enum ServiceType {
        /** direct pay by user */
        create_direct_pay_by_user,

        /** trade create by buyer .*/
        trade_create_by_buyer,

        /** create partner by buyer */
        create_partner_trade_by_buyer,

        /** create forex trade */
        create_forex_trade

    }

    /** Types of the currencies */
    public enum CurrencyType {
        USD,
        CNY
    }
    /**
     * Get the id.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id.
     *
     * @param id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the notify URL.
     *
     * @return the notifyURL
     */
    public String getNotifyURL() {
        return notifyURL;
    }

    /**
     * Set the notify URL.
     *
     * @param notifyURL to set
     */
    public void setNotifyURL(String notifyURL) {
        this.notifyURL = notifyURL;
    }

    /**
     * Get the return URL.
     *
     * @return the returnURL
     */
    public String getReturnURL() {
        return returnURL;
    }

    /**
     * Set the return URL.
     *
     * @param returnURL to set
     */
    public void setReturnURL(String returnURL) {
        this.returnURL = returnURL;
    }


     /**
     * Get the created date and time.
     *
     * @return createdDateTime
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the created date and time.
     *
     * @param createdDateTime the DateTime to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updated date and time.
     *
     * @return updatedDateTime
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updated date and time.
     *
     * @param updatedDateTime the DateTime to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get the created user Id.
     *
     * @return the createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Get the updated user id.
     *
     * @return the updatedBy
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the created user Id.
     *
     * @param createdBy to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     *  Set the updated user id.
     *
     * @param updatedBy to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the service type.
     *
     * @return the serviceType
     */
    public ServiceType getServiceType() {
        return serviceType;
    }

    /**
     * Set the service type.
     *
     * @param serviceType  to set
     */
    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * Get the security code.
     *
     * @return the securityCode
     */
    public String getSecurityCode() {
        return securityCode;
    }

    /**
     * Set the security code.
     *
     * @param securityCode the securityCode to set
     */
    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    /**
     * Get the module display name.
     *
     * @return the moduleDisplayName
     */
    public String getModuleDisplayName() {
        return moduleDisplayName;
    }

    /**
     * Set the module display name.
     *
     * @param moduleDisplayName the moduleDisplayName to set
     */
    public void setModuleDisplayName(String moduleDisplayName) {
        this.moduleDisplayName = moduleDisplayName;
    }

    /**
     * Get the seller email.
     *
     * @return the sellerEmail
     */
    public String getSellerEmail() {
        return sellerEmail;
    }

    /**
     * Set the seller email.
     *
     * @param sellerEmail the sellerEmail to set
     */
    public void setSellerEmail(String sellerEmail) {
        this.sellerEmail = sellerEmail;
    }

    /**
     * Get the currency type.
     *
     * @return the currencyType
     */
    public CurrencyType getCurrencyType() {
        return currencyType;
    }

    /**
     * Set the currency type.
     *
     * @param currencyType the currencyType to set
     */
    public void setCurrencyType(CurrencyType currencyType) {
        this.currencyType = currencyType;
    }

    /**
     * Get the partner.
     *
     * @return the partner
     */
    public String getPartner() {
        return partner;
    }

    /**
     * Set the partner.
     *
     * @param partner the partner to set
     */
    public void setPartner(String partner) {
        this.partner = partner;
    }

	/**
	 * @return the isActive
	 */
	public Boolean getIsActive() {
		return isActive;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

}
