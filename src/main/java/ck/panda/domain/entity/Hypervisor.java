package ck.panda.domain.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

import ck.panda.util.JsonUtil;

/**
 * A hypervisor is also known as a Virtual Machine Manager (VMM) and its sole purpose is to allow multiple “machines”
 * to share a single hardware platform.The hypervisor separates the operating system (OS) from the hardware by taking
 * the responsibility of allowing each running OS time with the underlying hardware.
 *
 */
@Entity
@Table(name = "ck_hypervisor")
@SuppressWarnings("serial")
public class Hypervisor implements Serializable {

    /** Id of the hypervisor. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /** Name of the hypervisor. */
    @Column(name = "name", nullable = false)
    private String name;

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

    /** Last updated date and time. */
    @LastModifiedDate
    @Column(name = "updated_date_time")
    private DateTime updatedDateTime;

    /**
     * Get the id.
     *
     * @return the id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the id.
     *
     * @param id - the id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the name.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name.
     *
     * @param name - the name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the version.
     *
     * @return the version.
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Set the version.
     *
     * @param version - the version to set.
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Get the createdBy.
     *
     * @return the createdBy.
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the createdBy.
     *
     * @param createdBy - the createdBy to set.
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the updatedBy.
     *
     * @return the updatedBy.
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the updatedBy.
     *
     * @param updatedBy - the updatedBy to set.
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Get the createdDateTime.
     *
     * @return the createdDateTime.
     */
    public DateTime getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Set the createdDateTime.
     *
     * @param createdDateTime - the createdDateTime to set.
     */
    public void setCreatedDateTime(DateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the updatedDateTime.
     *
     * @return the updatedDateTime.
     */
    public DateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Set the updatedDateTime.
     *
     * @param updatedDateTime - the updatedDateTime to set.
     */
    public void setUpdatedDateTime(DateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    /**
     * Convert JSONObject to hypervisor entity.
     *
     * @param object json object
     * @return hypervisor entity objects
     * @throws Exception unhandled errors.
     */
    public static Hypervisor convert(JSONObject object) throws Exception {
        Hypervisor hypervisor = new Hypervisor();
        hypervisor.setName(JsonUtil.getStringValue(object, "name"));
        return hypervisor;
    }

    /**
     * Mapping hypervisor entity object in list.
     *
     * @param hypervisorList list of hypervisors
     * @return hypervisor mapped values.
     */
    public static Map<String, Hypervisor> convert(List<Hypervisor> hypervisorList) {
        Map<String, Hypervisor> hypervisorMap = new HashMap<String, Hypervisor>();

        for (Hypervisor hypervisor : hypervisorList) {
            hypervisorMap.put(hypervisor.getName(), hypervisor);
        }
        return hypervisorMap;
    }
}
