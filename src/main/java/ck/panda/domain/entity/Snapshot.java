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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Type;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import ck.panda.constants.CloudStackConstants;
import ck.panda.util.JsonUtil;
import ck.panda.util.JsonValidator;

/**
 * Snapshots are a point-in-time capture of virtual machine disks. Memory and CPU states are not captured. If you are
 * using the Oracle VM hypervisor, you can not take snapshots, since OVM does not support them.
 *
 * Snapshots is taken for volumes, including both root and data disks
 */
@Entity
@Table(name = "snapshots")
@SuppressWarnings("serial")
public class Snapshot implements Serializable {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Snapshot.class);

    /** Unique id of the instance. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Cloudstack's volume snapshot uuid. */
    @Column(name = "uuid")
    private String uuid;

    /** Name of the snapshot. */
    @Column(name = "name")
    private String name;

    /** Department Object for the Network. */
    @JoinColumn(name = "department_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Department department;

    /** id for the Department. */
    @Column(name = "department_id")
    private Long departmentId;

    /** Instance domain id. */
    @JoinColumn(name = "domain_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Domain domain;

    /** Instance domain id. */
    @Column(name = "domain_id")
    private Long domainId;

    /** Instance zone. */
    @JoinColumn(name = "zone_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Zone zone;

    /** Instance zone id. */
    @Column(name = "zone_id")
    private Long zoneId;

    /** Instance volume id. */
    @JoinColumn(name = "volume_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Volume volume;

    // Todo relational mapping to be done with volume.
    /** Id of the volume. */
    @Column(name = "volume_id")
    private Long volumeId;

    /** Type of the snapshot. */
    @Column(name = "snapshottype")
    private String snapshotType;

    /** Snapshot month */
    @Column(name = "schedule_month")
    private Integer scheduleMonth;

    /** Maximum number of snapshots. */
    @Column(name = "max_snaps")
    private Integer maximumSnapshots;

    /** Snapshot policy time zone */
    @Column(name = "snapshot_policy_time_zone")
    private Integer snapshotPolicyTimeZone;

    /** Interval type. */
    @Column(name = "interval_type")
    private IntervalType intervalType;

    /** state of the snapshot. */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    /** */
    @Column(name = "is_active")
    private Boolean isActive;

    /** Set syncFlag. */
    @Transient
    private Boolean syncFlag;

    /** Transient department of the network. */
    @Transient
    private String transDepartmentId;

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

    /** Transient domain of the snapshot. */
    @Transient
    private String transDomainId;

    /** Transient zone of the snapshot. */
    @Transient
    private String transZoneId;

    /** Transient volume of the snapshot. */
    @Transient
    private String transVolumeId;

    /** Transient volume name of the snapshot. */
    @Transient
    private String transVolumeName;

    /** Status for snapshot. */
    public enum Status {

        /** Intial response while creating a snapshot. */
        BACKINGUP,

        /** Backed up is when snapshot is created. */
        BACKEDUP,

        /** Ready to create a snapshot. */
        READY,

        /** When snapshot gets destroyed. */
        DESTROYED,

        /** When snapshot gets Allocated . */
        ALLOCATED
    }

    /** Snapshot interval types */
    public enum IntervalType {

        /** Hourly snaphshot back up. */
        HOURLY,

        /** Weekly  snaphsot back up .*/
        WEEKLY,

        /** Daily snapshot back up. */
        DAILY,

        /** Monthly snapshot back up. */
        MONTHLY
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id.
     *
     * @param id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the uuid.
     *
     * @param uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name.
     *
     * @param name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the Department object.
     *
     * @return the department
     */
    public Department getDepartment() {
        return department;
    }

    /**
     * Set the Department object.
     *
     * @param department the department to set
     */

    public void setDepartment(Department department) {
        this.department = department;
    }

    /**
     * Get the department Id.
     *
     * @return the departmentId
     */

    public Long getDepartmentId() {
        return departmentId;
    }

    /**
     * Set the Department Id.
     *
     * @param departmentId the departmentId to set
     */
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }


    /**
     * Get Domain.
     *
     * @return the domain
     */
    public Domain getDomain() {
        return domain;
    }

    /**
     * Set the domain.
     *
     * @param domain to set
     */
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    /**
     * Get DomainId.
     *
     * @return the domainId
     */
    public Long getDomainId() {
        return domainId;
    }

    /**
     * Set the domainId.
     *
     * @param domainId to set
     */
    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    /**
     * Get Zone.
     *
     * @return the zone
     */
    public Zone getZone() {
        return zone;
    }

    /**
     * Set the zone .
     *
     * @param zone to set
     */
    public void setZone(Zone zone) {
        this.zone = zone;
    }

    /**
     * Get zoneID.
     *
     * @return the zoneId
     */
    public Long getZoneId() {
        return zoneId;
    }

    /**
     * Set the zoneId.
     *
     * @param zoneId to set
     */
    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * Get volume.
     *
     * @return the volume
     */
    public Volume getVolume() {
        return volume;
    }

    /**
     * Set the volume.
     *
     * @param volume to set
     */
    public void setVolume(Volume volume) {
        this.volume = volume;
    }

    /**
     * Get volume Id.
     *
     * @return the volumeId
     */
    public Long getVolumeId() {
        return volumeId;
    }

    /**
     * Set the volumeId.
     *
     * @param volumeId to set
     */
    public void setVolumeId(Long volumeId) {
        this.volumeId = volumeId;
    }

    /**
     * Get snapshottype.
     *
     * @return the snapshotType
     */
    public String getSnapshotType() {
        return snapshotType;
    }

    /**
     * Set the snapshotType.
     *
     * @param snapshotType to set
     */
    public void setSnapshotType(String snapshotType) {
        this.snapshotType = snapshotType;
    }

    /**
     * Get the snapshot interval type.
     *
     * @return the intervalType
     */
    public IntervalType getIntervalType() {
        return intervalType;
    }

    /**
     * Set the snapshot interval type.
     *
     * @param intervalType to set
     */
    public void setIntervalType(IntervalType intervalType) {
        this.intervalType = intervalType;
    }

    /**
     * Get Status.
     *
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set the status .
     *
     * @param status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Get Isactive.
     *
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Set isActive.
     *
     * @param isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Get SyncFlag.
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
     * Get the Department Id.
     *
     * @return the transDepartmentId
     */
    public String getTransDepartmentId() {
        return transDepartmentId;
    }

    /**
     * Set the department Id.
     *
     * @param transDepartmentId the transDepartmentId to set
     */
    public void setTransDepartmentId(String transDepartmentId) {
        this.transDepartmentId = transDepartmentId;
    }


    /**
     * Get the created user id.
     *
     * @return the createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Get the created user id.
     *
     * @param createdBy to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updated user id.
     *
     * @return the updatedBy
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updated by User id.
     *
     * @param updatedBy to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the createdDateTime.
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
     * Get the updatedDateTime.
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
     * Get the transient domain id.
     *
     * @return the transDomainId
     */
    public String getTransDomainId() {
        return transDomainId;
    }

    /**
     * Set the transient domain id..
     *
     * @param transDomainId to set
     */
    public void setTransDomainId(String transDomainId) {
        this.transDomainId = transDomainId;
    }

    /**
     * Get the transient zone id.
     *
     * @return the transZoneId
     */
    public String getTransZoneId() {
        return transZoneId;
    }

    /**
     * Set the transient zone id.
     *
     * @param transZoneId the transZoneId to set
     */
    public void setTransZoneId(String transZoneId) {
        this.transZoneId = transZoneId;
    }

    /**
     * @return the transVolumeId
     */
    public String getTransVolumeId() {
        return transVolumeId;
    }

    /**
     * @param transVolumeId the transVolumeId to set
     */
    public void setTransVolumeId(String transVolumeId) {
        this.transVolumeId = transVolumeId;
    }


    /**
     * Get the the transVolumeName .
     *
     * @return the transVolumeName
     */
    public String getTransVolumeName() {
        return transVolumeName;
    }

    /**
     * Set the transVolumeName .
     *
     * @param transVolumeName to set
     */
    public void setTransVolumeName(String transVolumeName) {
        this.transVolumeName = transVolumeName;
    }

    /**
     * @return the scheduleMonth
     */
    public Integer getScheduleMonth() {
        return scheduleMonth;
    }

    /**
     * @param scheduleMonth the scheduleMonth to set
     */
    public void setScheduleMonth(Integer scheduleMonth) {
        this.scheduleMonth = scheduleMonth;
    }

    /**
     * @return the maximumSnapshots
     */
    public Integer getMaximumSnapshots() {
        return maximumSnapshots;
    }

    /**
     * @param maximumSnapshots the maximumSnapshots to set
     */
    public void setMaximumSnapshots(Integer maximumSnapshots) {
        this.maximumSnapshots = maximumSnapshots;
    }

    /**
     * @return the snapshotPolicyTimeZone
     */
    public Integer getSnapshotPolicyTimeZone() {
        return snapshotPolicyTimeZone;
    }

    /**
     * @param snapshotPolicyTimeZone the snapshotPolicyTimeZone to set
     */
    public void setSnapshotPolicyTimeZone(Integer snapshotPolicyTimeZone) {
        this.snapshotPolicyTimeZone = snapshotPolicyTimeZone;
    }

    /**
     * Convert JSONObject to domain entity.
     *
     * @param jsonObject json object
     * @return domain entity object.
     * @throws JSONException handles json exception.
     */
    public static Snapshot convert(JSONObject jsonObject) throws JSONException {
        Snapshot snapshot = new Snapshot();
        snapshot.setSyncFlag(false);

        try {
            snapshot.setName(JsonUtil.getStringValue(jsonObject, "name"));
            snapshot.setIsActive(true);
            snapshot.setUuid(JsonUtil.getStringValue(jsonObject, "id"));
            snapshot.setTransZoneId(JsonUtil.getStringValue(jsonObject, "zoneid"));
            snapshot.setTransDomainId(JsonUtil.getStringValue(jsonObject, "domainid"));
            snapshot.setTransVolumeId(JsonUtil.getStringValue(jsonObject, "volumeid"));
            snapshot.setSnapshotType(JsonUtil.getStringValue(jsonObject, "snapshottype"));
            snapshot.setStatus(Status.valueOf(JsonUtil.getStringValue(jsonObject, "state").toUpperCase()));
            snapshot.setTransDepartmentId(JsonUtil.getStringValue(jsonObject, "account"));
            snapshot.setIntervalType(IntervalType.valueOf(JsonUtil.getStringValue(jsonObject, "intervaltype").toUpperCase()));
        } catch (Exception ex) {
            LOGGER.error("Snapshot-convert", ex);
        }
        return snapshot;
    }

    /**
     * Mapping entity object into list.
     *
     * @param snapshotList list of snapshots.
     * @return snapshot map
     */
    public static Map<String, Snapshot> convert(List<Snapshot> snapshotList) {
        Map<String, Snapshot> snapshotMap = new HashMap<String, Snapshot>();

        for (Snapshot snapshot : snapshotList) {
            snapshotMap.put(snapshot.getUuid(), snapshot);
        }
        return snapshotMap;
    }
}
