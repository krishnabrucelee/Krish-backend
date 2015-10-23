package ck.panda.domain.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * Application consists of different application types. Application types are unique to domain.
 * Application types may vary with respect to domain.
 *
 */
@Entity
@Table(name = "ck_application")
@SuppressWarnings("serial")
public class Application implements Serializable {

    /** Id of the Application. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Type of the Application. */
    @NotEmpty
    @Size(min = 4, max = 20)
    @Column(name = "type", nullable = false)
    private String type;

    /** Description of the Application. */
    @Column(name = "description")
    private String description;

    /** Application Domain. */
    @ManyToOne
    @JoinColumn(name = "domain_id", referencedColumnName = "id")
    private Domain domain;

    /** update status when delete an entity. */
	@Column(name = "is_active")
	private Boolean isActive;

	 /** Enum type for Application Status. */
    public enum Status {

    	/** Application type will be in a Enabled State. */
        ENABLED,
        /** Application type will be in a Disabled State. */
        DISABLED
    }

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
	private DateTime createdDateTime;

	/** Updated date and time. */
	@LastModifiedDate
	@Column(name = "updated_date_time")
	private DateTime updatedDateTime;

    /** Default constructor. */
    public Application() {
        super();
    }

	/**
	 * Get the id of the application.
	 *
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Set the id of the application.
	 *
	 * @param id - the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Get the application type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Set the application type.
	 *
	 * @param type - the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Get the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description.
	 *
	 * @param description - the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get the domain.
	 *
	 * @return the domain
	 */
	public Domain getDomain() {
		return domain;
	}

	/**
	 * Set the domain.
	 *
	 * @param domain - the domain to set
	 */
	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	/**
	 * Get the isActive.
	 *
	 * @return isActive
	 */
	public Boolean getIsActive() {
		return isActive;
	}

	/**
	 * Set the isActive.
	 *
	 * @param isActive - the isActive to set
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
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
	 * @param version - the version to set
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
	 * @param createdBy - the createdBy to set
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
	 * @param updatedBy - the updatedBy to set
	 */
	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}

	/**
	 * Get the createdDateTime.
	 *
	 * @return createdDateTime
	 */
	public DateTime getCreatedDateTime() {
		return createdDateTime;
	}

	/**
	 * Set the createdDateTime.
	 *
	 * @param createdDateTime - the createdDateTime to set
	 */
	public void setCreatedDateTime(DateTime createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	/**
	 * Get the updatedDateTime.
	 *
	 * @return updatedDateTime
	 */
	public DateTime getUpdatedDateTime() {
		return updatedDateTime;
	}

	/**
	 * Set the updatedDateTime.
	 *
	 * @param updatedDateTime - the updatedDateTime to set
	 */
	public void setUpdatedDateTime(DateTime updatedDateTime) {
		this.updatedDateTime = updatedDateTime;
	}

	/** Set the default value for isActive before executing entity manager. */
   @PrePersist
   void preInsert() {
       this.isActive = true;
   }
}
