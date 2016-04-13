package ck.panda.domain.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * The AliPay real-time international payment solution enables merchants to trade in China. Customers who are registered
 * AliPay account holders can select AliPay as their payment method on the merchant web site. *
 */
@Entity
@Table(name = "payment_history")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class Payment implements Serializable {

    /** Id of the payment. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Payment transaction number. */
    @Column(name = "transaction_id")
    private String transactionId;

    /** Invoice Order id. */
    @Column(name = "order_id")
    private String orderId;

    /** Payment status. */
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    /** Payment success status. */
    @Column(name = "isSuccess")
    private String isSuccess;

    /** Payment type merchant URL. */
    @Column(name = "payment_type")
    private String paymentType;

    /** Payment service type. */
    @Column(name = "service")
    private String service;

    /** Payment trade status. */
    @Column(name = "trade_status")
    private String tradeStatus;

    /** Payment trade status. */
    @Column(name = "notify_id")
    private String notifyId;

    /** Payment amount. */
    @Column(name = "total_fee")
    private String totalFee;

    /** Payment invoice name. */
    @Column(name = "subject")
    private String subject;

    /** Payment buyer email/phone no. */
    @Column(name = "buyer_email")
    private String buyerEmail;

    /** Payment buyer id. */
    @Column(name = "buyer_id")
    private String buyerId;

    /** Payment notification time. */
    @Column(name = "notify_time")
    private String notifyTime;

    /** Payment domain. */
    @ManyToOne
    @JoinColumn(name = "domain_id", referencedColumnName = "id", updatable = false, insertable = false)
    private Domain domain;

    /** Payment domain id. */
    @Column(name = "domain_id")
    private Long domainId;

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

    /** Last modified date and time. */
    @LastModifiedDate
    @Column(name = "updated_date_time")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentZonedDateTime")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime updatedDateTime;

    /**
     * url for return page.
     */
    @Transient
    private String url;

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /** Default constructor. */
    public Payment() {
        super();
    }

    /**
     * Get the id of the payment.
     *
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id of the payment.
     *
     * @param id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the transactionId
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * @param transactionId the transactionId to set
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * @return the orderId
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * @param orderId the orderId to set
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /**
     * @return the paymentStatus
     */
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    /**
     * @param paymentStatus the paymentStatus to set
     */
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    /**
     * @return the isSuccess
     */
    public String getIsSuccess() {
        return isSuccess;
    }

    /**
     * @param isSuccess the isSuccess to set
     */
    public void setIsSuccess(String isSuccess) {
        this.isSuccess = isSuccess;
    }

    /**
     * @return the paymentType
     */
    public String getPaymentType() {
        return paymentType;
    }

    /**
     * @param paymentType the paymentType to set
     */
    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    /**
     * @return the service
     */
    public String getService() {
        return service;
    }

    /**
     * @param service the service to set
     */
    public void setService(String service) {
        this.service = service;
    }

    /**
     * @return the tradeStatus
     */
    public String getTradeStatus() {
        return tradeStatus;
    }

    /**
     * @param tradeStatus the tradeStatus to set
     */
    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    /**
     * @return the notifyId
     */
    public String getNotifyId() {
        return notifyId;
    }

    /**
     * @param notifyId the notifyId to set
     */
    public void setNotifyId(String notifyId) {
        this.notifyId = notifyId;
    }

    /**
     * @return the totalFee
     */
    public String getTotalFee() {
        return totalFee;
    }

    /**
     * @param totalFee the totalFee to set
     */
    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    /**
     * @return the buyerEmail
     */
    public String getBuyerEmail() {
        return buyerEmail;
    }

    /**
     * @param buyerEmail the buyerEmail to set
     */
    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    /**
     * @return the buyerId
     */
    public String getBuyerId() {
        return buyerId;
    }

    /**
     * @param buyerId the buyerId to set
     */
    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    /**
     * @return the notifyTime
     */
    public String getNotifyTime() {
        return notifyTime;
    }

    /**
     * @param notifyTime the notifyTime to set
     */
    public void setNotifyTime(String notifyTime) {
        this.notifyTime = notifyTime;
    }

    /**
     * Get the domain.
     *
     * @return domain
     */
    public Domain getDomain() {
        return domain;
    }

    /**
     * Set the domain.
     *
     * @param domain to set
     */
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    /**
     * Get the domain id.
     *
     * @return the domainId
     */
    public Long getDomainId() {
        return domainId;
    }

    /**
     * Set the domain id.
     *
     * @param domainId the domainId to set
     */
    public void setDomainId(Long domainId) {
        this.domainId = domainId;
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
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /** Applicable payment type list. */
    public enum PaymentStatus {
        /** Payment has not been processed. */
        PENDING,
        /** Payment is complete. */
        COMPLETED,
        /** Customer cancelled the payment. */
        ABANDONED,
        /** Payment Failure. */
        FAILURE
    }
}
