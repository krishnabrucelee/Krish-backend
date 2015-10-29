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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;


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
    @Size(min = 4, max = 20)
    @Column(name = "name", nullable = false)
    private String name;

    /** Company name for the Domain. */
    @Column(name = "company_name")
    private String companyName;

    /** Domain owner for the account. */
    @Column(name = "domain_owner")
    private String domainOwner;

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
     * Get Company name.
     *
     * @return the companyName
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * Set company name of the Domain.
     *
     * @param companyName the companyName to set
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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
     * Enumeration status for Domain.
     */
    public enum Status {
        /** Enabled status is used to list domain through out the application. */
        ENABLED,

        /** Deleted status make domain as soft deleted and it will not list on the applicaiton. */
        DELETED
    }

  /**
   * Convert JSONObject to domain entity.
   *
   * @param object json object
   * @return domain entity object.
   * @throws JSONException handles json exception.
   */
  public static Domain convert(JSONObject object) throws JSONException {
      Domain domain = new Domain();
      domain.uuid = object.get("id").toString();
      domain.name = object.get("name").toString();

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
