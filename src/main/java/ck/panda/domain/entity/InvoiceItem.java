package ck.panda.domain.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Type;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import ck.panda.util.ConvertUtil;
import ck.panda.util.JsonUtil;

/**
 * Invoice for the accounts.
 * with different roles.
 * Roles should be classified based on Departments.
 */
@Entity
@Table(name = "invoice")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class InvoiceItem implements Serializable {

    /** Id of the Department. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "invoice_address")
    private String invoiceAddress;

    @Column(name = "billing_address")
    private String billingAddress;

    @Column(name = "reference_number")
    private String referenceNumber;

    /** Invoice for the department id. */
    @JoinColumn(name = "department_id", referencedColumnName = "id", updatable = false, insertable = false)
    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Department.class)
    private Department department;

    /** Invoice department id. */
    @Column(name = "department_id")
    private Long departmentId;

    /** Invoice due date */
    @Column(name = "due_date")
    private Date dueDate;


    /**
     * The sum of invoice item amount for this invoice.
     */
    @Column(name = "total_amount")
    private Double totalAmount = 0D;

    /**
     * Customer name for this invoice.
     */
    @Column(name = "customer_name")
    private String customerName;

    /**
     * Currency type for this invoice.
     */
    @Column(name = "currency")
    private String currency;

    /** Status for Invoice, Possible values include ENABLED, DISABLED, SUSPENDED, PAID and PARTIAL_PAID. */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;


    /**
     * The pending amount for this invoice, if any. Default value 0.
     */
    private Double previousBalance = 0D;

    /**
     * The total payment for this invoice.
     */
    private Double payment = 0D;

    /**
     * The invoice date
     */
    private Date date;

    /**
     * The current payable amount
     */
    private Double currentDue = 0D;

    /**
     * The previous invoice date
     */
    private Date previousInvoiceDate;

    /**
     * The previous invoice id for reference
     */
    private Long previousInvoiceId;

    /**
     * The total credit amount for this invoice
     */
    private Double credit = 0D;

    /**
     * The total refund amount for this invoice
     */
    private Double refundAmount = 0D;


    /** Check whether Invoice is in active state or in active state. */
    @Column(name = "is_active")
    private Boolean isActive;


    /** Version attribute to handle optimistic locking. */
    @Version
    @Column(name = "version")
    private Long version;

    /** Created by user. */
    @CreatedBy
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    @OneToOne
    private User createdBy;

    /** Last updated by user. */
    @LastModifiedBy
    @JoinColumn(name = "updated_by", referencedColumnName = "id")
    @OneToOne
    private User updatedBy;

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
     * Default constructor.
     */
    public InvoiceItem() {
        super();
    }

    /**
     * Parameterized constructor.
     *
     * @param name to set
     */
    public InvoiceItem(String name) {
        super();
    }

    /**
     * Get the id.
     *
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id.
     *
     * @param id the Long to set
     */
    public void setId(Long id) {
        this.id = id;
    }





    /**
     * @return the invoiceAddress
     */
    public String getInvoiceAddress() {
        return invoiceAddress;
    }

    /**
     * @param invoiceAddress the invoiceAddress to set
     */
    public void setInvoiceAddress(String invoiceAddress) {
        this.invoiceAddress = invoiceAddress;
    }

    /**
     * @return the billingAddress
     */
    public String getBillingAddress() {
        return billingAddress;
    }

    /**
     * @param billingAddress the billingAddress to set
     */
    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    /**
     * @return the referenceNumber
     */
    public String getReferenceNumber() {
        return referenceNumber;
    }

    /**
     * @param referenceNumber the referenceNumber to set
     */
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    /**
     * @return the department
     */
    public Department getDepartment() {
        return department;
    }

    /**
     * @param department the department to set
     */
    public void setDepartment(Department department) {
        this.department = department;
    }

    /**
     * @return the departmentId
     */
    public Long getDepartmentId() {
        return departmentId;
    }

    /**
     * @param departmentId the departmentId to set
     */
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * @return the dueDate
     */
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * @param dueDate the dueDate to set
     */
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * @return the totalAmount
     */
    public Double getTotalAmount() {
        return totalAmount;
    }

    /**
     * @param totalAmount the totalAmount to set
     */
    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * @return the customerName
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * @param customerName the customerName to set
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * @param currency the currency to set
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * @return the previousBalance
     */
    public Double getPreviousBalance() {
        return previousBalance;
    }

    /**
     * @param previousBalance the previousBalance to set
     */
    public void setPreviousBalance(Double previousBalance) {
        this.previousBalance = previousBalance;
    }

    /**
     * @return the payment
     */
    public Double getPayment() {
        return payment;
    }

    /**
     * @param payment the payment to set
     */
    public void setPayment(Double payment) {
        this.payment = payment;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the currentDue
     */
    public Double getCurrentDue() {
        return currentDue;
    }

    /**
     * @param currentDue the currentDue to set
     */
    public void setCurrentDue(Double currentDue) {
        this.currentDue = currentDue;
    }

    /**
     * @return the previousInvoiceDate
     */
    public Date getPreviousInvoiceDate() {
        return previousInvoiceDate;
    }

    /**
     * @param previousInvoiceDate the previousInvoiceDate to set
     */
    public void setPreviousInvoiceDate(Date previousInvoiceDate) {
        this.previousInvoiceDate = previousInvoiceDate;
    }

    /**
     * @return the previousInvoiceId
     */
    public Long getPreviousInvoiceId() {
        return previousInvoiceId;
    }

    /**
     * @param previousInvoiceId the previousInvoiceId to set
     */
    public void setPreviousInvoiceId(Long previousInvoiceId) {
        this.previousInvoiceId = previousInvoiceId;
    }

    /**
     * @return the credit
     */
    public Double getCredit() {
        return credit;
    }

    /**
     * @param credit the credit to set
     */
    public void setCredit(Double credit) {
        this.credit = credit;
    }

    /**
     * @return the refundAmount
     */
    public Double getRefundAmount() {
        return refundAmount;
    }

    /**
     * @param refundAmount the refundAmount to set
     */
    public void setRefundAmount(Double refundAmount) {
        this.refundAmount = refundAmount;
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
     * @param version the Long to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get the createdBy.
     *
     * @return createdBy
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the createdBy.
     *
     * @param createdBy the User to set
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updatedBy.
     *
     * @return updatedBy
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updatedBy.
     *
     * @param updatedBy the User to set
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the createdDateTime.
     *
     * @return createdDateTime
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the createdDateTime.
     *
     * @param createdDateTime the DateTime to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updatedDateTime.
     *
     * @return updatedDateTime
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updatedDateTime.
     *
     * @param updatedDateTime the DateTime to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get is Active state of the Department.
     *
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set is Active state of the Department.
     *
     * @param isActive the isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Get the department status.
     *
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set the department status.
     *
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }



    /**
     * Enumeration status for Department.
     */
    public enum Status {
        /** Invoice with the enabled status . */
        ENABLED,

        /** Invoice with the disabled status . */
        DISABLED,

        /** Suspended Invoice . */
        SUSPENDED,

        /** Paid Invoice. */
        PAID,

        /** Partially paid Invoice. */
        PARTIAL_PAID,

        /** Closed Invoice. */
        CLOSED,

        /** Draft Invoice. */
        DRAFT,

        /** Final Invoice. */
        FINAL
    }
}

