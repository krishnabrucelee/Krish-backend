package ck.panda.domain.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;
import ck.panda.util.JsonUtil;

/**
 * Accounts are grouped by domains. Domains usually contain multiple accounts that have some logical relationship to
 * each other and a set of delegated administrators with some authority over the domain and its subdomains.
 */
@Entity
@Table(name = "domains")
public class Domain implements Serializable {

    /** Unique ID of the Domain. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Unique ID from Cloud Stack. */
    @Column(name = "uuid")
    private String uuid;

    /** Name of the Domain. */
    @NotEmpty
    @Column(name = "name", nullable = false)
    private String name;

    /** Company name Abbreviation for the Domain. */
    @Column(name = "company_name_abb")
    private String companyNameAbbreviation;

    /** Portal user name for the Domain. */
    @Column(name = "portal_user_name")
    private String portalUserName;

    /** Portal user password for the Domain. */
    @Column(name = "password")
    private String password;

    /** City of Headquarters for the Domain. */
    @Column(name = "city_headquarter")
    private String cityHeadquarter;

    /** Company Address for the Domain. */
    @Column(name = "company_address")
    private String companyAddress;

    /** Company user primary first name. */
    @Column(name = "company_primary_firstname")
    private String primaryFirstName;

    /** Company user primary last name. */
    @Column(name = "company_primary_lastname")
    private String lastName;

    /** Company user primary email. */
    @Column(name = "company_primary_email")
    private String email;

    /** Phone for the Domain. */
    @Column(name = "company_primary_phone")
    private String phone;

    /** Secondary Contact for the Domain. */
    @Column(name = "secondary_contact_name")
    private String secondaryContactName;

    /** Secondary Contact for the Domain. */
    @Column(name = "secondary_contact_last_name")
    private String secondaryContactLastName;

    /** Secondary Contact for the Domain. */
    @Column(name = "secondary_contact_email")
    private String secondaryContactEmail;

    /** Secondary Contact for the Domain. */
    @Column(name = "secondary_contact_phone")
    private String secondaryContactPhone;

    /** Check whether Domain is in active state or in active state. */
    @Column(name = "is_active")
    private Boolean isActive;

    /** Status for Domain, whether it is Deleted, Disabled etc . */
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

    /** Temporary variable. */
    @Transient
    private Boolean syncFlag;

    /**
     * Get id of the Domain.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set id of the Domain.
     *
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get uuid of the Domain.
     *
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set uuid of the Domain.
     *
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get name of the Domain.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set name of the Domain.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get is Active state of the Domain.
     *
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set is Active state of the Domain.
     *
     * @param isActive the isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
     * Get version of the Domain.
     *
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Set version of the Domain.
     *
     * @param version the version to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get Created user.
     *
     * @return the createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set Created user.
     *
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get updated user.
     *
     * @return the updatedBy
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set updated user.
     *
     * @param updatedBy the updatedBy to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the domain created date time.
     *
     * @return the createdDateTime
     */
    public ZonedDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the domain created date time.
     *
     * @param createdDateTime the createdDateTime to set
     */
    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the Domain updated date and time.
     *
     * @return the updatedDateTime
     */
    public ZonedDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the Domain updated date time.
     *
     * @param updatedDateTime the updatedDateTime to set
     */
    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Get companyNameAbbreviation.
     *
     * @return the companyNameAbbreviation
     */
    public String getCompanyNameAbbreviation() {
        return companyNameAbbreviation;
    }

    /**
     * Set companyNameAbbreviation.
     *
     * @param companyNameAbbreviation to set
     */
    public void setCompanyNameAbbreviation(String companyNameAbbreviation) {
        this.companyNameAbbreviation = companyNameAbbreviation;
    }

    /**
     * Get portalUserName.
     *
     * @return the portalUserName
     */
    public String getPortalUserName() {
        return portalUserName;
    }

    /**
     * Set the portalUserName.
     *
     * @param portalUserName to set
     */
    public void setPortalUserName(String portalUserName) {
        this.portalUserName = portalUserName;
    }

    /**
     * Get the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the password.
     *
     * @param password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get cityHeadquarter.
     *
     * @return the cityHeadquarter
     */
    public String getCityHeadquarter() {
        return cityHeadquarter;
    }

    /**
     * Set the cityHeadquarter.
     *
     * @param cityHeadquarter to set
     */
    public void setCityHeadquarter(String cityHeadquarter) {
        this.cityHeadquarter = cityHeadquarter;
    }

    /**
     * Get the companyAddress.
     *
     * @return the companyAddress
     */
    public String getCompanyAddress() {
        return companyAddress;
    }

    /**
     * Set the companyAddress.
     *
     * @param companyAddress to set
     */
    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    /**
     * Get primaryFirstName.
     *
     * @return the primaryFirstName
     */
    public String getPrimaryFirstName() {
        return primaryFirstName;
    }

    /**
     * Set the primaryFirstName.
     *
     * @param primaryFirstName to set
     */
    public void setPrimaryFirstName(String primaryFirstName) {
        this.primaryFirstName = primaryFirstName;
    }

    /**
     * Get the lastName.
     *
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set the lastName .
     *
     * @param lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
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
     * @param phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
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
     * @param syncFlag to set
     */
    public void setSyncFlag(Boolean syncFlag) {
        this.syncFlag = syncFlag;
    }

    /**
     * Get secondaryContactName.
     *
     * @return the secondaryContactName
     */
    public String getSecondaryContactName() {
        return secondaryContactName;
    }

    /**
     * Set the secondaryContactName.
     *
     * @param secondaryContactName to set
     */
    public void setSecondaryContactName(String secondaryContactName) {
        this.secondaryContactName = secondaryContactName;
    }

    /**
     * Get secondaryContactLastName.
     *
     * @return the secondaryContactLastName
     */
    public String getSecondaryContactLastName() {
        return secondaryContactLastName;
    }

    /**
     * Set secondaryContactLastName.
     *
     * @param secondaryContactLastName the secondaryContactLastName to set
     */
    public void setSecondaryContactLastName(String secondaryContactLastName) {
        this.secondaryContactLastName = secondaryContactLastName;
    }

    /**
     * Get secondaryContactEmail.
     *
     * @return the secondaryContactEmail
     */
    public String getSecondaryContactEmail() {
        return secondaryContactEmail;
    }

    /**
     * Set the secondaryContactEmail.
     *
     * @param secondaryContactEmail to set
     */
    public void setSecondaryContactEmail(String secondaryContactEmail) {
        this.secondaryContactEmail = secondaryContactEmail;
    }

    /**
     * Set the secondaryContactPhone.
     *
     * @return the secondaryContactPhone
     */
    public String getSecondaryContactPhone() {
        return secondaryContactPhone;
    }

    /**
     * Set the secondaryContactPhone.
     *
     * @param secondaryContactPhone to set
     */
    public void setSecondaryContactPhone(String secondaryContactPhone) {
        this.secondaryContactPhone = secondaryContactPhone;
    }

    /**
     * Enumeration status for Domain.
     */
    public enum Status {
        /** Enabled status is used to list domain through out the application. */
        ACTIVE,

        /** Deleted status make domain as soft deleted and it will not list on the applicaiton. */
        INACTIVE
    }

    /**
     * Convert JSONObject to domain entity.
     *
     * @param jsonObject json object
     * @return domain entity object.
     * @throws Exception unhandled errors.
     */
    public static Domain convert(JSONObject jsonObject) throws Exception {
        Domain domain = new Domain();
        domain.setSyncFlag(false);
        domain.setUuid(JsonUtil.getStringValue(jsonObject, "id"));
        domain.setName(JsonUtil.getStringValue(jsonObject, "name"));
        domain.setCompanyNameAbbreviation(JsonUtil.getStringValue(jsonObject, "name"));
        domain.setIsActive(true);
        return domain;
    }

    /**
     * Mapping entity object into list.
     *
     * @param domainList list of domains.
     * @return domain map
     */
    public static Map<String, Domain> convert(List<Domain> domainList) {
        Map<String, Domain> domainMap = new HashMap<String, Domain>();

        for (Domain domain : domainList) {
            domainMap.put(domain.getUuid(), domain);
        }

        return domainMap;
    }
}
