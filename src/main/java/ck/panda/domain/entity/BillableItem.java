package ck.panda.domain.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
 * Billable Items are the list of billable services. Billable Items may Infrastructure, Managed Service or Optional.
 * Each billable items must have tax.
 */
@Entity
@Table(name = "billable_item")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class BillableItem implements Serializable {

    /** Id of the billable item. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Name of the billable item. */
    private String name;

    /** Type of the billable item. */
    private BillableType billableType;

    /** Unit of the billable item. */
    private BillableUnit billableUnit;

    /** Tax for the billable item. */
    @JoinColumn(name = "tax_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Tax tax;

    /** Tax id for the billable item. */
    @Column(name = "tax_id")
    private Long taxId;

    /** Whether billable item has discount or not. */
    @Column(name = "has_discount")
    private Boolean hasDiscount;

    /** Whether billable item is customized or not. */
    @Column(name = "is_customized")
    private Boolean isCustomized;

    /** Whether billable item is in active state or in active state. */
    @Column(name = "is_active")
    private Boolean isActive;

    /** Status for billable item, whether it is Deleted, Disabled etc . */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

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
     * Default constructor.
     */
    public BillableItem() {
        super();
    }

    /**
     * Parameterized constructor.
     *
     * @param name to set
     */
    public BillableItem(String name) {
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
     * Get name of the billable item.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set name of the billable item.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the billable item type.
     *
     * @return the billableType
     */
    public BillableType getBillableType() {
        return billableType;
    }

    /**
     * Set the billable item type.
     *
     * @param billableType the billableType to set
     */
    public void setBillableType(BillableType billableType) {
        this.billableType = billableType;
    }

    /**
     * Get the billable unit.
     *
     * @return the billableUnit
     */
    public BillableUnit getBillableUnit() {
        return billableUnit;
    }

    /**
     * Set the billable unit.
     *
     * @param billableUnit the billableUnit to set
     */
    public void setBillableUnit(BillableUnit billableUnit) {
        this.billableUnit = billableUnit;
    }

    /**
     * Get tax of the billable item.
     *
     * @return the tax
     */
    public Tax getTax() {
        return tax;
    }

    /**
     * Set tax of the billable item.
     *
     * @param tax the tax to set
     */
    public void setTax(Tax tax) {
        this.tax = tax;
    }

    /**
     * Get the tax id.
     *
     * @return the taxId
     */
    public Long getTaxId() {
        return taxId;
    }

    /**
     * Set the tax id.
     *
     * @param taxId the taxId to set
     */
    public void setTaxId(Long taxId) {
        this.taxId = taxId;
    }

    /**
     * Get the billable item discount status either true or false.
     *
     * @return the discountable
     */
    public Boolean getHasDiscount() {
        return hasDiscount;
    }

    /**
     * Set the billable item discount.
     *
     * @param hasDiscountable the discount to set for the billable item
     */
    public void setHasDiscountable(Boolean hasDiscountable) {
        this.hasDiscount = hasDiscountable;
    }

    /**
     * Get the billable item customized status either true or false.
     *
     * @return the customized
     */
    public Boolean getIsCustomized() {
        return isCustomized;
    }

    /**
     * Set the billable state is customized or not.
     *
     * @param isCustomized the isCustomized to set
     */
    public void setIsCustomized(Boolean isCustomized) {
        this.isCustomized = isCustomized;
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
     * Get the created user id.
     *
     * @return createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the created user id.
     *
     * @param createdBy the User to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updated user id.
     *
     * @return updatedBy
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updated user id.
     *
     * @param updatedBy the User to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the created date.
     *
     * @return createdDateTime
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the created date.
     *
     * @param createdDateTime the DateTime to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updated date.
     *
     * @return updatedDateTime
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updated date.
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
     * Get the billable item status.
     *
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set the billable item status.
     *
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Enumeration status for billable item.
     */
    public enum Status {
        /** Enabled status is used to list billable items through out the application. */
        ENABLED,

        /** Deleted status make billable items as soft deleted and it will not list on the applicaiton. */
        DELETED
    }

    /**
     * Billable type of the billable item.
     */
    public enum BillableType {
        /** Infrastructure billable type is the default billable items of cloudstack. */
        INFRASTRUCTURE,

        /** Additional billable items for panda portal. */
        MANAGED,

        /** Optional billable items for panda portal. */
        OPTIONAL
    }

    /**
     * Unit of the billable item.
     */
    public enum BillableUnit {

        /** Billable unit per Core per Hour. */
        PER_CORE_PER_HOUR,

        /** Billable unit per GB per Hour. */
        PER_GB_PER_HOUR,

        /** Billable unit per OS per Hour. */
        PER_OS_PER_HOUR,

        /** Billable unit per DB per Hour. */
        PER_DB_PER_HOUR,

        /** Billable unit per APP per Hour. */
        PER_APP_PER_HOUR
    }
}
