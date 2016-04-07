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
import javax.persistence.Version;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * The AliPay real-time international payment solution enables merchants to trade in China.
 * Customers who are registered AliPay account holders can select AliPay as their payment method on the merchant web site
 *
 */
@Entity
@Table(name = "payment_Transaction")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class Payment implements Serializable {

    /** Id of the payment. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Payment reply reason code status. */
    @Column(name = "ap_check_status_reply_reason_code")
    private Integer apCheckStatusReply_reasonCode;

    /** Payment reply reconciliation id status. */
    @Column(name = "ap_check_status_reply_reconciliation_id")
    private String apCheckStatusReply_reconciliationID;

    /** Reply payment status. */
    @Column(name = "ap_check_status_reply_payment_status")
    private PaymentStatus apCheckStatusReply_paymentStatus;

    /** Payment reply transaction id. */
    @Column(name = "ap_check_status_reply_processor_transaction_id")
    private String apCheckStatusReply_processorTransactionID;

    /** Payment initial merchant URL. */
    @Column(name = "ap_initiate_reply_merchant_url")
    private String apInitiateReply_merchantURL;

    /** Payment initial reason code. */
    @Column(name = "ap_initiate_reply_reason_code")
    private Integer apInitiateReply_reasonCode;

    /** Payment initial reconciliation id. */
    @Column(name = "ap_initiate_reply_reconciliation_id")
    private String apInitiateReply_reconciliationID;

    /** Payment decision. */
    @Column(name = "decision")
    private String decision;

    /** Payment merchant reference code. */
    @Column(name = "merchant_reference_code")
    private String merchantReferenceCode;

    /** Payment reason code. */
    @Column(name = "reason_code")
    private Integer reasonCode;

    /** Payment request id. */
    @Column(name = "request_id")
    private String request_id;

    /** Payment total currency. */
    @Column(name = "purchase_totals_currency")
    private String purchaseTotals_currency;

    /** Payment refund return reference. */
    @Column(name = "ap_refund_reply_return_ref")
    private String apRefundReply_returnRef;

    /** Payment refund reason code. */
    @Column(name = "ap_refund_reply_reason_code")
    private Integer apRefundReply_reasonCode;

    /** Payment refund reconciliation id. */
    @Column(name = "ap_refund_reply_reconciliation_id")
    private String apRefundReply_reconciliationID;

    /** Payment refund date time. */
    @Column(name = "ap_refund_reply_date_time")
    private String apRefundReply_dateTime;

    /** Payment refund amount. */
    @Column(name = "ap_refund_reply_amount")
    private Double apRefundReply_amount;

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
     * Get the payment reply reason code status.
     *
     * @return the apCheckStatusReply_reasonCode
     */
    public Integer getApCheckStatusReply_reasonCode() {
        return apCheckStatusReply_reasonCode;
    }

    /**
     * Set the payment reply reason code status.
     *
     * @param apCheckStatusReply_reasonCode the apCheckStatusReply_reasonCode to set
     */
    public void setApCheckStatusReply_reasonCode(Integer apCheckStatusReply_reasonCode) {
        this.apCheckStatusReply_reasonCode = apCheckStatusReply_reasonCode;
    }

    /**
     * Get the payment reply reconciliation id status.
     *
     * @return the apCheckStatusReply_reconciliationID
     */
    public String getApCheckStatusReply_reconciliationID() {
        return apCheckStatusReply_reconciliationID;
    }

    /**
     * Set the payment reply reconciliation id status.
     *
     * @param apCheckStatusReply_reconciliationID the apCheckStatusReply_reconciliationID to set
     */
    public void setApCheckStatusReply_reconciliationID(String apCheckStatusReply_reconciliationID) {
        this.apCheckStatusReply_reconciliationID = apCheckStatusReply_reconciliationID;
    }

    /**
     * Get the Reply payment status.
     *
     * @return the apCheckStatusReply_paymentStatus
     */
    public PaymentStatus getApCheckStatusReply_paymentStatus() {
        return apCheckStatusReply_paymentStatus;
    }

    /**
     * Set the Reply payment status.
     *
     * @param apCheckStatusReply_paymentStatus the apCheckStatusReply_paymentStatus to set
     */
    public void setApCheckStatusReply_paymentStatus(PaymentStatus apCheckStatusReply_paymentStatus) {
        this.apCheckStatusReply_paymentStatus = apCheckStatusReply_paymentStatus;
    }

    /**
     * Get the payment reply transaction id.
     *
     * @return the apCheckStatusReply_processorTransactionID
     */
    public String getApCheckStatusReply_processorTransactionID() {
        return apCheckStatusReply_processorTransactionID;
    }

    /**
     * Set the payment reply transaction id.
     *
     * @param apCheckStatusReply_processorTransactionID the apCheckStatusReply_processorTransactionID to set
     */
    public void setApCheckStatusReply_processorTransactionID(String apCheckStatusReply_processorTransactionID) {
        this.apCheckStatusReply_processorTransactionID = apCheckStatusReply_processorTransactionID;
    }

    /**
     * Get the payment initial merchant URL.
     *
     * @return the apInitiateReply_merchantURL
     */
    public String getApInitiateReply_merchantURL() {
        return apInitiateReply_merchantURL;
    }

    /**
     * Set the payment initial merchant URL.
     *
     * @param apInitiateReply_merchantURL the apInitiateReply_merchantURL to set
     */
    public void setApInitiateReply_merchantURL(String apInitiateReply_merchantURL) {
        this.apInitiateReply_merchantURL = apInitiateReply_merchantURL;
    }

    /**
     * Get the Payment initial reason code.
     *
     * @return the apInitiateReply_reasonCode
     */
    public Integer getApInitiateReply_reasonCode() {
        return apInitiateReply_reasonCode;
    }

    /**
     * Set the Payment initial reason code.
     *
     * @param apInitiateReply_reasonCode the apInitiateReply_reasonCode to set
     */
    public void setApInitiateReply_reasonCode(Integer apInitiateReply_reasonCode) {
        this.apInitiateReply_reasonCode = apInitiateReply_reasonCode;
    }

    /**
     * Get the payment initial reconciliation id.
     *
     * @return the apInitiateReply_reconciliationID
     */
    public String getApInitiateReply_reconciliationID() {
        return apInitiateReply_reconciliationID;
    }

    /**
     * Set the payment initial reconciliation id.
     *
     * @param apInitiateReply_reconciliationID the apInitiateReply_reconciliationID to set
     */
    public void setApInitiateReply_reconciliationID(String apInitiateReply_reconciliationID) {
        this.apInitiateReply_reconciliationID = apInitiateReply_reconciliationID;
    }

    /**
     * Get the payment decision.
     * @return the decision
     */
    public String getDecision() {
        return decision;
    }

    /**
     * Set the payment decision.
     *
     * @param decision the decision to set
     */
    public void setDecision(String decision) {
        this.decision = decision;
    }

    /**
     * Get the payment merchant reference code.
     * @return the merchantReferenceCode
     */
    public String getMerchantReferenceCode() {
        return merchantReferenceCode;
    }

    /**
     * Set the payment merchant reference code.
     *
     * @param merchantReferenceCode the merchantReferenceCode to set
     */
    public void setMerchantReferenceCode(String merchantReferenceCode) {
        this.merchantReferenceCode = merchantReferenceCode;
    }

    /**
     * Get the payment reason code.
     *
     * @return the reasonCode
     */
    public Integer getReasonCode() {
        return reasonCode;
    }

    /**
     * Set the payment reason code.
     *
     * @param reasonCode the reasonCode to set
     */
    public void setReasonCode(Integer reasonCode) {
        this.reasonCode = reasonCode;
    }

    /**
     * Get the payment request id.
     *
     * @return the request_id
     */
    public String getRequest_id() {
        return request_id;
    }

    /**
     * Set the payment request id.
     *
     * @param request_id the request_id to set
     */
    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    /**
     * Get the payment total currency.
     *
     * @return the purchaseTotals_currency
     */
    public String getPurchaseTotals_currency() {
        return purchaseTotals_currency;
    }

    /**
     * Set the payment total currency.
     *
     * @param purchaseTotals_currency the purchaseTotals_currency to set
     */
    public void setPurchaseTotals_currency(String purchaseTotals_currency) {
        this.purchaseTotals_currency = purchaseTotals_currency;
    }

    /**
     * Get the payment refund return reference.
     *
     * @return the apRefundReply_returnRef
     */
    public String getApRefundReply_returnRef() {
        return apRefundReply_returnRef;
    }

    /**
     * Set the payment refund return reference.
     *
     * @param apRefundReply_returnRef the apRefundReply_returnRef to set
     */
    public void setApRefundReply_returnRef(String apRefundReply_returnRef) {
        this.apRefundReply_returnRef = apRefundReply_returnRef;
    }

    /**
     * Get the payment refund reason code.
     *
     * @return the apRefundReply_reasonCode
     */
    public Integer getApRefundReply_reasonCode() {
        return apRefundReply_reasonCode;
    }

    /**
     * Set the payment refund reason code.
     *
     * @param apRefundReply_reasonCode the apRefundReply_reasonCode to set
     */
    public void setApRefundReply_reasonCode(Integer apRefundReply_reasonCode) {
        this.apRefundReply_reasonCode = apRefundReply_reasonCode;
    }

    /**
     * Get the payment refund reconciliation id.
     *
     * @return the apRefundReply_reconciliationID
     */
    public String getApRefundReply_reconciliationID() {
        return apRefundReply_reconciliationID;
    }

    /**
     * Set the payment refund reconciliation id.
     *
     * @param apRefundReply_reconciliationID the apRefundReply_reconciliationID to set
     */
    public void setApRefundReply_reconciliationID(String apRefundReply_reconciliationID) {
        this.apRefundReply_reconciliationID = apRefundReply_reconciliationID;
    }

    /**
     * Get the payment refund date time.
     * @return the apRefundReply_dateTime
     */
    public String getApRefundReply_dateTime() {
        return apRefundReply_dateTime;
    }

    /**
     * Set the payment refund date time.
     *
     * @param apRefundReply_dateTime the apRefundReply_dateTime to set
     */
    public void setApRefundReply_dateTime(String apRefundReply_dateTime) {
        this.apRefundReply_dateTime = apRefundReply_dateTime;
    }

    /**
     * Get the payment refund amount.
     * @return the apRefundReply_amount
     */
    public Double getApRefundReply_amount() {
        return apRefundReply_amount;
    }

    /**
     * Set the payment refund amount.
     *
     * @param apRefundReply_amount the apRefundReply_amount to set
     */
    public void setApRefundReply_amount(Double apRefundReply_amount) {
        this.apRefundReply_amount = apRefundReply_amount;
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

    /** Applicable payment type list. */
    public enum PaymentStatus {
        /** Payment has not been processed. */
        PENDING,
        /** Payment is complete. */
        COMPLETED,
        /** Customer cancelled the payment. */
        ABANDONED
    }
}
