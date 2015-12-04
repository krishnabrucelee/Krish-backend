package ck.panda.domain.entity;

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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
 * Accounts are grouped by domains. Domains usually contain multiple accounts
 * that have some logical relationship to each other and a set of delegated
 * administrators with some authority over the domain and its subdomains.
 *
 */
@Entity
@Table(name = "ck_domain")
public class Domain {

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
    private String companyNameAbb;

    /** Portal user name for the Domain. */
    @Column(name = "portal_user_name")
    private String portalUserName;

    @Transient
    private String password;

    @Transient
    private String confirmPassword;

    /** City of Headquarters for the Domain. */
    @Column(name = "city_headquarter")
    private String cityHeadquarter;

    /** Company Address for the Domain. */
    @Column(name = "company_address")
    private String companyAddress;

    @Transient
    private String primaryFirstName;

    @Transient
    private String lastName;

    @Transient
    private String email;

    /** Phone for the Domain. */
    @Column(name = "phone")
    private String phone;

    /** Secondary Contact for the Domain. */
    @Column(name = "secondary_contact")
    private String secondaryContact;

    /** Domain owner for the account. */
    @Column(name = "domain_owner")
    private String domainOwner;

    /** HOD of the domain. */
    @JoinColumn(name = "hod_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @OneToOne
    private Department hod;

    /** HOD id of the domain. */
    @Column(name = "hod_id")
    private Long hodId;

    /** Whether Domain child is present. */
    @Column(name = "is_child")
    private Boolean isChild;

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
    @JoinColumn(name = "created_user_id", referencedColumnName = "id")
    @OneToOne
    private User createdBy;

    /** Last updated by user. */
    @LastModifiedBy
    @JoinColumn(name = "updated_user_id", referencedColumnName = "id")
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
     * @param id
     *            the id to set
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
     * @param uuid
     *            the uuid to set
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
     * Get Domain Owner of the Domain.
     *
     * @return the domainOwner
     */
    public String getDomainOwner() {
        return domainOwner;
    }

    /**
     * Set Domain Owner of the Domain.
     *
     * @param domainOwner the domainOwner to set
     */
    public void setDomainOwner(String domainOwner) {
        this.domainOwner = domainOwner;
    }


    /**
    * @return the hod
    */
    public Department getHod() {
        return hod;
    }

    /**
    * @param hod the hod to set
    */
    public void setHod(Department hod) {
        this.hod = hod;
    }

    /**
    * @return the hodId
    */
    public Long getHodId() {
        return hodId;
    }

    /**
    * @param hodId the hodId to set
    */
    public void setHodId(Long hodId) {
        this.hodId = hodId;
    }

    /**
     * Get is child state of the Domain.
     *
     * @return the isChild
     */
    public Boolean getIsChild() {
        return isChild;
    }

    /**
     * Set is child state of the Domain.
     *
     * @param isChild the isChild to set
     */
    public void setIsChild(Boolean isChild) {
        this.isChild = isChild;
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
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * Set Created user.
     *
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get updated user.
     *
     * @return the updatedBy
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set updated user.
     *
     * @param updatedBy the updatedBy to set
     */
    public void setUpdatedBy(User updatedBy) {
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
    * @return the companyNameAbb
    */
    public String getCompanyNameAbb() {
        return companyNameAbb;
    }

    /**
    * @param companyNameAbb the companyNameAbb to set
    */
    public void setCompanyNameAbb(String companyNameAbb) {
        this.companyNameAbb = companyNameAbb;
    }

    /**
    * @return the portalUserName
    */
    public String getPortalUserName() {
        return portalUserName;
    }

    /**
    * @param portalUserName the portalUserName to set
    */
    public void setPortalUserName(String portalUserName) {
        this.portalUserName = portalUserName;
    }

    /**
    * @return the password
    */
    public String getPassword() {
        return password;
    }

    /**
    * @param password the password to set
    */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
    * @return the confirmPassword
    */
    public String getConfirmPassword() {
        return confirmPassword;
    }

    /**
    * @param confirmPassword the confirmPassword to set
    */
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    /**
    * @return the cityHeadquarter
    */
    public String getCityHeadquarter() {
        return cityHeadquarter;
    }

    /**
    * @param cityHeadquarter the cityHeadquarter to set
    */
    public void setCityHeadquarter(String cityHeadquarter) {
        this.cityHeadquarter = cityHeadquarter;
    }

    /**
    * @return the companyAddress
    */
    public String getCompanyAddress() {
        return companyAddress;
    }

    /**
    * @param companyAddress the companyAddress to set
    */
    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    /**
    * @return the primaryFirstName
    */
    public String getPrimaryFirstName() {
        return primaryFirstName;
    }

    /**
    * @param primaryFirstName the primaryFirstName to set
    */
    public void setPrimaryFirstName(String primaryFirstName) {
        this.primaryFirstName = primaryFirstName;
    }

    /**
    * @return the lastName
    */
    public String getLastName() {
        return lastName;
    }

    /**
    * @param lastName the lastName to set
    */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
    * @return the email
    */
    public String getEmail() {
        return email;
    }

    /**
    * @param email the email to set
    */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
    * @return the phone
    */
    public String getPhone() {
        return phone;
    }

    /**
    * @param phone the phone to set
    */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
    * @return the secondaryContact
    */
    public String getSecondaryContact() {
        return secondaryContact;
    }

    /**
    * @param secondaryContact the secondaryContact to set
    */
    public void setSecondaryContact(String secondaryContact) {
        this.secondaryContact = secondaryContact;
    }

    /**
    * @return the syncFlag
    */
    public Boolean getSyncFlag() {
        return syncFlag;
    }

    /**
    * @param syncFlag the syncFlag to set
    */
    public void setSyncFlag(Boolean syncFlag) {
        this.syncFlag = syncFlag;
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
