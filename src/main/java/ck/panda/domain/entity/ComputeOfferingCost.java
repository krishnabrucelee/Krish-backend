package ck.panda.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "service_offerings_cost")
@SuppressWarnings("serial")
public class ComputeOfferingCost {

    /** The id of the Compute offering Cost. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Zone id for this offering. */
    @JoinColumn(name = "zone_id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne
    private Zone zone;

    /** Id of the zone. */
    @Column(name = "zone_id")
    private Long zoneId;

    /** Many cost for one compute offfering. */
    @ManyToOne
    private ComputeOffering computeOffering;

    /** The Setup Cost. */
    @Column(name = "setup_cost")
    private Double setupCost;

    /** Cost of Running Instance for vcpu. */
    @Column(name = "instance_running_cost_per_vcpu")
    private Double instanceRunningCostPerVcpu;

    /** Cost of Running Instancefor memory. */
    @Column(name = "instance_running_cost_per_mb")
    private Double instanceRunningCostPerMB;

    /** Cost of Running Instance for iops. */
    @Column(name = "instance_running_cost_per_iops")
    private Double instanceRunningCostPerIops;

    /** Cost of Stoppage Instance for vcpu. */
    @Column(name = "instance_stoppage_cost_per_vcpu")
    private Double instanceStoppageCostPerVcpu;

    /** Cost of Stoppage Instance for memory. */
    @Column(name = "instance_stoppage_cost_per_mb")
    private Double instanceStoppageCostPerMB;

    /** Cost of Running Instance for vcpu. */
    @Column(name = "instance_stoppage_cost_per_iops")
    private Double instanceStoppageCostPerIops;

    /** Cost of Stoppage Instance for vcpu. */
    @Column(name = "instance_running_cost_vcpu")
    private Double instanceRunningCostVcpu;

    /** Cost of Running Instancefor memory. */
    @Column(name = "instance_running_cost_memory")
    private Double instanceRunningCostMemory;

    /** Cost of Running Instance for iops. */
    @Column(name = "instance_running_cost_iops")
    private Double instanceRunningCostIops;

    /** Cost of Stoppage Instance for vcpu. */
    @Column(name = "instance_stoppage_cost_vcpu")
    private Double instanceStoppageCostVcpu;

    /** Cost of Stoppage Instance for memory. */
    @Column(name = "instance_stoppage_cost_memory")
    private Double instanceStoppageCostMemory;

    /** Cost of Stoppage Instance. for iops. */
    @Column(name = "instance_stoppage_cost_iops")
    private Double instanceStoppageCostIops;

    /** Created by user. */
    @CreatedBy
    @Column(name = "created_by")
    private Long createdBy;

    /** Last updated by user. */
    @LastModifiedBy
    @Column(name = "updated_by")
    private Long updatedBy;

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
     * @return the createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * @return the updatedBy
     */
    public Long getUpdatedBy() {
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
     * @param setupCost the setupCost to set
     */
    public void setSetupCost(Double setupCost) {
        this.setupCost = setupCost;
    }

    /** @return the zone. */
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
     * @return the computeOffering
     */
    public ComputeOffering getComputeOffering() {
        return computeOffering;
    }

    /**
     * @param computeOffering the computeOffering to set
     */
    public void setComputeOffering(ComputeOffering computeOffering) {
        this.computeOffering = computeOffering;
    }

    /**
     * @return the instanceRunningCostVcpu
     */
    public Double getInstanceRunningCostVcpu() {
        return instanceRunningCostVcpu;
    }

    /**
     * @param instanceRunningCostVcpu the instanceRunningCostVcpu to set
     */
    public void setInstanceRunningCostVcpu(Double instanceRunningCostVcpu) {
        this.instanceRunningCostVcpu = instanceRunningCostVcpu;
    }

    /**
     * @return the instanceRunningCostMemory
     */
    public Double getInstanceRunningCostMemory() {
        return instanceRunningCostMemory;
    }

    /**
     * @param instanceRunningCostMemory the instanceRunningCostMemory to set
     */
    public void setInstanceRunningCostMemory(Double instanceRunningCostMemory) {
        this.instanceRunningCostMemory = instanceRunningCostMemory;
    }

    /**
     * @return the instanceRunningCostIops
     */
    public Double getInstanceRunningCostIops() {
        return instanceRunningCostIops;
    }

    /**
     * @param instanceRunningCostIops the instanceRunningCostIops to set
     */
    public void setInstanceRunningCostIops(Double instanceRunningCostIops) {
        this.instanceRunningCostIops = instanceRunningCostIops;
    }

    /**
     * @return the instanceStoppageCostVcpu
     */
    public Double getInstanceStoppageCostVcpu() {
        return instanceStoppageCostVcpu;
    }

    /**
     * @param instanceStoppageCostVcpu the instanceStoppageCostVcpu to set
     */
    public void setInstanceStoppageCostVcpu(Double instanceStoppageCostVcpu) {
        this.instanceStoppageCostVcpu = instanceStoppageCostVcpu;
    }

    /**
     * @return the instanceStoppageCostMemory
     */
    public Double getInstanceStoppageCostMemory() {
        return instanceStoppageCostMemory;
    }

    /**
     * @param instanceStoppageCostMemory the instanceStoppageCostMemory to set
     */
    public void setInstanceStoppageCostMemory(Double instanceStoppageCostMemory) {
        this.instanceStoppageCostMemory = instanceStoppageCostMemory;
    }

    /**
     * @return the instanceStoppageCostIops
     */
    public Double getInstanceStoppageCostIops() {
        return instanceStoppageCostIops;
    }

    /**
     * @param instanceStoppageCostIops the instanceStoppageCostIops to set
     */
    public void setInstanceStoppageCostIops(Double instanceStoppageCostIops) {
        this.instanceStoppageCostIops = instanceStoppageCostIops;
    }

    /**
     * @return the setupCost
     */
    public Double getSetupCost() {
        return setupCost;
    }

    /**
     * @return the instanceRunningCostPerVcpu
     */
    public Double getInstanceRunningCostPerVcpu() {
        return instanceRunningCostPerVcpu;
    }

    /**
     * @param instanceRunningCostPerVcpu the instanceRunningCostPerVcpu to set
     */
    public void setInstanceRunningCostPerVcpu(Double instanceRunningCostPerVcpu) {
        this.instanceRunningCostPerVcpu = instanceRunningCostPerVcpu;
    }

    /**
     * @return the instanceRunningCostPerMB
     */
    public Double getInstanceRunningCostPerMB() {
        return instanceRunningCostPerMB;
    }

    /**
     * @param instanceRunningCostPerMB the instanceRunningCostPerMB to set
     */
    public void setInstanceRunningCostPerMB(Double instanceRunningCostPerMB) {
        this.instanceRunningCostPerMB = instanceRunningCostPerMB;
    }

    /**
     * @return the instanceRunningCostPerIops
     */
    public Double getInstanceRunningCostPerIops() {
        return instanceRunningCostPerIops;
    }

    /**
     * @param instanceRunningCostPerIops the instanceRunningCostPerIops to set
     */
    public void setInstanceRunningCostPerIops(Double instanceRunningCostPerIops) {
        this.instanceRunningCostPerIops = instanceRunningCostPerIops;
    }

    /**
     * @return the instanceStoppageCostPerVcpu
     */
    public Double getInstanceStoppageCostPerVcpu() {
        return instanceStoppageCostPerVcpu;
    }

    /**
     * @param instanceStoppageCostPerVcpu the instanceStoppageCostPerVcpu to set
     */
    public void setInstanceStoppageCostPerVcpu(Double instanceStoppageCostPerVcpu) {
        this.instanceStoppageCostPerVcpu = instanceStoppageCostPerVcpu;
    }

    /**
     * @return the instanceStoppageCostPerMB
     */
    public Double getInstanceStoppageCostPerMB() {
        return instanceStoppageCostPerMB;
    }

    /**
     * @param instanceStoppageCostPerMB the instanceStoppageCostPerMB to set
     */
    public void setInstanceStoppageCostPerMB(Double instanceStoppageCostPerMB) {
        this.instanceStoppageCostPerMB = instanceStoppageCostPerMB;
    }

    /**
     * @return the instanceStoppageCostPerIops
     */
    public Double getInstanceStoppageCostPerIops() {
        return instanceStoppageCostPerIops;
    }

    /**
     * @param instanceStoppageCostPerIops the instanceStoppageCostPerIops to set
     */
    public void setInstanceStoppageCostPerIops(Double instanceStoppageCostPerIops) {
        this.instanceStoppageCostPerIops = instanceStoppageCostPerIops;
    }

    /**
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @param updatedBy the updatedBy to set
     */
    public void setUpdatedBy(Long updatedBy) {
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
