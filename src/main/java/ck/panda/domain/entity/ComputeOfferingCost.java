package ck.panda.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

/**
 * Computer offerings cost for each Vcpu, iops and memory usage.
 */

@Entity
@Table(name = "ck_service_offerings_cost")
@SuppressWarnings("serial")
public class ComputeOfferingCost {

    /** The id of the Compute offering Cost. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Zone id for this offering. */
    @JoinColumn(name = "zone_id", referencedColumnName = "id",insertable = false, updatable = false)
    @OneToOne
    private Zone zone;

    /** id of the zone.*/
    @Column(name = "zone_id")
    private Long zoneId;

    /** The id of the Compute offering.  */
    @Column(name = "compute_offering_id")
    private Long computeOfferingId;

    /** The Setup Cost. */
    @Column(name = "setup_cost")
    private Double setupCost;

    /** Cost of Running Instance. */
    @Column(name = "instance_running_cost")
    private Double instanceRunningCost;

    /** Cost of Stoppage Instance. */
    @Column(name = "instance_stoppage_cost")
    private Double instanceStoppageCost;

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
    private DateTime createdDateTime;

    /** Last modified date and time. */
    @LastModifiedDate
    private DateTime lastModifiedDateTime;

    /** Version attribute to handle optimistic locking. */
    @Version
    @Column(name = "version")
    private Long version;

    /** Status attribute to verify Active or Inactive. */
    @Column(name = "status")
    private String status;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the zoneId
     */
    public Long getZoneId() {
        return zoneId;
    }

    /**
     * @return the computeOfferingId
     */
    public Long getComputeOfferingId() {
        return computeOfferingId;
    }

    /**
     * @return the setupCost
     */
    public Double getSetupCost() {
        return setupCost;
    }

    /**
     * @return the instanceRunningCost
     */
    public Double getInstanceRunningCost() {
        return instanceRunningCost;
    }

    /**
     * @return the instanceStoppageCost
     */
    public Double getInstanceStoppageCost() {
        return instanceStoppageCost;
    }

    /**
     * @return the createdBy
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * @return the updatedBy
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * @return the createdDateTime
     */
    public DateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * @return the lastModifiedDateTime
     */
    public DateTime getLastModifiedDateTime() {
        return lastModifiedDateTime;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @param zoneId the zoneId to set
     */
    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * @param computeOfferingId the computeOfferingId to set
     */
    public void setComputeOfferingId(Long computeOfferingId) {
        this.computeOfferingId = computeOfferingId;
    }

    /**
     * @param setupCost the setupCost to set
     */
    public void setSetupCost(Double setupCost) {
        this.setupCost = setupCost;
    }

    /**
     * @param instanceRunningCost the instanceRunningCost to set
     */
    public void setInstanceRunningCost(Double instanceRunningCost) {
        this.instanceRunningCost = instanceRunningCost;
    }

    /**
     * @param instanceStoppageCost the instanceStoppageCost to set
     */
    public void setInstanceStoppageCost(Double instanceStoppageCost) {
        this.instanceStoppageCost = instanceStoppageCost;
    }

    /**
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @param updatedBy the updatedBy to set
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * @param createdDateTime the createdDateTime to set
     */
    public void setCreatedDateTime(DateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * @param lastModifiedDateTime the lastModifiedDateTime to set
     */
    public void setLastModifiedDateTime(DateTime lastModifiedDateTime) {
        this.lastModifiedDateTime = lastModifiedDateTime;
    }



    /**
     * @return the zone
     */
    public Zone getZone() {
        return zone;
    }

    /**
     * @param zone the zone to set
     */
    public void setZone(Zone zone) {
        this.zone = zone;
    }

    /**
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
