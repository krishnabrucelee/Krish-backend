package ck.panda.domain.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.json.JSONException;
import org.json.JSONObject;

import ck.panda.util.JsonValidator;

/**
 * Snapshots are a point-in-time capture of virtual machine disks. Memory and CPU states are not captured.
 *  If you are using the Oracle VM hypervisor, you can not take snapshots, since OVM does not support them.
 *
 * Snapshots is taken for volumes, including both root and data disks
 */
@Entity
@Table(name = "ck_snapshot")
public class Snapshot {

    /** Unique Id of the instance. */
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

    /** Name of the account associated with volume. */
    @Column(name = "account")
    private String account;

    /** Id of the domain. */
    @Column(name = "domain_id")
    private String domainId;

    /** Id of the zone. */
    @Column(name = "zone_id")
    private String zoneId;

    // Todo relational mapping to be done with volume.
    /** Id of the volume. */
    @Column(name = "volume_id")
    private String volumeId;

    /** Type of the snapshot. */
    @Column(name = "snapshottype")
    private String snapshotType;

    /** Interval type. */
    @Column(name = "interval_type")
    private String intervalType;

    /** state of the snapshot. */
    @Column(name = "state")
    private String state;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        id = id;
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the account
     */
    public String getAccount() {
        return account;
    }

    /**
     * @param account the account to set
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * @return the domainId
     */
    public String getDomainId() {
        return domainId;
    }

    /**
     * @param domainId the domainId to set
     */
    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    /**
     * @return the zoneId
     */
    public String getZoneId() {
        return zoneId;
    }

    /**
     * @param zoneId the zoneId to set
     */
    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * @return the volumeId
     */
    public String getVolumeId() {
        return volumeId;
    }

    /**
     * @param volumeId the volumeId to set
     */
    public void setVolumeId(String volumeId) {
        this.volumeId = volumeId;
    }

    /**
     * @return the snapshotType
     */
    public String getSnapshotType() {
        return snapshotType;
    }

    /**
     * @param snapshotType the snapshotType to set
     */
    public void setSnapshotType(String snapshotType) {
        this.snapshotType = snapshotType;
    }

    /**
     * @return the intervalType
     */
    public String getIntervalType() {
        return intervalType;
    }

    /**
     * @param intervalType the intervalType to set
     */
    public void setIntervalType(String intervalType) {
        this.intervalType = intervalType;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Convert JSONObject to domain entity.
     *
     * @param object json object
     * @return domain entity object.
     * @throws JSONException handles json exception.
     */
    public static Snapshot convert(JSONObject object) throws JSONException {
        Snapshot snapshot = new Snapshot();
        try {
            snapshot.uuid =  JsonValidator.jsonStringValidation(object, "id");
            snapshot.name =  JsonValidator.jsonStringValidation(object, "name");
            snapshot.domainId = JsonValidator.jsonStringValidation(object, "domainid");
            snapshot.zoneId = JsonValidator.jsonStringValidation(object, "zoneid");
            snapshot.volumeId = JsonValidator.jsonStringValidation(object, "volumeid");
            snapshot.account = JsonValidator.jsonStringValidation(object, "account");
            snapshot.snapshotType = JsonValidator.jsonStringValidation(object, "snapshottype");
            snapshot.intervalType = JsonValidator.jsonStringValidation(object, "intervaltype");
            snapshot.state = JsonValidator.jsonStringValidation(object, "state");
        } catch (Exception ex) {
            ex.printStackTrace();
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
