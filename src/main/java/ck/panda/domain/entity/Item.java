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
 * items are the list of billable services. items may Infrastructure, Managed Service or Optional.
 * Each items must have tax.
 */
@Entity
@Table(name = "item")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class Item implements Serializable {

    /** Id of the item. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Name of the item. */
    private String name;

    /** Type of the item. */
    private ItemType itemType;

    /** Unit of the item. */
    private ItemUnit itemUnit;

    /** Tax for the item. */
    @JoinColumn(name = "tax_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Tax tax;

    /** Tax id for the item. */
    @Column(name = "tax_id")
    private Long taxId;

    /** Whether item has discount or not. */
    @Column(name = "has_discount")
    private Boolean hasDiscount;

    /** Whether item is customized or not. */
    @Column(name = "is_customized")
    private Boolean isCustomized;

    /** Whether item is in active state or in active state. */
    @Column(name = "is_active")
    private Boolean isActive;

    /** Status for item, whether it is Deleted, Disabled etc . */
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
    public Item() {
        super();
    }

    /**
     * Parameterized constructor.
     *
     * @param name to set
     */
    public Item(String name) {
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
     * Get name of the item.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set name of the item.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the item type.
     *
     * @return the itemType
     */
    public ItemType getItemType() {
        return itemType;
    }

    /**
     * Set the item type.
     *
     * @param itemType the itemType to set
     */
    public void setItemType(ItemType billableType) {
        this.itemType = billableType;
    }

    /**
     * Get the Unit.
     *
     * @return the itemUnit
     */
    public ItemUnit getItemUnit() {
        return itemUnit;
    }

    /**
     * Set the Unit.
     *
     * @param itemUnit the itemUnit to set
     */
    public void setItemUnit(ItemUnit itemUnit) {
        this.itemUnit = itemUnit;
    }

    /**
     * Get tax of the item.
     *
     * @return the tax
     */
    public Tax getTax() {
        return tax;
    }

    /**
     * Set tax of the item.
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
     * Get the item discount status either true or false.
     *
     * @return the discountable
     */
    public Boolean getHasDiscount() {
        return hasDiscount;
    }

    /**
     * Set the item discount.
     *
     * @param hasDiscountable the discount to set for the item
     */
    public void setHasDiscountable(Boolean hasDiscountable) {
        this.hasDiscount = hasDiscountable;
    }

    /**
     * Get the item customized status either true or false.
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
     * Get the item status.
     *
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set the item status.
     *
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Enumeration status for item.
     */
    public enum Status {
        /** Enabled status is used to list items through out the application. */
        ENABLED,

        /** Deleted status make items as soft deleted and it will not list on the applicaiton. */
        DELETED
    }

    /**
     * Item type of the item.
     */
    public enum ItemType {
        /** Infrastructure item type is the default items of cloudstack. */
        INFRASTRUCTURE,

        /** Additional items for panda portal. */
        MANAGED,

        /** Optional items for panda portal. */
        OPTIONAL
    }

    /**
     * Unit of the item.
     */
    public enum ItemUnit {

        /** Unit per Core per Hour. */
        PER_CORE_PER_HOUR,

        /** Unit per GB per Hour. */
        PER_GB_PER_HOUR,

        /** Unit per OS per Hour. */
        PER_OS_PER_HOUR,

        /** Unit per DB per Hour. */
        PER_DB_PER_HOUR,

        /** Unit per APP per Hour. */
        PER_APP_PER_HOUR
    }
}
