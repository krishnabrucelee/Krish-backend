package ck.panda.domain.entity;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import ck.panda.domain.entity.ResourceLimitProject.ResourceType;
import ck.panda.util.JsonUtil;

@Entity
@Table(name = "snapshot_policy")
@SuppressWarnings("serial")
@EntityListeners(AuditingEntityListener.class)
public class SnapshotPolicy {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SnapshotPolicy.class);

     /** Unique id of the instance. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Cloudstack's volume snapshot policy uuid. */
    @Column(name = "uuid")
    private String uuid;

    /** Instance volume id. */
    @JoinColumn(name = "volume_id", referencedColumnName = "Id", updatable = false, insertable = false)
    @ManyToOne
    private Volume volume;

    /** Id of the volume. */
    @Column(name = "volume_id")
    private Long volumeId;

    @Transient
    private Integer dayOfMonth;

    @Transient
    private DayOfWeek dayOfWeek;

    /** Maximum number of snapshots. */
    @Column(name = "max_snaps")
    private Integer maximumSnapshots;

    /** SnapshotPolicy policy time zone */
    @Column(name = "snapshot_policy_time_zone")
    private String timeZone;

    /** SnapshotPolicy policy time zone */
    @Column(name = "shedule_time")
    private String scheduleTime;

    @Transient
    private String minutes;

    @Transient
    private String hours;

    /** Meridian for snapshot policy. */
    @Column(name = "meridian")
    private String meridian;

    /** Interval type. */
    @Column(name = "interval_type")
    private IntervalType intervalType;

    /** IsActive status of snapshot.*/
    @Column(name = "is_active")
    private Boolean isActive;

    /** Set syncFlag. */
    @Transient
    private Boolean syncFlag;

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

    /** Transient volume of the snapshot. */
    @Transient
    private String transVolumeId;

    /** SnapshotPolicy interval types */
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

    /** Lists seven days of week. */
    public enum DayOfWeek {

        /** First day of the week. */
        SUNDAY,

        /** Second day of the week. */
        MONDAY,

        /** Third day of the week. */
        TUESDAY,

        /** Fourth day of the week. */
       WEDNESDAY,

       /** Fifth day of the week .*/
       THURSDAY,

       /** Sixth day of the week. */
       FRIDAY,

       /** Seventh day of the week . */
       SATURDAY
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
     * Get snapshot policy month.
     *
     * @return the dayOfMonth
     */
    public Integer getDayOfMonth() {
        return dayOfMonth;
    }

    /**
     * Set snapshot policy month.
     *
     * @param dayOfMonth to set
     */
    public void setDayOfMonth(Integer dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    /**
     * Get the maximum number of snapshots.
     *
     * @return the maximumSnapshots
     */
    public Integer getMaximumSnapshots() {
        return maximumSnapshots;
    }

    /**
     * Set the maximum number of snapshots.
     *
     * @param maximumSnapshots  to set
     */
    public void setMaximumSnapshots(Integer maximumSnapshots) {
        this.maximumSnapshots = maximumSnapshots;
    }

    /**
     * Get snapshot policy time zone.
     *
     * @return the snapshotPolicyTimeZone
     */
    public String getTimeZone() {
        return timeZone;
    }

    /**
     * Set snapshot policy time zone.
     *
     * @param snapshotPolicyTimeZone  to set
     */
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * Get Set snapshot policy minutes.
     *
     * @return the minutes
     */
    public String getMinutes() {
        return minutes;
    }

    /**
     * Set snapshot policy minutes.
     *
     * @param minutes to set
     */
    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    /**
     * Get the snapshot policy hours.
     *
     * @return the hours
     */
    public String getHours() {
        return hours;
    }

    /**
     * Set the snapshot policy hours.
     *
     * @param hours to set
     */
    public void setHours(String hours) {
        this.hours = hours;
    }

    /**
     * Get the day of the week.
     *
     * @return the dayOfWeek
     */
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * Set Get the day of the week.
     *
     * @param dayOfWeek to set
     */
    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
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
     * Get the policy's scheduled time.
     *
     * @return the scheduleTime
     */
    public String getScheduleTime() {
        return scheduleTime;
    }

    /**
     * Set the policy's scheduled time.
     *
     * @param scheduleTime the scheduleTime to set
     */
    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    /**
     * Get the transient volume id.
     *
     * @return the transVolumeId
     */
    public String getTransVolumeId() {
        return transVolumeId;
    }

    /**
     * Set the transient volume id.
     *
     * @param transVolumeId  to set
     */
    public void setTransVolumeId(String transVolumeId) {
        this.transVolumeId = transVolumeId;
    }

    /**
     * Get the meridian time.
     *
     * @return the meridian
     */
    public String getMeridian() {
        return meridian;
    }

    /**
     * Set the meridian.
     *
     * @param meridian to set
     */
    public void setMeridian(String meridian) {
        this.meridian = meridian;
    }

    /**
     * Convert JSONObject to snapshot policy entity.
     *
     * @param jsonObject json object
     * @return snapshot policy entity object.
     * @throws JSONException handles json exception.
     */
    public static SnapshotPolicy convert(JSONObject jsonObject) throws JSONException {
        SnapshotPolicy snapshot = new SnapshotPolicy();
        snapshot.setSyncFlag(false);

        try {
            snapshot.setIsActive(true);
            snapshot.setUuid(JsonUtil.getStringValue(jsonObject, "id"));
            snapshot.setTransVolumeId(JsonUtil.getStringValue(jsonObject, "volumeid"));
            snapshot.setIntervalType(IntervalType.values()[(JsonUtil.getIntegerValue(jsonObject, "intervaltype"))]);
            snapshot.setTimeZone(JsonUtil.getStringValue(jsonObject, "timezone"));
            snapshot.setMaximumSnapshots((JsonUtil.getIntegerValue(jsonObject, "maxsnaps")));
            snapshot.setScheduleTime((JsonUtil.getStringValue(jsonObject, "schedule")));
        } catch (Exception ex) {
            LOGGER.error("SnapshotPolicy-convert", ex);
        }
        return snapshot;
    }

    /**
     * Mapping entity object into list.
     *
     * @param snapshotList list of snapshots.
     * @return snapshot map
     */
    public static Map<String, SnapshotPolicy> convert(List<SnapshotPolicy> snapshotList) {
        Map<String, SnapshotPolicy> snapshotMap = new HashMap<String, SnapshotPolicy>();

        for (SnapshotPolicy snapshot : snapshotList) {
            snapshotMap.put(snapshot.getUuid(), snapshot);
        }
        return snapshotMap;
    }
}
