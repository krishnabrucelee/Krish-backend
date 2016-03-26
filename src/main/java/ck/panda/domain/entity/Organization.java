package ck.panda.domain.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;


/**
 * Organization detatils.
 *
 */
@Entity
@Table(name = "organization")
@SuppressWarnings("serial")
@EntityListeners(AuditingEntityListener.class)
public class Organization implements Serializable {

     /** Unique Id of the organization. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Name of the organization. */
    @Column(name = "name")
    private String name;

    /** Email of the organization. */
    @Column(name = "email")
    private String email;

    /** Address of the organization. */
    @Column(name = "address")
    private String address;

    /** Address extenstion of the organization. */
    @Column(name = "address_extension")
    private String addressExtension;

    /** City of the organization. */
    @Column(name = "city")
    private String city;

    /** Country of the organization. */
    @Column(name = "country")
    private String country;

    /** State of the organization. */
    @Column(name = "state")
    private String state;

    /** Zipcode of the organization. */
    @Column(name = "zipcode")
    private String zipcode;

    /** Phone of the organization. */
    @Column(name = "phone")
    private String phone;

    /** Fax of the organization. */
    @Column(name = "fax")
    private String fax;

    /** Signature of the organization. */
    @Column(name = "signature")
    private String signature;

    /** Terms and condition of the organization. */
    @Column(name = "terms_condition")
    private String termsCondition;

    /** Logo URL of the organization. */
    @Column(name = "logo_url")
    private String logoURL;

    /** Background image url of the organization. */
    @Column(name = "image_url")
    private String backGroundImageURL;

    /** Status for Organization, whether it is enabled, disabled etc . */
    @Column(name = "status")
    private Status status;

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

    /** IsActive attribute to verify Active or Inactive. */
    @Column(name = "is_active")
    private Boolean isActive;

    /**
     * Get the id of the organization.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id of the organization.
     *
     * @param id  to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the organziation name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the organziation name.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the email.
     *
     * @param email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the address.
     *
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set the address.
     *
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Get the address Extenstion.
     *
     * @return the addressExtension
     */
    public String getAddressExtension() {
        return addressExtension;
    }

    /**
     * Set the address Extenstion.
     *
     * @param addressExtension the addressExtension to set
     */
    public void setAddressExtension(String addressExtension) {
        this.addressExtension = addressExtension;
    }

    /**
     * Get the city.
     *
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * Set the city.
     *
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Get the country.
     *
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Set the country.
     *
     * @param country the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Get the state.
     *
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * Set the state.
     *
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Get the zipcode.
     *
     * @return the zipcode
     */
    public String getZipcode() {
        return zipcode;
    }

    /**
     * Set the zipcode.
     *
     * @param zipcode the zipcode to set
     */
    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    /**
     * Get the phone.
     *
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Set the phone.
     *
     * @param phone  to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Get the fax.
     *
     * @return the fax
     */
    public String getFax() {
        return fax;
    }

    /**
     * Set the fax.
     *
     * @param fax the fax to set
     */
    public void setFax(String fax) {
        this.fax = fax;
    }

    /**
     * Get the signature.
     *
     * @return the signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * Set the signature.
     *
     * @param signature the signature to set
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }

    /**
     * Get the terms and condition.
     *
     * @return the termsCondition
     */
    public String getTermsCondition() {
        return termsCondition;
    }

    /**
     * Set the terms and condition.
     *
     * @param termsCondition the termsCondition to set
     */
    public void setTermsCondition(String termsCondition) {
        this.termsCondition = termsCondition;
    }

    /**
     * Get the domain status.
     *
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set the domain status.
     *
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Get the createdDatetime.
     *
     * @return the createdDateTime
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the createdDateTime.
     *
     * @param createdDateTime to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updatedDatetime.
     *
     * @return the updatedDateTime
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updatedDateTime.
     *
     * @param updatedDateTime to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get the logo URL.
     *
     * @return the logoURL
     */
    public String getLogoURL() {
        return logoURL;
    }

    /**
     * Set the logo URL.
     *
     * @param logoURL to set
     */
    public void setLogoURL(String logoURL) {
        this.logoURL = logoURL;
    }

    /**
     * Get the backGround Image URL.
     *
     * @return the backGroundImageURL
     */
    public String getBackGroundImageURL() {
        return backGroundImageURL;
    }

    /**
     * Set the backGround Image URL.
     *
     * @param backGroundImageURL to set
     */
    public void setBackGroundImageURL(String backGroundImageURL) {
        this.backGroundImageURL = backGroundImageURL;
    }

    /**
     * Enumeration for Status.
     */
    public enum Status {
         /** Enabled status is used to list domain through out the application. */
        ENABLED,

        /** Disabled status make domain as soft deleted and it will not list on the applicaiton. */
        DISABLED
    }

    /**
     * Get isActive.
     *
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set is Active.
     *
     * @param isActive the isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }


}
